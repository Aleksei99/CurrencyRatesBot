package com.smuraha.model.dto;

import com.smuraha.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWithUserDto {

    private Update update;
    private AppUser user;
}
