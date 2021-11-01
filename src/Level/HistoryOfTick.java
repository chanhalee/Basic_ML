package Level;

import Utility.ContainsThree;

import java.util.ArrayList;

public class HistoryOfTick {
	private ArrayList<ContainsThree<String, Double, Double>> history = new ArrayList<>();
	public HistoryOfTick(){};
	public void addNodeHistory(ContainsThree<String, Double, Double> nodeHistory){
		history.add(nodeHistory);
	}
	public ArrayList<ContainsThree<String, Double, Double>> getHistory(){
		return history;
	}
}
