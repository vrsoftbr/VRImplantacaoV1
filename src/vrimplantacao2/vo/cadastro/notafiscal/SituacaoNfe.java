package vrimplantacao2.vo.cadastro.notafiscal;

/**
 *
 * @author Leandro
 */
public enum SituacaoNfe {
    
    NAO_TRANSMITIDA (0),
    AUTORIZADA (1),
    REJEITADA (2),
    CANCELADA (3),
    INUTILIZADA (4),
    DENEGADA (5);

    /**
     * Retorna a situação da NF-e por um código.<br>
     * <br>
     * Código Descrição<br>
     * 00 Documento regular<br>
     * 01 Escrituração extemporânea de documento regular<br>
     * 02 Documento cancelado<br>
     * 03 Escrituração extemporânea de documento cancelado<br>
     * 04 NF-e, NFC-e ou CT-e - denegado<br>
     * 05 NF-e, NFC-e ou CT-e - Numeração inutilizada<br>
     * 06 Documento Fiscal Complementar<br>
     * 07 Escrituração extemporânea de documento complementar<br>
     * 08 Documento Fiscal emitido com base em Regime Especial ou Norma Específica<br>
     * 
     * @param line
     * @return 
     */
    public static SituacaoNfe getByCodigo(int line) {
        
        switch (line) {
            case 0: return AUTORIZADA;
            case 1: return AUTORIZADA;
            case 2: return CANCELADA;
            case 3: return CANCELADA;
            case 4: return DENEGADA;
            case 5: return INUTILIZADA;
            default: return null;
        }
    }
    
    private int id;

    private SituacaoNfe(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
