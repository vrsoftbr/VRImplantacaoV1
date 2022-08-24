package vrimplantacao2.dao.interfaces;

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
//import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao2_5.dao.conexao.ConexaoOracle;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class SiacDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Siac";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO,
                    //OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
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
                    OpcaoProduto.MAPA_TRIBUTACAO,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select empresa_id, fantasia from empresas order by empresa_id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("empresa_id"), rst.getString("fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grupo_icms_id, descricao from grupo_icms order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("grupo_icms_id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    /*@Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> result = new LinkedHashMap<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select grupo_id, descricao from grupos order by grupo_id"
            )) {
                while (rst.next()) {
                    String[] ids = rst.getString("grupo_id").split("\\.");

                    if (ids.length == 1) {
                        if (!result.containsKey(ids[0])) {
                            MercadologicoNivelIMP imp = new MercadologicoNivelIMP(ids[0], rst.getString("descricao"));
                            result.put(imp.getId(), imp);
                        }
                    } else if (ids.length == 2) {
                        MercadologicoNivelIMP pai = result.get(ids[0]);
                        pai.addFilho(ids[1], rst.getString("descricao"));
                    }
                }
            }
        }

        return new ArrayList<>(result.values());
    }*/
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "  g.grupo_id merc1, \n"
                    + "  g.descricao desc1,\n"
                    + "  substr(sg.grupo_id,6,8) merc2,\n"
                    + "  sg.DESCRICAO desc2\n"
                    + " FROM grupos g\n"
                    + " LEFT JOIN grupos sg ON substr(sg.grupo_id,1,4) = g.GRUPO_ID AND LENGTH(sg.GRUPO_ID) > 4\n"
                    + " WHERE \n"
                    + "  LENGTH(g.GRUPO_ID) = 4\n"
                    + " ORDER BY 1,3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
                    imp.setMerc3Descricao(imp.getMerc2Descricao());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select familia_id, descricao from produtos_familias order by familia_id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("familia_id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "WITH ean AS (\n"
                    + "SELECT\n"
                    + "	p.produto_id,\n"
                    + "	p.codigo_barra ean,\n"
                    + "	p.fator_mutiplicacao qtd_embalagem\n"
                    + "FROM produtos p\n"
                    + "WHERE\n"
                    + "	NOT NULLIF(trim(p.codigo_barra),'') IS NULL\n"
                    + "UNION\n"
                    + "SELECT\n"
                    + "	ean.produto_id,\n"
                    + "	ean.codigo_barra,\n"
                    + "	1 qtd_embalagem\n"
                    + "FROM codigo_barras ean\n"
                    + ")\n"
                    + "SELECT DISTINCT\n"
                    + "  p.produto_id id,\n"
                    + "  p.dt_cadastro datacadastro,\n"
                    + "  ean.ean,\n"
                    + "  p.emb_fracionada qtd_cotacao,\n"
                    + "  ean.qtd_embalagem,\n"
                    + "  p.unidade,\n"
                    + "  case when p.permite_venda_fracionada = 'S' then 1 else 0 end e_balanca,\n"
                    + "  case when p.pesavel = 'S' then 1 else 0 end pesavel,\n"
                    + "  p.nome_produto descricaocompleta,\n"
                    + "  coalesce(nullif(trim(p.breve_descricao),''), p.nome_produto) descricaoreduzida,\n"
                    + "  p.validade,\n"
                    + "  coalesce(p.grupo_id, '') grupo_id,\n"
                    + "  trim(substr(p.grupo_id,1,4)) mercid1,\n"
                    + "  trim(substr(p.grupo_id,6,8)) mercid2,\n"
                    + "  p.familia_id,\n"
                    + "  p.peso_unidade pesobruto,\n"
                    + "  p.peso_unidade_liquido pesoliquido,\n"
                    + "  est.estoque_atual,\n"
                    + "  est.estoque_minimo,\n"
                    + "  coalesce(p.perc_lucro, 0) margem,\n"
                    + "  p.custo_compra custosemimposto,\n"
                    + "  p.custo_venda preco,\n"
                    + "  case p.ativo when 'S' then 1 else 0 end situacaocadastro,\n"
                    + "  p.codigo_fiscal ncm,\n"
                    + "  pe.codigo_cest cest,\n"
                    + "  p.codigo_natureza_prod_pis pis_natureza_rec,\n"
                    + "  pe.grupo_icms_id id_icms,\n"
                    + "  p.grupo_pis_id,\n"
                    + "  p.codigo_fabrica id_fabricante,\n"
                    + "  CASE WHEN pe.NEW_GRUPO_PIS_COFINS_ID = 11 THEN 5\n"
                    + "       WHEN pe.NEW_GRUPO_PIS_COFINS_ID = 13 THEN 8\n"
                    + "       WHEN pe.NEW_GRUPO_PIS_COFINS_ID = 1 THEN 1\n"
                    + "       WHEN pe.NEW_GRUPO_PIS_COFINS_ID = 5 THEN 6\n"
                    + "       WHEN pe.NEW_GRUPO_PIS_COFINS_ID = 3 THEN 4\n"
                    + "       WHEN pe.NEW_GRUPO_PIS_COFINS_ID = 6 THEN 9\n"
                    + "  ELSE 7 END piscofins_saida,\n"
                    //+ "  pis_s.cst_pis piscofins_saida,\n"
                    + "  case when p.bloquear_venda = 'N' then 1 else 0 end vendapdv,\n"
                    + "  case when p.exibir_sugestao_compras = 'S' then 1 else 0 end sugestaocotacao,\n"
                    + "  case when p.descontinuado = 'S' then 1 else 0 end descontinuado\n"
                    + "from\n"
                    + "  produtos p\n"
                    + "  join empresas emp on emp.empresa_id = '" + getLojaOrigem() + "'\n"
                    + "  join produtos_empresas pe on pe.produto_id = p.produto_id and pe.empresa_id = emp.empresa_id\n"
                    + "  join estoques est on est.produto_id = p.produto_id and est.empresa_id = emp.empresa_id\n"
                    + "  left join ean on p.produto_id = ean.produto_id\n"
                    + "  left join new_grupo_piscofins pis on pis.grupo_piscofins_id = pe.new_grupo_pis_cofins_id\n"
                    // + "  left join new_itens_grupo_piscofins pis_e on pis_e.grupo_pis_id = pis.grupo_piscofins_id \n"
                    // + "  			and pis_e.movimento = 'E' \n"
                    //+ "  left join new_itens_grupo_piscofins pis_s on pis_s.grupo_pis_id = pis.grupo_piscofins_id \n"
                    //+ "  			and pis_s.movimento = 'S' and pis_s.cfop = 'Todos' \n"
                    + "order by e_balanca desc, id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEan(rst.getString("ean"));
                    //imp.setQtdEmbalagemCotacao(rst.getInt("qtd_cotacao"));
                    imp.setQtdEmbalagem(rst.getInt("qtd_embalagem"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtd_embalagem"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        if (rst.getBoolean("e_balanca")) {
                            imp.seteBalanca(true);
                            if (rst.getBoolean("pesavel")) {
                                imp.setTipoEmbalagem("KG");
                            } else {
                                imp.setTipoEmbalagem("UN");
                            }
                        } else {
                            imp.seteBalanca(false);
                            imp.setTipoEmbalagem(rst.getString("unidade"));
                            imp.setTipoEmbalagemVolume(rst.getString("unidade"));
                            imp.setTipoEmbalagemCotacao(rst.getString("unidade"));
                        }
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setValidade(rst.getInt("validade"));

                    /*String[] ids = rst.getString("grupo_id").split("\\.");
                    if (ids.length > 0) {
                        imp.setCodMercadologico1(ids[0]);
                        if (ids.length > 1) {
                            imp.setCodMercadologico2(ids[1]);
                            imp.setCodMercadologico3(ids[1]);
                        }
                    }*/
                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid2"));

                    imp.setIdFamiliaProduto(rst.getString("familia_id"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoque(rst.getDouble("estoque_atual"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("pis_natureza_rec"));

                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(rst.getString("id_icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("id_icms"));

                    imp.setFornecedorFabricante(rst.getString("id_fabricante"));
                    imp.setVendaPdv(rst.getBoolean("vendapdv"));
                    imp.setSugestaoCotacao(rst.getBoolean("sugestaocotacao"));
                    imp.setSugestaoPedido(rst.getBoolean("sugestaocotacao"));
                    imp.setDescontinuado(rst.getBoolean("descontinuado"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        String sql;
        sql = "WITH ean AS (\n"
                + "SELECT\n"
                + "	p.produto_id,\n"
                + "	p.codigo_barra ean,\n"
                + "	p.fator_mutiplicacao qtd_embalagem\n"
                + "FROM produtos p\n"
                + "WHERE\n"
                + "	NOT NULLIF(trim(p.codigo_barra),'') IS NULL\n"
                + "UNION\n"
                + "SELECT\n"
                + "	ean.produto_id,\n"
                + "	ean.codigo_barra ean,\n"
                + "	1 qtd_embalagem\n"
                + "FROM codigo_barras ean\n"
                + ")\n"
                + "SELECT produto_id, ean, qtd_embalagem FROM ean WHERE LENGTH(ean)>=13";
//        if (geraCodigoAtacado) {
//            sql = "select \n"
//                    + " id,\n"
//                    + " qtd_minimapv2 qtde\n"
//                    + "from produto\n"
//                    + "where qtd_minimapv2 > 1";
//        }

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(sql)) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

//                    if (geraCodigoAtacado) {
//                        String sistema = (getSistema() + " - " + getLojaOrigem());
//                        int codigoAtual = new ProdutoAnteriorDAO().getCodigoAnterior2(sistema, getLojaOrigem(), rs.getString("id"));
//
//                        if (codigoAtual > 0) {
//                            imp.setImportId(rs.getString("id"));
//                            imp.setEan("99999" + String.valueOf(codigoAtual));
//                            imp.setQtdEmbalagem(rs.getInt("qtde"));
//                            result.add(imp);
//                        }
//
//                    } else {
                    imp.setImportId(rs.getString("produto_id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtd_embalagem"));

                    result.add(imp);
//                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  f.cadastro_id id,\n"
                    + "  f.razao_social razao,\n"
                    + "  f.fantasia,\n"
                    + "  f.cadastro_id cnpj,\n"
                    + "  f.insc_estadual,\n"
                    + "  f.codigo_cliente,\n"
                    + "  case when upper(f.situacao) != 'NORMAL' then 0 else 1 end ativo,\n"
                    + "  f.endereco_fat,\n"
                    + "  f.numero_end_fat,\n"
                    + "  f.complemento_end_fat,\n"
                    + "  f.bairro_fat,\n"
                    + "  cid.nome municipio_fat,\n"
                    + "  cid.estado_id uf_fat,\n"
                    + "  f.cep_fat,\n"
                    + "  f.endereco_cob,\n"
                    + "  f.numero_end_cob,\n"
                    + "  f.complemento_end_cob,\n"
                    + "  f.bairro_cob,\n"
                    + "  cid.nome municipio_cob,\n"
                    + "  cid.estado_id uf_cob,\n"
                    + "  f.cep_cob,\n"
                    + "  f.ddd_fat,\n"
                    + "  f.fone_voz_fat,\n"
                    + "  f.fone_dados_fat,\n"
                    + "  f.fone_fax_fat,\n"
                    + "  f.fone_outros,\n"
                    + "  f.dt_cadastro,\n"
                    + "  f.tipo_cadastro\n"
                    + "from\n"
                    + "  cadastros f\n"
                    + "  left join cidades cid on\n"
                    + "       f.cidade_fat_id = cid.cidade_id\n"
                    + "  left join cidades cob on\n"
                    + "       f.cidade_cob_id = cob.cidade_id\n"
                    + "where\n"
                    + "  f.tipo_cadastro in ('F','I','A','T','B','E','D')\n"
                    + "order by\n"
                    + "  f.razao_social"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("insc_estadual"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco_fat"));
                    imp.setNumero(rst.getString("numero_end_fat"));
                    imp.setComplemento(rst.getString("complemento_end_fat"));
                    imp.setBairro(rst.getString("bairro_fat"));
                    imp.setMunicipio(rst.getString("municipio_fat"));
                    imp.setUf(rst.getString("uf_fat"));
                    imp.setCep(rst.getString("cep_fat"));
                    imp.setCob_endereco(rst.getString("endereco_cob"));
                    imp.setCob_numero(rst.getString("numero_end_cob"));
                    imp.setCob_complemento(rst.getString("complemento_end_cob"));
                    imp.setCob_bairro(rst.getString("bairro_cob"));
                    imp.setCob_municipio(rst.getString("municipio_cob"));
                    imp.setCob_uf(rst.getString("uf_cob"));
                    imp.setCob_cep(rst.getString("cep_cob"));
                    String ddd = Utils.stringLong(rst.getString("ddd_fat"));
                    imp.setTel_principal(ddd + Utils.stringLong(rst.getString("fone_voz_fat")));
                    imp.addTelefone("DADOS", ddd + Utils.stringLong(rst.getString("fone_dados_fat")));
                    imp.addTelefone("FAX", ddd + Utils.stringLong(rst.getString("fone_fax_fat")));
                    imp.addTelefone("OUTROS", ddd + Utils.stringLong(rst.getString("fone_outros")));
                    imp.setDatacadastro(rst.getDate("dt_cadastro"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select produto_id, fornecedor_id, codigo_produto_no_fornecedor from siac_produtos_fornecedores order by 1,2"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("fornecedor_id"));
                    imp.setIdProduto(rst.getString("produto_id"));
                    imp.setCodigoExterno(rst.getString("codigo_produto_no_fornecedor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.CEP,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  ca.cadastro_id id,\n"
                    + "  ca.cadastro_id cnpj,\n"
                    + "  ca.insc_estadual inscricaoestadual,\n"
                    + "  ca.orgao_expedidor orgaoemissor,\n"
                    + "  ca.razao_social,\n"
                    + "  ca.fantasia,\n"
                    + "  case when coalesce(upper(ca.situacao),'') in ('NORMAL','') then 1 else 0 end ativo,\n"
                    + "  case when coalesce(upper(ca.situacao),'') in ('SUSPENSO') then 1 else 0 end bloqueado,\n"
                    + "  ca.endereco_fat,\n"
                    + "  ca.numero_end_fat,\n"
                    + "  ca.complemento_end_fat,\n"
                    + "  ca.bairro_fat,\n"
                    + "  cd.nome municipio,\n"
                    + "  cd.estado_id uf,\n"
                    + "  ca.cep_fat,\n"
                    + "  ca.endereco_cob,\n"
                    + "  ca.numero_end_cob,\n"
                    + "  ca.complemento_end_cob,\n"
                    + "  ca.bairro_cob,\n"
                    + "  cd1.nome municipio_cob,\n"
                    + "  cd1.estado_id uf_cob,\n"
                    + "  ca.cep_cob,\n"
                    + "  ca.estado_civil,\n"
                    + "  ca.dt_nascimento,\n"
                    + "  ca.dt_cadastro,\n"
                    + "  ca.sexo,\n"
                    + "  ca.ddd_trabalho,\n"
                    + "  ca.fone_voz_trabalho,\n"
                    + "  ca.cargo,\n"
                    + "  ca.renda_liquida,\n"
                    + "  ca.limite_credito,\n"
                    + "  ca.nome_conjuge,\n"
                    + "  ca.nome_pai,\n"
                    + "  ca.nome_mae,\n"
                    + "  ca.dia_faturar diavencimento,\n"
                    + "  ca.e_mail,\n"
                    + "  ca1.email_compras,\n"
                    + "  ca1.email_financeiro,\n"
                    + "  ca1.email_vendas,\n"
                    + "  ca.ddd_fat,\n"
                    + "  ca.fone_dados_fat,\n"
                    + "  ca.fone_fax_fat,\n"
                    + "  ca.fone_outros,\n"
                    + "  ca.fone_voz_fat\n"
                    + "from\n"
                    + "  cadastros ca\n"
                    + "  left join cadastros1 ca1 on\n"
                    + "       ca.cadastro_id = ca1.cadastro_id and\n"
                    + "       ca.compl_cadastro_id = ca1.compl_cadastro_id\n"
                    + "  left join cidades cd on\n"
                    + "       cd.cidade_id = ca.cidade_fat_id\n"
                    + "  left join cidades cd1 on\n"
                    + "       cd1.cidade_id = ca.cidade_cob_id\n"
                    + "where\n"
                    + "       ca.tipo_cadastro in ('A','C','O','D','E')\n"
                    + "order by\n"
                    + "      id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));
                    imp.setEndereco(rst.getString("endereco_fat"));
                    imp.setNumero(rst.getString("numero_end_fat"));
                    imp.setComplemento(rst.getString("complemento_end_fat"));
                    imp.setBairro(rst.getString("bairro_fat"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep_fat"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cob"));
                    imp.setCobrancaNumero(rst.getString("numero_end_cob"));
                    imp.setCobrancaComplemento(rst.getString("complemento_end_cob"));
                    imp.setCobrancaBairro(rst.getString("bairro_cob"));
                    imp.setCobrancaMunicipio(rst.getString("municipio_cob"));
                    imp.setCobrancaUf(rst.getString("uf_cob"));
                    imp.setCobrancaCep(rst.getString("cep_cob"));
                    //imp.set(rst.getString("estado_civil"));
                    imp.setDataNascimento(rst.getDate("dt_nascimento"));
                    imp.setDataCadastro(rst.getDate("dt_cadastro"));
                    imp.setSexo("F".equals(rst.getString("sexo")) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setEmpresaTelefone(formatarTelefone(rst.getString("ddd_trabalho"), rst.getString("fone_voz_trabalho")));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("renda_liquida"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
                    imp.setNomeConjuge(rst.getString("nome_conjuge"));
                    imp.setNomePai(rst.getString("nome_pai"));
                    imp.setNomeMae(rst.getString("nome_mae"));
                    imp.setDiaVencimento(rst.getInt("diavencimento"));
                    imp.setEmail(rst.getString("e_mail"));
                    imp.addEmail("COMPRAS", rst.getString("email_compras"), TipoContato.COMERCIAL);
                    imp.addEmail("FINANCEIRO", rst.getString("email_financeiro"), TipoContato.FINANCEIRO);
                    imp.addEmail("VENDAS", rst.getString("email_vendas"), TipoContato.COMERCIAL);
                    imp.setTelefone(formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_voz_fat")));
                    imp.addTelefone("FAX", formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_fax_fat")));
                    imp.addTelefone("OUTROS", formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_outros")));
                    imp.addTelefone("DADOS", formatarTelefone(rst.getString("ddd_fat"), rst.getString("fone_dados_fat")));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private String formatarTelefone(String ddd, String numero) {
        numero = Utils.stringLong(numero);
        if ("0".equals(numero)) {
            return "";
        }
        return Utils.stringLong(ddd) + numero;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  f.empresa_id||'-'||f.tipo_conta||'-'||f.tipo_doc||'-'||f.cadastro_id||'-'||f.compl_cadastro_id||'-'||f.documento_id id,\n"
                    + "  f.dt_emissao,\n"
                    + "  f.documento_id numerocupom,\n"
                    + "  f.vlr_titulo valor,\n"
                    + "  f.cadastro_id cliente_id,\n"
                    + "  f.dt_vencto vencimento,\n"
                    + "  f.parcela,\n"
                    + "  f.vlr_juros,\n"
                    + "  f.cadastro_id cnpj,\n"
                    + "  f.status\n"
                    + "from\n"
                    + "  financeiro f\n"
                    + "where\n"
                    + "  f.empresa_id = '" + getLojaOrigem() + "' and\n"
                    + "  f.tipo_conta = 'CR' and\n"
                    + "  f.status = 'A' and\n"
                    + "  f.cadastro_id != '111.111.111/11'\n"
                    + "  and f.dt_emissao > to_date('03/04/2018', 'dd/MM/yyyy')"
                    + "order by\n"
                    + "  f.dt_emissao"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dt_emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setIdCliente(rst.getString("cliente_id"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setParcela(Utils.stringToInt(rst.getString("parcela")));
                    imp.setJuros(rst.getDouble("vlr_juros"));
                    imp.setCnpjCliente(rst.getString("cnpj"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  p.produto_id,\n"
                    + "  p.dt_inicio,\n"
                    + "  p.dt_final,\n"
                    + "  p.vlr_unitario\n"
                    + "from\n"
                    + "  promocoes p\n"
                    + "where\n"
                    + "  p.empresa_id = '" + getLojaOrigem() + "' and\n"
                    + "  p.dt_inicio <= current_date and\n"
                    + "  p.dt_final >= current_date"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("produto_id"));
                    imp.setDataInicio(rst.getDate("dt_inicio"));
                    imp.setDataFim(rst.getDate("dt_final"));
                    imp.setPrecoOferta(rst.getDouble("vlr_unitario"));

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
        return new SiacDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SiacDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoOracle.getConexao().createStatement();
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
                        String id = rst.getString("vendaid");
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
                        next.setSubTotalImpressora(rst.getDouble("total"));
                        //next.setCancelado(rst.getBoolean("cancelado"));
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
                    = "SELECT \n"
                    + "PEDIDO_ID vendaid,\n"
                    + "OCORRENCIA_ID numerocupom,\n"
                    + "1 ecf,\n"
                    + "TO_CHAR(DATA_HORA_ALTERACAO,'YYYY-MM-DD') data, \n"
                    + "TO_CHAR(DATA_HORA_ALTERACAO,'HH24:MI:SS') hora,\n"
                    + "VLR_MOVIMENTO total,\n"
                    + "EMPRESA_ID\n"
                    + "FROM CAPA_MOVIMENTO \n"
                    + "WHERE \n"
                    + "TO_CHAR(DATA_HORA_ALTERACAO,'YYYY-MM-DD') BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "AND \n"
                    + "EMPRESA_ID = '" + idLojaCliente + "'\n"
                    + "AND\n"
                    + "VLR_MOVIMENTO > 0\n"
                    + "ORDER BY 1";

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

                        next.setVenda(rst.getString("vendaid"));
                        next.setId(rst.getString("itemvenda_id"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setProduto(rst.getString("produto_id"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("preco_unitario"));
                        //next.setCancelado(rst.getBoolean("cancelado"));

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
                    + " concat(CONCAT(CONCAT(PEDIDO_ID,'.'), ITEM_NO), PRODUTO_ID) itemvenda_id,\n"
                    + " TO_CHAR(DATA_HORA_ALTERACAO,'YYYY-MM-DD') data,\n"
                    + " ITEM_NO sequencia,\n"
                    + " PEDIDO_ID vendaid,\n"
                    + " PRODUTO_ID,\n"
                    + " PRODUTO_NOME,\n"
                    + " PRECO_UNITARIO,\n"
                    + " QUANTIDADE,\n"
                    + " UNIDADE,\n"
                    + " VLR_LIQUIDO,\n"
                    + " EMPRESA_ID\n"
                    + "FROM ITENS_MOVIMENTO\n"
                    + "WHERE \n"
                    + "TO_CHAR(DATA_HORA_ALTERACAO,'YYYY-MM-DD') BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "AND \n"
                    + "EMPRESA_ID = '" + idLojaCliente + "'\n"
                    + "AND VLR_LIQUIDO > 0";

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
