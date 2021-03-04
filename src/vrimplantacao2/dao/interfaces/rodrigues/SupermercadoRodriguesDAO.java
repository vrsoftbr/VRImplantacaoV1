/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces.rodrigues;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class SupermercadoRodriguesDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Supermercado Rodrigues";
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	l.id,\n"
                    + "	f.nomefantasia as descricao\n"
                    + "from loja l\n"
                    + "join fornecedor f on f.id = l.id_fornecedor\n"
                    + "order by 1")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
        ));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "	secao,\n"
                    + "	categoria,\n"
                    + "	subcategoria\n"
                    + "from implantacao.correcaomercadologico\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("secao"));
                    imp.setMerc1Descricao(rst.getString("secao"));
                    imp.setMerc2ID(rst.getString("categoria"));
                    imp.setMerc2Descricao(rst.getString("categoria"));
                    imp.setMerc3ID(rst.getString("subcategoria"));
                    imp.setMerc3Descricao(rst.getString("subcategoria"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo_produto,\n"
                    + "	codigo_barras,\n"
                    + "	descricao_produto,\n"
                    + "	secao,\n"
                    + "	categoria,\n"
                    + "	subcategoria\n"
                    + "from implantacao.correcaomercadologico\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barras"));
                    imp.setDescricaoCompleta(rst.getString("descricao_produto"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("secao"));
                    imp.setCodMercadologico2(rst.getString("categoria"));
                    imp.setCodMercadologico3(rst.getString("subcategoria"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
