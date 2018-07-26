package vrimplantacao2.dao.cadastro.produto;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    }, ICMS_ENTRADA {
        @Override
        public String toString() {
            return "ICMS (Entrada)";
        }
    }, ICMS_SAIDA {
        @Override
        public String toString() {
            return "ICMS (Saída)";
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
    }, FABRICANTE {
        @Override
        public String toString() {
            return "Fabricante";
        }        
    }, DATA_CADASTRO {
        @Override
        public String toString() {
            return "Data Cadastro";
        }
    }, PAUTA_FISCAL_PRODUTO {
        @Override
        public String toString() {
            return "Pauta Fiscal";
        }        
    }, VENDA_PDV {
        @Override
        public String toString() {
            return "Venda (PDV)";
        }        
    }, EXCECAO {
        @Override
        public String toString() {
            return "Exceção";
        }
    }, SUGESTAO_COTACAO {
        @Override
        public String toString() {
            return "Sugestão de Cotação";
        }
    }, SUGESTAO_PEDIDO {
        @Override
        public String toString() {
            return "Sugestão de Pedido";
        }
    }, COMPRADOR_PRODUTO {
        @Override
        public String toString() {
            return "Comprador";
        }
    }, 
    /**
     * @deprecated Utilizar {@link #ICMS_ENTRADA}
     */
    @Deprecated
    ICMS_FORNECEDOR {
        @Override
        public String toString() {
            return "Icms Entrada Fornecedor";
        }
    }, DATA_ALTERACAO {
        @Override
        public String toString() {
            return "Data de Alteração";
        }
    }, ICMS_LOJA {
        @Override
        public String toString() {
            return "Icms Loja";
        }
    }, PISCOFINS_LOJA {
        @Override
        public String toString() {
            return "Piscofins Individual";
        }
    }, NCM_LOJA {
        @Override
        public String toString() {
            return "NCM Individual";
        }
    },

    FAMILIA_PRODUTO,PRODUTOS,EAN, EAN_EM_BRANCO, MERCADOLOGICO_PRODUTO, RECEITA_BALANCA, NUTRICIONAL, COMPRADOR, MAPA_TRIBUTACAO,
    IMPORTAR_RESETAR_BALANCA, IMPORTAR_GERAR_SUBNIVEL_MERC, IMPORTAR_MANTER_BALANCA, UNIFICAR_PRODUTO_BALANCA, OFERTA, PAUTA_FISCAL, ASSOCIADO, MERCADOLOGICO_POR_NIVEL_REPLICAR, MERCADOLOGICO_POR_NIVEL;

    public static Set<OpcaoProduto> getAll() {
        return new HashSet<>(Arrays.asList(OpcaoProduto.values()));
    }

    public static Set<OpcaoProduto> getPadrao() {
        Set<OpcaoProduto> result = new HashSet<>();
        result.addAll(getMercadologico());
        result.addAll(getFamilia());
        result.add(IMPORTAR_MANTER_BALANCA);
        result.add(PRODUTOS);
        result.add(EAN);
        result.add(EAN_EM_BRANCO);
        result.add(IMPORTAR_MANTER_BALANCA);
        result.addAll(getInfoAdicional());
        result.addAll(getComplementos());
        result.addAll(getTributos());
        
        return result;
    }
    
    public static Set<OpcaoProduto> getInfoAdicional() {
        return new HashSet<>(Arrays.asList(
                DESC_COMPLETA,
                DESC_REDUZIDA,
                DESC_GONDOLA,
                ATIVO,
                DESCONTINUADO,
                DATA_CADASTRO,
                DATA_ALTERACAO,
                PESAVEL,
                QTD_EMBALAGEM_COTACAO,
                QTD_EMBALAGEM_EAN,
                TIPO_EMBALAGEM_EAN,
                TIPO_EMBALAGEM_PRODUTO,
                VALIDADE,
                VENDA_PDV
        ));   
    }
    
    public static Set<OpcaoProduto> getComplementos() {
        return new HashSet<>(Arrays.asList(
                PRECO,
                CUSTO,
                CUSTO_COM_IMPOSTO,
                CUSTO_SEM_IMPOSTO,
                ESTOQUE,
                MARGEM
        ));        
    }
    
    public static Set<OpcaoProduto> getTributos() {
        return new HashSet<>(Arrays.asList(
                NCM,
                CEST,
                ICMS,
                PIS_COFINS,
                NATUREZA_RECEITA
        )); 
    }

    public static Set<OpcaoProduto> getFamilia() {
        return new HashSet<>(Arrays.asList(
                FAMILIA,
                FAMILIA_PRODUTO
        )); 
    }
    public static Set<OpcaoProduto> getMercadologico() {
        return new HashSet<>(Arrays.asList(
                MERCADOLOGICO,
                MERCADOLOGICO_PRODUTO
        )); 
    }
    
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
