package com.smuraha.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.AppUser;
import com.smuraha.model.Bank;
import com.smuraha.model.Subscription;
import com.smuraha.model.enums.Currencies;
import com.smuraha.model.enums.UserState;
import com.smuraha.repository.AppUserRepo;
import com.smuraha.repository.BankRepo;
import com.smuraha.repository.SubscriptionRepo;
import com.smuraha.service.CallBackService;
import com.smuraha.service.ChartService;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import com.smuraha.service.scheduler.SchedulerManager;
import com.smuraha.service.util.JsonMapper;
import com.smuraha.service.util.TelegramUI;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.smuraha.service.enums.CallBackKeys.*;
import static com.smuraha.service.enums.CallBackParams.*;

@Service
@RequiredArgsConstructor
public class CallBackServiceImpl implements CallBackService {

    private final BankRepo bankRepo;
    private final TelegramUI telegramUI;
    private final JsonMapper jsonMapper;
    private final ChartService chartService;
    private final AppUserRepo userRepo;
    private final SchedulerManager schedulerManager;
    private final SubscriptionRepo subscriptionRepo;

    @Override
    public SendMessage process(Update update) throws IOException, SchedulerException {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long userTelegramId = callbackQuery.getFrom().getId();
        Long chatId = callbackQuery.getMessage().getChatId();
        CustomCallBack callBack = jsonMapper.readCustomCallBack(update.getCallbackQuery().getData());
        Map<CallBackParams, String> params = callBack.getPrms();
        switch (callBack.getKey()) {
            case CCB -> {
                return setChooseAllBankOrSpecific(params);
            }
            case CCC -> {
                return setAnswerForSelectedCurrencyForChart(params, chatId.toString());
            }
            case CAB -> {
                return setAnswerForSelectedCurrencyForAllBanks(params);
            }
            case CB -> {
                return setChooseBank(params);
            }
            case CBC -> {
                return setAnswerForSelectedCurrencyForBank(params);
            }
            case CBS -> {
                return setChooseBankForSubscription(params);
            }
            case SUB -> {
                return setUserStateWaitForTimePick(params, userTelegramId);
            }
            case UNS -> {
                return setAnswerUnsubscribeUserFromCurrency(params, userTelegramId);
            }
        }
        return null;
    }

    private SendMessage setAnswerUnsubscribeUserFromCurrency(Map<CallBackParams, String> params, Long userTelegramId) throws SchedulerException {
        AppUser user = userRepo.findByTelegramUserIdAndCurrencyWithJPQLFetch(
                userTelegramId, Currencies.valueOf(params.get(C))
        );
        Subscription subscription = user.getSubscriptions().get(0);
        schedulerManager.stopSubscriptionJob(subscription);
        subscriptionRepo.delete(subscription);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Вы успешно отписались от " + subscription.getCurrency());
        return sendMessage;
    }

    private SendMessage setUserStateWaitForTimePick(Map<CallBackParams, String> params, Long userTelegramId) {
        AppUser user = userRepo.findByTelegramUserIdWithJPQLFetch(userTelegramId);
        Long bankId = Long.valueOf(params.get(B));
        Bank bank = bankRepo.findById(bankId).get();
        Currencies currency = Currencies.valueOf(params.get(C));
        user.addSubscription(
                Subscription.builder()
                        .bank(bank)
                        .user(user)
                        .currency(currency)
                        .build()
        );
        user.setUserState(UserState.WAIT_FOR_TIME_PICK);
        userRepo.save(user);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Введите время оповещения в формате h m, например: 9 10");
        return sendMessage;
    }

    private SendMessage setChooseBankForSubscription(Map<CallBackParams, String> params) throws JsonProcessingException {
        return getChooseBankWithPager(params, SUB, CBS);
    }

    private SendMessage setChooseBank(Map<CallBackParams, String> params) throws JsonProcessingException {
        return getChooseBankWithPager(params, CBC, CB);
    }

    private SendMessage getChooseBankWithPager(Map<CallBackParams, String> params, CallBackKeys next, CallBackKeys prev) throws JsonProcessingException {
        List<List<InlineKeyboardButton>> banks_KB = new ArrayList<>();

        int page = params.containsKey(P) ? Integer.parseInt(params.get(P)) : 0;

        Page<Bank> bankPages = bankRepo.findAll(PageRequest.of(page, 5));
        int totalPages = bankPages.getTotalPages();

        List<Bank> banks = bankPages.toList();
        for (Bank bank : banks) {
            List<InlineKeyboardButton> row_banks_KB = new ArrayList<>();
            InlineKeyboardButton cell_bank_KB = new InlineKeyboardButton();
            cell_bank_KB.setText(bank.getBankName());
            params.put(B, bank.getId().toString());
            cell_bank_KB.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(next, params)
            ));
            row_banks_KB.add(cell_bank_KB);
            banks_KB.add(row_banks_KB);
        }
        List<InlineKeyboardButton> pager = telegramUI.getCustomPager(prev, params, page, totalPages);

        banks_KB.add(pager);
        return telegramUI.getMessageWithButtons(banks_KB, "Выберите Банк: ");
    }

    private SendMessage setAnswerForSelectedCurrencyForChart(Map<CallBackParams, String> params, String chatId) throws IOException {
        chartService.drawChartByCurrency(chatId, Currencies.valueOf(params.get(C)));
        return null;
    }

    private SendMessage setAnswerForSelectedCurrencyForBank(Map<CallBackParams, String> params) {
        Long bankId = Long.valueOf(params.get(B));
        Currencies currency = Currencies.valueOf(params.get(C));
        Bank bank = bankRepo.getBankByIdAndCur(bankId, currency);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        String bankFormedRates = telegramUI.getBankFormedRates(bank);
        sendMessage.setText(bankFormedRates);
        return sendMessage;
    }


    private SendMessage setChooseAllBankOrSpecific(Map<CallBackParams, String> params) throws JsonProcessingException {
        List<InlineKeyboardButton> allBanksOrBankKB = new ArrayList<>();
        InlineKeyboardButton allBanksButton = new InlineKeyboardButton();
        allBanksButton.setText("Все банки");
        allBanksButton.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                new CustomCallBack(CAB, params)
        ));
        InlineKeyboardButton chooseBankButton = new InlineKeyboardButton();
        chooseBankButton.setText("Выбрать банк");
        chooseBankButton.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                new CustomCallBack(CB, params)
        ));
        allBanksOrBankKB.add(allBanksButton);
        allBanksOrBankKB.add(chooseBankButton);
        List<List<InlineKeyboardButton>> chooseAllBankOrSpecific = new ArrayList<>();
        chooseAllBankOrSpecific.add(allBanksOrBankKB);
        return telegramUI.getMessageWithButtons(chooseAllBankOrSpecific, "Выберите: ");
    }


    private SendMessage setAnswerForSelectedCurrencyForAllBanks(Map<CallBackParams, String> params) throws JsonProcessingException {
        Currencies currency = Currencies.valueOf(params.get(C));

        List<List<InlineKeyboardButton>> banks_KB = new ArrayList<>();
        int page = params.containsKey(P) ? Integer.parseInt(params.get(P)) : 0;
        Page<Bank> bankPages = bankRepo.getBanksByCur(currency, PageRequest.of(page, 5));
        int totalPages = bankPages.getTotalPages();

        List<Bank> allBanks = bankPages.toList();

        StringBuilder builder = new StringBuilder();
        for (Bank bank : allBanks) {
            builder.append(telegramUI.getBankFormedRates(bank));
        }
        List<InlineKeyboardButton> pager = telegramUI.getCustomPager(CAB, params, page, totalPages);
        banks_KB.add(pager);
        return telegramUI.getMessageWithButtons(banks_KB, builder.toString());
    }
}
