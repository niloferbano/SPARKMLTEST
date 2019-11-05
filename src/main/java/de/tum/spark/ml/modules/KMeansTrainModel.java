package de.tum.spark.ml.modules;

import com.squareup.javapoet.*;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.KMeansTrainModelDto;

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
        codeVariables.put("lowKParam", "lowK");
        codeVariables.put("highK", kMeansTrainModelDto.getHighK());
        codeVariables.put("highKParam", "highK");
        codeVariables.put("initMode", kMeansTrainModelDto.getInitMode());
        codeVariables.put("initModeParam", "initMode");
        codeVariables.put("maxIter", kMeansTrainModelDto.getMaxIter());
        codeVariables.put("maxIterParam", "maxIter");
        codeVariables.put("inputData", inputOutputMapper.getVariableName());
        codeVariables.put("wSSSE", JavaCodeGenerator.newVariableName());
        codeVariables.put("stepsParam", "step");
        codeVariables.put("steps", kMeansTrainModelDto.getSteps());
        codeVariables.put("seed", kMeansTrainModelDto.getSeed());
        codeVariables.put("seedParam", "seed");

        CodeBlock.Builder code = CodeBlock.builder();
        //If user has sends only on K value
        if (kMeansTrainModelDto.getHighK() == null) {
            code.add("$kmeans:T $kmeansVariable:L = new KMeans().setFeaturesCol(\"features\")", codeVariables);
            code.add(".setK($lowKParam:L)", codeVariables);
            if (kMeansTrainModelDto.getInitMode() != null) {
                code.add(".setInitMode($initModeParam:L)", codeVariables);
            }
            if (kMeansTrainModelDto.getSeed() != null) {
                code.add(".setSeed($seedParam:L)", codeVariables);
            }
            if (kMeansTrainModelDto.getMaxIter() != null) {
                code.add(".setInitMode($maxIterParam:L);\n", codeVariables);
            }

            code.add("$kmeansModel:T $kmeansModelVariable:L = $kmeansVariable:L.fit($inputData:L);\n", codeVariables);
            code.add("double $wSSSE:L = $kmeansModelVariable:L.computeCost($inputData:L);\n", codeVariables);
            code.addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f", codeVariables.get("WSSSE"));
        }
        //If user sends low and high K values
        else {
            TypeName integerType = ParameterizedTypeName.get(Integer.class);
            TypeName doubleType =  ParameterizedTypeName.get(Double.class);
            TypeName mapOfIntegerAndDouble = ParameterizedTypeName.get(ClassName.get(Map.class), integerType, doubleType);
            TypeName linkedHashMap = ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class), integerType, doubleType);
            ClassName comparingByValue = ClassName.get("java.util.Map.Entry","comparingByValue");
            ClassName toMap = ClassName.get("java.util.stream.Collectors", "toMap");
            TypeName mapOfIntegerkMeansModel = ParameterizedTypeName.get(ClassName.get(Map.class), integerType, kmeansModel);
            TypeName linkedHashMapOfModels = ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class), integerType, kmeansModel);

            codeVariables.put("integer", integerType);
            codeVariables.put("double", doubleType);
            codeVariables.put("map", mapOfIntegerAndDouble);
            codeVariables.put("wSSEArray", JavaCodeGenerator.newVariableName());
            codeVariables.put("sortedArray", JavaCodeGenerator.newVariableName());
            codeVariables.put("comparingByValue", comparingByValue);
            codeVariables.put("linkedHashMap", linkedHashMap);
            codeVariables.put("toMap", toMap);
            codeVariables.put("modelMap", mapOfIntegerkMeansModel);
            codeVariables.put("linkedHashMapOfModel", linkedHashMapOfModels);
            codeVariables.put("modelArray", JavaCodeGenerator.newVariableName());
            codeVariables.put("optimumK", JavaCodeGenerator.newVariableName());

            code.addNamed("$map:T $wSSEArray:L = new $linkedHashMap:T();\n", codeVariables);
            code.addNamed("$modelMap:T $modelArray:L = new $linkedHashMapOfModel:T();\n", codeVariables);

            code.beginControlFlow("for(int iter = $L; iter <= $L; iter +=$L)", codeVariables.get("lowKParam"),codeVariables.get("highKParam"), codeVariables.get("stepsParam"));

            code.addNamed("$kmeans:T $kmeansVariable:L = new KMeans().setFeaturesCol(\"features\")", codeVariables);
            code.addNamed(".setK(iter)", codeVariables);
            if (kMeansTrainModelDto.getInitMode() != null) {
                code.addNamed(".setInitMode($initModeParam:L)", codeVariables);
            }
            if (kMeansTrainModelDto.getMaxIter() != null) {
                code.addNamed(".setMaxIter($maxIterParam:L);\n", codeVariables);
            }

            code.addNamed("$kmeansModel:T $kmeansModelVariable:L = $kmeansVariable:L.fit($inputData:L);\n", codeVariables);
            code.addNamed("$double:T $wSSSE:L = $kmeansModelVariable:L.computeCost($inputData:L);\n", codeVariables);
            code.addNamed("$wSSEArray:L.put(iter, $wSSSE:L);\n", codeVariables);
            code.addNamed("$modelArray:L.put(iter, $kmeansModelVariable:L);\n", codeVariables);
            code.addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f", codeVariables.get("wSSSE"));
            code.endControlFlow();
            code.addNamed("$map:T $sortedArray:L = $wSSEArray:L.entrySet()\n" +
                    ".stream()\n" +
                    ".sorted($comparingByValue:T())\n" +
                    ".collect($toMap:T(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,\n" +
                    "LinkedHashMap::new));\n", codeVariables);
            code.addNamed("$integer:T $optimumK:L = $sortedArray:L.entrySet().stream().findFirst().get().getKey();\n", codeVariables);
            code.addNamed("$kmeansModel:T $kmeansModelVariable:L = $modelArray:L.get($optimumK:L);\n", codeVariables);
            code.addNamed("return $kmeansModelVariable:L;\n", codeVariables);
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
                "$methodName:L($initMode:S, $lowK:L, $highK:L, $maxIter:L, $seed:L, $steps:L, $inputData:L);\n", codeVariables);

        inputOutputMapper.setVariableTypeName(kmeansModel);
        inputOutputMapper.setVariableName(codeVariables.get("kmeansModelVariable").toString());
        return inputOutputMapper;
    }
}

