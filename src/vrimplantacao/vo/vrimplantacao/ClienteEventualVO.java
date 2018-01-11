package vrimplantacao.vo.vrimplantacao;

import java.util.Date;
import vrimplantacao.utils.Utils;

public class ClienteEventualVO {
    public int id = 0;
    public String nome = "";
    public String endereco = "";
    public String bairro = "";
    public int id_estado = 35;
    public String telefone = "";
    public int id_tipoinscricao = 1;
    public String inscricaoestadual = "";
    public int id_situacaocadastro = 0;
    public String fax = "";
    public String enderecocobranca = "";
    public String bairrocobranca = "";
    public int id_estadocobranca = -1;
    public String telefonecobranca = "";
    public int prazopagamento = 0;
    public int id_tipoorgaopublico = 0;
    public String datacadastro = "";
    public double limitecompra = 0;
    public boolean cobrataxanotafiscal = true;
    public int id_municipio = 35;
    public int id_municipiocobranca = -1;
    public long cep = 0;
    public long cnpj = 0;
    public long cepcobranca = 0;
    public int id_tiporecebimento = -1;
    public boolean bloqueado = false;
    public String numero = "";
    public String observacao = "";
    public int id_pais = 1058;
    public String inscricaomunicipal = "";
    public int id_contacontabilfiscalpassivo = -1;
    public String numerocobranca = "";
    public String complemento = "";
    public String complementocobranca = "";
    public int id_contacontabilfiscalativo = -1;
    public String telefone2 = "";
    public String telefone3 = "";
    public String contato = "";
    public String email = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Utils.acertarTexto(nome, 60, "SEM NOME " + getId());
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = Utils.acertarTexto(endereco, 50, "SEM ENDERECO");
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = Utils.acertarTexto(bairro, 30, "SEM BAIRRO");
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.acertarTexto(telefone, 14, "0000000000");
    }

    public int getId_tipoinscricao() {
        return id_tipoinscricao;
    }

    public void setId_tipoinscricao(int id_tipoinscricao) {
        this.id_tipoinscricao = id_tipoinscricao;
    }

    public String getInscricaoestadual() {
        return inscricaoestadual;
    }

    public void setInscricaoestadual(String inscricaoestadual) {
        this.inscricaoestadual = Utils.acertarTexto(inscricaoestadual, 20, "ISENTO");
    }

    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }

    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = Utils.acertarTexto(fax, 14, "0000000000");
    }

    public String getEnderecocobranca() {
        return enderecocobranca;
    }

    public void setEnderecocobranca(String enderecocobranca) {
        this.enderecocobranca = Utils.acertarTexto(enderecocobranca, 45, "SEM ENDERECO");
    }

    public String getBairrocobranca() {
        return bairrocobranca;
    }

    public void setBairrocobranca(String bairrocobranca) {
        this.bairrocobranca = Utils.acertarTexto(bairrocobranca, 30, "SEM BAIRRO");
    }

    public int getId_estadocobranca() {
        return id_estadocobranca;
    }

    public void setId_estadocobranca(int id_estadocobranca) {
        this.id_estadocobranca = id_estadocobranca;
    }

    public String getTelefonecobranca() {
        return telefonecobranca;
    }

    public void setTelefonecobranca(String telefonecobranca) {
        this.telefonecobranca = Utils.acertarTexto(telefonecobranca,14, "0000000000");
    }

    public int getPrazopagamento() {
        return prazopagamento;
    }

    public void setPrazopagamento(int prazopagamento) {
        this.prazopagamento = prazopagamento;
    }

    public int getId_tipoorgaopublico() {
        return id_tipoorgaopublico;
    }

    public void setId_tipoorgaopublico(int id_tipoorgaopublico) {
        this.id_tipoorgaopublico = id_tipoorgaopublico;
    }

    public String getDatacadastro() {
        return datacadastro;
    }

    public void setDatacadastro(String datacadastro) {
        this.datacadastro = datacadastro;
    }
    
    public void setDatacadastro(Date datacadastro) {
        this.datacadastro = Utils.formatDate(datacadastro);
    }

    public double getLimitecompra() {
        return limitecompra;
    }

    public void setLimitecompra(double limitecompra) {
        this.limitecompra = limitecompra;
    }

    public boolean isCobrataxanotafiscal() {
        return cobrataxanotafiscal;
    }

    public void setCobrataxanotafiscal(boolean cobrataxanotafiscal) {
        this.cobrataxanotafiscal = cobrataxanotafiscal;
    }

    public int getId_municipio() {
        return id_municipio;
    }

    public void setId_municipio(int id_municipio) {
        this.id_municipio = id_municipio;
    }

    public int getId_municipiocobranca() {
        return id_municipiocobranca;
    }

    public void setId_municipiocobranca(int id_municipiocobranca) {
        this.id_municipiocobranca = id_municipiocobranca;
    }

    public long getCep() {
        return cep;
    }

    public void setCep(long cep) {
        this.cep = cep;
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public long getCepcobranca() {
        return cepcobranca;
    }

    public void setCepcobranca(long cepcobranca) {
        this.cepcobranca = cepcobranca;
    }

    public int getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    public void setId_tiporecebimento(int id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = Utils.acertarTexto(numero, 6, "0");
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 25000, "IMPORTADO VR");
    }

    public int getId_pais() {
        return id_pais;
    }

    public void setId_pais(int id_pais) {
        this.id_pais = id_pais;
    }

    public String getInscricaomunicipal() {
        return inscricaomunicipal;
    }

    public void setInscricaomunicipal(String inscricaomunicipal) {
        this.inscricaomunicipal = Utils.acertarTexto(inscricaomunicipal,20);
    }

    public int getId_contacontabilfiscalpassivo() {
        return id_contacontabilfiscalpassivo;
    }

    public void setId_contacontabilfiscalpassivo(int id_contacontabilfiscalpassivo) {
        this.id_contacontabilfiscalpassivo = id_contacontabilfiscalpassivo;
    }

    public String getNumerocobranca() {
        return numerocobranca;
    }

    public void setNumerocobranca(String numerocobranca) {
        this.numerocobranca = Utils.acertarTexto(numerocobranca,6,"0");
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = Utils.acertarTexto(complemento, 30);
    }

    public String getComplementocobranca() {
        return complementocobranca;
    }

    public void setComplementocobranca(String complementocobranca) {
        this.complementocobranca = Utils.acertarTexto(complementocobranca, 30);
    }

    public int getId_contacontabilfiscalativo() {
        return id_contacontabilfiscalativo;
    }

    public void setId_contacontabilfiscalativo(int id_contacontabilfiscalativo) {
        this.id_contacontabilfiscalativo = id_contacontabilfiscalativo;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = Utils.acertarTexto(telefone2, 14);
    }

    public String getTelefone3() {
        return telefone3;
    }

    public void setTelefone3(String telefone3) {
        this.telefone3 = Utils.acertarTexto(telefone3, 14);
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = Utils.acertarTexto(contato, 30);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Utils.acertarTexto(email, 50);
    }
    
    
}