package com.test.backend.repository;


import com.test.backend.entity.Config;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigRepository extends MongoRepository<Config,String> {
    java.util.Optional<Config> findFirstByKeyOrderByIdDesc(String key);
    void deleteByKey(String key);

}
