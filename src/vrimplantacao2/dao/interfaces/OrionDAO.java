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
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class OrionDAO extends InterfaceDAO {

    public String i_arquivo;

    @Override
    public String getSistema() {
        return "Orion";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct "
                    + "codsub as id, "
                    + "titulograd as descricao "
                    + "from ESTOQUE "
                    + "where plu is not null "
                    + "or trim(plu) <> ''"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct "
                    + "m1.codsetor cod_m1, "
                    + "m1.setor desc_m1, "
                    + "m2.codgrupo cod_m2, "
                    + "m2.grupo desc_m2, "
                    + "m3.codigo cod_m3, "
                    + "m3.subgrupo desc_m3 "
                    + "from setor m1 "
                    + "left join grupo m2 on m2.codsetor = m1.codsetor "
                    + "left join subgrupo m3 on m3.codgrupo = m2.codgrupo "
                    + "order by m1.codsetor, m2.codgrupo, m3.codigo"
            )) {
                while (rst.next()) {
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
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "e.plu id_produto, "
                    + "l.codigo ean, "
                    + "e.codsetor mercadologico1, "
                    + "e.codgru, mercadologico2"
                    + "e.codsubgru, mercadologico3"
                    + "e.nome descricaocompleta, "
                    + "e.descricao descricaoreduzida, "
                    + "e.gondola descricaogondola, "
                    + "e.custo, "
                    + "e.classfis ncm, "
                    + "e.sittribut, "
                    + "e.icms, "
                    + "e.reducao, "
                    + "e.unidade, "
                    + "e.inclusao, "
                    + "e.piscst, "
                    + "e.cofinscst, "
                    + "e.vendavare, "
                    + "e.lucrovare margem, "
                    + "l.qtde, "
                    + "e.quantfisc, "
                    + "e.custobase, "
                    + "e.gradeum, "
                    + "e.gradedois, "
                    + "e.codsub, "
                    + "e.ultprecust custosemimposto, "
                    + "((e.ultprecust - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custocomimposto "
                    + "from estoque e "
                    + "left join ligplu l on e.plu = l.plu  "
                    + "where e.plu is not null"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setIdFamiliaProduto(rst.getString("codsub"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtde"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("vendavare"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setEstoque(rst.getDouble("quantfisc"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscst"));
                    imp.setPiscofinsCstCredito(rst.getString("cofinscst"));
                    imp.setIcmsCst(rst.getInt("sittribut"));
                    imp.setIcmsAliq(rst.getDouble("icms"));
                    imp.setIcmsReducao(rst.getDouble("reducao"));
                    imp.setDataCadastro(rst.getDate("inclusao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "codigo, "
                    + "nome, "
                    + "razao, "
                    + "inscest, "
                    + "cgc, "
                    + "rua, "
                    + "casa, "
                    + "edificio, "
                    + "sala, "
                    + "cidade, "
                    + "bairro, "
                    + "cep, "
                    + "estado, "
                    + "inclusao, "
                    + "email, "
                    + "contato, "
                    + "contatcom, "
                    + "telefone1, "
                    + "telefone2, "
                    + "telefone3, "
                    + "obs "
                    + "FROM FORNECE "
                    + "order by codigo "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj_cpf(rst.getString("cgc"));
                    imp.setIe_rg(rst.getString("inscest"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("casa") + " " + rst.getString("edificio") + " " + rst.getString("sala"));
                    imp.setDatacadastro(rst.getDate("inclusao"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
                    }
                    if ((rst.getString("contatcom") != null)
                            && (!rst.getString("contatcom").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOCOM " + rst.getString("contatcom"));
                    }

                    imp.setTel_principal(rst.getString("telefone1"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
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
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODFOR, "
                    + "CODINT, "
                    + "PLU, "
                    + "QTDE "
                    + "FROM LIGFAB"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("CODFOR"));
                    imp.setIdProduto(rst.getString("PLU"));
                    imp.setCodigoExterno(rst.getString("CODINT"));
                    imp.setQtdEmbalagem(rst.getInt("QTDE"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "codigo, "
                    + "nome, "
                    + "razao, "
                    + "nascimento, "
                    + "inscest, "
                    + "cgc, "
                    + "cic, "
                    + "firma, "
                    + "cargo, "
                    + "salario, "
                    + "saldo, "
                    + "pai, "
                    + "mae, "
                    + "rua, "
                    + "casa, "
                    + "edificio, "
                    + "apto, "
                    + "cidade, "
                    + "bairro, "
                    + "cep, "
                    + "estado, "
                    + "email, "
                    + "abertura, "
                    + "contato, "
                    + "telefone1, "
                    + "telefone2, "
                    + "telefone3, "
                    + "contatcom, "
                    + "rg "
                    + "from cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setEmpresa(rst.getString("firma"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("saldo"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setEmail(rst.getString("email"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(rst.getString("CONTATO " + rst.getString("contato")));
                    }
                    if ((rst.getString("contatcom") != null)
                            && (!rst.getString("contatcom").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOCOM " + rst.getString("contatcom"));
                    }

                    if ((rst.getString("cic") != null)
                            && (!rst.getString("cic").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cic"));
                    } else if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cgc"));
                    } else {
                        imp.setCnpj("");
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else if ((rst.getString("inscest") != null)
                            & (!rst.getString("inscest").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscest"));
                    } else {
                        imp.setInscricaoestadual("");
                    }

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
                                null,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        ConexaoDBF.abrirConexao(i_arquivo);
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "codigo, "
                    + "vencimento, "
                    + "dlanca, "
                    + "valorreceb, "
                    + "codigocli, "
                    + "terminal "
                    + "from receber "
                    + "where vlrpago = 0"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdCliente(rst.getString("codigocli"));
                    imp.setNumeroCupom(rst.getString("codigo"));
                    imp.setEcf(rst.getString("terminal"));
                    imp.setDataEmissao(rst.getDate("dlanca"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valorreceb"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
