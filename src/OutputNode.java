public class OutputNode extends Node implements OutputNodeInter{

    OutputNode(String name, int serial, double criticalPoint) {
        super(name, serial, criticalPoint);
    }

    @Override
    int getNodeFormat() {
        return NODE_FORMAT;
    }


}
