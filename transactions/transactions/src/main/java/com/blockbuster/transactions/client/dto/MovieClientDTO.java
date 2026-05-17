package com.blockbuster.transactions.client.dto;

import lombok.Data;

@Data
public class MovieClientDTO {
    private Long id;
    private String title;
    private Integer stock;
    private Boolean available;
}