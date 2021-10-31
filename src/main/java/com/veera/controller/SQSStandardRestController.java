package com.veera.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veera.data.Employee;
import com.veera.service.SQSService;

@RestController
public class SQSStandardRestController {
	
	
	@Autowired
	private SQSService sQSService;
	
	@PostMapping("/send")
	public String sendMessage(@RequestParam String msg)
	{
		return sQSService.sendMessage(msg);
	}
	
	@PostMapping("/sendobject")
	public String sendMessageObject(@RequestBody Employee emp)
	{
		return sQSService.sendMessageObject(emp);
	}
}
