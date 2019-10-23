package de.tum.spark.ml.repository;

import de.tum.spark.ml.model.decisionTreeModel.KMeansClustering;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KMeansRepository  extends MongoRepository<KMeansClustering, String> {

    public KMeansClustering findByModelName(String name);
}
