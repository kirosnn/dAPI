package fr.kirosnn.dAPI.sqlite;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteDatabase {

    private final File databaseFile;
    private Connection connection;
    private final Plugin plugin;

    /**
     * Constructor to initialize a SQLite database inside a "databases" folder.
     *
     * @param plugin  The plugin instance.
     * @param dbName  The name of the database file (with .db extension).
     * @throws IOException If the file or folders cannot be created.
     */
    public SQLiteDatabase(@NotNull Plugin plugin, String dbName) throws IOException {
        this.plugin = plugin;

        File databaseFolder = new File(plugin.getDataFolder(), "databases");
        if (!databaseFolder.exists()) {
            databaseFolder.mkdirs();
            plugin.getLogger().info("The folder " + databaseFolder.getPath() + " has been created.");
        }

        this.databaseFile = new File(databaseFolder, dbName);
        if (!this.databaseFile.exists()) {
            if (this.databaseFile.createNewFile()) {
                plugin.getLogger().info("The file " + dbName + " did not exist and has been created.");
            } else {
                throw new IOException("Failed to create the database file: " + dbName);
            }
        } else {
            plugin.getLogger().info("The file " + dbName + " already exists.");
        }
    }

    /**
     * Opens a connection to the database.
     *
     * @throws SQLException If an error occurs while connecting.
     */
    public void connect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            plugin.getLogger().info("The database is already connected.");
            return;
        }
        plugin.getLogger().info("Connecting to the database...");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        plugin.getLogger().info("Successfully connected to the database.");
    }

    /**
     * Ensures the database connection is alive. Reconnects if the connection is lost.
     *
     * @throws SQLException If the connection cannot be restored.
     */
    public void ensureConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            plugin.getLogger().warning("Database connection lost. Attempting to reconnect...");
            connect();
            plugin.getLogger().info("Database connection restored.");
        }
    }

    /**
     * Closes the connection to the database.
     *
     * @throws SQLException If an error occurs while closing.
     */
    public void close() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            plugin.getLogger().info("Closing database connection...");
            this.connection.close();
            plugin.getLogger().info("Database connection closed successfully.");
        }
    }

    /**
     * Executes an SQL update statement (e.g., CREATE, INSERT, UPDATE).
     *
     * @param query The SQL query to execute.
     * @throws SQLException If an error occurs during execution.
     */
    public void executeUpdate(String query) throws SQLException {
        ensureConnection();
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

    /**
     * Executes an SQL query of type SELECT and returns the result.
     *
     * @param query The SQL query to execute.
     * @return ResultSet The result of the query.
     * @throws SQLException If an error occurs during execution.
     */
    public ResultSet executeQuery(String query) throws SQLException {
        ensureConnection();
        PreparedStatement statement = this.connection.prepareStatement(query);
        return statement.executeQuery();
    }

    /**
     * Return database.
     *
     * @return Coonection SQLite.
     * @throws SQLException If connection is close or equal null.
     */
    public Connection getConnection() throws SQLException {
        ensureConnection();
        return this.connection;
    }

    /**
     * Checks if the database is connected.
     *
     * @return true if connected, false otherwise.
     * @throws SQLException If an error occurs during the check.
     */
    public boolean isConnected() throws SQLException {
        return this.connection != null && !this.connection.isClosed();
    }
}