package vrimplantacao2_5.dao.sistema;

import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
//import vrimplantacao.classe.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class ScvDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(ScvDAO.class.getName());

    @Override
    public String getSistema() {
        return "SCV";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.IMPORTAR_RESETAR_BALANCA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.VOLUME_QTD,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS,
            OpcaoProduto.ICMS_SAIDA,
            OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
            OpcaoProduto.ICMS_ENTRADA,
            OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.PDV_VENDA,
            OpcaoProduto.FABRICANTE
        }));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.CONVENIO_CONVENIADO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.CONTATOS
        ));
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " id,\n"
                    + " RAZAO_SOCIAL,\n"
                    + " NOME_FANTASIA,\n"
                    + " CNPJ\n"
                    + "FROM EMPRESAS\n"
                    + "WHERE id = 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("ID"),
                            rst.getString("NOME_FANTASIA") + " - " + rst.getString("CNPJ")));
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
                    + " a.ID,\n"
                    + " a.DESCRICAO,\n"
                    + " a.ICMS,\n"
                    + " a.CST,\n"
                    + " CAST(p.REDUCAO_BASE_ICMS AS decimal(10,2)) reducao\n"
                    + "FROM ALIQUOTAS_CADASTRO a\n"
                    + "LEFT JOIN PRODUTOS p ON p.ID_ALIQUOTA_CADASTRO = a.ID AND p.REDUCAO_BASE_ICMS > 0\n"
                    + "WHERE \n"
                    + "  a.ICMS IS NOT NULL "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("ID"),
                            rst.getString("DESCRICAO"),
                            rst.getInt("CST"),
                            rst.getDouble("ICMS"),
                            rst.getDouble("reducao")
                    ));
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
                    "SELECT \n"
                    + " g.ID mercid1,\n"
                    + " g.DESCRICAO desc1,\n"
                    + " s.ID mercid2,\n"
                    + " s.DESCRICAO desc2,\n"
                    + " s.ID mercid3,\n"
                    + " s.DESCRICAO desc3\n"
                    + "FROM GRUPOS g \n"
                    + "JOIN SUBGRUPOS s ON s.ID_GRUPO = g.ID"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("desc3"));
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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	id id_familia,\n"
                    + "	DESCRICAO familia\n"
                    + "FROM\n"
                    + "	PRODUTO_AGRUP_PRECO"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_familia"));
                    imp.setDescricao(rs.getString("familia"));

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
                    + " p.ID,\n"
                    + " replace(replace(replace\n"
                    + " (replace(replace(replace(replace\n"
                    + " (p.DESCRICAO, 'Ă', 'A'), 'Ő', 'O'), 'Ç', 'C'),\n"
                    + " 'Ę', 'E'), 'É', 'E'), 'Á', 'A'), 'Ô', 'O') descricaocompleta,\n"
                    + " replace(replace(replace\n"
                    + " (replace(replace(replace(replace\n"
                    + " (p.DESCRICAO_REDUZIDA, 'Ă', 'A'), 'Ő', 'O'), 'Ç', 'C'),\n"
                    + " 'Ę', 'E'), 'É', 'E'), 'Á', 'A'), 'Ô', 'O') descricaoreduzida,"
                    + " pp.PRECO_VAREJO precovenda,\n"
                    + " p.PRECO_ATACADO,\n"
                    + " p.PRECO_VAREJO_SUGERIDO,\n"
                    + " p.CUSTO_MEDIO precocustomedio,\n"
                    + " pp.PRECO_CUSTO precocusto,\n"
                    + " pp.PRECO_FORNECEDOR custo,\n"
                    + " p.PRECO_COMPRA,\n"
                    + " pp.LUCRO_VAREJO margem,\n"
                    + " u.SIGLA embalagem,\n"
                    + " CASE WHEN p.STATUS = 'A' THEN 1 ELSE 0 END situacao,\n"
                    + " p.DATA_CADASTRO,\n"
                    + " p.CODIGO_INTERNO,\n"
                    + " p.CODIGO_BARRA ean,\n"
                    + " p.BALANCA,\n"
                    + " id_agrup_prec id_familia,\n"
                    + " p.ID_GRUPO mercid1,\n"
                    + " p.ID_SUBGRUPO mercid2,\n"
                    + " p.ID_SUBGRUPO mercid3,\n"
                    + " e.ESTOQUE_MINIMO estoquemin,\n"
                    + " e.ESTOQUE_ATUAL estoque,\n"
                    + " p.ID_FORNECEDOR fabricante,\n"
                    + " p.VALOR_MEDIDA qtde,\n"
                    + " p.CEST,\n"
                    + " pp.COD_NATUREZA_RECEITA natrec,\n"
                    + " p.ID_ALIQUOTA_CADASTRO,\n"
                    + " p.CST,\n"
                    + " p.REDUCAO_BASE_ICMS,\n"
                    + " p.CST_PIS_COFINS,\n"
                    + " p.CST_PIS_COFINS_ENT,\n"
                    + " p.NCM\n"
                    + "FROM PRODUTOS p\n"
                    + "LEFT JOIN PRODUTOS_PREC_TRIB pp ON p.id = pp.ID_PRODUTO AND p.ID_EMPRESA = pp.ID_EMPRESA\n"
                    + "LEFT JOIN UNIDADES_MEDIDA u ON p.ID_UM_ENTRADA = u.ID \n"
                    + "LEFT JOIN PRODUTOS_ESTOQUE_NORMAL e ON e.ID_PRODUTO = p.ID AND e.ID_ESTOQUE = " + getLojaOrigem() + "\n"
                    + "WHERE \n"
                    + "	pp.ID_EMPRESA = " + getLojaOrigem()
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca("S".equals(rst.getString("balanca")));
                        imp.setTipoEmbalagem(rst.getString("embalagem"));
                        imp.setTipoEmbalagemVolume(rst.getString("embalagem"));
                        imp.setQtdEmbalagem(rst.getInt("qtde"));
                    }

                    imp.setDescricaoCompleta(Utils.acertarTexto(rst.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rst.getString("descricaoreduzida")));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));

                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));

                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setSituacaoCadastro(rst.getInt("situacao"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("CST_PIS_COFINS"));
                    imp.setPiscofinsCstCredito(rst.getString("CST_PIS_COFINS_ENT"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natrec"));

                    imp.setIcmsDebitoId(rst.getString("ID_ALIQUOTA_CADASTRO"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());

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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " ID,\n"
                    + " ID_PRODUTO produto,\n"
                    + " CODIGO_BARRA ean\n"
                    + "FROM PRODUTOS_CODIGOSBARRAS "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(1);

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
                    + " f.ID,\n"
                    + " f.TIPO,\n"
                    + " f.CNPJ,\n"
                    + " f.CPF,\n"
                    + " f.INSCRICAO_ESTADUAL,\n"
                    + " f.RAZAO_SOCIAL,\n"
                    + " f.NOME_FANTASIA,\n"
                    + " f.CEP,\n"
                    + " c.NOME CIDADE,\n"
                    + " c.ESTADO UF,\n"
                    + " f.ENDERECO,\n"
                    + " f.NUMERO,\n"
                    + " f.COMPLEMENTO,\n"
                    + " f.BAIRRO,\n"
                    + " f.TELEFONE,\n"
                    + " f.CONTATO\n"
                    + "FROM FORNECEDORES f\n"
                    + "LEFT JOIN CIDADES c ON c.ID = f.ID_CIDADE"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("ID"));
                    imp.setRazao(rst.getString("RAZAO_SOCIAL"));
                    imp.setFantasia(rst.getString("NOME_FANTASIA"));
                    imp.setCnpj_cpf(rst.getString("CNPJ"));
                    imp.setIe_rg(rst.getString("INSCRICAO_ESTADUAL"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("UF"));
                    imp.setTel_principal(rst.getString("TELEFONE"));

                    String contato = rst.getString("contato");

                    if (contato != null && !contato.isEmpty()) {
                        imp.addContato(contato, null, null, TipoContato.COMERCIAL, null);
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
                    + "	ID_PRODUTO ,\n"
                    + "	ID_FORNECEDOR ,\n"
                    + "	PRODUTO_FORNECEDOR AS codigo_externo\n"
                    + "FROM\n"
                    + "	FORNECEDORES_PRODUTOS fp "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("ID_PRODUTO"));
                    imp.setIdFornecedor(rst.getString("ID_FORNECEDOR"));
                    imp.setCodigoExterno(rst.getString("codigo_externo"));
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
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	c.id,\n"
                    + "	c.NOME,\n"
                    + "	CASE WHEN CPF IS NULL THEN CNPJ ELSE cpf END cpf_cnpj,\n"
                    + "	CASE WHEN CNPJ IS NOT NULL THEN INSCRICAO_ESTADUAL ELSE RG_NUMERO END rg_ie,\n"
                    + "	INSCRICAO_MUNICIPAL insc_mun,\n"
                    + "	RESIDENCIA_CLIENTE_ENDERECO as endereco,\n"
                    + "	RESIDENCIA_CLIENTE_NUMERO as numero,\n"
                    + "	RESIDENCIA_CLIENTE_COMPLEMENTO complemento,\n"
                    + "	RESIDENCIA_CLIENTE_CEP as cep,\n"
                    + "	RESIDENCIA_CLIENTE_BAIRRO as bairro,\n"
                    + "	RESIDENCIA_CLIENTE_ID_CIDADE mun_ibge,\n"
                    + "	c2.NOME AS cidade,\n"
                    + "	TELEFONE1  AS telefone,\n"
                    + "	TELEFONE2 AS telefone2,\n"
                    + "	EMAIL1 AS email,\n"
                    + "	DATA_NASCIMENTO AS data_nasc,\n"
                    + "	TELEFONE2 AS celular,\n"
                    + "	LIMITE_CREDITO AS limite,\n"
                    + "	CASE WHEN STATUS = 'A' THEN TRUE ELSE FALSE END AS ativo\n"
                    + "FROM\n"
                    + "	CLIENTES c \n"
                    + "	JOIN CIDADES c2 ON c2.ID = c.RESIDENCIA_CLIENTE_ID_CIDADE "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("rg_ie"));
                    imp.setInscricaoMunicipal(rst.getString("insc_mun"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setMunicipioIBGE(rst.getString("mun_ibge"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("telefone2"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataNascimento(rst.getDate("data_nasc"));

                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setAtivo(rst.getBoolean("ativo"));

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
                    + " cr.ID,\n"
                    + " cr.STATUS,\n"
                    + " cr.ID_CLIENTE,\n"
                    + " cr.ID_VENDA CUPOM,\n"
                    + " cr.DATA_CONTA,\n"
                    + " cr.DATA_VENCIMENTO,\n"
                    + " cr.PARCELA,\n"
                    + " cr.VALOR_NOMINAL,\n"
                    + " cr.VALOR_ABERTO\n"
                    + "FROM CONTAS_RECEBER cr\n"
                    + "LEFT JOIN CONTAS_RECEBER_RECEBIMENTOS r ON r.ID_CR = cr.ID \n"
                    + "WHERE \n"
                    + " r.ID_CR IS null and cr.id_empresa = 1"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setIdCliente(rst.getString("ID_CLIENTE"));
                    imp.setNumeroCupom(rst.getString("CUPOM"));
                    imp.setValor(rst.getDouble("VALOR_NOMINAL"));
                    imp.setDataEmissao(rst.getDate("DATA_CONTA"));
                    imp.setDataVencimento(rst.getDate("DATA_VENCIMENTO"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	cp.ID,\n"
                    + "	cp.ID_FORNECEDOR,\n"
                    + "	cp.STATUS,\n"
                    + "	cp.DATA_CONTA,\n"
                    + "	cp.DATA_VENCIMENTO,\n"
                    + "	cp.DOCUMENTO doc,\n"
                    + " cast(ID_NF_ENTRADA as varchar(10))||'- PARC '||CAST (PARCELA AS varchar(10)) documento,\n"
                    + "	cp.PARCELA,\n"
                    + "	cp.VALOR_NOMINAL,\n"
                    + "	cp.VALOR_ABERTO,\n"
                    + "	cp.ANOTACOES,\n"
                    + "	cp.NUMERO_DOC_FISCAL\n"
                    + "FROM\n"
                    + "	CONTAS_PAGAR cp\n"
                    + "LEFT JOIN CONTAS_PAGAR_PAGAMENTOS pa ON pa.ID_CP = cp.ID\n"
                    + "WHERE\n"
                    + "	pa.ID_CP IS NULL\n"
                    + "	AND DATA_VENCIMENTO >= 'now'\n"
                    + "	AND cp.id_empresa = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("ID"));
                    imp.setIdFornecedor(rst.getString("ID_FORNECEDOR"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("DATA_CONTA"));
                    imp.setValor(rst.getDouble("VALOR_NOMINAL"));
                    imp.setObservacao(rst.getString("ANOTACOES"));
                    imp.setVencimento(rst.getDate("DATA_VENCIMENTO"));

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

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new ScvDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new ScvDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private final Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private final Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("idvenda");
                        if (!uk.add(id)) {
                            LOG.log(Level.WARNING, "Venda {0} ja existe na listagem", id);
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data_venda"));

                        String horaInicio = timestampDate.format(rst.getDate("data_venda")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data_venda")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	ID as idvenda ,\n"
                    + "	CASE WHEN NFCE_NUMERO_CNF IS NULL THEN id \n"
                    + "		WHEN NFCE_NUMERO_CNF = 0 THEN id \n"
                    + "		ELSE NFCE_NUMERO_CNF END AS numerocupom,\n"
                    + "	PDV AS ecf,\n"
                    + "	PAF_DATA_MOVIMENTO AS data_venda,\n"
                    + "	HORA_ABERTURA AS hora,\n"
                    + "	TOTAL_BRUTO AS valor ,\n"
                    + "	TOTAL_LIQUIDO ,\n"
                    + "	CASE WHEN CANCELADA = 'N' THEN 0 ELSE 1 END AS cancelado\n"
                    + "FROM\n"
                    + "	VENDA v\n"
                    + "	WHERE ID_EMPRESA = " + idLojaCliente + "\n"
                    + " AND PAF_DATA_MOVIMENTO between '" + strDataInicio + "'and '" + strDataTermino + "'\n"
                    + "	ORDER BY 1";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private final Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("idVenda"));
                        next.setId(rst.getString("idItensVenda"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("idProduto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	vi.ID_VENDA idVenda,\n"
                    + "	vi.id idItensVenda,\n"
                    + "	SEQUENCIA ,\n"
                    + "	ID_PRODUTO as idProduto,\n"
                    + "	PAF_UNIDADE_MEDIDA AS unidade,\n"
                    + "	CASE WHEN QUANTIDADE > 9999 THEN 1 ELSE QUANTIDADE END quantidade,\n"
                    + "	VALOR_UNITARIO AS valor,\n"
                    + " CASE WHEN vi.CANCELADO = 'N' THEN 0 ELSE 1 END AS cancelado\n"
                    + "FROM\n"
                    + "	VENDA_ITENS vi\n"
                    + "	JOIN venda v ON v.ID = vi.ID_VENDA AND v.ID_EMPRESA = " + idLojaCliente + "\n"
                    + "WHERE v.PAF_DATA_MOVIMENTO between '" + VendaIterator.FORMAT.format(dataInicio) + "'\n"
                    + "	AND '" + VendaIterator.FORMAT.format(dataTermino) + "' \n"
                    + "	ORDER BY 1";

            LOG.log(Level.FINE, "SQL da venda: {0}", sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

}
