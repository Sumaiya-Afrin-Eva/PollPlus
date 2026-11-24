package com.example.pollpulse.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DataInitializer implements ApplicationRunner {
    private final Environment environment;

    public DataInitializer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean seedDemoUsers = Boolean.parseBoolean(
                environment.getProperty("app.seed-demo-users", "false")
        );

        if (seedDemoUsers) {
            String seedPassword = environment.getProperty("app.seed-password");
            if (!StringUtils.hasText(seedPassword)) {
                throw new IllegalStateException(
                        "SEED_PASSWORD must be set when SEED_DEMO_USERS=true"
                );
            }
        }
    }
}
