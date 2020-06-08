package de.tum.spark.ml.modules;

import com.squareup.javapoet.*;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.FeatureExtractionDto;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class FeatureExtraction {

    ArrayList<String> colto = new ArrayList<>();

    /**
     * Generates code for feature extraction step
     * This code generation works on any data file as source
     * @param inputOutputMapper
     * @param featureExtractionDto
     * @param javaCodeGenerator
     * @return
     */
    public static InputOutputMapper getJavaCode(InputOutputMapper inputOutputMapper, FeatureExtractionDto featureExtractionDto, JavaCodeGenerator javaCodeGenerator) {


        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );

        ClassName vectorAssembler = ClassName.get("org.apache.spark.ml.feature", "VectorAssembler");
        ParameterizedTypeName arrayOfString = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(String.class));

        CodeBlock.Builder code = CodeBlock.builder();
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        codeVariables.put("sourceType", datasetRow);
        codeVariables.put("variable", JavaCodeGenerator.newVariableName());
        codeVariables.put("constant", "\"labelCol\"");
        codeVariables.put("featuresdfVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("vectorAssembler", vectorAssembler);
        codeVariables.put("inputDataVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("vectorAssemblerVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("sparkSession", inputOutputMapper.getVariableName());
        codeVariables.put("filePath", featureExtractionDto.getFilePath());
        codeVariables.put("filePathVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("labelColName", featureExtractionDto.getLabelCol() );
        codeVariables.put("labelColNameVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("removeCol", JavaCodeGenerator.newVariableName());
        codeVariables.put("columnsToRemove", featureExtractionDto.getColWithString());
        codeVariables.put("arrayOfString", arrayOfString);


        code.addNamed("$sourceType:T $variable:L = $sparkSession:L.read()" +
                ".option(\"header\", false)" +
                ".option(\"inferSchema\", true).csv($filePathVariable:L);\n", codeVariables);

        //Machine learning algorithm can not be applied to string value.remove the string columns.
        if (featureExtractionDto.getColWithString() != null) {
            code.beginControlFlow("for(String col: $L)", codeVariables.get("removeCol"));
            code.addStatement("$L = $L.drop(col)", codeVariables.get("variable"), codeVariables.get("variable"));
            code.endControlFlow();
        }

        code.beginControlFlow("for(String c:  $L.columns())", codeVariables.get("variable"));
        if(javaCodeGenerator.getClassName().contains("KMeans")) {
            code.beginControlFlow("if(c.equals($S))", codeVariables.get("labelColName"));
            code.addStatement("continue");
            code.endControlFlow();
        }
        code.addStatement("$L = $L.withColumn(c, $L.col(c).cast(\"double\"))", codeVariables.get("variable"), codeVariables.get("variable"), codeVariables.get("variable"));
        code.endControlFlow();
        code.addNamed("$variable:L = $variable:L.withColumnRenamed($labelColNameVariable:L, $constant:L);\n", codeVariables);

        //DecisionTree needs labels starting at 0; subtract 1
        if(javaCodeGenerator.getClassName().contains("DecisionTree")) {
            code.addNamed("$variable:L = $variable:L.withColumn(\"labelCol\", $variable:L.col($constant:L).minus(1));\n", codeVariables);
        }


        code.addNamed("$sourceType:T $featuresdfVariable:L = $variable:L.drop($constant:L);\n", codeVariables);
        code.addNamed("$vectorAssembler:T $vectorAssemblerVariable:L = new $vectorAssembler:T()" +
                ".setInputCols($featuresdfVariable:L.columns())" +
                ".setOutputCol(\"features\");\n", codeVariables);
        code.addNamed("$sourceType:T $inputDataVariable:L = $vectorAssemblerVariable:L.transform($variable:L);\n", codeVariables);
        code.addNamed("return $inputDataVariable:L;\n", codeVariables);




        if (featureExtractionDto.getColWithString() != null) {
            ParameterizedTypeName listOfString = ParameterizedTypeName.get(
                    ClassName.get("java.util","List"),ClassName.get(String.class)
            );

            MethodSpec  featureExtractionMethod = MethodSpec.methodBuilder("featureExtraction")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(inputOutputMapper.getVariableTypeName(), inputOutputMapper.getVariableName())
                    .addParameter(String.class, codeVariables.get("filePathVariable").toString())
                    .addParameter(String.class, codeVariables.get("labelColNameVariable").toString())
                    .addParameter(listOfString, codeVariables.get("removeCol").toString())
                    .addCode(code.build())
                    .returns(datasetRow)
                    .build();

            javaCodeGenerator.addMethod(featureExtractionMethod);

            javaCodeGenerator.getMainMethod().addNamedCode("$arrayOfString:T $removeCol:L = new $arrayOfString:T();\n", codeVariables);
            for( String col: featureExtractionDto.getColWithString()) {
                codeVariables.put("col", col);
                javaCodeGenerator.getMainMethod().addNamedCode("$removeCol:L.add($col:S);\n", codeVariables);
            }

            javaCodeGenerator.getMainMethod()
                    .addNamedCode("$sourceType:T $inputDataVariable:L = featureExtraction($sparkSession:L, $filePath:S, $labelColName:S, $removeCol:L);\n", codeVariables);

        } else {
            MethodSpec  featureExtractionMethod = MethodSpec.methodBuilder("featureExtraction")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(inputOutputMapper.getVariableTypeName(), inputOutputMapper.getVariableName())
                    .addParameter(String.class, codeVariables.get("filePathVariable").toString())
                    .addParameter(String.class, codeVariables.get("labelColNameVariable").toString())
                    .addCode(code.build())
                    .returns(datasetRow)
                    .build();
            javaCodeGenerator.addMethod(featureExtractionMethod);
            javaCodeGenerator.getMainMethod()
                    .addNamedCode("$sourceType:T $inputDataVariable:L = featureExtraction($sparkSession:L, $filePath:S, $labelColName:S);\n", codeVariables);
        }


        return new InputOutputMapper(datasetRow, codeVariables.get("inputDataVariable").toString());
    }
}
