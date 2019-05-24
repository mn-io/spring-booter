package net.mnio.springbooter.bootstrap.listener;

import net.mnio.springbooter.bootstrap.EnvironmentProvider;
import net.mnio.springbooter.util.log.Log;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Log
    private Logger log;

    @Autowired
    private EnvironmentProvider environmentProvider;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        final String[] environments = environmentProvider.getEnvironments();
        log.info("Application started with environment(s) {} and db connection {}", Arrays.toString(environments), environmentProvider.getDatasourceUrl());
    }
}