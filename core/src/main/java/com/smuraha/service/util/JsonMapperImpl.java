package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.myExceptions.MyJsonProcessingException;
import org.springframework.stereotype.Service;

@Service
public class JsonMapperImpl implements JsonMapper{
    @Override
    public String writeCustomCallBackAsString(CustomCallBack callBack) throws JsonProcessingException {
        String value = new ObjectMapper().writeValueAsString(callBack);
        if(value.length()>64)
            throw new MyJsonProcessingException("Длина callBack-a не может быть больше 64 байтов");
        return value;
    }

    @Override
    public CustomCallBack readCustomCallBack(String callBack) throws JsonProcessingException {
        return new ObjectMapper().readValue(callBack, CustomCallBack.class);
    }
}
