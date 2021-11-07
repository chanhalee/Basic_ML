package Node;
/* ---------------------------------------------
## 목차
1. 노드의 기본형
2. 노드에 관련된 데이터 저장소
2.1. Node.NodeData
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

import Edge.Edge;
import java.util.*;
import java.util.function.Consumer;
/*-------------------------------1.노드의 기본형-------------------------------*/

public abstract class Node {
    static int totalNodeQuantity = 0;
    int activeCounter = 0;
    ArrayList<Edge> edgeList = new ArrayList<>(); // 여기서 할당하지 않고 null으로 초기화 한다면 outputNode 의 경우 회로에서 오류발생. (여긴 좀 더 수정 필요)

    final String NAME;
    public final int SERIAL_NUMBER;
    final double INIT_CRITICAL_POINT;
    double criticalPoint;
    protected boolean active = false;


    private NodeData backupData = null; /*prevData 로 변수명 변경 검토*/    // 이상치 발견시 SentinelNode 가 접근하여 롤백 (미구현)




    Node(String name, int serial, double criticalPoint) {
        this.NAME = name;
        this.SERIAL_NUMBER = serial;
        this.INIT_CRITICAL_POINT = criticalPoint;
        this.criticalPoint = criticalPoint;
        backupData = new NodeData(name, serial, criticalPoint);
        totalNodeQuantity++;
    }
    public abstract void addEdge(Edge edge);
    public abstract void addPrevEdge(Edge edge);

    public void setEdge(ArrayList<Edge> set){
        this.edgeList = set;
    }
    public ArrayList<Edge> getEdge(){
        return edgeList;
    }

    public double getCriticalPoint(){ return criticalPoint;}


    public boolean askIgnite(double sparkSum){
        return criticalPoint <= sparkSum;
    }

    public void activate(){
        Consumer<Edge> activateEdge = (edge)->edge.activate();
        active = true;
        edgeList.forEach(activateEdge);
        activeCounter++;
    }
    public abstract void cleanseTick();
    public abstract void adjustEdgeWeigh();
    public boolean isItActivated(){return active;}

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

    //센티넬을 위한 벡업기능 제작 -by NodeData 활용
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

/*-------------------------------2.1.Node.NodeData-------------------------------*/

class NodeData implements NodeDataStorage {

    private final int SERIAL_NUMBER;
    private final String NAME;
    private final double INIT_CRITICAL;


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

/*-------------------------------2.I.데이터 저장소와 관련된 인터페이스-------------------------------*/

interface NodeDataStorage {
    NodeDataStorage copy(); /// deep copy, shallow copy 잘 구현하기!
}

/*-------------------------------E.노드의 발전형에 이식될 인터페이스-------------------------------*/

// 미구현