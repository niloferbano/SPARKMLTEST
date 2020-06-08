package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Setter
@Document(collection = "DecisionTree")
public class DecisionTree {

    @Id
    private String id;
    @Column(unique = true)
    @Field("JobName")
    private String jobName;

    @Field("FeatureExtraction")
    private FeatureExtractionDto featureExtraction;
    @NotNull
    @Field("TrainModel")
    private DTTrainModelDto trainModel;


    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModel;


    public DecisionTree() {
    }

}
