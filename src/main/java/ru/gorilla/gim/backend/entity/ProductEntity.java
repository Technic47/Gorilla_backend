package ru.gorilla.gim.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Period;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProductEntity extends AbstractEntity {

    @Column(name = "description", unique = true)
    private String description;
    @Column(name = "comment")
    private String comment;
    @Column(name = "sum")
    private Long sum;
    @Column(name = "period", nullable = false)
    private Period period;
}
