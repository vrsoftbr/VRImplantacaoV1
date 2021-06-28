package vrimplantacao2_5.vo.enums;

/**
 *
 * @author guilhermegomes
 */
public enum ETipoLoja {
    LOJA_ORIGEM("LOJA DE ORIGEM"),
    LOJA_DESTINO("LOJA DESTINO DO VR");

    private final String tipoLoja;

    private ETipoLoja(String tipoLoja) {
        this.tipoLoja = tipoLoja;
    }

    public String getTipoLoja() {
        return tipoLoja;
    }
}
