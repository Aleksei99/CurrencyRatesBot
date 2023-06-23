package com.smuraha.service.scheduler;

import com.smuraha.model.Subscription;
import org.quartz.SchedulerException;

public interface SchedulerManager {
    void startSubscriptionJob(Subscription subscription) throws SchedulerException;
    void stopSubscriptionJob(Subscription subscription) throws SchedulerException;
}
