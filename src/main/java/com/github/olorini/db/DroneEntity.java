package com.github.olorini.db;

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

    public DroneEntity(ResultSet resultSet) throws SQLException {
        setId(resultSet.getLong(1));
        setSerialNumber(resultSet.getString(4));
        setModel(resultSet.getString(3));
        setWeightLimit(resultSet.getInt(6));
        setBatteryCapacity(resultSet.getInt(2));
        setState(resultSet.getString(5));
    }

    public DroneEntity(Drone source) {
        setSerialNumber(source.getSerialNumber());
        setModel(source.getModel().name());
        setWeightLimit(source.getWeightLimit());
        setBatteryCapacity(source.getBatteryCapacity());
        setState(source.getState().name());
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
