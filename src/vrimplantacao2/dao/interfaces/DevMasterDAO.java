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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class DevMasterDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca;
    public String lojaMesmoID;
    public boolean situacaoOferta;

    @Override
    public String getSistema() {
        if (lojaMesmoID == null) {
            lojaMesmoID = "";
        }
        return "DevMaster Sistemas" + lojaMesmoID;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select \n"
                    + "     zzz_codigo as id,\n"
                    + "     zzz_nome as nome\n"
                    + " from \n"
                    + "     dmzzz\n"
                    + " order by 1"
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
                    " select distinct \n"
                    + "     t.dm_id cod,\n"
                    + "     abc_descricao dsc,\n"
                    + "     abc_aliqicms aliq,\n"
                    + "     abc_sittrib csticms,\n"
                    + "     abc_reducicms reducao\n"
                    + " from\n"
                    + "     dmabc01 t\n"
                    + "     left join dmaaa01 p\n"
                    + "		on t.abc_codigo = aaa_tessaida\n"
                    + " order by 1"
            )) {
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

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "     aad_codigo Merc1ID,\n"
                    + "     aad_descricao Merc1Descricao\n"
                    + "from dmaad01\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("Merc1ID"));
                    imp.setMerc1Descricao(rs.getString("Merc1Descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*@Override
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
                    + " order by 1"
            )) {
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
    }*/

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select \n"
                    + "     cr.dm_id id,\n"
                    + "     abg_emissao dataEmissao,\n"
                    + "     abg_numero numeroCupom,\n"
                    //+ "     abg_prefixo ecf,\n"
                    + "     abg_valor valor,\n"
                    + "     abg_cliente idCliente,\n"
                    + "     abg_vencimento dataVencimento,\n"
                    + "     abg_parcela parcela,\n"
                    + "     abg_juros juros,\n"
                    + "     abg_multa multa,\n"
                    + "     aam_cpfcnpj cnpjCliente\n"
                    + " from\n"
                    + "     dmabg01 cr\n"
                    + "     left join dmaam01\n"
                    + "		on abg_cliente = aam_codigo\n"
                    + " where\n"
                    + "     abg_situacao <> 'B'"
                    + "     and cr.dm_deletado = 0"
                    + "     and aam_codigo not in ('000000','000001')\n"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setNumeroCupom(rs.getString("numeroCupom"));
                    //imp.setEcf("1");
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("idCliente"));
                    imp.setDataVencimento(rs.getDate("dataVencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setMulta(rs.getDouble("multa"));
                    imp.setCnpjCliente(rs.getString("cnpjCliente"));

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
                    "select	\n"
                    + "	aaa_codigo importId,\n"
                    + "	aaa_datacadastro dataCadastro,\n"
                    + "	p.dm_alterado dataAlteracao,\n"
                    + "	aaa_codbarras ean,\n"
                    + "	aaa_um tipoEmbalagem,\n"
                    //+ "	--case when aaa_um = 'KG' then eBalanca = 'T' end ebalanca,\n"
                    + "	aaa_diasvencimento validade,\n"
                    + "	aaa_descricao descricaoCompleta,\n"
                    + "	aaa_descricao descricaoReduzida,\n"
                    + "	aaa_descricao descricaoGondola,\n"
                    + "	aaa_grupo codMercadologico1,\n"
                    + "	aaa_peso pesoBruto,\n"
                    + "	aaa_margemlucro margem,\n"
                    + " aaa_precovenda precovenda,\n"
                    + "	case when aaa_status = 'A' then 1 else 0 end situacaoCadastro,\n"
                    + "	aaa_posipi ncm,\n"
                    + "	aaa_cest cest,\n"
                    + "	abc_cstpis piscofinsCstDebito,\n"
                    + "	aaa_csticms icmsCstSaida,\n"
                    + "	aaa_aliqicms icmsAliqSaida,\n"
                    + "	aaa_redicmsai icmsReducaoSaida\n"
                    + "    from \n"
                    + "	dmaaa01 p\n"
                    + "		left join dmabc01 t\n"
                    + "			on t.abc_codigo = aaa_tessaida\n"
                    + "order by 1"
            )) {

                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("importId"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataAlteracao"));
                    imp.setEan(rs.getString("ean"));
                    imp.setTipoEmbalagem(rs.getString("tipoEmbalagem"));
                    imp.setValidade(rs.getInt("validade"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaoGondola"));
                    imp.setCodMercadologico1(rs.getString("codMercadologico1"));
                    imp.setPesoBruto(rs.getInt("pesobruto"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setDescontinuado(rs.getBoolean("situacaoCadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofinsCstDebito"));
                    imp.setIcmsCstSaida(rs.getInt("icmsCstSaida"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsAliqSaida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsReducaoSaida"));
                    
                    //imp.setEstoque(rs.getDouble("estoque"));
                    
                    /*if (("1".equals(rs.getString("ebalanca").trim()))) {
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
                    }*/
                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*public List<OfertaIMP> getOfertas() throws Exception {
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
    }*/

    /*@Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select\n"
                    + "	d.id importId,\n"
                    + "	coalesce(pj.razaosocial, pf.nome) razao,\n"
                    + "	coalesce(pj.nomefantasia, pf.nome) fantasia,\n"
                    + "	d.identificador cnpj_cpf,\n"
                    + "	coalesce(pf.inscricaoestadual, pf.rg) ie_rg,\n"
                    + "	pj.inscricaomunicipal insc_municipal,\n"
                    + "	pj.suframa suframa,\n"
                    + "	case when coalesce(pj.statusid, pf.statusid) = 18 then 1 else 0 end ativo,\n"
                    + "	case when coalesce(pj.statusid, pf.statusid) = 20 then 1 else 0 end bloqueado,\n"
                    + "	e.logradouro endereco,\n"
                    + "	e.numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	e.cidade ibge_municipio,\n"
                    + "	cd.descricao municipio,\n"
                    + "	cd.estadoid uf,\n"
                    + "	e.cep,\n"
                    + "	d.datacadastro datacadastro,\n"
                    + "	pj.obs observacao\n"
                    + " from \n"
                    + "	dados d\n"
                    + "	join fornecedor f on\n"
                    + "		d.id = f.id\n"
                    + "	left join dadospessoafisica pf on\n"
                    + "		d.id = pf.id\n"
                    + "	left join dadospessoajuridica pj on\n"
                    + "		d.id = pj.id\n"
                    + "	left join endereco e on\n"
                    + "		e.dadosid = d.id\n"
                    + "	left join cidade cd on\n"
                    + "		e.cidade::integer = cd.id\n"
                    + " order by 1"
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
    }*/

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select \n"
                    + "     aam_codigo id,\n"
                    + "     aam_cpfcnpj cnpj,\n"
                    + "     aam_rgie inscricaoestadual,\n"
                    + "     aam_nome razao,\n"
                    + "     aam_fantasia fantasia,\n"
                    + "     aam_situacao ativo,\n"
                    + "     aam_bloqueado bloqueado,\n"
                    + "     aam_endereco endereco,\n"
                    + "     aam_numero numero,\n"
                    + "     aam_complemento complemento,\n"
                    + "     aam_bairro bairro,\n"
                    + "     aal_codibge municipioIBGE,\n"
                    + "     aal_descricao municipio,\n"
                    + "     aam_estado uf,\n"
                    + "     aam_cep cep,\n"
                    + "     aam_estcivil estadoCivil,\n"
                    + "     aam_nascimento dataNascimento,\n"
                    + "     aam_datacadastro dataCadastro,\n"
                    + "     aam_sexo sexo,\n"
                    + "     aam_limcred valorLimite,\n"
                    + "     aam_nomeconjuge nomeConjuge,\n"
                    + "     aam_nomepai nomePai,\n"
                    + "     aam_nomemae nomeMae,\n"
                    + "     aam_obs observacao,\n"
                    + "     aam_obsfin observacao2, \n"
                    //+ "     aam_telefone1 telefone,\n"
                    + "     aam_telefone2 celular,\n"
                    + "     aam_email email,\n"
                    + "     aam_fax fax,\n"
                    + "     aam_endcob cobrancaEndereco,\n"
                    + "     aam_numcob cobrancaNumero,\n"
                    + "     aam_endcomplcob cobrancaComplemento,\n"
                    + "     aam_estado cobrancaUf,\n"
                    + "     aam_cepcob cobrancaCep\n"
                    + " from dmaam01	\n"
                    + "	   left join dmaal01 \n"
                    + "		on aal_codigo = aam_municipio\n"
                    + " order by 1"
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
                    imp.setMunicipioIBGE(rs.getString("municipioIBGE"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setEstadoCivil(rs.getString("estadoCivil"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSexo(rs.getString("sexo"));
                    imp.setValorLimite(rs.getDouble("valorLimite"));
                    imp.setNomeConjuge(rs.getString("nomeConjuge"));
                    imp.setNomePai(rs.getString("nomePai"));
                    imp.setNomeMae(rs.getString("nomeMae"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setObservacao2(rs.getString("observacao"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email").toLowerCase());
                    imp.setFax(rs.getString("fax"));
                    imp.setCobrancaEndereco(rs.getString("cobrancaEndereco"));
                    imp.setCobrancaNumero(rs.getString("cobrancaNumero"));
                    imp.setCobrancaComplemento(rs.getString("cobrancaComplemento"));
                    imp.setCobrancaUf(rs.getString("cobrancaUf"));
                    imp.setCobrancaCep(rs.getString("cobrancaCep"));
                    //imp.addContato("","","",rs.getString("telefone2"),"");

                    result.add(imp);

                }
            }
        }
        return result;
    }
}
