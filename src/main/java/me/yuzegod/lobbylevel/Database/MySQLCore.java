package me.yuzegod.lobbylevel.Database;

import org.bukkit.configuration.*;
import me.yuzegod.lobbylevel.Util.*;
import java.sql.*;

public class MySQLCore extends DataBaseCore
{
    private static String driverName;
    private String username;
    private String password;
    private Connection connection;
    private String url;
    
    static {
        MySQLCore.driverName = "com.mysql.jdbc.Driver";
    }
    
    public MySQLCore(final ConfigurationSection cfg) {
        this(cfg.getString("ip"), cfg.getInt("port"), cfg.getString("database"), cfg.getString("username"), cfg.getString("password"));
    }
    
    public MySQLCore(final String host, final int port, final String dbname, final String username, final String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
        this.username = username;
        this.password = password;
        try {
            Class.forName(MySQLCore.driverName).newInstance();
        }
        catch (Exception e) {
            Log.warning("\u6570\u636e\u5e93\u521d\u59cb\u5316\u5931\u8d25 \u8bf7\u68c0\u67e5\u9a71\u52a8 " + MySQLCore.driverName + " \u662f\u5426\u5b58\u5728!");
        }
    }
    
    @Override
    public boolean createTables(final String tableName, final KeyValue fields, final String conditions) throws SQLException {
        final String sql = "CREATE TABLE IF NOT EXISTS `" + tableName + "` ( " + fields.toCreateString() + ((conditions == null) ? "" : (" , " + conditions)) + " ) ENGINE = MyISAM DEFAULT CHARSET=GBK;";
        return this.execute(sql);
    }
    
    @Override
    public Connection getConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return this.connection;
            }
            return this.connection = DriverManager.getConnection(this.url, this.username, this.password);
        }
        catch (SQLException e) {
            Log.warning("\u6570\u636e\u5e93\u64cd\u4f5c\u51fa\u9519: " + e.getMessage());
            Log.warning("\u767b\u5f55URL: " + this.url);
            Log.warning("\u767b\u5f55\u8d26\u6237: " + this.username);
            Log.warning("\u767b\u5f55\u5bc6\u7801: " + this.password);
            return null;
        }
    }
}
