package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class MSuperDAO extends InterfaceDAO implements MapaTributoProvider {

    private boolean importarCodigoPrincipal;

    public boolean isImportarCodigoPrincipal() {
        return this.importarCodigoPrincipal;
    }

    public void setImportarCodigoPrincipal(boolean importarCodigoPrincipal) {
        this.importarCodigoPrincipal = importarCodigoPrincipal;
    }

    @Override
    public String getSistema() {
        return "MSuper";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	SUP002 id,\n"
                    + "	DESCRICAO,\n"
                    + "	PERCENTUAL \n"
                    + "FROM\n"
                    + "	SUP002\n"
                    + "	ORDER BY 1"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst_saida") + "-" + rs.getString("aliquota_saida") + "-" + rs.getString("reducao_saida");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida")
                    )
                    );
                }
            }

            try (ResultSet rs = stm.executeQuery(
                    "SELECT DISTINCT\n"
                    + "    icm.compracsf AS cst_entrada,\n"
                    + "    icm.compraicms AS aliquota_entrada,\n"
                    + "    icm.comprareducao AS reducao_entrada\n"
                    + "FROM TESTPRODUTOGERAL pg\n"
                    + "JOIN TESTICMS icm ON icm.produto = pg.codigo\n"
                    + "    AND icm.empresa = " + getLojaOrigem() + "\n"
                    + "    AND icm.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst_entrada") + "-" + rs.getString("aliquota_entrada") + "-" + rs.getString("reducao_entrada");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst_entrada"),
                            rs.getDouble("aliquota_entrada"),
                            rs.getDouble("reducao_entrada")
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
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
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
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	SUP999 codigo,\n"
                    + "	FANTASIA nomefantasia,\n"
                    + "	CNPJ cpfcnpj\n"
                    + "FROM\n"
                    + "	SUP999\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"),
                                    rst.getString("nomefantasia") + "-" + rst.getString("cpfcnpj")
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
                    "SELECT\n"
                    + "	m1.CODIGO codmerc1,\n"
                    + "	m1.DESCRICAO descmerc1,\n"
                    + "	m2.CODIGO codmerc2,\n"
                    + "	m2.DESCRICAO descmerc2,\n"
                    + "	m3.CODIGO codmerc3,\n"
                    + "	m3.DESCRICAO descmerc3\n"
                    + "FROM \n"
                    + "	SUP004 m1\n"
                    + "	LEFT JOIN SUP005 m2 ON m2.SUP004 = m1.SUP004\n"
                    + "	LEFT JOIN SUP006 m3 ON m3.SUP005 = m2.SUP005 AND m2.SUP004 = m1.SUP004\n"
                    + "WHERE m1.ATIVO = 'S' AND m2.ATIVO = 'S' AND m3.ATIVO = 'S'\n"
                    + "ORDER BY 1,3,5"
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
                    + "    pg.produtoprincipal AS id,\n"
                    + "    pg.codigobarra AS ean,\n"
                    + "    pg.descricao AS descricaocompleta,\n"
                    + "    pg.referencia,\n"
                    + "    pg.descricaoreduzida AS descricaoreduzida,\n"
                    + "    pg.descricaograde AS descricaogondola,\n"
                    + "    pg.embalagem AS tipoembalagem,\n"
                    + "    pg.qtdeembalagem AS qtdembalagem,\n"
                    + "    pg.pesobruto AS pesobruto,\n"
                    + "    pg.pesoliquido AS pesoliquido,\n"
                    + "    pg.datacadastro AS datacadastro,\n"
                    + "    pg.classificacaofiscal AS ncm,\n"
                    + "    c.cest as cest,\n"
                    + "    p.custofabrica AS custosemimposto,\n"
                    + "    p.custofinal AS custocomimposto,\n"
                    + "    p.margemlucro AS margem,\n"
                    + "    p.prpraticado AS precovenda,\n"
                    + "    p.estoqueminimo AS estoqueminimo,\n"
                    + "    p.estoquemaximo AS estoquemaximo,\n"
                    + "    p.estdisponivel AS estoque,\n"
                    + "    p.grupo AS mercaologico1,\n"
                    + "    p.subgrupo AS mercadologico2,\n"
                    + "    '1' AS mercadologico3,\n"
                    + "    CASE p.ativo WHEN 'S' THEN 1 ELSE 0 END situacaocadastro,\n"
                    + "    CASE p.SETOR WHEN '001' THEN 1 ELSE 0 END ebalanca,\n"
                    + "    coalesce(pg.diasvalidade,0) as validade,\n"
                    + "    icm_s.vendacsf1 AS cst_saida,\n"
                    + "    icm_s.vendaicms1 AS aliquota_saida,\n"
                    + "    icm_s.vendareducao1 AS reducao_saida,\n"
                    + "    icm_e.compracsf AS cst_entrada,\n"
                    + "    icm_e.compraicms AS aliquota_entrada,\n"
                    + "    icm_e.comprareducao AS reducao_entrada\n"
                    + "FROM TESTPRODUTOGERAL pg\n"
                    + "LEFT JOIN TESTPRODUTO p ON p.produto = pg.codigo\n"
                    + "    AND p.empresa = '" + getLojaOrigem() + "'\n"
                    + "LEFT JOIN TESTGRUPOICMS gi ON gi.codigoid = pg.grupoicms\n"
                    + "LEFT JOIN TESTICMS icm_s on icm_s.produto = pg.codigo\n"
                    + "    AND icm_s.empresa = '" + getLojaOrigem() + "'\n"
                    + "    AND icm_s.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "LEFT JOIN TESTICMS icm_e on icm_e.produto = pg.codigo\n"
                    + "    AND icm_e.empresa = '" + getLojaOrigem() + "'\n"
                    + "    AND icm_e.estado = '" + Parametros.get().getUfPadraoV2().getSigla() + "'\n"
                    + "LEFT JOIN TESTCEST c on c.idcest = pg.idcest\n"
                    + "WHERE pg.descricao NOT LIKE '%BASE CORROMPIDA%'\n"
                    + "AND pg.codigo IN (SELECT produtoprincipal FROM TESTPRODUTOGERAL)"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("mercaologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    String idIcmsDebito, IdIcmsCredito;

                    idIcmsDebito = rst.getString("cst_saida") + "-" + rst.getString("aliquota_saida") + "-" + rst.getString("reducao_saida");
                    IdIcmsCredito = rst.getString("cst_entrada") + "-" + rst.getString("aliquota_entrada") + "-" + rst.getString("reducao_entrada");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);
                    imp.setIcmsCreditoId(IdIcmsCredito);
                    imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);
                    imp.setIcmsConsumidorId(idIcmsDebito);

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
                    "SELECT\n"
                    + "	SUP001 id,\n"
                    + "	EAN,\n"
                    + "	MULTIPLICADOR qtdembalagem\n"
                    + "FROM\n"
                    + "	SUP013\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));

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
                    + "	sup010 id,\n"
                    + "	razaosocial razao,\n"
                    + "	fantasia,\n"
                    + "	cgc cnpj_cpf,\n"
                    + "	inscricao ie_rg,\n"
                    + "	inscmunicipal insc_municipal,\n"
                    + "	case when ativo = 'S' then 1 else 0 end ativo,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	cep,\n"
                    + "	c.nome municipio,\n"
                    + "	f.sup118 ibge_municipio,\n"
                    + "	c.uf uf,\n"
                    + "	telefone tel_principal,\n"
                    + "	dtcadastro datacadastro,\n"
                    + "	obs observacao\n"
                    + "FROM\n"
                    + "	sup010 f\n"
                    + "JOIN sup118 c ON c.sup118 = f.sup118\n"
                    + "WHERE\n"
                    + "	sup999 = " + getLojaOrigem() + "\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setInsc_municipal(rst.getString("insc_municipal"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel_principal"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));

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
                    "  SELECT\n"
                    + "	f.SUP010 AS idfornecedor,\n"
                    + "	p.SUP001 AS idproduto,\n"
                    + "	f.DTALTERACAO AS dtalteracao,\n"
                    + "FROM\n"
                    + "	SUP010 f\n"
                    + "JOIN SUP001 p ON p.SUP010 = f.SUP010\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	cp.SUP020 AS id,\n"
                    + "	fr.SUP010 AS idfornecedor,\n"
                    + "	cp.SUP999 AS loja,\n"
                    + "	fr.CGC AS cnpj,\n"
                    + "	cp.DUPLICATA AS numerodocumento,\n"
                    + "	cp.EMISSAO AS dataemissao,\n"
                    + "	cp.DATA_ENTRADA AS dataentrada,\n"
                    + "	cp.DATALANCAMENTO,\n"
                    + "	cp.HORALANCAMENTO,\n"
                    + "	cp.VALOR as valor,\n"
                    + "	cp.VALOR_PGTO,\n"
                    + "	cp.VALOR_DESCONTAR,\n"
                    + "	cp.OBSERVACAO as obs,\n"
                    + "	cp.VENCIMENTO as datavencimento\n"
                    + "FROM\n"
                    + "	SUP020 cp\n"
                    + "JOIN SUP010 fr ON fr.SUP010 = cp.SUP010\n"
                    + "WHERE\n"
                    + "	cp.SUP999 = " + getLojaOrigem() + " --(Num.Loja)"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setNumeroDocumento(rs.getString("numerodocumento"));
                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setDataEntrada(rs.getDate("dataentrada"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("datavencimento"));
                    imp.setObservacao(rs.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	c.sup025 id,\n"
                    + "	c.nome razao,\n"
                    + "	APELIDO fantasia,\n"
                    + "	NASCIMENTO dtnascimento,\n"
                    + "	CPF,\n"
                    + "	rg,\n"
                    + "	TELEFONE1,\n"
                    + "	TELEFONE2,\n"
                    + "	CELULAR,\n"
                    + "	EMAIL,\n"
                    + "	EMISSAO AS datacadastro,\n"
                    + "	CASE\n"
                    + "		WHEN BLOQUEADO = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END BLOQUEADO,\n"
                    + "	databloq databloqueio,\n"
                    + "	CASE\n"
                    + "		WHEN CANCELADO = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END ativo,\n"
                    + "	OBSERVACAO,\n"
                    + "	PRAZO_PGTO,\n"
                    + "	DIA_VENCTO vencimento,\n"
                    + "	CASE\n"
                    + "		WHEN CHEQUE_BLOQUEADO = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END permitechq,\n"
                    + "	ENDERECO,\n"
                    + "	NUMERO,\n"
                    + "	COMPLEMENTO,\n"
                    + "	BAIRRO,\n"
                    + "	CEP,\n"
                    + "	mun.nome municipio,\n"
                    + "	mun.uf uf,\n"
                    + "	pai,\n"
                    + "	MAE,\n"
                    + "	CONJUGE,\n"
                    + "	CPFCONJUGE,\n"
                    + "	PROFISSAO,\n"
                    + "	RENDAMENSAL SALARIO,\n"
                    + "	LIMITE valorlimite\n"
                    + "FROM\n"
                    + "	SUP025 C\n"
                    + "JOIN sup118 Mun ON\n"
                    + "	Mun.sup118 = c.sup118\n"
                    + "WHERE\n"
                    + "	sup999 = 1\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    
                    imp.setDataNascimento(rs.getDate("dtnascimento"));
                                        
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setInscricaoestadual(rs.getString("rg"));
                    
                    imp.setTelefone(rs.getString("telefone1"));
                    imp.setFax(rs.getString("telefone2"));                    
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setDataBloqueio(rs.getDate("databloqueio"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("OBSERVACAO"));
                    imp.setPrazoPagamento(rs.getInt("prazo_pgto"));
                    imp.setDiaVencimento(rs.getInt("vencimento"));
                    imp.setPermiteCheque(rs.getBoolean("permitechq"));
                    
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setCep(rs.getString("cep"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("uf"));
                    
                    imp.setNomePai(rs.getString("pai"));
                    imp.setNomeMae(rs.getString("mae"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setCargo(rs.getString("profissao"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));

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
                    "SELECT    \n"
                    + "        cr.IDTRECPARCELA id,    \n"
                    + "        cr.DOCUMENTO numerocupom,    \n"
                    + "        cr.valorpendente valor,\n"
                    + "        cr.CLIENTE codcli,    \n"
                    + "        cpfcnpj cnpjcliente,    \n"
                    + "        1 AS ecf,    \n"
                    + "        t.EMISSAO emissao,    \n"
                    + "        cr.vencimento    \n"
                    + "FROM    \n"
                    + "       TRECPARCELA cr    \n"
                    + "LEFT JOIN TRECCLIENTEGERAL c ON c.CODIGO = cr.CLIENTE    \n"
                    + "LEFT JOIN TRECDOCUMENTO t ON cr.DOCUMENTO = t.DOCUMENTO     \n"
                    + "WHERE    \n"
                    + "       cr.EMPRESA = '" + getLojaOrigem() + "'\n"
                    + "AND cr.tipo = '01'\n"
                    + "AND cr.databaixa IS NULL\n"
                    + "AND cr.valorpendente >0"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cnpjcliente")));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
