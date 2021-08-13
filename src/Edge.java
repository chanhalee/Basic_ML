import java.util.*;
public class Edge {
    final double WEIGHT_COEFFICIENT = 0.05d;
    final int START_NODE_SERIAL;
    final int DESTINATION_NODE_SERIAL;
    double weight;
    double weightDelta;

    Edge(int start, int dest, double weight, double weighDelta){
        this(start, dest, weight);
        this.weightDelta = weighDelta;
    }

    Edge(int start, int dest, double weight){
        this.START_NODE_SERIAL = start;
        this.DESTINATION_NODE_SERIAL = dest;
        this.weight = weight;
        this.weightDelta = weight * WEIGHT_COEFFICIENT;
    }

    void weightAdjustFireTogether(){
        weight += weightDelta;
    }
    void weightAdjustFireTogether(double coefficient){
        weight += weightDelta * coefficient;
    }
    void weightAdjustDestNumb(){
        weight -= weightDelta;
    }
    void weightAdjustDestNumb(double coefficient){
        weight -= weightDelta * coefficient;
    }
    double getWeight(){
        return weight;
    }
    public int getDestination(){
        return DESTINATION_NODE_SERIAL;
    }

    @Override
    public int hashCode() {
        int result = (int) (START_NODE_SERIAL * 1000 + DESTINATION_NODE_SERIAL);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Edge) {
            Edge that = (Edge) obj;
            if (this.hashCode() == that.hashCode())
                return true;
        }
        return false;
    }

}