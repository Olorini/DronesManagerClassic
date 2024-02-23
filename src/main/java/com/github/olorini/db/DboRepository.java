package com.github.olorini.db;

import com.github.olorini.core.AppContext;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    private static final String EXIST_DRONE_SQL = "SELECT id FROM DRONES WHERE SERIAL_NUMBER = ?";
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
