package Node;

import Edge.Edge;

import java.util.ArrayList;
import java.util.function.Consumer;

public class OutputNode extends Node implements OutputNodeInter{

    private ArrayList<Edge> prevEdgeList = new ArrayList<>();
    public OutputNode(String name, int serial, double criticalPoint) {
        super(name, serial, criticalPoint);
    }

    @Override
    public void cleanseTick() {
        active = false;
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
    public void addEdge(Edge edge){
        System.out.println("OutPutNode 출발 Edge 삽입 시도");
    }
    @Override
    public void addPrevEdge(Edge edge){
        prevEdgeList.add(edge);
    }


    @Override
    int getNodeFormat() {
        return NODE_FORMAT;
    }


}
