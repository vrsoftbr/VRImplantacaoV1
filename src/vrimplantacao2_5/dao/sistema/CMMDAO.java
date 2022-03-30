package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Guilherme
 */
public class CMMDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "CMM";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
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
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA
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
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }
    
    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	coalesce(coalesce(d.CodDep, gg.CodGru), g.CodGru) merc1,\n" +
                    "	coalesce(coalesce(d.Departamento, gg.Grupo), g.Grupo) descmerc1,\n" +
                    "	coalesce(gg.CodGru, g.CodGru) merc2,\n" +
                    "	coalesce(gg.Grupo, g.Grupo) descmerc2,\n" +
                    "	g.CodGru merc3,\n" +
                    "	g.Grupo descmerc3\n" +
                    "from \n" +
                    "	Grupos g\n" +
                    "LEFT JOIN GrupoGeral gg on g.CodGer = gg.CodGru\n" +
                    "LEFT JOIN Departamentos d on gg.CodDep = d.CodDep"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
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
                    "select \n" +
                    "	pr.CodPro id,\n" +
                    "	pr.Produto descricaocompleta,\n" +
                    "	pr.CodBar codigobarras,\n" +
                    "	coalesce(coalesce(d.CodDep, gg.CodGru), g.CodGru) merc1,\n" +
                    "	coalesce(gg.CodGru, g.CodGru) merc2,\n" +
                    "	g.CodGru merc3,\n" +
                    "	pr.exportarbalanca,\n" +
                    "	pr.diasval validade,\n" +
                    "	pr.CodUni embalagemvenda,\n" +
                    "	pr.qtdecx qtdembalagemcompra,\n" +
                    "	pr.customedio,\n" +
                    "	pr.PrecoCompra custocomimposto,\n" +
                    "	pr.CustoEstoque,\n" +
                    "	pr.PrecoCusto,\n" +
                    "	pr.custovendido,\n" +
                    "	pr.PrecoVenda,\n" +
                    "	pr.PrecoAtacado,\n" +
                    "	pr.Margem,\n" +
                    "	pr.NCM,\n" +
                    "	pr.cest,\n" +
                    "	pr.DataCadastro,\n" +
                    "	pr.Ativo,\n" +
                    "	ef.estoque,\n" +
                    "	pr.EstoqueMin,\n" +
                    "	pr.EstoqueMax,\n" +
                    "	pr.PesoBruto,\n" +
                    "	pr.PesoLiquido,\n" +
                    "	case\n" +
                    "		when TPro.Cfop is null then TTri.CstIcms\n" +
                    "		else TPro.CstIcms\n" +
                    "	end as sac_cst,\n" +
                    "	case\n" +
                    "		when TPro.Cfop is null then TTri.PerIcms\n" +
                    "		else TPro.PerIcms\n" +
                    "	end as sac_alq,\n" +
                    "	case\n" +
                    "		when TPro.Cfop is null then TTri.RedBaseIcms\n" +
                    "		else TPro.RedBaseIcms\n" +
                    "	end as sac_rbc,\n" +
                    "	case\n" +
                    "		when TProE.Cfop is null then TTriE.CstIcms\n" +
                    "		else TProE.CstIcms\n" +
                    "	end as ei_cst,\n" +
                    "	case\n" +
                    "		when TProE.Cfop is null then TTriE.PerIcms\n" +
                    "		else TProE.PerIcms\n" +
                    "	end as ei_alq,\n" +
                    "	case\n" +
                    "		when TProE.Cfop is null then TTriE.RedBaseIcms\n" +
                    "		else TProE.RedBaseIcms\n" +
                    "	end as ei_rbc,\n" +
                    "	pr.CSTPCEnt piscredito,\n" +
                    "	pr.CSTPCSai pisdebito,\n" +
                    "	pr.CodTri idicmsdebito,\n" +
                    "	pr.PerMva as mva,\n" +
                    " 	pr.PerMva as mva_distribuidor,\n" +
                    " 	pr.ModIcmsST as tipo_mva\n" +
                    "from \n" +
                    "	produtos pr\n" +
                    "left join EstFil ef on pr.CodPro = ef.CodPro\n" +
                    "left join TribMov TPro on\n" +
                    "	pr.CodPro = TPro.CodTri\n" +
                    "	and TPro.CodMov = 1\n" +
                    "	and TPro.Cad = 1\n" +
                    "left join TribMov TTri on\n" +
                    "	pr.CodTri = TTri.CodTri\n" +
                    "	and TTri.CodMov = 1\n" +
                    "	and TTri.Cad = 2\n" +
                    "left join Tributos on\n" +
                    "	pr.CodTri = Tributos.CodTri\n" +
                    "left join TribMov TProE on\n" +
                    "	pr.CodPro = TProE.CodTri\n" +
                    "	and TProE.CodMov = 2\n" +
                    "	and TProE.Cad = 1\n" +
                    "left join TribMov TTriE on\n" +
                    "	TTriE.CodTri = Tributos.CodTri\n" +
                    "	and TTriE.CodMov = 2\n" +
                    "	and TTriE.Cad = 2\n" +
                    "left join Grupos g on pr.CodGru = g.CodGru\n" +
                    "LEFT JOIN GrupoGeral gg on g.CodGer = gg.CodGru\n" +
                    "LEFT JOIN Departamentos d on gg.CodDep = d.CodDep\n" +
                    "where \n" +
                    "	coalesce(ef.CodFil, 1) = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setTipoEmbalagem(rst.getString("embalagemvenda"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagemcompra"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rst.getInt("estoquemax"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pisdebito"));
                    
                    imp.setIcmsAliqSaida(rst.getDouble("sac_alq"));
                    imp.setIcmsCstSaida(rst.getInt("sac_cst"));
                    imp.setIcmsReducaoSaida(rst.getDouble("sac_rbc"));
                    
                    imp.setIcmsAliqConsumidor(imp.getIcmsAliqSaida());
                    imp.setIcmsCstConsumidor(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoConsumidor(imp.getIcmsReducaoSaida());
                    
                    imp.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaida());
                    imp.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoSaidaForaEstado(imp.getIcmsReducaoSaida());
                    
                    imp.setIcmsAliqSaidaForaEstadoNF(imp.getIcmsAliqSaida());
                    imp.setIcmsCstSaidaForaEstadoNF(imp.getIcmsCstSaida());
                    imp.setIcmsReducaoSaidaForaEstadoNF(imp.getIcmsReducaoSaida());
                    
                    imp.setIcmsAliqEntrada(rst.getDouble("ei_alq"));
                    imp.setIcmsCstEntrada(rst.getInt("ei_cst"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("ei_rbc"));
                    
                    imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqEntrada());
                    imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstEntrada());
                    imp.setIcmsReducaoEntradaForaEstado(imp.getIcmsReducaoEntrada());

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
                    "select \n" +
                    "	c.CodCli id,\n" +
                    "	c.nome,\n" +
                    "	c.Fantasia,\n" +
                    "	c.ie,\n" +
                    "	c.CNPJ,\n" +
                    "	c.Ativo,\n" +
                    "	c.DataCadastro,\n" +
                    "	c.DataNascimento,\n" +
                    "	c.DataAdmissao,\n" +
                    "	c.Endereco,\n" +
                    "	c.Complemento,\n" +
                    "	c.Bairro,\n" +
                    "	c.Numero,\n" +
                    "	c.CEP,\n" +
                    "	c.CodMun ibgemunicipio,\n" +
                    "	m.Municipio,\n" +
                    "	m.UF,\n" +
                    "	c.Telefone,\n" +
                    "	c.Celular,\n" +
                    "	c.Ramal,\n" +
                    "	c.Email,\n" +
                    "	c.Salario,\n" +
                    "	c.LimiteCompra,\n" +
                    "	c.TipoPessoa\n" +
                    "from \n" +
                    "	Clientes c\n" +
                    "left join Municipios m on c.CodMun = m.CodMun\n" +
                    "where \n" +
                    "	c.CadFor = 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setIbge_municipio(rst.getInt("ibgemunicipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addTelefone("CELULAR", rst.getString("celular"));
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
                    "SELECT \n" +
                    "	CodFor idfornecedor,\n" +
                    "	CodPro idproduto,\n" +
                    "	CodNoFor codigoexterno\n" +
                    "FROM \n" +
                    "	ProdForn"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    
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
                    "select \n" +
                    "	c.CodCli id,\n" +
                    "	c.nome,\n" +
                    "	c.Fantasia,\n" +
                    "	c.ie,\n" +
                    "	c.CNPJ,\n" +
                    "	c.Ativo,\n" +
                    "	c.DataCadastro,\n" +
                    "	c.DataNascimento,\n" +
                    "	c.DataAdmissao,\n" +
                    "	c.Endereco,\n" +
                    "	c.Complemento,\n" +
                    "	c.Bairro,\n" +
                    "	c.Numero,\n" +
                    "	c.CEP,\n" +
                    "	c.CodMun ibgemunicipio,\n" +
                    "	m.Municipio,\n" +
                    "	m.UF,\n" +
                    "	c.Telefone,\n" +
                    "	c.Celular,\n" +
                    "	c.Ramal,\n" +
                    "	c.Email,\n" +
                    "	c.Salario,\n" +
                    "	c.LimiteCompra,\n" +
                    "	c.TipoPessoa\n" +
                    "from \n" +
                    "	Clientes c\n" +
                    "left join Municipios m on c.CodMun = m.CodMun\n" +
                    "where \n" +
                    "	c.CadFor = 0"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("ibgemunicipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setFax(rst.getString("ramal"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("limitecompra"));

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
                    "select \n" +
                    "	cr.NumConta id,\n" +
                    "	cr.NumLcto lancamento,\n" +
                    "	cr.CodFil,\n" +
                    "	cr.CodCli idcliente,\n" +
                    "	cr.Valor,\n" +
                    "	coalesce(cr.NumCaixa, 421) ecf,\n" +
                    "	cr.NumCxaLan ecflancamento,\n" +
                    "	cr.NumVenda cupom,\n" +
                    "	cr.NumDoc documento,\n" +
                    "	cr.Descricao,\n" +
                    "	cr.DataEmissao,\n" +
                    "	cr.DataVencimento,\n" +
                    "	cr.ValorJuros,\n" +
                    "	cr.Parcela\n" +
                    "from \n" +
                    "	ContasReceber cr\n" +
                    "where \n" +
                    "	cr.CodFil = 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("descricao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    /*private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new MRC6DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new MRC6DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setNomeCliente(rst.getString("nome_cliente"));
                        next.setCpf(rst.getString("cpf_cnpj"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        String horaInicio = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
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
                    = "SELECT\n"
                    + "	v.VndNumeroVenda id_venda,\n"
                    + "	d.VndDocNumero numerocupom,\n"
                    + "	VndClienteID id_cliente,\n"
                    + "	c.PessoaNome nome_cliente,\n"
                    + "	v.VndNfpCpfCnpj cpf_cnpj,\n"
                    + "	SUBSTRING(e.EstacaoDescricao, 4, 2) ecf,\n"
                    + "	v.VndDtEmissao emissao,\n"
                    + "	CAST (VndDtAbertura as time) horainicio,\n"
                    + "	CAST (VndDtFechamento as time) horatermino,\n"
                    + "	CASE\n"
                    + "	  when v.VndClienteValor = 0\n"
                    + "   then v.VndConvenioValor\n"
                    + "	  ELSE v.VndClienteValor\n"
                    + "	END subtotalimpressora\n"
                    + "FROM\n"
                    + "	TB_VENDA v\n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID\n"
                    + "LEFT JOIN TB_ESTACAO e on e.EstacaoID = v.VndEstacaoID\n"
                    + "LEFT JOIN TB_PESSOA_PFPJ c on c.PessoaID = v.VndClienteID\n"
                    + "WHERE\n"
                    + " d.VndDocNumero is not NULL \n"
                    + "	and v.VndDtEmissao between '" + strDataInicio + "' and '" + strDataTermino + "'";
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
                        next.setSequencia(rst.getInt("nro_item"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
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
                    + "	v.VndNumeroVenda id_venda,\n"
                    + "	vi.DocBaseItemID id_item,\n"
                    + "	vi.DocBaseItemSequencia nro_item,\n"
                    + "	vi.DocBaseItemProdID produto,\n"
                    + "	un.UnSigla unidade,\n"
                    + "	case\n"
                    + "	   when p.ProdCodBarras1 is null then p.ProdCodInterno\n"
                    + "	   else p.ProdCodBarras1\n"
                    + "	end as codigobarras,\n"
                    + "	p.ProdDescricao descricao,\n"
                    + "	vi.DocBaseItemQuantidade quantidade,\n"
                    + "	vi.DocBaseItemValorUnitario precovenda,\n"
                    + "	vi.DocBaseItemValorTotal total\n"
                    + "from\n"
                    + "	TB_DOCUMENTO_BASE_ITENS vi\n"
                    + "left join TB_VENDA v on v.VndDocBaseID = vi.DocBaseItemDocBaseID \n"
                    + "left join TB_PRODUTO p on p.ProdID = vi.DocBaseItemProdID \n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID \n"
                    + "left join TB_UNIDADE_MEDIDA un on un.UnID = vi.DocBaseItemUnidadeID \n"
                    + "WHERE\n"
                    + " d.VndDocNumero is not NULL \n"
                    + "	and v.VndDtEmissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by 2,1";
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
    }*/
}
