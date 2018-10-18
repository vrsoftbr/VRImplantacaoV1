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
import vrimplantacao2.vo.enums.TipoSexo;
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
public class LyncisDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean v_usar_arquivoBalanca = false;

    String telefonesCliente = "";
    String array[];

    @Override
    public String getSistema() {
        return "Lyncis";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select id, descritivo from tributacao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descritivo")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	   id,\n"
                    + "    cnpj,\n"
                    + "    fantasia\n"
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	a.depto,\n"
                    + "	a.descritivo AS desc_depto,\n"
                    + "	coalesce(b.secao, 1) secao,\n"
                    + "	coalesce(b.descritivo, a.descritivo) AS desc_secao,\n"
                    + "	coalesce(c.grupo, 1) grupo,\n"
                    + "	coalesce(c.descritivo, coalesce(b.descritivo, a.descritivo)) AS desc_grupo,\n"
                    + "	coalesce(d.subgrupo, 1) subgrupo,\n"
                    + "	coalesce(d.descritivo, coalesce(c.descritivo, b.descritivo)) AS desc_subgrupo\n"
                  + "FROM \n"
                    + "	depto a\n"
                  + "LEFT JOIN depto b ON a.depto = b.depto AND b.secao <> 0 AND b.grupo = 0\n"
                  + "LEFT JOIN depto c ON b.depto = c.depto AND b.secao = c.secao AND c.grupo <> 0 AND c.subgrupo = 0\n"
                  + "LEFT JOIN depto d ON c.depto = d.depto AND c.secao = d.secao AND c.grupo = d.grupo AND d.subgrupo <> 0\n"
                  + "WHERE \n"
                    + "	a.secao = 0\n"
                  + "ORDER BY \n"
                    + "	a.depto, \n"
                    + "	b.secao, \n"
                    + "	c.grupo, \n"
                    + "	d.subgrupo")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("depto"));
                    imp.setMerc1Descricao(rs.getString("desc_depto"));
                    imp.setMerc2ID(rs.getString("secao"));
                    imp.setMerc2Descricao(rs.getString("desc_secao"));
                    imp.setMerc3ID(rs.getString("grupo"));
                    imp.setMerc3Descricao(rs.getString("desc_grupo"));
                    imp.setMerc4ID(rs.getString("subgrupo"));
                    imp.setMerc4Descricao(rs.getString("desc_subgrupo"));

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
                    "select\n"
                    + "    p.id,\n"
                    + "    ean.ean,\n"
                    + "    p.descritivo,\n"
                    + "    p.reduzido,\n"
                    + "    coalesce_varchar(p.descritivocompleto, p.descritivo) descricaocompleta,\n"
                    + "    pe.estoque_atual,\n"
                    + "    pe.estoque_minimo,\n"
                    + "    pe.estoque_padrao,\n"
                    + "    coalesce(p.pesob, 0::numeric) AS pesob,\n"
                    + "    coalesce(p.pesol, 0::numeric) AS pesol,\n"
                    + "    pl.custo,\n"
                    + "    pl.custo_liquido,\n"
                    + "    pl.custo_medio,\n"
                    + "    pl.custo_sem_imposto,\n"
                    + "    pl.margemcad,\n"
                    + "    pl.margemreal,\n"
                    + "    pl.venda1,\n"
                    + "    p.depto merc1,\n"
                    + "    case when \n"
                    + "	   p.secao = 0 then\n"
                    + "	   1 else\n"
                    + "	   p.secao end as merc2,\n"
                    + "    case when \n"
                    + "	   coalesce(p.grupo, coalesce(p.secao, p.depto)) = 0 then\n"
                    + "	   1 else \n"
                    + "	   coalesce(p.grupo, coalesce(p.secao, p.depto)) end as merc3,\n"
                    + "    case when \n"
                    + "	   coalesce(p.subgrupo, coalesce(p.grupo, p.secao)) = 0 then\n"
                    + "	   1 else\n"
                    + "	   coalesce(p.subgrupo, coalesce(p.grupo, p.secao)) end as merc4,\n"
                    + "    p.unidade_venda,\n"
                    + "    p.unidade_compra,\n"
                    + "    p.embalagem_venda,\n"
                    + "    p.embalagem_compra,\n"
                    + "    p.datahorac,\n"
                    + "    p.envia_balanca balanca,\n"
                    + "    p.situacao,\n"
                    + "    p.classificacao_fiscal ncm,\n"
                    + "    coalesce_varchar(pc.valor, nc.valor) AS cest,\n"
                    + "    (select t.icms from tributacao t where t.id = pl.idtributacao) icmsdebito,\n"
                    + "    (select t.situacao_tributaria from tributacao t where t.id = pl.idtributacao) cstdebito,\n"
                    + "    (select t.reducao from tributacao t where t.id = pl.idtributacao) reducaoicms,\n"
                    + "    (select pt.idpis from produto_tributacao pt where pt.idproduto = p.id) cstpis,\n"
                    + "    p.validade,\n"
                    + "    vw.idgrupo_tributacao tribgrupo\n"        
                    + "from\n"
                    + "    produto p\n"
                    + "join\n" 
                    + "	  vw_produto_tributacao vw on p.id = vw.idproduto\n"        
                    + "join \n"
                    + "    produto_loja pl on p.id = pl.produto\n"
                    + "left join \n"
                    + "    produto_ean ean on p.id = ean.produto\n"
                    + "join \n"
                    + "    produto_estoque pe on p.id = pe.produto\n"
                    + "inner join \n"
                    + "    empresa e on pl.empresa = e.id\n"
                    + "join \n"
                    + "    produto_config pc on p.id = pc.idproduto and pc.id::text = 'PRODUTO.CODIGO.CEST'::text\n"
                    + "left join \n"
                    + "    ncm n on p.classificacao_fiscal::text = n.id::text and\n"
                    + "    p.extipi::text = n.extipi::text\n"
                    + "left join \n"
                    + "    ncm_config nc on n.id::text = nc.idncm::text and nc.id::text = 'NCM.CODIGO.CEST'::text\n"
                    + "where\n"
                    + "    pe.estoque = 1 and\n"
                    + "    e.id = " + getLojaOrigem() + "\n"
                    + "    --p.situacao in (0) 0: Normal; 1: Excluído; 2: Fora de Linha\n"
                    + "order by\n"
                    + "	 p.id")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descritivo"));
                    imp.setDescricaoReduzida(rs.getString("reduzido"));
                    imp.setEstoque(rs.getDouble("estoque_atual"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_padrao"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setMargem(rs.getDouble("margemreal"));
                    imp.setPrecovenda(rs.getDouble("venda1"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setTipoEmbalagem(rs.getString("unidade_venda"));
                    imp.setQtdEmbalagem(rs.getInt("embalagem_venda"));
                    imp.setDataCadastro(rs.getDate("datahorac"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    
                    if(rs.getInt("tribgrupo") == 1) {
                        imp.setIcmsAliqSaida(18);
                        imp.setIcmsCstSaida(10);
                        imp.setIcmsReducaoSaida(0);
                    } else {
                        imp.setIcmsAliqSaida(18);
                        imp.setIcmsCstSaida(0);
                        imp.setIcmsReducaoSaida(0);
                    }
                    
                    imp.setValidade(rs.getInt("validade"));

                    if (v_usar_arquivoBalanca) {
                        if ((rs.getInt("balanca") == 1) || (rs.getInt("balanca") == 2)) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getImportId().trim());

                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.setEan(imp.getImportId());
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(true);
                            imp.setValidade(rs.getInt("validade"));
                        }
                    }
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    
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
                    + "	fornecedor,\n"
                    + "	produto,\n"
                    + " referencia\n"
                  + "from\n"
                    + "	produto_fornecedor\n"
                  + "order by\n"
                    + "	fornecedor, produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setIdProduto(rs.getString("produto"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
                    + "    f.id,\n"
                    + "    f.descritivo razaosocial,\n"
                    + "    f.ie,\n"
                    + "    f.cnpj,\n"
                    + "    f.observacao,\n"
                    + "    f.fantasia,\n"
                    + "    f.situacao,\n"
                    + "    t.tipo,\n"
                    + "    t.descritivo AS desc_tipo,\n"
                    + "    f.datahorac,\n"
                    + "    f.frequencia_visita visita,\n"
                    + "    f.prazo_entrega,\n"
                    + "    f.rg,\n"
                    + "    e.endereco,\n"
                    + "    e.logradouro,\n"
                    + "    e.bairro,\n"
                    + "    e.numero,\n"
                    + "    e.cidade municipioibge,\n"
                    + "    e.referencia,\n"
                    + "    e.cep,\n"
                    + "    e.complemento,\n"
                    + "    c.descritivo municipio,\n"
                    + "    c.estado,\n"
                    + "    fre.descritivo descfrete,\n"
                    + "    fre.valor valorfrete,\n"
                    + "    em.descritivo contato,\n"
                    + "    em.endereco email,\n"
                    + "    tel.observacao tipocontato,\n"
                    + "    tel.ddd || '' || tel.numero telefone\n"
                    + "from\n"
                    + "    fornecedor f\n"
                    + "left join\n"
                    + "    endereco e on f.id = e.cadid and\n"
                    + "    e.idtipo = 1 and e.cadtipo = 3\n"
                    + "left join\n"
                    + "    cidade c on e.cidade = c.id\n"
                    + "left join \n"
                    + "    frete_entrega fre on e.idfrete = fre.id\n"
                    + "left join \n"
                    + "    enderecot t on e.idtipo = t.id\n"
                    + "left join\n"
                    + "    enderecow em on f.id = em.cadid and\n"
                    + "    em.cadtipo = 3\n"
                    + "left join \n"
                    + "    telefone tel on f.id = tel.cadid and tel.cadtipo = 3\n"
                    + "order by\n"
                    + "    f.id")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setAtivo(rs.getInt("situacao") == 2 ? false : true);
                    if (rs.getString("observacao") != null && !"".equals(rs.getString("observacao"))) {
                        imp.setObservacao(rs.getString("observacao"));
                    }
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setDatacadastro(rs.getDate("datahorac"));
                    imp.setPrazoVisita(rs.getInt("visita"));
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));
                    imp.setEndereco(rs.getString("logradouro") + " " + rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("municipioibge"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    if (rs.getString("contato") != null && !"".equals(rs.getString("contato"))) {
                        imp.addContato("1", rs.getString("contato"), null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    if (rs.getString("telefone") != null && !"".equals(rs.getString("telefone"))) {
                        imp.addContato("2", rs.getString("tipocontato") == null ? "SEM NOME" : rs.getString("tipocontato"), rs.getString("telefone"), null, TipoContato.COMERCIAL, null);
                    }

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
                    + "    c.id,\n"
                    + "    0 tipo,\n"
                    + "    c.descritivo razaosocial,\n"
                    + "    c.fantasia,\n"
                    + "    c.cpf cnpj,\n"
                    + "    c.rg ie,\n"
                    + "    '' im,\n"
                    + "    c.observacao,\n"
                    + "    c.situacao,\n"
                    + "    c.sexo,\n"
                    + "    c.datahorac,\n"
                    + "    c.datanascimento,\n"
                    + "    e.endereco,\n"
                    + "    e.logradouro,\n"
                    + "    e.bairro,\n"
                    + "    e.numero,\n"       
                    + "    e.cidade municipioibge,\n"
                    + "    e.cep,\n" 
                    + "    e.complemento,\n"        
                    + "    ci.descritivo municipio,\n"
                    + "    ci.estado,\n"
                    + "    t.telefones,\n"
                    + "    valorlimite.valor valorlimite,\n"
                    + "    em.descritivo contatoemail,\n"
                    + "    em.endereco email\n"
                    + "from\n"
                    + "    clientef c\n"
                    + "left join\n"
                    + "    endereco e on c.id = e.cadid and\n"
                    + "    e.idtipo = 1 and e.cadtipo = 0\n"
                    + "left join \n"
                    + "    cidade ci on e.cidade = ci.id\n"
                    + "left join \n"
                    + "    fn_view_telefones() t(cadtipo, cadid, telefones) ON c.id =\n"
                    + "    t.cadid AND t.cadtipo = 0\n"
                    + "left join\n"
                    + "	(select \n"
                    + "		lim.cadid,\n"
                    + "		lim.valor,\n"
                    + "    	lim.fpagto\n"
                    + "	from \n"
                    + "		cliente_limite lim\n"
                    + "	join\n"
                    + "		fpagto f on lim.fpagto = f.id\n"
                    + "	where\n"
                    + "		lim.cadtipo = 0 and\n"
                    + "    	f.id = 7) valorlimite on c.id = valorlimite.cadid\n"
                    + "left join\n"
                    + "    enderecow em on c.id = em.cadid and\n"
                    + "    em.cadtipo = 0\n"
                    + "union all\n"
                    + "select\n"
                    + "    c.id,\n"
                    + "    1 tipo,\n"
                    + "    c.descritivo razaosocial,\n"
                    + "    c.fantasia,\n"
                    + "    c.cnpj,\n"
                    + "    c.ie,\n"
                    + "    c.im,\n"
                    + "    c.observacao,\n"
                    + "    c.situacao,\n"
                    + "    ''::character varying sexo,\n"
                    + "    c.datahorac,\n"
                    + "    NULL::date datanascimento,\n"
                    + "    e.endereco,\n"
                    + "    e.logradouro,\n"
                    + "    e.bairro,\n"
                    + "    e.numero,\n"
                    + "    e.cidade municipioibge,\n"
                    + "    e.cep,\n" 
                    + "    e.complemento,\n"        
                    + "    ci.descritivo municipio,\n"
                    + "    ci.estado,\n"
                    + "    t.telefones telefone,\n"
                    + "    valorlimite.valor valorlimite,\n"
                    + "    em.descritivo contatoemail,\n"
                    + "    em.endereco email\n"
                    + "from\n"
                    + "    clientej c\n"
                    + "left join\n"
                    + "    endereco e on c.id = e.cadid and\n"
                    + "    e.idtipo = 1 and e.cadtipo = 1\n"
                    + "left join\n"
                    + "    cidade ci on e.cidade = ci.id\n"
                    + "left join \n"
                    + "    fn_view_telefones() t(cadtipo, cadid, telefones) ON c.id =\n"
                    + "    t.cadid AND t.cadtipo = 1\n"
                    + "left join\n"
                    + "	(select \n"
                    + "		lim.cadid,\n"
                    + "		lim.valor,\n"
                    + "    	lim.fpagto\n"
                    + "	from \n"
                    + "		cliente_limite lim\n"
                    + "	join\n"
                    + "		fpagto f on lim.fpagto = f.id\n"
                    + "	where\n"
                    + "		lim.cadtipo = 1 and\n"
                    + "    	f.id = 7) valorlimite on c.id = valorlimite.cadid\n"
                    + "left join\n"
                    + "    enderecow em on c.id = em.cadid and\n"
                    + "    em.cadtipo = 1\n"
                    + "order by\n"
                    + "    id")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razaosocial"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setInscricaoMunicipal(rs.getString("im"));
                    if (rs.getString("observacao") != null && !"".equals(rs.getString("observacao"))) {
                        imp.setObservacao(rs.getString("observacao"));
                    }
                    imp.setAtivo(rs.getInt("situacao") == 2 ? false : true);
                    imp.setSexo("F".equals(rs.getString("sexo").trim()) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setDataCadastro(rs.getDate("datahorac"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setEndereco(rs.getString("logradouro") + " " + rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("estado"));
                    if (rs.getString("telefones") != null && !"".equals(rs.getString("telefones"))) {
                        array = new String[3];
                        telefonesCliente = rs.getString("telefones");
                        array = telefonesCliente.split(" / ");
                        int c = 1;
                        for (String telefones : array) {
                            imp.addContato(String.valueOf(c), "TELEFONE " + c, telefones.trim(), null, null);
                            c++;
                        }
                    }
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    if(rs.getString("contatoemail") != null & !"".equals(rs.getString("contatoemail"))) {
                        imp.addContato("EMAIL", rs.getString("contatoemail"), null, null, rs.getString("email"));
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
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	c.id, \n" +
                    "	c.idreferencia,\n" +
                    "	cf.cpf,\n" +
                    "	cj.cnpj,\n" +
                    "	c.emissao, \n" +
                    "	c.cadid idcliente, \n" +
                    "	c.vencimento, \n" +
                    "	c.valor, \n" +
                    "	c.parcela, \n" +
                    "	c.ecf \n" +
                    "from \n" +
                    "	contasr c \n" +
                    "left join\n" +
                    "	clientef cf on c.cadid = cf.id\n" +
                    "left join\n" +
                    "	clientej cj on c.cadid = cj.id\n" +
                    "where \n" +
                    "	c.pagamento is null\n" +
                    "order by \n" +
                    "	c.emissao")) {
                while(rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    if(rs.getString("cnpj") == null) {
                        imp.setCnpjCliente(rs.getString("cpf"));
                    } else {
                        imp.setCnpjCliente(rs.getString("cnpj"));
                    }
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setEcf(rs.getString("ecf"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
