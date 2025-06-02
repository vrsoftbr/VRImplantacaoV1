package vrimplantacao2_5.classe;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author Desenvolvimento
 */
public class Global {

    public static String VERSAO = "2.6.02";
    public static final Date DATA_VERSAO = new GregorianCalendar(2021, 05, 14).getTime();
    private static int idUsuario;
    private static String nomeUsuario;
    private static int idUnidade;
    private static String nomeUnidade;

    /**
     * @return the idUsuario
     */
    public static int getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param aIdUsuario the idUsuario to set
     */
    public static void setIdUsuario(int aIdUsuario) {
        idUsuario = aIdUsuario;
    }

    /**
     * @return the nomeUsuario
     */
    public static String getNomeUsuario() {
        return nomeUsuario;
    }

    /**
     * @param aNomeUsuario the nomeUsuario to set
     */
    public static void setNomeUsuario(String aNomeUsuario) {
        nomeUsuario = aNomeUsuario;
    }

    /**
     * @return the idUnidade
     */
    public static int getIdUnidade() {
        return idUnidade;
    }

    /**
     * @param aIdUnidade the idUnidade to set
     */
    public static void setIdUnidade(int aIdUnidade) {
        idUnidade = aIdUnidade;
    }

    /**
     * @return the nomeUnidade
     */
    public static String getNomeUnidade() {
        return nomeUnidade;
    }

    /**
     * @param aNomeUnidade the nomeUnidade to set
     */
    public static void setNomeUnidade(String aNomeUnidade) {
        nomeUnidade = aNomeUnidade;
    }
    
    
}
