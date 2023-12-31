package com.smuraha.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
@ToString
public class Bank extends BaseEntity{

    @Column(unique = true, name = "bank_name")
    private String bankName;

    @OneToMany(mappedBy = "bank",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CurrencyRate> rates;
}
