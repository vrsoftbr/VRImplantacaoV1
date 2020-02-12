package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoSqlServer;
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

/**
 *
 * @author Importacao
 */
public class AccesysDAO extends InterfaceDAO implements MapaTributoProvider {

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
                while(rs.next()) {
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
                    imp.setIcmsCstEntrada(rs.getInt("icms_cst_e"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icms_rbc_e"));
                    
                    //Aliquota Debito
                    imp.setIcmsAliqSaida(rs.getDouble("icms_alqt_s"));
                    imp.setIcmsCstSaida(rs.getInt("icms_cst_s"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icms_rbc_s"));
                    
                    imp.setSituacaoCadastro(rs.getInt("desativo") == 1 ?
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
                    "SELECT \n" +
                    "	COD_PRODFOR id_produto,\n" +
                    "	CODFOR_PRODFOR id_fornecedor,\n" +
                    "	CODBARRA_PRODFOR codigoexterno\n" +
                    "FROM \n" +
                    "	CONTROLE_ESTOQUE.dbo.CE_PRODFOR")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
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
}
