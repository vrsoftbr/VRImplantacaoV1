package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class G10DAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    public String lojaMesmoID;
    public boolean situacaoOferta;

    @Override
    public String getSistema() {
        if (lojaMesmoID == null) {
            lojaMesmoID = "";
        }
        return "G10 Sistemas" + lojaMesmoID;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     1 as id,\n"
                    + "     identificador as cnpj,\n"
                    + "     nomefantasia as nome\n"
                    + "from dadospessoajuridica dpj\n"
                    + "     join dados d \n"
                    + "     on d.id = dpj.id\n"
                    + " where d.id = 1428"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return lojas;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      distinct\n"
                    + "      fiscal.cod::integer,\n"
                    + "      fiscal.dsc,\n"
                    + "      fiscal.ctr,\n"
                    + "      fiscal.ica,\n"
                    + "      fiscal.icr,\n"
                    + "      fiscal.icf\n"
                    + "from\n"
                    + "    gceffs01 fiscal\n"
                    + "where\n"
                    + "     fiscal.ufo = 'RJ' and\n"
                    + "     fiscal.ufd = 'RJ' and\n"
                    + "     fiscal.rgf = 0\n"
                    + "order by\n"
                    + "     fiscal.cod::integer")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("cod"), rs.getString("dsc")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "     tp.id::varchar Merc1ID,\n"
                    + "     tp.descricao Merc1Descricao,\n"
                    + "     sb.id::varchar Merc2ID,\n"
                    + "     sb.descricao Merc2Descricao,\n"
                    + "     s.id::varchar Merc3ID,\n"
                    + "     s.descricao Merc3Descricao\n"
                    + "from produto p\n"
                    + "     inner join tipoproduto tp\n"
                    + "		on tp.id::varchar = p.tipoprodutoid::varchar\n"
                    + "	inner join subsecao sb\n"
                    + "		on p.subsecaoid = sb.id\n"
                    + "	inner join secao s\n"
                    + "		on sb.secaoid::varchar = s.id::varchar\n"
                    + "order by 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("Merc1ID"));
                    imp.setMerc1Descricao(rs.getString("Merc1Descricao"));
                    imp.setMerc2ID(rs.getString("Merc2ID"));
                    imp.setMerc2Descricao(rs.getString("Merc2Descricao"));
                    imp.setMerc3ID(rs.getString("Merc3ID"));
                    imp.setMerc3Descricao(rs.getString("Merc3Descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     fornecedorid idFornecedor,\n"
                    + "     produtoid idProduto,\n"
                    + "     codprodfornecedor codigoExterno,\n"
                    + "     (select qtdeembalagem::varchar from produto p where p.id::varchar = pf.produtoid::varchar) as qtdEmbalagem,\n"
                    + "     data dataAlteracao,\n"
                    + "     aliquotaipi ipi\n"
                    + "from produtofornecedor pf\n"
                    + "     order by 1")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setIpi(rs.getDouble("ipi"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "scp de produtos")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoresumida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));
                    imp.setCodMercadologico2(rs.getString("mercadologico2"));
                    imp.setCodMercadologico3(rs.getString("mercadologico3"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem") == 0 ? 1 : rs.getInt("qtdembalagem"));
                    imp.setEan(rs.getString("ean"));
                    imp.setValidade(rs.getInt("validade"));

                    if (("1".equals(rs.getString("balanca").trim()))) {
                        if (v_usar_arquivoBalanca) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getImportId().trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
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
                        }
                    } else {
                        imp.seteBalanca(rs.getString("tipoembalagem").contains("KG") ? true : false);
                        imp.setValidade(rs.getInt("validade"));
                    }
                    imp.setSituacaoCadastro("A".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPesoBruto(rs.getInt("pesobruto"));
                    imp.setPesoLiquido(rs.getInt("pesoliquido"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("vlvenda"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsCstSaida(rs.getInt("cstdebito"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsdebito"));
                    imp.setIcmsReducao(rs.getDouble("icmsreducao"));

                    if (rs.getInt("piscofins") == 1) {
                        imp.setPiscofinsCstCredito(50);
                        imp.setPiscofinsCstDebito(1);
                    } else {
                        imp.setPiscofinsCstCredito(71);
                        imp.setPiscofinsCstDebito(7);
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<OfertaIMP> getOfertas() throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select     \n"
                    + "	pp.produtoid idProduto,\n"
                    + "	p.inicio dataInicio,\n"
                    + "	p.fim dataFim,\n"
                    + "	pp.precoatual precoNormal,\n"
                    + "	pp.precopromocional precoOferta,\n"
                    + "	case when p.statusid = 29 then 1 else 0 end situacaoOferta\n"
                    + "from promocao p\n"
                    + "	left join promocaoproduto pp\n"
                    + "		on p.id = pp.promocaoid\n"
                    + "         where p.fim >= now()\n"
                    + "	order by pp.produtoid "
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setDataInicio(rs.getDate("dataInicio"));
                    imp.setDataFim(rs.getDate("dataFim"));
                    imp.setPrecoNormal(rs.getDouble("precoNormal"));
                    imp.setPrecoOferta(rs.getDouble("precoOferta"));
                    //imp.setSituacaoOferta(rs.getBoolean("situacaoOferta"));
                    
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	vf.id importId,\n"
                    + "	vf.razaosocial razao,\n"
                    + "	vf.nomefantasia fantasia,\n"
                    + "	identificador cnpj_cpf,\n"
                    + "	vf.inscricaoestadual ie_rg,\n"
                    + "	vf.inscricaomunicipal insc_municipal,\n"
                    + "	case when dpj.statusid = 18 then 0 else 1 end ativo,\n"
                    + "	logradouro endereco,\n"
                    + "	e.numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	e.cidade ibge_municipio,\n"
                    + "	c.cidade municipio,\n"
                    + "	est.descricao uf,\n"
                    + "	cep,\n"
                    + "	t.ddd||''||t.numero tel_principal,\n"
                    + "	datacadastro,\n"
                    + "	dpj.obs	observacao \n"
                    + "from fornecedor f\n"
                    + "	left join vw_pessoas vf\n"
                    + "		on f.id = vf.id\n"
                    + "	left  join endereco e\n"
                    + "		on e.dadosid = f.id\n"
                    + "	left join estado est\n"
                    + "		on est.id = e.estado::bigint \n"
                    + "	left join cidade c\n"
                    + "		on c.id::varchar = e.cidade\n"
                    + "	left join telefone t\n"
                    + "		on t.dadosid = f.id\n"
                    + "	left join dadospessoajuridica dpj\n"
                    + "		on f.id = dpj.id\n"
                    + "order by f.id"
            )) {

                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setInsc_municipal(rs.getString("insc_municipal"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "      t.cod as id,\n"
                    + "      t.nom as nome,\n"
                    + "      t.cgc::bigint as cpfcnpj,\n"
                    + "      t.pss as tipopessoa,\n"
                    + "      t.fan as fantasia,\n"
                    + "      t.log as endereco,\n"
                    + "      t.num as numero,\n"
                    + "      t.bai as bairro,\n"
                    + "      t.cmp as complemento,\n"
                    + "      t.mun as municipio,\n"
                    + "      t.cep,\n"
                    + "      t.cxp as caixapostal,\n"
                    + "      t.tel,\n"
                    + "      t.fax,\n"
                    + "      t.cel,\n"
                    + "      t.ins as ie,\n"
                    + "      t.oex as orgaoexp,\n"
                    + "      t.dtn as datanascimento,\n"
                    + "      t.eml as email,\n"
                    + "      t.emn as emailnf,\n"
                    + "      t.obs,\n"
                    + "      t.dtc as datacadastro,\n"
                    + "      t.dtm as datamovimentacao,\n"
                    + "      (select vlc from trsccv01 tr where tr.cod = t.cod) as limitecredito,\n"
                    + "      case when conv.dbl is null\n"
                    + "      then 0 else 1 end bloqueado \n"
                    + "from\n"
                    + "    trstra01 t\n"
                    + "left join trstra01 conv on t.cod = conv.cod\n"
                    + "where\n"
                    + "     t.tip in ('D', 'C')\n"
                    + "order by\n"
                    + "     t.cod")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("tel"));
                    imp.setFax(rs.getString("fax"));
                    imp.setCelular(rs.getString("cel"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setEmail(rs.getString("email").toLowerCase());
                    imp.addEmail(rs.getString("emailnf").toLowerCase(), TipoContato.NFE);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setLimiteCompra(rs.getDouble("limitecredito"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setObservacao(rs.getString("obs"));
                    imp.setOrgaoemissor(rs.getString("orgaoexp"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
