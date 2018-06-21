package vrimplantacao2.vo.cadastro.cliente.food;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Leandro
 */
public class ClienteFoodAnteriorVO {
 
    private String sistema;
    private String loja;
    private String id;
    private int codigoAtual;
    private String nome;
    private boolean forcarGravacao;

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCodigoAtual() {
        return codigoAtual;
    }

    public void setCodigoAtual(int codigoAtual) {
        this.codigoAtual = codigoAtual;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome);
    }

    public boolean isForcarGravacao() {
        return forcarGravacao;
    }

    public void setForcarGravacao(boolean forcarGravacao) {
        this.forcarGravacao = forcarGravacao;
    }
    
}
