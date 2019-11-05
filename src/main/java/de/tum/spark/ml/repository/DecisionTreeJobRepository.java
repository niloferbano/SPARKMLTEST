package de.tum.spark.ml.repository;

import de.tum.spark.ml.model.DecisionTree;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DecisionTreeJobRepository extends MongoRepository<DecisionTree, String> {
    public DecisionTree findByModelName(String name);
}
