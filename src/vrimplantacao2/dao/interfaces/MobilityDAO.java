package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;

/**
 *
 * @author Alan
 */
public class MobilityDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Mobility";
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id,s_razao_social from configuracoes order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("ID"), rst.getString("s_razao_social")));
                }
            }

        }

        return result;
    }


    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.PRECO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.MARGEM,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ATIVO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS
        }));
    }    
    

}
