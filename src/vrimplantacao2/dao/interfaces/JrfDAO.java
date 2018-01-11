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
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class JrfDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Jrf";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "idfamilia, familia \n"
                    + "from \n"
                    + "retaguarda.cad_familia "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idfamilia"));
                    imp.setDescricao(rst.getString("familia"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "p.idproduto, p.desccompleta, p.descresumida, p.descetiqueta, "
                    + "p.diasvalidade, p.status, p.idfamilia,\n"
                    + "f.codncm, f.cstpis_e, f.cstpis_s, f.natreceitacofins, f.codcest,\n"
                    + "i.svc_cst, i.svc_alq, svc_rbc,\n"
                    + "pr.idempresa, pr.preconormal, pr.margem,\n"
                    + "e.embalagem, e.quantidade, e.codigo ean\n"
                    + "from "
                    + "retaguarda.cad_produto p\n"
                    + "left join "
                    + "retaguarda.cad_prodfigurafiscal f on f.idproduto = p.idproduto\n"
                    + "left join "
                    + "integracao.mxf_vw_icms i on i.codigo_produto = p.idproduto\n"
                    + "left join "
                    + "retaguarda.cad_prodemppreco pr on pr.idproduto = p.idproduto and pr.idempresa = " + getLojaOrigem() + "\n"
                    + "left join "
                    + "retaguarda.vw_prodcodigo e on e.idproduto = p.idproduto\n"
                    + "order by "
                    + "p.idproduto, e.codigo, e.quantidade"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("desccompleta"));
                    imp.setDescricaoReduzida(rst.getString("descresumida"));
                    imp.setDescricaoGondola(rst.getString("descetiqueta"));
                    imp.setValidade(rst.getInt("diasvalidade"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("status")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setIdFamiliaProduto(rst.getString("idfamilia"));
                    imp.setNcm(rst.getString("codncm"));
                    imp.setCest(rst.getString("codcest"));
                    imp.setPiscofinsCstDebito(rst.getInt("cstpis_s"));
                    imp.setPiscofinsCstCredito(rst.getInt("cstpis_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("natreceitacofins"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("preconormal"));
                    imp.setIcmsCst(rst.getInt("svc_cst"));
                    imp.setIcmsAliq(rst.getDouble("svc_alq"));
                    imp.setIcmsReducao(rst.getDouble("svc_rbc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
}
