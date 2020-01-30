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
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
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
        List<Estabelecimento> result = new ArrayList<>();
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
                    imp.setIdFornecedor(rs.getString("idFornecedor"));
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
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "     (titulo||''||npar)::varchar as id,\n"
                    + "     datavenda dataEmissao,\n"
                    + "     numeropedido numeroCupom,\n"
                    + "     statusgrupoid ecf,\n"
                    + "     totaltitulo valor,\n"
                    + "     obstitulo observacao,\n"
                    + "     pessoaid idCliente,\n"
                    + "     datavencimento dataVencimento,\n"
                    + "     npar parcela,\n"
                    + "     juros,\n"
                    + "     identificador cnpjCliente\n"
                    + "from registrobaixatemporaria\n"
                    + "     where status = 'Aberto'\n"
                    + "     order by numerocupom, idcliente")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataEmissao(rs.getDate("dataEmissao"));
                    imp.setNumeroCupom(rs.getString("numeroCupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao("Doc.: " + rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("idCliente"));
                    imp.setDataVencimento(rs.getDate("dataVencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setCnpjCliente(rs.getString("cnpjcliente"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

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
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                      "select \n"
                    + "     p.codigo id,\n"
                    + "     cadastro dataCadastro,\n"
                    + "     ultimaalteracao dataAlteracao,\n"
                    + "     codigobarrasbuscapreco ean,\n"
                    + "     1 qtdembalagem,\n"
                    + "     un.descricao tipoEmbalagem,\n"
                    + "     case when balanca = '1' then 1 else 0 end eBalanca,\n"
                    + "     p.descricao descricaoCompleta,\n"
                    + "     descricaofiscal descricaoReduzida,\n"
                    + "     p.descricao descricaoGondola,\n"
                    //+ "     tipoprodutoid codMercadologico1,\n"
                    //+ "     subsecaoid codMercadologico2,\n"
                    + "     p.familiaprodutoid idFamiliaProduto,\n"
                    + "     pesobruto,\n"
                    + "     pesoliquido,\n"
                    + "     estoqueMaximo,\n"
                    + "     estoqueMinimo,\n"
                    + "     margemlucro margem,\n"
                    + "     margemlucrominima margemMinima,\n"
                    + "     valorcompra custoSemImposto,\n"
                    + "     valorcompra custoComImposto,\n"
                    + "     custoanterior custoAnteriorSemImposto,\n"
                    + "     valor precovenda,\n"
                    + "     pa.preco precoatacado,\n"
                    + "     case when statusid = 29 then 1 else 0 end situacaoCadastro ,\n"
                    + "     ncmsh ncm,\n"
                    + "     si.cest,\n"
                    + "     pc.pis_cst_e piscofinsCstCredito,\n"
                    + "     pc.pis_cst_s piscofinsCstDebito,\n"
                    + "     pc.cod_natureza_receita piscofinsNaturezaReceita,\n"
                    + "     ei.ei_cst icmsCstEntrada,\n"
                    + "     ei.ei_alq icmsAliqEntrada,\n"
                    + "     ei.ei_rbc icmsReducaoEntrada,\n"
                    + "     si.sac_cst icmsCstSaida,\n"
                    + "     si.sac_alq icmsAliqSaida,\n"
                    + "     si.sac_rbc icmsReducaoSaida\n"
                    + "from produto p\n"
                    + "     left join unidade un on un.id = p.unidadeid\n"
                    + "     left join familiaproduto f on p.familiaprodutoid = f.id\n"
                    //+ "     left join spedcodigoreceita scr on scr.id = p.spedcodigoreceitaid\n"
                    + "     left join produtoprecoauxiliar pa on p.id = pa.produtoid\n"
                    + "     left join mxf_vw_icms si on si.codigo_produto = p.codigo\n"
                    + "     left join mxf_vw_icms_entrada ei on ei.codigo_produto = p.codigo\n"
                    + "     left join mxf_vw_pis_cofins pc on pc.codigo_produto = p.codigo\n"
                    + "order by 1")) {

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

                    //imp.setCodMercadologico1(rs.getString("codMercadologico1"));
                    //imp.setCodMercadologico2(rs.getString("codMercadologico2"));
                    imp.setIdFamiliaProduto(rs.getString("idFamiliaProduto"));

                    imp.setPesoBruto(rs.getInt("pesobruto"));
                    imp.setPesoLiquido(rs.getInt("pesoliquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setMargemMinima(rs.getDouble("margemMinima"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setCustoAnteriorSemImposto(rs.getDouble("custoAnteriorSemImposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rs.getInt("situacaoCadastro"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    
                    imp.setPiscofinsCstCredito(rs.getString("piscofinsCstCredito"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofinsCstDebito"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("piscofinsNaturezaReceita"));
                    imp.setIcmsCstSaida(rs.getInt("icmsCstSaida"));
                    imp.setIcmsAliqSaida(rs.getInt("icmsAliqSaida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsReducaoSaida"));
                    imp.setIcmsCstEntrada(rs.getInt("icmsCstEntrada"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmsAliqEntrada"));
                    imp.setIcmsReducaoEntrada(rs.getDouble("icmsReducaoEntrada"));
                    imp.setIcmsCstSaida(rs.getInt("icmsCstSaida"));
                    imp.setIcmsAliqSaida(rs.getDouble("icmsAliqSaida"));
                    imp.setIcmsReducaoSaida(rs.getDouble("icmsReducaoSaida"));
                    
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
                    //imp.seteBalanca(rs.getString("tipoembalagem").contains("KG") ? true : false);
                    //imp.setValidade(rs.getInt("validade"));

                    //imp.setSituacaoCadastro("A".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
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
                    + "     pp.produtoid idProduto,\n"
                    + "     p.inicio dataInicio,\n"
                    + "     p.fim dataFim,\n"
                    + "     pp.precoatual precoNormal,\n"
                    + "     pp.precopromocional precoOferta,\n"
                    + "     case when p.statusid = 29 then 1 else 0 end situacaoOferta\n"
                    + "from promocao p\n"
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
                    "select\n"
                    + "vp.id importId,\n"
                    + "vp.razaosocial razao,\n"
                    + "vp.nomefantasia fantasia,\n"
                    + "vp.identificador cnpj_cpf,\n"
                    + "vp.inscricaoestadual ie_rg,\n"
                    + "vp.inscricaomunicipal insc_municipal,\n"
                    + "dpj.suframa suframa,\n"
                    + "case when vp.status = 18 then 1 else 0 end ativo,\n"
                    + "logradouro endereco,\n"
                    + "e.numero,\n"
                    + "complemento,\n"
                    + "bairro,\n"
                    + "e.cidade ibge_municipio,\n"
                    + "c.cidade municipio,\n"
                    + "est.descricao uf,\n"
                    + "cep,\n"
                    + "t.ddd||''||t.numero tel_principal,\n"
                    + "vp.datacadastro datacadastro,\n"
                    + "dpj.obs	observacao\n"
                    + "from vw_pessoas vp\n"
                    + "left join endereco e on e.dadosid = vp.id\n"
                    + "left join estado est on est.id = e.estado::bigint\n"
                    + "left join cidade c on c.id::varchar = e.cidade\n"
                    + "left join telefone t on t.dadosid = vp.id\n"
                    + "left join dadospessoajuridica dpj on vp.id = dpj.id\n"
                    + "where vp.id in (select id from vw_fornecedor)\n"
                    + "order by 1"
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
                    "select distinct\n"
                    + "     c.id as id,\n"
                    + "     vc.identificador as cnpj,\n"
                    + "     dpf.inscricaoestadual as inscricaoestadual,\n"
                    + "     vc.nome as razao,\n"
                    + "     vc.nome as fantasia,\n"
                    + "     case when vc.status <> 19 then 1 else 0 end as ativo,\n"
                    + "     case when vc.status = 18 then 0 else 1 end as bloqueado,\n"
                    + "     logradouro as endereco,\n"
                    + "     e.numero as numero,\n"
                    + "     complemento,\n"
                    + "     bairro,\n"
                    + "     upper(cd.cidade) as municipio,\n"
                    + "     est.descricao as uf,\n"
                    + "     cep as cep,\n"
                    + "     dpf.estadocivil as tipoestadocivil,\n"
                    + "     dpf.datanascimento as datanascimento,\n"
                    + "     vc.datacadastro as datacadastro,\n"
                    + "     c.limitefinanceiro as valorlimite,\n"
                    + "     dpf.conjuge as nomeconjuge,\n"
                    + "     dpf.nomepai as nomepai,\n"
                    + "     dpf.nomemae as nomemae,\n"
                    + "     dpf.obs as observacao,\n"
                    + "     c.limitefinanceiro as limitecompra,\n"
                    + "     vc.inscricaomunicipal as inscricaomunicipal,\n"
                    + "     d.contribuinteicms as tipoindicadorie\n"
                    + "from cliente c\n"
                    + "     left join vw_pessoas vc\n"
                    + "		on c.id = vc.id\n"
                    + "     left  join endereco e\n"
                    + "		on e.dadosid = c.id\n"
                    + "     left join estado est\n"
                    + "		on est.id = e.estado::bigint \n"
                    + "     left join cidade cd\n"
                    + "		on cd.id::varchar = e.cidade\n"
                    + "     left join telefone t\n"
                    + "		on t.dadosid = c.id\n"
                    + "     left join dados d\n"
                    + "		on d.id = c.id\n"
                    + "     left join dadospessoafisica dpf\n"
                    + "		on c.id = dpf.id\n"
                    + "order by c.id")) {
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
