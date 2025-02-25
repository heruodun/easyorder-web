package net.lab1024.sa.base.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

//@WritingConverter
public class LocalDateTimeToLongConverter implements Converter<LocalDateTime,Long > {


    @Override
    public Long convert(LocalDateTime source) {
        if (source == null) {
            return null; // 处理空值情况
        }
        // 转换为时间戳（自1970-01-01T00:00:00Z以来的秒数）
        return source.toEpochSecond(ZoneOffset.UTC);
    }
}
