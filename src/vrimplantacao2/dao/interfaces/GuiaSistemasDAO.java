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
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class GuiaSistemasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "GuiaSistemas";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select vfd_CodFilial, vfd_Descricao from tab_filial order by vfd_CodFilial"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("vfd_CodFilial"), rst.getString("vfd_Descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "m1.vfd_CodDepartamento merc1, m1.vfd_Descricao merc1_descricao,\n"
                    + "m2.vfd_CodSecao merc2, m2.vfd_Descricao merc2_descricao,\n"
                    + "m3.vfd_CodGrupo merc3, m3.vfd_Descricao merc3_descricao,\n"
                    + "m4.vfd_CodSubGrupo merc4, m4.vfd_Descricao merc4_descricao\n"
                    + "from tab_departamento2 m1\n"
                    + "inner join tab_secao2 m2 on m2.vfd_CodDepartamento = m1.vfd_CodDepartamento\n"
                    + "inner join tab_grupo2 m3 on m3.vfd_CodDepartamento = m1.vfd_CodDepartamento \n"
                    + "       and m3.vfd_CodSecao = m2.vfd_CodSecao\n"
                    + "inner join tab_subgrupo2 m4 on m4.vfd_CodDepartamento = m1.vfd_CodDepartamento \n"
                    + "       and m4.vfd_CodGrupo = m3.vfd_CodGrupo \n"
                    + "       and m4.vfd_CodSecao = m2.vfd_CodSecao\n"
                    + "order by \n"
                    + "m1.vfd_CodDepartamento,\n"
                    + "m2.vfd_CodSecao,\n"
                    + "m3.vfd_CodGrupo,\n"
                    + "m4.vfd_CodSubGrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("merc4_descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct 'FAMILIA' AS TIPO, "
                    + "vfd_codequival "
                    + "from tab_produto "
                    + "WHERE VFD_CODEQUIVAL IS NOT NULL"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("vfd_codequival"));
                    imp.setDescricao(rst.getString("TIPO"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "COALESCE(BALANCA.VFD_CODPRODUTOEAN,0) AS BALANCA, \n"
                    + "PROD.vfd_FlagBalanca,\n"
                    + "prod.vfd_codproduto, \n"
                    + "EMB.VFD_CODBARRA, \n"
                    + "EMB.VFD_QTDEMBALAGEM,\n"
                    + "prod.vfd_descricao,\n"
                    + "prod.vfd_descricaopdv,\n"
                    + "prod.vfd_tippeso,\n"
                    + "prod.vfd_codfornecedor,\n"
                    + "prod.vfd_situatributaria,\n"
                    + "prod.vfd_margem,\n"
                    + "prod.vfd_codgrupo, \n"
                    + "prod.vfd_codsubgrupo,\n"
                    + "prod.vfd_codsecao,\n"
                    + "prod.vfd_coddepartamento, \n"
                    + "prod.vfd_codequival,\n"
                    + "prod.vfd_validade,\n"
                    + "prod.vfd_dtcadastro,\n"
                    + "prod.vfd_classificacaofiscal,\n"
                    + "prod.vfd_flagpiscofins,\n"
                    + "prod.vfd_codmercadologico, \n"
                    + "prod.vfd_situacao, \n"
                    + "prod.vfd_codclassificacao,\n"
                    + "prod.vfd_idcomprador, \n"
                    + "prod.vfd_nbmsh, \n"
                    + "Prod.vfd_SetorBalanca,\n"
                    + "prod.vfd_codcofins, \n"
                    + "prod.vfd_codEQUIVAL, \n"
                    + "COFINS.VFD_CSTENTRADA, \n"
                    + "COFINS.VFD_CSTSAIDA,\n"
                    + "VFD_SITUACAO AS ATIVO, \n"
                    + "vfd_TipoInventarioFatorConversao as ProUnid,\n"
                    + "prod.vfd_icmss,\n"
                    + "i.vfd_CodIcms, \n"
                    + "i.vfd_Descricao, \n"
                    + "i.vfd_Aliquota, \n"
                    + "i.vfd_Base, \n"
                    + "i.vfd_CST,\n"
                    + "prod.vfd_CEST, \n"
                    + "pr.vfd_CustoAquisicao,\n"
                    + "pr.vfd_PrecoVenda, "
                    + "est.vfd_QtdLoja\n"
                    + "from tab_produto as prod\n"
                    + "LEFT JOIN tab_ICMS i on i.vfd_CodIcms = prod.vfd_icmss\n"
                    + "LEFT JOIN tab_EMBALAGEM AS EMB ON EMB.VFD_CODPRODUTO = prod.vfd_codproduto \n"
                    + "LEFT JOIN tmp_ListProdBalanca AS BALANCA ON BALANCA.VFD_CODPRODUTO = prod.vfd_codproduto\n"
                    + "LEFT OUTER JOIN [Tab_cadCOFINS] AS COFINS ON COFINS.vfd_CodCOFINS = PROD.VFD_CODCOFINS\n"
                    + "LEFT JOIN tab_precoatual pr on pr.vfd_CodProduto = prod.vfd_codproduto and pr.vfd_CodFilial = " + getLojaOrigem() + "\n"
                    + "LEFT JOIN tab_estoqueatual est on est.vfd_CodProduto = prod.vfd_CodProduto and est.vfd_CodFilial = " + getLojaOrigem() + "\n"
                    + "ORDER BY prod.vfd_codproduto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("vfd_codproduto"));
                    imp.setEan(rst.getString("VFD_CODBARRA"));
                    imp.setQtdEmbalagem(rst.getInt("VFD_QTDEMBALAGEM"));
                    imp.setTipoEmbalagem(rst.getString("ProUnid"));
                    imp.seteBalanca("V".equals(rst.getString("vfd_FlagBalanca")));
                    imp.setValidade(rst.getInt("vfd_validade"));
                    imp.setDescricaoCompleta(rst.getString("vfd_descricao"));
                    imp.setDescricaoReduzida(rst.getString("vfd_descricaopdv"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("vfd_dtcadastro"));
                    imp.setSituacaoCadastro("ATIVO".equals(rst.getString("ATIVO")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setCodMercadologico1(rst.getString("vfd_coddepartamento"));
                    imp.setCodMercadologico2(rst.getString("vfd_codsecao"));
                    imp.setCodMercadologico3(rst.getString("vfd_codgrupo"));
                    imp.setCodMercadologico4(rst.getString("vfd_codsubgrupo"));
                    imp.setNcm(rst.getString("vfd_classificacaofiscal"));
                    imp.setCest(rst.getString("vfd_CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("VFD_CSTSAIDA"));
                    imp.setPiscofinsCstCredito(rst.getString("VFD_CSTENTRADA"));
                    imp.setIcmsCst(rst.getInt("vfd_CST"));
                    imp.setIcmsAliq(rst.getDouble("vfd_Aliquota"));
                    imp.setIcmsReducao(imp.getIcmsCst() == 0 ? 0 : rst.getDouble("vfd_Base"));
                    imp.setMargem(rst.getDouble("vfd_margem"));
                    imp.setPrecovenda(rst.getDouble("vfd_PrecoVenda"));
                    imp.setCustoComImposto(rst.getDouble("vfd_CustoAquisicao"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("vfd_QtdLoja"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "fornecedor.vfd_codFornecedor, "
                    + "fornecedor.vfd_razao, "
                    + "fornecedor.vfd_Apelido, "
                    + "fornecedor.vfd_endereco, "
                    + "fornecedor.vfd_cidade, "
                    + "fornecedor.vfd_bairro, "
                    + "fornecedor.vfd_uf, "
                    + "fornecedor.vfd_cep, "
                    + "fornecedor.vfd_ie, "
                    + "fornecedor.vfd_rg, "
                    + "fornecedor.vfd_fone, "
                    + "fornecedor.vfd_fax, "
                    + "fornecedor.vfd_prazo, "
                    + "fornecedor.vfd_nomevendedor, "
                    + "fornecedor.vfd_faxvendedor, "
                    + "fornecedor.vfd_TipoPessoa, "
                    + "fornecedor.vfd_cpf, "
                    + "fornecedor.vfd_emailvendedor, "
                    + "fornecedor.vfd_emailvendas, "
                    + "prazo.vfd_dias as dias, "
                    + "fornecedor.VFD_NUMERO, "
                    + "tipoforn.vfd_codtipfornecedor as tipoforn, "
                    + "vfd_fonevendedor "
                    + "from tab_fornecedor as fornecedor "
                    + "inner join tab_prazopagamento as prazo on prazo.vfd_codprazo = fornecedor.vfd_codprazo "
                    + "inner join tab_tipofornecedor as tipoforn on tipoforn.vfd_codtipfornecedor = fornecedor.vfd_codtipofornecedor "
                    + "order by fornecedor.vfd_codfornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("vfd_codFornecedor"));
                    imp.setRazao(rst.getString("vfd_razao"));
                    imp.setFantasia(rst.getString("vfd_Apelido"));
                    imp.setEndereco(rst.getString("vfd_endereco"));
                    imp.setNumero(rst.getString("VFD_NUMERO"));
                    imp.setBairro(rst.getString("vfd_bairro"));
                    imp.setMunicipio(rst.getString("vfd_cidade"));
                    imp.setUf(rst.getString("vfd_uf"));
                    imp.setCep(rst.getString("vfd_cep"));
                    imp.setCnpj_cpf(rst.getString("vfd_cpf"));
                    imp.setIe_rg(rst.getString("vfd_ie"));
                    imp.setTel_principal(rst.getString("vfd_fone"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "VFD_CODPRODUTO, "
                    + "VFD_CODFORNECEDOR, "
                    + "VFD_CODREFERENCIA "
                    + "FROM TAB_REFPRODUTO "
                    + "ORDER BY VFD_CODPRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("VFD_CODPRODUTO"));
                    imp.setIdFornecedor(rst.getString("VFD_CODFORNECEDOR"));
                    imp.setCodigoExterno(rst.getString("VFD_CODREFERENCIA"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "vfd_codCliente, "
                    + "vfd_nomecliente, "
                    + "vfd_tipopessoa, "
                    + "vfd_rg, "
                    + "vfd_cpf, "
                    + "vfd_nomepdv, "
                    + "vfd_sexo, "
                    + "vfd_cidade, "
                    + "vfd_estadocivil, "
                    + "vfd_estado, "
                    + "vfd_endereco, "
                    + "vfd_numero, "
                    + "vfd_complemento, "
                    + "vfd_cep, "
                    + "vfd_ddd, "
                    + "vfd_fone, "
                    + "vfd_bairro, "
                    + "vfd_datanascimento, "
                    + "vfd_renda, "
                    + "vfd_situacao, "
                    + "vfd_datacadastro, "
                    + "vfd_limitecheque, "
                    + "vfd_email,"
                    + "vfd_dddcelular, "
                    + "vfd_celular, "
                    + "vfd_limitecredito, "
                    + "vfd_observacoes "
                    + "from tab_clientes "
                    + "order by vfd_codCliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("vfd_codCliente"));
                    imp.setRazao(rst.getString("vfd_nomecliente"));
                    imp.setFantasia(rst.getString("vfd_nomepdv"));
                    imp.setCnpj(rst.getString("vfd_cpf"));
                    imp.setInscricaoestadual(rst.getString("vfd_rg"));
                    imp.setEndereco(rst.getString("vfd_endereco"));
                    imp.setNumero(rst.getString("vfd_numero"));
                    imp.setComplemento(rst.getString("vfd_complemento"));
                    imp.setCep(rst.getString("vfd_cep"));
                    imp.setBairro(rst.getString("vfd_bairro"));
                    imp.setMunicipio(rst.getString("vfd_cidade"));
                    imp.setUf(rst.getString("vfd_estado"));
                    imp.setEmail(rst.getString("vfd_email"));
                    imp.setTelefone(rst.getString("vfd_ddd") + rst.getString("vfd_fone"));
                    imp.setCelular(rst.getString("vfd_dddcelular") + rst.getString("vfd_celular"));
                    imp.setDataCadastro(rst.getDate("vfd_datacadastro"));
                    imp.setDataNascimento(rst.getDate("vfd_datanascimento"));
                    imp.setSalario(rst.getDouble("vfd_renda"));
                    imp.setValorLimite(rst.getDouble("vfd_limitecredito"));
                    imp.setObservacao(rst.getString("vfd_observacoes"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "ctrnum, clicod, cxanum, ctrdatemi, "
                    + "ctrdatvnc, ctrvlrdev, ctrobs "
                    + "FROM CONTARECEBER "
                    + "WHERE CTRVLRPAG < CTRVLRNOM "
                    + "or CTRVLRPAG IS NULL "
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, "
                    + "c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, "
                    + "c.devol2, c.motdv2, c.reapre, c.quitad, c.codfor, c.nomfor, "
                    + "c.datfor, c.caixax, c.observ, c.seqdev, c.datcad, c.usucad, "
                    + "c.datalt, c.usualt, c.cobran, c.datcob, c.entrad "
                    + "FROM CHEQUES c "
                    + "WHERE c.FILIAL = " + getLojaOrigem()
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
