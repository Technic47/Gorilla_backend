package ru.gorilla.gim.backend.util;

import java.time.Period;

public class PeriodUtils {

    public static String buildPeriodDescription(Period period) {
        StringBuilder sb = new StringBuilder("Продление подписки на");
        if (period.getYears() > 0) {
            sb.append(" ").append(period.getYears()).append(" ").append(pluralYears(period.getYears()));
        }
        if (period.getMonths() > 0) {
            sb.append(" ").append(period.getMonths()).append(" ").append(pluralMonths(period.getMonths()));
        }
        if (period.getDays() > 0) {
            sb.append(" ").append(period.getDays()).append(" ").append(pluralDays(period.getDays()));
        }
        return sb.toString();
    }

    private static String pluralYears(int n) {
        return switch (n % 10) {
            case 1 -> "год";
            case 2, 3, 4 -> "года";
            default -> "лет";
        };
    }

    private static String pluralMonths(int n) {
        return switch (n % 10) {
            case 1 -> "месяц";
            case 2, 3, 4 -> "месяца";
            default -> "месяцев";
        };
    }

    private static String pluralDays(int n) {
        return switch (n % 10) {
            case 1 -> "день";
            case 2, 3, 4 -> "дня";
            default -> "дней";
        };
    }
}
