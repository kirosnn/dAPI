package fr.kirosnn.dAPI.sqlite;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

/**
 * A simple SQLite database manager for Bukkit plugins.
 */
public class SQLiteDatabase {

    private final File databaseFile;
    private Connection connection;
    private final Plugin plugin;

    /**
     * Initializes a new SQLite database.
     *
     * @param plugin the Bukkit plugin
     * @param dbName the name of the database file
     * @throws IOException if the file creation fails
     */
    public SQLiteDatabase(@NotNull Plugin plugin, @NotNull String dbName) throws IOException {
        this.plugin = plugin;

        File databaseFolder = new File(plugin.getDataFolder(), "databases");
        if (!databaseFolder.exists() && databaseFolder.mkdirs()) {
            plugin.getLogger().info("Created 'databases' folder: " + databaseFolder.getPath());
        }

        this.databaseFile = new File(databaseFolder, dbName);
        if (!this.databaseFile.exists() && this.databaseFile.createNewFile()) {
            plugin.getLogger().info("Created new SQLite database file: " + dbName);
        }
    }

    /**
     * Establishes a connection to the database.
     */
    public synchronized void connect() {
        if (isConnected()) return;

        try {
            plugin.getLogger().info("Connecting to the SQLite database...");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
            plugin.getLogger().info("Successfully connected to the database.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to the database!", e);
        }
    }

    /**
     * Closes the database connection.
     */
    public synchronized void close() {
        if (!isConnected()) return;

        try {
            plugin.getLogger().info("Closing SQLite database connection...");
            connection.close();
            connection = null;
            plugin.getLogger().info("Database connection closed successfully.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error closing the database connection!", e);
        }
    }

    /**
     * Checks if the database is connected.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Executes an SQL update query (INSERT, UPDATE, DELETE).
     *
     * @param query the SQL query
     */
    public synchronized void executeUpdate(@NotNull String query) {
        if (!isConnected()) connect();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error executing UPDATE query: " + query, e);
        }
    }

    /**
     * Executes an SQL query and returns a `ResultSet`.
     * **⚠ IMPORTANT:** Make sure to close the `ResultSet` after use.
     *
     * @param query the SQL query
     * @return a ResultSet containing the query results
     */
    public synchronized ResultSet executeQuery(@NotNull String query) {
        if (!isConnected()) connect();

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error executing SELECT query: " + query, e);
            return null;
        }
    }

    /**
     * Retrieves the current database connection.
     *
     * @return the Connection object
     */
    public Connection getConnection() {
        if (!isConnected()) connect();
        return connection;
    }

    /**
     * Begins an SQL transaction.
     */
    public synchronized void beginTransaction() {
        if (!isConnected()) connect();

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to begin transaction!", e);
        }
    }

    /**
     * Commits the current transaction.
     */
    public synchronized void commitTransaction() {
        if (!isConnected()) return;

        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error committing transaction!", e);
        }
    }

    /**
     * Rolls back the current transaction.
     */
    public synchronized void rollbackTransaction() {
        if (!isConnected()) return;

        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Error rolling back transaction!", e);
        }
    }
}