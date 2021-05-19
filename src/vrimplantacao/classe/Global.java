package vrimplantacao.classe;

import java.time.LocalDate;
import java.time.Month;

public class Global {

    public static String VERSAO = "1.1.14";
    /**
     * Data da liberação desta versão.
     */
    public static final LocalDate DATA_VERSAO = LocalDate.of(2021, Month.MAY, 19);
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
