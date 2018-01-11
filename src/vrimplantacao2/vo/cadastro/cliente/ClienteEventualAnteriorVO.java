package vrimplantacao2.vo.cadastro.cliente;

/**
 *
 * @author Leandro
 */
public class ClienteEventualAnteriorVO {
    private String sistema;
    private String loja;
    private String id;
    private ClienteEventualVO codigoAtual;
    private String cnpj;
    private String ie;
    private String nome;
    private boolean forcarGravacao = false;

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(ClienteEventualVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setForcarGravacao(boolean forcarGravacao) {
        this.forcarGravacao = forcarGravacao;
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

    public ClienteEventualVO getCodigoAtual() {
        return codigoAtual;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getIe() {
        return ie;
    }

    public String getNome() {
        return nome;
    }

    public boolean isForcarGravacao() {
        return forcarGravacao;
    }
    
}
