package com.github.olorini.endpoints.pojo;

import com.github.olorini.db.dao.MedicationEntity;

public class Medication {
    private Long id;
    private String name;
    private int weight;
    private String code;

    public Medication(MedicationEntity source) {
        setId(source.getId());
        setName(source.getName());
        setCode(source.getCode());
        setWeight(source.getWeight());
    }

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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
