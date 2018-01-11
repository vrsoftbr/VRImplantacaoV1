package vrimplantacao2.utils.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe que facilita a construção de inserts e updates.
 * @author Leandro
 */
public class SQLBuilder {
    private String schema = "";
    private String tableName = "";
    private final Map<String, String> fields = new LinkedHashMap<>();
    private final List<String> returning = new ArrayList<>();
    private boolean formatarSQL = false;
    private String where;

    public void setWhere(String where) {
        this.where = where;
    }

    public void setFormatarSQL(boolean formatarSQL) {
        this.formatarSQL = formatarSQL;
    }

    public void setSchema(String schema) {        
        this.schema = (schema == null) ? "" : schema.trim();
    }
    
    public void setTableName(String tableName) {
        if (tableName != null && !"".equals(tableName.trim())) {
            this.tableName = tableName;
        } else {
            throw new NullPointerException("O nome da tabela não pode ser vazio!");
        }        
    }

    public void put(String campo, int valor) {
        fields.put(campo, String.valueOf(valor));
    }
    
    public void put(String campo, int valor, int nullValue) {
        fields.put(campo, SQLUtils.longIntSQL(valor, nullValue));
    }
    
    public void put(String campo, String valor) {
        fields.put(campo, SQLUtils.stringSQL(valor));
    }
    
    public void put(String campo, String valor, String nullValue) {
        fields.put(campo, !nullValue.equals(valor) ? SQLUtils.stringSQL(valor) : null);
    }
    
    public void put(String campo, double valor) {
        fields.put(campo, String.valueOf(valor));
    }
    
    public void put(String campo, long valor) {
        fields.put(campo, String.valueOf(valor));
    }
    
    public void put(String campo, double valor, double nullValue) {
        fields.put(campo, SQLUtils.doubleSQL(valor, nullValue));
    }
    
    public void put(String campo, Date valor) {
        fields.put(campo, SQLUtils.timestampSQL(valor));
    }
    
    public void put(String campo, Object valor) {
        fields.put(campo, valor != null ? valor.toString() : null);
    }
    
    public void put(String campo, boolean valor) {
        fields.put(campo, valor ? "true" : "false");
    }
    
    public void putNull(String campo) {
        fields.put(campo, null);
    }
    
    public void putSql(String campo, String cmd) {
        fields.put(campo, cmd);
    }
    
    public void clear() {
        fields.clear();
        returning.clear();
    }

    public List<String> getReturning() {
        return returning;
    }

    public String getInsert() {        
        String campos = "";
        String valores = "";
        String saltaLinha = "";
        for (Iterator<String> iterator = fields.keySet().iterator(); iterator.hasNext();) {
            String campo = iterator.next();
            String valor = fields.get(campo);
            if (formatarSQL) {
                saltaLinha = "\n";
            }
            
            campos += campo + (iterator.hasNext() ? "," : "") + saltaLinha;
            valores += valor + (iterator.hasNext() ? "," : "") + saltaLinha;            
        }
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(schema).append(!"".equals(schema) ? "." : "").append(tableName).append("(").append(saltaLinha);
        sql.append(campos);
        sql.append(") values (").append(saltaLinha);
        sql.append(valores);
        sql.append(")");
        if (!returning.isEmpty()) {
            sql.append(" returning ").append(saltaLinha);
            for (Iterator<String> iterator = returning.iterator(); iterator.hasNext();) {
                sql.append(iterator.next());
                if (iterator.hasNext()) {
                    sql.append(",");
                }
                sql.append(saltaLinha);
            }
        }
        
        return sql.toString();
    }
    
    public String getUpdate() { 
        String valores = "";
        String saltaLinha = "";
        if (formatarSQL) {
            saltaLinha = "\n";
        }
        
        for (Iterator<String> iterator = fields.keySet().iterator(); iterator.hasNext();) {
            String campo = iterator.next();
            String valor = fields.get(campo);
            
            valores += campo + " = " + valor + (iterator.hasNext() ? "," : "") + saltaLinha;
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("update ").append(schema).append(!"".equals(schema) ? "." : "").append(tableName).append(" set ").append(saltaLinha);       
        sql.append(valores);
        sql.append(" where ").append(where).append(";");        
        
        return sql.toString();
    }
    
    /**
     * Retorna true se não há algum campo cadastrado.
     * @return True se não houver campos informados.
     */
    public boolean isEmpty() {
        return fields.isEmpty();
    }
}
