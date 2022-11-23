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
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
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
 * @author Alan
 */
public class GSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "GSoft";
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
                OpcaoProduto.FABRICANTE
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
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.VALOR_LIMITE));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	DISTINCT\n"
                    + "		CONCAT (COALESCE (Trb_ICMS_CST,0),'-',COALESCE (Trb_PICMS,0),'-', COALESCE (Trb_PReducao,0)) id,\n"
                    + "		CONCAT ('CST: ',COALESCE (Trb_ICMS_CST,0), ' ALIQ: ',COALESCE (Trb_PICMS,0), ' RED: ', COALESCE (Trb_PReducao,0)) descricao,\n"
                    + "		COALESCE (Trb_ICMS_CST,0) cst_icms,\n"
                    + "		COALESCE (Trb_PICMS,0) aliq_icms,\n"
                    + "		COALESCE (Trb_PReducao,0) red_icms\n"
                    + "from\n"
                    + "	MC_Produtos\n"
                    + "order by 1"
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

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	m1.Codigo merc1,\n"
                    + "	m1.Nome merc1_descricao,\n"
                    + "	m2.Codigo merc2,\n"
                    + "	m2.Nome merc2_descricao,\n"
                    + "	m2.Codigo merc3,\n"
                    + "	m2.Nome merc3_descricao\n"
                    + "from\n"
                    + "	MC_Setor m1\n"
                    + "	join MC_Grupo m2 on m2.CodSetor = m1.Codigo\n"
                    + "order by 1,3"
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

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	Codigo id_produto,\n"
                    + "	CodBarras ean,\n"
                    + "	1 qtde_emb,\n"
                    + "	Unidade tipo_emb\n"
                    + "from\n"
                    + "	MC_Produtos"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtde_emb"));
                    imp.setTipoEmbalagem(rs.getString("tipo_emb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo id,\n"
                    + "	Produto descricao_comp,\n"
                    + "	NomeCurto descricao_red,\n"
                    + "	CodBarras ean,\n"
                    + "	Setor merc1,\n"
                    + "	Grupo merc2,\n"
                    + "	Grupo merc3,\n"
                    + "	Unidade tipo_emb,\n"
                    + " COALESCE (VendaPorKilo, 'N') pesavel,\n"
                    + "	data data_cad,\n"
                    + "	Data_Modificacao data_alt,\n"
                    + "	Ativo,\n"
                    + " validade,\n"
                    + "	EstoqueMinino estmin,\n"
                    + "	EstoqueMaximo estmax,\n"
                    + "	EstoqueLoja estoque,\n"
                    + "	PrecoVenda,\n"
                    + "	CAST(CustoRealUnitario as numeric (10,2)) precocusto,\n"
                    + "	CAST(MargemLucro as numeric (10,2)) margem,\n"
                    + "	PesoBruto,\n"
                    + "	PesoLiquido,\n"
                    + "	Observacao,\n"
                    + "	CONCAT (COALESCE (Trb_ICMS_CST,0),'-',COALESCE (Trb_PICMS,0),'-', COALESCE (Trb_PReducao,0)) id_icms,\n"
                    + "	Trb_PIS_CST pis_cofins,\n"
                    + "	Trb_NCM ncm,\n"
                    + "	NCM,\n"
                    + "	CEST \n"
                    + "from\n"
                    + "	MC_Produtos"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca("S".equals(rst.getString("pesavel")));

                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        /*imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rst.getInt("validade"));*/
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricao_comp"));
                    imp.setDescricaoReduzida(rst.getString("descricao_red"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
//                  imp.setQtdEmbalagem(rst.getInt("VFD_QTDEMBALAGEM"));
                    imp.setTipoEmbalagem(rst.getString("tipo_emb"));
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setDataAlteracao(rst.getDate("data_alt"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("PrecoVenda"));
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_cofins"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_cofins"));

                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
//                    imp.setIcmsCreditoId(rst.getString("id_icms"));
//                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsCreditoId());

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo id,\n"
                    + "	Nome razao,\n"
                    + "	Fantasia,\n"
                    + "	CPF_CGC,\n"
                    + "	case when PessoaFJ = 'F' then RG else Insc_Estadual end ie,\n"
                    + "	Endereco,\n"
                    + "	Numero,\n"
                    + "	Bairro,\n"
                    + "	Cidade,\n"
                    + "	UF,\n"
                    + "	CEP,\n"
                    + "	Ativo,\n"
                    + "	Fone1,\n"
                    + "	Fone2,\n"
                    + "	Obs1 obs\n"
                    + "from\n"
                    + "	MC_Fornece mf"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("CPF_CGC"));
                    imp.setIe_rg(rst.getString("ie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    imp.setTel_principal(rst.getString("fone1"));

                    if ((rst.getString("Fone2") != null)
                            && (!rst.getString("Fone2").trim().isEmpty())) {
                        imp.addContato(
                                "Fone2",
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CodFornecedor id_fornecedor,\n"
                    + "	Codigo id_produto,\n"
                    + "	1 qtde_emb\n"
                    + "from\n"
                    + "	MC_Produtos\n"
                    + "where\n"
                    + "	CodFornecedor is not NULL\n"
                    + "	and CodFornecedor <> 0\n"
                    + "order by 1 ,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setQtdEmbalagem(rst.getDouble("qtde_emb"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo id,\n"
                    + "	CodFornece id_fornecedor,\n"
                    + "	NumDocto documento,\n"
                    + "	CASE \n"
                    + "     when NumDocto like '%/%'\n"
                    + "		then REPLACE(SUBSTRING(NumDocto, LEN(NumDocto)-1, 2),'/','')\n"
                    + "     when NumDocto like '% - %'\n"
                    + "         then REPLACE(SUBSTRING(NumDocto, LEN(NumDocto)-1, 2),'-','')\n"
                    + "     else 1\n"
                    + "	END parcela,\n"
                    + "	Emissao Emissao,\n"
                    + "	Valor,\n"
                    + "	Vencimento Vencimento,\n"
                    + "	DataPagamento pagamento\n"
                    + "from\n"
                    + "	MC_ContasMovimento cp"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("Valor"), rst.getDate("pagamento"));

                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("vencimento"), (rst.getDouble("valor")));
                    parc.setNumeroParcela(rst.getInt("parcela"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	Codigo id,\n"
                    + "	CPF_CGC cpf_cnpj,\n"
                    + "	RG rg_ie,\n"
                    + "	nome razao,\n"
                    + "	case when Condicao = 'BLOQUEADO' then 1 else 0 end bloqueado,\n"
                    + "	case when Ativo = 'SIM' then 1 else 0 end situacaocadastro,\n"
                    + "	CONVERT (date, DataNascimento) data_nasc,\n"
                    + "	Endereco,\n"
                    + "	Numero,\n"
                    + "	Bairro,\n"
                    + "	Cidade,\n"
                    + "	CodMunicipio mun_ibge,\n"
                    + "	uf,\n"
                    + "	Fone,\n"
                    + "	Celular,\n"
                    + "	Limite,\n"
                    + "	DataCadastro,\n"
                    + "	NomeMae,\n"
                    + "	NomePai,\n"
                    + "	Conjuge,\n"
                    + "	Obs1 observacao\n"
                    + "from\n"
                    + "	MC_Clientes mc\n"
                    + "order by Codigo"
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

                    if (rst.getDouble("limitecheque") > 0) {
                        imp.setPermiteCheque(true);
                        imp.setValorLimite(rst.getDouble("limitecheque"));
                    }

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(getLojaOrigem()
                            + rst.getString("empresa")
                            + rst.getString("documento")
                            + rst.getString("id_cliente")
                            + rst.getString("emissao")
                            + rst.getInt("parcela"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valorparcela"));
                    imp.setEcf(rst.getString("ecf"));

                    Result.add(imp);
                }
            }
        }
        return Result;
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

                        String id = rst.getString("idloja") + "-"
                                + rst.getString("ecf") + "-"
                                + rst.getString("coo") + "-"
                                + rst.getString("dataemissao") + "-"
                                + rst.getString("cro");

                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("coo")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("hora"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        next.setHoraInicio(timestamp.parse(rst.getString("hora")));
                        next.setHoraTermino(timestamp.parse(rst.getString("hora")));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select \n"
                    + "	tgc.vfd_CodFilial idloja,\n"
                    + "	tgc.vfd_Caixa ecf,\n"
                    + "	tgc.vfd_Cupom coo,\n"
                    + "	max(tgc.vfd_CodCliente) idcliente,\n"
                    + "	convert(varchar, tgc.vfd_DataHora, 23) dataemissao, \n"
                    + "	max(tgc.vfd_DataHora) hora,\n"
                    + "	sum(tgc.vfd_Valor) subtotalimpressora,\n"
                    + "	max(tgc.vfd_DataLancamento) emissao,\n"
                    + "	max(vfd_CodFinalizadora) finalizadora,\n"
                    + "	max(vfd_desdobramento) desdo,\n"
                    + "	max(vfd_CRO) cro\n"
                    + "from \n"
                    + "	tab_GastosClientes tgc\n"
                    + "where \n"
                    + "	tgc.vfd_CodFilial = " + idLojaCliente + " and \n"
                    + "	convert(varchar, tgc.vfd_DataHora, 23) between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "'\n"
                    + "group by \n"
                    + "	vfd_CodFilial, \n"
                    + "	vfd_Caixa,\n"
                    + "	vfd_Cupom, \n"
                    + "	convert(varchar, tgc.vfd_DataHora, 23)";
            LOG.log(Level.FINE, "SQL da venda: {0}", sql);
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
                        String id = rst.getString("filial") + "-"
                                + rst.getString("ecf") + "-"
                                + rst.getString("dataemissao")
                                + rst.getString("mov")
                                + rst.getString("coo")
                                + rst.getString("sequencia")
                                + rst.getString("cro")
                                + rst.getString("idproduto");

                        String idVenda = rst.getString("filial") + "-"
                                + rst.getString("ecf") + "-"
                                + rst.getString("coo") + "-"
                                + rst.getString("dataemissao") + "-"
                                + rst.getString("cro");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setSequencia(rst.getInt("sequencia"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select \n"
                    + "	vi.vfd_CodFilial filial,\n"
                    + "	vi.vfd_CodMovimentacao mov,\n"
                    + "	vi.vfd_CodProduto idproduto,\n"
                    + "	vi.vfd_CodBarra ean,\n"
                    + "   pr.vfd_TipoInventarioFatorConversao unidade,\n"
                    + "	vi.vfd_QtdOperac quantidade,\n"
                    + "	vi.vfd_ValorTotal total,\n"
                    + "	convert(varchar, vi.vfd_DTMovimentacao, 23) dataemissao,\n"
                    + "	vi.vfd_DTMovimentacao movimento,\n"
                    + "	vi.vfd_Caixa ecf,\n"
                    + "	vi.vfd_Cupom_NF coo,\n"
                    + "	vi.vfd_Indice sequencia,\n"
                    + "	vi.vfd_CRO_Serie cro\n"
                    + "from\n"
                    + "	tab_MovimentacaoVendas vi\n"
                    + "join tab_produto pr on vi.vfd_CodProduto = pr.vfd_CodProduto\n"
                    + "where\n"
                    + "	vi.vfd_CodFilial = " + idLojaCliente + " and\n"
                    + "	vi.vfd_DTMovimentacao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'";
            LOG.log(Level.FINE, "SQL da venda: {0}", sql);
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
