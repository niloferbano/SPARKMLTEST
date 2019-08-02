package de.tum.spark.ml.codegenerator;

import com.squareup.javapoet.*;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.apache.spark.sql.SparkSession;

public class TestCodeGenerator {

    public static void main(String[] args) {
        TestCodeGenerator tutorial = new TestCodeGenerator();
        tutorial.generateJavaSource();
    }

    private void generateJavaSource(){
        SparkSession spark = SparkSession
                .builder()
                .appName("Test")
                .config("spark.master", "local")
                .getOrCreate();


        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addCode("        SparkSession spark = SparkSession\n" +
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
                .addParameter(ClassName.get("org.apache.spark.sql", "SparkSession"),"sparkSession")
                .addParameter(String.class,"filePath")
                .addParameter(String.class, "labelColName")
                .returns(datasetRow)
                .build();

        TypeSpec DecisionTree = TypeSpec.classBuilder("DecisionTree")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .addMethod(featureExtractionMethod)
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
