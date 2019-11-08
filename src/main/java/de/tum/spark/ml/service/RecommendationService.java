package de.tum.spark.ml.service;


import com.squareup.javapoet.*;
import de.tum.spark.ml.SparkCollaborativeFiltering;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.codegenerator.MavenBuild;
import de.tum.spark.ml.model.CollaborativeFiltering;
import de.tum.spark.ml.model.FeatureExtractionFromTextFileDto;
import de.tum.spark.ml.modules.CFTrainModel;
import de.tum.spark.ml.modules.FeatureExtractionFromTextFile;
import de.tum.spark.ml.modules.SaveModel;
import de.tum.spark.ml.modules.SetUpSparkSession;
import de.tum.spark.ml.repository.CollaborativeFilteringRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.types.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.*;

@Service
public class RecommendationService {

    @Autowired
    private CollaborativeFilteringRepository collaborativeFilteringRepository;
    private static final String RECOMMENDING_PROJECT_PATH = "collaborativeFiltering";
    private static final String APP_NAME = "CollaborativeFiltering";
    private static final String PACKAGE_NAME = "de.tum.in.sparkml";


    private Map<String, String> sparkConfig = new LinkedHashMap<String, String>() {{
        put("spark.app.name", APP_NAME);
        put("spark.master", "local[*]");
        put("spark.driver.memory", "16g");
        put("spark.default.parallelism", "8");
        put("spark.driver.bindAddress", "127.0.0.1");
    }};

    public void generateCode(CollaborativeFiltering collaborativeFiltering) throws IOException {
        Map<String, Object> codeVariables = new LinkedHashMap<>();

        String outputPath = System.getProperty("user.home");
        String projectPath = String.join(File.separator,
                outputPath, RECOMMENDING_PROJECT_PATH);
        String codePath = String.join(File.separator,
                projectPath, "src", "main", "java");
        JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator(codePath, APP_NAME, PACKAGE_NAME);
        InputOutputMapper inputOutputMapper = SetUpSparkSession.getSparkSession("Recommendation", sparkConfig, javaCodeGenerator);
        codeVariables.put("sessionName", inputOutputMapper.getVariableName());
        javaCodeGenerator.getMainMethod().addNamedCode("$sessionName:L.sparkContext().setLogLevel(\"ERROR\");\n", codeVariables);
        TypeName integerType = ClassName.get(Integer.class);
        ParameterizedTypeName mapOfIntInt = ParameterizedTypeName.get(ClassName.get(Map.class), integerType, integerType);

        FieldSpec aliasMap = FieldSpec.builder(mapOfIntInt, "artistAliasMap", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                .initializer("new LinkedHashMap<>()")
                .build();

        javaCodeGenerator.getGeneratedClassName().addField(aliasMap);


        TypeSpec.Builder ratingClass = createRatingClass((Map<String, Object>) collaborativeFiltering.getFeatureExtraction().getSourceFilePath().get("sourceDetail"));

        javaCodeGenerator.getGeneratedClassName().addType(ratingClass.build());

        Map<String, String> sourceColmns = new LinkedHashMap<>();
        Map<String, String> sourceDetail = (Map)collaborativeFiltering.getFeatureExtraction().getSourceFilePath().get("sourceDetail");

        sourceColmns.put("userIdCol", sourceDetail.get("userIdColName"));
        sourceColmns.put("itemIdCol", sourceDetail.get("itemColName"));
        sourceColmns.put("ratingCol", sourceDetail.get("ratingColName"));

        MethodSpec methodSpec = MethodSpec.methodBuilder("updateArtistAlias")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addParameter(Integer.class,"key")
                .addParameter(Integer.class, "value")
                .addStatement(APP_NAME + ".artistAliasMap.put(key, value)")
                .returns(void.class)
                .build();
        javaCodeGenerator.getGeneratedClassName().addMethod(methodSpec);


        FeatureExtractionFromTextFileDto featureExtractionDto = collaborativeFiltering.getFeatureExtraction();
        Map<String, InputOutputMapper> inputData = FeatureExtractionFromTextFile.getJavaCode(inputOutputMapper, collaborativeFiltering.getFeatureExtraction(), javaCodeGenerator);


        inputOutputMapper = CFTrainModel.getJaveCode(collaborativeFiltering.getTrainModel(), javaCodeGenerator, inputData, sourceColmns);

        String methodName = SaveModel.getJavaCode(collaborativeFiltering.getSaveModel(), javaCodeGenerator, inputOutputMapper);


        //codeVariables.put("methodName", methodName);
        codeVariables.put("locationToSave", collaborativeFiltering.getSaveModel().getFilePath());
        //codeVariables.put("modelName", collaborativeFiltering.getSaveModel().getModelName());
        //codeVariables.put("finalModel", inputOutputMapper.getVariableName());
        codeVariables.put("integer", integerType);

        //javaCodeGenerator.getMainMethod().addNamedCode("$methodName:L($modelName:S, $locationToSave:S, $finalModel:L);\n", codeVariables);

        javaCodeGenerator.getMainMethod()
                .addNamedCode("$sessionName:L.stop();\n", codeVariables);

        FileUtils.copyFileToDirectory(new File(System.getProperty("user.dir") + "/src/main/resources/spark-sample-pom/pom.xml"), new File(projectPath));
        javaCodeGenerator.generateJaveClassFile();

        try {
            String result = MavenBuild.runMavenCommand("clean package", projectPath);
        } catch (Exception e) {
            System.out.println("An error occurred when building with Maven");
        }
    }


    public CollaborativeFiltering save(CollaborativeFiltering collaborativeFiltering) {
        String modelName = collaborativeFiltering.getModelName();
        CollaborativeFiltering exist = collaborativeFilteringRepository.findByModelName(modelName);

        if (exist != null) {
            return collaborativeFiltering;
        } else {
            return collaborativeFilteringRepository.save(collaborativeFiltering);
        }

    }

    public TypeSpec.Builder createRatingClass(Map<String, Object> sourceDetail) {
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        codeVariables.put("classname", APP_NAME);
        Map<String, String> types = (Map) sourceDetail.get("ratingColType");
        codeVariables.put("productId", sourceDetail.get("itemColName"));

        String[] inputs = sourceDetail.get("orderOfSourceColumns").toString().split(",");
        TypeSpec.Builder className = TypeSpec.classBuilder("Rating")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(Serializable.class);

        codeVariables.put("className", "Rating");
        LinkedList<FieldSpec> fieldSpecs = new LinkedList<>();
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder();
        MethodSpec.Builder parseRating = MethodSpec.methodBuilder("parseRating")
                .addParameter(String.class, "str")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement("String[] fields = str.split(\" \")")
                .addStatement("        if (fields.length != 3) {\n" +
                        "            throw new IllegalArgumentException(\"Each line must contain 3 fields\");\n" +
                        "        }")
                .returns(ClassName.get(PACKAGE_NAME + ".CollaborativeFiltering", "Rating"));



        Integer loop = 0;
        codeVariables.put("loop", loop);


        List<Map<String, String>> parameterOrder = new LinkedList<>();

        for (String str : inputs) {
            if (types.get(str) == null || types.get(str).equals("int")) {
                FieldSpec fieldSpec = FieldSpec.builder(Integer.class, str)
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                fieldSpecs.push(fieldSpec);
                constructor.addParameter(Integer.class, str);
                constructor.addStatement("this." + str + " = " + str);
                parseRating.addStatement("Integer " + str + " = Integer.parseInt(fields[$L])", loop);
            } else {
                FieldSpec fieldSpec = FieldSpec.builder(Float.class, str)
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                fieldSpecs.push(fieldSpec);
                constructor.addParameter(Float.class, str);
                constructor.addStatement("this." + str + " = " + str);
                parseRating.addStatement("Float " + str + " = Float.parseFloat(fields[$L])", loop);
            }
            loop++;
        }

        for (FieldSpec fieldSpec : fieldSpecs) {
            className.addField(fieldSpec);
        }


        constructor.addModifiers(Modifier.PUBLIC);
        className.addMethod(constructor.build());
        codeVariables.put("integer", ClassName.get(Integer.class));
        codeVariables.put("finalArtistData", "finalArtistData");
        codeVariables.put("productId", "finalArtistData");

        parseRating.addStatement("$T $L = $L.artistAliasMap.getOrDefault($L, $L)",
                codeVariables.get("integer"), codeVariables.get("finalArtistData"), codeVariables.get("classname"),
                sourceDetail.get("itemColName"),
                sourceDetail.get("itemColName"));
        CodeBlock.Builder constructorCall = CodeBlock.builder();
        constructorCall.addNamed("return new $className:L(", codeVariables);

//        if (inputs.length == 3) {
//            parseRating.addStatement("return new Rating(" + "$L" + ",$L," + "$L" + ")", codeVariables.get("finalArtistData"));
//        }

        Integer count = 0;
        for( String str: inputs) {
            if( str != sourceDetail.get("itemColName")) {
                codeVariables.put(count.toString(), str);
            }
            else {
                codeVariables.put(count.toString(), "finalArtistData");
            }
            count++;
        }
        parseRating.addStatement("return new Rating(" + "$L" + ",$L," + "$L" + ")",
                codeVariables.get("0"), codeVariables.get("1"),codeVariables.get("2"));

        AnnotationSpec getter = AnnotationSpec.builder(Getter.class).build();
        AnnotationSpec setter = AnnotationSpec.builder(Setter.class).build();
        className.addAnnotation(getter);
        className.addAnnotation(setter);
        className.addMethod(parseRating.build());

        return className;

    }

    public CollaborativeFiltering parseJsonData(Map<String, Object>  request) {
        List<String> keySet = new LinkedList<>(request.keySet());
        List<String> orderOfSteps = new LinkedList<>();
        orderOfSteps.add("modelName");
        orderOfSteps.add("featureExtraction");
        orderOfSteps.add("trainModel");
        orderOfSteps.add("saveModel");
        CollaborativeFiltering collaborativeFiltering = null;
        if (orderOfSteps.equals(keySet)) {
            collaborativeFiltering = new CollaborativeFiltering(request);
            if (collaborativeFiltering.getFeatureExtraction() == null
                    || collaborativeFiltering.getTrainModel() == null || collaborativeFiltering.getSaveModel() == null) {
                return null;
            } else {
                return collaborativeFiltering;
            }
        }
        return  null;
    }
}
