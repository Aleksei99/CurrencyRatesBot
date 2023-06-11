package com.smuraha.service.impl;

import com.smuraha.service.AnswerProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.smuraha.RabbitQueue.ANSWER_QUEUE;

@Service
@RequiredArgsConstructor
public class AnswerProducerImpl implements AnswerProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_QUEUE,sendMessage);
    }
}
