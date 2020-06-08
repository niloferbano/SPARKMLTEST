package de.tum.spark.ml.codegenerator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavaCodeGenerator_backup {

    public static void main(String[] args) {
        JavaCodeGenerator_backup tutorial = new JavaCodeGenerator_backup();
        tutorial.generateJavaSource();
    }


    private void generateJavaSource() {

        CodeBlock.Builder filePath = CodeBlock.builder().addStatement("String filePath = \"/Users/coworker/Downloads/\"");

        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );
        ClassName vectorAssembler = ClassName.get("org.apache.spark.ml.feature", "VectorAssembler");
        ClassName sparkSession = ClassName.get("org.apache.spark.sql", "SparkSession");
        ClassName Dataset = ClassName.get("org.apache.spark.sql", "Dataset");
        ClassName decisionTreeClassifier = ClassName.get("org.apache.spark.ml.classification",
                "DecisionTreeClassifier");
        ClassName decisionTreeClassifierModel = ClassName.get("org.apache.spark.ml.classification",
                "DecisionTreeClassificationModel");

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addCode(filePath.build())
                .addStatement("$T sparkSession = SparkSession\n" +
                        ".builder()\n" +
                        ".appName(\"Test\")\n" +
                        ".config(\"spark.master\", \"local\")\n" +
                        ".getOrCreate()", sparkSession)
                .addStatement("$T df = featureExtraction(sparkSession,\"/Users/coworker/Downloads/covtype.data\", \"_c54\");", datasetRow)
                .addStatement("$T features_df = df.drop(\"labelCol\")", datasetRow)
                .addStatement("$T assembler = new $T().setInputCols(features_df.columns())" +
                        ".setOutputCol(\"features\");", vectorAssembler, vectorAssembler)
                .addStatement("$T input_data = assembler.transform(df)", datasetRow)
                .addStatement("$T[] splits = input_data.randomSplit(new double[]{0.8, 0.2})", Dataset)
                .addStatement("$T training_data = splits[0]", datasetRow)
                .addStatement("$T test_data = splits[1]", datasetRow)
                .addStatement("$T dtc_model = decisionTreeClassificationModel(\"entropy\",\n" +
                        "20, 300, training_data)", decisionTreeClassifierModel)
                .addStatement("$T predictions = dtc_model.transform(test_data)", datasetRow)
                .addStatement("modelEvaluator(predictions)")
                .addStatement("saveModel(\"DTC_Model\", filePath,dtc_model)")
                .addStatement("sparkSession.stop()")
                .build();
        CodeBlock.Builder code = CodeBlock.builder();
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        codeVariables.put("sourceType", datasetRow);
        codeVariables.put("variable", JavaCodeGenerator.newVariableName());
        codeVariables.put("constant", "\"labelCol\"");
        codeVariables.put("sparkSession", "sparkSession");
        code.addNamed("$sourceType:T $variable:L = $sparkSession:L.read()" +
                ".option(\"header\", false)" +
                ".option(\"inferSchema\", true).csv(filePath);\n", codeVariables);
        code.beginControlFlow("for(String c:  $L.columns())", codeVariables.get("variable"));
        code.addStatement("$L = $L.withColumn(c, $L.col(c).cast(\"double\"))", codeVariables.get("variable"), codeVariables.get("variable"), codeVariables.get("variable"));
        code.endControlFlow();
        code.addNamed("$variable:L = $variable:L.withColumnRenamed(labelColName, $constant:L);\n", codeVariables);
        code.addNamed("$variable:L = $variable:L.withColumn(\"labelCol\", $variable:L.col($constant:L).minus(1));\n", codeVariables);
        code.addNamed("return $variable:L;\n", codeVariables);

        MethodSpec featureExtractionMethod = MethodSpec.methodBuilder("featureExtraction")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(sparkSession, "sparkSession")
                .addParameter(String.class, "filePath")
                .addParameter(String.class, "labelColName")
                .addCode(code.build())
                .returns(datasetRow)
                .build();

        ClassName multiclassClassificationEvaluator = ClassName.get("org.apache.spark.ml.evaluation", "MulticlassClassificationEvaluator");

        MethodSpec modelEvaluatorMethod = MethodSpec.methodBuilder("modelEvaluator")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(datasetRow, "predictions")
                .addStatement("$T evaluator = new $T().setLabelCol(\"labelCol\")" +
                        ".setPredictionCol(\"prediction\")\n" +
                        ".setMetricName(\"accuracy\")", multiclassClassificationEvaluator, multiclassClassificationEvaluator)
                .addStatement("double accuracy = evaluator.evaluate(predictions)")
                .addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f", "accuracy")
                .returns(void.class)
                .build();


        ClassName ioEx = ClassName.get(IOException.class);

        MethodSpec saveModelMethod = MethodSpec.methodBuilder("saveModel")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(String.class, "modelName")
                .addParameter(String.class, "filePath")
                .addParameter(decisionTreeClassifierModel, "dtc_model")
                .beginControlFlow("try")
                .addStatement("dtc_model.save(filePath + modelName+\".model\")")
                .addStatement("$T.out.println($S)", System.class, "Model successfully saved")
                .nextControlFlow("catch ($T io)", ioEx)
                .addStatement("$T.out.println($S)", System.class, "Model can not be saved")
                .endControlFlow()
                .build();


        CodeBlock codeBlock = CodeBlock.builder()
                .addStatement("$T dtc = new $T()" +
                        ".setLabelCol(\"labelCol\")\n" +
                        ".setFeaturesCol(\"features\")\n" +
                        ".setMaxDepth(depth)\n" +
                        ".setImpurity(impurity)\n" +
                        ".setMaxBins(maxBins)", decisionTreeClassifier, decisionTreeClassifier)
                .addStatement("$T dtc_model = dtc.fit(training_data)", decisionTreeClassifierModel)
                .addStatement("return dtc_model")
                .build();

        MethodSpec decisionTreeClassificationModelMethod = MethodSpec.methodBuilder("decisionTreeClassificationModel")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, "impurity")
                .addParameter(TypeName.INT, "depth")
                .addParameter(TypeName.INT, "maxBins")
                .addParameter(datasetRow, "training_data")
                .addCode(codeBlock)
                .returns(decisionTreeClassifierModel)
                .build();

        TypeSpec DecisionTree = TypeSpec.classBuilder("DecisionTree")
                .addModifiers(Modifier.PUBLIC)

                .addMethod(featureExtractionMethod)
                .addMethod(modelEvaluatorMethod)
                .addMethod(decisionTreeClassificationModelMethod)
                .addMethod(saveModelMethod)
                .addMethod(main)
                .build();


        //String codePath = String.join(File.separator, "src", "main", "java");

        JavaFile javaFile = JavaFile.builder("autogen", DecisionTree)
                .build();

        try {
            javaFile.writeTo(Paths.get("./src/main/java"));
        } catch (IOException ex) {
            System.out.println("An exception! " + ex.getMessage());
        }
    }
}
