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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class InfoBrasilDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "InfoBrasil";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select loj_codigo, loj_nome, loj_cnpj from lojas order by loj_codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("loj_codigo"), rst.getString("loj_nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    s.sec_codigo merc1,\n"
                    + "    s.sec_descricao merc1_desc,\n"
                    + "    coalesce(g.gru_codigo, 1) merc2,\n"
                    + "    coalesce(g.gru_descricao, s.sec_descricao) merc2_desc,\n"
                    + "    '1' merc3,\n"
                    + "    coalesce(g.gru_descricao, s.sec_descricao) merc3_desc\n"
                    + "from\n"
                    + "    seccao s\n"
                    + "left join grupospro g on s.sec_codigo = g.sec_codigo\n"
                    + "order by s.sec_codigo, g.gru_codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "   p.pro_codigo id,\n"
                    + "   p.pro_codigo ean,\n"
                    + "   p.pro_descricao descricaocompleta,\n"
                    + "   p.pro_descfiscal descricaoreduzida,\n"
                    + "   p.pro_descricao descricaogondola,\n"
                    + "   case pro_situacao when 'I' then 0 else 1 end as id_situacaocadastro,\n"
                    + "   p.pro_datacadastro datacadastro,\n"
                    + "   g.sec_codigo mercadologico1,\n"
                    + "   p.gru_codigo mercadologico2,\n"
                    + "   '1' as mercadologico3,\n"
                    + "   p.pro_ncm ncm,\n"
                    + "   0 margem,\n"
                    + "   p.pro_datacadastro,\n"
                    + "   case p.pro_balanca when 'S' then 1 else 0 end as e_balanca,\n"
                    + "   p.pro_val,\n"
                    + "   p.pro_unidade,\n"
                    + "   p.pro_peso pesobruto,\n"
                    + "   p.pis_codigo piscofins_cst_sai,\n"
                    + "   p.pis_codigoent piscofins_cst_ent,\n"
                    + "   null piscofins_natrec,\n"
                    + "   p.pro_prccusto custo,\n"
                    + "   p.pro_prcvenda1 preco,\n"
                    + "   p.pro_estminimo estoque_min,\n"
                    + "   p.pro_estmaximo estoque_max,\n"
                    + "   e.est_apoio estoque,\n"
                    + "   pis_s.pis_codigo cst_pis_s,\n"
                    + "   pis_e.pis_codigo cst_pis_e,\n"
                    + "   p.pro_icms icms_aliq,\n"
                    + "   p.pro_reducaoicms icms_reducao\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "left join grupospro g on p.gru_codigo = g.gru_codigo\n"
                    + "join estoque e on e.pro_codigo = p.pro_codigo and e.loj_codigo = " + getLojaOrigem() + "\n"
                    + "left join cst_pis pis_s on pis_s.pis_codigo = p.pis_codigo\n"
                    + "left join cst_pis pis_e on pis_e.pis_codigo = p.pis_codigoent\n"
                    + "order by\n"
                    + "    p.pro_codigo"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select pro_codigo, pra_codigo from prod_agregados\n"
                    + "order by pro_codigo"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "   f.cre_codigo id,\n"
                    + "   f.cre_datacadastro datacadastro,\n"
                    + "   f.cre_nome razao,\n"
                    + "   f.cre_fantasia fantasia,\n"
                    + "   f.cre_endereco endereco,\n"
                    + "   f.cre_numero numero,\n"
                    + "   f.cre_compl_endereco complemento,\n"
                    + "   f.cre_bairro bairro,\n"
                    + "   f.mun_codigo cidade,\n"
                    + "   f.cre_uf estado,\n"
                    + "   f.cre_cep cep,\n"
                    + "   f.cre_fone fone1,\n"
                    + "   f.cre_fonerep fone2,\n"
                    + "   f.cre_celular celular,\n"
                    + "   f.cre_celularrep,\n"
                    + "   f.cre_cgf inscricaoestadual,\n"
                    + "   f.cre_cnpj cnpj,\n"
                    + "   f.cre_email email,\n"
                    + "   case f.cre_situacao when 'I' then 1 else 0 end as bloqueado\n"
                    + "from\n"
                    + "   credores f\n"
                    + "order by\n"
                    + "   f.cre_codigo"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
