package vrimplantacao2.vo.cadastro;

import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.TipoEmbalagem;

public class ProdutoAutomacaoVO {
    
    private int id = -1;
    private ProdutoVO produto;
    private long codigoBarras = -2;
    private TipoEmbalagem tipoEmbalagem = TipoEmbalagem.UN;    
    private int qtdEmbalagem = 1;
    private double pesoBruto = 0;
    private boolean dun14 = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        if (codigoBarras <= 99999999999999L) {
            this.codigoBarras = codigoBarras;
        } else {
            this.codigoBarras = -2;
        }
    }

    public TipoEmbalagem getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(TipoEmbalagem tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public int getQtdEmbalagem() {
        return qtdEmbalagem;
    }

    public void setQtdEmbalagem(int qtdEmbalagem) {
        this.qtdEmbalagem = qtdEmbalagem;
    }

    public double getPesoBruto() {
        return pesoBruto;
    }

    public void setPesoBruto(double pesoBruto) {
        this.pesoBruto = Utils.truncar2(pesoBruto, 4);
    }

    public boolean isDun14() {
        return dun14;
    }

    public void setDun14(boolean dun14) {
        this.dun14 = dun14;
    }

    
}
