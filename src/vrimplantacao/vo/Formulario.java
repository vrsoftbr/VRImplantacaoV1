package vrimplantacao.vo;

public enum Formulario {

    IMPORTACAO_SISTEMA_CGA(1, true),    
    IMPORTACAO_SISTEMA_SHI(2, true),
    IMPORTACAO_SISTEMA_SYSPDV_FIREBIRD(3, true),
    IMPORTACAO_SISTEMA_IDEAL(4, true),
    IMPORTACAO_SISTEMA_MOBILITY(5, true),
    IMPORTACAO_SISTEMA_GDOOR(6, true),
    IMPORTACAO_SISTEMA_WISASOFT(7, true),
    IMPORTACAO_SISTEMA_SOFTAEX(8, true),
    
    IMPORTACAO_SISTEMA_JMASTER(9, true),
    IMPORTACAO_SISTEMA_MILENIO(10, true),
    IMPORTACAO_SISTEMA_GETWAY(11, true),
    IMPORTACAO_SISTEMA_SYSPDV_SQLSERVER(12, true),
    IMPORTACAO_SISTEMA_GUIASISTEMAS(13, true),
    IMPORTACAO_SISTEMA_BOECHATSOFT(14, true),
    
    IMPORTACAO_SISTEMA_RMS(15, true),
    IMPORTACAO_SISTEMA_GCF(16, true),
    IMPORTACAO_SISTEMA_PCSISTEMAS(17, true),    
    
    IMPORTACAO_SISTEMA_VRSOFTWARE(18, true),        
    
    IMPORTACAO_SISTEMA_FMSISTEMAS(19, true),        
    IMPORTACAO_SISTEMA_EVEREST(20, true),            
    
    IMPORTACAO_SISTEMA_ORION(21, true),   
    IMPORTACAO_ULTRASYST(22, true),
    IMPORTACAO_CONCRETIZE(23, true),
    IMPORTACAO_KAIROS(24, true),
    IMPORTACAO_DIRECTOR(25, true),
    IMPORTACAO_ECOS_INFORMATICA(26, true),  
    IMPORTACAO_CISS(27, true),  
    IMPORTACAO_CONTROLWARE(28, true),  
    IMPORTACAO_SYSMOURA(29, true),  
    IMPORTACAO_TOPSYSTEM(30, true),  
    IMPORTACAO_SCI(31, true),  
    IMPORTACAO_SBOMARKET(32, true),  
    IMPORTACAO_SICS(33, true),  
    IMPORTACAO_SIMSOFT(34, true),  
    IMPORTACAO_SIMS(35, true),  
    IMPORTACAO_GR7(37, true),  
    IMPORTACAO_SUPERUS(38, true),  
    IMPORTACAO_SOFGCE(39, true),  
    IMPORTACAO_INFOBRASIL(40, true),  
    
    CADASTRO_USUARIO(36, true),
    CADASTRO_PRODUTO_FORNECEDOR(41, false),
    FINANCEIRO_CONCILIACAO_BANCARIA(65, false),
    NOTAFISCAL_ENTRADA(66, true),
    FINANCEIRO_RECEBIMENTO_VENDA_PRAZO(81, true),
    FINANCEIRO_RECEBIMENTO_DEVOLUCAO(104, true),
    FISCAL_ESCRITURACAO_NOTA_FISCAL(167, false),
    NOTAFISCAL_SAIDA(211, true),    
    INTERFACE_IMPORTACAO_LOGVENDA(406, false),
    INTERFACE_IMPORTACAO_NFCE(503, false);
    
    private int id = 0;
    private boolean atalho = false;

    private Formulario(int i_id, boolean i_atalho) {
        this.id = i_id;
        this.atalho = i_atalho;
    }

    public int getId() {
        return id;
    }

    public boolean isAtalho() {
        return atalho;
    }
}
