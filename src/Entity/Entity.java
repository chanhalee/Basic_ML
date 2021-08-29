package Entity;
/* ---------------------------------------------
## 목차
1. 개체의 기본형
2.* 개체에 관련된 데이터 저장소
2.1. Entity.LevelData
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

import Edge.*;
import Edge.Exceptions.InValidEdgeFormatException;
import Edge.Interfaces.EdgeInter;
import Node.*;
import java.util.*;
import java.io.*;
/*-------------------------------1.개체의 기본형-------------------------------*/

public class Entity {
    static final int WAVE_SUSTAIN_TICK = 2;
    static final double WAVE_REDUCE_COEFFICIENT = 0.6;
    private HashMap<LevelData, HashMap<Node, HashSet<Edge>>> mindCircuit; // (levelData -(Node.Node - [Edge.Edge]))의 구성.
    private ArrayList<HashMap<Node, HashSet<Edge>>> circuitList;
    private HashMap<Integer, Double> serialNCriticalSet;
    private HashMap<Integer, HashMap<Integer, SingleEntryMap<Double, Double>>> stimulationSumLogInACycle;
    private HashMap<Integer,SingleEntryMap<HashSet<Node>, HashSet<Edge>>> previousSparkedNode;//여기도 방파제 모델 적용. 지난 2회기의 흥분노드 저장
    private HashMap<Integer, HashMap<Integer, Double>> stimulationDeposit;
    private HashSet<Node> currentSparkedNode = new HashSet<>();
    private HashMap<Integer, HashSet<Integer>> cycleLog;
    private HashMap<Integer, HashMap<Integer, HashSet<Integer>>> totalSimulationLog = new HashMap<>();
    private QueueDataStorage queueData;
    ArrayList<String> inputFileList;
    ArrayList<String> outputFileList;

    public Entity(ArrayList<String> inputFileList, ArrayList<String> outputFileList){
        this.inputFileList = inputFileList;
        this.outputFileList = outputFileList;
        mindCircuit =  Matcher(readNodeFromFile(inputFileList.get(0)), readEdgeFromFile(inputFileList.get(1)));
        circuitList = makeCircuitListFromMap(mindCircuit);
        queueData = readInputQueue(inputFileList.get(2));
        serialNCriticalSet = makeSerialNCriticalSet();
        previousSparkedNode = new HashMap<>();
        for(int i = 1; i <= WAVE_SUSTAIN_TICK; i++){
            previousSparkedNode.put((-i), new SingleEntryMap<HashSet<Node>, HashSet<Edge>>(new HashSet<Node>(), new HashSet<Edge>()));
        }
        stimulationDeposit = new HashMap<>();
        for (int i = 0; i <= WAVE_SUSTAIN_TICK; i++) {
            stimulationDeposit.put(-i, makeDepositForAll());
        }

    }
/*
    public void visualiseCircuit(){
        int level = 0;
        System.out.println("회로 도식화");
        if(circuitList.isEmpty()) {
            System.out.println("빈 회로!");
            return;
        }
        for(HashMap<Node.Node, HashSet<Edge.Edge>> nhm : circuitList){
            System.out.println("Level: " + level++);
            if(nhm == null)
                continue;
            for(Node.Node n: nhm.keySet()){
                System.out.print("\t\t ");
                System.out.println(n);
                HashSet<Edge.Edge> ehs = nhm.get(n);
                if(ehs == null)
                    continue;
                for(Edge.Edge e: ehs){
                    System.out.print("\t\t\t\t\t\t\t\t\t\t\t  ");
                    System.out.println(e);
                }
            }
        }
        System.out.println();
        System.out.println();
    }*/


    private HashMap<LevelData, HashSet<Node>> readNodeFromFile(String inputFile){// Node.Node 만 읽어 ArrayList 로 저장, level 과 묶어 map 을 만들어 반환.
        HashMap<LevelData, HashSet<Node>> result = new HashMap<>();
        LevelData levelData = null;
        HashSet<Node> nodeData = null;
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        try {
            BufferedReader inputBuffer = new BufferedReader(new FileReader(new File(inputFile)));
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
            inputBuffer.close();
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
                case InputNodeInter.NODE_FORMAT -> new InputNode(data.get(1).trim(), Integer.parseInt(data.get(2).trim()), Double.parseDouble(data.get(3).trim()));
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
            inputBuffer.close();
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
                    throw new InValidEdgeFormatException("정의되지 않은 Edge.Edge 생성자 매개변수 개수 값: " + data.size());
            }else if(edgeFormat == 2){
                if(data.size() == 6)
                    edge = new LoopEdge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Double.parseDouble(data.get(4)), Integer.parseInt(data.get(5)));
                else if(data.size() == 5)
                    edge = new LoopEdge(Integer.parseInt(data.get(1)), Integer.parseInt(data.get(2)), Double.parseDouble(data.get(3)), Integer.parseInt(data.get(4)));
                else throw new InValidEdgeFormatException("정의되지 않은 Edge.LoopEdge 생성자 매개변수 개수 값: " + data.size());
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
                    n.setEdge(edgeMap.get(i));
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
            inputBuffer.close();
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
    private HashMap<Integer, Double> makeSerialNCriticalSet(){
        HashMap<Integer, Double> result = new HashMap<>();
        for (HashMap<Node, HashSet<Edge>> nm: circuitList){
            for(Node n: nm.keySet()){
                result.put(n.SERIAL_NUMBER, n.getCriticalPoint());
            }
        }
        return result;
    }
    private HashMap<Integer, Double> makeDepositForAll(){
        if(serialNCriticalSet == null){
            System.out.println("serialNCriticalSet 없음. makeSerialNCriticalSet 메서드 실행이 선행되어야 함.");
            throw new RuntimeException("makeDepositForAll()에서 발생!");
        }
        HashMap<Integer, Double> result = new HashMap<>();
        serialNCriticalSet.keySet().forEach(integer -> result.put(integer, (double)0));
        return result;
    }
    private HashMap<Integer, SingleEntryMap<Double, Double>> makeStimulationSumLogForm(){
        if(serialNCriticalSet == null){
            System.out.println("serialNCriticalSet 없음!!! makeSerialNCriticalSet 메서드 실행이 선행되어야 함.");
            throw new RuntimeException("makeStimulationSumLogForm()에서 발생!");
        }
        HashMap<Integer, SingleEntryMap<Double, Double>> result = new HashMap<>();
        for(Integer i: serialNCriticalSet.keySet()){
            result.put(i, new SingleEntryMap<Double, Double>(serialNCriticalSet.get(i), Double.MIN_VALUE));
        }
        return result;
    }
    private HashMap<Integer, SingleEntryMap<Double, Double>> makeStimulationSumLogForm(double init){
        if(serialNCriticalSet == null){
            System.out.println("serialNCriticalSet 없음!!! makeSerialNCriticalSet 메서드 실행이 선행되어야 함.");
            throw new RuntimeException("makeStimulationSumLogForm()에서 발생!");
        }
        HashMap<Integer, SingleEntryMap<Double, Double>> result = new HashMap<>();
        for(Integer i: serialNCriticalSet.keySet()){
            result.put(i, new SingleEntryMap<Double, Double>(serialNCriticalSet.get(i), init));
        }
        return result;
    }
    private void annihilateBalance(Integer serial){
        for(int i = 0; i <= WAVE_SUSTAIN_TICK; i++){
            stimulationDeposit.get(-i).replace(serial, (double)0);
        }
    }


    public void runCircuit(){
        HashMap<Integer, Boolean> input;
        HashMap<Integer, Double> nextWave;
        Node tempNode;
        HashSet<Edge> ignitedEdgeSet;
        HashSet<Node> stimulatedNodeNext;
        Double tempDouble;
        int cycleCounter = 0;
        while(true) {
            try {
                stimulatedNodeNext = new HashSet<>();
                stimulationSumLogInACycle = new HashMap<>();
                cycleLog = new HashMap<>();
                stimulationSumLogInACycle.put(0, makeStimulationSumLogForm(0));
                cycleLog.put(0, new HashSet<Integer>());
                input = queueData.getNextQueue();
                for (Integer i : input.keySet()) {
                    stimulationSumLogInACycle.get(0).get(i).setItem2((double)(input.get(i) ? 1 : 0));
                    tempNode = findMatchingNode(i);
                    assert tempNode != null;
                    if (tempNode.askIgnite(input.get(i) ? 1 : 0)) {
                        currentSparkedNode.add(tempNode);
                        cycleLog.get(0).add(tempNode.SERIAL_NUMBER);
                    }
                }
            } catch (EndOfQueueException eqe) {
                System.out.println("** 모든 회기 종료 **");
                return;
            }
            int tickCounter = 1;
            while (!currentSparkedNode.isEmpty()) {
                ignitedEdgeSet = edgeOfIgnited(currentSparkedNode);
                nextWave = sumOfWeighInOrderOfDestNode(ignitedEdgeSet);
                cycleLog.put(tickCounter, new HashSet<Integer>());
                HashMap<Integer, SingleEntryMap<Double, Double>> formForTick = makeStimulationSumLogForm();
                stimulationSumLogInACycle.put(tickCounter, formForTick);
                for (Integer i : nextWave.keySet()) {
                    tempNode = findMatchingNode(i);
                    assert tempNode != null;
                    stimulationDeposit.get(0).put(i, nextWave.get(i));
                    if (tempNode.askIgnite(tempDouble = sumOfStimulationInDeposit(i))) { // true 일경우 다음 노드 흥분! , Deposit 잔고 소멸.
                        stimulationSumLogInACycle.get(tickCounter).get(i).setItem2(tempDouble);
                        annihilateBalance(i);
                        tempNode.activate();
                        cycleLog.get(tickCounter).add(tempNode.SERIAL_NUMBER);
                        stimulatedNodeNext.add(tempNode);
                    }
                }
                serialNCriticalSet.keySet().forEach(
                        (integer -> {if(formForTick.get(integer).item2==Double.MIN_VALUE)
                            formForTick.get(integer).setItem2(sumOfStimulationInDeposit(integer));}));
                postTickProcess(ignitedEdgeSet, stimulatedNodeNext);
                for(Node n : stimulatedNodeNext)
                    n.deActivate();
                currentSparkedNode.addAll(stimulatedNodeNext);
                stimulatedNodeNext = new HashSet<>();
                tickCounter++;
            }
            DisplayData.displaySingleCycle(circuitList, cycleLog, stimulationSumLogInACycle);
            totalSimulationLog.put(cycleCounter, cycleLog);
            postCycleProcess();
            cycleCounter++;
        }
        //모든 노드와 엣지 비활성화 과정 추가 (틱마다)

    }

    private Node findMatchingNode(Integer serial){
        for(HashMap<Node, HashSet<Edge>> h : circuitList)
            for(Node n : h.keySet())
                if(n.matches(serial))
                    return n;
        return null;
    }

    private HashMap<Integer, Double> sumOfWeighInOrderOfDestNode(HashSet<Edge> ignitedEdgeSet){

        HashMap<Integer, Double> result = new HashMap<>();
        for(Edge e : ignitedEdgeSet){
            if(result.containsKey(e.getDestination()))
                result.replace(e.getDestination(),result.get(e.getDestination()) + e.getWeight());
            else
                result.put(e.getDestination(), e.getWeight());
        }
        return result;
    }

    private HashSet<Edge> edgeOfIgnited(HashSet<Node> currentSparkedNode){
        HashSet<Edge> result = new HashSet<>();
        for(Node n :currentSparkedNode) {
            result.addAll(n.getEdge());
        }
        HashSet<Edge> tempSet = new HashSet<>();
        for(Edge e : result){
            if(! e.checkVital())
                tempSet.add(e);
        }
        result.removeAll(tempSet);
        return result;
    }

    private double sumOfStimulationInDeposit(Integer serial){
        double result = 0;
        for(int i = (-WAVE_SUSTAIN_TICK); i <= 0; i++){
            result += stimulationDeposit.get(i).getOrDefault(serial, (double) 0);
        }
        return result;
    }

    private void postTickProcess(HashSet<Edge> ignitedEdgeSet, HashSet<Node> stimulatedNodeNext){
        HashMap<Integer, Double> currentTickDeposit = sumOfWeighInOrderOfDestNode(ignitedEdgeSet);
        HashMap<Integer, Double> intDoubleMap = new HashMap<>();

        // 루프 노드의 카운더 조정 과정
        for(Edge e : ignitedEdgeSet){
            e.activated();
        }

        // 이전  WAVE_SUSTAIN_TICK 틱의 기간 동안 활성화된 엣지들 중 현재 흥분 노드를 도착점으로 하는 엣지들에 대한 가중치 보정 과정
        for(int i = -WAVE_SUSTAIN_TICK; i <= -1; i++){
            for(Edge e : previousSparkedNode.get(i).item2){
                for(Node n : currentSparkedNode){
                    if(n.matches(e.getDestination())){
                        e.weightAdjustFireTogether();
                    }
                }
            }
        }
        // 이번 틱에 활성화된 엣지들 중 후행노드가 흥분한 엣지의 가중치 보정과정
        for(Edge e: ignitedEdgeSet){
            for(Node n : currentSparkedNode){
                if(n.matches(e.getDestination())){
                    e.weightAdjustFireTogether();
                }
            }
        }

        // previousSparkedNode 업데이트 과정
        previousSparkedNode.remove(-WAVE_SUSTAIN_TICK);

        for(int i = (-WAVE_SUSTAIN_TICK +1); i <= -1; i++){
            previousSparkedNode.put(i-1, previousSparkedNode.get(i));
        }
        SingleEntryMap<HashSet<Node>, HashSet<Edge>> tempMap = new SingleEntryMap<>(currentSparkedNode, ignitedEdgeSet);
        previousSparkedNode.put(-1, tempMap);
        currentSparkedNode = stimulatedNodeNext;    // 다음에 사용될 소스 최신화




        // 반감주기 다다른 자극들에 대해 계수를 곱해 반감주기 갱신하고 다른 위치에 저장.
        if(! stimulationDeposit.get(-WAVE_SUSTAIN_TICK).isEmpty()){
            intDoubleMap = stimulationDeposit.get(-WAVE_SUSTAIN_TICK);
            for(Integer i : intDoubleMap.keySet()){
                stimulationDeposit.get(0).replace(i, stimulationDeposit.get(0).get(i)+intDoubleMap.get(i) * WAVE_REDUCE_COEFFICIENT);
            }
        }

        // stimulationDeposit 업데이트 과정.
        for(int i = (-WAVE_SUSTAIN_TICK) + 1; i <= 0; i++){
            stimulationDeposit.replace(i-1, stimulationDeposit.get(i));

            for(Node n : stimulatedNodeNext){
                stimulationDeposit.get(i).remove(n.SERIAL_NUMBER);
            }
        }
        stimulationDeposit.put(0, makeDepositForAll());

    }
    private void postCycleProcess(){
        for(HashMap<Node, HashSet<Edge>> level: circuitList){
            for(Node n : level.keySet()){
                n.deActivate();
                if(level.get(n) != null) {
                    for (Edge e : level.get(n)) {
                        e.resetActiveCounter();
                    }
                }
            }
        }
        previousSparkedNode = new HashMap<>();
        for(int i = 1; i <= WAVE_SUSTAIN_TICK; i++){
            previousSparkedNode.put((-i), new SingleEntryMap<HashSet<Node>, HashSet<Edge>>(new HashSet<Node>(), new HashSet<Edge>()));
        }
        stimulationDeposit = new HashMap<>();
        for (int i = 0; i <= WAVE_SUSTAIN_TICK; i++) {
            stimulationDeposit.put(-i, makeDepositForAll());
        }
        currentSparkedNode = new HashSet<>();

    }


    public static void makeInputFiles(){
        Scanner userInput = new Scanner(System.in);
        String fileName;
        String tempString;
        int fileFormat;
        while(true) {
            System.out.println(" *작성하실 파일의 내용을 알려주세요* ");
            System.out.println("1. Node.Node");
            System.out.println("2. Edge.Edge");
            System.out.println("3. InputData");
            System.out.print("파일 내용의 번호를 입력하세요: ");
            tempString = userInput.nextLine();
            try{
                fileFormat = Integer.parseInt(tempString.trim());
            }catch (NumberFormatException ne){
                System.out.println("잘못된 메뉴 선택입니다.");
                continue;
            }
            if(fileFormat < 4&& fileFormat > 0){
                break;
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
        System.out.print("입력하실 파일의 이름을 알려주세요(확장자 제외): ");
        fileName = userInput.nextLine() + "_IN.txt";
        if(fileFormat == 1){
            makeNodeInputFile(fileName);
        } else if(fileFormat == 2){
            makeEdgeInputFile(fileName);
        } else{
            makeInputQueueFile(fileName);
        }

    }
    private static void makeNodeInputFile(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter fileBuffer = new BufferedWriter(fileWriter);
            Scanner userInput = new Scanner(System.in);
            StringBuilder lineBuilder = new StringBuilder();
            int nodeCount;
            String tempString;
            System.out.println("*** <노드 입력형식 안내> ***");
            System.out.println("0. \t순서:\t <1. 레벨 / 2. 레벨에 속한 노드> 순으로 형식에 맞추어 입력");
            System.out.println("1-0. 레벨 형식:\t 1. 속한 노드들의 포멧 / 2. 레벨 / 3. 레벨명");
            System.out.println("1-1. 노드 포멧:\t " + "Node.InputNode: " + InputNodeInter.NODE_FORMAT + "Node.ProcessNode: " + ProcessNodeInter.NODE_FORMAT + "Node.OutputNode: " + OutputNodeInter.NODE_FORMAT);
            System.out.println("1-2. 레벨 형식:\t 1부터 시작하는 연속된 자연수를 사용할 것을 권장. / ");
            System.out.println("1-3. 레벨명 형식:\t 자유형식");
            System.out.println();
            System.out.println("2-0. 노드 형식:\t 1. 노드 포멧 / 2. 노드명 / 3. 시리얼 / 4. 흥분역치");
            System.out.println("2-1. 노드 포멧:\t 위에서 언급된 형식과 동일.");
            System.out.println("2-2. 노드명 형식:\t 자유형식");
            System.out.println("2-3. 시리얼 형식:\t 5자리 정수  / 중복될 경우 곤란함. (중복시 먼저입력된 내용 무시됨.)");
            System.out.println("\t\t\t 시리얼 권장형식:\t (노드형식 * 10000) + (level * 100) + (레벨내 순번 or 자율)");
            System.out.println("2-4. 흥분역치 형식:\t 0보다 큰 실수 / Node.InputNode 의 경우 흥분역치는 1로 고정되어 입력받지 않음.");
            System.out.println("\t\t\t 흥분역치 권장사항:\t 각 레벨의 흥분역치는 통일할 것을 권장.");
            System.out.println();
            System.out.println("-입력 시작-");
            System.out.println();
            do {
                System.out.println("\t<레벨 작성 단계>");
                lineBuilder.append("##").append(getNodeFormat(userInput)).append(getLevel(userInput)).append(getName(userInput));
                fileBuffer.write(lineBuilder.toString());
                fileBuffer.newLine();
                lineBuilder.delete(0, lineBuilder.capacity());
                while (true) {
                    System.out.print("레벨에 배정될 노드의 개수: ");
                    try {
                        nodeCount = Integer.parseInt(userInput.nextLine().trim());
                        break;
                    } catch (NumberFormatException ne) {
                        System.out.println("잘못된 입력입니다. 다시 입력하세요.");
                    }
                }
                System.out.println("\t<노드 작성 단계>");
                while (nodeCount > 0) {
                    tempString = getNodeFormat(userInput);
                    if (tempString.equals('[' + String.valueOf(InputNodeInter.NODE_FORMAT) + ']'))
                        lineBuilder.append(tempString).append(getName(userInput)).append(getSerialInput(userInput)).append("[1]");
                    else
                        lineBuilder.append(tempString).append(getName(userInput)).append(getSerialInput(userInput)).append(getCritical(userInput));
                    fileBuffer.write(lineBuilder.toString());
                    fileBuffer.newLine();
                    lineBuilder.delete(0, lineBuilder.capacity());
                    System.out.println("\n");
                    nodeCount--;
                }
                System.out.print("다음 레벨을 계속 입력하실건가요? [계속(y) / 중단([^y|Y].*)] : ");
                tempString = userInput.nextLine();
            } while (tempString.startsWith("y") || tempString.startsWith("Y"));
            fileBuffer.write("$$");
            fileBuffer.close();
        } catch(IOException ie){
            System.out.println("Node.Node 파일 작성과정에서 오류발생");
        }
    }
    private static void makeEdgeInputFile(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter fileBuffer = new BufferedWriter(fileWriter);
            Scanner userInput = new Scanner(System.in);
            StringBuilder lineBuilder = new StringBuilder();
            String tempString;
            int edgeCount;
            System.out.println("*** <엣지 입력형식 안내> ***");
            System.out.println("1-0. 엣지 형식:\t 1. 엣지포멧 / 2. 출발노드시리얼 / 3. 도착노드시리얼 / 4. 가중치 / 5. 가중치 변화량(옵션) / 6. 루프 횟수 (루프노드일 경우에만)");
            System.out.println("1-1. 엣지 포멧:\t " + "NoneLoopEdge: " + EdgeInter.NONE_LOOP_EDGE + "Edge.LoopEdge: " + EdgeInter.LOOP_EDGE);
            System.out.println("1-4. 가중치 규칙:\t 출발노드가 도착노드의 흥분을 억제하는 역할을 할 경우 음의 실수, 흥분을 촉진시킬 경우 양의 실수");
            System.out.println("1-5. 가중치 변화량:\t 작성하지 않을 경우 엔터로 넘어갈 것. 작성하지 않을 경우 가중치에 프로그램 내에 미리 설정된 상수를 곱해 산출");
            System.out.println("1-6. 루프 횟수:\t 루프엣지는 회로의 무한가동을 막기 위해 입력한 횟수 를 넘어 활성화될 수 없음.(각 회기 시작시 카운트는 초기화)");
            System.out.println();
            System.out.println("-입력 시작-");
            System.out.println();
            System.out.print("입력할 엣지 갯수를 알려주세요(입력된 갯수 입력 후에도 추가 입력 가능): ");
            while(true) {
                try {
                    edgeCount = Integer.parseInt(userInput.nextLine().trim());
                    break;
                } catch (NumberFormatException ne) {
                    System.out.println("잘못된 입력입니다. 다시 입력하세요.");
                }
            }
            System.out.println("<엣지 작성 단계>");
            while(edgeCount > 0){
                tempString = getEdgeFormat(userInput);
                if (tempString.equals('[' + String.valueOf(EdgeInter.NONE_LOOP_EDGE) + ']'))
                    lineBuilder.append(tempString).append(getStartSerial(userInput)).append(getDestSerial(userInput)).append(getWeight(userInput));
                else
                    lineBuilder.append(tempString).append(getStartSerial(userInput)).append(getDestSerial(userInput)).append(getWeight(userInput)).append(getLoopTimes(userInput));
                fileBuffer.write(lineBuilder.toString());
                fileBuffer.newLine();
                lineBuilder.delete(0, lineBuilder.capacity());
                System.out.println("\n");
                edgeCount--;
                if(edgeCount == 0){
                    while(true) {
                        System.out.print("추가적으로 작성하실 엣지의 갯수를 입력하시거나 원치 않으시면 0을 입력해주세요: ");
                        try {
                            edgeCount = Integer.parseInt(userInput.nextLine().trim());
                            break;
                        } catch (NumberFormatException ne) {
                            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                        }
                    }
                }
            }
            fileBuffer.write("$$");
            fileBuffer.close();
        } catch (IOException ie) {
            System.out.println("엣지 파일 작성 과정에서 문제 발생");
        }
    }
    private static void makeInputQueueFile(String fileName){
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter fileBuffer = new BufferedWriter(fileWriter);
            Scanner userInput = new Scanner(System.in);
            StringBuilder lineBuilder = new StringBuilder();
            ArrayList<Integer> inputNodeList;
            String tempString;
            int inputCount;
            while(true) {
                System.out.print("inputNode 가 있는 파일의 이름을 입력하세요(종료는 q): ");
                String inputFile = userInput.nextLine().trim();
                if (inputFile.equals("q"))
                    return;
                inputNodeList = bringInputNodeList(inputFile);
                if(inputNodeList.size() == 0){
                    System.out.println("입력노드 발견하지 못함. 파일 이름을 다시 입력하세요");
                    continue;
                }
                break;
            }
            lineBuilder.append("##");
            for(Integer i : inputNodeList){
                lineBuilder.append("[").append(i).append("]");
            }
            System.out.println("<인풋노드 명단>");
            System.out.println(lineBuilder.toString());
            fileBuffer.write(lineBuilder.toString());
            fileBuffer.newLine();
            lineBuilder.delete(0, lineBuilder.capacity());
            System.out.println("\n");
            System.out.print("입력할 큐 횟수를 알려주세요(입력된 횟수 입력 후에도 추가 입력 가능): ");
            while(true) {
                try {
                    inputCount = Integer.parseInt(userInput.nextLine().trim());
                    break;
                } catch (NumberFormatException ne) {
                    System.out.println("잘못된 입력입니다. 다시 입력하세요.");
                }
            }
            System.out.println("<엣지 작성 단계>");
            while(inputCount > 0){
                for(int i: inputNodeList){
                    lineBuilder.append(getBoolean(userInput, i));
                }
                fileBuffer.write(lineBuilder.toString());
                fileBuffer.newLine();
                lineBuilder.delete(0, lineBuilder.capacity());
                System.out.println("\n");
                inputCount--;
                if(inputCount == 0){
                    while(true) {
                        System.out.print("추가적으로 작성하실 큐 횟수를 입력하시거나 원치 않으시면 0을 입력해주세요: ");
                        try {
                            inputCount = Integer.parseInt(userInput.nextLine().trim());
                            break;
                        } catch (NumberFormatException ne) {
                            System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
                        }
                    }
                }
            }


            fileBuffer.write("$$");
            fileBuffer.close();
        } catch(IOException ie){
            System.out.println("큐 인풋 파일 작성 과정에서 문제 발생");
        }
    }
    private static String getSerialInput(Scanner userInput){
        String tempString;
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("시리얼: ");
                tempString = userInput.nextLine().trim();
                result.append(Integer.parseInt(tempString));
                result.append(']');
                if(result.length() != 7){
                    System.out.println("다시입력하세요!(시리얼은 5자리 정수)");
                    continue;
                }
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
    }
    private static String getLevel(Scanner userInput){
        String tempString;
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("Level: ");
                tempString = userInput.nextLine().trim();
                result.append(Integer.parseInt(tempString));
                result.append(']');
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
    }
    private static String getNodeFormat(Scanner userInput){
        Boolean flag = false;
        int tempInt;
        int[] formatList = {InputNodeInter.NODE_FORMAT, OutputNodeInter.NODE_FORMAT, ProcessNodeInter.NODE_FORMAT};
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("NodeFormat: ");
                tempInt = Integer.parseInt(userInput.nextLine().trim());
                for(int i : formatList){
                    if(tempInt == i)
                        flag = true;
                }
                if(! flag){
                    System.out.println("존재하지 않는 Format 입력! 다시입력하세요");
                    continue;
                }
                result.append(tempInt);
                result.append(']');
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
    }
    private static String getLoopTimes(Scanner userInput){
        String tempString;
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("Loop 횟수: ");
                tempString = userInput.nextLine().trim();
                result.append(Integer.parseInt(tempString));
                result.append(']');
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
    }
    private static String getName(Scanner userInput){
        StringBuilder result = new StringBuilder();
        result.append('[');
        System.out.print("이름: ");
        result.append(userInput.nextLine().trim());
        result.append(']');return result.toString();
    }
    private static String getCritical(Scanner userInput){
        String tempString;
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("흥분 역치: ");
                tempString = userInput.nextLine().trim();
                result.append(Double.parseDouble(tempString));
                result.append(']');
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
    }
    private static String getEdgeFormat(Scanner userInput){
        Boolean flag = false;
        int tempInt;
        int[] formatList = {EdgeInter.NONE_LOOP_EDGE, EdgeInter.LOOP_EDGE};
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("EdgeFormat: ");
                tempInt = Integer.parseInt(userInput.nextLine().trim());
                for(int i : formatList){
                    if(tempInt == i)
                        flag = true;
                }
                if(! flag){
                    System.out.println("존재하지 않는 Format 입력! 다시입력하세요");
                    continue;
                }
                result.append(tempInt);
                result.append(']');
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
    }
    private static String getStartSerial(Scanner userInput){
        System.out.println("* 출발노드 입력 *");
        return getSerialInput(userInput);
    }
    private static String getDestSerial(Scanner userInput){
        System.out.println("* 도착노드 입력 *");
        return getSerialInput(userInput);
    }
    private static String getWeight(Scanner userInput){
        String tempString;
        String resultString;
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("가중치: ");
                tempString = userInput.nextLine().trim();
                result.append(Double.parseDouble(tempString));
                result.append(']');
                resultString =  result.toString();
                break;
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print("가중치 변화량(입력을 원하지 않는다면 엔터로 넘어가기): ");
                tempString = userInput.nextLine().trim();
                if(tempString.equals(""))
                    return resultString;
                result.append(Double.parseDouble(tempString));
                result.append(']');
                resultString += result.toString();
                break;
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }
        return resultString;
    }
    private static ArrayList<Integer> bringInputNodeList(String inputFile){
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<String> splitInputData = new ArrayList<>();
        String fileLine;

        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++) {  /// 파일에서 라인 읽어오기
                if (fileLine.trim().startsWith("$$") || fileLine.trim().startsWith("##"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                if(Integer.parseInt(splitInputData.get(0)) == InputNodeInter.NODE_FORMAT){
                    result.add(Integer.valueOf(splitInputData.get(2)));
                }
            }

            inputBuffer.close();
        }catch(IOException ie){
            System.out.println("인풋노드 정보를 읽어오는 과정에서 문제 발생");
        }


        return result;
    }
    private static String getBoolean(Scanner userInput, int node){
        String tempString;
        while(true){
            try{
                StringBuilder result = new StringBuilder();
                result.append('[');
                System.out.print(node+"의 흥분 여부(y / n): ");
                tempString = userInput.nextLine().trim();
                if(tempString.equals("y")){
                    result.append("true");
                }
                else
                    result.append("false");
                result.append(']');
                return result.toString();
            }catch(NumberFormatException ne){
                System.out.println("잘못된 입력 내용 다시 입력하세요");
            }
        }

    }


}
























/*-------------------------------2.*개체에 관련된 데이터 저장소-------------------------------*/

/*-------------------------------2.1.Entity.LevelData-------------------------------*/

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

/*-------------------------------2.1.Entity.SingleEntryMap-------------------------------*/
class SingleEntryMap<T, S>{
    public T item1;
    public S item2;
    SingleEntryMap(T item1, S item2){
        this.item1 = item1;
        this.item2 = item2;
    }
    public void setItem1(T value){
        item1 = value;
    }
    public void setItem2(S value){
        item2 = value;
    }
}



/*-------------------------------3.* 개체의 동작에 필요한 클래스-------------------------------*/

/*-------------------------------3.1.inputQueue 관련 예외 클래스-------------------------------*/

class InValidInputQueueFormatException extends RuntimeException{
    InValidInputQueueFormatException(String msg){
        super(msg);
    }
    InValidInputQueueFormatException(){
        super("Entity.InValidInputQueueFormatException");
    }
}
class EndOfQueueException extends RuntimeException{
    EndOfQueueException(String msg){
        super(msg);
    }
    EndOfQueueException(){
        super("Entity.EndOfQueueException");
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

