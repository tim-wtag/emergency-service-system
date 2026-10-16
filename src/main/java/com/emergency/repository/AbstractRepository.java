package com.emergency.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emergency.config.DatabaseManager;

public abstract class AbstractRepository<E> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRepository.class);

    private Connection connection;

    @FunctionalInterface
    public interface ResultSetExtractor<T> {
        T extractData(ResultSet rs) throws SQLException;
    }

    protected abstract String getTableName();

    protected AbstractRepository() {
        try {
            this.connection = DatabaseManager.getConnection();
        } catch (SQLException e) {
            logger.error("Error obtaining database connection", e);
        }
    }

    public E save(E e) {
        return e;
    }

    public Connection getConnection() {
        return this.connection;
    }

    protected int executeInsertSQL(String sql, Object... parameters) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error executing update: ", e);
            return 0; 
        }
    }

    protected <T> T executeQuery(String sql, ResultSetExtractor<T> extractor, Object... parameters) {
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return extractor.extractData(rs);
            }
        } catch (SQLException e) {
            logger.error("Error executing query: ", e);
            return null;
        }
    }

    public Map<String, Integer> getStatusCounts(String statusColumnName) {
        String sql = String.format("SELECT %s, COUNT(*) as total FROM %s GROUP BY %s", 
                                   statusColumnName, getTableName(), statusColumnName);

        Map<String, Integer> result = executeQuery(sql, rs -> {
            Map<String, Integer> counts = new HashMap<>();
            while (rs.next()) {
                String status = rs.getString(1); 
                if (status != null) {
                    counts.put(status.toLowerCase(), rs.getInt("total"));
                }
            }
            return counts;
        });
        
        return result != null ? result : new HashMap<>();
    }
}