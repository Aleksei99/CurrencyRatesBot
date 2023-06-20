package com.smuraha.service;

import com.smuraha.model.AppUser;
import com.smuraha.model.enums.UserState;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface AppUserService {
    UserState getUserState(Long telegramUserId);
    AppUser findOrSaveUser(Update update);
}
