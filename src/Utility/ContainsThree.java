package Utility;

public class ContainsThree <T, S, U>{
	private T item1;
	private S item2;
	private U item3;
	public ContainsThree(T item1, S item2, U item3){
		this.item1 = item1;
		this.item2 = item2;
		this.item3 = item3;
	}



	public void setItem1(T value){
		item1 = value;
	}
	public void setItem2(S value){
		item2 = value;
	}
	public void setItem3(U item3) {
		this.item3 = item3;
	}
	public T getItem1() {
		return item1;
	}

	public S getItem2() {
		return item2;
	}

	public U getItem3() {
		return item3;
	}
}
