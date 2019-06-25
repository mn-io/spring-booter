package net.mnio.springbooter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;

@SpringBootApplication
@EnableTransactionManagement
@EnableRetry
public class Application {

    public static void main(final String[] args) {
        final SpringApplication springApplication = new SpringApplication();
        springApplication.addPrimarySources(Collections.singleton(Application.class));
        springApplication.run(args);
    }
}