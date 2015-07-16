package com.ibm.idmu.api;

import java.util.Map;
import java.util.Properties;

/**
 *
 */
public interface PoolManager {
    void applyConfig(PoolManagerConfiguration cfg) throws PoolManagerException;
    void closePool(String poolName) throws PoolManagerException;
    boolean isPoolName(String poolName);
    void createPool(String poolName, String jdbcConnectionUrl) throws PoolManagerException;
    void createPool(String poolName, String jdbcConnectionUrl, String username, String password) throws PoolManagerException;
    void createPool(String poolName, String jdbcConnectionUrl, Properties properties) throws PoolManagerException;
    <T> T runWithPool(String poolName, SqlOperation<T> sqlOperation) throws PoolManagerException;
    void loadDriverClass(String driverClassPath) throws ClassNotFoundException;
    Map<String, Object> statistics(String poolName) throws PoolManagerException;
    void reset();

    public static class PoolManagerException extends RuntimeException{
        public PoolManagerException(String message) {
            super(message);
        }

        public PoolManagerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}