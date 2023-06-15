package com.smuraha.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smuraha.service.dto.CustomCallBack;
import com.smuraha.service.enums.CallBackParams;
import com.smuraha.service.myExceptions.MyJsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.smuraha.service.enums.CallBackParams.*;
import static org.assertj.core.api.Assertions.*;
import static com.smuraha.service.enums.CallBackKeys.*;

class JsonMapperImplTest {

    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        jsonMapper = new JsonMapperImpl();
    }

    @Test
    void testFailMapCallBackWithLengthGreaterThan64() {
        Map<CallBackParams, String> params = new HashMap<>();
        params.putIfAbsent(C, "LONGG valueeeeeeeeeeeeeeeeeeeeeeeeee");
        assertThatThrownBy(() -> jsonMapper.writeCustomCallBackAsString(
                new CustomCallBack(
                        CAB,
                        params)
                )).isInstanceOf(MyJsonProcessingException.class)
                .hasMessageContaining("Длина callBack-a не может быть больше 64 байтов");

    }

    @Test
    void testSuccessMapCallBackWithLengthLessThan65() {
        Map<CallBackParams, String> params = new HashMap<>();
        params.putIfAbsent(C, "USD");
        params.put(B, "99");
        assertThatNoException().isThrownBy(() -> jsonMapper.writeCustomCallBackAsString(
                new CustomCallBack(
                        CBC,
                        params)
                ));

    }

    @Test
    void readCustomCallBack() throws JsonProcessingException {
        //expected
        Map<CallBackParams, String> params = new HashMap<>();
        params.putIfAbsent(C, "USD");
        params.put(B, "99");
        CustomCallBack customCallBack = new CustomCallBack(
                CBC,
                params);

        assertThat(jsonMapper.readCustomCallBack("{\"key\":\"CBC\",\"prms\":{\"C\":\"USD\",\"B\":\"99\"}}"))
                .isEqualTo(customCallBack);
    }
}