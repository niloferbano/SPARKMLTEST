package de.tum.spark.ml.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FeatureExtractionDto {

    private String filePath;
    private String labelCol;
    private List<String> colWithString;

    public FeatureExtractionDto() {
        this.colWithString = new ArrayList<>();
    }

    public FeatureExtractionDto(Map<String, Object> sourceFilePath) {
        this.setFilePath(sourceFilePath.get("filePath").toString());
        this.setLabelCol(sourceFilePath.get("labelCol").toString());
        this.setColWithString((List) sourceFilePath.get("colWithString"));

    }

}
