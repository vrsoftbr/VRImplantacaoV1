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
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class CerebroDAO extends InterfaceDAO {

    public String complSistema = "";
    
    @Override
    public String getSistema() {
        if ((complSistema != null) && (!complSistema.trim().isEmpty())) {
            return "Cerebro" + complSistema;
        } else {
            return "Cerebro";
        }
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo_empresa, descricao, cpf_cnpj from empresas\n"
                    + "order by codigo_empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo_empresa"), rst.getString("descricao")));
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
                    + "m1.codigo_grupo as merc1,\n"
                    + "m1.descricao as desc_merc1,\n"
                    + "coalesce(m2.codigo_subgrupo, '1') as merc2,\n"
                    + "coalesce(m2.descricao, m1.descricao) as desc_merc2,\n"
                    + "'1' as merc3,\n"
                    + "coalesce(m2.descricao, m1.descricao) as desc_merc3\n"
                    + "from grupos_produto m1\n"
                    + "left join subgrupos_produto m2\n"
                    + "    on m2.codigo_grupo = m1.codigo_grupo\n"
                    + "order by m1.codigo_grupo,  m2.codigo_subgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
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
                    + "p.codigo_produto, \n"
                    + "p.pesavel,\n"
                    + "p.codigo_barra,\n"
                    + "p.codigo_grupo,\n"
                    + "p.codigo_subgrupo, \n"
                    + "p.descricao, \n"
                    + "p.preco_venda,\n"
                    + "p.custo_atual,\n"
                    + "p.unidade_saida,\n"
                    + "p.validade,\n"
                    + "p.peso,\n"
                    + "p.codigo_tributo,\n"
                    + "p.status,\n"
                    + "p.cst,\n"
                    + "t.codigo_tributo as cod_trib,\n"
                    + "t.descricao as icms_desc,\n"
                    + "t.cst_icms as cst_icms_saida,\n"
                    + "t.icms_saida as icms_saida,\n"
                    + "t.reducao_saida as red_saida,\n"
                    + "t.cst_icms_ent as cst_icms_ent,\n"
                    + "t.icms_entrada as icms_ent,\n"
                    + "t.reducao_entrada as red_ent,\n"
                    + "p.cst_pis,\n"
                    + "p.cst_cofins,\n"
                    + "p.cst_pis_ent,\n"
                    + "p.cst_cofins_ent,\n"
                    + "p.ncm, \n"
                    + "p.cest\n"
                    + "from produtos p\n"
                    + "left join tributos t on t.codigo_tributo = p.codigo_tributo\n"
                    + "order by codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.seteBalanca("T".equals(rst.getString("pesavel")));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade_saida"));
                    imp.setCodMercadologico1(rst.getString("codigo_grupo"));
                    imp.setCodMercadologico2(rst.getString("codigo_subgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setCustoComImposto(rst.getDouble("custo_atual"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins_ent"));
                    imp.setIcmsCstSaida(rst.getInt("cst_icms_saida"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_saida"));
                    imp.setIcmsReducaoSaida(rst.getDouble("red_saida"));
                    imp.setIcmsCstEntrada(rst.getInt("cst_icms_ent"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_ent"));
                    imp.setIcmsReducao(rst.getDouble("red_ent"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_produto,\n"
                    + "codigo_barra,\n"
                    + "quantidade\n"
                    + "from produtos_codigo\n"
                    + "order by codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_produto"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "with mov as\n"
                        + "(\n"
                        + "  select codigo_produto, max(ultimo_movimento) as data\n"
                        + "  from produtos_saldo\n"
                        + "  where codigo_almoxarifado = 1\n"
                        + "  group by codigo_produto\n"
                        + ")\n"
                        + "select e.codigo_produto,\n"
                        + "       e.saldo_atual,\n"
                        + "       e.saldo_minimo,\n"
                        + "       e.saldo_maximo,\n"
                        + "       e.ultimo_movimento\n"
                        + "from produtos_saldo e\n"
                        + "inner join mov as mov2 on mov2.codigo_produto = e.codigo_produto\n"
                        + "where e.codigo_almoxarifado = 1"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_produto"));
                        imp.setEstoque(rst.getDouble("saldo_atual"));
                        imp.setEstoqueMaximo(rst.getDouble("saldo_maximo"));
                        imp.setEstoqueMinimo(rst.getDouble("saldo_minimo"));
                        result.add(imp);
                    }
                }
                return result;
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
                    + "codigo_fornecedor,\n"
                    + "descricao,\n"
                    + "razao_social,\n"
                    + "nome_fantasia,\n"
                    + "endereco,\n"
                    + "numero,\n"
                    + "bairro,\n"
                    + "cidade,\n"
                    + "estado,\n"
                    + "cep,\n"
                    + "cpf_cnpj,\n"
                    + "inscricao_estadual,\n"
                    + "contato,\n"
                    + "telefone,\n"
                    + "telefone2,\n"
                    + "fax,\n"
                    + "celular,\n"
                    + "email,\n"
                    + "observacao,\n"
                    + "prazo_pagto,\n"
                    + "data_cadastro,\n"
                    + "desativado\n"
                    + "from fornecedores\n"
                    + "order by codigo_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_fornecedor"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpf_cnpj"));
                    imp.setIe_rg(rst.getString("inscricao_estadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDatacadastro(rst.getDate("data_cadastro"));
                    imp.setAtivo("F".equals(rst.getString("desativado")));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "codigo_fornecedor,\n"
                    + "codigo_produto\n"
                    + "ultima_data,\n"
                    + "ultimo_custo,\n"
                    + "referencia\n"
                    + "from produtos_fornecedor\n"
                    + "order by codigo_fornecedor, codigo_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codigo_produto"));
                    imp.setIdFornecedor(rst.getString("codigo_fornecedor"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    imp.setDataAlteracao(rst.getDate("ultima_data"));
                    imp.setCustoTabela(rst.getDouble("ultimo_custo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
