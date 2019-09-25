package de.tum.spark.ml.repository;

import de.tum.spark.ml.model.decisionTreeModel.DecisionTreeDto;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DecisionTreeJobRepository extends MongoRepository<DecisionTreeDto, String> {
    public DecisionTreeDto findByModelName(String name);
}
