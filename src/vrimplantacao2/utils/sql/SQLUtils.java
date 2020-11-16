package vrimplantacao2.utils.sql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;

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
        if (string == null) {
            string = "null";
        }
        string = string.replace("'", "''");
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
    
    /**
     * Este método auxilia na quebra de um intervalo de tempo maior em diversos
     * intervalos menores de um mês. Coloque as tags {DATA_INICIO} e {DATA_TERMINO}
     * onde deverá ser substituído por uma data do intervalo gerado.
     * @param sql Comando sql que será transformado em intervalos.
     * @param vendaDataInicio Data inicial do intervalo.
     * @param vendaDataTermino Data final do intervalor.
     * @param format Formatador das datas informadas.
     * @return Lista de comandos SQL com as datas substituidas pelos intervalos menores.
     */
    public static List<String> quebrarSqlEmMeses (String sql, Date vendaDataInicio, Date vendaDataTermino, SimpleDateFormat format) {
        
        DateTime inicio = new DateTime(vendaDataInicio.getTime());
        DateTime termino = new DateTime(vendaDataTermino.getTime());        
        
        List<String> result = new ArrayList<>();
        
        if (inicio.equals(termino) || inicio.getMonthOfYear() == termino.getMonthOfYear()) {
            String copy = sql.replace("{DATA_INICIO}", format.format(new Date(inicio.getMillis())));
            copy = copy.replace("{DATA_TERMINO}", format.format(new Date(termino.getMillis())));
            result.add(copy);
        } else {
            while (inicio.isBefore(termino)) {
                DateTime prox = inicio.plusMonths(1);
                if (prox.isAfter(termino)) {
                    prox = termino;
                }
                String copy = sql.replace("{DATA_INICIO}", format.format(new Date(inicio.getMillis())));
                copy = copy.replace("{DATA_TERMINO}", format.format(new Date(prox.getMillis())));
                result.add(copy);
                inicio = prox.plusDays(1);
            }
        }
        
        return result;
    }
    
}
