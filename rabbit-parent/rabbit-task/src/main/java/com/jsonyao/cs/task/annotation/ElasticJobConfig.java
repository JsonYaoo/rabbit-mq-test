package com.jsonyao.cs.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义Elastic Job注解配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticJobConfig {

    /**
     * 作业名称
     * @return
     */
    String name();

    /**
     * Cron表达式
     * @return
     */
    String cron() default "";

    /**
     * 作业分片总数
     * @return
     */
    int shardingTotalCount() default 1;

    /**
     * 分片参数: eg: 分片序号1=分片参数1,分片序号2=分片参数2
     * @return
     */
    String shardingItemParameters() default "";

    /**
     * 参数作业参数
     * @return
     */
    String jobParameter() default "";

    /**
     * 是否开启失败转移
     * @return
     */
    boolean failover() default false;

    /**
     * 是否开启错过任务重新执行
     * @return
     */
    boolean misfire() default true;

    /**
     * 作业描述信息
     * @return
     */
    String description() default "";

    /**
     * 是否开启本地覆盖
     * @return
     */
    boolean overwrite() default false;

    /**
     * 是否流式处理数据
     * @return
     */
    boolean streamingProcess() default false;

    /**
     * 脚本型作业执行命令行
     * @return
     */
    String scriptCommandLine() default "";

    /**
     * 是否开启监控以作业状态: 瞬时任务建议关闭, 作业时间长建议开启
     * @return
     */
    boolean monitorExecution() default false;

    /**
     * 作业监控端口: 建议配置, 用于dump作业
     * @return
     */
    public int monitorPort() default -1;	//must

    /**
     * 最大允许的本机与注册中心时间误差描述: -1表示不校验时间误差
     * @return
     */
    public int maxTimeDiffSeconds() default -1;	//must

    /**
     * 作业分片策略: 默认轮训
     * @return
     */
    public String jobShardingStrategyClass() default "";	//must

    /**
     * 修复作业服务器不一致状态服务调度间隔时间: 小于1表示不执行修复
     * @return
     */
    public int reconcileIntervalMinutes() default 10;	//must

    /**
     * 作业事件追踪数据源
     * @return
     */
    public String eventTraceRdbDataSource() default "";	//must

    /**
     * 监听器
     * @return
     */
    public String listener() default "";	//must

    /**
     * 是否启用
     * @return
     */
    public boolean disabled() default false;	//must

    /**
     * 分布式监听器
     * @return
     */
    public String distributedListener() default "";

    /**
     * 启动超时时间
     * @return
     */
    public long startedTimeoutMilliseconds() default Long.MAX_VALUE;	//must

    /**
     * 完成超时时间
     * @return
     */
    public long completedTimeoutMilliseconds() default Long.MAX_VALUE;		//must

    /**
     * Job异常处理类: 当当网默认处理类DefaultJobExceptionHandler
     * @return
     */
    public String jobExceptionHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";

    /**
     * 任务执行服务处理类: 当当网默认处理类DefaultExecutorServiceHandler
     * @return
     */
    public String executorServiceHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";
}
