package vrimplantacao2.parametro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import vrframework.classe.Conexao;
import vrimplantacao.utils.Utils;

/**
 * Classe que controla e executa operações sobre a versão do VRMaster.
 * @author Leandro
 */
public final class Versao {
    
    private Versao(){}
    
    private static Integer[] versaoArray;

    public static void carregar(Integer... array) {
        versaoArray = array;
    }
    
    /**
     * Puxa a versão do banco de dados e prepara a classe.
     * @throws Exception 
     */
    public static void carregar() throws Exception {
        String versao = "";
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select versao from versao where id_programa = 86"
            )) {
                if (rst.next()) {
                    
                    if (rst.getString("versao").contains("-")) {
                        versao = rst.getString("versao").replace("-", ".");
                    }  else {
                        versao = rst.getString("versao");
                    }
                    
                    //String[] array = rst.getString("versao").split("\\.");
                    String[] array = versao.split("\\.");
                    versaoArray = new Integer[array.length];
                    for (int i = 0; i < array.length; i++) {
                        versaoArray[i] = Utils.stringToInt(array[i], 0);
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
     * @deprecated Utilizar {@link #igualOuMenorQue(int...)}
     */
    @Deprecated
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
     * @deprecated Utilizar {@link #igualOuMaiorQue(int...)}
     */
    @Deprecated
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
        int menorQtdDePosicoes = obterMenorQtdDePosicoes(versao);
        for (int i = 0; i < menorQtdDePosicoes; i++) {
            if (versao[i] != versaoArray[i])
                return false;
        }
        return true;
    }

    public static boolean igualOuMenorQue(int... versao) {
        int menorQtdDePosicoes = obterMenorQtdDePosicoes(versao);
        for (int i = 0; i < menorQtdDePosicoes; i++) {
            final Integer versaoAplicacao = versaoArray[i]; 
            final int versaoComparativa = versao[i]; 
            if (versaoAplicacao > versaoComparativa) 
                return false;
            if (versaoAplicacao < versaoComparativa) 
                return true;
        }
        return true;
    }
    
    public static boolean igualOuMaiorQue(int... versao) {
        int menorQtdDePosicoes = obterMenorQtdDePosicoes(versao);
        for (int i = 0; i < menorQtdDePosicoes; i++) {
            final Integer versaoAplicacao = versaoArray[i]; 
            final int versaoComparativa = versao[i]; 
            if (versaoAplicacao < versaoComparativa) 
                return false;
            if (versaoAplicacao > versaoComparativa) 
                return true;
        }
        return true;
    }

    private static int obterMenorQtdDePosicoes(int[] versao) {
        return versao.length < versaoArray.length ? versao.length : versaoArray.length;
    }
}
