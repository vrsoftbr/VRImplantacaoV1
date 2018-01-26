package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoDB2;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class CissDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "CISS";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT distinct\n"
                    + "    s.idsecao id_merc1,\n"
                    + "    s.descrsecao merc1,\n"
                    + "    g.idgrupo id_merc2,\n"
                    + "    g.descrgrupo merc2,\n"
                    + "    sub.idsubgrupo id_merc3,\n"
                    + "    sub.descrsubgrupo merc3\n"
                    + "from\n"
                    + "	dba.produto	p\n"
                    + "    left join DBA.secao as s on s.idsecao = p.idsecao\n"
                    + "    left join DBA.grupo  as g on g.idgrupo = p.idgrupo\n"
                    + "    left join DBA.subgrupo  as sub on sub.idsubgrupo = p.idsubgrupo\n"
                    + "order by id_merc1,id_merc2,id_merc3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("id_merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1"));
                    imp.setMerc2ID(rst.getString("id_merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2"));
                    imp.setMerc3ID(rst.getString("id_merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "        pg.idproduto,\n"
                    + "        p.DESCRCOMPRODUTO,\n"
                    + "        count(*)\n"
                    + "from\n"
                    + "        dba.produto_grade pg\n"
                    + "        join dba.PRODUTO p on\n"
                    + "                pg.idproduto = p.idproduto\n"
                    + "where\n"
                    + "        p.tipobaixamestre = 'I'\n"
                    + "group by pg.idproduto, p.descrcomproduto\n"
                    + "having count(*) > 1\n"
                    + "order by\n"
                    + "        pg.idproduto"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setDescricao(rst.getString("DESCRCOMPRODUTO"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ean.idsubproduto id,\n"
                    + "	ean.dtcadastro datacadastro,\n"
                    + "	bar.ean,\n"
                    + "	p.embalagemsaida tipoembalagem,\n"
                    + "	case p.flagexpbalanca when 'F' then 0 else 1 end ebalanca,\n"
                    + "	ean.diasvalidade validade,\n"
                    + "	p.descrcomproduto || coalesce(' ' || ean.subdescricao, '') descricaocompleta,\n"
                    + "	ean.descrresproduto descricaoreduzida,\n"
                    + "	p.descrcomproduto || coalesce(' ' || ean.subdescricao, '') descricaogondola,\n"
                    + "	p.idsecao as mercadologico1,\n"
                    + "	p.idgrupo as mercadologico2,\n"
                    + "	p.idsubgrupo as mercadologico3,\n"
                    + "	ean.idproduto as id_familiaproduto,\n"
                    + "	case ean.flaginativo when 'F' then 1 else 0 end as situacaocadastro,\n"
                    + "	ean.pesobruto,\n"
                    + "	ean.pesoliquido,\n"
                    + "	pc.qtdestminimo estoqueminimo,\n"
                    + "	pc.qtdestmaximo estoquemaximo,\n"
                    + "	est.qtdatualestoque estoque,\n"
                    + "	preco.permargemvarejo margem,\n"
                    + "	ean.ncm,\n"
                    + "	ean.codcest cest,\n"
                    + "	preco.valprecovarejo preco,\n"
                    + "	custo.custonotafiscal custosemimposto,\n"
                    + "	custo.custoultimacompra custocomimposto,\n"
                    + "	p.idcstpiscofinssaida piscofinssaida,\n"
                    + "	p.idcstpiscofinsentrada piscofinsentrada,\n"
                    + "	nat.idcodnatureza as tipoNaturezaReceita,\n"
                    + "	trib.idsittribsai as icmsCstDebito,\n"
                    + "	trib.idsittribent as icmsCstCredito,\n"
                    + "	trib.pericment as icmsAliqCredito,\n"
                    + "	trib.pericmsai as icmsAliqDebito,\n"
                    + "	trib.perredtribsai as icmsPercReducaoSaida,\n"
                    + "	trib.perredtribent as icmsPercReducaoEntrada,\n"
                    + "	trib.permargemsubsti as percSubst\n"
                    + "from\n"
                    + "	dba.produto_grade ean\n"
                    + "	join (\n"
                    + "		select\n"
                    + "			pg.idproduto,\n"
                    + "			pg.idsubproduto,\n"
                    + "			pg.idcodbarprod ean,\n"
                    + "			1 as qtd\n"
                    + "		from\n"
                    + "			dba.produto_grade pg\n"
                    + "		union\n"
                    + "		select\n"
                    + "			cx.idproduto,\n"
                    + "			cx.idsubproduto,\n"
                    + "			cx.idcodbarcx ean,\n"
                    + "			cx.qtdmultipla qtd\n"
                    + "		from\n"
                    + "			dba.produto_grade_codbarcx cx\n"
                    + "	) bar on bar.idproduto = ean.idproduto and bar.idsubproduto = ean.idsubproduto\n"
                    + "	join dba.produto p on ean.idproduto = p.idproduto\n"
                    + "	join dba.empresa emp on emp.idempresa = " + getLojaOrigem() + "\n"
                    + "	left join dba.produto_compras pc on \n"
                    + "		pc.idproduto = ean.idproduto and\n"
                    + "		pc.idsubproduto = ean.idsubproduto and\n"
                    + "		pc.idempresa = emp.idempresa\n"
                    + "	left join dba.estoque_saldo_atual est on\n"
                    + "		est.idproduto = ean.idproduto and\n"
                    + "		est.idsubproduto = ean.idsubproduto and\n"
                    + "		est.idempresa = emp.idempresa and\n"
                    + "		est.IDLOCALESTOQUE = 1\n"
                    + "	left join DBA.POLITICA_PRECO_PRODUTO preco on\n"
                    + "		preco.idproduto = ean.idproduto and\n"
                    + "		preco.idsubproduto = ean.idsubproduto and\n"
                    + "		preco.idempresa = emp.idempresa\n"
                    + "	left join dba.produto_grade_custo_view custo on\n"
                    + "		custo.idproduto = ean.idproduto and\n"
                    + "		custo.idsubproduto = ean.idsubproduto and\n"
                    + "		custo.idempresa = emp.idempresa\n"
                    + "	left join DBA.PRODUTO_TRIBUTACAO_VW trib on\n"
                    + "		trib.idproduto = ean.idproduto and\n"
                    + "		trib.idsubproduto = ean.idsubproduto and\n"
                    + "		trib.uf = emp.UF and \n"
                    + "		trib.uforigem = emp.uf\n"
                    + "	left join dba.piscofins_codigo_natureza_receita nat on\n"
                    + "           nat.idnaturezapiscofins = p.IDNATUREZAPISCOFINS\n"
                    + "order by ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setIdFamiliaProduto(rst.getString("id_familiaproduto"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofinsentrada"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofinssaida"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("tipoNaturezaReceita"));
                    imp.setIcmsCstSaida(rst.getInt("icmsCstDebito"));
                    imp.setIcmsCstEntrada(rst.getInt("icmsCstCredito"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icmsAliqCredito"));
                    imp.setIcmsAliqSaida(rst.getDouble("icmsAliqDebito"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icmsPercReducaoSaida"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icmsPercReducaoEntrada"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.QTD_EMBALAGEM_EAN) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "idcodbarcx as ean, "
                        + "idsubproduto as id_produto, "
                        + "qtdmultipla as qtdembalagem\n"
                        + "from dba.PRODUTO_GRADE_CODBARCX "
                        + "where idcodbarcx > 999999"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id_produto"));
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        Map<String, Estabelecimento> result = new LinkedHashMap<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select idempresa, razaosocial from dba.empresa order by idempresa"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("idempresa"), new Estabelecimento(rst.getString("idempresa"), rst.getString("razaosocial")));
                }
            }
        }

        return new ArrayList<>(result.values());
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.idclifor id,\n"
                    + "	f.NOME razao,\n"
                    + "	f.NOMEFANTASIA fantasia,\n"
                    + "	f.CNPJCPF cnpj,\n"
                    + "	f.INSCRESTADUAL inscricaoestadual,\n"
                    + "	f.FONE1,\n"
                    + "	f.ENDERECO,\n"
                    + "	f.BAIRRO,\n"
                    + "	f.NUMERO,\n"
                    + "	f.COMPLEMENTO,\n"
                    + "	f.IDCEP cep,\n"
                    + "	f.OBSGERAL observacao,\n"
                    + "	c.codigoibge id_municipio,\n"
                    + "	c.descrcidade,\n"
                    + "	c.uf id_estado,\n"
                    + "	f.dtcadastro datacadastro,\n"
                    + "	case f.idsituacao when 4 then 0 else 1 end id_situacaoCadastro,\n"
                    + "	f.FONE2,\n"
                    + "	f.FONEFAX fax,\n"
                    + "	f.EMAIL, f.tipofisicajuridica \n"
                    + "from \n"
                    + "	dba.cliente_fornecedor f\n"
                    + "	left join dba.cidades_ibge c on f.idcidade = c.idcidade\n"
                    + "where\n"
                    + "	f.TIPOCADASTRO in ('A','F')\n"
                    + "order by f.idclifor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setTel_principal(rst.getString("FONE1"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIbge_municipio(rst.getInt("id_municipio"));
                    imp.setMunicipio(rst.getString("descrcidade"));
                    imp.setUf(rst.getString("id_estado"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setAtivo(rst.getBoolean("id_situacaoCadastro"));
                    if (!"".equals(Utils.acertarTexto(rst.getString("FONE2")))) {
                        imp.addContato("A", "FONE2", rst.getString("FONE2"), "", TipoContato.COMERCIAL, "");
                    }
                    if (!"".equals(Utils.acertarTexto(rst.getString("fax")))) {
                        imp.addContato("B", "FAX", rst.getString("fax"), "", TipoContato.COMERCIAL, "");
                    }
                    if (!"".equals(Utils.acertarTexto(rst.getString("EMAIL")))) {
                        imp.addContato("C", "EMAIL", "", "", TipoContato.COMERCIAL, rst.getString("EMAIL"));
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

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pf.idclifor id_fornecedor,\n"
                    + "	pf.idsubproduto id_produto,\n"
                    + "	pf.CODIGOINTERNOFORN codigoexterno,\n"
                    + "	f.ufclifor uf,\n"
                    + "	f.CNPJCPF cnpj\n"
                    + "from\n"
                    + "	dba.PRODUTO_FORNECEDOR pf\n"
                    + "	join dba.CLIENTE_FORNECEDOR f on\n"
                    + "		pf.idclifor = f.idclifor\n"
                    /*+ "where\n"
                    + "	f.tipocadastro = 'F'\n"*/
                    + "order by\n"
                    + "	id_fornecedor,\n"
                    + "	id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
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

        try (Statement stm = ConexaoDB2.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "        c.idclifor id,\n"
                    + "        c.NOME razao,\n"
                    + "        c.NOMEFANTASIA fantasia,\n"
                    + "        c.CNPJCPF cnpj,\n"
                    + "        c.INSCRESTADUAL inscricaoestadual,\n"
                    + "        c.FONE1,\n"
                    + "        c.ENDERECO,\n"
                    + "        c.BAIRRO,\n"
                    + "        c.NUMERO,\n"
                    + "        c.COMPLEMENTO,\n"
                    + "        c.IDCEP cep,\n"
                    + "        c.OBSGERAL observacao,\n"
                    + "        cid.codigoibge id_municipio,\n"
                    + "        cid.descrcidade,\n"
                    + "        cid.uf id_estado,\n"
                    + "        c.dtcadastro datacadastro,\n"
                    + "        c.fonecelular,\n"
                    + "        c.vallimitecredito,\n"
                    + "        c.vallimiteconvenio,\n"
                    + "        coalesce(conv.diavencimento,0) diavencimento,\n"
                    + "        c.tipofisicajuridica,\n"
                    + "        case c.idsituacao when 4 then 0 else 1 end id_situacaoCadastro,\n"
                    + "        c.FONE2,\n"
                    + "        c.FONEFAX fax,\n"
                    + "        c.EMAIL\n"
                    + "from\n"
                    + "        dba.cliente_fornecedor c\n"
                    + "        left join dba.cidades_ibge cid on cid.idcidade = c.idcidade\n"
                    + "        left join dba.cliente_convenio conv on c.idconvenio = conv.idconvenio"//\n" +
            //"where\n" +
            //"        c.TIPOCADASTRO in ('A','C')"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setTelefone(rst.getString("FONE1"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setCep(rst.getString("cep"));
                    imp.setObservacao2(rst.getString("observacao"));
                    imp.setMunicipioIBGE(rst.getInt("id_municipio"));
                    imp.setMunicipio(rst.getString("descrcidade"));
                    imp.setUf(rst.getString("id_estado"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCelular(rst.getString("fonecelular"));
                    imp.setValorLimite(rst.getDouble("vallimitecredito") + rst.getDouble("vallimiteconvenio"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setAtivo(rst.getBoolean("id_situacaoCadastro"));
                    imp.setCelular(rst.getString("FONE2"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("EMAIL"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

}
