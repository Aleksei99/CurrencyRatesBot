package com.smuraha.service.dto;

import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Должен быть не больше 64 байтов
 */
public class CustomCallBack {

    private CallBackKeys callBackKey;

    private Map<CallBackParams,String> params;
}
