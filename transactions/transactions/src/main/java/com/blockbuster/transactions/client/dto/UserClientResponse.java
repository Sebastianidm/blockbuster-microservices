package com.blockbuster.transactions.client.dto;

import lombok.Data;

@Data
public class UserClientResponse {

	private Long id;

	private String username;

	private String email;

	private RoleClientResponse role;
}
