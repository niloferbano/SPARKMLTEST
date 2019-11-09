package de.tum.spark.ml.model;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;

@Getter
@Setter
public class DTTrainModelDto {
    @NotNull
    @NotEmpty(message = "impurity should not be empty")
    private String impurity;
    private Integer depth;
    private Integer maxBins;
    private Double training_size;
    private Double test_size;

//
//    public DTTrainModelDto(LinkedHashMap<String, Object> linkedHashMap) {
//
//        this.impurity =  linkedHashMap.get("impurity").toString();
//        this.depth =  (Integer) linkedHashMap.get("depth");
//        this.maxBins =  (Integer)linkedHashMap.get("maxBins");
//        this.training_size =   (Double) linkedHashMap.get("training_size");
//        this.test_size =  (Double) linkedHashMap.get("test_size");
//    }

}
