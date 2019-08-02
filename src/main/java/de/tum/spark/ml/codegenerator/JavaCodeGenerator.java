package de.tum.spark.ml.codegenerator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;

public class JavaCodeGenerator {



    public void generateJavaSource() {
        TypeName SparkSession = ParameterizedTypeName.get(
                ClassName.get("org.apache.sql.SparkSession","SparkSession"));

        FieldSpec android = FieldSpec.builder(SparkSession, "sparkSession")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        MethodSpec constructorSpec = MethodSpec.constructorBuilder()
                .addParameter(TypeName.INT, "aircraftId", Modifier.FINAL)
                .addParameter(String.class, "departure", Modifier.FINAL)
                .addStatement("this.aircraftId = aircraftId")
                .addStatement("this.departure = departure")
                .addModifiers(Modifier.PUBLIC).build();


    }
}
