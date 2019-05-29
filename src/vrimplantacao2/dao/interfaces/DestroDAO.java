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
import vrimplantacao.utils.Utils;
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
public class DestroDAO extends InterfaceDAO {

    public String v_estadoIcms = "";

    @Override
    public String getSistema() {
        return "Destro";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "filial_chave id,\n"
                    + "f_raz descricao\n"
                    + "from filial\n"
                    + "order by filial_chave"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
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
                    "select "
                    + "SECAO_CHAVE, "
                    + "S_DES "
                    + "from SECAO "
                    + "order by SECAO_CHAVE"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("SECAO_CHAVE"));
                    imp.setMerc1Descricao(rst.getString("S_DES"));
                    imp.setMerc2ID(rst.getString("SECAO_CHAVE"));
                    imp.setMerc2Descricao(rst.getString("S_DES"));
                    imp.setMerc3ID(rst.getString("SECAO_CHAVE"));
                    imp.setMerc3Descricao(rst.getString("S_DES"));
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
                    "SELECT\n"
                    + "P.ESTITEM_CHAVE id,\n"
                    + "P.I_BAR,\n"
                    + "p.codbalanca,\n"
                    + "P.I_DES,\n"
                    + "P.UNIDADE_UNV_CHAVE,\n"
                    + "P.I_QUN,\n"
                    + "P.I_PUN,\n"
                    + "P.I_DRD,\n"
                    + "P.ctribut_b_chave,\n"
                    + "P.DATA_INCLUSAO,\n"
                    + "P.DATA_ALTERACAO,\n"
                    + "P.NCM,\n"
                    + "p.cest,\n"
                    + "P.PESOLIQ,\n"
                    + "P.PESOBRUTO,\n"
                    + "P.PIS_SAIDA_SITTRIB,\n"
                    + "P.PIS_ENTRADA_SITTRIB,\n"
                    + "P.NATREC,\n"
                    + "I.FILIAL_CHAVE,\n"
                    + "I.T_CUS,\n"
                    + "T_PVL,\n"
                    + "I.secao_chave mercadologico,\n"
                    + "P.I_ICMS,\n"
                    + "IC.ICMS_COD_CHAVE,\n"
                    + "P.CST_ICMS_SAIDA,\n"
                    + "IC.I_PER,\n"
                    + "IC.i_prb\n"
                    + "FROM ESTITEM p\n"
                    + "left JOIN item I ON I.estitem_chave = P.estitem_chave and i.filial_chave = " + getLojaOrigem() + "\n"
                    + "left JOIN ICMS IC ON IC.ICMS_COD_CHAVE = P.i_icms and IC.estado_chave = '" + v_estadoIcms + "'\n"
                    + "order by p.estitem_chave"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("I_BAR"));
                    imp.setDescricaoCompleta(rst.getString("I_DES"));
                    imp.setDescricaoReduzida(rst.getString("I_DRD"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercadologico"));
                    imp.setCodMercadologico2(rst.getString("mercadologico"));
                    imp.setCodMercadologico3(rst.getString("mercadologico"));
                    imp.setTipoEmbalagem(rst.getString("UNIDADE_UNV_CHAVE"));
                    imp.setQtdEmbalagem(rst.getInt("I_QUN"));
                    imp.setDataCadastro(rst.getDate("DATA_INCLUSAO") == null ? rst.getDate("DATA_ALTERACAO") : rst.getDate("DATA_INCLUSAO"));
                    imp.setPesoBruto(rst.getDouble("PESOBRUTO"));
                    imp.setPesoLiquido(rst.getDouble("PESOLIQ"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS_SAIDA_SITTRIB"));
                    imp.setPiscofinsCstCredito(rst.getString("PIS_ENTRADA_SITTRIB"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NATREC"));
                    imp.setIcmsCst(rst.getDouble("i_prb") > 0 ? 20 : rst.getInt("CST_ICMS_SAIDA"));
                    imp.setIcmsAliq(rst.getDouble("I_PER"));
                    imp.setIcmsReducao(rst.getDouble("i_prb"));

                    if ((Double.parseDouble(Utils.formataNumero(rst.getString("i_bar")))
                            == (Double.parseDouble(Utils.formataNumero(rst.getString("id")))))) {
                        imp.seteBalanca(true);
                    } else {
                        imp.seteBalanca(false);
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.CUSTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "estitem_chave id_produto, "
                        + "t_cus\n"
                        + "from item\n"
                        + "where filial_chave = " + getLojaOrigem() + "\n"
                        + "order by estitem_chave"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setCustoComImposto(rst.getDouble("t_cus"));
                        imp.setCustoSemImposto(rst.getDouble("t_cus"));
                        result.add(imp);
                    }
                }
                return result;
            }
        } else if (opcao == OpcaoProduto.PRECO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "estitem_chave id_produto, "
                        + "t_pv1 venda "
                        + "from item\n"
                        + "where filial_chave = " + getLojaOrigem() + "\n"
                        + "order by estitem_chave"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setPrecovenda(rst.getDouble("venda"));
                        result.add(imp);
                    }
                }
                return result;
            }
        } else if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "WITH\n"
                        + "mov AS(\n"
                        + "    select m.estitem_chave, MAX(movestoq_chave) as movi\n"
                        + "    FROM movestoq as m\n"
                        + "    where m.filial_chave in (" + getLojaOrigem() + ")\n"
                        + "    group by m.estitem_chave\n"
                        + ")\n"
                        + "select\n"
                        + "    m.estitem_chave id_produto,\n"
                        + "    m.quantidade estoque\n"
                        + "from movestoq as m\n"
                        + "inner join mov on mov.estitem_chave = m.estitem_chave AND mov.movi = m.movestoq_chave\n"
                        + "where m.filial_chave in (" + getLojaOrigem() + ")"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEstoque(rst.getDouble("estoque"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "estitem_chave id_produto,\n"
                    + "codbarr_chave_bar codigobarras\n"
                    + "from CODBARR\n"
                    + "where cast(codbarr_chave_bar as numeric(14,0)) > 999999"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigobarras"));
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
                    "SELECT\n"
                    + "F.clifor_chave,\n"
                    + "F.c_tip,\n"
                    + "F.c_ins,\n"
                    + "F.c_des,\n"
                    + "F.c_fan,\n"
                    + "F.c_cad,\n"
                    + "F.data_exclusao,\n"
                    + "EN.estado_chave,\n"
                    + "EN.endereco,\n"
                    + "EN.numero,\n"
                    + "EN.cidade,\n"
                    + "EN.bairro,\n"
                    + "EN.complemento,\n"
                    + "EM.email,\n"
                    + "TEL.telefone,\n"
                    + "TEL.contato,\n"
                    + "EN.cep\n"
                    + "FROM CLIFOR F\n"
                    + "LEFT join CLIENDERECOS ON CLIENDERECOS.clifor_chave = F.clifor_chave\n"
                    + "LEFT JOIN ENDERECOS EN ON EN.id_endereco = CLIENDERECOS.id_endereco\n"
                    + "LEFT JOIN CLIEMAIL ON CLIEMAIL.clifor_chave = F.clifor_chave\n"
                    + "LEFT JOIN EMAIL EM ON  EM.id_email = CLIEMAIL.id_email\n"
                    + "LEFT JOIN clitelefones CTEL ON CTEL.clifor_chave = F.clifor_chave\n"
                    + "LEFT JOIN telefones TEL ON TEL.id_telefone = CTEL.id_telefone\n"
                    + "WHERE F.CLIFOR_TIPO IN (2,3)\n"
                    + "ORDER BY F.clifor_chave"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("clifor_chave"));
                    imp.setCnpj_cpf(rst.getString("clifor_chave"));
                    imp.setIe_rg(rst.getString("c_ins"));
                    imp.setRazao(rst.getString("c_des"));
                    imp.setFantasia(rst.getString("c_fan"));
                    imp.setDatacadastro(rst.getDate("c_cad"));
                    imp.setAtivo((rst.getDate("data_exclusao") == null));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado_chave"));
                    imp.setTel_principal(rst.getString("telefone"));
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO " + rst.getString("contato"));
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "EMAIL",
                                null,
                                TipoContato.COMERCIAL,
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
                    + "clifor_chave id_fornecedor,\n"
                    + "estitem_chave id_produto,\n"
                    + "cod as codigo_externo\n"
                    + "from fornec_prod "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigo_externo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
