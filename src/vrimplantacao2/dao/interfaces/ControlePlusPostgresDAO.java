/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;

/**
 *
 * @author Desenvolvimento
 */
public class ControlePlusPostgresDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "ControlePlus";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,                
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO
        ));
    }
    
    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '1'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	substring(se_codig, 3, 2) as merc2,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '2'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rst.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rst.getString("merc2"),
                                rst.getString("descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	substring(se_codig, 3, 2) as merc2,\n"
                    + "	substring(se_codig, 5, 2) as merc3,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '3'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	substring(se_codig, 1, 2) as merc1,\n"
                    + "	substring(se_codig, 3, 2) as merc2,\n"
                    + "	substring(se_codig, 5, 2) as merc3,\n"
                    + "	substring(se_codig, 7, 2) as merc4,\n"
                    + "	se_nome as descricao\n"
                    + "from implantacao.mercadologico_ondas\n"
                    + "where se_tipo = '4'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            MercadologicoNivelIMP merc3 = merc2.getNiveis().get(rst.getString("merc3"));
                            if (merc3 != null) {
                                merc3.addFilho(
                                        rst.getString("merc4"),
                                        rst.getString("descricao")
                                );
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }
}
