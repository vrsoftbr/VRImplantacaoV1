package vrimplantacao2.dao.cadastro.cliente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrimplantacao2.vo.importacao.ClienteIMP;

/**
 *
 * @author Leandro
 */
public enum OpcaoCliente {
    
    IMP_REINICIAR_NUMERACAO {
        @Override
        public boolean checkParametros() {
            if (!getParametros().containsKey("N_REINICIO")) {
                LOG.severe("Não foi informada o nº de reinicio do código do cliente");
                return false;
            }
            try {
                if (((int) getParametros().get("N_REINICIO")) < 1) {
                    LOG.severe("Número de reinicio menor que 0");
                    return false;
                }
            } catch (ClassCastException ex) {
                LOG.severe("N_REINICIO em formato inválido");
                return false;
            }
            return true;
        }        
    },
    IMP_CORRIGIR_TELEFONE,
    DADOS,
    CONTATOS,
    OBSERVACOES,
    OBSERVACOES2,
    SITUACAO_CADASTRO,
    VALOR_LIMITE,
    INSCRICAO_ESTADUAL,
    DATA_NASCIMENTO,
    ENDERECO, 
    TELEFONE,
    CELULAR,
    ENDERECO_COMPLETO,
    TIPO_INSCRICAO,
    PERMITE_CHEQUE,
    PERMITE_CREDITOROTATIVO, 
    NOVOS, 
    RAZAO, 
    FANTASIA,
    CNPJ,
    EMAIL;    
    
    private static final Logger LOG = Logger.getLogger(OpcaoCliente.class.getName());
    private List<ClienteIMP> listaEspecial;
    private Map<String, Object> parametros = new HashMap<>();
    
    public final OpcaoCliente addParametro(String key, Object value) {
        getParametros().put(key, value);
        return this;
    }

    public boolean checkParametros() {
        return true;
    }
    
    public Map<String, Object> getParametros() {
        return parametros;
    }
    
    public List<ClienteIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<ClienteIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }
}
