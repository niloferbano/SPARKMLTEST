package de.tum.spark.ml.service;

import com.squareup.javapoet.ClassName;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.codegenerator.MavenBuild;
import de.tum.spark.ml.model.DecisionTree;
import de.tum.spark.ml.model.DecisionTreeMapper;
import de.tum.spark.ml.modules.DecisionTreeTrainModel;
import de.tum.spark.ml.modules.FeatureExtraction;
import de.tum.spark.ml.modules.SaveModel;
import de.tum.spark.ml.modules.SetUpSparkSession;
import de.tum.spark.ml.repository.DecisionTreeJobRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DecisionTreeService {

    @Autowired
    private DecisionTreeJobRepository decisionTreeJobRepository;
    private static final  String DECISION_TREE_PROJECT_PATH = "decisontree";
    private static final  String APP_NAME = "DecisionTree";

    private Map<String, String> sparkConfig = new LinkedHashMap<String, String>() {{
        put("spark.app.name", APP_NAME);
        put("spark.master", "local[*]");
        put("spark.driver.memory", "16g");
        put("spark.driver.bindAddress", "127.0.0.1");
    }};

    public String generateCode(DecisionTree decisionTree) throws IOException {
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        ClassName sparkSession = ClassName.get("org.apache.spark.sql", "SparkSession");

        String outputPath = System.getProperty("user.home");
        String projectPath = String.join(File.separator,
                outputPath, DECISION_TREE_PROJECT_PATH);
        String codePath = String.join(File.separator,
                projectPath, "src", "main", "java");
        JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator(codePath, APP_NAME, "de.tum.in.sparkml");

        InputOutputMapper inputOutputMapper = SetUpSparkSession.getSparkSession("KMeans", sparkConfig, javaCodeGenerator);

        codeVariables.put("sparkSession", inputOutputMapper.getVariableTypeName());
        codeVariables.put("sessionName", inputOutputMapper.getVariableName());
        codeVariables.put("appName", "decisionTree");

        inputOutputMapper = FeatureExtraction.getJavaCode(inputOutputMapper, decisionTree.getFeatureExtraction(), javaCodeGenerator);
        ClassName Dataset = ClassName.get("org.apache.spark.sql", "Dataset");
        double trainSplit = decisionTree.getTrainModel().getTraining_size();
        double testSplit = decisionTree.getTrainModel().getTest_size();

        codeVariables.put("splits", JavaCodeGenerator.newVariableName());
        codeVariables.put("trainingData", JavaCodeGenerator.newVariableName());
        codeVariables.put("testData", JavaCodeGenerator.newVariableName());
        codeVariables.put("datasetRowType", inputOutputMapper.getVariableTypeName());
        codeVariables.put("datasetRow", inputOutputMapper.getVariableName());
        codeVariables.put("dataSet", Dataset);
        codeVariables.put("train", trainSplit);
        codeVariables.put("test", testSplit);

        javaCodeGenerator.getMainMethod()
                .addNamedCode("$dataSet:T[] $splits:L = $datasetRow:L.randomSplit(new double[]{$train:L, $test:L});\n", codeVariables)
                .addNamedCode("$datasetRowType:T $trainingData:L = $splits:L[0];\n", codeVariables)
                .addNamedCode("$datasetRowType:T $testData:L = $splits:L[1];\n", codeVariables);

        inputOutputMapper.setVariableName(codeVariables.get("trainingData").toString());
        inputOutputMapper = DecisionTreeTrainModel.getJavaCode(decisionTree.getTrainModel(), javaCodeGenerator, inputOutputMapper);

        ClassName multiclassClassificationEvaluator = ClassName.get("org.apache.spark.ml.evaluation", "MulticlassClassificationEvaluator");
        codeVariables.put("modelType", inputOutputMapper.getVariableTypeName());
        codeVariables.put("model", inputOutputMapper.getVariableName());
        codeVariables.put("prediction", JavaCodeGenerator.newVariableName());
        codeVariables.put("multiclassEvaluatorType", multiclassClassificationEvaluator);
        codeVariables.put("evaluator", JavaCodeGenerator.newVariableName());
        codeVariables.put("accuracy", JavaCodeGenerator.newVariableName());

        javaCodeGenerator.getMainMethod()
                .addNamedCode("$datasetRowType:T $prediction:L = $model:L.transform($testData:L);\n", codeVariables)
                .addNamedCode("$multiclassEvaluatorType:T $evaluator:L = new $multiclassEvaluatorType:T()\n" +
                        ".setLabelCol(\"labelCol\")\n" +
                        ".setPredictionCol(\"prediction\")\n" +
                        ".setMetricName(\"accuracy\");\n", codeVariables)
                .addNamedCode("double $accuracy:L = $evaluator:L.evaluate($prediction:L);\n", codeVariables);

        SaveModel.getJavaCode(decisionTree.getSaveModel(), javaCodeGenerator, inputOutputMapper);
        javaCodeGenerator.getMainMethod().addStatement("$T.out.printf($S,$N)", System.class, "======>Accuracy = %f\n", codeVariables.get("accuracy"));
        javaCodeGenerator.getMainMethod()
                .addNamedCode("$sessionName:L.stop();\n", codeVariables);
        System.out.println(System.getProperty("user.dir"));
        FileUtils.copyFileToDirectory(new File(System.getProperty("user.dir") + "/src/main/resources/spark-sample-pom/pom.xml"), new File(projectPath));
        javaCodeGenerator.generateJaveClassFile();


        try {
            String result = MavenBuild.runMavenCommand("clean package", projectPath);
            return projectPath;
        } catch (Exception e) {
            System.out.println("An error occurred when building with Maven");
            return "Build failed";
        }
    }

    public DecisionTree save(DecisionTree decisionTree) {
        String modelName = decisionTree.getJobName();
        DecisionTree exist = decisionTreeJobRepository.findByJobName(modelName);

        if (exist != null) {
            exist.setJobName(decisionTree.getJobName());
            exist.setFeatureExtraction(decisionTree.getFeatureExtraction());
            exist.setTrainModel(decisionTree.getTrainModel());
            exist.setSaveModel(decisionTree.getSaveModel());
            decisionTreeJobRepository.save(exist);
            return exist;
        } else {
            return decisionTreeJobRepository.save(decisionTree);
        }

    }

    public DecisionTree parseJsonData(Map<String, Object>  request) {
        List<String> keySet = new LinkedList<>(request.keySet());
        List<String> orderOfSteps = new LinkedList<>();

        orderOfSteps.add("jobName");
        orderOfSteps.add("featureExtraction");
        orderOfSteps.add("trainModel");
        orderOfSteps.add("saveModel");

        DecisionTree decisionTree = new DecisionTree();
        if (orderOfSteps.equals(keySet)) {
            decisionTree = DecisionTreeMapper.mapper(request);
            if (decisionTree.getFeatureExtraction() == null
                    || decisionTree.getTrainModel() == null || decisionTree.getSaveModel() == null) {
                return null;
            }
        else {
                return decisionTree;
            }
        }
        return  null;
    }
}
