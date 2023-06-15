package com.smuraha.service;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;

public interface AnswerProducer {
    <T extends Serializable> void  produce(PartialBotApiMethod<T> sendMessage);
}
