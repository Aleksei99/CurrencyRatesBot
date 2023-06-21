package com.smuraha.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.smuraha.model.enums.Currencies;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscription")
public class Subscription extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;
    @Enumerated(value = EnumType.STRING)
    private Currencies currency;

    @Column(name = "timenotify")
    private LocalTime timeNotify;
}
