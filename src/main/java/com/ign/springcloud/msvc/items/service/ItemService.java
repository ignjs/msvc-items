package com.ign.springcloud.msvc.items.service;

import java.util.List;
import java.util.Optional;

import com.ign.springcloud.msvc.items.model.Item;
import com.ign.springcloud.msvc.items.model.Product;

public interface ItemService {

	List<Item> findAll();

	Optional<Item> findAById(Long id);

	Product save(Product product);

	Product update(Product product, Long id);

	void delete(Long id);

}
