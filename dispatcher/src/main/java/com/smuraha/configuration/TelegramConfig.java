package com.smuraha.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class TelegramConfig {

    @Value("${bot.token}")
    private String token;
    @Value("${bot.name}")
    private String name;
}
