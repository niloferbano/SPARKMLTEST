package de.tum.spark.ml.codegenerator;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;

public class JavaCodeGenerator {



    public static void main(String[] args) {
        JavaCodeGenerator tutorial = new JavaCodeGenerator();
        tutorial.generateJavaSource();
    }

    private void generateJavaSource(){

        CodeBlock.Builder filePath = CodeBlock.builder().addStatement("String filePath = \"/Users/nilu/Downloads/\"");

        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );
        ClassName vectorAssembler = ClassName.get("org.apache.spark.ml.feature","VectorAssembler");
        ClassName sparkSession = ClassName.get("org.apache.spark.sql","SparkSession");
        ClassName Dataset = ClassName.get("org.apache.spark.sql","Dataset");
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
                .addStatement("$T df = featureExtraction(sparkSession,\"/Users/nilu/Downloads/covtype.csv\", \"_c54\");", datasetRow)
                .addStatement("$T features_df = df.drop(\"labelCol\")", datasetRow)
                .addStatement("$T assembler = new $T().setInputCols(features_df.columns())" +
                        ".setOutputCol(\"features\");", vectorAssembler,vectorAssembler)
                .addStatement("$T input_data = assembler.transform(df)",datasetRow )
                .addStatement("$T[] splits = input_data.randomSplit(new double[]{0.8, 0.2})", Dataset)
                .addStatement("$T training_data = splits[0]", datasetRow)
                .addStatement("$T test_data = splits[1]",datasetRow)
                .addStatement("$T dtc_model = decisionTreeClassificationModel(\"entropy\",\n" +
                        "20, 300, training_data)", decisionTreeClassifierModel)
                .addStatement("$T predictions = dtc_model.transform(test_data)", datasetRow)
                .addStatement("modelEvaluator(predictions)")
                .addStatement("saveModel(\"DTC_Model\", filePath,dtc_model)")
                .addStatement("sparkSession.stop()")
                .build();

        MethodSpec featureExtractionMethod = MethodSpec.methodBuilder("featureExtraction")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(sparkSession,"sparkSession")
                .addParameter(String.class,"filePath")
                .addParameter(String.class, "labelColName")
                .addStatement("       $T df = sparkSession.read()\n" +
                        "                .option(\"header\", false)\n" +
                        "                .option(\"inferSchema\", true).csv(filePath)", datasetRow)
                .beginControlFlow("for(String c:  df.columns())")
                .addStatement("df = df.withColumn(c, df.col(c).cast(\"double\"))")
                .endControlFlow()
                .addStatement("df = df.withColumnRenamed(labelColName, \"labelCol\")")
                .addStatement("df = df.withColumn(\"labelCol\", df.col(\"labelCol\").minus(1))")
                .addStatement("return df")
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
                .addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f","accuracy")
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
                .addMethod(main)
                .addMethod(featureExtractionMethod)
                .addMethod(modelEvaluatorMethod)
                .addMethod(decisionTreeClassificationModelMethod)
                .addMethod(saveModelMethod)
                .build();

        JavaFile javaFile = JavaFile.builder("autogen", DecisionTree)
                .build();

        try {
            javaFile.writeTo(Paths.get("./src/main/java"));//root maven source
        } catch (IOException ex) {
            System.out.println("An exception! " + ex.getMessage());
        }
    }
}
