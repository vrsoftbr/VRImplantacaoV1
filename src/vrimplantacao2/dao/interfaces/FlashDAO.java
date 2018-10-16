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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class FlashDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Flash";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigoproduto,\n"
                    + "ean.codigoean,\n"
                    + "p.descricaocompleta,\n"
                    + "p.descricaoreduzida,\n"
                    + "p.diasvalidade,\n"
                    + "p.unidadevenda,\n"
                    + "p.qtdunidade,\n"
                    + "est.codigomargem,\n"
                    + "m.porcentpreco1 as margem1,\n"
                    + "m.porcentpreco2 as margem2,\n"
                    + "est.custo,\n"
                    + "est.precovenda1 as preco,\n"
                    + "est.qtdunidade as estoque,\n"
                    + "p.codigotributacaoa,\n"
                    + "p.codigotributacaob,\n"
                    + "p.icmssaida,\n"
                    + "p.icmsentrada,\n"
                    + "p.pis,\n"
                    + "p.cofins,\n"
                    + "p.codigotributacaopis,\n"
                    + "p.codigotributacaocofins,\n"
                    + "p.classificacao as cod_ncm,\n"
                    + "p.cest as cod_cest,\n"
                    + "p.codigonatureza,\n"
                    + "pis.pis_cst_e,\n"
                    + "pis.pis_cst_s,\n"
                    + "pis.cofins_cst_e,\n"
                    + "pis.cofins_cst_s,\n"
                    + "icm.svc_cst,\n"
                    + "icm.svc_alq_st,\n"
                    + "icm.svc_rbc,\n"
                    + "icm.svc_rbc_st,\n"
                    + "icm.snc_cst,\n"
                    + "icm.snc_alq_st,\n"
                    + "icm.snc_rbc,\n"
                    + "icm.snc_rbc_st,\n"
                    + "icms_s.situacaotributaria as sittrib_s,\n"
                    + "icms_s.observacao as desctrib_s,\n"
                    + "icms_e.situacaotributaria as sittrib_e,\n"
                    + "icms_e.observacao as desctrib_e\n"
                    + "from produto p\n"
                    + "left join eanproduto ean on ean.codigoproduto = p.codigoproduto\n"
                    + "left join estoque est on est.codigoproduto = p.codigoproduto\n"
                    + "left join margem m on m.codigomargem = est.codigomargem\n"
                    + "left join mxf_vw_pis_cofins pis on pis.codigo_produto = p.codigoproduto\n"
                    + "left join mxf_vw_icms icm on icm.codigo_produto = p.codigoproduto\n"
                    + "left join icms icms_s on icms_s.codigoicms = p.icmssaida\n"
                    + "left join icms icms_e on icms_e.codigoicms = p.icmsentrada\n"
                    + "where est.codigofilial = '" + getLojaOrigem() + "'\n"
                    + "order by p.codigoproduto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigoproduto"));
                    imp.setEan(rst.getString("codigoean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setValidade(rst.getInt("diasvalidade"));
                    imp.setTipoEmbalagem(rst.getString("unidadevenda"));
                    imp.setQtdEmbalagem(rst.getInt("qtdunidade"));
                    imp.setMargem(rst.getDouble("margem1"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("cod_ncm"));
                    imp.setCest(rst.getString("cod_cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_cst_s"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_cst_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("codigonatureza"));
                    imp.setIcmsCstSaida(rst.getInt("svc_cst"));
                    imp.setIcmsAliqSaida(rst.getDouble("svc_alq_st"));
                    imp.setIcmsReducaoSaida(rst.getDouble("svc_rbc"));
                    imp.setIcmsCstEntrada(rst.getInt("svc_cst"));
                    imp.setIcmsAliqEntrada(rst.getDouble("svc_alq_st"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("svc_rbc"));
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
                    + "f.codigo,\n"
                    + "f.cnpjcpf,\n"
                    + "f.pessoainscricao as ie,\n"
                    + "f.fisicojuridico,\n"
                    + "f.pessoadescricao as razao,\n"
                    + "f.pessoafantasia as fantasia,\n"
                    + "f.datainclusao,\n"
                    + "f.pessoarepresentante as representante,\n"
                    + "f.enderecorua,\n"
                    + "f.endereconumero,\n"
                    + "f.enderecoreferencia,\n"
                    + "f.enderecocep,\n"
                    + "f.enderecobairro,\n"
                    + "f.enderecocidade,\n"
                    + "f.enderecoestado,\n"
                    + "f.enderecocodigomunicipio,\n"
                    + "f.cobrancarua,\n"
                    + "f.cobrancanumero,\n"
                    + "f.cobrancareferencia,\n"
                    + "f.cobrancacep,\n"
                    + "f.cobrancabairro,\n"
                    + "f.cobrancacidade,\n"
                    + "f.cobrancaestado,\n"
                    + "f.cobrancacodigomunicipio,\n"
                    + "f.entregarua,\n"
                    + "f.entreganumero,\n"
                    + "f.entregabairro,\n"
                    + "f.entregacidade,\n"
                    + "f.entregacep,\n"
                    + "f.entregaestado,\n"
                    + "f.entregacodigomunicipio,\n"
                    + "f.entregareferencia,\n"
                    + "f.emailcomercial,\n"
                    + "f.emailpessoal,\n"
                    + "f.telresidencia1,\n"
                    + "f.telresidencia2,\n"
                    + "f.telcomercial1,\n"
                    + "f.telcomercial2,\n"
                    + "f.telcelular,\n"
                    + "f.telfax,\n"
                    + "f.ativo\n"
                    + "from pessoa f\n"
                    + "where f.pessoafornecedor = 'S'\n"
                    + "and f.codigofilial = '" + getLojaOrigem() + "'\n"
                    + "order by f.codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setCnpj_cpf(rst.getString("cnpjcpf"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setDatacadastro(rst.getDate("datainclusao"));
                    imp.setEndereco(rst.getString("enderecorua"));
                    imp.setNumero(rst.getString("endereconumero"));
                    imp.setCep(rst.getString("enderecocep"));
                    imp.setBairro(rst.getString("enderecobairro"));
                    imp.setMunicipio(rst.getString("enderecocidade"));
                    imp.setIbge_municipio(rst.getInt("enderecocodigomunicipio"));
                    imp.setUf(rst.getString("enderecoestado"));
                    imp.setCob_endereco(rst.getString("cobrancarua"));
                    imp.setCob_numero(rst.getString("cobrancanumero"));
                    imp.setCob_cep(rst.getString("cobrancacep"));
                    imp.setCob_bairro(rst.getString("cobrancabairro"));
                    imp.setCob_municipio(rst.getString("cobrancacidade"));
                    imp.setCob_ibge_municipio(rst.getInt("cobrancacodigomunicipio"));
                    imp.setCob_uf(rst.getString("cobrancaestado"));
                    imp.setAtivo("S".equals(rst.getString("ativo")));
                    if ((rst.getString("representante") != null)
                            && (!rst.getString("representante").trim().isEmpty())) {
                        imp.setObservacao("REPRESENTANTE - " + rst.getString("representante"));
                    }

                    imp.setTel_principal(rst.getString("telcomercial1"));
                    if ((rst.getString("telcomercial2") != null)
                            && (!rst.getString("telcomercial2").trim().isEmpty())) {
                        imp.addContato(
                                "TEL COM 2",
                                rst.getString("telcomercial2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telresidencia1") != null)
                            && (!rst.getString("telresidencia1").trim().isEmpty())) {
                        imp.addContato(
                                "TEL RES 1",
                                rst.getString("telresidencia1"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telresidencia2") != null)
                            && (!rst.getString("telresidencia2").trim().isEmpty())) {
                        imp.addContato(
                                "TEL RES 2",
                                rst.getString("telresidencia2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telcelular") != null)
                            && (!rst.getString("telcelular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                rst.getString("telcelular"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telfax") != null)
                            && (!rst.getString("telfax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("telfax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "codigofilial,\n"
                    + "nome\n"
                    + "from filial\n"
                    + "order by codigofilial"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("codigofilial"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }
}
