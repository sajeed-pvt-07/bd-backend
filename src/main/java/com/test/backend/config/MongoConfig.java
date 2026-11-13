package com.test.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.test.backend.entity.SongType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.uri")
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:}")
    private String database;

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory(MongoClient mongoClient) {
        if (database == null || database.isBlank()) {
            return new SimpleMongoClientDatabaseFactory(mongoClient, new ConnectionString(mongoUri).getDatabase());
        }
        return new SimpleMongoClientDatabaseFactory(mongoClient, database);
    }

    // Custom conversions so SongType is stored as "bgm"/"cover" in MongoDB
    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = Arrays.asList(
                new SongTypeToStringConverter(),
                new StringToSongTypeConverter()
        );
        return new MongoCustomConversions(converters);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
        // Build MappingMongoConverter and register custom conversions
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MongoMappingContext mappingContext = new MongoMappingContext();
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(customConversions());
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MappingMongoConverter", e);
        }
        return new MongoTemplate(mongoDbFactory, converter);
    }

    // Converter implementations
    public static class SongTypeToStringConverter implements Converter<SongType, String> {
        @Override
        public String convert(SongType source) {
            return source == null ? null : source.getValue();
        }
    }

    public static class StringToSongTypeConverter implements Converter<String, SongType> {
        @Override
        public SongType convert(String source) {
            return SongType.fromValue(source);
        }
    }
}
