package vrimplantacao.vo.notafiscal;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao.gui.cadastro.FornecedorPagamentoVO;
import vrimplantacao.vo.cadastro.FornecedorContatoVO;
import vrimplantacao.vo.cadastro.FornecedorPrazoVO;

public class NotaFiscalFornecedorVO {

    public int id = 0;
    public String razaoSocial = "";
    public String nomeFantasia = "";
    public String endereco = "";
    public String numero = "";
    public String bairro = "";
    public String municipio = "";
    public int idMunicipio = 0;
    public int cep = 0;
    public int idEstado = 0;
    public String estado = "";
    public String telefone = "";
    public int idTipoInscricao = 0;
    public String tipoInscricao = "";
    public String inscricaoEstadual = "";
    public long cnpj = 0;
    public boolean revenda = false;
    public int idSituacaoCadastro = 0;
    public String situacaoCadastro = "";
    public int idTipoPagamento = 0;
    public String tipoPagamento = "";
    public int numeroDoc = 0;
    public int pedidoMinimoQtd = 0;
    public double pedidoMinimoValor = 0.0;
    public String serieNf = "";
    public boolean descontoFunRural = false;
    public int senha = 0;
    public int idTipoRecebimento = 0;
    public String tipoRecebimento = "";
    public String agencia = "";
    public String digitoAgencia = "";
    public String conta = "";
    public String digitoConta = "";
    public int idBanco = 0;
    public String banco = "";
    public int idFornecedorFavorecido = 0;
    public String fornecedorFavorecido = "";
    public String enderecoCobranca = "";
    public String bairroCobranca = "";
    public int cepCobranca = 0;
    public String municipioCobranca = "";
    public int idMunicipioCobranca = 0;
    public int idEstadoCobranca = 0;
    public String estadoCobranca = "";
    public int idFamiliaFornecedor = 0;
    public String familiaFornecedor = "";
    public boolean bloqueado = false;
    public int idTipoMotivoFornecedor = 0;
    public String tipoMotivoFornecedor = "";
    public String dataSintegra = "";
    public int idTipoEmpresa = 0;
    public String tipoEmpresa = "";
    public String inscricaoSuframa = "";
    public String modeloNf = "";
    public boolean utilizaIva = false;
    public boolean permiteNfSemPedido = false;
    public int idTipoInspecao = 0;
    public int numeroInspecao = 0;
    public int idTipoTroca = 0;
    public List<FornecedorContatoVO> vContato = new ArrayList();
    public List<FornecedorPrazoVO> vPrazo = new ArrayList();
    public int idTipoFornecedor = 0;
    public int idContaContabilFinanceiro = 0;
    public boolean utilizaNfe = false;
    public boolean utilizaConferencia = false;
    public int qtdLinha = 0;
    public boolean emiteNf = false;
    public int tipoNegociacao = 0;
    public boolean utilizaCrossDocking = false;
    public int idLojaCrossDocking = 0;
    public List<FornecedorPagamentoVO> vPagamento = new ArrayList();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public int getCep() {
        return cep;
    }

    public void setCep(int cep) {
        this.cep = cep;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getIdTipoInscricao() {
        return idTipoInscricao;
    }

    public void setIdTipoInscricao(int idTipoInscricao) {
        this.idTipoInscricao = idTipoInscricao;
    }

    public String getTipoInscricao() {
        return tipoInscricao;
    }

    public void setTipoInscricao(String tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public long getCnpj() {
        return cnpj;
    }

    public void setCnpj(long cnpj) {
        this.cnpj = cnpj;
    }

    public boolean isRevenda() {
        return revenda;
    }

    public void setRevenda(boolean revenda) {
        this.revenda = revenda;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }

    public String getSituacaoCadastro() {
        return situacaoCadastro;
    }

    public void setSituacaoCadastro(String situacaoCadastro) {
        this.situacaoCadastro = situacaoCadastro;
    }

    public int getIdTipoPagamento() {
        return idTipoPagamento;
    }

    public void setIdTipoPagamento(int idTipoPagamento) {
        this.idTipoPagamento = idTipoPagamento;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public int getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(int numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public int getPedidoMinimoQtd() {
        return pedidoMinimoQtd;
    }

    public void setPedidoMinimoQtd(int pedidoMinimoQtd) {
        this.pedidoMinimoQtd = pedidoMinimoQtd;
    }

    public double getPedidoMinimoValor() {
        return pedidoMinimoValor;
    }

    public void setPedidoMinimoValor(double pedidoMinimoValor) {
        this.pedidoMinimoValor = pedidoMinimoValor;
    }

    public String getSerieNF() {
        return serieNf;
    }

    public void setSerieNF(String serieNF) {
        this.serieNf = serieNF;
    }

    public boolean isDescontoFunRural() {
        return descontoFunRural;
    }

    public void setDescontoFunRural(boolean descontoFunRural) {
        this.descontoFunRural = descontoFunRural;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }

    public int getIdTipoRecebimento() {
        return idTipoRecebimento;
    }

    public void setIdTipoRecebimento(int idTipoRecebimento) {
        this.idTipoRecebimento = idTipoRecebimento;
    }

    public String getTipoRecebimento() {
        return tipoRecebimento;
    }

    public void setTipoRecebimento(String tipoRecebimento) {
        this.tipoRecebimento = tipoRecebimento;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getDigitoAgencia() {
        return digitoAgencia;
    }

    public void setDigitoAgencia(String digitoAgencia) {
        this.digitoAgencia = digitoAgencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDigitoConta() {
        return digitoConta;
    }

    public void setDigitoConta(String digitoConta) {
        this.digitoConta = digitoConta;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public int getIdFornecedorFavorecido() {
        return idFornecedorFavorecido;
    }

    public void setIdFornecedorFavorecido(int idFornecedorFavorecido) {
        this.idFornecedorFavorecido = idFornecedorFavorecido;
    }

    public String getFornecedorFavorecido() {
        return fornecedorFavorecido;
    }

    public void setFornecedorFavorecido(String fornecedorFavorecido) {
        this.fornecedorFavorecido = fornecedorFavorecido;
    }

    public String getEnderecoCobranca() {
        return enderecoCobranca;
    }

    public void setEnderecoCobranca(String enderecoCobranca) {
        this.enderecoCobranca = enderecoCobranca;
    }

    public String getBairroCobranca() {
        return bairroCobranca;
    }

    public void setBairroCobranca(String bairroCobranca) {
        this.bairroCobranca = bairroCobranca;
    }

    public int getCepCobranca() {
        return cepCobranca;
    }

    public void setCepCobranca(int cepCobranca) {
        this.cepCobranca = cepCobranca;
    }

    public String getMunicipioCobranca() {
        return municipioCobranca;
    }

    public void setMunicipioCobranca(String municipioCobranca) {
        this.municipioCobranca = municipioCobranca;
    }

    public int getIdMunicipioCobranca() {
        return idMunicipioCobranca;
    }

    public void setIdMunicipioCobranca(int idMunicipioCobranca) {
        this.idMunicipioCobranca = idMunicipioCobranca;
    }

    public int getIdEstadoCobranca() {
        return idEstadoCobranca;
    }

    public void setIdEstadoCobranca(int idEstadoCobranca) {
        this.idEstadoCobranca = idEstadoCobranca;
    }

    public String getEstadoCobranca() {
        return estadoCobranca;
    }

    public void setEstadoCobranca(String estadoCobranca) {
        this.estadoCobranca = estadoCobranca;
    }

    public int getIdFamiliaFornecedor() {
        return idFamiliaFornecedor;
    }

    public void setIdFamiliaFornecedor(int idFamiliaFornecedor) {
        this.idFamiliaFornecedor = idFamiliaFornecedor;
    }

    public String getFamiliaFornecedor() {
        return familiaFornecedor;
    }

    public void setFamiliaFornecedor(String familiaFornecedor) {
        this.familiaFornecedor = familiaFornecedor;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getIdTipoMotivoFornecedor() {
        return idTipoMotivoFornecedor;
    }

    public void setIdTipoMotivoFornecedor(int idTipoMotivoFornecedor) {
        this.idTipoMotivoFornecedor = idTipoMotivoFornecedor;
    }

    public String getTipoMotivoFornecedor() {
        return tipoMotivoFornecedor;
    }

    public void setTipoMotivoFornecedor(String tipoMotivoFornecedor) {
        this.tipoMotivoFornecedor = tipoMotivoFornecedor;
    }

    public String getDataSintegra() {
        return dataSintegra;
    }

    public void setDataSintegra(String dataSintegra) {
        this.dataSintegra = dataSintegra;
    }

    public int getIdTipoEmpresa() {
        return idTipoEmpresa;
    }

    public void setIdTipoEmpresa(int idTipoEmpresa) {
        this.idTipoEmpresa = idTipoEmpresa;
    }

    public String getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(String tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getInscricaoSuframa() {
        return inscricaoSuframa;
    }

    public void setInscricaoSuframa(String inscricaoSuframa) {
        this.inscricaoSuframa = inscricaoSuframa;
    }

    public String getModeloNF() {
        return modeloNf;
    }

    public void setModeloNF(String modeloNF) {
        this.modeloNf = modeloNF;
    }

    public boolean isUtilizaIva() {
        return utilizaIva;
    }

    public void setUtilizaIva(boolean utilizaIva) {
        this.utilizaIva = utilizaIva;
    }

    public int getIdTipoInspecao() {
        return idTipoInspecao;
    }

    public void setIdTipoInspecao(int idTipoInspecao) {
        this.idTipoInspecao = idTipoInspecao;
    }

    public int getNumeroInspecao() {
        return numeroInspecao;
    }

    public void setNumeroInspecao(int numeroInspecao) {
        this.numeroInspecao = numeroInspecao;
    }

    public List<FornecedorContatoVO> getvContato() {
        return vContato;
    }

    public void setvContato(List<FornecedorContatoVO> vContato) {
        this.vContato = vContato;
    }

    public List<FornecedorPrazoVO> getvPrazo() {
        return vPrazo;
    }

    public void setvPrazo(List<FornecedorPrazoVO> vPrazo) {
        this.vPrazo = vPrazo;
    }

    public int getQtdLinha() {
        return qtdLinha;
    }

    public void setQtdLinha(int qtdLinha) {
        this.qtdLinha = qtdLinha;
    }
}
