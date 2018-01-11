package vrimplantacao2.vo.cadastro.financeiro;

import java.util.Date;
import vrimplantacao.utils.Utils;

public class ReceberVerbaVO {

    private int idLoja;
    private int idTipoRecebimento = 0;
    private Date dataemissao;
    private Date datavencimento;
    private int idFornecedor;
    private int idDivisaoFornecedor = 0;
    private int idComprador = 1;
    private int mercadologico1 = 1;
    private int idTipoVerba = 7;
    private int idSituacaoCadastro = 1;
    private int idSituacaoReceberVerba = 0;
    private String representante;
    private String telefone;
    private boolean reciboImpresso = false;
    private int idTipoLocalCobranca = 0;
    private double valor;
    private String observacao;
    private long cpfRepresentante;
    private String rgRepresentante;
    private int idTipoOrigemVerba = 1;

    public int getIdLoja() {
        return idLoja;
    }

    public void setIdLoja(int idLoja) {
        this.idLoja = idLoja;
    }

    public int getIdTipoRecebimento() {
        return idTipoRecebimento;
    }

    public void setIdTipoRecebimento(int idTipoRecebimento) {
        this.idTipoRecebimento = idTipoRecebimento;
    }

    public Date getDataemissao() {
        return dataemissao;
    }

    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    public Date getDatavencimento() {
        return datavencimento;
    }

    public void setDatavencimento(Date datavencimento) {
        this.datavencimento = datavencimento;
    }

    public int getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(int idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public int getIdDivisaoFornecedor() {
        return idDivisaoFornecedor;
    }

    public void setIdDivisaoFornecedor(int idDivisaoFornecedor) {
        this.idDivisaoFornecedor = idDivisaoFornecedor;
    }

    public int getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(int idComprador) {
        this.idComprador = idComprador;
    }

    public int getMercadologico1() {
        return mercadologico1;
    }

    public void setMercadologico1(int mercadologico1) {
        this.mercadologico1 = mercadologico1;
    }

    public int getIdTipoVerba() {
        return idTipoVerba;
    }

    public void setIdTipoVerba(int idTipoVerba) {
        this.idTipoVerba = idTipoVerba;
    }

    public int getIdSituacaoCadastro() {
        return idSituacaoCadastro;
    }

    public void setIdSituacaoCadastro(int idSituacaoCadastro) {
        this.idSituacaoCadastro = idSituacaoCadastro;
    }

    public int getIdSituacaoReceberVerba() {
        return idSituacaoReceberVerba;
    }

    public void setIdSituacaoReceberVerba(int idSituacaoReceberVerba) {
        this.idSituacaoReceberVerba = idSituacaoReceberVerba;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        if (representante.length() > 40) {
            this.representante = Utils.acertarTexto(representante.substring(0, 40));
        } else {
            this.representante = Utils.acertarTexto(representante);
        }
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = Utils.formataNumero(telefone);
    }

    public boolean isReciboImpresso() {
        return reciboImpresso;
    }

    public void setReciboImpresso(boolean reciboImpresso) {
        this.reciboImpresso = reciboImpresso;
    }

    public int getIdTipoLocalCobranca() {
        return idTipoLocalCobranca;
    }

    public void setIdTipoLocalCobranca(int idTipoLocalCobranca) {
        this.idTipoLocalCobranca = idTipoLocalCobranca;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao);
    }

    public long getCpfRepresentante() {
        return cpfRepresentante;
    }

    public void setCpfRepresentante(long cpfRepresentante) {
        this.cpfRepresentante = cpfRepresentante;
    }

    public String getRgRepresentante() {
        return rgRepresentante;
    }

    public void setRgRepresentante(String rgRepresentante) {
        this.rgRepresentante = Utils.formataNumero(rgRepresentante);
    }

    public int getIdTipoOrigemVerba() {
        return idTipoOrigemVerba;
    }

    public void setIdTipoOrigemVerba(int idTipoOrigemVerba) {
        this.idTipoOrigemVerba = idTipoOrigemVerba;
    }
}
