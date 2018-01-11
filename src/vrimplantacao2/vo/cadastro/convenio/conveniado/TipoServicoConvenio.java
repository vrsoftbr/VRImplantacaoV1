package vrimplantacao2.vo.cadastro.convenio.conveniado;

/**
 * Tipos de Serviço de Convênio
 * @author Leandro
 */
public enum TipoServicoConvenio {
    CONVENIO (1),
    ALIMENTACAO (2);
    
    private int id;
    
    private TipoServicoConvenio(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
