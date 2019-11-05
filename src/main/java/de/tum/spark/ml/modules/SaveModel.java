package de.tum.spark.ml.modules;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.SaveModelDto;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SaveModel {
    public static String getJavaCode(SaveModelDto saveModelDto, JavaCodeGenerator javaCodeGenerator, InputOutputMapper inputOutputMapper) {

        ClassName ioEx = ClassName.get(IOException.class);

        CodeBlock.Builder code = CodeBlock.builder();
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        codeVariables.put("model", inputOutputMapper.getVariableName());
        codeVariables.put("filePath", saveModelDto.getFilePath());
        codeVariables.put("filePathVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("modelName", saveModelDto.getModelName());
        MethodSpec saveModelMethod = MethodSpec.methodBuilder("saveModel")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(String.class, codeVariables.get("modelName").toString())
                .addParameter(String.class, codeVariables.get("filePathVariable").toString())
                .addParameter(inputOutputMapper.getVariableTypeName(), inputOutputMapper.getVariableName())
                .beginControlFlow("try")
                .addNamedCode("$model:L.save($filePathVariable:L + $modelName:L+\".model\");\n", codeVariables)
                .addStatement("$T.out.println($S)", System.class, "Model successfully saved")
                .nextControlFlow("catch ($T io)", ioEx)
                .addStatement("$T.out.println($S)", System.class, "Model can not be saved")
                .endControlFlow()
                .build();
        javaCodeGenerator.addMethod(saveModelMethod);

        return "saveModel";
    }
}
