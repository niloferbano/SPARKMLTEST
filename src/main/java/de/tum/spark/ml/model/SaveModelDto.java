package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveModelDto {
    private String filePath;
    private String modelName;

    public SaveModelDto(String filePath, String modelName) {
        this.filePath = filePath;
        this.modelName = modelName;
    }
}
