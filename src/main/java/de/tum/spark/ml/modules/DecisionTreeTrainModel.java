package de.tum.spark.ml.modules;

import com.squareup.javapoet.*;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.decisionTreeModel.DTTrainModelDto;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class DecisionTreeTrainModel {

    public static InputOutputMapper getJavaCode(DTTrainModelDto DTTrainModelDto, JavaCodeGenerator javaCodeGenerator, InputOutputMapper inputOutputMapper) {
        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );
        ClassName decisionTreeClassifier = ClassName.get("org.apache.spark.ml.classification",
                "DecisionTreeClassifier");
        ClassName decisionTreeClassifierModel = ClassName.get("org.apache.spark.ml.classification",
                "DecisionTreeClassificationModel");
        CodeBlock.Builder code = CodeBlock.builder();


        Map<String, Object> codeVariables = new LinkedHashMap<>();
        codeVariables.put("classifier", decisionTreeClassifier);
        codeVariables.put("variabledtc", JavaCodeGenerator.newVariableName());
        codeVariables.put("constant", "\"labelCol\"");
        codeVariables.put("variabledtcmodel", JavaCodeGenerator.newVariableName());
        codeVariables.put("classifierModel", decisionTreeClassifierModel);
        codeVariables.put("depth", DTTrainModelDto.getDepth());
        codeVariables.put("impurity", DTTrainModelDto.getImpurity());
        codeVariables.put("maxBins", DTTrainModelDto.getMaxBins());
        codeVariables.put("trainingData", inputOutputMapper.getVariableName());
        codeVariables.put("depthParam", "depth");
        codeVariables.put("impurityParam", "impurity");
        codeVariables.put("maxBinsParam", "maxBins");

        code.addNamed("$classifier:T $variabledtc:L = new $classifier:T()" +
                ".setLabelCol($constant:L)\n" +
                ".setFeaturesCol(\"features\")\n" +
                ".setMaxDepth($depthParam:L)\n" +
                ".setImpurity($impurityParam:L)\n" +
                ".setMaxBins($maxBinsParam:L);\n", codeVariables);
        code.addNamed("$classifierModel:T $variabledtcmodel:L = $variabledtc:L.fit($trainingData:L);\n", codeVariables);
        code.addNamed("return $variabledtcmodel:L;\n", codeVariables);


        MethodSpec decisionTreeModelMethod = MethodSpec.methodBuilder("decisionTree")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "impurity")
                .addParameter(TypeName.INT, "depth")
                .addParameter(TypeName.INT, "maxBins")
                .addParameter(inputOutputMapper.getVariableTypeName(), inputOutputMapper.getVariableName())
                .addCode(code.build())
                .returns(decisionTreeClassifierModel)
                .build();
        javaCodeGenerator.addMethod(decisionTreeModelMethod);
        codeVariables.put("methodName", decisionTreeModelMethod.name);
        javaCodeGenerator.getMainMethod()
                .addNamedCode("$classifierModel:T $variabledtcmodel:L = $methodName:L($impurity:S, $depth:L, $maxBins:L,$trainingData:L);\n", codeVariables);

        inputOutputMapper.setVariableTypeName(decisionTreeClassifierModel);
        inputOutputMapper.setVariableName(codeVariables.get("variabledtcmodel").toString());
        return inputOutputMapper;
    }
}
