package com.example.familytree.service;

import com.example.familytree.dto.CreateMemberRequest;
import com.example.familytree.model.FamilyMember;
import com.example.familytree.model.FamilyTree;
import com.example.familytree.repository.FamilyMemberRepository;
import com.example.familytree.repository.FamilyTreeRepository;
import com.example.familytree.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FamilyTreeService {
    private final FamilyTreeRepository treeRepo;
    private final FamilyMemberRepository memberRepo;
    private final UserAccountRepository userRepo;

    public FamilyTreeService(FamilyTreeRepository treeRepo, FamilyMemberRepository memberRepo, UserAccountRepository userRepo) {
        this.treeRepo = treeRepo;
        this.memberRepo = memberRepo;
        this.userRepo = userRepo;
    }

    public List<FamilyTree> listTrees(String username) {
        var owner = userRepo.findByUsername(username).orElseThrow();
        return treeRepo.findAllByOwner(owner);
    }

    public FamilyTree createTree(String username, String name) {
        var owner = userRepo.findByUsername(username).orElseThrow();
        FamilyTree tree = new FamilyTree();
        tree.setName(name);
        tree.setOwner(owner);
        return treeRepo.save(tree);
    }

    public List<FamilyMember> listMembers(Long treeId) {
        FamilyTree tree = treeRepo.findById(treeId).orElseThrow();
        return memberRepo.findByTree(tree);
    }

    public FamilyMember addMember(Long treeId, CreateMemberRequest request) {
        FamilyTree tree = treeRepo.findById(treeId).orElseThrow();
        FamilyMember member = new FamilyMember();
        member.setName(request.name());
        member.setSurname(request.surname());
        member.setSex(request.sex());
        member.setDob(request.dob());
        member.setImage(request.image());
        member.setTree(tree);

        if (request.spouseId() != null) {
            FamilyMember spouse = memberRepo.findById(request.spouseId()).orElseThrow();
            member.setSpouse(spouse);
            spouse.setSpouse(member);
        }

        memberRepo.save(member);
        linkRelations(member, request.parentIds(), request.childIds());
        return memberRepo.save(member);
    }

    public void deleteMember(Long memberId) {
        FamilyMember member = memberRepo.findById(memberId).orElseThrow();
        if (member.getSpouse() != null) {
            FamilyMember spouse = member.getSpouse();
            spouse.setSpouse(null);
        }
        for (FamilyMember parent : Set.copyOf(member.getParents())) {
            parent.getChildren().remove(member);
        }
        for (FamilyMember child : Set.copyOf(member.getChildren())) {
            child.getParents().remove(member);
        }
        memberRepo.delete(member);
    }

    private void linkRelations(FamilyMember member, Set<Long> parentIds, Set<Long> childIds) {
        if (parentIds != null) {
            parentIds.forEach(parentId -> {
                FamilyMember parent = memberRepo.findById(parentId).orElseThrow();
                member.getParents().add(parent);
                parent.getChildren().add(member);
            });
        }
        if (childIds != null) {
            childIds.forEach(childId -> {
                FamilyMember child = memberRepo.findById(childId).orElseThrow();
                member.getChildren().add(child);
                child.getParents().add(member);
            });
        }
    }
}
