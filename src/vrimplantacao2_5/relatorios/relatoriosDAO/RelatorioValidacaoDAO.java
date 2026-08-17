package vrimplantacao2_5.relatorios.relatoriosDAO;

import java.util.Map;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.sql.ResultSetMetaData;
import vrframework.classe.Conexao;

/**
 *
 * @author Wesley
 */
public class RelatorioValidacaoDAO {

    public Map<String, Long> getValidacaoProduto() throws Exception {
        
        Map<String, Long> resultado = new LinkedHashMap<>();

        String sql
                = "SELECT "
                + "COUNT(*) AS total, "
                + "COUNT(*) FILTER (WHERE pesavel) AS pesavel, "
                + "COUNT(*) FILTER (WHERE vendacontrolada) AS vendacontrolada, "
                + "COUNT(*) FILTER (WHERE vendapdv) AS vendapdv, "
                + "COUNT(*) FILTER (WHERE conferido) AS conferido, "
                + "COUNT(*) FILTER (WHERE permitequebra) AS permitequebra, "
                + "COUNT(*) FILTER (WHERE permiteperda) AS permiteperda, "
                + "COUNT(*) FILTER (WHERE sazonal) AS sazonal, "
                + "COUNT(*) FILTER (WHERE consignado) AS consignado, "
                + "COUNT(*) FILTER (WHERE permitetroca) AS permitetroca, "
                + "COUNT(*) FILTER (WHERE sugestaopedido) AS sugestaopedido, "
                + "COUNT(*) FILTER (WHERE aceitamultiplicacaopdv) AS aceitamultiplicacaopdv, "
                + "COUNT(*) FILTER (WHERE sugestaocotacao) AS sugestaocotacao, "
                + "COUNT(*) FILTER (WHERE utilizavalidadeentrada) AS utilizavalidadeentrada, "
                + "COUNT(*) FILTER (WHERE produtoecommerce) AS produtoecommerce, "
                + "COUNT(*) FILTER (WHERE permitedescontopdv) AS permitedescontopdv, "
                + "COUNT(*) FILTER (WHERE verificapesopdv) AS verificapesopdv, "
                + "COUNT(*) FILTER (WHERE promocaoauditada) AS promocaoauditada, "
                + "COUNT(*) FILTER (WHERE produtoassessorado) AS produtoassessorado, "
                + "COUNT(*) FILTER (WHERE controlepoliciacivil) AS controlepoliciacivil, "
                + "COUNT(*) FILTER (WHERE operacaoprodutoperfumariape) AS operacaoprodutoperfumariape, "
                + "COUNT(*) FILTER (WHERE produtoincentivado) AS produtoincentivado, "
                + "COUNT(*) FILTER (WHERE cestabasica) AS cestabasica, "
                + "COUNT(*) FILTER (WHERE isentoanvisa) AS isentoanvisa, "
                + "COUNT(*) FILTER (WHERE desativarenviomasterfiscobrasil) AS desativarenviomasterfiscobrasil "
                + "FROM produto";

        try (Statement stm = Conexao.createStatement();
             ResultSet rs = stm.executeQuery(sql)) {

            if (rs.next()) {

                ResultSetMetaData metaData = rs.getMetaData();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {

                    String campo = metaData.getColumnName(i).toUpperCase();
                    Long valor = rs.getLong(i);

                    resultado.put(campo, valor);
                }
            }
        }

        return resultado;
    }
}
