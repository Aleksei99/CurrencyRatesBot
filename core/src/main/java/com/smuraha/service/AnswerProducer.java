package com.smuraha.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerProducer {
    void produce(SendMessage sendMessage);
}
