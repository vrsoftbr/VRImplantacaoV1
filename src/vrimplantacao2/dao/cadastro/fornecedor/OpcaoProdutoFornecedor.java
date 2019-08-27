package vrimplantacao2.dao.cadastro.fornecedor;

import java.util.List;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;

/**
 *
 * @author Guilherme
 */
public enum OpcaoProdutoFornecedor {
    
    IPI {
        @Override
        public String toString(){
            return "IPI Fornecedor";
        }
    },
    DIVISAO_FORNECEDOR {
        @Override
        public String toString(){
            return "Divisão do Fornecedor";
        }
    },
    QTDEMBALAGEM {
        @Override
        public String toString(){
            return "Qtde. Emb. Fornecedor";
        }
    };
    
    private List<ProdutoFornecedorIMP> listaEspecial;

    public List<ProdutoFornecedorIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<ProdutoFornecedorIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }
}
