
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
Change the path depending on your installation path ```MAVEN_PATH``` in src/main/java/de/tum/spark/ml/codegenerator/MavenBuild.java file
mvn clean install
mvn spring-boot:run or java -jar target/your-file.jar
```
* Start the frontend
```
Navigate to path : Flow-based-Spark-ML/src/main/resources/frontend/sparkML-ui
npm start
Browse to http://localhost:4200
```

### Links to the data used for the use cases
* Decision Tree use case: https://archive.ics.uci.edu/ml/machine-learning-databases/covtype/
* KMeans Clustering use case: https://kdd.ics.uci.edu/databases/kddcup99/kddcup99.html
* Collaborative Filtering use case: https://storage.googleapis.com/aas-data-sets/profiledata_06-May-2005.tar.gz


