package Node;

import Edge.Edge;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ProcessNode extends Node implements ProcessNodeInter {

    private ArrayList<Edge> prevEdgeList = new ArrayList<>();
    public ProcessNode(String name, int serial, double criticalPoint) {
        super(name, serial, criticalPoint);
    }

    @Override
    public void adjustEdgeWeigh() {
        if (this.active)
            for (Edge e : prevEdgeList) {
                if (e.isActivated()) {
                    if (this.active)
                        e.weightAdjustFireTogether();
                    else
                        e.weightAdjustDestNumb();
                }
            }
    }
    @Override
    public void addEdge(Edge edge){edgeList.add(edge);}
    @Override
    public void addPrevEdge(Edge edge){
        prevEdgeList.add(edge);
    }
    @Override
    public void cleanseTick() {
        Consumer<Edge> cleanse = (edge) -> edge.cleanse();
        active = false;
        edgeList.forEach(cleanse);
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
