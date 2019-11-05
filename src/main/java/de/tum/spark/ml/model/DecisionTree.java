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
    @Field("ModelName")
    private String modelName;

    @Field("FeatureExtraction")
    private FeatureExtractionDto featureExtraction;
    @NotNull
    @Field("TrainModelParameters")
    private DTTrainModelDto trainModel;


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
        this.setTrainModel(DTTrainModelDto);
        this.setSaveModel(saveModelDto);
    }

    public DecisionTree(Map<String, Object> decisionTreeData) {

        if (decisionTreeData.get("modelName").toString() == null || decisionTreeData.get("modelName").toString()  == "") {
            this.modelName = "_newModel";
        } else {
            this.modelName = decisionTreeData.get("modelName").toString() ;
        }

        System.out.println(decisionTreeData.get("featureExtraction"));
        LinkedHashMap<String, Object> sourceData = (LinkedHashMap) decisionTreeData.get("featureExtraction");
        this.setFeatureExtraction(new FeatureExtractionDto(sourceData));
        this.setTrainModel(new DTTrainModelDto((LinkedHashMap) decisionTreeData.get("trainModel")));
        this.setSaveModel(new SaveModelDto((LinkedHashMap<String, String>) decisionTreeData.get("saveModel")));
    }


}
