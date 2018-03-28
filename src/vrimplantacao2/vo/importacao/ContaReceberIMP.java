package vrimplantacao2.vo.importacao;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import vrimplantacao2.vo.enums.TipoLocalCobranca;
import vrimplantacao2.vo.enums.TipoReceita;

/**
 *
 * @author Leandro
 */
public class ContaReceberIMP {
    
    private String id;
    private String idClienteEventual;
    private Date dataEmissao;// date NOT NULL,
    private Date dataVencimento;// date NOT NULL,
    private double valor = 0;// numeric(11,2) NOT NULL,
    private String observacao;// character varying(500) NOT NULL,
    private boolean ativo = true;
    private TipoLocalCobranca tipoLocalCobranca = TipoLocalCobranca.CARTEIRA;// id_tipolocalcobranca integer NOT NULL,
    private TipoReceita tipoReceita = TipoReceita.CR_OUTRAS_UNIDADES;// id_tiporeceita integer NOT NULL,
    private String idFornecedor;//id_fornecedor integer,
    private final Set<ContaReceberPagamentoIMP> pagamentos = new LinkedHashSet<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdClienteEventual() {
        return idClienteEventual;
    }

    public void setIdClienteEventual(String idClienteEventual) {
        this.idClienteEventual = idClienteEventual;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
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
        this.observacao = observacao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoLocalCobranca getTipoLocalCobranca() {
        return tipoLocalCobranca;
    }

    public void setTipoLocalCobranca(TipoLocalCobranca tipoLocalCobranca) {
        this.tipoLocalCobranca = tipoLocalCobranca;
    }

    public TipoReceita getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceita tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    public String getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(String idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public Set<ContaReceberPagamentoIMP> getPagamentos() {
        return pagamentos;
    }

    public void add(ContaReceberPagamentoIMP pagamento) {
        pagamentos.add(pagamento);
    }
    
    public void add(String id, double valor, double valorDesconto, double valorJuros, double valorMulta, Date dataPagamento) {
        ContaReceberPagamentoIMP imp = new ContaReceberPagamentoIMP();
        imp.setId(id);
        imp.setValor(valor);
        imp.setValorDesconto(valorDesconto);
        imp.setValorJuros(valorJuros);
        imp.setValorMulta(valorMulta);
        imp.setDataPagamento(dataPagamento);
        pagamentos.add(imp);
    }
    
}
