package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class FeatureExtractionDto {

    private String filePath;
    private String labledCol;
    private ArrayList<String> colWithString;

    @Override
    public String toString() {
        return "filepath: " + filePath + " labledCol: " + labledCol;
    }
}
