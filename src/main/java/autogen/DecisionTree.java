package autogen;

import java.lang.String;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class DecisionTree {
  public static void main(String[] args) {
            SparkSession spark = SparkSession
                    .builder()
                    .appName("Test")
                    .config("spark.master", "local")
                    .getOrCreate();}

  public static Dataset<Row> featureExtraction(SparkSession sparkSession, String filePath,
      String labelColName) {
  }
}
