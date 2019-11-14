package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Document(collection = "CollaborativeFiltering")
@JsonPropertyOrder({ "featureExtraction", "trainModel", "saveModel", "modeName"})
public class CollaborativeFiltering {


    @Id
    private String id;
    @Column(unique = true)
    @Field("JobName")
    private String jobName;

    @NotNull
    @Field("FeatureExtraction")
    private FeatureExtractionFromTextFileDto featureExtraction;



    @NotNull
    @Field("TrainModel")
    private CollaborativeFilteringTrainModelDto trainModel;



    @NotNull
    @Field("SaveModel")
    private SaveModelDto saveModel;


    public CollaborativeFiltering() {
    }

//    public CollaborativeFiltering(String modelName, FeatureExtractionFromTextFileDto featureExtractionDto,
//                        CollaborativeFilteringTrainModelDto collaborativeFilteringTrainModelDto,
//                        SaveModelDto saveModelDto) {
//        if (modelName == null || modelName == "") {
//            this.modelName = "_newModel";
//        } else {
//            this.modelName = modelName;
//        }
//        this.setFeatureExtraction(featureExtractionDto);
//        this.setTrainModel(collaborativeFilteringTrainModelDto);
//        this.setSaveModel(saveModelDto);
//    }

//    public CollaborativeFiltering(Map<String, Object> mappedData) {
//        if (mappedData.get("modelName").toString() == null
//                || mappedData.get("modelName").toString()  == "") {
//            this.modelName = "_newModel";
//        } else {
//            this.modelName = mappedData.get("modelName").toString() ;
//        }
//
//        LinkedHashMap<String, Object> sourceData = (LinkedHashMap) mappedData.get("featureExtraction");
//        LinkedHashMap<String, Object>  sourceFile = (LinkedHashMap)sourceData.get("sourceFilePath");
//        LinkedHashMap<String, String> aliasPath = (LinkedHashMap) sourceData.get("aliasFilePath");
//        this.setFeatureExtraction(new FeatureExtractionFromTextFileDto(sourceFile, aliasPath));
//        this.setTrainModel(new CollaborativeFilteringTrainModelDto((LinkedHashMap)mappedData.get("trainModel")));
//        LinkedHashMap<String, String> saveDetail = (LinkedHashMap<String, String>) mappedData.get("saveModel");
//        this.setSaveModel(new SaveModelDto(saveDetail.get("filePath"), saveDetail.get("modelName")));
//    }
}
