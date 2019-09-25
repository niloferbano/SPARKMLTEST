package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureExtractionMapper {

    private String filePath;
    private String labledCol;

    @Override
    public String toString() {
        return "filepath: " + filePath + " labledCol: " + labledCol;
    }
}
