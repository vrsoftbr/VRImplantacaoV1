package vrimplantacao2.vo.cadastro.convenio.transacao;

/**
 *
 * @author Leandro
 */
public enum SituacaoTransacaoConveniado {
    OK (0, "O"),
    PENDENTE (1, "P"),
    CANCELADO (2, "C"),
    DESFEITO (3, "D");
    
    private int id;
    private String sigla;
    
    private SituacaoTransacaoConveniado(int id, String sigla) {
        this.id = id;
        this.sigla = sigla;
    }

    public int getId() {
        return id;
    }

    public String getSigla() {
        return sigla;
    }
    
    public static SituacaoTransacaoConveniado getByNome(String nome) {
        if (nome == null) return null;
        for (SituacaoTransacaoConveniado conv: values()) {
            if (nome.startsWith(conv.getSigla())) {
                return conv;
            }
        }
        return null;
    }
    
    public static SituacaoTransacaoConveniado getById(int id) {
        for (SituacaoTransacaoConveniado conv: values()) {
            if (conv.getId() == id) {
                return conv;
            }
        }
        return null;
    }
}
