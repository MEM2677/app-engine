package org.entando.entando.plugins.jpredis.aps.system.redis.session;

import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_ACTIVE;
import static org.entando.entando.plugins.jpredis.aps.system.redis.RedisEnvironmentVariables.REDIS_SESSION_ACTIVE;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class RedisSessionCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(RedisSessionActive.class.getName());
        boolean active = false;
        if (attrs != null) {
            active = (boolean) attrs.getFirst("value");
        }
        return active == this.isActive();
    }

    private boolean isActive() {
        return Boolean.toString(true).equals(System.getProperty(REDIS_ACTIVE))
                && Boolean.toString(true).equals(System.getProperty(REDIS_SESSION_ACTIVE));
    }
}
