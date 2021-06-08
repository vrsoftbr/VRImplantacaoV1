package vrimplantacao.classe;

import java.util.Date;
import java.util.GregorianCalendar;

public class Global {

    public static String VERSAO = "1.1.15";
    /**
     * Data da liberação desta versão.
     */
    public static final Date DATA_VERSAO = new GregorianCalendar(2021, 05, 8).getTime();
    public static int idLoja = 0;
    public static String loja = "";
    public static int idEstado = 0;
    public static int idMunicipio = 0;    
    public static int Cep = 0;        
    public static int idUsuario = 0;
    public static String usuario = "";
    public static String fornecedor = "";
    public static int idFornecedor = 0;
    public static int pisCofinsDebito = 1;
    public static int pisCofinsCredito = 13;    
    public static int tipoNaturezaReceita = 999;
    public static int idLojaFornecedor = 1;
    public static String cidadeDescricao = "SAO PAULO";
    public static String ufEstado = "SP";
    
    public static int mercadologicoPadrao1 = 0;        
    public static int mercadologicoPadrao2 = 0;        
    public static int mercadologicoPadrao3 = 0;
    public static boolean fazerUpdate = false;
    public static boolean compararCnpj = false;
    public static boolean compararCnpjProdUnificado = false;
}
