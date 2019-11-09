package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;

@Getter
@Setter
public class KMeansTrainModelDto {

    @NotNull
    private Integer lowK;
    private Integer highK;
    private Integer maxIter;
    private Integer steps;
    private String initMode;
    private Boolean scaleFeature;
    private Boolean withStd;
    private Double distanceThreshold;


//    public KMeansTrainModelDto(LinkedHashMap<String, Object> linkedHashMap) {
//
//        this.lowK =  (Integer) linkedHashMap.get("lowK");
//        this.highK =  (Integer) linkedHashMap.get("highK");
//        this.maxIter =  (Integer)linkedHashMap.get("maxIter");
//        this.steps =  (Integer)linkedHashMap.get("steps");
//        this.distanceThreshold = (Double) linkedHashMap.get("distanceThreshold");
//        this.initMode = linkedHashMap.get("initMode").toString();
//        this.scaleFeature = (Boolean) linkedHashMap.get("scaleFeature");
//        this.withStd = (Boolean) linkedHashMap.get("withStd");
//    }
}
