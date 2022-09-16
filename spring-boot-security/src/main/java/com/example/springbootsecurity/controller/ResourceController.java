package com.example.springbootsecurity.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
	
	@RequestMapping({"/hellouser"})
	public String getUser()
	{
		return "HelloUser";
	}
	
	@RequestMapping({"/helloadmin"})
	public String getAdmin()
	{
		return "HelloAdmin";
	}

}
