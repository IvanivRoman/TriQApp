package model;

public class Company {
	private String oldName; 
	private String newName; 
	private String register;
	private String trId;
	
	public Company() {
		super();
	}

	public Company(String oldName, String newName, String register, String trId) {
		super();
		this.oldName = oldName;
		this.newName = newName;
		this.register = register;
		this.trId = trId;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public String getTrId() {
		return trId;
	}

	public void setTrId(String trId) {
		this.trId = trId;
	}

	@Override
	public String toString() {
		return "Company [oldName=" + oldName + ", newName=" + newName + ", register=" + register + ", trId=" + trId
				+ "]";
	}

	
	
}
