package vrimplantacao2.vo.cadastro;

/**
 * Classe que representa um registro da tabela implantacao.produtobalanca.
 * @author Leandro
 */
public class ProdutoBalancaVO {
    
    private int codigo;
    private String descricao;
    private String pesavel;
    private int validade;

    public ProdutoBalancaVO() {
        this.validade = 0;
        this.pesavel = "P";
        this.descricao = "SEM DESCRICAO";
        this.codigo = 0;
    }

    public ProdutoBalancaVO(int codigo, String descricao, String pesavel, int validade) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.pesavel = pesavel;
        this.validade = validade;
    }

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
