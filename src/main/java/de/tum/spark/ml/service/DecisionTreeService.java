package de.tum.spark.ml.service;

import com.squareup.javapoet.ClassName;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.codegenerator.MavenBuild;
import de.tum.spark.ml.model.decisionTreeModel.DecisionTree;
import de.tum.spark.ml.modules.FeatureExtraction;
import de.tum.spark.ml.modules.SaveModel;
import de.tum.spark.ml.modules.DecisionTreeTrainModel;
import de.tum.spark.ml.repository.DecisionTreeJobRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DecisionTreeService {

    @Autowired
    private DecisionTreeJobRepository decisionTreeJobRepository;
    private static final  String DECISION_TREE_PROJECT_PATH = "decisontree";
    private static final  String APP_NAME = "DecisionTree";

    public void generateCode(DecisionTree decisionTree) throws IOException {
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        ClassName sparkSession = ClassName.get("org.apache.spark.sql", "SparkSession");

        String outputPath = System.getProperty("user.home");
        String projectPath = String.join(File.separator,
                outputPath, DECISION_TREE_PROJECT_PATH);
        String codePath = String.join(File.separator,
                projectPath, "src", "main", "java");
        JavaCodeGenerator javaCodeGenerator = new JavaCodeGenerator(codePath, APP_NAME, "de.tum.in.sparkml");

        codeVariables.put("sparkSession", sparkSession);
        codeVariables.put("sessionName", JavaCodeGenerator.newVariableName());
        codeVariables.put("appName", "decisionTree");
        javaCodeGenerator.getMainMethod()
                .addNamedCode("$sparkSession:T $sessionName:L = $sparkSession:T" +
                        ".builder()\n" +
                        ".appName(\"decisionTree\")\n" +
                        ".config(\"spark.master\",\"local\")\n" +
                        ".config(\"spark.driver.bindAddress\",\"127.0.0.1\")" +
                        ".getOrCreate();\n", codeVariables)
                .addStatement("$T.out.println($S)", System.class, "=====******Spark Started*******=====");

        InputOutputMapper inputOutputMapper = FeatureExtraction.getJavaCode(new InputOutputMapper(sparkSession, codeVariables.get("sessionName").toString()), decisionTree.getFeatureExtractionDto(), javaCodeGenerator);
        ClassName Dataset = ClassName.get("org.apache.spark.sql", "Dataset");
        double trainSplit = decisionTree.getDTTrainModelDto().getTraining_size();
        double testSplit = decisionTree.getDTTrainModelDto().getTest_size();

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
        inputOutputMapper = DecisionTreeTrainModel.getJavaCode(decisionTree.getDTTrainModelDto(), javaCodeGenerator, inputOutputMapper);

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
                .addNamedCode("double $accuracy:L = $evaluator:L.evaluate($prediction:L);\n", codeVariables)
                .addStatement("$T.out.printf($S,$N)", System.class, "Accuracy = %f", codeVariables.get("accuracy"));

        SaveModel.getJavaCode(decisionTree.getSaveModelDto(), javaCodeGenerator, inputOutputMapper);
        javaCodeGenerator.getMainMethod()
                .addNamedCode("$sessionName:L.stop();\n", codeVariables);
        System.out.println(System.getProperty("user.dir"));
        FileUtils.copyFileToDirectory(new File(System.getProperty("user.dir") + "/src/main/resources/spark-sample-pom/pom.xml"), new File(projectPath));
        javaCodeGenerator.generateJaveClassFile();


        try {
            String result = MavenBuild.runMavenCommand("clean package", projectPath);
        } catch (Exception e) {
            System.out.println("An error occurred when building with Maven");
        }
    }

    public DecisionTree save(DecisionTree decisionTree) {
        String modelName = decisionTree.getModelName();
        DecisionTree exist = decisionTreeJobRepository.findByModelName(modelName);

        if (exist != null) {
            return decisionTree;
        } else {
            return decisionTreeJobRepository.save(decisionTree);
        }

    }
}
