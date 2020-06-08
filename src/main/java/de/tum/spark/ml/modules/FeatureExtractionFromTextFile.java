package de.tum.spark.ml.modules;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import de.tum.spark.ml.codegenerator.InputOutputMapper;
import de.tum.spark.ml.codegenerator.JavaCodeGenerator;
import de.tum.spark.ml.model.FeatureExtractionFromTextFileDto;

import java.util.LinkedHashMap;
import java.util.Map;

public class FeatureExtractionFromTextFile {
    /**
     * Generates code for feature extraction step
     * This code generation works on text files as input data source
     * @param inputOutputMapper
     * @param featureExtractionDto
     * @param javaCodeGenerator
     * @return
     */
    public static Map<String, InputOutputMapper> getJavaCode(InputOutputMapper inputOutputMapper,
                                                      FeatureExtractionFromTextFileDto featureExtractionDto,
                                                      JavaCodeGenerator javaCodeGenerator) {

        ParameterizedTypeName datasetRow = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.sql", "Dataset"),
                ClassName.get("org.apache.spark.sql", "Row")
        );

        CodeBlock.Builder code = CodeBlock.builder();
        Map<String, Object> codeVariables = new LinkedHashMap<>();
        codeVariables.put("sparkSession", inputOutputMapper.getVariableName());



        ParameterizedTypeName javaRDD = ParameterizedTypeName.get(
                ClassName.get("org.apache.spark.api.java", "JavaRDD"),
                ClassName.get("de.tum.in.sparkml."+ javaCodeGenerator.getClassName(), "Rating")
        );
        codeVariables.put("javaRDDType", javaRDD);
        codeVariables.put("javaRDDVariable", JavaCodeGenerator.newVariableName());
        codeVariables.put("datasetRow", datasetRow);
        codeVariables.put("rawAliasData", JavaCodeGenerator.newVariableName());
        codeVariables.put("aliassep", featureExtractionDto.getAliasFilePath().get("separator"));
        codeVariables.put("aliasFile", featureExtractionDto.getAliasFilePath().get("filePath"));
        codeVariables.put("ratingClass", ClassName.get("de.tum.in.sparkml."+ javaCodeGenerator.getClassName(), "Rating"));

        code.addNamed("$datasetRow:T $rawAliasData:L = $sparkSession:L.read()" +
                ".option(\"sep\", $aliassep:S)\n" +
                ".option(\"emptyValue\", null)\n" +
                ".csv($aliasFile:S);\n", codeVariables);

        codeVariables.put("filterFunction", ClassName.get("org.apache.spark.api.java.function","FilterFunction"));

        code.addNamed("$rawAliasData:L = $rawAliasData:L.filter(new $filterFunction:T<Row>() {\n" +
                "   @Override\n" +
                "   public boolean call(Row row) throws Exception {\n" +
                "   if (row.getString(0) == null || row.getString(1) ==null) {\n" +
                "       return false;\n" +
                "   }\n" +
                "       return true;\n" +
                "   }\n" +
                "});\n", codeVariables);
        codeVariables.put("forEachFunction", ClassName.get("org.apache.spark.api.java.function","ForeachFunction"));


        code.addNamed("$rawAliasData:L.foreach(($forEachFunction:T<Row>) row -> {\n" +
                "updateArtistAlias(Integer.parseInt(row.getString(0)), Integer.parseInt(row.getString(1)));\n" +
                "});\n", codeVariables);

        codeVariables.put("sourceData", featureExtractionDto.getSourceFilePath().get("filePath"));

        code.addNamed("$javaRDDType:T $javaRDDVariable:L = $sparkSession:L.read()\n" +
                ".textFile($sourceData:S)\n" +
                ".javaRDD()\n" +
                ".map(Rating::parseRating);\n", codeVariables);


        codeVariables.put("ratings", JavaCodeGenerator.newVariableName());

        code.addNamed("$datasetRow:T $ratings:L = $sparkSession:L.createDataFrame($javaRDDVariable:L,$ratingClass:T.class );\n", codeVariables);


        javaCodeGenerator.getMainMethod().addCode(code.build());

        Map<String, InputOutputMapper> returnValues = new LinkedHashMap<>();
        returnValues.put("MainSourceFile", new InputOutputMapper(datasetRow, codeVariables.get("ratings").toString()));
        returnValues.put("AliasFile", new InputOutputMapper(datasetRow, codeVariables.get("rawAliasData").toString()));


        return returnValues;
    }
}
