package de.tum.spark.ml.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class KMeansClusteringMapper {
    public static KMeansClustering mapper(Map<String, Object> mappedData) {
        KMeansClustering kMeansClustering = new KMeansClustering();
        kMeansClustering.setJobName(mappedData.get("jobName").toString());
        kMeansClustering.setFeatureExtraction(new FeatureExtractionDto((LinkedHashMap) mappedData.get("featureExtraction")));
        kMeansClustering.setTrainModel(KMeansTrainModelDtoMapper.mapper((LinkedHashMap)mappedData.get("trainModel")));
        LinkedHashMap<String, String> saveDetail = (LinkedHashMap<String, String>) mappedData.get("saveModel");
        kMeansClustering.setSaveModel(new SaveModelDto(saveDetail.get("filePath"), saveDetail.get("modelName")));
        return kMeansClustering;

    }
}
