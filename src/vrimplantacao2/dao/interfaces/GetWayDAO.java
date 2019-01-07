package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.BancoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.cadastro.verba.receber.ReceberVerbaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberVerbaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class GetWayDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(GetWayDAO.class.getName());

    public int v_tipoDocumento;
    public int v_tipoDocumentoCheque;
    public boolean v_usar_arquivoBalanca;
    public boolean v_usar_arquivoBalancaUnificacao;
    public boolean usarMargemBruta = false;
    public String v_lojaMesmoId;
    public boolean usarQtdEmbDoProduto = false;
    public boolean usaMargemLiquidaPraticada = false;
    private boolean desconsiderarSetorBalanca = false;
    private boolean pesquisarKGnaDescricao;
    private boolean utilizarIdIcmsNaEntrada = false;

    public void setUsarQtdEmbDoProduto(boolean usarQtdEmbDoProduto) {
        this.usarQtdEmbDoProduto = usarQtdEmbDoProduto;
    }

    @Override
    public String getSistema() {
        return "GetWay" + v_lojaMesmoId;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CODLOJA id,\n"
                    + "	descricao\n"
                    + "from\n"
                    + "	LOJA\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codprod, "
                    + "     cast(p.dataini as date) as dataini, "
                    + "     cast(p.datafim as date) as datafim, "
                    + "     p.preco_unit precooferta, "
                    + "     prod.preco_unit as preconormal "
                    + "from promocao p "
                    + "inner join produtos prod on p.codprod = prod.codprod "
                    + "where datafim >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataTermino) + "' "
                    + "order by p.dataini"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("codprod"));
                    imp.setDataInicio(rst.getDate("dataini"));
                    imp.setDataFim(rst.getDate("datafim"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
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
                    "select "
                    + "CODFAMILIA, "
                    + "descricao "
                    + "from "
                    + "FAMILIA"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFAMILIA"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.CODCRECEITA m1,\n"
                    + "	coalesce(m1.DESCRICAO, 'PADRAO') m1_desc,\n"
                    + "	p.CODGRUPO m2,\n"
                    + "	coalesce(m2.DESCRICAO, 'PADRAO') m2_desc,\n"
                    + "	p.CODCATEGORIA m3,\n"
                    + "	coalesce(m3.DESCRICAO, 'PADRAO') m3_desc\n"
                    + "from\n"
                    + "	(select distinct\n"
                    + "		codcreceita,\n"
                    + "		codgrupo,\n"
                    + "		codcategoria\n"
                    + "	from\n"
                    + "		PRODUTOS) p\n"
                    + "	left join creceita m1 on\n"
                    + "		p.codcreceita = m1.codcreceita\n"
                    + "	left join grupo m2 on\n"
                    + "		p.CODCRECEITA = m2.CODCRECEITA and\n"
                    + "		p.CODGRUPO = m2.CODGRUPO\n"
                    + "	left join categoria m3 on\n"
                    + "		p.CODCATEGORIA = m3.CODCATEGORIA\n"
                    + "order by\n"
                    + "	m1, m2, m3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc3ID(rst.getString("m3"));
                    imp.setMerc1Descricao(rst.getString("m1_desc"));
                    imp.setMerc2Descricao(rst.getString("m2_desc"));
                    imp.setMerc3Descricao(rst.getString("m3_desc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        
        LOG.config("Parametros:\r\n"
                + " - Desconsiderar setor de balança:" + desconsiderarSetorBalanca + "\r\n");

        StringBuilder rep = new StringBuilder();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            MultiMap<Comparable, Void> icms = new MultiMap<>();
            try (Statement st = Conexao.createStatement()) {
                try (ResultSet rs = st.executeQuery(
                        "select \n"
                        + "	situacaotributaria cst, \n"
                        + "	porcentagem aliq, \n"
                        + "	reduzido \n"
                        + "from \n"
                        + "	aliquota \n"
                        + "where \n"
                        + "	id_situacaocadastro = 1 \n"
                        + "order by \n"
                        + "	1, 2, 3"
                )) {
                    while (rs.next()) {
                        icms.put(
                                null,
                                rs.getInt("cst"),
                                MathUtils.trunc(rs.getDouble("aliq"), 2),
                                MathUtils.trunc(rs.getDouble("reduzido"), 1)
                        );
                    }
                }
            }
            int qtdBalanca = 0, qtdNormal = 0;
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	prod.codprod id,\n" +
                    "	prod.dtinclui datacadastro,\n" +
                    "	prod.dtaltera dataalteracao,\n" +
                    "	ean.ean,\n" +
                    "	case when prod.qtd_emb < 1 then 1 else prod.qtd_emb end qtdembalagemcotacao,\n" +
                    "	ean.qtdembalagem,\n" +
                    "	prod.unidade,\n" +
                    "	case when prod.codsetor is null then 0 else 1 end e_balanca,\n" +
                    "	prod.validade,\n" +
                    "	prod.descricao descricaocompleta,\n" +
                    "	prod.desc_pdv descricaoreduzida,\n" +
                    "	coalesce(prod.codcreceita, 1) as merc1,\n" +
                    "	coalesce(prod.codgrupo, 1) as merc2,\n" +
                    "	coalesce(prod.codcategoria, 1) as merc3,\n" +
                    "	fam.codfamilia id_familia,\n" +
                    "	prod.peso_bruto pesobruto,\n" +
                    "	prod.peso_liq pesoliquido,\n" +
                    "	prod.estoque_max estoquemaximo,\n" +
                    "	prod.estoque_min estoqueminimo,\n" +
                    "	prod.estoque,\n" +
                    "	prod.preco_cust custo,\n" +
                    "	prod.preco_unit preco,\n" +
                    "	prod.margem_bruta,\n" +
                    "	prod.margem_param,\n" +
                    "	prod.lucroliq margemliquidapraticada,\n" +
                    "	prod.ativo,\n" +
                    "	case when prod.descricao like '*%' then 1 else 0 end descontinuado,\n" +
                    "	prod.codncm ncm,\n" +
                    "	prod.codcest cest,\n" +
                    "	prod.cst_pisentrada piscofins_entrada,\n" +
                    "	prod.cst_pissaida piscofins_saida,\n" +
                    "	prod.nat_rec piscofins_natrec,\n" +
                    "	prod.codaliq icms_saida_id,\n" +
                    "	prod.CODTRIB icms_cst_saida,\n" +
                    "	al.ALIQUOTA icms_aliquota_saida,\n" +
                    "	prod.PER_REDUC icms_reduzido_saida,\n" +
                    "	prod.CODTRIB_ENT icms_cst_entrada,\n" +
                    "	prod.ulticmscred icms_aliquota_entrada,\n" +
                    "	prod.PER_REDUC_ENT icms_reduzido_entrada,\n" +
                    "	prod.desativacompra\n" +
                    "from\n" +
                    "	produtos prod\n" +
                    "	left join (\n" +
                    "		select\n" +
                    "			codprod id_produto,\n" +
                    "			barra ean,\n" +
                    "			1 qtdembalagem,\n" +
                    "			preco_unit preco\n" +
                    "		from\n" +
                    "			produtos prod\n" +
                    "		union\n" +
                    "		select\n" +
                    "			codprod id_produto, \n" +
                    "			rtrim(barra_emb) ean, \n" +
                    "			qtd qtdembalagem,\n" +
                    "			preco_unit preco\n" +
                    "		from embalagens \n" +
                    "		where\n" +
                    "			barra_emb is not null \n" +
                    "		union\n" +
                    "		select\n" +
                    "			codprod id_produto,\n" +
                    "			barra ean,\n" +
                    "			1 qtdembalagem,\n" +
                    "			0 preco\n" +
                    "		from alternativo\n" +
                    "		where\n" +
                    "			barra is not null\n" +
                    "	) ean on\n" +
                    "		prod.codprod = ean.id_produto\n" +
                    "	left outer join prod_familia fam on\n" +
                    "		fam.codprod = prod.codprod and\n" +
                    "		prod.codprod > 0\n" +
                    "	join aliquota_icms al on\n" +
                    "		al.CODALIQ = prod.codaliq_nf\n" +
                    "order by\n" +
                    "	id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {

                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem") == 0 ? 1 : rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    if (usarMargemBruta) {
                        imp.setMargem(rst.getDouble("margem_bruta"));
                    } else if (usaMargemLiquidaPraticada) {
                        imp.setMargem(rst.getDouble("margemliquidapraticada"));
                    } else {
                        imp.setMargem(rst.getDouble("margem_param"));
                    }
                    imp.setSituacaoCadastro(("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO));
                    imp.setDescontinuado("S".equals(rst.getString("desativacompra")) || rst.getBoolean("descontinuado"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natrec"));

                    imp.setIcmsDebitoId(rst.getString("icms_saida_id"));
                    if (this.utilizarIdIcmsNaEntrada) {
                        imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    } else {
                        imp.setIcmsCstEntrada(Utils.stringToInt(rst.getString("icms_cst_entrada")));
                        imp.setIcmsAliqEntrada(Utils.stringToDouble(rst.getString("icms_aliquota_entrada")));
                        imp.setIcmsReducaoEntrada(Utils.stringToDouble(rst.getString("icms_reduzido_entrada")));

                        if (imp.getIcmsCstEntrada() != 20) {
                            imp.setIcmsReducaoEntrada(0);
                        }
                        if (imp.getIcmsCstEntrada() != 0
                                && imp.getIcmsCstEntrada() != 10
                                && imp.getIcmsCstEntrada() != 20
                                && imp.getIcmsCstEntrada() != 70) {
                            imp.setIcmsAliqEntrada(0);
                            imp.setIcmsReducaoEntrada(0);
                        }

                        String str = (imp.getImportId() + " - ICMS Entrada: "
                                + imp.getIcmsCstEntrada() + " - "
                                + MathUtils.trunc(imp.getIcmsAliqEntrada(), 2) + " - "
                                + MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1));

                        if (!icms.containsKey(
                                imp.getIcmsCstEntrada(),
                                MathUtils.trunc(imp.getIcmsAliqEntrada(), 2),
                                MathUtils.trunc(imp.getIcmsReducaoEntrada(), 1)
                        )) {
                            imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                        } else {
                            imp.setIcmsCreditoId(null);
                            str += " - Encontrou";
                        }
                        LOG.finest(str);
                    }

                    if (desconsiderarSetorBalanca) {
                        String st = Utils.acertarTexto(rst.getString("unidade"), 2);
                        if ("KG".equals(st)) {
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("KG");
                        } else {
                            String desc = Utils.acertarTexto(imp.getDescricaoCompleta());
                            if (pesquisarKGnaDescricao && desc.contains("KG") && !desc.matches(".*[0-9](\\s)*K?G")) {
                                imp.seteBalanca(true);
                                imp.setTipoEmbalagem("KG");
                            } else {
                                imp.setTipoEmbalagem("UN");
                                imp.seteBalanca(false);
                            }
                        }                        
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if(imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }                        
                    } else if (v_usar_arquivoBalanca) {
                        ProdutoBalancaVO produtoBalanca;
                        long codigoProduto;
                        codigoProduto = Long.parseLong(imp.getEan().trim());
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }
                        if (produtoBalanca != null) {
                            qtdBalanca++;
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rst.getInt("VALIDADE"));
                        } else {
                            qtdNormal++;
                            imp.setValidade(0);
                            imp.setTipoEmbalagem(rst.getString("unidade"));
                            imp.seteBalanca(false);
                        }
                    } else {
                        imp.seteBalanca((rst.getInt("e_balanca") == 1));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("VALIDADE"));
                        if(imp.isBalanca()) {
                            qtdBalanca++;
                        } else {
                            qtdNormal++;
                        }
                    }

                    vResult.add(imp);
                }
            }            
            LOG.fine("Produtos de balança: " + qtdBalanca + " normais: " + qtdNormal);
        }
        return vResult; 
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select  \n"
                        + "	codprod id_produto, \n"
                        + "	rtrim(barra_emb) ean, \n"
                        + "	qtd qtdembalagem,\n"
                        + "	preco_unit preco\n"
                        + "from embalagens \n"
                        + "where  \n"
                        + "	barra_emb is not null \n"
                        + "and \n"
                        + "	coalesce(PRECO_UNIT, 0) > 0"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rst.getDouble("preco"));
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
                    "select \n"
                    + "    f.codfornec, f.razao, f.fantasia, f.endereco, f.numero, f.bairro, f.complemento, \n"
                    + "    f.cidade, f.estado, f.cep, f.telefone, f.fax, f.email, f.celular, f.fone1, \n"
                    + "    f.contato, f.ie, f.cnpj_cpf, f.agencia, f.banco, f.conta,  f.dtcad, \n"
                    + "    f.valor_compra, f.ativo, \n"
                    + "    obs, \n"
                    + "    c.descricao as descricaopag, \n"
                    + "    f.pentrega, \n"
                    + "    f.pvisita, \n"
                    + "    coalesce(case \n"
                    + "    when codtipofornec = 1 then 0 \n"
                    + "    when codtipofornec = 2 then 1 \n"
                    + "    when codtipofornec = 3 then 2 \n"
                    + "    when codtipofornec = 4 then 3 \n"
                    + "    when codtipofornec = 5 then 5 \n"
                    + "    when codtipofornec = 6 then 6 \n"
                    + "    when codtipofornec = 7 then 7 \n"
                    + "    when codtipofornec = 8 then 8 \n"
                    + "    end, 2) as codtipofornec \n"
                    + "    from \n"
                    + "    fornecedores f left join condpagto c on (f.codcondpagto = c.codcondpagto) \n"
                    + "    order by codfornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("CODFORNEC"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setCnpj_cpf(rst.getString("CNPJ_CPF"));
                    imp.setIe_rg(rst.getString("IE"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setAtivo("S".equals(rst.getString("ATIVO")));

                    imp.setObservacao(rst.getString("OBS"));

                    if ((rst.getString("DESCRICAOPAG") != null) && (!rst.getString("DESCRICAOPAG").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " Cond. pag: " + Utils.acertarTexto(rst.getString("DESCRICAOPAG")));
                    }
                    if ((rst.getString("PENTREGA") != null) && (!rst.getString("PENTREGA").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " - Prazo entrega: " + rst.getInt("PENTREGA"));
                    }
                    if ((rst.getString("PVISITA") != null) && (!rst.getString("PVISITA").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao()
                                + " - Prazo visita: " + rst.getInt("PVISITA"));
                    }
                    /*imp.setObservacao(rst.getString("OBS").isEmpty() ? "" : rst.getString("OBS") + " Cond. pag: "
                     + Utils.acertarTexto(rst.getString("DESCRICAOPAG").isEmpty() ? "0" : rst.getString("DESCRICAOPAG"))
                     + " - Prazo entrega: " + rst.getInt("PENTREGA") + " - Prazo visita: " + rst.getInt("PVISITA"));*/

                    imp.setDatacadastro(rst.getDate("DTCAD"));
                    //imp.setTipoFornecedor(TipoFornecedor.getById(rst.getInt("CODTIPOFORNEC")));
                    imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);

                    imp.addTelefone("FAX", rst.getString("FAX"));
                    if ((rst.getString("CONTATO") != null)
                            && (!rst.getString("CONTATO").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("CONTATO"),
                                (Utils.stringLong(rst.getString("FONE1")).equals("0") ? rst.getString("TELEFONE") : rst.getString("FONE1")),
                                rst.getString("CELULAR"),
                                TipoContato.COMERCIAL,
                                rst.getString("EMAIL")
                        );
                    } else {
                        if ((rst.getString("EMAIL") != null)
                                && (!rst.getString("EMAIL").trim().isEmpty())) {
                            imp.addContato(
                                    "2",
                                    "EMAIL",
                                    null,
                                    null,
                                    TipoContato.COMERCIAL,
                                    rst.getString("EMAIL").toLowerCase()
                            );
                        }
                        if ((rst.getString("CELULAR") != null)
                                && (!rst.getString("CELULAR").trim().isEmpty())) {
                            imp.addContato(
                                    "3",
                                    "CELULAR",
                                    null,
                                    rst.getString("CELULAR"),
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
                        if ((rst.getString("FONE1") != null)
                                && (!rst.getString("FONE1").trim().isEmpty())) {
                            imp.addContato(
                                    "4",
                                    "TELEFONE",
                                    rst.getString("FONE1"),
                                    null,
                                    TipoContato.COMERCIAL,
                                    null
                            );
                        }
                    }

                    try (Statement stm2 = ConexaoSqlServer.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select f.CODFORNEC, "
                                + "cp.CODCONDPAGTO, "
                                + "replace(cp.DESCRICAO,'-','/') descricao, "
                                + "cp.NPARCELAS\n"
                                + "from FORNECEDORES f\n"
                                + "inner join CONDPAGTO cp on cp.CODCONDPAGTO = f.CODCONDPAGTO \n"
                                + "where f.CODFORNEC = " + imp.getImportId()
                                + "order by f.CODFORNEC, cp.CODCONDPAGTO"
                        )) {
                            int contador = 1;
                            if (rst2.next()) {
                                int numParcelas = rst2.getInt("NPARCELAS");
                                String descricao = Utils.formataNumeroParcela(rst2.getString("DESCRICAO").replace("//", "/").trim());
                                String[] cods = descricao.split("\\/");
                                if (numParcelas > 0) {

                                } else {
                                    if (!descricao.contains("/")) {
                                        if ("0".equals(Utils.formataNumero(descricao))) {
                                            imp.addPagamento(String.valueOf(contador), 1);
                                        } else {
                                            imp.addPagamento(String.valueOf(contador), Integer.parseInt(Utils.formataNumero(descricao).trim()));
                                        }

                                        System.out.println("Sem barra " + descricao + " Forn " + imp.getImportId() + "Tam " + cods.length);
                                    } else {
                                        System.out.println("Sem barra " + descricao + " Forn " + imp.getImportId() + "Tam " + cods.length);
                                        for (int i = 0; i < cods.length; i++) {
                                            if (!"".equals(cods[i])) {
                                                switch (i) {
                                                    case 0:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 1:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 2:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 3:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 4:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 5:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 6:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 7:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 8:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 9:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 10:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 11:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 12:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 13:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 14:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 15:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 16:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 17:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 18:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 19:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 20:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 21:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 22:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 23:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                    case 24:
                                                        imp.addPagamento(String.valueOf(i), Integer.parseInt(cods[i]));
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                                contador++;
                            }
                        }
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
                    "select\n"
                    + "	pf.CODFORNEC id_fornecedor,\n"
                    + "	pf.CODPROD id_produto,\n"
                    + "	pf.CODREF codigoexterno,\n"
                    + "	coalesce(pf.QTD_EMB, 1) qtdembalagem,\n"
                    + "	pf.DATAREF dataalteracao,\n"
                    + "	p.QTD_EMB qtd_cotacao\n"
                    + "from\n"
                    + "	PRODREF pf\n"
                    + "	join produtos p on\n"
                    + "		pf.CODPROD = p.CODPROD"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    if (this.usarQtdEmbDoProduto) {
                        imp.setQtdEmbalagem(rst.getDouble("qtd_cotacao"));
                    } else {
                        imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    }
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
                    "SELECT \n"
                    + "    CASE COALESCE(PESSOA,'F') WHEN 'F' THEN 1 ELSE 0 END AS PESSOA, \n"
                    + "    CODCLIE, \n"
                    + "    RAZAO, \n"
                    + "    ENDERECO, \n"
                    + "    COMPLEMENTO, \n"
                    + "    BAIRRO, \n"
                    + "    CIDADE, \n"
                    + "    ESTADO, \n"
                    + "    CEP, \n"
                    + "    NUMERO, \n"
                    + "    CNPJ_CPF, \n"
                    + "    TELEFONE, \n"
                    + "    RG,\n"
                    + "    IE, \n"
                    + "    FONE1, \n"
                    + "    FONE2, \n"
                    + "    EMAIL, \n"
                    + "    DTANIVER, \n"
                    + "    coalesce(LIMITECRED,0) LIMITECRED, \n"
                    + "    coalesce(RENDA,0) RENDA, \n"
                    + "    CARGO, \n"
                    + "    EMPRESA, \n"
                    + "    FONE_EMP, \n"
                    + "    CASE ATIVO WHEN 'S' THEN 1 ELSE 0 END AS ATIVO, \n"
                    + "    ESTADOCIVIL, \n"
                    + "    CASE SEXO WHEN 'F' THEN 1 ELSE 2 END AS SEXO, \n"
                    + "    NOMEPAI, \n"
                    + "    NOMEMAE, \n"
                    + "    DTALTERA, \n"
                    + "    CELULAR, \n"
                    + "    NOMECONJUGE, \n"
                    + "    CARGOCONJUGE, \n"
                    + "    CPF_CONJUGE, \n"
                    + "    RG_CONJUGE, \n"
                    + "    coalesce(RENDACONJUGE,0) as RENDACONJUGE, \n"
                    + "    DTCAD, \n"
                    + "    CASE ESTADOCIVIL WHEN 'S' THEN 1 \n"
                    + "    WHEN 'C' THEN 2 \n"
                    + "    WHEN 'V' THEN 3 \n"
                    + "    WHEN 'A' THEN 4 \n"
                    + "    WHEN 'O' THEN 5 ELSE 0 END AS ESTADOCIVILNOVO, \n"
                    + "    coalesce(OBS1, '') + ' ' +\n"
                    + "    coalesce(CONTATO, '') + ' ' +\n"
                    + "    coalesce(REF1_NOME, '') + ' ' +\n"
                    + "    coalesce(REF2_NOME, '') + ' ' +\n"
                    + "    coalesce(FONE1, '') AS OBS,\n"
                    + "    coalesce(obs2, '') + ' Agencia ' +\n"
                    + "    coalesce(agencia, '') + ' Banco ' +\n"
                    + "    coalesce(banco, '') + ' CC ' +\n"
                    + "    coalesce(cc, '') as obs2, \n"
                    + "    BLOQCARTAO, \n"
                    + "    senhacartao \n"
                    + "    FROM \n"
                    + "    CLIENTES \n"
                    + "    where \n"
                    + "    CODCLIE >= 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CODCLIE"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setDataCadastro(rst.getDate("DTCAD"));
                    imp.setCnpj(rst.getString("CNPJ_CPF"));
                    if ((rst.getString("RG") != null)
                            && (!rst.getString("RG").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("RG"));
                    } else if ((rst.getString("IE") != null)
                            && (!rst.getString("IE").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("IE"));
                    } else {
                        imp.setInscricaoestadual("ISENTO");
                    }
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setNomePai(rst.getString("NOMEPAI"));
                    imp.setNomeMae(rst.getString("NOMEMAE"));
                    imp.setNomeConjuge(rst.getString("NOMECONJUGE"));
                    imp.setDataNascimento(rst.getDate("DTANIVER"));
                    imp.setValorLimite(rst.getDouble("LIMITECRED"));
                    imp.setEmpresa(rst.getString("EMPRESA"));
                    imp.setEmpresaTelefone(rst.getString("FONE_EMP"));
                    imp.setCargo(rst.getString("CARGO"));
                    imp.setSalario(rst.getDouble("RENDA"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setObservacao2(rst.getString("OBS2"));
                    imp.setSenha(Utils.stringToInt(rst.getString("senhacartao")));
                    imp.setAtivo("1".equals(rst.getString("ATIVO")));
                    if ((rst.getString("BLOQCARTAO") != null)
                            && (!rst.getString("BLOQCARTAO").trim().isEmpty())) {
                        if ("N".equals(rst.getString("BLOQCARTAO").trim())) {
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                            imp.setBloqueado(false);
                        } else {
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                            imp.setBloqueado(true);
                        }
                    } else {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                        imp.setBloqueado(true);
                    }
                    imp.setSexo("1".equals(rst.getString("SEXO")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    if ((rst.getString("ESTADOCIVILNOVO") != null)
                            && (!rst.getString("ESTADOCIVILNOVO").trim().isEmpty())) {
                        if (null != rst.getString("ESTADOCIVILNOVO")) {
                            switch (rst.getString("ESTADOCIVILNOVO")) {
                                case "1":
                                    imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                                    break;
                                case "2":
                                    imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                    break;
                                case "3":
                                    imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                    break;
                                case "4":
                                    imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                    break;
                                case "5":
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                                default:
                                    imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                                    break;
                            }
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.OUTROS);
                    }

                    if ((rst.getString("FONE1") != null)
                            && (!rst.getString("FONE1").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 1",
                                rst.getString("FONE1"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("FONE2") != null)
                            && (!rst.getString("FONE2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 2",
                                rst.getString("FONE2"),
                                null,
                                null
                        );
                    }
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
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setIdCliente(rst.getString("CODCLIE"));
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataVencimento(rst.getDate("DTVENCTO"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setObservacao(rst.getString("OBS"));
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
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CLIENTES.RAZAO, "
                    + "CLIENTES.RG, "
                    + "CODRECEBER, "
                    + "NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "RECEBER.NUMCHEQUE, "
                    + "RECEBER.CODTIPODOCUMENTO, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS, "
                    + "CLIENTES.TELEFONE, "
                    + "RECEBER.CODBANCO, "
                    + "RECEBER.AGENCIA, "
                    + "RECEBER.CONTACORR "
                    + "FROM RECEBER "
                    + "INNER JOIN CLIENTES ON "
                    + "CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "and RECEBER.CODTIPODOCUMENTO IN (" + v_tipoDocumentoCheque + ") "
                    + "order by DTEMISSAO "
            )) {
                while (rst.next()) {
                    int idBanco = new BancoDAO().getId(rst.getInt("CODBANCO"));
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setDate(rst.getDate("DTEMISSAO"));
                    imp.setDataDeposito(rst.getDate("DTVENCTO"));
                    imp.setNumeroCupom(rst.getString("NOTAECF"));
                    imp.setNumeroCheque(rst.getString("NUMCHEQUE"));
                    imp.setAgencia(rst.getString("AGENCIA"));
                    imp.setConta(rst.getString("CONTACORR"));
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCpf(rst.getString("CNPJ_CPF"));
                    imp.setNome(rst.getString("RAZAO"));
                    imp.setTelefone(rst.getString("RG"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setBanco(idBanco);

                    if ((v_tipoDocumentoCheque == 5)
                            || (v_tipoDocumentoCheque == 13)) {
                        imp.setAlinea(11);
                    } else {
                        imp.setAlinea(0);
                    }

                    if (v_tipoDocumentoCheque == 5) {
                        imp.setIdLocalCobranca(1);
                    } else if (v_tipoDocumentoCheque == 13) {
                        imp.setIdLocalCobranca(2);
                    }
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select CODTIPODOCUMENTO, DESCRICAO from TIPODOCUMENTO order by CODTIPODOCUMENTO"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("CODTIPODOCUMENTO"),
                            rst.getString("CODTIPODOCUMENTO") + " - "
                            + rst.getString("DESCRICAO")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODPAGAR, "
                    + "CODFORNEC, "
                    + "NOTA, "
                    + "VALOR, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "OBS, "
                    + "OBS2, "
                    + "VALORPAGO, "
                    + "DTPAGTO, "
                    + "DTENTRADA "
                    + "FROM PAGAR "
                    + "where CODLOJA = " + getLojaOrigem() + " "
                    + "and DTPAGTO IS NULL "
                    + "order by DTEMISSAO "
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("CODPAGAR"));
                    imp.setIdFornecedor(rst.getString("CODFORNEC"));
                    imp.setNumeroDocumento(rst.getString("NOTA"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setDataEmissao(rst.getDate("DTEMISSAO"));
                    imp.setDataEntrada(rst.getDate("DTENTRADA"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("DTENTRADA"));
                    imp.setObservacao((rst.getString("OBS") == null ? "" : rst.getString("OBS")) + " "
                            + (rst.getString("OBS2") == null ? "" : rst.getString("OBS2")));
                    imp.addVencimento(rst.getDate("DTVENCTO"), imp.getValor());
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    public void importarReceberDevolucao(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados ReceberDevolucao...");
            vResult = getReceberDevolucao();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberDevolucaoVO> getReceberDevolucao() throws Exception {
        List<ReceberDevolucaoVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    String obs = "";
                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF"))));
                        if (idFornecedor != -1) {
                            ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                            imp.setIdFornecedor(idFornecedor);
                            if ((rst.getString("NOTAECF") != null)
                                    && (!rst.getString("NOTAECF").trim().isEmpty())) {
                                if (rst.getString("NOTAECF").trim().length() > 9) {
                                    obs = "NOTAECF " + rst.getString("NOTAECF");
                                } else {
                                    imp.setNumeroNota(Integer.parseInt(Utils.formataNumero(rst.getString("NOTAECF"))));
                                }
                            } else {
                                imp.setNumeroNota(0);
                            }
                            imp.setDataemissao(rst.getDate("DTEMISSAO"));
                            imp.setDatavencimento(rst.getDate("DTVENCTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBS") == null ? "" : rst.getString("OBS").trim())
                                    + " " + (rst.getString("NUMTIT") == null ? "" : rst.getString("NUMTIT").trim()) + " " + obs);
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarReceberVerba(int idLojaVR) throws Exception {
        List<ReceberVerbaVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados ReceberVerba...");
            vResult = getReceberVerba();
            if (!vResult.isEmpty()) {
                new ReceberVerbaDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberVerbaVO> getReceberVerba() throws Exception {
        List<ReceberVerbaVO> vResult = new ArrayList<>();
        int idFornecedor;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CLIENTES.RAZAO, "
                    + "CODRECEBER AS ID, "
                    + "CLIENTES.CNPJ_CPF, "
                    + "CLIENTES.RG, "
                    + "CLIENTES.IE, "
                    + "CODRECEBER, NUMTIT, "
                    + "RECEBER.CODCLIE, "
                    + "NOTAECF, "
                    + "DTVENCTO, "
                    + "DTEMISSAO, "
                    + "DTPAGTO, "
                    + "coalesce(VALOR,0) VALOR, "
                    + "coalesce(VALORJUROS,0) VALORJUROS, "
                    + "OBS, "
                    + "CLIENTES.TELEFONE "
                    + "FROM "
                    + "RECEBER "
                    + "INNER JOIN CLIENTES ON CLIENTES.CODCLIE = RECEBER.CODCLIE "
                    + "where UPPER(SITUACAO) = 'AB' "
                    + "and RECEBER.CODTIPODOCUMENTO = " + v_tipoDocumento + " "
                    + "and RECEBER.CODLOJA = " + getLojaOrigem() + " "
                    + "order by DTEMISSAO"
            )) {
                while (rst.next()) {
                    if ((rst.getString("CNPJ_CPF") != null)
                            && (!rst.getString("CNPJ_CPF").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("CNPJ_CPF"))));
                        if (idFornecedor != -1) {
                            ReceberVerbaVO imp = new ReceberVerbaVO();
                            imp.setIdFornecedor(idFornecedor);
                            imp.setDataemissao(rst.getDate("DTEMISSAO"));
                            imp.setDatavencimento(rst.getDate("DTVENCTO"));
                            imp.setValor(rst.getDouble("VALOR"));
                            imp.setRepresentante(rst.getString("RAZAO") == null ? "" : rst.getString("RAZAO").trim());
                            imp.setTelefone((rst.getString("TELEFONE") == null ? "" : rst.getString("TELEFONE").trim()));
                            imp.setCpfRepresentante(Long.parseLong(Utils.formataNumero("CNPJ_CPF")));
                            if ((rst.getString("RG") != null)
                                    && (!rst.getString("RG").trim().isEmpty())) {
                                imp.setRgRepresentante(rst.getString("RG").trim());
                            } else if ((rst.getString("IE") != null)
                                    && (!rst.getString("IE").trim().isEmpty())) {
                                imp.setRgRepresentante(rst.getString("IE").trim());
                            } else {
                                imp.setRgRepresentante("");
                            }
                            imp.setObservacao("IMPORTADO VR " + (rst.getString("OBS") == null ? "" : rst.getString("OBS").trim()) + " "
                                    + (rst.getString("NUMTIT") == null ? "" : rst.getString("NUMTIT").trim()));
                            vResult.add(imp);
                        }
                    }
                }
            }
        }
        return vResult;
    }

    public void importarProdutosGetWay(String i_arquio) throws Exception {
        int linha = 0;
        Statement stm = null;
        StringBuilder sql = null;
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquio), settings);
            Sheet[] sheets = arquivo.getSheets();

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellPr_codInt = sheet.getCell(0, i);
                    Cell cellPr_cBarra = sheet.getCell(1, i);
                    Cell cell_Pr_nome = sheet.getCell(2, i);

                    sql = new StringBuilder();
                    sql.append("insert into implantacao.produtos_getway ("
                            + "codprod, "
                            + "barras, "
                            + "descricao) "
                            + "values ("
                            + "'" + cellPr_codInt.getContents().trim() + "' ,"
                            + "lpad('" + cellPr_cBarra.getContents().trim() + "', 14, '0'), "
                            + "'" + Utils.acertarTexto(cell_Pr_nome.getContents().trim()) + "')");
                    stm.execute(sql.toString());
                    System.out.println(i);
                }
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select codaliq, descricao from aliquota_icms"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codaliq"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pi.codprod produtopai,\n"
                    + "	p.descricao descricaopai,\n"
                    + "	pi.qtd qtdembalagem,\n"
                    + "	pi.codprod_itm produtofilho,\n"
                    + "	pitem.descricao descricaofilho\n"
                    + "from\n"
                    + "	prod_itens pi\n"
                    + "	join produtos p on\n"
                    + "		pi.CODPROD = p.CODPROD\n"
                    + "	join produtos pitem on\n"
                    + "		pi.CODPROD_ITM = pitem.CODPROD\n"
                    + "order by\n"
                    + "	produtopai, produtofilho"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rst.getString("produtopai"));
                    imp.setDescricao(rst.getString("descricaopai"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setProdutoAssociadoId(rst.getString("produtofilho"));
                    imp.setDescricaoProdutoAssociado(rst.getString("descricaofilho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void setDesconsiderarSetorBalanca(boolean desconsiderarSetorBalanca) {
        this.desconsiderarSetorBalanca = desconsiderarSetorBalanca;
    }

    public void setPesquisarKGnaDescricao(boolean pesquisarKGnaDescricao) {
        this.pesquisarKGnaDescricao = pesquisarKGnaDescricao;
    }

    /**
     * Caso seja true, a aplicação copia o id do icms fixado na saída, para a 
     * entrada dos produtos também.
     * @param utilizarIdIcmsNaEntrada 
     */
    public void setUtilizarIdIcmsNaEntrada(boolean utilizarIdIcmsNaEntrada) {
        this.utilizarIdIcmsNaEntrada = utilizarIdIcmsNaEntrada;
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idclientepreferencial"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("complemento")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado")) + ","
                                + Utils.acertarTexto(rst.getString("cep"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    coalesce(cx.cliente, '') as idclientepreferencial,\n"
                    + "    min(cx.hora) as horainicio,\n"
                    + "    max(cx.hora) as horatermino,\n"
                    + "    min(case when cx.cancelado = 'N' then 0 else 1 end) as cancelado,\n"
                    + "    sum(cx.totitem) as subtotalimpressora,\n"
                    + "    cl.cnpj_cpf cpf,\n"
                    + "    sum(isnull(cx.descitem,0)) desconto,\n"
                    + "    sum(isnull(cx.acrescitem, 0)) acrescimo,\n"
                    + "    pdv.NUM_SERIE numeroserie,\n"
                    + "    pdv.IMP_MODELO modelo,\n"
                    + "    pdv.IMP_MARCA marca,\n"
                    + "    cl.razao nomecliente,\n"
                    + "    cl.endereco,\n"
                    + "    cl.numero,\n"
                    + "    cl.complemento,\n"
                    + "    cl.bairro,\n"
                    + "    cl.cidade,\n"
                    + "    cl.estado,\n"
                    + "    cl.cep\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join parampdv pdv on cx.codloja = pdv.CODLOJA and cx.codcaixa = pdv.CODCAIXA\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + FORMAT.format(dataInicio) + "', 23) and convert(date, '" + FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')\n"
                    + "group by\n"
                    + "	   cx.coo,\n"
                    + "    cx.codcaixa,\n"
                    + "    cx.data,\n"
                    + "    coalesce(cx.cliente, ''),\n"
                    + "	   cl.cnpj_cpf,\n"
                    + "    pdv.NUM_SERIE,\n"
                    + "    pdv.IMP_MODELO,\n"
                    + "    pdv.IMP_MARCA,\n"
                    + "    cl.razao,\n"
                    + "    cl.endereco,\n"
                    + "    cl.numero,\n"
                    + "    cl.complemento,\n"
                    + "    cl.bairro,\n"
                    + "    cl.cidade,\n"
                    + "    cl.estado,\n"
                    + "    cl.cep\n"
                    + "order by\n"
                    + "    data, numerocupom";
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
                        String id = rst.getString("numerocupom") + "-" + rst.getString("ecf") + "-" + rst.getString("data");

                        next.setId(rst.getString("id"));
                        next.setVenda(id);
                        next.setProduto(rst.getString("produto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        
                        String trib = rst.getString("codaliq_venda");
                        if (trib == null || "".equals(trib)) {
                            trib = rst.getString("codaliq_produto");
                        }

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             TA	7.00	ALIQUOTA 07%
             TB	12.00	ALIQUOTA 12%
             TC	18.00	ALIQUOTA 18%
             TD	25.00	ALIQUOTA 25%
             TE	11.00	ALIQUOTA 11%
             I	0.00	ISENTO
             F	0.00	SUBST TRIBUTARIA
             N	0.00	NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "TA":
                    cst = 0;
                    aliq = 7;
                    break;
                case "TB":
                    cst = 0;
                    aliq = 12;
                    break;
                case "TC":
                    cst = 0;
                    aliq = 18;
                    break;
                case "TD":
                    cst = 0;
                    aliq = 25;
                    break;
                case "TE":
                    cst = 0;
                    aliq = 11;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "    cx.id,\n"
                    + "    cx.coo as numerocupom,\n"
                    + "    cx.codcaixa as ecf,\n"
                    + "    cx.data as data,\n"
                    + "    cx.codprod as produto,\n"
                    + "    pr.DESC_PDV as descricao,    \n"
                    + "    isnull(cx.qtd, 0) as quantidade,\n"
                    + "    isnull(cx.totitem, 0) as total,\n"
                    + "    case when cx.cancelado = 'N' then 0 else 1 end as cancelado,\n"
                    + "    isnull(cx.descitem, 0) as desconto,\n"
                    + "    isnull(cx.acrescitem, 0) as acrescimo,\n"
                    + "    case\n"
                    + "     when LEN(cx.barra) > 14 \n"
                    + "     then SUBSTRING(cx.BARRA, 4, LEN(cx.barra))\n"
                    + "    else cx.BARRA end as codigobarras,\n"
                    + "    pr.unidade,\n"
                    + "    cx.codaliq codaliq_venda,\n"
                    + "    pr.codaliq codaliq_produto,\n"
                    + "    ic.DESCRICAO trib_desc\n"
                    + "from\n"
                    + "    caixageral as cx\n"
                    + "    join PRODUTOS pr on cx.codprod = pr.codprod\n"
                    + "    left join creceita c on pr.codcreceita = c.codcreceita\n"
                    + "    left join clientes cl on cx.cliente = cast(cl.codclie as varchar(20))\n"
                    + "    left join ALIQUOTA_ICMS ic on pr.codaliq = ic.codaliq\n"
                    + "where\n"
                    + "    cx.tipolancto = '' and\n"
                    + "    (cx.data between convert(date, '" + VendaIterator.FORMAT.format(dataInicio) + "', 23) and convert(date, '" + VendaIterator.FORMAT.format(dataTermino) + "', 23)) and\n"
                    + "    cx.codloja = " + idLojaCliente + " and\n"
                    + "    cx.atualizado = 'S' and\n"
                    + "    (cx.flgrupo = 'S' or cx.flgrupo = 'N')";
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
