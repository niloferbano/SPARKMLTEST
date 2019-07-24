//
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.SparkSession;
//
//
//public class SimpleApp {
//    public static void main(String[] args) {
//        String logFile = "/Users/nilu/Documents/Project/SparkMLTest/README.md";
//        SparkSession spark = SparkSession.builder().appName("Simple Application").getOrCreate();
//        Dataset<String> logData = spark.read().textFile(logFile).cache();
//        System.out.println(logData);
//
//        long numAs = logData.filter(s -> s.contains("a")).count();
//        long numBs = logData.filter(s -> s.contains("b")).count();
//
//        System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
//
//        spark.stop();
//    }
//}
