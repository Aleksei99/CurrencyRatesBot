package com.smuraha.service.impl;

import com.smuraha.model.AppUser;
import com.smuraha.model.enums.UserState;
import com.smuraha.repository.AppUserRepo;
import com.smuraha.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepo userRepo;

    @Override
    public UserState getUserState(Long telegramUserId) {
        return userRepo.findByTelegramUserId(telegramUserId).get().getUserState();
    }

    @Override
    public AppUser findOrSaveUser(Update update) {
        User from = update.getMessage().getFrom();
        AppUser appUser = userRepo.findByTelegramUserId(from.getId()).orElse(
                AppUser.builder()
                        .firstLoginDate(LocalDateTime.now())
                        .firstName(from.getFirstName())
                        .lastName(from.getLastName())
                        .telegramUserId(from.getId())
                        .username(from.getUserName())
                        .userState(UserState.BASIC_STATE)
                        .build()
        );
        appUser.setLastActionDate(LocalDateTime.now());
        return userRepo.saveAndFlush(appUser);
    }
}
