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
    @Field("TrainModel")
    private KMeansTrainModelDto trainModel;

    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModel;


//    public KMeansClustering() {
//    }
//
//    public KMeansClustering(KMeansClusteringData kMeansClusteringData) {
//        if (kMeansClusteringData.getModelName() == null || kMeansClusteringData.getModelName() == "") {
//            this.setModelName( "_newModel");
//        } else {
//            this.setModelName(kMeansClusteringData.getModelName());
//        }
//
//        this.setFeatureExtraction(kMeansClusteringData.getFeatureExtraction());
//        this.setSaveModel(kMeansClusteringData.getSaveModel());
//        this.setTrainModel(kMeansClusteringData.getTrainModel());
//    }

    public KMeansClustering(Map<String, Object> kMeansClustering) {

        if (kMeansClustering.get("modelName").toString() == null
                || kMeansClustering.get("modelName").toString()  == "") {
            this.modelName = "_newModel";
        } else {
            this.modelName = kMeansClustering.get("modelName").toString() ;
        }

        this.setFeatureExtraction(new FeatureExtractionDto((LinkedHashMap) kMeansClustering.get("featureExtraction")));
        this.setTrainModel(new KMeansTrainModelDto((LinkedHashMap)kMeansClustering.get("trainModel")));
        LinkedHashMap<String, String> saveDetail = (LinkedHashMap<String, String>) kMeansClustering.get("saveModel");
        this.setSaveModel(new SaveModelDto(saveDetail.get("filePath"), saveDetail.get("modelName")));
        //this.setSaveModel(new SaveModelDto((LinkedHashMap) mappedData.get("saveModel")));

    }
}
