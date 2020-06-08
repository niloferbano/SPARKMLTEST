package de.tum.spark.ml.modules;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.CollaborativeFilteringTrainModelDto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class CFTrainModel {
    /**
     * Generates the code for creating collaborative filtering recommender system.
     * The machine learning model is generated using alternative least square algorithm
     * to learn the latent factors
     * @param collaborativeFiltering
     * ML model parameters
     * @param javaCodeGenerator
     * JavaPoet generated code skeleton
     * @param inputOutputMapper
     * Object containing parameter name and type
     * @param sourceCols
     * Input data column names
     * @return inputOutputMapperRet
     */

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





        codeVariables.put("alphas", JavaCodeGenerator.newVariableName());
        codeVariables.put("regParams", JavaCodeGenerator.newVariableName());
        codeVariables.put("ranks", JavaCodeGenerator.newVariableName());
        code.addStatement("$T $L = $L", codeVariables.get("integer"), codeVariables.get("numOfBlocksParam"), collaborativeFiltering.getNumOfBlocks());
        codeVariables.put("implicitPref", collaborativeFiltering.getImplicitPref());
        codeVariables.put("implicitPrefParam", "implicitPref");
        ParameterizedTypeName arrayOfInteger = ParameterizedTypeName.get(ClassName.get(ArrayList.class), integerType);
        codeVariables.put("arrayOfInteger", arrayOfInteger);
        code.addNamed("$arrayOfInteger:T $ranks:L = new $arrayOfInteger:T();\n", codeVariables);

        ArrayList<Integer> ranks = new ArrayList<>();
        ranks = (ArrayList<Integer>)collaborativeFiltering.getRanks();
        for(Integer rank: ranks) {
            codeVariables.put("rank", rank);
            code.addNamed("$ranks:L.add($rank:L);\n", codeVariables);
        }

        ParameterizedTypeName arrayOfDouble = ParameterizedTypeName.get(ClassName.get(ArrayList.class), doubleType);
        codeVariables.put("arrayOfDouble", arrayOfDouble);

        code.addNamed("$arrayOfDouble:T $alphas:L = new $arrayOfDouble:T();\n", codeVariables);

        ArrayList<Double> alphas = new ArrayList<>();
        alphas = (ArrayList<Double>)collaborativeFiltering.getAlphas();
        for(Double alpha: alphas) {
            codeVariables.put("alpha", alpha);
            code.addNamed("$alphas:L.add($alpha:L);\n", codeVariables);
        }

        ArrayList<Double> regParamas = new ArrayList<>();
        regParamas = (ArrayList<Double>)collaborativeFiltering.getRegParams();
        code.addNamed("$arrayOfDouble:T $regParams:L = new $arrayOfDouble:T();\n", codeVariables);
        for(Double regParam: regParamas) {
            codeVariables.put("regParam", regParam);
            code.addNamed("$regParams:L.add($regParam:L);\n", codeVariables);
        }

        codeVariables.put("alpha", "alpha");
        codeVariables.put("reg", "regParam");
        codeVariables.put("rank", "rank");

        code.addNamed("$als:T $alsVariable:L = new ALS();\n", codeVariables);
        if( collaborativeFiltering.getImplicitPref()) {
            code.addNamed("$alsVariable:L.setImplicitPrefs($implicitPref:L);\n", codeVariables);
        }
        if( collaborativeFiltering.getNumOfBlocks() != null) {
            code.addNamed("$alsVariable:L.setNumBlocks($numOfBlocks:L);\n", codeVariables);
        }
        if(collaborativeFiltering.getMaxIter() != null) {
            code.addNamed("$alsVariable:L.setMaxIter($maxIter:L);\n", codeVariables);
        }
        code.addNamed("$alsVariable:L.setUserCol($userIdCol:S);\n", codeVariables);
        code.addNamed("$alsVariable:L.setItemCol($itemIdCol:S);\n", codeVariables);
        code.addNamed("$alsVariable:L.setRatingCol($ratingCol:S);\n", codeVariables);
        code.beginControlFlow("for($T rank: $L)",
                codeVariables.get("integer"), codeVariables.get("ranks"));
        code.beginControlFlow("for($T regParam: $L)",
                codeVariables.get("double"), codeVariables.get("regParams"));
        if( collaborativeFiltering.getImplicitPref()) {
            code.beginControlFlow("for($T alpha: $L)",
                    codeVariables.get("double"), codeVariables.get("alphas"));
        }
        code.addNamed("$linkedListString:T $hyperparamList:L = new $linkedListString:T();\n", codeVariables);
        code.addNamed("$hyperparamList:L.push(\"Rank: \" + rank);\n", codeVariables);
        code.addNamed("$hyperparamList:L.push(\"RegParams: \" + regParam);\n", codeVariables);
        if( collaborativeFiltering.getImplicitPref() == true) {
            code.addNamed("$hyperparamList:L.push(\"Alpha: \" + alpha);\n", codeVariables);
            code.addNamed("$alsVariable:L.setAlpha(alpha)\n" +
                    "   .setRank(rank)\n" +
                    "   .setRegParam(regParam);\n", codeVariables);
        }
        else {
            code.addNamed("$alsVariable:L.setRank(rank)\n" +
                    "   .setRegParam(regParam);\n", codeVariables);
        }
        code.addNamed("$alsModel:T $alsModelVariable:L = $alsVariable:L.fit($trainingData:L);\n", codeVariables);
        code.addNamed("$alsModelVariable:L.setColdStartStrategy(\"drop\");\n", codeVariables);
        code.addNamed("$datasetRow:T $predictions:L = $alsModelVariable:L.transform($testData:L);\n", codeVariables);
        code.addNamed("$regressionEvaluator:T $regressionEvaluatorVariable:L = new RegressionEvaluator()\n" +
                "   .setMetricName($evaluationMetric:S)\n" +
                "   .setLabelCol($ratingCol:S)\n" +
                "   .setPredictionCol(\"prediction\");\n", codeVariables);
        code.addNamed("$double:T rmse = $regressionEvaluatorVariable:L.evaluate($predictions:L);\n", codeVariables);
        code.addNamed("$errorArray:L.put(loop, rmse);\n", codeVariables);
        code.addNamed("$modelArray:L.put(loop, $alsModelVariable:L);\n", codeVariables);
        code.addNamed("$hyperparamMap:L.put(loop, $hyperparamList:L);\n", codeVariables);
        //code.addNamed("$hyperparamList:L.clear();\n",codeVariables);
        code.addStatement("loop++");
        code.endControlFlow();
        code.endControlFlow();
        if( collaborativeFiltering.getImplicitPref()) {
            code.endControlFlow();
        }

        codeVariables.put("sortedArray", JavaCodeGenerator.newVariableName());

        code.addNamed("$mapOfIntegerAndDouble:T $sortedArray:L = $errorArray:L.entrySet()\n" +
                ".stream()\n" +
                ".sorted(comparingByValue())\n" +
                ".collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));\n", codeVariables);
        code.addNamed("$integer:T leastError = $sortedArray:L.entrySet().stream().findFirst().get().getKey();\n", codeVariables);
        code.addNamed("$alsModel:T $alsModelVariable:L = $modelArray:L.get(leastError);\n", codeVariables);
        codeVariables.put("system", System.class);
        code.addNamed("$system:T.out.println(\"**********Least Error= \" + $errorArray:L.get(leastError) +\n" +
                        " \"********HyperParameters=\" + $hyperparamMap:L.get(leastError));\n"
                , codeVariables);

        code.beginControlFlow("for($T key: $L.keySet() )", codeVariables.get("integer"), codeVariables.get("sortedArray"));
        code.addNamed("$system:T.out.println(\"HyperParameters: \" + $hyperparamMap:L.get(key));\n", codeVariables);
        code.addNamed("$system:T.out.println(\"Error: \" + $sortedArray:L.get(key));\n", codeVariables);
        code.endControlFlow();

        javaCodeGenerator.getMainMethod().addCode(code.build());

        InputOutputMapper inputOutputMapperRet = new InputOutputMapper(alsModel, codeVariables.get("alsModelVariable").toString());

        return  inputOutputMapperRet;

    }
}
