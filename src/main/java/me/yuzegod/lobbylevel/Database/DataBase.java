package me.yuzegod.lobbylevel.Database;

import org.bukkit.configuration.*;
import me.yuzegod.lobbylevel.Util.*;
import java.util.*;
import java.sql.*;

public class DataBase
{
    private final DataBaseCore dataBaseCore;
    
    public DataBase(final DataBaseCore core) {
        this.dataBaseCore = core;
    }
    
    public static DataBase create(final ConfigurationSection dbConfig) {
        return new DataBase(new MySQLCore(dbConfig));
    }
    
    public boolean close() {
        try {
            this.dataBaseCore.getConnection().close();
            return true;
        }
        catch (SQLException ex) {
            return false;
        }
    }
    
    public boolean copyTo(final DataBaseCore db) {
        try {
            final String src = this.dataBaseCore.getConnection().getMetaData().getURL();
            final String des = db.getConnection().getMetaData().getURL();
            ResultSet rs = this.dataBaseCore.getConnection().getMetaData().getTables(null, null, "%", null);
            final List<String> tables = new LinkedList<String>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
            rs.close();
            int s = 0;
            for (final String table : tables) {
                Log.info("\u5f00\u59cb\u590d\u5236\u6e90\u6570\u636e\u5e93\u4e2d\u7684\u8868 " + table + " ...");
                if (!table.toLowerCase().startsWith("sqlite_autoindex_")) {
                    Log.info("\u6e05\u7a7a\u76ee\u6807\u6570\u636e\u5e93\u4e2d\u7684\u8868 " + table + " ...");
                    db.execute("DELETE FROM " + table);
                    rs = this.dataBaseCore.executeQuery("SELECT * FROM " + table);
                    int n = 0;
                    String query = "INSERT INTO " + table + " VALUES (";
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); ++i) {
                        query = String.valueOf(query) + "?, ";
                    }
                    query = String.valueOf(query.substring(0, query.length() - 2)) + ")";
                    final PreparedStatement ps = db.getConnection().prepareStatement(query);
                    long time = System.currentTimeMillis();
                    while (rs.next()) {
                        ++n;
                        for (int j = 1; j <= rs.getMetaData().getColumnCount(); ++j) {
                            ps.setObject(j, rs.getObject(j));
                        }
                        ps.addBatch();
                        if (n % 100 == 0) {
                            ps.executeBatch();
                        }
                        if (System.currentTimeMillis() - time > 500L) {
                            Log.info("\u5df2\u590d\u5236 " + n + " \u6761\u8bb0\u5f55...");
                            time = System.currentTimeMillis();
                        }
                    }
                    Log.info("\u6570\u636e\u8868 " + table + " \u590d\u5236\u5b8c\u6210 \u5171 " + n + " \u6761\u8bb0\u5f55...");
                    s += n;
                    ps.executeBatch();
                    rs.close();
                }
            }
            Log.info("\u6210\u529f\u4ece " + src + " \u590d\u5236 " + s + " \u6761\u6570\u636e\u5230 " + des + " ...");
            db.getConnection().close();
            this.dataBaseCore.getConnection().close();
            return true;
        }
        catch (SQLException ex) {
            return false;
        }
    }
    
    public boolean createTables(final String tableName, final KeyValue fields, final String Conditions) {
        try {
            this.dataBaseCore.createTables(tableName, fields, Conditions);
            return this.isTableExists(tableName);
        }
        catch (Exception e) {
            this.sqlerr("\u521b\u5efa\u6570\u636e\u8868 " + tableName + " \u5f02\u5e38(\u5185\u90e8\u65b9\u6cd5)...", e);
            return false;
        }
    }
    
    public int dbDelete(final String tableName, final KeyValue fields) {
        final String sql = "DELETE FROM `" + tableName + "` WHERE " + fields.toWhereString();
        try {
            return this.dataBaseCore.executeUpdate(sql);
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
            return 0;
        }
    }
    
    public boolean dbExist(final String tableName, final KeyValue fields) {
        final String sql = "SELECT * FROM " + tableName + " WHERE " + fields.toWhereString();
        try {
            return this.dataBaseCore.executeQuery(sql).next();
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
            return false;
        }
    }
    
    public int dbInsert(final String tabName, final KeyValue fields) {
        final String sql = "INSERT INTO `" + tabName + "` " + fields.toInsertString();
        try {
            return this.dataBaseCore.executeUpdate(sql);
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
            return 0;
        }
    }
    
    public List<KeyValue> dbSelect(final String tableName, final KeyValue fields, final KeyValue selCondition) {
        final String sql = "SELECT " + fields.toKeys() + " FROM `" + tableName + "`" + ((selCondition == null) ? "" : (" WHERE " + selCondition.toWhereString()));
        final List<KeyValue> kvlist = new ArrayList<KeyValue>();
        try {
            final ResultSet dbresult = this.dataBaseCore.executeQuery(sql);
            while (dbresult.next()) {
                final KeyValue kv = new KeyValue();
                String[] keys;
                for (int length = (keys = fields.getKeys()).length, i = 0; i < length; ++i) {
                    final String col = keys[i];
                    kv.add(col, dbresult.getString(col));
                }
                kvlist.add(kv);
            }
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
        }
        return kvlist;
    }
    
    public String dbSelectFirst(final String tableName, final String fields, final KeyValue selConditions) {
        final String sql = "SELECT " + fields + " FROM " + tableName + " WHERE " + selConditions.toWhereString() + " LIMIT 1";
        try {
            final ResultSet dbresult = this.dataBaseCore.executeQuery(sql);
            if (dbresult.next()) {
                return dbresult.getString(fields);
            }
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
        }
        return null;
    }
    
    public int dbUpdate(final String tabName, final KeyValue fields, final KeyValue upCondition) {
        final String sql = "UPDATE `" + tabName + "` SET " + fields.toUpdateString() + " WHERE " + upCondition.toWhereString();
        try {
            return this.dataBaseCore.executeUpdate(sql);
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
            return 0;
        }
    }
    
    public DataBaseCore getDataBaseCore() {
        return this.dataBaseCore;
    }
    
    public boolean isValueExists(final String tableName, final KeyValue fields, final KeyValue selCondition) {
        final String sql = "SELECT " + fields.toKeys() + " FROM `" + tableName + "`" + ((selCondition == null) ? "" : (" WHERE " + selCondition.toWhereString()));
        try {
            final ResultSet dbresult = this.dataBaseCore.executeQuery(sql);
            return dbresult.next();
        }
        catch (Exception e) {
            this.sqlerr(sql, e);
            return false;
        }
    }
    
    public boolean isFieldExists(final String tableName, final KeyValue fields) {
        try {
            final DatabaseMetaData dbm = this.dataBaseCore.getConnection().getMetaData();
            final ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next()) {
                final ResultSet f = dbm.getColumns(null, null, tableName, fields.getKeys()[0]);
                return f.next();
            }
        }
        catch (SQLException e) {
            this.sqlerr("\u5224\u65ad \u8868\u540d:" + tableName + " \u5b57\u6bb5\u540d:" + fields.getKeys()[0] + " \u662f\u5426\u5b58\u5728\u65f6\u51fa\u9519!", e);
        }
        return false;
    }
    
    public boolean isTableExists(final String tableName) {
        try {
            final DatabaseMetaData dbm = this.dataBaseCore.getConnection().getMetaData();
            final ResultSet tables = dbm.getTables(null, null, tableName, null);
            return tables.next();
        }
        catch (SQLException e) {
            this.sqlerr("\u5224\u65ad \u8868\u540d:" + tableName + " \u662f\u5426\u5b58\u5728\u65f6\u51fa\u9519!", e);
            return false;
        }
    }
    
    public void sqlerr(final String sql, final Exception e) {
        Log.warning("\u6570\u636e\u5e93\u64cd\u4f5c\u51fa\u9519: " + e.getMessage());
        Log.warning("SQL\u67e5\u8be2\u8bed\u53e5: " + sql);
    }
}
