package de.tum.spark.ml;

import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.storage.StorageLevel;
import scala.collection.Seq;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class SparkKMeansClustering {

    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Clustering")
                .config("spark.master", "local")
                .config("spark.driver.memory", "64g")
                .config("spark.default.parallelism", "1000")
                .getOrCreate();

        spark.conf();

        Dataset<Row> df = spark.read()
                .option("header", false)
                .option("inferSchema", true)
                .option("partition", 4)
                .csv("/Users/nilu/Downloads/kddcup.data");



        List<String> dropCols = new ArrayList<String>();

        /* These will be user inputs*/
        dropCols.add("_c1");
        dropCols.add("_c2");
        dropCols.add("_c3");

        //label column name..this will be user input
        String labelCol = "_c41";

        Dataset<Row> target_df = df.select(labelCol);
        df = df.drop(labelCol);

        df = removeNonNumericFeatures(df, dropCols);

        for (String c : df.columns()) {
            df = df.withColumn(c, df.col(c).cast("double"));
        }
        Dataset<Row> final_data = df.persist(StorageLevel.MEMORY_AND_DISK_2());
        String[] cols = final_data.columns();
        System.out.println(cols);
        VectorAssembler assembler = new VectorAssembler().setInputCols(final_data.columns()).setOutputCol("features");
        final_data = final_data.crossJoin(target_df);
        Dataset<Row> input_data = assembler.transform(final_data);
        final_data = input_data.persist(StorageLevel.MEMORY_AND_DISK());

        System.out.println("******Starting KMeans calculations*******" );
        KMeans kmeans = new KMeans().setFeaturesCol("features").setK(23).setSeed(1L);
        KMeansModel model = kmeans.fit(final_data);

        double WSSSE = model.computeCost(final_data);
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);
    }

    public static Dataset<Row> removeNonNumericFeatures(Dataset<Row> df, List<String> dropCols) {
        for (String col:dropCols
             ) {
            df = df.drop(col);
        }
        return df;
    }
}
