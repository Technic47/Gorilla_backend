package ru.gorilla.gim.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Period;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaymentEntity extends AbstractEntity {

    @Column(name = "period", nullable = false)
    private Period period;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
}
