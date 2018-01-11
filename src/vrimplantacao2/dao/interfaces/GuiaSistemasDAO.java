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

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "vfd_CodDepartamento as merc1, "
                    + "vfd_Descricao as descricao "
                    + "FROM Tab_Departamento"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "COALESCE(BALANCA.VFD_CODPRODUTOEAN,0) AS BALANCA, PROD.vfd_FlagBalanca, "
                    + "prod.vfd_codproduto, prod.vfd_descricao,prod.vfd_descricaopdv,prod.vfd_tippeso, "
                    + "prod.vfd_codfornecedor,prod.vfd_icmse,prod.vfd_icmss,prod.vfd_situatributaria, "
                    + "prod.vfd_margem,prod.vfd_codgrupo, prod.vfd_codsubgrupo,prod.vfd_codsecao, "
                    + "prod.vfd_coddepartamento, prod.vfd_codequival,prod.vfd_validade, "
                    + "prod.vfd_dtcadastro,prod.vfd_classificacaofiscal,prod.vfd_flagpiscofins, "
                    + "prod.vfd_codmercadologico, prod.vfd_situacao, prod.vfd_codclassificacao, "
                    + "prod.vfd_idcomprador, prod.vfd_nbmsh, Prod.vfd_SetorBalanca, "
                    + "prod.vfd_codcofins, prod.vfd_codEQUIVAL, COFINS.VFD_CSTENTRADA, COFINS.VFD_CSTSAIDA, "
                    + "VFD_SITUACAO AS ATIVO, vfd_TipoInventarioFatorConversao as ProUnid "
                    + "from tab_produto as prod "
                    + "LEFT JOIN tmp_ListProdBalanca AS BALANCA ON BALANCA.VFD_CODPRODUTO = prod.vfd_codproduto "
                    + "LEFT OUTER JOIN [Tab_cadCOFINS] AS COFINS ON COFINS.vfd_CodCOFINS = PROD.VFD_CODCOFINS "
                    + "ORDER BY prod.vfd_codproduto"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "fornecedor.vfd_codFornecedor, fornecedor.vfd_razao, "
                    + "fornecedor.vfd_Apelido, fornecedor.vfd_endereco, "
                    + "fornecedor.vfd_cidade, fornecedor.vfd_bairro, "
                    + "fornecedor.vfd_uf, fornecedor.vfd_cep, fornecedor.vfd_ie, "
                    + "fornecedor.vfd_rg, fornecedor.vfd_fone, fornecedor.vfd_fax, "
                    + "fornecedor.vfd_prazo, fornecedor.vfd_nomevendedor, "
                    + "fornecedor.vfd_faxvendedor, fornecedor.vfd_TipoPessoa, "
                    + "fornecedor.vfd_cpf, fornecedor.vfd_emailvendedor, "
                    + "fornecedor.vfd_emailvendas, prazo.vfd_dias as dias, "
                    + "fornecedor.VFD_NUMERO, tipoforn.vfd_codtipfornecedor as tipoforn, vfd_fonevendedor "
                    + "from tab_fornecedor as fornecedor "
                    + "inner join tab_prazopagamento as prazo on prazo.vfd_codprazo = fornecedor.vfd_codprazo "
                    + "inner join tab_tipofornecedor as tipoforn on tipoforn.vfd_codtipfornecedor = fornecedor.vfd_codtipofornecedor "
                    + "order by fornecedor.vfd_codfornecedor"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "VFD_CODPRODUTO, VFD_CODFORNECEDOR, VFD_CODREFERENCIA "
                    + "FROM TAB_REFPRODUTO ORDER BY VFD_CODPRODUTO"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "vfd_codCliente, vfd_nomecliente, vfd_tipopessoa, vfd_rg, vfd_cpf, "
                    + "vfd_nomepdv, vfd_sexo, vfd_cidade, vfd_estadocivil, vfd_estado, "
                    + "vfd_endereco, vfd_numero, vfd_complemento, vfd_cep, vfd_ddd, vfd_fone, "
                    + "vfd_bairro, vfd_datanascimento, vfd_renda, vfd_situacao, "
                    + "vfd_datacadastro, vfd_limitecheque, vfd_email,vfd_dddcelular, "
                    + "vfd_celular, vfd_limitecredito, vfd_observacoes "
                    + "from tab_clientes "
                    + "order by vfd_codCliente"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
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
