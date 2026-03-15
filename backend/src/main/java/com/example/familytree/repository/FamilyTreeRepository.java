package com.example.familytree.repository;

import com.example.familytree.model.FamilyTree;
import com.example.familytree.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyTreeRepository extends JpaRepository<FamilyTree, Long> {
    List<FamilyTree> findAllByOwner(UserAccount owner);
}
