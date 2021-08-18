import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> inputFileList = new ArrayList<>();
        ArrayList<String> outputFileList = new ArrayList<>();
        HashMap<LevelData, HashMap<Node, HashSet<Edge>>> testResult;
        ArrayList<HashMap<Node, HashSet<Edge>>> testResult2;
        inputFileList.add("testInputNode.txt");
        inputFileList.add("testInputEdge.txt");
        inputFileList.add("testInputQueue.txt");
        outputFileList.add("testOutputNode.txt");
        outputFileList.add("testOutputEdge.txt");
        outputFileList.add("testOutputQueue.txt");

        Entity entity = new Entity(inputFileList, outputFileList);
        entity.visualiseCircuit();
        System.out.println(entity.returnQueueDataStorage());

        HashMap<Integer, Integer> teMap = new HashMap<>();
        teMap.put(1,1);
        teMap.put(1,2);
        System.out.println(teMap);
        System.out.println(teMap.remove(2));
        System.out.println(teMap);
    }
}
