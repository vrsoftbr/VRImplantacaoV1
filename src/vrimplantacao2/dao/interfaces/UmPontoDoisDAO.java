package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class UmPontoDoisDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean usaBalanca = false;

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	CliCod id,\n" +
                    "	CliNom nome,\n" +
                    "	CliNomRed fantasia,\n" +
                    "	CliCnpj cnpj,\n" +
                    "	CliCpf cpf,\n" +
                    "	CliEnd endereco,\n" +
                    "	CliEndNum numero,\n" +
                    "	CliEndBai bairro,\n" +
                    "	ci.CidNom municipio,\n" +
                    "	CliCidCod ibgemunicipio,\n" +
                    "	CliCep cep,\n" +
                    "	CliFone telefone,\n" +
                    "	CliCel celular,\n" +
                    "	CliFax fax,\n" +
                    "	CliEmail email,\n" +
                    "	CliTp tipo,\n" +
                    "	CliAtv ativo,\n" +
                    "	CliIes inscricaoestadual,\n" +
                    "	CliRg rg,\n" +
                    "	CliContato nomecontato,\n" +
                    "	CliVlrLimCre limitecredito,\n" +
                    "	case when CliDtaNasc = '1000-01-01' then null else CliDtaNasc end dtnascimento,\n" +
                    "	CliOrgEmi orgaoemissor,\n" +
                    "	CliDtaCad dtcadastro\n" +
                    "from \n" +
                    "	clientes cl\n" +
                    "left join \n" +
                    "	cidades ci on cl.CliCidCod = ci.CidCod")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    if("F".equals(rs.getString("tipo"))) {
                        imp.setCnpj(rs.getString("cpf"));
                        imp.setInscricaoestadual(rs.getString("rg"));
                    } else {
                        imp.setCnpj(rs.getString("cnpj"));
                        imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    }
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("ibgemunicipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                    imp.setDataCadastro(rs.getDate("dtcadastro"));
                    if(rs.getString("nomecontato") != null && !"".equals(rs.getString("nomecontato"))) {
                        imp.addContato("1", 
                                rs.getString("nomecontato"), 
                                null, 
                                null, 
                                null);
                    }
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
                    "select \n" +
                    "	CcrSeq idconta,\n" +
                    "	CcrNumPed pedido,\n" +
                    "	CcrNumDoc documento,\n" +
                    "	CcrCliCod idcliente,\n" +
                    "	cl.clitp tipo,\n" +
                    "	cl.CliCnpj,\n" +
                    "	cl.CliCpf,\n" +
                    "	CcrDtaEmi dtemissao,\n" +
                    "	CcrDtaVct dtvencimento,\n" +
                    "	CcrVlr valor,\n" +
                    "	CcrVlrJur valorjuros,\n" +
                    "	CcrVlrDsc valordesconto,\n" +
                    "	CcrPar parcela,\n" +
                    "	CcrCxCod caixa,\n" +
                    "	CcrNumEcf ecf,\n" +
                    "	CcrObs obs\n" +
                    "from \n" +
                    "	contasareceber c\n" +
                    "inner join \n" +
                    "	clientes cl on cl.CliCod = c.CcrCliCod\n" +
                    "where \n" +
                    "	CcrSts = 1 and\n" +
                    "	CcrNumPed != 0\n" +
                    "order by\n" +
                    "	CcrDtaEmi")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("idconta"));
                    imp.setNumeroCupom(rs.getString("pedido"));
                    if("F".equals(rs.getString("tipo"))) {
                        imp.setCnpjCliente(rs.getString("clicpf"));
                    } else {
                        imp.setCnpjCliente(rs.getString("clicnpj"));
                    }
                    imp.setDataEmissao(rs.getDate("dtemissao"));
                    imp.setDataVencimento(rs.getDate("dtvencimento"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("obs"));
                    
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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	prdcod, \n"
                    + "	prdean \n"
                  + "from \n"
                    + "	cadastrodeprodutos \n"
                  + "where \n"
                    + "	prdean <> prdcodbarras and\n"
                    + "	prdean <> ''")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("prdcod"));
                    imp.setEan(rs.getString("prdean"));
                    imp.setQtdEmbalagem(1);

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	forcod id,\n" +
                    "	ForNom razaosocial,\n" +
                    "	ForNomRed fantasia,\n" +
                    "	ForCnpj cnpj,\n" +
                    "	ForCpf cpf,\n" +
                    "	c.CidNom municipio,\n" +
                    "	ForCidCod ibgemunicipio,\n" +
                    "	ForEnd endereco,\n" +
                    "	ForEndNum numero,\n" +
                    "	ForEndBai bairro,\n" +
                    "	ForEndCompl complemento,\n" +
                    "	ForAtv ativo,\n" +
                    "	ForCep cep,\n" +
                    "	ForIes inscricaoestadual,\n" +
                    "	ForRG rg,\n" +
                    "	ForSite site,\n" +
                    "	ForEmail email,\n" +
                    "	ForTp tipofornecedor,\n" +
                    "	ForContato nomecontato,\n" +
                    "	ForCont contato,\n" +
                    "	ForFone telefone,\n" +
                    "	ForFax fax,\n" +
                    "	ForCel celular,\n" +
                    "	case when ForDtaCad = '1000-01-01' then null else ForDtaCad end datacadastro\n" +
                    "from\n" +
                    "	fornecedores f\n" +
                    "left join \n" +
                    "	cidades c on f.ForCidCod = c.CidCod\n" +
                    "order by\n" +
                    "	ForCod")){
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    if("J".equals(rs.getString("tipofornecedor"))) {
                        imp.setCnpj_cpf(rs.getString("cnpj"));
                        imp.setIe_rg(rs.getString("inscricaoestadual"));
                    } else {
                        imp.setCnpj_cpf(rs.getString("cpf"));
                        imp.setIe_rg(rs.getString("rg"));
                    }
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setAtivo(rs.getInt("ativo") == 1);
                    if(rs.getString("email") != null && !"".equals(rs.getString("email"))) {
                        imp.addContato("EMAIL", 
                                    null, 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    rs.getString("email"));
                    }
                    if(rs.getString("nomecontato") != null && !"".equals(rs.getString("nomecontato"))) {
                        imp.addContato(rs.getString("nomecontato"), 
                                    null, 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("TEL CONTATO", 
                                    rs.getString("contato"), 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("fax") != null && !"".equals(rs.getString("fax"))) {
                        imp.addContato("FAX", 
                                    rs.getString("fax"), 
                                    null, 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    if(rs.getString("celular") != null && !"".equals(rs.getString("celular"))) {
                        imp.addContato("CELULAR", 
                                    null, 
                                    rs.getString("celular"), 
                                    TipoContato.COMERCIAL,
                                    null);
                    }
                    imp.setTel_principal(rs.getString("telefone"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	empcod id,\n"
                    + "	empnomfan fantasia\n"
                  + "from \n"
                    + "	empresa")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	GrpPrdCod merc1,\n"
                    + "	grpprddsc descmerc1,\n"
                    + "   GrpPrdCod merc2,\n"
                    + "	grpprddsc descmerc2,\n"
                    + "	GrpPrdCod merc3,\n"
                    + "	grpprddsc descmerc3\n"
                  + "from \n"
                    + "	grupodeprodutos\n"
                  + "order by \n"
                    + "	GrpPrdCod")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	PrdCod as id,\n"
                    + "	prdId as idtabela,\n"
                    + "	PrdDsc as descricaocompleta,\n"
                    + "	prdEan as ean,\n"
                    + "	PrdCodBarras codigobarras,\n"
                    + "	PrdAtv as ativo,\n"
                    + "	PrdVlrCus as valorcusto,\n"
                    + "	PrdCusMed as customedio,\n"
                    + "	PrdVlrCusSt as valorcustoST,\n"
                    + "	PrdVlrVen as valorvenda,\n"
                    + "	PrdMargLuc as margem,\n"
                    + "	PrdUndCod as embalagem,\n"
                    + "	PrdEstMin as estoqueminimo,\n"
                    + "	PrdEstAtu as estoque,\n"
                    + "	GrpPrdCod as merc1,\n"
                    + "	GrpPrdCod as merc2,\n"
                    + "	GrpPrdCod as merc3,\n"
                    + "	PrdPeso as peso,\n"
                    + "	PrdCstIcms as csticms,\n"
                    + "	PrdNcmCod as ncm,\n"
                    + "	cf.CfopSpAliq icmsdebito,\n"
                    + " PrdValidade validade,\n"
                    + " PrdValidade2,\n"
                    + " case when grpprdcod = 62 then 1 else 0 end balanca\n "
                  + "from \n"
                    + "	cadastrodeprodutos c \n"
                  + "left join\n"
                    + "	cfop cf on cf.CfopSeq = c.PrdCfopSeq\n"
                  + "order by\n"
                    + "	PrdCod")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagem(1);
                    imp.setSituacaoCadastro(rs.getInt("ativo"));
                    imp.setCustoComImposto(rs.getDouble("valorcusto"));
                    imp.setCustoSemImposto(rs.getDouble("valorcusto"));
                    imp.setPrecovenda(rs.getDouble("valorvenda"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    if (rs.getDouble("peso") != 0) {
                        imp.setPesoBruto(rs.getDouble("peso"));
                        imp.setPesoLiquido(rs.getDouble("peso"));
                    }
                    imp.setIcmsCstSaida(rs.getInt("csticms"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsdebito"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstCredito(99);
                    imp.setPiscofinsCstDebito(49);

                    if (rs.getInt("balanca") == 1) {
                        if (usaBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getImportId());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                                //imp.setEan(imp.getImportId());
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca((rs.getInt("balanca") == 1));
                            imp.setValidade(rs.getInt("validade"));
                        }
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
        try(Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	PrdCod idproduto,\n" +
                    "	prdforcod idfornecedor,\n" +
                    "	PrdRefProd referencia\n" +
                    "from \n" +
                    "	referenciaprodlevel1\n" +
                    "order by\n" +
                    "	prdcod, prdforcod")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("referencia"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public String getSistema() {
        return "1.2 Informatica";
    }
    
    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
