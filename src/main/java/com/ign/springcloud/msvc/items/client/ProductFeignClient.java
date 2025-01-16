package com.ign.springcloud.msvc.items.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ign.springcloud.msvc.items.model.Product;

@FeignClient(name = "msvc-products")
public interface ProductFeignClient {

	@GetMapping
	List<Product> findAll();

	@GetMapping("/{id}")
	Product details(@PathVariable Long id);
}
