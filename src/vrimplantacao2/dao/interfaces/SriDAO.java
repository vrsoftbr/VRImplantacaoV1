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
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SriDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SRI";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cod_aliquota as codigo,\n"
                    + "percentual as descricao,\n"
                    + "valor as percentual\n"
                    + "from aliquota\n"
                    + "where ativo = 'S'"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
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
                    + "empresa as id,\n"
                    + "fantasia as nome\n"
                    + "from empresa"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m1.cod_grupo as merc1, m1.descricao as merc1_descricao,\n"
                    + "m2.cod_subgrupo as merc2, m2.descricao as merc2_descricao,\n"
                    + "'1' as merc3, m2.descricao as merc3_descricao\n"
                    + "from grupo_prod m1\n"
                    + "inner join subgrupo_prod m2 on m2.cod_grupo = m1.cod_grupo\n"
                    + "where m1.cod_grupo > 0\n"
                    + "and m2.cod_subgrupo > 0\n"
                    + "and m1.empresa = " + getLojaOrigem() + "\n"
                    + "and m2.empresa = " + getLojaOrigem() + "\n"
                    + "order by m1.cod_grupo, m2.cod_subgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
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
                    + "cod_interno,\n"
                    + "cod_produto,\n"
                    + "descricao,\n"
                    + "cod_grupo,\n"
                    + "cod_subgrupo,\n"
                    + "bruto,\n"
                    + "liquido,\n"
                    + "estoque,\n"
                    + "minimo,\n"
                    + "custo,\n"
                    + "venda,\n"
                    + "icms_in,\n"
                    + "icms_out,\n"
                    + "st,\n"
                    + "st_out,\n"
                    + "aliquota,\n"
                    + "data_cadastro,\n"
                    + "balanca,\n"
                    + "bal_validade,\n"
                    + "cod_ncm,\n"
                    + "cest,\n"
                    + "cstpc,\n"
                    + "cstpc_entrada,\n"
                    + "cod_receita_pis\n"
                    + "inativo,\n"
                    + "markup\n"
                    + "from produto\n"
                    + "where empresa = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cod_interno"));
                    imp.setEan(rst.getString("cod_produto"));
                    imp.seteBalanca("S".equals(rst.getString("balanca")));
                    imp.setValidade(rst.getInt("bal_validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoGondola());
                    imp.setCodMercadologico1(rst.getString("cod_grupo"));
                    imp.setCodMercadologico2(rst.getString("cod_subgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("markup"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("venda"));
                    imp.setEstoqueMinimo(rst.getDouble("minimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("inativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("cod_ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpc"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpc_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("cod_receita_pis"));
                    imp.setIcmsCstSaida(rst.getInt("st_out"));
                    imp.setIcmsCstEntrada(rst.getInt("st"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms_out"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_in"));
                    imp.setIcmsReducaoSaida(0);
                    imp.setIcmsReducaoEntrada(0);
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
                    + "cod_cadastro,\n"
                    + "razao,\n"
                    + "fantasia,\n"
                    + "cnpj,\n"
                    + "estadual,\n"
                    + "municipal,\n"
                    + "cae,\n"
                    + "endereco,\n"
                    + "bairro,\n"
                    + "cep,\n"
                    + "cidade,\n"
                    + "estado,\n"
                    + "telefone,\n"
                    + "fax,\n"
                    + "celular,\n"
                    + "contato,\n"
                    + "endcob,\n"
                    + "bairrocob,\n"
                    + "cepcob,\n"
                    + "cidadecob,\n"
                    + "estadocob,\n"
                    + "obs,\n"
                    + "cadastrado_em,\n"
                    + "limite,\n"
                    + "prazo,\n"
                    + "senhacred,\n"
                    + "nrend1,\n"
                    + "e_mail,\n"
                    + "complementar,\n"
                    + "dia_vencimento\n"
                    + "from cadastro\n"
                    + "where tipo = 'F'\n"
                    + "and cod_cadastro > 0\n"
                    + "and empresa = 0\n"
                    + "order by cod_cadastro"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("cod_cadastro"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("estadual"));
                    imp.setInsc_municipal(rst.getString("municipal"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("nrend1"));
                    imp.setComplemento(rst.getString("complementar"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("cadastrado_em"));
                    imp.setPrazoEntrega(rst.getInt("prazo"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
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
}
