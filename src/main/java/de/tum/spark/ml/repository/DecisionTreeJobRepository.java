package de.tum.spark.ml.repository;

import de.tum.spark.ml.model.decisionTreeModel.DecisionTree;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DecisionTreeJobRepository extends MongoRepository<DecisionTree, Long> {
    public DecisionTree findByModelName(String name);
}
