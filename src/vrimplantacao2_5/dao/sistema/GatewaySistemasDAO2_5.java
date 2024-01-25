package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;

/**
 *
 * @author Alan
 */
public class GatewaySistemasDAO2_5 extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Gateway Sistemas";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "	(et.ST||'-'||et.ICMS||'-'||et.REDUCAO||'-'||et.ALIQ_FCP) AS id,\n"
                    + "	et.ST AS cst,\n"
                    + "	et.ICMS AS icms,\n"
                    + "	et.REDUCAO AS reducao,\n"
                    + "	et.ALIQ_FCP AS fcp\n"
                    + "FROM EST_TRIBUTACAO et"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("id"),
                            rs.getInt("cst"),
                            0.0,
                            0.0)
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT \n"
                    + "	g.CODIGO AS merc1,\n"
                    + "	g.DESCRICAO AS desc_merc1,\n"
                    + "	COALESCE(e.SUB_GRUPO, 1) AS merc2,\n"
                    + "	COALESCE(g2.DESCRICAO, g.DESCRICAO) AS desc_merc2,\n"
                    + "	'1' AS merc3,\n"
                    + "	COALESCE(g2.DESCRICAO, g.DESCRICAO) AS desc_merc3\n"
                    + "FROM ESTOQUE e\n"
                    + "LEFT JOIN GRUPOS g ON e.GRUPO = g.CODIGO\n"
                    + "LEFT JOIN GRUPOS g2 ON e.SUB_GRUPO = g2.CODIGO\n"
                    + "WHERE e.GRUPO IS NOT NULL\n"
                    + "ORDER BY 1, 3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(imp.getMerc1ID());
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID(imp.getMerc1ID());
                    imp.setMerc3Descricao(imp.getMerc1Descricao());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	e.CODIGO  AS id,\n"
                    + "	e.BARRAS  AS ean,\n"
                    + "	e.UND_V  AS tipoembalagem\n"
                    + "FROM ESTOQUE e\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	e.CODIGO  AS id,\n"
                    + "	e.BARRAS  AS ean,\n"
                    + "	e.PADRAO_BARRAS AS tipo,\n"
                    + "	e.UND_C  AS tipoembalagemcotacao,\n"
                    + "	e.UND_V  AS tipoembalagem,\n"
                    + "	e.ATIVO AS situacaocadastro,\n"
                    + "	e.NOME AS descricaocompleta,\n"
                    + " e.GRUPO as mercadologico1, \n"
                    + " coalesce(e.SUB_GRUPO, '1') as mercadologico2, \n"
                    + " '1' as mercadologico3, \n"
                    + "	e.QTD AS estoque,\n"
                    + "	e.QTD_MAXIMA AS estoquemaximo,\n"
                    + "	e.QTD_MINIMA AS estoqueminimo,\n"
                    + "	e.PRECO_CUSTO AS custo,\n"
                    + "	e.PRECO_VENDA AS precovenda,\n"
                    + " es.LUCRO_VENDA AS margem,\n"
                    + "	e.PESO_BRUTO AS pesobruto,\n"
                    + "	e.PESO_LIQUIDO AS pesoliquido,\n"
                    + "	e.NCM,\n"
                    + "	et.CEST AS cest,\n"
                    + "	et.TIPO_TRIBUTACAO,\n"
                    + "	et.ST AS cst,\n"
                    + "	et.ICMS AS icms,\n"
                    + "	et.REDUCAO AS reducao,\n"
                    + "	et.MVA AS mva,\n"
                    + "	et.PIS_ST AS cstpis,\n"
                    + "	et.COFINS_ST AS cstcofins,\n"
                    + "	et.ALIQ_FCP AS fcp,\n"
                    + " (et.ST||'-'||et.ICMS||'-'||et.REDUCAO||'-'||et.ALIQ_FCP) AS idIcms\n"
                    + "FROM ESTOQUE e\n"
                    + "LEFT JOIN EST_TRIBUTACAO et ON e.CODIGO = et.CODIGO\n"
                    + "LEFT JOIN EST_SIMULADOR es ON e.CODIGO = es.CODIGO\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagemcotacao"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("icms"));
                    imp.setPiscofinsCstDebito(rst.getString("cstcofins"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    vrimplantacao2.vo.cadastro.ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoembalagemcotacao"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    imp.setIcmsDebitoId(rst.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idicms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idicms"));
                    imp.setIcmsCreditoId(rst.getString("idicms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idicms"));
                    imp.setIcmsConsumidorId(rst.getString("idicms"));

                    result.add(imp);
                }

            }
            return result;
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	f.CODIGO AS id,\n"
                    + "	f.nome AS razao,\n"
                    + "	COALESCE(f.FANTASIA, f.NOME) AS fantasia,\n"
                    + "	f.CNPJ AS cnpj,\n"
                    + "	f.IE AS ie_rg,\n"
                    + "	f.CPF AS cpf,\n"
                    + "	f.RG AS rg,\n"
                    + "	f.ENDERECO AS enderenco,\n"
                    + "	f.NUMERO AS numero,\n"
                    + "	f.COMPLEMENTO AS complemento,\n"
                    + "	f.BAIRRO AS bairro,\n"
                    + "	f.CEP AS cep,\n"
                    + "	f.UF AS uf,\n"
                    + "	f.CIDADE AS municipio,\n"
                    + "	f.COD_CIDADE AS municipioibge,\n"
                    + "	f.TELEFONE AS telefone,\n"
                    + "	f.CELULAR AS celular,\n"
                    + "	f.FAX AS fax,\n"
                    + "	f.EMAIL AS email,\n"
                    + "	f.SITE AS site,\n"
                    + " f.ATIVO AS ativo\n"
                    + "FROM FORNECEDORES f \n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie_rg"));
                    imp.setEndereco(rs.getString("enderenco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("telefone")));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "     ea.COD_FORNECEDOR AS idfornecedor,\n"
                    + "     f.NOME AS descricaofornecedor,\n"
                    + "     ea.CODIGO AS idproduto,\n"
                    + "     p.NOME AS descricaoproduto,\n"
                    + "     ea.COD_FABRICANTE AS codigoexterno,\n"
                    + "     ea.DATA_CADASTRO AS dataalteracao\n"
                    + "FROM EST_ADICIONAIS ea\n"
                    + "JOIN ESTOQUE p ON p.CODIGO = ea.CODIGO \n"
                    + "JOIN FORNECEDORES f ON f.CODIGO = ea.COD_FORNECEDOR \n"
                    + "	AND ea.COD_FORNECEDOR != ''\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));
                    imp.setQtdEmbalagem(1);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	c.CODIGO AS id,\n"
                    + "	c.NOME AS razao,\n"
                    + "	COALESCE(c.FANTASIA, c.NOME) AS fantasia,\n"
                    + "	c.CNPJ AS cnpj,\n"
                    + "	c.IE AS inscricaoestadual,\n"
                    + "	c.CPF AS cpf,\n"
                    + "	c.RG AS rg,\n"
                    + "	c.ATIVO AS ativo,\n"
                    + "	c.ENDERECO AS endereco,\n"
                    + "	c.NUMERO AS numero,\n"
                    + "	c.COMPLEMENTO AS complemento,\n"
                    + "	c.BAIRRO AS bairro,\n"
                    + "	c.CEP AS cep,\n"
                    + "	c.UF AS uf,\n"
                    + "	c.CIDADE AS municipio,\n"
                    + "	c.COD_CIDADE AS municipioibge,\n"
                    + "	c.TELEFONE AS telefone,\n"
                    + "	c.CELULAR AS celular,\n"
                    + "	c.FAX AS fax,\n"
                    + "	c.EMAIL AS email,\n"
                    + "	c.OBSERVACOES AS obs,\n"
                    + "	c.NOME_MAE AS nomemae,\n"
                    + "	c.NOME_PAI AS nomepai,\n"
                    + "	c.CONJUGUE AS nomeconjuge,\n"
                    + "	c.PROFISSAO AS cargo,\n"
                    + "	c.NASCIMENTO AS datanascimento,\n"
                    + "	c.LIMITE_CREDITO AS valorlimite,\n"
                    + "	c.DATA_CADASTRO AS datacadastro,\n"
                    + "	c.BLOQUEADO AS bloqueado\n"
                    + "FROM CLIENTES c \n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual") == null ? rs.getString("rg") : rs.getString("inscricaoestadual"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));

                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	r.CODIGO AS id,\n"
                    + "	substring(r.DOCUMENTO FROM 4) AS numerodocumento,\n"
                    + "	r.EMISSAO AS dataemissao,\n"
                    + "	r.VENCIMENTO AS datavencimento,\n"
                    + "	r.CLIENTE AS idcliente,\n"
                    + "	r.CAIXA AS ecf,\n"
                    + " r.VALOR as valor, \n"
                    + "	r.DESCRICAO AS obs\n"
                    + "FROM RECEBER r\n"
                    + "WHERE r.CLIENTE IS NOT NULL\n"
                    + "AND r.CONVENIO IS NULL\n"
                    + "AND r.VALOR_RECEBIDO  < VALOR\n"
                    + "AND r.RECEBIMENTO IS NULL "
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerodocumento")));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
