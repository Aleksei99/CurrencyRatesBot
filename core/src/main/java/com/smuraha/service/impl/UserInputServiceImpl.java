package com.smuraha.service.impl;

import com.smuraha.model.AppUser;
import com.smuraha.model.Subscription;
import com.smuraha.model.dto.UpdateWithUserDto;
import com.smuraha.model.enums.UserState;
import com.smuraha.repository.AppUserRepo;
import com.smuraha.repository.SubscriptionRepo;
import com.smuraha.service.UserInputService;
import com.smuraha.service.scheduler.SchedulerManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserInputServiceImpl implements UserInputService {

    private final SubscriptionRepo subscriptionRepo;
    private final AppUserRepo userRepo;
    private final SchedulerManager schedulerManager;

    @Override
    public String setupSubscriptionSchedule(UpdateWithUserDto updateDto) {
        Update update = updateDto.getUpdate();
        AppUser user = updateDto.getUser();
        String answer;
        String text = update.getMessage().getText();
        try {
            int hour = Integer.parseInt(text);
            if (hour < 0 || hour > 24) {
                throw new NumberFormatException("must be hour>=0 || hour<=24");
            }
            Subscription subscription = subscriptionRepo.findByUserAndAndTimeNotifyIsNull(user);
            subscription.setTimeNotify(LocalTime.of(hour, 0));
            subscriptionRepo.saveAndFlush(subscription);
            AppUser fetchUser = userRepo.findByTelegramUserIdWithJPQLFetch(user.getTelegramUserId());
            fetchUser.setUserState(UserState.BASIC_STATE);
            userRepo.save(fetchUser);
            schedulerManager.startSubscriptionJob(subscription);
            answer = "Ваша подписка на " + subscription.getCurrency() + " Банка " + subscription.getBank().getBankName() + " Успешно оформлена";
        } catch (NumberFormatException e) {
            answer = "Введите число от 0 до 24!";
        } catch (SchedulerException e) {
            log.error("Ошибка в scheduler");
            answer="Внутренняя ошибка сервера!";
        }
        return answer;
    }
}
