package com.mediai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "medicine_category")
public class MedicineCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name; // e.g. Fever, Cold, Diabetes, Vitamins

    public MedicineCategory() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
