package vrimplantacao2.dao.cadastro.cliente;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    BAIRRO,
    TIPO_INSCRICAO,
    PERMITE_CHEQUE,
    PERMITE_CREDITOROTATIVO, 
    NOVOS, 
    RAZAO, 
    FANTASIA,
    CNPJ,
    EMAIL,
    BLOQUEADO,
    CEP,
    COMPLEMENTO,
    ESTADO_CIVIL,
    VENCIMENTO_ROTATIVO,
    SEXO,
    DATA_CADASTRO,
    DATA_NASCIMENTO_CONJUGE,
    EMPRESA,
    ENDERECO_EMPRESA,
    NUMERO_EMPRESA,
    COMPLEMENTO_EMPRESA,
    BAIRRO_EMPRESA,
    MUNICIPIO_EMPRESA,
    UF_EMPRESA,
    CEP_EMPRESA,
    TELEFONE_EMPRESA,
    SALARIO,
    CARGO,
    DATA_ADMISSAO,
    NUMERO,
    MUNICIPIO,
    UF,
    NOME_PAI,
    NOME_MAE,
    NOME_CONJUGE,
    RECEBER_CREDITOROTATIVO,
    RECEBER_CHEQUE,
    CLIENTE_EVENTUAL,
    IMPORTAR_SOMENTE_ATIVO_PREFERENCIAL,
    IMPORTAR_SOMENTE_ATIVO_EVENTUAL,
    IMPORTAR_SOMENTE_ATIVO,
    FORCAR_UNIFICACAO,
    CONVENIO_EMPRESA,
    CONVENIO_CONVENIADO,
    CONVENIO_TRANSACAO,
    OUTRAS_RECEITAS,
    DEPENDENTES;
    
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
    
    public static Set<OpcaoCliente> getDados() {
        return new HashSet<>(Arrays.asList(
                DADOS,
                RAZAO,
                FANTASIA,
                CNPJ,
                INSCRICAO_ESTADUAL
        ));
    }
    
    public static Set<OpcaoCliente> getEndereco() {
        return new HashSet<>(Arrays.asList(
                ENDERECO,
                NUMERO,
                COMPLEMENTO,
                BAIRRO,
                MUNICIPIO,
                UF,
                CEP
        ));
    }
    
    public static Set<OpcaoCliente> getContato() {
        return new HashSet<>(Arrays.asList(
                TELEFONE,
                CELULAR,
                EMAIL,
                CONTATOS
        ));
    }
    
    public static Set<OpcaoCliente> getClienteEventual() {
        return new HashSet<>(Arrays.asList(
                CLIENTE_EVENTUAL
        ));
    }
    
    public static Set<OpcaoCliente> getPadrao() {
        Set<OpcaoCliente> result = new HashSet<>();
        result.addAll(getDados());
        result.addAll(getEndereco());
        result.addAll(getContato());
        
        return result;
    }
    
    public static Set<OpcaoCliente> getClienteConvenio() {
        return new HashSet<>(Arrays.asList(
                CONVENIO_CONVENIADO,
                CONVENIO_EMPRESA,
                CONVENIO_TRANSACAO
        ));
    }
}
