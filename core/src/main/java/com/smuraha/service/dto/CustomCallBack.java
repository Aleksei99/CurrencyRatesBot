package com.smuraha.service.dto;

import com.smuraha.service.enums.CallBackKeys;
import com.smuraha.service.enums.CallBackParams;
import lombok.*;

import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Json объект должен быть не больше 64 байтов!
 */
public class CustomCallBack {

    private CallBackKeys key;

    private Map<CallBackParams,String> prms;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomCallBack that = (CustomCallBack) o;
        return key == that.key && Objects.equals(prms, that.prms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, prms);
    }
}
