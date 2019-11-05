package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
public class SaveModelDto {
    private String filePath;
    private String modelName;

    public SaveModelDto(LinkedHashMap<String, String> linkedHashMap) {
        this.filePath = linkedHashMap.get("filePath");
        this.modelName = linkedHashMap.get("modelName");

    }
}
