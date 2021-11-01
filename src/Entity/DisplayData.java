package Entity;

import Edge.Edge;
import Level.HistoryOfNode;
import Level.HistoryOfTick;
import Node.Node;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DisplayData {
    private static int cycleCounter = 0;
    public static void displaySingleCycle(ArrayList<ArrayList<HistoryOfTick>> historyOfCycle){
//        HashMap<Integer, ArrayList<Integer>> circuit = new HashMap<>();     //중첩 ArrayList로 하고싶었지만, Level의 이름이 0부터 시작하는 연속적인 자연수가 아닐 가능성을 고려함.
//        ArrayList<ArrayList<Integer>> cycleLog = new ArrayList<>();
        int tempInt = 0;
        int maxSize = 0;    // 도식화의 세로 길이 기준
        int totalNodeQuantity = 0;  // 도식화의 세로 길이 기준
        int levelSize = historyOfCycle.size();
//        for(HashMap<Node, HashSet<Edge>> nodeMap : inputCircuit){
//            ArrayList<Integer> nodeList = new ArrayList<>();
//            totalNodeQuantity += nodeMap.size();
//            if(nodeMap.size() > maxSize)
//                maxSize = nodeMap.size();
//            for(Node n : nodeMap.keySet()){
//                nodeList.add(n.SERIAL_NUMBER);
//            }
//            circuit.put(tempInt, nodeList);
//            tempInt++;
//        }
        //
        ArrayList<ArrayList<HistoryOfTick>> cycleData = new ArrayList<>();
        tempInt = -1;
        while(++tempInt < historyOfCycle.get(0).size())
            cycleData.add(new ArrayList<>());

        for(ArrayList<HistoryOfTick> tick: historyOfCycle){
            tempInt = -1; // 몇번째 틱인지 카운터
            for(HistoryOfTick h : tick){
                cycleData.get(++tempInt).add(h); // 해당 틱의 데이터를 저장하는 arraylist 에 LV 0부터 History 순차 삽입
            }
        }

        for(ArrayList<HistoryOfTick> al :historyOfCycle){
            tempInt = al.get(1).getHistory().size();
            totalNodeQuantity += tempInt;
            if(maxSize < tempInt)
                maxSize = tempInt;
        }
//        //
//        for(HashSet<Integer> hs : inputCycleLog.values()){
//            ArrayList<Integer> tempList = new ArrayList<>();
//            for(Integer i : hs){
//                tempList.add(i);
//            }
//            cycleLog.add(tempList);
//        }


        int tickCounter = 0;
        for(ArrayList<HistoryOfTick> tickData : cycleData) {   // 각 틱에 1회 for 문의 내용 실행. (한 웨이브가 모두 실행될 때까지 계속)
            System.out.print("\n    ----");
            for(int i = 0; i < totalNodeQuantity; i++){
                System.out.print("---------");
            }
            System.out.println("------------------------------------------------------");
            if(tickCounter == 0)
                System.out.println("    <"+cycleCounter+"번째 회기 input data>");
            else
                System.out.println("    <"+cycleCounter+"번째 Cycle "+tickCounter+"번째 Tick>");

//            ArrayList<ListIterator<Integer>> serialIter = new ArrayList<>();
//            ArrayList<ListIterator<Integer>> stimulatedIter = new ArrayList<>();
//            ArrayList<ListIterator<Integer>> depositIter = new ArrayList<>();
//            for(ArrayList<Integer> al : circuit.values()){
//                serialIter.add(al.listIterator());
//                stimulatedIter.add(al.listIterator());
//                depositIter.add(al.listIterator());
//            }
            for (int line = 0; line < maxSize; line++) { //
                for(int level = 0; level < levelSize ;level++){
                    System.out.print("    |    ");
                    int sizeTemp = tickData.get(level).getHistory().size();
                    if(line *2 < maxSize - sizeTemp || line * 2 >= maxSize + sizeTemp){
                        for(int k = 0; k < sizeTemp*2 +1; k++)
                            System.out.print("    ");
                        System.out.print("   ");
                    } else{
                        int indexTemp = line - (maxSize-sizeTemp+1)/ 2;
                        String name = tickData.get(level).getHistory().get(indexTemp).getItem1();
                        if(indexTemp % 2 == 0){ // 인덱스가 짝수일때 좌로 치우친 위치
                            if(indexTemp*2 < sizeTemp ){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("[");
                                System.out.print(name);
                                System.out.print("]");
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("[");
                                System.out.print(name);
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
                                System.out.print(name);
                                System.out.print("]");
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print  ("    ");
                                System.out.print("[");
                                System.out.print(name);
                                System.out.print("]");
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                            }
                        }
                    }
                    System.out.print("  ");
                }
                System.out.println("   |");
                // 첫번째 시리얼 라인 출력 완료
                for(int level = 0; level < levelSize ;level++){
                    System.out.print("    |    ");
                    int sizeTemp = tickData.get(level).getHistory().size();
                    if(line*2 < (maxSize - sizeTemp) || line * 2 >= maxSize + sizeTemp){
                        for(int k = 0; k < sizeTemp*2 +1; k++)
                            System.out.print("    ");
                        System.out.print("   ");
                    } else{
                        int indexTemp = line - (maxSize-sizeTemp+1)/ 2;
                        boolean isThisActivated = (tickData.get(level).getHistory().get(indexTemp).getItem2() < tickData.get(level).getHistory().get(indexTemp).getItem3());
                        if(indexTemp % 2 == 0){ // 인덱스가 짝수일때 좌로 치우친 위치
                            if(indexTemp*2 < sizeTemp){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.print("  [");
                                if(tickData.contains(isThisActivated))
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
                                if(tickData.contains(isThisActivated))
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
                                if(tickData.contains(isThisActivated))
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
                                if(tickData.contains(isThisActivated))
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
                }
                System.out.println("   |");
                //흥분여부 1줄 출력 완료
                for(int level = 0; level < levelSize ;level++){
                    System.out.print("    |    ");
                    int sizeTemp = tickData.get(level).getHistory().size();
                    if(line*2 < (maxSize - sizeTemp) || line * 2 >= maxSize + sizeTemp){
                        for(int k = 0; k < sizeTemp*2 +1; k++)
                            System.out.print("    ");
                        System.out.print("   ");
                    } else{

                        int indexTemp = line - (maxSize-sizeTemp+1)/ 2;
                        double input = tickData.get(level).getHistory().get(indexTemp).getItem3();
                        double critical = tickData.get(level).getHistory().get(indexTemp).getItem2();
                        if(indexTemp % 2 == 0){ // 인덱스가 짝수일때 좌로 치우친 위치
                            if(indexTemp*2 < sizeTemp){    // 인덱스 증가시 중심축에서 멀어짐
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",input);
                                System.out.print("\\");
                                System.out.printf("%1.1f",critical);
                                for(int k = 0; k < indexTemp*2; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",input);
                                System.out.print("\\");
                                System.out.printf("%1.1f",critical);
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
                                System.out.printf("%1.1f",input);
                                System.out.print("\\");
                                System.out.printf("%1.1f",critical);
                                for(int k = indexTemp*2; k < sizeTemp; k++)
                                    System.out.print("    ");
                            }else{  // 인덱스 증가시 중심축으로 모임
                                for(int k = indexTemp*2-sizeTemp; k < sizeTemp; k++)
                                    System.out.print("    ");
                                System.out.printf("%1.1f",input);
                                System.out.print("\\");
                                System.out.printf("%1.1f",critical);
                                for(int k = 0; k < indexTemp*2-sizeTemp; k++)
                                    System.out.print("    ");
                            }
                        }
                    }
                    System.out.print("  ");
                }
                System.out.println("   |");
                // 1틱동안의 deposit 출력 완료
            }

            System.out.print("    ----");
            for(int i = 0; i < totalNodeQuantity; i++){
                System.out.print("---------");
            }
            System.out.println("------------------------------------------------------");
//            try {
//                TimeUnit.MILLISECONDS.sleep(1000);
//            }catch (InterruptedException ie){
//                //
//            }
            tickCounter++;
        }
        // 1틱 출력 종료
        cycleCounter++;
    }


}
