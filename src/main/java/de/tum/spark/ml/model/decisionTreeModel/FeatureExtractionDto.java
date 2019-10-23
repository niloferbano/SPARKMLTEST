package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FeatureExtractionDto {

    private String filePath;
    private String labledCol;
    private List<String> colWithString;

    public FeatureExtractionDto() {
        this.colWithString = new ArrayList<>();
    }

}
