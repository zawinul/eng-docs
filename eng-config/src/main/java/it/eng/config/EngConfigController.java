package it.eng.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.eng.ms.restservice.exception.GenericServerException;
import it.eng.ms.restservice.exception.NotFoundException;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1")
@Api(value="Eng MS Configuration Management System")
public class EngConfigController {

	private static final String dummyPath = "C:\\Workspace\\eng-docs-config\\default";

	
	private ObjectMapper mapper = new ObjectMapper();
	
	@ApiOperation(value = "Legge il config di un servizio", response = Object.class)
	//@GetMapping("/service/config/{serviceName}")
	@GetMapping(value="/service/config/{serviceName}", produces = {"application/json"})
	
	public Object getServiceConfig(@PathVariable("serviceName") String serviceName) {
		File fileToBeRead = new File(new File(dummyPath), serviceName+".config.json");
		if (!fileToBeRead.exists())
			throw new NotFoundException("Non esiste il file "+fileToBeRead.getAbsolutePath());
		try {
			JsonNode obj = mapper.readTree(fileToBeRead);
			return obj;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		}
	}
	
	
	@ApiOperation(value = "Scrive il config di un servizio", response = Object.class)
	@PutMapping("/service/config/{serviceName}")
	public Object setServiceConfig(@PathVariable("serviceName") String serviceName, @RequestBody Map<String, Object> info) {
		try {
			File fileToBeWritten = new File(new File(dummyPath), serviceName+".info.json");
			JsonNode jsonNode = mapper.valueToTree(info);
			mapper.writerWithDefaultPrettyPrinter().writeValue(fileToBeWritten, jsonNode);
			return jsonNode;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		} 	
	}
	
	@ApiOperation(value = "Legge l'info di un servizio", response = Object.class)
	@GetMapping("/service/info/{serviceName}")
	public Object getServiceInfo(@PathVariable("serviceName") String serviceName) {
		File fileToBeRead = new File(new File(dummyPath), serviceName+".info.json");
		if (!fileToBeRead.exists())
			throw new NotFoundException("Non esiste il file "+fileToBeRead.getAbsolutePath());
		try {
			JsonNode obj = mapper.readTree(fileToBeRead);
			return obj;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		}
	}
	
	@ApiOperation(value = "Scrive l'info di un servizio", response = Object.class)
	@PutMapping("/service/info/{serviceName}")
	public Object setServiceInfo(@PathVariable("serviceName") String serviceName, @RequestBody Map<String, Object> info) {
		try {
			File fileToBeWritten = new File(new File(dummyPath), serviceName+".info.json");
			JsonNode jsonNode = mapper.valueToTree(info);
			mapper.writerWithDefaultPrettyPrinter().writeValue(fileToBeWritten, jsonNode);
			return jsonNode;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		} 	
	}
	

	
	
	
	
	
	
	
	
	@ApiOperation(value = "Legge il config di un app", response = Object.class)
	@GetMapping("/app/config/{appName}")
	public Object getAppConfig(@PathVariable("appName") String appName) {
		File fileToBeRead = new File(new File(dummyPath), appName+".config.json");
		if (!fileToBeRead.exists())
			throw new NotFoundException("Non esiste il file "+fileToBeRead.getAbsolutePath());
		try {
			JsonNode obj = mapper.readTree(fileToBeRead);
			return obj;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		}
	}
	
	@ApiOperation(value = "Scrive il config di un app", response = Object.class)
	@PutMapping("/app/config/{appName}")
	public Object setAppConfig(@PathVariable("appName") String appName, @RequestBody Map<String, Object> info) {
		try {
			File fileToBeWritten = new File(new File(dummyPath), appName+".config.json");
			JsonNode jsonNode = mapper.valueToTree(info);
			mapper.writerWithDefaultPrettyPrinter().writeValue(fileToBeWritten, jsonNode);
			return jsonNode;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		} 	
	}

	
	@ApiOperation(value = "Salva l'endpoint, con expire", response = Object.class)
	@PostMapping("/service/endpoint")
	public boolean postEndpoint(@RequestBody EndpointManager.CachedEndpoint info) {
		try {
			EndpointManager.put(info.name,  info.url,  info.expire);
			System.out.println("post endpoint "+info.name+": "+info.url);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		} 	
	}

	
	@ApiOperation(value = "dato un serviceName ritorna l'elenco degli URL degli endpoint attivi", response = Object.class)
	@GetMapping("/service/endpoint/{serviceName}")
	public List<String> getEndpoint(@PathVariable("serviceName") String serviceName) {
		try {
			return EndpointManager.get(serviceName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		} 	
	}

	
	@ApiOperation(value = "ritorna l'elenco di tutti gli endpoint attivi", response = Object.class)
	@GetMapping("/service/endpoint/all")
	public Map<String, List<String>> getAllEndpoint() {
		try {
			return EndpointManager.getAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GenericServerException(e.getMessage());
		} 	
	}

}
