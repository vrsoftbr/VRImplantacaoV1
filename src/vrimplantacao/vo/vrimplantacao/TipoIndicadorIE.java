package vrimplantacao.vo.vrimplantacao;

/**
 * Classe utilizada para facilitar a atribuição do indicado de IE nos fornecedores.
 * @author Leandro
 */
public enum TipoIndicadorIE {
    CONTRIBUINTE_ICMS {
        @Override
        public int getId() {
            return 1;
        }
    },
    CONTRIBUINTE_ISENTO{
        @Override
        public int getId() {
            return 2;
        }
    },
    NAO_CONTRIBUINTE{
        @Override
        public int getId() {
            return 9;
        }
    };
    /**
     * Retorna o id da enumeração no banco de dados.
     * @return Id da enumarão no banco de dados.
     */
    public abstract int getId();
    /**
     * Retorna o TipoIndicadorIE correspondente através do ID.
     * @param id ID do TipoIndicadorIE
     * @return Caso seja um ID válido, retorna o TipoIndicadorIE correspondente,
     * senão retorna null.
     */
    public static TipoIndicadorIE getById(int id) {
        for (TipoIndicadorIE valor : values()) {
            if (valor.getId() == id) {
                return valor;
            }
        }
        return null;
    }
}
