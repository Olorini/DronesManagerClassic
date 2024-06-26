package com.github.olorini.db.dao;

import com.github.olorini.endpoints.pojo.Drone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class DroneEntity {
    private Long id;
    private String serialNumber;
    private String model;
    private int weightLimit;
    private int batteryCapacity;
    private String state;

    public DroneEntity() {}

    public DroneEntity(ResultSet resultSet) throws SQLException {
        setId(resultSet.getLong("id"));
        setSerialNumber(resultSet.getString("serial_number"));
        setModel(resultSet.getString("model"));
        setWeightLimit(resultSet.getInt("weight_limit"));
        setBatteryCapacity(resultSet.getInt("battery_capacity"));
        setState(resultSet.getString("state"));
    }

    public DroneEntity(Drone source) {
        setSerialNumber(source.getSerialNumber());
        if (source.getModel() != null) {
            setModel(source.getModel().name());
        }
        setWeightLimit(source.getWeightLimit());
        setBatteryCapacity(source.getBatteryCapacity());
        if (source.getState() != null) {
            setState(source.getState().name());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getWeightLimit() {
        return weightLimit;
    }

    public void setWeightLimit(int weightLimit) {
        this.weightLimit = weightLimit;
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(int batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroneEntity that = (DroneEntity) o;
        return id.equals(that.id) && serialNumber.equals(that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serialNumber);
    }
}
