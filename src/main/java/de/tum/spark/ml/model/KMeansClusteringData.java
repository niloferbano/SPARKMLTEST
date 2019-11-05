package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KMeansClusteringData {
    private String modelName;
    private FeatureExtractionDto featureExtraction;
    private KMeansTrainModelDto trainModel;
    private SaveModelDto saveModel;

}
