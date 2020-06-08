package de.tum.spark.ml.controller;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.tum.spark.ml.model.CollaborativeFiltering;
import de.tum.spark.ml.model.DecisionTree;
import de.tum.spark.ml.model.KMeansClustering;
import de.tum.spark.ml.service.DecisionTreeService;
import de.tum.spark.ml.service.KMeansService;
import de.tum.spark.ml.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin(origins="http://localhost:4200")
public class Controller {


    @Autowired
    private DecisionTreeService decisionTreeService;
    @Autowired
    private KMeansService kMeansService;
    @Autowired
    private RecommendationService recommendationService;


    /**
     * Invokes service to generate Decision Tree ML application
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/runModel", method = RequestMethod.POST)
    public String createDecisionTreeModel(@RequestBody Map<String, Object> request) throws IOException {
        String response = null;
        try{
            DecisionTree decisionTree = decisionTreeService.parseJsonData(request);
            if(decisionTree != null) {
                DecisionTree decisionTree1 = decisionTreeService.save(decisionTree);
                String path = decisionTreeService.generateCode(decisionTree1);
                response = "Source and Jar files are created at path " + path;
            }
            else {
            response =  "Please send data in correct order. The correct order is : {modelName, featureExtraction, trainModel, saveModel}";
            }
         }
        catch(IOException err){
            System.out.println("Application creation failed");
        }
        return response;
    }

    /**
     * Invokes service to generate Kmeans clustering ML application
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/runKmeans", method = RequestMethod.POST)
    public String  createKMeansModel(@RequestBody Map<String, Object> request) throws IOException {
        KMeansClustering kMeansClusteringData = kMeansService.parseJsonData(request);
        if(kMeansClusteringData != null) {
            KMeansClustering kMeansClustering1 = kMeansService.save(kMeansClusteringData);
            String path = kMeansService.generateCode(kMeansClustering1);
            return "Source and jar files are created at path: " + path;
        }
         return "Please send data in correct order. The correct order is : {modelName, featureExtraction, trainModel, saveModel}";;
    }


    /**
     * Invokes service to generate Collaborative filtering  ML application
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    @JsonPropertyOrder()
    public String createRecommendationModel(@RequestBody Map<String, Object> request) throws IOException {
        CollaborativeFiltering collaborativeFiltering = recommendationService.parseJsonData(request);
        if (collaborativeFiltering != null) {
            CollaborativeFiltering collaborativeFiltering1 = recommendationService.save(collaborativeFiltering);
            String path = recommendationService.generateCode(collaborativeFiltering1);
            return "Source and Jar files are created at path: " + path;
        } else {
            return "Please send data in correct order. The correct order is : {modelName, featureExtraction, trainModel, saveModel}";
        }
    }
}
