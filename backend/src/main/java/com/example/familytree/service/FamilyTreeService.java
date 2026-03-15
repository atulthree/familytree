package com.example.familytree.service;

import com.example.familytree.dto.CreateMemberRequest;
import com.example.familytree.dto.ManageRelationshipRequest;
import com.example.familytree.model.FamilyMember;
import com.example.familytree.model.FamilyTree;
import com.example.familytree.repository.FamilyMemberRepository;
import com.example.familytree.repository.FamilyTreeRepository;
import com.example.familytree.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    @Transactional
    public void addMember(Long treeId, CreateMemberRequest request) {
        FamilyTree tree = treeRepo.findById(treeId).orElseThrow();
        FamilyMember member = new FamilyMember();
        member.setName(request.name());
        member.setSurname(request.surname());
        member.setSex(request.sex());
        member.setDob(request.dob());
        member.setImage(request.image());
        member.setTree(tree);
        memberRepo.save(member);

        if (request.spouseId() != null) {
            FamilyMember spouse = memberRepo.findById(request.spouseId()).orElseThrow();
            validateMembersCanRelate(member, spouse);
            setSpouseRelationship(member, spouse);
        }

        linkRelations(member, request.parentIds(), request.childIds());
    }

    @Transactional
    public void addRelationship(Long memberId, ManageRelationshipRequest request) {
        FamilyMember source = memberRepo.findById(memberId).orElseThrow();
        FamilyMember target = memberRepo.findById(request.targetMemberId()).orElseThrow();
        validateMembersCanRelate(source, target);

        switch (request.type()) {
            case SPOUSE -> setSpouseRelationship(source, target);
            case PARENT -> {
                source.getParents().add(target);
                target.getChildren().add(source);
            }
            case CHILD -> {
                source.getChildren().add(target);
                target.getParents().add(source);
            }
        }
    }

    @Transactional
    public void removeRelationship(Long memberId, ManageRelationshipRequest request) {
        FamilyMember source = memberRepo.findById(memberId).orElseThrow();
        FamilyMember target = memberRepo.findById(request.targetMemberId()).orElseThrow();
        validateMembersCanRelate(source, target);

        switch (request.type()) {
            case SPOUSE -> {
                if (source.getSpouse() != null && Objects.equals(source.getSpouse().getId(), target.getId())) {
                    source.setSpouse(null);
                    target.setSpouse(null);
                }
            }
            case PARENT -> {
                source.getParents().remove(target);
                target.getChildren().remove(source);
            }
            case CHILD -> {
                source.getChildren().remove(target);
                target.getParents().remove(source);
            }
        }
    }

    @Transactional
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
                validateMembersCanRelate(member, parent);
                member.getParents().add(parent);
                parent.getChildren().add(member);
            });
        }
        if (childIds != null) {
            childIds.forEach(childId -> {
                FamilyMember child = memberRepo.findById(childId).orElseThrow();
                validateMembersCanRelate(member, child);
                member.getChildren().add(child);
                child.getParents().add(member);
            });
        }
    }

    private void setSpouseRelationship(FamilyMember source, FamilyMember target) {
        if (source.getSpouse() != null && !Objects.equals(source.getSpouse().getId(), target.getId())) {
            source.getSpouse().setSpouse(null);
        }
        if (target.getSpouse() != null && !Objects.equals(target.getSpouse().getId(), source.getId())) {
            target.getSpouse().setSpouse(null);
        }
        source.setSpouse(target);
        target.setSpouse(source);
    }

    private void validateMembersCanRelate(FamilyMember source, FamilyMember target) {
        if (Objects.equals(source.getId(), target.getId())) {
            throw new IllegalArgumentException("A member cannot relate to itself");
        }
        if (!Objects.equals(source.getTree().getId(), target.getTree().getId())) {
            throw new IllegalArgumentException("Members must belong to the same tree");
        }
    }
}
