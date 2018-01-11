package vrimplantacao.vo.notafiscal;

import java.util.List;
import java.util.ArrayList;
import vrimplantacao.vo.interfaces.DivergenciaVO;

public class NotaSaidaVO {

    public long id = 0;
    public int idLoja = 0;
    public int numeroNota = 0;
    public int idTipoNota = 0;
    public int idFornecedorDestinatario = -1;
    public int idClienteEventualDestinatario = -1;
    public int idTipoSaida = 0;
    public long idNotaSaidaComplemento = -1;
    public int numeroNotaComplemento = 0;
    public String tipoSaida = "";
    public String dataHoraEmissao = "";
    public String dataSaida = "";
    public double valorIpi = 0;
    public double valorBaseIpi = 0;
    public double valorFrete = 0;
    public double valorOutrasDespesas = 0;
    public double valorProduto = 0;
    public double valorTotal = 0;
    public double valorBaseCalculo = 0;
    public double valorIcms = 0;
    public double valorBaseSubstituicao = 0;
    public double valorIcmsSubstituicao = 0;
    public double valorSeguro = 0;
    public double valorDesconto = 0;
    public double pesoBruto = 0;
    public boolean impressao = false;
    public int idSituacaoNotaSaida = 0;
    public String situacaoNotaSaida = "";
    public int idTipoFreteNotaFiscal = 0;
    public int idMotoristaTransportador = -1;
    public int idFornecedorTransportador = -1;
    public int idClienteEventualTransportador = -1;
    public String placa = "";
    public int idTipoDevolucao = 0;
    public String tipoDevolucao = "";
    public String informacaoComplementar = "";
    public String informacaoComplementarNfe = "";
    public String senha = "";
    public int tipoLocalBaixa = 0;
    public List<NotaSaidaItemVO> vItem = new ArrayList();
    public List<NotaSaidaItemNfeVO> vItemNfe = new ArrayList();
    public List<NotaSaidaReposicaoVO> vReposicao = new ArrayList();
    public List<NotaEntradaVO> vProdutor = new ArrayList();
    public int idDestinatario = 0;
    public String destinatario = "";
    public int idEstadoDestinatario = 0;
    public String estadoDestinatario = "";
    public int idTransportador = 0;
    public String transportador = "";
    public int idEstadoTransportador = 0;
    public int idSituacaoNfe = 0;
    public String chaveNfe = "";
    public String reciboNfe = "";
    public String motivoRejeicaoNfe = "";
    public String protocoloRecebimentoNfe = "";
    public String dataHoraRecebimentoNfe = "";
    public String justificativaCancelamentoNfe = "";
    public String protocoloCancelamentoNfe = "";
    public long volume = 0;
    public double pesoLiquido = 0;
    public boolean emailNfe = false;
    public boolean contingenciaNfe = false;
    public List<NotaSaidaCupomVO> vCupom = new ArrayList();
    public List<NotaSaidaNotaEntradaVO> vNotaEntrada = new ArrayList();
    public long idNotaEntrada = -1;
    public boolean aplicaIcmsDesconto = true;
    public boolean aplicaIcmsEncargo = true;
    public int numeronotaProdutor = 0;
    public List<NotaSaidaTrocaCupomVO> vTrocaCupom = new ArrayList();
    public List<NotaSaidaDevolucaoCupomVO> vDevolucaoCupom = new ArrayList();
    public List<NotaSaidaItemDesmembramentoVO> vDesmembramento = new ArrayList();
    public List<NotaSaidaConvenioVO> vConvenio = new ArrayList();
    public String xml = "";
    public ArrayList<DivergenciaVO> vDivergencia = new ArrayList();
    public List<NotaSaidaVencimentoVO> vVencimento = new ArrayList();
    public boolean aplicaPisCofinsDesconto;
    public boolean aplicaPisCofinsEncargo;

    public List<NotaSaidaItemDesmembramentoVO> getvDesmembramento() {
        return vDesmembramento;
    }

    public void setvDesmembramento(List<NotaSaidaItemDesmembramentoVO> vDesmembramento) {
        this.vDesmembramento = vDesmembramento;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        this.numeroNota = numeroNota;
    }

    public int getIdTipoNota() {
        return idTipoNota;
    }

    public void setIdTipoNota(int idTipoNota) {
        this.idTipoNota = idTipoNota;
    }

    public int getIdFornecedorDestinatario() {
        return idFornecedorDestinatario;
    }

    public void setIdFornecedorDestinatario(int idFornecedorDestinatario) {
        this.idFornecedorDestinatario = idFornecedorDestinatario;
    }

    public int getIdClienteEventualDestinatario() {
        return idClienteEventualDestinatario;
    }

    public void setIdClienteEventualDestinatario(int idClienteEventualDestinatario) {
        this.idClienteEventualDestinatario = idClienteEventualDestinatario;
    }

    public int getIdTipoSaida() {
        return idTipoSaida;
    }

    public void setIdTipoSaida(int idTipoSaida) {
        this.idTipoSaida = idTipoSaida;
    }

    public String getTipoSaida() {
        return tipoSaida;
    }

    public void setTipoSaida(String tipoSaida) {
        this.tipoSaida = tipoSaida;
    }

    public String getDataHoraEmissao() {
        return dataHoraEmissao;
    }

    public void setDataHoraEmissao(String dataHoraEmissao) {
        this.dataHoraEmissao = dataHoraEmissao;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public double getValorIpi() {
        return valorIpi;
    }

    public void setValorIpi(double valorIpi) {
        this.valorIpi = valorIpi;
    }

    public double getValorBaseIpi() {
        return valorBaseIpi;
    }

    public void setValorBaseIpi(double valorBaseIpi) {
        this.valorBaseIpi = valorBaseIpi;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public double getValorOutrasDespesas() {
        return valorOutrasDespesas;
    }

    public void setValorOutrasDespesas(double valorOutrasDespesas) {
        this.valorOutrasDespesas = valorOutrasDespesas;
    }

    public double getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(double valorProduto) {
        this.valorProduto = valorProduto;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorBaseCalculo() {
        return valorBaseCalculo;
    }

    public void setValorBaseCalculo(double valorBaseCalculo) {
        this.valorBaseCalculo = valorBaseCalculo;
    }

    public double getValorIcms() {
        return valorIcms;
    }

    public void setValorIcms(double valorIcms) {
        this.valorIcms = valorIcms;
    }

    public double getValorBaseSubstituicao() {
        return valorBaseSubstituicao;
    }

    public void setValorBaseSubstituicao(double valorBaseSubstituicao) {
        this.valorBaseSubstituicao = valorBaseSubstituicao;
    }

    public double getValorIcmsSubstituicao() {
        return valorIcmsSubstituicao;
    }

    public void setValorIcmsSubstituicao(double valorIcmsSubstituicao) {
        this.valorIcmsSubstituicao = valorIcmsSubstituicao;
    }

    public double getValorSeguro() {
        return valorSeguro;
    }

    public void setValorSeguro(double valorSeguro) {
        this.valorSeguro = valorSeguro;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public boolean isImpressao() {
        return impressao;
    }

    public void setImpressao(boolean impressao) {
        this.impressao = impressao;
    }

    public int getIdSituacaoNotaSaida() {
        return idSituacaoNotaSaida;
    }

    public void setIdSituacaoNotaSaida(int idSituacaoNotaSaida) {
        this.idSituacaoNotaSaida = idSituacaoNotaSaida;
    }

    public String getSituacaoNotaSaida() {
        return situacaoNotaSaida;
    }

    public void setSituacaoNotaSaida(String situacaoNotaSaida) {
        this.situacaoNotaSaida = situacaoNotaSaida;
    }

    public int getIdTipoFreteNotaFiscal() {
        return idTipoFreteNotaFiscal;
    }

    public void setIdTipoFreteNotaFiscal(int idTipoFreteNotaFiscal) {
        this.idTipoFreteNotaFiscal = idTipoFreteNotaFiscal;
    }

    public int getIdMotoristaTransportador() {
        return idMotoristaTransportador;
    }

    public void setIdMotoristaTransportador(int idMotoristaTransportador) {
        this.idMotoristaTransportador = idMotoristaTransportador;
    }

    public int getIdFornecedorTransportador() {
        return idFornecedorTransportador;
    }

    public void setIdFornecedorTransportador(int idFornecedorTransportador) {
        this.idFornecedorTransportador = idFornecedorTransportador;
    }

    public int getIdClienteEventualTransportador() {
        return idClienteEventualTransportador;
    }

    public void setIdClienteEventualTransportador(int idClienteEventualTransportador) {
        this.idClienteEventualTransportador = idClienteEventualTransportador;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getIdTipoDevolucao() {
        return idTipoDevolucao;
    }

    public void setIdTipoDevolucao(int idTipoDevolucao) {
        this.idTipoDevolucao = idTipoDevolucao;
    }

    public String getInformacaoComplementar() {
        return informacaoComplementar;
    }

    public void setInformacaoComplementar(String informacaoComplementar) {
        this.informacaoComplementar = informacaoComplementar;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getTipoLocalBaixa() {
        return tipoLocalBaixa;
    }

    public void setTipoLocalBaixa(int tipoLocalBaixa) {
        this.tipoLocalBaixa = tipoLocalBaixa;
    }

    public List<NotaSaidaItemVO> getvItem() {
        return vItem;
    }

    public void setvItem(List<NotaSaidaItemVO> vItem) {
        this.vItem = vItem;
    }

    public List<NotaSaidaReposicaoVO> getvReposicao() {
        return vReposicao;
    }

    public void setvReposicao(List<NotaSaidaReposicaoVO> vReposicao) {
        this.vReposicao = vReposicao;
    }

    public List<NotaEntradaVO> getvProdutor() {
        return vProdutor;
    }

    public void setvProdutor(List<NotaEntradaVO> vProdutor) {
        this.vProdutor = vProdutor;
    }

    public int getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(int idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public int getIdEstadoDestinatario() {
        return idEstadoDestinatario;
    }

    public void setIdEstadoDestinatario(int idEstadoDestinatario) {
        this.idEstadoDestinatario = idEstadoDestinatario;
    }

    public String getEstadoDestinatario() {
        return estadoDestinatario;
    }

    public void setEstadoDestinatario(String estadoDestinatario) {
        this.estadoDestinatario = estadoDestinatario;
    }

    public int getIdTransportador() {
        return idTransportador;
    }

    public void setIdTransportador(int idTransportador) {
        this.idTransportador = idTransportador;
    }

    public String getTransportador() {
        return transportador;
    }

    public void setTransportador(String transportador) {
        this.transportador = transportador;
    }

    public int getIdEstadoTransportador() {
        return idEstadoTransportador;
    }

    public void setIdEstadoTransportador(int idEstadoTransportador) {
        this.idEstadoTransportador = idEstadoTransportador;
    }

    public boolean isEmailNfe() {
        return emailNfe;
    }

    public void setEmailNfe(boolean emailNfe) {
        this.emailNfe = emailNfe;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = pesoBruto;
    }
}
