import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> inputFileList = new ArrayList<>();
        ArrayList<String> outputFileList = new ArrayList<>();
        HashMap<LevelData, HashMap<Node, HashSet<Edge>>> testResult;
        inputFileList.add("testInputNode.txt");
        inputFileList.add("testInputEdge.txt");
        outputFileList.add("testOutputNode.txt");
        outputFileList.add("testOutputEdge.txt");

        Entity entity = new Entity(inputFileList, outputFileList);
        testResult = entity.returnMindCircuit();
        System.out.println(testResult);
    }
}
