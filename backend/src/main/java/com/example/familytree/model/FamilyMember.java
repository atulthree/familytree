package com.example.familytree.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public FamilyTree getTree() {
        return tree;
    }

    public void setTree(FamilyTree tree) {
        this.tree = tree;
    }

    public FamilyMember getSpouse() {
        return spouse;
    }

    public void setSpouse(FamilyMember spouse) {
        this.spouse = spouse;
    }

    public Set<FamilyMember> getChildren() {
        return children;
    }

    public void setChildren(Set<FamilyMember> children) {
        this.children = children;
    }

    public Set<FamilyMember> getParents() {
        return parents;
    }

    public void setParents(Set<FamilyMember> parents) {
        this.parents = parents;
    }
}
