package de.tum.spark.ml;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import java.io.Serializable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SparkCollaborativeFiltering {

    private  static Map<Integer, Integer> artistAliasMap = new HashMap<>();

    public static class Rating implements Serializable {
        private Integer userId;
        private Integer artistId;
        private Integer count;

        public Rating() {}

        public Rating(Integer userId, Integer artistId, Integer count) {
            this.userId = userId;
            this.artistId = artistId;
            this.count = count;
        }
        public Integer getUserId() {
            return userId;
        }

        public Integer getArtistId() {
            return artistId;
        }

        public Integer getCount() {
            return count;
        }


        public static Rating parseRating(String str) {
            String[] fields = str.split(" ");
            if (fields.length != 3) {
                throw new IllegalArgumentException("Each line must contain 3 fields");
            }
            Integer userId = Integer.parseInt(fields[0]);
            Integer artistId = Integer.parseInt(fields[1]);
            Integer count = Integer.parseInt(fields[2]);
            Integer finalArtistData = SparkCollaborativeFiltering.artistAliasMap.getOrDefault(artistId, artistId);
            return new Rating(userId, finalArtistData, count);
        }
    }

    public static void main(String[] args) {


        SparkSession spark = SparkSession
                .builder()
                .appName("Recommendation")
                .config("spark.master", "local[*]")
                .config("spark.driver.memory", "16g")
                .config("spark.default.parallelism", "8")
                .config("spark.driver.bindAddress", "127.0.0.1")
                .getOrCreate();


        Dataset<Row>  rawArtistData  = spark.read()
                .option("sep", "\t")
                .option("ignoreLeadingWhiteSpace",true)
                .option("ignoreTrailingWhiteSpace",true)
                .option("emptyValue", null)
                .csv("/Users/coworker/Downloads/profiledata_06-May-2005/artist_data.txt");



        rawArtistData = rawArtistData.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row row) throws Exception {
                if (row.getString(0) == null) {
                    return false;
                }
                return true;
            }
        });
        rawArtistData = rawArtistData.withColumn("_c0",rawArtistData.col("_c0").cast("int"));
        rawArtistData = rawArtistData.withColumn("_c1",(rawArtistData.col("_c1").cast("String")));

        Dataset<Row>  rawArtistAliasData  = spark.read()
                .option("sep", "\t")
                //.option("ignoreLeadingWhiteSpace",true)
                //.option("ignoreTrailingWhiteSpace",true)
                .option("emptyValue", null)
                .csv("/Users/coworker/Downloads/profiledata_06-May-2005/artist_alias.txt");


        rawArtistAliasData = rawArtistAliasData.filter(new FilterFunction<Row>() {
            @Override
            public boolean call(Row row) throws Exception {
                if (row.getString(0) == null || row.getString(1) == null) {
                    return false;
                }
                return true;
            }
        });

        rawArtistAliasData.foreach((ForeachFunction<Row>) row -> {
            updateArtistAlias(Integer.parseInt(row.getString(0)), Integer.parseInt(row.getString(1)));
        });

        JavaRDD<Rating> userArtistData = spark.read()
                .textFile("/Users/coworker/Downloads/profiledata_06-May-2005/user_artist_data.txt")
                .javaRDD()
                .map(Rating::parseRating);

        Dataset<Row> ratings = spark.createDataFrame(userArtistData, Rating.class).cache();



        Dataset<Row>[] splits = ratings.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> training = splits[0];
        Dataset<Row> test = splits[1];


        LinkedList<Integer> ranks = new LinkedList<Integer>();
        ranks.add(10);
        ranks.add(50);

        LinkedList<Double> regParams = new LinkedList<Double>();
        regParams.add(1.0);
        regParams.add(0.0001);

        LinkedList<Double> alphas = new LinkedList<>();

        alphas.add(1.0);
        alphas.add(40.0);

        for( Integer rank: ranks){
            for( Double regParam: regParams) {
                for( Double alpha: alphas) {
                    ALS als = new ALS()
                            .setMaxIter(10)
                            .setAlpha(alpha)
                            .setRegParam(regParam)
                            .setRank(rank)
                            .setUserCol("userId")
                            .setItemCol("artistId")
                            .setRatingCol("count");

                    ALSModel alsModel = als.fit(training);
                    alsModel.setColdStartStrategy("drop");
                    Dataset<Row> predictions = alsModel.transform(test);

                    RegressionEvaluator evaluator = new RegressionEvaluator()
                            .setMetricName("rmse") //rmse, mse, mae, r2
                            .setLabelCol("count")
                            .setPredictionCol("prediction");
                    Double rmse = evaluator.evaluate(predictions);
                    System.out.println("Hyper Params = (" + rank + " " + alpha + " " + regParam + ")");
                    System.out.println("Root-mean-square error = " + rmse);
                }
            }
        }

    }

    private static void updateArtistAlias(Integer key, Integer value) {
        SparkCollaborativeFiltering.artistAliasMap.put(key, value);
    }

}
