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
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class LiteciDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Liteci";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from tbsecao order by codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
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
                    + "p.coditem,\n"
                    + "p.codbarra,\n"
                    + "p.codbalanca,\n"
                    + "p.debalanca,\n"
                    + "p.pesound,\n"
                    + "p.descricao,\n"
                    + "p.descabrev,\n"
                    + "p.validade,\n"
                    + "p.codundi as tipoembalagem,\n"
                    + "p.codgrupoi,\n"
                    + "p.codsubgrupoi,\n"
                    + "p.codsecaoi,\n"
                    + "p.codfamiliaprodutoi,\n"
                    + "p.dtcad,\n"
                    + "p.pesobruto,\n"
                    + "p.pesoliquido,\n"
                    + "p.valorultcompra as custo,\n"
                    + "p.valor as precovenda,\n"
                    + "p.qtdminima as estminimo,\n"
                    + "est.qtddisponivel,\n"
                    + "p.ativo,\n"
                    + "ncm.codncm,\n"
                    + "ncm.cstpis,\n"
                    + "ncm.cstcofins,\n"
                    + "ncm.cstpisentrada,\n"
                    + "ncm.cstcofinsentrada,\n"
                    + "ncm.codnaturezareceita,\n"
                    + "p.cest_st,\n"
                    + "ncm.csticms,\n"
                    + "ncm.taxaicms\n"
                    + "from tbitem p\n"
                    + "left join tbncm ncm on ncm.chave = p.codncm\n"
                    + "left join tbestoque est on est.coditemi = p.coditem\n"
                    + "where est.codfiliali = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
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
                    + "f.codfor,\n"
                    + "f.nome as razao,\n"
                    + "f.apelidio as fantasia,\n"
                    + "f.endereco,\n"
                    + "f.numero,\n"
                    + "f.complemento,\n"
                    + "f.cep,\n"
                    + "f.bairro,\n"
                    + "f.uf,\n"
                    + "f.cidade,\n"
                    + "cid.descricao municipio,\n"
                    + "cid.codibge municipio_ibge,\n"
                    + "cid.codufibge as uf_ibge,\n"
                    + "f.fone,\n"
                    + "f.fax,\n"
                    + "f.celular,\n"
                    + "f.celularcotacao,\n"
                    + "f.email,\n"
                    + "f.emailcotacao,\n"
                    + "f.rg,\n"
                    + "f.cnpj,\n"
                    + "f.dtcad,\n"
                    + "f.obsgerais,\n"
                    + "f.contato\n"
                    + "from tbfor f\n"
                    + "left join tbcidade cid on cid.codigo = f.codcidadei"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "coditemi as idproduto,\n"
                    + "codfori as idfornecedor,\n"
                    + "coditemforn as codexterno,\n"
                    + "qtdentrada as qtdembalagem\n"
                    + "from tbitemcodforn\n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "c.codcli,\n"
                    + "c.nome as razao,\n"
                    + "c.apelidio as fantasia,\n"
                    + "c.cpf,\n"
                    + "c.rg,\n"
                    + "c.orgemissorrg,\n"
                    + "c.dtcad,\n"
                    + "c.endereco,\n"
                    + "c.numero,\n"
                    + "c.complemento,\n"
                    + "c.bairro,\n"
                    + "cid.descricao as municipio,\n"
                    + "cid.codibge as municipio_ibge,\n"
                    + "cid.codufibge as uf_ibge,\n"
                    + "c.uf,\n"
                    + "c.cep,\n"
                    + "c.fone,\n"
                    + "c.fax,\n"
                    + "c.celular,\n"
                    + "c.email,\n"
                    + "c.renda,\n"
                    + "c.limite,\n"
                    + "c.dtnasc,\n"
                    + "c.obs,\n"
                    + "c.ativo,\n"
                    + "c.pai,\n"
                    + "c.mae,\n"
                    + "c.endentrega,\n"
                    + "c.numendentrega,\n"
                    + "c.complentrega,\n"
                    + "c.bairroentrega,\n"
                    + "c.cepentrega,\n"
                    + "c.codcidadeentrega,\n"
                    + "cide.descricao as municipio_ent,\n"
                    + "cide.codibge as municipio_ibge_ent,\n"
                    + "cide.codufibge as uf_ibge_ent,\n"
                    + "c.ufentrega as uf_ent,\n"
                    + "c.endcobranca,\n"
                    + "c.numendcobranca,\n"
                    + "c.complcobranca,\n"
                    + "c.cepcobranca,\n"
                    + "c.bairrocobranca,\n"
                    + "cidb.descricao as municipio_cob,\n"
                    + "cidb.codibge as municipio_ibge_cob,\n"
                    + "cidb.codufibge as uf_ibge_cob,\n"
                    + "c.ufcobranca as uf_cobranca,\n"
                    + "c.nomeempresatrabalho,\n"
                    + "c.contatoempresatrabalho,\n"
                    + "c.foneempresatrabalho,\n"
                    + "c.funcaotrabalho,\n"
                    + "c.sexo\n"
                    + "from tbcli c\n"
                    + "left join tbcidade cid on cid.codigo = c.codcidadei\n"
                    + "left join tbcidade cide on cide.codigo = c.codcidadeentrega\n"
                    + "left join tbcidade cidb on cidb.codigo = c.codcidadecobranca\n"
                    + "order by c.codcli"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                }
            }
        }
        return null;
    }
}
