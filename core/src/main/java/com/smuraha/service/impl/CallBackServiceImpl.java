package com.smuraha.service.impl;

import com.smuraha.model.Bank;
import com.smuraha.model.CurrencyRate;
import com.smuraha.model.enums.Currencies;
import com.smuraha.repository.BankRepo;
import com.smuraha.service.CallBackService;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Map;

import static com.smuraha.service.enums.CallBackParams.CUR;

@Service
@RequiredArgsConstructor
public class CallBackServiceImpl implements CallBackService {

    private final BankRepo bankRepo;

    @Override
    public SendMessage process(CustomCallBack callBack) {

        Map<CallBackParams, String> params = callBack.getParams();
        switch (callBack.getCallBackKey()){
            case SET_SELECTED_CURRENCY -> {
                return setMessageBySelectedCurrency(params);
            }
        }
        return null;
    }

    private SendMessage setMessageBySelectedCurrency(Map<CallBackParams, String> params) {
        String cur = params.get(CUR);
        Currencies currency = Currencies.valueOf(cur);
        List<Bank> allBanks = bankRepo.findAllByRatesLastUpdateCur(currency);
        SendMessage sendMessage = new SendMessage();
        StringBuilder builder= new StringBuilder();
        for (Bank bank:allBanks){
            CurrencyRate rate = bank.getRates().get(0);
            //TODO подредачить
            builder.append("Банк: ").append(bank.getBankName())
                    .append("Валюта: ").append(rate.getCurrency())
                    .append("Банк покупает: ").append(rate.getRateBuy())
                    .append("Банк продаёт: ").append(rate.getRateSell())
                    .append("\n");
        }
        sendMessage.setText(builder.toString());
        return sendMessage;
    }
}
