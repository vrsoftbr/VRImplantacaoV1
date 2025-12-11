package vrimplantacao2.vo.cadastro.financeiro.contareceber.verbas;

import java.util.Date;

/**
 *
 * @author Wesley
 */
public class VerbasVO {
    private int id;
    private int id_loja;
    private int id_tiporecebimento = 8;
    private Date dataEmissao;
    private Date dataVencimento;
    private int id_fornecedor;
    private int id_divisaofornecedor = 0;
    private int id_comprador = 1;
    private int mercadologico1;
    private int id_tipoverba = 7;
    private int id_situacaocadastro = 1;
    private int id_situacaoverba = 0;
    private String representante = "MIGRADO";
    private String telefone = "";
    private int id_tipolocalcobranca = 0;
    private double valor;
    private String observacao;
    private int cpfrepresentante = 0;
    private String rgrepresentante = "";
    private String id_verbasellout = null;
    private String id_notaentrada = null;
    private int id_tipoorigemverba = 1;
    private int valorrebaixacusto = 0;
    private int codigoAtualVencimento;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public int getId_tiporecebimento() {
        return id_tiporecebimento;
    }

    public void setId_tiporecebimento(int id_tiporecebimento) {
        this.id_tiporecebimento = id_tiporecebimento;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataemissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
    
    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public int getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(int id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public int getId_divisaofornecedor() {
        return id_divisaofornecedor;
    }

    public void setId_divisaofornecedor(int id_divisaofornecedor) {
        this.id_divisaofornecedor = id_divisaofornecedor;
    }

    public int getId_comprador() {
        return id_comprador;
    }

    public void setId_comprador(int id_comprador) {
        this.id_comprador = id_comprador;
    }

    public int getMercadologico1() {
        return mercadologico1;
    }

    public void setMercadologico1(int mercadologico1) {
        this.mercadologico1 = mercadologico1;
    }

    public int getId_tipoverba() {
        return id_tipoverba;
    }

    public void setId_tipoverba(int id_tipoverba) {
        this.id_tipoverba = id_tipoverba;
    }
    
    public int getId_situacaocadastro() {
        return id_situacaocadastro;
    }
    
    public void setId_situacaocadastro(int id_situacaocadastro) {
        this.id_situacaocadastro = id_situacaocadastro;
    }

    public int getId_situacaoverba() {
        return id_situacaoverba;
    }

    public void setId_situacaoverba(int id_situacaoverba) {
        this.id_situacaoverba = id_situacaoverba;
    }

    public String getRepresentante() {
        return representante;
    }

    public void setRepresentante(String representante) {
        this.representante = representante;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getId_tipolocalcobranca() {
        return id_tipolocalcobranca;
    }

    public void setId_tipolocalcobranca(int id_tipolocalcobranca) {
        this.id_tipolocalcobranca = id_tipolocalcobranca;
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

    public int getCpfrepresentante() {
        return cpfrepresentante;
    }

    public void setCpfrepresentante(int cpfrepresentante) {
        this.cpfrepresentante = cpfrepresentante;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getRgrepresentante() {
        return rgrepresentante;
    }

    public void setRgrepresentante(String rgrepresentante) {
        this.rgrepresentante = rgrepresentante;
    }

    public String getId_verbasellout() {
        return id_verbasellout;
    }

    public void setId_verbasellout(String id_verbasellout) {
        this.id_verbasellout = id_verbasellout;
    }

    public String getId_notaentrada() {
        return id_notaentrada;
    }

    public void setId_notaentrada(String id_notaentrada) {
        this.id_notaentrada = id_notaentrada;
    }

    public int getId_tipoorigemverba() {
        return id_tipoorigemverba;
    }

    public void setId_tipoorigemverba(int id_tipoorigemverba) {
        this.id_tipoorigemverba = id_tipoorigemverba;
    }

    public int getValorrebaixacusto() {
        return valorrebaixacusto;
    }

    public void setValorrebaixacusto(int valorrebaixacusto) {
        this.valorrebaixacusto = valorrebaixacusto;
    }

    public int getCodigoAtualVencimento() {
        return codigoAtualVencimento;
    }

    public void setCodigoAtualVencimento(int codigoAtualVencimento) {
        this.codigoAtualVencimento = codigoAtualVencimento;
    }
}
