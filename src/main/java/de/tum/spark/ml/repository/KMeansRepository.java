package de.tum.spark.ml.repository;

import de.tum.spark.ml.model.KMeansClustering;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KMeansRepository  extends MongoRepository<KMeansClustering, String> {

    KMeansClustering findByModelName(String name);
}
