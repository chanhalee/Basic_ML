package Level;

public class HistoryOfNode {
	private final String nodeName;
	private final double criticalPoint;
	private final double inputStimulus;

	public HistoryOfNode(String nodeName, double criticalPoint, double inputStimulus)
	{
		this.nodeName = nodeName;
		this.criticalPoint = criticalPoint;
		this.inputStimulus = inputStimulus;
	}

	public String getNodeName() {
		return nodeName;
	}

	public double getCriticalPoint() {
		return criticalPoint;
	}

	public double getInputStimulus() {
		return inputStimulus;
	}
}
