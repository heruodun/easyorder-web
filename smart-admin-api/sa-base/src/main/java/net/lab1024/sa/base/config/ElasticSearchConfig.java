package net.lab1024.sa.base.config;


import net.lab1024.sa.base.converter.LongToLocalDateTimeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Arrays;

@Configuration
//@EnableElasticsearchRepositories(basePackages = "net.lab1024")
public class ElasticSearchConfig {

    @Bean
    public ElasticsearchCustomConversions customConversions() {
        return new ElasticsearchCustomConversions(Arrays.asList(
//                new LocalDateTimeToLongConverter(),
//                new IntegerToLocalDateTimeConverter(),
                new LongToLocalDateTimeConverter()));
    }
}

