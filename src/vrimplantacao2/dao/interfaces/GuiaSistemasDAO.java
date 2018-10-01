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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
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
                    + "sai.vfd_CodIcms codIcmsS, \n"
                    + "sai.vfd_Descricao descIcmsS, \n"
                    + "sai.vfd_Aliquota aliqS, \n"
                    + "sai.vfd_Base baseS, \n"
                    + "sai.vfd_CST cstS,\n"
                    + "ent.vfd_CodIcms codIcmsE, \n"
                    + "ent.vfd_Descricao descIcmsE, \n"
                    + "ent.vfd_Aliquota aliqE, \n"
                    + "ent.vfd_Base baseE, \n"
                    + "ent.vfd_CST cstE,\n"
                    + "prod.vfd_CEST, \n"
                    + "pr.vfd_CustoAquisicao,\n"
                    + "pr.vfd_PrecoVenda, \n"
                    + "est.vfd_QtdLoja\n"
                    + "from tab_produto as prod\n"
                    + "LEFT JOIN tab_ICMS sai on sai.vfd_CodIcms = prod.vfd_icmss\n"
                    + "LEFT JOIN tab_ICMS ent on ent.vfd_CodIcms = prod.vfd_icmse\n"
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
                    imp.setIcmsCstSaida(rst.getInt("cstS"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliqS"));
                    imp.setIcmsReducaoSaida(imp.getIcmsCstSaida() == 0 ? 0 : rst.getDouble("baseS"));
                    imp.setIcmsCstEntrada(rst.getInt("cstE"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliqE"));
                    imp.setIcmsReducaoEntrada(imp.getIcmsCstEntrada() == 0 ? 0 : rst.getDouble("baseE"));
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
                    imp.setPrazoVisita(rst.getInt("dias"));
                    imp.setPrazoEntrega(rst.getInt("vfd_prazo"));
                    imp.setTel_principal(rst.getString("vfd_fone"));

                    if ((rst.getString("vfd_nomevendedor") != null)
                            && (!rst.getString("vfd_nomevendedor").trim().isEmpty())) {
                        imp.setObservacao("NOME VENDEDOR " + rst.getString("vfd_nomevendedor"));
                    }

                    if ((rst.getString("vfd_emailvendedor") != null)
                            && (!rst.getString("vfd_emailvendedor").trim().isEmpty())
                            && (rst.getString("vfd_emailvendedor").contains("@"))) {
                        imp.addContato(
                                "EMAIL VENDEDOR",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("vfd_emailvendedor").toLowerCase()
                        );
                    }

                    if ((rst.getString("vfd_emailvendas") != null)
                            && (!rst.getString("vfd_emailvendas").trim().isEmpty())
                            && (rst.getString("vfd_emailvendas").contains("@"))) {
                        imp.addContato(
                                "EMAIL VENDAS",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("vfd_emailvendas").toLowerCase()
                        );
                    }

                    if ((rst.getString("vfd_faxvendedor") != null)
                            && (!rst.getString("vfd_faxvendedor").trim().isEmpty())) {
                        imp.addContato(
                                "FAX VENDEDOR",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("vfd_faxvendedor").toLowerCase()
                        );
                    }

                    if ((rst.getString("vfd_fonevendedor") != null)
                            && (!rst.getString("vfd_fonevendedor").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE VENDEDOR",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("vfd_fonevendedor").toLowerCase()
                        );
                    }

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
                    "select \n"
                    + "vfd_Caixa, "
                    + "vfd_Cupom, "
                    + "vfd_NumDocumento,\n"
                    + "vfd_DataLancamento, "
                    + "vfd_NumeroParcela,\n"
                    + "vfd_CodSacado, "
                    + "vfd_DataVencimento,\n"
                    + "vfd_VlrDocumento, "
                    + "vfd_VlrJuros \n"
                    + "from tab_fin_contasrec \n"
                    + "where vfd_DataBaixa is null\n"
                    + "and vfd_CodFilial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(getLojaOrigem() + rst.getString("vfd_Caixa") + rst.getString("vfd_Cupom") + rst.getString("vfd_CodSacado") + rst.getString("vfd_DataLancamento"));
                    imp.setIdCliente(rst.getString("vfd_CodSacado"));
                    imp.setNumeroCupom(rst.getString("vfd_Cupom"));
                    imp.setDataEmissao(rst.getDate("vfd_DataLancamento"));
                    imp.setDataVencimento(rst.getDate("vfd_DataVencimento"));
                    imp.setValor(rst.getDouble("vfd_VlrDocumento"));
                    imp.setJuros(rst.getDouble("vfd_VlrJuros"));
                    imp.setEcf(rst.getString("vfd_Caixa"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm  = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cli.vfd_NomeCliente,\n"
                    + "cli.vfd_CPF,\n"
                    + "cli.vfd_RG,\n"
                    + "cli.vfd_DDD+vfd_fone as telefone,\n"
                    + "ch.vfd_CodFilial,\n"
                    + "ch.vfd_Caixa,\n"
                    + "ch.vfd_Cupom,\n"
                    + "ch.vfd_NumDocumento,\n"
                    + "ch.vfd_DataLancamento,\n"
                    + "ch.vfd_DataVencimento,\n"
                    + "ch.vfd_NumeroParcela,\n"
                    + "ch.vfd_CodSacado,\n"
                    + "ch.vfd_VlrTotal,\n"
                    + "ch.vfd_CodBanco,\n"
                    + "ch.vfd_CodAgencia,\n"
                    + "ch.vfd_NumConta\n"
                    + "from View_FinanceiroContasRec ch\n"
                    + "left join tab_clientes cli on cli.vfd_CodCliente = ch.vfd_CodSacado\n"
                    + "where ch.vfd_codfilial = " + getLojaOrigem() + "\n"
                    + "and ch.vfd_DataBaixa is null\n"
                    + "and ch.vfd_CodBanco is not null\n"
                    + "and ch.vfd_CodAgencia is not null\n"
                    + "and ch.vfd_NumConta is not null\n"
                    + "and ch.vfd_NumDocumento is not null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(getLojaOrigem() + rst.getString("vfd_Caixa") + rst.getString("vfd_Cupom") + rst.getString("vfd_CodSacado") + rst.getString("vfd_DataLancamento"));
                    imp.setNumeroCheque(rst.getString("vfd_NumDocumento"));
                    imp.setBanco(Utils.stringToInt(rst.getString("vfd_CodBanco")));
                    imp.setAgencia(rst.getString("vfd_CodAgencia"));
                    imp.setConta(rst.getString("vfd_NumConta"));
                    imp.setDate(rst.getDate("vfd_DataLancamento"));
                    imp.setDataDeposito(rst.getDate("vfd_DataVencimento"));
                    imp.setValor(rst.getDouble("vfd_VlrTotal"));
                    imp.setCpf(rst.getString("vfd_CPF"));
                    imp.setRg(rst.getString("vfd_RG"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("vfd_NomeCliente"));
                    imp.setObservacao("IMPORTADO VR");
                    imp.setAlinea(0);
                    vResult.add(imp);                    
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> vResult = new ArrayList<>();
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
                    + "vfd_observacoes, "
                    + "vfd_CodEmpresa, "
                    + "vfd_LimiteConvenio, "
                    + "vfd_CodFilial "
                    + "from tab_clientes\n"
                    + "where vfd_CodFilial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("vfd_codCliente"));
                    imp.setCnpj(rst.getString("vfd_cpf"));
                    imp.setNome(rst.getString("vfd_nomecliente"));
                    imp.setIdEmpresa(rst.getString("vfd_CodEmpresa"));
                    imp.setBloqueado(false);
                    imp.setConvenioLimite(rst.getDouble("vfd_LimiteConvenio"));
                    imp.setConvenioDesconto(0);    
                    imp.setLojaCadastro("10");
                    vResult.add(imp);
                }
            }
            return vResult;
        }        
    }
}
