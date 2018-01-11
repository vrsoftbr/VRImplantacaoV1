/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.venda;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.DateType;
import com.j256.ormlite.support.DatabaseResults;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe criada para persistir data como um número long ordenado "yyyyMMddhhmmssSSS".
 * y = ano<br>
 * M = mes<br>
 * d = dia<br>
 * h = hora<br>
 * m = minuto<br>
 * s = segundo<br>
 * S = milisegundo
 * @author Leandro
 */
public class DateTimePersister extends DateType {
    
    private static final Logger LOG = Logger.getLogger(DateTimePersister.class.getName());
    private static final DateTimePersister singleTon = new DateTimePersister();
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddhhmmssSSS");
    
    private DateTimePersister() {
        super(SqlType.LONG, new Class<?>[]{Date.class});
    }

    public static DateTimePersister getSingleton() {
        return singleTon;
    }

    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
        return results.getLong(columnPos);
    }
    
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {        
        if (javaObject != null) {
            if (fieldType.getType().equals(Date.class)) {
                return Long.parseLong(FORMAT.format((Date) javaObject));
            }
        }
        return super.javaToSqlArg(fieldType, javaObject);
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        if (sqlArg != null && sqlArg instanceof Number) {
            if (fieldType.getType().equals(Date.class)) {
                try {
                    return FORMAT.parse(String.valueOf(sqlArg));
                } catch (ParseException ex) {
                    LOG.log(Level.SEVERE, "Erro ao converter a data", ex);
                    throw new RuntimeException(ex);
                }
            }
        }
        return super.sqlArgToJava(fieldType, sqlArg, columnPos);
    }
    
    
    
}
