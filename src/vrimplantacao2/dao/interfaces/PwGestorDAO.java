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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class PwGestorDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "PwGestor";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.ordem as id,\n"
                    + "p.balanca,\n"
                    + "p.barra as ean,\n"
                    + "p.descricao,\n"
                    + "p.unidade,\n"
                    + "p.estoque,\n"
                    + "p.estoqueminimo,\n"
                    + "p.estoquemax,\n"
                    + "p.custo,\n"
                    + "p.vista as precovenda,\n"
                    + "p.vista2 as precovenda2,\n"
                    + "p.marckup,\n"
                    + "p.markup_fixo,\n"
                    + "p.cst,\n"
                    + "p.icms,\n"
                    + "p.icms2,\n"
                    + "p.reducao_icms,\n"
                    + "p.pis,\n"
                    + "p.cofins,\n"
                    + "p.ncm,\n"
                    + "p.dtcad,\n"
                    + "p.pesol,\n"
                    + "p.pesob\n"
                    + "from produtos p\n"
                    + "union all\n"
                    + "select\n"
                    + "p.ordem as id,\n"
                    + "p.balanca,\n"
                    + "p.barra2 as ean,\n"
                    + "p.descricao,\n"
                    + "p.unidade,\n"
                    + "p.estoque,\n"
                    + "p.estoqueminimo,\n"
                    + "p.estoquemax,\n"
                    + "p.custo,\n"
                    + "p.vista as precovenda,\n"
                    + "p.vista2 as precovenda2,\n"
                    + "p.marckup,\n"
                    + "p.markup_fixo,\n"
                    + "p.cst,\n"
                    + "p.icms,\n"
                    + "p.icms2,\n"
                    + "p.reducao_icms,\n"
                    + "p.pis,\n"
                    + "p.cofins,\n"
                    + "p.ncm,\n"
                    + "p.dtcad,\n"
                    + "p.pesol,\n"
                    + "p.pesob\n"
                    + "from produtos p\n"
                    + "where p.barra2 is not null\n"
                    + "and p.barra2 <> ''"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getString("balanca").contains("Y"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDataCadastro(rst.getDate("dtcad"));
                    imp.setPesoBruto(rst.getDouble("pesob"));
                    imp.setPesoLiquido(rst.getDouble("pesol"));
                    imp.setMargem(rst.getDouble("marckup"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cofins"));
                    imp.setIcmsCst(rst.getInt("cst"));
                    imp.setIcmsAliq(rst.getDouble("icms"));
                    imp.setIcmsReducao(rst.getDouble("reducao_icms"));
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
                    + "f.razao,\n"
                    + "f.fantasia,\n"
                    + "f.cnpj,\n"
                    + "f.ie,\n"
                    + "f.ende,\n"
                    + "f.end_num,\n"
                    + "f.bairro,\n"
                    + "f.codmunic,\n"
                    + "f.cidade,\n"
                    + "f.uf,\n"
                    + "f.cep,\n"
                    + "f.fone1,\n"
                    + "f.fone2,\n"
                    + "f.fax,\n"
                    + "f.celular,\n"
                    + "f.email,\n"
                    + "f.site,\n"
                    + "f.obs,\n"
                    + "f.contato,\n"
                    + "f.fonecont,\n"
                    + "f.celularcont,\n"
                    + "f.faxcont,\n"
                    + "f.emailcont,\n"
                    + "f.repre,\n"
                    + "f.fonerepre,\n"
                    + "f.prazo\n"
                    + "from fornecedores f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("ende"));
                    imp.setNumero(rst.getString("end_num"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone1"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("fone2"),
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
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("site").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
