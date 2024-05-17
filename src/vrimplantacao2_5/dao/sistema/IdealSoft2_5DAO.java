package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class IdealSoft2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    private String estado = null;
    private String operacao = "7";

    @Override
    public String getSistema() {
        return "IDEALSOFT";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.PDV_VENDA // Habilita importacão de Vendas
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        operacao = exibirMensagemComComboBox("Escolha a Operação", false);
        estado = exibirMensagemComComboBox("Escolha o estado", true);
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	i.Ordem id,\n"
                    + "	i.Nome descricao,\n"
                    + "	cioS.ICMS_CST_CSOSN cst,\n"
                    + "	cioS.ICMS_Percentual_Norm icms,\n"
                    + "	cioS.ICMS_Valor_Base_Subs_Reduzida reducao\n"
                    + "from\n"
                    + "	dbo.Prod_Serv p\n"
                    + " join dbo.Classe_Imposto i on\n"
                    + "	i.Ordem = p.Ordem_Classe_Imposto_Saida\n"
                    + "left join dbo.Classe_Imposto_Operacao cioS on\n"
                    + "	cioS.Ordem_Classe_Imposto = i.Ordem\n"
                    + "	and cioS.Estados = '" + estado + "' /*and cioS.Ordem_CFOP_Prod_NF = 39*/\n"
                    + "	and cioS.Ordem_Operacao = " + operacao
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao"))
                    );
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
                    "select DISTINCT \n"
                    + "	p.Ordem_Classe merc1,\n"
                    + "	c.Nome desc1,\n"
                    + "	p.Ordem_Subclasse merc2,\n"
                    + "	s.Nome desc2,\n"
                    + "	p.Ordem_Grupo merc3,\n"
                    + "	g.Nome desc3\n"
                    + "from\n"
                    + "	Prod_Serv p\n"
                    + "join Classes c on\n"
                    + "	p.Ordem_Classe = c.Ordem\n"
                    + "join Subclasses s on\n"
                    + "	p.Ordem_Subclasse = s.Ordem\n"
                    + "join Grupos g on\n"
                    + "	p.Ordem_Grupo = g.Ordem"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));

                    result.add(imp);
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
                    "SELECT Ordem id, Nome familia FROM Familias "
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("familia"));

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
                    + "	p.Ordem id,\n"
                    + "	p.Codigo codigointerno,\n"
                    + "	p.Codigo_Barras ean,\n"
                    + "	u.Nome unidade\n"
                    + "from\n"
                    + "	dbo.Prod_Serv p\n"
                    + "left join dbo.Unidades_Venda u on\n"
                    + "	u.Ordem = p.Ordem_Unidade_Venda"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codigointerno"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        estado = estado == null ? exibirMensagemComComboBox("Escolha o estado", true) : estado;
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.Ordem id,\n"
                    + "	p.Codigo codigointerno,	\n"
                    + "	p.Nome_Nota descricaoreduzida,\n"
                    + "	p.Nome descricaocompleta,\n"
                    + "	p.Nome descricaogondula,\n"
                    + "	p.Ordem_Classe merc1,\n"
                    + "	p.Ordem_Subclasse merc2,\n"
                    + "	p.Ordem_Grupo merc3,\n"
                    + "	p.Ordem_Familia idfamilia,\n"
                    + "	p.Peso_Liq,\n"
                    + "	p.Peso_Bruto,\n"
                    + "	p.Data_Cadastro,\n"
                    + "	p.Inativo,\n"
                    + "	p.Codigo_Barras ean,\n"
                    + "	p.Dias_Validade validade,\n"
                    + "	p.Ordem_NCM,\n"
                    + "	p.Ordem_CEST,\n"
                    + "	n.Codigo as ncm,\n"
                    + "	c.Codigo as cest,\n"
                    + "	m.Markup_Percentual margem,\n"
                    + "	u.Nome Unidade,\n"
                    + "	ie.Ordem Cod_Imposto_Entrada,\n"
                    + "	ie.Nome Imposto_Entrada,\n"
                    + "	i.Ordem Cod_Imposto_Saida,\n"
                    + "	i.Nome Imposto_Saida,\n"
                    + "	t.Perc_Estadual,\n"
                    + "	e.Qtde_Estoque_Atual estoque,\n"
                    + "	cioS.PIS_COFINS_CST cstpiscofins,\n"
                    + "	cioS.ICMS_Base_Norm_Reduzida,\n"
                    + "	cioS.ICMS_CST_CSOSN cst,\n"
                    + "	cioS.ICMS_Percentual_Norm icms,\n"
                    + "	cioS.ICMS_Valor_Base_Subs_Reduzida reducao,\n"
                    + "	COALESCE(pre.Preco, 0) precovenda,\n"
                    + "	cus.Preco custocomimposto,\n"
                    + "	coalesce(p.Tipo_Produto_Balanca_Toledo, -1) balanca\n"
                    + "from\n"
                    + "	dbo.Prod_Serv p\n"
                    + "left join dbo.NCM n on\n"
                    + "	n.Ordem = p.Ordem_NCM\n"
                    + "left join dbo.CEST c on\n"
                    + "	c.Ordem = p.Ordem_CEST\n"
                    + "left join dbo.Prod_Serv_Assis_Custo m on\n"
                    + "	m.Ordem = p.Ordem\n"
                    + "left join dbo.Unidades_Venda u on\n"
                    + "	u.Ordem = p.Ordem_Unidade_Venda\n"
                    + "left join dbo.Classe_Imposto ie on\n"
                    + "	ie.Ordem = p.Ordem_Classe_Imposto_Entrada \n"
                    + "	and ie.Tipo_Operacao = 'E'\n"
                    + "left join dbo.Classe_Imposto i on\n"
                    + "	i.Ordem = p.Ordem_Classe_Imposto_Saida\n"
                    + "	and i.Tipo_Operacao = 'S'\n"
                    + "left join dbo.Classe_Imposto_Operacao cioS on\n"
                    + "	cioS.Ordem_Classe_Imposto = i.Ordem\n"
                    + "	and cioS.Estados = '" + estado + "' /*and cioS.Ordem_CFOP_Prod_NF = 39*/\n"
                    + "	and cioS.Ordem_Operacao = 7\n"
                    + "left join dbo.Prod_Serv_Carga_Tributaria t on\n"
                    + "	t.Ordem_Prod_Serv = p.Ordem\n"
                    + "	and t.Estado = '" + estado + "'\n"
                    + "left join Estoque_Atual e on p.Ordem = e.Ordem_Prod_Serv and e.Ordem_Filial = 1\n"
                    + "left join Prod_Serv_Precos pre on pre.Ordem_Prod_Serv = p.Ordem and pre.Ordem_Tabela_Preco = 1 \n"
                    + "left join Prod_Serv_Precos cus on cus.Ordem_Prod_Serv = p.Ordem and cus.Ordem_Tabela_Preco = 4"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("codigointerno"));
                    imp.setEan(rst.getString("ean"));

                    int codigoProduto = Utils.stringToInt(rst.getString("codigointerno"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean").trim().isEmpty() || "".equals(rst.getString("ean").trim()) ? rst.getString("codigointerno") : rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondula"));
                    imp.setTipoEmbalagem(rst.getString("Unidade"));
                    imp.setTipoEmbalagemCotacao(rst.getString("Unidade"));
                    imp.setQtdEmbalagem(1);
                    imp.seteBalanca("0".equals(rst.getString("balanca").trim()));

                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setSituacaoCadastro(rst.getInt("Inativo") == 0 ? 1 : 0);
                    imp.setDataCadastro(rst.getDate("Data_Cadastro"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPesoBruto(rst.getDouble("Peso_Bruto"));
                    imp.setPesoLiquido(rst.getDouble("Peso_Liq"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito = rst.getString("Cod_Imposto_Saida");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofins"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpiscofins"));
                    //imp.setPiscofinsNaturezaReceita(rst.getString(""));

                    result.add(imp);
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
                    "SELECT\n"
                    + "	promo.Data_Inicial dataInicio,\n"
                    + "	promo.Data_Final dataFim,\n"
                    + "	p.Ordem_Prod_Serv idProduto,\n"
                    + "	p.Preco precoNormal,\n"
                    + "	promo.Preco precoOferta\n"
                    + "FROM\n"
                    + "	Prod_Serv_Precos p\n"
                    + "JOIN Prod_Serv_Promocao promo ON promo.Ordem_Prod_Serv  = p.Ordem_Prod_Serv \n"
                    + "WHERE\n"
                    + "	promo.Ordem_Filial = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("dataInicio"));
                    imp.setDataInicio(rst.getDate("dataFim"));
                    imp.setDataFim(rst.getDate("idProduto"));
                    imp.setPrecoNormal(rst.getDouble("precoNormal"));
                    imp.setPrecoOferta(rst.getDouble("precoOferta"));

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
                    "  select f.Codigo as ordem, f.Ordem_Cidade, f.Ordem_Pais, f.Tipo, f.Fisica_Juridica, \n"
                    + "       f.Nome, f.Fantasia, f.Endereco, f.Numero, f.Complemento, f.Bairro, \n"
                    + "       f.Cidade, f.Estado, f.CEP, f.CPF, f.CNPJ, f.RG_IE, f.Inscricao_Estadual_PF,\n"
                    + "       f.Inscricao_Municipal, f.Fone_1, f.Fone_2, f.Fax, f.Bloqueado, f.Data_Cadastro,\n"
                    + "       f.Inativo, upper(c.Cidade) Municipio, c.UF, c.Cod_Ibge,\n"
                    + "       f.Endereco_Cob, f.Numero_Cob, f.Complemento_Cob, f.Bairro_Cob, f.Cidade_Cob, f.Estado_Cob,\n"
                    + "       upper(cob.Cidade) Municipio_Cob, cob.UF Estado_Cod2, cob.Cod_Ibge Cod_Ibge_Cob\n"
                    + "  from dbo.Cli_For f\n"
                    + "  left join Cidades c on c.Ordem = f.Ordem_Cidade\n"
                    + "  left join Cidades cob on cob.Ordem = f.Ordem_Cidade_Cob\n"
                    + " where f.Tipo = 'F'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("Ordem"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(rst.getString("Fantasia"));
                    imp.setCnpj_cpf("".equals(rst.getString("CNPJ").trim()) ? rst.getString("CPF") : rst.getString("CNPJ"));
                    imp.setIe_rg("".equals(rst.getString("Inscricao_Estadual_PF").trim()) ? rst.getString("RG_IE") : rst.getString("Inscricao_Estadual_PF"));

                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("Estado"));
                    imp.setCep(rst.getString("CEP"));

                    imp.setAtivo("0".equals(rst.getString("Inativo")));
                    imp.setDatacadastro(rst.getDate("Data_Cadastro"));
                    imp.setTel_principal(rst.getString("Fone_1"));

                    if ((rst.getString("Fone_2") != null)
                            && (!rst.getString("Fone_2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "Telefone 2",
                                rst.getString("Fone_2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
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
                    + "	Ordem_Fornecedor1 as Fornecedor,\n"
                    + "	Codigo as Produto,\n"
                    + "	Codigo_Adicional1 as CodExterno\n"
                    + "from\n"
                    + "	dbo.Prod_Serv\n"
                    + "where\n"
                    + "	Ordem_Fornecedor1 > 0\n"
                    + "union all\n"
                    + "select\n"
                    + "	Ordem_Fornecedor2 as Fornecedor,\n"
                    + "	Codigo as Produto,\n"
                    + "	Codigo_Adicional2 as CodExterno\n"
                    + "from\n"
                    + "	dbo.Prod_Serv\n"
                    + "where\n"
                    + "	Ordem_Fornecedor2 > 0\n"
                    + "union all\n"
                    + "select\n"
                    + "	Ordem_Fornecedor3 as Fornecedor,\n"
                    + "	Codigo as Produto,\n"
                    + "	Codigo_Adicional3 as CodExterno\n"
                    + "from\n"
                    + "	dbo.Prod_Serv\n"
                    + "where\n"
                    + "	Ordem_Fornecedor3 > 0\n"
                    + "union all\n"
                    + "select\n"
                    + "	Ordem_Fornecedor4 as Fornecedor,\n"
                    + "	Codigo as Produto,\n"
                    + "	Codigo_Adicional4 as CodExterno\n"
                    + "from\n"
                    + "	dbo.Prod_Serv\n"
                    + "where\n"
                    + "	Ordem_Fornecedor4 > 0\n"
                    + "union all\n"
                    + "select\n"
                    + "	Ordem_Fornecedor5 as Fornecedor,\n"
                    + "	Codigo as Produto,\n"
                    + "	Codigo_Adicional5 as CodExterno\n"
                    + "from\n"
                    + "	dbo.Prod_Serv\n"
                    + "where\n"
                    + "	Ordem_Fornecedor5 > 0"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("Fornecedor"));
                    imp.setIdProduto(rst.getString("Produto"));
                    imp.setCodigoExterno(rst.getString("CodExterno"));

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
                    "select\n"
                    + "	c.codigo as ordem,\n"
                    + "	c.Ordem_Cidade,\n"
                    + "	c.Ordem_Pais,\n"
                    + "	c.Tipo,\n"
                    + "	c.Fisica_Juridica,\n"
                    + "	c.Nome,\n"
                    + "	c.Fantasia,\n"
                    + "	c.Endereco,\n"
                    + "	c.Numero,\n"
                    + "	c.Complemento,\n"
                    + "	c.Bairro,\n"
                    + "	c.Cidade,\n"
                    + "	c.Estado,\n"
                    + "	c.CEP,\n"
                    + "	c.CPF,\n"
                    + "	c.CNPJ,\n"
                    + "	c.RG_IE,\n"
                    + "	c.Inscricao_Estadual_PF,\n"
                    + "	c.Inscricao_Municipal,\n"
                    + "	c.Fone_1,\n"
                    + "	c.Fone_2,\n"
                    + "	c.Fax,\n"
                    + "	c.Bloqueado,\n"
                    + "	c.Data_Cadastro,\n"
                    + "	c.Inativo,\n"
                    + "	upper(m.Cidade) Municipio,\n"
                    + "	m.UF,\n"
                    + "	m.Cod_Ibge,\n"
                    + "	c.Endereco_Cob,\n"
                    + "	c.Numero_Cob,\n"
                    + "	c.Complemento_Cob,\n"
                    + "	c.Bairro_Cob,\n"
                    + "	c.Cidade_Cob,\n"
                    + "	c.Estado_Cob,\n"
                    + "	c.Limite_Credito,\n"
                    + "	upper(cob.Cidade) Municipio_Cob,\n"
                    + "	cob.UF,\n"
                    + "	cob.Cod_Ibge\n"
                    + "from\n"
                    + "	dbo.Cli_For c\n"
                    + "left join Cidades m on\n"
                    + "	m.Ordem = c.Ordem_Cidade\n"
                    + "left join Cidades cob on\n"
                    + "	cob.Ordem = c.Ordem_Cidade_Cob\n"
                    + "where\n"
                    + "	c.Tipo = 'C'"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("Ordem"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(rst.getString("Fantasia"));
                    imp.setCnpj("".equals(rst.getString("CNPJ").trim()) ? rst.getString("CPF") : rst.getString("CNPJ"));
                    imp.setInscricaoestadual("".equals(rst.getString("Inscricao_Estadual_PF").trim()) ? rst.getString("RG_IE") : rst.getString("Inscricao_Estadual_PF"));

                    imp.setEndereco(rst.getString("Endereco"));
                    imp.setNumero(rst.getString("Numero"));
                    imp.setComplemento(rst.getString("Complemento"));
                    imp.setBairro(rst.getString("Bairro"));
                    imp.setMunicipio(rst.getString("Cidade"));
                    imp.setUf(rst.getString("Estado"));
                    imp.setCep(rst.getString("CEP"));

                    imp.setDataCadastro(rst.getDate("Data_Cadastro"));
                    imp.setAtivo("0".equals(rst.getString("Inativo").trim()));
                    imp.setTelefone(rst.getString("Fone_1"));
                    imp.setCelular(rst.getString("Fone_2"));

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
                    + " r.Ordem id,\n"
                    + "	c.Codigo,\n"
                    + "	c.Nome,\n"
                    + "	r.Ordem,\n"
                    + "	r.Tela_Origem,\n"
                    + "	r.Pagar_Receber,\n"
                    + "	r.Tipo_Conta,\n"
                    + "	r.Situacao,\n"
                    + "	r.Ordem_Filial,\n"
                    + "	r.Ordem_Caixa,\n"
                    + "	r.Ordem_Cli_For,\n"
                    + "	r.Nota,\n"
                    + "	r.Fatura,\n"
                    + "	r.Descricao,\n"
                    + "	r.Data_Emissao,\n"
                    + "	r.Data_Vencimento,\n"
                    + "	r.Data_Quitacao,\n"
                    + "	r.Data_Baixa,\n"
                    + "	r.Valor_Base,\n"
                    + "	r.Valor_Total,\n"
                    + "	r.Valor_Juros,\n"
                    + "	r.Valor_Desconto,\n"
                    + "	r.Valor_Total_Calculado,\n"
                    + "	r.Valor_Quitado,\n"
                    + "	r.Valor_Final_Calculado\n"
                    + "from\n"
                    + "	dbo.Financeiro_Contas r\n"
                    + "inner join dbo.Cli_For c on\n"
                    + "	c.Ordem = r.Ordem_Cli_For\n"
                    + "	and c.Tipo = 'C'\n"
                    + "where\n"
                    + "	r.Situacao = 'A'\n"
                    + "	and r.Pagar_Receber = 'R'\n"
                    + "	and r.Ordem_Filial = 1\n"
                    + "	and r.Tipo_Conta = 'R'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("Nota")));
                    imp.setIdCliente(rst.getString("Codigo"));
                    imp.setEcf(rst.getString("Tela_Origem"));
                    imp.setValor(rst.getDouble("Valor_Final_Calculado"));
                    imp.setDataEmissao(rst.getDate("Data_Emissao"));
                    imp.setDataVencimento(rst.getDate("Data_Vencimento"));
                    imp.setObservacao(rst.getString("Descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public String exibirMensagemComComboBox(String titulo, boolean isEstado) throws Exception {
        JComboBox comboBox = new JComboBox<>();
        comboBox.setModel(new DefaultComboBoxModel<>());

        List<String> dados = carregarDadosOperacoes(isEstado);

        dados.forEach(dado -> comboBox.addItem(dado));
        JPanel panel = new JPanel();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(new JLabel(
                "<html>"
                + "<body>"
                + "<div style=\"width: 100%; border-bottom: 1px solid red; margin-bottom: 10px\">"
                + "<p style=\"display: inline-block;\"><font size=3 face=\"arial\">Escolha o estado ou tipo de operação:</font></p>"
                + "<p style=\"display: inline-block;\"><font size=3 face=\"arial\">Se estiver escolhendo operação, sugerimos a opção VENDA</font></p>"
                + "<br>"
                + "</div>"
                + "</body>"
                + "</html>"), java.awt.BorderLayout.NORTH);
        panel.add(comboBox, java.awt.BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                titulo,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String escolha = (String) comboBox.getSelectedItem();
            String[] primeiraString = escolha.split(" ");
            return primeiraString[0];
        } else {
            throw new Exception("É obrigatório escolher um item!");
        }
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private List<String> carregarDadosOperacoes(boolean isEstado) throws Exception {

        if (isEstado) {
            return new LocalDAO().getSiglas();
        }

        List<String> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT Ordem id, nome descricao FROM Operacoes order by 1"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("id") + " " + rst.getString("descricao"));
                }
            }
        }
        return result;
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
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("")));
                        next.setEcf(Utils.stringToInt(rst.getString("")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble(""));
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
                        next.setSequencia(rst.getInt(""));
                        next.setProduto(rst.getString(""));
                        next.setUnidadeMedida(rst.getString(""));
                        next.setCodigoBarras(rst.getString(""));
                        next.setDescricaoReduzida(rst.getString(""));
                        next.setQuantidade(rst.getDouble(""));
                        next.setPrecoVenda(rst.getDouble(""));
                        next.setTotalBruto(rst.getDouble(""));
                        next.setCancelado(rst.getBoolean(""));
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
