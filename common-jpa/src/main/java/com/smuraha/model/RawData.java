package com.smuraha.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.junit.platform.commons.annotation.Testable;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "raw_data")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class RawData extends BaseEntity{
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Update event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawData rawData = (RawData) o;
        return Objects.equals(event, rawData.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event);
    }
}
