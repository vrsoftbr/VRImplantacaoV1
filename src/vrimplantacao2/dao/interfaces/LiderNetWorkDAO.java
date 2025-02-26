/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class LiderNetWorkDAO extends InterfaceDAO implements MapaTributoProvider {

    private String arquivo;
    private String arquivoLojaCliente;
    private String arquivoMapaTributacao;
    private Map<String, String> opcoes = new LinkedHashMap<>();
    private SimpleDateFormat formatData = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd", "IMPORTACAO", "PLANILHA", "FORMATO_DATA"));
    private SimpleDateFormat formatDataCompleta = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd hh:mm:ss.SSS", "IMPORTACAO", "PLANILHA", "FORMATO_DATA_COMPLETA"));

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getArquivoLojaCliente() {
        return arquivoLojaCliente;
    }

    public void setArquivoLojaCliente(String arquivoLojaCliente) {
        this.arquivoLojaCliente = arquivoLojaCliente;
    }

    public String getArquivoMapaTributacao() {
        return arquivoMapaTributacao;
    }

    public void setArquivoMapaTributacao(String arquivoMapaTributacao) {
        this.arquivoMapaTributacao = arquivoMapaTributacao;
    }

    public Map<String, String> getOpcoes() {
        return opcoes;
    }

    @Override
    public String getSistema() {
        return "LiderNetWork";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
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
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.MAPA_TRIBUTACAO
                }
        ));
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        Arquivo lojas = ArquivoFactory.getArquivo(this.arquivoLojaCliente, null);

        for (LinhaArquivo rs : lojas) {
            result.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        Arquivo mapaTrib = ArquivoFactory.getArquivo(this.arquivoMapaTributacao, null);

        for (LinhaArquivo rs : mapaTrib) {
            result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, null);
        ProgressBar.setStatus("Carregando produtos...");

        for (LinhaArquivo rst : produtos) {
            String id = rst.getString("id");

            if (id != null && !"".equals(id.trim())) {
                ProdutoIMP imp = new ProdutoIMP();

                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(id.replace(".", ""));
                imp.setEan("0" + rst.getString("codigobarras"));
                
                if (Utils.formataNumero(imp.getEan()).trim().length() <= 6) {
                    imp.seteBalanca(true);
                } else {
                    imp.seteBalanca(false);
                }
                
                //imp.seteBalanca(Integer.parseInt(Utils.formataNumero(rst.getString("balanca"))) == 1);
                imp.setValidade(rst.getInt("validade"));
                imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setTipoEmbalagemCotacao(rst.getString("qtdembalagem_cotacao"));
                imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem_cotacao"));
                imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                imp.setSituacaoCadastro(rst.getInt("ativo"));
                imp.setMargem(Double.parseDouble(rst.getString("margem").replace(".", "").replace(",", ".")));
                imp.setCustoComImposto(Double.parseDouble(rst.getString("custo").replace(".", "").replace(",", ".")));
                imp.setCustoSemImposto(Double.parseDouble(rst.getString("custo").replace(".", "").replace(",", ".")));
                imp.setPrecovenda(Double.parseDouble(rst.getString("precovenda").replace(".", "").replace(",", ".")));
                imp.setEstoque(Double.parseDouble(rst.getString("estoque").replace(".", "").replace(",", ".")));
                imp.setEstoqueMinimo(Double.parseDouble(rst.getString("estoqueminimo").replace(".", "").replace(",", ".")));
                imp.setNcm(rst.getString("ncm"));
                imp.setCest(rst.getString("cest"));
                imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                imp.setPiscofinsCstCredito(rst.getString("piscofins_entrada"));
                imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                imp.setIcmsDebitoId(rst.getString("icms_cod_cf_est"));
                imp.setIcmsDebitoForaEstadoId(rst.getString("icms_cod_cf_fora"));
                imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_cod_cf_fora"));
                imp.setIcmsCreditoId(rst.getString("icms_cod_est"));
                imp.setIcmsCreditoForaEstadoId(rst.getString("icms_cod_fora"));
                result.add(imp);
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        Arquivo fornecedores = ArquivoFactory.getArquivo(this.arquivo, null);
        ProgressBar.setStatus("Carregando fornecedores...");

        for (LinhaArquivo rst : fornecedores) {
            FornecedorIMP imp = new FornecedorIMP();
            imp.setImportLoja(getLojaOrigem());
            imp.setImportSistema(getSistema());
            imp.setImportId(rst.getString("id"));
            imp.setRazao(rst.getString("razao"));
            imp.setFantasia(imp.getRazao());
            imp.setCnpj_cpf(rst.getString("cnpj"));
            imp.setIe_rg(rst.getString("ie_rg"));
            imp.setEndereco(rst.getString("endereco"));
            imp.setNumero(rst.getString("numero"));
            imp.setBairro(rst.getString("bairro"));
            imp.setMunicipio(rst.getString("cidade"));
            imp.setUf(rst.getString("estado"));
            imp.setCep(rst.getString("cep"));
            imp.setIbge_municipio(rst.getInt("cidade_ibge"));
            imp.setTel_principal(rst.getString("telefone"));

            if ((rst.getString("fax") != null)
                    && (!rst.getString("fax").trim().isEmpty())) {
                imp.addTelefone("FAX", rst.getString("fax"));
            }

            if ((rst.getString("email") != null)
                    && (!rst.getString("email").trim().isEmpty())) {
                imp.addEmail("EMAIL", rst.getString("email"), TipoContato.NFE);
            }

            result.add(imp);
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        Arquivo prodForn = ArquivoFactory.getArquivo(this.arquivo, null);
        ProgressBar.setStatus("Carregando produtos fornecedores...");

        for (LinhaArquivo rst : prodForn) {
            ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
            imp.setImportLoja(getLojaOrigem());
            imp.setImportSistema(getSistema());
            imp.setIdProduto(rst.getString("idproduto").replace(".", ""));
            imp.setIdFornecedor(rst.getString("idfornecedor"));
            imp.setCodigoExterno(rst.getString("codigoexterno"));
            imp.setQtdEmbalagem(Double.parseDouble(rst.getString("qtdembalagem").replace(".", "").replace(",", ".")));
            result.add(imp);
        }
        return result;
    }
}
