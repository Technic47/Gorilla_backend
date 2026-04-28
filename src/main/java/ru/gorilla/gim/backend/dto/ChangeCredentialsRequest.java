package ru.gorilla.gim.backend.dto;

public record ChangeCredentialsRequest(
        String currentPassword,
        String newUsername,
        String newPassword
) {}
