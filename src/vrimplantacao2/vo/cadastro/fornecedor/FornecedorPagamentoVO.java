package vrimplantacao2.vo.cadastro.fornecedor;

import vrimplantacao2.utils.Factory;
import vrimplantacao2.utils.multimap.MultiMap;

/**
 *
 * @author lucasrafael
 */
public class FornecedorPagamentoVO {
    private int id = 1;
    private FornecedorVO fornecedor;
    private int vencimento = 0;

    private final MultiMap<String, FornecedorPagamentoAnteriorVO> anteriores = new MultiMap<>(
        new Factory<FornecedorPagamentoAnteriorVO>() {
            @Override
            public FornecedorPagamentoAnteriorVO make() {
                FornecedorPagamentoAnteriorVO result = new FornecedorPagamentoAnteriorVO();                
                result.setCodigoAtual(FornecedorPagamentoVO.this);
                return result;
            }
        }
    );
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FornecedorVO getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(FornecedorVO fornecedor) {
        this.fornecedor = fornecedor;
    }

    public int getVencimento() {
        return vencimento;
    }

    public void setVencimento(int vencimento) {
        this.vencimento = vencimento;
    }
    
    public MultiMap<String, FornecedorPagamentoAnteriorVO> getAnteriores() {
        return anteriores;
    }
}
