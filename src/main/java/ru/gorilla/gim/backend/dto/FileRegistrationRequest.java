package ru.gorilla.gim.backend.dto;

public record FileRegistrationRequest(Long accountId, String fileName, String contentType) {
}
