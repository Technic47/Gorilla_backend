package ru.gorilla.gim.backend.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.charset.StandardCharsets;
import java.time.Period;

@Converter
public class PeriodConverter implements AttributeConverter<Period, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(Period period) {
        if (period == null) return null;
        return period.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Period convertToEntityAttribute(byte[] dbData) {
        if (dbData == null || dbData.length == 0) return null;

        // Превращает байты [80, 49, 77] обратно в строку "P1M"
        String rawString = new String(dbData, StandardCharsets.UTF_8);

        try {
            return Period.parse(rawString);
        } catch (Exception e) {
            // Если в байтах всё же лежит не ISO-период
            return null;
        }
    }
}
