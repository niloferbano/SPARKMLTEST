package de.tum.spark.ml.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CollaborativeFilteringTrainModelDtoMapper {
    public static CollaborativeFilteringTrainModelDto mapper(LinkedHashMap<String, Object> request) {
        CollaborativeFilteringTrainModelDto collaborativeFilteringTrainModelDto = new CollaborativeFilteringTrainModelDto();

        collaborativeFilteringTrainModelDto.setRanks((ArrayList) request.get("ranks"));
        collaborativeFilteringTrainModelDto.setAlphas ((ArrayList)request.get("alphas"));

        collaborativeFilteringTrainModelDto.setRegParams((ArrayList) request.get("regParams"));
        collaborativeFilteringTrainModelDto.setImplicitPref((Boolean) request.get("implicitPref"));
        collaborativeFilteringTrainModelDto.setNumOfBlocks((Integer) request.get("numOfBlocks"));
        collaborativeFilteringTrainModelDto.setEvaluationMetric(request.get("evaluationMetric").toString());
        collaborativeFilteringTrainModelDto.setMaxIter( (Integer) request.get("maxIter"));
        collaborativeFilteringTrainModelDto.setTrainingsize( (Double) request.get("trainingsize"));
        collaborativeFilteringTrainModelDto.setTestingsize( (Double) request.get("testingsize"));
        return  collaborativeFilteringTrainModelDto;
    }
}
