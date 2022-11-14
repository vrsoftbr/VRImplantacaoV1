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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Alan
 */
public class SincDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Sinc";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.ATACADO,
                OpcaoProduto.PAUTA_FISCAL,
                OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                OpcaoProduto.SUGESTAO_COTACAO,
                OpcaoProduto.COMPRADOR,
                OpcaoProduto.COMPRADOR_PRODUTO,
                OpcaoProduto.OFERTA,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.NORMA_REPOSICAO,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.FABRICACAO_PROPRIA,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.RECEITA_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.CEP,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.OBSERVACAO
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.OUTRAS_RECEITAS));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	tpro_sitribu_1 ||'-'|| round(prod_aliqsai_1,0) ||'-'|| round(prod_baseicm_1,2) as id,\n"
                    + "	'CST '|| tpro_sitribu_1 || ' ALIQ ' ||round(p2.prod_aliqsai_1,2) ||'% RED ' || coalesce (case\n"
                    + "				when tpro_sitribu_1 = 20\n"
                    + "				then round((p2.prod_baseicm_1-100)*-1,2)\n"
                    + "				else\n"
                    + "					case\n"
                    + "						when prod_baseicm_1 = 100\n"
                    + "						then 0\n"
                    + "					end\n"
                    + "				end, 0) descricao,\n"
                    + "	tpro_sitribu_1 cst_icms,\n"
                    + "	p2.prod_aliqsai_1 aliq_icms,\n"
                    + "	coalesce (case\n"
                    + "				when tpro_sitribu_1 = 20\n"
                    + "				then round((p2.prod_baseicm_1-100)*-1,2)\n"
                    + "				else\n"
                    + "					case\n"
                    + "						when prod_baseicm_1 = 100\n"
                    + "						then 0\n"
                    + "					end\n"
                    + "				end, 0) red_icms\n"
                    + "from\n"
                    + "	estpro p\n"
                    + "	left join estprod p2 on p.tpro_codprod_1 = p2.prod_codprod_1 and p2.prod_empresa_1 = 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ntab_codorig_2 cod_m1,\n"
                    + "	ntab_nomeori_2 desc_m1,\n"
                    + "	m2.ntab_suborig_17 cod_m2,\n"
                    + "	m2.ntab_nomeori_17 desc_m2,\n"
                    + "	m3.ntab_codlinh_6 cod_m3,\n"
                    + "	m3.ntab_desclin_6 desc_m3,\n"
                    + "	m4.ntab_sublinh_6 cod_m4,\n"
                    + "	m4.ntab_desclin_6 desc_m4\n"
                    + "from\n"
                    + "	sintab02 m1\n"
                    + "	left join sintab17 m2 on m1.ntab_codorig_2 = m2.ntab_codorig_17 \n"
                    + "	left join sintab06 m3 on m1.ntab_codorig_2 = m3.ntab_codorig_6 and m3.ntab_sublinh_6 = ' '\n"
                    + "	left join sintab06 m4 on m4.ntab_codlinh_6 = m3.ntab_codlinh_6\n"
                    + "order by 1,3,5,7"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("cod_m1"));
                    imp.setMerc1Descricao(rst.getString("desc_m1"));
                    imp.setMerc2ID(rst.getString("cod_m2"));
                    imp.setMerc2Descricao(rst.getString("desc_m2"));
                    imp.setMerc3ID(rst.getString("cod_m3"));
                    imp.setMerc3Descricao(rst.getString("desc_m3"));
                    imp.setMerc4ID(rst.getString("cod_m4"));
                    imp.setMerc4Descricao(rst.getString("desc_m4"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

//    @Override
//    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
//        List<FamiliaProdutoIMP> result = new ArrayList<>();
//
//        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
//            try (ResultSet rs = stm.executeQuery(
//                    "select \n"
//                    + "	codsimilar,\n"
//                    + "	descricao\n"
//                    + "from \n"
//                    + "	simprod")) {
//                while (rs.next()) {
//                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
//                    imp.setImportSistema(getSistema());
//                    imp.setImportLoja(getLojaOrigem());
//
//                    imp.setImportId(rs.getString("codsimilar"));
//                    imp.setDescricao(rs.getString("descricao"));
//
//                    result.add(imp);
//                }
//            }
//        }
//        return result;
//    }
    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	tpro_codprod_1 idproduto,\n"
                    + "	case\n"
                    + "	  when tpro_codbarr_1 = 0\n"
                    + "	  then tpro_codprod_1::varchar\n"
                    + "	  else tpro_codbarr_1::varchar\n"
                    + "	end ean,\n"
                    + "	tpro_embmini_1 quantidade\n"
                    + "from\n"
                    + "	estpro p\n"
                    + "union \n"
                    + "select\n"
                    + "	taux_codinte_1 idproduto,\n"
                    + "	taux_codean2_1::varchar ean,\n"
                    + "	1 quantidade\n"
                    + "from\n"
                    + "	estaux\n"
                    + "where\n"
                    + "	taux_codean2_1 != 0\n"
                    + "union\n"
                    + "select\n"
                    + "	taux_codinte_1 idproduto,\n"
                    + "	taux_codean3_1::varchar ean,\n"
                    + "	1 quantidade\n"
                    + "from\n"
                    + "	estaux\n"
                    + "where\n"
                    + "	taux_codean3_1 != 0\n"
                    + "union\n"
                    + "select\n"
                    + "	taux_codinte_1 idproduto,\n"
                    + "	taux_codean4_1::varchar ean,\n"
                    + "	1 quantidade\n"
                    + "from\n"
                    + "	estaux\n"
                    + "where\n"
                    + "	taux_codean4_1 != 0\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	tpro_codprod_1 id,\n"
                    + "	tpro_codbarr_1 ean,\n"
                    + "	tpro_descric_1 descricaocompleta,\n"
                    + "	ea.taux_descres_1 descricaoreduzida,\n"
                    + "	tpro_unimedi_1 tipoemb,\n"
                    + "	ea.taux_pesovar_1 e_balanca,\n"
                    + "	substring(ea.taux_subgrup_1::varchar,1,2) merc1,\n"
                    + "	substring(ea.taux_subgrup_1::varchar,3,2) merc2,\n"
                    + "	tpro_codlinh_1 merc3,\n"
                    + "	tpro_sublinh_1 merc4,\n"
                    + "	tpro_precrep_1 precocusto,\n"
                    + "	p3.prod_margens_1 margem,\n"
                    + "	tpro_precven_1 precovenda,\n"
                    + "	ncm.ntab_codclas_10 ncm,\n"
                    + "	ncm.ntab_codcest_10 cest,\n"
                    + "	tpro_embmini_1,\n"
                    + "	case when p3.prod_cancela_1 = 'A' then 1 else 0 end sit_cadastro,\n"
                    + "	tpro_sitribu_1 ||'-'|| round(prod_aliqsai_1,0) ||'-'|| round(prod_baseicm_1,2) as id_icms,\n"
                    + "	p3.prod_cstpiss_1 piscofins_saida,\n"
                    + "	p3.prod_cstpise_1 piscofins_entrada\n"
                    + "from\n"
                    + "	estpro p\n"
                    + "	left join estapl p2 on p.tpro_codprod_1 = p2.tapl_codprod_1\n"
                    + "	left join estprod p3 on p.tpro_codprod_1 = p3.prod_codprod_1 and p3.prod_empresa_1 = 1\n"
                    + "	left join estaux ea on ea.taux_codinte_1 = p.tpro_codprod_1 \n"
                    + "	left join sintab10 ncm on p.tpro_clasfis_1 = ncm.ntab_codtabe_10\n"
                    + "order by 1"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));

                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        /*imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rst.getInt("diasvalidade"));*/
                        imp.setEan(imp.getImportId());
                    }

//                    imp.setTipoEmbalagemCotacao(rst.getString("emb_compra"));
//                    imp.setQtdEmbalagemCotacao(rst.getInt("qtde_compra"));
                    imp.setTipoEmbalagem(rst.getString("tipoemb"));
//                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());
                    imp.setSituacaoCadastro(rst.getInt("sit_cadastro"));

                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setCodMercadologico4(rst.getString("merc4"));
//                    imp.setIdFamiliaProduto(rst.getString("codsimilar"));
//                    imp.setTipoEmbalagemVolume(rst.getString("tipo_volume"));
//                    imp.setVolume(rst.getDouble("qtde_volume"));
//                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
//                    imp.setEstoqueMaximo(rst.getDouble("estmaximo"));
//                    imp.setEstoque(rst.getDouble("estoque"));
//                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
//                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(rst.getDouble("precocusto"));
                    imp.setMargem(rst.getDouble("margem"));
//                    imp.setDataCadastro(rst.getDate("datainclusao"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsDebitoId(rst.getString("id_icms"));
                    imp.setIcmsCreditoId(rst.getString("id_icms"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());

                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
//                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ncad_cgcocpf_2 id,\n"
                    + "	ncad_nomecli_2 razao,\n"
                    + "	ncad_cgcocpf_2 cpfcnpj,\n"
                    + "	ncad_inscric_2 rgie,\n"
                    + "	ncad_endere1_2 endereco,\n"
                    + "	ncad_numeros_2 numero,\n"
                    + "	ncad_complem_2 complemento,\n"
                    + "	ncad_bairros_2 bairro,\n"
                    + " cd.ntab_nomemun_1 cidade,\n"
                    + "	ncad_numecep_2 cep,\n"
                    + "	ncad_siglest_2 uf,\n"
                    + "	ncad_dddfone_2::varchar||ncad_telefon_2 fone,\n"
                    + "	ncad_dddcelu_2::varchar||ncad_celular_2 celular,\n"
                    + "	ncad_internt_2 email,\n"
                    + "	ncad_contato_2 contato,\n"
                    + "	ncad_observa_2_o1 observacao\n"
                    + "from\n"
                    + "	sincad f\n"
                    + "	left join sintab01 cd on f.ncad_codmuni_2 = cd.ntab_codmuni_1\n"
                    + "where\n"
                    + "	ncad_tipocad_2 = 'F'\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("razao"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("rgie"));

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.setObservacao(rst.getString("observacao"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("contato"),
                                null,
                                rst.getString("celular"),
                                TipoContato.COMERCIAL,
                                rst.getString("email")
                        );
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	tpro_codprod_1 codproduto,\n"
                    + "	tpro_fornece_1 codfornec,\n"
                    + "	1 quantidade\n"
                    + "from \n"
                    + "	 estpro p"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rst.getString("codproduto"));
                    imp.setIdFornecedor(rst.getString("codfornec"));
                    //imp.setCodigoExterno(rst.getString("reffornec"));
                    imp.setQtdEmbalagem(rst.getDouble("quantidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	pdup_fornece_1||pdup_numdupl_1 id,\n"
                    + "	pdup_fornece_1 id_fornecedor,\n"
                    + "	pdup_numdupl_1 documento,\n"
                    + "	pdup_valdupl_1 valor,\n"
                    + "	pdup_datemis_1 emissao,\n"
                    + "	pdup_datainc_1 lancamento,\n"
                    + "	pdup_datvenc_1 vencimento\n"
                    + "from\n"
                    + "	pagdup\n"
                    + "where\n"
                    + "	pdup_liquida_1 != 'L'"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("lancamento"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ncad_cgcocpf_2 id,\n"
                    + "	case\n"
                    + "		when length(ncad_cgcocpf_2::varchar) > 9 then ncad_cgcocpf_2::varchar\n"
                    + "		else replace(replace (case\n"
                    + "			when ncad_fantasi_2 like 'CPF%' then ncad_fantasi_2\n"
                    + "		end,\n"
                    + "		'CPF',''),':','')\n"
                    + "	end cpf_cnpj,\n"
                    + "	trim(replace (case when ncad_fantasi_2 like 'RG%' then ncad_fantasi_2 end, 'RG', '')) rg_ie,\n"
                    + "	ncad_nomecli_2 razao,\n"
                    + "	ncad_endere1_2 endereco,\n"
                    + "	ncad_numeros_2 numero,\n"
                    + "	ncad_complem_2 complemento,\n"
                    + "	ncad_bairros_2 bairro,\n"
                    + " cd.ntab_nomemun_1 cidade,\n"
                    + "	ncad_numecep_2 cep,\n"
                    + "	ncad_siglest_2 uf,\n"
                    + "	ncad_dddfone_2 ddd_fone,\n"
                    + "	ncad_telefon_2 fone,\n"
                    + "	ncad_dddcelu_2 ddd_celular,\n"
                    + "	ncad_celular_2 celular,\n"
                    + " l.ntab_valfaix_14 limite,\n"
                    + "	case when ncad_credsus_2 = 2 then 1 else 0 end bloqueado,\n"
                    + "	ncad_datsusp_2 data_bloqueio,\n"
                    + "	ncad_sexocli_2 sexo,\n"
                    + "	ncad_datnasc_2 data_nasc,\n"
                    + "	case ncad_estcivi_2\n"
                    + "	  when 'S' then 1\n"
                    + "	  when 'C' then 2\n"
                    + "	  when 'D' then 6\n"
                    + "	  when 'V' then 3\n"
                    + "	  else 1\n"
                    + "	end estadocivil,\n"
                    + "	ncad_conjuge_2 conjuge,\n"
                    + "	ncad_nasconj_2 nasc_conjuge,\n"
                    + "	ncad_datincl_2 data_cadastro,\n"
                    + "	'COD_CONT: '||ncad_codcont_2 ||'  '||ncad_fantasi_2||'  '||ncad_referen_2 as observacao\n"
                    + "from\n"
                    + "	sincad c\n"
                    + " left join sintab01 cd on c.ncad_codmuni_2 = cd.ntab_codmuni_1\n"
                    + " left join SINTAB14 l on c.ncad_limcred_2 = l.ntab_codfaix_14\n"
                    + "where\n"
                    + "	ncad_tipocad_2 = 'C'\n"
                    + "order by ncad_cgcocpf_2"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rst.getString("rg_ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(imp.getRazao());

                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));

                    imp.setTelefone(rst.getString("ddd_fone") + rst.getString("fone"));
                    imp.setCelular(rst.getString("ddd_celular") + rst.getString("celular"));
                    imp.setBloqueado("1".equals(rst.getString("bloqueado")));
                    imp.setAtivo("0".equals(rst.getString("bloqueado")));

                    imp.setDataNascimento(rst.getDate("data_nasc"));
                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("M".equals(rst.getString("sexo"))) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setDataNascimentoConjuge(rst.getDate("nasc_conjuge"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));

                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	recd_empresa_1||recd_numdupl_1||recd_parcela_1 id,\n"
                    + "	recd_cliente_1 idcliente,\n"
                    + "	recd_numdupl_1 numerocupom,\n"
                    + "	recd_datemis_1 emissao,\n"
                    + "	recd_valdupl_1 valor,\n"
                    + "	recd_datvenc_1 vencimento,\n"
                    + "	recd_refere1_1 observacao\n"
                    + "from\n"
                    + "	recdup cr\n"
                    + "where\n"
                    + "	recd_valpgto_1 = 0\n"
                    + "order by 2,4"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "" + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCheque(rst.getString("nro_cheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setRg(rst.getString("rgie"));
                    imp.setCpf(rst.getString("cpfcnpj"));
                    imp.setNome(rst.getString("nome"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));

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
        return new SincDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new SincDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                    = "";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado(rst.getBoolean("cancelado"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "";
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
