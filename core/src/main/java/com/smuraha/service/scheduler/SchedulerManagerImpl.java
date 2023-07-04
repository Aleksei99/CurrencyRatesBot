package com.smuraha.service.scheduler;

import com.smuraha.model.Subscription;
import com.smuraha.repository.BankRepo;
import com.smuraha.repository.SubscriptionRepo;
import com.smuraha.service.AnswerProducer;
import com.smuraha.service.JsoupParserService;
import com.smuraha.service.util.TelegramUI;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchedulerManagerImpl implements SchedulerManager {

    private final Scheduler scheduler;

    private final BankRepo bankRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final AnswerProducer producer;
    private final TelegramUI telegramUI;
    private final JsoupParserService jsoupParserService;

    @Override
    public void startSubscriptionJob(Subscription subscription) throws SchedulerException {
        Long id = subscription.getId();
        Map<Object, Object> map = new HashMap<>();
        map.put("subscriptionRepo", subscriptionRepo);
        map.put("bankRepo", bankRepo);
        map.put("telegramUI", telegramUI);
        map.put("producer", producer);
        map.put("jsoupParserService", jsoupParserService);
        JobDetail jobDetail = JobBuilder.newJob(JobNotifyUserForBankCurrencyRate.class)
                .usingJobData("subId", id)
                .usingJobData(new JobDataMap(map))
                .withIdentity(id + "").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(id + "")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(
                        subscription.getTimeNotify().getHour(),
                        subscription.getTimeNotify().getMinute())
                ).build();
        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isStarted()) {
            scheduler.start();
        }
    }

    @Override
    public void stopSubscriptionJob(Subscription subscription) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(subscription.getId() + ""));
    }
}
