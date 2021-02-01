package com.jsonyao.cs.task.annotation;

import com.jsonyao.cs.task.autoconfigure.JobParserAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自定义Elastic Job Starter模块装配配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 1. 实现模块装配, 只有@EnableElasticJob导入了JobParserAutoConfiguration时, 才能启动自定义的@ElasticJobConfig注解
@Import(JobParserAutoConfiguration.class)
public @interface EnableElasticJob {
}
