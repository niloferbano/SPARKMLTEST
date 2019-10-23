package de.tum.spark.ml.model.decisionTreeModel;

import de.tum.spark.ml.modules.SaveModel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

@Getter
@Setter
public class KMeansClusteringData {
    private String modelName;
    private FeatureExtractionDto featureExtraction;
    private KMeansTrainModelDto trainModel;
    private SaveModelDto saveModel;

}
