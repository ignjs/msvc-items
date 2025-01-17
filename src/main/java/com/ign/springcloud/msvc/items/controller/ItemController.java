package com.ign.springcloud.msvc.items.controller;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ign.springcloud.msvc.items.model.Item;
import com.ign.springcloud.msvc.items.model.Product;
import com.ign.springcloud.msvc.items.service.ItemService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

@RefreshScope
@RestController
public class ItemController {

	private final ItemService service;
	private final CircuitBreakerFactory cBreakerFactory;
	private final Logger log = org.slf4j.LoggerFactory.getLogger(ItemController.class);

	// @Qualifier is used to specify which implementation of the ItemService
	// interface
	public ItemController(@Qualifier("itemServiceFeing") ItemService service,
			CircuitBreakerFactory cBreakerFactory) {
		this.cBreakerFactory = cBreakerFactory;
		this.service = service;
	}

	@GetMapping
	public List<Item> list(@RequestParam(name = "name", required = false) String name,
			@RequestHeader(name = "tokenRequest") String token) {
		System.out.println("name: " + name + " token: " + token);
		return service.findAll();
	}

	/**
	 * Handles GET requests to retrieve the details of an item by its ID.
	 * 
	 * @param id the ID of the item to retrieve
	 * @return a ResponseEntity containing the item details if found, or a 404 NOT
	 *         FOUND status with an error message if the item does not exist
	 * 
	 *         The method uses a circuit breaker to handle potential failures when
	 *         calling the service to find the item by ID.
	 *         If the service call fails, a default item is returned with predefined
	 *         values.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> details(@PathVariable Long id) {
		/* Optional<Item> itemOptional = service.findAById(id); */
		Optional<Item> itemOptional = cBreakerFactory.create("items").run(() -> service.findAById(id),
				throwable -> {
					log.error(throwable.getMessage());
					Product product = new Product();
					product.setCreateAt(LocalDate.now());
					product.setId(1L);
					product.setName("Amazon Fire TV Stick");
					product.setPrice(39.99);
					return Optional.of(new Item(product, 5));

				});
		if (itemOptional.isPresent()) {
			return ResponseEntity.ok(itemOptional.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Collections.singletonMap("message", "No existe el producto"));
	}

	/**
	 * Retrieves the details of an item by its ID.
	 * 
	 * This method uses a CircuitBreaker named "items" to handle potential failures.
	 * If the item is found, it returns the item details with an HTTP 200 status.
	 * If the item is not found, it returns an HTTP 404 status with a message
	 * indicating
	 * that the product does not exist.
	 * 
	 * @param id the ID of the item to retrieve
	 * @return a ResponseEntity containing the item details or an error message
	 */
	@CircuitBreaker(name = "items", fallbackMethod = "getFallbackProduct")
	@GetMapping("/details/{id}")
	public ResponseEntity<?> details2(@PathVariable Long id) {
		/* Optional<Item> itemOptional = service.findAById(id); */
		Optional<Item> itemOptional = service.findAById(id);
		if (itemOptional.isPresent()) {
			return ResponseEntity.ok(itemOptional.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Collections.singletonMap("message", "No existe el producto"));
	}

	/**
	 * Fallback method to handle exceptions and provide a default response.
	 *
	 * @param e the exception that triggered the fallback
	 * @return a ResponseEntity containing a default Item with a predefined Product
	 */
	public ResponseEntity<?> getFallbackProduct(Throwable e) {
		log.error(e.getMessage());
		Product product = new Product();
		product.setCreateAt(LocalDate.now());
		product.setId(1L);
		product.setName("Amazon Fire TV Stick");
		product.setPrice(39.99);
		return ResponseEntity.ok(new Item(product, 5));
	}

	/**
	 * Retrieves the details of an item asynchronously by its ID.
	 * This method is protected by a TimeLimiter named "items".
	 *
	 * @param id the ID of the item to retrieve
	 * @return a CompletableFuture containing the ResponseEntity with the item
	 *         details if found,
	 *         or a ResponseEntity with a not found status and a message if the item
	 *         does not exist
	 */
	@CircuitBreaker(name = "items", fallbackMethod = "getFallbackProductCompletableFuture")
	@TimeLimiter(name = "items", fallbackMethod = "getFallbackProductCompletableFuture")
	@GetMapping("/details3/{id}")
	public CompletableFuture<?> details3(@PathVariable Long id) {
		/* Optional<Item> itemOptional = service.findAById(id); */
		return CompletableFuture.supplyAsync(() -> {
			Optional<Item> itemOptional = service.findAById(id);
			if (itemOptional.isPresent()) {
				return ResponseEntity.ok(itemOptional.get());
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Collections.singletonMap("message", "No existe el producto"));
		});
	}

	/**
	 * Fallback method to handle exceptions and provide a default response.
	 *
	 * @param e the exception that triggered the fallback
	 * @return a ResponseEntity containing a default Item with a predefined Product
	 */
	public CompletableFuture<?> getFallbackProductCompletableFuture(Throwable e) {
		log.error(e.getMessage());
		Product product = new Product();
		product.setCreateAt(LocalDate.now());
		product.setId(1L);
		product.setName("Amazon Fire TV Stick");
		product.setPrice(39.99);
		return CompletableFuture.completedFuture(new Item(product, 5));
	}

	/**
	 * Handles the HTTP POST request to save a new product.
	 *
	 * @param product the product to be saved, provided in the request body
	 * @return a ResponseEntity containing the saved product and an HTTP status of
	 *         CREATED
	 */
	@PostMapping
	public ResponseEntity<?> save(@RequestBody Product product) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(product));
	}

	/**
	 * Updates an existing product with the given ID.
	 *
	 * @param product the product details to update
	 * @param id      the ID of the product to update
	 * @return a ResponseEntity containing the updated product and HTTP status code
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestBody Product product, @PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.update(product, id));
	}

	/**
	 * Deletes an item with the specified ID.
	 *
	 * @param id the ID of the item to be deleted
	 * @return a ResponseEntity with no content status
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<Item> itemOptional = service.findAById(id);
		if (itemOptional.isPresent()) {
			service.delete(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}