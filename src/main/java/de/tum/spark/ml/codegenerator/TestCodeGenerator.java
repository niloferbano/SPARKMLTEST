package de.tum.spark.ml.codegenerator;

import com.squareup.javapoet.*;
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
        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("autogen", helloWorld)
                .build();

        try {
            javaFile.writeTo(Paths.get("./src/main/java"));//root maven source
        } catch (IOException ex) {
            System.out.println("An exception! " + ex.getMessage());
        }
    }
}
