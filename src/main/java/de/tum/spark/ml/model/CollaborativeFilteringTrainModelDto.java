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

}
