package autogen;

import java.io.IOException;
import java.lang.String;
import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DecisionTree {
  public static void main(String[] args) {
            SparkSession sparkSession = SparkSession
                    .builder()
                    .appName("Test")
                    .config("spark.master", "local")
                    .getOrCreate();}

  public static Dataset<Row> featureExtraction(SparkSession sparkSession, String filePath,
      String labelColName) {
           Dataset<Row> df = sparkSession.read()
                        .option("header", false)
                        .option("inferSchema", true).csv(filePath);

                for (String c : df.columns()) {
                    df = df.withColumn(c, df.col(c).cast("double"));

                }

                df = df.withColumnRenamed(labelColName, "labelCol");
                df = df.withColumn("labelCol", df.col("labelCol").minus(1));return df;
  }

  public static void modelEvaluator(Dataset<Row> predictions) {
    MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator().setLabelCol("labelCol").setPredictionCol("prediction")
        .setMetricName("accuracy");
    double accuracy = evaluator.evaluate(predictions);
        predictions.select("prediction", "labelCol", "features").show(10);

        System.out.println("Accuracy = " + ( accuracy));;
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

  public static void saveModel(String modelName, DecisionTreeClassificationModel dtc_model) {
         try {
                        dtc_model.save("/Users/nilu/Downloads/" + modelName+".model");
                        throw  new IOException();
                    }catch (IOException io){
                        System.out.println("Model can not be saved");
                    };
  }
}
