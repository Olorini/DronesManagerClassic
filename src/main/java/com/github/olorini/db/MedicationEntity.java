package com.github.olorini.db;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicationEntity {

    private Long id;

    private String name;

    private int weight;

    private String code;

    private byte[] image;

    public MedicationEntity() {}

    public MedicationEntity(ResultSet resultSet) throws SQLException {
        setId(resultSet.getLong(1));
        setCode(resultSet.getString(2));
        setImage(resultSet.getBytes(3));
        setName(resultSet.getString(4));
        setWeight(resultSet.getInt(5));
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
