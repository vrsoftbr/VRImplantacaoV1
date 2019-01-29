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
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class WinNexusDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "WinNexus";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "CODIGO, "
                    + "NOME, "
                    + "CGC "
                    + "from "
                    + "gestordb.dbo.EMPRESA "
                    + "order by CODIGO"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CODIGO"), rst.getString("NOME") + " - " + rst.getString("CGC")));
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.CODIGO, \n"
                    + "p.COD_BARRAS,\n"
                    + "p.DESCRICAO,\n"
                    + "p.UNIDADE,\n"
                    + "p.CUSTO_BRUTO as CUSTOSEMIMPOSTO,\n"
                    + "p.CUSTO_LIQUIDO as CUSTOCOMIMPOSTO,\n"
                    + "p.CADASTRO,\n"
                    + "p.BS_CALC_ICM_SAIDA,\n"
                    + "ea.ESTOQUE_CALCULADO as ESTOQUE,\n"
                    + "pr.preco_venda,\n"
                    + "pm.custo_final,\n"
                    + "pm.preco_de_venda,\n"
                    + "pm.margem_liquida,\n"
                    + "e.NCM,\n"
                    + "e.CEST,\n"
                    + "e.CST_COFINS,\n"
                    + "e.CST_COFINS_ENT,\n"
                    + "e.CST_PIS,\n"
                    + "e.CST_PIS_ENT,\n"
                    + "e.NATUREZA_RECEITA_PIS,\n"
                    + "al.ST,\n"
                    + "e.CST,\n"
                    + "al.ICMS,\n"
                    + "p.ICM_VENDA,\n"
                    + "e.CST_ENT,\n"
                    + "p.ICMS_CREDITO\n"
                    + "from produtos p\n"
                    + "left join ESTOQUE e on e.ESTOQUE_ID = p.CODIGO\n"
                    + "left join ESTOQUE_ATUAL ea on ea.ESTOQUE_ID = p.CODIGO and e.FILIAL = 1\n"
                    + "left join precos_produtos pr on pr.codigo = p.CODIGO\n"
                    + "left join produtos_margem pm on pm.estoque_id = p.CODIGO\n"
                    + "left join ALIQUOTA_ECF al on al.ST = p.ST\n"
                    + "order by p.CODIGO"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODIGO"));
                    imp.setEan(rst.getString("COD_BARRAS"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    imp.setDataCadastro(rst.getDate("CADASTRO"));
                    imp.setMargem(rst.getDouble("margem_liquida"));
                    imp.setCustoSemImposto(rst.getDouble("CUSTOSEMIMPOSTO"));
                    imp.setCustoComImposto(rst.getDouble("CUSTOCOMIMPOSTO"));
                    imp.setPrecovenda(rst.getDouble("preco_de_venda"));
                    imp.setEstoque(rst.getDouble("ESTOQUE"));
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("CST_PIS"));
                    imp.setPiscofinsCstCredito(rst.getString("CST_PIS_ENT"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NATUREZA_RECEITA_PIS").substring(0, rst.getString("NATUREZA_RECEITA_PIS").indexOf("-")));
                    imp.setIcmsCstSaida(rst.getInt("CST"));
                    imp.setIcmsAliqSaida(rst.getDouble("ICMS"));
                    imp.setIcmsCstEntrada(rst.getInt("CST_ENT"));
                    imp.setIcmsAliqEntrada(rst.getDouble("ICMS_CREDITO"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "f.FORNE_ID as ID,\n"
                    + "f.NOME as RAZAO,\n"
                    + "f.FANTASIA,\n"
                    + "f.IE,\n"
                    + "f.CGC,\n"
                    + "f.LOGRADOURO,\n"
                    + "f.BAIRRO,\n"
                    + "f.CIDADE,\n"
                    + "f.ESTADO,\n"
                    + "f.CEP,\n"
                    + "f.TELEFONE,\n"
                    + "f.CELULAR,\n"
                    + "f.FAX,\n"
                    + "f.EMAIL,\n"
                    + "f.SITE,\n"
                    + "f.OBS,\n"
                    + "f.CADASTRO,\n"
                    + "f.ATIVO,\n"
                    + "f.NUMERO,\n"
                    + "f.TELEFONE2,\n"
                    + "f.TELEFONE3,\n"
                    + "f.MUNICIPIO_CODIGO,\n"
                    + "f.PAIS_CODIGO\n"
                    + "from FORNECEDORES f\n"
                    + "order by FORNE_ID"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("ID"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setCnpj_cpf(rst.getString("CGC"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setDatacadastro(rst.getDate("CADASTRO"));
                    imp.setAtivo("SIM".equals(rst.getString("ATIVO")));
                    imp.setEndereco(rst.getString("LOGRADOURO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setObservacao(rst.getString("OBS"));

                    if ((rst.getString("TELEFONE2") != null)
                            && (!rst.getString("TELEFONE2").trim().isEmpty())) {

                        imp.addContato(
                                "TELEFONE2",
                                rst.getString("TELEFONE2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("TELEFONE3") != null)
                            && (!rst.getString("TELEFONE3").trim().isEmpty())) {

                        imp.addContato(
                                "TELEFONE3",
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
                                TipoContato.COMERCIAL,
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
                                rst.getString("EMAIL")
                        );
                    }
                    if ((rst.getString("SITE") != null)
                            && (!rst.getString("SITE").trim().isEmpty())) {

                        imp.addContato(
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("SITE")
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
