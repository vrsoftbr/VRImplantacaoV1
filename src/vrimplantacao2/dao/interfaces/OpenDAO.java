package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class OpenDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS
        }));
    }

    @Override
    public String getSistema() {
        return "Open";
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, abrev, reduzido, cgc  from genfil order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("abrev") + " - " + rst.getString("reduzido") + " - " + rst.getString("cgc")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT depto, descricao FROM open.gendep where classe = '' and subclasse = '' order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("depto"), rst.getString("descricao"));
                    
                    importarMercadologicoNivel2(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void importarMercadologicoNivel2(MercadologicoNivelIMP imp) throws Exception {
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT depto, classe, subclasse, descricao FROM open.gendep where depto = '" + imp.getId() + "' and classe != '' and subclasse = '' order by 1,2"
            )) {
                while (rst.next()) {                    
                    importarMercadologicoNivel3(
                            imp.addFilho(rst.getString("classe"), rst.getString("descricao"))
                    );
                }
            }
        }
    }

    private void importarMercadologicoNivel3(MercadologicoNivelIMP imp) throws Exception {
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT depto, classe, subclasse, descricao FROM open.gendep where depto = '" + imp.getMercadologicoPai().getId() + "' and classe = '" + imp.getId() + "' and subclasse != '' order by 1,2,3"
            )) {
                while (rst.next()) {                    
                    imp.addFilho(rst.getString("subclasse"), rst.getString("descricao"));
                }
            }
        }
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.CODPRO10 id,\n" +
                    "    coalesce(nullif(p.daulp510,'0000-00-00'),nullif(p.daulp410,'0000-00-00'),nullif(p.daulp310,'0000-00-00'),nullif(p.daulp210,'0000-00-00'),nullif(p.daulp110,'0000-00-00')) datacadastro,\n" +
                    "    coalesce(nullif(p.DAULVE10,'0000-00-00'),nullif(p.dtalttrib,'0000-00-00')) dataalteracao,\n" +
                    "    ean.ean,\n" +
                    "    1 qtdembalagem,\n" +
                    "    p.UNIDAD10 unidade,\n" +
                    "    p.PESOVA10 pesavel,\n" +
                    "    p.validade,\n" +
                    "    p.DESCPR10 descricaocompleta,\n" +
                    "    p.DESCRE10 descricaoreduzida,\n" +
                    "    p.DEPTOS10 merc1,\n" +
                    "    p.CLASSE10 merc2,\n" +
                    "    p.SUBCLA10 merc3,\n" +
                    "    null id_familia,\n" +
                    "    p.PSOUNI10 peso,\n" +
                    "    p.ESTMIN10 estoqueminimo,\n" +
                    "    p.ESTMAX10 estoquemaximo,\n" +
                    "    p.ESTATU10 estoque,\n" +
                    "    coalesce(nullif(p.precu110,0),p.precu210) custo,\n" +
                    "    coalesce(nullif(p.preco110,0),p.preco210) precovenda,\n" +
                    "    p.ncm,\n" +
                    "    nullif(p.cest,'0000000') cest,\n" +
                    "    p.PISPIS10 piscofinssaida,\n" +
                    "    p.natureza_receita piscofinsnatrec,\n" +
                    "    concat(coalesce(p.tribut10,''),'|',coalesce(p.basred10,'')) id_icms\n" +
                    "from\n" +
                    "	genpro p\n" +
                    "    left join (\n" +
                    "		select\n" +
                    "			p.CODPRO10 id,\n" +
                    "			p.CODEAN10 ean\n" +
                    "		from\n" +
                    "			genpro p\n" +
                    "		union\n" +
                    "		select\n" +
                    "			p.CODPRO10 id,\n" +
                    "			p.BARRA210 ean\n" +
                    "		from\n" +
                    "			genpro p\n" +
                    "		where\n" +
                    "			p.BARRA210 != ''\n" +
                    "		union\n" +
                    "		select\n" +
                    "			p.CODPRO10 id,\n" +
                    "			p.BARRA310 ean\n" +
                    "		from\n" +
                    "			genpro p\n" +
                    "		where\n" +
                    "			p.BARRA310 != ''\n" +
                    "		union\n" +
                    "		select\n" +
                    "			p.CODPRO10 id,\n" +
                    "			p.BARRA410 ean\n" +
                    "		from\n" +
                    "			genpro p\n" +
                    "		where\n" +
                    "			p.BARRA410 != ''\n" +
                    "    ) ean on p.codpro10 = ean.id\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca("S".equals(rst.getString("pesavel")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	concat(coalesce(p.tribut10,''),'|',coalesce(p.basred10,'')) id_icms,\n" +
                    "    tr.idetri40 descricao,\n" +
                    "    case \n" +
                    "    when rd.redfis50 > 0 then 20\n" +
                    "    when rd.redfis50 = 0 and tr.PERTRI40 > 0 then 0\n" +
                    "    end cst,\n" +
                    "    tr.pertri40 aliquota,\n" +
                    "    rd.redfis50 reduzido\n" +
                    "from \n" +
                    "	genpro p\n" +
                    "    join gentri tr on p.tribut10 = tr.codtri40\n" +
                    "    join genred rd on p.basred10 = rd.codred50"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id_icms"),
                            String.format("%s (%03d - %.2f - %.2f)", 
                                    rst.getString("descricao"),
                                    rst.getInt("cst"),
                                    rst.getDouble("aliquota"),
                                    rst.getDouble("reduzido")
                            ),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reduzido")
                    ));
                }
            }
        }
        
        return result;
    }
    
}
