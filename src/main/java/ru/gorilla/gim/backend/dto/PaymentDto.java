package ru.gorilla.gim.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDto extends AbstractDto {

    private String description;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Long accountId;
    private ProductDto product;
}
