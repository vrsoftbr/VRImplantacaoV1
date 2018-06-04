package vrimplantacao2.parametro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import vrframework.classe.Conexao;

/**
 * Classe que controla e executa operações sobre a versão do VRMaster.
 * @author Leandro
 */
public final class Versao {
    
    private Versao(){}
    
    private static Integer[] versaoArray;

    /**
     * Puxa a versão do banco de dados e prepara a classe.
     * @throws Exception 
     */
    public static void carregar() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select versao from versao where id_programa = 0"
            )) {
                if (rst.next()) {
                    String[] array = rst.getString("versao").split("\\.");
                    versaoArray = new Integer[array.length];
                    for (int i = 0; i < array.length; i++) {
                        versaoArray[i] = Integer.parseInt(array[i]);
                    }                    
                }
            }
        }
    }
    
    /**
     * Retorna uma String representando a versão.
     * @return 
     */
    public static String getVersao() {
        StringBuilder builder = new StringBuilder();
        
        for (Iterator<Integer> iterator = Arrays.asList(versaoArray).iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(".");
            }
        }
        
        return builder.toString();
    }
    
    /**
     * Compara e retorna se a versão informada é menor que a atual.
     * @param versao Versão a ser comparada.
     * @return true se for menor.
     */
    public static boolean menorQue(int... versao) {
        for (int i = 0; i < versao.length; i++) {
            if (versao[i] > versaoArray[i]) {
                return true;
            } else if (versao[i] < versaoArray[i]) {
                return false;
            }
        }
        return false;
    }
    
    /**
     * Compara e retorna se a versão informada é maior que a atual.
     * @param versao Versão a ser comparada.
     * @return true se for menor.
     */
    public static boolean maiorQue(int... versao) {
        for (int i = 0; i < versao.length; i++) {
            if (versao[i] < versaoArray[i]) {
                return true;
            } else if (versao[i] > versaoArray[i]) {
                return false;
            }
        }
        return false;
    }
    
    /**
     * Compara e retorna se a versão informada é igual ao atual.
     * @param versao Versão a ser comparada.
     * @return true se for menor.
     */
    public static boolean igual(int... versao) {
        for (int i = 0; i < versao.length; i++) {
            if (versao[i] < versaoArray[i]) {
                return false;
            } else if (versao[i] > versaoArray[i]) {
                return true;
            }
        }
        return false;
    }
    
}
