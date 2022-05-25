package vrimplantacao2.vo.cadastro.desmembramento;

import java.sql.Timestamp;
import java.util.Date;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

public class DesmembramentoVO {

    private int id;
    private String desmem_old;       //provavelmente é a ID, to mantendo de bobo
    private ProdutoVO produto;
    private Double estoque;
    private Date data;
    private String observacao = "IMPORTACAO VR";
    private Timestamp dataHoraAlteracao = new Timestamp(new Date().getTime());

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDesmem_old() {
        return desmem_old;
    }

    public void setDesmem_old(String desmem_old) {
        this.desmem_old = desmem_old;
    }

    public ProdutoVO getProduto() {
        return produto;
    }

    public void setProduto(ProdutoVO produto) {
        this.produto = produto;
    }

    public Double getEstoque() {
        return estoque;
    }

    public void setEstoque(Double estoque) {
        this.estoque = estoque;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data != null ? data : new Date();
        if (this.data == null) {
            this.data = new Timestamp(this.data.getTime());
        }
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = Utils.acertarTexto(observacao, 280);
    }

    public Date getDataHoraAlteracao() {
        return dataHoraAlteracao;
    }

    public void setDataHoraAlteracao(Timestamp dataHoraAlteracao) {
        this.dataHoraAlteracao = dataHoraAlteracao != null ? dataHoraAlteracao : new Timestamp(new Date().getTime());
    }
}
