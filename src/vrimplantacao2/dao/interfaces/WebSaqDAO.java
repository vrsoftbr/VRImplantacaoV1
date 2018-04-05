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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class WebSaqDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "WebSaq";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "m1.coddepto cod_m1, m1.nome desc_m1,\n"
                    + "m2.codgrupo cod_m2, m2.descricao desc_m2,\n"
                    + "m3.codsubgrupo cod_m3, m3.descricao desc_m3\n"
                    + "from departamento m1\n"
                    + "inner join grupoprod m2 on m2.coddepto = m1.coddepto\n"
                    + "inner join subgrupo m3 on m3.codgrupo = m2.codgrupo\n"
                    + "order by m1.coddepto, m2.codgrupo, m3.codsubgrupo"
            )) {
                MercadologicoIMP imp = new MercadologicoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setMerc1ID(rst.getString("cod_m1"));
                imp.setMerc1Descricao(rst.getString("desc_m1"));
                imp.setMerc2ID(rst.getString("cod_m2"));
                imp.setMerc2Descricao(rst.getString("desc_m2"));
                imp.setMerc3ID(rst.getString("cod_m3"));
                imp.setMerc3Descricao(rst.getString("desc_m3"));
                result.add(imp);
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
                    + "p.codproduto,\n"
                    + "p.descricao,\n"
                    + "p.descricaofiscal,\n"
                    + "p.coddepto,\n"
                    + "p.codgrupo,\n"
                    + "p.codsubgrupo,\n"
                    + "p.codsimilar,\n"
                    + "p.estminimo,\n"
                    + "p.estmaximo,\n"
                    + "p.pesoliq,\n"
                    + "p.pesobruto,\n"
                    + "p.pesado,\n"
                    + "p.foralinha,\n"
                    + "p.qtdeetiq,\n"
                    + "p.diasvalidade,\n"
                    + "p.pesounid,\n"
                    + "p.vasilhame,\n"
                    + "p.codvasilhame,\n"
                    + "p.codfamilia,\n"
                    + "p.custotab,\n"
                    + "p.precoatc,\n"
                    + "p.precovrj,\n"
                    + "p.margematc,\n"
                    + "p.margemvrj,\n"
                    + "p.datainclusao,\n"
                    + "p.custorep,\n"
                    + "p.altura,\n"
                    + "p.largura,\n"
                    + "p.enviarecommerce,\n"
                    + "p.comprimento,\n"
                    + "p.cest,\n"
                    + "u.sigla,\n"
                    + "e.quantidade,\n"
                    + "pcs.codcst cstpiscofinssaida,\n"
                    + "pce.codcst cstpiscofinsentrada,\n"
                    + "p.natreceita,\n"
                    + "ncm.codigoncm,\n"
                    + "p.codcfpdv\n"
                    + "from produto p \n"
                    + "left join embalagem e on e.codembal = p.codembalvda\n"
                    + "inner join unidade u on u.codunidade = e.codunidade\n"
                    + "left join piscofins pcs on pcs.codpiscofins = p.codpiscofinssai\n"
                    + "left join piscofins pce on pce.codpiscofins = p.codpiscofinsent\n"
                    + "left join ncm on ncm.idncm = p.idncm\n"
                    + "order by p.codproduto"
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codproduto, \n"
                    + "codean, \n"
                    + "quantidade \n"
                    + "from produtoean \n"
                    + "order by codproduto"
            )) {

            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {

            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codproduto, \n"
                    + "codfornec, \n"
                    + "reffornec,\n"
                    + "principal \n"
                    + "from prodfornec\n"
                    + "order by principal desc"
            )) {

            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "c.codcliente,\n"
                    + "c.nome,\n"
                    + "c.razaosocial,\n"
                    + "c.enderfat,\n"
                    + "c.bairrofat,\n"
                    + "c.cepfat,\n"
                    + "c.codcidadefat,\n"
                    + "c.uffat,\n"
                    + "c.enderent,\n"
                    + "c.bairroent,\n"
                    + "c.cepent,\n"
                    + "c.codcidadeent,\n"
                    + "c.ufent,\n"
                    + "c.contato,\n"
                    + "c.site,\n"
                    + "c.email,\n"
                    + "c.tppessoa,\n"
                    + "c.cpfcnpj,\n"
                    + "c.rgie,\n"
                    + "c.observacao,\n"
                    + "c.dtnascto,\n"
                    + "c.sexo,\n"
                    + "c.estcivil,\n"
                    + "c.tipomoradia,\n"
                    + "c.dtmoradia,\n"
                    + "c.enderres,\n"
                    + "c.bairrores,\n"
                    + "c.cepres,\n"
                    + "c.codcidaderes,\n"
                    + "c.ufres,\n"
                    + "c.nomeconj,\n"
                    + "c.cpfconj,\n"
                    + "c.rgconj,\n"
                    + "c.salarioconj,\n"
                    + "c.foneres,\n"
                    + "c.celular,\n"
                    + "c.fonefat,\n"
                    + "c.faxfat,\n"
                    + "c.foneent,\n"
                    + "c.faxent,\n"
                    + "c.dtinclusao,\n"
                    + "c.salario,\n"
                    + "c.senha,\n"
                    + "c.numerofat,\n"
                    + "c.complementofat,\n"
                    + "c.numeroent,\n"
                    + "c.complementoent,\n"
                    + "c.numerores,\n"
                    + "c.complementores,\n"
                    + "(coalesce(c.limite1, 0) + coalesce(c.limite2) - coalesce(c.debito1, 0) - coalesce(debito2, 0)) as valorlimite,\n"
                    + "c.emailnfe,\n"
                    + "c.rgemissor,\n"
                    + "c.codstatus,\n"
                    + "s.descricao,\n"
                    + "s.bloqueado\n"
                    + "from cliente c\n"
                    + "inner join statuscliente s on s.codstatus = c.codstatus\n"
                    + "order by c.codcliente"
            )) {

            }
        }
        return null;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
