package vrimplantacao2.dao.interfaces;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import static vrimplantacao2.dao.cadastro.produto.OpcaoProduto.INVENTARIO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.utils.arquivo.csv.ArquivoCSV2;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.convenio.transacao.SituacaoTransacaoConveniado;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.SituacaoCheque;
import vrimplantacao2.vo.enums.TipoCancelamento;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoDesconto;
import vrimplantacao2.vo.enums.TipoEmbalagem;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoIva;
import vrimplantacao2.vo.enums.TipoOrgaoPublico;
import vrimplantacao2.vo.enums.TipoPagamento;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.enums.TipoVistaPrazo;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.InventarioIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.PromocaoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

public class PlanilhaDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(PlanilhaDAO.class.getName());

    private String arquivo;
    private String sistema = "PLANILHA";
    private Map<String, String> opcoes = new LinkedHashMap<>();
    private SimpleDateFormat formatData = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd", "IMPORTACAO", "PLANILHA", "FORMATO_DATA"));
    private SimpleDateFormat formatDataCompleta = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd hh:mm:ss.SSS", "IMPORTACAO", "PLANILHA", "FORMATO_DATA_COMPLETA"));
    private boolean naoUsaMapaTributacao = false;

    public void setNaoUsaMapaTributacao(boolean naoUsaMapaTributacao) {
        this.naoUsaMapaTributacao = naoUsaMapaTributacao;
    }

    public void setFormatoData(String format) {
        this.formatData = new SimpleDateFormat(format);
    }

    public void setFormatoDataCompleta(String format) {
        this.formatDataCompleta = new SimpleDateFormat(format);
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {

        Set<OpcaoProduto> result = super.getOpcoesDisponiveisProdutos();
        result.add(OpcaoProduto.PAUTA_FISCAL);
        result.add(OpcaoProduto.PAUTA_FISCAL_PRODUTO);
        result.add(INVENTARIO);
        result.add(OpcaoProduto.OFERTA);
        result.add(OpcaoProduto.IMPORTAR_COPIAR_ICMS_DEBITO_NO_CREDITO);
        result.add(OpcaoProduto.ICMS_CONSUMIDOR);
        result.add(OpcaoProduto.PAUTA_FISCAL);
        result.add(OpcaoProduto.PAUTA_FISCAL_PRODUTO);
        result.add(OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS);
        result.add(OpcaoProduto.ATACADO);
        result.add(OpcaoProduto.PRODUTOS);
        result.add(OpcaoProduto.EAN);
        result.add(OpcaoProduto.MANTER_CODIGO_MERCADOLOGICO);
        result.add(OpcaoProduto.NUTRICIONAL);
        result.add(OpcaoProduto.RECEITA_BALANCA);
        result.add(OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR);
        result.add(OpcaoProduto.ASSOCIADO);
        result.add(OpcaoProduto.PROMOCAO);

        return result;
    }

    public void setSistema(String sistema) {
        if (sistema == null) {
            sistema = "PLANILHA";
        }
        this.sistema = sistema;
    }

    @Override
    public String getSistema() {
        return this.sistema;
    }

    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public Map<String, String> getOpcoes() {
        return opcoes;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        Arquivo mercadologicos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando mercadologico de produtos...");
        int cont = 0;

        for (LinhaArquivo linha : mercadologicos) {
            MercadologicoIMP helper = new MercadologicoIMP();

            helper.setImportSistema(getSistema());
            helper.setImportLoja(getLojaOrigem());

            helper.setMerc1ID(linha.getString("cod_mercadologico1"));
            helper.setMerc1Descricao(linha.getString("mercadologico1"));

            helper.setMerc2ID(linha.getString("cod_mercadologico2"));
            helper.setMerc2Descricao(linha.getString("mercadologico2"));

            helper.setMerc3ID(linha.getString("cod_mercadologico3"));
            helper.setMerc3Descricao(linha.getString("mercadologico3"));

            helper.setMerc4ID(linha.getString("cod_mercadologico4"));
            helper.setMerc4Descricao(linha.getString("mercadologico4"));

            helper.setMerc5ID(linha.getString("cod_mercadologico5"));
            helper.setMerc5Descricao(linha.getString("mercadologico5"));

            result.add(helper);
            cont++;
            ProgressBar.setStatus("Carregando mercadológico..." + cont);
        }

        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        Arquivo nutricionais = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando nutriional de produtos...");
        int cont = 0;

        for (LinhaArquivo linha : nutricionais) {
            NutricionalIMP imp = new NutricionalIMP();

            imp.setId(linha.getString("id_nutricional") == null ? "0" : linha.getString("id_nutricional"));
            imp.setDescricao(linha.getString("descricao") == null ? "DESCRICAO VAZIA" : Utils.acertarTexto(linha.getString("descricao")) /*+ " " + Integer.parseInt(linha.getString("quantidade"))*/);
            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
            imp.setCaloria(linha.getString("caloria") == null ? 0 : linha.getInt("caloria"));
            imp.setCarboidrato(linha.getString("carboidrato") == null ? 0 : linha.getDouble("carboidrato"));
            imp.setProteina(linha.getString("proteina") == null ? 0 : linha.getDouble("proteina"));
            imp.setGordura(linha.getString("gordura") == null ? 0 : linha.getDouble("gordura"));
            imp.setGorduraSaturada(linha.getString("gordurasaturada") == null ? 0 : linha.getDouble("gordurasaturada"));
            imp.setGorduraTrans(linha.getString("gorduratrans") == null ? 0 : linha.getDouble("gorduratrans"));
            imp.setFibra(linha.getString("fibra") == null ? 0 : linha.getDouble("fibra"));
            imp.setSodio(linha.getString("sodio") == null ? 0 : linha.getDouble("sodio"));
            imp.setPorcao(linha.getString("quantidade") == null ? "0" : linha.getString("quantidade"));
            imp.setId_tipounidadeporcao(linha.getString("Id_tipounidadeporcao") == null ? 2 : linha.getInt("Id_tipounidadeporcao"));
            imp.setMedidaInteira(linha.getString("medidaInteira") == null ? 1 : linha.getInt("medidaInteira"));
            imp.setId_tipomedidadecimal(linha.getString("id_tipomedidadecimal") == null ? 5 : linha.getInt("id_tipomedidadecimal"));
            imp.setIdTipoMedida(linha.getString("id_tipomedida") == null ? -1 : linha.getInt("id_tipomedida"));
            imp.setAcucaresadicionados(linha.getDouble("acucaresadicionados"));
            imp.setAcucarestotais(linha.getDouble("acucarestotais"));
            imp.addProduto(linha.getString("id_produto") == null ? "0" : linha.getString("id_produto"));
            imp.getMensagemAlergico().add(linha.getString("alergenicos") == null ? "" : Utils.acertarTexto(linha.getString("alergenicos")));

            result.add(imp);
            cont++;
            ProgressBar.setStatus("Carregando mercadológico..." + cont);
        }
        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();
        Arquivo receitas = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando Receita de produtos...");
        int cont = 0;

        for (LinhaArquivo linha : receitas) {
            ReceitaBalancaIMP imp = new ReceitaBalancaIMP();
            imp.setId(linha.getString("id_receita"));
            imp.setDescricao(linha.getString("descricao"));
            imp.setReceita(linha.getString("receita"));
            imp.getProdutos().add(linha.getString("id_produto"));
            result.add(imp);
            cont++;
            ProgressBar.setStatus("Carregando receitas de produtos... " + cont);
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        Arquivo familias = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando família de produtos...");
        int cont = 0;

        for (LinhaArquivo linha : familias) {
            String id = linha.getString("id_familiaproduto");
            if (id != null && !"".equals(id.trim())) {
                FamiliaProdutoIMP familia = new FamiliaProdutoIMP();

                familia.setImportSistema(getSistema());
                familia.setImportLoja(getLojaOrigem());
                familia.setImportId(id);
                familia.setDescricao(linha.getString("familiaproduto"));
                familia.setSituacaoCadastro(SituacaoCadastro.ATIVO);

                result.add(familia);
            }
            cont++;
            ProgressBar.setStatus("Carregando família de produtos..." + cont);
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando produtos...");

        int cont1 = 0;
        int cont2 = 0;

        Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();

        for (LinhaArquivo linha : produtos) {
            String id = linha.getString("id");
            if (id != null && !"".equals(id.trim())) {
                ProdutoIMP produto = new ProdutoIMP();

                produto.setImportSistema(getSistema());
                produto.setImportLoja(getLojaOrigem());
                produto.setImportId(id);
                produto.setEan(linha.getString("codigobarras"));
                String ean_planilha = linha.getString("codigobarras");
                if ((ean_planilha != null) && (!"".equals(ean_planilha)) && (ean_planilha.length() > 14)) {
                    produto.setEan(ean_planilha.substring(0, 14));
                }
                produto.setQtdEmbalagem(linha.getInt("qtdembalagem"));
                produto.setQtdEmbalagemCotacao(linha.getInt("qtdembalagemcotacao"));
                if (produto.getQtdEmbalagemCotacao() == 0) {
                    produto.setQtdEmbalagemCotacao(produto.getQtdEmbalagem());
                }
                produto.setTipoEmbalagem(linha.getString("unidade"));
                produto.setTipoEmbalagemCotacao(linha.getString("unidadecotacao"));
                if (produto.getTipoEmbalagemCotacao() == null) {
                    produto.setTipoEmbalagemCotacao(produto.getTipoEmbalagem());
                }
                switch (Utils.acertarTexto(linha.getString("balanca"))) {
                    case "S": {
                        produto.seteBalanca(true);
                    }
                    ;
                    break;
                    case "P": {
                        produto.seteBalanca(true);
                        produto.setTipoEmbalagem(TipoEmbalagem.KG.getSigla());
                    }
                    ;
                    break;
                    case "U": {
                        produto.seteBalanca(true);
                        produto.setTipoEmbalagem(TipoEmbalagem.UN.getSigla());
                    }
                    ;
                    break;
                    default: {
                        produto.seteBalanca(false);
                    }
                    ;
                    break;
                }
                int eanBal;
                if ("0".equals(Utils.stringLong(produto.getEan()))) {
                    eanBal = Utils.stringToInt(produto.getImportId());
                } else {
                    eanBal = Utils.stringToInt(produto.getEan());
                }
                ProdutoBalancaVO bal = produtosBalanca.get(eanBal);
                if (bal != null) {
                    produto.seteBalanca(true);
                    switch (bal.getPesavel()) {
                        case "U": {
                            produto.setTipoEmbalagem(TipoEmbalagem.UN.getSigla());
                        }
                        ;
                        break;
                        default: {
                            produto.setTipoEmbalagem(TipoEmbalagem.KG.getSigla());
                        }
                        ;
                        break;
                    }
                    if (bal.getValidade() != 0) {
                        produto.setValidade(bal.getValidade());
                    }
                }
                produto.setDescricaoCompleta(linha.getString("descricaocompleta"));
                produto.setDescricaoReduzida(linha.getString("descricaoreduzida"));
                produto.setDescricaoGondola(linha.getString("descricaogondola"));
                produto.setCodMercadologico1(linha.getString("cod_mercadologico1"));
                produto.setCodMercadologico2(linha.getString("cod_mercadologico2"));
                produto.setCodMercadologico3(linha.getString("cod_mercadologico3"));
                produto.setCodMercadologico4(linha.getString("cod_mercadologico4"));
                produto.setCodMercadologico5(linha.getString("cod_mercadologico5"));
                produto.setIdFamiliaProduto(linha.getString("id_familiaproduto"));
                produto.setPesoBruto(linha.getDouble("pesobruto"));
                produto.setPesoLiquido(linha.getDouble("pesoliquido"));
                produto.setDataCadastro(getData(linha.getString("datacadastro")));
                produto.setDataAlteracao(getData(linha.getString("dataalteracao")));
                produto.setValidade(linha.getInt("validade"));
                produto.setMargem(linha.getDouble("margem"));
                produto.setMargemMaxima(linha.getDouble("margemmaxima"));
                produto.setMargemMinima(linha.getDouble("margemminima"));
                produto.setEstoqueMaximo(linha.getDouble("estoquemaximo"));
                produto.setEstoqueMinimo(linha.getDouble("estoqueminimo"));
                produto.setEstoque(linha.getDouble("estoque"));
                produto.setCustoComImposto(linha.getDouble("custocomimposto"));
                produto.setCustoAnteriorComImposto(linha.getDouble("custocomimpostoanterior"));
                produto.setCustoSemImposto(linha.getDouble("custosemimposto"));
                produto.setCustoAnteriorSemImposto(linha.getDouble("custosemimpostoanterior"));
                produto.setCustoMedioComImposto(linha.getDouble("customediocomimposto"));
                produto.setCustoMedioSemImposto(linha.getDouble("customediosemimposto"));
                produto.setVendaPdv(linha.getBoolean("vendapdv"));
                produto.setSituacaoCadastro(linha.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                produto.setDescontinuado(linha.getBoolean("descontinuado"));
                produto.setNcm(linha.getString("ncm"));
                produto.setCest(linha.getString("cest"));
                produto.setPiscofinsCstCredito(linha.getString("piscofins_cst_credito"));
                produto.setPiscofinsCstDebito(linha.getString("piscofins_cst_debito"));
                produto.setPiscofinsNaturezaReceita(linha.getString("piscofins_natureza_receita"));

                //produto.setIcmsCst(linha.getInt("icms_cst"));
                //produto.setIcmsAliq(linha.getDouble("icms_aliquota"));
                //produto.setIcmsReducao(linha.getDouble("icms_reduzido"));
                if (naoUsaMapaTributacao) {
                    //ICMS ENTRADA
                    if (linha.existsColumn("icms_cst_entrada")) {
                        produto.setIcmsCstEntrada(linha.getInt("icms_cst_entrada"));
                        produto.setIcmsAliqEntrada(linha.getDouble("icms_aliquota_entrada"));
                        produto.setIcmsReducaoEntrada(linha.getDouble("icms_reduzido_entrada"));
                    }

                    if (linha.existsColumn("icms_cst_entrada_foraestado")) {
                        produto.setIcmsCstEntradaForaEstado(linha.getInt("icms_cst_entrada_foraestado"));
                        produto.setIcmsAliqEntradaForaEstado(linha.getDouble("icms_aliquota_entrada_foraestado"));
                        produto.setIcmsReducaoEntradaForaEstado(linha.getDouble("icms_reduzido_entrada_foraestado"));
                    } else {
                        produto.setIcmsCstEntradaForaEstado(linha.getInt("icms_cst_entrada"));
                        produto.setIcmsAliqEntradaForaEstado(linha.getDouble("icms_aliquota_entrada"));
                        produto.setIcmsReducaoEntradaForaEstado(linha.getDouble("icms_reduzido_entrada"));
                    }

                    //ICMS SAIDA
                    if (linha.existsColumn("icms_cst_saida")) {
                        produto.setIcmsCstSaida(linha.getInt("icms_cst_saida"));
                        produto.setIcmsAliqSaida(linha.getDouble("icms_aliquota_saida"));
                        produto.setIcmsReducaoSaida(linha.getDouble("icms_reduzido_saida"));
                    }

                    if (linha.existsColumn("icms_cst_saida_foraestado")) {
                        produto.setIcmsCstSaidaForaEstado(linha.getInt("icms_cst_saida_foraestado"));
                        produto.setIcmsAliqSaidaForaEstado(linha.getDouble("icms_aliquota_saida_foraestado"));
                        produto.setIcmsReducaoSaidaForaEstado(linha.getDouble("icms_reduzido_saida_foraestado"));
                    } else {
                        produto.setIcmsCstSaidaForaEstado(linha.getInt("icms_cst_saida"));
                        produto.setIcmsAliqSaidaForaEstado(linha.getDouble("icms_aliquota_saida"));
                        produto.setIcmsReducaoSaidaForaEstado(linha.getDouble("icms_reduzido_saida"));

                    }

                    if (linha.existsColumn("icms_cst_saida_foraestadonf")) {
                        produto.setIcmsCstSaidaForaEstadoNF(linha.getInt("icms_cst_saida_foraestadonf"));
                        produto.setIcmsAliqSaidaForaEstadoNF(linha.getDouble("icms_aliquota_saida_foraestadonf"));
                        produto.setIcmsReducaoSaidaForaEstadoNF(linha.getDouble("icms_reduzido_saida_foraestadonf"));
                    } else {
                        produto.setIcmsCstSaidaForaEstadoNF(linha.getInt("icms_cst_saida"));
                        produto.setIcmsAliqSaidaForaEstadoNF(linha.getDouble("icms_aliquota_saida"));
                        produto.setIcmsReducaoSaidaForaEstadoNF(linha.getDouble("icms_reduzido_saida"));
                    }

                    if (linha.existsColumn("icms_cst_consumidor")) {
                        produto.setIcmsCstConsumidor(linha.getInt("icms_cst_consumidor"));
                        produto.setIcmsAliqConsumidor(linha.getDouble("icms_aliq_consumidor"));
                        produto.setIcmsReducaoConsumidor(linha.getDouble("icms_reduzido_consumidor"));
                    }

                } else {
                    //ICMS ENTRADA
                    if (linha.existsColumn("icms_credito_id")) {
                        produto.setIcmsCreditoId(linha.getString("icms_credito_id"));
                    }
                    if (linha.existsColumn("icms_credito_foraestado_id")) {
                        produto.setIcmsCreditoForaEstadoId(linha.getString("icms_credito_foraestado_id"));
                    }

                    //ICMS SAIDA
                    if (linha.existsColumn("icms_debito_id")) {
                        produto.setIcmsDebitoId(linha.getString("icms_debito_id"));
                    }
                    if (linha.existsColumn("icms_debito_foraestado_id")) {
                        produto.setIcmsDebitoForaEstadoId(linha.getString("icms_debito_foraestado_id"));
                    }
                    if (linha.existsColumn("icms_debito_foraestadonf_id")) {
                        produto.setIcmsDebitoForaEstadoNfId(linha.getString("icms_debito_foraestadonf_id"));
                    }
                    if (linha.existsColumn("icms_consumidor_id")) {
                        produto.setIcmsConsumidorId(linha.getString("icms_consumidor_id"));
                    }

                    //ID PAUTA FISCAL                
                    produto.setPautaFiscalId(linha.getString("id_pautafiscal"));
                }

                produto.setBeneficio("cbeneficio");
                produto.setCodigoGIA("cbeneficio");
                produto.setSugestaoCotacao(linha.getBoolean("sugestaocotacao"));
                produto.setSugestaoPedido(linha.getBoolean("sugestaopedido"));

                produto.setManterEAN(linha.getBoolean("manterean"));

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
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        if (opt == OpcaoProduto.ATACADO) {
            List<ProdutoIMP> vResult = new ArrayList<>();

            Arquivo atacado = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
            ProgressBar.setStatus("Carregando Atacado...");

            for (LinhaArquivo linha : atacado) {
                String id = linha.getString("id");
                if (id != null && !"".equals(id.trim())) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(id);

                    imp.setEan(linha.getString("codigobarras"));
                    String ean_planilha = imp.getEan();
                    if ((ean_planilha != null) && (!"".equals(ean_planilha)) && (ean_planilha.length() > 14)) {
                        imp.setEan(ean_planilha.substring(0, 14));
                    }
                    imp.setAtacadoPreco(linha.getDouble("precoatacado"));
                    imp.setPrecovenda(linha.getDouble("precovenda"));

                    vResult.add(imp);
                }
            }

            return vResult;
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo ean = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando EANs...");

        for (LinhaArquivo linha : ean) {
            String id = linha.getString("id");
            if (id != null && !"".equals(id.trim())) {
                ProdutoIMP imp = new ProdutoIMP();

                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(id);

                imp.setEan(linha.getString("codigobarras"));
                String ean_planilha = imp.getEan();
                if ((ean_planilha != null) && (!"".equals(ean_planilha)) && (ean_planilha.length() > 14)) {
                    imp.setEan(ean_planilha.substring(0, 14));
                }
                imp.setQtdEmbalagem(linha.getInt("qtdembalagem"));
                imp.setTipoEmbalagem(linha.getString("unidade"));

                result.add(imp);
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando fornecedores...");

        int cont1 = 0;
        int cont2 = 0;

        for (LinhaArquivo linha : produtos) {
            String id = linha.getString("id");
            if (id != null && !"".equals(id.trim())) {
                FornecedorIMP forn = new FornecedorIMP();

                forn.setImportSistema(getSistema());
                forn.setImportLoja(getLojaOrigem());
                forn.setImportId(id);

                forn.setRazao(linha.getString("razao"));
                forn.setFantasia(linha.getString("fantasia"));
                forn.setCnpj_cpf(linha.getString("cnpj_cpf"));
                forn.setIe_rg(linha.getString("ie_rg"));
                forn.setInsc_municipal(linha.getString("insc_municipal"));
                forn.setSuframa(linha.getString("suframa"));
                forn.setBloqueado("S".equals(linha.getString("bloqueado")));
                forn.setAtivo("S".equals(linha.getString("ativo")));

                forn.setEndereco(linha.getString("endereco"));
                forn.setNumero(linha.getString("numero"));
                forn.setComplemento(linha.getString("complemento"));
                forn.setBairro(linha.getString("bairro"));
                forn.setIbge_municipio(linha.getInt("ibge_municipio"));
                forn.setMunicipio(linha.getString("municipio"));
                forn.setIbge_uf(linha.getInt("ibge_uf"));
                forn.setUf(linha.getString("uf"));
                forn.setCep(linha.getString("cep"));

                forn.setCob_endereco(linha.getString("cob_endereco"));
                forn.setCob_numero(linha.getString("cob_numero"));
                forn.setCob_complemento(linha.getString("cob_complemento"));
                forn.setCob_bairro(linha.getString("cob_bairro"));
                forn.setCob_ibge_municipio(linha.getInt("cob_ibge_municipio"));
                forn.setCob_municipio(linha.getString("cob_municipio"));
                forn.setCob_ibge_uf(linha.getInt("cob_ibge_uf"));
                forn.setCob_uf(linha.getString("cob_uf"));
                forn.setCob_cep(linha.getString("cob_cep"));

                forn.setTel_principal(linha.getString("tel_principal"));
                forn.setQtd_minima_pedido(linha.getInt("qtd_minima_pedido"));
                forn.setValor_minimo_pedido(linha.getDouble("valor_minimo_pedido"));
                forn.setDatacadastro(getData(linha.getString("datacadastro")));
                forn.setObservacao(linha.getString("observacao"));
                if (linha.existsColumn("tipoempresa")) {
                    forn.setTipoEmpresa(linha.getInt("tipoempresa") == 1 ? TipoEmpresa.EPP_SIMPLES : TipoEmpresa.LUCRO_REAL);
                }

                int i = 1;
                while (true) {
                    String prefixo = "cont" + i + "_";
                    if (linha.existsColumn(prefixo + "nome")) {
                        if (!"".equals(linha.getString(prefixo + "nome").trim()));
                        {
                            FornecedorContatoIMP contato = forn.getContatos().make(
                                    forn.getImportSistema(),
                                    forn.getImportLoja(),
                                    forn.getImportId(),
                                    String.valueOf(i)
                            );

                            contato.setImportId(String.valueOf(i));
                            contato.setNome(linha.getString(prefixo + "nome"));
                            contato.setTelefone(linha.getString(prefixo + "telefone"));
                            contato.setCelular(linha.getString(prefixo + "celular"));
                            contato.setEmail(linha.getString(prefixo + "email"));
                            contato.setTipoContato(TipoContato.getByDescricao(linha.getString(prefixo + "tipo")));
                        }

                        i++;
                    } else {
                        break;
                    }
                }

                i = 1;
                while (true) {
                    String prefixo = "prazo" + i;
                    if (linha.existsColumn(prefixo)) {
                        forn.addPagamento(String.valueOf(i), Utils.stringToInt(linha.getString(prefixo)));
                        i++;
                    } else {
                        break;
                    }
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

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando Produtos Fornecedores");

        int cont1 = 0;
        int cont2 = 0;
        for (LinhaArquivo linha : produtos) {
            ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
            imp.setImportSistema(getSistema());
            imp.setImportLoja(getLojaOrigem());
            imp.setIdFornecedor(linha.getString("id_fornecedor"));
            imp.setIdProduto(linha.getString("id_produto"));
            imp.setCodigoExterno(linha.getString("codigoexterno"));
            imp.setQtdEmbalagem(linha.getInt("qtdembalagem"));
            imp.setCustoTabela(linha.getDouble("custo_tabelado"));
            imp.setDataAlteracao(getData(linha.getString("dataalteracao")));
            imp.setPesoEmbalagem(linha.getDouble("pesoembalagem"));
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
        String dataNascimento;
        Map<String, TipoEstadoCivil> estCivil = new HashMap<>();
        for (TipoEstadoCivil est : TipoEstadoCivil.values()) {
            estCivil.put(est.toString().substring(0, 3), est);
        }

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            ClienteIMP imp = new ClienteIMP();
            try {
                imp.setId(linha.getString("id"));
                if (linha.existsColumn("cpf")) {
                    imp.setCnpj(linha.getString("cnpj") == null || "".equals(linha.getString("cnpj"))
                            ? linha.getString("cpf") : linha.getString("cnpj"));
                } else {
                    imp.setCnpj(linha.getString("cnpj"));
                }

                long cnpj = Utils.stringToLong(linha.getString("cnpj"));
                if (cnpj > 99999999999L) {
                    imp.setInscricaoestadual(linha.getString("inscricaoestadual"));
                } else {
                    imp.setInscricaoestadual(linha.getString("rg"));
                }
                imp.setOrgaoemissor(linha.getString("orgaoemissor"));
                imp.setRazao(linha.getString("razao"));
                imp.setFantasia(linha.getString("fantasia"));
                imp.setAtivo(!"N".equalsIgnoreCase(linha.getString("ativo")));
                imp.setBloqueado("N".equalsIgnoreCase(linha.getString("bloqueado")));
                imp.setDataBloqueio(getData(linha.getString("dataBloqueio")));
                imp.setEndereco(linha.getString("endereco"));
                imp.setNumero(linha.getString("numero"));
                imp.setComplemento(linha.getString("complemento"));
                imp.setBairro(linha.getString("bairro"));
                imp.setMunicipioIBGE(linha.getInt("municipioIBGE"));
                imp.setMunicipio(linha.getString("municipio"));
                imp.setUfIBGE(linha.getInt("ufIBGE"));
                imp.setUf(linha.getString("uf"));
                imp.setCep(linha.getString("cep"));
                String civil = linha.getString("estadoCivil") + "   ";
                civil = (civil != null ? civil.substring(1, 3) : "NAO");
                imp.setEstadoCivil(estCivil.get(civil));
                imp.setDataNascimento(getData(linha.getString("dataNascimento")));
                imp.setDataCadastro(getData(linha.getString("dataCadastro")));
                String sexo = linha.getString("sexo") != null ? linha.getString("sexo") : "";
                imp.setSexo("F".startsWith(sexo.toUpperCase()) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                imp.setEmpresa(linha.getString("empresa"));
                imp.setEmpresaEndereco(linha.getString("empresaEndereco"));
                imp.setEmpresaNumero(linha.getString("empresaNumero"));
                imp.setEmpresaComplemento(linha.getString("empresaComplemento"));
                imp.setEmpresaBairro(linha.getString("empresaBairro"));
                imp.setEmpresaMunicipioIBGE(linha.getInt("empresaMunicipioIBGE"));
                imp.setEmpresaMunicipio(linha.getString("empresaMunicipio"));
                imp.setEmpresaUfIBGE(linha.getInt("empresaUfIBGE"));
                imp.setEmpresaUf(linha.getString("empresaUf"));
                imp.setEmpresaCep(linha.getString("empresaCep"));
                imp.setEmpresaTelefone(linha.getString("empresaTelefone"));
                imp.setDataAdmissao(getData(linha.getString("dataAdmissao")));
                imp.setCargo(linha.getString("cargo"));
                imp.setSalario(linha.getDouble("salario"));
                imp.setValorLimite(linha.getDouble("valorLimite"));
                imp.setNomeConjuge(linha.getString("nomeConjuge"));
                imp.setDataNascimentoConjuge(linha.getData("dataNascimentoConjuge"));
                imp.setNomePai(linha.getString("nomePai"));
                imp.setNomeMae(linha.getString("nomeMae"));
                imp.setObservacao(linha.getString("observacao"));
                imp.setObservacao2(linha.getString("observacao2"));
                imp.setDiaVencimento(linha.getInt("diaVencimento"));
                if (linha.getString("permiteCreditoRotativo") == null || linha.getString("permiteCreditoRotativo").isEmpty()) {
                    imp.setPermiteCreditoRotativo(true);
                } else {
                    imp.setPermiteCreditoRotativo(!"N".equalsIgnoreCase(linha.getString("permiteCreditoRotativo")));
                }
                imp.setPermiteCheque(!"N".equalsIgnoreCase(linha.getString("permiteCheque")));
                imp.setSenha(linha.getInt("senha"));
                imp.setTelefone(linha.getString("telefone"));
                imp.setCelular(linha.getString("celular"));
                imp.setEmail(linha.getString("email"));
                imp.setPrazoPagamento(linha.getInt("prazopagamento"));
                imp.setPonto(linha.getDouble("pontos"));
                //EVENTUAL
                imp.setFax(linha.getString("fax"));
                imp.setCobrancaTelefone(linha.getString("cobrancaTelefone"));
                imp.setPrazoPagamento(linha.getInt("prazopagamento"));
                imp.setCobrancaEndereco(linha.getString("cobrancaendereco"));
                imp.setCobrancaNumero(linha.getString("cobrancanumero"));
                imp.setCobrancaComplemento(linha.getString("cobrancacomplemento"));
                imp.setCobrancaBairro(linha.getString("cobrancabairro"));
                imp.setCobrancaMunicipioIBGE(linha.getInt("cobrancamunicipioibge"));
                imp.setCobrancaMunicipio(linha.getString("cobrancamunicipio"));
                imp.setCobrancaUfIBGE(linha.getInt("cobrancaufibge"));
                imp.setCobrancaUf(linha.getString("cobrancauf"));
                imp.setCobrancaCep(linha.getString("cobrancacep"));
                String tipoOrgaoPublicoStr = linha.getString("tipoorgaopublico");
                tipoOrgaoPublicoStr = Utils.acertarTexto(tipoOrgaoPublicoStr, "NENHUM");
                switch (tipoOrgaoPublicoStr) {
                    case "ESTADUAL":
                        imp.setTipoOrgaoPublico(TipoOrgaoPublico.ESTADUAL);
                        break;
                    case "FEDERAL":
                        imp.setTipoOrgaoPublico(TipoOrgaoPublico.FEDERAL);
                        break;
                    default:
                        imp.setTipoOrgaoPublico(TipoOrgaoPublico.NENHUM);
                        break;
                }
                imp.setLimiteCompra(linha.getDouble("limitecompra"));
                imp.setInscricaoMunicipal(linha.getString("inscricaomunicipal"));
                String tipoIndicadorIeStr = Utils.acertarTexto(linha.getString("tipoindicadorie"), "NAO CONTRIBUINTE");
                switch (tipoIndicadorIeStr) {
                    case "ICMS":
                        imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ICMS);
                        break;
                    case "ISENTO":
                        imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ISENTO);
                        break;
                    default:
                        imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                        break;
                }

                int i = 1;
                while (true) {
                    String prefixo = "cont" + i + "_";
                    if (linha.existsColumn(prefixo + "nome")) {
                        if (!"".equals(linha.getString(prefixo + "nome").trim()));
                        {
                            String email = linha.getString(prefixo + "email");
                            String cel = linha.getString(prefixo + "celular");
                            String fone = linha.getString(prefixo + "telefone");
                            String nome = linha.getString(prefixo + "nome");
                            if (!"".equals(nome)
                                    || !"".equals(cel)
                                    || !"".equals(fone)
                                    || !"".equals(email)) {
                                imp.addContato(String.valueOf(i), nome, fone, cel, email);
                            }
                        }

                        i++;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                throw e;
            }

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            CreditoRotativoIMP imp = new CreditoRotativoIMP();

            try {
                imp.setId(linha.getString("id"));
                imp.setCnpjCliente(linha.getString("cnpj"));
                imp.setDataEmissao(getData(linha.getString("emissao")));
                imp.setDataVencimento(getData(linha.getString("vencimento")));
                imp.setEcf(linha.getString("ecf"));
                imp.setIdCliente(linha.getString("idcliente"));
                imp.setJuros(linha.getDouble("juros"));
                imp.setMulta(linha.getDouble("multa"));
                imp.setNumeroCupom(linha.getString("cupom"));
                imp.setObservacao(linha.getString("observacao"));
                imp.setParcela(linha.getInt("parcela"));
                imp.setValor(linha.getDouble("valor"));
                if (linha.existsColumn("datapagamento")) {
                    if (getData(linha.getString("datapagamento")) != null) {
                        imp.addPagamento(
                                imp.getId(),
                                linha.getDouble("valorrecebido"),
                                linha.getDouble("descontopagamento"),
                                linha.getDouble("multapagamento"),
                                getData(linha.getString("datapagamento")),
                                linha.getString("observacaopagamento")
                        );
                    }
                }
            } catch (Exception e) {
                throw e;
            }

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<ContaReceberIMP> getContasReceber(Set<OpcaoContaReceber> opt) throws Exception {
        List<ContaReceberIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            ContaReceberIMP imp = new ContaReceberIMP();

            imp.setId(linha.getString("id"));
            imp.setIdClienteEventual(linha.getString("idclienteeventual"));
            imp.setIdFornecedor(linha.getString("idfornecedor"));
            imp.setDataEmissao(linha.getData("dataemissao"));
            imp.setDataVencimento(linha.getData("datavencimento"));
            imp.setValor(linha.getDouble("valor"));
            imp.setObservacao(linha.getString("observacao"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<CreditoRotativoPagamentoAgrupadoIMP> getCreditoRotativoPagamentoAgrupado() throws Exception {
        List<CreditoRotativoPagamentoAgrupadoIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            CreditoRotativoPagamentoAgrupadoIMP imp = new CreditoRotativoPagamentoAgrupadoIMP();

            imp.setIdCliente(linha.getString("idcliente"));
            imp.setValor(linha.getDouble("valor"));

            result.add(imp);
        }

        return result;
    }

    private String arquivoContaPagar;

    public void setArquivoContaPagar(String arquivoContaPagar) {
        this.arquivoContaPagar = arquivoContaPagar;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        Arquivo arq = ArquivoFactory.getArquivo(this.arquivoContaPagar, getOpcoes());

        Map<String, ContaPagarIMP> contas = new LinkedHashMap<>();

        for (LinhaArquivo linha : arq) {
            if (linha.getString("faturaid") != null && !"".equals(linha.getString("faturaid").trim())) {
                ContaPagarIMP imp = contas.get(linha.getString("faturaid"));
                if (imp == null) {
                    imp = new ContaPagarIMP();
                    imp.setId(linha.getString("faturaid"));
                    imp.setIdFornecedor(linha.getString("idfornecedor"));
                    imp.setIdTipoEntradaVR(linha.getString("tipoentradavr") != null ? linha.getInt("tipoentradavr") : null);
                    imp.setNumeroDocumento(linha.getString("numerodocumento"));
                    imp.setDataEntrada(getData(linha.getString("entrada")));
                    imp.setDataEmissao(getData(linha.getString("emissao")));
                    imp.setObservacao(linha.getString("observacaofatura"));
                    contas.put(imp.getId(), imp);
                }

                ContaPagarVencimentoIMP parc = imp.addVencimento(getData(linha.getString("vencimento")), linha.getDouble("valor"));
                parc.setId(linha.getString("parcelaid"));
                parc.setNumeroParcela(linha.getInt("numeroparcela") == 0 ? 1 : linha.getInt("numeroparcela"));
                parc.setDataPagamento(getData(linha.getString("pagoem")));
                parc.setObservacao(linha.getString("observacao"));
                parc.setPago(linha.getBoolean("pago"));
                if (linha.getString("tipopagamentovr") != null) {
                    TipoPagamento tp = new TipoPagamento(Utils.stringToInt(linha.getString("tipopagamentovr")), "");
                    parc.setTipoPagamento(tp);
                } else {
                    parc.setTipoPagamento(TipoPagamento.BOLETO_BANCARIO);
                }
                parc.setId_banco(linha.getInt("banco"));
                parc.setAgencia(linha.getString("agencia"));
                parc.setConta(linha.getString("conta"));
                parc.setNumerocheque(linha.getInt("numerocheque"));
                parc.setConferido(false);
            }
        }
        return new ArrayList<>(contas.values());
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            result.add(new MapaTributoIMP(
                    linha.getString("codtrib"),
                    linha.getString("descricao"),
                    linha.getInt("cst"),
                    linha.getDouble("aliquota"),
                    linha.getDouble("reduzido"),
                    linha.getDouble("fcp"),
                    linha.getBoolean("desonerado"),
                    linha.getDouble("percentualdesonerado")
            ));
        }

        return result;
    }

    private String arquivoVendas;
    private String arquivoVendasItens;

    public void setArquivoVendas(String arquivoVendas) {
        this.arquivoVendas = arquivoVendas;
    }

    public void setArquivoVendasItens(String arquivoVendasItens) {
        this.arquivoVendasItens = arquivoVendasItens;
    }

    @Override
    public List<VendaHistoricoIMP> getHistoricoVenda() throws Exception {
        List<VendaHistoricoIMP> result = new ArrayList<>();

        Arquivo arq = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        for (LinhaArquivo linha : arq) {
            VendaHistoricoIMP imp = new VendaHistoricoIMP();

            imp.setIdProduto(linha.getString("id_produto"));
            imp.setEan(linha.getString("ean"));
            imp.setData(getData(linha.getString("data")));
            imp.setPrecoVenda(linha.getDouble("preco"));
            imp.setQuantidade(linha.getDouble("qtd"));
            imp.setCustoComImposto(linha.getDouble("custocomimposto"));
            imp.setCustoSemImposto(linha.getDouble("custosemimposto"));
            imp.setPisCofinsCredito(linha.getDouble("piscofins_credito"));
            imp.setPisCofinsDebito(linha.getDouble("piscofins_debito"));
            imp.setOperacional(linha.getDouble("operacional"));
            imp.setIcmsCredito(linha.getDouble("icms_credito"));
            imp.setIcmsDebito(linha.getDouble("icms_debito"));
            imp.setValorTotal(linha.getDouble("valor_total"));
            imp.setOferta(linha.getBoolean("oferta"));
            imp.setCupomFiscal(linha.getBoolean("cupom"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {

        String delimiter = ";";
        char stringQuote = '\"';
        if (opcoes != null) {
            delimiter = getOpcoes().get("delimiter") != null ? getOpcoes().get("delimiter") : ";";
            stringQuote = (getOpcoes().get("quote") != null ? getOpcoes().get("quote") : "\"").charAt(0);
        }

        return new VendaIterator(new ArquivoCSV2(arquivoVendas, delimiter.charAt(0), stringQuote));

    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        String delimiter = ";";
        char stringQuote = '\"';
        if (opcoes != null) {
            delimiter = getOpcoes().get("delimiter") != null ? getOpcoes().get("delimiter") : ";";
            stringQuote = (getOpcoes().get("quote") != null ? getOpcoes().get("quote") : "\"").charAt(0);
        }

        return new VendaItemIterator(new ArquivoCSV2(arquivoVendasItens, delimiter.charAt(0), stringQuote));
    }

    private class VendaIterator implements Iterator<VendaIMP> {

        private ArquivoCSV2 csv;

        public VendaIterator(ArquivoCSV2 csv) {
            this.csv = csv;
        }

        @Override
        public boolean hasNext() {
            return this.csv.hasNext();
        }

        @Override
        public VendaIMP next() {

            LinhaArquivo ln = csv.next();

            VendaIMP imp = new VendaIMP();

            imp.setId(ln.getString("id"));
            imp.setNumeroCupom(ln.getInt("numerocupom"));
            imp.setEcf(ln.getInt("ecf"));
            imp.setData(ln.getData("data"));
            imp.setIdClientePreferencial(ln.getString("clientepreferencial"));
            imp.setHoraInicio(ln.getTime("horainicio"));
            imp.setHoraTermino(ln.getTime("horatermino"));
            imp.setCancelado("S".equals(ln.getString("cancelado")));
            imp.setSubTotalImpressora(ln.getDouble("subtotalimpressora"));
            imp.setTipoCancelamento(TipoCancelamento.getById(ln.getInt("tipocancelamento")));
            imp.setCpf(ln.getString("cpf"));
            imp.setValorDesconto(ln.getDouble("valordesconto"));
            imp.setValorAcrescimo(ln.getDouble("valoracrescimo"));
            imp.setCanceladoEmVenda("S".equals(ln.getString("canceladoemvenda")));
            imp.setNumeroSerie(ln.getString("numeroserie"));
            imp.setModeloImpressora(ln.getString("modeloimpressora"));
            imp.setNomeCliente(ln.getString("nomecliente"));
            imp.setEnderecoCliente(ln.getString("enderecocliente"));
            imp.setClienteEventual(ln.getString("clienteeventual"));
            imp.setChaveCfe(ln.getString("chavecfe"));
            imp.setChaveNfCe(ln.getString("chavenfce"));
            imp.setXml(ln.getString("xml"));
            imp.setTipoDesconto(TipoDesconto.getById(ln.getInt("tipodesconto")));
            imp.setChaveNfCeContingencia(ln.getString("chavenfcecontingencia"));

            /*if (!hasNext()) {
             try {
             csv.close();
             } catch (IOException ex) {
             LOG.log(Level.SEVERE, "Erro ao gerar a venda", ex);
             throw new RuntimeException(ex);
             }
             }*/
            return imp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private class VendaItemIterator implements Iterator<VendaItemIMP> {

        private ArquivoCSV2 csv;

        public VendaItemIterator(ArquivoCSV2 csv) {
            this.csv = csv;
        }

        @Override
        public boolean hasNext() {
            return this.csv.hasNext();
        }

        @Override
        public VendaItemIMP next() {

            LinhaArquivo ln = csv.next();

            VendaItemIMP imp = new VendaItemIMP();

            imp.setId(ln.getString("id"));
            imp.setSequencia(ln.getInt("sequencia"));
            VendaIMP vendaIMP = new VendaIMP();
            vendaIMP.setId(ln.getString("cod_venda"));
            imp.setVenda(vendaIMP);
            imp.setProduto(ln.getString("cod_produto"));
            imp.setDescricaoReduzida(ln.getString("descricaoreduzida"));
            imp.setQuantidade(ln.getDouble("quantidade"));
            imp.setPrecoVenda(ln.getDouble("precovenda"));
            imp.setTotalBruto(ln.getDouble("totalbruto"));
            imp.setCancelado("S".equals(Utils.acertarTexto(ln.getString("cancelado"))));
            if (!"".equals(ln.getString("tipocancelamento"))) {
                imp.setTipoCancelamento(TipoCancelamento.getById(ln.getInt("tipocancelamento")));
            }
            imp.setValorDesconto(ln.getDouble("valordesconto"));
            imp.setValorAcrescimo(ln.getDouble("valoracrescimo"));
            imp.setCodigoBarras(ln.getString("codigobarras"));
            imp.setUnidadeMedida(ln.getString("unidademedida"));
            if (!"".equals(ln.getString("tipodesconto"))) {
                imp.setTipoDesconto(TipoDesconto.getById(ln.getInt("tipodesconto")));
            }
            imp.setIcmsAliquotaId(ln.getString("id_aliquota"));
            imp.setIcmsCst(ln.getInt("icms_cst"));
            imp.setIcmsAliq(ln.getDouble("icms_aliq"));
            imp.setIcmsReduzido(ln.getDouble("icms_red"));
            imp.setContadorDoc(ln.getInt("contadordoc"));

            /*if (!hasNext()) {
             try {
             csv.close();
             } catch (IOException ex) {
             LOG.log(Level.SEVERE, "Erro ao gerar a venda", ex);
             throw new RuntimeException(ex);
             }
             }*/
            return imp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public List<AssociadoIMP> getAssociados(Set<OpcaoAssociado> opt) throws Exception {
        List<AssociadoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando Associados");

        for (LinhaArquivo linha : produtos) {
            AssociadoIMP imp = new AssociadoIMP();

            imp.setId(linha.getString("idproduto_principal"));
            imp.setDescricao(linha.getString("descricaoproduto_principal"));
            imp.setQtdEmbalagem(linha.getInt("qtdembalagem"));
            imp.setProdutoAssociadoId(linha.getString("idproduto_item"));
            imp.setDescricaoProdutoAssociado(linha.getString("descproduto_item"));
            imp.setQtdEmbalagemItem(linha.getInt("qtdembalagem_item"));
            imp.setPercentualPreco(linha.getDouble("percentualpreco"));
            imp.setAplicaPreco(linha.getBoolean("aplicapreco"));
            imp.setAplicaCusto(linha.getBoolean("aplicacusto"));
            imp.setAplicaEstoque(linha.getBoolean("aplicaestoque"));
            imp.setPercentualCusto(linha.getDouble("percentualcustoestoque"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<PromocaoIMP> getPromocoes() throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando promoções...");
        int contador = 0;
        for (LinhaArquivo linha : produtos) {
            PromocaoIMP imp = new PromocaoIMP();

            imp.setId_promocao(linha.getString("id_promocao"));
            imp.setDescricao(linha.getString("descricao_promocao"));
            imp.setDataInicio(getData(linha.getString("datainicio")));
            imp.setDataTermino(getData(linha.getString("datatermino")));
            imp.setEan(linha.getString("ean"));
            imp.setId_produto(linha.getString("id_produto"));
            imp.setDescricaoCompleta(linha.getString("descricaocompleta"));
            imp.setQuantidade(linha.getDouble("quantidade"));
            imp.setPaga(linha.getDouble("paga"));

            ProgressBar.setStatus("Carregando promoções... " + contador++);

            Result.add(imp);
        }
        return Result;
    }

    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando Pautas Fiscais");

        for (LinhaArquivo linha : produtos) {
            PautaFiscalIMP imp = new PautaFiscalIMP();

            imp.setId(linha.getString("id"));
            imp.setNcm(linha.getString("ncm"));
            imp.setIva(linha.getDouble("iva"));
            imp.setIvaAjustado(linha.getDouble("ivaajustado"));
            imp.setTipoIva(TipoIva.getByTipo(linha.getString("tipoiva")));
            imp.setIcmsRecolhidoAntecipadamente(linha.getBoolean("recolhidoantecipado"));
            imp.setExcecao(linha.getInt("excecao"));
            imp.setUf(linha.getString("uf"));
            if (linha.existsColumn("pauta_debito_id")) {
                imp.setAliquotaDebitoId(linha.getString("pauta_debito_id"));
            } else {
                imp.setAliquotaDebito(
                        linha.getInt("pauta_debito_cst"),
                        linha.getDouble("pauta_debito_aliquota"),
                        linha.getDouble("pauta_debito_reduzido")
                );
            }

            if (linha.existsColumn("pauta_credito_id")) {
                imp.setAliquotaCreditoId(linha.getString("pauta_credito_id"));
            } else {
                imp.setAliquotaCredito(
                        linha.getInt("pauta_credito_cst"),
                        linha.getDouble("pauta_credito_aliquota"),
                        linha.getDouble("pauta_credito_reduzido")
                );
            }

            if (linha.existsColumn("pauta_creditoforaestado_id")) {
                imp.setAliquotaCreditoForaEstadoId(linha.getString("pauta_creditoforaestado_id"));
            } else {
                imp.setAliquotaCreditoForaEstado(
                        linha.getInt("pauta_creditoforaestado_cst"),
                        linha.getDouble("pauta_creditoforaestado_aliquota"),
                        linha.getDouble("pauta_creditoforaestado_reduzido")
                );
            }

            if (linha.existsColumn("pauta_debitoforaestado_id")) {
                imp.setAliquotaDebitoForaEstadoId(linha.getString("pauta_debitoforaestado_id"));
            } else {
                imp.setAliquotaDebitoForaEstado(
                        linha.getInt("pauta_debitoforaestado_cst"),
                        linha.getDouble("pauta_debitoforaestado_aliquota"),
                        linha.getDouble("pauta_debitoforaestado_reduzido")
                );
            }

            result.add(imp);

        }

        return result;
    }

    private Date getData(String format) {
        if (format != null && !"".equals(format.trim())) {
            try {
                if (format.contains("/")) {

                    String[] dataAjustada = format.split("/");
                    if (dataAjustada.length == 3) {
                        String ano = dataAjustada[0];
                        if (ano.length() == 4) {
                            int anoConvert = Integer.parseInt(ano);

                            if (anoConvert > 1000) {
                                SimpleDateFormat ajustarAno = new SimpleDateFormat("yyyy/MM/dd");
                                return ajustarAno.parse(format);
                            }
                        }
                    }

                    SimpleDateFormat ajusteData = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat converteData = new SimpleDateFormat("yyyy/MM/dd");
                    return converteData.parse(converteData.format(ajusteData.parse(format)));

                } else if (format.contains("-")) {

                    String[] dataAjustada = format.split("-");
                    if (dataAjustada.length == 3) {
                        String ano = dataAjustada[0];
                        if (ano.length() == 4) {
                            int anoConvert = Integer.parseInt(ano);

                            if (anoConvert > 1000) {
                                SimpleDateFormat ajustarAno = new SimpleDateFormat("yyyy-MM-dd");
                                return ajustarAno.parse(format);
                            }
                        }
                    }

                    SimpleDateFormat ajusteData = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat converteData = new SimpleDateFormat("yyyy/MM/dd");
                    return converteData.parse(converteData.format(ajusteData.parse(format.replace("-", "/"))));
                }
                return null;

            } catch (ParseException | NumberFormatException ex) {
                System.out.println("Erro ao analisar a data: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return null;
    }

    private Date getDataCompleta(String format) throws ParseException {
        return format == null ? null : formatData.parse(format);
    }

    @Override
    public List<InventarioIMP> getInventario() throws Exception {
        List<InventarioIMP> result = new ArrayList<>();

        Arquivo inventario = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando Inventário...");

        for (LinhaArquivo linha : inventario) {
            InventarioIMP imp = new InventarioIMP();

            imp.setId(linha.getString("id"));
            imp.setIdProduto(linha.getString("id_produto"));
            imp.setData(getData(linha.getString("data")));
            imp.setDescricao(linha.getString("descricao"));
            imp.setPrecoVenda(linha.getDouble("preco"));
            imp.setQuantidade(linha.getDouble("quantidade"));
            imp.setCustoComImposto(linha.getDouble("custocomimposto"));
            imp.setCustoSemImposto(linha.getDouble("custosemimposto"));
            imp.setCstCredito(linha.getString("cst_credito"));
            imp.setAliquotaCredito(linha.getDouble("aliquota_credito"));
            imp.setReduzidoCredito(linha.getDouble("reducao_credito"));
            imp.setCstDebito(linha.getString("cst_debito"));
            imp.setAliquotaDebito(linha.getDouble("aliquota_debito"));
            imp.setReduzidoDebito(linha.getDouble("reducao_debito"));
            imp.setIdAliquotaCredito(linha.getString("id_icms_credito"));
            imp.setIdAliquotaDebito(linha.getString("id_icms_debito"));
            imp.setPis(linha.getDouble("pis"));
            imp.setCofins(linha.getDouble("cofins"));
            imp.setCustoMedioComImposto(linha.getDouble("customediocomimposto"));
            imp.setCustoMedioSemImposto(linha.getDouble("customediosemimposto"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        Arquivo inventario = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando ofertas...");
        for (LinhaArquivo linha : inventario) {
            OfertaIMP imp = new OfertaIMP();

            imp.setIdProduto(linha.getString("id"));
            imp.setDataInicio(getData(linha.getString("datainicio")));
            imp.setDataFim(getData(linha.getString("datatermino")));
            imp.setPrecoOferta(linha.getDouble("precooferta"));
            imp.setPrecoNormal(linha.getDouble("preconormal"));
            imp.setSituacaoOferta(SituacaoOferta.ATIVO);
            imp.setTipoOferta(TipoOfertaVO.CAPA);

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        Arquivo inventario = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());

        ProgressBar.setStatus("Carregando cheques...");
        for (LinhaArquivo linha : inventario) {
            ChequeIMP imp = new ChequeIMP();

            imp.setId(linha.getString("id"));
            imp.setCpf(linha.getString("cnpjcpf"));
            imp.setNumeroCheque(linha.getString("numerocheque"));
            imp.setBanco(Utils.stringToInt(linha.getString("banco")));
            imp.setAgencia(linha.getString("agencia"));
            imp.setConta(linha.getString("conta"));
            imp.setDate(getData(linha.getString("data")));
            imp.setDataDeposito(getData(linha.getString("datadeposito")));
            imp.setNumeroCupom(linha.getString("numerocupom"));
            imp.setEcf(linha.getString("ecf"));
            imp.setValor(linha.getDouble("valor"));
            imp.setRg(linha.getString("rg"));
            imp.setTelefone(linha.getString("telefone"));
            imp.setNome(linha.getString("nome"));
            imp.setObservacao(linha.getString("observacao"));
            imp.setSituacaoCheque(linha.getBoolean("baixado") ? SituacaoCheque.BAIXADO : SituacaoCheque.ABERTO);
            imp.setCmc7(linha.getString("cmc7"));
            imp.setAlinea(Utils.stringToInt(linha.getString("alinea")));
            imp.setValorJuros(linha.getDouble("valorjuros"));
            imp.setValorAcrescimo(linha.getDouble("valoracrescimo"));
            imp.setVistaPrazo(linha.getBoolean("aprazo") ? TipoVistaPrazo.PRAZO : TipoVistaPrazo.A_VISTA);

            result.add(imp);
        }

        return result;
    }

    private String arquivoConvenioEmpresas = "";
    private String arquivoConvenioConveniados = "";
    private String arquivoConvenioTransacoes = "";

    public void setArquivoConvenioEmpresas(String arquivoConvenioEmpresas) {
        this.arquivoConvenioEmpresas = arquivoConvenioEmpresas;
    }

    public void setArquivoConvenioConveniados(String arquivoConvenioConveniados) {
        this.arquivoConvenioConveniados = arquivoConvenioConveniados;
    }

    public void setArquivoConvenioTransacoes(String arquivoConvenioTransacoes) {
        this.arquivoConvenioTransacoes = arquivoConvenioTransacoes;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();

        Arquivo empresas = ArquivoFactory.getArquivo(this.arquivoConvenioEmpresas, getOpcoes());

        ProgressBar.setStatus("Carregando empresas do convênio...");
        for (LinhaArquivo linha : empresas) {
            ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

            imp.setId(linha.getString("id_empresaconvenio"));
            imp.setRazao(linha.getString("razao"));
            imp.setCnpj(linha.getString("cnpj"));
            imp.setInscricaoEstadual(linha.getString("inscricaoestadual"));
            imp.setEndereco(linha.getString("endereco"));
            imp.setNumero(linha.getString("numero"));
            imp.setComplemento(linha.getString("complemento"));
            imp.setBairro(linha.getString("bairro"));
            imp.setMunicipio(linha.getString("municipio"));
            imp.setUf(linha.getString("uf"));
            imp.setIbgeMunicipio(Utils.stringToInt(linha.getString("ibgemunicipio")));
            imp.setCep(linha.getString("cep"));
            imp.setTelefone(linha.getString("telefone"));
            imp.setDataInicio(getData(linha.getString("datainicio")));
            imp.setDataTermino(getData(linha.getString("datatermino")));
            imp.setSituacaoCadastro(linha.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
            imp.setDesconto(linha.getDouble("desconto"));
            imp.setRenovacaoAutomatica(linha.getBoolean("renovacaoautomatica"));
            imp.setDiaPagamento(Utils.stringToInt(linha.getString("diapagamento")));
            imp.setBloqueado(linha.getBoolean("bloqueado"));
            imp.setDataBloqueio(getData(linha.getString("databloqueio")));
            imp.setDiaInicioRenovacao(Utils.stringToInt(linha.getString("diainiciorenovacao")));
            imp.setDiaFimRenovacao(Utils.stringToInt(linha.getString("diafimrenovacao")));
            imp.setObservacoes(linha.getString("observacoes"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        Arquivo conveniados = ArquivoFactory.getArquivo(this.arquivoConvenioConveniados, getOpcoes());

        ProgressBar.setStatus("Carregando conveniados...");
        for (LinhaArquivo linha : conveniados) {
            ConveniadoIMP imp = new ConveniadoIMP();

            imp.setId(linha.getString("id"));
            imp.setNome(linha.getString("nome"));
            imp.setIdEmpresa(linha.getString("id_empresaconvenio"));
            imp.setBloqueado(linha.getBoolean("bloqueado"));
            imp.setSituacaoCadastro(linha.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
            imp.setSenha(Utils.stringToInt(linha.getString("senha")));
            imp.setCnpj(linha.getString("cnpj"));
            imp.setObservacao(linha.getString("observacao"));
            imp.setValidadeCartao(getData(linha.getString("validadecartao")));
            imp.setDataDesbloqueio(getData(linha.getString("datadesbloqueio")));
            imp.setVisualizaSaldo(linha.getBoolean("visualizasaldo"));
            imp.setDataBloqueio(getData(linha.getString("databloqueio")));
            imp.setConvenioLimite(linha.getDouble("conveniolimite"));
            imp.setConvenioDesconto(linha.getDouble("conveniodesconto"));
            imp.setLojaCadastro(Utils.stringToInt(linha.getString("lojacadastro")));
            imp.setIdentificacaoCartao(linha.getInt("identificacaocartao"));

            result.add(imp);
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        Arquivo conveniados = ArquivoFactory.getArquivo(this.arquivoConvenioTransacoes, getOpcoes());

        ProgressBar.setStatus("Carregando transação convenio...");
        for (LinhaArquivo linha : conveniados) {
            ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

            imp.setId(linha.getString("id"));
            imp.setIdConveniado(linha.getString("id_conveniado"));
            imp.setEcf(linha.getString("ecf"));
            imp.setNumeroCupom(linha.getString("numerocupom"));
            imp.setDataHora(new Timestamp(
                    (linha.getString("datahora") != null ? getData(linha.getString("datahora")) : new Date()).getTime()
            ));
            imp.setValor(linha.getDouble("valor"));
            SituacaoTransacaoConveniado byNome = SituacaoTransacaoConveniado.getByNome(linha.getString("situacaotransacao"));
            imp.setSituacaoTransacaoConveniado(byNome == null ? SituacaoTransacaoConveniado.OK : byNome);
            imp.setDataMovimento(getData(linha.getString("datamovimento")));
            imp.setFinalizado(linha.getBoolean("finalizado"));
            imp.setObservacao(linha.getString("observacao"));

            result.add(imp);
        }

        return result;
    }

}
