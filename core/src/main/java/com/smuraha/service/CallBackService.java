package com.smuraha.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.service.dto.CustomCallBack;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface CallBackService {
    SendMessage process(CustomCallBack callBack) throws JsonProcessingException;
}
