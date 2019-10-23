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
    private String id;
    @Column(unique = true)
    @Field("ModelName")
    private String modelName;

    @NotNull
    @Field("FeatureExtraction")
    private FeatureExtractionDto featureExtraction;



    @NotNull
    @Field("KMeansTrainModel")
    private KMeansTrainModelDto kMeansTrainModel;



    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModel;


    private KMeansClustering() {
    }

    public KMeansClustering(KMeansClusteringData kMeansClusteringData) {
        if (kMeansClusteringData.getModelName() == null || kMeansClusteringData.getModelName() == "") {
            this.setModelName( "_newModel");
        } else {
            this.setModelName(kMeansClusteringData.getModelName());
        }

        this.setFeatureExtraction(kMeansClusteringData.getFeatureExtraction());
        this.setSaveModel(kMeansClusteringData.getSaveModel());
        this.setKMeansTrainModel(kMeansClusteringData.getTrainModel());
    }
}
