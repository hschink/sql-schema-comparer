package hrm;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "managers")
public class Manager extends Employee {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5510057309739688470L;

	private String account;
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
}
