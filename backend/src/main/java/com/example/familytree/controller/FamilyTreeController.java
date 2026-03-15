package com.example.familytree.controller;

import com.example.familytree.dto.CreateMemberRequest;
import com.example.familytree.dto.ManageRelationshipRequest;
import com.example.familytree.dto.CreateTreeRequest;
import com.example.familytree.model.FamilyMember;
import com.example.familytree.model.FamilyTree;
import com.example.familytree.service.FamilyTreeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trees")
public class FamilyTreeController {
    private final FamilyTreeService familyTreeService;

    public FamilyTreeController(FamilyTreeService familyTreeService) {
        this.familyTreeService = familyTreeService;
    }

    @GetMapping
    public List<FamilyTree> listTrees(Authentication authentication) {
        return familyTreeService.listTrees(authentication.getName());
    }

    @PostMapping
    public FamilyTree createTree(Authentication authentication, @Valid @RequestBody CreateTreeRequest request) {
        return familyTreeService.createTree(authentication.getName(), request.name());
    }

    @GetMapping("/{treeId}/members")
    public List<FamilyMember> listMembers(@PathVariable Long treeId) {
        return familyTreeService.listMembers(treeId);
    }

    @PostMapping("/{treeId}/members")
    public ResponseEntity<Void> addMember(@PathVariable Long treeId, @Valid @RequestBody CreateMemberRequest request) {
        familyTreeService.addMember(treeId, request);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/members/{memberId}/relationships")
    public FamilyMember addRelationship(@PathVariable Long memberId, @Valid @RequestBody ManageRelationshipRequest request) {
        return familyTreeService.addRelationship(memberId, request);
    }

    @DeleteMapping("/members/{memberId}/relationships")
    public FamilyMember removeRelationship(@PathVariable Long memberId, @Valid @RequestBody ManageRelationshipRequest request) {
        return familyTreeService.removeRelationship(memberId, request);
    }

    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        familyTreeService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
