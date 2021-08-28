import Entity.Entity;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> inputFileList = new ArrayList<>();
        ArrayList<String> outputFileList = new ArrayList<>();
        inputFileList.add("src/Entity/DataSources/InputDataSources/testInputNode.txt");
        inputFileList.add("src/Entity/DataSources/InputDataSources/testInputEdge.txt");
        inputFileList.add("src/Entity/DataSources/InputDataSources/testInputQueue.txt");
        outputFileList.add("src/Entity/DataSources/OutputDataSources/testOutputNode.txt");
        outputFileList.add("src/Entity/DataSources/OutputDataSources/testOutputEdge.txt");
        outputFileList.add("src/Entity/DataSources/OutputDataSources/testOutputQueue.txt");
        //Entity.makeInputFiles();

        Entity entity = new Entity(inputFileList, outputFileList);
        entity.runCircuit();
    }
}
