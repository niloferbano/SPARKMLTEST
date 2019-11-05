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
    private KMeansTrainModelDto trainModel;



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
        this.setTrainModel(kMeansClusteringData.getTrainModel());
    }

    public KMeansClustering(Map<String, Object> kMeansClusteringData) {

        if (kMeansClusteringData.get("modelName").toString() == null || kMeansClusteringData.get("modelName").toString()  == "") {
            this.modelName = "_newModel";
        } else {
            this.modelName = kMeansClusteringData.get("modelName").toString() ;
        }

        System.out.println(kMeansClusteringData.get("featureExtraction"));
        LinkedHashMap<String, Object> sourceData = (LinkedHashMap) kMeansClusteringData.get("featureExtraction");
        this.setFeatureExtraction(new FeatureExtractionDto(sourceData));
        this.setTrainModel(new KMeansTrainModelDto((LinkedHashMap)kMeansClusteringData.get("trainModel")));
        this.setSaveModel(new SaveModelDto((LinkedHashMap<String, String>) kMeansClusteringData.get("saveModel")));
    }
}
