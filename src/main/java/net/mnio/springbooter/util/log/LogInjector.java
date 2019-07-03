package net.mnio.springbooter.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class LogInjector implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object o, final String s) {
        ReflectionUtils.doWithFields(o.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);

            if (field.getAnnotation(Log.class) == null) {
                return;
            }

            final Logger logger = LoggerFactory.getLogger(o.getClass());
            field.set(o, logger);
        });
        return o;
    }
}
