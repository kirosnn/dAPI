package fr.kirosnn.dAPI.sqlite;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * The type Sq lite database.
 */
public class SQLiteDatabase {

    private final File databaseFile;
    private Connection connection;
    private final Plugin plugin;

    /**
     * Instantiates a new Sq lite database.
     *
     * @param plugin the plugin
     * @param dbName the db name
     * @throws IOException the io exception
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
     * Connect.
     *
     * @throws SQLException the sql exception
     */
    public void connect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }
        plugin.getLogger().info("Connecting to the database...");
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        plugin.getLogger().info("Successfully connected to the database.");
    }

    /**
     * Reconnect.
     *
     * @throws SQLException the sql exception
     */
    public void reconnect() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            return;
        }
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());
    }

    /**
     * Ensure connection.
     *
     * @throws SQLException the sql exception
     */
    public void ensureConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            reconnect();
        }
    }

    /**
     * Close.
     *
     * @throws SQLException the sql exception
     */
    public void close() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            plugin.getLogger().info("Closing database connection...");
            this.connection.close();
            plugin.getLogger().info("Database connection closed successfully.");
        }
    }

    /**
     * Execute update.
     *
     * @param query the query
     * @throws SQLException the sql exception
     */
    public void executeUpdate(String query) throws SQLException {
        ensureConnection();
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.executeUpdate();
        }
    }

    /**
     * Execute query result set.
     *
     * @param query the query
     * @return the result set
     * @throws SQLException the sql exception
     */
    public ResultSet executeQuery(String query) throws SQLException {
        ensureConnection();
        PreparedStatement statement = this.connection.prepareStatement(query);
        return statement.executeQuery();
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    public Connection getConnection() throws SQLException {
        ensureConnection();
        return this.connection;
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public boolean isConnected() throws SQLException {
        return this.connection != null && !this.connection.isClosed();
    }
}