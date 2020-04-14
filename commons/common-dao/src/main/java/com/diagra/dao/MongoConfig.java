package com.diagra.dao;

import com.diagra.dao.manager.BaseManager;
import com.diagra.dao.manager.BaseManagerImpl;
import com.diagra.dao.model.UserEntity;
import com.diagra.dao.model.UserRoles;
import com.google.common.collect.ImmutableList;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;
import java.util.function.Function;

@PropertySource("classpath:application-dao.properties")
@SpringBootApplication(scanBasePackages = {"com.diagra.dao"})
@EnableMongoRepositories(basePackages = {"com.diagra.dao"})
@Configuration
public class MongoConfig {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    public MongoConfig(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        for (Class<?> aClass : ImmutableList.of(UserEntity.class)) {
            IndexOperations indexOps = mongoTemplate.indexOps(aClass);
            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
            resolver.resolveIndexFor(aClass).forEach(indexOps::ensureIndex);
        }
    }

    @Bean
    public Function<String, Example<UserEntity>> matcher() {
        return s -> Example.of(new UserEntity(null, s, null, Collections.emptyList()), ExampleMatcher.matchingAny());
    }

    @Bean
    public BaseManager<String, UserEntity> baseUserManager(Function<String, Example<UserEntity>> matcher, UserRepository rep) {
//        rep.insert(new UserEntity("stasik990315@gmail.com", "stas", "$2a$10$XpQxZ4.ZrDgB0uKPAo/J0OtXsY1DZzUJI5rShCMI1f3xOJTVc3LOC", Collections.singletonList(UserRoles.USER)));
        return new BaseManagerImpl<>(rep, matcher);
    }

}
