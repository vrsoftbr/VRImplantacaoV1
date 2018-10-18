package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AlphaSysDAO extends InterfaceDAO {
    
    private static final Logger LOG = Logger.getLogger(AlphaSysDAO.class.getName());

    @Override
    public String getSistema() {
        return "AlphaSys";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.NCM,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_empresa, cod_empresa||' - '||razao descricao from empresa order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cod_empresa"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cod_grupo merc1,\n" +
                    "    nome merc1_desc\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "where\n" +
                    "    nivel = 0\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("merc1"), rst.getString("merc1_desc"));
                    
                    mercNivel2(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void mercNivel2(MercadologicoNivelIMP pai) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    cod_grupo merc2,\n" +
                    "    nome merc2_desc\n" +
                    "from\n" +
                    "    grupo g\n" +
                    "where\n" +
                    "    g.nivel = 1 and\n" +
                    "    g.cod_juncao = " + pai.getId() + "\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    pai.addFilho(rst.getString("merc2"), rst.getString("merc2_desc"));
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
                    "    p.cod_produto id,\n" +
                    "    p.dt_atualizacao_preco datacadastro,\n" +
                    "    p.dt_atualizacao_preco dataalteracao,\n" +
                    "    p.cod_barras ean,\n" +
                    "    p.cod_busca_preco,\n" +
                    "    p.fator_conversao qtdemb_cotacao,\n" +
                    "    p.cod_unidade_saida unidade,\n" +
                    "    pc.validade,\n" +
                    "    case upper(p.produto_balanca) when 'TRUE' then 1 else 0 end e_balanca,\n" +
                    "    p.nome descricaocompleta,\n" +
                    "    p.nome_pdv descricaoreduzida,\n" +
                    "    p.cod_grupo mercadologico1,\n" +
                    "    p.cod_subgrupo mercadologico2,\n" +
                    "    pc.peso_bruto,\n" +
                    "    pc.peso_liquido,\n" +
                    "    pc.estoque_minimo,\n" +
                    "    pc.estoque_maximo,\n" +
                    "    pc.custo_lucro margem,\n" +
                    "    pc.preco_compra custocomimposto,\n" +
                    "    pc.preco_compra - coalesce(pc.custo_imposto, 0) custosemimposto,\n" +
                    "    p.preco_vista precovenda,\n" +
                    "    p.ncm,\n" +
                    "    pc.situacao_tributaria_pis piscofins_saida,\n" +
                    "    pc.situacao_tributaria_cfe icms_cst,\n" +
                    "    pc.cfe_cst_percentual icms_aliquota,\n" +
                    "    pc.reducao_bc_icms_cfe icms_reduzido\n" +
                    "from\n" +
                    "    produto p\n" +
                    "    join produto_complemento pc on\n" +
                    "        pc.cod_produto = p.cod_produto and\n" +
                    "        pc.cod_empresa = p.cod_empresa\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdemb_cotacao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    
    
}
