package com.smuraha.service.impl;

import com.smuraha.repository.AppUserRepo;
import com.smuraha.repository.BankRepo;
import com.smuraha.repository.SubscriptionRepo;
import com.smuraha.service.ChartService;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import com.smuraha.service.scheduler.SchedulerManager;
import com.smuraha.service.util.JsonMapper;
import com.smuraha.service.util.TelegramUI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.smuraha.model.enums.Currencies.USD;
import static com.smuraha.service.enums.CallBackKeys.CBC;
import static com.smuraha.service.enums.CallBackParams.B;
import static com.smuraha.service.enums.CallBackParams.C;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallBackServiceImplTest {

    @InjectMocks
    CallBackServiceImpl callBackService;

    @Mock
    BankRepo bankRepo;
    @Mock
    TelegramUI telegramUI;
    @Mock
    JsonMapper jsonMapper;
    @Mock
    ChartService chartService;
    @Mock
    AppUserRepo userRepo;
    @Mock
    SchedulerManager schedulerManager;
    @Mock
    SubscriptionRepo subscriptionRepo;
    @Mock
    CallbackQuery callbackQuery;
    @Mock
    Update update;

    @Test
    void process() throws SchedulerException, IOException {
        Map<CallBackParams, String> params = new HashMap<>();
        params.putIfAbsent(C, "USD");
        params.put(B, "99");
        when(callbackQuery.getFrom()).thenReturn(new User());
        Message message = new Message();
        Chat chat = new Chat();
        chat.setId(1L);
        message.setChat(chat);
        when(callbackQuery.getMessage()).thenReturn(message);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(jsonMapper.readCustomCallBack(any())).thenReturn(new CustomCallBack(
                CBC,
                params));
        when(telegramUI.getBankFormedRates(any())).thenReturn("Ok");

        callBackService.process(update);

        verify(bankRepo).getBankByIdAndCur(99L,USD);
    }
}