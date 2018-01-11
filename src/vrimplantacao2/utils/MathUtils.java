package vrimplantacao2.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe que possui diversas funções matemática para auxiliar em operações 
 * matemáticas.
 * @author Leandro
 */
public final class MathUtils {
    
    private MathUtils() {}
    
    /**
     * Trunca o número double informado. Este método utiliza uma abordagem 
     * diferente para truncar o número, utilizando Strings e vetor para isso.
     * @param valor Valor double a ser truncado.
     * @param qtdDecimal Quantidade de casas decimais a serem retornadas.
     * @param max Valor máximo, acima dele será retornado 0
     * @return 
     */
    public static double trunc(double valor, int qtdDecimal, double max) {
        if (max < 0) {
            max = -max;
        }
        
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            valor = 0;
        }
        
        BigDecimal valorExato = new BigDecimal(String.valueOf(valor)).setScale(qtdDecimal, RoundingMode.DOWN);
        double doubleValue = valorExato.doubleValue();
        
        if (!(doubleValue <= max && doubleValue >= -max)) {
            return 0;
        }
        
        return doubleValue;
    }
    
    /**
     * Trunca o número double informado. Este método utiliza uma abordagem 
     * diferente para truncar o número, utilizando Strings e vetor para isso.
     * @param valor Valor double a ser truncado.
     * @param qtdDecimal Quantidade de casas decimais a serem retornadas.
     * @return 
     */
    public static double trunc(double valor, int qtdDecimal) {
        return trunc(valor, qtdDecimal, 9999999999D);
    }
    
    /**
     * Arredonda o número double informado. Este método utiliza uma abordagem 
     * diferente para truncar o número, utilizando Strings e vetor para isso.
     * @param valor Valor double a ser truncado.
     * @param qtdDecimal Quantidade de casas decimais a serem retornadas.
     * @param max Valor máximo, acima dele será retornado 0.
     * @return 
     */
    public static double round(double valor, int qtdDecimal, double max) {
        if (max < 0) {
            max = -max;
        }
        
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            valor = 0;
        }

        BigDecimal valorExato = new BigDecimal(String.valueOf(valor)).setScale(qtdDecimal, RoundingMode.HALF_UP);
        double doubleValue = valorExato.doubleValue();
        
        if (!(doubleValue <= max && doubleValue >= -max)) {
            return 0;
        }
        
        return valorExato.doubleValue();
    }
    
    /**
     * Arredonda o número double informado. Este método utiliza uma abordagem 
     * diferente para truncar o número, utilizando Strings e vetor para isso.
     * @param valor Valor double a ser truncado.
     * @param qtdDecimal Quantidade de casas decimais a serem retornadas.
     * @return 
     */
    public static double round(double valor, int qtdDecimal) {
        return round(valor, qtdDecimal, 9999999999D);
    }
    
    /**
     * Obtem o digito verificador de um valor.
     * @param valor
     * @return 
     */
    public static int getDV(long valor) {
        int mult, soma, i, n, dig;
        
        boolean x10 = true;
        int numDig = 1, limMult = 9;
        String dado = String.valueOf(valor);
    
        if(!x10) numDig = 1;
        for(n=1; n<=numDig; n++){
            soma = 0;
            mult = 2;
            for(i=dado.length() - 1; i >= 0; i--){
                soma += (mult * Integer.parseInt(dado.substring(i, i + 1)));
                if(++mult > limMult) mult = 2;
            }
            if(x10){
                dig = ((soma * 10) % 11) % 10;
            } else {
                dig = soma % 11;
            }
            if(dig == 10){
                    dado += "X";
            } else {
                    dado += String.valueOf(dig);
            }
        }
        return Integer.parseInt(dado.substring(dado.length() - numDig, dado.length()));
    }
    
}
