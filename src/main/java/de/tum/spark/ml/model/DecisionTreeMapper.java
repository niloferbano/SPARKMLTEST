package de.tum.spark.ml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class DecisionTreeMapper {

    public static DecisionTree mapper(Map<String, Object> decisionTreeData) {

        DecisionTree decisionTree = new DecisionTree();
        decisionTree.setJobName(decisionTreeData.get("jobName").toString());
        LinkedHashMap<String, Object> sourceData = (LinkedHashMap) decisionTreeData.get("featureExtraction");
        decisionTree.setFeatureExtraction(new FeatureExtractionDto(sourceData));
        decisionTree.setTrainModel(DTTrainModelDtoMapper.mapper ((LinkedHashMap) decisionTreeData.get("trainModel")));
        LinkedHashMap<String, String> saveDetail = (LinkedHashMap<String, String>) decisionTreeData.get("saveModel");
        decisionTree.setSaveModel(new SaveModelDto(saveDetail.get("filePath"), saveDetail.get("modelName")));
        return decisionTree;

    }
}
