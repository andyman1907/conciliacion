package com.iam.conciliacion.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import com.iam.conciliacion.model.Employ;
import com.iam.conciliacion.model.State;
import com.iam.conciliacion.service.EmployService;
import com.iam.conciliacion.util.CustomErrorType;

@Controller
@RequestMapping(value = "/v1")
public class EmployController {

	@Autowired
	private EmployService _employService;

	@RequestMapping(value = "/employ", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<List<Employ>> getEmploys(
			@RequestParam(value = "employEmail", required = false) String employEmail,
			@RequestParam(value = "employIdentification", required = false) String employIdentification) {
		List<Employ> employs = new ArrayList<Employ>();
		if (employEmail != null) {
			Employ employ = _employService.findByEmail(employEmail);
			if (employ == null) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			employs.add(employ);
		}

		if (employIdentification != null) {
			Employ employ = _employService.findByIdentification(employIdentification);
			if (employs == null) {
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}
			employs.add(employ);
		}

		if (employEmail == null && employIdentification == null) {
			employs = (List<Employ>) _employService.findAllEmploys();
			if (employs.isEmpty()) {
				return new ResponseEntity(HttpStatus.NOT_FOUND);
			}
		}
		return new ResponseEntity<List<Employ>>(employs, HttpStatus.OK);
	}

	// GET BY ID
	@RequestMapping(value = "/employ/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<Employ> getEmployById(@PathVariable("id") Long id) {
		Employ employ = _employService.findById(id);
		if (employ == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<Employ>(employ, HttpStatus.OK);
	}

	// CREATE
	@RequestMapping(value = "/employ", method = RequestMethod.POST, headers = "Accept=application/json")
	public ResponseEntity<?> createEmploy(@RequestBody Employ employ, UriComponentsBuilder ucBuilder) {
		if (employ.getEmployEmail().equals(null) || employ.getEmployIdentification().equals(null)
				|| employ.getEmployName().equals(null) || employ.getEmployLastName().equals(null)
				|| employ.getEmployPosition().equals(null) || employ.getEmployIdentification().equals(null)) {
			return new ResponseEntity(new CustomErrorType("one of the data needed is not present"),
					HttpStatus.CONFLICT);
		}
		if (_employService.findByIdentification(employ.getEmployIdentification()) != null) {
			// logger.error("Unable to create. A User with name {} already
			// exist", user.getName());
			return new ResponseEntity(new CustomErrorType("Unable to create. A Employ with name "
					+ employ.getEmployName() + " " + employ.getEmployLastName() + " already exist."),
					HttpStatus.CONFLICT);
		}
		_employService.saveEmploy(employ);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/v1/employ/{id}").buildAndExpand(employ.getEmployId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	/*
	 * @RequestMapping(value = "/employ", method = RequestMethod.GET, headers =
	 * "Accept=application/json") public ResponseEntity<List<Employ>>
	 * getEmploys(@RequestParam(value = "employEmail", required = false) String
	 * employEmail,
	 * 
	 * @RequestParam(value = "stateCategory", required = false) Long
	 * stateCategory) { List<Employ> employs = new ArrayList<Employ>(); if
	 * (stateName == null && stateCategory == null) { employs = (List<Employ>)
	 * _employService.findActiveConciliator(); if (employs.isEmpty()) { return
	 * new ResponseEntity(HttpStatus.NOT_FOUND); } } return new
	 * ResponseEntity<List<Employ>>(employs, HttpStatus.OK); }
	 */
}