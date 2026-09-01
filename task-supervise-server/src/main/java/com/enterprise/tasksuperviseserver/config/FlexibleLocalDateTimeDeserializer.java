package com.enterprise.tasksuperviseserver.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 灵活的 LocalDateTime 反序列化器
 * 支持多种日期格式：yyyy-MM-dd、yyyy-MM-dd HH:mm:ss、ISO 8601 等
 *
 * @author grq
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FULL_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter ISO_LOCAL = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isBlank()) {
            return null;
        }

        text = text.trim();

        // 1. 尝试标准 ISO LocalDateTime（含 T 分隔符）
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException ignored) {
        }

        // 2. 尝试 yyyy-MM-dd HH:mm:ss
        try {
            return LocalDateTime.parse(text, FULL_FMT);
        } catch (DateTimeParseException ignored) {
        }

        // 3. 尝试纯日期 yyyy-MM-dd → 补 00:00:00
        try {
            LocalDate date = LocalDate.parse(text, ISO_LOCAL);
            return LocalDateTime.of(date, LocalTime.MIN);
        } catch (DateTimeParseException ignored) {
        }

        // 4. 兜底：手动按 "-" 分割解析
        try {
            String[] parts = text.split("-");
            if (parts.length >= 3) {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2].substring(0, 2));
                return LocalDateTime.of(year, month, day, 0, 0, 0);
            }
        } catch (Exception ignored) {
        }

        throw new IOException("无法解析日期时间: '" + text + "'，支持: yyyy-MM-dd, yyyy-MM-dd HH:mm:ss");
    }
}
