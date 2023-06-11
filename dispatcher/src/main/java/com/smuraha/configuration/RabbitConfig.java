package com.smuraha.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.smuraha.RabbitQueue.*;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public String userInputQueue(){
        return USER_INPUT_QUEUE;
    }

    @Bean
    public String commandQueue(){
        return COMMAND_QUEUE;
    }
    @Bean
    public String answerQueue(){
        return ANSWER_QUEUE;
    }

}
