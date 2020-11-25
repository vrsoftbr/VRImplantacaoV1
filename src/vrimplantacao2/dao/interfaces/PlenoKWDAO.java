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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class PlenoKWDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "PlenoKW";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cfg06_id as id,\n"
                    + "	cfg06_nome as nome\n"
                    + "from cfg06_filial cf \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("id"), rst.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.mcd01_id,\n"
                    + "	p.mcd01_codint,\n"
                    + "	ean.mcd02_codigo as codigobarras,\n"
                    + "	ean.mcd02_multiplicador as qtdembalagem,\n"
                    + "	un.adm01_unidade as tipoembalagem,\n"
                    + "	p.mcd01_flgbalanca as balanca,\n"
                    + "	p.mcd01_validade as validade,\n"
                    + "	p.mcd01_descricao as descricaocompleta,\n"
                    + "	p.mcd01_descricao_curta as descricaoreduzida,	\n"
                    + "	p.mcd01_flgativa as situacaocadastro,\n"
                    + "	p.mcd01_flgsazonal as sazonal,\n"
                    + "	pl.mcd03_flgativa_compra as descontinuado,\n"
                    + "	pl.mcd03_flgativa_venda as vendapdv,\n"
                    + "	p.mcd01_data_inclusao as datacadastro,\n"
                    + "	p.mcd01_flgativa as situacaocadastro,\n"
                    + "	p.mcd01_dthr_ultima_alteracao as dataalteracao,\n"
                    + "	pl.mcd03_qtdestoque_maximo as estoquemaximo,\n"
                    + "	est.est06_qtd as estoque,\n"
                    + "	est.est06_qtdtroca as estoquetroca,\n"
                    + "	p.mcd01_perc_margem_ideal as margem,\n"
                    + "	pl.mcd03_preco_venda as precovenda,\n"
                    + "	est.est06_vlrunit_ultnf as custo,\n"
                    + "	p.fis11_ncm_id as ncm_id,\n"
                    + "	ncm.fis11_codigo as ncm,\n"
                    + "	cest.fis15_codigo as cest,\n"
                    + "	cstpiss.fis08_codigo as piscofins_saida,\n"
                    + "	cstpise.fis08_codigo as piscofins_entrada,\n"
                    + "	pis.fis20_codigo_natureza_receita as naturezareceita,\n"
                    + "	cst.fis10_codigo as csticms,\n"
                    + "	pl.mcd03_perc_aliquota_efetiva_fcx,\n"
                    + "	pl.mcd03_perc_aliquota_normal_st,\n"
                    + "	pl.mcd03_perc_aliquota_original,\n"
                    + "	pl.mcd03_aliq_fcp as fcp,\n"
                    + "	pl.mcd03_cbenef as codigobeneficio,\n"
                    + "	iva.fse10_mva_interna as mva\n"
                    + "from mcd01_mercadoria p\n"
                    + "left join adm01_unidade_mercadoria un\n"
                    + "	on un.adm01_id = p.adm01_unidade_mercadoria_id \n"
                    + "left join mcd02_codigo_mercadoria ean\n"
                    + "	on ean.mcd01_mercadoria_id = p.mcd01_id\n"
                    + "left join fis11_ncm ncm\n"
                    + "	on ncm.fis11_id = p.fis11_ncm_id\n"
                    + "left join fis15_cest cest\n"
                    + "	on cest.fis15_id = p.fis15_cest_id \n"
                    + "left join mcd03_mercadoria_filial pl\n"
                    + "	on pl.mcd01_mercadoria_id = p.mcd01_id \n"
                    + "	and pl.cfg06_filial_id = " + getLojaOrigem() + "\n"
                    + "left join est06_estoque_atual est\n"
                    + "	on est.mcd03_mercadoria_filial_id = pl.mcd03_id\n"
                    + "left join fis20_regras_pis_cofins pis\n"
                    + "	on pis.fis11_ncm_id = ncm.fis11_id\n"
                    + "	and fis20_id = p.fis20_regras_pis_cofins_id \n"
                    + "left join fis08_cstpiscofins cstpise\n"
                    + "	on cstpise.fis08_codigo = pis.fis08_cstpiscofins_entrada_id \n"
                    + "	and cstpise.fis08_flgentrada = 1\n"
                    + "left join fis08_cstpiscofins cstpiss\n"
                    + "	on cstpiss.fis08_codigo = pis.fis08_cstpiscofins_saida_id \n"
                    + "	and cstpiss.fis08_flgentrada = 0\n"
                    + "left join fis10_csticms cst\n"
                    + "	on cst.fis10_id = pl.fis10_csticms_id_fcx\n"
                    + "left join fse10_valorbcst_mercadoria iva\n"
                    + "	on iva.mcd01_mercadoria_id = p.mcd01_id \n"
                    + "order by 1"
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.mcd01_id,\n"
                    + "	p.mcd01_codint,\n"
                    + "	ean.mcd02_codigo as codigobarras,\n"
                    + "	ean.mcd02_multiplicador as qtdembalagem,\n"
                    + "	un.adm01_unidade as tipoembalagem\n"
                    + "from mcd01_mercadoria p\n"
                    + "left join adm01_unidade_mercadoria un\n"
                    + "	on un.adm01_id = p.adm01_unidade_mercadoria_id \n"
                    + "left join mcd02_codigo_mercadoria ean\n"
                    + "	on ean.mcd01_mercadoria_id = p.mcd01_id\n"
                    + "order by 1"
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select	\n"
                    + "	f.com01_id as id,\n"
                    + "	pes.pes01_pessoa_fisica_id as pessoafisica,\n"
                    + "	pj.pes02_id as pessoajuridica, \n"
                    + "	pj.pes02_razao_social as razao,\n"
                    + "	pe.pes03_nome_fantasia as fantasia,\n"
                    + "	pf.pes01_nome as nomepessoa,\n"
                    + "	pf.pes01_cpf as cpf,\n"
                    + "	pe.pes03_cnpj as cnpj,\n"
                    + "	pes.pes04_inscricao_estadual as ie,\n"
                    + "	pe.pes03_inscricao_estadual,\n"
                    + "	pe.pes03_inscricao_municipal,\n"
                    + "	concat(upper(pes.pes04_tipo_logradouro), ' ', upper(pes.pes04_nome_logradouro))  as endereco,\n"
                    + "	pes.pes04_endereco_numero as numero,\n"
                    + "	pes.pes04_endereco_complemento as complemento,\n"
                    + "	pes.pes04_bairro as bairro,\n"
                    + "	mun.pes08_cidade as municipio,\n"
                    + "	mun.pes08_codigo_ibge_municipio as municipioibge,\n"
                    + "	uf.dom16_sigla as uf,\n"
                    + "	pes.pes04_cep as cep,\n"
                    + "	pes.pes04_fone as telefone,\n"
                    + "	pes.pes04_fax as fax,\n"
                    + "	pes.pes04_email as email,\n"
                    + "	pes.pes04_dthr_cadastro as datacadastro,\n"
                    + "	f.com01_prazo_pagto,\n"
                    + "	f.com01_prazo_entrega_pedido,\n"
                    + "	f.com01_flgativo as situacaocadastro,\n"
                    + "	f.com01_vlrminimo_pedido as pedidominimo,\n"
                    + "	f.com01_fone_comercial,\n"
                    + "	f.com01_fone_financeiro,\n"
                    + "	f.com01_fone_vendedor,\n"
                    + "	f.com01_email_financeiro,\n"
                    + "	f.com01_email_comercial,\n"
                    + "	f.com01_email_vendedor,\n"
                    + "	f.com01_dtcadastro as datacadastro,\n"
                    + "	pes.pes04_flgprodutor_rural as e_produtorural,\n"
                    + "	pes.pes04_flgtransportador as e_transportador\n"
                    + "from com01_fornecedor f\n"
                    + "join pes04_pessoa pes\n"
                    + "	on pes.pes04_id = f.pes04_pessoa_id \n"
                    + "left join dom16_uf uf\n"
                    + "	on uf.dom16_id = pes.dom16_uf_id\n"
                    + "left join pes08_ibge_municipio mun\n"
                    + "	on mun.pes08_id = pes.pes08_ibge_municipio_id\n"
                    + "left join pes03_estabelecimento pe \n"
                    + "	on pe.pes03_id = pes.pes03_estabelecimento_id\n"
                    + "left join pes02_pessoa_juridica pj 	\n"
                    + "	on pj.pes02_id = pe.pes02_pessoa_juridica_id\n"
                    + "left join pes01_pessoa_fisica pf 	\n"
                    + "	on pf.pes01_id = pes.pes01_pessoa_fisica_id\n"
                    + "order by 1"
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

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	mcd01_mercadoria_id as idproduto,\n"
                    + "	com01_fornecedor_id as idfornecedor,\n"
                    + "	com02_codmerc_fornecedor as codigoexterno,\n"
                    + "	com02_nrounid_embalagem as qtdembalagem,\n"
                    + "	com02_fator_conversao as fatorconversao\n"
                    + "from com02_mercadoria_fornecedor cmf \n"
                    + "order by 2, 1"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
