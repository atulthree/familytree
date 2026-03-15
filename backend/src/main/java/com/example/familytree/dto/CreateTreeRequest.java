package com.example.familytree.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTreeRequest(@NotBlank String name) {
}
