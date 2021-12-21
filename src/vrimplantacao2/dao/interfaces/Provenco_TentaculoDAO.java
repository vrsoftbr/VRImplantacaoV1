package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class Provenco_TentaculoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Tentaculo";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODITR id,\n"
                    + "    NOMETR descricao,\n"
                    + "    CODICST cst_saida,\n"
                    + "    PERC_ICM aliquota_saida,\n"
                    + "    PERC_RED reducao_saida\n"
                    + "FROM\n"
                    + "	TRIBUTA TR\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida")
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
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.ASSOCIADO
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
                    + "	EMP_CODIGO codigo,\n"
                    + "	EMP_FANTASIA fantasia,\n"
                    + "	EMP_CGC cnpj\n"
                    + "FROM\n"
                    + "	EMPRESAS e\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(
                            new Estabelecimento(
                                    rst.getString("codigo"), rst.getString("fantasia") + "-" + rst.getString("cnpj")
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
                    + "	m1.CODIGRU merc1,\n"
                    + "	m1.NOMEGRU desc_merc1,\n"
                    + "	m2.CODISGR merc2,\n"
                    + "	m2.NOMESGR desc_merc2,\n"
                    + "	m2.CODISGR merc3,\n"
                    + "	m2.NOMESGR desc_merc3\n"
                    + "FROM\n"
                    + "	GRUPOS m1\n"
                    + "JOIN SUBGRUPO m2 ON m2.CODIGRU = m1.CODIGRU\n"
                    + "ORDER BY 1,3"
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
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	CODIFAM id_familia,\n"
                    + "	NOMEFAM familia\n"
                    + "FROM\n"
                    + "	FAMILIAS f"
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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.CODIPRO idproduto,\n"
                    + "	CASE \n"
                    + "	  WHEN CAST(ean.COD_BARR AS bigint) > 999999\n"
                    + "	  THEN ean.COD_BARR ||(SELECT * FROM SP_PAF_DIGITO_EAN13(ean.COD_BARR))\n"
                    + "	  ELSE ean.COD_BARR\n"
                    + "	END ean,\n"
                    + "	QTD_UNI qtdembalagem,\n"
                    + "	e.ABRE_EMB tipoembalagem\n"
                    + "FROM\n"
                    + "	COD_BARR ean\n"
                    + "JOIN PRODUTOS p ON p.CODIPRO = ean.CODIPRO\n"
                    + "JOIN EMBALAG e ON e.CODIEMB = p.CODIEMB_V\n"
                    //+ "WHERE ean.COD_BARR ||(SELECT * FROM SP_PAF_DIGITO_EAN13(ean.COD_BARR)) IS NOT null\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));

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
                    + "	p.CODIPRO idproduto,\n"
                    + " CASE \n"
                    + "	  WHEN CAST(ean.COD_BARR AS bigint) > 999999\n"
                    + "	  THEN ean.COD_BARR ||(SELECT * FROM SP_PAF_DIGITO_EAN13(ean.COD_BARR))\n"
                    + "	  ELSE ean.COD_BARR\n"
                    + "	END ean,\n"
                    + "	(SELECT * FROM SP_PAF_DIGITO_EAN13(ean.COD_BARR)) AS digito,\n"
                    + "	DESCRICAO descricaocompleta,\n"
                    + "	DESCRI_AB descricaoreduzida,\n"
                    + "	un.ABRE_EMB tipoembalagem,\n"
                    + "	ean.QTD_UNI qtdembalagem,\n"
                    + "	PESO_BRU pesobruto,\n"
                    + "	PESO_LIQ pesoliquido,	\n"
                    + "	CASE WHEN balanca = 'S' THEN 1 ELSE 0 END e_balanca,\n"
                    + "	CODIFAM familia,\n"
                    + "	CODIGRU merc1,\n"
                    + "	CODISGR merc2,\n"
                    + "	CODISGR merc3,\n"
                    + "	CODINCM ncm,\n"
                    + "	p.CEST cest,\n"
                    + "	p.PERC_LUC margem,\n"
                    + "	pl.UC_CUSTO_C custocomimposto,\n"
                    + "	pl.UC_CUSTO_S custosemimposto,\n"
                    + "	pr.PREVE precovenda,\n"
                    + "	COALESCE (p.ESTOMIN,0) estmin,\n"
                    + "	p.ESTOMAX estmax,\n"
                    + "	t.CODICST cst_saida,\n"
                    + "	t.PERC_ICM aliq_saida,\n"
                    + "	t.PERC_RED red_saida,\n"
                    + "	DATA_ALTERA data_alteracao,\n"
                    + " pcc.CST_PIS piscofinscredito,\n"
                    + " pcd.CST_PIS piscofinsdebito,\n"
                    + " CASE WHEN p.atides = 'A' THEN 1 ELSE 0 END situacaocadastro\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "	LEFT JOIN COD_BARR ean ON p.CODIPRO = ean.CODIPRO\n"
                    + "	LEFT JOIN EMBALAG un ON un.CODIEMB = p.CODIEMB_V\n"
                    + "	LEFT JOIN PRECOS_LOJAS pr ON p.CODIPRO = pr.CODIPRO  AND pr.AGP_CODIGO = p.CODILF\n"
                    + "	LEFT JOIN PRODUTOS_LOJAS pl ON pl.CODIPRO = p.CODIPRO AND pl.EMP_CODIGO = p.CODILF \n"
                    + "	LEFT JOIN TRIBUTA_LOJAS i ON i.CODIPRO = p.CODIPRO\n"
                    + "	LEFT JOIN TRIBUTA t ON t.CODITR = i.CODITRC\n"
                    + " LEFT JOIN TRIBUTA_PIS pcc ON pcc.CODITRPIS = p.TRPIS_C AND pcc.ENTR_SAI = 'E'\n"
                    + "	LEFT JOIN TRIBUTA_PIS pcd ON pcd.CODITRPIS = p.TRPIS_V AND pcd.ENTR_SAI = 'S'\n"
                    + "WHERE\n"
                    + "	CODILF = " + getLojaOrigem() + ""
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));
                    //imp.setEstoque(rst.getDouble("estoque"));

                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));

                    String idIcmsDebito;//, IdIcmsCredito;

                    idIcmsDebito = rst.getString("cst_saida") + "-" + rst.getString("aliq_saida") + "-" + rst.getString("red_saida");
                    //IdIcmsCredito = rst.getString("cst_entrada") + "-" + rst.getString("aliquota_entrada") + "-" + rst.getString("reducao_entrada");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);
                    //imp.setIcmsCreditoId(IdIcmsCredito);
                    //imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);

                    imp.setPiscofinsCstCredito(rst.getString("piscofinscredito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsdebito"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	o.CODIPRO id_produto,\n"
                    + "	DTINPRO data_ini,\n"
                    + "	DTFIPRO data_fim,\n"
                    + "	PRECO_PRO precooferta,\n"
                    + "	pr.PREVE preconormal\n"
                    + "FROM\n"
                    + "	PROMOCOES o\n"
                    + "	LEFT JOIN PRODUTOS p ON p.CODIPRO = o.CODIPRO \n"
                    + "	LEFT JOIN PRECOS_LOJAS pr ON pr.CODIPRO = p.CODIPRO AND pr.AGP_CODIGO = o.EMP_CODIGO \n"
                    + "WHERE\n"
                    + "	o.EMP_CODIGO = " + getLojaOrigem() + "\n"
                    + "	AND DTFIPRO >= 'now'"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setDataInicio(rst.getDate("dataini"));
                    imp.setDataFim(rst.getDate("data_ini"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setPrecoNormal(rst.getDouble("preconormal"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	pa.CODIPRO produto_pai,\n"
                    + "	p.DESCRICAO descricao_pai,\n"
                    + "	pa.QTD_RELA qtdembalagem,\n"
                    + "	pa.COD_RELA produto_filho,\n"
                    + "	p2.DESCRICAO descricao_filho\n"
                    + "FROM\n"
                    + "	PRO_RELA pa\n"
                    + "	JOIN PRODUTOS p ON p.CODIPRO = pa.CODIPRO\n"
                    + "	JOIN PRODUTOS p2 ON pa.COD_RELA = p2.CODIPRO \n"
                    + "ORDER BY\n"
                    + "	produto_pai, produto_filho"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rst.getString("produto_pai"));
                    imp.setDescricao(rst.getString("descricao_pai"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setProdutoAssociadoId(rst.getString("produto_filho"));
                    imp.setDescricaoProdutoAssociado(rst.getString("descricao_filho"));

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
                    + "	FOR_CODIGO id,\n"
                    + "	FOR_RAZAO razao,\n"
                    + "	FOR_FANTASIA fantasia,\n"
                    + "	FOR_CGC cnpj,\n"
                    + "	FOR_INSC ie,\n"
                    + "	FOR_ENDERECO endereco,\n"
                    + "	FOR_NUMEND numero,\n"
                    + "	FOR_BAIRRO bairro,\n"
                    + "	CID_NOME cidade,\n"
                    + "	UF_SIGLA uf,\n"
                    + "	FOR_CEP cep,\n"
                    + "	FOR_FONE telefone,\n"
                    + " FOR_EMAIL email,\n"
                    + "	FOR_CONTATO contato,\n"
                    + "	FOR_OBSERV observacao\n"
                    + "FROM\n"
                    + "	FORNECEDORES f\n"
                    + "	JOIN CIDADES c ON c.CID_CODIGO = f.CID_CODIGO \n"
                    + "ORDER BY 1"
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
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(Utils.acertarTexto(rst.getString("telefone")));
                    imp.setObservacao(rst.getString("observacao"));

                    String email = Utils.acertarTexto(rst.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL, (email.length() > 50 ? email.substring(0, 50) : email));
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
                    "SELECT \n"
                    + "	CODIFAB idfornecedor,\n"
                    + "	CODIPRO idproduto,\n"
                    + "	1 qtdembalagem\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "	WHERE CODIFAB IS NOT NULL \n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
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
                    + "	NUMELAN id,\n"
                    + "	cp.FOR_CODIGO id_fornecedor,\n"
                    + "	f.FOR_CGC cnpj_cpf,\n"
                    + "	NUMEDO documento,\n"
                    + "	CP_DATAEM emissao,\n"
                    + "	CP_DATALAN entrada,\n"
                    + "	CP_VALORPA valor,\n"
                    + "	CP_DATAVE vencimento,\n"
                    + "	CP_OBS observacao\n"
                    + "FROM\n"
                    + "	CP cp\n"
                    + "	JOIN FORNECEDORES f ON f.FOR_CODIGO = cp.FOR_CODIGO \n"
                    + "WHERE\n"
                    + "	cp.EMP_CODIGO = " + getLojaOrigem() + "\n"
                    + "	AND cp.CP_DATAPA IS NULL\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rs.getString("id"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setNumeroDocumento(rs.getString("documento"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataEntrada(rs.getDate("entrada"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setVencimento(rs.getDate("vencimento"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    + "	CLI_CODIGO id,\n"
                    + "	CLI_NOME razao,\n"
                    + "	CLI_FANTASIA fantasia,\n"
                    + "	CLI_CGC cnpj_cpf,\n"
                    + "	CLI_RG rg_ie,\n"
                    + "	CLI_ENDERECO endereco,\n"
                    + "	CLI_NUMEND numero,\n"
                    + "	CLI_END_COMPLEMENTO complemento,\n"
                    + "	CLI_BAIRRO bairro,\n"
                    + "	m.CID_NOME cidade,\n"
                    + "	CLI_CEP cep,\n"
                    + "	m.UF_SIGLA uf,\n"
                    + "	CASE\n"
                    + "     WHEN CLI_SITUACAO = '01' THEN 1\n"
                    + "     ELSE 0\n"
                    + "	END bloqueado,\n"
                    + "	TRUNC(CLI_LIMITE,11) limite,\n"
                    + "	CLI_NASCIMENTO data_nascimento,\n"
                    + "	CLI_DTULCO data_cadastro,\n"
                    + "	CLI_EST_CIVIL estadocivil,\n"
                    + "	CLI_PROFISSAO profissao,\n"
                    + "	CLI_FONE telefone,\n"
                    + "	CLI_CELULAR celular,\n"
                    + "	CLI_E_MAIL email,\n"
                    + "	CLI_PAI nomepai,\n"
                    + "	CLI_MAE nomemae,\n"
                    + "	CLI_CJ_NOME conjuge,\n"
                    + "	CLI_CJ_NASC data_nasc_conjuge,\n"
                    + "	CLI_CJ_CPF cpfconjuge,\n"
                    + "	CLI_OBSERVACAO observacao\n"
                    + "FROM\n"
                    + "	CLIENTES c\n"
                    + "	JOIN CIDADES m ON c.CID_CODIGO = m.CID_CODIGO \n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    if (rs.getDouble("limite") > 9999999.0) {
                        imp.setValorLimite(0);
                    } else {
                        imp.setValorLimite(rs.getDouble("limite"));
                    }

                    imp.setDataNascimento(rs.getDate("data_nascimento"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setEstadoCivil(rs.getString("estadocivil"));
                    imp.setCargo(rs.getString("profissao"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setDataNascimentoConjuge(rs.getDate("data_nasc_conjuge"));
                    imp.setCpfConjuge(rs.getString("cpfconjuge"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    "SELECT\n"
                    + "	REPLACE(NUMELAN||NUMEDO,'/','') id,\n"
                    + "	NUMEDO numerocupom,\n"
                    + "	cr.CLI_CODIGO codcli,\n"
                    + "	c.CLI_CGC cpfcnpj,\n"
                    + "	PDV_NUMECAI ecf,\n"
                    + "	CR_VALORPA valor,\n"
                    + "	CR_DATAEM emissao,\n"
                    + "	CR_DATAVE vencimento\n"
                    + "FROM\n"
                    + "	CR cr\n"
                    + "	JOIN CLIENTES c ON c.CLI_CODIGO = cr.CLI_CODIGO \n"
                    + "WHERE\n"
                    + "	EMP_CODIGO = " + getLojaOrigem() + "\n"
                    + "	AND cr_datare is null\n"
                    + "ORDER BY 1"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rs.getString("numerocupom")));
                    imp.setIdCliente(rs.getString("codcli"));
                    imp.setCnpjCliente(Utils.formataNumero(rs.getString("cpfcnpj")));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
        DEV DE VENDAS INTERROMPIDO, NÃO TESTADAS AINDA
     */
    private Date vendaDataIni;
    private Date vendaDataFim;

    public void setVendaDataIni(Date vendaDataIni) {
        this.vendaDataIni = vendaDataIni;
    }

    public void setVendaDataFim(Date vendaDataFim) {
        this.vendaDataFim = vendaDataFim;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new Provenco_TentaculoDAO.VendaIterator(getLojaOrigem(), this.vendaDataIni, this.vendaDataFim);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new Provenco_TentaculoDAO.VendaItemIterator(getLojaOrigem(), this.vendaDataIni, this.vendaDataFim);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
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
                    + "	REPLACE((MOV_LOJA||MOV_COO||MOV_PDV||MOV_DT_MOVIMENTO), '-', '') AS id_venda,\n"
                    + "	MOV_LOJA loja,\n"
                    + "	MOV_PDV pdv,\n"
                    + "	MOV_ECF ecf,\n"
                    + "	MOV_COO numerocupom,\n"
                    + "	MOV_DT_MOVIMENTO data,\n"
                    + " SUBSTRING(MOV_DTHR_REGISTRO FROM 12 FOR 8) hora,\n"
                    + "	CAST(sum(MOV_VLR_TOTAL) AS numeric(11,2)) total,\n"
                    + "	CAST(sum(MOV_DESCONTO_CUPOM) AS numeric(11,2)) desconto,\n"
                    + "	CAST(sum(MOV_ACRESCIMO_CUPOM) AS numeric(11,2)) acrescimo\n"
                    + "FROM\n"
                    + "	TB_PDV_MOVOUTRA\n"
                    + "WHERE\n"
                    + "	PRO_ID IS NOT NULL\n"
                    + "	AND MOV_LOJA = " + idLojaCliente + "\n"
                    + "	AND MOV_DT_MOVIMENTO BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "GROUP BY 1, 2, 3, 4, 5, 6, 7";
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

        private Statement stm = ConexaoFirebird.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	REPLACE((pdv.MOV_LOJA || pdv.MOV_COO || pdv.MOV_PDV || pdv.MOV_DT_MOVIMENTO), '-', '') AS id_venda,\n"
                    + "	REPLACE((pdv.MOV_LOJA || pdv.MOV_COO || pdv.MOV_PDV || pdv.MOV_DT_MOVIMENTO || pdv.PRO_ID || pdv.MOV_SEQ_COO), '-', '') AS id_item,\n"
                    + "	SUBSTRING(pdv.PRO_COD_BARRA FROM 1 FOR CHAR_LENGTH(pdv.PRO_COD_BARRA)-1) ean,\n"
                    + "	p.PRO_DESCRICAO produto,\n"
                    + "	pdv.MOV_LOJA AS loja,\n"
                    + "	pdv.MOV_PDV AS pdv,\n"
                    + "	pdv.MOV_ECF AS ecf,\n"
                    + "	pdv.MOV_COO AS numerocupom,\n"
                    + "	pdv.MOV_SEQ_COO AS sequencia,\n"
                    + "	pdv.MOV_DT_MOVIMENTO AS DATA,\n"
                    + "	SUBSTRING(pdv.MOV_DTHR_REGISTRO FROM 11 FOR 9) AS hora,\n"
                    + "	CASE\n"
                    + "	 WHEN pdv.MOV_TPO_REGISTRO = 10 THEN 1\n"
                    + "	 ELSE 0\n"
                    + "	END cancelado,\n"
                    + "	p.PRO_UN_REFERENCIA AS unidade,\n"
                    + "	pdv.MOV_QTD_ITEM AS qtd,\n"
                    + "	pdv.MOV_VLR_UNIT AS valorunitario,\n"
                    + "	pdv.MOV_VLR_TOTAL AS valortotal,\n"
                    + "	pdv.MOV_DESCONTO_ITEM AS desconto\n"
                    + "FROM\n"
                    + "	TB_PDV_MOVOUTRA pdv\n"
                    + "JOIN TB_PRODUTOS p ON p.pro_id = pdv.PRO_ID\n"
                    + "WHERE\n"
                    + "	pdv.PRO_ID IS NOT NULL\n"
                    + "	AND pdv.MOV_LOJA = " + idLojaCliente + "\n"
                    + "	AND pdv.MOV_DT_MOVIMENTO BETWEEN '" + dataInicio + "' AND '" + dataTermino + "'";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
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
