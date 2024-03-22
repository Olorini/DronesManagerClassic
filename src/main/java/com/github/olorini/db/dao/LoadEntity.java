package com.github.olorini.db.dao;

public class LoadEntity {
    private Long id;
    private Long droneId;

    private Long medicationId;

    public LoadEntity() { }

    public LoadEntity(Long droneId, Long medicationId) {
        this.droneId = droneId;
        this.medicationId = medicationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDroneId() {
        return droneId;
    }

    public void setDroneId(Long droneId) {
        this.droneId = droneId;
    }

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }
}
