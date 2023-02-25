package com.bizzan.bitrade.core;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * @description: Model
 * @author: Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @create: 2021/07/04 14:32
 */
public class Model {
    private String tableName;
    private String conditionClause;
    private Object[] params;
    private String primaryKey = "id";
    private String action = "";
    private String fields = "*";
    private String orderClause = "";
    private String limitClause = "";
    private String groupByClause = "";
    private Map<String, Object> row = new HashMap();
    private Map<String, Object> dirtyData = new HashMap();

    public Model(String tableName) {
        this.tableName = tableName;
    }

    public Object get(String field) {
        return this.row.get(field);
    }

    public Object get(int index) {
        int i = 0;

        for(Iterator var4 = this.row.entrySet().iterator(); var4.hasNext(); ++i) {
            Map.Entry<String, Object> entry = (Map.Entry)var4.next();
            if (index == i) {
                return entry.getValue();
            }
        }

        return null;
    }

    public Model set(String field, Object value) {
        this.dirtyData.put(field, String.valueOf(value));
        return this;
    }

    public long delete() {
        this.action = "DELETE";
        long ret = -1L;

        try {
            String cmd = this.buildSQL();
            ret = DB.exec(cmd, this.params);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return ret;
    }

    public int count() {
        this.action = "COUNT";
        int ret = -1;

        try {
            String cmd = this.buildSQL();
            List<Map<String, String>> list = DB.query(cmd, this.params);
            if (list.size() == 0) {
                return 0;
            }

            ret = Convert.strToInt((String)((Map)list.get(0)).get("count"), 0);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        return ret;
    }

    public long delete(long key) {
        this.where(this.primaryKey + " = ?", key);
        return this.delete();
    }

    public Model create(Map<String, Object> map) {
        this.dirtyData = map;
        return this;
    }

    public Model create(Object bean) throws IllegalArgumentException, IllegalAccessException {
        this.create((Object)BeanMapUtils.bean2Map(bean));
        return this;
    }

    public long insert() throws SQLException {
        this.action = "INSERT INTO";
        long ret = -1L;

        try {
            String cmd = this.buildSQL();
            ret = DB.exec(cmd, this.params);
            return ret;
        } catch (SQLException var4) {
            var4.printStackTrace();
            throw var4;
        }
    }

    public long update() throws SQLException {
        this.action = "UPDATE";
        long ret = -1L;

        try {
            String cmd = this.buildSQL();
            ret = DB.exec(cmd, this.params);
            return ret;
        } catch (SQLException var4) {
            var4.printStackTrace();
            throw var4;
        }
    }

    public long setField(String field, Object value) throws SQLException {
        this.set(field, value);
        return this.update();
    }

    public long setInc(String field, Object value) throws SQLException {
        this.action = "UPDATE_INC";
        long ret = -1L;

        try {
            this.set(field, value);
            String cmd = this.buildSQL();
            ret = DB.exec(cmd, this.params);
            return ret;
        } catch (SQLException var6) {
            var6.printStackTrace();
            throw var6;
        }
    }

    public long setDec(String field, Object value) throws SQLException {
        this.action = "UPDATE_DEC";
        long ret = -1L;

        try {
            this.set(field, value);
            String cmd = this.buildSQL();
            ret = DB.exec(cmd, this.params);
            return ret;
        } catch (SQLException var6) {
            var6.printStackTrace();
            throw var6;
        }
    }

    public long update(long key) throws SQLException {
        this.where(this.primaryKey + " = ?", key);
        return this.update();
    }

    public List<Map<String, String>> select() throws Exception {
        this.action = "SELECT";
        List list = null;

        try {
            String cmd = this.buildSQL();
            if (this.params == null) {
                list = DB.query(cmd);
            } else {
                list = DB.query(cmd, this.params);
            }

            return list;
        } catch (Exception var3) {
            var3.printStackTrace();
            throw var3;
        }
    }

    public <T> List<T> select(Class<T> beanClass) throws Exception {
        List<Map<String, String>> list = this.select();
        List<T> listBean = new ArrayList();
        Iterator var5 = list.iterator();

        while(var5.hasNext()) {
            Map<String, String> map = (Map)var5.next();
            T bean = beanClass.newInstance();
            BeanMapUtils.map2Bean(map, bean);
            listBean.add(bean);
        }

        return listBean;
    }

    public Map<String, String> find() throws Exception {
        this.action = "SELECT";
        Map map = null;

        try {
            String cmd = this.buildSQL();
            List<Map<String, String>> list = null;
            if (this.params != null && this.params.length != 0) {
                list = DB.query(cmd, this.params);
            } else {
                list = DB.query(cmd);
            }

            if (list.size() > 0) {
                map = (Map)list.get(0);
            }

            return map;
        } catch (Exception var4) {
            var4.printStackTrace();
            throw var4;
        }
    }

    public Map<String, String> find(long key) throws Exception {
        this.where(this.primaryKey + " = ?", key);
        return this.find();
    }

    public <T> T find(Class<T> beanClass) throws Exception {
        Map<String, String> map = this.find();
        if (map != null) {
            T bean = beanClass.newInstance();
            BeanMapUtils.map2Bean(map, bean);
            return bean;
        } else {
            return null;
        }
    }

    public Model where(String clause, Object... params) {
        this.conditionClause = clause;
        if (params != null && params.length > 0) {
            this.params = params;
        }

        return this;
    }

    public Model where(String clause) {
        this.conditionClause = clause;
        return this;
    }

    public Model limit(int offset, int limit) {
        this.limitClause = "limit " + limit + " offset " + offset;
        return this;
    }

    public Model limit(int limit) {
        this.limitClause = "limit " + limit;
        return this;
    }

    public Model field(String fields) {
        this.fields = fields;
        return this;
    }

    public Model group(String clause) {
        this.groupByClause = "group by " + clause;
        return this;
    }

    public Model order(String... clause) {
        String clauseJoined = StringUtils.join(clause, ",");
        if (StringUtils.isNotBlank(clauseJoined)) {
            this.orderClause = "order by " + clauseJoined;
        }

        return this;
    }

    private String buildSQL() throws SQLException {
        if (this.action != null && !this.action.equals("")) {
            String mdl = this.action;
            if (this.action.equals("UPDATE_INC") || this.action.equals("UPDATE_DEC")) {
                mdl = "UPDATE";
            }

            StringBuilder sb = new StringBuilder(mdl);
            if (this.action.equals("SELECT")) {
                sb.append(" " + this.fields + " from " + this.tableName);
                if (StringUtils.isNotBlank(this.conditionClause)) {
                    sb.append(" where " + this.conditionClause);
                }

                if (StringUtils.isNotBlank(this.groupByClause)) {
                    sb.append(" " + this.groupByClause);
                }

                if (StringUtils.isNotBlank(this.orderClause)) {
                    sb.append(" " + this.orderClause);
                }

                if (StringUtils.isNotBlank(this.limitClause)) {
                    sb.append(" " + this.limitClause);
                }
            } else {
                ArrayList columnList;
                ArrayList valueList;
                Map.Entry entry;
                Iterator var7;
                if (this.action.equals("INSERT INTO")) {
                    sb.append(" " + this.tableName);
                    columnList = new ArrayList();
                    valueList = new ArrayList();
                    valueList = new ArrayList();
                    if (this.dirtyData.isEmpty()) {
                        throw new SQLException("row map for insert is empty");
                    }

                    var7 = this.dirtyData.entrySet().iterator();

                    while(var7.hasNext()) {
                        entry = (Map.Entry)var7.next();
                        columnList.add("`" + (String)entry.getKey() + "`");
                        valueList.add("?");
                        valueList.add(entry.getValue());
                    }

                    sb.append("(" + StringUtils.join(columnList, ',') + ")");
                    sb.append(" values(" + StringUtils.join(valueList, ',') + ")");
                    this.params = valueList.toArray();
                } else if (this.action.equals("UPDATE")) {
                    sb.append(" " + this.tableName + " set ");
                    if (this.dirtyData.isEmpty()) {
                        throw new SQLException("no data to update");
                    }

                    columnList = new ArrayList();
                    valueList = new ArrayList();
                    Iterator var10 = this.dirtyData.entrySet().iterator();

                    while(var10.hasNext()) {
                        entry = (Map.Entry)var10.next();
                        columnList.add("`" + (String)entry.getKey() + "` = ?");
                        valueList.add(entry.getValue());
                    }

                    sb.append(StringUtils.join(columnList, ','));
                    if (StringUtils.isNotBlank(this.conditionClause)) {
                        sb.append(" where " + this.conditionClause);
                    }

                    if (this.params != null && this.params.length != 0) {
                        this.params = ArrayUtils.addAll(valueList.toArray(), this.params);
                    } else {
                        this.params = valueList.toArray();
                    }
                } else if (!this.action.equals("UPDATE_INC") && !this.action.equals("UPDATE_DEC")) {
                    if (this.action.equals("DELETE")) {
                        sb.append(" from " + this.tableName);
                        if (StringUtils.isNotBlank(this.conditionClause)) {
                            sb.append(" where " + this.conditionClause);
                        }
                    } else if (this.action.equals("COUNT")) {
                        sb = new StringBuilder("SELECT ifnull(count(1),0) as count from " + this.tableName);
                        if (StringUtils.isNotBlank(this.conditionClause)) {
                            sb.append(" where " + this.conditionClause);
                        }

                        if (StringUtils.isNotBlank(this.groupByClause)) {
                            sb.append(" " + this.groupByClause);
                        }
                    }
                } else {
                    sb.append(" " + this.tableName + " set ");
                    char op = (char) (this.action.equals("UPDATE_INC") ? 43 : 45);
                    valueList = new ArrayList();
                    valueList = new ArrayList();
                    var7 = this.dirtyData.entrySet().iterator();

                    while(var7.hasNext()) {
                        entry = (Map.Entry)var7.next();
                        valueList.add("`" + (String)entry.getKey() + "` = `" + (String)entry.getKey() + "` " + op + " ?");
                        valueList.add(entry.getValue());
                    }

                    sb.append(StringUtils.join(valueList, ','));
                    if (StringUtils.isNotBlank(this.conditionClause)) {
                        sb.append(" where " + this.conditionClause);
                    }

                    if (this.params != null && this.params.length != 0) {
                        this.params = ArrayUtils.addAll(valueList.toArray(), this.params);
                    } else {
                        this.params = valueList.toArray();
                    }
                }
            }

            return sb.toString();
        } else {
            throw new SQLException("no action defined for model");
        }
    }
}
