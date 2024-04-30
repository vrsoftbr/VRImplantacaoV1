package vrimplantacao2_5.dao.sistema;

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
import javax.swing.JOptionPane;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Wagner
 */
public class HRTech2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "HRTECH";
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
                    "select distinct \n"
                    + "    concat(\n"
                    + "        CAST(ts.situatribu AS VARCHAR(50)), \n"
                    + "        '-', \n"
                    + "        CAST(ts.aliquotapdv AS VARCHAR(50)), \n"
                    + "        '-', \n"
                    + "        CAST(ts.mrger AS VARCHAR(50))\n"
                    + "    ) as id,\n"
                    + "	ts.situatribu cst,\n"
                    + "	ts.aliquotapdv icms,\n"
                    + "	ts.mrger icmsreducao\n"
                    + "from\n"
                    + "	fl300est p\n"
                    + "join fl304ven v on (p.codigoplu = v.codigoplu)\n"
                    + "join fl309est e on (p.codigoplu = e.codigoplu) and \n"
                    + "	 v.codigoloja = e.codigoloja\n"
                    + "join fl303cus c on (p.codigoplu = c.codigoplu) and\n"
                    + "	v.codigoloja = c.codigoloja\n"
                    + "join fltabncm_pro ncm on (p.codigoplu = ncm.codigoplu)\n"
                    + "join fl301est est on (p.codigoplu = est.codigoplu) and\n"
                    + "	est.codigoloja = v.codigoloja\n"
                    + "join fltribut ts on (est.codtribsai = ts.codigotrib) and\n"
                    + "	v.codigoloja = ts.codigoloja\n"
                    + "left join fl328bal bal on (p.codigoplu = bal.codigoplu)\n"
                    + "left join hrpdv_prepara_pro pis on (pis.codigoplu = p.codigoplu) and\n"
                    + "	pis.codigoloja = v.codigoloja\n"
                    + "where\n"
                    + "	v.codigoloja = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("id"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("icmsreducao")
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
                    + "	m1.codmerc1,\n"
                    + "	m1.descmerc1,\n"
                    + "	m2.codmerc2,\n"
                    + "	m2.descmerc2,\n"
                    + "	m3.codmerc3,\n"
                    + "	m3.descmerc3,\n"
                    + "	m4.codmerc4,\n"
                    + "	m4.descmerc4\n"
                    + "from\n"
                    + "	(\n"
                    + "		select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc35desc descmerc1\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup = '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = ''\n"
                    + "	) m1\n"
                    + "	join (\n"
                    + "		select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc03grup codmerc2,\n"
                    + "			gruc35desc descmerc2\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup != '' and gruc03subg = '' and gruc03fami = '' and gruc03subf = ''\n"
                    + "	) m2 on\n"
                    + "		m1.codmerc1 = m2.codmerc1\n"
                    + "	join (\n"
                    + "			select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc03grup codmerc2,\n"
                    + "			gruc03subg codmerc3,\n"
                    + "			gruc35desc descmerc3\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup != '' and gruc03subg != '' and gruc03fami = '' and gruc03subf = ''\n"
                    + "	) m3 on\n"
                    + "		m2.codmerc1 = m3.codmerc1 and m2.codmerc2 = m3.codmerc2\n"
                    + "	join (\n"
                    + "			select\n"
                    + "			gruc03seto codmerc1,\n"
                    + "			gruc03grup codmerc2,\n"
                    + "			gruc03subg codmerc3,\n"
                    + "			gruc03fami codmerc4,\n"
                    + "			gruc35desc descmerc4\n"
                    + "		from\n"
                    + "			fl100dpt s\n"
                    + "		where\n"
                    + "			gruc03seto != '' and gruc03grup != '' and gruc03subg != '' and gruc03fami != '' and gruc03subf = ''\n"
                    + "	) m4 on\n"
                    + "		m3.codmerc1 = m4.codmerc1 and m3.codmerc2 = m4.codmerc2 and m3.codmerc3 = m4.codmerc3\n"
                    + "order by\n"
                    + "	1,3,5,7"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    imp.setMerc3ID(rst.getString("codmerc4"));
                    imp.setMerc3Descricao(rst.getString("descmerc4"));
                    imp.setMerc3ID(rst.getString("codmerc5"));
                    imp.setMerc3Descricao(rst.getString("descmerc5"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	e.codigoplu idproduto,\n"
                        + "	e.estc13codi ean,\n"
                        + "	e.qtd_emb_vd quantidade,\n"
                        + "	e.por_des_vd porcentagematacado,\n"
                        + "	cast(p.vendaatua as numeric(10,4)) precovenda,\n"
                        + "	cast(round((p.vendaatua - (p.vendaatua * e.por_des_vd / 100)), 2) as numeric(10,4)) precovendaatacado\n"
                        + "from\n"
                        + "	FL322EAN e\n"
                        + "join\n"
                        + "	HRPDV_PREPARA_PRO p on (e.codigoplu = p.codigoplu)\n"
                        + "where\n"
                        + "	e.por_des_vd > 0 and\n"
                        + "	e.qtd_emb_vd > 1 and\n"
                        + "	p.codigoloja = " + getLojaOrigem())) {
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        String id = rs.getString("idproduto");
                        id = id.substring(0, id.length() - 1);
                        imp.setImportId(id);
                        imp.setEan(rs.getString("ean"));
                        imp.setPrecovenda(rs.getDouble("precovenda"));
                        imp.setAtacadoPorcentagem(rs.getDouble("porcentagematacado"));
                        imp.setQtdEmbalagem(rs.getInt("quantidade"));

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.codigoplu id, \n"
                    + "	case  \n"
                    + "		when p.estc13codi = '' then  \n"
                    + "		p.codigoplu  \n"
                    + "	else p.estc13codi end ean,\n"
                    + "	p.estc35desc descricaocompleta,\n"
                    + "	p.descreduzi descricaoreduzida,\n"
                    + "	p.dtcadastro,\n"
                    + "	p.situacao,\n"
                    + "	p.estc01peso pesavel,\n"
                    + "	coalesce(bal.diasvalida, 0) validade,\n"
                    + "	coalesce(bal.peso_varia, '') peso,\n"
                    + "	p.estc03seto merc1, \n"
                    + "	p.estc03grup merc2, \n"
                    + "	p.estc03subg merc3, \n"
                    + "	p.estc03fami merc4, \n"
                    + //"	p.estc03subf merc5,\n" +
                    "	est.estn05mrge margem,\n"
                    + "	est.qtd_emb_co qtdembalagemcotacao,\n"
                    + "	est.qtd_emb_vd qtdembalagem,\n"
                    + "	est.tip_emb_vd embalagem,\n"
                    + "	v.vendaatua venda,\n"
                    + "	c.custoliqui custocomimposto,\n"
                    + "	c.custorepos custosemimposto,\n"
                    + "	e.estoqueatu estoque,\n"
                    + "	est.estn10maxi estoquemaximo,\n"
                    + "	est.estn10mini estoqueminimo,\n"
                    + "	ncm.cod_ncm ncm,\n"
                    + "	ncm.id_cest cest,\n"
                    + "	ts.situatribu cst,\n"
                    + "	ts.aliquotapdv icms,\n"
                    + "	ts.mrger icmsreducao,\n"
                    + "	pis.cstpis cstpis,\n"
                    + "	pis.cstcof cstcofins, \n"
                    + "concat(\n"
                    + "        CAST(ts.situatribu AS VARCHAR(50)), \n"
                    + "        '-', \n"
                    + "        CAST(ts.aliquotapdv AS VARCHAR(50)), \n"
                    + "        '-', \n"
                    + "        CAST(ts.mrger AS VARCHAR(50))\n"
                    + "    ) as icmsId"
                    + "from\n"
                    + "	fl300est p\n"
                    + "join fl304ven v on (p.codigoplu = v.codigoplu)\n"
                    + "join fl309est e on (p.codigoplu = e.codigoplu) and \n"
                    + "	 v.codigoloja = e.codigoloja\n"
                    + "join fl303cus c on (p.codigoplu = c.codigoplu) and\n"
                    + "	v.codigoloja = c.codigoloja\n"
                    + "join fltabncm_pro ncm on (p.codigoplu = ncm.codigoplu)\n"
                    + "join fl301est est on (p.codigoplu = est.codigoplu) and\n"
                    + "	est.codigoloja = v.codigoloja\n"
                    + "join fltribut ts on (est.codtribsai = ts.codigotrib) and\n"
                    + "	v.codigoloja = ts.codigoloja\n"
                    + "left join fl328bal bal on (p.codigoplu = bal.codigoplu)\n"
                    + "left join hrpdv_prepara_pro pis on (pis.codigoplu = p.codigoplu) and\n"
                    + "	pis.codigoloja = v.codigoloja\n"
                    + "where\n"
                    + "	v.codigoloja = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "	p.codigoplu"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setCodMercadologico5(rst.getString("merc5"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("embalagem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("venda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setPiscofinsCstCredito(rst.getString("cstcofins"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setEan(rst.getString("ean"));
                    imp.setSituacaoCadastro(rst.getInt("situacao") == 0 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.seteBalanca("S".equals(rst.getString("pesavel")));
                    imp.setValidade(rst.getInt("validade"));

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
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("embalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    String icms = rst.getString("icmsId");
                    imp.setIcmsDebitoId(icms);
                    imp.setIcmsDebitoForaEstadoId(icms);
                    imp.setIcmsDebitoForaEstadoNfId(icms);
                    imp.setIcmsCreditoId(icms);
                    imp.setIcmsCreditoForaEstadoId(icms);
                    imp.setIcmsConsumidorId(icms);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {        
        List<FornecedorIMP> result = new ArrayList<>();
        int decisao = JOptionPane.showConfirmDialog(null, "Em alguns casos, para importar contas a pagar será necesário importar funcionários como fornecedor.\nDeseja importar funcionários?\nSe sim, depois repita a operação e selecione a negativa para importar somente os fornecedores.");
        if (decisao == JOptionPane.YES_OPTION){
            result = getFornecedoresFuncionarios();
            return result;
        }
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.codigoenti id_fornecedor,\n" +
                    "	f.datusucada datacadastro,\n" +
                    "	cpf.nomeentida razao,\n" +
                    "	cpf.nomapelido fantasia,\n" +
                    "	cpf.codinsc_rg rgie,\n" +
                    "	cpf.numcgc_cpf cnpj,\n" +
                    "	cpf.tipempresa tipo,\n" +
                    "	cpf.datanascim datanascimento,\n" +
                    "	cpf.codcepcome cep,\n" +
                    "	cpf.compcomerc numero,\n" +
                    "	f.forn02visi prazovisita,\n" +
                    "	f.forn02pent prazoentrega,\n" +
                    "	rtrim(pg.nomcondpgt) condicaopagamento,\n" +
                    "	f.diasemanas,\n" +
                    "	f.prod_rural produtorural,\n" +
                    "	ltrim(cep.titulo + ' ' + cep.logradouro) endereco,\n" +
                    "	cep.bairro,\n" +
                    "	cep.cidade,\n" +
                    "	cep.estado,\n" +
                    "	tel.telefone01 telefone\n" +
                    "from \n" +
                    "	FL800FOR f\n" +
                    "left join flcgccpf cpf on (f.id_entidade = cpf.id_entidade)\n" +
                    "left join fl423cep cep on (f.codigoenti = cep.codigoenti)\n" +
                    "left join fltelefo_cad tel on (f.codigoenti = tel.id_cadastro)\n" +
                    "left join flcondpg pg on (f.codcondpgt = pg.codcondpgt)\n" +
                    "where\n" +
                    "	cep.tipocadast = 'FOR' and\n" +
                    "	tel.TP_CADASTRO = 'FOR'\n" +
                    "order by\n" +
                    "	f.codigoenti")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id_fornecedor"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setIe_rg(rs.getString("rgie"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setTipo_inscricao("J".equals(rs.getString("tipo")) ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setPrazoVisita(rs.getInt("prazovisita"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    
                    String pagamento[] = rs.getString("condicaopagamento").split("/");
                    for(String pag : pagamento) {
                        imp.setCondicaoPagamento(Utils.stringToInt(pag.trim()));
                    }
                    if (rs.getInt("produtorural") == 1) {
                        imp.setProdutorRural();
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.copiarEnderecoParaCobranca();

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    /*
    Este método foi serve para trazer a importação de funcionários
    que está em uma tabela especifíca no HRTech. Pois é necessário
    para a importação de contas a pagar dos funcionários. Após usar o mesmo,
    comentar este método e descomentar o método principal getFornecedores()
    */
    public List<FornecedorIMP> getFornecedoresFuncionarios() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	'U' + fun.codigoenti id_fornecedor,\n" +
                    "	getdate() datacadastro,\n" +
                    "	cpf.nomeentida razao,\n" +
                    "	cpf.nomapelido fantasia,\n" +
                    "	cpf.codinsc_rg rgie,\n" +
                    "	cpf.numcgc_cpf cnpj,\n" +
                    "	cpf.tipempresa tipo,\n" +
                    "	cpf.datanascim datanascimento,\n" +
                    "	cpf.codceplent cep,\n" +
                    "	cpf.complocent numero,\n" +
                    "	0 prazovisita,\n" +
                    "	0 prazoentrega,\n" +
                    "	0 condicaopagamento,\n" +
                    "	0 diasemanas,\n" +
                    "	0 produtorural,\n" +
                    "	ltrim(cep.titulo + ' ' + cep.logradouro) endereco,\n" +
                    "	cep.bairro,\n" +
                    "	cep.cidade,\n" +
                    "	cep.estado,\n" +
                    "	0 telefone\n" +
                    "from \n" +
                    "	FL040FUN fun\n" +
                    "join \n" +
                    "	flcgccpf cpf on (fun.codcgccpfs = cpf.codigoenti)\n" +
                    "left join fl423cep cep on (fun.codigoenti = cep.codigoenti) and\n" +
                    "	cep.tipocadast = 'FUN'")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id_fornecedor"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setIe_rg(rs.getString("rgie"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setTipo_inscricao("J".equals(rs.getString("tipo")) ? TipoInscricao.JURIDICA : TipoInscricao.FISICA);
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setPrazoVisita(rs.getInt("prazovisita"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));
                    
                    String pagamento[] = rs.getString("condicaopagamento").split("/");
                    for(String pag : pagamento) {
                        imp.setCondicaoPagamento(Utils.stringToInt(pag.trim()));
                    }
                    if (rs.getInt("produtorural") == 1) {
                        imp.setProdutorRural();
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.copiarEnderecoParaCobranca();

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigoenti id_fornecedor,\n"
                    + "	codigoplu id_produto,\n"
                    + "	coalesce(dataaltera, '') dataalteracao,\n"
                    + "	coalesce(qtd_emb_co, 1) qtdcotacao,\n"
                    + "	coalesce(referencia, '') referencia\n"
                    + "from \n"
                    + "	FL324FOR \n"
                    + "where\n"
                    + "	codigoenti != '' and\n"
                    + "	codigoenti not in (100001)\n"
                    + "order by\n"
                    + "	codigoenti, codigoplu")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    String id_produto = rs.getString("id_produto");
                    id_produto = rs.getString("id_produto").substring(0, id_produto.length() - 1);
                    imp.setIdProduto(id_produto);
                    imp.setQtdEmbalagem(rs.getDouble("qtdcotacao"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigoplu idproduto,\n"
                    + "	estc13codi ean,\n"
                    + "	qtd_emb_vd quantidade,\n"
                    + "	por_des_vd desconto\n"
                  + "from\n"
                    + "	FL322EAN")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    String id = rs.getString("idproduto");
                    id = id.substring(0, id.length() - 1);
                    imp.setImportId(id);
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("quantidade"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "    c.codigoenti id,\n"
                    + "    cpf.nomeentida razao,\n"
                    + "    cpf.nomapelido fantasia,\n"
                    + "    cpf.codinsc_rg rgie,\n"
                    + "    cpf.numcgc_cpf cnpj,\n"
                    + "    cpf.datanascim datanascimento,\n"
                    + "    c.clin12limi limite,\n"
                    + "    c.clic01stat situacao,\n"
                    + "    c.codigosexo sexo,\n"
                    + "    c.estadocivi estadocivil,\n"
                    + "    c.datacadast datacadastro,\n"
                    + "    cpf.codcepresi cep,\n"
                    + "    cpf.compreside numero,\n"
                    + "    ltrim(cep.titulo + ' ' + cep.logradouro) endereco,\n"
                    + "    cep.bairro,\n"
                    + "    cep.cidade,\n"
                    + "    cep.estado,\n"
                    + "    tel.telefone01 telefone\n"
                    + "from\n"
                    + "    FL400CLI c \n"
                    + "left join\n"
                    + "    flcgccpf cpf on (c.codcgccpfs = cpf.codigoenti)\n"
                    + "left join\n"
                    + "    fl423cep cep on (c.id_cliente = cep.id_cliente) and\n"
                    + "    cpf.codcepresi = cep.codigocep\n"
                    + "left join\n"
                    + "    fltelefo_cad tel on (c.id_cliente = tel.id_cadastro)\n"
                    + "where\n"
                    + "    cep.tipocadast = 'CLI'\n"
                    + "union\n"
                    + "select \n"
                    + "	distinct\n"
                    + "	f.codigoenti id,\n"
                    + "	cpf.nomeentida razao,\n"
                    + "	cpf.nomapelido fantasia,\n"
                    + "	cpf.codinsc_rg rgie,\n"
                    + "	cpf.numcgc_cpf cnpj,\n"
                    + "	cpf.datanascim datanascimento,\n"
                    + "	0 limite,\n"
                    + "	1 situacao,\n"
                    + "	'M' sexo,\n"
                    + "	0 estadocivil,\n"
                    + "	f.datusucada datacadastro,\n"
                    + "	cpf.codcepcome cep,\n"
                    + "	cpf.compcomerc numero,\n"
                    + "	ltrim(cep.titulo + ' ' + cep.logradouro) endereco,\n"
                    + "	cep.bairro,\n"
                    + "	cep.cidade,\n"
                    + "	cep.estado,\n"
                    + "	tel.telefone01 telefone\n"
                    + "from \n"
                    + "	FL800FOR f\n"
                    + "left join flcgccpf cpf on (f.id_entidade = cpf.id_entidade)\n"
                    + "left join fl423cep cep on (f.codigoenti = cep.codigoenti)\n"
                    + "left join fltelefo_cad tel on (f.codigoenti = tel.id_cadastro)\n"
                    + "join FL700FIN cr on (f.codigoenti = cr.codigoenti)\n"
                    + "where\n"
                    + "	cep.tipocadast = 'FOR' and\n"
                    + "	tel.TP_CADASTRO = 'FOR' and\n"
                    + "	cr.TIPOLANCAM = 'R'")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setInscricaoestadual(rs.getString("rgie").trim());
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setAtivo(rs.getInt("situacao") == 0 ? true : false);
                    imp.setSexo("F".equals(rs.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEstadoCivil(rs.getInt("estadocivil") == 0 ? TipoEstadoCivil.CASADO : TipoEstadoCivil.SOLTEIRO);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setTelefone(rs.getString("telefone"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	distinct\n" +
                    "	f.codi_relacio id,\n" +
                    "	cl.codigoenti idcliente,\n" +
                    "	f.numcgc_cpf cnpj,\n" +
                    "	f.numeroecf ecf,\n" +
                    "	f.numerocoo coo,\n" +
                    "	f.datamovime data,\n" +
                    "	f.vdg_dia valor,\n" +
                    "	f.datadeposi vencimento \n" +
                    "from \n" +
                    "	vw305fin f\n" +
                    "join flcgccpf cpf on cast(f.numcgc_cpf as bigint) = cast(cpf.numcgc_cpf as bigint)\n" +
                    "join fl400cli cl on cpf.codigoenti = cl.codcgccpfs and\n" +
                    "	cl.id_entidade = cpf.id_entidade\n" +
                    "where  \n" +
                    "	f.datamovime >= '2005-01-01 00:00:00' and \n" +
                    "	f.codigofina in ('007') and \n" +
                    "	(ORIGEM != CASE WHEN \n" +
                    "		DATAMOVIME > '20131231' THEN 'C' ELSE '\\' END OR \n" +
                    "	 EXISTS (SELECT \n" +
                    "			CODI_RELACIO \n" +
                    "		 FROM \n" +
                    "			FL404CON \n" +
                    "            WHERE \n" +
                    "			CODIGOLOJA = f.CODIGOLOJA AND \n" +
                    "			CODI_RELACIO = f.CODI_RELACIO)) and\n" +
                    "	f.codigoloja = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	f.datamovime")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("data"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
    O código do fornecedor foi alterado para trazer os títulos de funcionários
    que está em uma tabela especifica para funcionários. Antes, é necessário importar
    os funcionários como fornecedor.
    */
    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        JOptionPane.showMessageDialog(null, "Em alguns casos é necessário importar os funcionários como fornecedor.");
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	numerolanc id,\n"
                    + "	case \n" +
                      " when tipocadast = 'U' \n" +
                      " then 'U' + codigoenti \n" +
                      " else \n" +
                      " codigoenti end idfornecedor,\n"
                    + "	notafiscal documento,\n"
                    + "	parcela,\n"
                    + "	datemissao emissao,\n"
                    + "	datvencime vencimento,\n"
                    + "	vlrtotalnf valor,\n"
                    + "	historico observacao,\n"
                    + " cast(datpagto as date) pagamento\n"
                    + "from\n"
                    + "	FL700FIN\n"
                    + "where\n"
                    + "	codigoloja = " + getLojaOrigem() + " and\n"
                    + "	tipolancam = 'P'\n"
                    + "order by\n"
                    + "	datvencime")) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("emissao"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setValor(rs.getDouble("valor"));
                    String dataPagamento = rs.getString("pagamento");
                    if ((dataPagamento != null) && (!"1900-01-01".equals(dataPagamento))) {
                        imp.setObservacao(rs.getString("observacao").trim() + " - FLAG_BAIXADO");
                    } else {
                        imp.setObservacao(rs.getString("observacao").trim());
                    }
                    imp.addVencimento(rs.getDate("vencimento"), imp.getValor());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

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
                SimpleDateFormat timestamp = new SimpleDateFormat("HHmm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = "".equals(rst.getString("horainicio").trim()) ? "0000" : rst.getString("horainicio");
                        String horaTermino = "".equals(rst.getString("horafim").trim()) ? "0000" : rst.getString("horafim");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cnpj"));
                        next.setNomeCliente(rst.getString("razao"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                        next.setChaveCfe(rst.getString("chavenfe"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	c.codi_relacio id,\n"
                    + "	coalesce(cl.codigoenti, '') idcliente,\n"
                    + " case c.vdl_dia when 0.00 then 1 else 0 end cancelado,\n" 
                    + "	c.numerocaix ecf,\n"
                    + "	c.numerocupo coo,\n"
                    + "	c.datamovime data,\n"
                    + "	c.vdg_dia subtotalimpressora,\n"
                    + "	c.numcgc_cpf cnpj,\n"
                    + "	c.hora_ini horainicio,\n"
                    + "	c.hora_fin horafim,\n"
                    + "	c.chave_nfe chavenfe,\n"
                    + "	coalesce(cpf.nomeentida, '') razao,\n"
                    + "	coalesce(cep.logradouro, '') endereco,\n"
                    + "	coalesce(cpf.complocent, '') complemento,\n"
                    + "	coalesce(cpf.compreside, '') numero,\n"
                    + "	coalesce(cep.bairro, '') bairro,\n"
                    + "	coalesce(cep.cidade, '') cidade,\n"
                    + "	coalesce(cep.estado, '') estado,\n"
                    + "	coalesce(cpf.codcepresi, '') cep\n"
                    + "from\n"
                    + "	FL305CUP c\n"
                    + "left join flcgccpf cpf on \n"
                    + "	(case when (cast(c.numcgc_cpf as bigint)) = 0 then 1 \n"
                    + "		else (cast(c.numcgc_cpf as bigint)) end = cast(cpf.numcgc_cpf as bigint))\n"
                    + "left join FL400CLI cl on (cl.id_entidade = cpf.id_entidade)\n"
                    + "left join fl423cep cep on (cl.id_cliente = cep.id_cliente) and\n"
                    + "	cep.codigocep = cpf.codcepcobr and\n"
                    + "	cep.tipocadast = 'CLI'\n"
                    + "where\n"
                    + "	c.codigoloja = " + idLojaCliente + " and\n"
                    + "	cast(c.datamovime as date) between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'\n"
                    + "order by\n"
                    + "	c.datamovime, c.numerocupo";
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

                        next.setId(rst.getString("id"));
                        next.setVenda(rst.getString("id_venda"));
                        String id = rst.getString("id_produto");
                        next.setCancelado(rst.getInt("cancelado") == 1 ? true : false);
                        id = id.substring(0, id.length() - 1);
                        next.setProduto(id);
                        if (rst.getString("id_produto").equals(rst.getString("codigobarras"))) {
                            next.setCodigoBarras(next.getProduto());
                        } else {
                            next.setCodigoBarras(rst.getString("codigobarras"));
                        }
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setIcmsAliq(rst.getDouble("icms"));
                        next.setIcmsCst(rst.getInt("cst"));
                        next.setIcmsReduzido(rst.getDouble("icmsreducao"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + " it.codi_relacio + '-' + cast(coalesce(it.id_item, 1) as varchar) + '-' + cast(it.vdg_dia as varchar) id,\n" 
                    + "	it.codi_relacio id_venda,\n"
                    + "	it.codigoplu id_produto,\n"
                    + "	pr.estc35desc descricao,\n"
                    + " case upper(it.origem) when 'C' then 1 else 0 end cancelado,\n"
                    + "   case\n"
                    + "		pr.estc13codi when '' then pr.codigoplu else\n"
                    + "		pr.estc13codi end as codigobarras,\n"
                    + "	pr.tip_emb_vd unidade,\n"
                    + "	it.datamovime data,\n"
                    + "	it.id_item sequencia,\n"
                    + "	it.vdg_dia total,\n"
                    + "	it.qtd_dia quantidade,\n"
                    + "	tr.valoricm icms,\n"
                    + "	tr.situatribu cst,\n"
                    + "	tr.mrger icmsreducao\n"
                    + "from\n"
                    + "	FL305DIA it\n"
                    + "join FLTRIBUT tr on (it.codigoloja = tr.codigoloja) and\n"
                    + "	it.codtribsai = tr.codigotrib\n"
                    + "join HRPDV_PREPARA_PRO pr on (it.codigoplu = pr.codigoplu) and\n"
                    + "	it.codigoloja = pr.codigoloja\n"
                    + "where \n"
                    + "	it.codigoloja = " + idLojaCliente + " and\n"
                    + "	(it.datamovime between convert(date, '" + VendaIterator.FORMAT.format(dataInicio) + "', 23) and convert(date, '" + VendaIterator.FORMAT.format(dataTermino) + "', 23))\n"
                    + "order by\n"
                    + "	it.codi_relacio, it.id_item";
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
