package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SiaCriareMySqlDAO extends InterfaceDAO implements MapaTributoProvider {

    public String i_arquivo;
    public String v_pahtFileXls;

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }

    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "SiaCriareMySQL";
        } else {
            return "SiaCriareMySQL - " + complemento;
        }
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.OFERTA
        ));
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select \n"
                    + "	 p.CODITEM id_produto,\n"
                    + "    p.ncm,\n"
                    + "    aliq.mva,\n"
                    + "    0 cst,\n"
                    + "    ai.ALIQUOTA_INTERNA aliquota\n"
                    + "from\n"
                    + "	produto p\n"
                    + "    join empresas emp on\n"
                    + "		emp.CODIGO_N = 1\n"
                    + "	join aliquotas_internas_produto aliq on\n"
                    + "		aliq.id_produto = p.coditem and\n"
                    + "        aliq.uf_dest = emp.ESTADO\n"
                    + "	join aliquotas_internas ai on\n"
                    + "		aliq.UF_DEST = ai.ESTADO\n"
                    + "order by\n"
                    + "   p.coditem"
            )) {
                while (rs.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();

                    imp.setId(rs.getString("id_produto"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setIva(rs.getDouble("mva"));
                    imp.setIvaAjustado(rs.getDouble("mva"));
                    imp.setAliquotaCredito(rs.getInt("cst"), rs.getDouble("aliquota"), 0);
                    imp.setAliquotaCreditoForaEstado(rs.getInt("cst"), rs.getDouble("aliquota"), 0);
                    imp.setAliquotaDebito(rs.getInt("cst"), rs.getDouble("aliquota"), 0);
                    imp.setAliquotaDebitoForaEstado(rs.getInt("cst"), rs.getDouble("aliquota"), 0);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT\n"
                    + "	posicao id,\n"
                    + "	descricao,\n"
                    + "	coalesce(cstecf, 0) cst,\n"
                    + "	COALESCE(aliquota, 0) aliquota\n"
                    + "FROM\n"
                    + "	aliquotas\n"
                    + "ORDER BY\n"
                    + "	id"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            "descricao. " + rs.getString("cst") + " ALIQ. " + rs.getString("aliquota"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            0
                    ));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo_n id,\n"
                    + "   nome razao,\n"
                    + "   cgc cnpj\n"
                    + "from\n"
                    + "	empresas"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("id"),
                            rst.getString("razao") + " " + rst.getString("cnpj"))
                    );
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
                    "select \n"
                    + "	m1.CODGRUPO as merc1,\n"
                    + "	m1.NOMEGRUPO as desc_merc1, \n"
                    + "	m2.ID_CATEGORIA as merc2,\n"
                    + "	m2.DESCRICAO as desc_merc2, \n"
                    + "	m3.CODIGO as merc3,\n"
                    + "	m3.DESCRICAO as desc_merc3 \n"
                    + "from\n"
                    + "	produto p \n"
                    + "	left join grupo m1 on m1.CODGRUPO = p.GRUPO \n"
                    + "	left join categorias m2 on m2.ID_CATEGORIA = p.CATEGORIA \n"
                    + "	left join familias m3 on m3.CODIGO = p.FAMILIA \n"
                    + "order by\n"
                    + "	m1.CODGRUPO,\n"
                    + "	m2.ID_CATEGORIA,\n"
                    + "	m3.CODIGO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codigo id_familia,\n"
                    + "	descricao \n"
                    + "from\n"
                    + "	familias"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_familia"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.CODITEM id, \n"
                    + "	p.GRUPO, \n"
                    + "	p.CATEGORIA, \n"
                    + "	p.FAMILIA, \n"
                    + "	p.DESCRICAO descricaocompleta, \n"
                    + "	p.ABREVIA descricaoreduzida, \n"
                    + "	p.CUSTO custo, \n"
                    + "	p.UNITARIO preco, \n"
                    + "	p.BALANCA e_balanca, \n"
                    + "	p.ALIQUOTASAIDA as ICMS, \n"
                    + "	p.UNIDADE, \n"
                    + "	p.id_fabricante, \n"
                    + "	ean.ean, \n"
                    + "	p.NCM, \n"
                    + "	p.CATEGORIA, \n"
                    + "	p.ATIVO, \n"
                    + "	p.PESO_LIQUIDO, \n"
                    + "	p.PESO_BRUTO, \n"
                    + "	(p.QTDEMBALAGEM / 1000) as qtdembalagem, \n"
                    + "	p.PIS pis_entrada, \n"
                    + "	p.PISVENDAS pis_saida, \n"
                    + "	p.NAT_REC pis_nat_rec, \n"
                    + "	p.MARKDOWN, \n"
                    + "	p.CEST, \n"
                    + "	p.familia id_familia,\n"
                    + "	est.qtd estoque,\n"
                    + "	est.qtd_maxima estoquemaximo,\n"
                    + "	est.qtd_minima estoqueminino,\n"
                    + "	est.etiqueta_impressa,\n"
                    + "	est.markdownrevenda ,\n"
                    + "	est.qtd_troca troca\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	left JOIN (\n"
                    + "		SELECT\n"
                    + "			coditem id_produto,\n"
                    + "			codbarra ean\n"
                    + "		FROM\n"
                    + "			produto\n"
                    + "		union\n"
                    + "		SELECT \n"
                    + "			id_produto, \n"
                    + "			codigo ean\n"
                    + "		FROM\n"
                    + "			codigos\n"
                    + "		ORDER BY\n"
                    + "			id_produto\n"
                    + "	) ean on\n"
                    + "		p.coditem = ean.id_produto\n"
                    + "	left join estoques est on\n"
                    + "		est.id_produto = p.coditem and\n"
                    + "		est.id_empresa = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "   p.coditem"
            )) {
                int cont = 0;
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportId(rst.getString("id"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean")));
                    if (bal != null) {
                        imp.setEan(bal.getCodigo() + "");
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(bal.getValidade());
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setValidade(0);
                        imp.seteBalanca("S".equals(rst.getString("e_balanca")));
                        imp.setTipoEmbalagem(rst.getString("UNIDADE"));
                    }

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setCodMercadologico1(rst.getString("GRUPO"));
                    imp.setCodMercadologico2(rst.getString("CATEGORIA"));
                    imp.setCodMercadologico3(rst.getString("FAMILIA"));
                    imp.setMargem(rst.getDouble("MARKDOWN"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("CUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setNcm(rst.getString("NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS_SAIDA"));
                    imp.setPiscofinsCstCredito(rst.getString("PIS_ENTRADA"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("PIS_NAT_REC"));
                    imp.setIcmsDebitoId(rst.getString("ICMS"));
                    imp.setIcmsCreditoId(rst.getString("ICMS"));
                    imp.setIcmsConsumidorId(rst.getString("ICMS"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("ICMS"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("ICMS"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("ICMS"));
                    
                    imp.setFornecedorFabricante(rst.getString("id_fabricante"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminino"));
                    imp.setTroca(rst.getDouble("troca"));

                    imp.setPautaFiscalId(rst.getString("id"));

                    result.add(imp);

                    cont++;

                    ProgressBar.setStatus(String.valueOf(cont));
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
                    "select \n"
                    + "    f.codigocli id,\n"
                    + "    f.razao,\n"
                    + "    f.nomecli fantasia,\n"
                    + "    f.cpfcgc cnpj,\n"
                    + "    f.IDENINSC ie,\n"
                    + "    f.suframa,\n"
                    + "    f.ativo,\n"
                    + "    f.ENDERCLI endereco,\n"
                    + "    f.NUMERO,\n"
                    + "    f.COMPLEMENTO,\n"
                    + "    f.BAIRROCLI bairro,\n"
                    + "    f.CIDADECLI cidade,\n"
                    + "    f.ESTADOCLI estado,\n"
                    + "    f.CEPCLI cep,\n"
                    + "    f.ENDERCOB endereco_cob,\n"
                    + "    f.NUMEROCOB numero_cob,\n"
                    + "    f.COMPLEMENTOCOB complemento_cob,\n"
                    + "    f.BAIRROCOB bairro_cob,\n"
                    + "    f.CIDADECOB cidade_cob,\n"
                    + "    f.ESTADOCOB estado_cob,\n"
                    + "    f.CEPCOB cep_cob,\n"
                    + "    f.FONECLI telefone,\n"
                    + "    f.CADASTRADO datacadastro,\n"
                    + "    f.ALTERADO dataalteracao,\n"
                    + "    f.OBSERVACAO,\n"
                    + "    f.EMAIL,\n"
                    + "    f.FAXCLI fax\n"
                    + "from\n"
                    + "	clientes f\n"
                    + "where\n"
                    + "	f.tipo = 'F'\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setSuframa(rst.getString("suframa"));
                    imp.setAtivo("S".equals(rst.getString("ativo")));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));

                    imp.setCob_endereco(rst.getString("endereco_cob"));
                    imp.setCob_numero(rst.getString("numero_cob"));
                    imp.setCob_complemento(rst.getString("complemento_cob"));
                    imp.setCob_bairro(rst.getString("bairro_cob"));
                    imp.setCob_municipio(rst.getString("cidade_cob"));
                    imp.setCob_uf(rst.getString("estado_cob"));
                    imp.setCob_cep(rst.getString("cep_cob"));

                    imp.setTel_principal(rst.getString("telefone"));
                    imp.addTelefone("FAX", rst.getString("fax"));
                    imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.id_produto,\n"
                    + "	pf.id_fornecedor,\n"
                    + "	n.codigo codigoexterno,\n"
                    + "	pf.ULTIMACOMPRA dataalteracao,\n"
                    + "	coalesce(n.fator, 1) fator\n"
                    + "from\n"
                    + "	produtos_fornecedores pf\n"
                    + "	join codigos_fornecedores n on\n"
                    + "		pf.id_fornecedor = n.id_fornecedor and\n"
                    + "		pf.ID_PRODUTO = n.ID_PRODUTO"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setQtdEmbalagem(rst.getInt("fator"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	CODIGOCLI, \n"
                    + "	NOMECLI, \n"
                    + "	BAIRROCLI, \n"
                    + "	CIDADECLI, \n"
                    + "	ESTADOCLI, \n"
                    + "	CEPCLI, \n"
                    + "	FONECLI, \n"
                    + "	FAXCLI, \n"
                    + "	CPFCGC, \n"
                    + "	IDENINSC, \n"
                    + "	EMAIL, \n"
                    + "	ENDERCLI, \n"
                    + "	BANCO, \n"
                    + "	CONTA, \n"
                    + "	AGENCIA, \n"
                    + "	ATIVO, \n"
                    + "	RAZAO, \n"
                    + "	ID_CIDADE, \n"
                    + "	NUMERO,\n"
                    + "	NOMEPAI, \n"
                    + "	NOMEMAE,\n"
                    + "	OBSERVACAO, \n"
                    + "	CARGO, \n"
                    + "	RENDA_TITULAR, \n"
                    + "	LIMITE_CREDITO \n"
                    + "from\n"
                    + "	clientes \n"
                    + "where\n"
                    + "	TIPO = 'C'\n"
                    + "order by\n"
                    + "	codigocli"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("CODIGOCLI"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("NOMECLI"));
                    imp.setCnpj(rst.getString("CPFCGC"));
                    imp.setInscricaoestadual(rst.getString("IDENINSC"));
                    imp.setAtivo("S".equals(rst.getString("ATIVO")));
                    imp.setEndereco(rst.getString("ENDERCLI"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setCep(rst.getString("CEPCLI"));
                    imp.setBairro(rst.getString("BAIRROCLI"));
                    imp.setMunicipio(rst.getString("CIDADECLI"));
                    imp.setUf(rst.getString("ESTADOCLI"));
                    imp.setTelefone(rst.getString("FONECLI"));
                    imp.setFax(rst.getString("FAXCLI"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));
                    imp.setNomePai(rst.getString("NOMEPAI"));
                    imp.setNomeMae(rst.getString("NOMEMAE"));
                    imp.setCargo(rst.getString("CARGO"));
                    imp.setSalario(rst.getDouble("RENDA_TITULAR"));
                    imp.setValorLimite(rst.getDouble("LIMITE_CREDITO"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT\n"
                    + "	c.contador id,\n"
                    + "	c.EMISSAO dataemissao,\n"
                    + "	c.caixa ecf,\n"
                    + "	c.cupom numerocupom,\n"
                    + "	c.coo,\n"
                    + "	c.VALOR_PARCELA valor,\n"
                    + "	c.OBSERVACAO,\n"
                    + "	c.HISTORICO,\n"
                    + "	c.ID_PORTADOR,\n"
                    + "	c.CODIGO_N id_cliente,\n"
                    + "	c.VENCTO_PARCELA datavencimento,\n"
                    + "	c.JUROS\n"
                    + "FROM\n"
                    + "	creceber c\n"
                    + "WHERE\n"
                    + "   c.EMPRESA = " + getLojaOrigem() + " and\n"
                    + "	c.VLPAGTO < c.VALOR_PARCELA and\n"
                    + "	c.BAIXADO != 'S' AND dc = 'D'\n"
                    + "ORDER BY\n"
                    + "	id"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("HISTORICO"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setJuros(rs.getDouble("JUROS"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ID_CHEQUE ID,\n"
                    + "	EMISSAO DT_EMISSAO,\n"
                    + "	VENCTO DT_VENCI,\n"
                    + "	CUPOM,\n"
                    + "	NCHEQUE NUMCHEQUE,\n"
                    + "	CH.BANCO,\n"
                    + "	CH.AGENCIA,\n"
                    + "	CH.CONTA,\n"
                    + "	CH.CGCCPF CPF_CNPJ,\n"
                    + "	C.NOMECLI NOME_CLI,\n"
                    + "	C.IDENINSC RG_IE,\n"
                    + " C.FONECLI FONE_CLI,\n"
                    + "	VALOR,\n"
                    + "	OBS \n"
                    + "FROM\n"
                    + "	CHEQUES CH \n"
                    + "	LEFT JOIN CLIENTES C ON CH.ID_CLIENTE = C.CODIGOCLI \n"
                    + "WHERE\n"
                    + "	CH.ID_EMPRESA = " + getLojaOrigem() + "\n"
                    + "	AND BAIXADO = 'N' AND DATABAIXA IS NULL\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("ID"));
                    imp.setDate(rst.getDate("DT_EMISSAO"));
                    imp.setDataDeposito(rst.getDate("DT_VENCI"));
                    imp.setNumeroCupom(rst.getString("CUPOM"));
                    imp.setNumeroCheque(rst.getString("NUMCHEQUE"));
                    imp.setBanco(rst.getInt("BANCO"));
                    imp.setAgencia(rst.getString("AGENCIA"));
                    imp.setConta(rst.getString("CONTA"));
                    imp.setTelefone(rst.getString("FONE_CLI"));
                    imp.setCpf(rst.getString("CPF_CNPJ"));
                    imp.setNome(rst.getString("NOME_CLI"));
                    imp.setRg(rst.getString("RG_IE"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setObservacao(rst.getString("OBS"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<OfertaIMP> getOfertas(java.util.Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        java.sql.Date dataFimOferta, dataInicioOferta;
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_pahtFileXls + "//estoques.xls"), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        Calendar c = Calendar.getInstance();

        try {

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellIdProduto = sheet.getCell(0, i);
                    Cell cellPrecoOferta = sheet.getCell(7, i);
                    Cell cellInicioOferta = sheet.getCell(20, i);
                    Cell cellFimOferta = sheet.getCell(21, i);

                    if ((cellInicioOferta.getContents() != null)
                            && (!cellInicioOferta.getContents().trim().isEmpty())
                            && (!cellInicioOferta.getContents().contains("-"))
                            && (cellFimOferta.getContents() != null)
                            && (!cellFimOferta.getContents().trim().isEmpty())
                            && (!cellFimOferta.getContents().contains("-"))) {

                        if ((cellFimOferta.getContents() != null)
                                && (!cellFimOferta.getContents().trim().isEmpty())) {
                            dataFimOferta = new java.sql.Date(fmt.parse(cellFimOferta.getContents()).getTime());
                        } else {
                            dataFimOferta = new Date(new java.util.Date().getTime());
                        }

                        dataInicioOferta = new Date(new java.util.Date().getTime());

                        if (dataFimOferta.after(dataInicioOferta)) {
                            OfertaIMP imp = new OfertaIMP();
                            imp.setTipoOferta(TipoOfertaVO.CAPA);
                            imp.setIdProduto(cellIdProduto.getContents());
                            imp.setPrecoOferta(Double.parseDouble(cellPrecoOferta.getContents()));
                            imp.setDataInicio(dataInicioOferta);
                            imp.setDataFim(dataFimOferta);
                            result.add(imp);
                        }
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
