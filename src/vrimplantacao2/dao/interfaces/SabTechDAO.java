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
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SabTechDAO extends InterfaceDAO implements MapaTributoProvider {

    public String user_banco = "Todos";
    public String pass_banco = "123";

    @Override
    public String getSistema() {
        return "SabTech";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MAPA_TRIBUTACAO,
        }));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "     icm.Codigo as codigo,\n"
                    + "     icm.ST as cst,\n"
                    + "     icm.SubsTrib as tributacao\n"
                    + "     from dbo.CPro_TabICMS icm\n"
                    + "     where icm.Codigo in (select ICMSTabela from dbo.CPro_Produto)\n"
                    + "     order by icm.Codigo"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("codigo"),
                            rs.getString("cst")
                            + " - " + rs.getString("tributacao"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	distinct \n"
                    + "	m1.Depto as merc1, m1.Descricao as desc_merc1,\n"
                    + "	m2.Classe as merc2, m2.Descricao as desc_merc2\n"
                    + "from dbo.CPro_Produto p\n"
                    + "inner join dbo.CPro_Depto m1 on m1.Depto = p.Depto\n"
                    + "inner join dbo.CPro_Classe m2 on m2.Classe = p.Classe\n"
                    + "order by m1.Depto, m2.Classe"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "     p.Produto as id,\n"
                    + "     p.CodBarras as ean,\n"
                    + "     p.Balanca,\n"
                    + "     p.Validade,\n"
                    + "     p.Unidade as tipoembalagem,\n"
                    + "     p.Descricao as descricaocompleta,\n"
                    + "     p.DescricaoCurta as descricaoreduzida,\n"
                    + "     p.Depto as merc1,\n"
                    + "     p.Classe as merc2,\n"
                    + "     p.EstMin as estoqueminimo,\n"
                    + "     p.EstMax as estoquemaximo,\n"
                    + "     p.EstAtual as estoque,\n"
                    + "     p.ValorCusto as custo,\n"
                    + "     p.VlVenda as precovenda,\n"
                    + "     p.Lucro as margem,\n"
                    + "     p.ICMSTabela idIcms,\n"
                    + "     p.Inativo,\n"
                    + "     p.ClaFiscal as ncm,\n"
                    + "     REPLACE(ces.CEST_Codigo, '.', '') as cest,\n"
                    + "     pis.ST as cst_pis,\n"
                    + "     cof.ST as cst_cofins	\n"
                    + "     from dbo.CPro_Produto p\n"
                    + "     left join dbo.CPro_TabCEST ces on ces.CEST = p.CESTTabela\n"
                    + "     left join dbo.CPro_TabPIS pis on pis.PISTabela = p.PISTabela\n"
                    + "     left join dbo.CPro_TabCOFINS cof on cof.COFINSTabela = p.COFINSTabela"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("Balanca") == 0);
                    imp.setValidade(rst.getInt("Validade"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setSituacaoCadastro(rst.getInt("Inativo") == 0 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins"));
                    imp.setIcmsDebitoId(rst.getString("idIcms"));
                    imp.setIcmsCreditoId(rst.getString("idIcms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "     ean.Produto as idproduto, \n"
                    + "     ean.CodBarras as ean, \n"
                    + "     ean.Unidade as tipoembalagem \n"
                    + "     from dbo.CPro_CodBarras ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + " a.Produto as idproduto,\n"
                        + " a.Unidade as tipoembalagem,\n"
                        + " a.Qtde as qtdembalagem,\n"
                        + " a.Unitario as precoatacado,\n"
                        + " p.VlVenda as precovenda\n"
                        + "from dbo.CPro_Preco a\n"
                        + "inner join dbo.CPro_Produto p on p.Produto = a.Produto\n"
                        + "where a.Qtde > 1\n"
                        + "and a.Qtde < 100\n"
                        + "and a.Unitario < p.VlVenda\n"
                        + "order by a.Produto"
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("idproduto"));

                        if (codigoAtual > 0) {
                            ProdutoIMP imp = new ProdutoIMP();
                            imp.setImportLoja(getLojaOrigem());
                            imp.setImportSistema(getSistema());
                            imp.setImportId(rst.getString("idproduto"));
                            imp.setEan("999999" + String.valueOf(codigoAtual));
                            imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                            imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                            imp.setPrecovenda(rst.getDouble("precovenda"));
                            imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }
}
