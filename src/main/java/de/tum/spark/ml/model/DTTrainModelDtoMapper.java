package de.tum.spark.ml.model;

import java.util.LinkedHashMap;

public class DTTrainModelDtoMapper {
    public static DTTrainModelDto mapper(LinkedHashMap<String, Object> request) {
        DTTrainModelDto dtTrainModelDto = new DTTrainModelDto();

        dtTrainModelDto.setImpurity(request.get("impurity").toString());
        dtTrainModelDto.setDepth((Integer) request.get("depth"));
        dtTrainModelDto.setMaxBins((Integer)request.get("maxBins"));
        dtTrainModelDto.setTraining_size((Double) request.get("training_size"));
        dtTrainModelDto.setTest_size((Double) request.get("test_size"));
        return dtTrainModelDto;
    }
}
