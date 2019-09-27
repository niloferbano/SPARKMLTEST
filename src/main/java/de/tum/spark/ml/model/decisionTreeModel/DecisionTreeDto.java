package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DecisionTreeDto {

    private String modelName;
    private FeatureExtractionDto featureExtractionDto;
    private DTTrainModelDto DTTrainModelDto;
    private SaveModelDto saveModelDto;


}
