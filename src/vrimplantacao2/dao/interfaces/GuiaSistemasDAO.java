package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class GuiaSistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "GuiaSistemas";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.VASILHAME
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.SEXO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
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
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	vfd_CodICMS id,\n"
                    + "	vfd_Descricao descricao,\n"
                    + "	vfd_CST cst_icms,\n"
                    + "	vfd_Aliquota aliq_icms,\n"
                    + "	vfd_Base red_icms \n"
                    + "from\n"
                    + "	tab_icms ti\n"
                    + "order by vfd_CodICMS"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms"))
                    );
                }
            }
        }
        return result;
    }

    /*@Override
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
    }*/
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	vm.wrk_coddepartamento merc1,\n" +
                    "	vm.wrk_DescDepartamento merc1_descricao,\n" +
                    "	vm.wrk_codsecao merc2,\n" +
                    "	vm.wrk_DescSecao merc2_descricao,\n" +
                    "	vm.wrk_codgrupo merc3,\n" +
                    "	vm.wrk_DescGrupo merc3_descricao\n" +
                    "from \n" +
                    "	view_Mercadologico vm"
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
                    + "prod.vfd_codgrupo merc3,\n"
                    + "prod.vfd_codsubgrupo,\n"
                    + "prod.vfd_codsecao merc2,\n"
                    + "prod.vfd_coddepartamento merc1,\n"      
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
                    + "prod.vfd_icmss id_icms,\n"
                    // + "sai.vfd_CodIcms codIcmsS, \n"
                    // + "sai.vfd_Descricao descIcmsS, \n"
                    // + "sai.vfd_Aliquota aliqS, \n"
                    // + "sai.vfd_Base baseS, \n"
                    // + "sai.vfd_CST cstS,\n"
                    // + "ent.vfd_CodIcms codIcmsE, \n"
                    // + "ent.vfd_Descricao descIcmsE, \n"
                    // + "ent.vfd_Aliquota aliqE, \n"
                    // + "ent.vfd_Base baseE, \n"
                    // + "ent.vfd_CST cstE,\n"
                    + "prod.vfd_CEST, \n"
                    + "pr.vfd_CustoAquisicao,\n"
                    + "pr.vfd_PrecoVenda, \n"
                    + "est.vfd_QtdLoja,\n"
                    + "prod.vfd_CodProdVasilhame vasilhame\n"        
                    + "from tab_produto as prod\n"
                    + "LEFT JOIN tab_ICMS sai on sai.vfd_CodIcms = prod.vfd_icmss\n"
                    + "LEFT JOIN tab_ICMS ent on ent.vfd_CodIcms = prod.vfd_icmse\n"
                    + "LEFT JOIN tab_EMBALAGEM AS EMB ON EMB.VFD_CODPRODUTO = prod.vfd_codproduto \n"
                    + "LEFT JOIN tmp_ListProdBalanca AS BALANCA ON BALANCA.VFD_CODPRODUTO = prod.vfd_codproduto\n"
                    + "LEFT OUTER JOIN [Tab_cadCOFINS] AS COFINS ON COFINS.vfd_CodCOFINS = PROD.VFD_CODCOFINS\n"
                    + "LEFT JOIN tab_precoatual pr on pr.vfd_CodProduto = prod.vfd_codproduto and pr.vfd_CodFilial = " + getLojaOrigem() + "\n"
                    + "LEFT JOIN tab_estoqueatual est on est.vfd_CodProduto = prod.vfd_CodProduto and est.vfd_CodFilial = " + getLojaOrigem() + "\n"       
                    + "WHERE pr.vfd_QtdEmb = 1\n"
                    + "ORDER BY prod.vfd_codproduto"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("vfd_codproduto"));
                    imp.seteBalanca("V".equals(rst.getString("vfd_FlagBalanca")));

                    imp.setEan(rst.getString("VFD_CODBARRA"));
                    imp.setValidade(rst.getInt("vfd_validade"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rst.getInt("vfd_validade"));
                        imp.setEan(imp.getImportId());
                    }

                    imp.setDescricaoCompleta(rst.getString("vfd_descricao"));
                    imp.setDescricaoReduzida(rst.getString("vfd_descricaopdv"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setQtdEmbalagem(rst.getInt("VFD_QTDEMBALAGEM"));
                    imp.setTipoEmbalagem(rst.getString("ProUnid"));
                    imp.setDataCadastro(rst.getDate("vfd_dtcadastro"));
                    
                    String situacao = rst.getString("vfd_situacao");
                    
                    if (situacao != null && !situacao.equals("")) {
                        switch(situacao.trim()) {
                            case "ATIVO": imp.setSituacaoCadastro(1);
                            break;
                            case "FORA DE LINHA": imp.setSituacaoCadastro(0);
                            break;
                            case "COMPRA SUSPENSA": imp.setDescontinuado(true);
                                imp.setSituacaoCadastro(1);
                            break;
                            default: imp.setSituacaoCadastro(1); break;
                        }
                    }
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    //imp.setCodMercadologico4(rst.getString("vfd_codsubgrupo"));

                    imp.setMargem(rst.getDouble("vfd_margem"));
                    imp.setPrecovenda(rst.getDouble("vfd_PrecoVenda"));
                    imp.setCustoComImposto(rst.getDouble("vfd_CustoAquisicao"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("vfd_QtdLoja"));

                    imp.setNcm(rst.getString("vfd_classificacaofiscal"));
                    imp.setCest(rst.getString("vfd_CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("VFD_CSTSAIDA"));
                    imp.setPiscofinsCstCredito(rst.getString("VFD_CSTENTRADA"));

                    /*
                    imp.setIcmsCstSaida(rst.getInt("cstS"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliqS"));
                    imp.setIcmsReducaoSaida(imp.getIcmsCstSaida() == 0 ? 0 : rst.getDouble("baseS"));
                    imp.setIcmsCstEntrada(rst.getInt("cstE"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliqE"));
                    imp.setIcmsReducaoEntrada(imp.getIcmsCstEntrada() == 0 ? 0 : rst.getDouble("baseE"));
                     */
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIdVasilhame(rst.getString("vasilhame"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.TIPO_EMBALAGEM_PRODUTO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	em.vfd_CodProduto idproduto,\n" +
                        "	em.vfd_CodBarra ean,\n" +
                        "	em.vfd_QtdEmbalagem qtdembalagem,\n" +
                        "	tu.vfd_Descricao embalagem\n" +
                        "from \n" +
                        "	tab_EMBALAGEM em\n" +
                        "left join tab_Unidade tu on em.vfd_unidade = tu.vfd_CodUnidade \n" +
                        "where \n" +
                        "	vfd_flagembcd = 'V'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setTipoEmbalagemCotacao(rst.getString("embalagem"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }
        
        if (opt == OpcaoProduto.QTD_EMBALAGEM_COTACAO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	em.vfd_CodProduto idproduto,\n" +
                        "	em.vfd_CodBarra ean,\n" +
                        "	em.vfd_QtdEmbalagem qtdembalagem,\n" +
                        "	tu.vfd_Descricao embalagem\n" +
                        "from \n" +
                        "	tab_EMBALAGEM em\n" +
                        "left join tab_Unidade tu on em.vfd_unidade = tu.vfd_CodUnidade \n" +
                        "where \n" +
                        "	vfd_flagembcd = 'V'"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setTipoEmbalagemCotacao(rst.getString("embalagem"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));

                        vResult.add(imp);
                    }
                }
            }
            return vResult;
        }

        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + " fornecedor.vfd_codFornecedor, "
                    + " fornecedor.vfd_razao, "
                    + " fornecedor.vfd_Apelido, "
                    + " fornecedor.vfd_endereco, "
                    + " fornecedor.vfd_cidade, "
                    + " fornecedor.vfd_bairro, "
                    + " fornecedor.vfd_uf, "
                    + " fornecedor.vfd_cep, "
                    + " fornecedor.vfd_ie, "
                    + " fornecedor.vfd_rg, "
                    + " fornecedor.vfd_fone, "
                    + " fornecedor.vfd_fax, "
                    + " fornecedor.vfd_prazo, "
                    + " fornecedor.vfd_nomevendedor, "
                    //+ " fornecedor.vfd_faxvendedor, "
                    + " fornecedor.vfd_TipoPessoa, "
                    + " fornecedor.vfd_cpf, "
                    //+ " fornecedor.vfd_emailvendedor, "
                    //+ " fornecedor.vfd_emailvendas, "
                    //+ " prazo.vfd_dias as dias, "
                    + " fornecedor.VFD_NUMERO, "
                    + " tipoforn.vfd_codtipfornecedor as tipoforn, "
                    //+ " vfd_fonevendedor, "
                    + " fornecedor.vfd_PedidoMinimo, "
                    + " fornecedor.vfd_FreqVisita, "
                    + " vfd_NomeVendedor contato1, "
                    + "	vfd_FoneVendedor fone1, "
                    + "	vfd_EmailVendedor email1, "
                    + "	vfd_Nomegerente contato2, "
                    + "	vfd_FoneGerente fone2, "
                    + "	vfd_EmailGerente email2, "
                    + "	vfd_NomePromotor contato3, "
                    + "	vfd_FonePromotor fone3, "
                    + "	vfd_EmailPromotor email3,"
                    + " vfd_DeptoVendas contato4,\n"
                    + "	vfd_FoneDepto fone4,\n"
                    + "	vfd_NomeCobranca contato5,\n"
                    + "	vfd_FoneCobranca fone5,\n"
                    + " vfd_EmailVendas email4,\n"
                    + " vfd_EmailCobranca email5,\n"
                    + " (select \n" +
                    "		te.vfd_Email\n" +
                    "	from \n" +
                    "		tab_Emails te \n" +
                    "	where te.vfd_Codigo = fornecedor.vfd_CodFornecedor and \n" +
                    "		te.vfd_Tipo = 'F' and \n" +
                    "		te.vfd_IdEmail = 1) emailnfe,\n"        
                    + " fornecedor.vfd_ObsFor obs\n"        
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
                    imp.setPrazoVisita(rst.getInt("vfd_FreqVisita"));
                    imp.setPrazoEntrega(rst.getInt("vfd_prazo"));
                    imp.setValor_minimo_pedido(rst.getDouble("vfd_PedidoMinimo"));
                    imp.setTel_principal(rst.getString("vfd_fone"));

                    if ((rst.getString("vfd_nomevendedor") != null)
                            && (!rst.getString("vfd_nomevendedor").trim().isEmpty())) {
                        imp.setObservacao("NOME VENDEDOR " + rst.getString("vfd_nomevendedor"));
                    }

                    if ((rst.getString("contato1") != null)
                            && (!rst.getString("contato1").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato1"),
                                rst.getString("fone1"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email1")
                        );
                    }

                    if ((rst.getString("contato2") != null)
                            && (!rst.getString("contato2").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato2"),
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email2")
                        );
                    }

                    if ((rst.getString("contato3") != null)
                            && (!rst.getString("contato3").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato3"),
                                rst.getString("fone3"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email3")
                        );
                    }
                    
                    if ((rst.getString("contato4") != null)
                            && (!rst.getString("contato4").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato4"),
                                rst.getString("fone4"),
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email4")
                        );
                    }
                    
                    if ((rst.getString("contato5") != null)
                            && (!rst.getString("contato5").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato5"),
                                rst.getString("fone5"),
                                null,
                                TipoContato.NFE,
                                rst.getString("email5")
                        );
                    }
                    
                    if ((rst.getString("emailnfe") != null)
                            && (!rst.getString("emailnfe").trim().isEmpty())) {
                        imp.addContato(
                                "XML",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("emailnfe")
                        );
                    }

                    /*if ((rst.getString("vfd_emailvendedor") != null)
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
                    }*/
                    
                    imp.setObservacao(rst.getString("obs"));
                    
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
                    "SELECT\n"
                    + "	VFD_CODPRODUTO,\n"
                    + "	VFD_CODFORNECEDOR,\n"
                    + "	VFD_CODREFERENCIA,\n"
                    + "	case\n"
                    + "		when vfd_QtdEmbCompra = 0 then 1\n"
                    + "		else vfd_QtdEmbCompra\n"
                    + "	end vfd_QtdEmbCompra\n"
                    + "FROM\n"
                    + "	TAB_REFPRODUTO\n"
                    + "ORDER BY\n"
                    + "	VFD_CODPRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("VFD_CODPRODUTO"));
                    imp.setIdFornecedor(rst.getString("VFD_CODFORNECEDOR"));
                    imp.setCodigoExterno(rst.getString("VFD_CODREFERENCIA"));
                    imp.setQtdEmbalagem(rst.getDouble("vfd_QtdEmbCompra"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	vfd_CodFilial empresa,\n"
                    + "	vfd_CodFavorecido id_fornecedor,\n"
                    + "	vfd_NumDocumento documento,\n"
                    + "	vfd_NumeroParcela parcela,\n"
                    + "	vfd_DataEmissao emissao,\n"
                    + "	vfd_DataEntrada entrada,\n"
                    + "	vfd_DataVencimento vencimento,\n"
                    + "	vfd_ValorParcela valor,\n"
                    + "	vfd_Obs observacao\n"
                    + "from\n"
                    + "	tab_Fin_CPagar\n"
                    + "where\n"
                    + "	vfd_CodFilial = " + getLojaOrigem() + "\n"
                    + "	and vfd_CodGrupoPag = 1 and vfd_CodSubGrupoPag = 11\n"
                    + "	and vfd_DataPagamento is null\n"
                    + "order BY \n"
                    + "	 vfd_NumDocumento, vfd_NumeroParcela"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("empresa")+'-'+(rst.getString("id_fornecedor"))+'-'+(rst.getString("documento")));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
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
                    + "case when vfd_sexo = 'F' then 0 else 1 end vfd_Sexo, "
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
                    imp.setSexo(rst.getInt("vfd_Sexo") == 0 ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEstadoCivil(rst.getInt("vfd_estadocivil"));
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
                    "select\n"
                    + "	vfd_CodFilial empresa,\n"
                    + "	vfd_NumDocumento documento,\n"
                    + "	vfd_CodSacado id_cliente,\n"
                    + "	vfd_Caixa ecf,\n"
                    + "	vfd_Cupom numerocupom,\n"
                    + "	vfd_DataLancamento emissao,\n"
                    + "	vfd_NumeroParcela,\n"
                    + "	vfd_DataVencimento vencimento,\n"
                    + "	vfd_VlrDocumento valor,\n"
                    + "	vfd_VlrJuros juros\n"
                    + "from\n"
                    + "	tab_fin_contasrec\n"
                    + "where\n"
                    + "	vfd_CodFilial = " + getLojaOrigem() + "\n"
                    + "	and vfd_CodGrupoPag = 7 and vfd_CodSubGrupoPag = 717\n"
                    + "	and vfd_DataBaixa is null\n"
                    + "order BY vfd_DataLancamento"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(getLojaOrigem() + rst.getString("empresa") + rst.getString("documento") + rst.getString("id_cliente") + rst.getString("emissao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setEcf(rst.getString("ecf"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
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
                    vResult.add(imp);
                }
            }
            return vResult;
        }
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new GuiaSistemasDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new GuiaSistemasDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
