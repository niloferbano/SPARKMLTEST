package de.tum.spark.ml.controller;

import de.tum.spark.ml.SparkDecisionTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    @Autowired
    private SparkDecisionTree sparkDecisionTree;

   
}
