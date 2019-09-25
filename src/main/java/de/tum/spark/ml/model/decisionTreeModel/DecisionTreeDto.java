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
public class DecisionTreeDto {

    @Id
    private String id;
    @Column(unique = true)
    @Field("ModelName")
    private String modelName;

    @Field("FeatureExtraction")
    private FeatureExtractionMapper featureExtractionMapper;
    @NotNull
    @Field("TrainModelParameters")
    private TrainModelMapper trainModelMapper;

    @NotNull
    @Field("SaveModel")
    private SaveModelMapper saveModelMapper;


    private DecisionTreeDto() {
    }

    public DecisionTreeDto(String modelName, FeatureExtractionMapper featureExtractionMapper,
                           TrainModelMapper trainModelMapper,
                           SaveModelMapper saveModelMapper) {
        if (modelName == null || modelName == "") {
            this.modelName = "_newModel";
        } else {
            this.modelName = modelName;
        }
        this.featureExtractionMapper = featureExtractionMapper;
        this.trainModelMapper = trainModelMapper;
        this.saveModelMapper = saveModelMapper;
    }
//    @Override
//    public String toString() {
//        return "ID: "+ this.getId()+ " features: "+ this.getFeatureExtractionMapper();
//    }

}
