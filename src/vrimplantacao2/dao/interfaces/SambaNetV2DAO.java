/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class SambaNetV2DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SambaNet";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	CODALIQ as id, \n"
                    + "	coalesce(VALORTRIB, 0) as valor,\n"
                    + "	coalesce(ALIQUOTA, 0) as aliquota,\n"
                    + "	coalesce(REDUCAO, 0) as reducao,\n"
                    + "	DESCRICAO as descricao\n"
                    + "from dbo.ALIQUOTA_ICMS"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            0,
                            rst.getDouble("aliquota"),
                            rst.getDouble("reduzido")
                    ));
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
                    + "	p.CODPROD as idproduto,\n"
                    + "	p.CODPROD_SAMBANET as idproduto_sambanet,\n"
                    + "	p.BARRA as ean,\n"
                    + "	p.UNIDADE as tipoembalagem,\n"
                    + "	coalesce(p.CODSETOR, 0) as balanca,\n"
                    + "	p.VALIDADE as validade,\n"
                    + "	p.CODCRECEITA as merc1,\n"
                    + "	p.CODGRUPO as merc2,\n"
                    + "	p.DESCRICAO as descricaocompleta,\n"
                    + "	p.DESC_PDV as descricaoreduzida,\n"
                    + "	p.ESTOQUE as estoque,\n"
                    + "	p.ESTOQUE_MIN as estoque_minimo,\n"
                    + "	p.ESTOQUE_MAX as estoque_maximo,\n"
                    + "	p.PRECO_CUST as custo,\n"
                    + "	p.PRECO_UNIT as precovenda,\n"
                    + "	p.PESO_BRUTO as pesobruto,\n"
                    + "	p.PESO_LIQ as pesoliquido,\n"
                    + "	p.ATIVO as situacaocadastro,\n"
                    + "	coalesce(p.DESATIVACOMPRA, 0) as descontinuado,\n"
                    + "	p.CODNCM as ncm,\n"
                    + "	p.CODCEST as cest,\n"
                    + "	p.CST_PISSAIDA,\n"
                    + "	p.CST_PISENTRADA,\n"
                    + "	p.CST_COFINSSAIDA,\n"
                    + "	p.CST_COFINSENTRADA,\n"
                    + "	p.NAT_REC as naturezareceita,\n"
                    + "	p.CODTRIB as cst,\n"
                    + "	p.CODTRIB_ENT as cst_entrada,\n"
                    + "	p.CODALIQ as aliquota,\n"
                    + "	p.CODALIQ_NF as aliquota_nf,\n"
                    + "	p.ALIQICMS_INTER as aliquota_inter,\n"
                    + "	p.ALIQICMSDESONERADO as aliquota_deso,\n"
                    + "	p.ALIQSUBTRIB as aliquota_sub,\n"
                    + "	p.ALIQUOTA_IBPT as aliquota_ibpt,\n"
                    + "	p.ALIQUOTA_IBPTEST as aliquota_ibptest,\n"
                    + "	p.ALIQUOTA_IBPTMUN as aliquota_ibptmun,\n"
                    + "	p.PER_REDUC as reducao,\n"
                    + "	p.PER_REDUC_ENT as reducao_entrada\n"
                    + "from PRODUTOS p\n"
                    + "order by CODPROD"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto_sambanet"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescontinuado(rst.getInt("descontinuado") != 0);
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CST_PISSAIDA"));
                    imp.setPiscofinsCstCredito(rst.getString("CST_PISENTRADA"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsDebitoId(rst.getString("aliquota"));
                    imp.setIcmsCreditoId(rst.getString("aliquota"));
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
                    "select "
                    + "CODPROD, "
                    + "BARRA "
                    + "from ALTERNATIVO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODPROD"));
                    imp.setEan(rst.getString("BARRA"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
