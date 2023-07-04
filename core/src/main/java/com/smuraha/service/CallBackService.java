package com.smuraha.service;

import org.quartz.SchedulerException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface CallBackService {
    SendMessage process(Update update) throws IOException, SchedulerException;
}
