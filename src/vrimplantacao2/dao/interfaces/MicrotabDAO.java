package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoInformix;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Wagner
 */
public class MicrotabDAO extends InterfaceDAO implements MapaTributoProvider {

    private boolean importarCodigoPrincipal;

    public boolean isImportarCodigoPrincipal() {
        return this.importarCodigoPrincipal;
    }

    public void setImportarCodigoPrincipal(boolean importarCodigoPrincipal) {
        this.importarCodigoPrincipal = importarCodigoPrincipal;
    }

    @Override
    public String getSistema() {
        return "Microtab";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	ID,\n"
                    + "	INDICE descricao,\n"
                    + "CAST(replace(REPLACE(PORCENT,'%',''),',','.') AS float) AS aliquota\n"
                    + "FROM\n"
                    + "	ALIQUOTA"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            0,
                            rs.getFloat("aliquota"),
                            0));
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.PRODUTOS_BALANCA,
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
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT \n"
                    + "  1 codigo,\n"
                    + "  'Loja 01' nomefantasia\n"
                    + " FROM CONFIG"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia")
                            )
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
                    "SELECT DISTINCT\n"
                    + "	p.ID_SETOR mercid1,\n"
                    + "	s.DESCRICAO desc1,\n"
                    + "	p.ID_SUB_SETOR mercid2,\n"
                    + "	sb.DESCRICAO desc2,\n"
                    + "	p.ID_SUB_SETOR mercid3,\n"
                    + "	sb.DESCRICAO desc3\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "JOIN SETOR s ON	s.ID = p.ID_SETOR\n"
                    + "JOIN SUB_SETOR sb ON sb.ID = p.ID_SUB_SETOR"
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
    public List<ProdutoIMP> getProdutosBalanca() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.ID,\n"
                    + "	CASE WHEN char_length(p.CODIGO) > 3 THEN substring(p.codigo from 2 for char_length(p.CODIGO))\n"
                    + "	     ELSE p.CODIGO END ean,\n"
                    + "	p.DESCRICAO descricaocompleta,\n"
                    + "	p.DESCRI_REDUZ descricaoreduzida,\n"
                    + "	p.ID_SETOR idmerc1,\n"
                    + "	p.ID_SUB_SETOR idmerc2,\n"
                    + "	p.ID_SUB_SETOR idmerc3,\n"
                    + "	p.CUSTO_MEDIO custo,\n"
                    + "	p.VALOR_VAREJO precovenda,\n"
                    + "	CASE\n"
                    + "		WHEN p.STATUS = 'A' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END situacaocadastro,\n"
                    + "	p.UNIDADE,\n"
                    + "	p.CST,\n"
                    + "	p.CSOSN,\n"
                    + "	p.CEST,\n"
                    + "	p.NCM,\n"
                    + "	p.ID_ALIQUOTA,\n"
                    + "	sp.SALDO estoque,\n"
                    + "	p.DT_ALTERA dtcadastro\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "JOIN ALIQUOTA a ON a.ID = p.ID_ALIQUOTA\n"
                    + "LEFT JOIN SALDO_PROD sp ON sp.ID_PROD = p.ID\n"
                    + "WHERE \n"
                    + " p.codigo LIKE '2%'\n"
                    + " AND\n"
                    + " char_length(p.CODIGO) < 7\n"
                    + " AND \n"
                    + " char_length(p.CODIGO) > 2"
            )) {

                while (rst.next()) {
                    Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();

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
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setCodMercadologico1(rst.getString("idmerc1"));
                    imp.setCodMercadologico2(rst.getString("idmerc2"));
                    imp.setCodMercadologico3(rst.getString("idmerc3"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsDebitoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsCreditoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsConsumidorId(rst.getString("ID_ALIQUOTA"));

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
                    "SELECT\n"
                    + "	p.ID,\n"
                    + "	CASE WHEN char_length(p.CODIGO) > 3 THEN substring(p.codigo from 2 for char_length(p.CODIGO))\n"
                    + "	     ELSE p.CODIGO END ean,\n"
                    + "	p.DESCRICAO descricaocompleta,\n"
                    + "	p.DESCRI_REDUZ descricaoreduzida,\n"
                    + "	p.ID_SETOR idmerc1,\n"
                    + "	p.ID_SUB_SETOR idmerc2,\n"
                    + "	p.ID_SUB_SETOR idmerc3,\n"
                    + "	p.CUSTO_MEDIO custo,\n"
                    + "	p.VALOR_VAREJO precovenda,\n"
                    + "	CASE\n"
                    + "		WHEN p.STATUS = 'A' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END situacaocadastro,\n"
                    + "	p.UNIDADE,\n"
                    + "	p.CST,\n"
                    + "	p.CSOSN,\n"
                    + "	p.CEST,\n"
                    + "	p.NCM,\n"
                    + "	p.ID_ALIQUOTA,\n"
                    + "	sp.SALDO estoque,\n"
                    + "	p.DT_ALTERA dtcadastro\n"
                    + "FROM\n"
                    + "	PRODUTO p\n"
                    + "JOIN ALIQUOTA a ON a.ID = p.ID_ALIQUOTA\n"
                    + "LEFT JOIN SALDO_PROD sp ON sp.ID_PROD = p.ID"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setCodMercadologico1(rst.getString("idmerc1"));
                    imp.setCodMercadologico2(rst.getString("idmerc2"));
                    imp.setCodMercadologico3(rst.getString("idmerc3"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsDebitoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsCreditoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("ID_ALIQUOTA"));
                    imp.setIcmsConsumidorId(rst.getString("ID_ALIQUOTA"));

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
                    "SELECT\n"
                    + "	ID,\n"
                    + "	NOME razao,\n"
                    + "	CNPJ cpfcnpj,\n"
                    + "	DT_CAD dtcadastro,\n"
                    + "	ENDERECO,\n"
                    + "	BAIRRO,\n"
                    + "	CIDADE municipio,\n"
                    + "	UF,\n"
                    + "	CEP,\n"
                    + "	INSC_ESTADUAL inscestadual,\n"
                    + "	TELEFONE tel_principal,\n"
                    + "	TEL_FAX fax,\n"
                    + "	SITE,\n"
                    + "	EMAIL,\n"
                    + "	OBS\n"
                    + "FROM\n"
                    + "	FORNECEDOR"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("inscestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("dtcadastro"));
                    imp.setObservacao(rst.getString("obs"));

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
                    + "	ID_FORNECEDOR idfornecedor,\n"
                    + "	ID idproduto,\n"
                    + "	DT_ALTERA dtalteracao\n"
                    + "FROM\n"
                    + "	PRODUTO\n"
                    + "WHERE\n"
                    + "	ID_FORNECEDOR IS NOT NULL;"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataAlteracao(rst.getDate("dtalteracao"));

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
                    "SELECT\n"
                    + "	ID,\n"
                    + "	TIPO,\n"
                    + "	DATA_CAD data_cadastro,\n"
                    + "	NOME razao,\n"
                    + "	ENDERECO,\n"
                    + "	BAIRRO,\n"
                    + "	CIDADE,\n"
                    + "	UF,\n"
                    + "	CEP,\n"
                    + "	CNPJ_CPF cpfcnpj,\n"
                    + "	INSC_EST_RG inscestrg,\n"
                    + "	TELEFONE,\n"
                    + "	CELULAR,\n"
                    + "	EMAIL email,\n"
                    + "	LIMITE limite_credito,\n"
                    + "	OBS\n"
                    + "FROM\n"
                    + "	CLIENTES"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("inscestrg"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setValorLimite(rs.getDouble("limite_credito"));
                    imp.setEmail(rs.getString("email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
