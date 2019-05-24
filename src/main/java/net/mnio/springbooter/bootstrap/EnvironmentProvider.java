package net.mnio.springbooter.bootstrap;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentProvider {

    @Autowired
    private Environment environment;

    public String[] getEnvironments() {
        return environment.getActiveProfiles();
    }

    public boolean isDevelopment() {
        return ArrayUtils.contains(environment.getActiveProfiles(), "development");
    }

    public boolean isProduction() {
        return ArrayUtils.contains(environment.getActiveProfiles(), "production");
    }

    public boolean isTest() {
        return ArrayUtils.contains(environment.getActiveProfiles(), "test");
    }

    public String getDatasourceUrl() {
        return environment.getProperty("spring.datasource.url");
    }
}
