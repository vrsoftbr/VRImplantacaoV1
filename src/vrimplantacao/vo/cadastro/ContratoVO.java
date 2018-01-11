package vrimplantacao.vo.cadastro;

import java.util.List;
import java.util.ArrayList;

public class ContratoVO {

    public int id = 0;
    public int idTipoRecebimento = 0;
    public String tipoRecebimento = "";
    public int diaVencimento = 0;
    public String dataInicio = "";
    public String dataTermino = "";
    public boolean abatimentoPisCofins = false;
    public boolean abatimentoIcms = false;
    public boolean abatimentoIpi = false;
    public boolean abatimentoIcmsRetido = false;
    public boolean imprimeDesconto = false;
    public int idComprador = 0;
    public String comprador = "";
    public int idSituacaoCadastro = 0;
    public String situacaoCadastro = "";
    public int idFornecedor = 0;
    public String fornecedor = "";
    public List<ContratoAcordoVO> vAcordo = new ArrayList();
    public List<ContratoFornecedorVO> vFornecedor = new ArrayList();
    public int tipoApuracao = -1;
    public String dataInicioApuracao = "";
    public List<ContratoEscalaCrescimentoVO> vEscala = new ArrayList<ContratoEscalaCrescimentoVO>();

    public int getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(int diaVencimento) {
        this.diaVencimento = diaVencimento;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(String dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(String dataTermino) {
        this.dataTermino = dataTermino;
    }

    public boolean isAbatimentoPisCofins() {
        return abatimentoPisCofins;
    }

    public void setAbatimentoPisCofins(boolean abatimentoPisCofins) {
        this.abatimentoPisCofins = abatimentoPisCofins;
    }

    public boolean isAbatimentoIcms() {
        return abatimentoIcms;
    }

    public void setAbatimentoIcms(boolean abatimentoIcms) {
        this.abatimentoIcms = abatimentoIcms;
    }

    public boolean isAbatimentoIpi() {
        return abatimentoIpi;
    }

    public void setAbatimentoIpi(boolean abatimentoIpi) {
        this.abatimentoIpi = abatimentoIpi;
    }

    public boolean isAbatimentoIcmsRetido() {
        return abatimentoIcmsRetido;
    }

    public void setAbatimentoIcmsRetido(boolean abatimentoIcmsRetido) {
        this.abatimentoIcmsRetido = abatimentoIcmsRetido;
    }

    public boolean isImprimeDesconto() {
        return imprimeDesconto;
    }

    public void setImprimeDesconto(boolean imprimeDesconto) {
        this.imprimeDesconto = imprimeDesconto;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public String getComprador() {
        return comprador;
    }

    public void setComprador(String comprador) {
        this.comprador = comprador;
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

    public List<ContratoAcordoVO> getvAcordo() {
        return vAcordo;
    }

    public void setvAcordo(List<ContratoAcordoVO> vAcordo) {
        this.vAcordo = vAcordo;
    }

    public List<ContratoFornecedorVO> getvFornecedor() {
        return vFornecedor;
    }

    public void setvFornecedor(List<ContratoFornecedorVO> vFornecedor) {
        this.vFornecedor = vFornecedor;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }
}
