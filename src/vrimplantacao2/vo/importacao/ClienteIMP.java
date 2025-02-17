package vrimplantacao2.vo.importacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoOrgaoPublico;
import vrimplantacao2.vo.enums.TipoSexo;

/**
 * Classe utilizada para importar clientes
 *
 * @author Leandro
 */
public class ClienteIMP {

    /**
     * @return the dataNascimentoConjuge
     */
    public Date getDataNascimentoConjuge() {
        return dataNascimentoConjuge;
    }

    /**
     * @param dataNascimentoConjuge the dataNascimentoConjuge to set
     */
    public void setDataNascimentoConjuge(Date dataNascimentoConjuge) {
        this.dataNascimentoConjuge = dataNascimentoConjuge;
    }
    private String id;
    private String cnpj;
    private TipoInscricao tipoInscricao = TipoInscricao.VAZIO;
    private String inscricaoestadual;
    private String orgaoemissor;
    private String razao;
    private String fantasia;
    private boolean ativo = true;
    private boolean bloqueado = false;
    private Date dataBloqueio;
    private String endereco;
    private String numero;
    private String complemento;
    private String bairro;
    private int municipioIBGE = 0;
    private String municipio;
    private int ufIBGE;
    private String uf;
    private String cep;
    private TipoEstadoCivil estadoCivil = TipoEstadoCivil.NAO_INFORMADO;
    private Date dataNascimento;
    private Date dataCadastro;
    private TipoSexo sexo = TipoSexo.MASCULINO;
    private String empresa;
    private String empresaEndereco;
    private String empresaNumero;
    private String empresaComplemento;
    private String empresaBairro;
    private int empresaMunicipioIBGE;
    private String empresaMunicipio;
    private int empresaUfIBGE = 0;
    private String empresaUf;
    private String empresaCep;
    private String empresaTelefone;
    private Date dataAdmissao;
    private String cargo;
    private double salario = 0;
    private double valorLimite = 0;
    private String nomeConjuge;
    private String cpfConjuge;
    private Date dataNascimentoConjuge;
    private String nomePai;
    private String nomeMae;
    private String observacao;
    private String observacao2;
    private int diaVencimento;
    private boolean permiteCreditoRotativo;
    private boolean permiteCheque;
    private boolean permiteChequeAVista = false;
    private int senha;
    private double ponto;

    private String telefone;
    private String celular;
    private String email;
    //Cliente Eventual
    private String fax;
    private String cobrancaTelefone;
    private int prazoPagamento;
    private String cobrancaEndereco;
    private String cobrancaNumero;
    private String cobrancaComplemento;
    private String cobrancaBairro;
    private int cobrancaMunicipioIBGE = 0;
    private String cobrancaMunicipio;
    private int cobrancaUfIBGE;
    private String cobrancaUf;
    private String cobrancaCep;
    private TipoOrgaoPublico tipoOrgaoPublico = TipoOrgaoPublico.NENHUM;
    private double limiteCompra = 0;
    private String inscricaoMunicipal;
    private TipoIndicadorIE tipoIndicadorIe = TipoIndicadorIE.NAO_CONTRIBUINTE;
    private int grupo;

    private List<ClienteContatoIMP> contatos = new ArrayList<>();
    private List<ClienteDependenteIMP> dependentes = new ArrayList<>();

    public ClienteIMP() {
    }

    public ClienteIMP(String id, String cnpj, String inscricaoestadual, String razao, String fantasia) {
        this.id = id;
        this.cnpj = cnpj;
        this.inscricaoestadual = inscricaoestadual;
        this.razao = razao;
        this.fantasia = fantasia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoInscricao getTipoInscricao() {
        return tipoInscricao;
    }

    public void setTipoInscricao(TipoInscricao tipoInscricao) {
        this.tipoInscricao = tipoInscricao != null ? tipoInscricao : TipoInscricao.VAZIO;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Date getDataBloqueio() {
        return dataBloqueio;
    }

    public void setDataBloqueio(Date dataBloqueio) {
        this.dataBloqueio = dataBloqueio;
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

    public int getMunicipioIBGE() {
        return municipioIBGE;
    }

    public void setMunicipioIBGE(int municipioIBGE) {
        this.municipioIBGE = municipioIBGE;
    }

    public void setMunicipioIBGE(String municipioIBGE) {
        this.municipioIBGE = Utils.stringToInt(municipioIBGE);
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int getUfIBGE() {
        return ufIBGE;
    }

    public void setUfIBGE(int ufIBGE) {
        this.ufIBGE = ufIBGE;
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

    public TipoEstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(TipoEstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public void setEstadoCivil(int estadoCivil) {
        setEstadoCivil(TipoEstadoCivil.getById(estadoCivil));
    }

    public void setEstadoCivil(String estadoCivil) {
        setEstadoCivil(TipoEstadoCivil.getByString(estadoCivil));
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public TipoSexo getSexo() {
        return sexo;
    }

    public void setSexo(TipoSexo sexo) {
        this.sexo = sexo;
    }

    public void setSexo(String sexo) {
        if (sexo == null || sexo.trim().equals("")) {
            this.sexo = TipoSexo.MASCULINO;
            return;
        }
        this.sexo = "F".equals(sexo.trim().substring(1)) ? TipoSexo.FEMININO : TipoSexo.MASCULINO;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEmpresaEndereco() {
        return empresaEndereco;
    }

    public void setEmpresaEndereco(String empresaEndereco) {
        this.empresaEndereco = empresaEndereco;
    }

    public String getEmpresaNumero() {
        return empresaNumero;
    }

    public void setEmpresaNumero(String empresaNumero) {
        this.empresaNumero = empresaNumero;
    }

    public String getEmpresaComplemento() {
        return empresaComplemento;
    }

    public void setEmpresaComplemento(String empresaComplemento) {
        this.empresaComplemento = empresaComplemento;
    }

    public String getEmpresaBairro() {
        return empresaBairro;
    }

    public void setEmpresaBairro(String empresaBairro) {
        this.empresaBairro = empresaBairro;
    }

    public int getEmpresaMunicipioIBGE() {
        return empresaMunicipioIBGE;
    }

    public void setEmpresaMunicipioIBGE(int empresaMunicipioIBGE) {
        this.empresaMunicipioIBGE = empresaMunicipioIBGE;
    }

    public String getEmpresaMunicipio() {
        return empresaMunicipio;
    }

    public void setEmpresaMunicipio(String empresaMunicipio) {
        this.empresaMunicipio = empresaMunicipio;
    }

    public int getEmpresaUfIBGE() {
        return empresaUfIBGE;
    }

    public void setEmpresaUfIBGE(int empresaUfIBGE) {
        this.empresaUfIBGE = empresaUfIBGE;
    }

    public String getEmpresaUf() {
        return empresaUf;
    }

    public void setEmpresaUf(String empresaUf) {
        this.empresaUf = empresaUf;
    }

    public String getEmpresaCep() {
        return empresaCep;
    }

    public void setEmpresaCep(String empresaCep) {
        this.empresaCep = empresaCep;
    }

    public String getEmpresaTelefone() {
        return empresaTelefone;
    }

    public void setEmpresaTelefone(String empresaTelefone) {
        this.empresaTelefone = empresaTelefone;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public double getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(double valorLimite) {
        this.valorLimite = valorLimite;
    }

    public String getNomeConjuge() {
        return nomeConjuge;
    }

    public void setNomeConjuge(String nomeConjuge) {
        this.nomeConjuge = nomeConjuge;
    }

    public String getCpfConjuge() {
        return cpfConjuge;
    }

    public void setCpfConjuge(String cpfConjuge) {
        this.cpfConjuge = cpfConjuge;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(int diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public boolean isPermiteCreditoRotativo() {
        return permiteCreditoRotativo;
    }

    public void setPermiteCreditoRotativo(boolean permiteCreditoRotativo) {
        this.permiteCreditoRotativo = permiteCreditoRotativo;
    }

    public boolean isPermiteCheque() {
        return permiteCheque;
    }

    public void setPermiteCheque(boolean permiteCheque) {
        this.permiteCheque = permiteCheque;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(telefone);
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = Utils.formataNumero(celular);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addEmail(String descricao, String email, TipoContato tipo) {
        if (email != null && !"".equals(email.trim())) {
            addContato(null, descricao, "", "", email);
        }
    }

    public void addEmail(String email, TipoContato tipo) {
        addEmail(tipo.getDescricao(), email, tipo);
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCobrancaTelefone() {
        return cobrancaTelefone;
    }

    public void setCobrancaTelefone(String cobrancaTelefone) {
        this.cobrancaTelefone = cobrancaTelefone;
    }

    public int getPrazoPagamento() {
        return prazoPagamento;
    }

    public void setPrazoPagamento(int prazoPagamento) {
        this.prazoPagamento = prazoPagamento;
    }

    public String getCobrancaEndereco() {
        return cobrancaEndereco;
    }

    public void setCobrancaEndereco(String cobrancaEndereco) {
        this.cobrancaEndereco = cobrancaEndereco;
    }

    public String getCobrancaNumero() {
        return cobrancaNumero;
    }

    public void setCobrancaNumero(String cobrancaNumero) {
        this.cobrancaNumero = cobrancaNumero;
    }

    public String getCobrancaComplemento() {
        return cobrancaComplemento;
    }

    public void setCobrancaComplemento(String cobrancaComplemento) {
        this.cobrancaComplemento = cobrancaComplemento;
    }

    public String getCobrancaBairro() {
        return cobrancaBairro;
    }

    public void setCobrancaBairro(String cobrancaBairro) {
        this.cobrancaBairro = cobrancaBairro;
    }

    public int getCobrancaMunicipioIBGE() {
        return cobrancaMunicipioIBGE;
    }

    public void setCobrancaMunicipioIBGE(int cobrancaMunicipioIBGE) {
        this.cobrancaMunicipioIBGE = cobrancaMunicipioIBGE;
    }

    public String getCobrancaMunicipio() {
        return cobrancaMunicipio;
    }

    public void setCobrancaMunicipio(String cobrancaMunicipio) {
        this.cobrancaMunicipio = cobrancaMunicipio;
    }

    public int getCobrancaUfIBGE() {
        return cobrancaUfIBGE;
    }

    public void setCobrancaUfIBGE(int cobrancaUfIBGE) {
        this.cobrancaUfIBGE = cobrancaUfIBGE;
    }

    public String getCobrancaUf() {
        return cobrancaUf;
    }

    public void setCobrancaUf(String cobrancaUf) {
        this.cobrancaUf = cobrancaUf;
    }

    public String getCobrancaCep() {
        return cobrancaCep;
    }

    public void setCobrancaCep(String cobrancaCep) {
        this.cobrancaCep = cobrancaCep;
    }

    public TipoOrgaoPublico getTipoOrgaoPublico() {
        return tipoOrgaoPublico;
    }

    public void setTipoOrgaoPublico(TipoOrgaoPublico tipoOrgaoPublico) {
        this.tipoOrgaoPublico = tipoOrgaoPublico;
    }

    public double getLimiteCompra() {
        return limiteCompra;
    }

    public void setLimiteCompra(double limiteCompra) {
        this.limiteCompra = limiteCompra;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public TipoIndicadorIE getTipoIndicadorIe() {
        return tipoIndicadorIe;
    }

    public void setTipoIndicadorIe(TipoIndicadorIE tipoIndicadorIe) {
        this.tipoIndicadorIe = tipoIndicadorIe;
    }

    public List<ClienteContatoIMP> getContatos() {
        return contatos;
    }

    public List<ClienteDependenteIMP> getDependentes() {
        return dependentes;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    /**
     * Inclui um contato.
     *
     * @param id Código do contato.
     * @param nome Nome do contato.
     * @param telefone Telefone do contato.
     * @param celular Celular do contato.
     * @param email E-mail do contato. (Eventual)
     */
    public void addContato(String id, String nome, String telefone, String celular, String email) {
        if (nome != null && !"".equals(nome.trim())) {
            ClienteContatoIMP contato = new ClienteContatoIMP();

            if (id == null) {
                Set<String> ids = new HashSet<>();
                for (ClienteContatoIMP cont : contatos) {
                    ids.add(cont.getId());
                }
                int cont = 1;
                while (ids.contains("CONTATO " + cont)) {
                    cont++;
                }
                id = "CONTATO " + cont;
            }

            contato.setId(id);
            contato.setCliente(this);
            contato.setNome(nome);
            contato.setTelefone(telefone);
            contato.setCelular(celular);
            contato.setEmail(email);
            contatos.add(contato);
        }
    }

    public void addDependente(String id, String nome, String cpf, String tipoDependente) {
        if (nome != null && !"".equals(nome.trim())) {
            ClienteDependenteIMP dependente = new ClienteDependenteIMP();

            if (id == null) {
                Set<String> ids = new HashSet<>();
                for (ClienteDependenteIMP dep : dependentes) {
                    ids.add(dep.getId());
                }
                int cont = 1;
                while (ids.contains("DEPENDETE " + cont)) {
                    cont++;
                }
                id = "DEPENDENTE " + cont;
            }

            dependente.setId(id);
            dependente.setCliente(this);
            dependente.setNome(nome);
            dependente.setCpf(Long.valueOf(cpf));
            dependente.setTipodependente(tipoDependente);
            dependentes.add(dependente);
        }
    }

    public void addTelefone(String descricao, String numero) {
        descricao = Utils.acertarTexto(descricao);
        numero = Utils.stringLong(numero);
        if (!"".equals(descricao) && !"0".equals(numero)) {
            addContato(descricao, descricao, numero, "", "");
        }
    }

    public void addCelular(String descricao, String numero) {
        descricao = Utils.acertarTexto(descricao);
        numero = Utils.stringLong(numero);
        if (!"".equals(descricao) && !"0".equals(numero)) {
            addContato(descricao, descricao, "", numero, "");
        }
    }

    public void copiarEnderecoParaEmpresa() {
        this.empresaEndereco = this.endereco;
        this.empresaNumero = this.numero;
        this.empresaComplemento = this.complemento;
        this.empresaBairro = this.bairro;
        this.empresaMunicipio = this.municipio;
        this.empresaMunicipioIBGE = this.municipioIBGE;
        this.empresaUf = this.uf;
        this.empresaUfIBGE = this.ufIBGE;
        this.empresaCep = this.cep;
    }

    public void copiarEnderecoParaCobranca() {
        this.cobrancaEndereco = this.endereco;
        this.cobrancaNumero = this.numero;
        this.cobrancaComplemento = this.complemento;
        this.cobrancaBairro = this.bairro;
        this.cobrancaMunicipio = this.municipio;
        this.cobrancaMunicipioIBGE = this.municipioIBGE;
        this.cobrancaUf = this.uf;
        this.cobrancaUfIBGE = this.ufIBGE;
        this.cobrancaCep = this.cep;
    }

    public String getObservacao2() {
        return observacao2;
    }

    public void setObservacao2(String observacao2) {
        this.observacao2 = observacao2;
    }

    public int getGrupo() {
        return grupo;
    }

    public void setGrupo(int grupo) {
        this.grupo = grupo;
    }

    public double getPonto() {
        return ponto;
    }

    public void setPonto(double ponto) {
        this.ponto = ponto;
    }

    /**
     * @return the permiteChequeAVista
     */
    public boolean isPermiteChequeAVista() {
        return permiteChequeAVista;
    }

    /**
     * @param permiteChequeAVista the permiteChequeAVista to set
     */
    public void setPermiteChequeAVista(boolean permiteChequeAVista) {
        this.permiteChequeAVista = permiteChequeAVista;
    }

}
