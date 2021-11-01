package Utility;

public class ContainsTwo  <T, S>{
	private T item1;
	private S item2;
	public ContainsTwo(T item1, S item2){
		this.item1 = item1;
		this.item2 = item2;
	}



	public void setItem1(T value){
		item1 = value;
	}
	public void setItem2(S value){
		item2 = value;
	}
	public T getItem1() {
		return item1;
	}
	public S getItem2() {
		return item2;
	}
}
