/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Michael
 */
public class FacilDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "FACIL";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
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
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select DISTINCT \n"
                    + "	p.ALIQUOTAS_ID id,\n"
                    + "	a.ALIQUOTA_LEGENDA descricao,\n"
                    + "    p.CST_ID cst_icms,\n"
                    + "    a.ALIQUOTA_VALOR aliq_icms,\n"
                    + "    p.PRODUTO_ICMS_RED red_icms\n"
                    + "from \n"
                    + "	PRODUTOS p \n"
                    + "	JOIN ALIQUOTAS a ON p.ALIQUOTAS_ID = a.ALIQUOTAS_ID "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "s.SUBGRUPOS_ID as merc1,\n"
                    + "s.SUBGRUPO_NOME merc1_descricao\n"
                    + "from SUBGRUPOS s "
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "s.SUBGRUPOS_ID as merc1,\n"
                    + "s.GRUPOS_ID as merc2,\n"
                    + "g.GRUPO_NOME as merc2_descricao\n"
                    + "from SUBGRUPOS s\n"
                    + "JOIN GRUPOS g ON s.GRUPOS_ID = g.GRUPOS_ID "
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2"),
                                rst.getString("merc2_descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "s.SUBGRUPOS_ID as merc1,\n"
                    + "s.GRUPOS_ID as merc2,\n"
                    + "'1' as merc3,\n"
                    + "g.GRUPO_NOME as merc3_descricao\n"
                    + "from SUBGRUPOS s\n"
                    + "JOIN GRUPOS g ON s.GRUPOS_ID = g.GRUPOS_ID  "
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    /*
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select 
                        s.SUBGRUPOS_ID as merc1,
                        s.SUBGRUPO_NOME AS merc1_descricao,
                        s.GRUPOS_ID as merc2,
                        g.GRUPO_NOME AS merc2_descricao,
                        g.GRUPOS_ID AS merc3,
                        g.GRUPO_NOME as merc3_descricao
                        from SUBGRUPOS s
                        JOIN GRUPOS g ON s.GRUPOS_ID = g.GRUPOS_ID "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    p.PRODUTOS_ID id_produto, \n"
                    + "    p.PRODUTO_CODIGO_BARRAS codigo_barras,\n"
                    + "    u.UNIDADE_NOME unidade,\n"
                    + "    '1' as qtdembalagem,\n"
                    + "    p.PRODUTO_NOME as descricao, \n"
                    + "    CASE \n"
                    + "    	WHEN p.PRODUTO_STATUS = 2 OR p.PRODUTO_STATUS = 4\n"
                    + "    	THEN 0\n"
                    + "    	ELSE 1\n"
                    + "    END situacao,\n"
                    + "    p.FAMILIAS_ID familia, \n"
                    + "    u.UNIDADE_NOME as tipoembalagem,\n"
                    + "    p.SUBGRUPOS_ID as mercadologico,\n"
                    + "    p.PRODUTO_CADASTRO as datacadastro,\n"
                    + "    p.PRODUTO_PRECO_CUSTO preco_custo,\n"
                    + "    p.PRODUTO_CUSTO_MEDIO custo_medio,\n"
                    + "    p.PRODUTO_PRECO preco_venda,\n"
                    + "    p.PRODUTO_PIS as pis,\n"
                    + "    p.PRODUTO_COFINS cofins,\n"
                    + "    p.CST_PIS_ID_ENTRADA idpis,\n"
                    + "    p.CST_COFINS_ID_ENTRADA idcofins,\n"
                    + "    p.CODIGO_NATUREZA_PISCOFINS_ID as naturezareceita,\n"
                    + "    p.NCM_NCM as ncm,\n"
                    + "    p.PRODUTO_CEST as cest,\n"
                    + "    p.PRODUTO_MARGEM_LUCRO margem,\n"
                    + "    CASE\n"
                    + "    	WHEN p.PRODUTO_STATUS = 3 \n"
                    + "    	THEN 1\n"
                    + "    	ELSE 0\n"
                    + "    END balanca,\n"
                    + "    P.PRODUTO_ESTOQUE as estoque,\n"
                    + "    P.PRODUTO_ESTOQUE_MINIMO estoque_minimo,\n"
                    + "    p.PRODUTO_ESTOQUE_MAXIMO estoque_maximo,\n"
                    + "    p.CST_ID cst_icms,\n"
                    + "    p.ALIQUOTAS_ID icmsid,\n"
                    + "    p.PRODUTO_ICMS_RED red_icms\n"
                    + "from PRODUTOS p \n"
                    + "LEFT JOIN UNIDADES u ON p.UNIDADES_ID = u.UNIDADES_ID"
            )) {
                while (rst.next()) {
                    Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigo_barras"));

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setIdFamiliaProduto(rst.getString("familia"));

                    imp.setCodMercadologico1(rst.getString("mercadologico"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setCustoMedioComImposto(rst.getDouble("custo_medio"));
                    imp.setCustoMedioSemImposto(rst.getDouble("custo_medio"));
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("idpis"));
                    imp.setPiscofinsCstCredito(rst.getString("idcofins"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    imp.setIcmsConsumidorId(rst.getString("icmsid"));
                    imp.setIcmsDebitoId(rst.getString("icmsid"));
                    imp.setIcmsCreditoId(rst.getString("icmsid"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icmsid"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icmsid"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icmsid"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id_produto"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("codigo_barras"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	FAMILIAS_ID id,\n"
                    + "	FAMILIA_NOME descricao\n"
                    + "FROM\n"
                    + "	FAMILIAS"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
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
                    "select\n"
                    + "f.FORNECEDORES_ID as id,\n"
                    + "f.FORNECEDOR_NOME as razao,\n"
                    + "f.FORNECEDOR_NOME1 as fantasia,\n"
                    + "f.FORNECEDOR_CPF_CNPJ cnpj,\n"
                    + "f.FORNECEDOR_RG_IE ie,\n"
                    + "f.FORNECEDOR_CADASTRO as datacadastro,\n"
                    + "f.FORNECEDOR_ENDERECO1 as endereco,\n"
                    + "f.FORNECEDOR_NUMERO1  numero_endereco,\n"
                    + "f.FORNECEDOR_BAIRRO bairro,\n"
                    + "f.LOCALIDADE_CEP cep,\n"
                    + "l.LOCALIDADE_CODIBGE  as ibge_cidade,\n"
                    + "l.LOCALIDADE_NOME as cidade,\n"
                    + "fed.FEDERACAO_NOME uf,\n"
                    + "f.FORNECEDOR_EMAIL email,\n"
                    + "f.FORNECEDOR_OBSERVACAO observacao,\n"
                    + "f.FORNECEDOR_DDD_TELEFONE1 as ddd1,\n"
                    + "f.FORNECEDOR_TELEFONE as telefone1,\n"
                    + "f.FORNECEDOR_DDD_TELEFONE2 as ddd2,\n"
                    + "f.FORNECEDOR_TELEFONE1 as telefone2,\n"
                    + "f.FORNECEDOR_STATUS ativo\n"
                    + "from FORNECEDORES f \n"
                    + "LEFT JOIN LOCALIDADES l ON f.LOCALIDADE_CEP = l.LOCALIDADE_CEP\n"
                    + "LEFT JOIN FEDERACOES fed ON l.FEDERACAO_ID = fed.FEDERACAO_ID "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("ibge_cidade"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("ddd1") + rst.getString("telefone2"));

                    String email = (rst.getString("ddd2") + rst.getString("ddd2"));
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
                    }
                    String fone2 = (rst.getString("email"));
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
                        cont.setNome("TELEFONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
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
                    "SELECT  \n"
                    + "PRODUTOS_ID id_produto, \n"
                    + "FORNECEDORES_ID id_fornecedor,\n"
                    + "PROD_FOR_CODIGO codigoexterno\n"
                    + "FROM\n"
                    + "PRODUTO_FORNECEDOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
                    "select \n"
                    + "    p.PRODUTOS_ID id_produto, \n"
                    + "    p.PRODUTO_CODIGO_BARRAS codigo_barras,\n"
                    + "    u.UNIDADE_NOME as tipoembalagem    \n"
                    + "from PRODUTOS p \n"
                    + "LEFT JOIN UNIDADES u ON p.UNIDADES_ID = u.UNIDADES_ID")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("codigo_barras"));
                    imp.setTipoEmbalagem(rs.getString("tipoembalagem"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.CLIENTES_ID id,\n"
                    + "f.CLIENTE_NOME razao,\n"
                    + "f.CLIENTE_NOME1 fantasia,\n"
                    + "f.CLIENTE_CPF_CNPJ cnpj,\n"
                    + "f.CLIENTE_RG_IE ie,\n"
                    + "f.CLIENTE_CADASTRO datacadastro,\n"
                    + "f.CLIENTE_ENDERECO endereco,\n"
                    + "f.CLIENTE_NUMERO numero_endereco,\n"
                    + "f.CLIENTE_BAIRRO bairro,\n"
                    + "f.LOCALIDADE_CEP cep,\n"
                    + "l.LOCALIDADE_CODIBGE ibge_cidade,\n"
                    + "l.LOCALIDADE_NOME cidade,\n"
                    + "fed.FEDERACAO_NOME uf,\n"
                    + "f.CLIENTE_EMAIL email,\n"
                    + "f.CLIENTE_OBS observacao,\n"
                    + "f.CLIENTE_TELEFONE telefone1,\n"
                    + "CASE \n"
                    + "	WHEN f.CLIENTE_STATUS = 2 OR f.CLIENTE_STATUS = 4\n"
                    + "	THEN 0\n"
                    + "	ELSE 1\n"
                    + "END ativo,\n"
                    + "f.CLIENTE_LIMITE_CREDITO limite_credito\n"
                    + "from CLIENTES f\n"
                    + "LEFT JOIN LOCALIDADES l ON f.LOCALIDADE_CEP = l.LOCALIDADE_CEP \n"
                    + "LEFT JOIN FEDERACOES fed ON l.FEDERACAO_ID = fed.FEDERACAO_ID"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("ativo") == 1);
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipioIBGE(rst.getInt("ibge_cidade"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
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
                    + "	a.ARECEBER_ID id,\n"
                    + "	-- a.ARECEBER_DOCUMENTO documento,\n"
                    + "	CASE \n"
                    + "		WHEN a.CUPONS_ID IS NULL\n"
                    + "		THEN a.ARECEBER_DOCUMENTO\n"
                    + "		ELSE a.CUPONS_ID \n"
                    + "	END documento,\n"
                    + "	a.CLIENTES_ID cliente,\n"
                    + "	a.ARECEBER_EMISSAO data_emissao,\n"
                    + "	a.ARECEBER_VENCIMENTO data_vencimento,\n"
                    + "	a.ARECEBER_VALOR valor,\n"
                    + "	a.ARECEBER_SALDO saldo,\n"
                    + "	a.ARECEBER_PAGO,\n"
                    + "	a.ARECEBER_PARCELA,\n"
                    + "	s.STATUS_NOME situacao\n"
                    + "FROM \n"
                    + "	ARECEBER a\n"
                    + "LEFT JOIN STATUS s ON a.ARECEBER_STATUS = s.STATUS_ID \n"
                    + "WHERE s.STATUS_ID != 2")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("cliente"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setValor(rs.getDouble("saldo"));
                    imp.setNumeroCupom(rs.getString("documento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

}
