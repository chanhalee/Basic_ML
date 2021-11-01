import Entity.Entity;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> inputFileList = new ArrayList<>();
        ArrayList<String> outputFileList = new ArrayList<>();
        //inputFileList.add("src/DataSources/InputDataSources/testInputNode_1.txt");
        //inputFileList.add("src/DataSources/InputDataSources/testInputEdge_1.txt");
        //inputFileList.add("src/DataSources/InputDataSources/testInputQueue_1.txt");
        inputFileList.add("src/DataSources/InputDataSources/testInputNode.txt");
        inputFileList.add("src/DataSources/InputDataSources/testInputEdge.txt");
        inputFileList.add("src/DataSources/InputDataSources/testInputQueue.txt");
        outputFileList.add("src/DataSources/OutputDataSources/testOutputNode.txt");
        outputFileList.add("src/DataSources/OutputDataSources/testOutputEdge.txt");
        outputFileList.add("src/DataSources/OutputDataSources/testOutputQueue.txt");
        //Entity.makeInputFiles();

        Entity entity = new Entity(inputFileList, outputFileList);
        entity.run();
    }
}
