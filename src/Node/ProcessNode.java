package Node;

public class ProcessNode extends Node implements ProcessNodeInter {

    public ProcessNode(String name, int serial, double criticalPoint) {
        super(name, serial, criticalPoint);
    }

    @Override
    int getNodeFormat() {
        return NODE_FORMAT;
    }


    @Override
    public boolean transmitSpark() {

        return false;
    }
}
