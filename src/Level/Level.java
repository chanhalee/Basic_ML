package Level;

import Edge.Edge;
import Node.Node;
import Utility.ContainsThree;
import Utility.ContainsTwo;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Level {
	private ArrayList<ContainsTwo<Node, StimulusAccount>> nodesWithAccount = new ArrayList<>();// 노드, retaining Term, account 의 arrayList
	private ArrayList<HistoryOfTick> historyOfCycle = new ArrayList<>();
	private final String NAME;
	private final int level;
	private boolean anyActivated = false;

	public Level(String NAME, int level){
		this.NAME = NAME;
		this.level = level;
	}

	public void addNode(Node newNode, int retainingTerm, double adjustCoefficient){
		nodesWithAccount.add(new ContainsTwo<Node, StimulusAccount>(newNode, new StimulusAccount(retainingTerm, adjustCoefficient)));
	}

	public ArrayList<HistoryOfTick> getHistory(){
		return (historyOfCycle);
	}

	public void makeHistory(){
		HistoryOfTick newHistory = new HistoryOfTick();
		for(ContainsTwo<Node, StimulusAccount> ct: nodesWithAccount){
			newHistory.addNodeHistory(
					new ContainsThree<String, Double, Double>(String.valueOf(ct.getItem1().SERIAL_NUMBER),
					ct.getItem1().getCriticalPoint(),
					ct.getItem2().getBalance())
			);
		}
		historyOfCycle.add(newHistory);
	}

	public void setQueueData(int nodeSerial){
		for(ContainsTwo<Node, StimulusAccount> ct: nodesWithAccount){
			if(ct.getItem1().SERIAL_NUMBER == nodeSerial) {
				ct.getItem2().addStimulus(ct.getItem1().getCriticalPoint());
				break;
			}
		}
	}

	public boolean tick(){
		anyActivated = false;
		for(ContainsTwo<Node, StimulusAccount> ct: nodesWithAccount){
			if(ct.getItem2().getBalance() >= ct.getItem1().getCriticalPoint()) {
				ct.getItem1().activate();
				if(!anyActivated)
					anyActivated = true;
			}
		}
		return anyActivated;
	}

	public void LinkingEdgeWithStartNode(Edge edge){
		for(ContainsTwo<Node, ?> ct: nodesWithAccount){
			if(ct.getItem1().SERIAL_NUMBER == edge.getSTART_NODE_SERIAL()) {
				ct.getItem1().addEdge(edge);
				break;
			}
		}
	}
	public void LinkingEdgeWithDestNode(Edge edge){
		for(ContainsTwo<Node, StimulusAccount> ct: nodesWithAccount){
			if(ct.getItem1().SERIAL_NUMBER == edge.getDestination()) {
				ct.getItem1().addPrevEdge(edge);
				edge.linkFollowingNodeAccount(ct.getItem2());
				break;
			}
		}
	}


	public void postTickProcess(){
		makeHistory();
		activatedNodeBalanceAdjust();
		renewAccount();
		adjustEdgeWeigh();
		cleanseActive();
		anyActivated = false;
	}
	public void preCycleProcess(){
		renewAccount();
	}

	public void postCycleProcess(){
		Consumer<ContainsTwo<Node, StimulusAccount>> cleanseAll
				= (containsTwo)->{containsTwo.getItem1().cleanseTick(); containsTwo.getItem2().cleanseAfterTick();};
		/// visualize 이식.
		historyOfCycle.clear();
		nodesWithAccount.forEach(cleanseAll);
	}

	private void renewAccount(){
		for(ContainsTwo<Node, StimulusAccount> ct: nodesWithAccount){
			ct.getItem2().postTickProcess();
		}
	}
	private void adjustEdgeWeigh(){
		Consumer<ContainsTwo<Node, StimulusAccount>> adjustWeigh = (containsTwo)->containsTwo.getItem1().adjustEdgeWeigh();
		nodesWithAccount.forEach(adjustWeigh);
	}
	private void cleanseActive(){
		Consumer<ContainsTwo<Node, StimulusAccount>> cleanseNode = (containsTwo)->containsTwo.getItem1().cleanseTick();
		nodesWithAccount.forEach(cleanseNode);
	}
	private void activatedNodeBalanceAdjust(){
		Consumer<ContainsTwo<Node, StimulusAccount>> adjustBalance
				= (containsTwo)->{if(containsTwo.getItem1().isItActivated()) containsTwo.getItem2().activatedRenewal(containsTwo.getItem1().getCriticalPoint());};
		nodesWithAccount.forEach(adjustBalance);
	}

	private double balanceOfAccount(int index){
		double result = 0;
		if(index < 0 || index >= historyOfCycle.size()) {
			System.out.println("잘못된 인덱스 청구! error_code: Level.(private)balanceOfAccount( "+index+" )");
			return Double.MIN_VALUE;
		}
		return nodesWithAccount.get(index).getItem2().getBalance();
	}
}
