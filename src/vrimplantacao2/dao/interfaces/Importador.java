package vrimplantacao2.dao.interfaces;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.sqlite.SQLiteException;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.NutricionalFilizolaRepository;
import vrimplantacao.dao.cadastro.NutricionalToledoRepository;
import vrimplantacao.dao.financeiro.contareceber.OutraReceitaRepository;
import vrimplantacao.dao.financeiro.contareceber.OutraReceitaRepositoryProvider;
import vrimplantacao.vo.vrimplantacao.NutricionalFilizolaVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao2.dao.cadastro.MercadologicoDAO;
import vrimplantacao2.dao.cadastro.cheque.ChequeRepository;
import vrimplantacao2.dao.cadastro.cheque.ChequeRepositoryProvider;
import vrimplantacao2.dao.cadastro.cliente.ClienteRepository;
import vrimplantacao2.dao.cadastro.cliente.ClienteRepositoryProvider;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.comprador.CompradorRepository;
import vrimplantacao2.dao.cadastro.comprador.CompradorRepositoryProvider;
import vrimplantacao2.dao.cadastro.convenio.OpcaoConvenio;
import vrimplantacao2.dao.cadastro.convenio.conveniado.ConvenioConveniadoRepository;
import vrimplantacao2.dao.cadastro.convenio.conveniado.ConvenioConveniadoRepositoryProvider;
import vrimplantacao2.dao.cadastro.convenio.empresa.ConvenioEmpresaRepository;
import vrimplantacao2.dao.cadastro.convenio.empresa.ConvenioEmpresaRepositoryProvider;
import vrimplantacao2.dao.cadastro.convenio.receber.ConvenioReceberRepository;
import vrimplantacao2.dao.cadastro.convenio.receber.ConvenioReceberRepositoryProvider;
import vrimplantacao2.dao.cadastro.desmembramento.AutorizadoraRepository;
import vrimplantacao2.dao.cadastro.desmembramento.ContaContabilFinanceiroRepository;
import vrimplantacao2.dao.cadastro.desmembramento.ContaContabilFiscalRepository;
import vrimplantacao2.dao.cadastro.desmembramento.DesmembramentoRepository;
import vrimplantacao2.dao.cadastro.desmembramento.DesmembramentoRepositoryProvider;
import vrimplantacao2.dao.cadastro.desmembramento.HistoricoPadraoRepository;
import vrimplantacao2.dao.cadastro.desmembramento.TipoRecebivelRepository;
import vrimplantacao2.dao.cadastro.desmembramento.TipoTefRepository;
import vrimplantacao2.dao.cadastro.financeiro.FinanceiroRepository;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.AutorizadoraRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContaContabilFinanceirolRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.ContaContabilFiscalRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.HistoricoPadraoRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.TipoRecebivelRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.TipoTefRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoProvider;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoRepository;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.OpcaoCreditoRotativo;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.OpcaoRecebimentoCaixa;
import vrimplantacao2.dao.cadastro.fiscal.FiscalRepository;
import vrimplantacao2.dao.cadastro.fiscal.FiscalRepositoryProvider;
import vrimplantacao2.dao.cadastro.fiscal.inventario.InventarioRepository;
import vrimplantacao2.dao.cadastro.fiscal.inventario.InventarioRepositoryProvider;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorRepository;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorRepositoryProvider;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoProdutoFornecedor;
import vrimplantacao2.dao.cadastro.fornecedor.ProdutoFornecedorDAO;
import vrimplantacao2.dao.cadastro.mercadologico.MercadologicoRepository;
import vrimplantacao2.dao.cadastro.notafiscal.NotaFiscalRepository;
import vrimplantacao2.dao.cadastro.notafiscal.NotaFiscalRepositoryProvider;
import vrimplantacao2.dao.cadastro.notafiscal.OpcaoNotaFiscal;
import vrimplantacao2.dao.cadastro.nutricional.NutricionalRepository;
import vrimplantacao2.dao.cadastro.nutricional.NutricionalRepositoryProvider;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.pdv.acumulador.AcumuladorRepository;
import vrimplantacao2.dao.cadastro.pdv.acumulador.AcumuladorRepositoryProvider;
import vrimplantacao2.dao.cadastro.pdv.ecf.EcfPdvVO;
import vrimplantacao2.dao.cadastro.pdv.ecf.EcfRepository;
import vrimplantacao2.dao.cadastro.pdv.operador.OperadorRepository;
import vrimplantacao2.dao.cadastro.pdv.operador.OperadorRepositoryProvider;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoDAO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepository;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.dao.cadastro.produto2.associado.AssociadoRepository;
import vrimplantacao2.dao.cadastro.produto2.associado.AssociadoRepositoryProvider;
import vrimplantacao2.dao.cadastro.produto2.associado.OpcaoAssociado;
import vrimplantacao2.dao.cadastro.receita.ReceitaBalancaRepository;
import vrimplantacao2.dao.cadastro.receita.ReceitaBalancaRepositoryProvider;
import vrimplantacao2.dao.cadastro.receita.ReceitaRepository;
import vrimplantacao2.dao.cadastro.receita.ReceitaRepositoryProvider;
import vrimplantacao2.dao.cadastro.venda.OpcaoVenda;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoDAO;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoRepository;
import vrimplantacao2.dao.cadastro.venda.VendaImpDao;
import vrimplantacao2.dao.cadastro.venda.VendaItemImpDao;
import vrimplantacao2.dao.cadastro.venda.VendaRepository;
import vrimplantacao2.dao.cadastro.venda.VendaRepositoryProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.divisao.DivisaoRepository;
import vrimplantacao2.vo.cadastro.divisao.DivisaoRepositoryProvider;
import vrimplantacao2.vo.cadastro.financeiro.contareceber.OpcaoContaReceber;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.OpcaoFiscal;
import vrimplantacao2.vo.importacao.AcumuladorIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutIMP;
import vrimplantacao2.vo.importacao.AcumuladorLayoutRetornoIMP;
import vrimplantacao2.vo.importacao.AssociadoIMP;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CompradorIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaReceberIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoPagamentoAgrupadoIMP;
import vrimplantacao2.vo.importacao.DivisaoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.InventarioIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.OperadorIMP;
import vrimplantacao2.vo.importacao.PautaFiscalIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.RecebimentoCaixaIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2_5.controller.interfaces.InterfaceController;

import vrimplantacao2.dao.cadastro.promocao.PromocaoRepository;
import vrimplantacao2.dao.cadastro.promocao.PromocaoRepositoryProvider;
import vrimplantacao2.dao.cadastro.venda.PublicVendaRepository;
import vrimplantacao2.vo.importacao.AutorizadoraIMP;
import vrimplantacao2.vo.importacao.ContaContabilFinanceiroIMP;
import vrimplantacao2.vo.importacao.ContaContabilFiscalIMP;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2.vo.importacao.HistoricoPadraoIMP;
import vrimplantacao2.vo.importacao.PromocaoIMP;
import vrimplantacao2.vo.importacao.TipoRecebivelIMP;
import vrimplantacao2.vo.importacao.TipoTefIMP;
import vrimplantacao2_5.Financeiro.IMP.AtivoImobilizadoIMP;
import vrimplantacao2_5.Financeiro.IMP.CaixaDiferencaIMP;
import vrimplantacao2_5.Financeiro.IMP.CaixaVendaIMP;
import vrimplantacao2_5.Financeiro.IMP.CfopEntradaIMP;
import vrimplantacao2_5.Financeiro.IMP.CfopIMP;
import vrimplantacao2_5.Financeiro.IMP.CfopSaidaIMP;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeAbatimentoIMP;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeTipoEntradaIMP;
import vrimplantacao2_5.Financeiro.IMP.ContabilidadeTipoSaidaIMP;
import vrimplantacao2_5.Financeiro.IMP.EcfIMP;
import vrimplantacao2_5.Financeiro.IMP.EcfLayoutIMP;
import vrimplantacao2_5.Financeiro.IMP.EntradaSaidaTipoEntradaIMP;
import vrimplantacao2_5.Financeiro.IMP.EntradaSaidaTipoSaidaIMP;
import vrimplantacao2_5.Financeiro.IMP.FinalizadoraConfiguracaoIMP;
import vrimplantacao2_5.Financeiro.IMP.FinalizadoraIMP;
import vrimplantacao2_5.Financeiro.IMP.FinalizadoraLayoutRetornoIMP;
import vrimplantacao2_5.Financeiro.IMP.RecebivelConfiguracaoIMP;
import vrimplantacao2_5.Financeiro.IMP.RecebivelConfiguracaoTabelaIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoEntradaIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoPlanoContaIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoRecebivelFinalizadoraIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoSaidaContabilidadeIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoSaidaIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoSaidaNotaFiscalSequenciaIMP;
import vrimplantacao2_5.Financeiro.Provider.CfopEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.CfopSaidaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.EntradaSaidaTipoEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.EntradaSaidaTipoSaidaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.RecebivelConfiguracaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.RecebivelConfiguracaoTabelaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoPlanoContaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoRecebivelFinalizadoraRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoSaidaContabilidadeRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoSaidaNotaSaidaSequenciaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoSaidaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Repository.CfopEntradaRepository;
import vrimplantacao2_5.Financeiro.Repository.CfopSaidaRepository;
import vrimplantacao2_5.Financeiro.Repository.EntradaSaidaTipoEntradaRepository;
import vrimplantacao2_5.Financeiro.Repository.EntradaSaidaTipoSaidaRepository;
import vrimplantacao2_5.Financeiro.Repository.RecebivelConfiguracaoRepository;
import vrimplantacao2_5.Financeiro.Repository.RecebivelConfiguracaoTabelaRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoEntradaRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoPlanoContaRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoRecebivelFinalizadoraRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoSaidaContabilidadeRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoSaidaNotaSaidaSequenciaRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoSaidaRepository;
import vrimplantacao2_5.relatorios.gerador.GeradorArquivosRepository;
import vrimplantacao2_5.Financeiro.IMP.GrupoAtivoIMP;
import vrimplantacao2_5.Financeiro.IMP.MapaResumoIMP;
import vrimplantacao2_5.Financeiro.IMP.PdvFuncaoIMP;
import vrimplantacao2_5.Financeiro.IMP.PdvFuncaoOperadorIMP;
import vrimplantacao2_5.Financeiro.IMP.PdvTecladoFuncaoIMP;
import vrimplantacao2_5.Financeiro.IMP.TecladoLayoutIMP;
import vrimplantacao2_5.Financeiro.IMP.TipoModeloIMP;
import vrimplantacao2_5.Financeiro.Provider.AtivoImobilizadoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.CaixaDiferencaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.CaixaVendaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.CfopRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.ContabilidadeAbatimentoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.ContabilidadeTipoEntradaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.ContabilidadeTipoSaidaRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.EcfLayoutRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.EcfRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.FinalizadoraConfiguracaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.FinalizadoraLayoutRetornoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.FinalizadoraRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.GrupoAtivoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.MapaResumoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.PdvFuncaoOperadorRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.PdvFuncaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.PdvTecladoFuncaoRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TecladoLayoutRepositoryProvider;
import vrimplantacao2_5.Financeiro.Provider.TipoModeloRepositoryProvider;
import vrimplantacao2_5.Financeiro.Repository.AtivoImobilizadoRepository;
import vrimplantacao2_5.Financeiro.Repository.CaixaDiferencaRepository;
import vrimplantacao2_5.Financeiro.Repository.CaixaVendaRepository;
import vrimplantacao2_5.Financeiro.Repository.CfopRepository;
import vrimplantacao2_5.Financeiro.Repository.ContabilidadeAbatimentoRepository;
import vrimplantacao2_5.Financeiro.Repository.ContabilidadeTipoEntradaRepository;
import vrimplantacao2_5.Financeiro.Repository.ContabilidadeTipoSaidaRepository;
import vrimplantacao2_5.Financeiro.Repository.EcPdvRepository;
import vrimplantacao2_5.Financeiro.Repository.EcfLayoutRepository;
import vrimplantacao2_5.Financeiro.Repository.FinalizadoraConfiguracaoRepository;
import vrimplantacao2_5.Financeiro.Repository.FinalizadoraLayoutRetornoRepository;
import vrimplantacao2_5.Financeiro.Repository.FinalizadoraRepository;
import vrimplantacao2_5.Financeiro.Repository.GrupoAtivoRepository;
import vrimplantacao2_5.Financeiro.Repository.MapaResumoRepository;
import vrimplantacao2_5.Financeiro.Repository.PdvFuncaoOperadorRepository;
import vrimplantacao2_5.Financeiro.Repository.PdvFuncaoRepository;
import vrimplantacao2_5.Financeiro.Repository.PdvTecladoFuncaoRepository;
import vrimplantacao2_5.Financeiro.Repository.TecladoLayoutRepository;
import vrimplantacao2_5.Financeiro.Repository.TipoModeloRepository;

public class Importador {

    private static final Logger LOG = Logger.getLogger(Importador.class.getName());

    private InterfaceDAO interfaceDAO;
    private InterfaceController interfaceController;
    private int lojaVR = 1;
    private int idConexao = 0;
    private boolean importarIndividualLoja = false;
    public boolean idProdutoSemUltimoDigito = false;
    public boolean eBancoUnificado = false;
    public boolean importarPorPlanilha = false;
    public boolean checarVendasDataAtual = false;

    public Importador(InterfaceDAO interfaceDAO) {
        this.interfaceDAO = interfaceDAO;
    }

    public Importador(InterfaceController interfaceController) {
        this.interfaceController = interfaceController;
    }

    public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
        this.interfaceDAO = interfaceDAO;
    }

    public InterfaceDAO getInterfaceDAO() {
        return interfaceDAO;
    }

    public void setInterfaceController(InterfaceController interfaceController) {
        this.interfaceController = interfaceController;
    }

    public InterfaceController getInterfaceController() {
        return this.interfaceController;
    }

    public int getIdConexao() {
        return idConexao;
    }

    public void setIdConexao(int idConexao) {
        this.idConexao = idConexao;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
    }

    /**
     * @return the importarIndividualLoja
     */
    public boolean isImportarIndividualLoja() {
        return importarIndividualLoja;
    }

    /**
     * @param importarIndividualLoja the importarIndividualLoja to set
     */
    public void setImportarIndividualLoja(boolean importarIndividualLoja) {
        this.importarIndividualLoja = importarIndividualLoja;
    }

    /**
     *
     * @return
     */
    public String getSistema() {
        if (getIdConexao() != 0) {
            return interfaceDAO.getSistema() + " - " + getIdConexao();
        } else {
            return interfaceDAO.getSistema();
        }
    }

    /**
     * Retorna a loja de origem.
     *
     * @return Código da loja de origem.
     */
    public String getLojaOrigem() {
        return interfaceDAO.getLojaOrigem();
    }

    /**
     * Define qual será a loja de origem.
     *
     * @param LojaOrigem
     */
    public void setLojaOrigem(String LojaOrigem) {
        interfaceDAO.setLojaOrigem(LojaOrigem);
    }

    /**
     * Importa os mercadológicos dos produtos.
     *
     * @param opcoes
     * @throws Exception
     */
    public void importarMercadologico(OpcaoProduto... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando dados do mercadológico...");

        List<MercadologicoIMP> mercadologicos = getInterfaceDAO().getMercadologicos();
        Set<OpcaoProduto> opt = new HashSet<>(Arrays.asList(opcoes));

        if (Parametros.OpcoesExperimentaisDeProduto.isImportacaoMercadologicoExperimentalAtiva()) {
            MercadologicoRepository repository = new MercadologicoRepository(
                    getSistema(),
                    getLojaOrigem(),
                    getLojaVR()
            );
            repository.salvarNormal(mercadologicos, new HashSet<>(Arrays.asList(opcoes)));
        } else {
            MercadologicoDAO dao = new MercadologicoDAO();

            dao.setSistema(getSistema());
            dao.setIdLojaVR(getLojaVR());
            dao.salvar(mercadologicos, opt);
        }
    }

    /**
     * Importa os mercadológicos dos produtos por níveis.
     *
     * @param opcoes entre os seus subníveis.
     * @throws Exception
     */
    public void importarMercadologicoPorNiveis(OpcaoProduto... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando dados do mercadológico em níveis...");
        List<MercadologicoNivelIMP> mercadologicos = getInterfaceDAO().getMercadologicoPorNivel();
        MercadologicoRepository repository = new MercadologicoRepository(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        repository.salvar(mercadologicos, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Executa a importação das famílias dos produtos.
     *
     * @throws Exception
     */
    public void importarFamiliaProduto() throws Exception {
        ProgressBar.setStatus("Carregando dados da família do produto...");
        List<FamiliaProdutoIMP> familias = getInterfaceDAO().getFamiliaProduto();
        FamiliaProdutoDAO dao = new FamiliaProdutoDAO();
        dao.setSistema(getSistema());
        dao.salvar(familias);
    }

    /**
     * Executa a importação dos produtos no sistema.
     *
     * @param manterCodigoDeBalanca True para utilizar o código de barras dos
     * produtos de balança (PLU) como id dos produtos. OBSERVAÇÃO: para que esta
     * opções funcione corretamente, é necessário que o código de barras seja
     * informado no {@link ProdutoIMP}.
     * @param gerarNiveisComoSubniveis Gerar um subnivel para cada nível de
     * mercadológico.
     * @throws Exception
     */
    @Deprecated
    public void importarProduto(boolean manterCodigoDeBalanca, boolean gerarNiveisComoSubniveis) throws Exception {
        List<OpcaoProduto> opcoes = new ArrayList<>();
        if (manterCodigoDeBalanca) {
            opcoes.add(OpcaoProduto.IMPORTAR_MANTER_BALANCA);
        }
        if (gerarNiveisComoSubniveis) {
            opcoes.add(OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC);
        }

        importarProduto(opcoes.toArray(new OpcaoProduto[]{}));
    }

    @Deprecated
    public void importarProdutoBalanca(boolean manterCodigoDeBalanca, boolean gerarNiveisComoSubniveis) throws Exception {
        List<OpcaoProduto> opcoes = new ArrayList<>();
        if (manterCodigoDeBalanca) {
            opcoes.add(OpcaoProduto.IMPORTAR_MANTER_BALANCA);
        }
        if (gerarNiveisComoSubniveis) {
            opcoes.add(OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC);
        }

        importarProduto(opcoes.toArray(new OpcaoProduto[]{}));
    }

    @Deprecated
    public void importarProduto(boolean manterCodigoDeBalanca) throws Exception {
        this.importarProduto(manterCodigoDeBalanca, false);
    }

    @Deprecated
    public void importarProdutoBalanca(boolean manterCodigoDeBalanca) throws Exception {
        this.importarProdutoBalanca(manterCodigoDeBalanca, false);
    }

    public void importarProdutoPdvVr(OpcaoProduto... opcoes) throws Exception {

        ProgressBar.setStatus("Carregando produtos Pdv Vr...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutos();
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setLoja(getLojaOrigem());
        provider.setSistema(getSistema());
        provider.setLojaVR(getLojaVR());
        provider.setOpcoes(opcoes);

        ProdutoRepository repository = new ProdutoRepository(provider);
        repository.salvar(produtos);

    }

    public void importarProduto(OpcaoProduto... opcoes) throws Exception {

        ProgressBar.setStatus("Carregando produtos...");

        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutos();

        System.out.println("Qtd. produtos: " + produtos.size());

        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();

        provider.setIdConexao(getIdConexao());
        provider.setLoja(getLojaOrigem());
        provider.setSistema(getSistema());
        provider.setLojaVR(getLojaVR());
        provider.setOpcoes(opcoes);
        provider.setImportarPorPlanilha(this.importarPorPlanilha);

        ProdutoRepository repository = new ProdutoRepository(provider);

        repository.salvar2_5(produtos);

        Object[] options = {"Gerar", "Cancelar"};
        int decisao = JOptionPane.showOptionDialog(null, "Deseja gerar SPED e demais relatórios?",
                "Gerar Relatórios", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (decisao == 0) {
            try {
                new GeradorArquivosRepository().geraPlanilha();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void importarAtacado() throws Exception {

    }

    public void importarProdutosBalanca(OpcaoProduto... opcoes) throws Exception {

        ProgressBar.setStatus("Carregando produtos de Balança...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutosBalanca();
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setLoja(getLojaOrigem());
        provider.setSistema(getSistema());
        provider.setLojaVR(getLojaVR());
        provider.setOpcoes(opcoes);

        ProdutoRepository repository = new ProdutoRepository(provider);
        repository.salvar(produtos);

    }

    public void importarProdutoComplemento(OpcaoProduto... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutosComplemento();
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setLoja(getLojaOrigem());
        provider.setSistema(getSistema());
        provider.setLojaVR(getLojaVR());
        provider.setOpcoes(opcoes);
        ProdutoRepository repository = new ProdutoRepository(provider);
        repository.salvar(produtos);
    }

    public void importarProdutoBalanca(OpcaoProduto... opcoes) throws Exception {

        ProgressBar.setStatus("Carregando produtos...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutosBalanca();
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setLoja(getLojaOrigem());
        provider.setSistema(getSistema());
        provider.setLojaVR(getLojaVR());
        provider.setOpcoes(opcoes);

        ProdutoRepository repository = new ProdutoRepository(provider);
        repository.salvar(produtos);

    }

    public void importarFornecedor(OpcaoFornecedor... opt) throws Exception {
        this.importarFornecedor(new HashSet<>(Arrays.asList(opt)));
    }

    /**
     * Executa a importação dos fornecedores.
     *
     * @param opt
     * @throws Exception
     */
    public void importarFornecedor(Set<OpcaoFornecedor> opt) throws Exception {
        ProgressBar.setStatus("Carregando fornecedores...");
        List<FornecedorIMP> fornecedores = getInterfaceDAO().getFornecedores();
        FornecedorRepositoryProvider provider = new FornecedorRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        provider.setOpcoes(opt);
        provider.setIdConexao(getIdConexao());
        FornecedorRepository rep = new FornecedorRepository(provider);
        rep.salvar2_5(fornecedores);
    }

    /**
     * Executa a importação do produto fornecedor.
     *
     * @param opcoes
     * @throws Exception
     */
    public void importarProdutoFornecedor(OpcaoProdutoFornecedor... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos dos fornecedores...");
        List<ProdutoFornecedorIMP> produtos = getInterfaceDAO().getProdutosFornecedores();

        ProdutoFornecedorDAO dao = new ProdutoFornecedorDAO();

        dao.setImportSistema(getSistema());
        dao.setImportLoja(getLojaOrigem());
        dao.setIdLojaVR(getLojaVR());
        dao.salvar(produtos, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Importa os códigos de barras dos produtos.
     *
     * @param opcoes Opções de importação de produto
     * @throws Exception
     */
    public void importarEAN(OpcaoProduto... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getEANs();
        ProdutoDAO dao = new ProdutoDAO();
        dao.setImportSistema(getSistema());
        dao.setImportLoja(getLojaOrigem());
        dao.setIdLojaVR(getLojaVR());
        dao.salvarEAN(produtos, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Importa os códigos de barras atacado dos produtos.
     *
     * @param opcoes
     * @throws Exception
     */
    public void importarEANAtacado(OpcaoProduto... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getEANsAtacado();
        ProdutoDAO dao = new ProdutoDAO();
        dao.setImportSistema(getSistema());
        dao.setImportLoja(getLojaOrigem());
        dao.setIdLojaVR(getLojaVR());
        dao.salvarEAN(produtos, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Todo produto que não possuir um EAN, ao executar este método, eles
     * recebem um código de barras, baseado em seu ID.
     *
     * @throws Exception
     */
    public void importarEANemBranco() throws Exception {
        ProgressBar.setStatus("Preenchendo EANs em branco...");
        ProdutoDAO dao = new ProdutoDAO();
        dao.setImportSistema(getSistema());
        dao.setImportLoja(getLojaOrigem());
        dao.setIdLojaVR(getLojaVR());
        dao.salvarEANemBranco();
    }

//    public void importarTipoRecebivel() throws Exception {
//        ProgressBar.setStatus("Carregando Dados...");
//
//        // Instanciação das listas
//        //pdv.tipomodelo
//        List<TipoModeloIMP> modelo = getInterfaceDAO().getModeloEcf();
//        TipoModeloRepositoryProvider modeloprovider = new TipoModeloRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.ecf
//        List<EcfIMP> ecf = getInterfaceDAO().getPdvEcf();
//        EcfRepositoryProvider ecfProvider = new EcfRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.ecflayout
//        List<EcfLayoutIMP> ecfLayout = getInterfaceDAO().getEcfLayout();
//        EcfLayoutRepositoryProvider ecfLayoutProvider = new EcfLayoutRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.tecladolayout
//        List<TecladoLayoutIMP> tecladoLayout = getInterfaceDAO().getTecladoLayout();
//        TecladoLayoutRepositoryProvider tecladoLayoutProvider = new TecladoLayoutRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.tecladolayoutfuncao
//        List<PdvTecladoFuncaoIMP> tecladoImp = getInterfaceDAO().getPdvFuncaoTeclado();
//        PdvTecladoFuncaoRepositoryProvider tecladoprov = new PdvTecladoFuncaoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.funcaoniveloperador
//        List<PdvFuncaoOperadorIMP> funcaoOperador = getInterfaceDAO().getPdvFuncaoOperador();
//        PdvFuncaoOperadorRepositoryProvider opePro = new PdvFuncaoOperadorRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.funcao
//        List<PdvFuncaoIMP> funcaoImp = getInterfaceDAO().getPdvFuncao();
//        PdvFuncaoRepositoryProvider funcaoPro = new PdvFuncaoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.finalizadoraconfiguracao
//        List<FinalizadoraConfiguracaoIMP> finalizadoraConf = getInterfaceDAO().getFinalizadoraConfiguracao();
//        FinalizadoraConfiguracaoRepositoryProvider configuracao = new FinalizadoraConfiguracaoRepositoryProvider(
//                getSistema(),
//                getLojaOrigem(),
//                lojaVR);
//
//        //pdv.finalizadoralayoutretorno
//        List<FinalizadoraLayoutRetornoIMP> retImp = getInterfaceDAO().getFinalizadoraLayout();
//        FinalizadoraLayoutRetornoRepositoryProvider layRep = new FinalizadoraLayoutRetornoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.finalizadora
//        List<FinalizadoraIMP> finImp = getInterfaceDAO().getPdvFinalizadora();
//        FinalizadoraRepositoryProvider finProv = new FinalizadoraRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.cfop
//        List<CfopIMP> cfopmImp = getInterfaceDAO().getCfop();
//        CfopRepositoryProvider cfopProv = new CfopRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        List<CaixaVendaIMP> caixaVendaImp = getInterfaceDAO().getCaixa();
//        CaixaVendaRepositoryProvider caixaPro = new CaixaVendaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //contabilidade.tipoentrada
//        List<MapaResumoIMP> mapaImp = getInterfaceDAO().getMapa();
//        MapaResumoRepositoryProvider mapaPro = new MapaResumoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //contabilidade.tipoentrada
//        List<AtivoImobilizadoIMP> ativoImp = getInterfaceDAO().getAtivo();
//        AtivoImobilizadoRepositoryProvider AtivoPro = new AtivoImobilizadoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //contabilidade.tipoentrada
//        List<ContabilidadeAbatimentoIMP> abatimentoImp = getInterfaceDAO().getAbatimento();
//        ContabilidadeAbatimentoRepositoryProvider abatimentoProvider = new ContabilidadeAbatimentoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //contabilidade.tipoentrada
//        List<ContabilidadeTipoSaidaIMP> tipoSaidaContabil = getInterfaceDAO().getTipoSaidaContabil();
//        ContabilidadeTipoSaidaRepositoryProvider saidaProvider = new ContabilidadeTipoSaidaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //contabilidade.tipoentrada
//        List<ContabilidadeTipoEntradaIMP> tipoEntradaContabil = getInterfaceDAO().getTipoEntradaContabil();
//        ContabilidadeTipoEntradaRepositoryProvider EntradaProvider = new ContabilidadeTipoEntradaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //ativo.grupoativo
//        List<GrupoAtivoIMP> grup = getInterfaceDAO().getGrupoAtivo();
//        GrupoAtivoRepositoryProvider grppPro = new GrupoAtivoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //contabilidade.caixadiferenca
//        List<CaixaDiferencaIMP> caixaImp = getInterfaceDAO().getCaixaDiferenca();
//        CaixaDiferencaRepositoryProvider caixaProv = new CaixaDiferencaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.tipoplanoconta
//        List<TipoPlanoContaIMP> tipoImp = getInterfaceDAO().getTipoPlanoConta();
//        TipoPlanoContaRepositoryProvider planoProv = new TipoPlanoContaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.recebivelconfiguracaotabela
//        List<RecebivelConfiguracaoTabelaIMP> recConf = getInterfaceDAO().getConfiguracaoRecebivelTabela();
//        RecebivelConfiguracaoTabelaRepositoryProvider confRecTab = new RecebivelConfiguracaoTabelaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.recebivelconfiguracao
//        List<RecebivelConfiguracaoIMP> confRe = getInterfaceDAO().getConfiguracaoRecebivel();
//        RecebivelConfiguracaoRepositoryProvider confPro = new RecebivelConfiguracaoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.tiporecebivelfinalizadora
//        List<TipoRecebivelFinalizadoraIMP> tpRec = getInterfaceDAO().getTipoRecebivelFinalizadora();
//        TipoRecebivelFinalizadoraRepositoryProvider tpPro = new TipoRecebivelFinalizadoraRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.entradasaidatipoentrada
//        List<EntradaSaidaTipoEntradaIMP> entradaSaidaTipoEntrada = getInterfaceDAO().getEntradaSaidaTipoEntrada();
//        EntradaSaidaTipoEntradaRepositoryProvider entradaSaidaEntrada = new EntradaSaidaTipoEntradaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.entradasaidatiposaida
//        List<EntradaSaidaTipoSaidaIMP> entradaSaidaTipoSaida = getInterfaceDAO().getEntradaSaida();
//        EntradaSaidaTipoSaidaRepositoryProvider entradaSaida = new EntradaSaidaTipoSaidaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //public.tiposaidacontabilidade
//        List<TipoSaidaContabilidadeIMP> tipoSaidaConta = getInterfaceDAO().getSaidaContabil();
//        TipoSaidaContabilidadeRepositoryProvider saidaContabil = new TipoSaidaContabilidadeRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        // public.tiposaidanotasaidasequencia
//        List<TipoSaidaNotaFiscalSequenciaIMP> seqSaida = getInterfaceDAO().getSequenceSaida();
//        TipoSaidaNotaSaidaSequenciaRepositoryProvider seqrep = new TipoSaidaNotaSaidaSequenciaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        // CFOP SAIDA  public.cfoptiposaida
//        List<CfopSaidaIMP> cfopSaida = getInterfaceDAO().getCfopSaida();
//        CfopSaidaRepositoryProvider cforef = new CfopSaidaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //  TIPO SAIDA public.tiposaida
//        List<TipoSaidaIMP> saida = getInterfaceDAO().getTipoSaida();
//        TipoSaidaRepositoryProvider saidarep = new TipoSaidaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        // CFOP ENTRADA public.tipoentrada
//        List<CfopEntradaIMP> cfopE = getInterfaceDAO().getCfopEntrada();
//        CfopEntradaRepositoryProvider cfopRe = new CfopEntradaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //Tipo Entrada  public.tipoentrada
//        List<TipoEntradaIMP> entrada = getInterfaceDAO().getTipoEntrada();
//        TipoEntradaRepositoryProvider entradaP = new TipoEntradaRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //Historico Padrão public.historicopadrao
//        List<HistoricoPadraoIMP> historico = getInterfaceDAO().getHistorico();
//        HistoricoPadraoRepositoryProvider rephis = new HistoricoPadraoRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //Conta Contabil Financeiro
//        List<ContaContabilFinanceiroIMP> financeiro = getInterfaceDAO().getContaContabilFinanceiro();
//        ContaContabilFinanceirolRepositoryProvider financeiroprovi = new ContaContabilFinanceirolRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //Conta Contabil Fiscal
//        List<ContaContabilFiscalIMP> conta = getInterfaceDAO().getContaContabilFiscal();
//        ContaContabilFiscalRepositoryProvider provi = new ContaContabilFiscalRepositoryProvider(getSistema(), getLojaOrigem(), lojaVR);
//
//        //pdv.tipotef
//        List<TipoTefIMP> tef = getInterfaceDAO().getTipoTef();
//        TipoTefRepositoryProvider prov = new TipoTefRepositoryProvider(getSistema(), getLojaOrigem(), getLojaVR());
//
//        //public.tiporecebivel
//      /*  List<TipoRecebivelIMP> recebivel = getInterfaceDAO().getRecebivel();
//        TipoRecebivelRepositoryProvider provider = new TipoRecebivelRepositoryProvider(getSistema(), getLojaOrigem(), getLojaVR());*/
//
//        //pdv.autorizadora
//        List<AutorizadoraIMP> autorizadora = getInterfaceDAO().getAutorizadora();
//        AutorizadoraRepositoryProvider aut = new AutorizadoraRepositoryProvider(getSistema(), getLojaOrigem(), getLojaVR());
//
//        HistoricoPadraoRepository hispad = new HistoricoPadraoRepository(rephis);
//        hispad.importarHistoricoPadrao(historico);
//
//        ContaContabilFinanceiroRepository fina = new ContaContabilFinanceiroRepository(financeiroprovi);
//        fina.importarContaContabilFiscal(financeiro);
//
//        //METODO DE IMPORTAR FORNECEDORES
//        importarFornecedor();
//
//        ContaContabilFiscalRepository cont = new ContaContabilFiscalRepository(provi);
//        cont.importarContaContabilFiscal(conta);
//
//        CaixaDiferencaRepository caixaRep = new CaixaDiferencaRepository(caixaProv);
//        caixaRep.importarCaixaDiferenca(caixaImp);
//
//        ContabilidadeAbatimentoRepository abat = new ContabilidadeAbatimentoRepository(abatimentoProvider);
//        abat.importarAbatimento(abatimentoImp);
//
//        AtivoImobilizadoRepository ativoRep = new AtivoImobilizadoRepository(AtivoPro);
//        ativoRep.importarAtivo(ativoImp);
//
//        CaixaVendaRepository caixaRepository = new CaixaVendaRepository(caixaPro);
//        caixaRepository.importarCaixaVenda(caixaVendaImp);
//
//        MapaResumoRepository mapaRep = new MapaResumoRepository(mapaPro);
//        mapaRep.importarCaixaDiferenca(mapaImp);
//
//        ContabilidadeTipoSaidaRepository contSaida = new ContabilidadeTipoSaidaRepository(saidaProvider);
//        contSaida.importarTipoEntradaContabil(tipoSaidaContabil);
//
//        ContabilidadeTipoEntradaRepository contEntrada = new ContabilidadeTipoEntradaRepository(EntradaProvider);
//        contEntrada.importarTipoEntradaContabil(tipoEntradaContabil);
//
//        GrupoAtivoRepository grppRepo = new GrupoAtivoRepository(grppPro);
//        grppRepo.importarGrupoAtivo(grup);
//
//        TipoPlanoContaRepository planoRepo = new TipoPlanoContaRepository(planoProv);
//        planoRepo.importarTipoPlano(tipoImp);
//
//        TipoEntradaRepository entrarep = new TipoEntradaRepository(entradaP);
//        entrarep.importarTipoEntrada(entrada);
//
//        CfopRepository cfopRep = new CfopRepository(cfopProv);
//        cfopRep.importarCfop(cfopmImp);
//        cfopmImp.clear();
//
//        CfopEntradaRepository cfop = new CfopEntradaRepository(cfopRe);
//        cfop.importarCfopEntrada(cfopE);
//
//        TipoSaidaRepository saidar = new TipoSaidaRepository(saidarep);
//        saidar.importarTipoSaida(saida);
//
//        CfopSaidaRepository cfopSaidaRep = new CfopSaidaRepository(cforef);
//        cfopSaidaRep.importarCfopSaida(cfopSaida);
//
//        TipoSaidaNotaSaidaSequenciaRepository seqRep = new TipoSaidaNotaSaidaSequenciaRepository(seqrep);
//        seqRep.importarSequenceTipoSaida(seqSaida);
//
//        TipoSaidaContabilidadeRepository saiRep = new TipoSaidaContabilidadeRepository(saidaContabil);
//        saiRep.importarTipoSaidaContabil(tipoSaidaConta);
//
//        EntradaSaidaTipoSaidaRepository entradarep = new EntradaSaidaTipoSaidaRepository(entradaSaida);
//        entradarep.importarEntradaSaidaTipoSaida(entradaSaidaTipoSaida);
//
//        EntradaSaidaTipoEntradaRepository entradaSaidarep = new EntradaSaidaTipoEntradaRepository(entradaSaidaEntrada);
//        entradaSaidarep.importarEntradaSaidaTipoEntrada(entradaSaidaTipoEntrada);
////************//
//
//        //inserir tipomodelo
//        TipoModeloRepository modeloRepo = new TipoModeloRepository(modeloprovider);
//        modeloRepo.importarModelo(modelo);
//
//        EcPdvRepository ecfRepository = new EcPdvRepository(ecfProvider);
//        ecfRepository.importarEcf(ecf);
//
//        PdvFuncaoRepository funcaorepository = new PdvFuncaoRepository(funcaoPro);
//        funcaorepository.importarPdvFuncao(funcaoImp);
//
//        TecladoLayoutRepository tecRep = new TecladoLayoutRepository(tecladoLayoutProvider);
//        tecRep.importarTecladoLayout(tecladoLayout);
//
//        PdvTecladoFuncaoRepository tecladoRep = new PdvTecladoFuncaoRepository(tecladoprov);
//        tecladoRep.importarFuncaoeTeclado(tecladoImp);
//
//        EcfLayoutRepository ecfLayoutRep = new EcfLayoutRepository(ecfLayoutProvider);
//        ecfLayoutRep.importarEcfLayout(ecfLayout);
//
//        PdvFuncaoOperadorRepository funcaoRepository = new PdvFuncaoOperadorRepository(opePro);
//        funcaoRepository.importarFuncaoOperador(funcaoOperador);
//
//        FinalizadoraRepository finRep = new FinalizadoraRepository(finProv);
//        finRep.importarFinalizadora(finImp);
//
//        FinalizadoraConfiguracaoRepository confReposi = new FinalizadoraConfiguracaoRepository(configuracao);
//        confReposi.finalizadoraConf(finalizadoraConf);
//
//        FinalizadoraLayoutRetornoRepository layRepo = new FinalizadoraLayoutRetornoRepository(layRep);
//        layRepo.importarFinalizadoraLayout(retImp);
//
//        AutorizadoraRepository auto = new AutorizadoraRepository(aut);
//        auto.importarAutorizadora(autorizadora);
//
//        TipoTefRepository repository = new TipoTefRepository(prov);
//        repository.importarTipoTef(tef);
//
///*        TipoRecebivelRepository rep = new TipoRecebivelRepository(provider);
//        rep.importarRecebivel(recebivel);
//*/
//        RecebivelConfiguracaoRepository configRep = new RecebivelConfiguracaoRepository(confPro);
//        configRep.importarConfiguracaoRecebivel(confRe);
//
//        RecebivelConfiguracaoTabelaRepository confRepo = new RecebivelConfiguracaoTabelaRepository(confRecTab);
//        confRepo.importarConfiguracaoRecebivelTabela(recConf);
//
//        TipoRecebivelFinalizadoraRepository tprRepo = new TipoRecebivelFinalizadoraRepository(tpPro);
//        tprRepo.importarTipoRecebivelFinalizadora(tpRec);
//
//    }

    /**
     * Importa os ECFs do banquinho pdv Firebird.
     *
     * @throws Exception
     */
    public void importarECFPdv() throws Exception {
        List<EcfPdvVO> ecfs = getInterfaceDAO().getECF();
        EcfRepository rep = new EcfRepository();
        rep.salvarECFPdv(ecfs);
    }

    /**
     * Importa o histórico de vendas do cliente.
     *
     * @param utilizarEAN
     * @throws Exception
     */
    public void importarHistoricoVendas(boolean utilizarEAN) throws Exception {
        ProgressBar.setStatus("Preenchendo as vendas...");
        VendaHistoricoRepository repository = new VendaHistoricoRepository(
                new VendaHistoricoDAO(),
                new ProdutoAnteriorDAO(false)
        );
        repository.setImportSistema(getSistema());
        repository.setImportLoja(getLojaOrigem());
        repository.setIdLojaVR(getLojaVR());
        repository.importar(getInterfaceDAO().getHistoricoVenda(), utilizarEAN);
    }

    /**
     * Atualiza as informações dos produtos conforme as opções informadas.
     *
     * @param opcoes Opções que determinam o que será atualizado no produto.
     * @throws Exception
     */
    public void atualizarProdutos(List<OpcaoProduto> opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutos();
        for (OpcaoProduto opt : opcoes) {
            opt.setListaEspecial(getInterfaceDAO().getProdutos(opt));
        }
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLoja(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        provider.setIdConexao(getIdConexao());
        //TODO: Remover essa duplicidade para informar os parâmetros ao provider.
        if (isImportarIndividualLoja()) {
            provider.getOpcoes().add(OpcaoProduto.IMPORTAR_INDIVIDUAL_LOJA);
        }

        provider.getOpcoes().add(OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC);
        ProdutoRepository repository = new ProdutoRepository(provider);
        LOG.info("Produtos da listagem normal repassados: " + produtos.size());
        repository.atualizar(produtos, opcoes.toArray(new OpcaoProduto[]{}));
    }

    /**
     * Importa o cadastro dos clientes preferênciais.
     *
     * @param opcoes Opções para importar os dados do cliente.
     * @throws Exception
     */
    public void importarClientePreferencial(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes preferenciais...");
        List<ClienteIMP> clientes = getInterfaceDAO().getClientesPreferenciais();

        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();

        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        provider.setIdConexao(getIdConexao());
        ClienteRepository rep = new ClienteRepository(provider);
        rep.salvarClientePreferencial2_5(clientes, new HashSet<>(Arrays.asList(opcoes)));
    }

    public void importarClientePontuacao() throws Exception {
        ProgressBar.setStatus("Carregando clientes preferenciais...");

        List<ClienteIMP> clientes = getInterfaceDAO().getClientesPreferenciais();
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();

        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());

        ClienteRepository rep = new ClienteRepository(provider);

        rep.importarClientePontuacao(clientes);
    }

    /**
     * Importa o cadastro dos clientes eventuais.
     *
     * @param opcoes Opções para importar os dados do cliente.
     * @throws Exception
     */
    public void importarClienteEventual(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes eventuais...");

        List<ClienteIMP> clientes = getInterfaceDAO().getClientesEventuais();
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();

        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        provider.setIdConexao(getIdConexao());

        ClienteRepository rep = new ClienteRepository(provider);
        rep.salvarClienteEventual2_5(clientes, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Importa o cadastro de clientes do VR Food.
     *
     * @param opcoes Opções para importar os dados do cliente.
     * @throws Exception
     */
    public void importarClienteVRFood(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes (VRFood)...");
        List<ClienteIMP> clientes = getInterfaceDAO().getClientesVRFood();
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        ClienteRepository rep = new ClienteRepository(provider);
        rep.importarClienteVRFood(clientes, new HashSet<>(Arrays.asList(opcoes)));
    }

    public void importarCreditoRotativo() throws Exception {
        ProgressBar.setStatus("Carregando crédito rotativo...");
        List<CreditoRotativoIMP> rotativo = getInterfaceDAO().getCreditoRotativo();
        CreditoRotativoProvider provider = new CreditoRotativoProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        rep.importarCreditoRotativo(rotativo);
    }

    public void unificarCreditoRotativo() throws Exception {
        ProgressBar.setStatus("Carregando crédito rotativo...");
        List<CreditoRotativoIMP> rotativo = getInterfaceDAO().getCreditoRotativo();
        CreditoRotativoProvider provider = new CreditoRotativoProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        CreditoRotativoRepository rep = new CreditoRotativoRepository(provider);
        rep.unificarCreditoRotativo(rotativo);
    }

    /**
     * Unifica o cadastro de produtos. Todos os produtos com EAN válido serão
     * importados e aqueles que não possuirem EAN maior que 999999 são gravados
     * apenas na tabela implantacao.codant_produto.
     *
     * @param opcoes
     * @throws Exception
     */
    public void unificarProdutos(OpcaoProduto... opcoes) throws Exception {
        unificarProdutos(new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Unifica o cadastro de produtos. Todos os produtos com EAN válido serão
     * importados e aqueles que não possuirem EAN maior que 999999 são gravados
     * apenas na tabela implantacao.codant_produto.
     *
     * @param opcoes
     * @throws Exception
     */
    public void unificarProdutos(Set<OpcaoProduto> opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos (Unificação)...");
        List<ProdutoIMP> produtos = getInterfaceDAO().getProdutos();
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLoja(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        provider.setOpcoes(opcoes);
        if (Parametros.OpcoesExperimentaisDeProduto.isUnificacaoExperimentalAtiva()) {
            new ProdutoRepository(provider).unificar2(produtos);
        } else {
            new ProdutoRepository(provider).unificar(produtos);
        }

    }

    public void unificarFornecedor(OpcaoFornecedor... opt) throws Exception {
        unificarFornecedor(new HashSet<>(Arrays.asList(opt)));
    }

    /**
     * Unifica o cadastro dos fornecedores, apenas aqueles com CPF/CNPJ válidos,
     * e aqueles que não se enquadram nessa categoria são gravados apenas na
     * tabela implantacao.codant_fornecedor.
     *
     * @param opt
     * @throws Exception
     */
    public void unificarFornecedor(Set<OpcaoFornecedor> opt) throws Exception {
        ProgressBar.setStatus("Carregando fornecedores (Unificação)...");
        List<FornecedorIMP> fornecedores = getInterfaceDAO().getFornecedores();
        FornecedorRepositoryProvider provider = new FornecedorRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        provider.setOpcoes(opt);
        FornecedorRepository rep = new FornecedorRepository(provider);
        rep.unificar(fornecedores);
    }

    /**
     * Unifica o cadastro de Produto Fornecedor. Se o fornecedor existir e se o
     * código externo não estiver sendo utilizado grava o registro.
     *
     * @param opcoes
     * @throws Exception
     */
    public void unificarProdutoFornecedor(OpcaoProdutoFornecedor... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando produtos dos fornecedores (Unificação)...");
        List<ProdutoFornecedorIMP> produtos = getInterfaceDAO().getProdutosFornecedores();
        ProdutoFornecedorDAO dao = new ProdutoFornecedorDAO();
        dao.setImportSistema(getSistema());
        dao.setImportLoja(getLojaOrigem());
        dao.setIdLojaVR(getLojaVR());
        dao.salvar(produtos, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Unifica o cadastro dos clientes preferenciais. Todo cliente com CNPJ/CPF
     * válido será importado e os que não se enquadram nessa regra ficam
     * gravados apenas na tabela implantacao.codant_clientepreferencial.
     *
     * @param opcoes Opções de importação.
     * @throws Exception
     */
    public void unificarClientePreferencial(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes preferenciais(Unificação)...");
        List<ClienteIMP> clientes = getInterfaceDAO().getClientesPreferenciais();
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        new ClienteRepository(provider).unificarClientePreferencial(clientes, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Unifica o cadastro dos clientes eventuais. Todo cliente com CNPJ/CPF
     * válido será importado e os que não se enquadram nessa regra ficam
     * gravados apenas na tabela implantacao.codant_clienteeventual.
     *
     * @param opcoes Opções da importação.
     * @throws Exception
     */
    public void unificarClienteEventual(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes eventuais(Unificação)...");
        List<ClienteIMP> clientes = getInterfaceDAO().getClientesEventuais();
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        new ClienteRepository(provider).unificarClienteEventual(clientes, new HashSet<>(Arrays.asList(opcoes)));
    }

    /**
     * Atualiza informações do cadastro de fornecedores.
     *
     * @param opcoes
     * @throws Exception
     */
    public void atualizarFornecedor(OpcaoFornecedor... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando fornecedores (atualização)...");
        List<FornecedorIMP> fornecedores = getInterfaceDAO().getFornecedores();
        FornecedorRepositoryProvider provider = new FornecedorRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        provider.setOpcoes(new HashSet<>(Arrays.asList(opcoes)));
        FornecedorRepository rep = new FornecedorRepository(provider);
        rep.atualizar(fornecedores, opcoes);
    }

    /**
     * Atualiza as informações dos produtos fornecedores conforme as opções
     * informadas.
     *
     * @param opcoes Opções que determinam o que será atualizado no produto.
     * @throws Exception
     */
    public void atualizarProdutoFornecedor(List<OpcaoProdutoFornecedor> opcoes) throws Exception {
        ProgressBar.setStatus("Carregando Produtos Fornecedores (atualização)...");
        //List<ProdutoFornecedorIMP> produtoFornecedores = getInterfaceDAO().getProdutosFornecedores();
        List<ProdutoFornecedorIMP> produtoFornecedores = null;
        for (OpcaoProdutoFornecedor opt : opcoes) {
            opt.setListaEspecial(getInterfaceDAO().getProdutosFornecedores(opt));
            produtoFornecedores = opt.getListaEspecial();
        }

        FornecedorRepositoryProvider provider = new FornecedorRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );

        FornecedorRepository rep = new FornecedorRepository(provider);
        rep.atualizarProdFornecedor(produtoFornecedores, opcoes.toArray(new OpcaoProdutoFornecedor[]{}));
    }

    public void atualizarFornecedorNovo(List<OpcaoFornecedor> opcoes) throws Exception {
        ProgressBar.setStatus("Carregando fornecedores (atualização)...");
        List<FornecedorIMP> fornecedores = null;
        for (OpcaoFornecedor opt : opcoes) {
            opt.setListaEspecial(getInterfaceDAO().getFornecedores(opt));
            fornecedores = opt.getListaEspecial();
        }

        FornecedorRepositoryProvider provider = new FornecedorRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        FornecedorRepository repository = new FornecedorRepository(provider);
        repository.atualizar(fornecedores, opcoes.toArray(new OpcaoFornecedor[]{}));
    }

    public void atualizarClientePreferencial(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes (atualização)...");
        List<ClienteIMP> clientes = getInterfaceDAO().getClientes();
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        ClienteRepository rep = new ClienteRepository(provider);
        rep.atualizarClientePreferencial(clientes, opcoes);
    }

    public void atualizarClientePreferencialNovo(OpcaoCliente... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando clientes (atualização)...");
        List<ClienteIMP> clientes = null;
        for (OpcaoCliente opt : opcoes) {
            opt.setListaEspecial(getInterfaceDAO().getClientes(opt));
            clientes = opt.getListaEspecial();
        }
        ClienteRepositoryProvider provider = new ClienteRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLojaOrigem(getLojaOrigem());
        provider.setLojaVR(getLojaVR());
        ClienteRepository rep = new ClienteRepository(provider);
        rep.atualizarClientePreferencial(clientes, opcoes);
    }

    public void importarCheque() throws Exception {
        ProgressBar.setStatus("Carregando cheques...");
        List<ChequeIMP> cheques = getInterfaceDAO().getCheques();
        ChequeRepositoryProvider provider = new ChequeRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        ChequeRepository rep = new ChequeRepository(provider);
        rep.salvar(cheques);
    }

    public void importarConvenioEmpresa() throws Exception {
        ProgressBar.setStatus("Carregando empresas (Convênio)...");
        List<ConvenioEmpresaIMP> empresas = getInterfaceDAO().getConvenioEmpresa();
        ConvenioEmpresaRepositoryProvider provider = new ConvenioEmpresaRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        ConvenioEmpresaRepository rep = new ConvenioEmpresaRepository(provider);
        rep.salvar(empresas);
    }

    public void importarConvenioConveniado(OpcaoConvenio... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando conveniados (Convênio)...");
        List<ConveniadoIMP> conveniados = getInterfaceDAO().getConveniado();
        ConvenioConveniadoRepositoryProvider provider = new ConvenioConveniadoRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        ConvenioConveniadoRepository rep = new ConvenioConveniadoRepository(provider);
        rep.salvar(conveniados, new HashSet<>(Arrays.asList(opcoes)));
    }

    public void importarConvenioTransacao() throws Exception {
        ProgressBar.setStatus("Carregando recebimentos (Convênio)...");
        List<ConvenioTransacaoIMP> recebimentos = getInterfaceDAO().getConvenioTransacao();
        ConvenioReceberRepositoryProvider provider = new ConvenioReceberRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        ConvenioReceberRepository rep = new ConvenioReceberRepository(provider);
        rep.salvar(recebimentos);
    }

    public void importarContasPagar(OpcaoContaPagar... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando despesas (Outras)...");
        List<ContaPagarIMP> pagamentos = getInterfaceDAO().getContasPagar();
        FinanceiroRepository rep = new FinanceiroRepository(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        rep.getContasPagar().salvar(pagamentos, opcoes);
    }

    public void importarRecebimentoCaixa(OpcaoRecebimentoCaixa... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando recebimento caixa...");
        List<RecebimentoCaixaIMP> recebimentos = getInterfaceDAO().getRecebimentosCaixa();
        FinanceiroRepository rep = new FinanceiroRepository(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        rep.getRecebimentoCaixa().salvar(recebimentos, opcoes);
    }

    /**
     * Utilize esta importação quando o sistema a ser importado trabalha com o
     * contas à receber semelhante a uma "conta corrente", ou seja, na mesma
     * tabela existem operações de crédito e débito, e um pagamento não está
     * relacionado diretamente ( 1 para 1) com uma conta.
     *
     * @param opcoes Array com as opções de importação do Crédito Rotativo.
     * @throws Exception
     */
    public void importarCreditoRotativoBaixasAgrupadas(OpcaoCreditoRotativo... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando pagamentos agrupados dos rotativos...");
        List<CreditoRotativoPagamentoAgrupadoIMP> pags = getInterfaceDAO().getCreditoRotativoPagamentoAgrupado();
        FinanceiroRepository rep = new FinanceiroRepository(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        rep.getCreditoRotativo().salvarPagamentosAgrupados(pags, opcoes);
    }

    public void importarCestInvalido() throws Exception {
        ProgressBar.setStatus("Carregando cests...");
        List<ProdutoIMP> cests = getInterfaceDAO().getProdutos();

        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLoja(getLojaOrigem());
        provider.setLojaVR(getLojaVR());

        ProdutoRepository rep = new ProdutoRepository(provider);

        rep.converterCest(cests);
    }

    /**
     * Importa ofertas de acordo com a data de termino.
     *
     * @param dataTermino
     * @throws Exception
     */
    public void importarOfertas(Date dataTermino) throws Exception {
        ProgressBar.setStatus("Ofertas...Gerando listagem...");

        List<OfertaIMP> ofertas = getInterfaceDAO().getOfertas(dataTermino);
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        provider.setSistema(getSistema());
        provider.setLoja(getLojaOrigem());
        provider.setLojaVR(getLojaVR());

        ProdutoRepository rep = new ProdutoRepository(provider);
        rep.salvarOfertas(ofertas);
    }

    /**
     * Efetua a importação da Pauta Fiscal.
     *
     * @param opcoes Opções de importação da Pauta Fiscal.
     * @throws Exception
     */
    public void importarPautaFiscal(OpcaoFiscal... opcoes) throws Exception {
        Set<OpcaoFiscal> opt = new HashSet<>(Arrays.asList(opcoes));
        ProgressBar.setStatus("Pauta Fiscal...Gerando listagem...");
        List<PautaFiscalIMP> pautas = getInterfaceDAO().getPautasFiscais(opt);
        FiscalRepositoryProvider provider = new FiscalRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        FiscalRepository rep = new FiscalRepository(provider);
        rep.pautaFiscal().importar(pautas, opt);
    }

    private JdbcConnectionSource getSource() throws SQLException {
        return new JdbcConnectionSource("jdbc:sqlite:" + Parametros.get().getBancoImplantacao());
    }

    /**
     * Efetua a importação das Vendas conforme as opções passadas.
     *
     * @param opcoes Opções das importações de venda.
     * @throws Exception
     */
    public void importarVendas(OpcaoVenda... opcoes) throws Exception {
        Set<OpcaoVenda> opt = new HashSet<>(Arrays.asList(opcoes));
        ProgressBar.setStatus("Vendas...Gerando listagem...");

        if (Parametros.get().isGerarBancoImplantacao()) {
            try (JdbcConnectionSource source = this.getSource()) {
                ProgressBar.setStatus("Vendas...Gerando listagem de cabeçalho de venda...");
                new VendaImpDao(source).persistir(getInterfaceDAO().getVendaIterator());
                System.gc();
                ProgressBar.setStatus("Vendas...Gerando listagem dos itens das vendas...");
                new VendaItemImpDao(source).persistir(getInterfaceDAO().getVendaItemIterator());
                System.gc();
            } catch (Exception ex) {
                if (ex.getCause() instanceof SQLiteException) {
                    ex = (Exception) ex.getCause();
                }
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                throw ex;
            }
        }

        if (Parametros.get().isImportarBancoImplantacao()) {
            VendaRepositoryProvider provider = new VendaRepositoryProvider(
                    getSistema(),
                    getLojaOrigem(),
                    getLojaVR()
            );
            Object[] options = {"pdv.venda", "public.venda", "Cancelar"};
            int decisao = JOptionPane.showOptionDialog(null, "Qual tabela você deseja preencher?\n\n",
                    "Importando Vendas...", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (decisao == 0) {
                VendaRepository rep = new VendaRepository(provider);
                rep.idProdutoSemUltimoDigito = idProdutoSemUltimoDigito;
                rep.eBancoUnificado = eBancoUnificado;
                rep.importar(opt);
            }
            if (decisao == 1) {
                PublicVendaRepository rep = new PublicVendaRepository(provider,
                        this.checarVendasDataAtual);
                rep.idProdutoSemUltimoDigito = idProdutoSemUltimoDigito;
                rep.eBancoUnificado = eBancoUnificado;
                rep.importar(opt);
            }
            if (decisao == 2) {
                throw new NullPointerException("Nunhuma venda foi importada");
            }
//            
//            PublicVendaRepository rep = new PublicVendaRepository(provider, 
//                    this.checarVendasDataAtual);
//            rep.idProdutoSemUltimoDigito = idProdutoSemUltimoDigito;
//            rep.eBancoUnificado = eBancoUnificado;
//            rep.importar(opt);
        }
    }

    /**
     * Executa a importação do Nutricional para a impressora Filizola.
     *
     * @throws Exception
     */
    public void importarNutricionalFilizola() throws Exception {
        ProgressBar.setStatus("Carregando Nutricional Filizola...");
        List<NutricionalFilizolaVO> nutri = getInterfaceDAO().getNutricionalFilizola();
        NutricionalFilizolaRepository rep = new NutricionalFilizolaRepository();
        rep.salvarClassesEspecificas(nutri, getSistema(), getLojaOrigem());
    }

    /**
     * Executa a importação do Nutricional para a impressora Toledo.
     *
     * @throws Exception
     */
    public void importarNutricionalToledo() throws Exception {
        ProgressBar.setStatus("Carregando Nutricional Toledo...");
        List<NutricionalToledoVO> nutri = getInterfaceDAO().getNutricionalToledo();
        NutricionalToledoRepository rep = new NutricionalToledoRepository();
        rep.salvarClassesEspecificas(nutri, getSistema(), getLojaOrigem());
    }

    /**
     * Executa a importação dos nutricionais para as balanças que o VR trabalha.
     *
     * @param opcoes Opções de importação dos nutricionais.
     * @throws Exception
     */
    public void importarNutricional(OpcaoNutricional... opcoes) throws Exception {
        Set<OpcaoNutricional> opt = new HashSet<>(Arrays.asList(opcoes));
        ProgressBar.setStatus("Nutricionais...Gerando listagem...");
        List<NutricionalIMP> nutricionais = getInterfaceDAO().getNutricional(opt);
        NutricionalRepositoryProvider provider = new NutricionalRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        NutricionalRepository rep = new NutricionalRepository(provider);
        rep.importar(nutricionais, opt);
    }

    public void importarComprador() throws Exception {
        ProgressBar.setStatus("Compradores...Gerando listagem...");
        List<CompradorIMP> compradores = getInterfaceDAO().getCompradores();
        CompradorRepositoryProvider provider = new CompradorRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        CompradorRepository rep = new CompradorRepository(provider);
        rep.importar(compradores);
    }

    public void importarReceitaBalanca(OpcaoReceitaBalanca... opcoes) throws Exception {
        Set<OpcaoReceitaBalanca> opt = new HashSet<>(Arrays.asList(opcoes));
        ProgressBar.setStatus("Receita Balança...Gerando listagem...");
        List<ReceitaBalancaIMP> receita = getInterfaceDAO().getReceitaBalanca(opt);
        ReceitaBalancaRepositoryProvider provider = new ReceitaBalancaRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        ReceitaBalancaRepository rep = new ReceitaBalancaRepository(provider);
        rep.importar(receita, opt);
    }

    public void importarReceitasProducao() throws Exception {
        ProgressBar.setStatus("Receitas...Gerando listagem...");
        List<receita.ReceitaIMP> receitas = getInterfaceDAO().getReceitasProducao();
        receita2.ReceitaRepository rep = new receita2.ReceitaRepository(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        rep.importar(receitas);
    }

    public void importarReceitas() throws Exception {
        ProgressBar.setStatus("Receitas...Gerando listagem...");
        List<ReceitaIMP> receitas = getInterfaceDAO().getReceitas();
        ReceitaRepositoryProvider provider = new ReceitaRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        ReceitaRepository rep = new ReceitaRepository(provider);
        rep.importar(receitas);
    }

    public void importarOutrasReceitas(OpcaoContaReceber... opcoes) throws Exception {
        Set<OpcaoContaReceber> opt = new HashSet<>(Arrays.asList(opcoes));
        ProgressBar.setStatus("Outras Receitas...Gerando listagem...");
        List<ContaReceberIMP> receita = getInterfaceDAO().getContasReceber(opt);
        OutraReceitaRepositoryProvider provider = new OutraReceitaRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        OutraReceitaRepository rep = new OutraReceitaRepository(provider);
        rep.importar(receita, opt);
    }

    /**
     * Importa o cadastro dos operadores.
     *
     * @throws Exception
     */
    public void importarOperador() throws Exception {
        ProgressBar.setStatus("Carregando operadores...");
        List<OperadorIMP> operadores = getInterfaceDAO().getOperadores();
        OperadorRepositoryProvider provider = new OperadorRepositoryProvider(
                getLojaVR()
        );
        OperadorRepository rep = new OperadorRepository(provider);
        rep.importarOperador(operadores);
    }

    /**
     * Importa o cadastro dos operadores.
     *
     * @throws Exception
     */
    public void importarAcumulador() throws Exception {
        ProgressBar.setStatus("Carregando acumuladores...");
        List<AcumuladorIMP> acumuladores = getInterfaceDAO().getAcumuladores();
        List<AcumuladorLayoutIMP> acumuladoresLayout = getInterfaceDAO().getAcumuladoresLayout();
        List<AcumuladorLayoutRetornoIMP> acumuladoresLayoutRetorno = getInterfaceDAO().getAcumuladoresLayoutRetorno();
        AcumuladorRepositoryProvider provider = new AcumuladorRepositoryProvider();
        provider.setLojaVR(getLojaVR());
        AcumuladorRepository rep = new AcumuladorRepository(provider);
        rep.importarAcumulador(acumuladores, acumuladoresLayout, acumuladoresLayoutRetorno);
    }

    /**
     * Importa o inventário.
     *
     * @throws Exception
     */
    public void importarInventario() throws Exception {
        ProgressBar.setStatus("Carregando inventário...");
        List<InventarioIMP> inventario = getInterfaceDAO().getInventario();
        InventarioRepositoryProvider provider = new InventarioRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        InventarioRepository rep = new InventarioRepository(provider);
        rep.importarInventario(inventario);
    }

    /**
     * Importa o cadastro de associados do sistema.
     *
     * @param opcoes Opções de importação do associado.
     * @throws Exception
     */
    public void importarAssociado(OpcaoAssociado... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando associado...");
        HashSet<OpcaoAssociado> opt = new HashSet<>(Arrays.asList(opcoes));
        List<AssociadoIMP> associados = getInterfaceDAO().getAssociados(opt);
        AssociadoRepositoryProvider provider = new AssociadoRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        AssociadoRepository rep = new AssociadoRepository(provider);
        rep.importarAssociado(associados, opt);
    }

    /**
     * Efetua a importação de notas fiscais no sistema.
     *
     * @param opcoes Opções de importação de notas fiscais.
     * @throws Exception
     */
    public void importarNotas(OpcaoNotaFiscal... opcoes) throws Exception {
        ProgressBar.setStatus("Carregando notas fiscais...");
        HashSet<OpcaoNotaFiscal> opt = new HashSet<>(Arrays.asList(opcoes));
        List<NotaFiscalIMP> notas = getInterfaceDAO().getNotasFiscais();
        NotaFiscalRepositoryProvider provider = new NotaFiscalRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        NotaFiscalRepository repository = new NotaFiscalRepository(provider);
        repository.importar(notas, opt);
    }

    /**
     * Importa as divisões de fornecedor.
     *
     * @throws Exception
     */
    public void importarDivisoes() throws Exception {
        ProgressBar.setStatus("Carregando divisões de fornecedor...");

        List<DivisaoIMP> divisoes = getInterfaceDAO().getDivisoes();
        DivisaoRepositoryProvider provider = new DivisaoRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        DivisaoRepository repository = new DivisaoRepository(provider);
        repository.importar(divisoes);
    }

    /**
     * Importa as promoções.
     *
     * @throws Exception
     */
    public void importarPromocao() throws Exception {
        ProgressBar.setStatus("Carregando Promoções...");
        List<PromocaoIMP> listaPromocoesVemDoDAO = getInterfaceDAO().getPromocoes();
        PromocaoRepositoryProvider provider = new PromocaoRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR(),
                getIdConexao()
        );
        PromocaoRepository rep = new PromocaoRepository(provider);
        rep.salvar(listaPromocoesVemDoDAO);
    }

    public void importarDesmembramento() throws Exception {
        ProgressBar.setStatus("Carregando Desmembramentos...");
        List<DesmembramentoIMP> desmembramentos = getInterfaceDAO().getDesmembramentos();
        DesmembramentoRepositoryProvider provider = new DesmembramentoRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR()
        );
        DesmembramentoRepository rep = new DesmembramentoRepository(provider);
        rep.importarDesmembramento(desmembramentos);
    }
}
