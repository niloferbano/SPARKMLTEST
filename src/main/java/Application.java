import de.tum.spark.ml.FileStorageProperties;
import org.apache.spark.sql.SparkSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);

        SparkSession spark = SparkSession
                .builder()
                .appName("Test")
                .config("spark.master", "local")
                .getOrCreate();

    }
}
