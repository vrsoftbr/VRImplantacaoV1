package vrimplantacao2.vo.importacao;

import java.util.Date;
import vrimplantacao2.vo.enums.SituacaoCadastro;

/**
 *
 * @author Leandro
 */
public class ConveniadoIMP {
    
    private String id;
    private String nome;
    private String idEmpresa;
    private boolean bloqueado = false;
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;
    private String senha;
    private String cnpj; // cpf do cliente
    private String observacao;        
    private Date validadeCartao;
    private Date dataDesbloqueio;
    private boolean visualizaSaldo = true;
    private Date dataBloqueio;
    private double convenioLimite = 0;
    private double convenioDesconto = 0;
    private int lojaCadastro = -1;
    private int identificacaoCartao = 0;

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setValidadeCartao(Date validadeCartao) {
        this.validadeCartao = validadeCartao;
    }

    public void setDataDesbloqueio(Date dataDesbloqueio) {
        this.dataDesbloqueio = dataDesbloqueio;
    }

    public void setVisualizaSaldo(boolean visualizaSaldo) {
        this.visualizaSaldo = visualizaSaldo;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
    }

    public void setConvenioLimite(double convenioLimite) {
        this.convenioLimite = convenioLimite;
    }

    public void setConvenioDesconto(double convenioDesconto) {
        this.convenioDesconto = convenioDesconto;
    }

    public void setLojaCadastro(int lojaCadastro) {
        this.lojaCadastro = lojaCadastro;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public String getSenha() {
        return senha;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getObservacao() {
        return observacao;
    }

    public Date getValidadeCartao() {
        return validadeCartao;
    }

    public Date getDataDesbloqueio() {
        return dataDesbloqueio;
    }

    public boolean isVisualizaSaldo() {
        return visualizaSaldo;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public double getConvenioLimite() {
        return convenioLimite;
    }

    public double getConvenioDesconto() {
        return convenioDesconto;
    }
    public int getLojaCadastro() {
        return lojaCadastro;
    }    

    public int getIdentificacaoCartao() {
        return identificacaoCartao;
    }

    public void setIdentificacaoCartao(int identificacaoCartao) {
        this.identificacaoCartao = identificacaoCartao;
    }
    
    
}
