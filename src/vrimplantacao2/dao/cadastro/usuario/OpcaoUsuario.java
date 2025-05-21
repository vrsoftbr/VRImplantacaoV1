package vrimplantacao2.dao.cadastro.usuario;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum OpcaoUsuario {
    /**
     * Dados básicos do usuario.
     */
    DADOS,
    FORCAR_UNIFICACAO;

//    private List<FornecedorIMP> listaEspecial;
//
//    public List<FornecedorIMP> getListaEspecial() {
//        return listaEspecial;
//    }
//
//    public void setListaEspecial(List<FornecedorIMP> listaEspecial) {
//        this.listaEspecial = listaEspecial;
//    }
    public static Set<OpcaoUsuario> getDados() {
        return new HashSet<>(Arrays.asList(
                DADOS
        ));
    }
//    
//    public static Set<OpcaoUsuario> getEndereco() {
//        return new HashSet<>(Arrays.asList(
//                ENDERECO,
//                NUMERO,
//                COMPLEMENTO,
//                BAIRRO,
//                MUNICIPIO,
//                UF,
//                CEP
//        ));
//    }
//    
//    public static Set<OpcaoUsuario> getContato() {
//        return new HashSet<>(Arrays.asList(
//                TELEFONE,
//                CONTATOS
//        ));
//    }
//    
//    public static Set<OpcaoUsuario> getDadosComplementares() {
//        return new HashSet<>(Arrays.asList(
//                DATA_CADASTRO,
//                SITUACAO_CADASTRO,
//                PRAZO_FORNECEDOR,
//                CONDICAO_PAGAMENTO,
//                TIPO_INDICADOR_IE,
//                OBSERVACAO,
//                TIPO_EMPRESA
//        ));
//    }
//    
//    public static Set<OpcaoUsuario> getPadrao() {
//        Set<OpcaoUsuario> result = new HashSet<>();
//        result.addAll(getDados());
//        result.addAll(getEndereco());
//        result.addAll(getContato());
//        result.addAll(getDadosComplementares());
//        result.add(PRODUTO_FORNECEDOR);
//        
//        return result;
//    }
}
