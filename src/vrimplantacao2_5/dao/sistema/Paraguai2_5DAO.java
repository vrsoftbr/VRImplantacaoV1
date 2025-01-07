package vrimplantacao2_5.dao.sistema;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;

/**
 *
 * @author Wesley-Correa
 */
public class Paraguai2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Sistema-Paraguai";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TROCA,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV, // Libera produto para Venda no PDV
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.RECEITA,
                OpcaoProduto.PDV_VENDA // Habilita importacão de Vendas
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.MUNICIPIO
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " IMPU_CODIGO AS id,\n"
                    + " IMPU_DESC AS descricao,\n"
                    + " IMPU_PORCENTAJE AS cst,\n"
                    + " IMPU_PORC_BASE_IMPONIBLE AS aliq\n"
                    + "FROM ADCS.GEN_IMPUESTO "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            0)
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " s.SECC_CODIGO merc1,\n"
                    + " s.SECC_DESC desc_merc1,\n"
                    + " f.FLIA_CODIGO merc2,\n"
                    + " f.FLIA_DESC desc_merc2,\n"
                    + " g.GRUP_CODIGO merc3,\n"
                    + " g.GRUP_DESC desc_merc3,\n"
                    + " sb.SUGR_CODIGO merc4,\n"
                    + " sb.SUGR_DESC desc_merc4\n"
                    + "FROM ADCS.STK_CLAS_SUBGRUPO sb\n"
                    + "LEFT JOIN ADCS.STK_CLAS_GRUPO g ON g.GRUP_CODIGO = sb.SUGR_CODIGO\n"
                    + "LEFT JOIN ADCS.STK_CLAS_FAMILIA f ON f.FLIA_CODIGO = g.GRUP_FAMILIA\n"
                    + "LEFT JOIN ADCS.STK_CLAS_SECCION s ON s.SECC_CODIGO = f.FLIA_SECCION"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    imp.setMerc4ID(rst.getString("merc4"));
                    imp.setMerc4Descricao(rst.getString("desc_merc4"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    //                    "SELECT \n"
                    //                    + " LIN_CODIGO id,\n"
                    //                    + " LIN_DESC descricao\n"
                    //                    + "FROM ADCS.STK_LINEA"
                    "SELECT\n"
                    + "	m.MARC_CODIGO id,\n"
                    + "	m.MARC_DESC descricao\n"
                    + "FROM ADCS.STK_MARCA m "
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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " COBA_ART AS import_id,\n"
                    + " COBA_CODIGO_BARRA AS ean,\n"
                    + " COBA_TIPO AS qtd_embalagem,\n"
                    + " sa.ART_UNID_MED tipo_embalagem,\n"
                    + " sa.ART_DESC \n"
                    + "FROM ADCS.STK_ART_COD_BARRA c\n"
                    + "LEFT JOIN ADCS.STK_ARTICULO sa ON sa.ART_CODIGO = c.COBA_ART \n"
                    + "WHERE ART_UNID_MED <> 'KG'"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("import_id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem"));
                    imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + " p.ART_CODIGO id,\n"
                    + " p.ART_DESC descricao,\n"
                    + " p.ART_DESC_ABREV descricao_red,\n"
                    + " CASE WHEN p.ART_UNID_MED = 'KG' THEN 'KG' ELSE 'UN' END tipo_embalagem,\n"
                    + " p.ART_CONTENIDO qtd_minima,\n"
                    + " CASE WHEN p.ART_TIPO = 7 THEN 1 ELSE 0 END AS produto_balanca,\n"
                    + " pr.AREM_PRECIO_VTA preco_venda,\n"
                    + " pr.AREM_PRECIO_VTA_CAJA preco_comum,\n"
                    + " pr.AREM_COSTO_BRUTO custo_com_imposto,\n"
                    + " pr.AREM_COSTO_NETO custo_sem_imposto,\n"
                    + " pr.AREM_EXIST_MIN,\n"
                    + " pr.AREM_EXIST_MAX,\n"
                    + " b.COBA_CODIGO_BARRA ean,\n"
                    + " s.SECC_CODIGO merc1,\n"
                    + " s.SECC_DESC desc_merc1,\n"
                    + " f.FLIA_CODIGO merc2,\n"
                    + " f.FLIA_DESC  desc_merc2,\n"
                    + " g.GRUP_CODIGO merc3,\n"
                    + " g.GRUP_DESC desc_merc3,\n"
                    + " sb.SUGR_CODIGO merc4,\n"
                    + " sb.SUGR_DESC desc_merc4,\n"
                    + " p.ART_MARCA familia_produto,\n"
                    + " p.ART_IMPU,\n"
                    + " CASE WHEN p.ART_IVA_PORCENTAJE = 0 THEN 1 \n"
                    + " WHEN p.ART_IVA_PORCENTAJE = 10 THEN 2 \n"
                    + " WHEN p.ART_IVA_PORCENTAJE = 5 THEN 3 \n"
                    + " ELSE 1 END AS iva,\n"
                    + " CASE WHEN p.ART_EST = 'A' THEN 1 ELSE 0 END situacao_cadastro,\n"
                    + " p.FEC_INSERCION data_cadastro,\n"
                    + " p.FEC_MODIFICACION data_alteracao,\n"
                    + " e.ARDE_CANT_ACT estoque_atual, -- (entrada - saida) + inicial\n"
                    + " p.ART_IMPU icms\n"
                    + "FROM ADCS.STK_ARTICULO p\n"
                    + "LEFT JOIN ADCS.STK_ART_COD_BARRA b ON b.COBA_ART = p.ART_CODIGO AND b.COBA_DESC != 'PESABLE' AND b.COBA_DESC != 'UNITARIO'\n"
                    + "LEFT JOIN ADCS.STK_CLAS_SUBGRUPO sb ON sb.SUGR_CODIGO = p.ART_CLAS_SUBGRUPO\n"
                    + "LEFT JOIN ADCS.STK_CLAS_GRUPO g ON g.GRUP_CODIGO = sb.SUGR_CODIGO\n"
                    + "LEFT JOIN ADCS.STK_CLAS_FAMILIA f ON f.FLIA_CODIGO = g.GRUP_FAMILIA\n"
                    + "LEFT JOIN ADCS.STK_CLAS_SECCION s ON s.SECC_CODIGO = f.FLIA_SECCION\n"
                    + "LEFT JOIN ADCS.STK_ARTICULO_DEPOSITO e ON e.ARDE_ART = p.ART_CODIGO\n"
                    + "LEFT JOIN ADCS.STK_ARTICULO_EMPRESA pr ON pr.AREM_ART = p.ART_CODIGO"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
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
                        imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagemCotacao(rst.getString("tipo_embalagem"));

                    }

//                    imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));

                    imp.setPrateleira(rst.getString("qtd_minima"));//QUANTIDADE MÍNIMA
                    imp.setCustoAnteriorComImposto(rst.getDouble("preco_comum"));//PREÇO COMUM

//                    imp.seteBalanca(rst.getBoolean("produto_balanca"));
                    imp.setCustoComImposto(rst.getDouble("custo_com_imposto"));
                    imp.setCustoSemImposto(rst.getDouble("custo_sem_imposto"));
                    imp.setPrecovenda(rst.getDouble("preco_venda"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
                    imp.setIdFamiliaProduto(rst.getString("familia_produto"));

                    imp.setSituacaoCadastro(rst.getInt("situacao_cadastro"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setEstoque(rst.getDouble("estoque_atual"));

//                    imp.setNcm(rst.getString(""));
                    String idIcmsDebito = rst.getString("iva");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoNfId(idIcmsDebito);

                    imp.setIcmsCreditoId(idIcmsDebito);
                    imp.setIcmsCreditoForaEstadoId(idIcmsDebito);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	o.OFER_EMPR id,\n"
                    + "	o.OFER_FEC_INI data_inicio,\n"
                    + "	o.OFER_FEC_FIN data_fim,\n"
                    + "	sa.AREM_PRECIO_VTA preco_atual,\n"
                    + "	o.OFER_PRECIO preco_oferta\n"
                    + "FROM\n"
                    + "	FAC_OFERTA o\n"
                    + "	LEFT JOIN ADCS.STK_ARTICULO_EMPRESA sa ON sa.AREM_ART = o.OFER_ART "
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("id"));
                    imp.setDataInicio(rst.getDate("data_inicio"));
                    imp.setDataFim(rst.getDate("data_fim"));
                    imp.setPrecoNormal(rst.getDouble("preco_atual"));
                    imp.setPrecoOferta(rst.getDouble("preco_oferta"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    imp.setId(rst.getString(""));
                    imp.setQtdEmbalagem(rst.getInt(""));
                    imp.setProdutoAssociadoId(rst.getString(""));
                    imp.setQtdEmbalagemItem(rst.getInt(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());

                    imp.setImportid(rst.getString(""));
                    imp.setIdproduto(rst.getString(""));
                    imp.setDescricao(rst.getString(""));
                    imp.setRendimento(rst.getDouble(""));
                    imp.setQtdembalagemreceita(rst.getInt(""));
                    imp.setQtdembalagemproduto(1);
                    imp.setFator(1);
                    imp.setFichatecnica("");
                    imp.getProdutos().add(rst.getString(""));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " f.PROV_CODIGO id,\n"
                    + " f.PROV_RAZON_SOCIAL razao,\n"
                    + " f.PROV_DIR endereco,\n"
                    + " f.PROV_TEL telefone,\n"
                    + " f.PROV_CELULAR celular,\n"
                    + "CASE \n"
                    + "        WHEN gp.PAIS_CODIGO = 1 THEN 5860 \n"
                    + "        ELSE 1058 \n"
                    + "    END AS pais,\n"
                    + " f.PROV_RUC ruc,\n"
                    + " CASE WHEN  f.PROV_EST_PROV = 'A' THEN 1 ELSE 0 END AS ativo,\n"
                    + " f.PROV_EMAIL,\n"
                    + " f.PROV_CELULAR,\n"
                    + " gc.CIUD_DESC municipio,\n"
                    + "CASE \n"
                    + " WHEN gc.CIUD_DESC= 'Ponta Porã' THEN 5006606 \n"
                    + " WHEN gc.CIUD_DESC= 'Asunción' THEN 9999991 \n"
                    + " WHEN gc.CIUD_DESC= 'Pedro Juan Caballero' THEN 9999992 \n"
                    + " WHEN gc.CIUD_DESC= 'Concepción' THEN 9999993 \n"
                    + " WHEN gc.CIUD_DESC= 'Ciudad del Este' THEN 9999994\n"
                    + " WHEN gc.CIUD_DESC= 'Dourados' THEN 5003702\n"
                    + " ELSE 9999992 END AS municipioibge,\n"
                    + "   CASE \n"
                    + " WHEN gc.CIUD_DESC= 'Ponta Porã' THEN 'MS' \n"
                    + " WHEN gc.CIUD_DESC= 'Asunción' THEN 'EX'\n"
                    + " WHEN gc.CIUD_DESC= 'Pedro Juan Caballero' THEN 'EX'\n"
                    + " WHEN gc.CIUD_DESC= 'Concepción' THEN 'EX' \n"
                    + " WHEN gc.CIUD_DESC= 'Ciudad del Este' THEN 'EX'\n"
                    + " WHEN gc.CIUD_DESC= 'Dourados' THEN 'MS'\n"
                    + " ELSE 'EX'END AS uf,"
                    + " f.PROV_NOMBRE_2,\n"
                    + " f.PROV_CNPJ cnpj,\n"
                    + " f.PROV_INSC_ESTADUAL inscricao_estadual,\n"
                    + " f.PROV_OBS obs\n"
                    + "FROM ADCS.FIN_PROVEEDOR f\n"
                    + "LEFT JOIN ADCS.GEN_CIUDAD gc ON gc.CIUD_CODIGO = f.PROV_CIUDAD\n"
                    + "LEFT JOIN ADCS.GEN_ESTADO_DEP ged ON ged.ESTD_CODIGO = f.PROV_ESTADO_DEP \n"
                    + "LEFT JOIN ADCS.GEN_PAIS gp ON gp.PAIS_CODIGO = f.PROV_PAIS"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj_cpf(rst.getString("ruc"));
                    imp.setIe_rg(rst.getString("inscricao_estadual"));
                    imp.setIdPais(rst.getInt("pais"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setMunicipio(Utils.acertarTexto(rst.getString("municipio")));
                    imp.setCob_ibge_municipio(rst.getInt("municipioibge"));
                    imp.setUf(rst.getString("uf"));

                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setObservacao(rst.getString("cnpj") == null ? rst.getString("ruc") : rst.getString("cnpj"));
                    imp.setTel_principal(rst.getString("telefone"));
                    imp.addCelular(rst.getString("razao"), rst.getString("celular"));

                    imp.addContato(rst.getString("razao"),
                            rst.getString("telefone"),
                            rst.getString("celular"),
                            TipoContato.COMERCIAL, "");

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " PRPR_PROV AS id_fornecedor,\n"
                    + " PRPR_ART AS id_produto,\n"
                    + "  PRPR_UNID_MED_PROV AS codigo_externo,\n"
                    + " PRPR_CANT_MIN_EXIGIDO_PROV AS qtd_embralagem\n"
                    + "FROM ADCS.STK_ART_PROVEEDOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setCodigoExterno(rst.getString("codigo_externo"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd_embralagem"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdFornecedor(rst.getString(""));
                    imp.setNumeroDocumento(rst.getString(""));
                    imp.setDataEmissao(rst.getDate(""));
                    imp.setDataEntrada(imp.getDataEmissao());
                    imp.addVencimento(rst.getDate(""), rst.getDouble(""), rst.getString(""));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaReceberIMP imp = new ContaReceberIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdFornecedor(rst.getString(""));
                    imp.setDataEmissao(rst.getDate(""));
                    imp.setDataVencimento(rst.getDate(""));
                    imp.setValor(rst.getDouble(""));
                    imp.setObservacao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	CLI_CODIGO AS id,\n"
                    + "	CLI_NOM AS razao,\n"
                    + "	CLI_NOM_FANTASIA AS fantasia,\n"
                    + "	CLI_DOC_IDENT_PROPIETARIO AS identidade,\n"
                    + "CASE WHEN CLI_IND_PAGO_CON_CHEQUE = 'S' THEN 1 ELSE 0 END AS cheque,"
                    + "	CLI_RUC cnpj,\n"
                    + "	CLI_DIR AS endereco,\n"
                    + "	CLI_BARRIO AS bairro,\n"
                    + "	fz.ZONA_DESC AS municipio,\n"
                    + "	CLI_FEC_INGRESO AS data_cadastro,\n"
                    + " CAST(CLI_IMP_LIM_CR AS varchar(50)) limite,\n"
                    + "	CASE WHEN CLI_EST_CLI = 'A' THEN 1 ELSE 0 END AS status,\n"
                    + "	CLI_TEL AS telefone,\n"
                    + "CLI_TEL_PARTICULAR AS telefone2,\n"
                    + "	CLI_OBS AS observacao\n"
                    + "FROM\n"
                    + "	ADCS.FIN_CLIENTE\n"
                    + "	LEFT JOIN ADCS.FAC_ZONA fz ON CLI_ZONA = fz.ZONA_CODIGO"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setInscricaoestadual(rst.getString("identidade"));
                    imp.setCnpj(rst.getString("cnpj"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));

                    imp.setLimiteCompra(rst.getDouble("limite"));
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setAtivo(rst.getBoolean("status"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("cnpj"));
                    imp.setCelular(rst.getString("telefone2"));
                    imp.setPermiteCheque(rst.getBoolean("cheque"));
                    imp.setPermiteCreditoRotativo(true);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	fd.DOC_CLAVE id,\n"
                    + "	fd.DOC_NRO_DOC numero_cupom,\n"
                    + "	fd.DOC_CLI id_cliente,\n"
                    + "	fd.DOC_SALDO_INI_MON valor,\n"
                    + "	fd.DOC_OBS obs\n"
                    + "FROM ADCS.FIN_DOCUMENTO fd \n"
                    + "LEFT JOIN ADCS.FIN_CUOTA f2 ON f2.CUO_CLAVE_DOC = fd.DOC_CLAVE\n"
                    + "LEFT JOIN ADCS.FIN_PAGO fp ON fp.PAG_CLAVE_DOC = fd.DOC_CLAVE\n"
                    + "LEFT JOIN ADCS.FIN_CLIENTE fc ON fc.CLI_CODIGO = fd.DOC_CLI \n"
                    + "WHERE fd.DOC_CLI IS NOT NULL \n"
                    + "AND fd.DOC_SALDO_INI_MON IS not null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setNumeroCupom(Utils.formataNumero(rst.getString("numero_cupom")));
                    imp.setIdCliente(rst.getString("id_cliente"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	f.CHEQ_CLAVE id,\n"
                    + "	f.CHEQ_FEC_DEPOSITAR data_deposito,\n"
                    + "	f.CHEQ_NRO numero_cheque,\n"
                    + "	f.CHEQ_FEC_EMIS data_emis,\n"
                    + "	fb.BCO_CODIGO cod_banco,\n"
                    + "	fb.BCO_DESC desc_banco,\n"
                    + "	fc.CLI_NOM nome_cliente,\n"
                    + "	fc.CLI_TEL teledone_cliente,\n"
                    + "	f.CHEQ_IMPORTE valor,\n"
                    + "	f.CHEQ_SERIE serie_cheque\n"
                    + "FROM\n"
                    + "	ADCS.FIN_CHEQUE f\n"
                    + "	 LEFT JOIN ADCS.FIN_BANCO fb ON fb.bco_codigo = f.cheq_bco\n"
                    + "	 LEFT JOIN ADCS.FIN_CLIENTE fc ON fc.CLI_CODIGO = f.CHEQ_CLI "
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataDeposito(rst.getDate("data_deposito"));
                    imp.setNumeroCheque(rst.getString("numero_cheque"));
                    imp.setDate(rst.getDate("data_emis"));
                    imp.setBanco(rst.getInt("cod_banco"));
                    imp.setAgencia(rst.getString(""));
                    imp.setConta(rst.getString(""));
                    imp.setNome(rst.getString("nome_cliente"));
                    imp.setTelefone(rst.getString("teledone_cliente"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("serie_cheque"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString(""));
                    imp.setCnpj(rst.getString(""));
                    imp.setInscricaoEstadual(rst.getString(""));
                    imp.setRazao(rst.getString(""));
                    imp.setEndereco(rst.getString(""));
                    imp.setNumero(rst.getString(""));
                    imp.setBairro(rst.getString(""));
                    imp.setMunicipio(rst.getString(""));
                    imp.setUf(rst.getString(""));
                    imp.setCep(rst.getString(""));
                    imp.setTelefone(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rst.getString(""));
                    imp.setNome(rst.getString(""));
                    imp.setIdEmpresa(rst.getString(""));
                    imp.setCnpj(rst.getString(""));
                    imp.setConvenioLimite(rst.getDouble(""));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rst.getInt("") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString(""));
                    imp.setIdConveniado(rst.getString(""));
                    imp.setNumeroCupom(rst.getString(""));
                    imp.setDataHora(rst.getTimestamp(""));
                    imp.setValor(rst.getDouble(""));
                    imp.setObservacao(rst.getString(""));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static class VendaIterator implements Iterator<VendaIMP> {

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(rst.getInt("id_venda"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

//                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
//                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(rst.getDate("dataInicio"));
                        next.setHoraTermino(rst.getDate("dataTermino"));
                        next.setSubTotalImpressora(rst.getDouble("subtotal"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("DD/MM/YYYY").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("DD/MM/YYYY").format(dataTermino);
            this.sql
                    = "SELECT \n"
                    + "TICK_CLAVE AS id_venda,\n"
                    + "TICK_CLAVE || TICK_CLAVE_Y AS numero_cupom,\n"
                    + "TICK_NRO_TICK ecf,\n"
                    + "TICK_FEC_EMIS AS \"data\",\n"
                    + "TICK_FEC_HORA_INI AS dataInicio,\n"
                    + "TICK_FEC_HORA_FIN AS dataTermino,\n"
                    + "TICK_IMP AS subtotal,\n"
                    + "CASE WHEN TICK_ESTADO = 'A' THEN 1 ELSE 0 END cancelado\n"
                    + "FROM POS.POS_TICKET_HIST\n"
                    + "WHERE TICK_FEC_EMIS >= '" + DATE_FORMAT.format(dataInicio) + "' AND \n"
                    + "TICK_FEC_EMIS <= '" + DATE_FORMAT.format(dataTermino) + "'";
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

        private Statement stm = ConexaoOracle.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item_venda"));
//                        next.setSequencia(rst.getInt(""));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigo_barra"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("preco_venda"));
//                        next.setTotalBruto(rst.getDouble(""));
//                        next.setCancelado(rst.getBoolean(""));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT \n"
                    + "DETT_CLAVE_TICK||DETT_ITEM id_item_venda,\n"
                    + "DETT_CLAVE_TICK id_venda,\n"
                    + "pa.ART_CODIGO id_produto,\n"
                    + "CASE WHEN DETT_PRECIO_TIPO = 'U' THEN 'UN' ELSE 'KG' END AS unidade,\n"
                    + "DETT_ART_COD_BARRA codigo_barra,\n"
                    + "pa.ART_DESC descricao,\n"
                    + "DETT_CANT quantidade,\n"
                    + "DETT_PRECIO_VENTA preco_venda\n"
                    + "FROM POS.POS_TICKET_DET_HIST v\n"
                    + "LEFT JOIN POS.POS_TICKET_HIST vi ON vi.TICK_CLAVE = v.DETT_CLAVE_TICK \n"
                    + "LEFT JOIN POS.POS_ARTICULO pa ON pa.ART_COD_BARRA = v.DETT_ART_COD_BARRA\n"
                    + "WHERE vi.TICK_FEC_EMIS >= '" + DATE_FORMAT.format(dataInicio) + "' AND \n"
                    + "vi.TICK_FEC_EMIS <= '" + DATE_FORMAT.format(dataTermino) + "'";
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
