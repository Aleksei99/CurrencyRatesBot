package com.smuraha.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

}
