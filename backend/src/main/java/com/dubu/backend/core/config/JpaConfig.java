package com.dubu.backend.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.dubu.backend",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class JpaConfig {
}
