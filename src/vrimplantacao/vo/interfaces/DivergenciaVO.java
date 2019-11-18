package vrimplantacao.vo.interfaces;

public class DivergenciaVO {
    
    public static final int OK = 0; 
    public static final int NOTA_JA_IMPORTADA = 1;
    public static final int ARQUIVO_INVALIDO = 2;
    public static final int EAN_NAO_ENCONTRADO = 3;
    public static final int CNPJ_DESTINATARIO_NAO_ENCONTRADO = 4; 
    public static final int ALIQUOTA_INVALIDA = 5;
    public static final int CNPJ_MOTORISTA_NAO_ENCONTRADO = 6;   

    public int id = -9;
    public int tipo = 0;
    public String descricao = "";

    public DivergenciaVO(int id, String i_descricao, int i_tipo) {
        this.id = id;
        this.descricao = i_descricao;
        this.tipo = i_tipo;
    }

    @Deprecated
    public DivergenciaVO(String i_descricao, int i_tipo) {
        this(-9, i_descricao, i_tipo);
    }
    
}
