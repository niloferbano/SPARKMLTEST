package de.tum.spark.ml.repository;

import de.tum.spark.ml.model.CollaborativeFiltering;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollaborativeFilteringRepository extends MongoRepository<CollaborativeFiltering, String> {
     CollaborativeFiltering findByModelName(String name);
}
