package net.lab1024.sa.base.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ReadingConverter
public class LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {
    @Override
    public LocalDateTime convert(Long source) {
        return source != null
                ? LocalDateTime.ofInstant(Instant.ofEpochMilli(source), ZoneId.systemDefault())
                : null;
    }
}
