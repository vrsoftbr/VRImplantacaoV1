package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ClienteContatoIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Wagner
 */
public class BlueSoftDAO extends InterfaceDAO implements MapaTributoProvider {

    private boolean inverteAssociado = false;

    public void setInverteAssociado(boolean inverteAssociado) {
        this.inverteAssociado = inverteAssociado;
    }

    @Override
    public String getSistema() {
        return "BLUESOFT";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.ATIVO,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.CEST,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.NCM,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.PRECO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                OpcaoProduto.NUTRICIONAL,
                OpcaoProduto.ASSOCIADO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.EMAIL,
                OpcaoCliente.CELULAR,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct --saida\n"
                    + " (cst_saida_pdv||'.'||replace(aliquota_saida_pdv,',','.')||'.'||replace(reducao_saida_pdv,',','.')||'.S') id,\n"
                    + " descricao_saida_pdv descricao,\n"
                    + " cst_saida_pdv cst,\n"
                    + " replace(aliquota_saida_pdv,',','.') aliquota,\n"
                    + " replace(reducao_saida_pdv,',','.')::numeric(10,2)  reducao\n"
                    + "from tributacao_produto \n"
                    + "union\n"
                    + "select distinct --entrada\n"
                    + " (cst_entrada||'.'||replace(aliquota_entrada,',','.')||'.'||replace(reducao_entrada,',','.')||'.E') id,\n"
                    + " descricao_entrada descricao,\n"
                    + " cst_entrada cst,\n"
                    + " replace(aliquota_entrada,',','.') aliquota,\n"
                    + " replace(reducao_entrada,',','.')::numeric(10,2) reducao\n"
                    + "from tributacao_produto "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " departamento_key merc1,\n"
                    + " departamento desc1,\n"
                    + " secao_key merc2,\n"
                    + " secao desc2,\n"
                    + " grupo_produto_key merc3,\n"
                    + " grupo desc3,\n"
                    + " sub_grupo_produto_key merc4,\n"
                    + " subgrupo desc4 \n"
                    + "from mercadologico \n"
                    + "order by 1,3,5,7"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));
                    imp.setMerc4ID(rs.getString("merc4"));
                    imp.setMerc4Descricao(rs.getString("desc4"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " p.produto_key id,\n"
                    + " p.descricao descricaocompleta,\n"
                    + " p.descricao_gondola descricaogondola,\n"
                    + " p.descricao_cupom descricaoreduzida,\n"
                    + " p.descricao_balanca,\n"
                    + " p.embalagem_key embalagem,\n"
                    + " replace(e.quantidadeestoque,',','.') estoque,\n"
                    //+ " replace(p.estoque_gerencial,',','.') estoque,\n"
                    + " case when p.status = 'Inativo' then 0 else 1 end situacao,\n"
                    + " case when p.exporta_balanca = 'Sim' then 1 else 0 end ebalanca,\n"
                    + " p.peso_bruto,\n"
                    + " p.peso_liquido,\n"
                    + " case when p.produto_key \n"
                    + " 	 in (select distinct on (preco_equivalente)\n"
                    + " 				preco_equivalente id\n"
                    + " 			from produtos  \n"
                    + " 			where preco_equivalente is not null\n"
                    + " 				and caixa = 'Não'\n"
                    + " 				and fator_preco = '1'\n"
                    + " 				and status  = 'Ativo') then p.produto_key\n"
                    + " 				else p.preco_equivalente end familiaid,\n"
                    + " case when p.exporta_balanca = 'Sim' then substring(p.gtin_principal,1,4)\n"
                    + "  else p.gtin_principal end ean,\n"
                    + " replace(p.preco_venda,',','.') precovenda,\n"
                    + " replace(p.custo_liquido,',','.') custosemimposto,\n"
                    + " replace(p.custo_bruto,',','.') custocomimposto,\n"
                    + " p.custo_contabil,\n"
                    + " p.departamento_key merc1,\n"
                    + " p.secao_key merc2,\n"
                    + " p.grupo_produto_key merc3,\n"
                    + " p.sub_grupo_produto_key merc4,\n"
                    + " t.ncm,\n"
                    + " t.cest,\n"
                    + " t.cst_pis_saida,\n"
                    + " t.cst_pis_entrada,\n"
                    + " (t.cst_saida_pdv||'.'||replace(t.aliquota_saida_pdv,',','.')||'.'||replace(t.reducao_saida_pdv,',','.')||'.S') idtributacao_saida,\n"
                    + " (t.cst_entrada||'.'||replace(t.aliquota_entrada,',','.')||'.'||replace(t.reducao_entrada,',','.')||'.E') idtributacao_entrada\n"
                    + "from produtos p\n"
                    + "left join tributacao_produto t on t.produto_key = p.produto_key\n"
                    + "left join estoque e on e.codigointerno = p.produto_key\n"
                    + "where \n"
                    + " p.caixa = 'Não'\n"
                    + "order by p.embalagem_key;"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));

                    ProdutoBalancaVO balanca = produtosBalanca.get(Utils.stringToInt(rs.getString("ean"), -2));

                    if (balanca != null) {
                        imp.setEan(String.valueOf(balanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(balanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(balanca.getValidade());
                    } else {
                        imp.setEan(rs.getString("ean"));
                        imp.setTipoEmbalagem(rs.getString("embalagem"));
                    }

                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setIdFamiliaProduto(rs.getString("familiaid"));

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));

                    imp.setTipoEmbalagemCotacao(rs.getString("embalagem"));
                    imp.setTipoEmbalagemVolume(rs.getString("embalagem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));

                    imp.setIcmsConsumidorId(rs.getString("idtributacao_saida"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());

                    imp.setIcmsCreditoId(rs.getString("idtributacao_entrada"));
                    imp.setIcmsCreditoForaEstadoId(rs.getString("idtributacao_entrada"));

                    imp.setPiscofinsCstDebito(rs.getInt("cst_pis_saida"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select \n"
                    + "  p.produto_unitario_key produtoid,\n"
                    + "  p.gtin_principal ean,\n"
                    + "  p.embalagem_key embalagem,\n"
                    + "  replace(p.fator_preco,',','.') qtde\n"
                    + " from produtos p\n"
                    + " where \n"
                    + " p.caixa = 'Sim'\n"
                    + " UNION\n"
                    + " select \n"
                    + " produto_key produtoid,\n"
                    + " ean,\n"
                    + " 'UN' embalagem,\n"
                    + " '1' qtde\n"
                    + "from barras\n"
                    + "where tipo <> 'PLU'\n"
                    + " UNION \n"
                    + "select \n"
                    + " p.produto_unitario_key produtoid,\n"
                    + " b.ean,\n"
                    + " p.embalagem_key embalagem,\n"
                    + " replace(p.fator_preco,',','.') qtde\n"
                    + "from produtos p\n"
                    + " join barras b on b.produto_key = p.produto_key"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("produtoid"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtde"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct on (preco_equivalente)\n"
                    + " 	preco_equivalente id,\n"
                    + " 	substring(descricao,1,30) descricao\n"
                    + " from produtos  \n"
                    + " where preco_equivalente is not null\n"
                    + " 	and caixa = 'Não'\n"
                    + " 	and fator_preco = '1'\n"
                    + " 	and status  = 'Ativo'"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "with a as (\n"
                    + "  select \n"
                    + "  produto_key produtoid,\n"
                    + "  p.gtin_principal ean,\n"
                    + "  p.embalagem_key embalagem,\n"
                    + "  p.fator_estoque qtde\n"
                    + " from produtos p\n"
                    + "  where \n"
                    + "  p.produto_key = p.produto_unitario_key\n"
                    + "  and\n"
                    + "  p.caixa = 'Não'\n"
                    + " )\n"
                    + " select \n"
                    + "  p.produto_key produtopai,\n"
                    + "  p.gtin_principal ean,\n"
                    + "  p.embalagem_key embalagem,\n"
                    + "  round(replace(p.fator_estoque,',','.')::numeric) qtde,\n"
                    + "  a.produtoid produtofilho,\n"
                    + "  a.ean ean_filho,\n"
                    + "  a.embalagem emb_filho,\n"
                    + "  a.qtde qtde_filho\n"
                    + " from produtos p\n"
                    + " join a on a.produtoid = p.produto_unitario_key\n"
                    + " where \n"
                    + " p.produto_key <> p.produto_unitario_key\n"
                    + " and\n"
                    + " p.caixa = 'Não'"
            )) {
                while (rst.next()) {
                    AssociadoIMP imp = new AssociadoIMP();

                    if (inverteAssociado) {
                        imp.setId(rst.getString("produtofilho"));
                        imp.setQtdEmbalagem(rst.getInt("qtde_filho"));
                        imp.setProdutoAssociadoId(rst.getString("produtopai"));
                        imp.setQtdEmbalagemItem(rst.getInt("qtde"));
                        imp.setAplicaCusto(true);
                        imp.setAplicaEstoque(false);
                    } else {
                        imp.setId(rst.getString("produtopai"));
                        imp.setQtdEmbalagem(rst.getInt("qtde"));
                        imp.setProdutoAssociadoId(rst.getString("produtofilho"));
                        imp.setQtdEmbalagemItem(rst.getInt("qtde_filho"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " n.produto_key id,\n"
                    + " p.descricao descricao,\n"
                    + " n.qtde_porcao porcao,\n"
                    + " n.medida_caseira_inteira medidaInteira,\n"
                    + " n.q_calorias calorias,\n"
                    + " replace(n.q_gorduras_totais,',','.') gordurastotais,\n"
                    + " replace(n.q_gorduras_trans,',','.') gordurastrans,\n"
                    + " replace(n.q_gorduras_saturadas,',','.') gordurassaturadas,\n"
                    + " replace(n.q_carboidratos,',','.') carboidratos,\n"
                    + " replace(n.q_proteinas,',','.') proteinas,\n"
                    + " replace(n.q_colesterol,',','.') colesterol,\n"
                    + " replace(n.q_fibra,',','.') fibra,\n"
                    + " replace(n.q_calcio,',','.') calcio,\n"
                    + " replace(n.q_ferro,',','.') ferro,\n"
                    + " replace(n.q_sodio,',','.') sodio\n"
                    + "from nutricional n\n"
                    + "join produtos p on p.produto_key = n.produto_key;"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setCaloria(rst.getInt("calorias"));
                    imp.setCarboidrato(rst.getDouble("carboidratos"));
                    imp.setProteina(rst.getDouble("proteinas"));
                    imp.setGordura(rst.getDouble("gordurastotais"));
                    imp.setGorduraSaturada(rst.getDouble("gordurassaturadas"));
                    imp.setGorduraTrans(rst.getDouble("gordurastrans"));
                    imp.setFibra(rst.getDouble("fibra"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao(rst.getString("porcao"));
                    imp.setCalcio(rst.getDouble("calcio"));
                    imp.setFerro(rst.getDouble("ferro"));
                    imp.setMedidaInteira(rst.getInt("medidaInteira"));

                    imp.addProduto(rst.getString("id"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private void gravarContatoFornecedor(FornecedorIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            String sql
                    = "select \n"
                    + " pessoa_key fornecedorid,\n"
                    + " tipo_contato tipocontato,\n"
                    + " descritivo contato\n"
                    + "from contatofornecedor\n"
                    + " where pessoa_key::int = " + imp.getImportId();

            LOG.finer(sql);
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {

                    if ("Telefone Comercial".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Telefone Comercial",
                                rst.getString("contato"),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("Telefone Cobrança".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Telefone Cobrança",
                                rst.getString("contato"),
                                "",
                                TipoContato.FINANCEIRO,
                                "");
                        System.out.println(c);
                    }
                    if ("Telefone Referência".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Telefone Referência",
                                rst.getString("contato"),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("Telefone Residencial".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Telefone Residencial",
                                rst.getString("contato"),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("WhatsApp".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "WhatsApp",
                                "",
                                rst.getString("contato"),
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("Rádio Nextel".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Rádio Nextel",
                                rst.getString("contato"),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("Home Page".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Home Page",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                rst.getString("contato"));
                        System.out.println(c);
                    }
                    if ("Fax Cobrança".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Fax Cobrança",
                                rst.getString("contato"),
                                "",
                                TipoContato.FINANCEIRO,
                                "");
                        System.out.println(c);
                    }
                    if ("Fax".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Fax",
                                rst.getString("contato"),
                                "",
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("Email de envio NFS-e".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Email de envio NFS-e",
                                "",
                                "",
                                TipoContato.NFE,
                                rst.getString("contato"));
                        System.out.println(c);
                    }
                    if ("Email de envio NF-e".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Email de envio NF-e",
                                "",
                                "",
                                TipoContato.NFE,
                                rst.getString("contato"));
                        System.out.println(c);
                    }
                    if ("Email Particular".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Email Particular",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                rst.getString("contato"));
                        System.out.println(c);
                    }
                    if ("Email Comercial".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Email Comercial",
                                "",
                                "",
                                TipoContato.COMERCIAL,
                                rst.getString("contato"));
                        System.out.println(c);
                    }
                    if ("Email Cobrança".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Email Cobrança",
                                "",
                                "",
                                TipoContato.FINANCEIRO,
                                rst.getString("contato"));
                        System.out.println(c);
                    }
                    if ("Celular Particular".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Celular Particular",
                                "",
                                rst.getString("contato"),
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                    if ("Celular Empresa".equals(rst.getString("tipocontato"))) {
                        FornecedorContatoIMP c = imp.addContato(
                                "Celular Empresa",
                                "",
                                rst.getString("contato"),
                                TipoContato.COMERCIAL,
                                "");
                        System.out.println(c);
                    }
                }
            }
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "with teste as (\n"
                    + "select \n"
                    + " codigo id,\n"
                    + " nomerazao razao,\n"
                    + " nomeabreviado fantasia,\n"
                    + " cpfcnpj,\n"
                    + " rua endereco,\n"
                    + " num numero,\n"
                    + " '0' complemento,\n"
                    + " cidade,\n"
                    + " bairro,\n"
                    + " uf,\n"
                    + " '0' cep,\n"
                    + " '0' codigo_ibge,\n"
                    + " inscestadual insc,\n"
                    + " case when status = 'INATIVA' then 0 else 1 end situacao,\n"
                    + " '0' produtor_rural\n"
                    + "from fornecedor2\n"
                    + "where \n"
                    + " tipopessoa ilike '%Governo Federal%' or\n"
                    + " tipopessoa ilike '%Fornecedor%' or\n"
                    + " tipopessoa ilike '%Loja%' or\n"
                    + " tipopessoa ilike '%Prestador de Serviço%' or\n"
                    + " tipopessoa ilike '%Administradora Cartão%' or\n"
                    + " tipopessoa ilike '%Empresa de Cobrança%' or\n"
                    + " tipopessoa ilike '%Prefeitura Municipal%' or\n"
                    + " tipopessoa ilike '%Transportadora%'\n"
                    + " or\n"
                    + " tipopessoa is null\n"
                    + ")\n"
                    + "select \n"
                    + " id, razao, fantasia, cpfcnpj, endereco, numero, complemento, cidade, \n"
                    + " bairro, uf, cep, codigo_ibge, insc, situacao, produtor_rural\n"
                    + "from teste \n"
                    + "where \n"
                    + " id not in (select fornecedor_key from fornecedor)\n"
                    + " union\n"
                    + " select distinct on (fornecedor_key)\n"
                    + " fornecedor_key id,\n"
                    + " nome_razao razao,\n"
                    + " nome_fantasia fantasia,\n"
                    + " cpf_cnpj cpfcnpj,\n"
                    + " endereco_rua endereco,\n"
                    + " endereco_numero numero,\n"
                    + " endereco_complemento complemento,\n"
                    + " cidade,\n"
                    + " bairro,\n"
                    + " estado_sigla uf,\n"
                    + " cep,\n"
                    + " codigo_ibge,\n"
                    + " insc,\n"
                    + " case when status = 'Inativo' then 0 else 1 end situacao,\n"
                    + " produtor_rural\n"
                    + "from fornecedor"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cpfcnpj"));
                    imp.setIe_rg(rs.getString("insc"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    if ("Ativo".equals(rs.getString("produtor_rural"))) {
                        imp.setTipoFornecedor(TipoFornecedor.PRODUTORRURAL);
                    }

                    gravarContatoFornecedor(imp);

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " fornecedor_produto_key id,\n"
                    + " fornecedor_key fornecedorid,\n"
                    + " produto_key produtoid,\n"
                    + " case when codigo_referencia = '' then 'SANZITO'\n"
                    + "   else codigo_referencia end referencia,\n"
                    + " divisao_key quantidade \n"
                    + "from produto_fornecedor"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setCodigoExterno(rs.getString("referencia"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    /*private void gravarContatoCliente(ClienteIMP imp) throws Exception {
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            String sql
                    = "select \n"
                    + " pessoa_key clienteid,\n"
                    + " tipo_contato tipocontato,\n"
                    + " descritivo contato\n"
                    + "from contatocliente\n";
                    //+ " where pessoa_key::int = " + imp.getId();

            LOG.finer(sql);
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {

                    if ("Telefone Comercial".equals(rst.getString("tipocontato"))) {

                        imp.addTelefone("Telefone Comercial", rst.getString("contato"));  
                        
                    }

                }
            }
        }
    }*/
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct on (c.pessoa_key)\n"
                    + " c.pessoa_key id,\n"
                    + " c.nome_razao razao,\n"
                    + " c.nome_fantasia fantasia,\n"
                    + " c.nome_abreviado obs,\n"
                    + " c.cpf_cnpj cpfcnpj,\n"
                    + " c.endereco_rua endereco,\n"
                    + " c.endereco_numero numero,\n"
                    + " c.endereco_complemento complemento,\n"
                    + " c.cidade,\n"
                    + " c.bairro, \n"
                    + " c.estado_sigla uf,\n"
                    + " c.cep,\n"
                    + " c.codigo_ibge,\n"
                    + " c.sexo,\n"
                    + " c.rg,\n"
                    + " replace(c.data_nascimento,'/','-') datanascimento,\n"
                    + " c.insc ie,\n"
                    + " case when c.status = 'Inativo' then 0 else 1 end situacao,\n"
                    + " ct.descritivo celular,\n"
                    + " ct2.descritivo email,\n"
                    + " ct3.descritivo telefone\n"
                    + "from cliente c\n"
                    + "left join contatocliente ct on ct.pessoa_key = c.pessoa_key and ct.tipo_contato = 'Celular Particular'\n"
                    + "left join contatocliente ct2 on ct2.pessoa_key = c.pessoa_key and ct2.tipo_contato = 'Email Comercial'\n"
                    + "left join contatocliente ct3 on ct3.pessoa_key = c.pessoa_key and ct3.tipo_contato = 'Telefone Residencial'\n"
                    + "where \n"
                    + " c.pessoa_key not in (\n"
                    + "  select codigo\n"
                    + "	from cliente2\n"
                    + "   where nomerazao = colativo )"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setTelefone(rs.getString("telefone"));

                    //gravarContatoCliente(imp);
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
                    "select \n"
                    + " n_duplicata id,\n"
                    + " sacadodescritivo obs,\n"
                    + " split_part(vencimento,'/',3)||'-'||split_part(vencimento,'/',2)||'-'||split_part(vencimento,'/',1) vencimento,\n"
                    + " split_part(emissao,'/',3)||'-'||split_part(emissao,'/',2)||'-'||split_part(emissao,'/',1) emissao,\n"
                    + " replace(valorliquido,',','.') valor,\n"
                    + " codcliente clienteid,\n"
                    + " coalesce(ndoc, n_duplicata) numerocupom\n"
                    + "from contasareceber\n"
                    + "where \n"
                    + " codcliente not in (\n"
                    + "  select codigo\n"
                    + "	from cliente2\n"
                    + "   where nomerazao = colativo )"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("clienteid"));
                    imp.setDataVencimento(rst.getDate("vencimento"));

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
                    "select \n"
                    + " c.duplicata id,\n"
                    + " c.fatura numerodocumento,\n"
                    + " c.favorecido obs,\n"
                    + " split_part(c.vencimento,'/',3)||'-'||split_part(c.vencimento,'/',2)||'-'||split_part(c.vencimento,'/',1) vencimento,\n"
                    + " split_part(c.datadeemissao,'/',3)||'-'||split_part(c.datadeemissao,'/',2)||'-'||split_part(c.datadeemissao,'/',1) datadeemissao,\n"
                    + " replace(c.valorliquido,',','.') valor,\n"
                    + " f.fornecedor_key fornecedorid\n"
                    + "from contasapagar c\n"
                    + "join fornecedor f on f.cpf_cnpj::bigint = regexp_replace(c.favorecidocpf_cnpj,'[^0-9]','','g')::bigint\n"
                    + "union\n"
                    + "select \n"
                    + " c.duplicata id,\n"
                    + " c.fatura numerodocumento,\n"
                    + " c.favorecido obs,\n"
                    + " split_part(c.vencimento,'/',3)||'-'||split_part(c.vencimento,'/',2)||'-'||split_part(c.vencimento,'/',1) vencimento,\n"
                    + " split_part(c.datadeemissao,'/',3)||'-'||split_part(c.datadeemissao,'/',2)||'-'||split_part(c.datadeemissao,'/',1) datadeemissao,\n"
                    + " replace(c.valorliquido,',','.') valor,\n"
                    + " f2.codigo fornecedorid\n"
                    + "from contasapagar c\n"
                    + " join fornecedor2 f2 on f2.cpfcnpj::bigint = regexp_replace(c.favorecidocpf_cnpj,'[^0-9]','','g')::bigint"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("datadeemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " codigo id ,\n"
                    + " nomerazao razao,\n"
                    + " cpfcnpj cnpj,\n"
                    + " inscestadual inscricaoestadual,\n"
                    + " rua endereco,\n"
                    + " num,\n"
                    + " bairro,\n"
                    + " uf\n"
                    + "from fornecedor2 \n"
                    + "where codigo = '2'"
            )) {
                //SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("num"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setUf(rst.getString("uf"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " codigo id,\n"
                    + " nomerazao razao,\n"
                    + " cpfcnpj cnpj,\n"
                    + " '2' empresa\n"
                    + "from cliente2\n"
                    + "where nomerazao = colativo "
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setNome(rst.getString("razao"));
                    imp.setIdEmpresa(rst.getString("empresa"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " n_duplicata id,\n"
                    + " sacadodescritivo obs,\n"
                    + " split_part(vencimento,'/',3)||'-'||split_part(vencimento,'/',2)||'-'||split_part(vencimento,'/',1) vencimento,\n"
                    + " split_part(emissao,'/',3)||'-'||split_part(emissao,'/',2)||'-'||split_part(emissao,'/',1) emissao,\n"
                    + " replace(valorliquido,',','.') valor,\n"
                    + " codcliente clienteid,\n"
                    + " coalesce(ndoc, n_duplicata) numerocupom\n"
                    + "from contasareceber\n"
                    + "where \n"
                    + " codcliente in (\n"
                    + "  select codigo\n"
                    + "	from cliente2\n"
                    + "   where nomerazao = colativo )"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("clienteid"));
                    imp.setEcf("1");
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(rst.getTimestamp("vencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

}
