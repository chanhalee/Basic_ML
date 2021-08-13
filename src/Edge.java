import java.util.*;
public class Edge {
    final double WEIGHT_COEFFICIENT = 0.05d;
    final IdentityData START_NODE_ID;
    final IdentityData DESTINATION_NODE_ID;
    double weight;
    double weightDelta;

    Edge(IdentityData start, IdentityData dest, double weight, double weighDelta){
        this(start, dest, weight);
        this.weightDelta = weighDelta;
    }

    Edge(IdentityData start, IdentityData dest, double weight){
        this.START_NODE_ID = start;
        this.DESTINATION_NODE_ID = dest;
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
    public IdentityData getDestination(){
        return DESTINATION_NODE_ID;
    }

    @Override
    public int hashCode() {
        int result = (int) (START_NODE_ID.hashCode() * 1000 + DESTINATION_NODE_ID.hashCode());
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