package hrm;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="departments")
public class Department implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7188150899545658081L;
	
	private int id;
	private String name;
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
