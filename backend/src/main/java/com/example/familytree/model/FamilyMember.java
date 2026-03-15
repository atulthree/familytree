package com.example.familytree.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "family_members")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    private LocalDate dob;

    private String image;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tree_id")
    @JsonIgnore
    private FamilyTree tree;

    @ManyToOne
    @JoinColumn(name = "spouse_id")
    private FamilyMember spouse;

    @ManyToMany
    @JoinTable(
            name = "member_relationships",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private Set<FamilyMember> children = new HashSet<>();

    @ManyToMany(mappedBy = "children")
    private Set<FamilyMember> parents = new HashSet<>();
}
