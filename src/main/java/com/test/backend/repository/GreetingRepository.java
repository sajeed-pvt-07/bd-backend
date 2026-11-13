package com.test.backend.repository;

import java.util.List;
import java.util.Optional;

import com.test.backend.entity.Greeting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GreetingRepository extends MongoRepository<Greeting, String> {

    Optional<Greeting> findFirstByUserId(String userId);

    List<Greeting> findByUserId(String userId);

    List<Greeting> findByUserIdAndActiveTrue(String userId);
}
