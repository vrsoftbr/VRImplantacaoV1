package vrimplantacao.vo.fiscal;

public enum ModeloNotaFiscal {

    SERVICO(""),
    NOTAFISCAL("01"),
    PRODUTOR("04"),
    ENERGIA("06"),
    TRANSPORTE("07"),
    CTRC("08"),
    COMUNICACAO("21"),
    TELECOMUNICACAO("22"),
    GASCANALIZADO("28"),
    AGUACANALIZADA("29"),
    NFE("55"),
    NFSE("56"),
    CTE("57"),
    CFE("59"),
    NFCE("65"),
    CUPOMFISCAL("2D"),
    GAS("GS"),
    NOTAFISCALAVULSA("1B");
    
    private String modelo;

    private ModeloNotaFiscal(String i_modelo) {
        this.modelo = i_modelo;
    }

    public String getModelo() {
        return this.modelo;
    }
    
}
