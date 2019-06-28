/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class IQSistemasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "IQSistemas";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "CodigoFilial, \n"
                    + "empresa, \n"
                    + "cnpj \n"
                    + "FROM filiais\n"
                    + "ORDER BY CodigoFilial"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CodigoFilial"), rst.getString("cnpj") + " - " + rst.getString("empresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "g.codigo,\n"
                    + "g.grupo AS descgrupo,\n"
                    + "sg.codigosubgrupo,\n"
                    + "sg.subgrupo AS descsubgrupo\n"
                    + "FROM grupos g\n"
                    + "INNER JOIN subgrupos sg ON sg.codigogrupo = g.codigo\n"
                    + "ORDER BY g.codigo, sg.codigosubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("descgrupo"));
                    imp.setMerc2ID(rst.getString("codigosubgrupo"));
                    imp.setMerc2Descricao(rst.getString("descsubgrupo"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    result.add(imp);
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
                    "SELECT \n"
                    + "p.codigo AS id,\n"
                    + "p.codigobarras,\n"
                    + "p.descricao,\n"
                    + "p.unidade AS tipoembalagem,\n"
                    + "p.embalagem AS qtdembalagem,\n"
                    + "p.datacadastro AS datacadastro,\n"
                    + "p.tributacao AS csticms,\n"
                    + "p.icms AS aliqicms,\n"
                    + "p.percentualRedICMsST AS redIcms,\n"
                    + "p.ncm,\n"
                    + "p.cest,\n"
                    + "p.tributacaoPIS,\n"
                    + "p.tributacaoCOFINS,\n"
                    + "p.cstpisEntrada,\n"
                    + "p.cstcofinsEntrada,\n"
                    + "p.codigosuspensaopis as naturezareceita,\n"
                    + "p.grupo,\n"
                    + "p.subgrupo,\n"
                    + "p.custo,\n"
                    + "p.margemlucro,\n"
                    + "p.precovenda,\n"
                    + "p.estminimo,\n"
                    + "p.saldofinalestoque AS estoque,\n"
                    + "p.validade,\n"
                    + "p.pesobruto,\n"
                    + "p.pesoliquido\n"
                    + "FROM produtos p\n"
                    + "WHERE p.CodigoFilial = '" + getLojaOrigem() + "'\n"
                    + "ORDER BY p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margemlucro"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("tributacaoPIS"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisEntrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsCst(rst.getInt("cstIcms"));
                    imp.setIcmsAliq(rst.getDouble("aliqicms"));
                    imp.setIcmsReducao(rst.getDouble("redIcms"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "f.Codigo,\n"
                    + "f.razaosocial AS razao,\n"
                    + "f.empresa AS fantasia,\n"
                    + "f.CGC AS cnpj,\n"
                    + "f.INSCRICAO AS ie_rg,\n"
                    + "f.CPF,\n"
                    + "f.ENDERECO,\n"
                    + "f.numero,\n"
                    + "f.CEP,\n"
                    + "f.BAIRRO,\n"
                    + "f.CIDADE,\n"
                    + "f.ESTADO,\n"
                    + "f.TELEFONE,\n"
                    + "f.TELEFONE2,\n"
                    + "f.TELEFONE3,\n"
                    + "f.FAX,\n"
                    + "f.FAX2,\n"
                    + "f.FAX3,\n"
                    + "f.EMAIL,\n"
                    + "f.DATACAD,\n"
                    + "f.situacao,\n"
                    + "f.OBSERVACAO\n"
                    + "FROM fornecedores f\n"
                    + "WHERE f.fornecedor = 'S'\n"
                    + "AND f.CodigoFilial = '" + getLojaOrigem() + "'\n"
                    + "ORDER BY f.Codigo;"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setDatacadastro(rst.getDate("DATACAD"));
                    imp.setAtivo(rst.getString("situacao").contains("Ativo"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));

                    if ((rst.getString("TELEFONE2") != null)
                            && (!rst.getString("TELEFONE2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("TELEFONE2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("TELEFONE3") != null)
                            && (!rst.getString("TELEFONE3").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 3",
                                rst.getString("TELEFONE3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("FAX"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("FAX2") != null)
                            && (!rst.getString("FAX2").trim().isEmpty())) {
                        imp.addContato(
                                "FAX2",
                                rst.getString("FAX2"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("FAX3") != null)
                            && (!rst.getString("FAX3").trim().isEmpty())) {
                        imp.addContato(
                                "FAX3",
                                rst.getString("FAX3"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("EMAIL").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "codigo AS idproduto, \n"
                    + "idfornecedor, \n"
                    + "documento,\n"
                    + "custofornecedor\n"
                    + "FROM produtos\n"
                    + "WHERE idfornecedor IS NOT NULL\n"
                    + "AND CodigoFilial = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("documento"));
                    imp.setCustoTabela(rst.getDouble("custofornecedor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
