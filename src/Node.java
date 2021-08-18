
/* ---------------------------------------------
## 목차
1. 노드의 기본형
2. 노드에 관련된 데이터 저장소
2.1. NodeData
2.1.1. IdentityData
2.2.
2.I. 데이터 저장소와 관련된 인터페이스
I. 노드의 발전형에 이식될 인터페이스
E. 노드에 관련된 사용자 정의 예외
----------------------------------------------- */
//
//
//
//
//
//
//
//

import java.util.*;
/*-------------------------------1.노드의 기본형-------------------------------*/

abstract class Node {
    static int totalNodeQuantity = 0;
    int activeCounter = 0;
    HashSet<Edge> edgeSet = new HashSet<>();

    final String NAME;
    public final int SERIAL_NUMBER;
    final double INIT_CRITICAL_POINT;
    double criticalPoint;
    boolean active = false;


    private NodeData backupData = null; /*prevData 로 변수명 변경 검토*/    // 이상치 발견시 SentinelNode 가 접근하여 롤백




    Node(String name, int serial, double criticalPoint) {
        this.NAME = name;
        this.SERIAL_NUMBER = serial;
        this.INIT_CRITICAL_POINT = criticalPoint;
        this.criticalPoint = criticalPoint;
        backupData = new NodeData(name, serial, criticalPoint);
        totalNodeQuantity++;
    }

    public void setEdge(HashSet<Edge> set){
        this.edgeSet = set;
    }
    public HashSet<Edge> getEdge(){

        return edgeSet;
    }


    public boolean askIgnite(double sparkSum){
        return criticalPoint <= sparkSum;
    }

    public void activate(){
        active = true;
        activeCounter++;
    }

    public void deActivate() {
        active = false;
    }

    public boolean matches(Object obj) {
        if(obj instanceof Integer){
            Integer that = (Integer) obj;
            return this.hashCode() == that;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + NAME + " -CP " + criticalPoint + " -AC " + activeCounter + " -Hash " + SERIAL_NUMBER + "]";
    }
    @Override
    public int hashCode() {
        int result = SERIAL_NUMBER;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Node) {
            Node that = (Node) obj;
            if (this.hashCode() == that.hashCode())
                return true;

        }
        if(obj instanceof Integer){
            Integer that = (Integer) obj;
            if(this.hashCode() == that)
                return true;
        }
        return false;
    }

    private void updateBackupData(){
        backupData.updateData(criticalPoint, activeCounter);
    }


    ////----- ABSTRACT METHOD------
    abstract int getNodeFormat();

    //센티넬을 위한 벡업기능 제작
}
//
//
//
//
//
//
//
//
//
/*-------------------------------2.노드와 관련된 데이터 저장소-------------------------------*/

/*-------------------------------2.1.NodeData-------------------------------*/

class NodeData implements DataStorage {

    final int SERIAL_NUMBER;
    final String NAME;
    final double INIT_CRITICAL;


    int activeCounter = 0;
    int backUpCounter = 0;
    double criticalPoint;

    NodeData(String name, int serial, double criticalPoint) {
        this.NAME = name;
        this.INIT_CRITICAL = criticalPoint;
        this.criticalPoint = criticalPoint;
        this.SERIAL_NUMBER = serial;
    }


    private NodeData(String name, int serial, double criticalPoint, int activeCounter, int backUpCounter) {
        this.NAME = name;
        this.INIT_CRITICAL = criticalPoint;
        this.criticalPoint = criticalPoint;
        this.activeCounter = activeCounter;
        this.backUpCounter = backUpCounter;
        this.SERIAL_NUMBER = serial;
    }

    void updateData(double criticalPoint, int activeCounter){
        this.criticalPoint = criticalPoint;
        this.activeCounter = activeCounter;
        backUpCounter++;
    }

    @Override
    public NodeData copy() {
        return new NodeData(NAME.toString(), SERIAL_NUMBER, criticalPoint, activeCounter, backUpCounter);
    }

    @Override
    public String toString() {
        return "[" + NAME + " -CP " + criticalPoint + " -AC " + activeCounter + " -Hash " + SERIAL_NUMBER + "]";
    }

}

/*-------------------------------2.1.1.IdentityData-------------------------------*/

/*class IdentityData implements DataStorage {
    private final int SERIAL_NUMBER;
    private final int NODE_FORMAT;

    IdentityData(int nodeFormat, int serial) {
        NODE_FORMAT = nodeFormat;
        SERIAL_NUMBER = serial;
    }


    @Override
    public IdentityData copy() {
        return new IdentityData(SERIAL_NUMBER, NODE_FORMAT);
    }

    @Override
    public int hashCode() {
        int result = (int) (NODE_FORMAT * 1000 + SERIAL_NUMBER);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof IdentityData) {
            IdentityData that = (IdentityData) obj;
            if (this.hashCode() == that.hashCode())
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "" + this.hashCode();
    }

}*/
//
//
/*-------------------------------2.2.Edge-------------------------------*/
/// 독립된 파일에 만듦. (node 밖에서도 사용.)



//
//
//
//
//
//
//
//
//
/*-------------------------------2.I.데이터 저장소와 관련된 인터페이스-------------------------------*/

interface DataStorage {
    DataStorage copy(); /// deep copy, shallow copy 잘 구현하기!
}

//
//
//
//
//
//
//
//
//
/*-------------------------------I.노드의 발전형에 이식될 인터페이스-------------------------------*/
interface NoneOutputNodeInter {
    boolean transmitSpark();
}

interface InputNodeInter extends NoneOutputNodeInter {
    final int NODE_FORMAT = 1;
}

interface ProcessNodeInter extends NoneOutputNodeInter {
    final int NODE_FORMAT = 2;
}

interface OutputNodeInter {
    final int NODE_FORMAT = 3;
}
/*-------------------------------E.노드의 발전형에 이식될 인터페이스-------------------------------*/

class InValidNodeFormatException extends RuntimeException{
    InValidNodeFormatException(String msg){
        super(msg);
    }
    InValidNodeFormatException(){
        super("InValidNodeFormatException");
    }
}