package com.example.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.bean.ThisIsMain;

@CrossOrigin
@RestController
@RequestMapping("/web")
public class MainRestController  {

	@Autowired
	ThisIsMain thisIsMain;
	
	@RequestMapping(value="/sayHello",method= RequestMethod.GET)
	public String testURL() {
		return thisIsMain.getThisIsBean("WWWW", "12123").getName();
	}
	
	@RequestMapping(value="/list",method= RequestMethod.GET)
	public List<String> testList() {
		return Arrays.asList("123","456");
	}

}
