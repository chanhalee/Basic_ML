public class OutputNode extends Node implements OutputNodeInter{

    OutputNode(int level, String name, double criticalPoint) {
        super(level, name, criticalPoint, NODE_FORMAT);
    }

    @Override
    void deActivate() {

    }
}
