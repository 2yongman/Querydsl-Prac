package com.codestates.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderCoffeeResponseDto {
    private long coffeeId;
    private Integer quantity;
    private String korName;
    private String engName;
    private Integer price;
}
