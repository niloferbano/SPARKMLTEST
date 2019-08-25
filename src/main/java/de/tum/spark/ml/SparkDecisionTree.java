package de.tum.spark.ml;

import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.classification.RandomForestClassificationModel;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import java.io.IOException;



public class SparkDecisionTree {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Test")
                .config("spark.master", "local")
                .getOrCreate();


        Dataset<Row> df = featureExtraction(spark,"/Users/nilu/Downloads/covtype.csv", "_c54");

        Dataset<Row> features_df = df.drop("labelCol");
        //Dataset<Row> target_df = df.select("labelCol");


        VectorAssembler assembler = new VectorAssembler().setInputCols(features_df.columns()).setOutputCol("features");
        Dataset<Row> input_data = assembler.transform(df);

        input_data.take(10);
        Dataset[] splits = input_data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> training_data = splits[0];
        Dataset<Row> test_data = splits[1];

        DecisionTreeClassificationModel dtc_model = DecisionTree("entropy",
                20, 300, training_data);

        Dataset<Row> predictions = dtc_model.transform(test_data);
        modelEvaluator(predictions);
        SaveModel("DTC_Model", dtc_model);

        spark.stop();
    }

    public static void SaveModel(String modelName, DecisionTreeClassificationModel dtc_model) {
        try {
            dtc_model.save("/Users/nilu/Downloads/" + modelName+".model");
        }catch (IOException io){
            System.out.println("Model can not be saved");
        }

    }

    public static DecisionTreeClassificationModel DecisionTree(String impurity, int treeDepth,
                                                               int maxBins,
                                                               Dataset<Row> training_data) {
        DecisionTreeClassifier dtc = new DecisionTreeClassifier()
                .setLabelCol("labelCol")
                .setFeaturesCol("features")
                .setMaxDepth(treeDepth)
                .setImpurity(impurity)
                .setMaxBins(maxBins);
        DecisionTreeClassificationModel dtc_model = dtc.fit(training_data);

        return dtc_model;

    }

    public static RandomForestClassificationModel RandomTree(String impurity, int treeDepth,
                                                               int maxBins,
                                                               Dataset<Row> training_data) {
        RandomForestClassifier rfc = new RandomForestClassifier()
                .setLabelCol("labelCol")
                .setFeaturesCol("features")
                .setMaxDepth(treeDepth)
                .setImpurity(impurity)
                .setMaxBins(maxBins);
        RandomForestClassificationModel rfc_model = rfc.fit(training_data);

        return rfc_model;

    }

    public static Dataset<Row> featureExtraction(SparkSession spark, String filePath, String labelColName) {
        Dataset<Row> df = spark.read()
                .option("header", false)
                .option("inferSchema", true).csv(filePath);

        for (String c : df.columns()) {
            df = df.withColumn(c, df.col(c).cast("double"));

        }

        df = df.withColumnRenamed(labelColName, "labelCol");
        df = df.withColumn("labelCol", df.col("labelCol").minus(1));

        return df;
    }

    public static void modelEvaluator(Dataset<Row> predictions) {
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("labelCol")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");
        double accuracy = evaluator.evaluate(predictions);
        predictions.select("prediction", "labelCol", "features").show(10);

        System.out.println("Accuracy = " + ( accuracy));
    }

}
