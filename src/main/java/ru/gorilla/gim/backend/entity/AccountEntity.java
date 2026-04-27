package ru.gorilla.gim.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends AbstractEntity {

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "second_name")
    private String secondName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "card_number")
    private String cardNumber;
    @Column(name = "is_blocked")
    private Boolean isBlocked;
    @Column(name = "paid_until")
    private LocalDateTime paidUntil;
}
