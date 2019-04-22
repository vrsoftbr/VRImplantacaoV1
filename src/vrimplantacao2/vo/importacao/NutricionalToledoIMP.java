package vrimplantacao2.vo.importacao;

public class NutricionalToledoIMP {
    private int codigo = 0;
    private String descricao = "";
    private String pesavel = "";
    private int validade = 0;
    private int nutricional = 0;
    

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPesavel() {
        return pesavel;
    }

    public void setPesavel(String pesavel) {
        this.pesavel = pesavel;
    }

    public int getValidade() {
        return validade;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public int getNutricional() {
        return nutricional;
    }

    public void setNutricional(int nutricional) {
        this.nutricional = nutricional;
    }
}