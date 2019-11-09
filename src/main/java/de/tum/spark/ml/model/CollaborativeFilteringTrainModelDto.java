package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
@Setter
public class CollaborativeFilteringTrainModelDto {

    private ArrayList<Integer> ranks;
    private ArrayList<Double> alphas;
    private ArrayList<Double> regParams;
    private Boolean implicitPref;
    private Integer numOfBlocks;
    private String evaluationMetric;
    private Integer maxIter;
    private Double trainingsize;
    private Double testingsize;

//    public CollaborativeFilteringTrainModelDto(LinkedHashMap<String, Object> linkedHashMap) {
//        this.alphas = new ArrayList<Double>();
//
//        this.ranks = (ArrayList) linkedHashMap.get("ranks");
//        ArrayList<Double> alphas_json = (ArrayList) linkedHashMap.get("alphas");
//        for( Double alpha: alphas_json) {
//            this.alphas.add(new Double(alpha));
//        }
//        this.regParams = (ArrayList) linkedHashMap.get("regParams");
//        this.implicitPref =  (Boolean) linkedHashMap.get("implicitPref");
//        this.numOfBlocks = (Integer) linkedHashMap.get("numOfBlocks");
//        this.evaluationMetric = linkedHashMap.get("evaluationMetric").toString();
//        this.maxIter = (Integer) linkedHashMap.get("maxIter");
//        this.trainingsize = (Double) linkedHashMap.get("trainingsize");
//        this.testingsize = (Double) linkedHashMap.get("testingsize");
//    }
}