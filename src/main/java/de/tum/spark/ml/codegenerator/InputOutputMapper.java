package de.tum.spark.ml.codegenerator;

import com.squareup.javapoet.TypeName;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class InputOutputMapper {

    private TypeName variableTypeName;
    private String  variableName;

    public InputOutputMapper(TypeName typeName, String variableName) {
        this.variableName = variableName;
        this.variableTypeName = typeName;
    }
}
