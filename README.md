
# FLow based programming for automatic ML application creation
## Getting Started
### Prerequsite
 * JDK 8 (set JAVA_HOME environment variable)
 * Maven
 * Node
 * angular cli (version 8.3.18)
 * Mongodb

### Clone
To get started you can simply clone this repository using git:
```
git clone https://github.com/niloferbano/Flow-based-Spark-ML.git
cd Flow-based-Spark-ML
```
* Start the backend
```
Change the path depending on your installation path MAVEN_PATH in 
src/main/java/de/tum/spark/ml/codegenerator/MavenBuild.java file
mvn clean install
mvn spring-boot:run or java -jar target/your-file.jar or you can use
choice of your IDE to run the application.
```
* Start the frontend
```
Navigate to path : Flow-based-Spark-ML/src/main/resources/frontend/sparkML-ui
npm start
Browse to http://localhost:4200
```

### Creating an ML application jar file from front end
* Go to http://localhost:4200
* There are four steps involved in creating a flow for application generation. 
  1. Start
  2. FeatureExtraction/FeatureExtractionFromTextFile
  3. ML model of your choice(Decision Tree/ KMeans/ Collaborative filtering) 
  4. Save Model
* After creating a flow by dragging and dropping on the pipeline panel.
  Click Generate Jar button to submit the job.
* You will get either successful message with path to the package/code 
  created by the application or a failure message in case something goes wrong.

### Links to the data used for the use cases
* Decision Tree use case: https://archive.ics.uci.edu/ml/machine-learning-databases/covtype/
* KMeans Clustering use case: https://kdd.ics.uci.edu/databases/kddcup99/kddcup99.html
* Collaborative Filtering use case: https://storage.googleapis.com/aas-data-sets/profiledata_06-May-2005.tar.gz



