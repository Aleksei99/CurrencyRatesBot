package com.smuraha.service;

import com.smuraha.model.dto.UpdateWithUserDto;

public interface UserInputService {
    String setupSubscriptionSchedule(UpdateWithUserDto updateDto);
}
