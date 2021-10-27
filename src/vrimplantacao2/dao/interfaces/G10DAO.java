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
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
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
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                      " select\n"
                    + "     1 as id,\n"
                    + "     identificador as cnpj,\n"
                    + "     nomefantasia as nome\n"
                    + " from dadospessoajuridica dpj\n"
                    + "     join dados d \n"
                    + "     on d.id = dpj.id\n"
                    + " where d.id = 1428"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("nome")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                      " select \n"
                    + "     i.id cod,\n"
                    + "     i.descricao dsc,\n"
                    + "     aliquotaicms aliq,\n"
                    + "     c.codigo csticms,\n"
                    + "     percentbasecalcicms reducao\n"
                    + " from tabelaimpostos i\n"
                    + "     left join cst c\n"
                    + "     on i.cstid = c.id\n"
                    + " order by 1")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("cod"),
                            String.format(
                                    "%d-%.2f-%.2f-%s",
                                    rs.getInt("csticms"),
                                    rs.getDouble("aliq"),
                                    rs.getDouble("reducao"),
                                    rs.getString("dsc")
                            ),
                            rs.getInt("csticms"),
                            rs.getDouble("aliq"),
                            rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }

    /*    @Override
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
     }*/
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                      " select\n"
                    + "     fornecedorid idFornecedor,\n"
                    + "     p.codigo idProduto,\n"
                    + "     codprodfornecedor codigoExterno,\n"
                    + "     p.qtdeembalagem::varchar qtdEmbalagem,\n"
                    + "     data dataAlteracao,\n"
                    + "     pf.aliquotaipi ipi\n"
                    + " from produtofornecedor pf\n"
                    + "     left join produto p\n"
                    + "		on p.id = pf.produtoid\n"
                    + " order by 1")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rs.getString("idFornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));
                    imp.setIpi(rs.getDouble("ipi"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select\n" +
                    "	cr.id id,\n" +
                    "	cr.dataperiodoinicial dataemissao,\n" +
                    "	vc.numeropedido::bigint numerocupom,\n" +
                    "	parc.valor,\n" +
                    "	cr.obs observacao,\n" +
                    "	c.id idcliente,\n" +
                    "	parc.datavencimento datavencimento,\n" +
                    "	numerototalparcelas parcela,\n" +
                    "	round((parc.valor * (j.jurosmora/100) *\n" +
                    "	(extract(day from now() - parc.datavencimento)))::numeric,2) juros\n" +
                    " from\n" +
                    "	titulo cr\n" +
                    "	join parcela parc on\n" +
                    "		parc.tituloid = cr.id\n" +
                    "	join (select * from juros j limit 1) j on\n" +
                    "		true\n" +
                    "	join cliente c on\n" +
                    "		c.id = cr.pessoaid\n" +
                    "	left join vendacliente vc on\n" +
                    "		cr.id = vc.tituloid\n" +
                    " where\n" +
                    "	cr.statusid = 1\n" +
                    "	and	cr.tipodocumentoid = 1\n" +
                    "	and cr.cmfid = 3\n" +
                    "	and parc.baixacancelada is null\n" +
                    " order by\n" +
                    "	parc.datavencimento"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setNumeroCupom(rs.getString("numeroCupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("idCliente"));
                    imp.setDataVencimento(rs.getDate("dataVencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setJuros(rs.getDouble("juros"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
     @Override
     public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
     List<FamiliaProdutoIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
     try (ResultSet rs = stm.executeQuery(
     "select\n"
     + "     id CodFamilia,\n"
     + "     descricao DescricaoFamilia\n"
     + "from familiaproduto")) {
     while (rs.next()) {
     FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
     imp.setImportLoja(getLojaOrigem());
     imp.setImportSistema(getSistema());

     imp.setImportId(rs.getString("CodFamilia"));
     imp.setDescricao(rs.getString("DescricaoFamilia"));

     result.add(imp);
     }
     }
     }
     return result;
     }*/
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    /*"select * from fechamentomensalestoquemestre loja01\n" +
                    "    (\n" +
                    "		select\n" +
                    "			saldoatual\n" +
                    "		from\n" +
                    "			fechamentomensalestoqueloja01\n" +
                    "		 where\n" +
                    "			mestreid = (select max(mestreid)from fechamentomensalestoqueloja01) and produtoid::bigint = p.id::bigint \n" +
                    "	) as estoque,\n" +*/
                    "WITH ean AS (\n" +
                    "	select\n" +
                    "		id produtoid,\n" +
                    "		codigobarrasbuscapreco ean\n" +
                    "	from\n" +
                    "		produto p\n" +
                    "	union\n" +
                    "	select\n" +
                    "		produtoid,\n" +
                    "		codigobarras ean\n" +
                    "	from\n" +
                    "		produtovinculovenda pvv\n" +
                    ")\n" +
                    "select \n" +
                    "	p.codigo id,\n" +
                    "	p.cadastro dataCadastro,\n" +
                    "	p.ultimaalteracao dataAlteracao,\n" +
                    "	ean.ean,\n" +
                    "	1 qtdembalagem,\n" +
                    "	un.descricao tipoEmbalagem,\n" +
                    "	case \n" +
                    "		when balanca = '1' then 1 else 0 \n" +
                    "	end eBalanca,\n" +
                    "	p.descricao descricaoCompleta,\n" +
                    "	p.descricaofiscal descricaoReduzida,\n" +
                    "	p.descricao descricaoGondola,\n" +
                    "	p.familiaprodutoid mercadologico1,\n" +
                    "	p.pesobruto,\n" +
                    "	p.pesoliquido,\n" +
                    "	p.estoqueMaximo,\n" +
                    "	p.estoqueMinimo,\n" +
                    "	p.margemlucro margem,\n" +
                    "	p.margemlucrominima margemMinima,\n" +
                    "	p.valorcompra custoSemImposto,\n" +
                    "	p.valorcompra custoComImposto,\n" +
                    "	p.custoanterior custoAnteriorSemImposto,\n" +
                    "	p.valor precovenda,\n" +
                    "	pa.preco precoatacado,\n" +
                    "	case \n" +
                    "		when statusid = 29 then 1 else 0 \n" +
                    "	end situacaoCadastro,\n" +
                    "	ncmsh ncm,\n" +
                    "	p.cest,\n" +
                    "	piscste.codigo piscofinsCstCredito,\n" +
                    "	piscsts.codigo piscofinsCstDebito,\n" +
                    "	p.cod_natureza_receita piscofinsNaturezaReceita,\n" +
                    "	icms.tabelaimpostosid\n" +
                    "from \n" +
                    "	produto p\n" +
                    "	left JOIN ean ON\n" +
                    "		ean.produtoid = p.id\n" +
                    "	join unidade un on \n" +
                    "		un.id = p.unidadeid\n" +
                    "	left join produtoprecoauxiliar pa on\n" +
                    "		p.id = pa.produtoid	\n" +
                    "	join impostosproduto icms on\n" +
                    "		icms.produtoid = p.id and\n" +
                    "		icms.pessoaemitente = 1428\n" +
                    "	join tabelaimpostos imp ON \n" +
                    "		imp.id = icms.tabelaimpostosid\n" +
                    "	left join cstpis piscste ON\n" +
                    "		piscste.id = imp.cstpisentradaid\n" +
                    "	left join cstpis piscsts ON\n" +
                    "		piscsts.id = imp.cstpissaidaid\n" +
                    "order by 1"
            )) {

                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataAlteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));

                    //imp.setEstoque(rs.getDouble("estoque"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaoGondola"));
                    imp.setCodMercadologico1(rs.getString("mercadologico1"));

                    //imp.setCodMercadologico1(rs.getString("codMercadologico1"));
                    //imp.setCodMercadologico2(rs.getString("codMercadologico2"));
                    //imp.setIdFamiliaProduto(rs.getString("idFamiliaProduto"));
                    imp.setPesoBruto(rs.getInt("pesobruto"));
                    imp.setPesoLiquido(rs.getInt("pesoliquido"));
                    //imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setMargemMinima(rs.getDouble("margemMinima"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rs.getDouble("custoComImposto"));
                    imp.setCustoAnteriorSemImposto(rs.getDouble("custoAnteriorSemImposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setDescontinuado(rs.getBoolean("situacaoCadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setSituacaoCadastro(rs.getInt("situacaoCadastro"));

                    imp.setPiscofinsCstCredito(rs.getString("piscofinsCstCredito"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofinsCstDebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("piscofinsNaturezaReceita"));

                    imp.setIcmsDebitoId(rs.getString("tabelaimpostosid"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("tabelaimpostosid"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("tabelaimpostosid"));
                    imp.setIcmsCreditoId(rs.getString("tabelaimpostosid"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("tabelaimpostosid"));
                    imp.setIcmsConsumidorId(rs.getString("tabelaimpostosid"));

                    imp.setAtacadoPreco(rs.getDouble("precoatacado"));
                    //imp.setCodigoSped(rs.getString("codigoSped"));

                    //imp.setValidade(rs.getInt("validade"));
                    if (("1".equals(rs.getString("ebalanca").trim()))) {
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
                                //imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        }
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
                      " select     \n"
                    + "     pp.produtoid idProduto,\n"
                    + "     p.inicio dataInicio,\n"
                    + "     p.fim dataFim,\n"
                    + "     pp.precoatual precoNormal,\n"
                    + "     pp.precopromocional precoOferta,\n"
                    + "     case when p.statusid = 29 then 1 else 0 end situacaoOferta\n"
                    + " from promocao p\n"
                    + "     left join promocaoproduto pp on p.id = pp.promocaoid\n"
                    + "         where p.statusid = 29\n"
                    + "	order by pp.produtoid"
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
                    "select\n" +
                    "	d.id importId,\n" +
                    "	coalesce(pj.razaosocial, pf.nome) razao,\n" +
                    "	coalesce(pj.nomefantasia, pf.nome) fantasia,\n" +
                    "	d.identificador cnpj_cpf,\n" +
                    "	coalesce(pf.inscricaoestadual, pf.rg) ie_rg,\n" +
                    "	pj.inscricaomunicipal insc_municipal,\n" +
                    "	pj.suframa suframa,\n" +
                    "	case when coalesce(pj.statusid, pf.statusid) = 18 then 1 else 0 end ativo,\n" +
                    "	case when coalesce(pj.statusid, pf.statusid) = 20 then 1 else 0 end bloqueado,\n" +
                    "	e.logradouro endereco,\n" +
                    "	e.numero,\n" +
                    "	complemento,\n" +
                    "	bairro,\n" +
                    "	e.cidade ibge_municipio,\n" +
                    "	cd.descricao municipio,\n" +
                    "	cd.estadoid uf,\n" +
                    "	e.cep,\n" +
                    "	d.datacadastro datacadastro,\n" +
                    "	pj.obs observacao\n" +
                    "from \n" +
                    "	dados d\n" +
                    "join dadosvinculo f on\n" +
                    "		d.id = f.dadosid  and vinculoid=4\n" +
                    "left join dadospessoafisica pf on\n" +
                    "		d.id = pf.id\n" +
                    "left join dadospessoajuridica pj on\n" +
                    "		d.id = pj.id\n" +
                    "left join endereco e on\n" +
                    "		e.dadosid = d.id\n" +
                    "left join cidade cd on\n" +
                    "		e.cidade::integer = cd.id\n" +
                    "order by 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("importId"));
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
                    " select\n" +
                    "	 d.id as id,\n" +
                    "	 d.identificador as cnpj,\n" +
                    "	 coalesce(pf.rg, pf.inscricaoestadual) as inscricaoestadual,\n" +
                    "	 coalesce(pf.nome, pj.razaosocial) as razao,\n" +
                    "	 coalesce(pf.nome, pj.nomefantasia) as fantasia,\n" +
                    "	 case when coalesce(pf.statusid, pj.statusid) <> 19 then 1 else 0 end as ativo,\n" +
                    "	 case when coalesce(pf.statusid, pj.statusid) = 18 then 1 else 0 end as bloqueado,\n" +
                    "	 e.logradouro as endereco,\n" +
                    "	 e.numero as numero,\n" +
                    "	 e.complemento,\n" +
                    "	 e.bairro,\n" +
                    "	 upper(cd.cidade) as municipio,\n" +
                    "	 cd.estadoid uf,\n" +
                    "	 cep as cep,\n" +
                    "	 pf.estadocivil as tipoestadocivil,\n" +
                    "	 pf.datanascimento as datanascimento,\n" +
                    "	 d.datacadastro as datacadastro,\n" +
                    "	 c.limitefinanceiro as valorlimite,\n" +
                    "	 c.diapagamento diavencimento,\n" +
                    "	 pf.conjuge as nomeconjuge,\n" +
                    "	 pf.nomepai as nomepai,\n" +
                    "	 pf.nomemae as nomemae,\n" +
                    "	 pf.obs as observacao,\n" +
                    "	 c.limitefinanceiro as limitecompra,\n" +
                    "	 pj.inscricaomunicipal as inscricaomunicipal,\n" +
                    "	 d.contribuinteicms as tipoindicadorie\n" +
                    " from \n" +
                    "	dados d\n" +
                    "	left join cliente c on\n" +
                    "		d.id = c.id\n" +
                    "	left join dadospessoafisica pf on\n" +
                    "		d.id = pf.id\n" +
                    "	left join dadospessoajuridica pj on\n" +
                    "		d.id = pj.id\n" +
                    "	left join endereco e on\n" +
                    "		e.dadosid = d.id\n" +
                    "	left join cidade cd on\n" +
                    "		e.cidade::integer = cd.id\n" +
                    " where\n" +
                    "	not c.id is null"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setEstadoCivil(rs.getString("tipoestadocivil"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setDiaVencimento(rs.getInt("diavencimento"));
                    //imp.setTipoIndicadorIe(rs.getInt("tipoindicadorie"));
                    imp.setNomeConjuge(rs.getString("nomeconjuge"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setLimiteCompra(rs.getDouble("limitecompra"));
                    imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));

                    //imp.setTelefone(rs.getString("tel"));
                    //imp.setCelular(rs.getString("cel"));
                    //imp.setEmail(rs.getString("email").toLowerCase());
                    //imp.addEmail(rs.getString("emailnf").toLowerCase(), TipoContato.NFE);
                    result.add(imp);

                }
            }
        }
        return result;
    }
}
