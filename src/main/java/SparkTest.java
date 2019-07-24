import org.apache.spark.ml.classification.DecisionTreeClassificationModel;
import org.apache.spark.ml.classification.DecisionTreeClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkTest {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Test")
                .config("spark.master", "local")
                .getOrCreate();


        Dataset<Row> df = spark.read()
                .option("header", false)
                .option("inferSchema", true).csv("/Users/nilu/Downloads/covtype.csv");

        for (String c : df.columns()) {
            df = df.withColumn(c, df.col(c).cast("double"));

        }
        df = df.withColumnRenamed("_c54", "labelCol");
        Dataset<Row> features_df = df.drop("labelCol");
        Dataset<Row> target_df = df.select("labelCol");


        VectorAssembler assembler = new VectorAssembler().setInputCols(features_df.columns()).setOutputCol("features");
        Dataset<Row> input_data = assembler.transform(df);
        //input_data.printSchema();
        Dataset[] splits = input_data.randomSplit(new double[]{0.8, 0.1, 0.1});
        Dataset<Row> training_data = splits[0];
        Dataset<Row> cv_data = splits[1];
        Dataset<Row> test_data = splits[2];
        //training_data.printSchema();
//
        DecisionTreeClassifier dtc = new DecisionTreeClassifier()
                .setLabelCol("labelCol")
                .setFeaturesCol("features")
                .setMaxDepth(5)
                .setImpurity("Gini");
        Dataset<Row> final_data = input_data.select("features", "labelCol");
        final_data.head();
        DecisionTreeClassificationModel dtc_model = dtc.fit(training_data);
        dtc_model.featureImportances();
        Dataset<Row> predictions = dtc_model.transform(test_data);
        //predictions.printSchema();
        predictions.select("prediction", "labelCol", "features").show(10);
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setLabelCol("labelCol")
                .setPredictionCol("prediction")
                .setMetricName("accuracy");
        double accuracy = evaluator.evaluate(predictions);
        System.out.println("Test Error = " + (1.0 - accuracy));
    }

}
