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

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addCode("        SparkSession sparkSession = SparkSession\n" +
                        "                .builder()\n" +
                        "                .appName(\"Test\")\n" +
                        "                .config(\"spark.master\", \"local\")\n" +
                        "                .getOrCreate();")
                .build();
        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );

        MethodSpec featureExtractionMethod = MethodSpec.methodBuilder("featureExtraction")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get("org.apache.spark.sql", "sparkSession"),"sparkSession")
                .addParameter(String.class,"filePath")
                .addParameter(String.class, "labelColName")
                .addStatement("       Dataset<Row> df = sparkSession.read()\n" +
                        "                .option(\"header\", false)\n" +
                        "                .option(\"inferSchema\", true).csv(filePath);\n" +
                        "\n" +
                        "        for (String c : df.columns()) {\n" +
                        "            df = df.withColumn(c, df.col(c).cast(\"double\"));\n" +
                        "\n" +
                        "        }\n" +
                        "\n" +
                        "        df = df.withColumnRenamed(labelColName, \"labelCol\");\n" +
                        "        df = df.withColumn(\"labelCol\", df.col(\"labelCol\").minus(1));" +
                        "return df")
                .returns(datasetRow)
                .build();

        ClassName multiclassClassificationEvaluator = ClassName.get("org.apache.spark.ml.evaluation", "MulticlassClassificationEvaluator");

        MethodSpec modelEvaluatorMethod = MethodSpec.methodBuilder("modelEvaluator")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(datasetRow, "predictions")
                .addStatement("$T evaluator = new $T().setLabelCol(\"labelCol\")" +
                        ".setPredictionCol(\"prediction\")\n" +
                        ".setMetricName(\"accuracy\")", multiclassClassificationEvaluator, multiclassClassificationEvaluator)
                .addStatement(
                        "double accuracy = evaluator.evaluate(predictions);\n" +
                                "predictions.select(\"prediction\", \"labelCol\", \"features\").show(10);\n" +
                                "\n" +
                                "System.out.println(\"Accuracy = \" + ( accuracy));")

                .returns(void.class)
                .build();

        ClassName decisionTreeClassifier = ClassName.get("org.apache.spark.ml.classification",
                "DecisionTreeClassifier");
        ClassName decisionTreeClassifierModel = ClassName.get("org.apache.spark.ml.classification",
                "DecisionTreeClassificationModel");
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

        ClassName ioEx = ClassName.get(IOException.class);
        MethodSpec saveModelMethod  = MethodSpec.methodBuilder("saveModel")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(String.class, "modelName")
                .addParameter(decisionTreeClassifierModel,"dtc_model")
                .addStatement("     try {\n" +
                        "                dtc_model.save(\"/Users/nilu/Downloads/\" + modelName+\".model\");\n" +
                        "                throw  new $T();\n" +
                        "            }catch ($T io){\n" +
                        "                System.out.println(\"Model can not be saved\");\n" +
                        "            }", ioEx, ioEx)
                .returns(void.class)
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
