package Level;


public class StimulusAccount {
	private int retainingTerm;
	private int iterator = 0;
	private double adjustingCoefficient;
	private double[] account;

	public StimulusAccount(int retainingTerm, double adjustingCoefficient){
		account = new double[retainingTerm+1];
		int iterTemp = -1;
		while(++iterTemp <= retainingTerm){
			account[iterTemp] = 0d;
		}
		this.retainingTerm = retainingTerm;
		this.adjustingCoefficient = adjustingCoefficient;
	}
	public void addStimulus(double stimulus){
		if(stimulus != 0)
			account[iterator] += stimulus;
	}
	public double getBalance(){
		double result = 0;
		for (double d: account){
			result += d;
		}
		return result - account[iterator]; // 당 틱 중에 받은 자극은 당 틱에 영향을 행사할 수 업음. Readme.md의 Tick 세부 참고
	}
	public void activatedRenewal(double critical){
		int iterTemp = iterator;
		double deletedBalance = 0d;
		while(deletedBalance < critical) {
			deletedBalance += account[iterator];
			account[iterator] = 0;
			replaceIterator();
		}
		iterator = iterTemp;
	}
	public void postTickProcess(){
		replaceIterator();
		renewAccount();
	}
	public void adjustRetainingTerm(int newTerm){
		int counter = -1;
		double[] newAccount = new double[retainingTerm+1];
		while(++counter < newTerm){
			if(counter < retainingTerm+1)
				newAccount[counter] = account[iterator];
			else
				newAccount[counter] = 0d;
			replaceIterator();
		}
		account = newAccount;
		this.retainingTerm = newTerm;
		iterator = 0;
	}
	public void adjustCoefficient(double newCoefficient){this.adjustingCoefficient = newCoefficient;}
	private void renewAccount(){
		account[iterator] = account[iterator]*adjustingCoefficient;
	}
	private void replaceIterator(){
		++iterator;
		if(iterator > retainingTerm)
			iterator = 0;
	}
	public void cleanseAfterTick(){
		int iterTemp = -1;
		while(++iterTemp <= retainingTerm){
			account[iterTemp] = 0d;
		}
		iterator = 0;
	}


}
