package com.smuraha.controller;

import com.smuraha.model.AppUser;
import com.smuraha.model.RawData;
import com.smuraha.model.enums.UserState;
import com.smuraha.service.AppUserService;
import com.smuraha.service.RawDataService;
import com.smuraha.service.UpdateProducer;
import com.smuraha.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.smuraha.RabbitQueue.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateController {

    private TelegramBot telegramBot;
    private final MessageGenerator messageGenerator;
    private final UpdateProducer updateProducer;
    private final RawDataService rawDataService;
    private final AppUserService userService;

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }
        if (update.hasMessage()) {
            distributeMessageByType(update);
        } else if (update.hasCallbackQuery()) {
            updateProducer.produce(CALLBACK_QUEUE, update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    private void distributeMessageByType(Update update) {
        Message message = update.getMessage();
        rawDataService.save(RawData.builder()
                .event(update).build());
        AppUser user = userService.findOrSaveUser(update);
        if (message.hasText()) {
            String text = message.getText();
            ///TODO реализовать обработку
            if (text.equals("/cancel")) {
                updateProducer.produce(CANCEL_QUEUE, update, user);
            } else if (user.getUserState().equals(UserState.WAIT_FOR_TIME_PICK)) {
                updateProducer.produce(USER_INPUT_QUEUE, update, user);
            } else if (text.startsWith("/")) {
                updateProducer.produce(COMMAND_QUEUE, update);
            }else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Выполните предыдущий запрос или для отмены предыдущей операции введите /cancel");
                sendMessage.setChatId(message.getChatId());
                setView(sendMessage);
            }
        } else {
            setUnsupportedMessageType(message);
        }
    }

    private void setUnsupportedMessageType(Message message) {
        if (message.getChat().getType().equals("private")) {
            SendMessage sendMessage = messageGenerator.generateSendMessageWithText(message, "Бот не поддерживает отправку файлов!");
            setView(sendMessage);
        }
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendMessage(sendMessage);
    }

    public void delete(DeleteMessage deleteMessage) {
        telegramBot.deleteMessage(deleteMessage);
    }

    public void edit(EditMessageText editMessageText) {
        telegramBot.editMessage(editMessageText);
    }
}
