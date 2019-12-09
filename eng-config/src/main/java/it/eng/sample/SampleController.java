package it.eng.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.eng.ms.restservice.exception.NotFoundException;


@RestController
@RequestMapping("/api/v1")
@Api(value="Employee Management System", description="Operations pertaining to employee in Employee Management System")
public class SampleController {
	
	private static Map<Long, Employee> db;
	static {
		db = new HashMap<Long, Employee>();
		Employee e1 = new Employee("John", "Smith", "john.smith@gmail.com");
		Employee e2 = new Employee("Paul", "White", "paul.white@gmail.com");
		db.put(e1.id,  e1);
		db.put(e2.id,  e2);
	};
	
	@ApiOperation(value = "View a list of available employees", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrieved list"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })

	@GetMapping("/employees")
	public List<Employee> getAllEmployees() {
		List<Employee> ret = new ArrayList<Employee>();
		for(Employee e: db.values())
			ret.add(e);
		return ret;
	}

	@ApiOperation(value = "Get an employee by Id")
	@GetMapping("/employees/{id}")
	public ResponseEntity<Employee> getEmployeeById(
			@ApiParam(value = "Employee id from which employee object will retrieve", required = true)
			@PathVariable(value = "id") Long employeeId) {
		
		Employee employee = db.get(employeeId);
		return ResponseEntity.ok().body(employee);
	}

	@ApiOperation(value = "Add an employee")
	@PostMapping("/employees")
	public Employee createEmployee(
			@ApiParam(value = "Employee object store in database table", required = true)
			@Valid @RequestBody Employee employee) {
		long id = (long)(Math.random()*Long.MAX_VALUE);
		db.put(id, employee);
		return db.get(id);
	}

	@ApiOperation(value = "Update an employee")
	@PutMapping("/employees/{id}")
	public ResponseEntity<Employee> updateEmployee(
			@ApiParam(value = "Employee Id to update employee object", required = true)
			@PathVariable(value = "id") Long employeeId,
			@ApiParam(value = "Update employee object", required = true)
			@Valid @RequestBody Employee employeeDetails) throws NotFoundException {
		Employee employee = db.get(employeeId);
		employee.emailId = employeeDetails.emailId;
		employee.lastName = employeeDetails.lastName;
		employee.firstName = employeeDetails.firstName;
		db.put(employeeId, employee);
		return ResponseEntity.ok(db.get(employeeId));
	}

	@ApiOperation(value = "Delete an employee")
	@DeleteMapping("/employees/{id}")
	public Map<String, Boolean> deleteEmployee(
			@ApiParam(value = "Employee Id from which employee object will delete from database table", required = true)
			@PathVariable(value = "id") Long employeeId)  {
		Employee employee = db.get(employeeId);
		db.remove(employeeId);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return response;
	}
}
