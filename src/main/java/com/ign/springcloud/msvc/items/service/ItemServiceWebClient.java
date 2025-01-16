package com.ign.springcloud.msvc.items.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ign.springcloud.msvc.items.model.Item;
import com.ign.springcloud.msvc.items.model.Product;

@Service
@Primary // This annotation is used to indicate that if there are multiple beans of the
					// same type, the one with this annotation should be used.
public class ItemServiceWebClient implements ItemService {

	private final WebClient.Builder webClientBuilder;

	public ItemServiceWebClient(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	@Override
	public List<Item> findAll() {
		return this.webClientBuilder.build()
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(Product.class)
				.map(p -> new Item(p, new Random().nextInt(10) + 1))
				.collectList()
				.block();
	}

	@Override
	public Optional<Item> findAById(Long id) {
		/* try { */
		return Optional.ofNullable(webClientBuilder.build().get().uri("/{id}", id)
				.retrieve()
				.bodyToMono(Product.class)
				.map(p -> new Item(p, new Random().nextInt(10) + 1))
				.block());
		/*
		 * } catch (WebClientResponseException e) {
		 * return Optional.empty();
		 * }
		 */

	}

}
