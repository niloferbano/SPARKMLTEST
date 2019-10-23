package de.tum.spark.ml.modules;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;

import java.util.LinkedHashMap;
import java.util.Map;

public class SetUpSparkSession {
    public static InputOutputMapper getSparkSession(String appName, Map<String, String> sparkConfig, JavaCodeGenerator javaCodeGenerator) {

        CodeBlock.Builder code = CodeBlock.builder();
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        ClassName sparkSession = ClassName.get("org.apache.spark.sql", "SparkSession");
        codeVariables.put("sparkSession", sparkSession);
        codeVariables.put("sessionName", JavaCodeGenerator.newVariableName());
        codeVariables.put("appName", appName);

        code.addNamed("$sparkSession:T $sessionName:L = $sparkSession:T.builder().appName($appName:S)", codeVariables);

        sparkConfig.forEach((key, value) -> {
            Map<String, String> codeConfig = new LinkedHashMap<>();
            codeConfig.put("key", key);
            codeConfig.put("value", value);

            code.addNamed(".config($key:S, $value:S)\n", codeConfig);
        });
        code.add(".getOrCreate();\n", codeVariables);

        javaCodeGenerator.getMainMethod().addCode(code.build());

        return  new InputOutputMapper(sparkSession, codeVariables.get("sessionName").toString());

    }
}
