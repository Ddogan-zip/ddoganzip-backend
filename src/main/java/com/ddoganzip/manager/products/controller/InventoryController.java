package com.ddoganzip.manager.products.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@Tag(name = "재고관리", description = "Inventory API")
public class InventoryController {

}
