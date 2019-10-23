package de.tum.spark.ml.controller;

import de.tum.spark.ml.model.decisionTreeModel.DecisionTree;
import de.tum.spark.ml.model.decisionTreeModel.KMeansClustering;
import de.tum.spark.ml.model.decisionTreeModel.KMeansClusteringData;
//import de.tum.spark.ml.service.DecisionTreeService;
import de.tum.spark.ml.service.KMeansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DecisionTreeController {

    //@Autowired
    //private DecisionTreeService decisionTreeService;
    @Autowired
    private KMeansService kMeansService;

//    @RequestMapping(value = "/runModel", method = RequestMethod.POST)
//    public DecisionTree createDecisionTreeModel(@RequestBody DecisionTree decisionTree) throws IOException {
//        System.out.println("========> In Controller: " + decisionTree.getFeatureExtraction().toString());
//        DecisionTree decisionTree1 = decisionTreeService.save(decisionTree);
//        decisionTreeService.generateCode(decisionTree1);
//        return decisionTree1;
//    }

    @RequestMapping(value = "/runKmeans", method = RequestMethod.POST)
    public KMeansClustering createKMeansModel(@RequestBody KMeansClusteringData kMeansClusteringData) throws IOException {
        KMeansClustering kMeansClustering = new KMeansClustering(kMeansClusteringData);
        KMeansClustering kMeansClusteringdata = kMeansService.save(kMeansClustering);
        kMeansService.generateCode(kMeansClusteringdata);
        return kMeansClusteringdata;
    }
}
