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
@Document(collection = "KMeans")
public class KMeansClustering {

    @Id
    private Long id;
    @Column(unique = true)
    @Field("ModelName")
    private String modelName;

    @Field("FeatureExtraction")
    private FeatureExtractionDto featureExtractionDto;
    @NotNull
    @Field("TrainModelParameters")
    private KMeansTrainModelDto kMeansTrainModelDto;

    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModelDto;


    private KMeansClustering() {
    }

    public KMeansClustering(String modelName, FeatureExtractionDto featureExtractionDto,
                            KMeansTrainModelDto kMeansTrainModelDto,
                            SaveModelDto saveModelDto) {
        if (modelName == null || modelName == "") {
            this.modelName = "_newModel";
        } else {
            this.modelName = modelName;
        }
        this.featureExtractionDto = featureExtractionDto;
        this.kMeansTrainModelDto = kMeansTrainModelDto;
        this.saveModelDto = saveModelDto;
    }
}
