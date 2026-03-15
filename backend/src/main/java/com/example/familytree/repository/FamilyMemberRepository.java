package com.example.familytree.repository;

import com.example.familytree.model.FamilyMember;
import com.example.familytree.model.FamilyTree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByTree(FamilyTree tree);
}
