package com.smuraha.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.smuraha.RabbitQueue.*;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public Queue userInputQueue() {
        return new Queue(USER_INPUT_QUEUE);
    }

    @Bean
    public Queue commandQueue() {
        return new Queue(COMMAND_QUEUE);
    }

    @Bean
    public Queue answerQueue() {
        return new Queue(ANSWER_QUEUE);
    }

    @Bean
    public Queue callbackQueue() {
        return new Queue(CALLBACK_QUEUE);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(DELETE_QUEUE);
    }

    @Bean
    public Queue editQueue() {
        return new Queue(EDIT_QUEUE);
    }

    @Bean
    public Queue cancelQueue() {
        return new Queue(CANCEL_QUEUE);
    }

}
