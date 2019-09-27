package de.tum.spark.ml;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.StandardScalerModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.storage.StorageLevel;

import java.util.ArrayList;
import java.util.List;

public class SparkKMeansClustering {


    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Clustering")
                .config("spark.master", "local[*]")
                .config("spark.driver.memory", "16g")
                .config("spark.default.parallelism", "8")
                .config("spark.driver.bindAddress", "127.0.0.1")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .config("spark.kryoserializer.buffer.max","1g")
                // .config("spark.executor.memory", "32g")
                .getOrCreate();

        //spark.conf();
        //System.out.println("==================>" + spark.conf());
        Dataset<Row> df = spark.read()
                .option("header", false)
                .option("inferSchema", true)
                //.option("partition", 4)
                .csv("/Users/coworker/Downloads/kddcup.data").cache();



        List<String> dropCols = new ArrayList<String>();

        /* These will be user inputs*/
        dropCols.add("_c1");
        dropCols.add("_c2");
        dropCols.add("_c3");

        //label column name..this will be user input
        String labelCol = "_c41";



        df = removeNonNumericFeatures(df, dropCols);
        Dataset<Row> target_df = df.drop(labelCol);
        df = df.drop(labelCol);

        for (String c : df.columns()) {
            if (c.equals(labelCol)) {
                continue;
            }
            df = df.withColumn(c, df.col(c).cast("double"));

        }
        //Dataset<Row> final_data = df.persist(StorageLevel.MEMORY_AND_DISK_2());
        VectorAssembler assembler = new VectorAssembler().setInputCols(target_df.columns()).setOutputCol("features");
        //df = df.crossJoin(target_df);
        //df = df.join(target_df);
        Dataset<Row> input_data = assembler.transform(df);
        //Dataset<Row> final_data = input_data.persist(StorageLevel.MEMORY_ONLY());

        StandardScaler standardScaler = new StandardScaler()
                .setInputCol("features")
                .setOutputCol("scaledFeatures")
                .setWithStd(true);
        StandardScalerModel standardScalerModel =  standardScaler.fit(input_data);
        Dataset<Row> finalClusterData = standardScalerModel.transform(input_data).persist(StorageLevel.MEMORY_ONLY());


        System.out.println("*************************Starting KMeans calculations*********************************" );
        KMeans kmeans = new KMeans().setFeaturesCol("features").setK(23).setInitMode("random").setMaxIter(10);
        //KMeansModel model = kmeans.fit(input_data);

        //double WSSSE = model.computeCost(input_data);
        int startIter = 30;
        int endIter = 100;
        int step = 10;

        for(int iter = startIter; iter <= endIter; iter += step) {
            KMeans kMeans = new KMeans().setFeaturesCol("scaledFeatures")
                    .setK(iter)
                    .setMaxIter(10)
                    .setInitMode("random");
            KMeansModel model = kMeans.fit(finalClusterData);
            double WSSSE = model.computeCost(finalClusterData);
            System.out.print("With k{" + iter + "}");
            System.out.println("*******Sum of Squared Errors = " + WSSSE);
            System.out.println("----------------------------------------------------------");

        }
        spark.stop();
    }

    public static Dataset<Row> removeNonNumericFeatures(Dataset<Row> df, List<String> dropCols) {
        for (String col:dropCols
        ) {
            df = df.drop(col);
        }
        return df;
    }
}
