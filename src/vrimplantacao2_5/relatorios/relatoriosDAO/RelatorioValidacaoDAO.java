package vrimplantacao2_5.relatorios.relatoriosDAO;

import java.util.Map;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.sql.ResultSetMetaData;
import vrframework.classe.Conexao;
import vrimplantacao2.vo.cadastro.validacao.ProdutoBalancaValidacaoVO;

/**
 *
 * @author Wesley
 */
public class RelatorioValidacaoDAO {

    public Map<String, Long> getValidacaoProduto(String sistema, String lojaOrigem) throws Exception {

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
                + "FROM produto p "
                + "JOIN implantacao.codant_produto cp ON cp.codigoatual = p.id";

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

    public Map<String, Long> getValidacaoFornecedor(String sistema, String lojaOrigem) throws Exception {

        Map<String, Long> resultado = new LinkedHashMap<>();

        String sql
                = "SELECT "
                + "COUNT(*) AS total, "
                + "COUNT(*) FILTER (WHERE revenda) AS revenda, "
                + "COUNT(*) FILTER (WHERE descontofunrural) AS descontofunrural, "
                + "COUNT(*) FILTER (WHERE utilizaiva) AS utilizaiva, "
                + "COUNT(*) FILTER (WHERE utilizanfe) AS utilizanfe, "
                + "COUNT(*) FILTER (WHERE utilizaconferencia) AS utilizaconferencia, "
                + "COUNT(*) FILTER (WHERE permitenfsempedido) AS permitenfsempedido, "
                + "COUNT(*) FILTER (WHERE emitenf) AS emitenf, "
                + "COUNT(*) FILTER (WHERE utilizacrossdocking) AS utilizacrossdocking, "
                + "COUNT(*) FILTER (WHERE utilizaedi) AS utilizaedi, "
                + "COUNT(*) FILTER (WHERE nfemitidapostofiscal) AS nfemitidapostofiscal, "
                + "COUNT(*) FILTER (WHERE utilizaprodepe) AS utilizaprodepe, "
                + "COUNT(*) FILTER (WHERE alteradopaf) AS alteradopaf, "
                + "COUNT(*) FILTER (WHERE antecipacaopagamento) AS antecipacaopagamento, "
                + "COUNT(*) FILTER (WHERE recalcularnotafiscal) AS recalcularnotafiscal, "
                + "COUNT(*) FILTER (WHERE bloqueadoautomatico) AS bloqueadoautomatico, "
                + "COUNT(*) FILTER (WHERE bloqueado) AS bloqueado "
                + "FROM fornecedor f "
                + "JOIN implantacao.codant_fornecedor cf ON cf.codigoatual = f.id";

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

    public List<ProdutoBalancaValidacaoVO> getCodigosProdutos(
            String sistema,
            String lojaOrigem) throws Exception {

        List<ProdutoBalancaValidacaoVO> resultado = new ArrayList<>();

        String sql
                = "SELECT \n"
                + " cp.impid, \n"
                + " cp.codigoatual, \n"
                + " p.descricaocompleta, \n"
                + " p.pesavel, \n"
                + " p.id_tipoembalagem AS tipo_embalagem_produto, \n"
                + " p2.id_tipoembalagem AS tipo_embalagem_automacao, \n"
                + " p2.codigobarras \n"
                + "FROM implantacao.codant_produto cp \n"
                + "JOIN produto p \n"
                + "ON p.id = cp.codigoatual \n"
                + "JOIN produtoautomacao p2 \n"
                + "ON p2.id_produto = p.id";

        try (Statement stm = Conexao.createStatement();
                ResultSet rs = stm.executeQuery(sql)) {

            while (rs.next()) {

                ProdutoBalancaValidacaoVO vo =
                        new ProdutoBalancaValidacaoVO();

                vo.setCodigoAntigo(rs.getLong("impid"));
                vo.setCodigoAtual(rs.getLong("codigoatual"));
                vo.setDescricao(rs.getString("descricaocompleta"));
                vo.setPesavel(rs.getBoolean("pesavel"));
                vo.setTipoEmbalagem(rs.getInt("tipo_embalagem_produto"));
                vo.setTipoEmbalagemAutomacao(rs.getInt("tipo_embalagem_automacao"));
                vo.setCodigoBarras(rs.getLong("codigobarras"));

                resultado.add(vo);
            }
        }

        return resultado;
    }
}
