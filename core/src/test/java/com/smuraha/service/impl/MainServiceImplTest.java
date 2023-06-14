package com.smuraha.service.impl;

import com.smuraha.service.AnswerProducer;
import com.smuraha.service.CallBackService;
import com.smuraha.service.JsoupParserService;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.util.TelegramUI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainServiceImplTest {

    @InjectMocks
    private MainServiceImpl service;
    @Mock
    private AnswerProducer answerProducer;
    @Mock
    private JsoupParserService jsoupParserService;
    @Mock
    private CallBackService callBackService;
    @Mock
    private TelegramUI telegramUI;

    @Test
    void testProcessCallback() {

    }
}