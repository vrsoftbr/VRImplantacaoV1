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
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author guilhermegomes
 */
public class PhoenixDAO extends InterfaceDAO {

    private String arquivoProduto;
    private String arquivoProdutoLoja;
    private String arquivoFamilia;
    private String arquivoFornecedor;
    private String arquivoProdutoFornecedor;
    private String arquivoCliente;
    private String arquivoRotativo;

    private Map<String, String> opcoes = new LinkedHashMap<>();
    private SimpleDateFormat formatData = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd", "IMPORTACAO", "PLANILHA", "FORMATO_DATA"));

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
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO
                }
        ));
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

    public String getArquivoLoja() {
        return arquivoProdutoLoja;
    }

    public void setArquivoLoja(String arquivo) {
        this.arquivoProdutoLoja = arquivo;
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
        return "Phoenix";
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        Arquivo familias = ArquivoFactory.getArquivo(this.arquivoFamilia, getOpcoes());
        ProgressBar.setStatus("Carregando família de produtos...");
        int cont = 0;

        for (LinhaArquivo linha : familias) {

            String id = linha.getString("Codigo");

            if (id != null && !"".equals(id.trim())) {

                FamiliaProdutoIMP familia = new FamiliaProdutoIMP();

                familia.setImportSistema(getSistema());
                familia.setImportLoja(getLojaOrigem());
                familia.setImportId(id);
                familia.setDescricao(linha.getString("Descricao"));
                familia.setSituacaoCadastro(SituacaoCadastro.ATIVO);

                result.add(familia);
            }
            cont++;
            ProgressBar.setStatus("Carregando família de produtos..." + cont);
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.VR_ATACADO) {
            
            List<ProdutoIMP> result = new ArrayList<>();

            Arquivo produtos = ArquivoFactory.getArquivo(this.arquivoProduto, getOpcoes());
            int cont1 = 0;
            int cont2 = 0;

            ProgressBar.setStatus("Carregando produtos atacado...");

            for (LinhaArquivo linha : produtos) {
                String id = linha.getString("Codigo");

                if (id != null && !"".equals(id.trim())) {

                    ProdutoIMP produto = new ProdutoIMP();

                    produto.setImportSistema(getSistema());
                    produto.setImportLoja(getLojaOrigem());
                    produto.setImportId(id);
                    produto.setPrecovenda(linha.getDouble("PrecoAtacado"));

                    result.add(produto);
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
        return null;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivoProduto, getOpcoes());

        int cont1 = 0;
        int cont2 = 0;

        ProgressBar.setStatus("Carregando produtos...");

        for (LinhaArquivo linha : produtos) {

            String id = linha.getString("Codigo");

            if (id != null && !"".equals(id.trim())) {

                ProdutoIMP produto = new ProdutoIMP();

                produto.setImportSistema(getSistema());
                produto.setImportLoja(getLojaOrigem());
                produto.setImportId(id);
                produto.setEan(linha.getString("CodigoBarrasUnidade"));
                String ean_planilha = linha.getString("CodigoBarrasUnidade");
                if ((ean_planilha != null) && (!"".equals(ean_planilha)) && (ean_planilha.length() > 14)) {
                    produto.setEan(ean_planilha.substring(0, 14));
                }
                produto.setQtdEmbalagem(linha.getInt("EmbalagemVolume"));
                produto.setQtdEmbalagemCotacao(linha.getInt("qtdembalagemcotacao"));
                if (produto.getQtdEmbalagemCotacao() == 0) {
                    produto.setQtdEmbalagemCotacao(produto.getQtdEmbalagem());
                }
                produto.setTipoEmbalagem(linha.getString("Unidade"));
                produto.setTipoEmbalagemCotacao(linha.getString("unidadecotacao"));
                if (produto.getTipoEmbalagemCotacao() == null) {
                    produto.setTipoEmbalagemCotacao(produto.getTipoEmbalagem());
                }

                produto.setDescricaoCompleta(linha.getString("Descricao"));
                produto.setDescricaoReduzida(produto.getDescricaoCompleta());
                produto.setDescricaoGondola(produto.getDescricaoCompleta());
                produto.setIdFamiliaProduto(linha.getString("EkCodigoFamilia"));
                produto.setDataCadastro(getData(linha.getString("DataCadastro")));
                produto.setValidade(linha.getInt("ValidadeDias"));
                produto.setMargem(linha.getDouble("margem"));
                produto.setMargemMaxima(linha.getDouble("margemmaxima"));
                produto.setMargemMinima(linha.getDouble("margemminima"));
                produto.setEstoque(linha.getDouble("Quantidade"));
                produto.setEstoqueMinimo(linha.getDouble("QuantidadeMinima"));
                produto.setEstoqueMaximo(linha.getDouble("QuantidadeMaxima"));
                produto.setCustoComImposto(linha.getDouble("Custo"));
                produto.setCustoSemImposto(linha.getDouble("CustoReal"));
                produto.setPrecovenda(linha.getDouble("PrecoVarejo"));
                produto.setNcm(linha.getString("CodigoNCM"));
                produto.setCest(linha.getString("CEST"));

                int pisCofins = linha.getInt("EkCodigoGrupoTributario");

                switch (pisCofins) {
                    case 21:
                        produto.setPiscofinsCstDebito(1);
                        break;
                    case 31:
                        produto.setPiscofinsCstDebito(1);
                        break;
                    case 33:
                        produto.setPiscofinsCstDebito(6);
                        break;
                    case 41:
                        produto.setPiscofinsCstDebito(1);
                        break;
                    case 43:
                        produto.setPiscofinsCstDebito(6);
                        break;
                }

                String icmsDebito = linha.getString("TributacaoPDV");
                int cst;
                double reducao, aliquota;

                switch (icmsDebito) {
                    case "12":
                        cst = 40;
                        reducao = 0;
                        aliquota = 0;
                        break;
                    case "13":
                        cst = 41;
                        reducao = 0;
                        aliquota = 0;
                        break;
                    case "21":
                        cst = 41;
                        reducao = 0;
                        aliquota = 0;
                        break;
                    case "01":
                        cst = 0;
                        reducao = 0;
                        aliquota = 19;
                        break;
                    case "03":
                        cst = 0;
                        reducao = 0;
                        aliquota = 7;
                        break;
                    case "05":
                        cst = 0;
                        reducao = 0;
                        aliquota = 8;
                        break;
                    case "15":
                        cst = 60;
                        reducao = 0;
                        aliquota = 0;
                        break;
                    case "20":
                        cst = 40;
                        reducao = 0;
                        aliquota = 0;
                        break;
                    case "22":
                        cst = 40;
                        reducao = 0;
                        aliquota = 0;
                        break;
                    case "02":
                        cst = 0;
                        reducao = 0;
                        aliquota = 13;
                        break;
                    case "04":
                        cst = 0;
                        reducao = 0;
                        aliquota = 26;
                        break;
                    default:
                        cst = 41;
                        reducao = 0;
                        aliquota = 0;
                }

                produto.setIcmsAliqConsumidor(aliquota);
                produto.setIcmsCstConsumidor(cst);
                produto.setIcmsReducaoConsumidor(reducao);
                produto.setIcmsAliqSaida(aliquota);
                produto.setIcmsCstSaida(cst);
                produto.setIcmsReducaoSaida(reducao);
                produto.setIcmsAliqSaidaForaEstado(aliquota);
                produto.setIcmsCstSaidaForaEstado(cst);
                produto.setIcmsReducaoSaidaForaEstado(reducao);
                produto.setIcmsAliqSaidaForaEstadoNF(aliquota);
                produto.setIcmsCstSaidaForaEstadoNF(cst);
                produto.setIcmsReducaoSaidaForaEstadoNF(reducao);
                produto.setIcmsAliqEntrada(aliquota);
                produto.setIcmsCstEntrada(cst);
                produto.setIcmsReducaoEntrada(reducao);
                produto.setIcmsAliqEntradaForaEstado(aliquota);
                produto.setIcmsCstEntradaForaEstado(cst);
                produto.setIcmsReducaoEntradaForaEstado(reducao);

                result.add(produto);
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
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivoFornecedor, getOpcoes());
        ProgressBar.setStatus("Carregando fornecedores...");

        int cont1 = 0;
        int cont2 = 0;

        for (LinhaArquivo linha : produtos) {

            String id = linha.getString("PKCodigo");

            if (id != null && !"".equals(id.trim())) {

                FornecedorIMP forn = new FornecedorIMP();

                forn.setImportSistema(getSistema());
                forn.setImportLoja(getLojaOrigem());
                forn.setImportId(id);

                forn.setRazao(linha.getString("RazaoSocial"));
                forn.setFantasia(linha.getString("NomeFantasia"));
                forn.setCnpj_cpf(linha.getString("CGC"));
                forn.setIe_rg(linha.getString("INSC"));
                forn.setInsc_municipal(linha.getString("insc_municipal"));

                forn.setEndereco(linha.getString("RuaNumero"));
                forn.setComplemento(linha.getString("complemento"));
                forn.setBairro(linha.getString("Bairro"));
                forn.setMunicipio(linha.getString("Cidade"));
                forn.setUf(linha.getString("Estado"));
                forn.setCep(linha.getString("CEP"));

                forn.setTel_principal(linha.getString("Telefone1"));

                String email = linha.getString("Email2");

                if (email != null && !email.isEmpty()) {
                    forn.addContato("1", "EMAIL", null, null, TipoContato.NFE, email);
                }

                result.add(forn);
            }

            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando fornecedores..." + cont1);
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivoProdutoFornecedor, getOpcoes());

        ProgressBar.setStatus("Carregando Produtos Fornecedores");

        int cont1 = 0;
        int cont2 = 0;

        for (LinhaArquivo linha : produtos) {

            ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

            imp.setImportSistema(getSistema());
            imp.setImportLoja(getLojaOrigem());
            imp.setIdFornecedor(linha.getString("EkCodigoFornecedor"));
            imp.setIdProduto(linha.getString("EkCodigoMercadoria"));
            imp.setCodigoExterno(linha.getString("EspecificacaoNova"));

            result.add(imp);

            cont2++;
            cont1++;
            if (cont2 == 1000) {
                cont2 = 0;
                ProgressBar.setStatus("Carregando Produtos Fornecedores..." + cont1);
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        Map<String, TipoEstadoCivil> estCivil = new HashMap<>();
        for (TipoEstadoCivil est : TipoEstadoCivil.values()) {
            estCivil.put(est.toString().substring(0, 3), est);
        }

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivoCliente, getOpcoes());

        for (LinhaArquivo linha : arq) {

            ClienteIMP imp = new ClienteIMP();

            imp.setId(linha.getString("CPFCGC"));
            imp.setCnpj(linha.getString("CPFCGC"));

            long cnpj = Utils.stringToLong(linha.getString("CPFCGC"));
            if (cnpj > 99999999999L) {
                imp.setInscricaoestadual(linha.getString("Inscricao"));
            } else {
                imp.setInscricaoestadual(linha.getString("RG"));
            }

            imp.setRazao(linha.getString("Nome"));
            imp.setFantasia(linha.getString("fantasia"));
            imp.setEndereco(linha.getString("Rua"));
            imp.setNumero(linha.getString("Numero"));
            imp.setComplemento(linha.getString("Complemento"));
            imp.setBairro(linha.getString("Bairro"));
            imp.setMunicipio(linha.getString("Cidade"));
            imp.setUf(linha.getString("Estado"));
            imp.setCep(linha.getString("Cep"));
            String civil = linha.getString("EstadoCivil") + "   ";
            civil = (civil != null ? civil.substring(1, 3) : "NAO");
            imp.setEstadoCivil(estCivil.get(civil));
            imp.setDataNascimento(getData(linha.getString("DataNascimento")));
            imp.setDataCadastro(getData(linha.getString("DataCadastro")));
            String sexo = linha.getString("sexo") != null ? linha.getString("Sexo") : "";
            imp.setSexo("F".startsWith(sexo.toUpperCase()) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
            imp.setValorLimite(linha.getDouble("LimiteCredito"));
            imp.setTelefone(linha.getString("telefone"));
            imp.setCelular(linha.getString("celular"));
            imp.setEmail(linha.getString("Email2"));
            imp.setPrazoPagamento(linha.getInt("Prazo"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivoRotativo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            CreditoRotativoIMP imp = new CreditoRotativoIMP();

            imp.setId(linha.getString("Lancamento"));
            imp.setCnpjCliente(linha.getString("CPFCGC"));
            imp.setDataEmissao(getData(linha.getString("DataEmissao")));
            imp.setDataVencimento(getData(linha.getString("DataVencimento")));
            imp.setEcf(linha.getString("ECF"));
            imp.setIdCliente(linha.getString("CPFCGC"));
            imp.setValor(linha.getDouble("Valor"));

            result.add(imp);
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
}
