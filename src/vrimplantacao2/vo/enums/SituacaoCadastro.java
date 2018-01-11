package vrimplantacao2.vo.enums;

/**
 * Representa a situação cadastral utilizada no VR.
 */
public enum SituacaoCadastro {
    
    EXCLUIDO {
        @Override
        public int getId() {
            return 0;
        }
    },
    ATIVO {
        @Override
        public int getId() {
            return 1;
        }
    };
    
    public abstract int getId();
    
    /**
     * Retorna uma situação cadastro pelo ID.
     * @param id ID da situação cadastral.
     * @return SituacaoCadastral encontrada ou null caso não localizada.
     */
    public static SituacaoCadastro getById(int id) {
        for (SituacaoCadastro sit: values()) {
            if (sit.getId() == id) {
                return sit;
            }
        }
        return null;
    }
}
