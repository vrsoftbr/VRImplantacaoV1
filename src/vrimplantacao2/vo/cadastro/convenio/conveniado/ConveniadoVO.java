package vrimplantacao2.vo.cadastro.convenio.conveniado;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoInscricao;

/**
 *
 * @author Leandro
 */
public class ConveniadoVO {
    private int id;// integer NOT NULL,
    private String nome = "SEM NOME";// character varying(40) NOT NULL,
    private int id_empresa;// integer NOT NULL,
    private boolean bloqueado = false;// boolean NOT NULL,
    private SituacaoCadastro situacaoCadastro = SituacaoCadastro.ATIVO;// id_situacaocadastro integer NOT NULL,
    //senha integer NOT NULL,
    private int id_loja;// integer NOT NULL,
    private long cnpj;// numeric(14,0) NOT NULL,
    private String observacao;// character varying(60) NOT NULL,
    //id_tipoinscricao integer NOT NULL,
    private int matricula = 50001;// integer NOT NULL,
    private Date dataValidadeCartao;// date NOT NULL,
    private Date dataDesbloqueio;// date,
    private boolean visualizaSaldo = true;// boolean NOT NULL,
    private Date dataBloqueio;// date
    private int senha = 0;
    private int identificacaoCartao = 0;
    
    public void setId(int id) {
        this.id = id; 
    }

    public int getIdentificacaoCartao() {
        return identificacaoCartao;
    }

    public void setIdentificacaoCartao(int identificacaoCartao) {
        this.identificacaoCartao = identificacaoCartao;
    }
    
    

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40, "SEM NOME");
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setSituacaoCadastro(SituacaoCadastro situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro != null ? situacaoCadastro : SituacaoCadastro.ATIVO;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public void setCnpj(long cnpj) {
        if (cnpj > 99999999999999L || cnpj < 0) {
            cnpj = id;
        }
        this.cnpj = cnpj;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarObservacao(observacao, 60);
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public void setDataValidadeCartao(Date dataValidadeCartao) {
        this.dataValidadeCartao = dataValidadeCartao == null ? new Date() : dataValidadeCartao;
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

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public SituacaoCadastro getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public int getId_loja() {
        return id_loja;
    }

    public long getCnpj() {
        return cnpj;
    }

    public String getObservacao() {
        return observacao;
    }

    public int getMatricula() {
        return matricula;
    }

    public Date getDataValidadeCartao() {
        return dataValidadeCartao;
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
    
    public TipoInscricao getTipoInscricao() {
        return getCnpj() > 99999999999L ? TipoInscricao.JURIDICA : TipoInscricao.FISICA;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

}
