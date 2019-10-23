package de.tum.spark.ml;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

public class SparkRecommender {
    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("Clustering")
                .config("spark.master", "local[*]")
                .config("spark.driver.memory", "16g")
                .config("spark.default.parallelism", "8")
                .config("spark.driver.bindAddress", "127.0.0.1")
                .getOrCreate();


        JavaRDD<String> rawUserArtistData = spark.read().textFile("/Users/coworker/Downloads/profiledata_06-May-2005/user_artist_data.txt").javaRDD();

    }
}
