package com.smuraha.service.impl;

import com.smuraha.service.AnswerProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.io.Serializable;

import static com.smuraha.RabbitQueue.*;

@Service
@RequiredArgsConstructor
public class AnswerProducerImpl implements AnswerProducer {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public <T extends Serializable> void produce(PartialBotApiMethod<T> message) {
        if (message instanceof SendMessage) {
            rabbitTemplate.convertAndSend(ANSWER_QUEUE, message);
        } else if (message instanceof DeleteMessage) {
            rabbitTemplate.convertAndSend(DELETE_QUEUE, message);
        } else if (message instanceof EditMessageText) {
            rabbitTemplate.convertAndSend(EDIT_QUEUE, message);
        }
    }
}
