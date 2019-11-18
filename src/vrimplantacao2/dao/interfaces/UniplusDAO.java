package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class UniplusDAO extends InterfaceDAO {

    private int prefixoAtacado = 999;
    private String complemento = "";
    private boolean forcarIdProdutoQuandoPesavel = false;

    public void setComplemento(String complemento) {
        this.complemento = complemento != null ? complemento.trim() : "";
    }
    
    public void setPrefixoAtacado(int prefixoAtacado) {
        this.prefixoAtacado = prefixoAtacado;
    }

    public void setForcarIdProdutoQuandoPesavel(boolean forcarIdProdutoQuandoPesavel) {
        this.forcarIdProdutoQuandoPesavel = forcarIdProdutoQuandoPesavel;
    }
    
    @Override
    public String getSistema() {
       return "Uniplus" + ("".equals(this.complemento) ? "" : " - " + this.complemento);
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	codigo,\n" +
                    "	nome,\n" +
                    "	cnpj\n" +
                    "from \n" +
                    "	filial")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome")));
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.CUSTO_COM_IMPOSTO,
            OpcaoProduto.CUSTO_SEM_IMPOSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE
        }));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	id as merc1,\n" +
                    "	nome as descmerc1,\n" +
                    "	id as merc2,\n" +
                    "	nome as descmerc2,\n" +
                    "	id as merc3,\n" +
                    "	nome as descmerc3 \n" +
                    "from \n" +
                    "	hierarquia \n" +
                    "order by\n" +
                    "	id")) {
                while(rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
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
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.id,\n" +
                    "	p.codigo, \n" +
                    "	case when p.pesavel = 1 then p.codigo else coalesce(nullif(trim(p.ean),''), p.codigo) end ean, \n" +
                    "	p.inativo, \n" +
                    "	p.diasvencimento as validade,\n" +
                    "	p.nome as descricaocompleta, \n" +
                    "	p.nomeecf as descricaoreduzida, \n" +
                    "	p.nome as descricaogondola, \n" +
                    "	p.datacadastro, \n" +
                    "	p.unidademedida as unidade, \n" +
                    "	1 qtdembalagem, \n" +
                    "	p.custoindireto custooperacional,\n" +
                    "	p.percentuallucroajustado margemlucro,\n" +
                    "	p.precocusto, \n" +
                    "	p.preco,        \n" +
                    "	preco.percentualmarkupajustado margem, \n" +
                    "	preco.precoultimacompra custosemimposto,\n" +
                    "	preco.precocusto custocomimposto,\n" +
                    "	preco.preco as precovenda,\n" +
                    "	p.quantidademinima, \n" +
                    "	p.quantidademaxima, \n" +
                    "	e.quantidade, \n" +
                    "	p.tributacao, \n" +
                    "	p.situacaotributaria as cst, \n" +
                    "	p.cstpis, \n" +
                    "	p.cstcofins, \n" +
                    "	p.cstpisentrada, \n" +
                    "	p.icmsentrada as icmscredito, \n" +
                    "	p.icmssaida as icmsdebito, \n" +
                    "	p.aliquotaicmsinterna, \n" +
                    "	p.pesavel, \n" +
                    "	p.ncm, \n" +
                    "	p.idcest, \n" +
                    "	cest.codigo as cest, \n" +
                    "	p.cstpisentrada, \n" +
                    "	p.cstpis, \n" +
                    "	p.idfamilia, \n" +
                    "	p.idhierarquia as merc1, \n" +
                    "	p.idhierarquia as merc2, \n" +
                    "	p.idhierarquia as merc3,\n" +
                    "	r.codigo naturezareceita\n" +
                    "from \n" +
                    "	produto p\n" +
                    "	join filial f on\n" +
                    "		f.id = " + getLojaOrigem() + "\n" +
                    "	left join formacaoprecoproduto preco on\n" +
                    "		preco.idproduto = p.id and\n" +
                    "		preco.idfilial = f.id\n" +
                    "	left join saldoestoque e on\n" +
                    "		e.idproduto = p.id and\n" +
                    "		e.codigoproduto = p.codigo and\n" +
                    "		e.idfilial = f.id\n" +
                    "	left join cest on\n" +
                    "		cest.id = p.idcest\n" +
                    "	left join\n" +
                    "		receitasemcontribuicao r on p.idreceitasemcontribuicao = r.id\n" +
                    " order by \n" +
                    "	p.id"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    
                    imp.setSituacaoCadastro(rs.getInt("inativo") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    
                    ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(rs.getString("ean")));
                    if (bal == null) {
                        imp.setEan(rs.getString("ean"));
                        imp.seteBalanca(rs.getBoolean("pesavel"));                    
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setValidade(rs.getInt("validade"));
                    } else {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.setQtdEmbalagem(1);
                        imp.setValidade(rs.getInt(bal.getValidade()));
                    }
                    
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    if((rs.getDouble("precovenda") == 0) && (rs.getDouble("custocomimposto") == 0)) {
                        imp.setCustoSemImposto(rs.getDouble("precocusto"));
                        imp.setCustoComImposto(rs.getDouble("precocusto"));
                        imp.setPrecovenda(rs.getDouble("preco"));
                        imp.setMargem(rs.getDouble("margemlucro"));
                    } else {
                        imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                        imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                        imp.setPrecovenda(rs.getDouble("precovenda"));
                        imp.setMargem(rs.getDouble("margem"));
                    }
                    
                    imp.setEstoqueMinimo(rs.getDouble("quantidademinima"));
                    imp.setEstoqueMaximo(rs.getDouble("quantidademaxima"));
                    imp.setEstoque(rs.getDouble("quantidade"));
                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqEntrada(rs.getDouble("icmscredito"));
                    imp.setIcmsAliqSaidaForaEstado(rs.getDouble("aliquotaicmsinterna"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rs.getDouble("aliquotaicmsinterna"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpisentrada"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setPiscofinsNaturezaReceita(rs.getString("naturezareceita"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {

        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "	codigo,\n"
                        + "	codigo ean,\n"
                        + "	precopauta1 precoatacado,\n"
                        + "	quantidadepauta1 qtdembalagem,\n"
                        + "       preco\n"
                        + "from\n"
                        + "	produto\n"
                        + "where\n"
                        + "	precopauta1 > 0\n"
                        + "union all \n"
                        + "select	\n"
                        + "	p.codigo,\n"
                        + "	p.codigo ean,\n"
                        + "	p.precopauta1 precoatacado,\n"
                        + "	p.quantidadepauta1 qtdembalagem,\n"
                        + "        preco.preco\n"
                        + "from produto p\n"
                        + "join filial f on f.id = " + getLojaOrigem() + "\n"
                        + "left join formacaoprecoproduto preco on preco.idproduto = p.id\n"
                        + "	and preco.idfilial = f.id\n"
                        + "where precopauta1 > 0"
                )) {
                    while (rst.next()) {
                        
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("codigo"));
                        
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo"));
                        imp.setEan(prefixoAtacado + String.valueOf(codigoAtual));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        imp.setPrecovenda(rst.getDouble("preco"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }
    
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	p.codigo idproduto,\n" +
                    "	e.codigo idfornecedor,\n" +
                    "	pf.referenciafornecedor\n" +
                    "from\n" +
                    "	produtofornecedor pf\n" +
                    "join\n" +
                    "	produto p on p.id = pf.idproduto\n" +
                    "join\n" +
                    "	entidade e on e.id = pf.idfornecedor \n" +
                    "where\n" +
                    "	e.fornecedor = 1\n" +
                    "order by\n" +
                    "	pf.idproduto, pf.idfornecedor")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("referenciafornecedor"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.codigo idproduto,\n" +
                    "	ean.ean,\n" +
                    "	1 qtdembalagem \n" +
                    "from \n" +
                    "	produtoean ean\n" +
                    "join\n" +
                    "	produto p on p.id = ean.idproduto\n" +
                    "order by\n" +
                    "	idproduto")) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("idproduto"));
                    if(rs.getString("ean") != null && !"".equals(rs.getString("ean"))) {
                        if(rs.getString("ean").length() > 14) {
                            imp.setEan(rs.getString("ean").substring(0, 14));
                        } else {
                            imp.setEan(rs.getString("ean"));
                        }
                    }
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	e.codigo,\n" +
                    "	e.nome,\n" +
                    "	e.razaosocial,\n" +
                    "	e.tipopessoa,\n" +
                    "	e.cnpjcpf,\n" +
                    "	e.inscricaoestadual,\n" +
                    "	e.rg,\n" +
                    "	e.endereco,\n" +
                    "	e.numeroendereco,\n" +
                    "	e.complemento,\n" +
                    "	e.bairro,\n" +
                    "	e.idestado,\n" +
                    "	est.nome as estado,\n" +
                    "	est.codigoibge as ibgeestado,\n" +
                    "	e.idcidade,\n" +
                    "	c.nome as municipio,\n" +
                    "	c.codigoibge as ibgemunicipio,\n" +
                    "	e.cep,\n" +
                    "	e.telefone,\n" +
                    "	e.celular,\n" +
                    "	e.fax,\n" +
                    "	e.email,\n" +
                    "	e.nascimento,\n" +
                    "	e.limitecredito,\n" +
                    "	e.enderecoentrega,\n" +
                    "	e.numeroenderecoentrega,\n" +
                    "	e.complementoentrega,\n" +
                    "	e.bairroentrega,\n" +
                    "	e.idcidadeentrega,\n" +
                    "	e.cepentrega,\n" +
                    "	e.estadocivil,\n" +
                    "	e.datacadastro,\n" +
                    "	e.inativo\n" +
                    "from\n" +
                    "	entidade e\n" +
                    "left join\n" +
                    "	cidade c on c.id = e.idcidade\n" +
                    "left join\n" +
                    "	estado est on est.id = e.idestado\n" +
                    "where\n" +
                    "	e.fornecedor = 1\n" +
                    "	or e.id in (select distinct identidade from financeiro where tipo = 'P')\n" +
                    "order by\n" +
                    "	e.codigo::integer")) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("razaosocial"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_municipio(rs.getInt("ibgemunicipio"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if((rs.getString("celular") != null) 
                            && (!"".equals(rs.getString("celular")))) {
                        imp.addContato("Celular", null, rs.getString("celular"), TipoContato.COMERCIAL, null);
                    }
                    if((rs.getString("fax") != null) 
                            && (!"".equals(rs.getString("fax")))) {
                        imp.addContato("Fax", rs.getString("fax"), null, TipoContato.COMERCIAL, null);
                    }
                    if((rs.getString("email") != null) 
                            && (!"".equals(rs.getString("email")))) {
                        imp.addContato("Email", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    }
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo((rs.getInt("inativo") == 0));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try(Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	e.codigo,\n" +
                    "	e.nome,\n" +
                    "	e.razaosocial,\n" +
                    "	e.tipopessoa,\n" +
                    "	e.cnpjcpf,\n" +
                    "	e.inscricaoestadual,\n" +
                    "	e.rg,\n" +
                    "	e.endereco,\n" +
                    "	e.numeroendereco,\n" +
                    "	e.complemento,\n" +
                    "	e.bairro,\n" +
                    "	c.codigo municipioibge,\n" +
                    "	c.nome municipio,\n" +
                    "   es.codigoibge estadoibge,\n" +
                    "   es.codigo estado,\n" +
                    "	e.cep,\n" +
                    "	e.telefone,\n" +
                    "	e.celular,\n" +
                    "	e.fax,\n" +
                    "	e.email,\n" +
                    "	e.nascimento,\n" +
                    "	e.limitecredito,\n" +
                    "	e.datacadastro,\n" +
                    "	e.inativo\n" +
                    "from \n" +
                    "	entidade e\n" +
                    "left join\n" +
                    "	cidade c on c.id = e.idcidade\n" +
                    "left join\n" +
                    "	estado es on c.idestado = es.id\n" +
                    "where\n" +
                    "	e.cliente = 1\n" +
                    "order by\n" +
                    "	e.codigo::integer")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("codigo"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numeroendereco"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setMunicipioIBGE(rs.getInt("municipioibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setUfIBGE(rs.getInt("estadoibge"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataNascimento(rs.getDate("nascimento"));
                    imp.setValorLimite(rs.getDouble("limitecredito"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setAtivo((rs.getInt("inativo") == 0));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	f.id,\n" +
                    "	f.emissao,\n" +
                    "	f.documento cupom,\n" +
                    "	0 ecf,\n" +
                    "	f.valor,\n" +
                    "	f.historico observacao,\n" +
                    "	e.codigo id_cliente,\n" +
                    "	f.vencimento,\n" +
                    "	f.parcela,\n" +
                    "	f.juros,\n" +
                    "	f.multa\n" +
                    "from\n" +
                    "	financeiro f\n" +
                    "	join entidade e on\n" +
                    "           f.identidade = e.id\n" +
                    "where\n" +
                    "	f.tipo = 'R'\n" +
                    "	and f.idfilial = " + getLojaOrigem() + "\n" +
                    "	and f.idtipodocumentofinanceiro in (1,8)\n" +
                    "order by\n" +
                    "	f.id"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(complemento);
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    
                    incluirLancamentos(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void incluirLancamentos(CreditoRotativoIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	fl.id,\n" +
                    "	fl.valor,\n" +
                    "	fl.desconto,\n" +
                    "	fl.multa,\n" +
                    "	fl.baixa datapagamento,\n" +
                    "	fl.historico observacao\n" +
                    "from\n" +
                    "	financeirolancamento fl\n" +
                    "where\n" +
                    "	fl.idfinanceiro = " + imp.getId() + "\n" +
                    "order by\n" +
                    "	fl.id"
            )) {
                while (rst.next()) {
                    imp.addPagamento(
                            rst.getString("id"),
                            rst.getDouble("valor"),
                            rst.getDouble("desconto"),
                            rst.getDouble("multa"),
                            rst.getDate("datapagamento"),
                            rst.getString("observacao")
                    );
                }
            }
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	f.id,\n" +
                    "	e.cnpjcpf cpf,\n" +
                    "	f.documento numerocheque,\n" +
                    "	b.codigo banco,\n" +
                    "	f.agencia,\n" +
                    "	f.numerocontacorrente,\n" +
                    "	f.numerocheque,\n" +
                    "	f.emissao date,\n" +
                    "	f.baixa datadeposito,\n" +
                    "	0 ecf,\n" +
                    "	e.rg,\n" +
                    "	e.telefone,\n" +
                    "	e.nome,\n" +
                    "	f.historico observacao,\n" +
                    "	f.valor,\n" +
                    "	f.juros,\n" +
                    "	f.pagamento\n" +
                    "from\n" +
                    "	financeiro f\n" +
                    "	left join entidade e on\n" +
                    "		f.identidade = e.id\n" +
                    "	left join banco b on\n" +
                    "		f.idbanco = b.id\n" +
                    "where\n" +
                    "	f.tipo = 'R'\n" +
                    "	and f.idfilial = " + getLojaOrigem() + "\n" +
                    "	and f.idtipodocumentofinanceiro in (5)\n" +
                    "order by\n" +
                    "	f.id"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("numerocontacorrente"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setDate(rst.getDate("date"));
                    imp.setDataDeposito(rst.getDate("datadeposito"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("nome"));
                    imp.setObservacao("NUM. CHEQUE: " + rst.getString("numerocheque") + "\r\n" + rst.getString("observacao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setValorJuros(rst.getDouble("juros"));
                    if (rst.getString("pagamento") == null || rst.getString("pagamento").trim().equals("")) {
                        imp.setSituacaoCheque(SituacaoCheque.ABERTO);
                    } else {
                        imp.setSituacaoCheque(SituacaoCheque.BAIXADO);
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	f.id,\n" +
                    "	e.codigo identidade,\n" +
                    "	f.documento,\n" +
                    "	f.emissao,\n" +
                    "	f.entrada,\n" +
                    "	f.historico observacao,\n" +
                    "	doc.descricao tipodocumento,\n" +
                    "	f.vencimento,\n" +
                    "	f.valor,\n" +
                    "	f.saldo\n" +
                    "from\n" +
                    "	financeiro f\n" +
                    "	join entidade e on\n" +
                    "		f.identidade = e.id\n" +
                    "	left join tipodocumentofinanceiro doc on\n" +
                    "		f.idtipodocumentofinanceiro = doc.id\n" +
                    "where\n" +
                    "	f.tipo = 'P'\n" +
                    "	and f.idfilial = 1\n" +
                    "	and (select sum(valor) from financeirolancamento where idfinanceiro = f.id) < f.valor\n" +
                    "order by\n" +
                    "	f.id"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("identidade"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setIdTipoEntradaVR(210);
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.setObservacao(
                            new StringBuilder(rst.getString("tipodocumento"))
                                    .append(rst.getDouble("saldo") > 0 ? " - Valor original RS" + rst.getDouble("valor"): "")
                                    .append(" - ")
                                    .append(rst.getString("observacao"))
                                    .toString()
                    );
                    imp.addVencimento(
                            rst.getDate("vencimento"), 
                            (rst.getDouble("saldo") > 0 ? rst.getDouble("saldo") : rst.getDouble("valor"))
                    );
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
