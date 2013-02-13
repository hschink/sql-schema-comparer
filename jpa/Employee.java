package hrm;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="employees")
@Inheritance(strategy = InheritanceType.JOINED)
public class Employee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2740703043297793512L;
	
	private int id;
	private String name;
	private String surname;
	private float salary;
	private Department department;
	private Manager boss;
	
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}
	
	public void setFirstName(String name) {
		this.name = name;
	}
	
	public String getFirstName() {
		return name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSalary(float salary) {
		this.salary = salary;
	}

	public float getSalary() {
		return salary;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	@ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE} )
	@JoinColumn(name="department")
	public Department getDepartment() {
		return department;
	}

	public void setBoss(Manager boss) {
		this.boss = boss;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "boss")
	public Manager getBoss() {
		return boss;
	}
}
