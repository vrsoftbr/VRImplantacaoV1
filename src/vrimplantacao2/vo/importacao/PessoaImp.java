package vrimplantacao2.vo.importacao;

import java.util.Date;

/**
 *
 * @author Michael-Oliveira
 */
public class PessoaImp {

    private String CnpjLojaMercado = "";
    private Integer impid = 0;
    private Long cnpj = 0L;
    private String inscricaoestadual = "";
    private String orgaoemissor = "";
    private String razao = "";
    private String fantasia = "";
    private String ativo = "";
    private String bloqueado = "";
    private Date databloqueio = new Date();
    private String endereco = "";
    private String numero = "";
    private String complemento = "";
    private String bairro = "";
    private String municipioibge = "";
    private String municipio = "";
    private String ufibge = "";
    private String uf = "";
    private String cep = "";
    private String estadocivil = "";
    private String datanascimento = "";
    private String datacadastro = "";
    private String sexo = "";
    private String empresa = "";
    private String empresaendereco = "";
    private String empresanumero = "";
    private String empresacomplemento = "";
    private String empresabairro = "";
    private Integer empresamunicipioibge;
    private String empresamunicipio = "";
    private Integer empresaufibge;
    private String empresauf = "";
    private String empresacep = "";
    private String empresatelefone = "";
    private String dataadmissao = "";
    private String cargo = "";
    private String salario = "";
    private String valorlimite = "";
    private String nomeconjuge = "";
    private String nomepai = "";
    private String nomemae = "";
    private String observacao = "";
    private Integer diavencimento;
    private String permitecreditorotativo = "";
    private String permitecheque = "";
    private String telefone = "";
    private String celular = "";
    private String email = "";
    private String fax = "";
    private String cobrancatelefone = "";
    private Integer prazopagamento;
    private String cobrancaendereco = "";
    private String cobrancanumero = "";
    private String cobrancacomplemento = "";
    private String cobrancabairro = "";
    private Integer cobrancamunicipioibge;
    private String cobrancamunicipio = "";
    private Integer cobrancaufibge;
    private String cobrancauf = "";
    private String cobrancacep = "";
    private String tipoorgaopublico = "";
    private Integer limitecompra;
    private String inscricaomunicipal = "";
    private String tipoindicadorie = "";
    private Boolean fornecedor = false;
    private Boolean conveniado = false;

    public String getCnpjLojaMercado() {
        return CnpjLojaMercado;
    }

    public void setCnpjLojaMercado(String CnpjLojaMercado) {
        this.CnpjLojaMercado = CnpjLojaMercado;
    }

    public Integer getImpid() {
        return impid;
    }

    public void setImpid(Integer impid) {
        this.impid = impid;
    }

    public Long getCnpj() {
        return cnpj;
    }

    public void setCnpj(Long cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoestadual() {
        return inscricaoestadual;
    }

    public void setInscricaoestadual(String inscricaoestadual) {
        this.inscricaoestadual = inscricaoestadual;
    }

    public String getOrgaoemissor() {
        return orgaoemissor;
    }

    public void setOrgaoemissor(String orgaoemissor) {
        this.orgaoemissor = orgaoemissor;
    }

    public String getRazao() {
        return razao;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(String bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Date getDatabloqueio() {
        return databloqueio;
    }

    public void setDatabloqueio(Date databloqueio) {
        this.databloqueio = databloqueio;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipioibge() {
        return municipioibge;
    }

    public void setMunicipioibge(String municipioibge) {
        this.municipioibge = municipioibge;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUfibge() {
        return ufibge;
    }

    public void setUfibge(String ufibge) {
        this.ufibge = ufibge;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEstadocivil() {
        return estadocivil;
    }

    public void setEstadocivil(String estadocivil) {
        this.estadocivil = estadocivil;
    }

    public String getDatanascimento() {
        return datanascimento;
    }

    public void setDatanascimento(String datanascimento) {
        this.datanascimento = datanascimento;
    }

    public String getDatacadastro() {
        return datacadastro;
    }

    public void setDatacadastro(String datacadastro) {
        this.datacadastro = datacadastro;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEmpresaendereco() {
        return empresaendereco;
    }

    public void setEmpresaendereco(String empresaendereco) {
        this.empresaendereco = empresaendereco;
    }

    public String getEmpresanumero() {
        return empresanumero;
    }

    public void setEmpresanumero(String empresanumero) {
        this.empresanumero = empresanumero;
    }

    public String getEmpresacomplemento() {
        return empresacomplemento;
    }

    public void setEmpresacomplemento(String empresacomplemento) {
        this.empresacomplemento = empresacomplemento;
    }

    public String getEmpresabairro() {
        return empresabairro;
    }

    public void setEmpresabairro(String empresabairro) {
        this.empresabairro = empresabairro;
    }

    public Integer getEmpresamunicipioibge() {
        return empresamunicipioibge;
    }

    public void setEmpresamunicipioibge(Integer empresamunicipioibge) {
        this.empresamunicipioibge = empresamunicipioibge;
    }

    public String getEmpresamunicipio() {
        return empresamunicipio;
    }

    public void setEmpresamunicipio(String empresamunicipio) {
        this.empresamunicipio = empresamunicipio;
    }

    public Integer getEmpresaufibge() {
        return empresaufibge;
    }

    public void setEmpresaufibge(Integer empresaufibge) {
        this.empresaufibge = empresaufibge;
    }

    public String getEmpresauf() {
        return empresauf;
    }

    public void setEmpresauf(String empresauf) {
        this.empresauf = empresauf;
    }

    public String getEmpresacep() {
        return empresacep;
    }

    public void setEmpresacep(String empresacep) {
        this.empresacep = empresacep;
    }

    public String getEmpresatelefone() {
        return empresatelefone;
    }

    public void setEmpresatelefone(String empresatelefone) {
        this.empresatelefone = empresatelefone;
    }

    public String getDataadmissao() {
        return dataadmissao;
    }

    public void setDataadmissao(String dataadmissao) {
        this.dataadmissao = dataadmissao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getValorlimite() {
        return valorlimite;
    }

    public void setValorlimite(String valorlimite) {
        this.valorlimite = valorlimite;
    }

    public String getNomeconjuge() {
        return nomeconjuge;
    }

    public void setNomeconjuge(String nomeconjuge) {
        this.nomeconjuge = nomeconjuge;
    }

    public String getNomepai() {
        return nomepai;
    }

    public void setNomepai(String nomepai) {
        this.nomepai = nomepai;
    }

    public String getNomemae() {
        return nomemae;
    }

    public void setNomemae(String nomemae) {
        this.nomemae = nomemae;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Integer getDiavencimento() {
        return diavencimento;
    }

    public void setDiavencimento(Integer diavencimento) {
        this.diavencimento = diavencimento;
    }

    public String getPermitecreditorotativo() {
        return permitecreditorotativo;
    }

    public void setPermitecreditorotativo(String permitecreditorotativo) {
        this.permitecreditorotativo = permitecreditorotativo;
    }

    public String getPermitecheque() {
        return permitecheque;
    }

    public void setPermitecheque(String permitecheque) {
        this.permitecheque = permitecheque;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCobrancatelefone() {
        return cobrancatelefone;
    }

    public void setCobrancatelefone(String cobrancatelefone) {
        this.cobrancatelefone = cobrancatelefone;
    }

    public Integer getPrazopagamento() {
        return prazopagamento;
    }

    public void setPrazopagamento(Integer prazopagamento) {
        this.prazopagamento = prazopagamento;
    }

    public String getCobrancaendereco() {
        return cobrancaendereco;
    }

    public void setCobrancaendereco(String cobrancaendereco) {
        this.cobrancaendereco = cobrancaendereco;
    }

    public String getCobrancanumero() {
        return cobrancanumero;
    }

    public void setCobrancanumero(String cobrancanumero) {
        this.cobrancanumero = cobrancanumero;
    }

    public String getCobrancacomplemento() {
        return cobrancacomplemento;
    }

    public void setCobrancacomplemento(String cobrancacomplemento) {
        this.cobrancacomplemento = cobrancacomplemento;
    }

    public String getCobrancabairro() {
        return cobrancabairro;
    }

    public void setCobrancabairro(String cobrancabairro) {
        this.cobrancabairro = cobrancabairro;
    }

    public Integer getCobrancamunicipioibge() {
        return cobrancamunicipioibge;
    }

    public void setCobrancamunicipioibge(Integer cobrancamunicipioibge) {
        this.cobrancamunicipioibge = cobrancamunicipioibge;
    }

    public String getCobrancamunicipio() {
        return cobrancamunicipio;
    }

    public void setCobrancamunicipio(String cobrancamunicipio) {
        this.cobrancamunicipio = cobrancamunicipio;
    }

    public Integer getCobrancaufibge() {
        return cobrancaufibge;
    }

    public void setCobrancaufibge(Integer cobrancaufibge) {
        this.cobrancaufibge = cobrancaufibge;
    }

    public String getCobrancauf() {
        return cobrancauf;
    }

    public void setCobrancauf(String cobrancauf) {
        this.cobrancauf = cobrancauf;
    }

    public String getCobrancacep() {
        return cobrancacep;
    }

    public void setCobrancacep(String cobrancacep) {
        this.cobrancacep = cobrancacep;
    }

    public String getTipoorgaopublico() {
        return tipoorgaopublico;
    }

    public void setTipoorgaopublico(String tipoorgaopublico) {
        this.tipoorgaopublico = tipoorgaopublico;
    }

    public Integer getLimitecompra() {
        return limitecompra;
    }

    public void setLimitecompra(Integer limitecompra) {
        this.limitecompra = limitecompra;
    }

    public String getInscricaomunicipal() {
        return inscricaomunicipal;
    }

    public void setInscricaomunicipal(String inscricaomunicipal) {
        this.inscricaomunicipal = inscricaomunicipal;
    }

    public String getTipoindicadorie() {
        return tipoindicadorie;
    }

    public void setTipoindicadorie(String tipoindicadorie) {
        this.tipoindicadorie = tipoindicadorie;
    }

    public Boolean getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Boolean fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Boolean getConveniado() {
        return conveniado;
    }

    public void setConveniado(Boolean conveniado) {
        this.conveniado = conveniado;
    }
    
    
}
