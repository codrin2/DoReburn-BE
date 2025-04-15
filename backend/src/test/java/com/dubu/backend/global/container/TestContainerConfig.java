package com.dubu.backend.global.container;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainerConfig {
    private static final int REDIS_PORT = 6379;

    static MySQLContainer<?> mysql =  new MySQLContainer<>(
            DockerImageName.parse("ubuntu/mysql:edge").asCompatibleSubstituteFor("mysql"))
            .withCommand("--innodb_print_all_deadlocks=ON",
                    "--general-log=ON",
                    "--general-log-file=/var/lib/mysql/general.log");


    static RedisContainer redis = new RedisContainer(
            DockerImageName.parse("redis:7.2-alpine")
    ).withExposedPorts(REDIS_PORT);

    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    static{
        mysql.start();
        redis.start();
        rabbitmq.start();
    }

    @BeforeAll
    static void beforeAll(){
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(REDIS_PORT)));
        System.setProperty("spring.rabbitmq.host", rabbitmq.getHost());
        System.setProperty("spring.rabbitmq.port", rabbitmq.getAmqpPort().toString());
        System.setProperty("spring.rabbitmq.username", rabbitmq.getAdminUsername());
        System.setProperty("spring.rabbitmq.password", rabbitmq.getAdminPassword());
    }
}
