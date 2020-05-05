package io.gitee.inrgihc.webmvc.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping("/hello")
	public String hello(@RequestParam(value = "key", defaultValue = "world") String key) {
		return "hello " + key;
	}

	@RequestMapping("/test")
	public Map<String, Object> test(@RequestParam(value = "key") String key) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("name", key);
		ret.put("age", 12);
		return ret;
	}

}