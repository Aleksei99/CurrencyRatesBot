package com.smuraha.service.scheduler;

import com.smuraha.model.Bank;
import com.smuraha.model.Subscription;
import com.smuraha.model.enums.Currencies;
import com.smuraha.repository.BankRepo;
import com.smuraha.repository.SubscriptionRepo;
import com.smuraha.service.AnswerProducer;
import com.smuraha.service.util.TelegramUI;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@RequiredArgsConstructor
@Service
public class JobNotifyUserForBankCurrencyRate implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        long subId = context.getJobDetail().getJobDataMap().getLong("subId");
        SubscriptionRepo subscriptionRepo = (SubscriptionRepo) context.getMergedJobDataMap().get("subscriptionRepo");
        BankRepo bankRepo = (BankRepo) context.getMergedJobDataMap().get("bankRepo");
        TelegramUI telegramUI = (TelegramUI) context.getMergedJobDataMap().get("telegramUI");
        AnswerProducer producer = (AnswerProducer) context.getMergedJobDataMap().get("producer");

        Subscription subscription = subscriptionRepo.findById(subId).get();
        Long bankId = subscription.getBank().getId();
        Currencies currency = subscription.getCurrency();
        Bank freshCurrencyData = bankRepo.getBankByIdAndCur(bankId, currency);
        String bankFormedRatesAnswer = telegramUI.getBankFormedRates(freshCurrencyData);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setChatId(subscription.getUser().getTelegramUserId());
        sendMessage.setText(bankFormedRatesAnswer);
        producer.produce(sendMessage);
    }

}

