package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class PrimeDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Prime";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
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
                OpcaoProduto.ESTOQUE_MINIMO,
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
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.MAPA_TRIBUTACAO
        ));
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'E'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    clat_simbicms as id,\n"
                    + "    clat_descricao as descricao,\n"
                    + "    clat_cst as cst,\n"
                    + "    clat_icms as icms,\n"
                    + "    clat_redbcicms as reducao\n"
                    + "from classtrib\n"
                    + "where clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "and clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    empr_codigo as id,\n"
                    + "    empr_nomereduzido as nome,\n"
                    + "    empr_cnpjcpf as cnpj\n"
                    + "from empresas order by 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("id"),
                                    rst.getString("nome") + "-" + rst.getString("cnpj")
                            )
                    );
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    p.cadp_codigo as id,\n"
                    + "    p.cadp_balanca as balanca,\n"
                    + "    p.cadp_codigobarra as ean,\n"
                    + "    p.cadp_descricaounmedida as tipoembalagem,\n"
                    + "    p.cadp_situacao as situacaocadastro,\n"
                    + "    p.cadp_descricao as descricaocompleta,\n"
                    + "    p.cadp_descricaoreduzida as descricaoreduzida,\n"
                    + "    p.cadp_codcategoria,\n"
                    + "    p.cadp_categoria,\n"
                    + "    p.cadp_dtcadastro as datacadastro,\n"
                    + "    p.cadp_dtalteracao as dataalteracao,\n"
                    + "    p.cadp_codigoncm as ncm,\n"
                    + "    p.cadp_cest as cest,\n"
                    + "    p.cadp_cstpise as cstpisentrada,\n"
                    + "    p.cadp_cstpiss as cstpissaida,\n"
                    + "    pe.cade_codclassificacaoe,\n"
                    + "    cle.clat_cst as csticmsentrada,\n"
                    + "    cle.clat_icms as aliqicmsentrada,\n"
                    + "    cle.clat_redbcicms as redicmsentrada,\n"
                    + "    pe.cade_codclassificacaos,\n"
                    + "    cls.clat_cst as csticmsentrada,\n"
                    + "    cls.clat_icms as aliqicmsentrada,\n"
                    + "    cls.clat_redbcicms as redicmsentrada,\n"
                    + "    pe.cade_estmin as estoqueminimo,\n"
                    + "    pe.cade_estmax as estoquemaximo,\n"
                    + "    pe.cade_qemb as qtdembalagem, \n"
                    + "    pe.cade_margemcontribmin as margem,\n"
                    + "    pe.cade_prvenda as precovenda,\n"
                    + "    pe.cade_ctnota as custo\n"
                    + "from cadprod p\n"
                    + "left join cadprodemp pe on pe.cade_codigo = p.cadp_codigo\n"
                    + "	and pe.cade_codempresa = '" + getLojaOrigem() + "'\n"
                    + "left join classtrib cle on cle.clat_codsimb = pe.cade_codclassificacaoe\n"
                    + "	and cle.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cle.clat_es = 'E'\n"
                    + "left join classtrib cls on cls.clat_codsimb = pe.cade_codclassificacaos\n"
                    + "	and cls.clat_uf = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "	and cls.clat_es = 'S'\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca(!"N".equals(rst.getString("balanca")));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpissaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisentrada"));
                    
                    imp.setIcmsDebitoId(rst.getString("cade_codclassificacaos"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    
                    imp.setIcmsCreditoId(rst.getString("cade_codclassificacaoe"));
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());
                    
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    codb_codprod as idproduto,\n"
                    + "    codb_codbarra as ean\n"
                    + "from codigosbarra\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    result.add(imp);
                }
            }
        }

        return result;
    }
}
