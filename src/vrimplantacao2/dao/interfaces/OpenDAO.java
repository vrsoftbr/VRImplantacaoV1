package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.tributacao.AliquotaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class OpenDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(OpenDAO.class.getName());

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PAUTA_FISCAL,
            OpcaoProduto.PAUTA_FISCAL_PRODUTO
        }));
    }
    
    private boolean importarSomenteBalanca = false;

    public void setImportarSomenteBalanca(boolean importarSomenteBalanca) {
        this.importarSomenteBalanca = importarSomenteBalanca;
    }

    @Override
    public String getSistema() {
        return "Open";
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, abrev, reduzido, cgc  from genfil order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("abrev") + " - " + rst.getString("reduzido") + " - " + rst.getString("cgc")));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT depto, descricao FROM gendep where classe = '' and subclasse = '' order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP(rst.getString("depto"), rst.getString("descricao"));
                    
                    importarMercadologicoNivel2(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void importarMercadologicoNivel2(MercadologicoNivelIMP imp) throws Exception {
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT depto, classe, subclasse, descricao FROM gendep where depto = '" + imp.getId() + "' and classe != '' and subclasse = '' order by 1,2"
            )) {
                while (rst.next()) {                    
                    importarMercadologicoNivel3(
                            imp.addFilho(rst.getString("classe"), rst.getString("descricao"))
                    );
                }
            }
        }
    }

    private void importarMercadologicoNivel3(MercadologicoNivelIMP imp) throws Exception {
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT depto, classe, subclasse, descricao FROM gendep where depto = '" + imp.getMercadologicoPai().getId() + "' and classe = '" + imp.getId() + "' and subclasse != '' order by 1,2,3"
            )) {
                while (rst.next()) {                    
                    imp.addFilho(rst.getString("subclasse"), rst.getString("descricao"));
                }
            }
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        Map<String, List<String>> eans = new HashMap<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            String sql = "select\n" +
                    "    p.CODPRO10 id,\n" +
                    "    coalesce(nullif(p.daulp510,'0000-00-00'),nullif(p.daulp410,'0000-00-00'),nullif(p.daulp310,'0000-00-00'),nullif(p.daulp210,'0000-00-00'),nullif(p.daulp110,'0000-00-00')) datacadastro,\n" +
                    "    coalesce(nullif(p.DAULVE10,'0000-00-00'),nullif(p.dtalttrib,'0000-00-00')) dataalteracao,\n" +
                    "    1 qtdembalagem,\n" +
                    "    p.UNIDAD10 unidade,\n" +
                    "    p.CODEAN10 ean,\n" +
                    "    if (p.PESOVA10 = 'S',1,0) pesavel,\n" +
                    "    p.validade,\n" +
                    "    p.DESCPR10 descricaocompleta,\n" +
                    "    p.DESCRE10 descricaoreduzida,\n" +
                    "    if(coalesce(DADESA10, '0000-00-00 00:00:00') = '0000-00-00 00:00:00',1,0) situacao,\n" +
                    "    p.DEPTOS10 merc1,\n" +
                    "    p.CLASSE10 merc2,\n" +
                    "    p.SUBCLA10 merc3,\n" +
                    "    null id_familia,\n" +
                    "    p.PSOUNI10 peso,\n" +
                    "    p.ESTMIN10 estoqueminimo,\n" +
                    "    p.ESTMAX10 estoquemaximo,\n" +
                    "    p.ESTATU10 estoque,\n" +
                    "    cld.precomp custo,\n" +
                    "	 CASE g.LISTADEF\n" +
                    "	 WHEN 1 THEN p.PRECO110 \n" +
                    "	 WHEN 2 THEN p.PRECO210\n" +
                    "	 WHEN 3 THEN p.PRECO310\n" +
                    "	 WHEN 4 THEN p.PRECO410\n" +
                    "	 WHEN 5 THEN p.PRECO510\n" +
                    "	 END precovenda,\n" +
                    "    p.ncm,\n" +
                    "    nullif(p.cest,'0000000') cest,\n" +
                    "    p.PISPIS10 piscofinssaida,\n" +
                    "    cld.pis piscofinsentrada,\n" +
                    "    p.natureza_receita piscofinsnatrec,\n" +
                    "    concat(coalesce(p.tribut10,''),'|',coalesce(p.basred10,'')) id_icms,\n" +
                    "    cld.*\n" +
                    "from\n" +
                    "	 genpro p\n" +
                    "	 join genpar g\n" +
                    "	 left join comcld cld on p.codpro10 = codpro30\n" +
                    (
                            this.importarSomenteBalanca ? 
                            "where\n" + 
                            "   (p.PRECO110 > 0 or p.PRECO210 > 0 or p.PRECO310 > 0 or p.PRECO410 > 0 or p.PRECO510 > 0) and\n" +
                            " 	(p.PESOVA10 = 'S' or p.ETIQPR10 = 'S')\n" +
                            " 	and if(coalesce(DADESA10, '0000-00-00 00:00:00') = '0000-00-00 00:00:00',1,0) = 1\n" +
                            " 	and p.PRECO110 > 0\n" : 
                            ""
                    ) +
                    " order by\n" +
                    "	p.CODPRO10";
            System.out.println(sql);
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(imp.getEan(), -2));
                    if (bal != null) {
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.seteBalanca(true);
                        imp.setValidade(bal.getValidade());
                    } else {
                        if (this.importarSomenteBalanca) {
                            continue;
                        }
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.seteBalanca(rst.getBoolean("pesavel"));
                        imp.setValidade(rst.getInt("validade"));
                    }
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso"));
                    imp.setPesoLiquido(rst.getDouble("peso"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(MathUtils.round(((rst.getDouble("precovenda") / rst.getDouble("custo")) - 1) * 100, 2, 9999999));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setSituacaoCadastro(rst.getInt("situacao"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofinsnatrec"));
                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setPautaFiscalId(imp.getImportId());         
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "		select\n" +
                "			p.CODPRO10 id,\n" +
                "			p.BARRA210 ean,\n" +
                "                   p.UNIDAD10 unidade\n" +
                "		from\n" +
                "			genpro p\n" +
                "		where\n" +
                "			p.BARRA210 != ''\n" +
                "		union\n" +
                "		select\n" +
                "			p.CODPRO10 id,\n" +
                "			p.BARRA310 ean,\n" +
                "                   p.UNIDAD10 unidade\n" +
                "		from\n" +
                "			genpro p\n" +
                "		where\n" +
                "			p.BARRA310 != ''\n" +
                "		union\n" +
                "		select\n" +
                "			p.CODPRO10 id,\n" +
                "			p.BARRA410 ean,\n" +
                "                   p.UNIDAD10 unidade\n" +
                "		from\n" +
                "			genpro p\n" +
                "		where\n" +
                "			p.BARRA410 != ''\n"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(false);

                    result.add(imp);
                }
            }
        }        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n" +
                    "	concat(coalesce(p.tribut10,''),'|',coalesce(p.basred10,'')) id_icms,\n" +
                    "    tr.idetri40 descricao,\n" +
                    "    case \n" +
                    "    when rd.redfis50 > 0 then 20\n" +
                    "    when rd.redfis50 = 0 and tr.PERTRI40 > 0 then 0\n" +
                    "    end cst,\n" +
                    "    tr.pertri40 aliquota,\n" +
                    "    rd.redfis50 reduzido\n" +
                    "from \n" +
                    "	genpro p\n" +
                    "    join gentri tr on p.tribut10 = tr.codtri40\n" +
                    "    join genred rd on p.basred10 = rd.codred50"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id_icms"),
                            String.format("%s (%03d - %.2f - %.2f)", 
                                    rst.getString("descricao"),
                                    rst.getInt("cst"),
                                    rst.getDouble("aliquota"),
                                    rst.getDouble("reduzido")
                            ),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reduzido")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        Map<String, PautaFiscalIMP> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	s.sequencia,\n" +
                    "	s.codpro,\n" +
                    "	p.ncm,\n" +
                    "	'RS' uf,\n" +
                    "	s.PercentMVA iva,\n" +
                    "	s.PercentMVACalc ivaAjustado,\n" +
                    "	s.PercentICMSST aliqst,\n" +
                    "	s.PercentReducao redst	\n" +
                    "from\n" +
                    "	logstd s\n" +
                    "	left join genpro p on s.CodPro = p.CODPRO10"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    
                    imp.setId(rst.getString("codpro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIva(rst.getDouble("iva"));
                    imp.setIvaAjustado(rst.getDouble("ivaAjustado"));
                    AliquotaVO aliq;
                    if (rst.getDouble("redst") > 0) {
                        aliq = new AliquotaVO(20, rst.getDouble("aliqst"), rst.getDouble("redst"));
                    } else {
                        aliq = new AliquotaVO(0, rst.getDouble("aliqst"), 0);
                    }
                    imp.setAliquotaCredito(aliq);
                    imp.setAliquotaCreditoForaEstado(aliq);
                    imp.setAliquotaDebito(aliq);
                    imp.setAliquotaDebitoForaEstado(aliq);
                    
                    result.put(imp.getId(), imp);
                }
            }
        }
        
        return new ArrayList<>(result.values());
    }
    
    

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	 f.CODFOR10 id,\n" +
                    "    f.NOMFOR10 razao,\n" +
                    "    f.NOMFOR10 fantasia,\n" +
                    "    f.CGCMFF10 cnpj,\n" +
                    "    f.INSEST10 inscricaoestadual,\n" +
                    "    f.desativado,\n" +
                    "    f.ENDERE10 endereco,\n" +
                    "    f.NUMERO10 numero,\n" +
                    "    f.COMPLE10 complemento,\n" +
                    "    f.BAIRRO10 bairro,\n" +
                    "    f.CIDADE10 cidade,\n" +
                    "    f.ESTADO10 estado,\n" +
                    "    f.CEPEST10 cep,\n" +
                    "    f.TEFON110 telefone1,\n" +
                    "    f.TEFON210 telefone2,\n" +
                    "    f.FFAAXX10 fax,\n" +
                    "    f.CONTA110 contato1,\n" +
                    "    f.CONTA210 contato2,\n" +
                    "    f.CONTA310 contato3,\n" +
                    "    f.CONTA410 contato4,\n" +
                    "    f.INFADICIONAIS observacoes,\n" +
                    "    f.OBSERV,\n" +
                    "    f.OBSERV_TOLEDO,\n" +
                    "    f.PERIOD10 vencimento\n" +
                    "from\n" +
                    "	comfor f\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setAtivo("N".equals(rst.getString("desativado")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone1"));
                    imp.addTelefone("TELEFONE2", rst.getString("telefone2"));
                    imp.addTelefone("FAX", rst.getString("fax"));
                    imp.addContato(rst.getString("contato1"), rst.getString("telefone1"), "", TipoContato.COMERCIAL, "");
                    imp.addContato(rst.getString("contato2"), rst.getString("telefone2"), "", TipoContato.COMERCIAL, "");
                    imp.addContato(rst.getString("contato3"), "", "", TipoContato.COMERCIAL, "");
                    imp.addContato(rst.getString("contato4"), "", "", TipoContato.COMERCIAL, "");
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setPrazoEntrega(rst.getInt("vencimento"));
                    
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
                    "select\n" +
                    "	pf.codfor20 id_fornecedor,\n" +
                    "	pf.codpro20 id_produto,\n" +
                    "	coalesce(pf.referencia, pf.sequencial, pf.codpro20) codigoexterno,\n" +
                    "	case when pf.quauni <= 0 then 1 else pf.quauni end qtdembalagem\n" +
                    "from\n" +
                    "	comprf pf\n" +
                    "	join comfor f on pf.codfor20 = f.codfor10	\n" +
                    "where \n" +
                    "	pf.desativado = 'N'\n" +
                    "order by\n" +
                    "	1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    /*@Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	c.codigo id,\n" +
                    "	c.cgc cnpj,\n" +
                    "	c.inscricao ie,\n" +
                    "	c.razaos razao,\n" +
                    "	coalesce(nullif(c.reduzido,''), c.razaos) fantasia,\n" +
                    "	c.desativado,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.codibge cidadeibge,\n" +
                    "	c.cidade,\n" +
                    "	c.estado,\n" +
                    "	c.cep,\n" +
                    "	c.estado_civil,\n" +
                    "	c.datacad datacadastro,\n" +
                    "	coalesce(c.sexo,'') sexo,\n" +
                    "	c.infadicionais observacoes,\n" +
                    "	c.telefone,\n" +
                    "	c.fax,\n" +
                    "	c.email,\n" +
                    "	c.email_xml\n" +
                    "from \n" +
                    "	estcli c\n" +
                    "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(!"S".equals(rst.getString("desativado")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipioIBGE(rst.getInt("cidadeibge"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estado_civil"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getString("sexo").startsWith("F") ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setObservacao2(rst.getString("observacoes"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email"));
                    imp.addEmail(rst.getString("email_xml"), TipoContato.NFE);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }*/
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "    codigo id,\n" +
                    "    cpf_cnpj cnpj,\n" +
                    "    IDEN_INSC ie,\n" +
                    "    nome_razao razao,\n" +
                    "    '' fantasia,\n" +
                    "    coalesce(deletado, 'N') desativado,\n" +
                    "    endereco,\n" +
                    "    bairro,\n" +
                    "    cidade,\n" +
                    "    uf,\n" +
                    "    numero,\n" +
                    "    dtnasc nascimento,\n" +
                    "    dtalt alteracao,\n" +
                    "    mae,\n" +
                    "    cep,\n" +
                    "    telefones,\n" +
                    "    email,\n" +
                    "    limite2,\n" +
                    "    obs,\n" +
                    "    tipocli\n" +
                    "FROM \n" +
                    "	cliente c\n" +
                    "order by\n" +
                    "	codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(!"S".equals(rst.getString("desativado")));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacao2(rst.getString("obs"));
                    imp.setTelefone(rst.getString("telefones"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("limite2"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.codigo id,\n" +
                    "	c.cgc cnpj,\n" +
                    "	c.inscricao ie,\n" +
                    "	c.razaos razao,\n" +
                    "	coalesce(nullif(c.reduzido,''), c.razaos) fantasia,\n" +
                    "	c.desativado,\n" +
                    "	c.endereco,\n" +
                    "	c.numero,\n" +
                    "	c.complemento,\n" +
                    "	c.bairro,\n" +
                    "	c.codibge cidadeibge,\n" +
                    "	c.cidade,\n" +
                    "	c.estado,\n" +
                    "	c.cep,\n" +
                    "	c.estado_civil,\n" +
                    "	c.datacad datacadastro,\n" +
                    "	coalesce(c.sexo,'') sexo,\n" +
                    "	c.infadicionais observacoes,\n" +
                    "	c.telefone,\n" +
                    "	c.fax,\n" +
                    "	c.email,\n" +
                    "	c.email_xml,\n" +
                    "   '2020-01-01' datainicio,\n" +
                    "   '2020-12-31' datatermino\n" +        
                    "from \n" +
                    "	estcli c\n" +
                    "order by 1")) {
                while(rs.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoEstadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    
                    imp.setSituacaoCadastro(!"S".equals(rs.getString("desativado")) ? 
                            SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataTermino(rs.getDate("datatermino"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "    codigo id_cliente,\n" +
                    "    CPF_CNPJ,\n" +
                    "    cupom,\n" +
                    "    EQUIPAMENTO ecf,\n" +
                    "    data,\n" +
                    "    dtvenc vencimento,\n" +
                    "    hora,\n" +
                    "    (valor - valorpago) valor,\n" +
                    "    obs\n" +
                    "FROM \n" +
                    "	climov\n" +
                    "where\n" +
                    "    situacao = 0 and\n" +
                    "    dtvenc is not null and\n" +
                    "    dtvenc != '1899-12-30'\n" +
                    "order by\n" +
                    "	dtvenc")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id_cliente") + "-" + 
                            rs.getString("cupom") + "-" + 
                            rs.getString("ecf") + "-" +
                            rs.getString("data"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("data"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n" +
                    "    ofe.DATAINI datainicio,\n" +
                    "    ofe.DATAFIM datafim,\n" +
                    "    ofei.PRODUTO id_produto,\n" +
                    "    ofei.DESCRICAO descricao_produto,\n" +
                    "    ofei.PRECOANT precovenda,\n" +
                    "    ofei.PRECOPRM precopromocao\n" +
                    "FROM \n" +
                    "	cadprc ofe\n" +
                    "join cadpri ofei on ofei.CODIGO = ofe.CODIGO\n" +
                    "where\n" +
                    "	ofe.datafim > now() and \n" +
                    "	ofe.TIPO = 1\n" +
                    "order by\n" +
                    "	ofe.datafim"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datafim"));
                    imp.setPrecoOferta(rst.getDouble("precopromocao"));
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataVendaInicio;
    private Date dataVendaTermino;

    public void setDataVendaInicio(Date dataVendaInicio) {
        this.dataVendaInicio = dataVendaInicio;
    }

    public void setDataVendaTermino(Date dataVendaTermino) {
        this.dataVendaTermino = dataVendaTermino;
    }
    
    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataVendaInicio, dataVendaTermino);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataVendaInicio, dataVendaTermino);
    }
    
    private static final SimpleDateFormat VENDA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        next.setId((
                                rst.getString("ecf") + "-" + 
                                rst.getString("numerocupom") + "-" + 
                                VENDA_DATE_FORMAT.format(rst.getDate("data"))
                        ));
                        next.setEcf(rst.getInt("ecf"));
                        next.setNumeroCupom(rst.getInt("numerocupom"));
                        next.setData(rst.getDate("data"));
                        next.setHoraInicio(rst.getTime("horainicio"));
                        next.setHoraInicio(rst.getTime("horatermino"));
                        //next.setIdClientePreferencial(rst.getString("id_cliente"));
                        //next.setCpf(rst.getString("cnpj"));
                        if (rst.getDouble("subtotalimpressora") == 0) {
                            next.setCancelado(true);
                            next.setSubTotalImpressora(rst.getDouble("valorcancelado"));
                        } else {
                            next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        }                        
                        next.setChaveNfCe(rst.getString("chv_nfe"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql = "SELECT\n" +
                "	mov.nrequip ecf,\n" +
                "	mov.cupom numerocupom,\n" +
                "	mov.data,\n" +
                "	min(mov.hora) horainicio,\n" +
                "	max(mov.hora) horatermino,\n" +
                "	##if(cli.cgc = 0, null, mov.cliente) id_cliente,\n" +
                "	##cli.cgc cnpj,\n" +
                "	SUM(\n" +
                "		IF(\n" +
                "			(mov.ENTRSAI = \"S\" AND mov.TROCO = \"\" AND mov.SITUACAO = \"C\"), \n" +
                "			ROUND(mov.VALOR, 6),\n" +
                "			0\n" +
                "		)\n" +
                "	) valorcancelado,\n" +
                "	SUM(\n" +
                "		IF(\n" +
                "			(mov.ENTRSAI = \"S\" AND mov.TROCO = \"S\" AND mov.SITUACAO = \"\") OR\n" +
                "			(mov.ENTRSAI = \"S\" AND mov.TROCO = \"\" AND mov.SITUACAO = \"C\"), \n" +
                "			ROUND(mov.VALOR * -1, 6), \n" +
                "			ROUND(mov.VALOR, 6)\n" +
                "		)\n" +
                "	) subtotalimpressora,\n" +
                "	min(mc.chv_nfe) chv_nfe,\n" +
                "	mc.ser serie,\n" +
                "	mc.cod_mod modelo\n" +
                "FROM\n" +
                "	RETMOV mov\n" +
                "	left join memcab mc on date_format(mc.Dt_Doc,'%Y%m%d') =date_format(mov.data,'%Y%m%d') AND mc.Ser = mov.nrequip AND mc.NroTransacao = mov.cupom\n" +
                "	left join auxequ ecf on ecf.codigo = mov.nrequip and ecf.tipo = 1\n" +
                "	left join estcli cli on cli.codigo = mov.cliente\n" +
                "WHERE\n" +
                "	mov.DATA between " + SQLUtils.dateSQL(dataInicio) + " and " + SQLUtils.dateSQL(dataTermino) + "\n" +
                "	AND UCASE(mov.SANGRIA) NOT IN (\"F\", \"S\", \"P\", \"R\")\n" +
                "	AND (\n" +
                "                   (mov.ENTRSAI = \"E\" AND mov.TROCO = \"\" AND mov.SITUACAO = \"\")\n" +
                "		OR  (mov.ENTRSAI = \"S\" AND mov.TROCO = \"S\" AND mov.SITUACAO = \"\")\n" +
                "		OR  (mov.ENTRSAI = \"S\" AND mov.TROCO = \"\" AND mov.SITUACAO = \"C\")\n" +
                "		OR  (mov.ENTRSAI = \"E\" AND mov.TROCO = \"S\" AND mov.SITUACAO = \"C\")\n" +
                "	) AND mov.valor > 0\n" +
                "group by\n" +
                "	mov.nrequip,\n" +
                "	mov.cupom,\n" +
                "	mov.data,\n" +
                "	##mov.cliente,\n" +
                "	##cli.cgc,\n" +
                "	mc.ser,\n" +
                "	mc.cod_mod\n" +
                "order by\n" +
                "	1, 2";
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

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setId(
                                rst.getString("nrequip") + "-" + 
                                rst.getString("cupom") + "-" + 
                                VENDA_DATE_FORMAT.format(rst.getDate("data")) + "-" + 
                                rst.getString("item") +
                                rst.getString("id_produto")
                        );
                        next.setVenda(
                                rst.getString("nrequip") + "-" + 
                                rst.getString("cupom") + "-" + 
                                VENDA_DATE_FORMAT.format(rst.getDate("data"))
                        );
                        next.setSequencia(Utils.stringToInt(rst.getString("item")));
                        next.setProduto(rst.getString("id_produto"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("qtd"));
                        next.setTotalBruto(rst.getDouble("totalbruto"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));                        
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql = "select\n" +
            "	i.nrequip,\n" +
            "	i.data,\n" +
            "	min(i.hora) hora,\n" +
            "	i.cupom,\n" +
            "	i.item,\n" +
            "	i.codpro10 id_produto,\n" +
            "	p.descpr10 descricaoreduzida,\n" +
            "	avg(i.quantid) qtd,\n" +
            "	sum(case when i.situacao = 'C' then -1 * i.vendtot else i.vendtot end) totalbruto,\n" +
            "	max(case when i.situacao = 'C' then 1 else 0 end) cancelado,\n" +
            "	sum(i.desconto) desconto,\n" +
            "	sum(i.acrescimo) acrescimo,\n" +
            "	i.produto ean,\n" +
            "	p.UNIDAD10 unidade\n" +
            "from\n" +
            "	retvdi i\n" +
            "	join genpro p on i.codpro10 = p.codpro10\n" +
            "where\n" +
            "	i.data between " + SQLUtils.dateSQL(dataInicio) + " and " + SQLUtils.dateSQL(dataTermino) + "\n" +
            "group by\n" +
            "	nrequip,\n" +
            "	data,\n" +
            "	cupom,\n" +
            "	item,\n" +
            "	id_produto,\n" +
            "	descricaoreduzida,\n" +
            "	ean,\n" +
            "	unidade\n" +
            "order by\n" +
            "	nrequip,\n" +
            "	data,\n" +
            "	cupom,\n" +
            "	item";
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
