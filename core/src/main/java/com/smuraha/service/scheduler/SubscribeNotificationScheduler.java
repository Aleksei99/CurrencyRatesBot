package com.smuraha.service.scheduler;

import com.smuraha.service.AnswerProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SubscribeNotificationScheduler {

    private final AnswerProducer answerProducer;

    @Scheduled(cron = "0 0 * ? * *")
    public void notifySubscriber(){

    }
}
