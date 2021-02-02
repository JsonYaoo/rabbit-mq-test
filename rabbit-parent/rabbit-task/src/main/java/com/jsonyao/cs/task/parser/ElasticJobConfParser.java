package com.jsonyao.cs.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.jsonyao.cs.task.annotation.ElasticJobConfig;
import com.jsonyao.cs.task.autoconfigure.JobZookeeperProperties;
import com.jsonyao.cs.task.enums.ElasticJobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 自定义@ElasticJobConfig注解解析类
 */
@Slf4j
public class ElasticJobConfParser  implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * ElasticJob注册中心核心类
     */
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    /**
     * 自定义注册中心属性类
     */
    private JobZookeeperProperties zookeeperProperties;

    /**
     * 构造方法
     * @param zookeeperRegistryCenter
     * @param zookeeperProperties
     */
    public ElasticJobConfParser(ZookeeperRegistryCenter zookeeperRegistryCenter, JobZookeeperProperties zookeeperProperties) {
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
        this.zookeeperProperties = zookeeperProperties;
    }

    /**
     * 解析@ElasticJobConfig自定义注解, 注入ElasticJob核心配置: 在整个Spring 应用完成时, 才执行ElasticJobConfig注解解析, 保证所有的Bean或者服务都实例化完成
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // 1. 获取应用上下文
            ConfigurableApplicationContext applicationContext = event.getApplicationContext();

            // 2. 遍历所有所有使用了ElasticJobConfig注解的类
            Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
            for(Iterator<?> it = beanMap.values().iterator(); it.hasNext();){
                // 3. 获取当前Bean Class, 如果是动态代理的, 还需要截取真正的ClassName以加载原生的Class对象
                Object confBean = it.next();
                Class<?> clazz = confBean.getClass();
                if(clazz.getName().indexOf("$") > 0){
                    String delegateClazzName = clazz.getName();
                    clazz = Class.forName(delegateClazzName.substring(0, delegateClazzName.indexOf("$")));
                }

                // 4. 遍历真正Class对象实现的所有接口 => 这里为了简单, 只获取第一个接口的名称, 用于判断ElasticJob作业的类型
                String jobTypeName = clazz.getInterfaces()[0].getSimpleName();

                // 5. 获取真正Class对象上@ElasticJobConfig注解Bean的所有属性
                ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
                String jobClass = clazz.getName();
                String jobName = this.zookeeperProperties.getNamespace() + "." + conf.name();
                String cron = conf.cron();
                String shardingItemParameters = conf.shardingItemParameters();
                String description = conf.description();
                String jobParameter = conf.jobParameter();
                String jobExceptionHandler = conf.jobExceptionHandler();
                String executorServiceHandler = conf.executorServiceHandler();
                String jobShardingStrategyClass = conf.jobShardingStrategyClass();
                String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
                String scriptCommandLine = conf.scriptCommandLine();
                boolean failover = conf.failover();
                boolean misfire = conf.misfire();
                boolean overwrite = conf.overwrite();
                boolean disabled = conf.disabled();
                boolean monitorExecution = conf.monitorExecution();
                boolean streamingProcess = conf.streamingProcess();
                int shardingTotalCount = conf.shardingTotalCount();
                int monitorPort = conf.monitorPort();
                int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
                int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();

                // 6. 构造当当网的JobCoreConfiguration
                JobCoreConfiguration coreConfig = JobCoreConfiguration
                        .newBuilder(jobName, cron, shardingTotalCount)
                        .shardingItemParameters(shardingItemParameters)
                        .description(description)
                        .failover(failover)
                        .jobParameter(jobParameter)
                        .misfire(misfire)
                        .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
                        .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
                        .build();

                // 7. 根据任务类型构造当当网的JobTypeConfiguration
                JobTypeConfiguration typeConfig = null;
                if(ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)){
                    typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);// jobClass使用了自定义ElasticJob注解的类Class对象
                }else if(ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)){
                    typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, streamingProcess);// jobClass使用了自定义ElasticJob注解的类Class对象
                }else if(ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)){
                    typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);// jobClass使用了自定义ElasticJob注解的类Class对象
                }

                // 8. 构造LiteJobConfiguration
                LiteJobConfiguration jobConfig = LiteJobConfiguration
                        .newBuilder(typeConfig)// JobTypeConfiguration
                        .overwrite(overwrite)
                        .disabled(disabled)
                        .monitorPort(monitorPort)
                        .monitorExecution(monitorExecution)
                        .maxTimeDiffSeconds(maxTimeDiffSeconds)
                        .jobShardingStrategyClass(jobShardingStrategyClass)
                        .reconcileIntervalMinutes(reconcileIntervalMinutes)
                        .build();

                // 9. 构造SpringJobScheduler
                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                factory.setInitMethodName("init");
                factory.setScope("prototype");// 多例模式
                /* 9.1. 为SpringJobScheduler构造器: 添加ElasticJob参数, 脚本型作业不用注入*/
                if(!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)){
                    factory.addConstructorArgValue(confBean);// confBean为使用了ElasticJobConfig注解的类实例
                }
                /* 9.2. 为SpringJobScheduler构造器: 添加注册中心*/
                factory.addConstructorArgValue(zookeeperRegistryCenter);
                /* 9.3. 为SpringJobScheduler构造器: 添加LiteJobConfiguration*/
                factory.addConstructorArgValue(jobConfig);
                /* 9.3. 为SpringJobScheduler构造器: 添加JobEventConfiguration, 用于追踪ElasticJob事件*/
                if(StringUtils.hasText(eventTraceRdbDataSource)){
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbFactory.addConstructorArgValue(eventTraceRdbDataSource);// 注入作业事件追踪数据源
                    factory.addConstructorArgValue(rdbFactory.getBeanDefinition());// 注入JobEventConfiguration的BeanDefinition
                }
                /* 9.4. 为SpringJobScheduler构造器: 添加监听器BeanDefinition*/
                List<BeanDefinition> targetElasticJobListeners = getTargetElasticJobListeners(conf);
                factory.addConstructorArgValue(targetElasticJobListeners);
                /* 9.5 把SpringJobScheduler BeanDefinition交给DefaultListableBeanFactory, 交由Spring管理并生成对应的实例*/
                String registerBeanName = conf.name() + "SpringJobScheduler";// @ElasticJob自定义注解上的name属性, 设置SpringJobScheduler名称
                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
                SpringJobScheduler springJobScheduler = (SpringJobScheduler) applicationContext.getBean(registerBeanName);
                /* 9.6 调用SpringJobScheduler初始化方法*/
                springJobScheduler.init();

                // 10. @ElasticJobConfig核心配置注入完毕, 打印消息
                log.info("启动elastic-job作业: " + jobName);// 作业名称
            }
            log.info("共计启动elastic-job作业数量为: {} 个", beanMap.values().size());
        } catch (Exception e) {
            log.error("elasticjob 启动异常: {}, 系统强制退出", e);
            System.exit(1);
        }
    }

    /**
     * 根据真正Class对象上@ElasticJobConfig注解Bean的属性, 获取配置的监听器的BeanDefinition
     * @return
     */
    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf){
        // 1. 创建管理BeanDefinition的容器
        List<BeanDefinition> result = new ManagedList<>(2);

        // 2. 获取@ElasticJobConfig自定义注解配置的listener Class
        String listener = conf.listener();
        if(StringUtils.hasText(listener)){
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listener);
            factory.setScope("prototype");// TODO 多例模式?
            result.add(factory.getBeanDefinition());
        }

        // 3. 获取@ElasticJobConfig自定义注解配置的分布式监听器distributedListener Class以及相关属性
        String distributedListener = conf.distributedListener();
        long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();
        if(StringUtils.hasText(distributedListener)){
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListener);
            factory.setScope("prototype");// TODO 多例模式?
            factory.addConstructorArgValue(Long.valueOf(startedTimeoutMilliseconds));
            factory.addConstructorArgValue(Long.valueOf(completedTimeoutMilliseconds));
            result.add(factory.getBeanDefinition());
        }

        // 4. 返回设置好的BeanDefinition集合
        return result;
    }
}
