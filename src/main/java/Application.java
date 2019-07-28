import org.apache.spark.sql.SparkSession;

public class Application {
    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Test")
                .config("spark.master", "local")
                .getOrCreate();

    }
}
