package de.tum.spark.ml.codegenerator;


import com.squareup.javapoet.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;


@Getter
@Setter

public class JavaCodeGenerator {

    public static final int VARIABLES_NAME_LENGTH = 10;

    private CodeBlock.Builder code;
    private String outputPath;
    private String className;
    private TypeSpec.Builder generatedClassName;
    private ArrayList<MethodSpec> classMethods;
    private MethodSpec.Builder mainMethod;
    private String packageName;


    public JavaCodeGenerator( String outputPath, String className, String packageName ) {


        this.outputPath = outputPath;
        this.className = className;
        this.generatedClassName = TypeSpec.classBuilder(className)
                                    .addModifiers(Modifier.PUBLIC);
        this.classMethods = new ArrayList<>();
        this.mainMethod = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args");
        this.setPackageName(packageName);

    }

    public void generateJaveClassFile() throws IOException {

        for( MethodSpec methodSpec: this.classMethods) {
            this.generatedClassName
            .addMethod(methodSpec);
        }

        this.generatedClassName.addMethod(this.getMainMethod().build());
        


        JavaFile javaFile = JavaFile.builder(this.getPackageName(), this.getGeneratedClassName().build())
                .addStaticImport(ClassName.get("java.util.Map","Entry"),"comparingByValue")
                .addStaticImport(ClassName.get("java.util.stream","Collectors"),"toMap")
                .build();

        try {
            javaFile.writeTo(Paths.get(this.getOutputPath()));
            System.out.println(this.getOutputPath());
        } catch (IOException ex) {
            System.out.println("An exception! " + ex.getMessage());
        }
    }

    public void addMethod(MethodSpec methodSpec) {
        this.generatedClassName.addMethod(methodSpec);

    }

    public static String newVariableName() {
        return RandomStringUtils.randomAlphabetic(VARIABLES_NAME_LENGTH).toLowerCase();
    }


}
