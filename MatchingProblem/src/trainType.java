

public class trainType {
	public double length;
	public double inspectionTime;
	public double repairTime;
	public double cleaningTime;
	public double washingTime;
	public double inspectionProb;
	public double repairProb;
	public double cleanProb;
	public double washProb;
	public double interchangeProb;

	public trainType(double length, double inspectiontime, double repairtime, double cleaningtime, double washingtime, double inspectionProb, double repairProb, double cleanProb,double washProb,double interchangeProb){
		this.length = length;
		this.inspectionTime = inspectiontime;
		this.repairTime = repairtime; 
		this.cleaningTime = cleaningtime;
		this.washingTime = washingtime;
		this.inspectionProb=inspectionProb;
		this.repairProb=repairProb;
		this.cleanProb=cleanProb;
		this.washProb=washProb;
		this.interchangeProb=interchangeProb;
	}

	public double getLength(){
		return length;
	}

	public double getInspectiontime(){
		return inspectionTime;
	}

	public double getRepairtime(){
		return repairTime;
	}

	public double getCleaningtime(){
		return cleaningTime;
	}

	public double getWashingtime(){
		return washingTime;
	}
	
	public double getInspectionprob(){
		return inspectionProb;
	}
	
	public double getRepairprob(){
		return repairProb;
	}
	
	public double getCleanprob(){
		return cleanProb;
	}
	
	public double getWashprob(){
		return washProb;
	}
	
	public double getInterchangeprob(){
		return interchangeProb;
	}
	
}
