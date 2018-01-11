package vrimplantacao2.vo.cadastro;

/**
 * Classe que representa um registro da tabela implantacao.produtobalanca.
 * @author Leandro
 */
public class ProdutoBalancaVO {
    
    private int codigo = 0;
    private String descricao = "SEM DESCRICAO";
    private String pesavel = "P";
    private int validade = 0;

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPesavel() {
        return pesavel;
    }

    public int getValidade() {
        return validade;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPesavel(String pesavel) {
        this.pesavel = pesavel;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }
    
}
