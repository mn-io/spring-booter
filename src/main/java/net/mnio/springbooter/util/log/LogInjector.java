package net.mnio.springbooter.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.lang.reflect.Field;

@Component
public class LogInjector implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object o, final String s) {
        ReflectionUtils.doWithFields(o.getClass(), new LoggerFieldCallback(o));
        return o;
    }

    private static class LoggerFieldCallback implements FieldCallback {

        private final Object o;

        LoggerFieldCallback(final Object o) {
            this.o = o;
        }

        @Override
        public void doWith(final Field field) throws IllegalAccessException {
            ReflectionUtils.makeAccessible(field);

            if (field.getAnnotation(Log.class) == null) {
                return;
            }

            final Logger logger = LoggerFactory.getLogger(o.getClass());
            field.set(o, logger);
        }
    }
}
