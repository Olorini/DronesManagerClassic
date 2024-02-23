package com.github.olorini.endpoints.pojo;

import java.util.List;

public class Load {
    Long droneId;
    List<Long> medicineIds;

    public Long getDroneId() {
        return droneId;
    }

    public void setDroneId(Long droneId) {
        this.droneId = droneId;
    }

    public List<Long> getMedicineIds() {
        return medicineIds;
    }

    public void setMedicineIds(List<Long> medicineIds) {
        this.medicineIds = medicineIds;
    }
}
