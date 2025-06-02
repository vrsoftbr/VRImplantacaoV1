package vrimplantacao2_5.dao.sistema;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
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
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClienteEventuallDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.LogEstoqueVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.devolucao.receber.ReceberDevolucaoDAO;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.PagarFornecedorDAO;
import vrimplantacao2.dao.cadastro.financeiro.recebervendaprazo.ReceberVendaPrazaoDAO;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.financeiro.PagarFornecedorVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberDevolucaoVO;
import vrimplantacao2.vo.cadastro.financeiro.ReceberVendaPrazoVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.cadastro.venda.PdvVendaVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Lucas
 */
public class VisualMixDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(VisualMixDAO.class.getName());

    public boolean i_contaAbertaDevolucao = false;
    public String dataEmissaoDevolucao;

    public boolean i_contaAbertaVendaPrazo = false;
    public String dataEmissaoVendaPr;
    
    public String i_arquivo;
    
    public String dataVirada;

    @Override
    public String getSistema() {
        return "VisualMix";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.FABRICANTE,
                    OpcaoProduto.ASSOCIADO,
                    OpcaoProduto.COMPRADOR,
                    OpcaoProduto.COMPRADOR_PRODUTO,
                    OpcaoProduto.RECEITA,
                    OpcaoProduto.RECEITA_BALANCA,
                    OpcaoProduto.NUMERO_PARCELA,
                    OpcaoProduto.TECLA_ASSOCIADA,
                    OpcaoProduto.PRODUTOS_BALANCA,
                    OpcaoProduto.TROCA
                }
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
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.CONVENIO_CONVENIADO));
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
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OUTRAS_RECEITAS));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codigo, \n"
                    + "	descricao\n"
                    + "from dbo.Lojas\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	al.CODIGO as id, \n"
                    + "	al.DESCRICAO as descricao, "
                    + "	al.SITUACAOTRIBUTARIA as cst,\n"
                    + " al.PERCENTUAL as aliquota, \n"
                    + "	al.REDUCAO as reducao \n"
                    + "from dbo.Aliquotas_NF al\n"
                    + "where codigo in (select Aliquota_NF from dbo.Produtos)\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "cast(f.Codigo as bigint) as Codigo, "
                    + "f.Descricao "
                    + "from dbo.Grupo_Precos f "
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setDescricao(rst.getString("Descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "Mercadologico1 as merc1, "
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 1\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));

                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Mercadologico1 as merc1, \n"
                    + "Mercadologico2 as merc2, \n"
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 2\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rst.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rst.getString("merc2"),
                                rst.getString("descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Mercadologico1 as merc1,\n"
                    + "	Mercadologico2 as merc2, \n"
                    + "	Mercadologico3 as merc3, \n"
                    + "	Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 3\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutosBalanca() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cast(p.Produto_Id as bigint) as id,\n"
                    + " cast(p.Digito_Id as bigint) as digito,\n"
                    + "	cast(ean.Codigo_Automacao as bigint) as Codigo_Automacao,\n"
                    + "	cast(ean.Digito_Automacao as bigint) as Digito_Automacao,\n"
                    + " cast(ean.Tipo_Codigo as bigint) as tipocodigo,\n"
                    + " p.Peso_Variavel,\n"
                    + " p.Pre_Pesado,\n"
                    + " p.Qtd_Decimal,\n"
                    + " p.ProdutoPai,\n"
                    + "	p.Descricao_Completa as descricaocompleta, \n"
                    + " p.Descricao_Reduzida as descricaoreduzida, \n"
                    + " p.Descricao_Balanca,\n"
                    + "	est.Custo_Ultima_Entrada_Com_Icms as custocomimposto,\n"
                    + " est.Custo_Ultima_Entrada_Sem_Icms as custosemimposto,\n"
                    + "	pre.preco_venda as precovenda,\n"
                    + " p.Margem_Atacado, \n"
                    + " p.Margem_Teorica, \n"
                    + " p.MargemFixa, \n"
                    + " p.Aliquota, \n"
                    + " p.Aliquota_FCP, \n"
                    + " p.Aliquota_Interna, \n"
                    + " p.Aliquota_NF,\n"
                    + " cast(f.Codigo as bigint) as idfamiliaproduto,\n"
                    + "	p.Mercadologico1, \n"
                    + " p.Mercadologico2, \n"
                    + " p.Mercadologico3, \n"
                    + " p.Mercadologico4, \n"
                    + " p.Mercadologico5, \n"
                    + " p.Situacao as situacaocadastro,\n"
                    + "	p.SituacaoTributaria as csticms, \n"
                    + " est.EstoqueInicial01 as estoque, \n"
                    + " est.EstoqueInicial02 as troca,\n"
                    + " p.EspecUnitariaTipo as tipoembalagem, \n"
                    + " p.EspecUnitariaQtde as qtdembalagem,\n"
                    + " emb.Qtd_Produto as qtdembalagem_ean,"
                    + "	p.TipoProduto, \n"
                    //+ " p.Codigo_NCMCodigo_NCM as ncm, \n"
                    + " p.ClasSitFiscal as ncm,\n"
                    + " p.CEST as cest, \n"
                    + " p.TipoCodMercad as tipomercadoria,\n"
                    + "	p.CstPisCofinsEntrada, \n"
                    + " p.CstPisCofinsSaida, \n"
                    + " p.NaturezaReceita,\n"
                    + " cast(p.Fabricante as bigint) as idfabricante,\n"
                    + " cast(p.Comprador as bigint) as idcomprador,\n"
                    + " fz.Tecla,\n"
                    + " p.DataInclusao as datacadastro,\n"
                    + " p.DataAlteracao as dataalteracao\n"
                    + "from dbo.Produtos p\n"
                    + "left join dbo.Precos_Loja pre on pre.produto_id = p.Produto_Id\n"
                    + "	and pre.loja = " + getLojaOrigem() + " and pre.sequencia = 1\n"
                    + "left join dbo.Produtos_Estoque est on est.Produto_Id = p.Produto_Id\n"
                    + "	and est.Loja = " + getLojaOrigem() + "\n"
                    + "left join dbo.Automacao ean on ean.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Grupo_Precos_Produtos f on f.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Filizola fz on fz.CodigoProduto = p.Produto_Id\n"
                    + "left join dbo.Embalagem emb on emb.Produto_Id = p.Produto_Id\n"
                    + "	and emb.Sequencia = ean.Seq_Embalagem\n"
                    + "where p.Peso_Variavel = 1\n"
                    + "and ean.Codigo_Automacao is not null\n"
                    + "and ean.Codigo_Automacao <= 999999\n"
                    + "order by p.Produto_Id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id") + rst.getString("digito"));

                    String ean = (rst.getString("Codigo_Automacao") + rst.getString("Digito_Automacao")).trim();

                    if ((rst.getString("Codigo_Automacao") != null)
                            && (!rst.getString("Codigo_Automacao").trim().isEmpty())
                            && (rst.getString("Digito_Automacao") != null)
                            && (!rst.getString("Digito_Automacao").trim().isEmpty())) {

                        long codigoProduto;
                        codigoProduto = Long.parseLong(rst.getString("Codigo_Automacao").trim());
                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            imp.setEan(rst.getString("Codigo_Automacao").trim());
                        } else {

                            if (rst.getInt("Peso_Variavel") == 1) {
                                imp.seteBalanca(true);
                                imp.setEan(rst.getString("Codigo_Automacao").trim());
                            } else {
                                imp.seteBalanca(false);
                                imp.setEan(ean);
                            }
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setEan(ean);
                    }

                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem_ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rst.getString("Mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("Mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("Mercadologico3"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setFornecedorFabricante(rst.getString("idfabricante"));
                    imp.setIdComprador(rst.getString("idcomprador"));
                    imp.setMargem(rst.getDouble("Margem_Teorica"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setTroca(rst.getDouble("troca"));
                    imp.setTeclaAssociada(rst.getInt("Tecla"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CstPisCofinsSaida"));
                    imp.setPiscofinsCstCredito(rst.getString("CstPisCofinsEntrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NaturezaReceita"));
                    imp.setIcmsDebitoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsCreditoId(rst.getString("Aliquota_NF"));
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
                    + "	cast(p.Produto_Id as bigint) as id,\n"
                    + " cast(p.Digito_Id as bigint) as digito,\n"
                    + "	cast(ean.Codigo_Automacao as bigint) as Codigo_Automacao,\n"
                    + "	cast(ean.Digito_Automacao as bigint) as Digito_Automacao,\n"
                    + " cast(ean.Tipo_Codigo as bigint) as tipocodigo,\n"
                    + " p.Peso_Variavel,\n"
                    + " p.Pre_Pesado,\n"
                    + " p.Qtd_Decimal,\n"
                    + " p.ProdutoPai,\n"
                    + "	p.Descricao_Completa as descricaocompleta, \n"
                    + " p.Descricao_Reduzida as descricaoreduzida, \n"
                    + " p.Descricao_Balanca,\n"
                    + "	est.Custo_Ultima_Entrada_Com_Icms as custocomimposto,\n"
                    + " est.Custo_Ultima_Entrada_Sem_Icms as custosemimposto,\n"
                    + "	pre.preco_venda as precovenda,\n"
                    + " p.Margem_Atacado, \n"
                    + " p.Margem_Teorica, \n"
                    + " p.MargemFixa, \n"
                    + " p.Aliquota, \n"
                    + " p.Aliquota_FCP, \n"
                    + " p.Aliquota_Interna, \n"
                    + " p.Aliquota_NF,\n"
                    + " cast(f.Codigo as bigint) as idfamiliaproduto,\n"
                    + "	p.Mercadologico1, \n"
                    + " p.Mercadologico2, \n"
                    + " p.Mercadologico3, \n"
                    + " p.Mercadologico4, \n"
                    + " p.Mercadologico5, \n"
                    + " p.Situacao as situacaocadastro,\n"
                    + "	p.SituacaoTributaria as csticms, \n"
                    + " est.EstoqueInicial01 as estoque, \n"
                    + " est.EstoqueInicial02 as troca,\n"
                    + " p.EspecUnitariaTipo as tipoembalagem, \n"
                    + " p.EspecUnitariaQtde as qtdembalagem,\n"
                    + " emb.Qtd_Produto as qtdembalagem_ean,"
                    + "	p.TipoProduto, \n"
                    //+ " p.Codigo_NCM as ncm, \n"
                    + " p.ClasSitFiscal as ncm,\n"
                    + " p.CEST as cest, \n"
                    + " p.TipoCodMercad as tipomercadoria,\n"
                    + "	p.CstPisCofinsEntrada, \n"
                    + " p.CstPisCofinsSaida, \n"
                    + " p.NaturezaReceita,\n"
                    + " cast(p.Fabricante as bigint) as idfabricante,\n"
                    + " cast(p.Comprador as bigint) as idcomprador,\n"
                    + " fz.Tecla,\n"
                    + " p.DataInclusao as datacadastro,\n"
                    + " p.DataAlteracao as dataalteracao\n"
                    + "from dbo.Produtos p\n"
                    + "left join dbo.Precos_Loja pre on pre.produto_id = p.Produto_Id\n"
                    + "	and pre.loja = " + getLojaOrigem() + " and pre.sequencia = 1\n"
                    + "left join dbo.Produtos_Estoque est on est.Produto_Id = p.Produto_Id\n"
                    + "	and est.Loja = " + getLojaOrigem() + "\n"
                    + "left join dbo.Automacao ean on ean.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Grupo_Precos_Produtos f on f.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Filizola fz on fz.CodigoProduto = p.Produto_Id\n"
                    + "left join dbo.Embalagem emb on emb.Produto_Id = p.Produto_Id\n"
                    + "	and emb.Sequencia = ean.Seq_Embalagem\n"
                    + "order by p.Produto_Id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id") + rst.getString("digito"));

                    String ean = (rst.getString("Codigo_Automacao") + rst.getString("Digito_Automacao")).trim();

                    if ((rst.getString("Codigo_Automacao") != null)
                            && (!rst.getString("Codigo_Automacao").trim().isEmpty())
                            && (rst.getString("Digito_Automacao") != null)
                            && (!rst.getString("Digito_Automacao").trim().isEmpty())) {

                        if (ean.trim().length() <= 6) {
                            if (rst.getInt("tipocodigo") == 2) {
                                imp.seteBalanca(true);
                                imp.setEan(rst.getString("Codigo_Automacao"));
                            } else {
                                imp.seteBalanca(false);
                                imp.setEan(ean);
                                imp.setManterEAN(true);
                            }
                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(ean);
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setEan(ean);
                    }

                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem_ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rst.getString("Mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("Mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("Mercadologico3"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setFornecedorFabricante(rst.getString("idfabricante"));
                    imp.setIdComprador(rst.getString("idcomprador"));
                    imp.setMargem(rst.getDouble("Margem_Teorica"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setTroca(rst.getDouble("troca"));
                    imp.setTeclaAssociada(rst.getInt("Tecla"));
                    imp.setSituacaoCadastro("A".equals(rst.getString("situacaocadastro")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CstPisCofinsSaida"));
                    imp.setPiscofinsCstCredito(rst.getString("CstPisCofinsEntrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("NaturezaReceita"));

                    imp.setIcmsDebitoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("Aliquota_NF"));
                    imp.setIcmsCreditoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("Aliquota_NF"));
                    imp.setIcmsConsumidorId(rst.getString("Aliquota_NF"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.EXCECAO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	cast(p.Produto_Id as bigint) as id,\n"
                        + "	cast(p.Digito_Id as bigint) as digito,\n"
                        + "	cast(mx.ncm as bigint) as ncm,\n"
                        + "	mx.mxf_icms_cst_s as icms_cst_s,\n"
                        + "	(cast(coalesce(mx.mxf_icms_alq_s, '0') as numeric) / 1000) icms_alqt_s,\n"
                        + "	(cast(coalesce(mx.mxf_icms_rbc_s, '0') as numeric) / 1000) icms_rbc_s,\n"
                        + "	mx.mxf_icms_cst_e as icms_cst_e,\n"
                        + "	(cast(coalesce(mx.mxf_icms_alq_e, '0') as numeric) / 1000) icms_alqt_e,\n"
                        + "	(cast(coalesce(mx.mxf_icms_rbc_e, '0') as numeric) / 1000) icms_rbc_e, \n"
                        + "	(cast(coalesce(mx.mxf_icms_iva_valor, '0') as numeric) / 1000) iva\n"
                        + "from dbo.produtosMixFiscal mx\n"
                        + "join dbo.Produtos p on p.Produto_Id = mx.codigo_produto\n"
                        + "where (cast(coalesce(mx.mxf_icms_iva_valor, '0') as numeric) / 1000) > 0\n"
                        + "and mx.ncm is not null"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id") + rst.getString("digito"));

                        String id_pautafiscal = rst.getString("ncm")
                                + rst.getString("icms_cst_s")
                                + rst.getString("icms_alqt_s")
                                + rst.getString("icms_rbc_s")
                                + rst.getString("icms_cst_e")
                                + rst.getString("icms_alqt_e")
                                + rst.getString("icms_rbc_e")
                                + rst.getString("iva");

                        imp.setPautaFiscalId(id_pautafiscal);

                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(p1.Produto_Id as bigint) as produto_pai,\n"
                    + " cast(p1.Digito_Id as bigint) as digito_pai_id,\n"
                    + "	p1.EspecUnitariaQtde as qtembalagem_pai,\n"
                    + "	cast(p2.Produto_Id as bigint) as produto_filho,\n"
                    + " cast(p2.Digito_Id as bigint) as digito_filho_id,\n"
                    + "	p2.EspecUnitariaQtde as qtdembalagem_filho\n"
                    + "from dbo.Produtos p1 \n"
                    + "join dbo.Produtos p2 on p2.ProdutoPai = p1.Produto_Id"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();
                    imp.setImpIdProduto(rst.getString("produto_pai") + rst.getString("digito_pai_id"));
                    imp.setQtdEmbalagem(rst.getInt("qtembalagem_pai") == 0 ? 1 : rst.getInt("qtembalagem_pai"));
                    imp.setImpIdProdutoItem(rst.getString("produto_filho") + rst.getString("digito_filho_id"));
                    imp.setQtdEmbalagemItem(rst.getInt("qtdembalagem_filho") == 0 ? 1 : rst.getInt("qtdembalagem_filho"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	distinct cast(mx.ncm as bigint) as ncm,\n"
                    + "	mx.mxf_icms_cst_s as icms_cst_s,\n"
                    + "	(cast(coalesce(mx.mxf_icms_alq_s, '0') as numeric) / 1000) icms_alqt_s,\n"
                    + "	(cast(coalesce(mx.mxf_icms_rbc_s, '0') as numeric) / 1000) icms_rbc_s,\n"
                    + "	mx.mxf_icms_cst_e as icms_cst_e,\n"
                    + "	(cast(coalesce(mx.mxf_icms_alq_e, '0') as numeric) / 1000) icms_alqt_e,\n"
                    + "	(cast(coalesce(mx.mxf_icms_rbc_e, '0') as numeric) / 1000) icms_rbc_e, \n"
                    + "	(cast(coalesce(mx.mxf_icms_iva_valor, '0') as numeric) / 1000) iva\n"
                    + "from dbo.produtosMixFiscal mx\n"
                    + "where (cast(coalesce(mx.mxf_icms_iva_valor, '0') as numeric) / 1000) > 0\n"
                    + "and mx.ncm is not null"
            )) {
                while (rst.next()) {
                    PautaFiscalIMP imp = new PautaFiscalIMP();
                    imp.setTipoIva(TipoIva.VALOR);
                    imp.setId(
                            rst.getString("ncm")
                            + rst.getString("icms_cst_s")
                            + rst.getString("icms_alqt_s")
                            + rst.getString("icms_rbc_s")
                            + rst.getString("icms_cst_e")
                            + rst.getString("icms_alqt_e")
                            + rst.getString("icms_rbc_e")
                            + rst.getString("iva")
                    );

                    System.out.println(rst.getString("ncm"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setIva(rst.getDouble("iva"));
                    imp.setIvaAjustado(imp.getIva());
                    imp.setUf(Parametros.get().getUfPadraoV2().getSigla());

                    int cstSaida = rst.getInt("icms_cst_s");
                    double aliquotaSaida = rst.getDouble("icms_alqt_s");
                    double reduzidoSaida = rst.getDouble("icms_rbc_s");
                    int cstEntrada = rst.getInt("icms_cst_e");
                    double aliquotaEntrada = rst.getDouble("icms_alqt_e");
                    double reduzidoEntrada = rst.getDouble("icms_rbc_e");

                    if (aliquotaSaida > 0 && reduzidoSaida == 0) {
                        cstSaida = 0;
                    }
                    if (aliquotaEntrada > 0 && reduzidoEntrada == 0) {
                        cstEntrada = 0;
                    }

                    if (aliquotaSaida > 0 && reduzidoSaida > 0) {
                        cstSaida = 20;
                    }
                    if (aliquotaEntrada > 0 && reduzidoEntrada > 0) {
                        cstEntrada = 20;
                    }

                    imp.setAliquotaDebito(cstSaida, aliquotaSaida, reduzidoSaida);
                    imp.setAliquotaDebitoForaEstado(cstSaida, aliquotaSaida, reduzidoSaida);
                    imp.setAliquotaCredito(cstEntrada, aliquotaEntrada, reduzidoEntrada);
                    imp.setAliquotaCreditoForaEstado(cstEntrada, aliquotaEntrada, reduzidoEntrada);

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
                    "select \n"
                    + "	cast(f.Codigo as bigint) as id,\n"
                    + " f.Tipo as idtipofornecedor,\n"
                    + " tf.Descricao as tipofornecedor,\n"
                    + " f.RazaoSocial as razao,\n"
                    + " f.NomeFantasia as fantasia,\n"
                    + "	f.TipoLogradouro as logradouro,\n"
                    + " f.Endereco,\n"
                    + " CAST(f.NumeroEnd as bigint) as numero,\n"
                    + " f.Complemento,\n"
                    + " f.Bairro,\n"
                    + " f.Cidade as municipio,\n"
                    + " f.Estado as uf,\n"
                    + "	f.Cep,\n"
                    + " f.CxPostal as caixapostal,\n"
                    + " f.Telefone,\n"
                    + " f.Fax,\n"
                    + " f.Telex,\n"
                    + " f.TeleContato,\n"
                    + " f.Contato,\n"
                    + "	cast(f.CGC as bigint) as cnpj,\n"
                    + " f.InscricaoEstadual as ie,\n"
                    + " f.InscrMunicipal as im,\n"
                    + " f.PrazoEntrega,\n"
                    + " f.FrequenciaVisita as prazoVisita,\n"
                    + " f.DataCadastro,\n"
                    + "	f.CondicaoPagto,\n"
                    + " cp.Descricao as condicaopagamento,\n"
                    + " cp.Qtd_Parcelas,\n"
                    + " f.Observacao,\n"
                    + "	f.Supervisor,\n"
                    + " f.CelSupervisor,\n"
                    + " f.EmailSupervisor,\n"
                    + " f.TelSupervisor,\n"
                    + " f.Email,\n"
                    + " f.Vendedor,\n"
                    + " f.TelVendedor,\n"
                    + " f.CelVendedor,\n"
                    + "	f.EmailVendedor,\n"
                    + " f.Gerente,\n"
                    + " f.TelGerente,\n"
                    + " f.CelGerente,\n"
                    + " f.EmailGerente,\n"
                    + "	f.Situacao,\n"
                    + " f.Status\n"
                    + "from dbo.Fornecedores f\n"
                    + "left join dbo.Condicoes_Pagto cp on cp.Codigo = f.CondicaoPagto\n"
                    + "left join dbo.TipoFornecedor tf on tf.Tipo = f.Tipo\n"
                    + "order by f.Codigo"
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
                    imp.setInsc_municipal(rst.getString("im"));

                    if ((rst.getString("Endereco") != null)
                            && (!rst.getString("Endereco").trim().isEmpty())) {
                        imp.setEndereco(rst.getString("logradouro") + " " + rst.getString("Endereco"));
                    }

                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("Cep"));
                    imp.setDatacadastro(rst.getDate("DataCadastro"));

                    if ((rst.getString("Telefone") != null)
                            && (!rst.getString("Telefone").trim().isEmpty())) {

                        if (rst.getString("Telefone").startsWith("0")) {
                            imp.setTel_principal(rst.getString("Telefone").substring(1));
                        }
                    }

                    imp.setPrazoEntrega(rst.getInt("PrazoEntrega"));
                    imp.setPrazoVisita(rst.getInt("prazoVisita"));
                    imp.setPrazoSeguranca(2);
                    imp.setPrazoPedido(rst.getInt("PrazoEntrega"));

                    imp.addDivisao(
                            imp.getImportId(),
                            imp.getPrazoVisita(),
                            imp.getPrazoEntrega(),
                            imp.getPrazoSeguranca()
                    );

                    imp.setCondicaoPagamento(rst.getInt("CondicaoPagto"));
                    imp.setObservacao(rst.getString("Observacao"));

                    switch (rst.getInt("idtipofornecedor")) {
                        case 1:
                            imp.setTipoFornecedor(TipoFornecedor.INDUSTRIA);
                            break;
                        case 2:
                            imp.setTipoFornecedor(TipoFornecedor.DISTRIBUIDOR);
                            break;
                        case 3:
                            imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
                            break;
                        case 6:
                            imp.setTipoFornecedor(TipoFornecedor.PRESTADOR);
                        default:
                            break;
                    }

                    if ((rst.getString("Email") != null)
                            && (!rst.getString("Email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("Email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("FAX", rst.getString("fax"));
                    }
                    if ((rst.getString("Telex") != null)
                            && (!rst.getString("Telex").trim().isEmpty())) {
                        imp.addTelefone("TELEX", rst.getString("Telex"));
                    }
                    if ((rst.getString("TeleContato") != null)
                            && (!rst.getString("TeleContato").trim().isEmpty())) {
                        imp.addTelefone(rst.getString("Contato") == null ? "CONTATO" : rst.getString("Contato"), rst.getString("TeleContato"));
                    }

                    // Dados do Supervisor
                    imp.addContato(
                            rst.getString("Supervisor") == null ? "" : rst.getString("Supervisor"),
                            rst.getString("TelSupervisor") == null ? "" : rst.getString("TelSupervisor"),
                            rst.getString("CelSupervisor") == null ? "" : rst.getString("CelSupervisor"),
                            TipoContato.COMERCIAL,
                            rst.getString("EmailSupervisor") == null ? "" : rst.getString("EmailSupervisor").toLowerCase()
                    );

                    // Dados do Vendedor
                    imp.addContato(
                            rst.getString("Vendedor") == null ? "" : rst.getString("Vendedor"),
                            rst.getString("TelVendedor") == null ? "" : rst.getString("TelVendedor"),
                            rst.getString("CelVendedor") == null ? "" : rst.getString("CelVendedor"),
                            TipoContato.COMERCIAL,
                            rst.getString("EmailVendedor") == null ? "" : rst.getString("EmailVendedor").toLowerCase()
                    );

                    // Dados do Gerente
                    imp.addContato(
                            rst.getString("Gerente") == null ? "" : rst.getString("Gerente"),
                            rst.getString("TelGerente") == null ? "" : rst.getString("TelGerente"),
                            rst.getString("CelGerente") == null ? "" : rst.getString("CelGerente"),
                            TipoContato.COMERCIAL,
                            rst.getString("EmailGerente") == null ? "" : rst.getString("EmailGerente").toLowerCase()
                    );

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
                    "select \n"
                    + "	cast(pf.Fornecedor as bigint) as idfornecedor,\n"
                    + "	cast(pf.Produto_Id as bigint) as idproduto,\n"
                    + " cast(p.Digito_Id as bigint) as digito_id,\n"
                    + "	pf.Referencia as codigoexterno,\n"
                    + "	pf.Qtde_Emb as qtdembalagem,\n"
                    + "	pf.Preco_Tabela as custo\n"
                    + "from dbo.Produtos_Fornecedor pf\n"
                    + "join dbo.Produtos p on p.Produto_Id = pf.Produto_Id\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto") + rst.getString("digito_id"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custo"));
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
                    "select \n"
                    + "	c.Codigo as id, \n"
                    + "	c.Nome as razao, \n"
                    + "	c.Apelido as fantasia,\n"
                    + "	c.RG, \n"
                    + "	cast(c.CPF as bigint) as CPF, \n"
                    + "	c.IDSexo as sexo,\n"
                    + "	c.DataNascimento,\n"
                    + "	c.EstadoCivil,\n"
                    + "	c.NomeConjuge,\n"
                    + "	c.DataNascimentoConjuge,\n"
                    + "	c.Endereco,\n"
                    + "	c.Numero,\n"
                    + "	c.Complemento,\n"
                    + "	c.Bairro,\n"
                    + "	c.CEP,\n"
                    + "	c.Cidade as municipio,\n"
                    + "	c.Estado as uf,\n"
                    + "	c.Referencia,\n"
                    + "	c.TipoEndereco,\n"
                    + "	c.eMail,\n"
                    + "	c.Empresa,\n"
                    + "	c.DataAdmissao,\n"
                    + "	c.CodigoProfissao,\n"
                    + "	c.TelefoneEmpresa,\n"
                    + "	e.Descricao as nomeempresa,\n"
                    + "	e.EnderecoEmpresa,\n"
                    + "	e.NumeroEmpresa,\n"
                    + "	e.ComplementoEmpresa,\n"
                    + "	e.BairroEmpresa,\n"
                    + "	e.CidadeEmpresa,\n"
                    + "	e.EstadoEmpresa,\n"
                    + "	e.CEPEmpresa,"
                    + "	c.RamalEmpresa,\n"
                    + "	c.DataInclusao as datacadastro,\n"
                    + "	c.Telefone,\n"
                    + "	c.InscEstadual as ie_rg,\n"
                    + "	c.Status,\n"
                    + "	c.LimiteCredito as valorlimite,\n"
                    + "	c.LimiteCheques,\n"
                    + "	c.DescProfissao as cargo,\n"
                    + " p.Descricao as profissao\n,"
                    + "	c.Renda as salario,\n"
                    + "	c.EnderecoEntrega,\n"
                    + "	c.NumeroEntrega,\n"
                    + "	c.ComplEntrega,\n"
                    + "	c.BairroEntrega,\n"
                    + "	c.CidadeEntrega as municipioentrega,\n"
                    + "	c.UFEntrega as ufentrega,\n"
                    + "	c.CEPEntrega as cepentrega,\n"
                    + "	c.FoneEntrega as telefoneentrega,\n"
                    + " c.DataNascimentoConjuge as datanascimentoconjuge\n"
                    + "from dbo.Clientes c\n"
                    + "left join [dbo].Empresa e on e.Codigo = c.Empresa\n"
                    + "left join [dbo].Profissao p on p.Codigo = c.CodigoProfissao\n"
                    + "where c.IDLoja = " + getLojaOrigem() + "\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("CPF"));
                    imp.setInscricaoestadual(rst.getString("RG"));
                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setTelefone(rst.getString("Telefone"));
                    imp.setEmail(rst.getString("eMail") == null ? "" : rst.getString("eMail").toLowerCase());
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    imp.setDataNascimento(rst.getDate("DataNascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setNomeConjuge(rst.getString("NomeConjuge"));
                    imp.setDataNascimentoConjuge(rst.getDate("datanascimentoconjuge"));

                    imp.setEmpresa(rst.getString("nomeempresa"));
                    imp.setEmpresaEndereco(rst.getString("EnderecoEmpresa"));
                    imp.setEmpresaNumero(rst.getString("NumeroEmpresa"));
                    imp.setEmpresaComplemento(rst.getString("ComplementoEmpresa"));
                    imp.setEmpresaBairro(rst.getString("BairroEmpresa"));
                    imp.setEmpresaMunicipio(rst.getString("CidadeEmpresa"));
                    imp.setEmpresaUf(rst.getString("EstadoEmpresa"));
                    imp.setEmpresaCep(rst.getString("CEPEmpresa"));
                    imp.setEmpresaTelefone(rst.getString("TelefoneEmpresa"));
                    imp.setDataAdmissao(rst.getDate("DataAdmissao"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setSalario(rst.getDouble("salario"));

                    if (rst.getInt("EstadoCivil") == 2) {
                        imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

                    switch (rst.getInt("sexo")) {
                        case 1:
                            imp.setSexo(TipoSexo.MASCULINO);
                            break;
                        default:
                            imp.setSexo(TipoSexo.FEMININO);
                            break;
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CompradorIMP> getCompradores() throws Exception {
        List<CompradorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(Codigo as bigint) as id,\n"
                    + "	Nome\n"
                    + "from dbo.Compradores"
            )) {
                while (rst.next()) {
                    CompradorIMP imp = new CompradorIMP();

                    imp.setManterId(true);

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("Nome"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(r.Codigo_Produto as bigint) as codigo_receita,\n"
                    + "	cast(p.Produto_Id as bigint) as id_produto,\n"
                    + " cast(p.Digito_Id as bigint) as digito_id,\n"
                    + "	p.Descricao_Completa as descricao_receita,\n"
                    + "	r.Quant_Produto as qtd,\n"
                    + "	r.Fator as fator,\n"
                    + "	cast(p2.Produto_Id as bigint) as codigo_item,\n"
                    + " cast(p2.Digito_Id as bigint) as digito_id_item,\n"
                    + "	p2.Descricao_Completa as descrocao_item,\n"
                    + "	(ri.Quant_Produto * 1000) as qtd_item,\n"
                    + "	ri.Fator as fator_item\n"
                    + "from dbo.Receita r\n"
                    + "join dbo.Produtos p on p.Produto_Id = r.Codigo_Produto\n"
                    + "join dbo.Receita_Itens ri on ri.Codigo_Produto_Receita = r.Codigo_Produto\n"
                    + "join dbo.Produtos p2 on p2.Produto_Id = ri.Codigo_Produto \n"
                    + "order by r.Codigo_Produto"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportsistema(getSistema());
                    imp.setImportid(rst.getString("codigo_receita"));
                    imp.setIdproduto(rst.getString("id_produto") + rst.getString("digito_id"));
                    imp.setDescricao(rst.getString("descricao_receita"));
                    imp.setRendimento(rst.getDouble("qtd"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setQtdembalagemreceita(rst.getInt("qtd_item") == 0 ? 1 * 1000 : rst.getInt("qtd_item"));
                    imp.setFator(rst.getDouble("fator_item") == 0 ? 1 : rst.getDouble("fator_item"));
                    imp.setFichatecnica("");

                    imp.getProdutos().add(rst.getString("codigo_item") + rst.getString("digito_id_item"));
                    result.add(imp);
                }
            }
        }
        return result;
    }*/

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cast(rb.Codigo as bigint) as id,\n"
                    + "	rb.Descricao as descricao,\n"
                    + "	CONCAT(\n"
                    + "	rb.Linha01, ' ', rb.Linha02, ' ', rb.Linha03, ' ',\n"
                    + "	rb.Linha04, ' ', rb.Linha05, ' ', rb.Linha06, ' ',\n"
                    + "	rb.Linha07, ' ', rb.Linha08, ' ', rb.Linha09, ' ',\n"
                    + "	rb.Linha10, ' ' , rb.Linha11, ' ', rb.Linha12) as receita,\n"
                    + "	cast(p.Produto_Id as bigint) as id_produto,\n"
                    + " cast(p.Digito_Id as bigint) as digito_id,\n"
                    + "	p.Descricao_Completa as desricaoproduto\n"
                    + "from dbo.Ingrediente_Novo rb\n"
                    + "join dbo.Embalagem rbp on rbp.Ingredientes = rb.Codigo\n"
                    + "join dbo.Produtos p on p.Produto_Id = rbp.Produto_Id\n"
                    + "order by rb.Descricao"
            )) {
                Map<String, ReceitaBalancaIMP> receitas = new HashMap<>();
                while (rst.next()) {

                    ReceitaBalancaIMP imp = receitas.get(rst.getString("id"));

                    if (imp == null) {
                        imp = new ReceitaBalancaIMP();
                        imp.setId(rst.getString("id"));
                        imp.setDescricao(rst.getString("descricao"));
                        imp.setReceita(rst.getString("receita"));
                        receitas.put(imp.getId(), imp);
                    }

                    imp.getProdutos().add(rst.getString("id_produto") + rst.getString("digito_id"));
                }

                return new ArrayList<>(receitas.values());
            }
        }
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	cp.Codigo as id,\n"
                    + "	cp.NroDocumento as numerodocumento,\n"
                    + "	cp.Serie,\n"
                    + "	cp.Fornecedor as id_fornecedor,\n"
                    + "	cp.DataEmissao as dataemissao,\n"
                    + " cp.DataEntrada as dataentrada,\n"
                    + "	cp.Datavecto as vencimento,\n"
                    + "	cp.ValorDocumento as valor,\n"
                    + "	cp.Dataalteracao,\n"
                    + "	cp.observacao as observacao,\n"
                    + "	td.Anotacao,\n"
                    + "	tp.descricao,\n"
                    + "	cast(cp.Sequencial as bigint) as parcela,\n"
                    + "	cp.Serie,\n"
                    + "	cp.Juros,\n"
                    + " cp.BancoCobranca as idbanco\n"
                    + "from dbo.Cobranca cp\n"
                    + "left join dbo.Cobranca_TipoDocto td on td.Codigo = cp.TipoDocumento\n"
                    + "left join dbo.Tipos_Pagtos tp on tp.codigo = td.TipoPagto\n"
                    + "where cp.Loja = " + getLojaOrigem() + "\n"
                    //+ "and cp.Codigo not in (select codigocobranca from dbo.Cobranca_PAGTO where empresa = " + getLojaOrigem() + ")\n"
                    + "and cp.status = 1"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("Dataalteracao"));
                    imp.setObservacao(rst.getString("observacao"));

                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));
                    parc.setNumeroParcela(Utils.stringToInt(rst.getString("parcela"), 1));
                    parc.setId_banco(rst.getInt("idbanco"));
                    parc.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public void importarReceberDevolucaoPartir0108(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados Receber Devolução à partir 01/08...");
            vResult = getReceberDevolucaoPartir0108();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    List<ReceberDevolucaoVO> getReceberDevolucaoPartir0108() throws Exception {
        List<ReceberDevolucaoVO> result = new ArrayList<>();
        int idFornecedor;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "v.Situacao,\n"
                    + "r.Tipo,\n"
                    + "	cast(r.NumeroLancamento as bigint) as numero_lanc,\n"
                    + "	cast(r.NumeroNota as bigint) as numero_nf,\n"
                    + "	cast(f.Codigo as bigint) as id_fornecedor,\n"
                    + "	cast(r.Codigo_Destinatario as bigint) as id_fornecedor_nf,\n"
                    + "	r.nome as nome_fornecedor,\n"
                    + "	cast(f.CGC as bigint) as cnpj_cadastro,\n"
                    + "	cast(r.CGC as bigint) as cnpj_nf,\n"
                    + "	r.TotalDaNota as valor_nota,\n"
                    + "	r.DataEmissao as data_emissao,\n"
                    + " p.Vencimento as data_vencimento_v,\n"
                    + "	v.DataVencimento as data_vencimento,\n"
                    + "	v.DataVencimentoOri as data_vencimento_origem,\n"
                    + "	v.ValorBruto,\n"
                    + "	v.ValorBruto as valor_parcela,\n"
                    + "	p.Sequencia as numero_parcela,\n"
                    + "	r.OBS1 as obs_1,\n"
                    + "	r.OBS2 as obs_2,\n"
                    + "	r.OBS3 as obs_3,\n"
                    + "	r.OBS4 as obs_4,\n"
                    + "	r.OBS5 as obs_5\n"
                    + "from dbo.Notas_Capas_Emitidas r\n"
                    + "join dbo.Fornecedores f on f.Codigo = r.Codigo_Destinatario\n"
                    + "left join dbo.Notas_Emitidas_Vencimentos p \n"
                    + "	on p.NumeroLanc = r.NumeroLancamento\n"
                    + "left join dbo.Fin_Receber_Lancamentos v\n"
                    + "	on v.Pessoa = r.Codigo_Destinatario\n"
                    + "	and v.NroDocto = r.NumeroNota\n"
                    + "	and r.DataEmissao = v.DataReferencia\n"
                    + "where r.Tipo in (7, 8, 46, 47, 48, 49)\n"
                    + "and r.NumeroLoja = " + getLojaOrigem() + "\n"
                    + "and coalesce(v.DataVencimento, p.Vencimento) >= '2020-08-01'\n"
                    + "order by r.CGC"
            )) {
                while (rst.next()) {
                    ReceberDevolucaoVO vo = new ReceberDevolucaoVO();

                    if ((rst.getString("cnpj_cadastro") != null)
                            && (!rst.getString("cnpj_cadastro").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cnpj_cadastro"))));

                        if (idFornecedor != -1) {
                            ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                            imp.setIdFornecedor(idFornecedor);
                            imp.setNumeroNota(rst.getInt("numero_nf"));
                            imp.setDataemissao(rst.getDate("data_emissao"));
                            imp.setDatavencimento(rst.getDate("data_vencimento") == null ? rst.getDate("data_vencimento_v") : rst.getDate("data_vencimento"));
                            imp.setValor(rst.getDouble("valor_parcela") == 0 ? rst.getDouble("valor_nota") : rst.getDouble("valor_parcela"));
                            imp.setNumeroParcela(rst.getInt("numero_parcela"));
                            imp.setObservacao("IMPORTADO VR " + " Tipo Conta "
                                    + rst.getString("tipo") + " - "
                                    + " " + rst.getString("obs_1")
                                    + " " + rst.getString("obs_2")
                                    + " " + rst.getString("obs_3")
                                    + " " + rst.getString("obs_4")
                                    + " " + rst.getString("obs_5"));
                            result.add(imp);
                        } else {
                            System.out.println("Fornecedor " + rst.getString("cnpj_cadastro")
                                    + " id " + rst.getString("id_fornecedor")
                                    + " nome " + rst.getString("nome_fornecedor"));
                        }
                    }
                }
            }
        }
        return result;
    }

    public void importarReceberDevolucaoAnterior0108(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados Receber Devolução Anterior 01/08...");
            vResult = getReceberDevolucaoAnterior0108();
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    List<ReceberDevolucaoVO> getReceberDevolucaoAnterior0108() throws Exception {
        List<ReceberDevolucaoVO> result = new ArrayList<>();
        int idFornecedor;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "v.Situacao,\n"
                    + "r.Tipo,\n"
                    + "	cast(r.NumeroLancamento as bigint) as numero_lanc,\n"
                    + "	cast(r.NumeroNota as bigint) as numero_nf,\n"
                    + "	cast(f.Codigo as bigint) as id_fornecedor,\n"
                    + "	cast(r.Codigo_Destinatario as bigint) as id_fornecedor_nf,\n"
                    + "	r.nome as nome_fornecedor,\n"
                    + "	cast(f.CGC as bigint) as cnpj_cadastro,\n"
                    + "	cast(r.CGC as bigint) as cnpj_nf,\n"
                    + "	r.TotalDaNota as valor_nota,\n"
                    + "	r.DataEmissao as data_emissao,\n"
                    + " p.Vencimento as data_vencimento_v,\n"
                    + "	v.DataVencimento as data_vencimento,\n"
                    + "	v.DataVencimentoOri as data_vencimento_origem,\n"
                    + "	v.ValorBruto,\n"
                    + "	v.ValorBruto as valor_parcela,\n"
                    + "	p.Sequencia as numero_parcela,\n"
                    + "	r.OBS1 as obs_1,\n"
                    + "	r.OBS2 as obs_2,\n"
                    + "	r.OBS3 as obs_3,\n"
                    + "	r.OBS4 as obs_4,\n"
                    + "	r.OBS5 as obs_5\n"
                    + "from dbo.Notas_Capas_Emitidas r\n"
                    + "join dbo.Fornecedores f on f.Codigo = r.Codigo_Destinatario\n"
                    + "left join dbo.Notas_Emitidas_Vencimentos p \n"
                    + "	on p.NumeroLanc = r.NumeroLancamento\n"
                    + "left join dbo.Fin_Receber_Lancamentos v\n"
                    + "	on v.Pessoa = r.Codigo_Destinatario\n"
                    + "	and v.NroDocto = r.NumeroNota\n"
                    + "	and r.DataEmissao = v.DataReferencia\n"
                    + "where r.Tipo in (7, 8, 46, 47, 48, 49)\n"
                    + "and r.NumeroLoja = " + getLojaOrigem() + "\n"
                    + "and coalesce(v.DataVencimento, p.Vencimento) < '2020-08-01'\n"
                    + "and coalesce(v.Situacao, 0) in (0, 1)\n"
                    + "order by r.CGC"
            )) {
                while (rst.next()) {
                    ReceberDevolucaoVO vo = new ReceberDevolucaoVO();

                    if ((rst.getString("cnpj_cadastro") != null)
                            && (!rst.getString("cnpj_cadastro").trim().isEmpty())) {

                        idFornecedor = new FornecedorDAO().getIdByCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cnpj_cadastro"))));

                        if (idFornecedor != -1) {
                            ReceberDevolucaoVO imp = new ReceberDevolucaoVO();
                            imp.setIdFornecedor(idFornecedor);
                            imp.setNumeroNota(rst.getInt("numero_nf"));
                            imp.setDataemissao(rst.getDate("data_emissao"));
                            imp.setDatavencimento(rst.getDate("data_vencimento") == null ? rst.getDate("data_vencimento_v") : rst.getDate("data_vencimento"));
                            imp.setValor(rst.getDouble("valor_parcela") == 0 ? rst.getDouble("valor_nota") : rst.getDouble("valor_parcela"));
                            imp.setNumeroParcela(rst.getInt("numero_parcela"));
                            imp.setObservacao("IMPORTADO VR " + " Tipo Conta "
                                    + rst.getString("tipo") + " - "
                                    + " " + rst.getString("obs_1")
                                    + " " + rst.getString("obs_2")
                                    + " " + rst.getString("obs_3")
                                    + " " + rst.getString("obs_4")
                                    + " " + rst.getString("obs_5"));
                            result.add(imp);
                        } else {
                            System.out.println("Fornecedor " + rst.getString("cnpj_cadastro")
                                    + " id " + rst.getString("id_fornecedor")
                                    + " nome " + rst.getString("nome_fornecedor"));
                        }
                    }
                }
            }
        }
        return result;
    }

    public void gravarIdReceberDevolucaoDuplicado(int idLojaVR) throws Exception {
        List<ReceberDevolucaoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados Id Devolução Duplicado...");
            vResult = getDevolucaoFornecedorDuplicados(idLojaVR);
            if (!vResult.isEmpty()) {
                new ReceberDevolucaoDAO().gravarIdReceberDevolucaoDuplicado(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<ReceberDevolucaoVO> getDevolucaoFornecedorDuplicados(int idLoja) throws Exception {
        List<ReceberDevolucaoVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	r.id_fornecedor, \n"
                    + "	r.numeronota, \n"
                    + "	r.dataemissao,\n"
                    + "	r.datavencimento,\n"
                    + "	r.valor,\n"
                    + "	count(*) qtd \n"
                    + "from receberdevolucao r\n"
                    + "where r.id_loja = " + idLoja + "\n"
                    + "group by r.id_fornecedor, r.numeronota, r.dataemissao, r.datavencimento, r.valor\n"
                    + "having count(*) > 1\n"
                    + "order by r.numeronota"
            )) {
                while (rst.next()) {
                    ReceberDevolucaoVO vo = new ReceberDevolucaoVO();
                    vo.setIdLoja(idLoja);
                    vo.setIdFornecedor(rst.getInt("id_fornecedor"));
                    vo.setNumeroNota(rst.getInt("numeronota"));
                    vo.setDataemissao(rst.getDate("dataemissao"));
                    vo.setDatavencimento(rst.getDate("datavencimento"));
                    vo.setValor(rst.getDouble("valor"));
                    
                    System.out.println("Forn " + vo.getIdFornecedor() + " "
                            + "Nota " + vo.getNumeroNota() + " "
                            + "Emissao " + vo.getDataemissao() + " "
                            + "Vencimento " + vo.getDatavencimento() + " "
                            + "Valor " + vo.getValor());
                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void gravarIdPagarFornecedorDuplicado(int idLojaVR) throws Exception {
        List<PagarFornecedorVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados Id Pagar Fornecedor Duplicado...");
            vResult = getPagarFornecedorDuplicado(idLojaVR);
            if (!vResult.isEmpty()) {
                new PagarFornecedorDAO().gravarIdPagarFornecedorDuplicado(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<PagarFornecedorVO> getPagarFornecedorDuplicado(int idLoja) throws Exception {
        List<PagarFornecedorVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.id_fornecedor, \n"
                    + "	p.numerodocumento,\n"
                    + "	p.dataentrada, \n"
                    + "	p.dataemissao,\n"
                    + "	i.datavencimento,\n"
                    + "	p.valor,\n"
                    + "	count(*) qtd \n"
                    + "	from pagarfornecedor p\n"
                    + "	inner join pagarfornecedorparcela i\n"
                    + "		on i.id_pagarfornecedor = p.id\n"
                    + "where p.id_loja = " + idLoja + "\n"
                    + "group by p.id_fornecedor, p.numerodocumento, p.dataentrada, p.dataemissao, i.datavencimento, p.valor\n"
                    + "having count(*) > 1 \n"
                    + "order by numerodocumento"
            )) {
                while (rst.next()) {
                    PagarFornecedorVO vo = new PagarFornecedorVO();
                    vo.setId_loja(idLoja);
                    vo.setId_fornecedor(rst.getInt("id_fornecedor"));
                    vo.setNumerodocumento(rst.getInt("numerodocumento"));
                    vo.setDataentrada(rst.getDate("dataentrada"));
                    vo.setDataemissao(rst.getDate("dataemissao"));
                    vo.setDatavencimento(rst.getDate("datavencimento"));
                    vo.setValor(rst.getDouble("valor"));
                    result.add(vo);
                }
            }
        }
        return result;
    }
    
    public void importarReceberVendaPrazoPartir0108(int idLojaVR) throws Exception {
        List<ReceberVendaPrazoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados Receber Venda Prazo à partir 01/08...");
            vResult = getReceberVendaPrazaoPartir0108();
            if (!vResult.isEmpty()) {
                new ReceberVendaPrazaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberVendaPrazoVO> getReceberVendaPrazaoPartir0108() throws Exception {
        List<ReceberVendaPrazoVO> result = new ArrayList<>();
        int idClienteEventual;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "v.Situacao,\n"
                    + "r.Tipo,\n"
                    + "	cast(r.NumeroLancamento as bigint) as numero_lanc,\n"
                    + "	cast(r.NumeroNota as bigint) as numero_nf,\n"
                    + "	cast(c.Codigo as bigint) as id_cliente,\n"
                    + "	cast(r.Codigo_Destinatario as bigint) as id_cliente_nf,\n"
                    + "	r.nome as nome_cliente,\n"
                    + "	cast(c.CPF as bigint) as cnpj_cadastro,\n"
                    + "	cast(r.CGC as bigint) as cnpj_nf,\n"
                    + "	r.TotalDaNota as valor_nota,\n"
                    + "	r.DataEmissao as data_emissao,\n"
                    + " p.Vencimento as data_vencimento_v,\n"
                    + "	v.DataVencimento as data_vencimento,\n"
                    + "	v.DataVencimentoOri as data_vencimento_origem,\n"
                    + "	v.ValorBruto,\n"
                    + "	v.ValorBruto as valor_parcela,\n"
                    + "	p.Sequencia as numero_parcela,\n"
                    + "	r.OBS1 as obs_1,\n"
                    + "	r.OBS2 as obs_2,\n"
                    + "	r.OBS3 as obs_3,\n"
                    + "	r.OBS4 as obs_4,\n"
                    + "	r.OBS5 as obs_5\n"
                    + "from dbo.Notas_Capas_Emitidas r\n"
                    + "join dbo.clientes c on c.Codigo = r.Codigo_Destinatario\n"
                    + "left join dbo.Notas_Emitidas_Vencimentos p \n"
                    + "	on p.NumeroLanc = r.NumeroLancamento\n"
                    + "left join dbo.Fin_Receber_Lancamentos v\n"
                    + "	on v.Pessoa = r.Codigo_Destinatario\n"
                    + "	and v.NroDocto = r.NumeroNota\n"
                    + "	and r.DataEmissao = v.DataReferencia\n"
                    + "where r.Tipo in (6, 42, 50)\n"
                    + "and r.NumeroLoja = " + getLojaOrigem() + "\n"
                    + "and coalesce(v.DataVencimento, p.Vencimento) >= '2020-08-01'\n"
            )) {
                int cont = 0;
                while (rst.next()) {
                    idClienteEventual = new ClienteEventuallDAO().getIdByCnpj(rst.getLong("cnpj_cadastro"));
                    //idClienteEventual = new ClienteEventuallDAO().getIdByCodAnt(getSistema(), getLojaOrigem(), rst.getString("id_cliente"));
                    if (idClienteEventual != -1) {
                        ReceberVendaPrazoVO vo = new ReceberVendaPrazoVO();
                        vo.setId_clienteeventual(idClienteEventual);
                        vo.setNumeronota(rst.getInt("numero_nf"));
                        vo.setDataemissao(rst.getDate("data_emissao"));
                        vo.setDatavencimento(rst.getDate("data_vencimento") == null ? rst.getDate("data_vencimento_v") : rst.getDate("data_vencimento"));
                        vo.setValor(rst.getDouble("valor_parcela") == 0 ? rst.getDouble("valor_nota") : rst.getDouble("valor_parcela"));
                        vo.setValorliquido(vo.getValor());
                        vo.setNumeroparcela(rst.getInt("numero_parcela"));
                        vo.setObservacao("IMPORTADO VR " + " Tipo Conta "
                                + rst.getString("tipo") + " - "
                                + " " + rst.getString("obs_1")
                                + " " + rst.getString("obs_2")
                                + " " + rst.getString("obs_3")
                                + " " + rst.getString("obs_4")
                                + " " + rst.getString("obs_5"));
                        result.add(vo);

                    } else {
                        System.out.println("Loja " + getLojaOrigem() + " Cliente " + rst.getString("cnpj_cadastro")
                                + " id " + rst.getString("id_cliente")
                                + " nome " + rst.getString("nome_cliente"));
                    }
                    cont++;
                    System.out.println(cont);
                }
            }
        }
        return result;
    }

    public void importarReceberVendaPrazoAnterior0108(int idLojaVR) throws Exception {
        List<ReceberVendaPrazoVO> vResult;
        try {
            ProgressBar.setStatus("Carregando dados Receber Venda Prazo Anterior 01/08...");
            vResult = getReceberVendaPrazaoAnterior0108();
            if (!vResult.isEmpty()) {
                new ReceberVendaPrazaoDAO().salvar(vResult, idLojaVR);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public List<ReceberVendaPrazoVO> getReceberVendaPrazaoAnterior0108() throws Exception {
        List<ReceberVendaPrazoVO> result = new ArrayList<>();
        int idClienteEventual;

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "v.Situacao,\n"
                    + "r.Tipo,\n"
                    + "	cast(r.NumeroLancamento as bigint) as numero_lanc,\n"
                    + "	cast(r.NumeroNota as bigint) as numero_nf,\n"
                    + "	cast(c.Codigo as bigint) as id_cliente,\n"
                    + "	cast(r.Codigo_Destinatario as bigint) as id_cliente_nf,\n"
                    + "	r.nome as nome_cliente,\n"
                    + "	cast(c.CPF as bigint) as cnpj_cadastro,\n"
                    + "	cast(r.CGC as bigint) as cnpj_nf,\n"
                    + "	r.TotalDaNota as valor_nota,\n"
                    + "	r.DataEmissao as data_emissao,\n"
                    + " p.Vencimento as data_vencimento_v,\n"
                    + "	v.DataVencimento as data_vencimento,\n"
                    + "	v.DataVencimentoOri as data_vencimento_origem,\n"
                    + "	v.ValorBruto,\n"
                    + "	v.ValorBruto as valor_parcela,\n"
                    + "	p.Sequencia as numero_parcela,\n"
                    + "	r.OBS1 as obs_1,\n"
                    + "	r.OBS2 as obs_2,\n"
                    + "	r.OBS3 as obs_3,\n"
                    + "	r.OBS4 as obs_4,\n"
                    + "	r.OBS5 as obs_5\n"
                    + "from dbo.Notas_Capas_Emitidas r\n"
                    + "join dbo.clientes c on c.Codigo = r.Codigo_Destinatario\n"
                    + "left join dbo.Notas_Emitidas_Vencimentos p \n"
                    + "	on p.NumeroLanc = r.NumeroLancamento\n"
                    + "left join dbo.Fin_Receber_Lancamentos v\n"
                    + "	on v.Pessoa = r.Codigo_Destinatario\n"
                    + "	and v.NroDocto = r.NumeroNota\n"
                    + "	and r.DataEmissao = v.DataReferencia\n"
                    + "where r.Tipo in (6, 42, 50)\n"
                    + "and r.NumeroLoja = " + getLojaOrigem() + "\n"
                    + "and coalesce(v.DataVencimento, p.Vencimento) < '2020-08-01'\n"
                    + "and coalesce(v.Situacao, 0) in (0, 1)\n"
            )) {
                int cont = 0;
                while (rst.next()) {
                    idClienteEventual = new ClienteEventuallDAO().getIdByCnpj(rst.getLong("cnpj_cadastro"));
                    //idClienteEventual = new ClienteEventuallDAO().getIdByCodAnt(getSistema(), getLojaOrigem(), rst.getString("id_cliente"));
                    if (idClienteEventual != -1) {
                        ReceberVendaPrazoVO vo = new ReceberVendaPrazoVO();
                        vo.setId_clienteeventual(idClienteEventual);
                        vo.setNumeronota(rst.getInt("numero_nf"));
                        vo.setDataemissao(rst.getDate("data_emissao"));
                        vo.setDatavencimento(rst.getDate("data_vencimento") == null ? rst.getDate("data_vencimento_v") : rst.getDate("data_vencimento"));
                        vo.setValor(rst.getDouble("valor_parcela") == 0 ? rst.getDouble("valor_nota") : rst.getDouble("valor_parcela"));
                        vo.setValorliquido(vo.getValor());
                        vo.setNumeroparcela(rst.getInt("numero_parcela"));
                        vo.setObservacao("IMPORTADO VR " + " Tipo Conta "
                                + rst.getString("tipo") + " - "
                                + " " + rst.getString("obs_1")
                                + " " + rst.getString("obs_2")
                                + " " + rst.getString("obs_3")
                                + " " + rst.getString("obs_4")
                                + " " + rst.getString("obs_5"));
                        result.add(vo);

                    } else {
                        System.out.println("Loja " + getLojaOrigem() + " Cliente " + rst.getString("cnpj_cadastro")
                                + " id " + rst.getString("id_cliente")
                                + " nome " + rst.getString("nome_cliente"));
                    }
                    cont++;
                    System.out.println(cont);
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
                    "select\n"
                    + "	cast(p.Produto_Id as bigint) as id_produto,\n"
                    + "	cast(p.Digito_Id as bigint) as digito_produto, \n"
                    + "	o.Data_Inicio as datainicio,\n"
                    + "	o.Data_Fim as datatermino,\n"
                    + "	o.Preco_Promocao as precooferta\n"
                    + "from dbo.promocoes o\n"
                    + "join dbo.Produtos p on p.Produto_Id = o.Produto_Id\n"
                    + "where o.loja = 0 \n"
                    + "and o.Data_Fim >= '2020-08-04'\n"
                    + "union all\n"
                    + "select\n"
                    + "	cast(p.Produto_Id as bigint) as id_produto,\n"
                    + "	cast(p.Digito_Id as bigint) as digito_produto, \n"
                    + "	o.Data_Inicio as datainicio,\n"
                    + "	o.Data_Fim as datatermino,\n"
                    + "	o.Preco_Promocao as precooferta\n"
                    + "from dbo.promocoes o\n"
                    + "join dbo.Produtos p on p.Produto_Id = o.Produto_Id\n"
                    + "where o.loja = " + getLojaOrigem() + " \n"
                    + "and o.Data_Fim >= '2020-08-04'"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("id_produto") + rst.getString("digito_produto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datatermino"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

                    result.add(imp);

                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;
    
    public String dataInicioPdvVenda;
    public String dataTerminoPdvVenda;

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

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();
        private SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
        private SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("idloja") + rst.getString("pdv") + rst.getString("numerocupom") + timestampDate.format(rst.getDate("data"));
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("pdv")));
                        next.setData(rst.getDate("data"));

                        if ((rst.getString("cpf_cliente") != null)
                                && (!rst.getString("cpf_cliente").trim().isEmpty())) {

                            next.setCpf(rst.getString("cpf_cliente").length() > 14 ? rst.getString("cpf_cliente").substring(0, 14) : rst.getString("cpf_cliente"));
                        }

                        next.setIdClientePreferencial(rst.getString("id_cliente"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));

                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setChaveCfe(rst.getString("ChaveCfe"));
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
                    + "	cast(v.LOJA as bigint) as idloja,\n"
                    + "	convert(varchar, v.DATA, 23) as data,\n"
                    + "	convert(varchar, v.HORA_INICIO, 108) as horainicio,\n"
                    + "	convert(varchar, v.HORA_FIM, 108) as horatermino,\n"
                    + "	cast(v.NUM_PDV as bigint) as pdv,\n"
                    + "	cast(v.NUM_CUPOM as bigint) as numerocupom,\n"
                    + "	v.VENDA_BRUTA as subtotalimpressora,\n"
                    + "	v.DESCONTOS as desconto,\n"
                    + "	v.CANCELAMENTOS,\n"
                    + "	v.RECEBIMENTOS,\n"
                    + "	v.TROCO,\n"
                    + " v.ACRESCIMO as acrescimo,\n"
                    + "	v.CLIENTE id_cliente,\n"
                    + "	v.CPFCNPJCLIENTE as cpf_cliente,\n"
                    + "	cast(v.CANCELADO as bigint) as cancelado,\n"
                    + "	nfe.CHAVE as chaveCfe,\n"
                    + "	nfe.CHAVE as chaveNfce,\n"
                    + "	nfe.NUMEROSERIESAT as numeroserie\n"
                    + "from dbo.Sint_total_Cupom v\n"
                    + "left join dbo.SINT_NFCE nfe on \n"
                    + "	nfe.DATA = v.DATA and \n"
                    + "	nfe.NUM_PDV = v.NUM_PDV and \n"
                    + "	nfe.NUM_CUPOM = v.NUM_CUPOM and\n"
                    + "	nfe.LOJA = v.LOJA\n"
                    + "where v.loja = " + idLojaCliente + "\n"
                    + "and v.data between convert(datetime, '" + FORMAT.format(dataInicio) + "', 103) and "
                    + "convert(datetime,'" + FORMAT.format(dataTermino) + "', 103) "
                    + " and nfe.CHAVE in (\n"
                    + "'35200860186376000164590000161125078202719711'\n"
                    + ", '35200860186376000164590000161125078217618797'\n"
                    + ", '35200860186376000164590000161125078227178722'\n"
                    + ", '35200860186376000164590000161125078239778231'\n"
                    + ", '35200860186376000164590000161125078249246836'\n"
                    + ", '35200860186376000164590000161125078251749092'\n"
                    + ", '35200860186376000164590000161125078268543450'\n"
                    + ", '35200860186376000164590000161125078277362640'\n"
                    + ", '35200860186376000164590000161125078289659686'\n"
                    + ", '35200860186376000164590000161125078290324951'\n"
                    + ", '35200860186376000164590000161125078307984920'\n"
                    + ", '35200860186376000164590000161125078311014999'\n"
                    + ", '35200860186376000164590000161125078322740726'\n"
                    + ", '35200860186376000164590000161125078338719349'\n"
                    + ", '35200860186376000164590004040501259763905780'\n"
                    + ", '35200860186376000164590004040501259776116283'\n"
                    + ", '35200860186376000164590004040501259783911352'\n"
                    + ", '35200860186376000164590004040501259797081780'\n"
                    + ", '35200860186376000164590004040501259808128281'\n"
                    + ", '35200860186376000164590004040501259812811853'\n"
                    + ", '35200860186376000164590004040501259820961214')";

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

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;
        private SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("idloja") + rst.getString("pdv") + rst.getString("numerocupom") + timestampDate.format(rst.getDate("data"));
                        String id = rst.getString("idloja") + rst.getString("pdv") + rst.getString("numerocupom") + timestampDate.format(rst.getDate("data"))
                                + rst.getString("idproduto") + rst.getString("digitoproduto")
                                + rst.getString("sequencia") /*+ rst.getString("Quantidade")
                                + rst.getString("Preco_Total")
                                + rst.getString("cancelado")*/;

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto") + rst.getString("digitoproduto"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        next.setQuantidade(rst.getDouble("Quantidade"));
                        next.setTotalBruto(rst.getDouble("Preco_Total"));
                        next.setValorDesconto(rst.getDouble("Desconto"));
                        next.setPrecoVenda(rst.getDouble("Preco_Venda"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codautomacao") + rst.getString("digitoautomacao"));
                        next.setUnidadeMedida(rst.getString("tipoembalagem"));

                        String trib = Utils.acertarTexto(rst.getString("trib"));
                        if (trib == null || "".equals(trib)) {
                            trib = Utils.acertarTexto(rst.getString("trib"));
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
             T04   7.00    ALIQUOTA 07%
             T03   12.00   ALIQUOTA 12%
             T01   18.00   ALIQUOTA 18%
             T02   25.00   ALIQUOTA 25%
             T05   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "T01":
                    cst = 0;
                    aliq = 18;
                    break;
                case "T02":
                    cst = 0;
                    aliq = 25;
                    break;
                case "T03":
                    cst = 0;
                    aliq = 12;
                    break;
                case "T04":
                    cst = 0;
                    aliq = 7;
                    break;
                case "T05":
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
                    = "select \n"
                    + " i.Loja as idloja,\n"
                    + " convert(varchar, i.DATA, 23) as data,\n"
                    + " cast(i.Num_Pdv as bigint) as pdv,\n"
                    + " cast(i.Num_Cupom as bigint) as numerocupom,\n"
                    + " cast(i.Sequencial as bigint) as sequencia,\n"
                    + " cast(p.Produto_Id as bigint) as idproduto,\n"
                    + " cast(p.Digito_Id as bigint) as digitoproduto,\n"
                    + " p.Descricao_Reduzida as descricaoreduzida,\n"
                    + " cast(i.Cod_Automacao as bigint) as codautomacao,\n"
                    + " cast(Dig_Automacao as bigint) as digitoautomacao,\n"
                    + " p.EspecUnitariaTipo as tipoembalagem,\n"
                    + " i.Preco_Venda,\n"
                    + " i.Quantidade,\n"
                    + " i.Preco_Total,\n"
                    + " i.Desconto,\n"
                    + " i.Desconto_Item,\n"
                    + " i.Cancelado,\n"
                    + " i.Aliquota,\n"
                    + " a.Identificador as trib,\n"
                    + " a.Descricao as tribdesc,\n"
                    + " a.Percentual,\n"
                    + " a.Tipo\n"
                    + "from dbo.Sint_item_venda i \n"
                    + "join dbo.Produtos p on p.Produto_Id = i.Cod_Interno\n"
                    + "join dbo.Aliquotas a on a.Codigo = i.Aliquota\n"
                    + "join dbo.Sint_total_Cupom v "
                    + "      on v.loja = i.loja"
                    + "     and cast(v.NUM_CUPOM as bigint) = cast(i.Num_Cupom as bigint) \n"
                    + "     and cast(v.NUM_PDV as bigint) = cast(i.Num_Pdv as bigint) \n"
                    + "     and convert(varchar, v.DATA, 23) = convert(varchar, i.DATA, 23) \n"
                    + "join dbo.SINT_NFCE nfe on \n"
                    + "	nfe.DATA = v.DATA and \n"
                    + "	nfe.NUM_PDV = v.NUM_PDV and \n"
                    + "	nfe.NUM_CUPOM = v.NUM_CUPOM and\n"
                    + "	nfe.LOJA = v.LOJA\n"                    
                    + "where i.Loja = " + idLojaCliente + "\n"
                    + "and i.data between convert(datetime, '" + FORMAT.format(dataInicio) + "', 103)\n"
                    + "and convert(datetime,'" + FORMAT.format(dataTermino) + "', 103) \n"
                    + " and nfe.CHAVE in (\n"
                    + "'35200860186376000164590000161125078202719711'\n"
                    + ", '35200860186376000164590000161125078217618797'\n"
                    + ", '35200860186376000164590000161125078227178722'\n"
                    + ", '35200860186376000164590000161125078239778231'\n"
                    + ", '35200860186376000164590000161125078249246836'\n"
                    + ", '35200860186376000164590000161125078251749092'\n"
                    + ", '35200860186376000164590000161125078268543450'\n"
                    + ", '35200860186376000164590000161125078277362640'\n"
                    + ", '35200860186376000164590000161125078289659686'\n"
                    + ", '35200860186376000164590000161125078290324951'\n"
                    + ", '35200860186376000164590000161125078307984920'\n"
                    + ", '35200860186376000164590000161125078311014999'\n"
                    + ", '35200860186376000164590000161125078322740726'\n"
                    + ", '35200860186376000164590000161125078338719349'\n"
                    + ", '35200860186376000164590004040501259763905780'\n"
                    + ", '35200860186376000164590004040501259776116283'\n"
                    + ", '35200860186376000164590004040501259783911352'\n"
                    + ", '35200860186376000164590004040501259797081780'\n"
                    + ", '35200860186376000164590004040501259808128281'\n"
                    + ", '35200860186376000164590004040501259812811853'\n"
                    + ", '35200860186376000164590004040501259820961214')";


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
    
    public List<PdvVendaVO> carregarVendasChaveCfe() throws Exception {
        List<PdvVendaVO> result = new ArrayList<>();
        SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + " 	v.DATA as datavenda, \n"
                    + " 	cast(v.NUM_PDV as bigint) as ecf, \n"
                    + " 	cast(v.NUM_CUPOM as bigint) as numerocupom, \n"
                    + "         cast(nfe.NUMERONOTA as bigint) as numeronota, \n"
                    + "         v.CPFCNPJCLIENTE as cpf_cliente, \n"        
                    + " 	nfe.CHAVE as chaveCfe\n"
                    + " from dbo.Sint_total_Cupom v \n"
                    + " join dbo.SINT_NFCE nfe on  \n"
                    + " 	nfe.DATA = v.DATA and  \n"
                    + " 	nfe.NUM_PDV = v.NUM_PDV and  \n"
                    + " 	nfe.NUM_CUPOM = v.NUM_CUPOM and \n"
                    + " 	nfe.LOJA = v.LOJA \n"
                    + " where v.loja = " + getLojaOrigem() + "\n"
                    + " and v.data between '" + dataInicioPdvVenda + "' and '" + dataTerminoPdvVenda + "'\n"
            )) {
                int cont = 0;
                while (rst.next()) {
                    PdvVendaVO vo = new PdvVendaVO();
                    vo.setData(rst.getDate("datavenda"));
                    vo.setEcf(rst.getInt("ecf"));
                    vo.setNumeroCupom(rst.getInt("numerocupom"));
                    vo.setNumeronota(rst.getInt("numeronota"));
                    vo.setChaveCfe(rst.getString("chavecfe"));
                    vo.setCpf(rst.getLong("cpf_cliente"));
                    result.add(vo);

                    ProgressBar.setStatus("Carregando vendas loja " + getLojaOrigem() + "..." + cont++);
                }
            }
        }
        return result;
    }
    
    public void importarChaveCfe(int idLoja) throws Exception {
        List<PdvVendaVO> result = new ArrayList<>();
        try {
            result = carregarVendasChaveCfe();
            
            if (!result.isEmpty()) {
                gravarChaveCfe(result, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void gravarChaveCfe(List<PdvVendaVO> vo, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        String sql = "";
        
        try {
            Conexao.begin();
            
            ProgressBar.setMaximum(vo.size());
            ProgressBar.setStatus("Acertando Cpf...");
            
            stm = Conexao.createStatement();
            
            for (PdvVendaVO i_vo : vo) {
                sql = /*"update pdv.venda set "
                        + "cpf = " + i_vo.getCpf() + " "
                        + "where data = '" + i_vo.getData() + "' "
                        + "and ecf = " + i_vo.getEcf() + " "
                        //+ "and chavecfe != '' "
                        + "and chavecfe = '"+i_vo.getChaveCfe()+"' "
                        + "and id_loja = " + idLoja + ";";
                        /*+ "\n\n"*/
                        "update escrita set "
                        + "numeronota = " + i_vo.getNumeronota() + ", "
                        + "cpfadquirente = " + i_vo.getCpf() + " "
                        + "where data = '" + i_vo.getData() + "' "
                        //+ "and ecf = " + i_vo.getEcf() + " "
                        + "and chavecfe != '' "
                        + "and chavecfe = '"+i_vo.getChaveCfe()+"' "
                        //+ "and especie = 'CFE' "
                        //+ "and modelo = '59' "
                        + "and id_loja = " + idLoja + ";";
                        
                /*sql = "update escrita set "
                        + "chavecfe = '"+ i_vo.getChaveCfe()+"', "
                        + "especie = 'CFE', "
                        + "modelo = '59' "
                        + "where data = '" + i_vo.getData() + "' "
                        + "and ecf = " + i_vo.getEcf() + " "
                        + "and numeronota = " + i_vo.getNumeroCupom() + " "
                        + "and chavecfe = '' "
                        + "and especie = 'ECF' "
                        + "and modelo = '2D' "
                        + "and id_loja = " + idLoja + "; \n";*/
                
                stm.execute(sql);
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
            
        } catch (Exception ex) {            
            Conexao.rollback();
            throw ex;
        }
    }
    
    public List<LogEstoqueVO> getProdutosNotaEntradaLogEstoque(int idLojaVR) throws Exception {
        List<LogEstoqueVO> result = new ArrayList<>();
        int linha, numeronota, idfornecedor;

        try {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            Workbook arquivo = Workbook.getWorkbook(new File(i_arquivo), settings);
            Sheet[] sheets = arquivo.getSheets();

            linha = 0;

            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                linha = 0;

                for (int i = 0; i < sheet.getRows(); i++) {
                    linha++;

                    if (linha == 1) {
                        continue;
                    }

                    Cell cellNF = sheet.getCell(0, i);
                    Cell cellForncedor = sheet.getCell(1, i);

                    numeronota = Integer.parseInt(cellNF.getContents().trim());
                    idfornecedor = Integer.parseInt(cellForncedor.getContents().trim());
                    
                    System.out.println(
                                "select "
                                + "id_produto, "
                                + "quantidade "
                                + "from logestoque "
                                + "where id_loja = " + idLojaVR + " "
                                + "and datahora::date >= '" + dataVirada + "' "
                                + "and observacao like '%NOTA: ' ||" + numeronota + "||', FORNECEDOR: '||" + idfornecedor + "||'%' "                    
                    );
                    
                    try (Statement stm = Conexao.createStatement()) {
                        try (ResultSet rst = stm.executeQuery(
                                "select "
                                + "id_produto, "
                                + "quantidade "
                                + "from logestoque "
                                + "where id_loja = " + idLojaVR + " "
                                + "and datahora::date >= '" + dataVirada + "' "
                                + "and observacao like '%NOTA: ' ||" + numeronota + "||', FORNECEDOR: '||" + idfornecedor + "||'%' "
                        )) {
                            while (rst.next()) {
                                LogEstoqueVO vo = new LogEstoqueVO();
                                vo.setId_produto(rst.getInt("id_produto"));
                                vo.setQuantidade(rst.getDouble("quantidade"));
                                result.add(vo);
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutosNotaEntradaLogEstoque(int idLoja) throws Exception {
        List<LogEstoqueVO> result = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando quantidade das notas de entrada...");
            result = getProdutosNotaEntradaLogEstoque(idLoja);
            
            if (!result.isEmpty()) {
                gravarProdutosNotaEntrada(result, idLoja);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void gravarProdutosNotaEntrada(List<LogEstoqueVO> vo, int idLoja) throws Exception {
        
        Statement stm = null;
        String sql;
        
        try {
            Conexao.begin();
            
            ProgressBar.setStatus("Gravando quantidade dos produtos das notas de entrada...");
            ProgressBar.setMaximum(vo.size());
            
            stm = Conexao.createStatement();
            
            sql = "delete from implantacao.notaentradaitem_quantidade where id_loja = " + idLoja + ";\n"
                    + "delete from implantacao.acertoestoque where id_loja = " + idLoja + ";";
            
            stm.execute(sql);
            
            for (LogEstoqueVO i_vo : vo) {
                
                sql = "insert into implantacao.notaentradaitem_quantidade("
                        + "id_loja, "
                        + "id_produto, "
                        + "quantidade) "
                        + "values ("
                        + idLoja + ", "
                        + i_vo.getId_produto() + ", "
                        + i_vo.getQuantidade() + ");";
                
                stm.execute(sql);
                
                ProgressBar.next();
            }
            
            sql = "insert into implantacao.acertoestoque(select "+idLoja+", id_produto, coalesce(sum(quantidade), 0) "
                    + "from implantacao.notaentradaitem_quantidade where id_loja = "+idLoja+" group by id_produto);";
            
            
            
            stm.execute(sql);
            
            stm.close();            
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}
