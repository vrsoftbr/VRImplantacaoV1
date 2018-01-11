package vrimplantacao2.utils.sql;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe com diversas operações para facilitar a criação scripts sql.
 * @author Leandro
 */
public final class SQLUtils {
    
    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");
    
    private SQLUtils(){ }
    
    /**
     * Coloca aspas simples ao redor de uma string não nula. Utilize para auxiliar na criação de SQLUtils.
     * @param string string a ser tratada.
     * @return string = 'string', se for null retorna null.
     */
    public static String stringSQL(String string) {
        return string != null ? "'" + string + "'" : null;
    }

    /**
     * Formata datas para o padrão do Postgres.
     * @param data
     * @return String com a data formatada ou null se null for passado com parâmetro.
     */
    public static String dateSQL(Date data) {
        if (data != null) {
            return stringSQL(DATE_FORMAT.format(data));
        }
        return null;
    }
    
    /**
     * Formata datas para o padrão Timestamp do Postgres.
     * @param data
     * @return String com a data formatada ou null se null for passado com parâmetro.
     */
    public static String timestampSQL(Date data) {
        if (data != null) {
            return stringSQL(TIMESTAMP_FORMAT.format(data));
        }
        return null;
    }
    
    /**
     * Formata um número inteiro para ser utilizado no comando SQLUtils. Caso o número
     * informado seja igual ao nullValue então "null" é retornado.
     * @param number Número a ser convertido.
     * @param nullValue Caso o number seja igual a esse valor "null" é retornado.
     * @return String com o numero formatado ou null.
     */
    public static String longIntSQL(long number, long nullValue) {
        return number != nullValue ? number + "" : null;
    }
    
    /**
     * Formata um número double para ser utilizado no comando SQLUtils. Caso o número
     * informado seja igual ao nullValue então "null" é retornado.
     * @param number Número a ser convertido.
     * @param nullValue Caso o number seja igual a esse valor "null" é retornado.
     * @return String com o numero formatado ou null.
     */
    public static String doubleSQL(double number, double nullValue) {
        return number != nullValue ? number + "" : null;
    }
        
}
