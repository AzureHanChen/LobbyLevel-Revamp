package me.yuzegod.lobbylevel.Database;

import java.util.*;

public class KeyValue
{
    private Map<Object, Object> keyvalues;
    
    public KeyValue() {
        this.keyvalues = new HashMap<Object, Object>();
    }
    
    public KeyValue(final String key, final Object value) {
        this.keyvalues = new HashMap<Object, Object>();
        this.add(key, value);
    }
    
    public KeyValue add(final String key, final Object value) {
        this.keyvalues.put(key, value);
        return this;
    }
    
    public String[] getKeys() {
        return this.keyvalues.keySet().toArray(new String[0]);
    }
    
    public String getString(final String key) {
        final Object obj = this.keyvalues.get(key);
        return (obj == null) ? "" : obj.toString();
    }
    
    public Object[] getValues() {
        final List<Object> keys = new ArrayList<Object>();
        for (final Map.Entry<Object, Object> next : this.keyvalues.entrySet()) {
            keys.add(next.getValue());
        }
        return keys.toArray(new Object[0]);
    }
    
    public boolean isEmpty() {
        return this.keyvalues.isEmpty();
    }
    
    public String toCreateString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<Object, Object> next : this.keyvalues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("` ");
            sb.append(next.getValue());
            sb.append(", ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }
    
    public String toInsertString() {
        String ks = "";
        String vs = "";
        for (final Map.Entry<Object, Object> next : this.keyvalues.entrySet()) {
            ks = String.valueOf(ks) + "`" + next.getKey() + "`, ";
            vs = String.valueOf(vs) + "'" + next.getValue() + "', ";
        }
        return "(" + ks.substring(0, ks.length() - 2) + ") VALUES (" + vs.substring(0, vs.length() - 2) + ")";
    }
    
    public String toKeys() {
        final StringBuilder sb = new StringBuilder();
        for (final Object next : this.keyvalues.keySet()) {
            sb.append("`");
            sb.append(next);
            sb.append("`, ");
        }
        return sb.toString().substring(0, sb.length() - 2);
    }
    
    @Override
    public String toString() {
        return this.keyvalues.toString();
    }
    
    public String toUpdateString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<Object, Object> next : this.keyvalues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("`='");
            sb.append(next.getValue());
            sb.append("' ,");
        }
        return sb.substring(0, sb.length() - 2);
    }
    
    public String toWhereString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<Object, Object> next : this.keyvalues.entrySet()) {
            sb.append("`");
            sb.append(next.getKey());
            sb.append("`='");
            sb.append(next.getValue());
            sb.append("' and ");
        }
        return sb.substring(0, sb.length() - 5);
    }
}
