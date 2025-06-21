package org.nvictor.distance.web.controller;

import lombok.RequiredArgsConstructor;
import org.nvictor.distance.service.AddressService;
import org.nvictor.distance.web.dto.request.AddressRequest;
import org.nvictor.distance.web.dto.response.AddressResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
	private final AddressService addressService;

	@PostMapping("/compare")
	public Mono<AddressResponse> compareAddress(@RequestBody AddressRequest request) {
		return addressService.compareAddressCoordinates(request.getAddress());
	}
}