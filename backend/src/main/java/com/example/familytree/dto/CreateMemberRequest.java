package com.example.familytree.dto;

import com.example.familytree.model.Sex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record CreateMemberRequest(
        @NotBlank String name,
        @NotBlank String surname,
        @NotNull Sex sex,
        LocalDate dob,
        String image,
        Long spouseId,
        Set<Long> parentIds,
        Set<Long> childIds
) {
}
