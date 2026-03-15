package com.example.familytree.dto;

import jakarta.validation.constraints.NotNull;

public record ManageRelationshipRequest(
        @NotNull RelationshipType type,
        @NotNull Long targetMemberId
) {
    public enum RelationshipType {
        SPOUSE,
        PARENT,
        CHILD
    }
}
