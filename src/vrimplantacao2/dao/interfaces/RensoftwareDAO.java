package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class RensoftwareDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Rensoftware";
        } else {
            return "Rensoftware - " + complemento;
        }
    }

    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODIGO, NOME from empresa order by codigo"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(rst.getString("codigo"), rst.getString("nome"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO
        ));
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.CODIGO id,\n" +
                    "	coalesce(dt.DATA_INC, p.DATA_ALTERACAO) datacadastro,\n" +
                    "	coalesce(dt.data_alt, p.DATA_ALTERACAO) dataalteracao,\n" +
                    "	coalesce(pxc.CODIGOBARRAS, p.cbarra, p.cbarra2, p.cbarra3) ean,\n" +
                    "	coalesce(pxc.fator, p.embalagem) qtdembalagem,\n" +
                    "	coalesce(pxc.UNIDADE, p.unidade) unidadevenda,\n" +
                    "	p.EMBALAGEM qtdembalagemcotacao,\n" +
                    "	p.QTD_VOLUMES qtdembalagem,\n" +
                    "	p.BALANCA balanca,\n" +
                    "	p.VALIDIAS validade,\n" +
                    "	p.NOME descricaocompleta,\n" +
                    "	p.peso pesobruto,\n" +
                    "	p.PESO_LIQ pesoliquido,\n" +
                    "	pl.EST_MINIM estoqueminimo,\n" +
                    "	pl.EST_LOJA estoque,\n" +
                    "	pl.FORNECEDOR id_fabricante,\n" +
                    "	pl.PCO_COMPRA,\n" +
                    "	coalesce(pxl.PRECOSISTEMA, pl.PCO_VENDA) preco,\n" +
                    "	coalesce(pxc.ATIVO, 'N') ativo,\n" +
                    "	p.CODIGO_NCM ncm,\n" +
                    "	p.cod_cest cest,\n" +
                    "	p.COD_CSTPIS pis_saida,\n" +
                    "	p.COD_CSTPIS_ENTRADA pis_entrada,\n" +
                    "	pl.COD_FIG_FISCAL_ent icms_entrada_id,\n" +
                    "	pl.COD_FIG_FISCAL_sai icms_saida_id,\n" +
                    "	pl.PER_IVA iva,	\n" +
                    "	pxl.PRECOVENDA precoatacado\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "	join EMPRESA e on\n" +
                    "		e.CODIGO = " + getLojaOrigem() + "\n" +
                    "	left join PRODEXPL_CADASTRO PXC on\n" +
                    "		pxc.CODIGOPRODUTO = p.CODIGO\n" +
                    "	left join PRODEXPL_CADASTROLOJA pxl on\n" +
                    "		pxl.CODIGOPRODUTO = pxc.CODIGOPRODUTO and\n" +
                    "		pxl.ITEM = pxc.ITEM and\n" +
                    "		pxl.CODLOJA = e.codigo		\n" +
                    "	left join produtos_data dt on\n" +
                    "		dt.codigo_produto = p.codigo\n" +
                    "	join PRODLOJAS pl on\n" +
                    "		p.codigo = pl.CODIGO and\n" +
                    "		p.CODLOJA = e.CODIGO\n" +
                    "order by\n" +
                    "	p.CODIGO"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidadevenda"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagemcotacao"));
                    imp.setTipoEmbalagemCotacao(rs.getString("qtdembalagem"));
                    imp.seteBalanca("S".equals(rs.getString("balanca")));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setFornecedorFabricante(rs.getString("id_fabricante"));
                    imp.setCustoComImposto(rs.getDouble("PCO_COMPRA"));
                    imp.setCustoSemImposto(rs.getDouble("PCO_COMPRA"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setSituacaoCadastro("S".equals(rs.getString("ATIVO")) ? 1 : 0);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("pis_saida"));
                    imp.setPiscofinsCstCredito(rs.getString("pis_entrada"));
                    imp.setIcmsCreditoId(rs.getString("icms_entrada_id"));
                    imp.setIcmsDebitoId(rs.getString("icms_saida_id"));
                    imp.setAtacadoPreco(rs.getDouble("precoatacado"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	CODIGO_CST_PADRAO cst\n" +
                    "from\n" +
                    "	figurafiscal\n" +
                    "order by\n" +
                    "	codigo"
            )) {
                while (rs.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rs.getString("codigo"), 
                                    rs.getString("nome"), 
                                    rs.getInt("cst"), 
                                    0, 
                                    0
                            )
                    );
                }
            }
        }
        
        return result;
    }
    
}
