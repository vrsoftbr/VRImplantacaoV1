package vrimplantacao2.vo.cadastro.convenio.conveniado;

/**
 *
 * @author Leandro
 */
public class ConveniadoAnteriorVO {
    
    private String sistema;
    private String loja;
    private String id;
    private int codigoAtual = -1;
    private String cnpj;
    private String razao;
    private String lojaCadastro;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }

    public void setLojaCadastro(String lojaCadastro) {
        this.lojaCadastro = lojaCadastro;
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getId() {
        return id;
    }

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getRazao() {
        return razao;
    }

    public String getLojaCadastro() {
        return lojaCadastro;
    }
    
}
