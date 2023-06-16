package com.smuraha.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "currency_update_history")
public class CurrencyUpdateHistory extends BaseEntity{
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
