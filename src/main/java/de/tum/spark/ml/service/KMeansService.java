package de.tum.spark.ml.service;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.decisionTreeModel.KMeansClustering;
import de.tum.spark.ml.modules.FeatureExtraction;
import de.tum.spark.ml.modules.SetUpSparkSession;
import de.tum.spark.ml.repository.KMeansRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KMeansService {
    @Autowired
    private KMeansRepository kMeansRepository;
    private static final String KMEANS_PROJECT_PATH = "kmeans";
    private static final String APP_NAME = "KMeansClustering";

    private Map<String, String> sparkConfig = new LinkedHashMap<String, String>() {{
        put("spark.app.name", APP_NAME);
        put("spark.master", "local[*]");
        put("spark.driver.memory", "16g");
        put("spark.default.parallelism", "8");
        put("spark.driver.bindAddress", "127.0.0.1");
        put("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        put("spark.kryoserializer.buffer.max", "1g");

    }};


    public void generateCode(KMeansClustering kMeansClustering) throws IOException {
        Map<String, Object> codeVariables = new LinkedHashMap<>();

        String outputPath = System.getProperty("user.home");
        String projectPath = String.join(File.separator,
                outputPath, KMEANS_PROJECT_PATH);
        String codePath = String.join(File.separator,
                projectPath, "src", "main", "java");
        JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator(codePath, APP_NAME, "de.tum.in.sparkml");
        InputOutputMapper inputOutputMapper = SetUpSparkSession.getSparkSession("KMeans", sparkConfig, javaCodeGenerator);

        inputOutputMapper = FeatureExtraction.getJavaCode(inputOutputMapper, kMeansClustering.getFeatureExtractionDto(), javaCodeGenerator);


        //Scaling the input features
        ClassName StandardScalar = ClassName.get("org.apache.spark.ml.feature", "StandardScaler");
        ClassName StandardScalarModel = ClassName.get("org.apache.spark.ml.feature", "StandardScalerModel");
        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );


        codeVariables.put("standardScalar", StandardScalar);
        codeVariables.put("standardScalarVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("standardScalarModel", StandardScalarModel);
        codeVariables.put("standardScalarModelVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("datasetRow", datasetRow);
        codeVariables.put("finalClusteringData", JavaCodeGenerator.newVariableName());
        codeVariables.put("inputDataVariable", inputOutputMapper.getVariableName());
        codeVariables.put("storageLevel", ClassName.get("rg.apache.spark.storage", "StorageLevel"));

        if (kMeansClustering.getKMeansTrainModelDto().getScaleFeature()) {
            javaCodeGenerator.getMainMethod()
                    .addNamedCode("$standardScalar:T $standardScalarVariable:L = new $standardScalar()" +
                                    ".setInputCol(\"featueres\")" +
                                    ".setOutputCol(\"scaledFeatures\")",
                            codeVariables);
            if (kMeansClustering.getKMeansTrainModelDto().getWithStd()) {
                javaCodeGenerator.getMainMethod()
                        .addStatement(".setWithStd(true)\n");
            } else {
                javaCodeGenerator.getMainMethod()
                        .addStatement(".setWithMean(true)\n");
            }

            javaCodeGenerator.getMainMethod()
                    .addNamedCode("$standardScalarModel:T $standardScalarModelVariable:L = $standardScalarVariable:L.fit($inputDataVariable:L);\n", codeVariables)
                    .addNamedCode("$datasetRow:T $finalClusteringData:L = " +
                            " $standardScalarModelVariable:L.transform($inputDataVariable:L).persist($storageLevel.MEMORY_ONLY());\n", codeVariables);
            inputOutputMapper.setVariableName(codeVariables.get("finalClusteringData").toString());
        }

    }


}
