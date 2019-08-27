package vrimplantacao2.vo.cadastro.fornecedor;

import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;

/**
 *
 * @author Leandro
 */
public class ProdutoFornecedorVO {
    
    private int id = -1;
    private FornecedorVO fornecedor;    
    private ProdutoVO produto;
    private EstadoVO estado;
    private double custoTabela = 0;
    private String codigoExterno = "";
    private Date dataAlteracao = new Date();
    private double qtdEmbalagem = 1;
    private double pesoEmbalagem = 0;
    private double ipi = 0;
    private int tipoIpi = 0;
    private int idDivisaoFornecedor = 0;

    public int getId() {
        return id;
    }

    public double getCustoTabela() {
        return custoTabela;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public double getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public FornecedorVO getFornecedor() {
        return fornecedor;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public EstadoVO getEstado() {
        return estado;
    }

    public double getPesoEmbalagem() {
        return pesoEmbalagem;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustoTabela(double custoTabela) {
        this.custoTabela = MathUtils.trunc(custoTabela, 4, 999999999D);
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = Utils.acertarTexto(codigoExterno, 50);
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public void setQtdEmbalagem(double qtdEmbalagem) {
        this.qtdEmbalagem = MathUtils.trunc(qtdEmbalagem, 2, 999999999D);
    }

    public void setFornecedor(FornecedorVO fornecedor) {
        this.fornecedor = fornecedor;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public void setEstado(EstadoVO estado) {
        this.estado = estado;
    }

    public void setPesoEmbalagem(double pesoEmbalagem) {
        this.pesoEmbalagem = MathUtils.trunc(pesoEmbalagem, 3, 999999999D);
    }
    
    public double getIpi() {
        return ipi;
    }

    public void setIpi(double ipi) {
        this.ipi = ipi;
    }

    public int getTipoIpi() {
        return tipoIpi;
    }

    public void setTipoIpi(int tipoIpi) {
        this.tipoIpi = tipoIpi;
    }

    public int getIdDivisaoFornecedor() {
        return idDivisaoFornecedor;
    }

    public void setIdDivisaoFornecedor(int idDivisaoFornecedor) {
        this.idDivisaoFornecedor = idDivisaoFornecedor;
    }
    
}
