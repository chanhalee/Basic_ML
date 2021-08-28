package Node;

public class OutputNode extends Node implements OutputNodeInter{

    public OutputNode(String name, int serial, double criticalPoint) {
        super(name, serial, criticalPoint);
    }

    @Override
    int getNodeFormat() {
        return NODE_FORMAT;
    }


}
