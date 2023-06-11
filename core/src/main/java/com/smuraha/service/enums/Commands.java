package com.smuraha.service.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Commands {
    UPDATE("/update");

    private final String command;

    Commands(String command) {
        this.command = command;
    }

    public static Commands getCommand(String command) {
        return Arrays.stream(Commands.values()).filter(com ->
                command.equals(com.command)).findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Эта команда не найдена!"));
    }
}
