package vrimplantacao.vo.vrimplantacao;

import java.sql.Date;
import vrimplantacao.utils.Utils;

public class ProdutoFornecedorVO {
    public int id = 0;
    public int id_produto = 0;
    public double id_produtoDouble = 0;
    public int id_fornecedor = 0;
    public double id_fornecedorDouble = 0;    
    public int id_estado = 0;
    public double custotabela = 0;
    public String codigoexterno = "";
    public int qtdembalagem = 1;
    public int id_divisaofornecedor = 0;
    public Date dataalteracao = null;
    public double desconto = 0;
    public int tipoipi = 0;
    public double ipi = 0;
    public int tipobonificacao = 0;
    public double bonificacao = 0;
    public int tipoverba = 0;
    public double verba = 0;
    public double custoinicial = 0;
    public int tipodesconto = 0;
    public double pesoembalagem = 0;
    public int id_tipopiscofins = 0; // default 0
    public int csosn = -1;
    public int fatorembalagem = 1;
    public long cnpFornecedor = 0;
    public long idProdutoLong = 0;
    public long idFornecedorLong = 0;
    public int idTipoEmbalagem = 0;
    public double iva = 0;
    public double pesoEmbalagem = 0;
    public int idTipoPisCofins = 0;
    public double precoVenda = 0;
    public String dataAlteracao = "";    
    private String id_produtoStr = null;

    public String getId_produtoStr() {
        return id_produtoStr;
    }

    public void setId_produtoStr(String id_produtoStr) {
        this.id_produtoStr = id_produtoStr;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_produto() {
        return id_produto;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public double getId_produtoDouble() {
        return id_produtoDouble;
    }

    public void setId_produtoDouble(double id_produtoDouble) {
        this.id_produtoDouble = id_produtoDouble;
    }

    public int getId_fornecedor() {
        return id_fornecedor;
    }

    public void setId_fornecedor(int id_fornecedor) {
        this.id_fornecedor = id_fornecedor;
    }

    public double getId_fornecedorDouble() {
        return id_fornecedorDouble;
    }

    public void setId_fornecedorDouble(double id_fornecedorDouble) {
        this.id_fornecedorDouble = id_fornecedorDouble;
    }

    public int getId_estado() {
        return id_estado;
    }

    public void setId_estado(int id_estado) {
        this.id_estado = id_estado;
    }

    public double getCustotabela() {
        return custotabela;
    }

    public void setCustotabela(double custotabela) {
        this.custotabela = custotabela;
    }

    public String getCodigoexterno() {
        return codigoexterno;
    }

    public void setCodigoexterno(String codigoexterno) {
        this.codigoexterno = Utils.acertarTexto(codigoexterno, 50);
    }

    public int getQtdembalagem() {
        return qtdembalagem;
    }

    public void setQtdembalagem(int qtdembalagem) {
        this.qtdembalagem = qtdembalagem;
    }

    public int getId_divisaofornecedor() {
        return id_divisaofornecedor;
    }

    public void setId_divisaofornecedor(int id_divisaofornecedor) {
        this.id_divisaofornecedor = id_divisaofornecedor;
    }

    public Date getDataalteracao() {
        return dataalteracao;
    }

    public void setDataalteracao(Date dataalteracao) {
        this.dataalteracao = dataalteracao;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public int getTipoipi() {
        return tipoipi;
    }

    public void setTipoipi(int tipoipi) {
        this.tipoipi = tipoipi;
    }

    public double getIpi() {
        return ipi;
    }

    public void setIpi(double ipi) {
        this.ipi = ipi;
    }

    public int getTipobonificacao() {
        return tipobonificacao;
    }

    public void setTipobonificacao(int tipobonificacao) {
        this.tipobonificacao = tipobonificacao;
    }

    public double getBonificacao() {
        return bonificacao;
    }

    public void setBonificacao(double bonificacao) {
        this.bonificacao = bonificacao;
    }

    public int getTipoverba() {
        return tipoverba;
    }

    public void setTipoverba(int tipoverba) {
        this.tipoverba = tipoverba;
    }

    public double getVerba() {
        return verba;
    }

    public void setVerba(double verba) {
        this.verba = verba;
    }

    public double getCustoinicial() {
        return custoinicial;
    }

    public void setCustoinicial(double custoinicial) {
        this.custoinicial = custoinicial;
    }

    public int getTipodesconto() {
        return tipodesconto;
    }

    public void setTipodesconto(int tipodesconto) {
        this.tipodesconto = tipodesconto;
    }

    public double getPesoembalagem() {
        return pesoembalagem;
    }

    public void setPesoembalagem(double pesoembalagem) {
        this.pesoembalagem = pesoembalagem;
    }

    public int getId_tipopiscofins() {
        return id_tipopiscofins;
    }

    public void setId_tipopiscofins(int id_tipopiscofins) {
        this.id_tipopiscofins = id_tipopiscofins;
    }

    public int getCsosn() {
        return csosn;
    }

    public void setCsosn(int csosn) {
        this.csosn = csosn;
    }

    public int getFatorembalagem() {
        return fatorembalagem;
    }

    public void setFatorembalagem(int fatorembalagem) {
        this.fatorembalagem = fatorembalagem;
    }

    public long getCnpFornecedor() {
        return cnpFornecedor;
    }

    public void setCnpFornecedor(long cnpFornecedor) {
        this.cnpFornecedor = cnpFornecedor;
    }   
    
    public String getDataAlteracao() {
        return this.dataAlteracao;
    }
    
    public void setDataAlteracao(String dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}
