package com.smuraha.service.impl;

import com.smuraha.controller.UpdateController;
import com.smuraha.service.AnswerConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import static com.smuraha.RabbitQueue.*;

@Service
@RequiredArgsConstructor
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateController updateController;

    @Override
    @RabbitListener(queues = {ANSWER_QUEUE})
    public void consumeSendMessage(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = {DELETE_QUEUE})
    public void consumeDeleteMessage(DeleteMessage deleteMessage) {
        updateController.delete(deleteMessage);
    }

    @Override
    @RabbitListener(queues = {EDIT_QUEUE})
    public void consumeEditMessage(EditMessageText editMessageText) {
        updateController.edit(editMessageText);
    }

}
