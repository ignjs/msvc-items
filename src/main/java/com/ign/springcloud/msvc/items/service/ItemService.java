package com.ign.springcloud.msvc.items.service;

import java.util.List;
import java.util.Optional;

import com.ign.springcloud.msvc.items.model.Item;

public interface ItemService {

	List<Item> findAll();

	Optional<Item> findAById(Long id);

}
