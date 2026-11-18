package org.springframework.samples.petclinic.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint used by the React error page to show how an unexpected failure is reported.
 */
@RestController
@RequestMapping("/api")
public class FailingRestController {

	@GetMapping("/oops")
	public void failingRequest() {
		throw new RuntimeException("Expected: controller used to showcase what happens when an exception is thrown");
	}

}
