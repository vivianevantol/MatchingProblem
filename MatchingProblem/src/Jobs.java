
public class Jobs {

	private int number;
	private int tail;
	private int release;
	private int processing1;
	private int processing2;
	private int ID;
	
	public Jobs(int number, int tail, int release, int processing1, int processing2, int ID){
		this.number = number;
		this.ID = ID;
		this.tail = tail;
		this.release = release;
		this.processing1 = processing1;
		this.processing2 = processing2;
	}
	
	public int getTail(){
		return tail;
	}
	
	public int getID(){
		return ID;
	}
	
	public int getRelease(){
		return release;
	}
	
	public int getProcessing1(){
		return processing1;
	}
	
	public int getProcessing2(){
		return processing2;
	}
	
	public int getNumber(){
		return number;
	}
}
