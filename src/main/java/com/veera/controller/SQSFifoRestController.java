package com.veera.controller;

import java.io.IOException;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.veera.data.Employee;
import com.veera.service.SQSFifoService;

@RestController
public class SQSFifoRestController {
	
	@Autowired
	private SQSFifoService sQSFifoService;
	
	@PostMapping("/fifosend")
	public void sendMesageToFifoQueue(@RequestParam("msg") String msg) throws JMSException
	{
		sQSFifoService.sendToFifoQueue(msg);
	}
	
	@PostMapping("/sendFifoEmpObject")
	public void sendFifoEmpObject(@RequestBody Employee emp) throws JMSException, IOException
	{
		sQSFifoService.sendMessageObject(emp);
	}

}
