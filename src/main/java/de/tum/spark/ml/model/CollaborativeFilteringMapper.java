package de.tum.spark.ml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CollaborativeFilteringMapper {

    public static CollaborativeFiltering mapper(Map<String, Object> mappedData) {
        CollaborativeFiltering collaborativeFiltering = new CollaborativeFiltering();
        collaborativeFiltering.setJobName(mappedData.get("jobName").toString());
        LinkedHashMap<String, Object> sourceData = (LinkedHashMap) mappedData.get("featureExtraction");
        LinkedHashMap<String, Object>  sourceFile = (LinkedHashMap)sourceData.get("sourceFilePath");
        LinkedHashMap<String, String> aliasPath = (LinkedHashMap) sourceData.get("aliasFilePath");
        collaborativeFiltering.setFeatureExtraction(new FeatureExtractionFromTextFileDto(sourceFile, aliasPath));
        collaborativeFiltering.setTrainModel( CollaborativeFilteringTrainModelDtoMapper.mapper((LinkedHashMap)mappedData.get("trainModel")));
        LinkedHashMap<String, String> saveDetail = (LinkedHashMap<String, String>) mappedData.get("saveModel");
        collaborativeFiltering.setSaveModel(new SaveModelDto(saveDetail.get("filePath"), saveDetail.get("modelName")));
        return collaborativeFiltering;

    }
}
