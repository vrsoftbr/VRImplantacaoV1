/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class MRC6DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "MRC6";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
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
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " situacaotributariatributacaoID as id,\n"
                    + " descricao,\n"
                    + " situacaotributariatributacaoID as cst\n"
                    + "from \n"
                    + " situacaotributariatributacao"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            0,
                            0
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " fam.codigo as id,\n"
                    + " fam.descricao as descricao\n"
                    + "from produtos prod\n"
                    + "join produtosgrupos fam on fam.codigo = prod.Familiaid\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    //result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " prod.Grupoid as codmerc1,\n"
                    + " merc1.descricao as descmerc1,\n"
                    + " prod.Subgrupoid as codmerc2,\n"
                    + " merc2.descricao as descmerc2,\n"
                    + " prod.Subgrupoid as codmerc3,\n"
                    + " merc2.descricao as descmerc3\n"
                    + "from produtos prod\n"
                    + "left join produtosgrupos merc1 on merc1.codigo = prod.Grupoid\n"
                    + "left join produtosgrupos merc2 on merc2.codigo = prod.Subgrupoid\n"
                    + "order by 1,3,5;"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("codmerc1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("codmerc2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("codmerc3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    //result.add(imp);
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
                    "select distinct\n"
                    + " prod.codigo id,\n"
                    + "	prod.produto descricao,\n"
                    + "	prod.codigodebarras ean,\n"
                    + "	prod.familiaID familiaid,\n"
                    + "	prod.grupoID merc1 ,\n"
                    + "	prod.subgrupoID merc2,\n"
                    + "	prod.subgrupoID merc3,\n"
                    + "	prod.precocusto custo,\n"
                    + "	prod.precotabela precovenda,\n"
                    + "	prod.peso pesoliquido,\n"
                    + "	prod.pesobruto pesobruto,\n"
                    + "	prod.tipo tipo,\n"
                    + "	prod.validade validade,\n"
                    + "	prod.dtcadastro datacadastro,\n"
                    + "	est.estoquereal estoque,\n"
                    + "	prod.exibir ativo,\n"
                    + "	prod.situacaotributariaorigemID,\n"
                    + "	replace(prod.classificacaoID, '.', '') as ncm,\n"
                    + "	tax.codigocest as cest,\n"
                    + "	coalesce(tax.comerciosituacaotributariatributacaoID,\n"
                    + "	0) as cst,\n"
                    + "	coalesce(tax.comerciosituacaotributariatributacaoID,\n"
                    + "	0) as idaliquota\n"
                    + "from\n"
                    + "	produtos prod\n"
                    + "left join estoque est on est.produtoID = prod.codigo\n"
                    + "left join taxasicms tax on tax.produtoid = prod.codigo\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setIdFamiliaProduto(rst.getString("familiaid"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setTipoProduto(rst.getString("tipo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setSituacaoCadastro(rst.getInt("ativo"));
                    imp.setCest(rst.getString("cest"));
                    imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsConsumidorId(rst.getString("idaliquota"));

                    //result.add(imp);
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
                    + " codigo as id,\n"
                    + " codigodebarrasdun14 as ean,\n"
                    + " 'CX' as unidade\n"
                    + " from produtos\n"
                    + " where \n"
                    + "  codigodebarrasdun14 is not NULL \n"
                    + " and \n"
                    + "  codigodebarrasdun14 <> ''"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    //result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.codigo as id,\n"
                    + "	f.nome as razao,\n"
                    + "	f.nomefantasia as fantasia,\n"
                    + "	f.cnpj,\n"
                    + "	f.inscrest as ie_rg,\n"
                    + "	f.inscrmunicipal,\n"
                    + "	f.endereco,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	c.cidade,\n"
                    + "	f.desativado as status,\n"
                    + "	c.codigoibge,\n"
                    + "	f.cep,\n"
                    + "	c.estado,\n"
                    + "	f.telefone1,\n"
                    + "	f.telefone2,\n"
                    + "	f.dtcadastro,\n"
                    + "	f.email\n"
                    + "from\n"
                    + "	fornecedores f\n"
                    + "join cidades c on c.codigo = f.cidadeID\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal(rst.getString("inscrmunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setIbge_municipio(rst.getInt("codigoibge"));
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTel_principal(rst.getString("telefone1"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));

                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addEmail("EMAIL", rst.getString("email").toLowerCase(), TipoContato.NFE);
                    }
                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
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
                    "select\n"
                    + "	pf.fornecedorID as idfornecedor,\n"
                    + "	pf.produtoID as idproduto,\n"
                    + "	p.referencia as codigoexterno\n"
                    + " from\n"
                    + "	produtosfornecedores pf\n"
                    + " join produtos p on p.codigo = pf.produtoID\n"
                    + " join fornecedores f on f.codigo = pf.fornecedorID"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    //result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	a.codigo as id,\n"
                    + "	b.descricao,\n"
                    + "	a.cnpj,\n"
                    + "	a.inscrest as ie_rg,\n"
                    + "	a.nome as razao,\n"
                    + "	a.nomefantasia as fantasia,\n"
                    + "	case\n"
                    + "		when a.ativoinativo = 'A' then 1\n"
                    + "		else 0\n"
                    + "	end as status,\n"
                    + "	a.dtativoinativo,\n"
                    + "	a.endereco,\n"
                    + "	a.complemento,\n"
                    + "	a.bairro,\n"
                    + "	c.codigoibge,\n"
                    + "	c.estado as uf,\n"
                    + "	c.cidade,\n"
                    + "	a.cep,\n"
                    + "	a.estadocivil,\n"
                    + "	a.dtcadastro,\n"
                    + "	a.sexo,\n"
                    + "	a.limitecredito,\n"
                    + "	a.naoliberarcredito,\n"
                    + "	a.telefone1,\n"
                    + "	a.telefone2,\n"
                    + "	a.celular,\n"
                    + "	a.email,\n"
                    + "	a.fax,\n"
                    + "	a.enderecoc,\n"
                    + "	a.complementoc,\n"
                    + "	a.bairroc,\n"
                    + "	d.codigoibge,\n"
                    + "	d.estado as c_uf,\n"
                    + "	d.cidade as c_cidade,\n"
                    + "	a.cepc\n"
                    + "from\n"
                    + "	clientes a\n"
                    + "join clientesgrupos b on b.codigo = a.grupoID\n"
                    + "left join cidades c on c.codigo = a.cidadeid\n"
                    + "left join cidades d on d.codigo = a.cidadecID;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setMunicipioIBGE(rst.getInt("codigoibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("limitecredito"));
                    imp.setPermiteCreditoRotativo(rst.getBoolean("naoliberarcredito"));

                    imp.setCobrancaBairro(rst.getString("bairroc"));
                    imp.setCobrancaCep(rst.getString("cepc"));
                    imp.setEndereco(rst.getString("enderecoc"));
                    imp.setCobrancaMunicipio(rst.getString("c_cidade"));
                    imp.setCobrancaUf(rst.getString("c_uf"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("codigoibge"));
                    imp.setCobrancaComplemento(rst.getString("complementoc"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addTelefone("TELEFONE 2", rst.getString("telefone2"));
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
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " dupliad.duplicatainfoadicionalID as id,\n"
                    + " dup.duplicata as numerodocumento,\n"
                    + " dupliad.valor as valor,\n"
                    + " dup.clientefornecedorID as fornecedorid,\n"
                    + " dup.dtcadastro as emissao,\n"
                    + " dup.dtvencimento as vencimento,\n"
                    + " dup.descricao as obs,\n"
                    + " dup.numerodocumento\n"
                    + "from duplicatasinfoadicionais dupliad\n"
                    + "join duplicatas dup on dup.codigo = dupliad.codigoID \n"
                    + "where \n"
                    + "dup.flagclientefornecedor = 'F'\n"
                    + "and\n"
                    + "dup.dtbaixa is null \n"
                    + "and\n"
                    + "dupliad.dtefetivopagamento is null\n"
                    + "and\n"
                    + "dup.tipo In ('P','E','T','M')"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setVencimento(rst.getDate("vencimento"));

                    //result.add(imp);
                }
            }
        }
        return result;
    }
}
