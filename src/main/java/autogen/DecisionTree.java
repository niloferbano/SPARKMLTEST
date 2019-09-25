package autogen;

import java.io.IOException;
import java.lang.String;
import java.lang.System;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DecisionTree {
  public static void main(String[] args) {
    String filePath = "/Users/coworker/Downloads/";
    SparkSession sparkSession = SparkSession
            .builder()
            .appName("Test")
            .config("spark.master", "local")
            .config("spark.driver.bindAddress","127.0.0.1")
            .getOrCreate();
    Dataset<Row> df = featureExtraction(sparkSession,"/Users/coworker/Downloads/covtype.data", "_c54");;
    Dataset<Row> features_df = df.drop("labelCol");
    VectorAssembler assembler = new VectorAssembler().setInputCols(features_df.columns()).setOutputCol("features");;
    Dataset<Row> input_data = assembler.transform(df);
    Dataset[] splits = input_data.randomSplit(new double[]{0.8, 0.2});
    Dataset<Row> training_data = splits[0];
    Dataset<Row> test_data = splits[1];
    DecisionTreeClassificationModel dtc_model = decisionTreeClassificationModel("entropy",
            20, 300, training_data);
    Dataset<Row> predictions = dtc_model.transform(test_data);
    modelEvaluator(predictions);
    saveModel("DTC_Model", filePath,dtc_model);
    sparkSession.stop();
  }
  public static Dataset<Row> featureExtraction(SparkSession sparkSession, String filePath,
      String labelColName) {
    Dataset<Row> df = sparkSession.read().option("header", false).option("inferSchema", true).csv(filePath);
    for(String c:  df.columns()) {
      df = df.withColumn(c, df.col(c).cast("double"));
    }
    df = df.withColumnRenamed(labelColName, "labelCol");
    df = df.withColumn("labelCol", df.col("labelCol").minus(1));
    return df;
  }

  public static void modelEvaluator(Dataset<Row> predictions) {
    MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator().setLabelCol("labelCol").setPredictionCol("prediction")
        .setMetricName("accuracy");
    double accuracy = evaluator.evaluate(predictions);
    System.out.printf("Accuracy = %f",accuracy);
  }

  public static DecisionTreeClassificationModel decisionTreeClassificationModel(String impurity,
      int depth, int maxBins, Dataset<Row> training_data) {
    DecisionTreeClassifier dtc = new DecisionTreeClassifier().setLabelCol("labelCol")
        .setFeaturesCol("features")
        .setMaxDepth(depth)
        .setImpurity(impurity)
        .setMaxBins(maxBins);
    DecisionTreeClassificationModel dtc_model = dtc.fit(training_data);
    return dtc_model;
  }

  public static void saveModel(String modelName, String filePath,
      DecisionTreeClassificationModel dtc_model) {
    try {
      dtc_model.save(filePath + modelName+".model");
      System.out.println("Model successfully saved");
    } catch (IOException io) {
      System.out.println("Model can not be saved");
    }
  }


}
