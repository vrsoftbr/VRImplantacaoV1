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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class ZpfDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Zpf";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select e.codigo, e.codigo||' - '||e.razao_social descricao from empresa e order by e.codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_RESETAR_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.MAPA_TRIBUTACAO
        ));
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();        
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from grupo order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();                    
                    imp.setId(rst.getString("codigo"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    addMercadologicoNivel2(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void addMercadologicoNivel2(MercadologicoNivelIMP imp) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from subgrupo where grupo = " + imp.getId() + " order by 1"
            )) {
                while (rst.next()) {
                    imp.addFilho(rst.getString("codigo"), rst.getString("descricao"));
                }
            }
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.codigo id,\n" +
                    "    p.data_inclusao datacadastro,\n" +
                    "    p.data_ultima_alteracao dataalteracao,\n" +
                    "    case upper(p.balanca) when 'S' then p.codbalanca else coalesce(ean.codigo_barra, p.codbalanca) end ean,\n" +
                    "    case when coalesce(p.qtde_caixa, 1) <= 0 then 1 else coalesce(p.qtde_caixa, 1) end qtdembalagemcotacao,\n" +
                    "    p.unidade,\n" +
                    "    p.balanca,\n" +
                    "    coalesce(p.validade, 0) validade,\n" +
                    "    p.descricao descricaocompleta,\n" +
                    "    p.grupo merc1,\n" +
                    "    p.subgrupo merc2,\n" +
                    "    p.peso_bruto,\n" +
                    "    p.peso_liquido,\n" +
                    "    p.estoque_maximo,\n" +
                    "    p.estoque_minimo,\n" +
                    "    p.estoque_atual,\n" +
                    "    p.valor_custo custosemimposto,\n" +
                    "    p.valor_venda precovenda,\n" +
                    "    case p.fora_linha when 'S' then 0 else 1 end situacaocadastro,\n" +
                    "    case p.inativo when 'S' then 1 else 0 end descontinuado,\n" +
                    "    p.classificacao_fiscal ncm,\n" +
                    "    cest.cest,\n" +
                    "    p.cst_pis_cf piscofins_cst_entrada,\n" +
                    "    coalesce(sg.nat_rec, g.nat_rec) piscofins_natrec,\n" +
                    "    p.tributacao id_icms\n" +
                    "from\n" +
                    "    produtos p\n" +
                    "    left join cod_barras ean on\n" +
                    "        p.codigo = ean.produto\n" +
                    "    left join cest on\n" +
                    "        p.id_cest = cest.codigo\n" +
                    "    left join grupo g on\n" +
                    "        p.grupo = g.codigo\n" +
                    "    left join subgrupo sg on\n" +
                    "        p.subgrupo = sg.codigo\n" +
                    "order by\n" +
                    "    p.codigo"
            )) {
                while (rst.next()) {
                    
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoque(rst.getDouble("estoque_atual"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    codigo,\n" +
                    "    descricao,\n" +
                    "    porc_icms\n" +
                    "from\n" +
                    "    tributacao\n" +
                    "order by\n" +
                    "    codigo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("codigo"),
                            rst.getString("descricao"),
                            0,
                            rst.getDouble("porc_icms"),
                            0
                    ));
                }
            }
        }
        
        return result;
    }
    
    
    
}
