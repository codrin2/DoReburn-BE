package com.dubu.backend.core.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class SecurityConfig {
    public SecurityConfig() {
        Security.addProvider(new BouncyCastleProvider());
    }
}