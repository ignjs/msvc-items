package com.ign.springcloud.msvc.items.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.ign.springcloud.msvc.items.model.Item;
import com.ign.libs.msvc.commons.entity.Product;

@Service
//@Primary // This annotation is used to indicate that if there are multiple beans of the
					// same type, the one with this annotation should be used.
public class ItemServiceWebClient implements ItemService {

	private final WebClient webClient;

	public ItemServiceWebClient(WebClient webClient) {
		this.webClient = webClient;
	}

	/**
	 * Retrieves a list of all items by making a GET request to an external service.
	 * The response is expected to be in JSON format and contains a list of
	 * products.
	 * Each product is then mapped to an Item object with a random quantity between
	 * 1 and 10.
	 *
	 * @return a list of Item objects retrieved from the external service.
	 */
	@Override
	public List<Item> findAll() {
		return this.webClient
				.get()
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(Product.class)
				.map(p -> new Item(p, new Random().nextInt(10) + 1))
				.collectList()
				.block();
	}

	/**
	 * Finds an Item by its ID using a WebClient.
	 *
	 * This method sends a GET request to the specified URI with the given ID,
	 * retrieves the response as a Product, and maps it to an Item with a random
	 * quantity.
	 *
	 * @param id the ID of the Item to find
	 * @return an Optional containing the found Item, or an empty Optional if not
	 *         found
	 */
	@Override
	public Optional<Item> findAById(Long id) {
		/* try { */
		return Optional.ofNullable(webClient.get().uri("/{id}", id)
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

	/**
	 * Saves the given product using a WebClient.
	 *
	 * This method sends a POST request with the product data in JSON format to the
	 * configured endpoint.
	 * It expects a response containing the saved product data in JSON format, which
	 * is then converted
	 * back to a Product object.
	 *
	 * @param product the product to be saved
	 * @return the saved product
	 */
	@Override
	public Product save(Product product) {
		return webClient.post()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(product)
				.retrieve()
				.bodyToMono(Product.class)
				.block();
	}

	/**
	 * Updates an existing product with the given ID using a WebClient.
	 *
	 * @param product the product to update
	 * @param id      the ID of the product to update
	 * @return the updated product
	 */
	@Override
	public Product update(Product product, Long id) {
		return webClient.put()
				.uri("/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(product)
				.retrieve()
				.bodyToMono(Product.class)
				.block();
	}

	/**
	 * Deletes an item with the specified ID.
	 *
	 * @param id the ID of the item to be deleted
	 */
	@Override
	public void delete(Long id) {
		webClient.delete().uri("/{id}", id).retrieve().bodyToMono(Void.class).block();
	}

}
