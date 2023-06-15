package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.service.dto.CustomCallBack;

public interface JsonMapper {
    String writeCustomCallBackAsString(CustomCallBack callBack) throws JsonProcessingException;
    CustomCallBack readCustomCallBack(String callBack) throws JsonProcessingException;
}
