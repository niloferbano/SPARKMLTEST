package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FeatureExtractionFromTextFileDto {

    private Map<String, Object> sourceFilePath;
    private Map<String, String> aliasFilePath;
    public FeatureExtractionFromTextFileDto(Map<String, Object> sourceFilePath, Map<String, String> aliasFilePath) {
        this.setSourceFilePath(sourceFilePath);
        this.setAliasFilePath(aliasFilePath);
    }
}
