package com.smuraha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smuraha.model.enums.UserState;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser extends BaseEntity {

    @Column(name = "telegram_user_id")
    private Long telegramUserId;
    @CreationTimestamp
    @Column(name = "first_login_date")
    private LocalDateTime firstLoginDate;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "username")
    private String username;

    @Column(name = "last_action_date")
    private LocalDateTime lastActionDate;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Subscription> subscriptions;

    @Enumerated(value = EnumType.STRING)
    private UserState userState;

    @Transactional
    public void addSubscription(Subscription subscription){
        this.subscriptions.add(subscription);
    }

}
