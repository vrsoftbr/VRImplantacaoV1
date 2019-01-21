package vrimplantacao2.vo.cadastro.notafiscal;

/**
 *
 * @author Leandro
 */
public enum TipoFreteNotaFiscal {
    
    CONTRATADO_REMETENTE (0, 1),
    CONTRATADO_DESTINATARIO (1, 2),
    CONTRATADO_TERCEIRO (2, 0),
    PROPRIO_REMETENTE (3, 1),
    PROPRIO_DESTINATARIO (4, 2),
    SEM_OCORRENCIA_TRANSPORTE (9, 3);
    
    private final int id;
    private final int idVR;

    private TipoFreteNotaFiscal(int id, int idVR) {
        this.id = id;
        this.idVR = idVR;
    }

    public int getId() {
        return id;
    }
    
    public int getIdVR() {
        return idVR;
    }
    
    public static TipoFreteNotaFiscal getById(int id) {
        for (TipoFreteNotaFiscal tipo : values()) {
            if (tipo.getId() == id) {
                return tipo;
            }
        }
        return null;
    }

}
