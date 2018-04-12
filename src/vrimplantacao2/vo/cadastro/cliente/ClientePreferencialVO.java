package vrimplantacao2.vo.cadastro.cliente;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoSexo;

/**
 *
 * @author Leandro
 */
public class ClientePreferencialVO {
    private int id;
    private String nome = "SEM NOME";
    private SituacaoCadastro situacaocadastro = SituacaoCadastro.ATIVO;
    private String endereco = "SEM ENDERECO";
    private String bairro = "SEM BAIRRO";
    private int id_estado;
    private int id_municipio;
    private int cep;
    private String telefone = "0000000000";
    private String celular;
    private String email;// character varying(50) NOT NULL,
    private String inscricaoEstadual = "ISENTO";// character varying(18) NOT NULL,
    private String orgaoEmissor;// character varying(6) NOT NULL,
    private long cnpj = 0;// numeric(14,0) NOT NULL,
    private TipoEstadoCivil tipoEstadoCivil = TipoEstadoCivil.NAO_INFORMADO;// integer NOT NULL,
    private Date dataNascimento;// date,    
    private Date dataCadastro = new Date();// date NOT NULL,
    private TipoSexo sexo = TipoSexo.MASCULINO;// integer NOT NULL,
    private String observacao = "IMPORTACAO VR";// character varying(80) NOT NULL,
    private String empresa;// character varying(35) NOT NULL,
    private int id_estadoEmpresa;// integer,
    private int id_municipioEmpresa;// integer,
    private String enderecoEmpresa = "SEM ENDERECO";// character varying(30) NOT NULL,
    private String bairroEmpresa = "SEM BAIRRO";// character varying(30) NOT NULL,
    private int cepEmpresa;// numeric(8,0) NOT NULL,
    private String telefoneEmpresa;// character varying(14) NOT NULL,
    private Date dataAdmissao;// date,
    private String cargo;// character varying(25) NOT NULL,
    private double salario = 0;// numeric(11,2) NOT NULL,    
    private double valorLimite = 0;// numeric(11,2) NOT NULL,
    private String nomeConjuge;// character varying(35) NOT NULL,
    private TipoInscricao tipoInscricao = TipoInscricao.FISICA;
    private int vencimentoCreditoRotativo = 0;// integer NOT NULL,
    private String observacao2 = "";// character varying(2500) NOT NULL,
    private boolean permiteCreditoRotativo = true;// boolean NOT NULL,
    private boolean permiteCheque = true;// boolean NOT NULL,
    private String nomeMae;// character varying(40) NOT NULL,
    private String nomePai;// character varying(40) NOT NULL,
    private Date dataRestricao;// date,
    private boolean bloqueado;// boolean NOT NULL,
    private String numero = "0";// character varying(6) NOT NULL,
    private String numeroEmpresa = "0";// character varying(6) NOT NULL DEFAULT '0'::character varying,
    private String complemento;// character varying(30) NOT NULL DEFAULT ''::character varying,
    private String complementoEmpresa;// character varying(30) NOT NULL DEFAULT ''::character varying,
    private int senha;// integer NOT NULL DEFAULT 0,

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public SituacaoCadastro getSituacaocadastro() {
        return situacaocadastro;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public int getId_estado() {
        return id_estado;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public int getCep() {
        return cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCelular() {
        return celular;
    }

    public String getEmail() {
        return email;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public String getOrgaoEmissor() {
        return orgaoEmissor;
    }

    public long getCnpj() {
        return cnpj;
    }

    public TipoEstadoCivil getTipoEstadoCivil() {
        return tipoEstadoCivil;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public TipoSexo getSexo() {
        return sexo;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getEmpresa() {
        return empresa;
    }

    public int getId_estadoEmpresa() {
        return id_estadoEmpresa;
    }

    public int getId_municipioEmpresa() {
        return id_municipioEmpresa;
    }

    public String getEnderecoEmpresa() {
        return enderecoEmpresa;
    }

    public String getBairroEmpresa() {
        return bairroEmpresa;
    }

    public int getCepEmpresa() {
        return cepEmpresa;
    }

    public String getTelefoneEmpresa() {
        return telefoneEmpresa;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public String getCargo() {
        return cargo;
    }

    public double getSalario() {
        return salario;
    }

    public double getValorLimite() {
        return valorLimite;
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public int getVencimentoCreditoRotativo() {
        return vencimentoCreditoRotativo;
    }

    public String getObservacao2() {
        return observacao2;
    }

    public boolean isPermiteCreditoRotativo() {
        return permiteCreditoRotativo;
    }

    public boolean isPermiteCheque() {
        return permiteCheque;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public Date getDataRestricao() {
        return dataRestricao;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public String getNumero() {
        return numero;
    }

    public String getNumeroEmpresa() {
        return numeroEmpresa;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getComplementoEmpresa() {
        return complementoEmpresa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 40, "SEM NOME");
    }

    public void setSituacaocadastro(SituacaoCadastro situacaocadastro) {
        this.situacaocadastro = situacaocadastro;
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 50, "SEM ENDERECO");
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public void setCep(int cep) {
        this.cep = cep <= 99999999 ? cep : 0;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(telefone, 14, "0000000000");
    }

    public void setCelular(String celular) {
        this.celular = Utils.formataNumero(celular, 14);
    }

    public void setEmail(String email) {
        this.email = Utils.acertarTexto(email, 50).toLowerCase();
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = Utils.acertarTexto(inscricaoEstadual, 18, "ISENTO");
    }

    public void setOrgaoEmissor(String orgaoEmissor) {
        this.orgaoEmissor = Utils.acertarTexto(orgaoEmissor, 6);
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj <= 99999999999999L ? cnpj : this.id;
        if (this.cnpj < 99999999999L) {
            setTipoInscricao(TipoInscricao.FISICA);
        } else {
            setTipoInscricao(TipoInscricao.JURIDICA);
        }
    }

    public void setTipoEstadoCivil(TipoEstadoCivil tipoEstadoCivil) {
        this.tipoEstadoCivil = tipoEstadoCivil != null ? tipoEstadoCivil : TipoEstadoCivil.NAO_INFORMADO;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro != null ? dataCadastro : new Date();
    }

    public void setSexo(TipoSexo sexo) {
        this.sexo = sexo != null ? sexo : TipoSexo.MASCULINO;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 80, "IMPORTACAO VR");
    }

    public void setEmpresa(String empresa) {
        this.empresa = Utils.acertarTexto(empresa, 35);
    }

    public void setId_estadoEmpresa(int id_estadoEmpresa) {
        this.id_estadoEmpresa = id_estadoEmpresa;
    }

    public void setId_municipioEmpresa(int id_municipioEmpresa) {
        this.id_municipioEmpresa = id_municipioEmpresa;
    }

    public void setEnderecoEmpresa(String enderecoEmpresa) {
        this.enderecoEmpresa = Utils.acertarTexto(enderecoEmpresa, 30, "SEM ENDERECO");
    }

    public void setBairroEmpresa(String bairroEmpresa) {
        this.bairroEmpresa = Utils.acertarTexto(bairroEmpresa, 30, "SEM BAIRRO");
    }

    public void setCepEmpresa(int cepEmpresa) {
        this.cepEmpresa = cepEmpresa <= 99999999 ? cepEmpresa : 0;
    }

    public void setTelefoneEmpresa(String telefoneEmpresa) {
        this.telefoneEmpresa = Utils.acertarTexto(telefoneEmpresa, 14);
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public void setCargo(String cargo) {
        this.cargo = Utils.acertarTexto(cargo, 25);
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public void setValorLimite(double valorLimite) {
        this.valorLimite = valorLimite;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = Utils.acertarTexto(nomeConjuge, 35);
    }

    public void setTipoInscricao(TipoInscricao tipoInscricao) {
        this.tipoInscricao = tipoInscricao != null ? tipoInscricao : TipoInscricao.JURIDICA;
    }

    public void setVencimentoCreditoRotativo(int vencimentoCreditoRotativo) {
        this.vencimentoCreditoRotativo = vencimentoCreditoRotativo;
    }

    public void setObservacao2(String observacao2) {
        this.observacao2 = Utils.acertarTexto(observacao2, 2500);
    }

    public void setPermiteCreditoRotativo(boolean permiteCreditoRotativo) {
        this.permiteCreditoRotativo = permiteCreditoRotativo;
    }

    public void setPermiteCheque(boolean permiteCheque) {
        this.permiteCheque = permiteCheque;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = Utils.acertarTexto(nomeMae, 40);
    }

    public void setNomePai(String nomePai) {
        this.nomePai = Utils.acertarTexto(nomePai, 40);
    }

    public void setDataRestricao(Date dataRestricao) {
        this.dataRestricao = dataRestricao;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6, "0");
    }

    public void setNumeroEmpresa(String numeroEmpresa) {
        this.numeroEmpresa = Utils.acertarTexto(numeroEmpresa, 6, "0");
    }

    public void setComplemento(String complemento) {
        this.complemento = Utils.acertarTexto(complemento, 30);
    }

    public void setComplementoEmpresa(String complementoEmpresa) {
        this.complementoEmpresa = Utils.acertarTexto(complementoEmpresa, 30);
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }
    
}
