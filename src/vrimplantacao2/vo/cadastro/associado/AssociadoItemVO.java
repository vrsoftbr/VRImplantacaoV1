package vrimplantacao2.vo.cadastro.associado;

import vrimplantacao2.utils.MathUtils;

/**
 * Classe que representa a tabela associadoitem.
 * @author Leandro
 */
public class AssociadoItemVO {
    
    private int id;// integer NOT NULL DEFAULT nextval('associadoitem_id_seq'::regclass),
    private int idAssociado; //id_associado bigint NOT NULL,
    private int idProduto; //id_produto integer NOT NULL,
    private int qtdEmbalagem; // integer NOT NULL,
    private double percentualPreco; // numeric(19,4) NOT NULL,
    private boolean aplicaPreco; // boolean NOT NULL,
    private boolean aplicaCusto; // boolean NOT NULL,
    private boolean aplicaEstoque; // boolean NOT NULL,
    private double percentualCustoEstoque; // numeric(19,4) NOT NULL,

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAssociado() {
        return idAssociado;
    }

    public void setIdAssociado(int idAssociado) {
        this.idAssociado = idAssociado;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem < 1 ? 1 : qtdEmbalagem;
    }

    public double getPercentualPreco() {
        return percentualPreco;
    }

    public void setPercentualPreco(double percentualPreco) {
        this.percentualPreco = MathUtils.round(percentualPreco, 4);
    }

    public boolean isAplicaPreco() {
        return aplicaPreco;
    }

    public void setAplicaPreco(boolean aplicaPreco) {
        this.aplicaPreco = aplicaPreco;
    }

    public boolean isAplicaCusto() {
        return aplicaCusto;
    }

    public void setAplicaCusto(boolean aplicaCusto) {
        this.aplicaCusto = aplicaCusto;
    }

    public boolean isAplicaEstoque() {
        return aplicaEstoque;
    }

    public void setAplicaEstoque(boolean aplicaEstoque) {
        this.aplicaEstoque = aplicaEstoque;
    }

    public double getPercentualCustoEstoque() {
        return percentualCustoEstoque;
    }

    public void setPercentualCustoEstoque(double percentualCustoEstoque) {
        this.percentualCustoEstoque = MathUtils.round(percentualCustoEstoque, 4);
    }
    
}
