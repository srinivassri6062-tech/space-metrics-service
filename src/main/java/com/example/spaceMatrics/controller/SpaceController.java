package com.example.spaceMatrics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spaceMatrics.service.SpaceServiceImpl;

@RestController
@RequestMapping("/api/space")
public class SpaceController {

	@Autowired
	SpaceServiceImpl spaceService;

	@GetMapping("/accept")
	public ResponseEntity<String> accept(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "endPoint", required = false) String endPoint) {

		Boolean save = spaceService.processRequest(id, endPoint);

		return ResponseEntity.ok(save ? "ok" : "failed");

	}

}
