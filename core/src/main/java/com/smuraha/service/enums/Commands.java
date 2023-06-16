package com.smuraha.service.enums;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

@Getter
public enum Commands {
    UPDATE("/update"),
    RATES("/rates"),
    HELP("/help"),
    START("/start"),
    RATES_STAT("/rates_stat"),
    SUBSCRIBE("/subscribe"),
    UNSUBSCRIBE("/unsubscribe")
    ;

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public static String getBotName(){
        return System.getenv("BOT_NAME");
    }

    public static Commands getCommand(String command) {
        return Arrays.stream(Commands.values()).filter(com ->
                command.equals(com.command) || command.equals(com.command+"@"+ getBotName())).findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Эта команда не найдена!"));
    }
}
