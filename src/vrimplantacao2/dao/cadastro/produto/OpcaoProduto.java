package vrimplantacao2.dao.cadastro.produto;

import java.util.List;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public enum OpcaoProduto {
    PRECO {
        @Override
        public String toString() {
            return "Preço";
        }
    }, CUSTO {
        @Override
        public String toString() {
            return "Custo";
        }
    }, CUSTO_COM_IMPOSTO {
        @Override
        public String toString() {
            return "Custo Com Imposto";
        }
    }, CUSTO_SEM_IMPOSTO {
        @Override
        public String toString() {
            return "Custo Sem Imposto";
        }
    }, ESTOQUE {
        @Override
        public String toString() {
            return "Estoque";
        }
    }, PIS_COFINS {
        @Override
        public String toString() {
            return "PIS/COFINS";
        }
    }, NATUREZA_RECEITA {
        @Override
        public String toString() {
            return "Natureza da Receita";
        }
    }, ICMS {
        @Override
        public String toString() {
            return "ICMS";
        }
    }, ATIVO {
        @Override
        public String toString() {
            return "Ativo/Inativo";
        }
    }, MERCADOLOGICO {
        @Override
        public String toString() {
            return "Mercadológico no produto";
        }        
    }, DESC_COMPLETA {
        @Override
        public String toString() {
            return "Descrição Completa";
        }        
    }, DESC_REDUZIDA {
        @Override
        public String toString() {
            return "Descrição Reduzida";
        }        
    }, DESC_GONDOLA {
        @Override
        public String toString() {
            return "Descrição de Gondola";
        }        
    }, VALIDADE {
        @Override
        public String toString() {
            return "Validade";
        }        
    }, ATACADO {
        @Override
        public String toString() {
            return "Atacado";
        }        
    }, FAMILIA {
        @Override
        public String toString() {
            return "Família";
        }        
    }, TIPO_EMBALAGEM_EAN {
        @Override
        public String toString() {
            return "Tipo Embalagem EAN";
        }        
    }, QTD_EMBALAGEM_EAN {
        @Override
        public String toString() {
            return "Qtd. Embalagem EAN";
        }        
    }, CEST {
        @Override
        public String toString() {
            return "C.E.S.T.";
        }        
    }, NCM {
        @Override
        public String toString() {
            return "N.C.M.";
        }        
    }, QTD_EMBALAGEM_COTACAO {
        @Override
        public String toString() {
            return "Qtd. Embalagem (Cotação)";
        }        
    }, PESAVEL {
        @Override
        public String toString() {
            return "Pesavel";
        }
    }, MARGEM {
        @Override
        public String toString() {
            return "Margem";
        }
    }, TIPO_EMBALAGEM_PRODUTO {
        @Override
        public String toString() {
            return "Tipo embalagem (Produto)";
        }
    }, DESCONTINUADO {
        @Override
        public String toString() {
            return "Descontinuado";
        }
    }, IMPORTAR_RESETAR_BALANCA, IMPORTAR_GERAR_SUBNIVEL_MERC, IMPORTAR_MANTER_BALANCA, 
    CATEGORIA;
    
    private List<ProdutoIMP> listaEspecial;
    
    public static OpcaoProduto[] PRODUTO_COMPLEMENTO_TODOS() {
        return new OpcaoProduto[] {
            PRECO, CUSTO, ESTOQUE, ATIVO
        };
    }

    public List<ProdutoIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<ProdutoIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }
    
    
    
}
