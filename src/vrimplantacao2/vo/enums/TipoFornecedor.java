package vrimplantacao2.vo.enums;

/**
 * Tipo de Fornecedor.
 * @author Leandro
 */
public enum TipoFornecedor {
    
    INDUSTRIA (0,"INDUSTRIA"),
    ATACADO (1,"ATACADO"),
    DISTRIBUIDOR (2,"DISTRIBUIDOR"),
    PRESTADOR (3,"PRESTADOR SERVICO"),
    VAREJO (4,"VAREJO"),
    PRODUTORRURAL (5,"PRODUTOR RURAL"),
    FILIAL (6,"FILIAL"),
    USOCONSUMO (7,"USO CONSUMO"),
    REPOSITORCORREDOR (8,"REPOSITOR CORREDOR"),
    SEMTIPO (9,"SEM TIPO FORNECEDOR");
    
    
    public static TipoFornecedor getById(int id) {
        for (TipoFornecedor tf: values()) {
            if (id == tf.id) {
                return tf;
            }
        }
        return ATACADO;
    }
    
    private int id;
    private String descricao;

    private TipoFornecedor(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
    
}
