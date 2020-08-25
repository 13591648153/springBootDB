package com.yd.test.components.flyway;

/***
 * 
 * FlywayInitializerBean  空类
   *     防止spring.flyway.enabled=false时，使用@DependsOn("flywayInitializer") 报错
 */
public class FlywayInitializerBean {

}
