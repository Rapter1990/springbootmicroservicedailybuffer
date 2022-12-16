package com.microservice.orderservice.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetails {

    private String productName;
    private long productId;
    private long quantity;
    private long price;
}
