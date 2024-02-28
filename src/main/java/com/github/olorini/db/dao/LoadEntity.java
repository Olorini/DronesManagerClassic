package com.github.olorini.db.dao;

public class LoadEntity {
    private Long id;
    private DroneEntity drone;

    private MedicationEntity medication;

    public LoadEntity() { }

    public LoadEntity(DroneEntity drone, MedicationEntity medication) {
        this.drone = drone;
        this.medication = medication;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DroneEntity getDrone() {
        return drone;
    }

    public void setDrone(DroneEntity drone) {
        this.drone = drone;
    }

    public MedicationEntity getMedication() {
        return medication;
    }

    public void setMedication(MedicationEntity medication) {
        this.medication = medication;
    }
}
