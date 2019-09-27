package de.tum.spark.ml.model.decisionTreeModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveModelDto {

    private String filePath;
    private String modelName;
}
