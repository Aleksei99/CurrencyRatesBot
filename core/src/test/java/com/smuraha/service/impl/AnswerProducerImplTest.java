package com.smuraha.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static com.smuraha.RabbitQueue.*;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AnswerProducerImplTest {

    @InjectMocks
    AnswerProducerImpl answerProducer;
    @Mock
    RabbitTemplate rabbitTemplate;

    @Mock
    SendMessage sendMessage;
    @Mock
    DeleteMessage deleteMessage;
    @Mock
    EditMessageText editMessageText;


    @Test
    void produce_MessageSentToAnswerQueueForSendMessage() {
        answerProducer.produce(sendMessage);
        verify(rabbitTemplate).convertAndSend(ANSWER_QUEUE, sendMessage);
    }

    @Test
    void produce_MessageSentToDeleteQueueForDeleteMessage() {
        answerProducer.produce(deleteMessage);
        verify(rabbitTemplate).convertAndSend(DELETE_QUEUE, deleteMessage);
    }

    @Test
    void produce_MessageSentToEditQueueForEditMessageText() {
        answerProducer.produce(editMessageText);
        verify(rabbitTemplate).convertAndSend(EDIT_QUEUE, editMessageText);
    }
}