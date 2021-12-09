package vrimplantacao2.vo.cadastro.cliente;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class ClientePreferencialAnteriorVO {

    private String sistema;
    private String loja;
    private String id;
    private ClientePreferencialVO codigoAtual;
    private String cnpj;
    private String ie;
    private String nome;
    private boolean forcarGravacao = false;
    private int ponto;
    private int idConexao;

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public String getId() {
        return id;
    }

    public ClientePreferencialVO getCodigoAtual() {
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

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoAtual(ClientePreferencialVO codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setIe(String ie) {
        this.ie = ie;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome);
    }

    public void setForcarGravacao(boolean forcarGravacao) {
        this.forcarGravacao = forcarGravacao;
    }

    public int getPonto() {
        return ponto;
    }

    public void setPonto(int ponto) {
        this.ponto = ponto;
    }

    public int getIdConexao() {
        return this.idConexao;
    }
    
    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }
}
