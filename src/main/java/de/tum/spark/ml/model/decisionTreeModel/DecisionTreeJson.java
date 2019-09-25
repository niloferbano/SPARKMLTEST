package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;


@Getter
@Setter
public class DecisionTreeJson {

    @Id
    private String id;
    @Column(unique=true)
    @Field("ModelName")
    private String modelName;

    @Field("FeatureExtraction")
    @NonNull
    private FeatureExtractionMapper featureExtractionMapper;
    @NotNull
    @Field("TrainModelParameters")
    private TrainModelMapper trainModelMapper;

    private LinkedHashMap steps = new LinkedHashMap();


    private DecisionTreeJson() {}

    public DecisionTreeJson(String modelName, FeatureExtractionMapper featureExtractionMapper,
                           TrainModelMapper trainModelMapper) {
        if(modelName == null || modelName == "") {
            this.modelName = "_newModel";
        }
        else {
            this.modelName = modelName;
        }
        this.featureExtractionMapper = featureExtractionMapper;
        this.trainModelMapper = trainModelMapper;
    }
    @Override
    public String toString() {
        return "ID: "+ this.getId()+ " features: "+ this.getFeatureExtractionMapper();
    }

}
