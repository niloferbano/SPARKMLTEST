package de.tum.spark.ml.model;

import de.tum.spark.ml.modules.KMeansTrainModel;

import java.util.LinkedHashMap;

public class KMeansTrainModelDtoMapper {
    public static KMeansTrainModelDto mapper(LinkedHashMap<String, Object> request) {
        KMeansTrainModelDto kMeansTrainModelDto = new KMeansTrainModelDto();
        kMeansTrainModelDto.setLowK((Integer) request.get("lowK"));
        kMeansTrainModelDto.setHighK((Integer) request.get("highK"));
        kMeansTrainModelDto.setMaxIter((Integer)request.get("maxIter"));
        kMeansTrainModelDto.setSteps((Integer)request.get("steps"));
        kMeansTrainModelDto.setDistanceThreshold((Double) request.get("distanceThreshold"));
        kMeansTrainModelDto.setInitMode( request.get("initMode").toString());
        kMeansTrainModelDto.setScaleFeature((Boolean) request.get("scaleFeature"));
        kMeansTrainModelDto.setWithStd((Boolean) request.get("withStd"));
        return kMeansTrainModelDto;
    }
}
