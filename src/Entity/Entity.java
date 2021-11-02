package Entity;
/* ---------------------------------------------
## 목차
1. 개체의 기본형
2.* 개체에 관련된 데이터 저장소
3.* 개체의 동작에 필요한 클래스
3.1.파일 입출력 관련 함수

----------------------------------------------- */
//
//개체에 기본형을 만든 이유는 기본형을 상속한 여러 성격의 객체를 구현할 계획이 있기 때문이다.
// ex) 덤벙대는 개체, 예민한 개체, 우두머리 개체 등..
//
//
// idea) input 이 첫 레벨에서 이루어지는게 아니라 회로의 중간에서도 이루어질 수 있다면?
// re-idea) 차라리 레벨이 연속적으로(0->1->2->3) 이어지는게 아니라 유기적으로 이어지게 한다면? (0->2->4)
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
import Utility.ContainsTwo;
import Level.*;

import java.util.*;
import java.io.*;
/*-------------------------------1.개체의 기본형-------------------------------*/

public class Entity {
    static final int WAVE_SUSTAIN_TICK = 2;
    static final double WAVE_REDUCE_COEFFICIENT = 0.6;
    private QueueDataStorage queueData;
    ArrayList<Level> circuit;
    ArrayList<String> inputFileList;
    ArrayList<String> outputFileList;

    public Entity(ArrayList<String> inputFileList, ArrayList<String> outputFileList){
        this.inputFileList = inputFileList;
        this.outputFileList = outputFileList;
        circuit =  readNodeFromFile(inputFileList.get(0));
        readAndMatchEdgeFromFile(inputFileList.get(1), circuit);
        queueData = readInputQueue(inputFileList.get(2));
    }

    public void run(){
        ArrayList<ContainsTwo<Integer, Boolean>> presentQueue;
        while((presentQueue = queueData.getNextQueue()) != null){
            runCycle(presentQueue);
            circuit.forEach(level -> level.preCycleProcess());
        }
    }

    private void runCycle(ArrayList<ContainsTwo<Integer, Boolean>> presentQueue){
        boolean flag = true;
        ArrayList<ArrayList<HistoryOfTick>> history = new ArrayList<>();
        for(ContainsTwo<Integer, Boolean> ct: presentQueue){
            if(ct.getItem2())// input 입력
                circuit.get((ct.getItem1() /100)%100).setQueueData(ct.getItem1());
        }
        circuit.forEach(level -> level.preCycleProcess());
        while(flag){
            flag = false;
            for(Level level: circuit){
                if(level.tick())
                    flag = true;
                level.postTickProcess();
            }
        }
        for(Level level: circuit){
            history.add(level.getHistory());
        }
        DisplayData.displaySingleCycle(history);
    }


    private ArrayList<Level> readNodeFromFile(String inputFile){// Node.Node 만 읽어 ArrayList 로 저장, level 과 묶어 map 을 만들어 반환.
        String fileLine = "";
        ArrayList<String> splitInputData = null;
        ArrayList<Level> result = new ArrayList<>();
        Level presentLevel = null;
        try {
            BufferedReader inputBuffer = new BufferedReader(new FileReader(new File(inputFile)));
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                if(fileLine.trim().startsWith("##")) {    //라인의 가장 첫번째에 오는 ## 새로운 레벨에 속하는 노드의 입력임을 나타냄.
                    fileLine = fileLine.replace("#", "");
                    fileLine = fileLine.replace("[", "");
                    splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                    presentLevel = new Level(splitInputData.get(2), Integer.parseInt(splitInputData.get(1)));
                    result.add(presentLevel);
                    continue;
                }
                fileLine = fileLine.replace("[", "");
                splitInputData = new ArrayList<String>(Arrays.stream(fileLine.split("]")).toList());
                if(presentLevel == null) {
                    System.out.println("주석이 노드보다 선행되어야 함!!, 파일 형식 오류");
                    throw new IOException();
                }
                else
                    presentLevel.addNode(makeNodeFromAList(splitInputData), WAVE_SUSTAIN_TICK, WAVE_REDUCE_COEFFICIENT);
            }
            inputBuffer.close();
        } catch (IOException | NumberFormatException ie){
            ie.printStackTrace();
        }
        return result;
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

    private void readAndMatchEdgeFromFile(String inputFile, ArrayList<Level> circuit){  // 파일로부터 엣지 정보를 읽어 시작노드 시리얼과 엣지를 묶어 저장.
        String fileLine = "";
        String[] splitInputData = null;
        Edge newEdge;
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++){  /// 파일에서 라인 읽어오기
                if(fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                fileLine = fileLine.replace("[", "");
                splitInputData = fileLine.split("]");
                newEdge = makeEdgeFromArr(splitInputData);
                circuit.get((newEdge.getSTART_NODE_SERIAL()/100)%100).LinkingEdgeWithStartNode(newEdge);
                circuit.get((newEdge.getDestination()/100)%100).LinkingEdgeWithDestNode(newEdge);
            }
            inputBuffer.close();
        }catch (IOException ie){
            ie.printStackTrace();
        }
    }


    private Edge makeEdgeFromArr(String[] data){
        Edge edge = null;
        try {
            int edgeFormat = Integer.parseInt(data[0].trim());
            if(edgeFormat == 1) {
                if (data.length == 5)
                    edge = new Edge(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]));
                else if (data.length == 4)
                    edge = new Edge(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Double.parseDouble(data[3]));
                else
                    throw new InValidEdgeFormatException("정의되지 않은 Edge.Edge 생성자 매개변수 개수 값: " + data.length);
            }else if(edgeFormat == 2){
                if(data.length == 6)
                    edge = new LoopEdge(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]), Integer.parseInt(data[5]));
                else if(data.length == 5)
                    edge = new LoopEdge(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Double.parseDouble(data[3]), Integer.parseInt(data[4]));
                else throw new InValidEdgeFormatException("정의되지 않은 Edge.LoopEdge 생성자 매개변수 개수 값: " + data.length);
            }
            else throw new InValidEdgeFormatException("정의되지 않은 EDGE_FORMAT 입력. 값: " + edgeFormat);
        } catch(InValidEdgeFormatException | NumberFormatException ee){
            System.out.println("에러 메시지: " + ee.getMessage());
            ee.printStackTrace();
        }
        return edge;
    }



    private QueueDataStorage readInputQueue(String inputFile) {
        QueueDataStorage result;
        ArrayList<Integer> inputNodeSerials = new ArrayList<>();
        String[] splitInputData;
        String fileLine = "";
        ArrayList<ArrayList<ContainsTwo<Integer, Boolean>>> inputs = new ArrayList<>();
        try {
            FileReader fileInput = new FileReader(inputFile);
            BufferedReader inputBuffer = new BufferedReader(fileInput);
            for (int i = 1; (fileLine = inputBuffer.readLine()) != null; i++) {  /// 파일에서 라인 읽어오기
                if (fileLine.trim().startsWith("$$"))    //라인의 가장 앞에 나오는 $$는 주석역할
                    continue;
                if (fileLine.trim().startsWith("##")){
                    fileLine = fileLine.replace("#", "");
                    fileLine = fileLine.replace("[", "");
                    splitInputData = fileLine.trim().split("]");
                    for(String s : splitInputData){
                        inputNodeSerials.add(Integer.valueOf(s.trim()));
                    }
                    continue;
                }
                ArrayList<ContainsTwo<Integer, Boolean>> presentQueue = new ArrayList<>();
                inputs.add(presentQueue);
                fileLine = fileLine.replace("[", "");
                splitInputData = fileLine.trim().split("]");
                for(int j = 0; j < splitInputData.length; j++){
                    presentQueue.add(new ContainsTwo<>(inputNodeSerials.get(j), Boolean.parseBoolean(splitInputData[j])));
                }
            }
            inputBuffer.close();
        } catch (IOException ie){
            ie.printStackTrace();
        }
        result = new QueueDataStorage(inputs);
        return result;
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


/*-------------------------------3.* 개체의 동작에 필요한 클래스-------------------------------*/

/*-------------------------------3.1.inputQueue 관련 예외 클래스-------------------------------*/


/*-------------------------------3.2 회로 진행 데이터를 관리하는 클래스-------------------------------*/
class QueueDataStorage{
    private ArrayList<ArrayList<ContainsTwo<Integer, Boolean>>> inputs;
    private ArrayList<ArrayList<HistoryOfTick>> totalHistory;
    private int cycleCounter = 0;
    private int totalInputCycle;

    QueueDataStorage(ArrayList<ArrayList<ContainsTwo<Integer, Boolean>>> inputs){
        this.inputs = inputs;
        totalInputCycle = inputs.size();
        totalHistory = new ArrayList<>();
    }
    public ArrayList<ContainsTwo<Integer, Boolean>> getNextQueue(){
        if(cycleCounter < inputs.size()){
            return inputs.get(cycleCounter++);
        }
        else
            return null;
    }

    public void addHistory(ArrayList<HistoryOfTick> newTotalHistory) {
        totalHistory.add(newTotalHistory);
    }
}


