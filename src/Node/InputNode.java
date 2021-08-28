package Node;

import Node.InputNodeInter;
import Node.Node;

public class InputNode extends Node implements InputNodeInter {
    private static int inputNodeQuantity = 0;

    public InputNode(String name, int serial, double criticalPoint) {

        super(name, serial, criticalPoint);
        inputNodeQuantity++;
    }





    @Override
    public boolean transmitSpark() {

        return false;
    }

    @Override
    int getNodeFormat() {
        return NODE_FORMAT;
    }
}