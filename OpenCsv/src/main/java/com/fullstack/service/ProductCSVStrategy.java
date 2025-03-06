package com.fullstack.service;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCSVStrategy {
    @CsvBindByName(column = "name")
    private String name;
    @CsvBindByName(column = "category")
    private String category;
    @CsvBindByName(column = "price")
    private double price;
    @CsvBindByName(column = "sku")
    private String sku;
    @CsvBindByName(column = "stock")
    private int stock;
}
