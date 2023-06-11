package com.smuraha.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
public class Bank extends BaseEntity{

    private String bankName;

    @OneToMany(mappedBy = "bank")
    private List<CurrencyRate> rates;
}
