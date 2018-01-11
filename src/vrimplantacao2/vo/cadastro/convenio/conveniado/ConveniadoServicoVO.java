package vrimplantacao2.vo.cadastro.convenio.conveniado;

/**
 *
 * @author Leandro
 */
public class ConveniadoServicoVO {
    private int id;// integer NOT NULL DEFAULT nextval('conveniadoservico_id_seq'::regclass),
    private int id_conveniado;// integer NOT NULL,
    private TipoServicoConvenio tipoServicoConvenio = TipoServicoConvenio.CONVENIO; //id_tipoServicoConvenio;// integer NOT NULL,
    private double valor;// numeric(11,2) NOT NULL,
    private double valorDesconto;// numeric(11,2) NOT NULL,

    public void setId(int id) {
        this.id = id;
    }

    public void setId_conveniado(int id_conveniado) {
        this.id_conveniado = id_conveniado;
    }

    public void setTipoServicoConvenio(TipoServicoConvenio tipoServicoConvenio) {
        this.tipoServicoConvenio = tipoServicoConvenio != null ? tipoServicoConvenio : TipoServicoConvenio.ALIMENTACAO;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public int getId() {
        return id;
    }

    public int getId_conveniado() {
        return id_conveniado;
    }

    public TipoServicoConvenio getTipoServicoConvenio() {
        return tipoServicoConvenio;
    }

    public double getValor() {
        return valor;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }
    
}
