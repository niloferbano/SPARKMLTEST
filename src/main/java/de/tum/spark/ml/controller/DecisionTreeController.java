package de.tum.spark.ml.controller;

import de.tum.spark.ml.model.decisionTreeModel.DecisionTree;
import de.tum.spark.ml.service.DecisionTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DecisionTreeController {

    @Autowired
    private DecisionTreeService decisionTreeService;

    @RequestMapping(value = "/runModel", method = RequestMethod.POST)
    public DecisionTree getTrainingData(@RequestBody DecisionTree decisionTreeDto) throws IOException {
        System.out.println("========> In Controller: " + decisionTreeDto.getFeatureExtractionDto().toString());
        DecisionTree decisionTree = decisionTreeDto;
        DecisionTree decisionTree1 = decisionTreeService.save(decisionTree);
        decisionTreeService.generateCode(decisionTree);
        return decisionTree1;
    }
}
