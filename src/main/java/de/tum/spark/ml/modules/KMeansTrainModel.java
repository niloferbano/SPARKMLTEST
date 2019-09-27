package de.tum.spark.ml.modules;

import com.squareup.javapoet.*;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.decisionTreeModel.KMeansTrainModelDto;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public class KMeansTrainModel {

    public static InputOutputMapper getJaveCode(KMeansTrainModelDto kMeansTrainModelDto,
                                                JavaCodeGenerator javaCodeGenerator,
                                                InputOutputMapper inputOutputMapper) {
        System.out.println("*************************Starting KMeans calculations*********************************");
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        ClassName kmeans = ClassName.get("org.apache.spark.ml.clustering", "KMeans");
        ClassName kmeansModel = ClassName.get("org.apache.spark.ml.clustering", "KMeansModel");

        codeVariables.put("kmeans", kmeans);
        codeVariables.put("kmeansVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("kmeansModel", kmeansModel);
        codeVariables.put("kmeansModelVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("lowK", kMeansTrainModelDto.getLowK());
        codeVariables.put("lowKParama", "lowK");
        codeVariables.put("highK", kMeansTrainModelDto.getHighK());
        codeVariables.put("highKParam", "highK");
        codeVariables.put("initMode", kMeansTrainModelDto.getInitMode());
        codeVariables.put("initModeParam", "initMode");
        codeVariables.put("maxIter", kMeansTrainModelDto.getMaxIter());
        codeVariables.put("maxIterParam", "maxIter");
        codeVariables.put("inputData", inputOutputMapper.getVariableName());
        codeVariables.put("WSSSE", JavaCodeGenerator.newVariableName());
        codeVariables.put("stepsParam", "step");
        codeVariables.put("steps", kMeansTrainModelDto.getSteps());
        codeVariables.put("seedParam", "seed");

        CodeBlock.Builder code = CodeBlock.builder();
        //If user has sends only on K value
        if (kMeansTrainModelDto.getHighK() == null) {
            code.addNamed("$kmeans:T $kmeansVariable:L = new KMeans().setFeaturesCol(\"features\")", codeVariables);
            code.addNamed(".setK($lowKParama:L)", codeVariables);
            if (kMeansTrainModelDto.getInitMode() != null) {
                code.addNamed(".setInitMode($initModeParam:L)", codeVariables);
            }
            if (kMeansTrainModelDto.getSeed() != null) {
                code.addNamed(".setSeed($seedParam:L)", codeVariables);
            }
            if (kMeansTrainModelDto.getMaxIter() != null) {
                code.addNamed(".setInitMode($maxIterParam:L);\n", codeVariables);
            }

            code.addNamed("$kmeansModel:T $kmeansModelVariable:L = $kmeansVariable:L.fit($inputData:L);\n", codeVariables);
            code.addNamed("double $WSSSE:L = $kmeansModelVariable:L.computeCost($inputData:L);\n", codeVariables);
            code.addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f", codeVariables.get("WSSSE"));
        }
        //If user sends low and high K values
        else {
            TypeName integerType = ParameterizedTypeName.get(ClassName.get(Integer.class));
            TypeName doubleType =  ParameterizedTypeName.get(ClassName.get(Double.class));
            TypeName mapOfIntegerAndDouble = ParameterizedTypeName.get(ClassName.get(Map.class), integerType, doubleType);
            TypeName linkedHashMap = ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class), integerType, doubleType);
            TypeName comparingByValue = ParameterizedTypeName.get(ClassName.get("java.util.Map.Entry","comparingByValue"));
            TypeName toMap = ParameterizedTypeName.get(ClassName.get("java.util.stream.Collectors", "toMap"));


            codeVariables.put("Integer", integerType);
            codeVariables.put("Double", doubleType);
            codeVariables.put("Map", mapOfIntegerAndDouble);
            codeVariables.put("WSSEArray", JavaCodeGenerator.newVariableName());
            codeVariables.put("sortedArray", JavaCodeGenerator.newVariableName());
            codeVariables.put("comparingByValue", comparingByValue);
            codeVariables.put("LinkedHashMap", linkedHashMap);
            codeVariables.put("toMap", toMap);

            code.addNamed("$Map:T $WSSEArray:L = new $LinkedHashMap:T<>();\n", codeVariables);

            code.beginControlFlow("for(int iter = $lowKParam:L; iter <= $highKParam; iter +=$stepsParam:L)", codeVariables);

            code.addNamed("$kmeans:T $kmeansVariable:L = new KMeans().setFeaturesCol(\"features\")", codeVariables);
            code.addNamed(".setK(iter)", codeVariables);
            if (kMeansTrainModelDto.getInitMode() != null) {
                code.addNamed(".setInitMode($initModeParam:L)", codeVariables);
            }
            if (kMeansTrainModelDto.getMaxIter() != null) {
                code.addNamed(".setInitMode($maxIterParam:L);\n", codeVariables);
            }

            code.addNamed("$kmeansModel:T $kmeansModelVariable:L = $kmeansVariable:L.fit($inputData:L);\n", codeVariables);
            code.addNamed("$Double:T $WSSSE:L = $kmeansModelVariable:L.computeCost($inputData:L);\n", codeVariables);
            code.addNamed("$WSSEArray:L.put(iter, $WSSSE:L)", codeVariables);
            code.addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f", codeVariables.get("WSSSE"));
            code.endControlFlow();
            code.addNamed("$Map:T $sortedArray:L = new $LinkedHashMap:T<>()", codeVariables);
            code.addNamed("$Map:T $sortedArray:L = $WSSEArray:L.entrySet()" +
                    ".stream()" +
                    ".sorted($comparingByValue:T())" +
                    ".collect($toMap:T(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,\n" +
                    "                                LinkedHashMap::new))", codeVariables);
        }

        MethodSpec kMeansClusteringMethod = MethodSpec.methodBuilder("kMeansClustering")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "initMode")
                .addParameter(TypeName.INT, "lowK")
                .addParameter(TypeName.INT, "highK")
                .addParameter(TypeName.INT, "maxIter")
                .addParameter(TypeName.INT, "seed")
                .addParameter(TypeName.INT, "step")
                .addParameter(inputOutputMapper.getVariableTypeName(), inputOutputMapper.getVariableName())
                .addCode(code.build())
                .returns(kmeansModel)
                .build();

        javaCodeGenerator.addMethod(kMeansClusteringMethod);
        codeVariables.put("methodName", kMeansClusteringMethod.name);

        javaCodeGenerator.getMainMethod().addNamedCode("$kmeansModel:T $kmeansVariable:L = " +
                "$methodName:L($initMode:L, $lowK:L, $highK:L, $maxIter:L, $seed:L, $step:L, $inputData:L);\n", codeVariables);

        inputOutputMapper.setVariableTypeName(kmeansModel);
        inputOutputMapper.setVariableName(codeVariables.get("kmeansModelVariable").toString());
        return inputOutputMapper;
    }
}

