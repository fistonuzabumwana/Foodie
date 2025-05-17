package com.example.Foodie.config; // Or your main application package

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// No need to import EnableMongoRepositories if you don't have MongoRepository interfaces

@Configuration
@EnableJpaRepositories(basePackages = "com.example.Foodie.repository")
// If you are NOT using MongoRepository interfaces for any @Document entities,
// you do not need @EnableMongoRepositories.
// The GridFsTemplate used in FileStorageServiceImpl will still be auto-configured
// by spring-boot-starter-data-mongodb.
public class PersistenceConfig {
    // You can leave this class empty if all you need is to specify the base packages.
}