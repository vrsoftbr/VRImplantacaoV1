package vrimplantacao.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import javax.swing.Box;
import javax.swing.DefaultDesktopManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import org.openide.util.Exceptions;
import vr.implantacao.main.App;
import vr.view.helpers.ConexaoPropertiesEditorGUI;
import vrframework.bean.busca.VRBusca;
import vrframework.bean.busca.VREventoBusca;
import vrframework.bean.busca.VREventoBuscaListener;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrframework.gui.SobreGUI;
import vrframework.remote.Arquivo;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.gui.assistente.mapamercadologico.MapaMercadologicoGUI;
import vrimplantacao.gui.assistente.parametro.ParametroGUI;
import vrimplantacao.gui.cadastro.LojaConsultaGUI;
import vrimplantacao.gui.interfaces.AcertarCodigoInternoGUI;
import vrimplantacao.gui.interfaces.AcertarIdsProdutoGUI;
import vrimplantacao.gui.interfaces.ActiveGUI;
import vrimplantacao.gui.interfaces.AlfaSoftwareGUI;
import vrimplantacao.gui.interfaces.AlterarProdutoPdvVendaItemGUI;
import vrimplantacao.gui.interfaces.AriusGUI;
import vrimplantacao.gui.interfaces.ArquivoPadraoGUI;
import vrimplantacao.gui.interfaces.BoechatSoftGUI;
import vrimplantacao.gui.interfaces.ConcretizeGUI;
import vrimplantacao2.gui.interfaces.ControlWareGUI;
import vrimplantacao.gui.interfaces.ControllGUI;
import vrimplantacao.gui.interfaces.CupermaxGUI;
import vrimplantacao.gui.interfaces.DGComGUI;
import vrimplantacao2.gui.interfaces.DelphiGUI;
import vrimplantacao.gui.interfaces.EccusInformaticaGUI;
import vrimplantacao.gui.interfaces.EverastGUI;
import vrimplantacao.gui.interfaces.FGGUI;
import vrimplantacao.gui.interfaces.FMGUI;
import vrimplantacao.gui.interfaces.FabTechGUI;
import vrimplantacao.gui.interfaces.FaucomGUI;
import vrimplantacao.gui.interfaces.FlatanGUI;
import vrimplantacao.gui.interfaces.GCFGUI;
import vrimplantacao2.gui.interfaces.GZSistemasGUI;
import vrimplantacao.gui.interfaces.GdoorGUI;
import vrimplantacao.gui.interfaces.GeraContaPagarGUI;
import vrimplantacao.gui.interfaces.GerarCodigoBarrasAtacadoGUI;
import vrimplantacao.gui.interfaces.GetWayCloudGUI;
import vrimplantacao.gui.interfaces.HostMundoGUI;
import vrimplantacao.gui.interfaces.IdealGUI;
import vrimplantacao.gui.interfaces.IdealSoftGUI;
import vrimplantacao.gui.interfaces.ImportacoesDiversasGUI;
import vrimplantacao.gui.interfaces.ImportarNotaSaidaImportacaoArquivoGUI;
import vrimplantacao.gui.interfaces.InfoStoreGUI;
import vrimplantacao.gui.interfaces.InteragemGUI_2;
import vrimplantacao.gui.interfaces.JMasterGUI;
import vrimplantacao.gui.interfaces.LogusGUI;
import vrimplantacao.gui.interfaces.Maximus_DatasyncGUI;
import vrimplantacao.gui.interfaces.RMSGUI;
import vrimplantacao.gui.interfaces.RMSGUI_2;
import vrimplantacao2.gui.interfaces.RootacGUI;
import vrimplantacao.gui.interfaces.SBOMarketGUI;
import vrimplantacao2.gui.interfaces.SIMSGUI;
import vrimplantacao.gui.interfaces.SaacGUI;
import vrimplantacao.gui.interfaces.SacLumiGUI;
import vrimplantacao.gui.interfaces.SciGUI;
import vrimplantacao.gui.interfaces.SicomGUI;
import vrimplantacao.gui.interfaces.SicsGUI;
import vrimplantacao.gui.interfaces.SimSoftGUI;
import vrimplantacao.gui.interfaces.SofgceGUI;
import vrimplantacao.gui.interfaces.SoftClass_AutoComGUI;
import vrimplantacao.gui.interfaces.SoftLineGUI;
import vrimplantacao.gui.interfaces.SoftaExGUI;
import vrimplantacao.gui.interfaces.SuperusGUI;
import vrimplantacao.gui.interfaces.SuperusGUI2;
import vrimplantacao.gui.interfaces.SysMouraGUI;
import vrimplantacao.gui.interfaces.UltraSistGUI;
import vrimplantacao.gui.interfaces.VRSoftwareGUI;
import vrimplantacao.gui.interfaces.VRSoftwarePDVGUI;
import vrimplantacao.gui.interfaces.WisaSoftGUI_2;
import vrimplantacao.gui.interfaces.nfce.NotaSaidaNfceImportacaoArquivoGUI;
import vrimplantacao.gui.interfaces.rfd.ImportacaoLogVendaGUI;
import vrimplantacao2.gui.planilha.PlanilhaProdutoGUI;
import vrimplantacao.vo.Formulario;
import vrimplantacao2_5.dao.copias.GerarCodantDAO;
import vrimplantacao2.gui.SuperControle_PostgresGUI;
import vrimplantacao2.gui.component.CleanDataBase;
import vrimplantacao2.gui.component.sqleditor.SQLEditor;
import vrimplantacao2_5.gui.copias.CopiaEntreLojasGUI;
import vrimplantacao2.gui.interfaces.ASoftGUI;
import vrimplantacao2.gui.interfaces.AccesysGUI;
import vrimplantacao2.gui.interfaces.AcomGUI;
import vrimplantacao2.gui.interfaces.AdmMacenoGUI;
import vrimplantacao2.gui.interfaces.AlphaSysGUI;
import vrimplantacao2.gui.interfaces.ApolloGUI;
import vrimplantacao2.gui.interfaces.ArtSystemGUI;
import vrimplantacao2.gui.interfaces.AsefeGUI;
import vrimplantacao2.gui.interfaces.AtenasGUI;
import vrimplantacao2.gui.interfaces.AtenasSQLSERVERGUI;
import vrimplantacao2.gui.interfaces.AtmaFirebirdGUI;
import vrimplantacao2.gui.interfaces.AtmaGUI;
import vrimplantacao2.gui.interfaces.AutoAdmGUI;
import vrimplantacao2.gui.interfaces.AutoSystemGUI;
import vrimplantacao2.gui.interfaces.AutomaqGUI;
import vrimplantacao2.gui.interfaces.AvanceGUI;
import vrimplantacao2.gui.interfaces.BaseGUI;
import vrimplantacao2.gui.interfaces.BrainSoftGUI;
import vrimplantacao2.gui.interfaces.BrajanGestoresGUI;
import vrimplantacao2.gui.interfaces.CFSoftSiaECFGUI;
import vrimplantacao2.gui.interfaces.CPGestorGUI;
import vrimplantacao2.gui.interfaces.CPlusGUI;
import vrimplantacao2.gui.interfaces.CadastraFacilGUI;
import vrimplantacao2.gui.interfaces.CefasGUI;
import vrimplantacao2.gui.interfaces.CerebroGUI;
import vrimplantacao2.gui.interfaces.CgaGUI;
import vrimplantacao2.gui.interfaces.CissGUI;
import vrimplantacao2.gui.interfaces.ClickGUI;
import vrimplantacao2.gui.interfaces.ContechGUI;
import vrimplantacao2.gui.interfaces.ControlXGUI;
import vrimplantacao2.gui.interfaces.Cronos20GUI;
import vrimplantacao2.gui.interfaces.DJSystemGUI;
import vrimplantacao2.gui.interfaces.DLinkGUI;
import vrimplantacao2.gui.interfaces.DataSyncGUI;
import vrimplantacao2.gui.interfaces.DestroGUI;
import vrimplantacao2.gui.interfaces.DevMasterGUI;
import vrimplantacao2.gui.interfaces.DirectorGUI;
import vrimplantacao2.gui.interfaces.DtComGUI;
import vrimplantacao2.gui.interfaces.EmporioGUI;
import vrimplantacao2.gui.interfaces.EsSystemGUI;
import vrimplantacao2.gui.interfaces.EticaGUI;
import vrimplantacao2.gui.interfaces.ExodusGUI;
import vrimplantacao2.gui.interfaces.FHOnlineGUI;
import vrimplantacao2.gui.interfaces.FaciliteGUI;
import vrimplantacao2.gui.interfaces.Farm2000GUI;
import vrimplantacao2.gui.interfaces.FenixGUI;
import vrimplantacao2.gui.interfaces.FlashGUI;
import vrimplantacao2.gui.interfaces.FortGUI;
import vrimplantacao2.gui.interfaces.FortiGUI;
import vrimplantacao2.gui.interfaces.G10GUI;
import vrimplantacao2.gui.interfaces.G3_v2GUI;
import vrimplantacao2.gui.interfaces.GDoorGUI;
import vrimplantacao2.gui.interfaces.GR7_2GUI;
import vrimplantacao2.gui.interfaces.GTechGUI;
import vrimplantacao2.gui.interfaces.GenericGUI;
import vrimplantacao2.gui.interfaces.GestorPdvGUI;
import vrimplantacao2.gui.interfaces.GetWay_ProfitGUI;
import vrimplantacao2.gui.interfaces.GuiaSistemasGUI;
import vrimplantacao2.gui.interfaces.HRTechGUI;
import vrimplantacao2.gui.interfaces.HRTechGUI_v2;
import vrimplantacao2.gui.interfaces.HerculesIntCashGUI;
import vrimplantacao2.gui.interfaces.HiperGUI;
import vrimplantacao2.gui.interfaces.ICommerceGUI;
import vrimplantacao2.gui.interfaces.IQSistemasGUI;
import vrimplantacao2.gui.interfaces.InfoBrasilGUI;
import vrimplantacao2.gui.interfaces.InfoMacGUI;
import vrimplantacao2.gui.interfaces.InovaGUI;
import vrimplantacao2.gui.interfaces.IntelliCashGUI;
import vrimplantacao2.gui.interfaces.IntelliconGUI;
import vrimplantacao2.gui.interfaces.InterDataGUI;
import vrimplantacao2.gui.interfaces.InventerGUI;
import vrimplantacao2.gui.interfaces.JM2OnlineGUI;
import vrimplantacao2.gui.interfaces.JacsysGUI;
import vrimplantacao2.gui.interfaces.JrfGUI;
import vrimplantacao2.gui.interfaces.KairosGUI;
import vrimplantacao2.gui.interfaces.KcmsGUI;
import vrimplantacao2.gui.interfaces.LBSoftwareGUI;
import vrimplantacao2.gui.interfaces.LiderNetWorkGUI;
import vrimplantacao2.gui.interfaces.LinceGUI;
import vrimplantacao2.gui.interfaces.LinearGUI;
import vrimplantacao2.gui.interfaces.LinnerGUI;
import vrimplantacao2.gui.interfaces.LiteciGUI;
import vrimplantacao2.gui.interfaces.LogTECGUI;
import vrimplantacao2.gui.interfaces.LogusRetailGUI;
import vrimplantacao2.gui.interfaces.LyncisGUI;
import vrimplantacao2.gui.interfaces.MSIInforGUI;
import vrimplantacao2.gui.interfaces.MarketGUI;
import vrimplantacao2.gui.interfaces.MasterGUI;
import vrimplantacao2.gui.interfaces.MilenioGUI;
import vrimplantacao2.gui.interfaces.MobilityGUI;
import vrimplantacao2.gui.interfaces.MobnePdvGUI;
import vrimplantacao2.gui.interfaces.MrsGUI;
import vrimplantacao2.gui.interfaces.MultiPdvGUI;
import vrimplantacao2.gui.interfaces.NATISistemasGUI;
import vrimplantacao2.gui.interfaces.NCAGUI;
import vrimplantacao2.gui.interfaces.OpenGUI;
import vrimplantacao2.gui.interfaces.OrionGUI;
import vrimplantacao2.gui.interfaces.OrionTechGUI;
import vrimplantacao2.gui.interfaces.OryonGUI;
import vrimplantacao2.gui.interfaces.PdvVrGUI;
import vrimplantacao2.gui.interfaces.PhixaGUI;
import vrimplantacao2.gui.interfaces.PlanilhaVrGUI;
import vrimplantacao2.gui.interfaces.PomaresGUI;
import vrimplantacao2.gui.interfaces.ProtonGUI;
import vrimplantacao2.gui.interfaces.PwGestorGUI;
import vrimplantacao2.gui.interfaces.PwsGUI;
import vrimplantacao2.gui.interfaces.RCNetGUI;
import vrimplantacao2.gui.interfaces.RKSoftwareGUI;
import vrimplantacao2.gui.interfaces.RMSAutomaHelpGUI;
import vrimplantacao2.gui.interfaces.RPInfoGUI;
import vrimplantacao2.gui.interfaces.RensoftwareGUI;
import vrimplantacao2.gui.interfaces.RepleisGUI;
import vrimplantacao2.gui.interfaces.SDInformaticaGUI;
import vrimplantacao2.gui.interfaces.STI3GUI;
import vrimplantacao2.gui.interfaces.STIGUI;
import vrimplantacao2.gui.interfaces.STSistemasGUI;
import vrimplantacao2.gui.interfaces.SabTechGUI;
import vrimplantacao2.gui.interfaces.SaefGUI;
import vrimplantacao2.gui.interfaces.SambaNetGUI;
import vrimplantacao2.gui.interfaces.SambaNetV2GUI;
import vrimplantacao2.gui.interfaces.SatecfeGUI;
import vrimplantacao2.gui.interfaces.SavGUI;
import vrimplantacao2.gui.interfaces.ScefGUI;
import vrimplantacao2.gui.interfaces.ShiGUI_v2;
import vrimplantacao2.gui.interfaces.SiaCriareByFileGUI;
import vrimplantacao2.gui.interfaces.SiaCriareDbfGUI;
import vrimplantacao2.gui.interfaces.SiaCriareMySqlGUI;
import vrimplantacao2.gui.interfaces.SiacGUI;
import vrimplantacao2.gui.interfaces.SifatGUI;
import vrimplantacao2.gui.interfaces.Sifat_2GUI;
import vrimplantacao2.gui.interfaces.SigmaGUI;
import vrimplantacao2.gui.interfaces.SiitGUI;
import vrimplantacao2.gui.interfaces.SircomGUI;
import vrimplantacao2.gui.interfaces.SisMouraGUI;
import vrimplantacao2.gui.interfaces.SnSistemaGUI;
import vrimplantacao2.gui.interfaces.SoftcomGUI;
import vrimplantacao2.gui.interfaces.SofttechGUI;
import vrimplantacao2.gui.interfaces.SolidoGUI;
import vrimplantacao2.gui.interfaces.SolidusGUI;
import vrimplantacao2.gui.interfaces.SolutionSuperaGUI;
import vrimplantacao2.gui.interfaces.SophyxGUI;
import vrimplantacao2.gui.interfaces.SophyxVendaGUI;
import vrimplantacao2.gui.interfaces.SriGUI;
import vrimplantacao2.gui.interfaces.SuperGUI;
import vrimplantacao2.gui.interfaces.SuperLoja10GUI;
import vrimplantacao2.gui.interfaces.SuperControle_SuperServerGUI;
import vrimplantacao2.gui.interfaces.SyncTecGUI;
import vrimplantacao2.gui.interfaces.SysERPGUI;
import vrimplantacao2.gui.interfaces.SysPdvGUI;
import vrimplantacao2.gui.interfaces.SysmoFirebirdGUI;
import vrimplantacao2.gui.interfaces.SysmoPostgresGUI;
import vrimplantacao2.gui.interfaces.TGAGUI;
import vrimplantacao2.gui.interfaces.TecnosoftGUI;
import vrimplantacao2.gui.interfaces.TiTecnologiaGUI;
import vrimplantacao2.gui.interfaces.TopSystemGUI;
import vrimplantacao2.gui.interfaces.TopSystemGUI3;
import vrimplantacao2.gui.interfaces.TpaRootacGUI;
import vrimplantacao2.gui.interfaces.TstiGUI;
import vrimplantacao2.gui.interfaces.UmPontoDoisGUI;
import vrimplantacao2.gui.interfaces.UpFortiGUI;
import vrimplantacao2.gui.interfaces.VCashGUI;
import vrimplantacao2.gui.interfaces.VarejoFacilGUI;
import vrimplantacao2.gui.interfaces.ViaSoftGUI;
import vrimplantacao2.gui.interfaces.ViggoGUI;
import vrimplantacao2.gui.interfaces.VisualComercioGUI;
import vrimplantacao2.gui.interfaces.VisualMixGUI;
import vrimplantacao2.gui.interfaces.W2AGUI;
import vrimplantacao2.gui.interfaces.AlterData_WShopGUI;
import vrimplantacao2.gui.interfaces.AthosGUI;
import vrimplantacao2.gui.interfaces.AutoMacGUI;
import vrimplantacao2.gui.interfaces.AutocomGUI;
import vrimplantacao2.gui.interfaces.CervantesGUI;
import vrimplantacao2.gui.interfaces.ControlePlusPostgresGUI;
import vrimplantacao2.gui.interfaces.CorrecaoImpostosDSoftGUI;
import vrimplantacao2.gui.interfaces.CorrecaoImpostosSuperContole_SuperServerGUI;
import vrimplantacao2.gui.interfaces.DSoftGUI;
import vrimplantacao2.gui.interfaces.DTComPlanilhaGUI;
import vrimplantacao2.gui.interfaces.EasySacGUI;
import vrimplantacao2.gui.interfaces.EcoCentauroGUI;
import vrimplantacao2.gui.interfaces.EptusGUI;
import vrimplantacao2.gui.interfaces.FuturaGUI;
import vrimplantacao2.gui.interfaces.GComGUI;
import vrimplantacao2.gui.interfaces.SysAutGUI;
import vrimplantacao2.gui.interfaces.GDIGUI;
import vrimplantacao2.gui.interfaces.GestoraV2GUI;
import vrimplantacao2.gui.interfaces.GigatronGUI;
import vrimplantacao2.gui.interfaces.GondolaGUI;
import vrimplantacao2.gui.interfaces.IServerGUI;
import vrimplantacao2.gui.interfaces.LCSistemaGUI;
import vrimplantacao2.gui.interfaces.LogicboxGUI;
import vrimplantacao2.gui.interfaces.MRC6GUI;
import vrimplantacao2.gui.interfaces.MSuperGUI;
import vrimplantacao2.gui.interfaces.MercaLiteGUI;
import vrimplantacao2.gui.interfaces.MerceariaSeneGUI;
import vrimplantacao2.gui.interfaces.NeoGUI;
import vrimplantacao2.gui.interfaces.PhoenixGUI;
import vrimplantacao2.gui.interfaces.PlenoKWGUI;
import vrimplantacao2.gui.interfaces.PlenusGUI;
import vrimplantacao2.gui.interfaces.PoligonGUI;
import vrimplantacao2.gui.interfaces.PrimeGUI;
import vrimplantacao2.gui.interfaces.ResultMaisGUI;
import vrimplantacao2.gui.interfaces.SBOnlineGUI;
import vrimplantacao2.gui.interfaces.SGMasterGUI;
import vrimplantacao2.gui.interfaces.SicGUI;
import vrimplantacao2.gui.interfaces.StockGUI;
import vrimplantacao2.gui.interfaces.Stock_PostgresGUI;
import vrimplantacao2.gui.interfaces.TeleconGUI;
import vrimplantacao2.gui.interfaces.WebSacGUI;
import vrimplantacao2.gui.interfaces.WeberGUI;
import vrimplantacao2.gui.interfaces.WinNexusGUI;
import vrimplantacao2.gui.interfaces.Winthor_PcSistemasGUI;
import vrimplantacao2.gui.interfaces.Wm_byFileGUI;
import vrimplantacao2.gui.interfaces.WmsiGUI;
import vrimplantacao2.gui.interfaces.ZoomboxGUI;
import vrimplantacao2.gui.interfaces.ZpfGUI;
import vrimplantacao2.gui.interfaces.rodrigues.SupermercadoRodriguesGUI;
import vrimplantacao2.gui.interfaces.unificacao.primeiropreco.PrimeiroPrecoGUI;
import vrimplantacao2.gui.planilha.PlanilhaV2GUI;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.classe.Global;
import vrimplantacao2_5.controller.atualizador.AtualizadorController;
import vrimplantacao2_5.conversor.ConversorDbfGUI;
import vrimplantacao2_5.gui.cadastro.bancodados.ConsultaBancoDadosGUI;
import vrimplantacao2_5.gui.cadastro.configuracao.ConsultaConfiguracaoBaseDadosGUI;
import vrimplantacao2_5.gui.cadastro.sistema.ConsultaSistemaGUI;
import vrimplantacao2_5.gui.cadastro.sistemabancodados.ConsultaSistemaBancoDadosGUI;
import vrimplantacao2_5.gui.cadastro.unidade.ConsultaUnidadeGUI;
import vrimplantacao2_5.gui.cadastro.usuario.ConsultaUsuarioGUI;
import vrimplantacao2_5.conversor.ConversorGUI;
import vrimplantacao2_5.gui.copias.CopiarPromocoesGUI;
import vrimplantacao2_5.gui.copias.GeradorDeBackupGUI;
import vrimplantacao2_5.gui.copias.ZerarEstoqueGUI;
import vrimplantacao2_5.gui.sistema.SysPdv2_5GUI;
import vrimplantacao2_5.gui.sistema.Uniplus2_5GUI;
import vrimplantacao2_5.gui.login.LoginGUI;
import vrimplantacao2_5.gui.sistema.Hipcom2_5GUI;
import vrimplantacao2_5.gui.sistema.VRToVR2_5GUI;
import vrimplantacao2_5.mercadologicopadrao.gui.MercadologicoPadraoGUI;
import vrimplantacao2_5.mural.gui.MemorandoGUI;
import vrimplantacao2_5.mural.service.MemorandoService;
import vrimplantacao2_5.nutricional.GUI.NutricionalArqGUI;
import vrimplantacao2_5.relatorios.gerador.GeradorArquivosRepository;
import vrimplantacao2_5.relatorios.gerador.GeradorProdutoEstoqueFiscal;
import vrimplantacao2_5.relatorios.relatoriosDAO.ExecutaSpedDAO;

public final class MenuGUI extends VRMdiFrame {

    public LojaConsultaGUI formLojaConsulta = null;
    public VRSoftwareGUI formMigracaoVR = null;
    public CgaGUI formImportarCga = null;
    public MilenioGUI formImportarMilenio = null;
    public JMasterGUI formImportarJMaster = null;
    public GetWay_ProfitGUI formImportarGetWay = null;
    public IdealGUI formImportarIdeal = null;
    public ImportacaoLogVendaGUI formImportacaoLogVendaGUI = null;
    public RMSGUI formImportarRM = null;
    public GdoorGUI formImportarGdoor = null;
    public WisaSoftGUI_2 formImportarWisaSoft = null;
    public SoftaExGUI formImportarSoftaEx = null;
    public FMGUI formImportarFM = null;
    public EverastGUI formImportarEverast = null;
    public GuiaSistemasGUI formImportarGuiaSistemas = null;
    public GCFGUI formImportarGCF = null;
    public MultiPdvGUI formImportarMultiPdv = null;
    public OrionGUI formImportarOrion = null;
    public RootacGUI formImportarRootac = null;
    public BoechatSoftGUI formImportarBoechatSoft = null;
    public UltraSistGUI formImportarUltraSyst = null;
    public IntelliCashGUI formImportarIntelliCash = null;
    public ConcretizeGUI formImportarConcretize = null;
    public KairosGUI formImportarKairos = null;
    public DirectorGUI formImportarDirector = null;
    public VRSoftwarePDVGUI formImportarVRSoftwarePDV = null;
    public EccusInformaticaGUI formImportarEccusInformatica = null;
    public SuperControle_SuperServerGUI formImportarSuperServer = null;
    public ControlWareGUI formImportarControlWare = null;
    public DestroGUI formImportarDestroGUI = null;
    public SysMouraGUI formImportarSysMoura = null;
    public ImportarNotaSaidaImportacaoArquivoGUI formNotaSaidaImportacaoArquivoGUI = null;
    public TopSystemGUI formTopSystemGUI = null;
    public SciGUI formSciGUI = null;
    public GZSistemasGUI formImportarGZSistemas = null;
    public SBOMarketGUI formImportarSBOMarket = null;
    public GetWayCloudGUI formImportarGetWayCloud = null;
    public SicsGUI formImportarSics = null;
    public SimSoftGUI formImportarSimSoft = null;
    public FaucomGUI formImportarFaucom = null;
    public SIMSGUI formImportarSIMS = null;
    public SuperusGUI formImportarSuperus = null;
    public SofgceGUI formImportarSofgce = null;
    public ImportacoesDiversasGUI formImportacoesDiversas = null;
    public Maximus_DatasyncGUI formImportarMaximusDatasync = null;
    public SoftClass_AutoComGUI formImportarSoftClass = null;
    public AlfaSoftwareGUI formImportarAlfaSoftware = null;
    public InfoBrasilGUI formImportarInfoBrasil = null;
    public SoftLineGUI formImportarSoftLine = null;
    public InfoStoreGUI formImportarInfoStore = null;
    public DGComGUI formImportarDGCom = null;
    public SaacGUI formImportarSaac = null;
    public LogusGUI formImportarLogus = null;
    public InteragemGUI_2 formImportarInteragem = null;
    public FGGUI formImportarFG = null;
    public IdealSoftGUI formImportarIdealSoft = null;
    public SicomGUI formImportarSicom = null;
    public HostMundoGUI formImportarHostMundo = null;
    public FlatanGUI formImportarFlatan = null;
    public BrainSoftGUI formImportarBrainSoft = null;
    public FabTechGUI formImportarFabTech = null;
    public JacsysGUI formImportarJacsys = null;
    public SifatGUI formImportarSifat = null;
    public GerarCodigoBarrasAtacadoGUI formImportarGerarCodigoBarrasAtacado = null;
    public CorrecaoImpostosSuperContole_SuperServerGUI formCorrecaoImpostosSuperControle_SuperServer = null;
    public NotaSaidaNfceImportacaoArquivoGUI formNotaSaidaNfceImportacaoArquivoGUI = null;
    public AlterarProdutoPdvVendaItemGUI formAlterarProdutoPdvVendaItem = null;
    public ArquivoPadraoGUI formArquivoPadrao = null;
    public PlanilhaV2GUI formPlanilhaV2 = null;
    public AcertarCodigoInternoGUI formAcertarCodigoInterno = null;
    public AcertarIdsProdutoGUI formAcertarIdsProduto = null;
    public CorrecaoImpostosDSoftGUI formCorrecaoImpostosDSoft = null;

    public VRBusca txtBusca = null;

    private LoginGUI loginFrame = null;

    private AtualizadorController atualizadorController = null;

    private MemorandoService memorandoService = new MemorandoService();

    public MenuGUI(LoginGUI i_loginFrame) throws Exception {
        initComponents();

        loginFrame = i_loginFrame;
        setExtendedState(VRMdiFrame.MAXIMIZED_BOTH);

        setIcon("/vrframework/img/icone/loja.png");

        desktopPane = vrDesktopPane;
        desktopPane.setDesktopManager(new DefaultDesktopManager());

        atualizarRodape();
        configurarBusca();
        atualizarJanela();

        if (Global.getIdUnidade() != 1) {
            mnuCadastro2_5.setVisible(false);
        }

        if (memorandoService.inicializaMemorando() != 0) {
            try {
                MemorandoGUI.exibir(this, memorandoService);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void atualizarRodape() throws Exception {

        lblUsuario.setText(Global.getNomeUsuario());
        lblUnidade.setText(Global.getNomeUnidade());
        lblData.setText(new DataProcessamentoDAO().get());
        lblVersao.setText("VERSÃO " + Global.VERSAO);
    }

    private void sair() throws Exception {
        Util.exibirMensagemConfirmar("Deseja realmente sair do programa?", getTitle());
        System.exit(0);
    }

    private void configurarBusca() throws Exception {
        txtBusca = new VRBusca();
        txtBusca.setXml("/vrimplantacao/xml/formulario.xml");

        txtBusca.addEventoBuscaListener(new VREventoBuscaListener() {
            @Override
            public void abreTela(VREventoBusca evt) {
                try {
                    if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_CGA.getId()) {
                        jMenuItemCGAActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SHI.getId()) {
                        jMenuItemSHIActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SYSPDV_FIREBIRD.getId()) {
                        jMenuItemSysPDVActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_IDEAL.getId()) {
                        jMenuItemIdealActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_MOBILITY.getId()) {
                        jMenuItemMobilityActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GDOOR.getId()) {
                        jMenuItemGdoorActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_WISASOFT.getId()) {
                        jMenuItemGdoorActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SOFTAEX.getId()) {
                        jMenuItemSoftaExActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_JMASTER.getId()) {
                        jMenuItemJMasterActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_MILENIO.getId()) {
                        jMenuItemMilenioActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GETWAY.getId()) {
                        jMenuItemGetWayActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SYSPDV_SQLSERVER.getId()) {
                        jMenuItemSysPDVSQLServerActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GUIASISTEMAS.getId()) {
                        jMenuItemGuiaSistemasActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_BOECHATSOFT.getId()) {
                        jMenuItemBoechatSoftActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_RMS.getId()) {
                        jMenuItemRMSActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GCF.getId()) {
                        jMenuItemGCFActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_FMSISTEMAS.getId()) {
                        jMenuItemFMSistemasActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_EVEREST.getId()) {
                        jMenuItemEverastActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_ORION.getId()) {
                        jMenuItemOrionActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_ULTRASYST.getId()) {
                        jMenuItemUltraSystActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_CONCRETIZE.getId()) {
                        jMenuItemConcretizeActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_KAIROS.getId()) {
                        jMenuItemKairosActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_DIRECTOR.getId()) {
                        jMenuItemDirectorActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_ECOS_INFORMATICA.getId()) {
                        jMenuItemEcosInformaticaActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_CISS.getId()) {
                        jMenuItemCISSActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_CONTROLWARE.getId()) {
                        jMenuItemControlWareActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SYSMOURA.getId()) {
                        jMenuItemSysMouraActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_TOPSYSTEM.getId()) {
                        jMenuItemTopSystemActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SCI.getId()) {
                        jMenuItemSciActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SBOMARKET.getId()) {
                        jMenuItemSBOMarketActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SICS.getId()) {
                        jMenuItemSicsActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SIMSOFT.getId()) {
                        jMenuItemSimSoftActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SIMS.getId()) {
                        jMenuItemSIMSActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_GR7.getId()) {
                        jMenuItemGR7ActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SUPERUS.getId()) {
                        jMenuItemSuperusActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_SOFGCE.getId()) {
                        jMenuItemSofgceActionPerformed(null);
                    } else if (evt.idFormulario == Formulario.IMPORTACAO_INFOBRASIL.getId()) {
                        jMenuItemSofgceActionPerformed(null);
                    }
                } catch (Exception ex) {
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        });

        mnuMenu.add(Box.createHorizontalGlue());
        mnuMenu.add(txtBusca);
    }

    @Override
    public void atualizarJanela() throws Exception {
        //verifica janela selecionada
        VRInternalFrame oInternalFrame = null;

        for (int i = 0; i < this.desktopPane.getComponentCount(); i++) {
            Component oComponente = this.desktopPane.getComponent(i);

            if (oComponente instanceof VRInternalFrame) {
                oInternalFrame = (VRInternalFrame) oComponente;

                if (oInternalFrame.isSelected()) {
                    break;
                }
            }
        }

        //recria menu janela
        mnuJanela.removeAll();

        for (int i = 0; i < vJanela.size(); i++) {
            VRInternalFrame oJanela = vJanela.get(i);

            JCheckBoxMenuItem mnuItem = new JCheckBoxMenuItem(oJanela.getTitle());
            mnuJanela.add(mnuItem);

            if (oJanela.equals(oInternalFrame)) {
                mnuItem.setSelected(true);
            }

            mnuItem.setActionCommand(String.valueOf(i));

            mnuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int i = Integer.parseInt(e.getActionCommand());
                        vJanela.get(i).setSelected(true);

                        ((JCheckBoxMenuItem) e.getSource()).setSelected(true);
                    } catch (NumberFormatException | PropertyVetoException ex) {
                        Util.exibirMensagemErro(ex, null);
                    }
                }
            });
        }

        //resetar janela
        if (vJanela.size() > 0) {
            mnuJanela.add(new JSeparator());
        }

        JMenuItem mnuResetarJanela = new JMenuItem("Resetar Janelas");
        mnuJanela.add(mnuResetarJanela);

        mnuResetarJanela.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Util.exibirMensagemContinuar("Todas as janelas voltarão ao seu formato padrão!", getTitle());

                    Arquivo arq = new Arquivo(Util.getRoot() + Util.FILE_LOG_GUI, "w");
                    arq.close();

                    Util.exibirMensagem("Resetado com sucesso!", getTitle());

                } catch (Exception ex) {
                }
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tlbToolBar = new javax.swing.JToolBar();
        tlbAtalho = new vrframework.bean.toolBar.VRToolBar();
        tlbFixo = new vrframework.bean.toolBar.VRToolBar();
        btnSair = new javax.swing.JButton();
        btnMemo = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        vrDesktopPane = new vrframework.bean.desktopPane.VRDesktopPane();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        lblUsuario = new vrframework.bean.label.VRLabel();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        lblVersao = new vrframework.bean.label.VRLabel();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        lblUnidade = new vrframework.bean.label.VRLabel();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        lblData = new vrframework.bean.label.VRLabel();
        mnuMenu = new javax.swing.JMenuBar();
        mnuCadastro = new javax.swing.JMenu();
        mnuLoja = new javax.swing.JMenuItem();
        mnuAjuda1 = new javax.swing.JMenu();
        mnuCadastro2_5 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        mnuCadastros = new javax.swing.JMenu();
        mnuMapeamentoDeLojas = new javax.swing.JMenuItem();
        mnuInterface = new javax.swing.JMenu();
        mnuImpSistema = new javax.swing.JMenu();
        mnuDatabase = new javax.swing.JMenu();
        mnuAccess = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        mnuBase = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        mnuOryon = new javax.swing.JMenuItem();
        mnuStock = new javax.swing.JMenuItem();
        mnuW2A = new javax.swing.JMenuItem();
        mnuADS = new javax.swing.JMenu();
        mnuInfoMacStore = new javax.swing.JMenuItem();
        mnuCache = new javax.swing.JMenu();
        jMenuItemSIMS = new javax.swing.JMenuItem();
        mnuOrion = new javax.swing.JMenu();
        mnuAdmRioPreto = new javax.swing.JMenuItem();
        mnuDJSystem = new javax.swing.JMenuItem();
        mnuDtCom = new javax.swing.JMenuItem();
        mnuEsSystem = new javax.swing.JMenuItem();
        mnuFG = new javax.swing.JMenuItem();
        mnuJacsys = new javax.swing.JMenuItem();
        mnuMSIInfor = new javax.swing.JMenuItem();
        jMenuItemOrion = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItemSics = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        mnuSiaCriareDbf = new javax.swing.JMenuItem();
        mnuVCash = new javax.swing.JMenuItem();
        mnuFirebird = new javax.swing.JMenu();
        jMenuItemActive = new javax.swing.JMenuItem();
        mnuASoft = new javax.swing.JMenuItem();
        mnuCadastraFacil = new javax.swing.JMenuItem();
        jMenuItemCGA = new javax.swing.JMenuItem();
        mnuControll = new javax.swing.JMenuItem();
        mnuCplus = new javax.swing.JMenuItem();
        mnuDelfi = new javax.swing.JMenuItem();
        jMenuItemDestro = new javax.swing.JMenuItem();
        mnuFacilite = new javax.swing.JMenuItem();
        mnuFenix = new javax.swing.JMenuItem();
        mnuFort = new javax.swing.JMenuItem();
        jMenuItemGdoor = new javax.swing.JMenuItem();
        jMenuItemIdeal = new javax.swing.JMenuItem();
        jMenuItemInfoBrasil = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        mnuIntellicon = new javax.swing.JMenuItem();
        mnuInteragem = new javax.swing.JMenuItem();
        jMenuItemMobility = new javax.swing.JMenuItem();
        mnuPws = new javax.swing.JMenuItem();
        mnuRepleis = new javax.swing.JMenuItem();
        mnuSaac = new javax.swing.JMenuItem();
        mnuScef = new javax.swing.JMenuItem();
        mnuSDInformatica = new javax.swing.JMenuItem();
        jMenuItemSHI = new javax.swing.JMenuItem();
        mnuSigma = new javax.swing.JMenuItem();
        jMenuItemSoftaEx = new javax.swing.JMenuItem();
        mnuSolidus = new javax.swing.JMenuItem();
        mnuSri = new javax.swing.JMenuItem();
        mnuSuper = new javax.swing.JMenuItem();
        jMenuItemSysPDV = new javax.swing.JMenuItem();
        mnuSysmoFirebird = new javax.swing.JMenuItem();
        jMenuItemWisaSoft = new javax.swing.JMenuItem();
        mnuFirebird2 = new javax.swing.JMenu();
        mnuAlphaSys = new javax.swing.JMenuItem();
        mnuAtenas = new javax.swing.JMenuItem();
        mnuAtmaFirebird = new javax.swing.JMenuItem();
        mnuAutoADM = new javax.swing.JMenuItem();
        mnuAutomaq = new javax.swing.JMenuItem();
        mnuCerebro = new javax.swing.JMenuItem();
        mnuCFSoftSiaECF = new javax.swing.JMenuItem();
        mnuEtica = new javax.swing.JMenuItem();
        mnuFlash = new javax.swing.JMenuItem();
        mnuGDI = new javax.swing.JMenuItem();
        mnuGDoor = new javax.swing.JMenuItem();
        mnuInterData = new javax.swing.JMenuItem();
        mnuLiderNetWork = new javax.swing.JMenuItem();
        mnuLiteci = new javax.swing.JMenuItem();
        mnuMaster = new javax.swing.JMenuItem();
        mnuThotau = new javax.swing.JMenuItem();
        mnuPwGestor = new javax.swing.JMenuItem();
        mnuRKSoftware = new javax.swing.JMenuItem();
        mnuiSircom = new javax.swing.JMenuItem();
        mnuSolido = new javax.swing.JMenuItem();
        mnuSolutionSupera = new javax.swing.JMenuItem();
        mnuiSophyx = new javax.swing.JMenuItem();
        mnuSyncTech = new javax.swing.JMenuItem();
        mnuTecnosoft = new javax.swing.JMenuItem();
        mnuTga = new javax.swing.JMenuItem();
        mnuUpForti = new javax.swing.JMenuItem();
        mnuZpf = new javax.swing.JMenuItem();
        mnuiWeber = new javax.swing.JMenuItem();
        mnuFirebird3 = new javax.swing.JMenu();
        mnuAutocom = new javax.swing.JMenuItem();
        mnuAutoMac = new javax.swing.JMenuItem();
        mnuDSoft = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuFutura = new javax.swing.JMenuItem();
        mnuGCom = new javax.swing.JMenuItem();
        chkGigatron = new javax.swing.JMenuItem();
        chkLogicBox = new javax.swing.JMenuItem();
        mnuMercaLite = new javax.swing.JMenuItem();
        mnuMSuper = new javax.swing.JMenuItem();
        mnuNeo = new javax.swing.JMenuItem();
        mnuPlenus = new javax.swing.JMenuItem();
        chkSGMaster = new javax.swing.JMenuItem();
        mnuAutoMac1 = new javax.swing.JMenuItem();
        mnuDB2 = new javax.swing.JMenu();
        jMenuItemCISS = new javax.swing.JMenuItem();
        mnuInformix = new javax.swing.JMenu();
        mnuiLogus = new javax.swing.JMenuItem();
        mnuMySQL = new javax.swing.JMenu();
        mnuUmPontoDois = new javax.swing.JMenuItem();
        mnuAvance = new javax.swing.JMenuItem();
        mnuClick = new javax.swing.JMenuItem();
        mnuEmporio = new javax.swing.JMenuItem();
        jMenuItemEverast = new javax.swing.JMenuItem();
        jMenuItemFMSistemas = new javax.swing.JMenuItem();
        jMenuItemGR7 = new javax.swing.JMenuItem();
        mnuGTech = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        mnuHipcom = new javax.swing.JMenuItem();
        mnuIQSistemas = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        mnuOpen = new javax.swing.JMenuItem();
        mnuRCNet = new javax.swing.JMenuItem();
        jMenuItemSacLumi = new javax.swing.JMenuItem();
        mnuSatecfe = new javax.swing.JMenuItem();
        jMenuItemSci = new javax.swing.JMenuItem();
        mnuSifat = new javax.swing.JMenuItem();
        mnuSuperLoja10 = new javax.swing.JMenuItem();
        jMenuItemTiTecnologia = new javax.swing.JMenuItem();
        jMenuItemTopSystem = new javax.swing.JMenuItem();
        mnuTsti = new javax.swing.JMenuItem();
        mnuMySQL2 = new javax.swing.JMenu();
        mnuDLink = new javax.swing.JMenuItem();
        mnuExodus = new javax.swing.JMenuItem();
        mnuG3 = new javax.swing.JMenuItem();
        mnuGestorPDV = new javax.swing.JMenuItem();
        mnuIServer = new javax.swing.JMenuItem();
        mnuLinear = new javax.swing.JMenuItem();
        mnuMobnePdv = new javax.swing.JMenuItem();
        mnuPlenoKW = new javax.swing.JMenuItem();
        mnuG4 = new javax.swing.JMenuItem();
        mnuSiit = new javax.swing.JMenuItem();
        mnuSTI = new javax.swing.JMenuItem();
        mnuSTI3 = new javax.swing.JMenuItem();
        mnuEptus = new javax.swing.JMenuItem();
        mnuLCSistema = new javax.swing.JMenuItem();
        mnuOracle = new javax.swing.JMenu();
        mnuApollo = new javax.swing.JMenuItem();
        jMenuItemArius = new javax.swing.JMenuItem();
        mnuCefas = new javax.swing.JMenuItem();
        jMenuItemConcretize = new javax.swing.JMenuItem();
        mnuCPGestor = new javax.swing.JMenuItem();
        jMenuItemCupermax = new javax.swing.JMenuItem();
        jMenuItemGCF = new javax.swing.JMenuItem();
        mnuGondola = new javax.swing.JMenuItem();
        mnuLinner = new javax.swing.JMenuItem();
        mnuProton = new javax.swing.JMenuItem();
        jMenuItemRMS = new javax.swing.JMenuItem();
        mnuRMS_2 = new javax.swing.JMenuItem();
        mnuWmsi1 = new javax.swing.JMenuItem();
        mnuSicom = new javax.swing.JMenuItem();
        mnuSTSistemas = new javax.swing.JMenuItem();
        jMenuItemSuperus = new javax.swing.JMenuItem();
        mnuViaSoft = new javax.swing.JMenuItem();
        mnuWinthor_PcSistemas = new javax.swing.JMenuItem();
        mnuWmsi = new javax.swing.JMenuItem();
        mnuParadox = new javax.swing.JMenu();
        mnuBrainSoft = new javax.swing.JMenuItem();
        mnuFarm2000 = new javax.swing.JMenuItem();
        mnuForti = new javax.swing.JMenuItem();
        mnuLogus = new javax.swing.JMenuItem();
        mnuSic = new javax.swing.JMenuItem();
        mnuPlanilhaEspecifica = new javax.swing.JMenu();
        mnuGeneric = new javax.swing.JMenuItem();
        mnuMerceariaSene = new javax.swing.JMenuItem();
        mnuSambaNetGetWay = new javax.swing.JMenuItem();
        mnuVarejoFacil = new javax.swing.JMenuItem();
        mnuPhoenix = new javax.swing.JMenuItem();
        mnuDTCom = new javax.swing.JMenuItem();
        mnuPostgres = new javax.swing.JMenu();
        mnuAlterDataWShop = new javax.swing.JMenuItem();
        mnuAutoSystem = new javax.swing.JMenuItem();
        mnuBrajanGestores = new javax.swing.JMenuItem();
        jMenuItemControlWare = new javax.swing.JMenuItem();
        mnuCronos20 = new javax.swing.JMenuItem();
        mnuDevMaster = new javax.swing.JMenuItem();
        mnuFlatan = new javax.swing.JMenuItem();
        mnuG10 = new javax.swing.JMenuItem();
        mnuMrs2 = new javax.swing.JMenuItem();
        mnuJrf = new javax.swing.JMenuItem();
        mnuLogTec = new javax.swing.JMenuItem();
        mnuLyncis = new javax.swing.JMenuItem();
        mnuMarket = new javax.swing.JMenuItem();
        mnuMrs = new javax.swing.JMenuItem();
        mnuNCA = new javax.swing.JMenuItem();
        mnuRPInfo = new javax.swing.JMenuItem();
        mnuAutoSystem1 = new javax.swing.JMenuItem();
        mnuSofttech = new javax.swing.JMenuItem();
        mnuSysmoPostgres = new javax.swing.JMenuItem();
        mnuUniplus = new javax.swing.JMenuItem();
        mnuVRToVR = new javax.swing.JMenuItem();
        mnuMrs1 = new javax.swing.JMenuItem();
        mnuWebsac = new javax.swing.JMenuItem();
        mnuPostgres2 = new javax.swing.JMenu();
        mnuAthos = new javax.swing.JMenuItem();
        mnuCervantes = new javax.swing.JMenuItem();
        mnuControlePlus = new javax.swing.JMenuItem();
        mnuPrime = new javax.swing.JMenuItem();
        mnuRMSistemas = new javax.swing.JMenuItem();
        mnuViggo = new javax.swing.JMenuItem();
        mnuSuperControle = new javax.swing.JMenuItem();
        mnuStockPostgres = new javax.swing.JMenuItem();
        mnuSQLite = new javax.swing.JMenu();
        mnuSQLiteSophyx = new javax.swing.JMenuItem();
        mnuSQLServer = new javax.swing.JMenu();
        mnuAsefe = new javax.swing.JMenuItem();
        jMenuItemBoechatSoft = new javax.swing.JMenuItem();
        mnuFabTech1 = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        mmnuDGCom = new javax.swing.JMenuItem();
        jMenuItemDirector = new javax.swing.JMenuItem();
        jMenuItemEcosInformatica = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        mmnuGestora = new javax.swing.JMenuItem();
        jMenuItemGetWay = new javax.swing.JMenuItem();
        jMenuItemGuiaSistemas = new javax.swing.JMenuItem();
        mnuHipicom = new javax.swing.JMenuItem();
        mnuIcommerce = new javax.swing.JMenuItem();
        mnuIdealSoft = new javax.swing.JMenuItem();
        mnuInventer = new javax.swing.JMenuItem();
        mnuJM2Online = new javax.swing.JMenuItem();
        jMenuItemJMaster = new javax.swing.JMenuItem();
        jMenuItemKairos = new javax.swing.JMenuItem();
        jMenuItemMilenio = new javax.swing.JMenuItem();
        mnuFabTech = new javax.swing.JMenuItem();
        mnuSambaNet = new javax.swing.JMenuItem();
        jMenuItemSBOMarket = new javax.swing.JMenuItem();
        jMenuItemSimSoft = new javax.swing.JMenuItem();
        jMenuItemSysMoura = new javax.swing.JMenuItem();
        jMenuItemSofgce = new javax.swing.JMenuItem();
        mnuSoftcom = new javax.swing.JMenuItem();
        jMenuItemSuperServer = new javax.swing.JMenuItem();
        jMenuItemSysPDVSQLServer = new javax.swing.JMenuItem();
        jMenuItemUltraSyst = new javax.swing.JMenuItem();
        mnuVisualComercio = new javax.swing.JMenuItem();
        mnuSQLServer2 = new javax.swing.JMenu();
        mnuAccesys = new javax.swing.JMenuItem();
        mnuAcom = new javax.swing.JMenuItem();
        mnuArtSystem = new javax.swing.JMenuItem();
        mnuAtenasSQLServer = new javax.swing.JMenuItem();
        mnuAtma = new javax.swing.JMenuItem();
        mnuControlX = new javax.swing.JMenuItem();
        mnuDataSync = new javax.swing.JMenuItem();
        mnuDirector = new javax.swing.JMenuItem();
        mnuEasySac = new javax.swing.JMenuItem();
        mnuFHOnline = new javax.swing.JMenuItem();
        mnuHercules = new javax.swing.JMenuItem();
        mnuHiper = new javax.swing.JMenuItem();
        mnuHRTechV1 = new javax.swing.JMenuItem();
        mnuHRTechV2 = new javax.swing.JMenuItem();
        mnuKcms = new javax.swing.JMenuItem();
        mnuLince = new javax.swing.JMenuItem();
        mnuNATISistemas = new javax.swing.JMenuItem();
        mnuPhixa = new javax.swing.JMenuItem();
        mnuRensoftware = new javax.swing.JMenuItem();
        mnuSaef = new javax.swing.JMenuItem();
        mnuSav = new javax.swing.JMenuItem();
        mnuAccesys1 = new javax.swing.JMenuItem();
        mnuiSysERP = new javax.swing.JMenuItem();
        mnuTpaRootac = new javax.swing.JMenuItem();
        mnuVisualMix = new javax.swing.JMenuItem();
        mnuWinNexus = new javax.swing.JMenuItem();
        mnuSQLServer3 = new javax.swing.JMenu();
        mnuMRC6 = new javax.swing.JMenuItem();
        mnuPoligon = new javax.swing.JMenuItem();
        mnuSBOnline = new javax.swing.JMenuItem();
        mnuSTSistemas_v2 = new javax.swing.JMenuItem();
        mnuSysAut = new javax.swing.JMenuItem();
        mnuTelecon = new javax.swing.JMenuItem();
        mnuFile = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        mnuHostMundo = new javax.swing.JMenuItem();
        mnuWmByFile = new javax.swing.JMenuItem();
        mnuSiaCriareByFile = new javax.swing.JMenuItem();
        mnuAlteracaoID = new javax.swing.JMenu();
        jMenuItem29 = new javax.swing.JMenuItem();
        mnuPlanilhaV2 = new javax.swing.JMenuItem();
        mnuLogVenda = new javax.swing.JMenuItem();
        mnuNFe = new javax.swing.JMenuItem();
        mnuCodigoBarrasAtacado = new javax.swing.JMenuItem();
        mnuSambanet = new javax.swing.JMenuItem();
        mnuImportarNfce = new javax.swing.JMenuItem();
        mnuEspeciais = new javax.swing.JMenu();
        mnupdvvendaitem = new javax.swing.JMenuItem();
        mnuPlanilhaVr = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        mnuAcertarIdsProdutos = new javax.swing.JMenuItem();
        mnuContaPagar = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        copiaEntreLojas = new javax.swing.JMenuItem();
        copiarPromocoes = new javax.swing.JMenuItem();
        mnuVRPdv = new javax.swing.JMenuItem();
        mnuFerramentas = new javax.swing.JMenu();
        mnuEditarConexoes = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuAvancadas = new javax.swing.JMenu();
        mnuImportarNCM = new javax.swing.JMenuItem();
        mnuParametros = new javax.swing.JMenuItem();
        mnuParametros1 = new javax.swing.JMenuItem();
        mnuDelRegistro = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        mnuCorrecaoImpostosDSoft = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        mnuRelatorios = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        gerarCodant = new javax.swing.JMenuItem();
        gerarBackup = new javax.swing.JMenuItem();
        zerarEstoque = new javax.swing.JMenuItem();
        mnuPlanilha = new javax.swing.JMenu();
        mnuPlanilhaProduto = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        mnuJanela = new javax.swing.JMenu();
        mnuAjuda = new javax.swing.JMenu();
        jSeparator4 = new javax.swing.JSeparator();
        mnuAjudaSobre = new javax.swing.JMenuItem();
        mnuSistema = new javax.swing.JMenu();
        mnuSistemaLogin = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("VR Implantação"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tlbToolBar.setFloatable(false);
        tlbToolBar.setRollover(true);

        tlbAtalho.setRollover(true);
        tlbToolBar.add(tlbAtalho);

        tlbFixo.setRollover(true);

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair20.png"))); // NOI18N
        btnSair.setToolTipText("Sair (Alt + F4)");
        btnSair.setFocusable(false);
        btnSair.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSair.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });
        tlbFixo.add(btnSair);

        btnMemo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/cupom.png"))); // NOI18N
        btnMemo.setToolTipText("Sair (Alt + F4)");
        btnMemo.setFocusable(false);
        btnMemo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMemo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnMemo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMemoActionPerformed(evt);
            }
        });
        tlbFixo.add(btnMemo);

        tlbToolBar.add(tlbFixo);
        tlbToolBar.add(jSeparator3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(tlbToolBar, gridBagConstraints);

        vrDesktopPane.setBackground(java.awt.SystemColor.menu);
        vrDesktopPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        getContentPane().add(vrDesktopPane, gridBagConstraints);

        vRPanel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblUsuario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsuario.setText("@@@@@@@@");

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 60.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        getContentPane().add(vRPanel5, gridBagConstraints);

        vRPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblVersao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVersao.setText("@@@@");

        javax.swing.GroupLayout vRPanel6Layout = new javax.swing.GroupLayout(vRPanel6);
        vRPanel6.setLayout(vRPanel6Layout);
        vRPanel6Layout.setHorizontalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblVersao, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel6Layout.setVerticalGroup(
            vRPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblVersao, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        getContentPane().add(vRPanel6, gridBagConstraints);

        vRPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblUnidade.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUnidade.setText("@@@");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUnidade, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblUnidade, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        getContentPane().add(vRPanel3, gridBagConstraints);

        vRPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        lblData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblData.setText("@@@@@@");
        lblData.setMaximumSize(new java.awt.Dimension(40, 40));
        lblData.setMinimumSize(new java.awt.Dimension(40, 20));

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblData, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblData, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 1, 2);
        getContentPane().add(vRPanel2, gridBagConstraints);

        mnuMenu.setAlignmentY(1.0F);

        mnuCadastro.setText("Cadastro");

        mnuLoja.setText("Loja");
        mnuLoja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLojaActionPerformed(evt);
            }
        });
        mnuCadastro.add(mnuLoja);

        mnuMenu.add(mnuCadastro);

        mnuAjuda1.setText("Importação 2.5");

        mnuCadastro2_5.setText("Cadastro");

        jMenuItem7.setText("Banco de Dados");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        mnuCadastro2_5.add(jMenuItem7);

        jMenuItem9.setText("Sistemas");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        mnuCadastro2_5.add(jMenuItem9);

        jMenuItem13.setText("Sistema x Banco Dados");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        mnuCadastro2_5.add(jMenuItem13);

        jMenuItem15.setText("Unidades VR");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        mnuCadastro2_5.add(jMenuItem15);

        jMenuItem16.setText("Usuários VRImplantacao");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        mnuCadastro2_5.add(jMenuItem16);

        mnuAjuda1.add(mnuCadastro2_5);

        mnuCadastros.setText("Configuração");

        mnuMapeamentoDeLojas.setText("Configuração de Base de Dados");
        mnuMapeamentoDeLojas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMapeamentoDeLojasActionPerformed(evt);
            }
        });
        mnuCadastros.add(mnuMapeamentoDeLojas);

        mnuAjuda1.add(mnuCadastros);

        mnuMenu.add(mnuAjuda1);

        mnuInterface.setText("Interface");

        mnuImpSistema.setText("Importação");

        mnuDatabase.setText("Sistemas");
        mnuDatabase.setEnabled(false);

        mnuAccess.setText("Access");

        jMenuItem25.setText("Alfa Software");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        mnuAccess.add(jMenuItem25);

        mnuBase.setText("Base");
        mnuBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBaseActionPerformed(evt);
            }
        });
        mnuAccess.add(mnuBase);

        jMenuItem5.setText("LB Software");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        mnuAccess.add(jMenuItem5);

        mnuOryon.setText("Oryon");
        mnuOryon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOryonActionPerformed(evt);
            }
        });
        mnuAccess.add(mnuOryon);

        mnuStock.setText("Stock");
        mnuStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuStockActionPerformed(evt);
            }
        });
        mnuAccess.add(mnuStock);

        mnuW2A.setText("W2A Brasil");
        mnuW2A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuW2AActionPerformed(evt);
            }
        });
        mnuAccess.add(mnuW2A);

        mnuDatabase.add(mnuAccess);

        mnuADS.setText("ADS");

        mnuInfoMacStore.setText("InfoMac - Store");
        mnuInfoMacStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInfoMacStoreActionPerformed(evt);
            }
        });
        mnuADS.add(mnuInfoMacStore);

        mnuDatabase.add(mnuADS);

        mnuCache.setText("Caché");

        jMenuItemSIMS.setText("SIMS");
        jMenuItemSIMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSIMSActionPerformed(evt);
            }
        });
        mnuCache.add(jMenuItemSIMS);

        mnuDatabase.add(mnuCache);

        mnuOrion.setText("DBF");

        mnuAdmRioPreto.setText("ADM (Rio Preto)");
        mnuAdmRioPreto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAdmRioPretoActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuAdmRioPreto);

        mnuDJSystem.setText("DJ System");
        mnuDJSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDJSystemActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuDJSystem);

        mnuDtCom.setText("DtCom");
        mnuDtCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDtComActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuDtCom);

        mnuEsSystem.setText("ES System");
        mnuEsSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEsSystemActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuEsSystem);

        mnuFG.setText("FG");
        mnuFG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFGActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuFG);

        mnuJacsys.setText("Jacsys");
        mnuJacsys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJacsysActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuJacsys);

        mnuMSIInfor.setText("MSI Infor");
        mnuMSIInfor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMSIInforActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuMSIInfor);

        jMenuItemOrion.setText("Orion");
        jMenuItemOrion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOrionActionPerformed(evt);
            }
        });
        mnuOrion.add(jMenuItemOrion);

        jMenuItem3.setText("Rootac");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        mnuOrion.add(jMenuItem3);

        jMenuItemSics.setText("SICS");
        jMenuItemSics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSicsActionPerformed(evt);
            }
        });
        mnuOrion.add(jMenuItemSics);

        jMenuItem23.setText("SoftClass/AutoCom");
        jMenuItem23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem23ActionPerformed(evt);
            }
        });
        mnuOrion.add(jMenuItem23);

        mnuSiaCriareDbf.setText("SiaCriare (versão Dbf)");
        mnuSiaCriareDbf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSiaCriareDbfActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuSiaCriareDbf);

        mnuVCash.setText("VCash");
        mnuVCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVCashActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuVCash);

        mnuDatabase.add(mnuOrion);

        mnuFirebird.setText("Firebird / Interbase");

        jMenuItemActive.setText("Active");
        jMenuItemActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemActiveActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemActive);

        mnuASoft.setText("ASoft");
        mnuASoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuASoftActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuASoft);

        mnuCadastraFacil.setText("CadastraFacil");
        mnuCadastraFacil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCadastraFacilActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuCadastraFacil);

        jMenuItemCGA.setText("CGA");
        jMenuItemCGA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCGAActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemCGA);

        mnuControll.setText("Controll");
        mnuControll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuControllActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuControll);

        mnuCplus.setText("CPlus");
        mnuCplus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCplusActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuCplus);

        mnuDelfi.setText("Delfi");
        mnuDelfi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDelfiActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuDelfi);

        jMenuItemDestro.setText("Destro");
        jMenuItemDestro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDestroActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemDestro);

        mnuFacilite.setText("FACILITE");
        mnuFacilite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFaciliteActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuFacilite);

        mnuFenix.setText("Fenix (POINTER)");
        mnuFenix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFenixActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuFenix);

        mnuFort.setText("Fort (Firebird)");
        mnuFort.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFortActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuFort);

        jMenuItemGdoor.setText("GDOOR");
        jMenuItemGdoor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGdoorActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemGdoor);

        jMenuItemIdeal.setText("Ideal");
        jMenuItemIdeal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIdealActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemIdeal);

        jMenuItemInfoBrasil.setText("InfoBrasil");
        jMenuItemInfoBrasil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInfoBrasilActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemInfoBrasil);

        jMenuItem2.setText("IntelliCash");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItem2);

        mnuIntellicon.setText("Intellicon");
        mnuIntellicon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIntelliconActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuIntellicon);

        mnuInteragem.setText("Interage");
        mnuInteragem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInteragemActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuInteragem);

        jMenuItemMobility.setText("Mobility");
        jMenuItemMobility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMobilityActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemMobility);

        mnuPws.setText("PWS (POINTER)");
        mnuPws.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPwsActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuPws);

        mnuRepleis.setText("Répleis");
        mnuRepleis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRepleisActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuRepleis);

        mnuSaac.setText("Saac");
        mnuSaac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaacActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSaac);

        mnuScef.setText("Scef");
        mnuScef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuScefActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuScef);

        mnuSDInformatica.setText("SD Informática");
        mnuSDInformatica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSDInformaticaActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSDInformatica);

        jMenuItemSHI.setText("SHI");
        jMenuItemSHI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSHIActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemSHI);

        mnuSigma.setText("Sigma");
        mnuSigma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSigmaActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSigma);

        jMenuItemSoftaEx.setText("SoftaEx");
        jMenuItemSoftaEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSoftaExActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemSoftaEx);

        mnuSolidus.setText("Solidus");
        mnuSolidus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSolidusActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSolidus);

        mnuSri.setText("SRI");
        mnuSri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSriActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSri);

        mnuSuper.setText("Super");
        mnuSuper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSuperActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSuper);

        jMenuItemSysPDV.setText("SysPDV");
        jMenuItemSysPDV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysPDVActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemSysPDV);

        mnuSysmoFirebird.setText("Sysmo");
        mnuSysmoFirebird.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSysmoFirebirdActionPerformed(evt);
            }
        });
        mnuFirebird.add(mnuSysmoFirebird);

        jMenuItemWisaSoft.setText("WisaSoft");
        jMenuItemWisaSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWisaSoftActionPerformed(evt);
            }
        });
        mnuFirebird.add(jMenuItemWisaSoft);

        mnuDatabase.add(mnuFirebird);

        mnuFirebird2.setText("Firebird / Interbase 2");

        mnuAlphaSys.setText("AlphaSys");
        mnuAlphaSys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAlphaSysActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuAlphaSys);

        mnuAtenas.setText("Atenas");
        mnuAtenas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAtenasActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuAtenas);

        mnuAtmaFirebird.setText("ATMA (Firebird)");
        mnuAtmaFirebird.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAtmaFirebirdActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuAtmaFirebird);

        mnuAutoADM.setText("AutoADM");
        mnuAutoADM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoADMActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuAutoADM);

        mnuAutomaq.setText("Automaq");
        mnuAutomaq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutomaqActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuAutomaq);

        mnuCerebro.setText("Cerebro/SCOL");
        mnuCerebro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCerebroActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuCerebro);

        mnuCFSoftSiaECF.setText("CFSoft/SiaECF");
        mnuCFSoftSiaECF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCFSoftSiaECFActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuCFSoftSiaECF);

        mnuEtica.setText("Etica");
        mnuEtica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEticaActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuEtica);

        mnuFlash.setText("Flash");
        mnuFlash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFlashActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuFlash);

        mnuGDI.setText("GDI");
        mnuGDI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGDIActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuGDI);

        mnuGDoor.setText("GDoor");
        mnuGDoor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGDoorActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuGDoor);

        mnuInterData.setText("InterData");
        mnuInterData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInterDataActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuInterData);

        mnuLiderNetWork.setText("LiderNetWork");
        mnuLiderNetWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLiderNetWorkActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuLiderNetWork);

        mnuLiteci.setText("Liteci");
        mnuLiteci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLiteciActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuLiteci);

        mnuMaster.setText("Master");
        mnuMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMasterActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuMaster);

        mnuThotau.setText("OrionTech (Thotau)");
        mnuThotau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuThotauActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuThotau);

        mnuPwGestor.setText("PwGestor");
        mnuPwGestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPwGestorActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuPwGestor);

        mnuRKSoftware.setText("RK Software");
        mnuRKSoftware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRKSoftwareActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuRKSoftware);

        mnuiSircom.setText("Sircom");
        mnuiSircom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuiSircomActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuiSircom);

        mnuSolido.setText("Solido");
        mnuSolido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSolidoActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuSolido);

        mnuSolutionSupera.setText("Solution Supera");
        mnuSolutionSupera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSolutionSuperaActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuSolutionSupera);

        mnuiSophyx.setText("Sophyx");
        mnuiSophyx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuiSophyxActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuiSophyx);

        mnuSyncTech.setText("SyncTech");
        mnuSyncTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSyncTechActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuSyncTech);

        mnuTecnosoft.setText("Tecnosoft");
        mnuTecnosoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTecnosoftActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuTecnosoft);

        mnuTga.setText("TGA");
        mnuTga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTgaActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuTga);

        mnuUpForti.setText("UpForti");
        mnuUpForti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUpFortiActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuUpForti);

        mnuZpf.setText("ZPF Sistema");
        mnuZpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuZpfActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuZpf);

        mnuiWeber.setText("Weber");
        mnuiWeber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuiWeberActionPerformed(evt);
            }
        });
        mnuFirebird2.add(mnuiWeber);

        mnuDatabase.add(mnuFirebird2);

        mnuFirebird3.setText("Firebird / Interbase 3");

        mnuAutocom.setText("Autocom");
        mnuAutocom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutocomActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuAutocom);

        mnuAutoMac.setText("AutoMac");
        mnuAutoMac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoMacActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuAutoMac);

        mnuDSoft.setText("DSoft");
        mnuDSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDSoftActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuDSoft);

        jMenuItem1.setText("Eco Centauro");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuFirebird3.add(jMenuItem1);

        mnuFutura.setText("Futura");
        mnuFutura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFuturaActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuFutura);

        mnuGCom.setText("GCom");
        mnuGCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGComActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuGCom);

        chkGigatron.setText("Gigatron");
        chkGigatron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkGigatronActionPerformed(evt);
            }
        });
        mnuFirebird3.add(chkGigatron);

        chkLogicBox.setText("LogicBox");
        chkLogicBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkLogicBoxActionPerformed(evt);
            }
        });
        mnuFirebird3.add(chkLogicBox);

        mnuMercaLite.setText("MercaLite");
        mnuMercaLite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMercaLiteActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuMercaLite);

        mnuMSuper.setText("MSuper");
        mnuMSuper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMSuperActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuMSuper);

        mnuNeo.setText("Neo");
        mnuNeo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNeoActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuNeo);

        mnuPlenus.setText("Plenus");
        mnuPlenus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlenusActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuPlenus);

        chkSGMaster.setText("SG Master");
        chkSGMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSGMasterActionPerformed(evt);
            }
        });
        mnuFirebird3.add(chkSGMaster);

        mnuAutoMac1.setText("AutoMac");
        mnuAutoMac1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoMacActionPerformed(evt);
            }
        });
        mnuFirebird3.add(mnuAutoMac1);

        mnuDatabase.add(mnuFirebird3);

        mnuDB2.setText("IBM DB2");

        jMenuItemCISS.setText("CISS");
        jMenuItemCISS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCISSActionPerformed(evt);
            }
        });
        mnuDB2.add(jMenuItemCISS);

        mnuDatabase.add(mnuDB2);

        mnuInformix.setText("Informix");

        mnuiLogus.setText("Logus Retail");
        mnuiLogus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuiLogusActionPerformed(evt);
            }
        });
        mnuInformix.add(mnuiLogus);

        mnuDatabase.add(mnuInformix);

        mnuMySQL.setText("MySQL");

        mnuUmPontoDois.setText("1.2 Informatica");
        mnuUmPontoDois.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUmPontoDoisActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuUmPontoDois);

        mnuAvance.setText("Avance");
        mnuAvance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAvanceActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuAvance);

        mnuClick.setText("Click");
        mnuClick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClickActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuClick);

        mnuEmporio.setText("Emporio");
        mnuEmporio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEmporioActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuEmporio);

        jMenuItemEverast.setText("Everest");
        jMenuItemEverast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEverastActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemEverast);

        jMenuItemFMSistemas.setText("FM Sistemas");
        jMenuItemFMSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFMSistemasActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemFMSistemas);

        jMenuItemGR7.setText("GR7");
        jMenuItemGR7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGR7ActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemGR7);

        mnuGTech.setText("GTech");
        mnuGTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGTechActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuGTech);

        jMenuItem11.setText("GZ Sistemas");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItem11);

        mnuHipcom.setText("Hipcom");
        mnuHipcom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHipcomActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuHipcom);

        mnuIQSistemas.setText("IQSistemas");
        mnuIQSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIQSistemasActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuIQSistemas);

        jMenuItem4.setText("MRS");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItem4);

        mnuOpen.setText("Open");
        mnuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOpenActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuOpen);

        mnuRCNet.setText("RCNet");
        mnuRCNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRCNetActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuRCNet);

        jMenuItemSacLumi.setText("SAC Lumi");
        jMenuItemSacLumi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSacLumiActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemSacLumi);

        mnuSatecfe.setText("Satecfe");
        mnuSatecfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSatecfeActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuSatecfe);

        jMenuItemSci.setText("Sci");
        jMenuItemSci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSciActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemSci);

        mnuSifat.setText("Sifat");
        mnuSifat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSifatActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuSifat);

        mnuSuperLoja10.setText("Super Loja 10");
        mnuSuperLoja10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSuperLoja10ActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuSuperLoja10);

        jMenuItemTiTecnologia.setText("Ti Tecnologia");
        jMenuItemTiTecnologia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTiTecnologiaActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemTiTecnologia);

        jMenuItemTopSystem.setText("TopSystem");
        jMenuItemTopSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTopSystemActionPerformed(evt);
            }
        });
        mnuMySQL.add(jMenuItemTopSystem);

        mnuTsti.setText("TSTI");
        mnuTsti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTstiActionPerformed(evt);
            }
        });
        mnuMySQL.add(mnuTsti);

        mnuDatabase.add(mnuMySQL);

        mnuMySQL2.setText("MySQL 2");

        mnuDLink.setText("DLink");
        mnuDLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDLinkActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuDLink);

        mnuExodus.setText("Exodus");
        mnuExodus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExodusActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuExodus);

        mnuG3.setText("G3");
        mnuG3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuG3ActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuG3);

        mnuGestorPDV.setText("Gestor PDV");
        mnuGestorPDV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGestorPDVActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuGestorPDV);

        mnuIServer.setText("IServer");
        mnuIServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIServerActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuIServer);

        mnuLinear.setText("Linear");
        mnuLinear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLinearActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuLinear);

        mnuMobnePdv.setText("Mobne (PDV)");
        mnuMobnePdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMobnePdvActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuMobnePdv);

        mnuPlenoKW.setText("Pleno KW");
        mnuPlenoKW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlenoKWActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuPlenoKW);

        mnuG4.setText("Sia Criare (MySQL)");
        mnuG4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuG4ActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuG4);

        mnuSiit.setText("Siit");
        mnuSiit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSiitActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuSiit);

        mnuSTI.setText("STI");
        mnuSTI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSTIActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuSTI);

        mnuSTI3.setText("STI3");
        mnuSTI3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSTI3ActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuSTI3);

        mnuEptus.setText("Eptus");
        mnuEptus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEptusActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuEptus);

        mnuLCSistema.setText("LC Sistema");
        mnuLCSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLCSistemaActionPerformed(evt);
            }
        });
        mnuMySQL2.add(mnuLCSistema);

        mnuDatabase.add(mnuMySQL2);

        mnuOracle.setText("Oracle");

        mnuApollo.setText("Apollo");
        mnuApollo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuApolloActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuApollo);

        jMenuItemArius.setText("Arius");
        jMenuItemArius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAriusActionPerformed(evt);
            }
        });
        mnuOracle.add(jMenuItemArius);

        mnuCefas.setText("Cefas / Concretize V2");
        mnuCefas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCefasActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuCefas);

        jMenuItemConcretize.setText("Concretize");
        jMenuItemConcretize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConcretizeActionPerformed(evt);
            }
        });
        mnuOracle.add(jMenuItemConcretize);

        mnuCPGestor.setText("CPGestor");
        mnuCPGestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCPGestorActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuCPGestor);

        jMenuItemCupermax.setText("Cupermax");
        jMenuItemCupermax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCupermaxActionPerformed(evt);
            }
        });
        mnuOracle.add(jMenuItemCupermax);

        jMenuItemGCF.setText("GCF");
        jMenuItemGCF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGCFActionPerformed(evt);
            }
        });
        mnuOracle.add(jMenuItemGCF);

        mnuGondola.setText("Gondola");
        mnuGondola.setName("Gondola"); // NOI18N
        mnuGondola.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGondolaActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuGondola);

        mnuLinner.setText("Linner");
        mnuLinner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLinnerActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuLinner);

        mnuProton.setText("Proton");
        mnuProton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuProtonActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuProton);

        jMenuItemRMS.setText("RMS");
        jMenuItemRMS.setEnabled(false);
        jMenuItemRMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRMSActionPerformed(evt);
            }
        });
        mnuOracle.add(jMenuItemRMS);

        mnuRMS_2.setText("RMS v2");
        mnuRMS_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRMS_2ActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuRMS_2);

        mnuWmsi1.setText("Siac");
        mnuWmsi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWmsi1ActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuWmsi1);

        mnuSicom.setText("Sicom");
        mnuSicom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSicomActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuSicom);

        mnuSTSistemas.setText("ST Sistemas");
        mnuSTSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSTSistemasActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuSTSistemas);

        jMenuItemSuperus.setText("Superus");
        jMenuItemSuperus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSuperusActionPerformed(evt);
            }
        });
        mnuOracle.add(jMenuItemSuperus);

        mnuViaSoft.setText("Via Soft");
        mnuViaSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViaSoftActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuViaSoft);

        mnuWinthor_PcSistemas.setText("Winthor (PC Sistemas)");
        mnuWinthor_PcSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWinthor_PcSistemasActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuWinthor_PcSistemas);

        mnuWmsi.setText("Wmsi");
        mnuWmsi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWmsiActionPerformed(evt);
            }
        });
        mnuOracle.add(mnuWmsi);

        mnuDatabase.add(mnuOracle);

        mnuParadox.setText("Paradox");

        mnuBrainSoft.setText("BrainSoft");
        mnuBrainSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBrainSoftActionPerformed(evt);
            }
        });
        mnuParadox.add(mnuBrainSoft);

        mnuFarm2000.setText("Farm 2000");
        mnuFarm2000.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFarm2000ActionPerformed(evt);
            }
        });
        mnuParadox.add(mnuFarm2000);

        mnuForti.setText("Forti");
        mnuForti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFortiActionPerformed(evt);
            }
        });
        mnuParadox.add(mnuForti);

        mnuLogus.setText("Logus");
        mnuLogus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLogusActionPerformed(evt);
            }
        });
        mnuParadox.add(mnuLogus);

        mnuSic.setText("Sic");
        mnuSic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSicActionPerformed(evt);
            }
        });
        mnuParadox.add(mnuSic);

        mnuDatabase.add(mnuParadox);

        mnuPlanilhaEspecifica.setText("Planilhas Específicas");

        mnuGeneric.setText("Generic");
        mnuGeneric.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGenericActionPerformed(evt);
            }
        });
        mnuPlanilhaEspecifica.add(mnuGeneric);

        mnuMerceariaSene.setText("Mercearia Sene (cliente)");
        mnuMerceariaSene.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMerceariaSeneActionPerformed(evt);
            }
        });
        mnuPlanilhaEspecifica.add(mnuMerceariaSene);

        mnuSambaNetGetWay.setText("SambaNet");
        mnuSambaNetGetWay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSambaNetGetWayActionPerformed(evt);
            }
        });
        mnuPlanilhaEspecifica.add(mnuSambaNetGetWay);

        mnuVarejoFacil.setText("Varejo Fácil");
        mnuVarejoFacil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVarejoFacilActionPerformed(evt);
            }
        });
        mnuPlanilhaEspecifica.add(mnuVarejoFacil);

        mnuPhoenix.setText("Phoenix");
        mnuPhoenix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPhoenixActionPerformed(evt);
            }
        });
        mnuPlanilhaEspecifica.add(mnuPhoenix);

        mnuDTCom.setText("DTCom");
        mnuDTCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDTComActionPerformed(evt);
            }
        });
        mnuPlanilhaEspecifica.add(mnuDTCom);

        mnuDatabase.add(mnuPlanilhaEspecifica);

        mnuPostgres.setText("PostgreSQL");

        mnuAlterDataWShop.setText("AlterData (WShop)");
        mnuAlterDataWShop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAlterDataWShopActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuAlterDataWShop);

        mnuAutoSystem.setText("AutoSystem");
        mnuAutoSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoSystemActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuAutoSystem);

        mnuBrajanGestores.setText("Brajan/Gestores");
        mnuBrajanGestores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBrajanGestoresActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuBrajanGestores);

        jMenuItemControlWare.setText("ControlWare");
        jMenuItemControlWare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemControlWareActionPerformed(evt);
            }
        });
        mnuPostgres.add(jMenuItemControlWare);

        mnuCronos20.setText("Cronos 20");
        mnuCronos20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCronos20ActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuCronos20);

        mnuDevMaster.setText("DevMaster");
        mnuDevMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDevMasterActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuDevMaster);

        mnuFlatan.setText("Flatan");
        mnuFlatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFlatanActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuFlatan);

        mnuG10.setText("G10");
        mnuG10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuG10ActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuG10);

        mnuMrs2.setText("Inove");
        mnuMrs2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMrs2ActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuMrs2);

        mnuJrf.setText("Jrf");
        mnuJrf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJrfActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuJrf);

        mnuLogTec.setText("LogTEC");
        mnuLogTec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLogTecActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuLogTec);

        mnuLyncis.setText("Lyncis");
        mnuLyncis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLyncisActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuLyncis);

        mnuMarket.setText("Market");
        mnuMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMarketActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuMarket);

        mnuMrs.setText("Mrs");
        mnuMrs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMrsActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuMrs);

        mnuNCA.setLabel("NCA");
        mnuNCA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNCAActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuNCA);

        mnuRPInfo.setText("RPInfo");
        mnuRPInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRPInfoActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuRPInfo);

        mnuAutoSystem1.setText("RMS Compras");
        mnuAutoSystem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoSystem1ActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuAutoSystem1);

        mnuSofttech.setText("Softtech");
        mnuSofttech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSofttechActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuSofttech);

        mnuSysmoPostgres.setText("Sysmo");
        mnuSysmoPostgres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSysmoPostgresActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuSysmoPostgres);

        mnuUniplus.setText("Uniplus");
        mnuUniplus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUniplusActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuUniplus);

        mnuVRToVR.setText("VR para VR");
        mnuVRToVR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVRToVRActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuVRToVR);

        mnuMrs1.setText("Zoombox");
        mnuMrs1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMrs1ActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuMrs1);

        mnuWebsac.setText("WebSac");
        mnuWebsac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWebsacActionPerformed(evt);
            }
        });
        mnuPostgres.add(mnuWebsac);

        mnuDatabase.add(mnuPostgres);

        mnuPostgres2.setText("PostgreSQL 2");

        mnuAthos.setText("Athos");
        mnuAthos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAthosActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuAthos);

        mnuCervantes.setText("Cervantes");
        mnuCervantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCervantesActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuCervantes);

        mnuControlePlus.setText("Controle Plus");
        mnuControlePlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuControlePlusActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuControlePlus);

        mnuPrime.setText("Prime");
        mnuPrime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrimeActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuPrime);

        mnuRMSistemas.setText("ResultMais");
        mnuRMSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRMSistemasActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuRMSistemas);

        mnuViggo.setText("Viggo");
        mnuViggo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuViggoActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuViggo);

        mnuSuperControle.setText("Super Controle");
        mnuSuperControle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSuperControleActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuSuperControle);

        mnuStockPostgres.setText("Stock");
        mnuStockPostgres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuStockPostgresActionPerformed(evt);
            }
        });
        mnuPostgres2.add(mnuStockPostgres);

        mnuDatabase.add(mnuPostgres2);

        mnuSQLite.setText("SQLite");

        mnuSQLiteSophyx.setText("Sophyx");
        mnuSQLiteSophyx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSQLiteSophyxActionPerformed(evt);
            }
        });
        mnuSQLite.add(mnuSQLiteSophyx);

        mnuDatabase.add(mnuSQLite);

        mnuSQLServer.setText("SQL Server");

        mnuAsefe.setText("Asefe");
        mnuAsefe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsefeActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuAsefe);

        jMenuItemBoechatSoft.setText("BoechatSoft");
        jMenuItemBoechatSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBoechatSoftActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemBoechatSoft);

        mnuFabTech1.setText("Costa Azul - Pomares");
        mnuFabTech1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFabTech1ActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuFabTech1);

        jMenuItem19.setText("Datasync/Maximus");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItem19);

        mmnuDGCom.setText("DGCom");
        mmnuDGCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmnuDGComActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mmnuDGCom);

        jMenuItemDirector.setText("Director (OLD)");
        jMenuItemDirector.setEnabled(false);
        jMenuItemDirector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDirectorActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemDirector);

        jMenuItemEcosInformatica.setText("Eccus Informática");
        jMenuItemEcosInformatica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEcosInformaticaActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemEcosInformatica);

        jMenuItem14.setText("Faucom");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItem14);

        mmnuGestora.setText("Gestora");
        mmnuGestora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmnuGestoraActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mmnuGestora);

        jMenuItemGetWay.setText("GetWay");
        jMenuItemGetWay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetWayActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemGetWay);

        jMenuItemGuiaSistemas.setText("Guia Sistemas");
        jMenuItemGuiaSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuiaSistemasActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemGuiaSistemas);

        mnuHipicom.setText("Hipicom");
        mnuHipicom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHipicomActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuHipicom);

        mnuIcommerce.setLabel("ICommerce");
        mnuIcommerce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIcommerceActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuIcommerce);

        mnuIdealSoft.setText("IdealSoft - Shop9");
        mnuIdealSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIdealSoftActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuIdealSoft);

        mnuInventer.setText("Inventer");
        mnuInventer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInventerActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuInventer);

        mnuJM2Online.setText("JM2 Online");
        mnuJM2Online.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJM2OnlineActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuJM2Online);

        jMenuItemJMaster.setText("JMaster");
        jMenuItemJMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemJMasterActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemJMaster);

        jMenuItemKairos.setText("Kairos");
        jMenuItemKairos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemKairosActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemKairos);

        jMenuItemMilenio.setText("Milênio");
        jMenuItemMilenio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMilenioActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemMilenio);

        mnuFabTech.setText("SabTech");
        mnuFabTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFabTechActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuFabTech);

        mnuSambaNet.setText("SambaNet V2");
        mnuSambaNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSambaNetActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuSambaNet);

        jMenuItemSBOMarket.setText("SBOMarket");
        jMenuItemSBOMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSBOMarketActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemSBOMarket);

        jMenuItemSimSoft.setText("SimSoft");
        jMenuItemSimSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSimSoftActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemSimSoft);

        jMenuItemSysMoura.setText("SisMoura");
        jMenuItemSysMoura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysMouraActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemSysMoura);

        jMenuItemSofgce.setText("SOFGCE");
        jMenuItemSofgce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSofgceActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemSofgce);

        mnuSoftcom.setText("Softcom");
        mnuSoftcom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSoftcomActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuSoftcom);

        jMenuItemSuperServer.setText("Super Controler - SuperServer");
        jMenuItemSuperServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSuperServerActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemSuperServer);

        jMenuItemSysPDVSQLServer.setText("SysPDV");
        jMenuItemSysPDVSQLServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysPDVSQLServerActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemSysPDVSQLServer);

        jMenuItemUltraSyst.setText("UltraSyst");
        jMenuItemUltraSyst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUltraSystActionPerformed(evt);
            }
        });
        mnuSQLServer.add(jMenuItemUltraSyst);

        mnuVisualComercio.setText("Visual Comercio");
        mnuVisualComercio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVisualComercioActionPerformed(evt);
            }
        });
        mnuSQLServer.add(mnuVisualComercio);

        mnuDatabase.add(mnuSQLServer);

        mnuSQLServer2.setText("SQL Server 2");

        mnuAccesys.setText("Accesys");
        mnuAccesys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAccesysActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuAccesys);

        mnuAcom.setText("ACOM");
        mnuAcom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAcomActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuAcom);

        mnuArtSystem.setText("ArtSystem");
        mnuArtSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuArtSystemActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuArtSystem);

        mnuAtenasSQLServer.setText("Atenas (SQL Server)");
        mnuAtenasSQLServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAtenasSQLServerActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuAtenasSQLServer);

        mnuAtma.setText("Atma");
        mnuAtma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAtmaActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuAtma);

        mnuControlX.setText("ControlX");
        mnuControlX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuControlXActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuControlX);

        mnuDataSync.setText("DataSync");
        mnuDataSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDataSyncActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuDataSync);

        mnuDirector.setText("Director");
        mnuDirector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDirectorActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuDirector);

        mnuEasySac.setText("EasySac");
        mnuEasySac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEasySacActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuEasySac);

        mnuFHOnline.setText("FHOnline");
        mnuFHOnline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFHOnlineActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuFHOnline);

        mnuHercules.setText("Hercules");
        mnuHercules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHerculesActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuHercules);

        mnuHiper.setText("Hiper");
        mnuHiper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHiperActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuHiper);

        mnuHRTechV1.setText("HRTech(v1)");
        mnuHRTechV1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHRTechV1ActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuHRTechV1);

        mnuHRTechV2.setText("HRTech(v2)");
        mnuHRTechV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHRTechV2ActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuHRTechV2);

        mnuKcms.setText("KCMS");
        mnuKcms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKcmsActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuKcms);

        mnuLince.setText("Lince");
        mnuLince.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLinceActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuLince);

        mnuNATISistemas.setText("NATISistemas");
        mnuNATISistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNATISistemasActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuNATISistemas);

        mnuPhixa.setLabel("Phixa");
        mnuPhixa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPhixaActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuPhixa);

        mnuRensoftware.setText("Rensoftware");
        mnuRensoftware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRensoftwareActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuRensoftware);

        mnuSaef.setText("Saef Sistemas");
        mnuSaef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaefActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuSaef);

        mnuSav.setText("Sav");
        mnuSav.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSavActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuSav);

        mnuAccesys1.setText("SN Sistemas");
        mnuAccesys1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAccesys1ActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuAccesys1);

        mnuiSysERP.setText("SysERP");
        mnuiSysERP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuiSysERPActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuiSysERP);

        mnuTpaRootac.setText("TPA/ROOTAC");
        mnuTpaRootac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTpaRootacActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuTpaRootac);

        mnuVisualMix.setText("Visual Mix");
        mnuVisualMix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVisualMixActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuVisualMix);

        mnuWinNexus.setText("WinNexus");
        mnuWinNexus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWinNexusActionPerformed(evt);
            }
        });
        mnuSQLServer2.add(mnuWinNexus);

        mnuDatabase.add(mnuSQLServer2);

        mnuSQLServer3.setText("SQL Server 3");

        mnuMRC6.setText("MRC6");
        mnuMRC6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMRC6ActionPerformed(evt);
            }
        });
        mnuSQLServer3.add(mnuMRC6);

        mnuPoligon.setText("Poligon");
        mnuPoligon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPoligonActionPerformed(evt);
            }
        });
        mnuSQLServer3.add(mnuPoligon);

        mnuSBOnline.setText("SBOnline");
        mnuSBOnline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSBOnlineActionPerformed(evt);
            }
        });
        mnuSQLServer3.add(mnuSBOnline);

        mnuSTSistemas_v2.setText("ST Sistemas");
        mnuSTSistemas_v2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSTSistemas_v2ActionPerformed(evt);
            }
        });
        mnuSQLServer3.add(mnuSTSistemas_v2);

        mnuSysAut.setText("SysAut");
        mnuSysAut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSysAutActionPerformed(evt);
            }
        });
        mnuSQLServer3.add(mnuSysAut);

        mnuTelecon.setText("Telecon");
        mnuTelecon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTeleconActionPerformed(evt);
            }
        });
        mnuSQLServer3.add(mnuTelecon);

        mnuDatabase.add(mnuSQLServer3);

        mnuImpSistema.add(mnuDatabase);

        mnuFile.setText("Arquivos");

        jMenuItem8.setText("Padrão");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        mnuFile.add(jMenuItem8);

        jMenuItem24.setText("Contech");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        mnuFile.add(jMenuItem24);

        jMenuItem28.setText("MultiPDV");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        mnuFile.add(jMenuItem28);

        jMenuItem27.setText("SoftLine");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        mnuFile.add(jMenuItem27);

        jMenuItem32.setText("InfoStore");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        mnuFile.add(jMenuItem32);

        mnuHostMundo.setText("HostMundo");
        mnuHostMundo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHostMundoActionPerformed(evt);
            }
        });
        mnuFile.add(mnuHostMundo);

        mnuWmByFile.setText("Wm");
        mnuWmByFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWmByFileActionPerformed(evt);
            }
        });
        mnuFile.add(mnuWmByFile);

        mnuSiaCriareByFile.setText("Sia - Criare");
        mnuSiaCriareByFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSiaCriareByFileActionPerformed(evt);
            }
        });
        mnuFile.add(mnuSiaCriareByFile);

        mnuImpSistema.add(mnuFile);

        mnuAlteracaoID.setText("Alteração ID Produtos");

        jMenuItem29.setText("Padrão");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        mnuAlteracaoID.add(jMenuItem29);

        mnuImpSistema.add(mnuAlteracaoID);

        mnuPlanilhaV2.setText("Planilha (2.0)");
        mnuPlanilhaV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlanilhaV2ActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuPlanilhaV2);

        mnuLogVenda.setText("Log Venda");
        mnuLogVenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLogVendaActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuLogVenda);

        mnuNFe.setText("NFe (Saída)");
        mnuNFe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNFeActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuNFe);

        mnuCodigoBarrasAtacado.setText("Gerar Codigo Barras Atacado");
        mnuCodigoBarrasAtacado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCodigoBarrasAtacadoActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuCodigoBarrasAtacado);

        mnuSambanet.setText("Sambanet");
        mnuSambanet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSambanetActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuSambanet);

        mnuImportarNfce.setText("NFC-e");
        mnuImportarNfce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportarNfceActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuImportarNfce);

        mnuEspeciais.setText("Especiais");

        mnupdvvendaitem.setText("Alterar Produto pdv.vendaitem");
        mnupdvvendaitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnupdvvendaitemActionPerformed(evt);
            }
        });
        mnuEspeciais.add(mnupdvvendaitem);

        mnuPlanilhaVr.setText("Acertar Fiscal Produtos Vr");
        mnuPlanilhaVr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlanilhaVrActionPerformed(evt);
            }
        });
        mnuEspeciais.add(mnuPlanilhaVr);

        jMenuItem10.setText("Unificação Primeiro Preço");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        mnuEspeciais.add(jMenuItem10);

        jMenuItem12.setText("Correção Mercadológico Supermercado Rodrigues");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        mnuEspeciais.add(jMenuItem12);

        mnuImpSistema.add(mnuEspeciais);

        mnuAcertarIdsProdutos.setText("Acertar Ids Produtos");
        mnuAcertarIdsProdutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAcertarIdsProdutosActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuAcertarIdsProdutos);

        mnuContaPagar.setText("Gerar Conta Pagar");
        mnuContaPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuContaPagarActionPerformed(evt);
            }
        });
        mnuImpSistema.add(mnuContaPagar);

        jMenu4.setText("Copia entre Lojas");

        copiaEntreLojas.setText("Copiar Info de Produtos");
        copiaEntreLojas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copiaEntreLojasActionPerformed(evt);
            }
        });
        jMenu4.add(copiaEntreLojas);

        copiarPromocoes.setText("Copiar Promoções");
        copiarPromocoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copiarPromocoesActionPerformed(evt);
            }
        });
        jMenu4.add(copiarPromocoes);

        mnuImpSistema.add(jMenu4);

        mnuInterface.add(mnuImpSistema);

        mnuVRPdv.setText("VR Software (PDV)");
        mnuVRPdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVRPdvActionPerformed(evt);
            }
        });
        mnuInterface.add(mnuVRPdv);

        mnuMenu.add(mnuInterface);

        mnuFerramentas.setText("Ferramentas");

        mnuEditarConexoes.setText("Editar conexões");
        mnuEditarConexoes.setToolTipText("");
        mnuEditarConexoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEditarConexoesActionPerformed(evt);
            }
        });
        mnuFerramentas.add(mnuEditarConexoes);
        mnuFerramentas.add(jSeparator1);

        mnuAvancadas.setText("Avançadas");

        mnuImportarNCM.setText("Importar NCM da Legislação");
        mnuImportarNCM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportarNCMActionPerformed(evt);
            }
        });
        mnuAvancadas.add(mnuImportarNCM);

        mnuParametros.setText("Parâmetros");
        mnuParametros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuParametrosActionPerformed(evt);
            }
        });
        mnuAvancadas.add(mnuParametros);

        mnuParametros1.setText("Editor SQL");
        mnuParametros1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuParametros1ActionPerformed(evt);
            }
        });
        mnuAvancadas.add(mnuParametros1);

        mnuDelRegistro.setText("Deleta Registro");
        mnuDelRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDelRegistroActionPerformed(evt);
            }
        });
        mnuAvancadas.add(mnuDelRegistro);

        jMenuItem6.setText("Correção Impostos SuperControle - SuperServer");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        mnuAvancadas.add(jMenuItem6);

        mnuCorrecaoImpostosDSoft.setText("Correção Impostos DSfoft");
        mnuCorrecaoImpostosDSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCorrecaoImpostosDSoftActionPerformed(evt);
            }
        });
        mnuAvancadas.add(mnuCorrecaoImpostosDSoft);

        mnuFerramentas.add(mnuAvancadas);

        jMenu2.setText("Conversores");

        jMenuItem17.setText("Conversor");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem17);

        jMenuItem21.setText("Conversor DBF");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem21);

        mnuFerramentas.add(jMenu2);

        mnuRelatorios.setText("Relatórios e Sped");
        mnuRelatorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRelatoriosActionPerformed(evt);
            }
        });
        mnuFerramentas.add(mnuRelatorios);

        jMenuItem18.setText("Nutricional Arquivo");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        mnuFerramentas.add(jMenuItem18);

        jMenuItem22.setText("Mercadológico Padrão");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        mnuFerramentas.add(jMenuItem22);

        gerarCodant.setText("Criação de Codant");
        gerarCodant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerarCodantActionPerformed(evt);
            }
        });
        mnuFerramentas.add(gerarCodant);

        gerarBackup.setText("Gerar Backup de Tabelas");
        gerarBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerarBackupActionPerformed(evt);
            }
        });
        mnuFerramentas.add(gerarBackup);

        zerarEstoque.setText("Zerar Estoque");
        zerarEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zerarEstoqueActionPerformed(evt);
            }
        });
        mnuFerramentas.add(zerarEstoque);

        mnuMenu.add(mnuFerramentas);

        mnuPlanilha.setText("Planilha");

        mnuPlanilhaProduto.setText("Planilha de Produto");
        mnuPlanilhaProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlanilhaProdutoActionPerformed(evt);
            }
        });
        mnuPlanilha.add(mnuPlanilhaProduto);

        mnuMenu.add(mnuPlanilha);

        jMenu1.setText("Ferramentas Fiscais");

        jMenuItem20.setText("Particionar Relatórios");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem20);

        mnuMenu.add(jMenu1);

        mnuJanela.setText("Janela");
        mnuMenu.add(mnuJanela);

        mnuAjuda.setText("Ajuda");
        mnuAjuda.add(jSeparator4);

        mnuAjudaSobre.setText("Sobre...");
        mnuAjudaSobre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAjudaSobreActionPerformed(evt);
            }
        });
        mnuAjuda.add(mnuAjudaSobre);

        mnuMenu.add(mnuAjuda);

        mnuSistema.setText("Sistema");

        mnuSistemaLogin.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        mnuSistemaLogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/atalho/login.png"))); // NOI18N
        mnuSistemaLogin.setText("Login");
        mnuSistemaLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSistemaLoginActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuSistemaLogin);

        mnuMenu.add(mnuSistema);

        setJMenuBar(mnuMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuAjudaSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAjudaSobreActionPerformed
        try {
            this.setWaitCursor();
            SobreGUI form = new SobreGUI();

            form.setSobre("VR Implantaçao 2.5", Global.VERSAO, new SimpleDateFormat("dd/MM/yyyy").format(Global.DATA_VERSAO));
            form.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
}//GEN-LAST:event_mnuAjudaSobreActionPerformed
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            this.setWaitCursor();
            sair();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_formWindowClosing
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        try {
            this.setWaitCursor();
            sair();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
}//GEN-LAST:event_btnSairActionPerformed
    private void mnuLojaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLojaActionPerformed
        try {
            this.setWaitCursor();
            if (formLojaConsulta == null || formLojaConsulta.isClosed()) {
                formLojaConsulta = new LojaConsultaGUI(this);
            }

            formLojaConsulta.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuLojaActionPerformed
    private void mnuSistemaLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSistemaLoginActionPerformed
        try {
            this.setWaitCursor();
            loginFrame.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuSistemaLoginActionPerformed

    private void jMenuItemCGAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCGAActionPerformed
        CgaGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemCGAActionPerformed

    private void jMenuItemMilenioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMilenioActionPerformed
        MilenioGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemMilenioActionPerformed


    private void jMenuItemJMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemJMasterActionPerformed

        try {
            this.setWaitCursor();
            if (formImportarJMaster == null || formImportarJMaster.isClosed()) {
                formImportarJMaster = new JMasterGUI(this);
            }

            formImportarJMaster.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemJMasterActionPerformed


    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        try {
            this.setWaitCursor();
            if (formArquivoPadrao == null || formArquivoPadrao.isClosed()) {
                formArquivoPadrao = new ArquivoPadraoGUI(this);
            }

            formArquivoPadrao.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItemSHIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSHIActionPerformed
        ShiGUI_v2.exibir(this);
    }//GEN-LAST:event_jMenuItemSHIActionPerformed

    private void jMenuItemGetWayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGetWayActionPerformed
        GetWay_ProfitGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemGetWayActionPerformed

    private void jMenuItemSysPDVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSysPDVActionPerformed
        SysPdv2_5GUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSysPDVActionPerformed

    private void jMenuItemIdealActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemIdealActionPerformed

        try {
            this.setWaitCursor();
            if (formImportarIdeal == null || formImportarIdeal.isClosed()) {
                formImportarIdeal = new IdealGUI(this);
            }

            formImportarIdeal.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemIdealActionPerformed

    private void mnuLogVendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLogVendaActionPerformed
        try {
            this.setWaitCursor();
            if (formImportacaoLogVendaGUI == null || formImportacaoLogVendaGUI.isClosed()) {
                formImportacaoLogVendaGUI = new ImportacaoLogVendaGUI(this);
            }

            formImportacaoLogVendaGUI.setConsultaCampo(null);
            formImportacaoLogVendaGUI.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuLogVendaActionPerformed

    private void jMenuItemMobilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMobilityActionPerformed
        MobilityGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemMobilityActionPerformed

    private void jMenuItemSysPDVSQLServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSysPDVSQLServerActionPerformed
        SysPdvGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSysPDVSQLServerActionPerformed

    private void jMenuItemGdoorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGdoorActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarGdoor == null || formImportarGdoor.isClosed()) {
                formImportarGdoor = new GdoorGUI(this);
            }

            formImportarGdoor.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemGdoorActionPerformed

    private void jMenuItemWisaSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWisaSoftActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarWisaSoft == null || formImportarWisaSoft.isClosed()) {
                formImportarWisaSoft = new WisaSoftGUI_2(this);
            }

            formImportarWisaSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemWisaSoftActionPerformed

    private void jMenuItem24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem24ActionPerformed
        ContechGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem24ActionPerformed

    private void jMenuItemFMSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFMSistemasActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarFM == null || formImportarFM.isClosed()) {
                formImportarFM = new FMGUI(this);
            }

            formImportarFM.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemFMSistemasActionPerformed

    private void jMenuItemGuiaSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuiaSistemasActionPerformed
        GuiaSistemasGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemGuiaSistemasActionPerformed

    private void jMenuItemBoechatSoftActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.setWaitCursor();
            if (formImportarBoechatSoft == null || formImportarBoechatSoft.isClosed()) {
                formImportarBoechatSoft = new BoechatSoftGUI(this);
            }

            formImportarBoechatSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }

    private void jMenuItemGCFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGCFActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarGCF == null || formImportarGCF.isClosed()) {
                formImportarGCF = new GCFGUI(this);
            }

            formImportarGCF.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemGCFActionPerformed

    private void jMenuItem28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem28ActionPerformed
        MultiPdvGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem28ActionPerformed

    private void jMenuItem29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem29ActionPerformed
        try {
            this.setWaitCursor();
            if (formAcertarCodigoInterno == null || formAcertarCodigoInterno.isClosed()) {
                formAcertarCodigoInterno = new AcertarCodigoInternoGUI(this);
            }

            formAcertarCodigoInterno.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_jMenuItem29ActionPerformed

    private void jMenuItemEverastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEverastActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarEverast == null || formImportarEverast.isClosed()) {
                formImportarEverast = new EverastGUI(this);
            }

            formImportarEverast.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemEverastActionPerformed

    private void mnuAjudaBuscaActionPerformed(java.awt.event.ActionEvent evt) {
        txtBusca.requestFocusLater();
    }

    private void jMenuItem33ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.setWaitCursor();
            if (formImportarBoechatSoft == null || formImportarBoechatSoft.isClosed()) {
                formImportarBoechatSoft = new BoechatSoftGUI(this);
            }
            formImportarBoechatSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }
    private void jMenuItemSoftaExActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSoftaExActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarSoftaEx == null || formImportarSoftaEx.isClosed()) {
                formImportarSoftaEx = new SoftaExGUI(this);
            }

            formImportarSoftaEx.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_jMenuItemSoftaExActionPerformed

    private void jMenuItemOrionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOrionActionPerformed

        OrionGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemOrionActionPerformed

    private void jMenuItemUltraSystActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUltraSystActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarUltraSyst == null || formImportarUltraSyst.isClosed()) {
                formImportarUltraSyst = new UltraSistGUI(this);
            }

            formImportarUltraSyst.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemUltraSystActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        IntelliCashGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItemConcretizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemConcretizeActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarConcretize == null || formImportarConcretize.isClosed()) {
                formImportarConcretize = new ConcretizeGUI(this);
            }
            formImportarConcretize.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemConcretizeActionPerformed

    private void jMenuItemKairosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemKairosActionPerformed
        KairosGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemKairosActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        RootacGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemDirectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDirectorActionPerformed

        /*try {
            this.setWaitCursor();
            if (formImportarDirector == null || formImportarDirector.isClosed()) {
                formImportarDirector = new DirectorGUI(this);
            }
            formImportarDirector.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }*/
    }//GEN-LAST:event_jMenuItemDirectorActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        MrsGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void mnuVRPdvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVRPdvActionPerformed

        PdvVrGUI.exibir(this);
    }//GEN-LAST:event_mnuVRPdvActionPerformed

    private void jMenuItemEcosInformaticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEcosInformaticaActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarEccusInformatica == null || formImportarEccusInformatica.isClosed()) {
                formImportarEccusInformatica = new EccusInformaticaGUI(this);
            }

            formImportarEccusInformatica.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemEcosInformaticaActionPerformed

    private void jMenuItemSuperServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperServerActionPerformed

        SuperControle_SuperServerGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSuperServerActionPerformed

    private void jMenuItemCISSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCISSActionPerformed
        CissGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemCISSActionPerformed

    private void jMenuItemControlWareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemControlWareActionPerformed
        ControlWareGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemControlWareActionPerformed

    private void jMenuItemDestroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDestroActionPerformed

        DestroGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemDestroActionPerformed

    private void jMenuItemSysMouraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSysMouraActionPerformed
        SisMouraGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSysMouraActionPerformed

    private void mnuNFeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNFeActionPerformed
        try {
            this.setWaitCursor();
            if (formNotaSaidaImportacaoArquivoGUI == null || formNotaSaidaImportacaoArquivoGUI.isClosed()) {
                formNotaSaidaImportacaoArquivoGUI = new ImportarNotaSaidaImportacaoArquivoGUI(this);
            }

            formNotaSaidaImportacaoArquivoGUI.setConsultaCampo(null);
            formNotaSaidaImportacaoArquivoGUI.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuNFeActionPerformed

    private void jMenuItemTopSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTopSystemActionPerformed

        TopSystemGUI3.exibir(this);
    }//GEN-LAST:event_jMenuItemTopSystemActionPerformed

    private void jMenuItemSciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSciActionPerformed
        try {
            this.setWaitCursor();
            if (formSciGUI == null || formSciGUI.isClosed()) {
                formSciGUI = new SciGUI(this);
            }

            formSciGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSciActionPerformed

    private void mnuMapeamentoMercadologicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMapeamentoMercadologicoActionPerformed
        MapaMercadologicoGUI.Exibir(this);
    }//GEN-LAST:event_mnuMapeamentoMercadologicoActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        GZSistemasGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItemSBOMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSBOMarketActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarSBOMarket == null || formImportarSBOMarket.isClosed()) {
                formImportarSBOMarket = new SBOMarketGUI(this);
            }
            formImportarSBOMarket.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSBOMarketActionPerformed

    private void mnuSambaNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSambaNetActionPerformed
        SambaNetV2GUI.exibir(this);
    }//GEN-LAST:event_mnuSambaNetActionPerformed

    private void jMenuItemSicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSicsActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarSics == null || formImportarSics.isClosed()) {
                formImportarSics = new SicsGUI(this);
            }
            formImportarSics.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSicsActionPerformed

    private void jMenuItemSimSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSimSoftActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarSimSoft == null || formImportarSimSoft.isClosed()) {
                formImportarSimSoft = new SimSoftGUI(this);
            }

            formImportarSimSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSimSoftActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarFaucom == null || formImportarFaucom.isClosed()) {
                formImportarFaucom = new FaucomGUI(this);
            }

            formImportarFaucom.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItemSIMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSIMSActionPerformed
        SIMSGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSIMSActionPerformed

    private void jMenuItemGR7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGR7ActionPerformed

        GR7_2GUI.exibir(this);
    }//GEN-LAST:event_jMenuItemGR7ActionPerformed

    private void mnuImportarNCMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportarNCMActionPerformed
        try {
            this.setWaitCursor();
            if (formImportacoesDiversas == null || formImportacoesDiversas.isClosed()) {
                formImportacoesDiversas = new ImportacoesDiversasGUI(this);
            }

            formImportacoesDiversas.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuImportarNCMActionPerformed

    private void jMenuItemSuperusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSuperusActionPerformed
        /*try {
            this.setWaitCursor();
            if (formImportarSuperus == null || formImportarSuperus.isClosed()) {
                formImportarSuperus = new SuperusGUI(this);
            }

            formImportarSuperus.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }*/
        SuperusGUI2.exibir(this);
    }//GEN-LAST:event_jMenuItemSuperusActionPerformed

    private void jMenuItemSofgceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSofgceActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarSofgce == null || formImportarSofgce.isClosed()) {
                formImportarSofgce = new SofgceGUI(this);
            }

            formImportarSofgce.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSofgceActionPerformed

    private void mnuParametrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuParametrosActionPerformed
        ParametroGUI.Exibir(this);
    }//GEN-LAST:event_mnuParametrosActionPerformed

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarMaximusDatasync == null || formImportarMaximusDatasync.isClosed()) {
                formImportarMaximusDatasync = new Maximus_DatasyncGUI(this);
            }

            formImportarMaximusDatasync.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem23ActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarSoftClass == null || formImportarSoftClass.isClosed()) {
                formImportarSoftClass = new SoftClass_AutoComGUI(this);
            }

            formImportarSoftClass.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem23ActionPerformed

    private void jMenuItem25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem25ActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarAlfaSoftware == null || formImportarAlfaSoftware.isClosed()) {
                formImportarAlfaSoftware = new AlfaSoftwareGUI(this);
            }

            formImportarAlfaSoftware.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem25ActionPerformed

    private void jMenuItemInfoBrasilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInfoBrasilActionPerformed
        InfoBrasilGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemInfoBrasilActionPerformed

    private void mnuPlanilhaV2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlanilhaV2ActionPerformed
        PlanilhaV2GUI.Exibir(this);
    }//GEN-LAST:event_mnuPlanilhaV2ActionPerformed

    private void jMenuItem27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem27ActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarSoftLine == null || formImportarSoftLine.isClosed()) {
                formImportarSoftLine = new SoftLineGUI(this);
            }

            formImportarSoftLine.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem27ActionPerformed

    private void mnuEditarConexoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEditarConexoesActionPerformed
        ConexaoPropertiesEditorGUI.editar(App.properties());
    }//GEN-LAST:event_mnuEditarConexoesActionPerformed

    private void jMenuItemAriusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAriusActionPerformed
        AriusGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemAriusActionPerformed

    private void jMenuItemActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemActiveActionPerformed
        ActiveGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemActiveActionPerformed

    private void jMenuItem32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem32ActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarInfoStore == null || formImportarInfoStore.isClosed()) {
                formImportarInfoStore = new InfoStoreGUI(this);
            }

            formImportarInfoStore.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem32ActionPerformed

    private void mmnuDGComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmnuDGComActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarDGCom == null || formImportarDGCom.isClosed()) {
                formImportarDGCom = new DGComGUI(this);
            }

            formImportarDGCom.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_mmnuDGComActionPerformed

    private void mnuSaacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaacActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarSaac == null || formImportarSaac.isClosed()) {
                formImportarSaac = new SaacGUI(this);
            }

            formImportarSaac.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuSaacActionPerformed

    private void mnuControllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuControllActionPerformed
        ControllGUI.exibir(this);
    }//GEN-LAST:event_mnuControllActionPerformed

    private void mnuLogusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLogusActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarLogus == null || formImportarLogus.isClosed()) {
                formImportarLogus = new LogusGUI(this);
            }

            formImportarLogus.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuLogusActionPerformed

    private void mnuSigmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSigmaActionPerformed
        SigmaGUI.exibir(this);
    }//GEN-LAST:event_mnuSigmaActionPerformed

    private void jMenuItemSacLumiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSacLumiActionPerformed
        SacLumiGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSacLumiActionPerformed

    private void mnuInteragemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInteragemActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarInteragem == null || formImportarInteragem.isClosed()) {
                formImportarInteragem = new InteragemGUI_2(this);
            }

            formImportarInteragem.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuInteragemActionPerformed

    private void mnuFGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFGActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarFG == null || formImportarFG.isClosed()) {
                formImportarFG = new FGGUI(this);
            }

            formImportarFG.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuFGActionPerformed

    private void mmnuGestoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mmnuGestoraActionPerformed
        GestoraV2GUI.exibir(this);
    }//GEN-LAST:event_mmnuGestoraActionPerformed

    private void mnuIdealSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIdealSoftActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarIdealSoft == null || formImportarIdealSoft.isClosed()) {
                formImportarIdealSoft = new IdealSoftGUI(this);
            }

            formImportarIdealSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuIdealSoftActionPerformed

    private void mnuSicomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSicomActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarSicom == null || formImportarSicom.isClosed()) {
                formImportarSicom = new SicomGUI(this);
            }

            formImportarSicom.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuSicomActionPerformed

    private void mnuRMS_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRMS_2ActionPerformed
        RMSGUI_2.exibir(this);
    }//GEN-LAST:event_mnuRMS_2ActionPerformed

    private void mnuHostMundoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHostMundoActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarHostMundo == null || formImportarHostMundo.isClosed()) {
                formImportarHostMundo = new HostMundoGUI(this);
            }

            formImportarHostMundo.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuHostMundoActionPerformed

    private void mnuFlatanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFlatanActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarFlatan == null || formImportarFlatan.isClosed()) {
                formImportarFlatan = new FlatanGUI(this);
            }
            formImportarFlatan.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuFlatanActionPerformed

    private void mnuBrainSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBrainSoftActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarBrainSoft == null || formImportarBrainSoft.isClosed()) {
                formImportarBrainSoft = new BrainSoftGUI(this);
            }
            formImportarBrainSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuBrainSoftActionPerformed

    private void mnuWinthor_PcSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWinthor_PcSistemasActionPerformed
        Winthor_PcSistemasGUI.exibir(this);
    }//GEN-LAST:event_mnuWinthor_PcSistemasActionPerformed

    private void mnuDelfiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDelfiActionPerformed
        // TODO add your handling code here:
        DelphiGUI.exibir(this);
    }//GEN-LAST:event_mnuDelfiActionPerformed

    private void mnuFabTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFabTechActionPerformed
        // TODO add your handling code here:
        SabTechGUI.exibir(this);
    }//GEN-LAST:event_mnuFabTechActionPerformed

    private void jMenuItemTiTecnologiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTiTecnologiaActionPerformed
        TiTecnologiaGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemTiTecnologiaActionPerformed

    private void mnuJacsysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJacsysActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarJacsys == null || formImportarJacsys.isClosed()) {
                formImportarJacsys = new JacsysGUI(this);
            }
            formImportarJacsys.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_mnuJacsysActionPerformed

    private void mnuCodigoBarrasAtacadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCodigoBarrasAtacadoActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarGerarCodigoBarrasAtacado == null || formImportarGerarCodigoBarrasAtacado.isClosed()) {
                formImportarGerarCodigoBarrasAtacado = new GerarCodigoBarrasAtacadoGUI(this);
            }
            formImportarGerarCodigoBarrasAtacado.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuCodigoBarrasAtacadoActionPerformed

    private void mnuRepleisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRepleisActionPerformed
        RepleisGUI.exibir(this);
    }//GEN-LAST:event_mnuRepleisActionPerformed

    private void mnuASoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuASoftActionPerformed
        ASoftGUI.exibir(this);
    }//GEN-LAST:event_mnuASoftActionPerformed

    private void mnuBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBaseActionPerformed
        BaseGUI.exibir(this);
    }//GEN-LAST:event_mnuBaseActionPerformed

    private void mnuEsSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEsSystemActionPerformed
        // TODO add your handling code here:
        EsSystemGUI.exibir(this);
    }//GEN-LAST:event_mnuEsSystemActionPerformed

    private void mnuSifatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSifatActionPerformed
        // TODO add your handling code here:        
        Sifat_2GUI.exibir(this);
    }//GEN-LAST:event_mnuSifatActionPerformed

    private void mnuApolloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuApolloActionPerformed
        ApolloGUI.exibir(this);
    }//GEN-LAST:event_mnuApolloActionPerformed

    private void mnuRCNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRCNetActionPerformed
        RCNetGUI.exibir(this);
    }//GEN-LAST:event_mnuRCNetActionPerformed

    private void mnuEmporioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEmporioActionPerformed
        EmporioGUI.exibir(this);
    }//GEN-LAST:event_mnuEmporioActionPerformed

    private void mnuMSIInforActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMSIInforActionPerformed
        MSIInforGUI.exibir(this);
    }//GEN-LAST:event_mnuMSIInforActionPerformed

    private void mnuSatecfeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSatecfeActionPerformed
        SatecfeGUI.exibir(this);
    }//GEN-LAST:event_mnuSatecfeActionPerformed

    private void mnuJrfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJrfActionPerformed
        // TODO add your handling code here:
        JrfGUI.exibir(this);
    }//GEN-LAST:event_mnuJrfActionPerformed

    private void mnuFarm2000ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFarm2000ActionPerformed
        Farm2000GUI.exibir(this);
    }//GEN-LAST:event_mnuFarm2000ActionPerformed

    private void mnuHipicomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHipicomActionPerformed

    }//GEN-LAST:event_mnuHipicomActionPerformed

    private void mnuLinnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLinnerActionPerformed
        LinnerGUI.exibir(this);
    }//GEN-LAST:event_mnuLinnerActionPerformed

    private void mnuSDInformaticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSDInformaticaActionPerformed
        SDInformaticaGUI.exibir(this);
    }//GEN-LAST:event_mnuSDInformaticaActionPerformed

    private void mnuFabTech1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFabTech1ActionPerformed
        PomaresGUI.exibir(this);
    }//GEN-LAST:event_mnuFabTech1ActionPerformed

    private void mnuCPGestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCPGestorActionPerformed
        // TODO add your handling code here:
        CPGestorGUI.exibir(this);
    }//GEN-LAST:event_mnuCPGestorActionPerformed

    private void mnuImportarNfceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportarNfceActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formNotaSaidaNfceImportacaoArquivoGUI == null || formNotaSaidaNfceImportacaoArquivoGUI.isClosed()) {
                formNotaSaidaNfceImportacaoArquivoGUI = new NotaSaidaNfceImportacaoArquivoGUI(this);
            }

            formNotaSaidaNfceImportacaoArquivoGUI.setConsultaCampo(null);
            formNotaSaidaNfceImportacaoArquivoGUI.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuImportarNfceActionPerformed

    private void mnuTstiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTstiActionPerformed
        // TODO add your handling code here:
        TstiGUI.exibir(this);
    }//GEN-LAST:event_mnuTstiActionPerformed

    private void mnuCplusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCplusActionPerformed
        // TODO add your handling code here:
        CPlusGUI.exibir(this);
    }//GEN-LAST:event_mnuCplusActionPerformed

    private void mnuWmsiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWmsiActionPerformed
        // TODO add your handling code here:
        WmsiGUI.exibir(this);
    }//GEN-LAST:event_mnuWmsiActionPerformed

    private void mnuSolidusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSolidusActionPerformed
        SolidusGUI.exibir(this);
    }//GEN-LAST:event_mnuSolidusActionPerformed

    private void mnuVisualComercioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVisualComercioActionPerformed
        VisualComercioGUI.exibir(this);
    }//GEN-LAST:event_mnuVisualComercioActionPerformed

    private void mnuAvanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAvanceActionPerformed
        AvanceGUI.exibir(this);
    }//GEN-LAST:event_mnuAvanceActionPerformed

    private void mnuAsefeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAsefeActionPerformed
        AsefeGUI.exibir(this);
    }//GEN-LAST:event_mnuAsefeActionPerformed

    private void mnuSuperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSuperActionPerformed
        SuperGUI.exibir(this);
    }//GEN-LAST:event_mnuSuperActionPerformed

    private void mnuAutoSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAutoSystemActionPerformed
        AutoSystemGUI.exibir(this);
    }//GEN-LAST:event_mnuAutoSystemActionPerformed

    private void mnuWmByFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWmByFileActionPerformed
        Wm_byFileGUI.exibir(this);
    }//GEN-LAST:event_mnuWmByFileActionPerformed

    private void mnuScefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuScefActionPerformed
        // TODO add your handling code here:
        ScefGUI.exibir(this);
    }//GEN-LAST:event_mnuScefActionPerformed

    private void mnuInfoMacStoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInfoMacStoreActionPerformed
        InfoMacGUI.exibir(this);
    }//GEN-LAST:event_mnuInfoMacStoreActionPerformed

    private void mnuHipcomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHipcomActionPerformed
        Hipcom2_5GUI.exibir(this);
    }//GEN-LAST:event_mnuHipcomActionPerformed

    private void mnuParametros1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuParametros1ActionPerformed
        SQLEditor.exibir(this);
    }//GEN-LAST:event_mnuParametros1ActionPerformed

    private void mnuWmsi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWmsi1ActionPerformed
        SiacGUI.exibir(this);
    }//GEN-LAST:event_mnuWmsi1ActionPerformed

    private void mnuAutoSystem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAutoSystem1ActionPerformed
        RMSAutomaHelpGUI.exibir(this);
    }//GEN-LAST:event_mnuAutoSystem1ActionPerformed

    private void mnuWebsacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWebsacActionPerformed
        WebSacGUI.exibir(this);
    }//GEN-LAST:event_mnuWebsacActionPerformed

    private void mnuSiaCriareByFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSiaCriareByFileActionPerformed
        SiaCriareByFileGUI.exibir(this);
    }//GEN-LAST:event_mnuSiaCriareByFileActionPerformed

    private void mnuPwsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPwsActionPerformed
        PwsGUI.exibir(this);
    }//GEN-LAST:event_mnuPwsActionPerformed

    private void mnuJM2OnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuJM2OnlineActionPerformed
        JM2OnlineGUI.exibir(this);
    }//GEN-LAST:event_mnuJM2OnlineActionPerformed

    private void mnuFenixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFenixActionPerformed
        FenixGUI.exibir(this);
    }//GEN-LAST:event_mnuFenixActionPerformed

    private void mnuInventerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInventerActionPerformed
        InventerGUI.exibir(this);
    }//GEN-LAST:event_mnuInventerActionPerformed

    private void mnuSoftcomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSoftcomActionPerformed
        SoftcomGUI.exibir(this);
    }//GEN-LAST:event_mnuSoftcomActionPerformed

    private void mnuFortiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFortiActionPerformed
        FortiGUI.exibir(this);
    }//GEN-LAST:event_mnuFortiActionPerformed

    private void mnuFaciliteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFaciliteActionPerformed
        FaciliteGUI.exibir(this);
    }//GEN-LAST:event_mnuFaciliteActionPerformed

    private void mnupdvvendaitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnupdvvendaitemActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formAlterarProdutoPdvVendaItem == null || formAlterarProdutoPdvVendaItem.isClosed()) {
                formAlterarProdutoPdvVendaItem = new AlterarProdutoPdvVendaItemGUI(this);
            }

            formAlterarProdutoPdvVendaItem.setConsultaCampo(null);
            formAlterarProdutoPdvVendaItem.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnupdvvendaitemActionPerformed

    private void mnuSysmoPostgresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSysmoPostgresActionPerformed
        SysmoPostgresGUI.exibir(this);
    }//GEN-LAST:event_mnuSysmoPostgresActionPerformed

    private void mnuIcommerceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIcommerceActionPerformed
        ICommerceGUI.exibir(this);
    }//GEN-LAST:event_mnuIcommerceActionPerformed

    private void mnuFortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFortActionPerformed
        FortGUI.exibir(this);
    }//GEN-LAST:event_mnuFortActionPerformed

    private void mnuSysmoFirebirdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSysmoFirebirdActionPerformed
        SysmoFirebirdGUI.exibir(this);
    }//GEN-LAST:event_mnuSysmoFirebirdActionPerformed

    private void mnuDtComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDtComActionPerformed
        DtComGUI.exibir(this);
    }//GEN-LAST:event_mnuDtComActionPerformed

    private void mnuKcmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuKcmsActionPerformed
        KcmsGUI.exibir(this);
    }//GEN-LAST:event_mnuKcmsActionPerformed

    private void mnuSriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSriActionPerformed
        SriGUI.exibir(this);
    }//GEN-LAST:event_mnuSriActionPerformed

    private void mnuAlterDataWShopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAlterDataWShopActionPerformed
        AlterData_WShopGUI.exibir(this);
    }//GEN-LAST:event_mnuAlterDataWShopActionPerformed

    private void mnuCadastraFacilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCadastraFacilActionPerformed
        CadastraFacilGUI.exibir(this);
    }//GEN-LAST:event_mnuCadastraFacilActionPerformed

    private void mnuIntelliconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIntelliconActionPerformed
        IntelliconGUI.exibir(this);
    }//GEN-LAST:event_mnuIntelliconActionPerformed

    private void mnuPlanilhaVrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlanilhaVrActionPerformed
        // TODO add your handling code here:
        PlanilhaVrGUI.exibir(this);
    }//GEN-LAST:event_mnuPlanilhaVrActionPerformed

    private void mnuMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMarketActionPerformed
        MarketGUI.exibir(this);
    }//GEN-LAST:event_mnuMarketActionPerformed

    private void mnuAcertarIdsProdutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAcertarIdsProdutosActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formAcertarIdsProduto == null || formAcertarIdsProduto.isClosed()) {
                formAcertarIdsProduto = new AcertarIdsProdutoGUI(this);
            }

            formAcertarIdsProduto.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_mnuAcertarIdsProdutosActionPerformed

    private void mnuUniplusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUniplusActionPerformed
        Uniplus2_5GUI.exibir(this);
    }//GEN-LAST:event_mnuUniplusActionPerformed

    private void mnuUpFortiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUpFortiActionPerformed
        // TODO add your handling code here:
        UpFortiGUI.exibir(this);
    }//GEN-LAST:event_mnuUpFortiActionPerformed

    private void mnuCefasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCefasActionPerformed
        CefasGUI.exibir(this);
    }//GEN-LAST:event_mnuCefasActionPerformed

    private void mnuSofttechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSofttechActionPerformed
        SofttechGUI.exibir(this);
    }//GEN-LAST:event_mnuSofttechActionPerformed

    private void mnuLyncisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLyncisActionPerformed
        LyncisGUI.exibir(this);
    }//GEN-LAST:event_mnuLyncisActionPerformed

    private void mnuFHOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFHOnlineActionPerformed
        // TODO add your handling code here:
        FHOnlineGUI.exibir(this);
    }//GEN-LAST:event_mnuFHOnlineActionPerformed

    private void mnuAlphaSysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAlphaSysActionPerformed
        AlphaSysGUI.exibir(this);
    }//GEN-LAST:event_mnuAlphaSysActionPerformed

    private void mnuUmPontoDoisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUmPontoDoisActionPerformed
        UmPontoDoisGUI.exibir(this);
    }//GEN-LAST:event_mnuUmPontoDoisActionPerformed

    private void mnuZpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuZpfActionPerformed
        ZpfGUI.exibir(this);
    }//GEN-LAST:event_mnuZpfActionPerformed

    private void mnuFlashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFlashActionPerformed
        // TODO add your handling code here:
        FlashGUI.exibir(this);
    }//GEN-LAST:event_mnuFlashActionPerformed

    private void mnuHiperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHiperActionPerformed
        // TODO add your handling code here:
        HiperGUI.exibir(this);
    }//GEN-LAST:event_mnuHiperActionPerformed

    private void mnuLinceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLinceActionPerformed
        LinceGUI.exibir(this);
    }//GEN-LAST:event_mnuLinceActionPerformed

    private void mnuSiaCriareDbfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSiaCriareDbfActionPerformed
        // TODO add your handling code here:
        SiaCriareDbfGUI.exibir(this);
    }//GEN-LAST:event_mnuSiaCriareDbfActionPerformed

    private void mnuCFSoftSiaECFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCFSoftSiaECFActionPerformed
        CFSoftSiaECFGUI.exibir(this);
    }//GEN-LAST:event_mnuCFSoftSiaECFActionPerformed

    private void mnuDataSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDataSyncActionPerformed
        DataSyncGUI.exibir(this);
    }//GEN-LAST:event_mnuDataSyncActionPerformed

    private void mnuWinNexusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWinNexusActionPerformed
        // TODO add your handling code here:
        WinNexusGUI.exibir(this);
    }//GEN-LAST:event_mnuWinNexusActionPerformed

    private void jMenuItemRMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRMSActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarRM == null || formImportarRM.isClosed()) {
                formImportarRM = new RMSGUI(this);
            }

            formImportarRM.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemRMSActionPerformed

    private void mnuRPInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRPInfoActionPerformed
        RPInfoGUI.exibir(this);
    }//GEN-LAST:event_mnuRPInfoActionPerformed

    private void mnuCerebroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCerebroActionPerformed
        // TODO add your handling code here:
        CerebroGUI.exibir(this);
    }//GEN-LAST:event_mnuCerebroActionPerformed

    private void mnuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOpenActionPerformed
        OpenGUI.exibir(this);
    }//GEN-LAST:event_mnuOpenActionPerformed

    private void mnuSyncTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSyncTechActionPerformed
        SyncTecGUI.exibir(this);
    }//GEN-LAST:event_mnuSyncTechActionPerformed

    private void mnuPlanilhaProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlanilhaProdutoActionPerformed
        PlanilhaProdutoGUI.exibir(this);
    }//GEN-LAST:event_mnuPlanilhaProdutoActionPerformed
    private void mnuSuperLoja10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSuperLoja10ActionPerformed
        // TODO add your handling code here:
        SuperLoja10GUI.exibir(this);
    }//GEN-LAST:event_mnuSuperLoja10ActionPerformed

    private void mnuHRTechV1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHRTechV1ActionPerformed
        HRTechGUI.exibir(this);
    }//GEN-LAST:event_mnuHRTechV1ActionPerformed

    private void mnuSolutionSuperaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSolutionSuperaActionPerformed
        // TODO add your handling code here:
        SolutionSuperaGUI.exibir(this);
    }//GEN-LAST:event_mnuSolutionSuperaActionPerformed

    private void mnuContaPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuContaPagarActionPerformed
        GeraContaPagarGUI.exibir(this);
    }//GEN-LAST:event_mnuContaPagarActionPerformed

    private void mnuGDoorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGDoorActionPerformed
        GDoorGUI.exibir(this);
    }//GEN-LAST:event_mnuGDoorActionPerformed

    private void mnuMrsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMrsActionPerformed
        // TODO add your handling code here:
        MrsGUI.exibir(this);
    }//GEN-LAST:event_mnuMrsActionPerformed

    private void mnuLiteciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLiteciActionPerformed
        // TODO add your handling code here:
        LiteciGUI.exibir(this);
    }//GEN-LAST:event_mnuLiteciActionPerformed

    private void mnuSambaNetGetWayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSambaNetGetWayActionPerformed
        SambaNetGUI.exibir(this);
    }//GEN-LAST:event_mnuSambaNetGetWayActionPerformed

    private void mnuTgaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTgaActionPerformed
        TGAGUI.exibir(this);
    }//GEN-LAST:event_mnuTgaActionPerformed

    private void mnuAcomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAcomActionPerformed
        AcomGUI.exibir(this);
    }//GEN-LAST:event_mnuAcomActionPerformed

    private void mnuViaSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViaSoftActionPerformed
        ViaSoftGUI.exibir(this);
    }//GEN-LAST:event_mnuViaSoftActionPerformed

    private void mnuGTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGTechActionPerformed
        GTechGUI.exibir(this);
    }//GEN-LAST:event_mnuGTechActionPerformed

    private void mnuClickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClickActionPerformed
        ClickGUI.exibir(this);
    }//GEN-LAST:event_mnuClickActionPerformed

    private void mnuPwGestorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPwGestorActionPerformed
        PwGestorGUI.exibir(this);
    }//GEN-LAST:event_mnuPwGestorActionPerformed
    private void mnuAutomaqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAutomaqActionPerformed
        AutomaqGUI.exibir(this);
    }//GEN-LAST:event_mnuAutomaqActionPerformed

    private void mnuIQSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIQSistemasActionPerformed
        IQSistemasGUI.exibir(this);
    }//GEN-LAST:event_mnuIQSistemasActionPerformed

    private void mnuDLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDLinkActionPerformed
        DLinkGUI.exibir(this);
    }//GEN-LAST:event_mnuDLinkActionPerformed

    private void mnuVCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVCashActionPerformed
        VCashGUI.exibir(this);
    }//GEN-LAST:event_mnuVCashActionPerformed

    private void mnuSTI3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSTI3ActionPerformed
        STI3GUI.exibir(this);
    }//GEN-LAST:event_mnuSTI3ActionPerformed

    private void mnuOryonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOryonActionPerformed
        OryonGUI.exibir(this);
    }//GEN-LAST:event_mnuOryonActionPerformed

    private void mnuHerculesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHerculesActionPerformed
        HerculesIntCashGUI.exibir(this);
    }//GEN-LAST:event_mnuHerculesActionPerformed

    private void mnuMrs1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMrs1ActionPerformed
        ZoomboxGUI.exibir(this);
    }//GEN-LAST:event_mnuMrs1ActionPerformed

    private void mnuAtenasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAtenasActionPerformed
        AtenasGUI.exibir(this);
    }//GEN-LAST:event_mnuAtenasActionPerformed

    private void mnuSavActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSavActionPerformed
        SavGUI.exibir(this);
    }//GEN-LAST:event_mnuSavActionPerformed

    private void mnuEticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEticaActionPerformed
        EticaGUI.exibir(this);
    }//GEN-LAST:event_mnuEticaActionPerformed

    private void mnuArtSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuArtSystemActionPerformed
        // TODO add your handling code here:
        ArtSystemGUI.exibir(this);
    }//GEN-LAST:event_mnuArtSystemActionPerformed

    private void mnuThotauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuThotauActionPerformed
        OrionTechGUI.exibir(this);
    }//GEN-LAST:event_mnuThotauActionPerformed

    private void mnuiWeberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuiWeberActionPerformed
        WeberGUI.exibir(this);
    }//GEN-LAST:event_mnuiWeberActionPerformed

    private void mnuG3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuG3ActionPerformed
        // TODO add your handling code here:
        G3_v2GUI.exibir(this);
    }//GEN-LAST:event_mnuG3ActionPerformed

    private void mnuAdmRioPretoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAdmRioPretoActionPerformed
        AdmMacenoGUI.exibir(this);
    }//GEN-LAST:event_mnuAdmRioPretoActionPerformed

    private void mnuiSophyxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuiSophyxActionPerformed
        SophyxGUI.exibir(this);
    }//GEN-LAST:event_mnuiSophyxActionPerformed

    private void mnuiSysERPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuiSysERPActionPerformed
        SysERPGUI.exibir(this);
    }//GEN-LAST:event_mnuiSysERPActionPerformed

    private void mnuMrs2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMrs2ActionPerformed
        InovaGUI.exibir(this);
    }//GEN-LAST:event_mnuMrs2ActionPerformed

    private void mnuAtmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAtmaActionPerformed
        // TODO add your handling code here:
        AtmaGUI.exibir(this);
    }//GEN-LAST:event_mnuAtmaActionPerformed

    private void mnuG4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuG4ActionPerformed
        SiaCriareMySqlGUI.exibir(this);
    }//GEN-LAST:event_mnuG4ActionPerformed

    private void mnuControlXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuControlXActionPerformed
        ControlXGUI.exibir(this);
    }//GEN-LAST:event_mnuControlXActionPerformed

    private void mnuHRTechV2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHRTechV2ActionPerformed
        HRTechGUI_v2.exibir(this);
    }//GEN-LAST:event_mnuHRTechV2ActionPerformed

    private void mnuSQLiteSophyxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSQLiteSophyxActionPerformed
        SophyxVendaGUI.exibir(this);
    }//GEN-LAST:event_mnuSQLiteSophyxActionPerformed

    private void mnuNATISistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNATISistemasActionPerformed
        NATISistemasGUI.exibir(this);
    }//GEN-LAST:event_mnuNATISistemasActionPerformed

    private void mnuGestorPDVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGestorPDVActionPerformed
        GestorPdvGUI.exibir(this);
    }//GEN-LAST:event_mnuGestorPDVActionPerformed

    private void mnuDirectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDirectorActionPerformed
        DirectorGUI.exibir(this);
    }//GEN-LAST:event_mnuDirectorActionPerformed

    private void mnuRensoftwareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRensoftwareActionPerformed
        RensoftwareGUI.exibir(this);
    }//GEN-LAST:event_mnuRensoftwareActionPerformed

    private void mnuExodusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExodusActionPerformed
        ExodusGUI.exibir(this);
    }//GEN-LAST:event_mnuExodusActionPerformed

    private void mnuSiitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSiitActionPerformed
        // TODO add your handling code here:
        SiitGUI.exibir(this);
    }//GEN-LAST:event_mnuSiitActionPerformed

    private void mnuProtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuProtonActionPerformed
        ProtonGUI.exibir(this);
    }//GEN-LAST:event_mnuProtonActionPerformed

    private void mnuAtenasSQLServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAtenasSQLServerActionPerformed
        AtenasSQLSERVERGUI.exibir(this);
    }//GEN-LAST:event_mnuAtenasSQLServerActionPerformed

    private void mnuG10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuG10ActionPerformed
        G10GUI.exibir(this);
    }//GEN-LAST:event_mnuG10ActionPerformed

    private void mnuiSircomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuiSircomActionPerformed
        SircomGUI.exibir(this);
    }//GEN-LAST:event_mnuiSircomActionPerformed

    private void mnuAccesysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAccesysActionPerformed
        AccesysGUI.exibir(this);
    }//GEN-LAST:event_mnuAccesysActionPerformed

    private void mnuMobnePdvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMobnePdvActionPerformed
        MobnePdvGUI.exibir(this);
    }//GEN-LAST:event_mnuMobnePdvActionPerformed

    private void mnuDevMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDevMasterActionPerformed
        DevMasterGUI.exibir(this);
    }//GEN-LAST:event_mnuDevMasterActionPerformed

    private void mnuLiderNetWorkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLiderNetWorkActionPerformed
        // TODO add your handling code here:
        LiderNetWorkGUI.exibir(this);
    }//GEN-LAST:event_mnuLiderNetWorkActionPerformed

    private void mnuVRToVRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVRToVRActionPerformed
        VRToVR2_5GUI.exibir(this);
    }//GEN-LAST:event_mnuVRToVRActionPerformed

    private void mnuRKSoftwareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRKSoftwareActionPerformed
        // TODO add your handling code here:
        RKSoftwareGUI.exibir(this);
    }//GEN-LAST:event_mnuRKSoftwareActionPerformed

    private void mnuW2AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuW2AActionPerformed
        W2AGUI.exibir(this);
    }//GEN-LAST:event_mnuW2AActionPerformed

    private void mnuAccesys1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAccesys1ActionPerformed
        SnSistemaGUI.exibir(this);
    }//GEN-LAST:event_mnuAccesys1ActionPerformed

    private void mnuBrajanGestoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBrajanGestoresActionPerformed
        // TODO add your handling code here:
        BrajanGestoresGUI.exibir(this);
    }//GEN-LAST:event_mnuBrajanGestoresActionPerformed

    private void mnuLogTecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLogTecActionPerformed
        LogTECGUI.exibir(this);
    }//GEN-LAST:event_mnuLogTecActionPerformed

    private void mnuNCAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNCAActionPerformed
        NCAGUI.exibir(this);
    }//GEN-LAST:event_mnuNCAActionPerformed

    private void mnuiLogusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuiLogusActionPerformed
        LogusRetailGUI.exibir(this);
    }//GEN-LAST:event_mnuiLogusActionPerformed

    private void mnuDelRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDelRegistroActionPerformed
        CleanDataBase.exibir(this);
    }//GEN-LAST:event_mnuDelRegistroActionPerformed

    private void mnuVisualMixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVisualMixActionPerformed
        // TODO add your handling code here:
        VisualMixGUI.exibir(this);
    }//GEN-LAST:event_mnuVisualMixActionPerformed

    private void mnuTecnosoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTecnosoftActionPerformed
        TecnosoftGUI.exibir(this);
    }//GEN-LAST:event_mnuTecnosoftActionPerformed

    private void mnuDJSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDJSystemActionPerformed
        DJSystemGUI.exibir(this);
    }//GEN-LAST:event_mnuDJSystemActionPerformed

    private void mnuPhixaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPhixaActionPerformed
        PhixaGUI.exibir(this);
    }//GEN-LAST:event_mnuPhixaActionPerformed

    private void mnuTpaRootacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTpaRootacActionPerformed
        TpaRootacGUI.exibir(this); //WIN-GE2SQLJG01G\ROOTAC_DB
    }//GEN-LAST:event_mnuTpaRootacActionPerformed

    private void mnuCronos20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCronos20ActionPerformed
        Cronos20GUI.exibir(this);
    }//GEN-LAST:event_mnuCronos20ActionPerformed

    private void mnuViggoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuViggoActionPerformed
        ViggoGUI.exibir(this);
    }//GEN-LAST:event_mnuViggoActionPerformed

    private void mnuLinearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLinearActionPerformed
        LinearGUI.exibir(this);
    }//GEN-LAST:event_mnuLinearActionPerformed

    private void mnuVarejoFacilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVarejoFacilActionPerformed
        // TODO add your handling code here:
        VarejoFacilGUI.exibir(this);
    }//GEN-LAST:event_mnuVarejoFacilActionPerformed

    private void mnuGenericActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGenericActionPerformed
        // TODO add your handling code here:
        GenericGUI.exibir(this);
    }//GEN-LAST:event_mnuGenericActionPerformed

    private void mnuInterDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInterDataActionPerformed
        InterDataGUI.exibir(this);
    }//GEN-LAST:event_mnuInterDataActionPerformed

    private void mnuSTIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSTIActionPerformed
        STIGUI.exibir(this);
    }//GEN-LAST:event_mnuSTIActionPerformed

    private void mnuAutoADMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAutoADMActionPerformed
        AutoAdmGUI.exibir(this);
    }//GEN-LAST:event_mnuAutoADMActionPerformed

    private void mnuAtmaFirebirdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAtmaFirebirdActionPerformed
        AtmaFirebirdGUI.exibir(this);
    }//GEN-LAST:event_mnuAtmaFirebirdActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        LBSoftwareGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItemCupermaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCupermaxActionPerformed
        CupermaxGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemCupermaxActionPerformed

    private void mnuSolidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSolidoActionPerformed
        SolidoGUI.exibir(this);
    }//GEN-LAST:event_mnuSolidoActionPerformed

    private void mnuSaefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSaefActionPerformed
        SaefGUI.exibir(this);
    }//GEN-LAST:event_mnuSaefActionPerformed

    private void mnuMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMasterActionPerformed
        MasterGUI.exibir(this);
    }//GEN-LAST:event_mnuMasterActionPerformed

    private void mnuSTSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSTSistemasActionPerformed
        // TODO add your handling code here:
        STSistemasGUI.exibir(this);
    }//GEN-LAST:event_mnuSTSistemasActionPerformed

    private void mnuSTSistemas_v2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSTSistemas_v2ActionPerformed
        // TODO add your handling code here:
        STSistemasGUI.exibir(this);
    }//GEN-LAST:event_mnuSTSistemas_v2ActionPerformed

    private void mnuEasySacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEasySacActionPerformed
        EasySacGUI.exibir(this);
    }//GEN-LAST:event_mnuEasySacActionPerformed

    private void mnuAthosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAthosActionPerformed
        // TODO add your handling code here:
        AthosGUI.exibir(this);
    }//GEN-LAST:event_mnuAthosActionPerformed

    private void mnuCervantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCervantesActionPerformed
        CervantesGUI.exibir(this);
    }//GEN-LAST:event_mnuCervantesActionPerformed

    private void mnuSysAutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSysAutActionPerformed
        // TODO add your handling code here:
        SysAutGUI.exibir(this);
    }//GEN-LAST:event_mnuSysAutActionPerformed

    private void mnuGDIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGDIActionPerformed
        GDIGUI.exibir(this);
    }//GEN-LAST:event_mnuGDIActionPerformed

    private void mnuDSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDSoftActionPerformed
        // TODO add your handling code here:
        DSoftGUI.exibir(this);
    }//GEN-LAST:event_mnuDSoftActionPerformed

    private void mnuAutocomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAutocomActionPerformed
        // TODO add your handling code here:
        AutocomGUI.exibir(this);
    }//GEN-LAST:event_mnuAutocomActionPerformed

    private void mnuPlenoKWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlenoKWActionPerformed
        // TODO add your handling code here:
        PlenoKWGUI.exibir(this);
    }//GEN-LAST:event_mnuPlenoKWActionPerformed

    private void mnuSicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSicActionPerformed
        // TODO add your handling code here:
        SicGUI.exibir(this);
    }//GEN-LAST:event_mnuSicActionPerformed

    private void mnuMercaLiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMercaLiteActionPerformed
        // TODO add your handling code here:
        MercaLiteGUI.exibir(this);
    }//GEN-LAST:event_mnuMercaLiteActionPerformed

    private void mnuMerceariaSeneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMerceariaSeneActionPerformed
        // TODO add your handling code here:
        MerceariaSeneGUI.exibir(this);
    }//GEN-LAST:event_mnuMerceariaSeneActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formCorrecaoImpostosSuperControle_SuperServer == null || formCorrecaoImpostosSuperControle_SuperServer.isClosed()) {
                formCorrecaoImpostosSuperControle_SuperServer = new CorrecaoImpostosSuperContole_SuperServerGUI(this);
            }
            formCorrecaoImpostosSuperControle_SuperServer.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void mnuGondolaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGondolaActionPerformed
        GondolaGUI.exibir(this);
    }//GEN-LAST:event_mnuGondolaActionPerformed

    private void mnuTeleconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTeleconActionPerformed
        TeleconGUI.exibir(this);
    }//GEN-LAST:event_mnuTeleconActionPerformed

    private void mnuCorrecaoImpostosDSoftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCorrecaoImpostosDSoftActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formCorrecaoImpostosDSoft == null || formCorrecaoImpostosDSoft.isClosed()) {
                formCorrecaoImpostosDSoft = new CorrecaoImpostosDSoftGUI(this);
            }
            formCorrecaoImpostosDSoft.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_mnuCorrecaoImpostosDSoftActionPerformed


    private void mnuFuturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFuturaActionPerformed
        FuturaGUI.exibir(this);
    }//GEN-LAST:event_mnuFuturaActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        PrimeiroPrecoGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        SupermercadoRodriguesGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void mnuIServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIServerActionPerformed
        // TODO add your handling code here:
        IServerGUI.exibir(this);
    }//GEN-LAST:event_mnuIServerActionPerformed

    private void mnuRMSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRMSistemasActionPerformed
        // TODO add your handling code here:
        ResultMaisGUI.exibir(this);
    }//GEN-LAST:event_mnuRMSistemasActionPerformed

    private void mnuPlenusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlenusActionPerformed
        PlenusGUI.exibir(this);
    }//GEN-LAST:event_mnuPlenusActionPerformed

    private void mnuPoligonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPoligonActionPerformed
        PoligonGUI.exibir(this);
    }//GEN-LAST:event_mnuPoligonActionPerformed

    private void chkSGMasterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSGMasterActionPerformed
        SGMasterGUI.exibir(this);
    }//GEN-LAST:event_chkSGMasterActionPerformed

    private void chkLogicBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkLogicBoxActionPerformed
        LogicboxGUI.exibir(this);
    }//GEN-LAST:event_chkLogicBoxActionPerformed
    private void chkGigatronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkGigatronActionPerformed
        GigatronGUI.exibir(this);
    }//GEN-LAST:event_chkGigatronActionPerformed

    private void mnuGComActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGComActionPerformed
        // TODO add your handling code heregti :
        GComGUI.exibir(this);
    }//GEN-LAST:event_mnuGComActionPerformed

    private void mnuControlePlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuControlePlusActionPerformed
        // TODO add your handling code here:
        ControlePlusPostgresGUI.exibir(this);
    }//GEN-LAST:event_mnuControlePlusActionPerformed

    private void mnuMapeamentoDeLojasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMapeamentoDeLojasActionPerformed
        ConsultaConfiguracaoBaseDadosGUI.exibir(this);
    }//GEN-LAST:event_mnuMapeamentoDeLojasActionPerformed

    private void mnuEptusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEptusActionPerformed
        EptusGUI.exibir(this);
    }//GEN-LAST:event_mnuEptusActionPerformed

    private void mnuNeoActionPerformed(java.awt.event.ActionEvent evt) {
        NeoGUI.exibir(this);
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        EcoCentauroGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void mnuPhoenixActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPhoenixActionPerformed
        PhoenixGUI.exibir(this);
    }//GEN-LAST:event_mnuPhoenixActionPerformed

    private void mnuStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuStockActionPerformed
        StockGUI.exibir(this);
    }//GEN-LAST:event_mnuStockActionPerformed

    private void mnuPrimeActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        PrimeGUI.exibir(this);
    }

    private void mnuAutoMacActionPerformed(java.awt.event.ActionEvent evt) {
        AutoMacGUI.exibir(this);
    }

    private void mnuDTComActionPerformed(java.awt.event.ActionEvent evt) {
        DTComPlanilhaGUI.exibir(this);
    }

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        ConsultaBancoDadosGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        ConsultaSistemaGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        // TODO add your handling code here:
        ConsultaSistemaBancoDadosGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        // TODO add your handling code here:
        ConsultaUnidadeGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        // TODO add your handling code here:
        ConsultaUsuarioGUI.exibir(this);
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void mnuSBOnlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSBOnlineActionPerformed
        SBOnlineGUI.exibir(this);
    }//GEN-LAST:event_mnuSBOnlineActionPerformed

    private void mnuMSuperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMSuperActionPerformed
        MSuperGUI.exibir(this);
    }//GEN-LAST:event_mnuMSuperActionPerformed

    private void mnuMRC6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMRC6ActionPerformed
        MRC6GUI.exibir(this);
    }//GEN-LAST:event_mnuMRC6ActionPerformed

    private void mnuLCSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLCSistemaActionPerformed
        LCSistemaGUI.exibir(this);
    }//GEN-LAST:event_mnuLCSistemaActionPerformed

    private void mnuStockPostgresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuStockPostgresActionPerformed
        Stock_PostgresGUI.exibir(this);
    }//GEN-LAST:event_mnuStockPostgresActionPerformed

    private void mnuSuperControleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSuperControleActionPerformed
        SuperControle_PostgresGUI.exibir(this);
    }//GEN-LAST:event_mnuSuperControleActionPerformed

    private void mnuRelatoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRelatoriosActionPerformed
        Object[] options = {"Relatatórios e Sped", "Sped", "Relatórios", "Cancelar"};
        int decisao = JOptionPane.showOptionDialog(null, "Selecionando a primeira opção, serão gerados relatórios fiscais em vr/implantacao/planilhas. \n"
                + "E também será inserido as alterações de código na tabela de SPED.\n\n"
                + "A segunda opção apenas insere dados de SPED.\n"
                + "A terceira opção apenas gera os relatórios fiscais separados por tipo.\n"
                + "\nÉ preciso ter uma importação concluida para os relatórios serem gerados.\n\n",
                "Gerar Relatórios", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (decisao == 0) {
            try {
                new GeradorArquivosRepository().geraPlanilha();
                JOptionPane.showMessageDialog(null, "Relatorios e SPED gerados", "Relatórios e SPED", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (decisao == 1) {
            try {
                new ExecutaSpedDAO().executaSped();
                JOptionPane.showMessageDialog(null, "SPED gerado", "Sped", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        if (decisao == 2) {
            try {
                new GeradorArquivosRepository().geraRelaotirosTexto();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_mnuRelatoriosActionPerformed

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        try {
            new ConversorGUI().setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void mnuSambanetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSambanetActionPerformed
        SambaNetGUI.exibir(this);
    }//GEN-LAST:event_mnuSambanetActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        try {
            new NutricionalArqGUI().setVisible(true);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        try {
            new GeradorProdutoEstoqueFiscal().gerarProdutoEstoqueFiscalTxt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        try {
            new ConversorDbfGUI().setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        try {
            MercadologicoPadraoGUI.exibir(this);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jMenuItem22ActionPerformed

    private void btnMemoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMemoActionPerformed
        try {
            memorandoService.inicializaMemorando();
            MemorandoGUI.exibir(this, this.memorandoService);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnMemoActionPerformed

    private void copiaEntreLojasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copiaEntreLojasActionPerformed

        try {
            CopiaEntreLojasGUI.Exibir(this);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_copiaEntreLojasActionPerformed

    private void gerarCodantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gerarCodantActionPerformed

        try {
            GerarCodantDAO codant = new GerarCodantDAO();
            codant.gerarCodant();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_gerarCodantActionPerformed

    private void copiarPromocoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copiarPromocoesActionPerformed

        try {
            CopiarPromocoesGUI.Exibir(this);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_copiarPromocoesActionPerformed

    private void gerarBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gerarBackupActionPerformed

        try {
            GeradorDeBackupGUI.Exibir(this);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_gerarBackupActionPerformed

    private void zerarEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zerarEstoqueActionPerformed
        
        try {
            ZerarEstoqueGUI.Exibir(this);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_zerarEstoqueActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMemo;
    private javax.swing.JButton btnSair;
    private javax.swing.JMenuItem chkGigatron;
    private javax.swing.JMenuItem chkLogicBox;
    private javax.swing.JMenuItem chkSGMaster;
    private javax.swing.JMenuItem copiaEntreLojas;
    private javax.swing.JMenuItem copiarPromocoes;
    private javax.swing.JMenuItem gerarBackup;
    private javax.swing.JMenuItem gerarCodant;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMenuItemActive;
    private javax.swing.JMenuItem jMenuItemArius;
    private javax.swing.JMenuItem jMenuItemBoechatSoft;
    private javax.swing.JMenuItem jMenuItemCGA;
    private javax.swing.JMenuItem jMenuItemCISS;
    private javax.swing.JMenuItem jMenuItemConcretize;
    private javax.swing.JMenuItem jMenuItemControlWare;
    private javax.swing.JMenuItem jMenuItemCupermax;
    private javax.swing.JMenuItem jMenuItemDestro;
    private javax.swing.JMenuItem jMenuItemDirector;
    private javax.swing.JMenuItem jMenuItemEcosInformatica;
    private javax.swing.JMenuItem jMenuItemEverast;
    private javax.swing.JMenuItem jMenuItemFMSistemas;
    private javax.swing.JMenuItem jMenuItemGCF;
    private javax.swing.JMenuItem jMenuItemGR7;
    private javax.swing.JMenuItem jMenuItemGdoor;
    private javax.swing.JMenuItem jMenuItemGetWay;
    private javax.swing.JMenuItem jMenuItemGuiaSistemas;
    private javax.swing.JMenuItem jMenuItemIdeal;
    private javax.swing.JMenuItem jMenuItemInfoBrasil;
    private javax.swing.JMenuItem jMenuItemJMaster;
    private javax.swing.JMenuItem jMenuItemKairos;
    private javax.swing.JMenuItem jMenuItemMilenio;
    private javax.swing.JMenuItem jMenuItemMobility;
    private javax.swing.JMenuItem jMenuItemOrion;
    private javax.swing.JMenuItem jMenuItemRMS;
    private javax.swing.JMenuItem jMenuItemSBOMarket;
    private javax.swing.JMenuItem jMenuItemSHI;
    private javax.swing.JMenuItem jMenuItemSIMS;
    private javax.swing.JMenuItem jMenuItemSacLumi;
    private javax.swing.JMenuItem jMenuItemSci;
    private javax.swing.JMenuItem jMenuItemSics;
    private javax.swing.JMenuItem jMenuItemSimSoft;
    private javax.swing.JMenuItem jMenuItemSofgce;
    private javax.swing.JMenuItem jMenuItemSoftaEx;
    private javax.swing.JMenuItem jMenuItemSuperServer;
    private javax.swing.JMenuItem jMenuItemSuperus;
    private javax.swing.JMenuItem jMenuItemSysMoura;
    private javax.swing.JMenuItem jMenuItemSysPDV;
    private javax.swing.JMenuItem jMenuItemSysPDVSQLServer;
    private javax.swing.JMenuItem jMenuItemTiTecnologia;
    private javax.swing.JMenuItem jMenuItemTopSystem;
    private javax.swing.JMenuItem jMenuItemUltraSyst;
    private javax.swing.JMenuItem jMenuItemWisaSoft;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private vrframework.bean.label.VRLabel lblData;
    private vrframework.bean.label.VRLabel lblUnidade;
    private vrframework.bean.label.VRLabel lblUsuario;
    private vrframework.bean.label.VRLabel lblVersao;
    private javax.swing.JMenuItem mmnuDGCom;
    private javax.swing.JMenuItem mmnuGestora;
    private javax.swing.JMenu mnuADS;
    private javax.swing.JMenuItem mnuASoft;
    private javax.swing.JMenu mnuAccess;
    private javax.swing.JMenuItem mnuAccesys;
    private javax.swing.JMenuItem mnuAccesys1;
    private javax.swing.JMenuItem mnuAcertarIdsProdutos;
    private javax.swing.JMenuItem mnuAcom;
    private javax.swing.JMenuItem mnuAdmRioPreto;
    private javax.swing.JMenu mnuAjuda;
    private javax.swing.JMenu mnuAjuda1;
    private javax.swing.JMenuItem mnuAjudaSobre;
    private javax.swing.JMenuItem mnuAlphaSys;
    private javax.swing.JMenuItem mnuAlterDataWShop;
    private javax.swing.JMenu mnuAlteracaoID;
    private javax.swing.JMenuItem mnuApollo;
    private javax.swing.JMenuItem mnuArtSystem;
    private javax.swing.JMenuItem mnuAsefe;
    private javax.swing.JMenuItem mnuAtenas;
    private javax.swing.JMenuItem mnuAtenasSQLServer;
    private javax.swing.JMenuItem mnuAthos;
    private javax.swing.JMenuItem mnuAtma;
    private javax.swing.JMenuItem mnuAtmaFirebird;
    private javax.swing.JMenuItem mnuAutoADM;
    private javax.swing.JMenuItem mnuAutoMac;
    private javax.swing.JMenuItem mnuAutoMac1;
    private javax.swing.JMenuItem mnuAutoSystem;
    private javax.swing.JMenuItem mnuAutoSystem1;
    private javax.swing.JMenuItem mnuAutocom;
    private javax.swing.JMenuItem mnuAutomaq;
    private javax.swing.JMenu mnuAvancadas;
    private javax.swing.JMenuItem mnuAvance;
    private javax.swing.JMenuItem mnuBase;
    private javax.swing.JMenuItem mnuBrainSoft;
    private javax.swing.JMenuItem mnuBrajanGestores;
    private javax.swing.JMenuItem mnuCFSoftSiaECF;
    private javax.swing.JMenuItem mnuCPGestor;
    private javax.swing.JMenu mnuCache;
    private javax.swing.JMenuItem mnuCadastraFacil;
    private javax.swing.JMenu mnuCadastro;
    private javax.swing.JMenu mnuCadastro2_5;
    private javax.swing.JMenu mnuCadastros;
    private javax.swing.JMenuItem mnuCefas;
    private javax.swing.JMenuItem mnuCerebro;
    private javax.swing.JMenuItem mnuCervantes;
    private javax.swing.JMenuItem mnuClick;
    private javax.swing.JMenuItem mnuCodigoBarrasAtacado;
    private javax.swing.JMenuItem mnuContaPagar;
    private javax.swing.JMenuItem mnuControlX;
    private javax.swing.JMenuItem mnuControlePlus;
    private javax.swing.JMenuItem mnuControll;
    private javax.swing.JMenuItem mnuCorrecaoImpostosDSoft;
    private javax.swing.JMenuItem mnuCplus;
    private javax.swing.JMenuItem mnuCronos20;
    private javax.swing.JMenu mnuDB2;
    private javax.swing.JMenuItem mnuDJSystem;
    private javax.swing.JMenuItem mnuDLink;
    private javax.swing.JMenuItem mnuDSoft;
    private javax.swing.JMenuItem mnuDTCom;
    private javax.swing.JMenuItem mnuDataSync;
    private javax.swing.JMenu mnuDatabase;
    private javax.swing.JMenuItem mnuDelRegistro;
    private javax.swing.JMenuItem mnuDelfi;
    private javax.swing.JMenuItem mnuDevMaster;
    private javax.swing.JMenuItem mnuDirector;
    private javax.swing.JMenuItem mnuDtCom;
    private javax.swing.JMenuItem mnuEasySac;
    private javax.swing.JMenuItem mnuEditarConexoes;
    private javax.swing.JMenuItem mnuEmporio;
    private javax.swing.JMenuItem mnuEptus;
    private javax.swing.JMenuItem mnuEsSystem;
    private javax.swing.JMenu mnuEspeciais;
    private javax.swing.JMenuItem mnuEtica;
    private javax.swing.JMenuItem mnuExodus;
    private javax.swing.JMenuItem mnuFG;
    private javax.swing.JMenuItem mnuFHOnline;
    private javax.swing.JMenuItem mnuFabTech;
    private javax.swing.JMenuItem mnuFabTech1;
    private javax.swing.JMenuItem mnuFacilite;
    private javax.swing.JMenuItem mnuFarm2000;
    private javax.swing.JMenuItem mnuFenix;
    private javax.swing.JMenu mnuFerramentas;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuFirebird;
    private javax.swing.JMenu mnuFirebird2;
    private javax.swing.JMenu mnuFirebird3;
    private javax.swing.JMenuItem mnuFlash;
    private javax.swing.JMenuItem mnuFlatan;
    private javax.swing.JMenuItem mnuFort;
    private javax.swing.JMenuItem mnuForti;
    private javax.swing.JMenuItem mnuFutura;
    private javax.swing.JMenuItem mnuG10;
    private javax.swing.JMenuItem mnuG3;
    private javax.swing.JMenuItem mnuG4;
    private javax.swing.JMenuItem mnuGCom;
    private javax.swing.JMenuItem mnuGDI;
    private javax.swing.JMenuItem mnuGDoor;
    private javax.swing.JMenuItem mnuGTech;
    private javax.swing.JMenuItem mnuGeneric;
    private javax.swing.JMenuItem mnuGestorPDV;
    private javax.swing.JMenuItem mnuGondola;
    private javax.swing.JMenuItem mnuHRTechV1;
    private javax.swing.JMenuItem mnuHRTechV2;
    private javax.swing.JMenuItem mnuHercules;
    private javax.swing.JMenuItem mnuHipcom;
    private javax.swing.JMenuItem mnuHiper;
    private javax.swing.JMenuItem mnuHipicom;
    private javax.swing.JMenuItem mnuHostMundo;
    private javax.swing.JMenuItem mnuIQSistemas;
    private javax.swing.JMenuItem mnuIServer;
    private javax.swing.JMenuItem mnuIcommerce;
    private javax.swing.JMenuItem mnuIdealSoft;
    private javax.swing.JMenu mnuImpSistema;
    private javax.swing.JMenuItem mnuImportarNCM;
    private javax.swing.JMenuItem mnuImportarNfce;
    private javax.swing.JMenuItem mnuInfoMacStore;
    private javax.swing.JMenu mnuInformix;
    private javax.swing.JMenuItem mnuIntellicon;
    private javax.swing.JMenuItem mnuInterData;
    private javax.swing.JMenuItem mnuInteragem;
    private javax.swing.JMenu mnuInterface;
    private javax.swing.JMenuItem mnuInventer;
    private javax.swing.JMenuItem mnuJM2Online;
    private javax.swing.JMenuItem mnuJacsys;
    private javax.swing.JMenu mnuJanela;
    private javax.swing.JMenuItem mnuJrf;
    private javax.swing.JMenuItem mnuKcms;
    private javax.swing.JMenuItem mnuLCSistema;
    private javax.swing.JMenuItem mnuLiderNetWork;
    private javax.swing.JMenuItem mnuLince;
    private javax.swing.JMenuItem mnuLinear;
    private javax.swing.JMenuItem mnuLinner;
    private javax.swing.JMenuItem mnuLiteci;
    private javax.swing.JMenuItem mnuLogTec;
    private javax.swing.JMenuItem mnuLogVenda;
    private javax.swing.JMenuItem mnuLogus;
    private javax.swing.JMenuItem mnuLoja;
    private javax.swing.JMenuItem mnuLyncis;
    private javax.swing.JMenuItem mnuMRC6;
    private javax.swing.JMenuItem mnuMSIInfor;
    private javax.swing.JMenuItem mnuMSuper;
    private javax.swing.JMenuItem mnuMapeamentoDeLojas;
    private javax.swing.JMenuItem mnuMarket;
    private javax.swing.JMenuItem mnuMaster;
    private javax.swing.JMenuBar mnuMenu;
    private javax.swing.JMenuItem mnuMercaLite;
    private javax.swing.JMenuItem mnuMerceariaSene;
    private javax.swing.JMenuItem mnuMobnePdv;
    private javax.swing.JMenuItem mnuMrs;
    private javax.swing.JMenuItem mnuMrs1;
    private javax.swing.JMenuItem mnuMrs2;
    private javax.swing.JMenu mnuMySQL;
    private javax.swing.JMenu mnuMySQL2;
    private javax.swing.JMenuItem mnuNATISistemas;
    private javax.swing.JMenuItem mnuNCA;
    private javax.swing.JMenuItem mnuNFe;
    private javax.swing.JMenuItem mnuNeo;
    private javax.swing.JMenuItem mnuOpen;
    private javax.swing.JMenu mnuOracle;
    private javax.swing.JMenu mnuOrion;
    private javax.swing.JMenuItem mnuOryon;
    private javax.swing.JMenu mnuParadox;
    private javax.swing.JMenuItem mnuParametros;
    private javax.swing.JMenuItem mnuParametros1;
    private javax.swing.JMenuItem mnuPhixa;
    private javax.swing.JMenuItem mnuPhoenix;
    private javax.swing.JMenu mnuPlanilha;
    private javax.swing.JMenu mnuPlanilhaEspecifica;
    private javax.swing.JMenuItem mnuPlanilhaProduto;
    private javax.swing.JMenuItem mnuPlanilhaV2;
    private javax.swing.JMenuItem mnuPlanilhaVr;
    private javax.swing.JMenuItem mnuPlenoKW;
    private javax.swing.JMenuItem mnuPlenus;
    private javax.swing.JMenuItem mnuPoligon;
    private javax.swing.JMenu mnuPostgres;
    private javax.swing.JMenu mnuPostgres2;
    private javax.swing.JMenuItem mnuPrime;
    private javax.swing.JMenuItem mnuProton;
    private javax.swing.JMenuItem mnuPwGestor;
    private javax.swing.JMenuItem mnuPws;
    private javax.swing.JMenuItem mnuRCNet;
    private javax.swing.JMenuItem mnuRKSoftware;
    private javax.swing.JMenuItem mnuRMS_2;
    private javax.swing.JMenuItem mnuRMSistemas;
    private javax.swing.JMenuItem mnuRPInfo;
    private javax.swing.JMenuItem mnuRelatorios;
    private javax.swing.JMenuItem mnuRensoftware;
    private javax.swing.JMenuItem mnuRepleis;
    private javax.swing.JMenuItem mnuSBOnline;
    private javax.swing.JMenuItem mnuSDInformatica;
    private javax.swing.JMenu mnuSQLServer;
    private javax.swing.JMenu mnuSQLServer2;
    private javax.swing.JMenu mnuSQLServer3;
    private javax.swing.JMenu mnuSQLite;
    private javax.swing.JMenuItem mnuSQLiteSophyx;
    private javax.swing.JMenuItem mnuSTI;
    private javax.swing.JMenuItem mnuSTI3;
    private javax.swing.JMenuItem mnuSTSistemas;
    private javax.swing.JMenuItem mnuSTSistemas_v2;
    private javax.swing.JMenuItem mnuSaac;
    private javax.swing.JMenuItem mnuSaef;
    private javax.swing.JMenuItem mnuSambaNet;
    private javax.swing.JMenuItem mnuSambaNetGetWay;
    private javax.swing.JMenuItem mnuSambanet;
    private javax.swing.JMenuItem mnuSatecfe;
    private javax.swing.JMenuItem mnuSav;
    private javax.swing.JMenuItem mnuScef;
    private javax.swing.JMenuItem mnuSiaCriareByFile;
    private javax.swing.JMenuItem mnuSiaCriareDbf;
    private javax.swing.JMenuItem mnuSic;
    private javax.swing.JMenuItem mnuSicom;
    private javax.swing.JMenuItem mnuSifat;
    private javax.swing.JMenuItem mnuSigma;
    private javax.swing.JMenuItem mnuSiit;
    private javax.swing.JMenu mnuSistema;
    private javax.swing.JMenuItem mnuSistemaLogin;
    private javax.swing.JMenuItem mnuSoftcom;
    private javax.swing.JMenuItem mnuSofttech;
    private javax.swing.JMenuItem mnuSolido;
    private javax.swing.JMenuItem mnuSolidus;
    private javax.swing.JMenuItem mnuSolutionSupera;
    private javax.swing.JMenuItem mnuSri;
    private javax.swing.JMenuItem mnuStock;
    private javax.swing.JMenuItem mnuStockPostgres;
    private javax.swing.JMenuItem mnuSuper;
    private javax.swing.JMenuItem mnuSuperControle;
    private javax.swing.JMenuItem mnuSuperLoja10;
    private javax.swing.JMenuItem mnuSyncTech;
    private javax.swing.JMenuItem mnuSysAut;
    private javax.swing.JMenuItem mnuSysmoFirebird;
    private javax.swing.JMenuItem mnuSysmoPostgres;
    private javax.swing.JMenuItem mnuTecnosoft;
    private javax.swing.JMenuItem mnuTelecon;
    private javax.swing.JMenuItem mnuTga;
    private javax.swing.JMenuItem mnuThotau;
    private javax.swing.JMenuItem mnuTpaRootac;
    private javax.swing.JMenuItem mnuTsti;
    private javax.swing.JMenuItem mnuUmPontoDois;
    private javax.swing.JMenuItem mnuUniplus;
    private javax.swing.JMenuItem mnuUpForti;
    private javax.swing.JMenuItem mnuVCash;
    private javax.swing.JMenuItem mnuVRPdv;
    private javax.swing.JMenuItem mnuVRToVR;
    private javax.swing.JMenuItem mnuVarejoFacil;
    private javax.swing.JMenuItem mnuViaSoft;
    private javax.swing.JMenuItem mnuViggo;
    private javax.swing.JMenuItem mnuVisualComercio;
    private javax.swing.JMenuItem mnuVisualMix;
    private javax.swing.JMenuItem mnuW2A;
    private javax.swing.JMenuItem mnuWebsac;
    private javax.swing.JMenuItem mnuWinNexus;
    private javax.swing.JMenuItem mnuWinthor_PcSistemas;
    private javax.swing.JMenuItem mnuWmByFile;
    private javax.swing.JMenuItem mnuWmsi;
    private javax.swing.JMenuItem mnuWmsi1;
    private javax.swing.JMenuItem mnuZpf;
    private javax.swing.JMenuItem mnuiLogus;
    private javax.swing.JMenuItem mnuiSircom;
    private javax.swing.JMenuItem mnuiSophyx;
    private javax.swing.JMenuItem mnuiSysERP;
    private javax.swing.JMenuItem mnuiWeber;
    private javax.swing.JMenuItem mnupdvvendaitem;
    private vrframework.bean.toolBar.VRToolBar tlbAtalho;
    private vrframework.bean.toolBar.VRToolBar tlbFixo;
    private javax.swing.JToolBar tlbToolBar;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel5;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.desktopPane.VRDesktopPane vrDesktopPane;
    private javax.swing.JMenuItem zerarEstoque;
    // End of variables declaration//GEN-END:variables

    /**
     * Verifica se foi configurado os parâmetros, caso não tenha sido abre o
     * form de configuração.
     */
    public void checkParametros() {
        if (!Parametros.get().isParametroConfigurado()) {
            ParametroGUI.Exibir(this);
        }
    }

    public void verificarLite() {
        String param = Parametros.lite;
        if (param != null && !"".equals(param)) {
            mnuMenu.setVisible(false);

            if ("lince".equals(param)) {
                LinceGUI.exibir(this, true);
            }
        }
    }
}
// IMPLANTACAO
