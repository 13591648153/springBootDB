package com.yd.test.components.flyway;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/***
 * 
 * DbFlywayEnabledCondition
 * 防止spring.flyway.enabled=false时，使用@DependsOn("flywayInitializer") 报错
 * @author <a href="mailto:he.jf@neusoft.com">he.jf</a>
 * @version $Revision 1.0 $ 2020年3月11日 下午2:00:49
 */
public class FlywayEnabledCondition implements Condition{
    
    private Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String flywayEnabled = context.getEnvironment().getProperty("spring.flyway.enabled");
        if(StringUtils.isEmpty(flywayEnabled)) {
        	flywayEnabled = "false";
        }
        log.info("系统是否启用flyway功能："+flywayEnabled);
        return !Boolean.valueOf(flywayEnabled);
    }

    
}
