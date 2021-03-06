package it.eng.sample;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="All details about the Employee. ")
public class Employee {

	@ApiModelProperty(notes = "The database generated employee ID")
	public long id;

	@ApiModelProperty(notes = "The employee first name")
	public String firstName;

	@ApiModelProperty(notes = "The employee last name")
	public String lastName;

	@ApiModelProperty(notes = "The employee email id")
	public String emailId;

	public Employee() {
		this.id = (long)(Math.random()*Long.MAX_VALUE);
	}

	public Employee(String firstName, String lastName, String emailId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailId = emailId;
		
		this.id = (long)(Math.random()*Long.MAX_VALUE);
	}

}
