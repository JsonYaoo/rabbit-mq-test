package com.jsonyao.cs.task.annotation;

import java.lang.annotation.*;

/**
 * 自定义Elastic Job Starter模块装配配置类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableElasticJob {
}
