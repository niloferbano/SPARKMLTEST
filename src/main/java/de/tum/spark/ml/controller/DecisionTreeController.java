package de.tum.spark.ml.controller;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.tum.spark.ml.model.CollaborativeFiltering;
import de.tum.spark.ml.model.DecisionTree;
import de.tum.spark.ml.model.KMeansClustering;
import de.tum.spark.ml.model.KMeansClusteringData;
//import de.tum.spark.ml.service.DecisionTreeService;
import de.tum.spark.ml.service.DecisionTreeService;
import de.tum.spark.ml.service.KMeansService;
import de.tum.spark.ml.service.RecommendationService;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scala.math.Ordering;

import java.io.IOException;
import java.util.*;

@RestController
public class DecisionTreeController {


    @Autowired
    private DecisionTreeService decisionTreeService;
    @Autowired
    private KMeansService kMeansService;
    @Autowired
    private RecommendationService recommendationService;




    @RequestMapping(value = "/runModel", method = RequestMethod.POST)
    public DecisionTree createDecisionTreeModel(@RequestBody Map<String, Object> request) throws IOException {
        DecisionTree decisionTree = decisionTreeService.parseJsonData(request);
        if(decisionTree != null) {
            DecisionTree decisionTree1 = decisionTreeService.save(decisionTree);
            decisionTreeService.generateCode(decisionTree1);
            return decisionTree1;
        }
        return null;
    }

    @RequestMapping(value = "/runKmeans", method = RequestMethod.POST)
    public KMeansClustering createKMeansModel(@RequestBody Map<String, Object> request) throws IOException {
        KMeansClustering kMeansClusteringData = kMeansService.parseJsonData(request);
        if(kMeansClusteringData != null) {
            KMeansClustering kMeansClustering = kMeansService.save(kMeansClusteringData);
            kMeansService.generateCode(kMeansClustering);

            return kMeansClustering;
        }

        return null;
    }


    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    @JsonPropertyOrder()
    public String createRecommendationModel(@RequestBody Map<String, Object> request) throws IOException {
        CollaborativeFiltering collaborativeFiltering = recommendationService.parseJsonData(request);
        if (collaborativeFiltering != null) {
            CollaborativeFiltering collaborativeFiltering1 = recommendationService.save(collaborativeFiltering);
            recommendationService.generateCode(collaborativeFiltering1);
            return "SuccessFully jar file created";
        } else {
            return "Please send data in correct order. The correct order is : {modelName, featureExtraction, trainModel, saveModel}";
        }
    }
}
