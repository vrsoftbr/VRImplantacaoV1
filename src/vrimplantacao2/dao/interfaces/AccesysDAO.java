package vrimplantacao2.dao.interfaces;

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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Importacao
 */
public class AccesysDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(AccesysDAO.class.getName());
    
    @Override
    public String getSistema() {
        return "Accesys";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Estabelecimento> getLojaCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	COD_EMPRESA id,\n" +
                    "	NOMEFANTASIA fantasia\n" +
                    "from\n" +
                    "	CONTROLE_CLIENTES.dbo.CC_EMPRESA")) {
                while(rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	distinct\n" +
                    "	p.CODIGOSETOR merc1,\n" +
                    "	s.DESCRICAO descmerc1,\n" +
                    "	p.CODGRU_PRODUTOS merc2,\n" +
                    "	g.DESCRICAO_GRUPOS descmerc2\n" +
                    "from\n" +
                    "	CONTROLE_ESTOQUE.dbo.CE_PRODUTOS p\n" +
                    "inner join controle_estoque.dbo.CE_SETORES s on p.CODIGOSETOR = s.CODIGO\n" +
                    "inner join controle_estoque.dbo.CE_GRUPOS g on p.CODGRU_PRODUTOS = g.CODIGO_GRUPOS\n" +
                    "order by\n" +
                    "	1, 3")) {
                while(rs.next())
                {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "SELECT \n" +
                    "	Codigo,\n" +
                    "	Descricao\n" +
                    "FROM \n" +
                    "	CONTROLE_ESTOQUE.dbo.Familias")) {
                while(rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setDescricao(rs.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.CODPROD_PRODUTOS id,\n" +
                    "	p.CODBARRA_PRODUTOS ean,\n" +
                    "	p.DESCRICAO_PRODUTOS descricaocompleta,\n" +
                    "	p.DescricaoResumida,\n" +
                    "	p.UNIDADE_PRODUTOS embalagem,\n" +
                    "	pe.Custo,\n" +
                    "	pe.Margem,\n" +
                    "	pe.Venda,\n" +
                    "	pe.Quantidade estoque,\n" +
                    "	p.CODIGOSETOR merc1,\n" +
                    "	p.CODGRU_PRODUTOS merc2,\n" +
                    "	p.CodFamilia familia,\n" +
                    "	p.QTDMINIMA_PRODUTOS estoquemin,\n" +
                    "	p.PRAZOVAL_PRODUTOS validade,\n" +
                    "	p.PRODUTOPESAVEL pesavel,\n" +
                    "	p.NCM_PRODUTOS ncm,\n" +
                    "	p.STPIS pis,\n" +
                    "	p.STCOFINS cofins,\n" +
                    "	p.STPisEntrada piscredito,\n" +
                    "	p.STCofinsEntrada cofinscredito,\n" +
                    "	p.Nat_Rec_Cofins naturezareceita,\n" +
                    "	p.DataCadastro,\n" +
                    "	p.CEST,\n" +
                    "	p.STICMSEntrada icms_cst_e,\n" +
                    "	red_e.VALORREDUCAO icms_rbc_e,\n" +
                    "	p.MixAliquotaICMSEntrada icms_alqt_e,\n" +
                    "	p.STICMS icms_cst_s,\n" +
                    "	p.MixAliquotaICMSSaida icms_alqt_s,\n" +
                    "	red_s.VALORREDUCAO icms_rbc_s,\n" +
                    "	p.IVA,\n" +
                    "	p.TipoIVA tipo_iva,\n" +
                    "	p.Inutilizado desativado\n" +
                    "from\n" +
                    "	ce_produtos p\n" +
                    "inner join ProdutosEmpresa pe on p.CODBARRA_PRODUTOS = pe.Barras\n" +
                    "left outer join CE_REDUCAOICMS red_e on p.ReducaoEntrada = red_e.CODIGO\n" +
                    "left outer join CE_REDUCAOICMS red_s on p.REDUCAO = red_s.CODIGO\n" +
                    "where\n" +
                    "	pe.CodEmpresa = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	p.CODPROD_PRODUTOS")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    
                    if(rs.getInt("pesavel") == 1) {
                        imp.seteBalanca(true);
                        imp.setEan(imp.getImportId());
                    }
                    
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoGondola());
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rs.getDouble("venda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3("1");
                    imp.setIdFamiliaProduto(rs.getString("familia"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstCredito(rs.getString("piscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("pis"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setCest(rs.getString("cest"));
                    
                    //Aliquota Credito
                    imp.setIcmsAliqEntrada(rs.getDouble("icms_alqt_e"));
                    imp.setIcmsCstEntrada(Utils.stringToInt(rs.getString("icms_cst_e")));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_rbc_e"));
                    
                    //Aliquota Debito
                    imp.setIcmsAliqSaida(rs.getDouble("icms_alqt_s"));
                    imp.setIcmsCstSaida(Utils.stringToInt(rs.getString("icms_cst_s")));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_rbc_s"));
                    
                    imp.setSituacaoCadastro(rs.getInt("desativado") == 1 ?
                            SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	f.CODIGO_FORNECEDORES id,\n" +
                    "	f.RAZAO_FORNECEDORES razao,\n" +
                    "	f.NomeFantasia fantasia,\n" +
                    "	f.CNPJ_FORNECEDORES cnpj,\n" +
                    "	f.IE_FORNECEDORES ie,\n" +
                    "	f.TELEFONE_FORNECEDORES telefone,\n" +
                    "	f.ENDERECO_FORNECEDORES endereco,\n" +
                    "	f.BAIRRO_FORNECEDORES bairro,\n" +
                    "	f.CIDADE_FORNECEDORES cidade,\n" +
                    "	f.CEP_FORNECEDORES cep,\n" +
                    "	f.UF_FORNECEDORES uf,\n" +
                    "	f.CodMunicipio cidadeibge,\n" +
                    "	f.CodUF ufibge,\n" +
                    "	f.NUMERO,\n" +
                    "	f.Complemento,\n" +
                    "	f.email1,\n" +
                    "	f.email2,\n" +
                    "	f.email3,\n" +
                    "	f.InscricaoMunicipal,\n" +
                    "	f.CNAE,\n" +
                    "	f.Fax,\n" +
                    "	f.Observacoes\n" +
                    "from\n" +
                    "	CE_FORNECEDORES f\n" +
                    "order by\n" +
                    "	1")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setIbge_municipio(rs.getInt("cidadeibge"));
                    imp.setIbge_uf(rs.getInt("ufibge"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    
                    if(rs.getString("observacoes") != null && !"".equals(rs.getString("observacoes"))) {
                        imp.setObservacao(rs.getString("observacoes"));
                    }
                    
                    if(rs.getString("email1") != null && !"".equals(rs.getString("email1"))) {
                        imp.addContato("1", "EMAIL", null, null, TipoContato.NFE, rs.getString("email1"));
                    }
                    
                    if(rs.getString("email2") != null && !"".equals(rs.getString("email2"))) {
                        imp.addContato("2", "EMAIL2", null, null, TipoContato.NFE, rs.getString("email2"));
                    }
                    
                    if(rs.getString("email3") != null && !"".equals(rs.getString("email3"))) {
                        imp.addContato("3", "EMAIL3", null, null, TipoContato.NFE, rs.getString("email3"));
                    }
                    
                    if(rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("4", "FAX", null, null, TipoContato.NFE, rs.getString("fax"));
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
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	pf.CODFOR_PRODFOR idfornecedor,\n" +
                    "	p.CODPROD_PRODUTOS idproduto,\n" +
                    "	pf.CODBARRA_PRODFOR codigoexterno\n" +
                    "from \n" +
                    "	CONTROLE_ESTOQUE.dbo.CE_PRODFOR pf\n" +
                    "join CONTROLE_ESTOQUE.dbo.CE_PRODUTOS p on pf.CODBARRA_PRODFOR = p.CODBARRA_PRODUTOS")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	c.CodCliente id,\n" +
                    "	c.Carteira,\n" +
                    "	c.NomeCliente razao,\n" +
                    "	c.CpfCliente cpf,\n" +
                    "	c.RgCliente rg,\n" +
                    "	c.EnderecoCliente endereco,\n" +
                    "	c.BairroCliente bairro,\n" +
                    "	c.CidadeCliente cidade,\n" +
                    "	c.CodMunicipio cidadeibge,\n" +
                    "	c.UF,\n" +
                    "	c.CodUf ufibge,\n" +
                    "	c.NUMERO,\n" +
                    "	c.CepCliente cep,\n" +
                    "	c.LimiteCheque,\n" +
                    "	c.LimiteCliente,\n" +
                    "	c.TelCliente telefone,\n" +
                    "	c.CelCliente celular,\n" +
                    "	c.Datanascimento,\n" +
                    "	c.Obs,\n" +
                    "	c.email,\n" +
                    "	c.Sexo,\n" +
                    "	c.DataAbertura\n" +
                    "from\n" +
                    "	controle_clientes.dbo.cc_clientes c")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("Carteira"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getString("cidadeibge"));
                    imp.setUf(rs.getString("uf"));
                    imp.setUfIBGE(rs.getInt("ufibge"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setValorLimite(rs.getDouble("limitecliente"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    
                    if(rs.getString("obs") != null && !"".equals(rs.getString("obs"))) {
                        imp.setObservacao(rs.getString("obs"));
                    }
                    
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("dataabertura"));
                    imp.setSexo(rs.getString("sexo"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	 r.Codigo id,\n" +
                    "	 r.CodVenda coo,\n" +
                    "	 r.CodCliente carteira,\n" +
                    "	 r.Numero parcela,\n" +
                    "	 r.ValorRestante,\n" +
                    "	 r.Data dataemissao,\n" +
                    "	 r.DataVencimento,\n" +
                    "	 r.NumeroCaixa ecf\n" +
                    "from \n" +
                    "	dbo.ParcelasCrediario r\n" +
                    "where	\n" +
                    "	r.DataPagamento is null or\n" +
                    "	ValorRestante < valor and ValorRestante != 0 and\n" +
                    "	r.CodEmpresa = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	r.data")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setIdCliente(rs.getString("carteira"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valorrestante"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setEcf(rs.getString("ecf"));
                    
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
                        String id = rst.getString("id");
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
                        next.setNomeCliente(rst.getString("NomeCliente"));
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
                    = "select\n" +
                    "	dbo.CE_VendasCaixa.CodVenda id,\n" +
                    "	convert(datetime, convert(varchar(10), dbo.CE_VendasCaixa.Data, 103), 103) as data,\n" +
                    "	dbo.CE_VendasCaixa.Data as DATAHORA,\n" +
                    "	dbo.CE_VendasCaixa.ValorItens,\n" +
                    "	dbo.CE_VendasCaixa.DescAcr,\n" +
                    "	dbo.CE_VendasCaixa.ValorTotal,\n" +
                    "	dbo.CE_VendasCaixa.ValorPago,\n" +
                    "	dbo.CE_VendasCaixa.Troco,\n" +
                    "	dbo.CE_VendasCaixa.NumeroCaixa,\n" +
                    "	dbo.CE_VendasCaixa.TotalCusto as CustoItens,\n" +
                    "	dbo.CE_VendasCaixa.TotalCusto as CustoNota,\n" +
                    "	case\n" +
                    "		when (TOTALCUSTO + ISNULL(dbo.CE_VendasCaixa.DescAcr, 0)) > 0 then \n" +
                    "			round(dbo.CE_VendasCaixa.ValorTotal / (TOTALCUSTO + ISNULL(dbo.CE_VendasCaixa.DescAcr, 0)) * 100 - 100, 2)\n" +
                    "		else 0 end as MargemLucro,\n" +
                    "	dbo.CE_VendasCaixa.ValorTotal - (dbo.CE_VendasCaixa.TotalCusto + ISNULL(dbo.CE_VendasCaixa.DescAcr, 0)) as ValorLucro,\n" +
                    "	dbo.CE_VendasCaixa.COO numerocupom,\n" +
                    "	dbo.CE_VendasCaixa.NumImpFiscal,\n" +
                    "	dbo.CE_VendasCaixa.Impresso,\n" +
                    "	dbo.CE_VendasCaixa.Estornada,\n" +
                    "	dbo.CE_VendasCaixa.Observacoes,\n" +
                    "	dbo.CE_VendasCaixa.Selecionada,\n" +
                    "	dbo.CE_VendasCaixa.CodEmpresa,\n" +
                    "	dbo.CE_VendasCaixa.CodCliente idclientepreferencial,\n" +
                    "	CONTROLE_CLIENTES.dbo.CC_Clientes.NomeCliente,\n" +
                    "	dbo.CE_VendasCaixa.DescAcrItens,\n" +
                    "	ISNULL(dbo.CE_VendasCaixa.DescAcr, 0) + ISNULL(dbo.CE_VendasCaixa.DescAcrItens, 0) as TotalDescontoAcrescimo,\n" +
                    "	dbo.CE_VendasCaixa.ValorItens + ISNULL(dbo.CE_VendasCaixa.DescAcrItens, 0) as ValorItensDesconto,\n" +
                    "	dbo.CE_VendasCaixa.ChaveVenda,\n" +
                    "	dbo.CE_VendasCaixa.DescAcrVenda\n" +
                    "from\n" +
                    "	dbo.CE_VendasCaixa\n" +
                    "left outer join CONTROLE_CLIENTES.dbo.CC_Clientes on\n" +
                    "	dbo.CE_VendasCaixa.CodCliente = CONTROLE_CLIENTES.dbo.CC_Clientes.Carteira\n" +
                    "where\n" +
                    "	convert(datetime, convert(varchar(10), dbo.CE_VendasCaixa.Data, 103), 103) between '" + FORMAT.format(dataInicio) + "' and '" + FORMAT.format(dataTermino) + "' and\n" +
                    "	dbo.CE_VendasCaixa.CodEmpresa = " + idLojaCliente;
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
                        next.setVenda(rst.getString("coo"));
                        next.setProduto(rst.getString("idproduto"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("qtd"));
                        next.setTotalBruto(rst.getDouble("subtotalimpressora"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("ean"));
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
                    = "select\n" +
                    "	dbo.CE_MOVIMENTACAO.cod_mov id,\n" +
                    "	dbo.CE_MOVIMENTACAO.data_mov emissao,\n" +
                    "	dbo.CE_MOVIMENTACAO.custo_mov custototal,\n" +
                    "	dbo.CE_MOVIMENTACAO.qtd_mov qtd,\n" +
                    "	(dbo.CE_MOVIMENTACAO.venda_mov + dbo.CE_MOVIMENTACAO.VLACRDESC) subtotalimpressora,\n" +
                    "	dbo.CE_MOVIMENTACAO.tipo_mov,\n" +
                    "	dbo.CE_MOVIMENTACAO.caixa_mov ecf,\n" +
                    "	dbo.CE_MOVIMENTACAO.coo,\n" +
                    "	dbo.CE_MOVIMENTACAO.numimpfiscal,\n" +
                    "	dbo.CE_MOVIMENTACAO.S_Trib_Aliquota,\n" +
                    "	dbo.CE_MOVIMENTACAO.Valor_Icms,\n" +
                    "	dbo.CE_PRODUTOS.CODPROD_PRODUTOS idproduto,\n" +
                    "	dbo.CE_PRODUTOS.DESCRICAO_PRODUTOS descricao,\n" +
                    "	dbo.CE_PRODUTOS.UNIDADE_PRODUTOS unidade,\n" +
                    "	dbo.CE_MOVIMENTACAO.codbarra_mov ean,\n" +
                    "	dbo.CE_MOVIMENTACAO.Estornada cancelado\n" +
                    "from\n" +
                    "	dbo.CE_PRODUTOS\n" +
                    "right outer join dbo.CE_MOVIMENTACAO on\n" +
                    "	dbo.CE_PRODUTOS.CODBARRA_PRODUTOS = dbo.CE_MOVIMENTACAO.codbarra_mov\n" +
                    "left outer join dbo.CE_VendasCaixa on\n" +
                    "	dbo.CE_MOVIMENTACAO.caixa_mov = dbo.CE_VendasCaixa.NumeroCaixa\n" +
                    "	and dbo.CE_MOVIMENTACAO.numimpfiscal = dbo.CE_VendasCaixa.NumImpFiscal\n" +
                    "	and dbo.CE_MOVIMENTACAO.coo = dbo.CE_VendasCaixa.COO\n" +
                    "where\n" +
                    "	dbo.CE_VendasCaixa.CodEmpresa = " + idLojaCliente + " and\n" +
                    "	convert(datetime, convert(varchar(10), dbo.CE_MOVIMENTACAO.data_mov, 103), 103) "
                    + "between '" + VendaIterator.FORMAT.format(dataInicio) + "' and "
                    + "'" + VendaIterator.FORMAT.format(dataTermino) + "'\n" +
                    "order by\n" +
                    "	dbo.CE_MOVIMENTACAO.data_mov";
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
