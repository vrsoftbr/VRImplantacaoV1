package vrimplantacao2.vo.cadastro.financeiro.contareceber;

/**
 *
 * @author Leandro
 */
public class SituacaoReceberOutrasReceitas {
    
    public static final SituacaoReceberOutrasReceitas ABERTO = new SituacaoReceberOutrasReceitas(0, "ABERTO");
    public static final SituacaoReceberOutrasReceitas BAIXADO = new SituacaoReceberOutrasReceitas(1, "BAIXADO");
    
    private int id;
    private String descricao;

    public SituacaoReceberOutrasReceitas(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public SituacaoReceberOutrasReceitas() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
