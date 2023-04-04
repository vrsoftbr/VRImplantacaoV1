/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class Target_G3DAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "TARGET-G3";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATACADO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MARGEM_MAXIMA,
                OpcaoProduto.MARGEM_MINIMA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.CONDICAO_PAGAMENTO2,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "DISTINCT \n"
                    + "	case\n"
                    + "		when ISNULL (cd_sit_trib_esp,0 ) = ''\n"
                    + "		then 0\n"
                    + "		else ISNULL (cd_sit_trib_esp,0 )\n"
                    + "	end cst,\n"
                    + "	aliq_icm icms,\n"
                    + "	ISNULL ( perc_red_baseicm_esp,\n"
                    + "	0 ) reducao\n"
                    + "from\n"
                    + "	IcmProdEmpresa ipe\n"
                    + "WHERE\n"
                    + "	CdEmp = " + getLojaOrigem() + "\n"
                    + "	and est_de = 'SP'\n"
                    + "	and est_para = 'SP'"
            )) {
                while (rst.next()) {
                    String id = rst.getString("cst") + "-" + rst.getString("icms") + "-" + rst.getString("reducao");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	d.cd_depto mercid1,\n"
                    + "	d.descricao descmerc1,	\n"
                    + "	s.cd_secao mercid2,\n"
                    + "	s.descricao descmerc2,\n"
                    + "	c.cd_categprd mercid3,\n"
                    + "	c.descricao descmerc3\n"
                    + "from\n"
                    + "	linha l\n"
                    + "left join secao s on\n"
                    + "	l.cd_secao = s.cd_secao\n"
                    + "left join depto d on\n"
                    + "	s.cd_depto = d.cd_depto\n"
                    + "left join categprd c on\n"
                    + "	l.cd_categprd = c.cd_categprd "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    result.add(imp);
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
                    "select\n"
                    + "	p.cd_prod Id,\n"
                    + "	p.dt_cad dataCadastro,\n"
                    + "	p.DataUltimaAtualizacaoSystax dataAlteracao,\n"
                    + "	p.cd_barra ean,\n"
                    + "	p.cd_barra_compra dun14,\n"
                    + "	p.qtde_unid_cmp qtdEmbalagem,\n"
                    + "	p.qtde_unid embEan,\n"
                    + "	p.unid_est tipoEmbalagem,\n"
                    + "	p.unid_cmp tipoEmbalagemCotacao,\n"
                    + "	p.pesado e_balanca,\n"
                    + "	p.descricao descricaoCompleta,\n"
                    + "	p.desc_resum descricaoReduzida,\n"
                    + "	p.desc_resum descricaoGondola,\n"
                    + "	l.cd_linha id_familia,\n"
                    + "	d.cd_depto merc1,\n"
                    + "	s.cd_secao merc2,\n"
                    + "	cat.cd_categprd merc3,\n"
                    + "	p.peso_brt pesoBruto,\n"
                    + "	p.peso_liq pesoLiquido,\n"
                    + "	e.qtde estoque,\n"
                    + "	(pe.PercMgBr * 100) margem,\n"
                    + "	pc.vl_custo_sem_imposto custoSemImposto,\n"
                    + "	pc.vl_cust_brt custoComImposto,\n"
                    + "	pe.vl_preco precovenda,\n"
                    + "	p.ativo situacaoCadastro,\n"
                    + "	p.cd_prod_ncm ncm,\n"
                    + "	c.Cest cest,\n"
                    + "	nt.cst_pis_cofins piscofinsCstDebito,\n"
                    + "	nt.cst_pis_cofins piscofinsCstCredito,\n"
                    + "	nt.cd_cst_nat_receita piscofinsNaturezaReceita,\n"
                    + " case\n"
                    + "		when ISNULL (icms.cd_sit_trib_esp,0 ) = ''\n"
                    + "		then 0\n"
                    + "		else ISNULL (icms.cd_sit_trib_esp,0 )\n"
                    + "	end Cst,\n"
                    + "	icms.aliq_icm icmsAliqSaida,\n"
                    + "	ISNULL(icms.perc_red_baseicm_esp,0)  icmsReducao\n"
                    + "from\n"
                    + "	produto p\n"
                    + "left join preco pe on\n"
                    + "	p.cd_prod = pe.cd_prod and pe.cd_tabela = '03'\n"
                    + "left join produto_custo pc on\n"
                    + "	p.cd_prod = pc.cd_prod and pc.cd_emp = " + getLojaOrigem() + " and pc.tp_custo = 'CUE'\n"
                    + "left join Cest c on\n"
                    + "	p.CestID = c.CestID\n"
                    + "left join estoque e on\n"
                    + "	e.cd_prod = p.cd_prod and e.cd_emp = " + getLojaOrigem() + " and e.cd_local = 'CENTRAL'\n"
                    + "left join linha l on\n"
                    + "	p.cd_linha = l.cd_linha\n"
                    + "left join secao s on\n"
                    + "	l.cd_secao = s.cd_secao\n"
                    + "left join depto d on\n"
                    + "	s.cd_depto = d.cd_depto\n"
                    + "left join categprd cat on\n"
                    + "	l.cd_categprd = cat.cd_categprd\n"
                    + "left join prod_ncm pn on\n"
                    + "	p.cd_prod_ncm = pn.cd_prod_ncm\n"
                    + "left join cst_nat_receita nt on\n"
                    + "	pn.seq_cst_nat_receita = nt.seq_cst_nat_receita\n"
                    + "left join IcmProdEmpresa icms on\n"
                    + "	p.cd_prod = icms.cd_prod and icms.est_de = 'SP'	and icms.est_para = 'SP' and icms.CdEmp = " + getLojaOrigem()
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setTipoEmbalagemVolume(rst.getString("tipoEmbalagem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setMargemMaxima(rst.getDouble("margem"));
                    imp.setMargemMinima(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsCstCredito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsCstDebito"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setDataAlteracao(rst.getDate("dataAlteracao"));
                    imp.setEan(rst.getString("ean"));
                    if (rst.getString("id").equals("6395")) {
                        System.out.println("inferno");
                    }
                    if (rst.getString("ean") == null) {
                        imp.setEan("9999999" + rst.getString("id"));
                    } else {
                        if (rst.getString("ean").equals("9999999999999")) {
                            System.out.println("13" + " - " + "9999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("id").length()) + rst.getString("id"));
                        }
                        if (rst.getString("ean").equals("99999999999999")) {
                            System.out.println("14" + " - " + "99999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("id").length()) + rst.getString("id"));
                        }
                        if (rst.getInt("situacaoCadastro") == 0) {
                            System.out.println(rst.getInt("id") + " - " + "ativo 0");
                            imp.setEan("9999999" + rst.getString("id"));
                        }
                        if (rst.getString("ean").equals("9999999999999") && rst.getInt("situacaoCadastro") == 0) {
                            System.out.println("13" + " - " + "9999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("id").length()) + rst.getString("id"));
                        }
                    }
                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoEmbalagemCotacao"));
                    imp.setQtdEmbalagem(rst.getInt("embEan"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdEmbalagem"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));
                    imp.setSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    //imp.setSituacaoCadastro(rst.getInt("situacaoCadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));
                    //imp.setFornecedorFabricante(rst.getString("fornec"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        if (rst.getString("id").equals("6395")) {
                            System.out.println("inferno");
                        }
                        if (rst.getString("ean") == null) {
                            imp.setEan("9999999" + rst.getString("id"));
                        } else {
                            if (rst.getString("ean").equals("9999999999999")) {
                                System.out.println("13" + " - " + "9999999999999");
                                imp.setEan(rst.getString("ean").substring(rst.getString("id").length()) + rst.getString("id"));
                            }
                            if (rst.getString("ean").equals("99999999999999")) {
                                System.out.println("14" + " - " + "99999999999999");
                                imp.setEan(rst.getString("ean").substring(rst.getString("id").length()) + rst.getString("id"));
                            }
                            if (rst.getInt("situacaoCadastro") == 0) {
                                System.out.println(rst.getInt("id") + " - " + "ativo 0");
                                imp.setEan("9999999" + rst.getString("id"));
                            }
                            if (rst.getString("ean").equals("9999999999999") && rst.getInt("situacaoCadastro") == 0) {
                                System.out.println("13" + " - " + "9999999999999");
                                imp.setEan(rst.getString("ean").substring(rst.getString("id").length()) + rst.getString("id"));
                            }
                        }
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    String id = rst.getString("Cst") + "-" + rst.getString("icmsAliqSaida") + "-" + rst.getString("icmsReducao");

                    imp.setIcmsDebitoId(id);
                    imp.setIcmsDebitoForaEstadoId(id);
                    imp.setIcmsDebitoForaEstadoNfId(id);
                    imp.setIcmsCreditoId(id);
                    imp.setIcmsCreditoForaEstadoId(id);
                    imp.setIcmsConsumidorId(id);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT cd_linha id, descricao from linha l "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.cd_forn id,\n"
                    + "	f.raz_soc razao,\n"
                    + "	ef.logradouro endereco,\n"
                    + "	ef.bairro bairro,\n"
                    + "	ef.cep cep,\n"
                    + "	ef.municipio municipio,\n"
                    + "	ef.estado uf,\n"
                    + "	ef.numero numero,\n"
                    + "	f.inscricao ,\n"
                    + "	f.cgc_cpf cpfcnpj,\n"
                    + "	f.inscricao inscestadual,\n"
                    + "	f.nome_fant fantasia,\n"
                    + "	cf.e_mail email,\n"
                    + "	f.dt_cad dtcadastro,\n"
                    + "	f.ativo ATIVO,\n"
                    + "	tf.ddd,\n"
                    + "	tf.numero fone,\n"
                    + "	case\n"
                    + "		when pg.descricao is null\n"
                    + "		then ''\n"
                    + "		else pg.descricao\n"
                    + "	end condicaopgto\n"
                    + "from\n"
                    + "	fornec f\n"
                    + "left join end_for ef on\n"
                    + "	f.cd_forn = ef.cd_forn\n"
                    + "left join cont_for cf on\n"
                    + "	cf.cd_forn = f.cd_forn\n"
                    + "left join fma_pgto pg on f.cd_forma_pgto = pg.cd_forma_pgto \n"
                    + "LEFT join tel_for tf on f.cd_forn = tf.cd_forn and tf.seq = 1\n"
                    + "where\n"
                    + "	tp_end = 'CO'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("inscestadual") == null ? "" : rst.getString("inscestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("ddd") + rst.getString("fone"));

                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    imp.setAtivo((rst.getInt("ATIVO") == 1));
                    if (!rst.getString("condicaopgto").equals("A VISTA") && !rst.getString("condicaopgto").equals("")) {
                        String[] menosDias = rst.getString("condicaopgto").split(" ");
                        String[] numerodDias = menosDias[0].trim().split("/");
                        for (String dia : numerodDias) {
                            imp.setCondicaoPagamento(Integer.parseInt(dia));
                        }
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	distinct f.cd_forn id_forn,\n"
                    + "	p.cd_prod id_prod,\n"
                    + "	p.cd_prod_fabric externo,\n"
                    + "	p.cd_emp\n"
                    + "from\n"
                    + "	forn_fabr f\n"
                    + "join produto p on\n"
                    + "	p.cd_fabric = f.cd_fabric\n"
                    + "join fabric f2 on\n"
                    + "	f.cd_fabric = f2.cd_fabric"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("id_forn"));
                    imp.setIdProduto(rst.getString("id_prod"));
                    imp.setCodigoExterno(rst.getString("externo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.cd_prod produto,\n"
                    + "	c.cd_barra ean,\n"
                    + "	c.unid_vda un,\n"
                    + "	p.ativo ativo,\n"
                    + "	u.qtde_unid qtd\n"
                    + "from\n"
                    + "	cd_barra_unprd c\n"
                    + "join unid_prod u on\n"
                    + "	c.cd_prod = u.cd_prod\n"
                    + "	and c.unid_vda = u.unid_vda\n"
                    + "	and u.ativo = 1\n"
                    + "LEFT join produto p on p.cd_prod = c.cd_prod "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produto"));
                    imp.setEan(rst.getString("ean"));
                    if (rst.getString("produto").equals("6395")) {
                        System.out.println("inferno");
                    }
                    if (rst.getString("ean") == null) {
                        imp.setEan("9999999" + rst.getString("produto"));
                    } else {
                        if (rst.getString("ean").equals("9999999999999")) {
                            System.out.println("13" + " - " + "9999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("produto").length()) + rst.getString("produto"));
                        }
                        if (rst.getString("ean").equals("99999999999999")) {
                            System.out.println("14" + " - " + "99999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("produto").length()) + rst.getString("produto"));
                        }
                        if (rst.getInt("ativo") == 0) {
                            System.out.println(rst.getInt("produto") + " - " + "ativo 0");
                            imp.setEan("9999999" + rst.getString("produto"));
                        }
                        if (rst.getString("ean").equals("9999999999999") && rst.getInt("ativo") == 0) {
                            System.out.println("13" + " - " + "9999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("produto").length()) + rst.getString("produto"));
                        }
                        if (rst.getString("ean").equals("99999999999999") && rst.getInt("ativo") == 0) {
                            System.out.println("14" + " - " + "99999999999999");
                            imp.setEan(rst.getString("ean").substring(rst.getString("produto").length()) + rst.getString("produto"));
                        }
                    }
                    imp.setQtdEmbalagem(rst.getInt("qtd"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "	SELECT distinct\n"
                    + "	c.cd_clien id,\n"
                    + "    c.cgc_cpf cpfcnpj,\n"
                    + "    c.nome razao,\n"
                    + "    c.nome_res fantasia,\n"
                    + "    c.ativo ativo,\n"
                    + "    e.logradouro endereco,\n"
                    + "    e.numero numero,\n"
                    + "    e.bairro bairro,\n"
                    + "    e.municipio municipio,\n"
                    + "    e.estado uf,\n"
                    + "    e.cep cep,\n"
                    + "    c.dt_cad dataCadastro,\n"
                    + "    tc.ddd,\n"
                    + "    tc.numero fone,\n"
                    + "    c.e_mail email,\n"
                    + "    c.vl_lim_cred limite\n"
                    + "FROM\n"
                    + "	cliente c \n"
                    + "left join end_cli e on c .cd_clien = e.cd_clien\n"
                    + "left join tel_cli tc on c.cd_clien = tc.cd_clien \n"
                    + "where c.cd_clien != 223767"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    //imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setCelular(rst.getString("ddd") + rst.getString("fone"));
                    imp.setTelefone(rst.getString("ddd") + rst.getString("fone"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEmail(rst.getString("email"));

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	DISTINCT \n"
                    + "	TitrecID id,\n"
                    + "	cd_clien idcliente,\n"
                    + "	dt_emis dataemissao,\n"
                    + "	dt_venc_orig datavencimento,\n"
                    + "	nu_tit_emp_fat numerodocumento,\n"
                    + "	ValorSaldo,\n"
                    + "	valor,\n"
                    + "	vl_pago \n"
                    + "from\n"
                    + "	titrec t\n"
                    + "where\n"
                    + "	situacao = 'AB'\n"
                    + "	and cd_emp = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

//    private String removerAcentos(String texto) {
//        texto = texto != null ? Normalizer.normalize(texto, Normalizer.Form.NFD) : "";
//        texto = texto != null ? texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "") : "";
//        texto = texto != null ? texto.replaceAll("�", "C") : "";
//        texto = texto != null ? texto.replaceAll("[^\\p{ASCII}]", "") : "";
//
//        return texto;
//    }
    private int gerarCodigoAtacado() {
        Object[] options = {"ean atacado", "ean13", "ean14", "Cancelar"};
        int decisao = JOptionPane.showOptionDialog(null, "Escolha uma opção de ean",
                "Gerar eans", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return decisao;
    }
}
