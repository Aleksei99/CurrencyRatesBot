package com.smuraha.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.model.Bank;
import com.smuraha.model.enums.Currencies;
import com.smuraha.repository.BankRepo;
import com.smuraha.service.CallBackService;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import com.smuraha.service.util.JsonMapper;
import com.smuraha.service.util.TelegramUI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.smuraha.service.enums.CallBackKeys.*;
import static com.smuraha.service.enums.CallBackParams.B;
import static com.smuraha.service.enums.CallBackParams.C;

@Service
@RequiredArgsConstructor
public class CallBackServiceImpl implements CallBackService {

    private final BankRepo bankRepo;
    private final TelegramUI telegramUI;
    private final JsonMapper jsonMapper;

    @Override
    public SendMessage process(CustomCallBack callBack) throws JsonProcessingException {

        Map<CallBackParams, String> params = callBack.getPrms();
        switch (callBack.getKey()) {
            case CH_CUR -> {
                return setChooseAllBankOrSpecific(params);
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
        }
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

    private SendMessage setChooseBank(Map<CallBackParams, String> params) throws JsonProcessingException {
        List<List<InlineKeyboardButton>> banks_KB = new ArrayList<>();

        List<Bank> banks = bankRepo.findAll();
        for (Bank bank : banks) {
            List<InlineKeyboardButton> row_banks_KB = new ArrayList<>();
            InlineKeyboardButton cell_bank_KB = new InlineKeyboardButton();
            cell_bank_KB.setText(bank.getBankName());
            params.put(B, bank.getId().toString());
            cell_bank_KB.setCallbackData(jsonMapper.writeCustomCallBackAsString(
                    new CustomCallBack(CBC, params)
            ));
            row_banks_KB.add(cell_bank_KB);
            banks_KB.add(row_banks_KB);
        }
        return telegramUI.getMessageWithButtons(banks_KB, "Выберите: ");
    }

    private SendMessage setAnswerForSelectedCurrencyForAllBanks(Map<CallBackParams, String> params) {
        Currencies currency = Currencies.valueOf(params.get(C));
        List<Bank> allBanks = bankRepo.getBanksByCur(currency, PageRequest.of(1, 20));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        StringBuilder builder = new StringBuilder();
        for (Bank bank : allBanks) {
            builder.append(telegramUI.getBankFormedRates(bank));
        }
        sendMessage.setText(builder.toString());
        return sendMessage;
    }
}
