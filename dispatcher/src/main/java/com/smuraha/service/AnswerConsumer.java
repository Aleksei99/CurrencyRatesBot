package com.smuraha.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface AnswerConsumer {
    void consumeSendMessage(SendMessage sendMessage);

    void consumeDeleteMessage(DeleteMessage deleteMessage);

    void consumeEditMessage(EditMessageText editMessageText);
}
