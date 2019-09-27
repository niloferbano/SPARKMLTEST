package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Document(collection = "DecisionTree")
public class DecisionTree {

    @Id
    private Long id;
    @Column(unique = true)
    @Field("ModelName")
    private String modelName;

    @Field("FeatureExtraction")
    private FeatureExtractionDto featureExtractionDto;
    @NotNull
    @Field("TrainModelParameters")
    private DTTrainModelDto DTTrainModelDto;

    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModelDto;


    private DecisionTree() {
    }

    public DecisionTree(String modelName, FeatureExtractionDto featureExtractionDto,
                        DTTrainModelDto DTTrainModelDto,
                        SaveModelDto saveModelDto) {
        if (modelName == null || modelName == "") {
            this.modelName = "_newModel";
        } else {
            this.modelName = modelName;
        }
        this.featureExtractionDto = featureExtractionDto;
        this.DTTrainModelDto = DTTrainModelDto;
        this.saveModelDto = saveModelDto;
    }


}
