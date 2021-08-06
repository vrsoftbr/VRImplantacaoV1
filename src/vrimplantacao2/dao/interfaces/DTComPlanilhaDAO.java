package vrimplantacao2.dao.interfaces;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.Exceptions;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import static vrimplantacao2.dao.interfaces.DtComDAO.vBalanca;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class DTComPlanilhaDAO extends InterfaceDAO implements MapaTributoProvider {

    private String arquivoProduto;
    private String arquivoCEST;
    private String arquivoTributacao;
    private String arquivoFamilia;
    private String arquivoFornecedor;
    private String arquivoProdutoFornecedor;
    private String arquivoCliente;
    private String arquivoRotativo;

    private Map<String, String> opcoes = new LinkedHashMap<>();
    private SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
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
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.VR_ATACADO
                }
        ));
    }

    public String getArquivoCEST() {
        return arquivoCEST;
    }

    public void setArquivoCEST(String arquivoCEST) {
        this.arquivoCEST = arquivoCEST;
    }

    public void setFormatoData(String format) {
        this.formatData = new SimpleDateFormat(format);
    }

    public String getArquivo() {
        return arquivoProduto;
    }

    public void setArquivo(String arquivo) {
        this.arquivoProduto = arquivo;
    }

    public String getArquivoTributacao() {
        return arquivoTributacao;
    }

    public void setArquivoTributacao(String arquivoTributacao) {
        this.arquivoTributacao = arquivoTributacao;
    }

    public String getArquivoFamilia() {
        return arquivoFamilia;
    }

    public void setArquivoFamilia(String arquivo) {
        this.arquivoFamilia = arquivo;
    }

    public String getArquivoFornecedor() {
        return arquivoFornecedor;
    }

    public void setArquivoFornecedor(String arquivo) {
        this.arquivoFornecedor = arquivo;
    }

    public String getArquivoProdutoFornecedor() {
        return arquivoProdutoFornecedor;
    }

    public void setArquivoProdutoFornecedor(String arquivo) {
        this.arquivoProdutoFornecedor = arquivo;
    }

    public String getArquivoCliente() {
        return arquivoCliente;
    }

    public void setArquivoCliente(String arquivo) {
        this.arquivoCliente = arquivo;
    }

    public String getArquivoRotativo() {
        return arquivoRotativo;
    }

    public void setArquivoRotativo(String arquivo) {
        this.arquivoRotativo = arquivo;
    }

    public Map<String, String> getOpcoes() {
        return opcoes;
    }

    @Override
    public String getSistema() {
        return "DTCOM";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        Map<String, String> cests = new HashMap<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivoProduto, getOpcoes());
        Arquivo CEST = ArquivoFactory.getArquivo(this.arquivoCEST, getOpcoes());

        int cont1 = 0;
        int cont2 = 0;

        ProgressBar.setStatus("Carregando produtos...");

        Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().
                carregarProdutosBalanca();

        for (LinhaArquivo linha : CEST) {
            cests.put(linha.getString("NCM"), linha.getString("CEST"));
        }

        for (LinhaArquivo linha : produtos) {

            String id = linha.getString("codigo");

            if (id != null && !"".equals(id.trim())) {

                ProdutoIMP imp = new ProdutoIMP();

                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(id);
                imp.setDescricaoCompleta(Utils.acertarTexto(linha.getString("descricao")));
                imp.setDescricaoReduzida(Utils.acertarTexto(linha.getString("descpdv")));
                imp.setDescricaoGondola(Utils.acertarTexto(linha.getString("descricao")));
                imp.setTipoEmbalagem(linha.getString("unidade").toUpperCase());
                imp.setValidade(linha.getInt("validade"));
                imp.setCodMercadologico1(linha.getString("secao"));
                imp.setCodMercadologico2(linha.getString("subsec"));
                imp.setCodMercadologico3("1");
                imp.setEan(linha.getString("ean1"));
                imp.setPrecovenda(linha.getDouble("venda"));
                imp.setCustoComImposto(linha.getDouble("cust_fisca"));
                imp.setCustoSemImposto(linha.getDouble("custunit"));
                imp.setMargem(linha.getDouble("perfixo"));
                imp.setEstoque(linha.getDouble("qtest"));
                imp.setEstoqueMinimo(linha.getDouble("minimo"));
                imp.setDataCadastro(getData("dat_cad"));
                imp.setPesoLiquido(linha.getDouble("peso"));
                imp.setPiscofinsCstDebito(linha.getString("cstpis"));

                imp.setNcm(linha.getString("ncm"));

                if (cests.containsKey(imp.getNcm())) {
                    imp.setCest(cests.get(imp.getNcm()));
                }

                imp.setIcmsConsumidorId(linha.getString("tributo"));
                imp.setIcmsDebitoId(linha.getString("tributo"));
                imp.setIcmsDebitoForaEstadoNfId(linha.getString("tributo"));
                imp.setIcmsDebitoForaEstadoId(linha.getString("tributo"));
                imp.setIcmsCreditoId(linha.getString("tributo"));
                imp.setIcmsCreditoForaEstadoId(linha.getString("tributo"));
                imp.setSituacaoCadastro((getData("dat_can")) == null
                        ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                if (vBalanca) {
                    if ((linha.getString("ean1") != null)
                            && ("F".equals(linha.getString("tipvenda").trim()))) {
                        ProdutoBalancaVO produtoBalanca;
                        long codigoProduto;
                        String ean = linha.getString("ean1").trim().substring(6, 12);
                        imp.setEan(ean);
                        codigoProduto = Long.parseLong(imp.getEan().trim());

                        if (codigoProduto <= Integer.MAX_VALUE) {
                            produtoBalanca = produtosBalanca.get((int) codigoProduto);
                        } else {
                            produtoBalanca = null;
                        }

                        if (produtoBalanca != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(produtoBalanca.getValidade() > 1
                                    ? produtoBalanca.getValidade() : linha.getInt("validade"));
                        } else {
                            imp.setValidade(0);
                            imp.seteBalanca(false);
                        }
                    }
                } else {
                    imp.seteBalanca(false);
                    imp.setValidade(linha.getInt("validade"));
                }

                result.add(imp);
            }

            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando produtos..." + cont1);
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivoProduto, getOpcoes());

        int cont1 = 0;
        int cont2 = 0;

        ProgressBar.setStatus("Carregando EANs...");

        for (LinhaArquivo linha : produtos) {
            String id = linha.getString("codigo");

            if (id != null && !"".equals(id.trim())) {
                ProdutoIMP imp = new ProdutoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(id);
                imp.setQtdEmbalagem(linha.getInt("qtdcaixa"));

                if ((linha.getString("ean2") != null)
                        && (!"0000000000000".equals(linha.getString("ean2")))) {
                    imp.setEan(linha.getString("ean2"));
                }

                result.add(imp);
            }
            
            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando EANs..." + cont1);
            }
        }

        return result;
    }

    private Date getData(String format) {
        if (format != null && !"".equals(format.trim())) {
            try {
                return format == null ? null : formatData.parse(format);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivoTributacao, getOpcoes());

        for (LinhaArquivo linha : arq) {
            result.add(new MapaTributoIMP(
                    linha.getString("tribu"),
                    linha.getString("desctribu"),
                    linha.getInt("cst"),
                    linha.getDouble("PERCENT"),
                    linha.getDouble("REDBC"),
                    linha.getDouble("FECP"),
                    false,
                    0
            ));
        }

        return result;
    }
}
