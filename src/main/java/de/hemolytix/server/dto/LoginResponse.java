package de.hemolytix.server.dto;

public record LoginResponse(
        String token,
        String username,
        String fullName,
        String role
) {}