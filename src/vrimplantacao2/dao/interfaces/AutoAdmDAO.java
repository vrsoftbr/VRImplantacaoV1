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
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class AutoAdmDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "AutoADM";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.cd_produto as id,\n"
                    + "    b.cd_barra as ean,\n"
                    + "    um.sg_unidade_medida as tipoembalagem,\n"
                    + "    pm.qt_multiplicador as qtdembalagem,\n"
                    + "    um.ds_unidade_medida,\n"
                    + "    pm.qt_divisor,\n"
                    + "    pm.qt_embalagem,\n"
                    + "    p.fg_produto_balanca as balanca,\n"
                    + "    p.fg_unidade_peso as pesavel,\n"
                    + "    p.vl_validade as validade,\n"
                    + "    p.nr_dias_validade,\n"
                    + "    p.nm_produto as descricaocompleta,\n"
                    + "    p.nm_produto_reduzido as descricaoreduzida,\n"
                    + "    p.vl_venda as precovenda,\n"
                    + "    p.pe_margem as margem,\n"
                    + "    p.vl_custo as custo,\n"
                    + "    p.dt_inclusao as datacadastro,\n"
                    + "    p.dt_alteracao as dataalteracao,\n"
                    + "    p.fg_situacao as situacaocadastro,\n"
                    + "    p.nr_ncm as ncm,\n"
                    + "    cest.nr_cest as cest,\n"
                    + "    tp.cd_produto_tipo as tipoproduto,\n"
                    + "    tp.ds_tipo_produto as descricaotipoproduto,\n"
                    + "    pis_e.cd_governo as pisentrada,\n"
                    + "    pis_s.cd_governo as pissaida,\n"
                    + "    cof_e.cd_governo as cofinsentrada,\n"
                    + "    cof_s.cd_governo as cofinssaida,\n"
                    + "    nat.nr_natureza_receita as naturezareita,\n"
                    + "    tpg.nm_tributacao_produto_grupo,\n"
                    + "    icms.cd_governo as icmscst,\n"
                    + "    coalesce(tpro.pe_icms_dentro_estado, 0) as icmsaliq,\n"
                    + "    coalesce(tpro.pe_icms_fora_estado, 0) as icmsaliqforaestado,\n"
                    + "    coalesce(tpro.pe_reducao_base_icms, 0) as icmsreducao,\n"
                    + "    icms.fg_icms_obrigatorio,\n"
                    + "    icms.fg_isento,\n"
                    + "    icms.fg_naotributado,\n"
                    + "    icms.fg_substituicao_tributaria,\n"
                    + "    icms.fg_reducao_base_calculo,\n"
                    + "    icms.fg_st_embutido,\n"
                    + "    icms.fg_icms_simples\n"
                    + "from tb_produto p\n"
                    + "left join tb_produto_tipo tp on\n"
                    + "    tp.cd_produto_tipo = p.cd_produto_tipo\n"
                    + "left join tb_produto_unidade_medida pm\n"
                    + "    on pm.cd_produto = p.cd_produto\n"
                    + "left join tb_produto_codigo_barra b\n"
                    + "    on b.cd_produto_unidade_medida = pm.cd_produto_unidade_medida\n"
                    + "left join tb_unidade_medida um\n"
                    + "    on um.cd_unidade_medida = pm.cd_unidade_medida\n"
                    + "left join tb_tributacao_produto tpro\n"
                    + "    on tpro.cd_tributacao_produto = p.cd_tributacao_produto\n"
                    + "left join tb_tributacao_icms icms\n"
                    + "    on icms.cd_tributacao_icms = tpro.cd_tributacao_icms\n"
                    + "left join tb_tributacao_produto_grupo tpg\n"
                    + "    on tpg.cd_tributacao_produto_grupo = tpro.cd_tributacao_produto_grupo\n"
                    + "left join tb_cest cest\n"
                    + "    on cest.cd_cest = p.cd_cest\n"
                    + "left join tb_tributacao_pis_cofins pis_e\n"
                    + "    on pis_e.cd_tributacao_pis_cofins = p.cd_tributacao_pis_e\n"
                    + "left join tb_tributacao_pis_cofins pis_s\n"
                    + "    on pis_s.cd_tributacao_pis_cofins = p.cd_tributacao_pis_s\n"
                    + "left join tb_tributacao_pis_cofins cof_e\n"
                    + "    on cof_e.cd_tributacao_pis_cofins = p.cd_tributacao_cofins_e\n"
                    + "left join tb_tributacao_pis_cofins cof_s\n"
                    + "    on cof_s.cd_tributacao_pis_cofins = p.cd_tributacao_cofins_s\n"
                    + "left join tb_natureza_receita nat\n"
                    + "    on nat.cd_natureza_receita = p.cd_natureza_receita"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("pissaida"));
                    imp.setPiscofinsCstCredito(rst.getInt("cofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareita"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pe.cd_pessoa as id,\n"
                    + "    pe.nm_pessoa as razao,\n"
                    + "    pe.nm_fantasia as fantasia,\n"
                    + "    pe.nr_cpf as cpf,\n"
                    + "    pe.nr_rg as rg,\n"
                    + "    pe.nr_cnpj as cnpj,\n"
                    + "    pe.nr_cpf_cnpj as cpfcnpj,\n"
                    + "    pe.nr_ie as inscricaoestadual,\n"
                    + "    pe.nr_im as inscricaomunicipal,\n"
                    + "    pe.dt_inclusao as datacadastro,\n"
                    + "    f.dt_inclusao,\n"
                    + "    pe.nr_cep as cep,\n"
                    + "    (lt.ds_logradouro_tipo||' '||l.nm_logradouro) as endereco,\n"
                    + "    pe.nr_endereco as numero,\n"
                    + "    pe.ds_complemento as complemento,\n"
                    + "    ba.nm_bairro as bairro,\n"
                    + "    cid.nm_cidade as municipio,\n"
                    + "    cid.cd_municipio_ibge as municipioibge,\n"
                    + "    uf.sg_estado as uf,\n"
                    + "    pe.ds_observacao as observacao,\n"
                    + "    pe.nm_pai as pai,\n"
                    + "    pe.nm_mae as mae,\n"
                    + "    pe.dt_nascimento as datanascimento,\n"
                    + "    f.nr_dias_entrega as prazoentrega,\n"
                    + "    cp.ds_condicao_pagamento as condicaopagamento,\n"
                    + "    f.fg_situacao as situacaocadastro,\n"
                    + "    f.fg_produtor_rural,\n"
                    + "    f.fg_atacado,\n"
                    + "    ct.ds_contato_tipo as tipocontato,\n"
                    + "    pc.ds_pessoa_contato as telefone\n"
                    + "from tb_pessoa pe\n"
                    + "join tb_fornecedor f\n"
                    + "    on f.cd_pessoa_fornecedor = pe.cd_pessoa\n"
                    + "left join tb_pessoa_contato pc\n"
                    + "    on pc.cd_pessoa = pe.cd_pessoa\n"
                    + "left join tb_contato_tipo ct\n"
                    + "    on ct.cd_contato_tipo = pc.cd_contato_tipo\n"
                    + "left join tb_condicao_pagamento cp\n"
                    + "    on cp.cd_condicao_pagamento = f.cd_condicao_pagamento\n"
                    + "left join tb_logradouro_bairro lb\n"
                    + "    on lb.cd_logradouro_bairro = pe.cd_logradouro_bairro\n"
                    + "left join tb_logradouro l\n"
                    + "    on l.cd_logradouro = lb.cd_logradouro\n"
                    + "left join tb_logradouro_tipo lt\n"
                    + "    on lt.cd_logradouro_tipo = l.cd_logradouro_tipo\n"
                    + "left join tb_bairro ba\n"
                    + "    on ba.cd_bairro = lb.cd_bairro\n"
                    + "left join tb_cidade cid\n"
                    + "    on cid.cd_cidade = ba.cd_cidade\n"
                    + "left join tb_estado uf\n"
                    + "    on uf.cd_estado = cid.cd_estado"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                }
            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pe.cd_pessoa as id,\n"
                    + "    pe.nm_pessoa as razao,\n"
                    + "    pe.nm_fantasia as fantasia,\n"
                    + "    pe.nr_cpf as cpf,\n"
                    + "    pe.nr_rg as rg,\n"
                    + "    pe.nr_cnpj as cnpj,\n"
                    + "    pe.nr_cpf_cnpj as cpfcnpj,\n"
                    + "    pe.nr_ie as inscricaoestadual,\n"
                    + "    pe.nr_im as inscricaomunicipal,\n"
                    + "    pe.dt_inclusao,\n"
                    + "    c.dt_inclusao as datacadastro,\n"
                    + "    pe.nr_cep as cep,\n"
                    + "    (lt.ds_logradouro_tipo||' '||l.nm_logradouro) as endereco,\n"
                    + "    pe.nr_endereco as numero,\n"
                    + "    pe.ds_complemento as complemento,\n"
                    + "    ba.nm_bairro as bairro,\n"
                    + "    cid.nm_cidade as municipio,\n"
                    + "    cid.cd_municipio_ibge as municipioibge,\n"
                    + "    uf.sg_estado as uf,\n"
                    + "    pe.ds_observacao as observacao,\n"
                    + "    pe.nm_pai as pai,\n"
                    + "    pe.nm_mae as mae,\n"
                    + "    pe.dt_nascimento as datanascimento,\n"
                    + "    c.fg_situacao as situacaocadastro,\n"
                    + "    ct.ds_contato_tipo as tipocontato,\n"
                    + "    pc.ds_pessoa_contato as telefone,\n"
                    + "    c.ds_obs,\n"
                    + "    c.fg_estado_civil as estadocivil,\n"
                    + "    c.nm_empresa as empresa,\n"
                    + "    c.dt_admissao as dataadmissao,\n"
                    + "    c.ds_profissao as cargo,\n"
                    + "    c.vl_renda_principal as salario,\n"
                    + "    c.nr_cep_cobranca as cepcobranca,\n"
                    + "    (lt_cob.ds_logradouro_tipo||' '||l_cob.nm_logradouro) as enderecocobranca,\n"
                    + "    c.nr_endereco_cobranca as numerocobranca,\n"
                    + "    c.ds_complemento_cobranca as complementocobranca,\n"
                    + "    ba_cob.nm_bairro as bairrocobranca,\n"
                    + "    cid_cob.nm_cidade as municipiocobranca,\n"
                    + "    cid_cob.cd_municipio_ibge as municipioibgecobranca,\n"
                    + "    uf_cob.sg_estado as ufcobranca,\n"
                    + "    car.nm_carteira as tipovalorlimite,\n"
                    + "    coalesce(cc.vl_limite, 0) as valorlimite\n"
                    + "from tb_pessoa pe\n"
                    + "join tb_cliente c\n"
                    + "    on c.cd_pessoa_cliente = pe.cd_pessoa\n"
                    + "left join tb_cliente_carteira cc\n"
                    + "    on cc.cd_pessoa_cliente = c.cd_pessoa_cliente\n"
                    + "left join tb_carteira car\n"
                    + "    on car.cd_carteira = cc.cd_carteira\n"
                    + "left join tb_pessoa_contato pc\n"
                    + "    on pc.cd_pessoa = pe.cd_pessoa\n"
                    + "left join tb_contato_tipo ct\n"
                    + "    on ct.cd_contato_tipo = pc.cd_contato_tipo\n"
                    + "left join tb_logradouro_bairro lb\n"
                    + "    on lb.cd_logradouro_bairro = pe.cd_logradouro_bairro\n"
                    + "left join tb_logradouro l\n"
                    + "    on l.cd_logradouro = lb.cd_logradouro\n"
                    + "left join tb_logradouro_tipo lt\n"
                    + "    on lt.cd_logradouro_tipo = l.cd_logradouro_tipo\n"
                    + "left join tb_bairro ba\n"
                    + "    on ba.cd_bairro = lb.cd_bairro\n"
                    + "left join tb_cidade cid\n"
                    + "    on cid.cd_cidade = ba.cd_cidade\n"
                    + "left join tb_estado uf\n"
                    + "    on uf.cd_estado = cid.cd_estado\n"
                    + "left join tb_logradouro_bairro lb_cob\n"
                    + "    on lb_cob.cd_logradouro_bairro = c.cd_logradouro_bairro_cobranca\n"
                    + "left join tb_logradouro l_cob\n"
                    + "    on l_cob.cd_logradouro = lb_cob.cd_logradouro\n"
                    + "left join tb_logradouro_tipo lt_cob\n"
                    + "    on lt_cob.cd_logradouro_tipo = l_cob.cd_logradouro_tipo\n"
                    + "left join tb_bairro ba_cob\n"
                    + "    on ba_cob.cd_bairro = lb_cob.cd_bairro\n"
                    + "left join tb_cidade cid_cob\n"
                    + "    on cid_cob.cd_cidade = ba_cob.cd_cidade\n"
                    + "left join tb_estado uf_cob\n"
                    + "    on uf_cob.cd_estado = cid_cob.cd_estado"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.cd_receber as id,\n"
                    + "    r.cd_pessoa_loja as idcliente,\n"
                    + "    r.nr_documento as numerocupom,\n"
                    + "    r.dt_emissao as dataemissao,\n"
                    + "    i.dt_vencimento as datavencimento,\n"
                    + "    r.vl_receber as valor,\n"
                    + "    i.vl_parcela,\n"
                    + "    i.vl_juros,\n"
                    + "    i.vl_saldo,\n"
                    + "    r.fg_situacao as situacao_r,\n"
                    + "    i.fg_situacao as situacao_i,\n"
                    + "    r.ds_historico as historico,\n"
                    + "    r.ds_observacao as observacao,\n"
                    + "    r.cd_carteira,\n"
                    + "    r.cd_tipo_documento,\n"
                    + "    doc.ds_tipo_documento as documento\n"
                    + "from tb_receber r\n"
                    + "join tb_receber_item i\n"
                    + "    on i.cd_receber = r.cd_receber\n"
                    + "join tb_tipo_documento doc\n"
                    + "    on doc.cd_tipo_documento = r.cd_tipo_documento\n"
                    + "where r.fg_situacao = 'A'"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
