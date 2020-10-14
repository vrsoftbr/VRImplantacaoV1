/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class STSitemasDAO extends InterfaceDAO implements MapaTributoProvider {

    public String compLoja = "";

    @Override
    public String getSistema() {
        if (!compLoja.trim().isEmpty()) {
            return "STSitemas - " + compLoja;
        } else {
            return "STSitemas";
        }

    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	local as id,\n"
                    + "	concat(RAZAO, ' - ', \n"
                    + "	CGCCPF) as descricao\n"
                    + "from LOCALEST\n"
                    + "order by 1\n"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	t.GRFATURA as id,\n"
                    + "	t.DESCRICAO as descricao,\n"
                    + "	t.CODTRIBUTA as cst,\n"
                    + "	t.ICMS as icms,\n"
                    + "	t.REDUCAOBAS as reducao\n"
                    + "from GRFATURA t \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("icms"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.DESCONTINUADO,
                    OpcaoProduto.ATACADO
                }
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.setor as merc1,\n"
                    + "	m1.descricao as desc_merc1,\n"
                    + "	m2.GRUPO as merc2,\n"
                    + "	m2.DESCRICAO as desc_merc2,\n"
                    + "	'1' as merc3,\n"
                    + "	m2.DESCRICAO as desc_merc3\n"
                    + "from SETOR m1\n"
                    + "join GRUPOS m2 on m2.SETOR = m1.SETOR\n"
                    + "order by 1, 3")) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.ITEM as id,\n"
                    + "	p.CODBARRAS as ean,\n"
                    + "	case p.UsaBalanca when 'S' then 1 else 0 end balanca,\n"
                    + "	CONCAT(p.DESCRICAO, ' ', p.EMBALAGEM)  as descricaocompleta,\n"
                    + "	case p.EXCLUIDO when 'S' then 0 else 1 end situacaocadastro,\n"
                    + "	p.UNIDADE as tipoembalagem,\n"
                    + "	p.PESOBRUTO as pesobruto,\n"
                    + "	p.PESOLIQUIDO as pesoliquido,\n"
                    + "	g.SETOR as mercadologico1,\n"
                    + "	g.GRUPO as mercadologico2,\n"
                    + "	'1' as mercadologico3,\n"
                    + "	g.DESCRICAO as descricaomercadologico,\n"
                    + " p.ESTMIN as estoqueminimo, \n"
                    + " p.ESTMAX as estoquemaximo, \n"
                    + "	e.ULTESTOQUE as estoque,\n"
                    + "	coalesce(e.MargemPrcFututo1, 0) as margem,\n"
                    + "	coalesce(e.PrcFuturo1, 0) as precovenda,\n"
                    + "	coalesce(e.CustoRep, 0) as custo,\n"
                    + "	p.COD_NCM as ncm,\n"
                    + "	p.cest as cest,\n"
                    + "	p.PisCofins,\n"
                    + "	p.GRFATURA as icms_id,"
                    + "	pis.CST_Pis,\n"
                    + "	pis.Cst_Pis_Ent,\n"
                    + "	pis.Nat_Rec\n"
                    + "from ITENS p\n"
                    + "left join ESTOQUE e on e.ITEM = p.ITEM\n"
                    + "	and e.LOCAL = " + getLojaOrigem() + "\n"
                    + "left join GRUPOS g on g.GRUPO = p.GRUPO\n"
                    + "left join ProTools_PisCofins pis on pis.Codigo = p.PisCofins\n"
                    + "where p.ITEM != 0\n"
                    + "order by 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));

                    long codigoProduto;
                    codigoProduto = Long.parseLong(imp.getImportId());
                    if (codigoProduto <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) codigoProduto);
                    } else {
                        produtoBalanca = null;
                    }

                    if (produtoBalanca != null) {
                        imp.seteBalanca(true);
                        imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                    } else {
                        imp.setValidade(0);
                        imp.seteBalanca(false);
                    }
                    
                    if ((imp.getEan() != null)
                            && (!imp.getEan().trim().isEmpty())
                            && (imp.getEan().trim().length() <= 6)) {

                        imp.setManterEAN(true);
                    } else {
                        imp.setManterEAN(false);
                    }

                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CST_Pis"));
                    imp.setPiscofinsCstCredito(rst.getString("Cst_Pis_Ent"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("Nat_Rec"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_id"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsConsumidorId(rst.getString("icms_id"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.ITEM as idproduto,\n"
                    + "	p.CODBARRASCAIXA as ean\n"
                    + "from ITENS p\n"
                    + "where p.CODBARRASCAIXA != ''"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(1);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String codigoBarras;

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "	e.ITEM as idproduto,\n"
                        + "	coalesce(e.PrcVenda, 0) as precoatacado, \n"
                        + "	coalesce(e.PrcFuturo1, 0) as precovenda \n"
                        + "from ESTOQUE e\n"
                        + "where coalesce(e.PrcVenda, 0) < coalesce(e.PrcFuturo1, 0) \n"
                        + "and e.LOCAL = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(getSistema(), getLojaOrigem(), rst.getString("idproduto"));

                        codigoBarras = "999999" + String.valueOf(codigoAtual);

                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("idproduto"));
                        imp.setPrecovenda(rst.getDouble("precovenda"));
                        imp.setAtacadoPreco(rst.getDouble("precoatacado"));
                        imp.setQtdEmbalagem(6);
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.FORNEC as id,\n"
                    + "	f.CGC as cnpj,\n"
                    + "	f.INSCEST as inscricaoestadual,\n"
                    + "	f.INSCMUN as inscricaomunicipal,\n"
                    + "	f.RAZAO as razaosocial,\n"
                    + "	f.FANTASIA as nomefantasia,\n"
                    + "	f.ENDERECO as endereco,\n"
                    + "	coalesce(f.Numero, '') as numero,\n"
                    + "	coalesce(f.Complemento, '') as complemento,\n"
                    + "	f.BAIRRO as bairro,\n"
                    + "	f.CIDADE as municipio,\n"
                    + "	f.COD_MUNI as municipio_ibge,\n"
                    + "	f.COD_UF as uf_ibge,\n"
                    + "	f.ESTADO as uf,\n"
                    + "	f.CEP as cep,\n"
                    + "	coalesce(f.DDD, '') as ddd,\n"
                    + "	f.TELEFONE1 as telefoneprincipal,\n"
                    + "	f.TELEFONE2 as telefone2,\n"
                    + "	coalesce(f.TELEFONE3, '') as telefone3,\n"
                    + "	f.Fax as fax,\n"
                    + "	f.FAX1 as fax1,\n"
                    + "	coalesce(f.FAX2, '') as fax2,\n"
                    + "	coalesce(f.EMail, '') as email,\n"
                    + "	f.CONTATO1 as contato\n"
                    + "from FORNEC f\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipio_ibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setIbge_uf(rst.getInt("uf_ibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("ddd") + rst.getString("telefoneprincipal"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {

                        imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
                    }

                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {

                        imp.addTelefone("TELEFONE 3", rst.getString("telefone3"));
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {

                        imp.addTelefone("FAX", rst.getString("fax"));
                    }

                    if ((rst.getString("fax2") != null)
                            && (!rst.getString("fax2").trim().isEmpty())) {

                        imp.addTelefone("FAX 2", rst.getString("fax2"));
                    }

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {

                        imp.addEmail("EMAIL", rst.getString("email"), TipoContato.NFE);
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	FORNECEDOR as idfornecedor,\n"
                    + "	ITEM as idproduto,\n"
                    + "	ITEMFORNEC as codigoexterno,\n"
                    + "	ULTCOMPRA as dataalteracao,\n"
                    + "	coalesce(Quantidade, 1) as qtdembalagem,\n"
                    + "	ULTVALOR as custo\n"
                    + "from FORITEM\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        String obs = "";

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.CLIENTE as id,\n"
                    + "	c.RAZAO as razaosocial,\n"
                    + "	c.FANTASIA as nomefantasia,\n"
                    + "	c.CGCCPF as cnpj,\n"
                    + "	c.INSCESTADUAL as incricaoestadual,\n"
                    + "	c.INSCMUNICIPAL as inscricaomunicipal,\n"
                    + "	c.ENDERECO as endereco,\n"
                    + "	c.BAIRRO as bairro,\n"
                    + "	c.CIDADE as municipio,\n"
                    + "	c.UF as uf,\n"
                    + "	c.CEP as cep,\n"
                    + "	c.CREDITO_TICKET,\n"
                    + "	c.Motivo_Credito,\n"
                    + "	case c.Bloqueado when 'S' then 1 else 0 end bloqueado,\n"
                    + "	c.ObsCred1,\n"
                    + "	c.ObsCred2,\n"
                    + "	c.ObsCred3,\n"
                    + "	c.END_FIS_ENDERE as endereco_fis,\n"
                    + "	c.END_FIS_NUMERO as numero_fis,\n"
                    + "	c.END_FIS_COMPLE as complemento_fis,\n"
                    + "	c.END_FIS_BAIRRO as bairro_fis,\n"
                    + "	c.END_N_FIS_MUNI as municipio_fis,\n"
                    + "	c.END_C_FIS_MUNI as municipio_ibge_fis,\n"
                    + "	c.END_N_FIS_UF as uf_fis,\n"
                    + "	c.END_C_FIS_UF as uf_ibge_fis,\n"
                    + "	c.END_N_FIS_CEP as cep_fis,\n"
                    + "	c.TELEFONE as telefone,\n"
                    + "	c.FAX as fax,\n"
                    + "	c.CEL as celular,\n"
                    + "	c.EMAIL1 as email1,\n"
                    + "	c.EMAIL2 as email2,\n"
                    + "	c.MSN as msn,\n"
                    + "	c.SKYPE as skype,\n"
                    + "	c.VOIP as voip,\n"
                    + "	c.DATAANIVER as datanascimento,\n"
                    + "	c.ESTCIVIL as estadocivil,\n"
                    + "	c.PAI as nomepai,\n"
                    + "	c.MAE as nomemae,\n"
                    + "	c.NOME_CONJ as nomeconjuge,\n"
                    + "	c.END_FIS_FONE as telefone,\n"
                    + "	c.END_FIS_FAX as fax,\n"
                    + "	c.END_COB_ENDERE as endereco_cobranca,\n"
                    + "	c.END_COB_NUMERO as numero_cobranca,\n"
                    + "	c.END_COB_BAIRRO as bairro_cobranca,\n"
                    + "	c.END_N_COB_CEP as cep_cobranca,\n"
                    + "	c.END_COB_FONE as telefone_cobranca,\n"
                    + "	c.END_COB_FAX as fax_cobranca,\n"
                    + "	c.END_N_COB_MUNI as municipio_cobranca,\n"
                    + "	c.END_C_COB_MUNI as municipio_ibge_cobranca,\n"
                    + "	c.END_C_COB_UF as uf_ibge_cobranca,\n"
                    + "	c.END_N_COB_UF as uf_cobranca,\n"
                    + "	c.END_N_COB_CEP as cep_cobranca,\n"
                    + "	c.END_TRA_ENDERE as endereco_trabalho,\n"
                    + "	c.END_TRA_BAIRRO as bairro_trabalho,\n"
                    + "	c.END_TRA_NUMERO as numero_trabalho,\n"
                    + "	c.END_TRA_COMPLE as complemento_trabalho,\n"
                    + "	c.END_C_TRA_MUNI as municipio_ibge_trabalho,\n"
                    + "	c.END_N_TRA_MUNI as municipio_trabalho,\n"
                    + "	c.END_C_TRA_UF as uf_ibge_trabalho,\n"
                    + "	c.END_N_TRA_UF as uf_trabalho,\n"
                    + "	c.END_N_TRA_CEP as cep_trabalho,\n"
                    + "	c.END_TRA_FONE as telefone_trabalho,\n"
                    + "	c.END_TRA_FAX as fax_trabalho,\n"
                    + "	c.END_TRA_CELULA as celular_trabalho,\n"
                    + "	c.PROFISSAO as cargo,\n"
                    + "	c.LIMCREDITO as valorlimite,\n"
                    + "	c.RENDA_BASICA as salario,\n"
                    + "	c.SITUACAO as situacao,\n"
                    + "	c.DATA_CADASTRO as datacadastro,\n"
                    + "	coalesce(c.OBSERVACAO1, '') as obs1,\n"
                    + "	coalesce(c.OBSERVACAO2, '') as obs2\n"
                    + "from CLIENTES c\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nomefantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("incricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setEndereco(rst.getString("endereco_fis"));
                    imp.setBairro(rst.getString("bairro_fis"));
                    imp.setMunicipio(rst.getString("municipio_fis"));
                    //imp.setMunicipioIBGE(rst.getString("municipio_ibge_fis"));
                    imp.setUf(rst.getString("uf_fis"));
                    //imp.setUfIBGE(rst.get("uf_ibge_fis"));
                    imp.setCep(rst.getString("cep_fis"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setBloqueado(rst.getInt("bloqueado") == 1);
                    imp.setAtivo(rst.getInt("situacao") == 1);
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email1") == null ? "" : rst.getString("email1").toLowerCase());
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));

                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cobranca"));
                    //imp.setCobrancaMunicipioIBGE(rst.getInt("municipio_ibge_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    //imp.setCobrancaUfIBGE(rst.getInt("uf_ibge_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setCobrancaTelefone(rst.getString("telefone_cobranca"));

                    imp.setEmpresaEndereco(rst.getString("endereco_trabalho"));
                    imp.setEmpresaNumero(rst.getString("numero_trabalho"));
                    imp.setEmpresaBairro(rst.getString("bairro_trabalho"));
                    imp.setEmpresaMunicipio(rst.getString("municipio_trabalho"));
                    //imp.setEmpresaMunicipioIBGE(rst.getInt("municipio_ibge_trabalho"));
                    imp.setEmpresaUf(rst.getString("uf_trabalho"));
                    //imp.setEmpresaUfIBGE(rst.getInt("uf_ibge_trabalho"));
                    imp.setEmpresaTelefone(rst.getString("telefone_trabalho"));

                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));

                    if ((rst.getString("estadocivil") != null)
                            && (!rst.getString("estadocivil").trim().isEmpty())) {

                        if ("C".equals(rst.getString("estadocivil").trim())) {
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                        } else if ("S".equals(rst.getString("estadocivil").trim())) {
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                        } else {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        }
                    }

                    if ((rst.getString("email2") != null)
                            && (!rst.getString("email2").trim().isEmpty())) {
                        imp.addEmail(rst.getString("email2"), TipoContato.COMERCIAL);
                    }

                    if ((rst.getString("Motivo_Credito") != null)
                            && (!rst.getString("Motivo_Credito").trim().isEmpty())) {

                        obs = "MOTIVO CREDITO - " + rst.getString("Motivo_Credito");
                    }

                    if ((rst.getString("ObsCred1") != null)
                            && (!rst.getString("ObsCred1").trim().isEmpty())) {

                        obs = obs + "OBS CRED 1 - " + rst.getString("ObsCred1");
                    }

                    if ((rst.getString("ObsCred2") != null)
                            && (!rst.getString("ObsCred2").trim().isEmpty())) {

                        obs = obs + "OBS CRED 2 - " + rst.getString("ObsCred2");
                    }

                    if ((rst.getString("ObsCred3") != null)
                            && (!rst.getString("ObsCred3").trim().isEmpty())) {

                        obs = obs + "OBS CRED 3 - " + rst.getString("ObsCred3");
                    }

                    imp.setObservacao(rst.getString("obs1") + " " + rst.getString("obs2"));
                    imp.setObservacao2(obs);

                    result.add(imp);

                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	r.CLIENTE as idcliente,\n"
                    + "	r.NUMDOC as numerocupom,\n"
                    + "	r.EMISSAO as dataemissao,\n"
                    + "	r.VENCTO as datavencimento,\n"
                    + "	r.VALOR as valor,\n"
                    + "	r.OBS as observacao,\n"
                    + "	r.NUMSP as parcela\n"
                    + "from TITLRECH r\n"
                    + "where r.DATAPAG is null\n"
                    + "and r.LOCAL = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("idcliente") + rst.getString("numerocupom") + rst.getString("dataemissao"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setParcela(rst.getInt("parcela"));
                    imp.setObservacao(rst.getString("observacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
