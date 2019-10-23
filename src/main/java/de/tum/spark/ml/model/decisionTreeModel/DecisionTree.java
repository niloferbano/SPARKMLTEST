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
    private String id;
    @Column(unique = true)
    @Field("ModelName")
    private String modelName;

    @Field("FeatureExtraction")
    private FeatureExtractionDto featureExtraction;
    @NotNull
    @Field("TrainModelParameters")
    private DTTrainModelDto dtTrainModel;

    private KMeansTrainModelDto kMeansTrainModelDto;

    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModel;


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
        this.setFeatureExtraction(featureExtractionDto);
        this.setDtTrainModel(DTTrainModelDto);
        this.setSaveModel(saveModelDto);
    }


}
