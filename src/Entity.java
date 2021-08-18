
/* ---------------------------------------------
## 목차
1. 개체의 기본형
2.* 개체에 관련된 데이터 저장소
2.1. LevelData
3.* 개체의 동작에 필요한 클래스
3.1.파일 입출력 관련 함수

----------------------------------------------- */
//
//개체에 기본형을 만든 이유는 기본형을 상속한 여러 성격의 객체를 구현할 계획이 있기 때문이다.
// ex) 덤벙대는 개체, 예민한 개체, 우두머리 개체 등..
//
//
//idea) input 이 첫 레벨에서 이루어지는게 아니라 회로의 중간에서도 이루어질 수 있다면?
//re-idea) 차라리 레벨이 연속적으로(0->1->2->3) 이어지는게 아니라 유기적으로 이어지게 한다면? (0->2->4)
// 도전과제) 역행구현 *고려1: 무한순환 방지 (아웃풋 노드까지 경로 길이 계산 구현) 무한일 경우 센티넬 개입 (경로 파괴, 경로 조작, 구간반복 해제)
// 도전과제2) 전달 속도 다양화 (빠른 생각, 느린 생각)
//re-re-idea) level0는 인풋으로 고정! -> 자극 전달을 레벨과 무관하게 진행되도록
//
//
//

import java.util.*;
import java.io.*;
import static java.lang.System.exit;
import java.util.logging.Level;
/*-------------------------------1.개체의 기본형-------------------------------*/

public class Entity {
    private HashMap<LevelData, HashMap<Node, HashSet<Edge>>> mindCircuit; // (levelData -(Node - [Edge]))의 구성.
    private ArrayList<HashMap<Node, HashSet<Edge>>> circuitList;
    private HashSet<Node> previousSparkedNode = new HashSet<>();//여기도 방파제 모델 적용 지난 2회기의 흥분노드 저장
    private HashSet<Node> currentSparkedNode = new HashSet<>();
    private QueueDataStorage queueData;
    ArrayList<String> inputFileList;
    ArrayList<String> outputFileList;

    Entity(ArrayList<String> inputFileList, ArrayList<String> outputFileList){
        this.inputFileList = inputFileList;
        this.outputFileList = outputFileList;
        mindCircuit =  Matcher(readNodeFromFile(inputFileList.get(0)), readEdgeFromFile(inputFileList.get(1)));
        circuitList = makeCircuitListFromMap(mindCircuit);
        queueData = readInputQueue(inputFileList.get(2));
    }

    public HashMap<LevelData, HashMap<Node, HashSet<Edge>>> returnMindCircuit(){    // 프로젝트 완성시 삭제할것!
        return mindCircuit;
    }
    public ArrayList<HashMap<Node, HashSet<Edge>>> returnCircuitList() {return circuitList;}
    public QueueDataStorage returnQueueDataStorage(){ return queueData;}

    public void visualiseCircuit(){
        int level = 0;
        System.out.println("회로 도식화");
        if(circuitList.isEmpty()) {
            System.out.println("빈 회로!");
            return;
        }
        for(HashMap<Node, HashSet<Edge>> nhm : circuitList){
            System.out.println("Level: " + level++);
            if(nhm == null)
                continue;
            for(Node n: nhm.keySet()){
                System.out.print("\t\t ");
                System.out.println(n);
                HashSet<Edge> ehs = nhm.get(n);
                if(ehs == null)
                    continue;
                for(Edge e: ehs){
                    System.out.print("\t\t\t\t\t\t\t\t\t\t\t  ");
                    System.out.println(e);
                }
            }
        }
        System.out.println();
        System.out.println();
    }


    private HashMap<LevelData, HashSet<Node>> readNodeFromFile(String inputFile){// Node 만 읽어 ArrayList 로 저장, level 과 묶어 map 을 만들어 반환.
        HashMap<LevelData, HashSet<Node>> result = new HashMap<>();
        LevelData levelData = null;
        HashSet<Node> nodeData = null;
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                if(fileLine.trim().startsWith("##")) {    //라인의 가장 첫번째에 오는 ## 새로운 레벨에 속하는 노드의 입력임을 나타냄.
                    fileLine = fileLine.replace("#", "");
                    fileLine = fileLine.replace("[", "");
                    splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                    levelData = makeLevelDataFromAList(splitInputData);
                    nodeData = new HashSet<Node>();
                    result.put(levelData, nodeData);
                    continue;
                }
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                assert nodeData != null;        // 컴파일러가 처리한 코드. assert 명령어 공부 필요.
                nodeData.add(makeNodeFromAList(splitInputData));
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
        return result;
    }

    private LevelData makeLevelDataFromAList(ArrayList<String> data){
        LevelData levelData = null;
        levelData = new LevelData(Integer.parseInt(data.get(0).trim()), Integer.parseInt(data.get(1).trim()), data.get(2).trim());
        return levelData;
    }

    private Node makeNodeFromAList(ArrayList<String> data){
        Node node = null;
        int nodeFormat = Integer.parseInt(data.get(0).trim());
        try {
            node = switch (nodeFormat) {
                case InputNodeInter.NODE_FORMAT -> new InputNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()), Boolean.parseBoolean(data.get(4).trim()));
                case ProcessNodeInter.NODE_FORMAT -> new ProcessNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()));
                case OutputNodeInter.NODE_FORMAT -> new OutputNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()));
                default -> throw new InValidNodeFormatException("정의되지 않은 NODE_FORMAT 입력. 값: " + nodeFormat);
            };
        } catch(InValidNodeFormatException ne){
            System.out.println("에러 메시지: " + ne.getMessage());
            ne.printStackTrace();
        }
        return node;
    }


    private HashMap<Integer, HashSet<Edge>> readEdgeFromFile(String inputFile){  // 파일로부터 엣지 정보를 읽어 시작노드 시리얼과 엣지를 묶어 저장.
        HashMap<Integer, HashSet<Edge>> result = new HashMap<>();
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                if(!result.containsKey(Integer.valueOf(splitInputData.get(1)))){
                    HashSet<Edge> tempHashSet = new HashSet<Edge>();
                    result.put(Integer.valueOf(splitInputData.get(1)), tempHashSet);
                }
                result.get(Integer.valueOf(splitInputData.get(1))).add(makeEdgeFromAList(splitInputData));
            }
        }catch (IOException ie){
            ie.printStackTrace();
        }
        return result;
    }


    private Edge makeEdgeFromAList(ArrayList<String> data){
        Edge edge = null;
        Integer startNodeSerial;
        int edgeFormat = Integer.parseInt(data.get(0).trim());
        try {
            if(edgeFormat == 1) {
                if (data.size() == 5)
                    edge = new Edge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)));
                else if (data.size() == 4)
                    edge = new Edge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)));
                else
                    throw new InValidEdgeFormatException("정의되지 않은 Edge 생성자 매개변수 개수 값: " + data.size());
            }else if(edgeFormat == 2){
                if(data.size() == 6)
                    edge = new LoopEdge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Integer.parseInt(data.get(5)));
                else if(data.size() == 5)
                    edge = new LoopEdge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Integer.parseInt(data.get(4)));
                else throw new InValidEdgeFormatException("정의되지 않은 LoopEdge 생성자 매개변수 개수 값: " + data.size());
            }
            else throw new InValidEdgeFormatException("정의되지 않은 EDGE_FORMAT 입력. 값: " + edgeFormat);
        } catch(InValidEdgeFormatException ee){
            System.out.println("에러 메시지: " + ee.getMessage());
            ee.printStackTrace();
        }
        return edge;
    }


    private HashMap<LevelData, HashMap<Node, HashSet<Edge>>> Matcher(HashMap<LevelData, HashSet<Node>> nodeMap, HashMap<Integer, HashSet<Edge>> edgeMap){
        HashMap<LevelData, HashMap<Node, HashSet<Edge>>> result = new HashMap<>();
        HashMap<Node, HashSet<Edge>> NodeNEdgeMap = new HashMap<>();
        HashSet<Node> tempNodeSet = new HashSet<>();
        for(LevelData ld: nodeMap.keySet()){
            result.put(ld, new HashMap<Node, HashSet<Edge>>());
        }
        for(HashSet<Node> i :nodeMap.values()) {
            tempNodeSet.addAll(i);
        }
        for(Node n : tempNodeSet){
            NodeNEdgeMap.put(n, null);
            for(Integer i : edgeMap.keySet()){
                if(n.SERIAL_NUMBER == i){
                    NodeNEdgeMap.replace(n, edgeMap.get(i));
                    //실행속도를 높이기 위해 아래 두줄을 만들었었는데 foreach문 안에서 foreach의 소스를 건드리는 것이 예외를 발생시킴!
                    // 해당 매칭 함수에서 상당한 시간이 소모될 것으로 예상되어 향후 개선이 필요함.
/*                    edgeMap.remove(i);
                    tempNodeSet.remove(n);*/
                }
            }
        }
        for(LevelData ld: nodeMap.keySet()){
            HashSet<Node> ns = nodeMap.get(ld);
            for(Node n : NodeNEdgeMap.keySet()){
                if(ns.contains(n)){
                    result.get(ld).put(n, NodeNEdgeMap.get(n));
                }
            }
        }
        return result;
    }

    private ArrayList<HashMap<Node, HashSet<Edge>>> makeCircuitListFromMap(HashMap<LevelData, HashMap<Node, HashSet<Edge>>> map){
        ArrayList<HashMap<Node, HashSet<Edge>>> result = new ArrayList<HashMap<Node, HashSet<Edge>>>();
        Set<LevelData> keySet = map.keySet();
        Iterator<LevelData> iter;
        LevelData curVal;
        LevelData minVal;
        LevelData prevMinVal = new LevelData(0, 0, "");
        while(keySet.size() != result.size()) {
            iter = keySet.iterator();
            curVal = iter.next();
            while(curVal.hashCode() <= prevMinVal.hashCode()){
                curVal = iter.next();
            }
            minVal = curVal;
            iter = keySet.iterator();
            while(iter.hasNext()){
                curVal = iter.next();
                if(curVal.hashCode() < minVal.hashCode() && curVal.hashCode() > prevMinVal.hashCode()){
                    minVal = curVal;
                }
            }
            result.add(map.get(minVal));
            prevMinVal = minVal;
        }
        return result;
    }

    private QueueDataStorage readInputQueue(String inputFile) {
        QueueDataStorage result;
        LinkedList<ArrayList<Boolean>> inputDataList = new LinkedList<>();
        ArrayList<Integer> inputNodeSerials = new ArrayList<>();
        ArrayList<String> splitInputData;
        String fileLine = "";
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++) {  /// 파일에서 라인 읽어오기
                if (fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                if (fileLine.trim().startsWith("##")){
                    fileLine = fileLine.replace("#", "");
                    fileLine = fileLine.replace("[", "");
                    splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                    for(String s : splitInputData){
                        inputNodeSerials.add(Integer.valueOf(s.trim()));
                    }
                    continue;
                }
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                inputDataList.add(makeQueueFromSplitData(splitInputData));
            }
        } catch (IOException ie){
            ie.printStackTrace();
        }
        result = new QueueDataStorage(inputNodeSerials, inputDataList);
        return result;
    }
    private ArrayList<Boolean> makeQueueFromSplitData(ArrayList<String> data){
        ArrayList<Boolean> result = new ArrayList<>();
        try {
            if(data.size() != circuitList.get(0).size()){
                throw new InValidInputQueueFormatException("입력의 갯수와 인풋노드의 개수가 불일치합니다. 입력 갯수: " + data.size() + ", 필요 갯수: "+ circuitList.get(0).size());
            }
            for(String s : data){
                result.add(Boolean.parseBoolean(s));
            }
        }catch(InValidInputQueueFormatException qe){
            System.out.println("에러 메시지: " + qe.getMessage());
            qe.printStackTrace();
        }
        return result;
    }


    public void runCircuit(){
        HashMap<Integer, Boolean> input;

        try{
            input = queueData.getNextQueue();
        }catch (EndOfQueueException eqe){
            System.out.println("** 회기 종료 **");
            return;
        }


    }

    private Node findMatchingNode(Integer serial){
        for(HashMap<Node, HashSet<Edge>> h : circuitList)
            for(Node n : h.keySet())
                if(n.matches(serial))
                    return n;
        return null;
    }


}
























/*-------------------------------2.*개체에 관련된 데이터 저장소-------------------------------*/

/*-------------------------------2.1.LevelData-------------------------------*/

class LevelData{
    private final int NODE_FORMAT;
    private final int LEVEL;
    private final String LEVEl_NAME;

    LevelData(int nodeFormat, int level, String name) {

        this.NODE_FORMAT = nodeFormat;
        this.LEVEL = level;
        this.LEVEl_NAME = name;
    }


    @Override
    public int hashCode() {
        return (int) (NODE_FORMAT * 1000 + LEVEL);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof LevelData) {
            LevelData that = (LevelData) obj;
            if (this.hashCode() == that.hashCode())
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + this.hashCode();
    }

}


/*-------------------------------3.* 개체의 동작에 필요한 클래스-------------------------------*/

/*-------------------------------3.1.inputQueue 관련 예외 클래스-------------------------------*/

class InValidInputQueueFormatException extends RuntimeException{
    InValidInputQueueFormatException(String msg){
        super(msg);
    }
    InValidInputQueueFormatException(){
        super("InValidInputQueueFormatException");
    }
}
class EndOfQueueException extends RuntimeException{
    EndOfQueueException(String msg){
        super(msg);
    }
    EndOfQueueException(){
        super("EndOfQueueException");
    }
}

/*-------------------------------3.2 회로 진행 데이터를 관리하는 클래스-------------------------------*/
class QueueDataStorage{
    private ArrayList<Integer> inputNodeSerials;
    private LinkedList<ArrayList<Boolean>> inputQueue;
    private int cycleCounter = 0;
    private int totalInputCycle;
    private Iterator<ArrayList<Boolean>> queueIter;

    QueueDataStorage(ArrayList<Integer> inputNodeSerials, LinkedList<ArrayList<Boolean>> inputQueue){
        this.inputNodeSerials = inputNodeSerials;
        this.inputQueue = inputQueue;
        totalInputCycle = inputQueue.size();
        queueIter = this.inputQueue.iterator();
    }
    public HashMap<Integer, Boolean> getNextQueue() throws EndOfQueueException{
        HashMap<Integer, Boolean> result = new HashMap<>();
        if(queueIter.hasNext()){
            ArrayList<Boolean> al = queueIter.next();
            for(int i = 0; i < inputNodeSerials.size(); i++){
                result.put(inputNodeSerials.get(i), al.get(i));
            }
            queueIter.remove();
            cycleCounter ++;
            return result;
        }
        throw new EndOfQueueException();
    }

    @Override
    public String toString(){
        return inputNodeSerials.toString() +"\n"+ inputQueue.toString();
    }


}
