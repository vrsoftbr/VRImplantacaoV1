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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GatewaySistemasDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Gateway Sistemas";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	l.COD_EMPRESA AS id, \n"
                    + "	(l.NOME||' - '||l.CNPJ) AS empresa \n"
                    + "FROM EMITENTE l \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("empresa")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "	(et.ST||'-'||et.ICMS||'-'||et.REDUCAO||'-'||et.ALIQ_FCP) AS id,\n"
                    + "	et.ST AS cst,\n"
                    + "	et.ICMS AS icms,\n"
                    + "	et.REDUCAO AS reducao,\n"
                    + "	et.ALIQ_FCP AS fcp\n"
                    + "FROM ESTOQUE e\n"
                    + "LEFT JOIN EST_TRIBUTACAO et ON e.CODIGO = et.CODIGO\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("id"),
                            rst.getInt("cst"),
                            Double.parseDouble(rst.getString("icms").replace(",", ".")),
                            rst.getDouble("reducao"),
                            rst.getDouble("fcp"),
                            false,
                            0
                    )
                    );
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
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
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    if (rst.getString("ean") != null && !rst.getString("ean").trim().isEmpty()) {

                        if (rst.getString("ean").trim().length() <= 6 && rst.getString("ean").startsWith("20")) {

                            imp.setEan(rst.getString("ean").trim().substring(1));
                        } else {
                            imp.setEan(rst.getString("ean"));
                        }
                    }

                    if ("Balan".equals(rst.getString("tipo")) || "Peso".equals(rst.getString("tipo"))) {
                        imp.seteBalanca(true);
                    } else {
                        imp.seteBalanca(false);
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagemcotacao"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpis"));
                    imp.setPiscofinsCstCredito(rst.getString("cstcofins"));

                    imp.setIcmsDebitoId(rst.getString("idIcms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idIcms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idIcms"));
                    imp.setIcmsCreditoId(rst.getString("idIcms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idIcms"));
                    imp.setIcmsConsumidorId(rst.getString("idIcms"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                    + "	f.SITE AS site\n"
                    + "FROM FORNECEDORES f \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if (rst.getString("cnpj") != null && !rst.getString("cnpj").trim().isEmpty()) {
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    }

                    if (rst.getString("ie_rg") != null && !rst.getString("ie_rg").trim().isEmpty()) {
                        imp.setIe_rg(rst.getString("ie_rg"));
                    } else {
                        imp.setIe_rg(rst.getString("rg"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if (rst.getString("celular") != null && !rst.getString("celular").trim().isEmpty()) {
                        imp.addCelular("CELULAR", rst.getString("celular"));
                    }
                    if (rst.getString("fax") != null && !rst.getString("fax").trim().isEmpty()) {
                        imp.addTelefone("FAX", rst.getString("Fax"));
                    }
                    if (rst.getString("email") != null && !rst.getString("email").trim().isEmpty()) {
                        imp.addEmail("EMAIL", rst.getString("email"), TipoContato.COMERCIAL);
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {

                    String[] codigosExternos = rst.getString("codigoexterno").split("\\|");

                    for (int i = 0; i < codigosExternos.length; i++) {

                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setIdProduto(rst.getString("idproduto"));
                        imp.setIdFornecedor(rst.getString("idfornecedor"));
                        imp.setCodigoExterno(codigosExternos[i].trim());
                        imp.setDataAlteracao(rst.getDate("dataalteracao"));

                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));

                    if (rst.getString("cnpj") != null && !rst.getString("cnpj").trim().isEmpty()) {
                        imp.setCnpj(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj(rst.getString("cpf"));
                    }

                    if (rst.getString("inscricaoestadual") != null && !rst.getString("inscricaoestadual").trim().isEmpty()) {
                        imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    } else {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    }

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setMunicipioIBGE(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setBloqueado(rst.getInt("bloqueado") == 1);
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("fax"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setObservacao(rst.getString("obs"));

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
            try (ResultSet rst = stm.executeQuery(
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
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
