package me.yuzegod.lobbylevel.Database;

import java.sql.*;

public abstract class DataBaseCore
{
    public abstract boolean createTables(final String p0, final KeyValue p1, final String p2) throws SQLException;
    
    public boolean execute(final String sql) throws SQLException {
        return this.getStatement().execute(sql);
    }
    
    public ResultSet executeQuery(final String sql) throws SQLException {
        return this.getStatement().executeQuery(sql);
    }
    
    public int executeUpdate(final String sql) throws SQLException {
        return this.getStatement().executeUpdate(sql);
    }
    
    public abstract Connection getConnection();
    
    private Statement getStatement() throws SQLException {
        return this.getConnection().createStatement();
    }
}
