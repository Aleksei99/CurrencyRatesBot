package com.smuraha.service;

import com.smuraha.model.AppUser;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {
    void produce(String queue, Update update);

    void produce(String queue, Update update, AppUser user);
}
