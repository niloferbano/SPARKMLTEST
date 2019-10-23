package de.tum.spark.ml.model.decisionTreeModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

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

}
