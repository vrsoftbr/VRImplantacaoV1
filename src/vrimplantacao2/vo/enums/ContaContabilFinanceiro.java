package vrimplantacao2.vo.enums;

public enum ContaContabilFinanceiro {
    CAIXA {        
        @Override
        public int getID() {
            return 1;
        }
    },
    PAGAMENTO_FORNECEDOR {        
        @Override
        public int getID() {
            return 3;
        }
    };
    public abstract int getID();
    
    public static ContaContabilFinanceiro getByID(int id) {
        for (ContaContabilFinanceiro conta: values()) {
            if (conta.getID() == id) {
                return conta;
            }
        }
        return null;
    }

}
