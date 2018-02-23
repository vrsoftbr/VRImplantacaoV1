package vrimplantacao2.dao.cadastro.cliente;

import java.util.List;
import vrimplantacao2.vo.importacao.ClienteIMP;

/**
 *
 * @author Leandro
 */
public enum OpcaoCliente {

    DADOS,
    CONTATOS,
    OBSERVACOES2,
    SITUACAO_CADASTRO,
    VALOR_LIMITE,
    INSCRICAO_ESTADUAL,
    DATA_NASCIMENTO,
    ENDERECO, 
    TELEFONE,
    ENDERECO_COMPLETO;
    
    private List<ClienteIMP> listaEspecial;

    public List<ClienteIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<ClienteIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }
}
