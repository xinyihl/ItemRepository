package cn.xinyihl.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private HikariDataSource dataSource;

    public DatabaseManager(FileConfiguration config) {
        initializeDataSource(config);
    }

    private void initializeDataSource(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(config.getString("mysql.drive"));
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getInt("mysql.port") + "/" + config.getString("mysql.database") + "?useSSL=" + config.getBoolean("mysql.useSSL"));
        hikariConfig.setUsername(config.getString("mysql.username"));
        hikariConfig.setPassword(config.getString("mysql.password"));
        hikariConfig.setMaximumPoolSize(config.getInt("mysql.pool.maximumPoolSize"));
        hikariConfig.setMinimumIdle(config.getInt("mysql.pool.minimumIdle"));
        hikariConfig.setConnectionTimeout(config.getLong("mysql.pool.connectionTimeout"));
        hikariConfig.setIdleTimeout(config.getLong("mysql.pool.idleTimeout"));
        hikariConfig.setMaxLifetime(config.getLong("mysql.pool.maxLifetime"));
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    public void disconnect() {
        if (this.dataSource != null && !this.dataSource.isClosed())
            this.dataSource.close();
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource == null || this.dataSource.isClosed())
            throw new SQLException("数据源未初始化或已关闭");
        return this.dataSource.getConnection();
    }

    public void createDatabase() {
        String createSQL = "CREATE TABLE IF NOT EXISTS `saveitem`  (  `id` int NOT NULL AUTO_INCREMENT,  `name` varchar(255) NOT NULL UNIQUE,  `data` longtext NOT NULL,  PRIMARY KEY (`id`)) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(createSQL)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("创建表时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

