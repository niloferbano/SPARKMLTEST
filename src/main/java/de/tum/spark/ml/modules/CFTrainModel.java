package de.tum.spark.ml.modules;

import com.squareup.javapoet.*;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.CollaborativeFilteringTrainModelDto;
import org.apache.spark.ml.recommendation.ALSModel;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class CFTrainModel {

    public static InputOutputMapper getJaveCode(CollaborativeFilteringTrainModelDto collaborativeFiltering,
                                                JavaCodeGenerator javaCodeGenerator,
                                                Map<String,InputOutputMapper> inputOutputMapper,
                                                Map<String, String> sourceCols) {

        CodeBlock.Builder code = CodeBlock.builder();

        Map<String, Object> codeVariables = new LinkedHashMap<>();
        ClassName als = ClassName.get("org.apache.spark.ml.recommendation", "ALS");
        ClassName alsModel = ClassName.get("org.apache.spark.ml.recommendation", "ALSModel");


        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );
        TypeName integerType = ParameterizedTypeName.get(Integer.class);
        TypeName doubleType =  ParameterizedTypeName.get(Double.class);
        TypeName mapOfIntegerAndDouble = ParameterizedTypeName.get(ClassName.get(Map.class), integerType, doubleType);
        TypeName linkedHashMap = ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class), integerType, doubleType);
        TypeName linkedListString = ParameterizedTypeName.get(ClassName.get(LinkedList.class), ClassName.get(String.class));
        TypeName mapOfIntLinkLisStr = ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class), integerType, linkedListString);
        ClassName comparingByValue = ClassName.get("java.util.Map.Entry","comparingByValue");
        ClassName toMap = ClassName.get("java.util.stream.Collectors", "toMap");
        TypeName mapOfIntegerAlsModel = ParameterizedTypeName.get(ClassName.get(Map.class), integerType, alsModel);
        TypeName linkedHashMapOfModels = ParameterizedTypeName.get(ClassName.get(LinkedHashMap.class), integerType, alsModel);
        ClassName regressionEvaluator = ClassName.get("org.apache.spark.ml.evaluation", "RegressionEvaluator");


        codeVariables.put("datasetRow", datasetRow);
        codeVariables.put("predictions", JavaCodeGenerator.newVariableName());
        codeVariables.put("integer", integerType);
        codeVariables.put("double", doubleType);
        codeVariables.put("mapOfIntegerAndDouble", mapOfIntegerAndDouble);
        codeVariables.put("errorArray", JavaCodeGenerator.newVariableName());

        codeVariables.put("comparingByValue", comparingByValue);
        codeVariables.put("linkedHashMap", linkedHashMap);
        codeVariables.put("toMap", toMap);
        codeVariables.put("linkedHashMapOfModel", linkedHashMapOfModels);
        codeVariables.put("maxIter", collaborativeFiltering.getMaxIter());
        codeVariables.put("maxIterParam", "maxIter");


        codeVariables.put("numOfBlocks", collaborativeFiltering.getNumOfBlocks());
        codeVariables.put("numOfBlocksParam", "numOfBlocks");
        codeVariables.put("itemIdCol", sourceCols.get("itemIdCol"));
        codeVariables.put("userIdCol", sourceCols.get("userIdCol"));
        codeVariables.put("ratingCol", sourceCols.get("ratingCol"));
        codeVariables.put("regressionEvaluator", regressionEvaluator);
        codeVariables.put("regressionEvaluatorVariable", "evaluator");

        codeVariables.put("evaluationMetric", collaborativeFiltering.getEvaluationMetric());


        double trainSplit = collaborativeFiltering.getTrainingsize();
        double testSplit = collaborativeFiltering.getTestingsize();

        codeVariables.put("datasetRow", datasetRow);

        codeVariables.put("trainingData", JavaCodeGenerator.newVariableName());
        codeVariables.put("testData", JavaCodeGenerator.newVariableName());
        codeVariables.put("datasetRowType", inputOutputMapper.get("MainSourceFile").getVariableTypeName());
        codeVariables.put("mainSourceFile", inputOutputMapper.get("MainSourceFile").getVariableName());
        codeVariables.put("train", trainSplit);
        codeVariables.put("test", testSplit);
        codeVariables.put("splits", JavaCodeGenerator.newVariableName());

        javaCodeGenerator.getMainMethod()
                .addNamedCode("$datasetRow:T[] $splits:L = $mainSourceFile:L.randomSplit(new double[]{$train:L, $test:L});\n", codeVariables)
                .addNamedCode("$datasetRow:T $trainingData:L = $splits:L[0];\n", codeVariables)
                .addNamedCode("$datasetRow:T $testData:L = $splits:L[1];\n", codeVariables);


        codeVariables.put("modelMap", mapOfIntegerAlsModel);
        codeVariables.put("modelArray", JavaCodeGenerator.newVariableName());
        codeVariables.put("als", als);
        codeVariables.put("alsVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("alsModel", alsModel);
        codeVariables.put("alsModelVariable", JavaCodeGenerator.newVariableName());


        code.addNamed("$mapOfIntegerAndDouble:T $errorArray:L = new $linkedHashMap:T();\n", codeVariables);
        code.addNamed("$modelMap:T $modelArray:L = new $linkedHashMapOfModel:T();\n", codeVariables);

        codeVariables.put("mapOfIntLinkLisStr", mapOfIntLinkLisStr);
        codeVariables.put("hyperparamMap", JavaCodeGenerator.newVariableName());
        codeVariables.put("hyperparamList", JavaCodeGenerator.newVariableName());
        code.addNamed("$mapOfIntLinkLisStr:T $hyperparamMap:L = new $mapOfIntLinkLisStr:T();\n", codeVariables);
        code.addNamed("$integer:T loop = 0;\n", codeVariables);

        codeVariables.put("linkedListString", linkedListString);

        codeVariables.put("rank", "rank");

        //Integer[] testArr = new Integer[] collaborativeFiltering.getRanks();
        ArrayList<Integer> ranks = new ArrayList<>();
        ranks = (ArrayList)collaborativeFiltering.getRanks();
        ArrayList<Double> alphas = new ArrayList<>();
        alphas = (ArrayList)collaborativeFiltering.getAlphas();
        ArrayList<Double> regParamas = new ArrayList<>();
        regParamas = (ArrayList)collaborativeFiltering.getRegParams();

        codeVariables.put("alpha", "alpha");
        codeVariables.put("reg", "regParam");
        codeVariables.put("rank", "rank");
        code.addStatement("$T $L = 0", codeVariables.get("integer"), codeVariables.get("rank"));
        code.addStatement("$T $L = 0.0", codeVariables.get("double"), codeVariables.get("reg"));
        code.addStatement("$T $L = 0.0", codeVariables.get("double"), codeVariables.get("alpha"));
        code.addStatement("$T $L = $L", codeVariables.get("integer"), codeVariables.get("numOfBlocksParam"), collaborativeFiltering.getNumOfBlocks());
        codeVariables.put("implicitPref", collaborativeFiltering.getImplicitPref());
        codeVariables.put("implicitPrefParam", "implicitPref");
        code.addStatement("$T $L = $L", Boolean.class,  codeVariables.get("implicitPrefParam"), codeVariables.get("implicitPref"));

//        code.beginControlFlow("for($T $L: $L)",
//                codeVariables.get("integer"), codeVariables.get("rank"),codeVariables.get("rank"));
//        code.beginControlFlow("for($T regParam: $L)",
//                codeVariables.get("double"),codeVariables.get("reg"));
//        code.beginControlFlow("for($T alpha: $L)",
//                codeVariables.get("double"),codeVariables.get("alphas"));
        code.addNamed("$als:T $alsVariable:L = new ALS();\n", codeVariables);
        code.addNamed("$linkedListString:T $hyperparamList:L = new $linkedListString:T();\n", codeVariables);
        code.addNamed("$alsModel:T $alsModelVariable:L;\n", codeVariables);
        code.addNamed("$regressionEvaluator:T $regressionEvaluatorVariable:L = new RegressionEvaluator()\n" +
                "   .setMetricName($evaluationMetric:S)\n" +
                "   .setLabelCol($ratingCol:S)\n" +
                "   .setPredictionCol(\"prediction\");\n", codeVariables);
        code.addNamed("$double:T rmse;\n", codeVariables);
        code.addNamed("$datasetRow:T $predictions:L;\n", codeVariables);
        Integer loop = 0;
        for(Integer rank: ranks){
            for(Double alpha: alphas) {
                for(Double regParam: regParamas){
                    code.addStatement("$L = $L", codeVariables.get("rank"), rank);
                    code.addStatement("$L = $L", codeVariables.get("reg"), regParam);
                    code.addStatement("$L = $L", codeVariables.get("alpha"), alpha);
                    code.addStatement("$L = $L", "loop", loop);
                    code.addNamed("$hyperparamList:L.push(\"Rank: \" + $rank:L);\n", codeVariables);
                    code.addNamed("$hyperparamList:L.push(\"RegParams: \" + regParam);\n", codeVariables);
                    code.addNamed("$hyperparamList:L.push(\"Alpha: \" + alpha);\n", codeVariables);
                    code.addNamed(
                            "   $alsVariable:L.setMaxIter($maxIter:L)\n" +
                            "   .setAlpha($alpha:L)\n" +
                            "   .setRank($rank:L)\n" +
                            "   .setRegParam($reg:L)\n" +
                            "   .setImplicitPrefs($implicitPrefParam:L)\n" +
                            "   .setNumBlocks($numOfBlocksParam:L)\n" +
                            "   .setUserCol($userIdCol:S)\n" +
                            "   .setItemCol($itemIdCol:S)\n" +
                            "   .setRatingCol($ratingCol:S);\n", codeVariables);
                    code.addNamed("$alsModelVariable:L = $alsVariable:L.fit($trainingData:L);\n", codeVariables);
                    code.addNamed("$alsModelVariable:L.setColdStartStrategy(\"drop\");\n", codeVariables);
                    code.addNamed("$predictions:L = $alsModelVariable:L.transform($testData:L);\n", codeVariables);
                    code.addNamed("rmse = $regressionEvaluatorVariable:L.evaluate($predictions:L);\n", codeVariables);
                    code.addNamed("$errorArray:L.put(loop, rmse);\n", codeVariables);
                    code.addNamed("$modelArray:L.put(loop, $alsModelVariable:L);\n", codeVariables);
                    code.addNamed("$hyperparamMap:L.put(loop, new $linkedListString:T($hyperparamList:L));\n", codeVariables);
                    code.addNamed("$hyperparamList:L.clear();\n",codeVariables);
                    loop++;

                }
            }
         }
//        code.addNamed("$linkedListString:T $hyperparamList:L = new $linkedListString:T();\n", codeVariables);
//        code.addNamed("$hyperparamList:L.push(\"Rank: \" + $rank:L);\n", codeVariables);
//        code.addNamed("$hyperparamList:L.push(\"RegParams: \" + regParam);\n", codeVariables);
//        code.addNamed("$hyperparamList:L.push(\"Alpha: \" + alpha);\n", codeVariables);
//        code.addNamed("$als:T als = new ALS()\n" +
//                "   .setMaxIter($maxIterParam:L)\n" +
//                "   .setAlpha(alpha)\n" +
//                "   .setRank($rank:L)\n" +
//                "   .setRegParam(regParam)\n" +
//                "   .setImplicitPrefs($implicitPrefParam:L)\n" +
//                "   .setNumBlocks($numOfBlocksParam:L)\n" +
//                "   .setUserCol($userIdCol:L)\n" +
//                "   .setItemCol($itemIdCol:L)\n" +
//                "   .setRatingCol($ratingCol:L);\n", codeVariables);
//        code.addNamed("$alsModel:T $alsModelVariable:L = $alsVariable:L.fit($trainingData:L);\n", codeVariables);
//        code.addNamed("$alsModelVariable:L.setColdStartStrategy(\"drop\");\n", codeVariables);
//        code.addNamed("$datasetRow:T $predictions:L = $alsModelVariable:L.transform($testData:L);\n", codeVariables);
//        code.addNamed("$regressionEvaluator:T $regressionEvaluatorVariable:L = new RegressionEvaluator()\n" +
//                "   .setMetricName($evaluationMetric:L)\n" +
//                "   .setLabelCol($ratingCol:L)\n" +
//                "   .setPredictionCol(\"prediction\");\n", codeVariables);
//        code.addNamed("$double:T rmse = $regressionEvaluatorVariable:L.evaluate($predictions:L);\n", codeVariables);
//        code.addNamed("$errorArray:L.put(loop, rmse);\n", codeVariables);
//        code.addNamed("$modelArray:L.put(loop, $alsModelVariable:L);\n", codeVariables);
//        code.addNamed("$hyperparamMap:L.put(loop, $hyperparamList:L);\n", codeVariables);
//        code.addNamed("$hyperparamList:L.clear();\n",codeVariables);
//        code.addStatement("loop++");
//        code.endControlFlow();
//        code.endControlFlow();
//        code.endControlFlow();

        codeVariables.put("sortedArray", JavaCodeGenerator.newVariableName());

        code.addNamed("$mapOfIntegerAndDouble:T $sortedArray:L = $errorArray:L.entrySet()\n" +
                ".stream()\n" +
                ".sorted(comparingByValue())\n" +
                ".collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));\n", codeVariables);
        code.addNamed("$integer:T leastError = $sortedArray:L.entrySet().stream().findFirst().get().getKey();\n", codeVariables);
        code.addNamed("$alsModelVariable:L = $modelArray:L.get(leastError);\n", codeVariables);
        codeVariables.put("system", System.class);
        code.addNamed("$system:T.out.println(\"Least Error= \" + $errorArray:L.get(leastError) + \"HyperParameters=\" + $hyperparamMap:L.get(leastError));\n"
                , codeVariables);

        javaCodeGenerator.getMainMethod().addCode(code.build());



        InputOutputMapper returnVal = new InputOutputMapper(alsModel, codeVariables.get("alsModelVariable").toString());

        return  returnVal;




    }
}
