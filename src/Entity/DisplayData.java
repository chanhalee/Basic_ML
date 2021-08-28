package Entity;

import Edge.Edge;
import Node.Node;

import java.util.*;

public class DisplayData {
    private static int cycleCounter = 0;
    public static void displaySingleCycle(ArrayList<HashMap<Node, HashSet<Edge>>> inputCircuit, HashMap<Integer, HashSet<Integer>> inputCycleLog, HashMap<Integer, HashMap<Integer, SingleEntryMap<Double, Double>>> stimulationSumLogInACycle){
        HashMap<Integer, ArrayList<Integer>> circuit = new HashMap<>();     //중첩 ArrayList로 하고싶었지만, Level의 이름이 0부터 시작하는 연속적인 자연수가 아닐 가능성을 고려함.
        ArrayList<ArrayList<Integer>> cycleLog = new ArrayList<>();
        int tempInt = 0;
        int levelSize;
        int tickSize;
        int maxSize = 0;    // 도식화의 세로 길이 기준
        int totalNodeQuantity = 0;  // 도식화의 세로 길이 기준
        for(HashMap<Node, HashSet<Edge>> nodeMap : inputCircuit){
            ArrayList<Integer> nodeList = new ArrayList<>();
            totalNodeQuantity += nodeMap.size();
            if(nodeMap.size() > maxSize)
                maxSize = nodeMap.size();
            for(Node n : nodeMap.keySet()){
                nodeList.add(n.SERIAL_NUMBER);
            }
            circuit.put(tempInt, nodeList);
            tempInt++;
        }
        for(HashSet<Integer> hs : inputCycleLog.values()){
            ArrayList<Integer> tempList = new ArrayList<>();
            for(Integer i : hs){
                tempList.add(i);
            }
            cycleLog.add(tempList);
        }
        levelSize = circuit.size();
        tickSize = inputCycleLog.size();


        int tickCounter = 0;
        for(ArrayList<Integer> tickData : cycleLog) {   // 각 틱에 1회 for 문의 내용 실행. (한 웨이브가 모두 실행될 때까지 계속)
            System.out.print("\n    ----");
            for(int i = 0; i < totalNodeQuantity; i++){
                System.out.print("---------");
            }
            System.out.println("------------------------------------------------------");
            if(tickCounter == 0)
                System.out.println("    <"+cycleCounter+"번째 회기 input data>");
            else
                System.out.println("    <"+cycleCounter+"번째 Cycle "+tickCounter+"번째 Tick>");

            ArrayList<ListIterator<Integer>> serialIter = new ArrayList<>();
            ArrayList<ListIterator<Integer>> stimulatedIter = new ArrayList<>();
            ArrayList<ListIterator<Integer>> depositIter = new ArrayList<>();
            for(ArrayList<Integer> al : circuit.values()){
                serialIter.add(al.listIterator());
                stimulatedIter.add(al.listIterator());
                depositIter.add(al.listIterator());
            }
            for (int line = 0; line < maxSize; line++) { //
                int tempLevelData = 0;
                for(Integer level : circuit.keySet()){
                    System.out.print("    |    ");
                    int sizeTemp = circuit.get(level).size();
                    if(line *2 < maxSize - sizeTemp || (! serialIter.get(tempLevelData).hasNext())){
                        for(int k = 0; k < circuit.get(level).size()*2 +1; k++)
                            System.out.print("    ");
                        System.out.print("   ");
                    } else{
                        int indexTemp = serialIter.get(tempLevelData).nextIndex();
                        Integer serial = serialIter.get(tempLevelData).next();
                        if(indexTemp % 2 == 0){ // 인덱스가 짝수일때 좌로 치우친 위치
                            if(indexTemp*2 < sizeTemp ){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("[");
                                System.out.print(serial);
                                System.out.print("]");
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("[");
                                System.out.print(serial);
                                System.out.print("]");
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }
                            for(int k = 0; k < sizeTemp ; k++)
                                System.out.print("    ");
                        }
                        else{   // 인덱스가 홀수일때 우로 치우친 위치
                            for(int k = 0; k < sizeTemp ; k++)
                                System.out.print("    ");
                            if(indexTemp*2 < sizeTemp ){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                                System.out.print("[");
                                System.out.print(serial);
                                System.out.print("]");
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print  ("    ");
                                System.out.print("[");
                                System.out.print(serial);
                                System.out.print("]");
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                            }
                        }
                    }
                    System.out.print("  ");
                    tempLevelData ++;
                }
                System.out.println("   |");
                // 1틱동안의 시리얼넘버 출력 완료
                tempLevelData = 0;
                for(Integer level : circuit.keySet()){
                    System.out.print("    |    ");
                    int sizeTemp = circuit.get(level).size();
                    if(line*2 < (maxSize - sizeTemp) || (! stimulatedIter.get(tempLevelData).hasNext())){
                        for(int k = 0; k < circuit.get(level).size()*2 +1; k++)
                            System.out.print("    ");
                        System.out.print("   ");
                    } else{
                        int indexTemp = stimulatedIter.get(tempLevelData).nextIndex();
                        Integer serial = stimulatedIter.get(tempLevelData).next();
                        if(indexTemp % 2 == 0){ // 인덱스가 짝수일때 좌로 치우친 위치
                            if(indexTemp*2 < sizeTemp){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("  [");
                                if(tickData.contains(serial))
                                    System.out.print("O");
                                else
                                    System.out.print("x");
                                System.out.print("]  ");
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("  [");
                                if(tickData.contains(serial))
                                    System.out.print("O");
                                else
                                    System.out.print("x");
                                System.out.print("]  ");
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                      System.out.print("    ");
                            }
                            for(int k = 0; k < sizeTemp; k++)
                                System.out.print("    ");
                        }
                        else{   // 인덱스가 홀수일때 우로 치우친 위치
                            for(int k = 0; k < sizeTemp; k++)
                                System.out.print("    ");
                            if(indexTemp*2 < sizeTemp ){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                                System.out.print("  [");
                                if(tickData.contains(serial))
                                    System.out.print("O");
                                else
                                    System.out.print("x");
                                System.out.print("]  ");
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("  [");
                                if(tickData.contains(serial))
                                    System.out.print("O");
                                else
                                    System.out.print("x");
                                System.out.print("]  ");
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                            }
                        }
                    }
                    System.out.print("  ");
                    tempLevelData ++;
                }
                System.out.println("   |");
                //1틱동안의 흥분여부 출력 완료
                tempLevelData = 0;
                for(Integer level : circuit.keySet()){
                    System.out.print("    |    ");
                    int sizeTemp = circuit.get(level).size();
                    if(line*2 < (maxSize - sizeTemp) || (! depositIter.get(tempLevelData).hasNext())){
                        for(int k = 0; k < circuit.get(level).size()*2 +1; k++)
                            System.out.print("    ");
                        System.out.print("   ");
                    } else{

                        int indexTemp = depositIter.get(tempLevelData).nextIndex();
                        Integer serial = depositIter.get(tempLevelData).next();
                        if(indexTemp % 2 == 0){ // 인덱스가 짝수일때 좌로 치우친 위치
                            if(indexTemp*2 < sizeTemp){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item2);
                                System.out.print("\\");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item1);
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item2);
                                System.out.print("\\");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item1);
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }
                            for(int k = 0; k < sizeTemp; k++)
                                System.out.print("    ");
                        }
                        else{   // 인덱스가 홀수일때 우로 치우친 위치
                            for(int k = 0; k < sizeTemp; k++)
                                System.out.print("    ");
                            if(indexTemp*2 < sizeTemp ){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item2);
                                System.out.print("\\");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item1);
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item2);
                                System.out.print("\\");
                                System.out.printf("%1.1f",stimulationSumLogInACycle.get(tickCounter).get(serial).item1);
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                            }
                        }
                    }
                    System.out.print("  ");
                    tempLevelData ++;
                }
                System.out.println("   |");
                // 1틱동안의 deposit 출력 완료
            }
            tickCounter++;

            System.out.print("    ----");
            for(int i = 0; i < totalNodeQuantity; i++){
                System.out.print("---------");
            }
            System.out.println("------------------------------------------------------");
        }
        // 1틱 출력 종료
        cycleCounter++;
    }


}
