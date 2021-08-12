
/* ---------------------------------------------
## 목차
1. 노드의 기본형
2. 노드에 관련된 데이터 저장소
2.1 데이터 저장소와 관련된 인터페이스
3. 노드의 발전형에 이식될 인터페이스
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
    NodeData data = null;
    private NodeData backUpData = null; /*prevData 로 변수명 변경 검토*/
    // 이상치 발견시 SentinelNode 가 접근하여 롤백


    Node(int level, String name, double criticalPoint, int nodeFormat) {
        data = new NodeData(level, name, criticalPoint, nodeFormat, ++totalNodeQuantity);
        backUpData = data.copy();
    }

    public IdentityData getIdentity() {
        return data.getIdentity();
    }


    @Override
    public String toString() {
        return "[" + data.NAME + " -CP " + data.criticalPoint + " -AC " + data.activeCounter + " -Hash " + data.getIdentity() + "]";
    }


    ////----- ABSTRACT METHOD------
    abstract void deActivate();
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

class NodeData implements DataStorage {

    final int NODE_LEVEL;
    final String NAME;
    final double INIT_CRITICAL;


    private IdentityData IDENTITY_DATA;
    int activeCounter = 0;
    int backUpCounter = 0;
    boolean active = false;
    double criticalPoint;

    NodeData(int level, String name, double criticalPoint, int nodeFormat, int serial) {
        this(level, name, criticalPoint);
        this.IDENTITY_DATA = new IdentityData(nodeFormat, serial);
    }

    private NodeData(int level, String name, double criticalPoint) {
        NODE_LEVEL = level;
        this.NAME = name;
        this.INIT_CRITICAL = criticalPoint;
        this.criticalPoint = criticalPoint;
    }

    private NodeData(int level, String name, double criticalPoint, int activeCounter, int backUpCounter, IdentityData IDENTITY_DATA) {
        this(level, name, criticalPoint);
        this.IDENTITY_DATA = IDENTITY_DATA;
        this.activeCounter = activeCounter;
        this.backUpCounter = backUpCounter;
    }

    public IdentityData getIdentity() {
        IdentityData result = IDENTITY_DATA.copy();
        return result;
    }


    @Override
    public NodeData copy() {
        return new NodeData(NODE_LEVEL, NAME, criticalPoint, activeCounter, backUpCounter, IDENTITY_DATA);
    }
}


class IdentityData implements DataStorage {
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
/*-------------------------------3.데이터 저장소와 관련된 인터페이스-------------------------------*/

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
/*-------------------------------3.노드의 발전형에 이식될 인터페이스-------------------------------*/
interface NoneOutputNodeInter {
    boolean transmitSpark();
}

interface InputNodeInter extends NoneOutputNodeInter {
    final int NODE_FORMAT = 1;
}

interface ProcessNodeInter extends NoneOutputNodeInter {
    final int NODE_FORMAT = 2;
}