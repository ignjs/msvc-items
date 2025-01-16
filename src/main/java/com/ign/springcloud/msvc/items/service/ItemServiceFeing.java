package com.ign.springcloud.msvc.items.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ign.springcloud.msvc.items.client.ProductFeignClient;
import com.ign.springcloud.msvc.items.model.Item;
import com.ign.springcloud.msvc.items.model.Product;

import feign.FeignException;

@Service
public class ItemServiceFeing implements ItemService {

	@Autowired(required = true)
	private ProductFeignClient client;

	@Override
	public List<Item> findAll() {
		return client.findAll().stream().map(p -> new Item(p, new Random().nextInt(10) + 1)).collect(Collectors.toList());
	}

	@Override
	public Optional<Item> findAById(Long id) {
		try {
			Product product = client.details(id);
			return Optional.of(new Item(product, new Random().nextInt(10) + 1));
		} catch (FeignException e) {
			return Optional.empty();
		}
	}
}
