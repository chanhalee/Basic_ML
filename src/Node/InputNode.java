package Node;

import Edge.Edge;
import Node.InputNodeInter;
import Node.Node;

import java.util.function.Consumer;

public class InputNode extends Node implements InputNodeInter {
    private static int inputNodeQuantity = 0;

    public InputNode(String name, int serial, double criticalPoint) {

        super(name, serial, criticalPoint);
        inputNodeQuantity++;
    }

    @Override
    public void cleanseTick() {
        Consumer<Edge> cleanse = (edge) -> edge.cleanse();
        active = false;
        edgeList.forEach(cleanse);
    }

    @Override
    public void adjustEdgeWeigh() {
        //doNoting;
    }
    @Override
    public void addEdge(Edge edge){edgeList.add(edge);}
    @Override
    public void addPrevEdge(Edge edge){
        System.out.println("inputNode 도착 Edge 삽입 시도");
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