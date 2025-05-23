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
import vrimplantacao2.dao.cadastro.cliente.PessoaImpRepository;
import vrimplantacao2.dao.cadastro.comprador.CompradorRepository;
import vrimplantacao2.dao.cadastro.comprador.CompradorRepositoryProvider;
import vrimplantacao2.dao.cadastro.convenio.OpcaoConvenio;
import vrimplantacao2.dao.cadastro.convenio.conveniado.ConvenioConveniadoRepository;
import vrimplantacao2.dao.cadastro.convenio.conveniado.ConvenioConveniadoRepositoryProvider;
import vrimplantacao2.dao.cadastro.convenio.empresa.ConvenioEmpresaRepository;
import vrimplantacao2.dao.cadastro.convenio.empresa.ConvenioEmpresaRepositoryProvider;
import vrimplantacao2.dao.cadastro.convenio.receber.ConvenioReceberRepository;
import vrimplantacao2.dao.cadastro.convenio.receber.ConvenioReceberRepositoryProvider;
import vrimplantacao2.dao.cadastro.desmembramento.DesmembramentoRepository;
import vrimplantacao2.dao.cadastro.desmembramento.DesmembramentoRepositoryProvider;
import vrimplantacao2.dao.cadastro.financeiro.FinanceiroRepository;
import vrimplantacao2.dao.cadastro.financeiro.contaspagar.OpcaoContaPagar;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoProvider;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.CreditoRotativoRepository;
import vrimplantacao2.dao.cadastro.financeiro.creditorotativo.OpcaoCreditoRotativo;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.OpcaoRecebimentoCaixa;
import vrimplantacao2.dao.cadastro.fiscal.FiscalRepository;
import vrimplantacao2.dao.cadastro.fiscal.FiscalRepositoryProvider;
import vrimplantacao2.dao.cadastro.fiscal.inventario.InventarioRepository;
import vrimplantacao2.dao.cadastro.fiscal.inventario.InventarioRepositoryProvider;
import vrimplantacao2.dao.cadastro.fornecedor.FamiliaFornecedorDAO;
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
import vrimplantacao2.dao.cadastro.usuario.OpcaoUsuario;
import vrimplantacao2.dao.cadastro.usuario.TipoSetorDAO;
import vrimplantacao2.dao.cadastro.usuario.UsuarioRepository;
import vrimplantacao2.dao.cadastro.usuario.UsuarioRepositoryProvider;
import vrimplantacao2.dao.cadastro.venda.PublicVendaRepository;
import vrimplantacao2.vo.importacao.DesmembramentoIMP;
import vrimplantacao2.vo.importacao.FamiliaFornecedorIMP;
import vrimplantacao2.vo.importacao.PessoaImp;
import vrimplantacao2.vo.importacao.PromocaoIMP;
import vrimplantacao2.vo.importacao.TipoSetorIMP;
import vrimplantacao2.vo.importacao.UsuarioIMP;
import vrimplantacao2_5.relatorios.gerador.GeradorArquivosRepository;

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
     * Executa a importação da familia fornecedor.
     *
     * @param opcoes
     * @throws Exception
     */
    public void importarFamiliaFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando familias dos fornecedores...");
        List<FamiliaFornecedorIMP> familias = getInterfaceDAO().getFamiliaFornecedor();

        FamiliaFornecedorDAO dao = new FamiliaFornecedorDAO();
        dao.setSistema(getSistema());
        dao.salvar(familias);
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
     * Executa a importação do Tipo de Setor dos Usuários.
     *
     * @param opcoes
     * @throws Exception
     */
    public void importarTipoSetor() throws Exception {
        ProgressBar.setStatus("Carregando tipos de setores...");
        List<TipoSetorIMP> setores = getInterfaceDAO().getTipoSetor();

        TipoSetorDAO dao = new TipoSetorDAO();
        dao.setSistema(getSistema());
        dao.salvar(setores);
    }

    public void importarUsuarios(OpcaoUsuario... opt) throws Exception {
        this.importarUsuarios(new HashSet<>(Arrays.asList(opt)));
    }

    /**
     * Executa a importação dos usuários.
     *
     * @param opt
     * @throws Exception
     */
    public void importarUsuarios(Set<OpcaoUsuario> opt) throws Exception {
        ProgressBar.setStatus("Carregando usuários...");
        List<UsuarioIMP> usuarios = getInterfaceDAO().getUsuarios();

        UsuarioRepositoryProvider provider = new UsuarioRepositoryProvider(
                getSistema(),
                getLojaOrigem(),
                getLojaVR(),
                getIdConexao()
        );
        provider.setOpcoes(opt);
        UsuarioRepository rep = new UsuarioRepository(provider);
        rep.salvar2_5(usuarios);
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

    public void importarPessoaImp() throws Exception {
        ProgressBar.setStatus("Carregando clientes Master 5.0...");
        List<PessoaImp> clientes = getInterfaceDAO().getPessoaImp();
        PessoaImpRepository rep = new PessoaImpRepository();
        rep.salvarPessoaImp(clientes);
    }
}
