package com.institut.sysmat.desktop.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }
    
    public static String formatTime(LocalDateTime datetime) {
        return datetime != null ? datetime.format(TIME_FORMATTER) : "";
    }
    
    public static String formatDateTime(LocalDateTime datetime) {
        return datetime != null ? datetime.format(DATETIME_FORMATTER) : "";
    }
    
    public static String formatISODateTime(LocalDateTime datetime) {
        return datetime != null ? datetime.format(ISO_FORMATTER) : "";
    }
    
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    public static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    public static LocalDateTime parseISODateTime(String isoString) {
        try {
            return LocalDateTime.parse(isoString, ISO_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6; // 6 = Saturday, 7 = Sunday
    }
    
    public static boolean isWorkingHours(LocalDateTime datetime) {
        int hour = datetime.getHour();
        return hour >= 8 && hour <= 18; // 8h-18h
    }
    
    public static LocalDateTime addWorkingDays(LocalDateTime startDate, int days) {
        LocalDateTime result = startDate;
        int addedDays = 0;
        
        while (addedDays < days) {
            result = result.plusDays(1);
            if (!isWeekend(result.toLocalDate())) {
                addedDays++;
            }
        }
        
        return result;
    }
}