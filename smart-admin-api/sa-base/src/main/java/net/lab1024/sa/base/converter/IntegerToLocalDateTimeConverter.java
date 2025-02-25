package net.lab1024.sa.base.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

//@ReadingConverter
public class IntegerToLocalDateTimeConverter implements Converter<Integer, LocalDateTime> {
    @Override
    public LocalDateTime convert(Integer source) {
        return source != null
                ? LocalDateTime.ofInstant(Instant.ofEpochSecond(source), ZoneId.of("UTC"))
                : null;
    }
}

