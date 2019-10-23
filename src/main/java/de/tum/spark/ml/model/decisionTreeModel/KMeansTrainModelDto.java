package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class KMeansTrainModelDto {

    @NotNull
    private Integer lowK;
    private Integer highK;
    private Integer maxIter;
    private Double seed;
    private Integer steps;
    private String initMode;
    private Boolean scaleFeature;
    private Boolean withStd;
}
