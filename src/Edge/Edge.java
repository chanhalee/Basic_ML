package Edge;


import Edge.Interfaces.EdgeInter;
import Level.StimulusAccount;

public class Edge implements EdgeInter {
    final double WEIGHT_COEFFICIENT = 0.05d;
    final int START_NODE_SERIAL;
    final int DESTINATION_NODE_SERIAL;
    int activeCounter = 0;
    protected boolean startingNodeActive = false;
    double weight;
    double weightDelta;
    protected StimulusAccount followingNodeAccount;

    public Edge(int start, int dest, double weight, double weighDelta){
        this(start, dest, weight);
        this.weightDelta = weighDelta;
    }
    public Edge(int start, int dest, double weight){
        this.START_NODE_SERIAL = start;
        this.DESTINATION_NODE_SERIAL = dest;
        this.weight = weight;
        this.weightDelta = weight * WEIGHT_COEFFICIENT;
    }
    public boolean checkVital(){
        return true;
    }
    public void cleanse(){startingNodeActive = false;}
    public void activate(){    // 속한 노드가 흥분했을 경우 실행
        activeCounter++;
        followingNodeAccount.addStimulus(weight);
        weight += weightDelta;
    }
    public int getSTART_NODE_SERIAL(){return START_NODE_SERIAL;}

    public void linkFollowingNodeAccount(StimulusAccount account){
        followingNodeAccount = account;
    }
    public void weightAdjustFireTogether(){
        weight += weightDelta;
    }
    public boolean isActivated(){return startingNodeActive;}
    public void weightAdjustFireTogether(double coefficient){
        weight += weightDelta * coefficient;
    }
    public void weightAdjustDestNumb(){
        weight -= weightDelta;
    }
    public void weightAdjustDestNumb(double coefficient){
        weight -= weightDelta * coefficient;
    }
    public double getWeight(){
        return weight;
    }
    public int getDestination(){
        return DESTINATION_NODE_SERIAL;
    }
    public void resetActiveCounter(){
        this.activeCounter = 0;
    }
    @Override
    public int hashCode() {
        return Integer.parseInt(String.valueOf(START_NODE_SERIAL)+ String.valueOf(DESTINATION_NODE_SERIAL));
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
    @Override
    public String toString(){
        return "["+START_NODE_SERIAL+"] ["+DESTINATION_NODE_SERIAL+"] ["+weight +"]";
    }
}



