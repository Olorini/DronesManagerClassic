package com.github.olorini.db;

import com.github.olorini.core.AppContext;
import com.github.olorini.db.dao.DroneEntity;
import com.github.olorini.db.dao.LoadEntity;
import com.github.olorini.db.dao.MedicationEntity;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class DboRepository {
    private final Logger LOGGER = Logger.getLogger(DboRepository.class);
    private static final String SELECT_DRONES_SQL = "SELECT * FROM DRONES";
    public List<DroneEntity> getDrones() throws DboException {
        try(Connection conn = AppContext.getConnection()) {
            Statement stat = conn.createStatement();
            List<DroneEntity> result = new ArrayList<>();
            try (ResultSet resultSet = stat.executeQuery(SELECT_DRONES_SQL)) {
                while (resultSet.next()) {
                    result.add(new DroneEntity(resultSet));
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }
    public List<DroneEntity> findDroneByState(String state) throws DboException {
        String query = "SELECT * FROM DRONES WHERE state = ?";
        try(Connection conn = AppContext.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, state);
            List<DroneEntity> result = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new DroneEntity(resultSet));
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }

    private static final String SELECT_MED_SQL = "SELECT * from MEDICATION";
    public List<MedicationEntity> getMedicine() throws DboException {
        try(Connection conn = AppContext.getConnection()) {
            Statement stat = conn.createStatement();
            List<MedicationEntity> result = new ArrayList<>();
            try (ResultSet resultSet = stat.executeQuery(SELECT_MED_SQL)) {
                while (resultSet.next()) {
                    result.add(new MedicationEntity(resultSet));
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }

    private static final String EXIST_DRONE_SQL = "SELECT id FROM DRONES" +
            " WHERE SERIAL_NUMBER = ?";
    public boolean existsBySerialNumber(String serialNumber) throws DboException {
        try (Connection conn = AppContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(EXIST_DRONE_SQL);
            ps.setString(1, serialNumber);

            try (ResultSet resultSet = ps.executeQuery()) {
                return resultSet.isBeforeFirst();
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }
    private static final String SAVE_DRONE_SQL =
            "INSERT INTO DRONES (BATTERY_CAPACITY,MODEL,SERIAL_NUMBER,STATE,WEIGHT_LIMIT)" +
            " VALUES (?,?,?,?,?)";
    public Long saveDrone(DroneEntity drone) throws DboException {
        try (Connection conn = AppContext.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(SAVE_DRONE_SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, drone.getBatteryCapacity());
            statement.setString(2, drone.getModel());
            statement.setString(3, drone.getSerialNumber());
            statement.setString(4, drone.getState());
            statement.setInt(5, drone.getWeightLimit());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DboException("Creating record failed, no rows affected");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long result = generatedKeys.getLong(1);
                    conn.commit();
                    return result;
                } else {
                    throw new DboException("Creating record failed, no ID obtained");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }

    private static final String SAVE_DRONE_STATE_SQL =
            "UPDATE drones SET state = ? WHERE id = ?";
    public void saveDroneState(Long droneId, String state) throws DboException {
        try (Connection conn = AppContext.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(SAVE_DRONE_STATE_SQL);
            statement.setString(1, state);
            statement.setLong(2, droneId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DboException("Updating record failed, no rows affected");
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }

    private static final String FIND_DRONE_SQL = "SELECT id,battery_capacity,model,serial_number,state,weight_limit" +
            " FROM DRONES WHERE id = ?";
    public Optional<DroneEntity> findDroneById(Long id) throws DboException {
        try (Connection conn = AppContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(FIND_DRONE_SQL);
            ps.setLong(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                return (resultSet.next()) ? Optional.of(new DroneEntity(resultSet)) : Optional.empty();
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }

    public List<MedicationEntity> findMedication(Set<Long> ids) throws DboException {
        List<MedicationEntity> result = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT id,code,name,weight FROM medication");
        if (ids != null && !ids.isEmpty()) {
            query.append(DboTools.getIn("id", ids.size()));
        }
        try (Connection conn = AppContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query.toString());
            if (ids != null && !ids.isEmpty()) {
                int i = 1;
                for (Long id : ids) {
                    ps.setLong(i++, id);
                }
            }
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new MedicationEntity(resultSet));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
        return result;
    }

    public List<MedicationEntity> findMedicationByDroneId(Long droneId) throws DboException {
        List<MedicationEntity> result = new ArrayList<>();
        String query = "SELECT M.id, M.code, M.name, M.weight" +
                " FROM LOADS as L" +
                " LEFT JOIN MEDICATION M on M.ID = L.MEDICATION_ID" +
                " WHERE L.DRONE_ID = ?";
        try (Connection conn = AppContext.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setLong(1, droneId);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new MedicationEntity(resultSet));
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
        return result;
    }

    private static final String SAVE_LOAD_SQL = "INSERT INTO LOADS (DRONE_ID,MEDICATION_ID) VALUES (?,?)";
    public Long saveLoad(LoadEntity load) throws DboException {
        try (Connection conn = AppContext.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(SAVE_LOAD_SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, load.getDroneId());
            statement.setLong(2, load.getMedicationId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DboException("Creating record failed, no rows affected");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new DboException("Creating record failed, no ID obtained");
                }
            }
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DboException(e.getMessage());
        }
    }

}
