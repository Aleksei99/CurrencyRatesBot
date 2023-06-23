package com.smuraha.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Getter
@Setter
public class DbProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
