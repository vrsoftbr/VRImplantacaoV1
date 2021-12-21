/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

import vrimplantacao.dao.interfaces.AriusDAO;
import vrimplantacao2.dao.interfaces.ASoftDAO;
import vrimplantacao2.dao.interfaces.AccesysDAO;
import vrimplantacao2.dao.interfaces.AcomDAO;
import vrimplantacao2.dao.interfaces.AlphaSysDAO;
import vrimplantacao2.dao.interfaces.ApolloDAO;
import vrimplantacao2.dao.interfaces.ArtSystemDAO;
import vrimplantacao2.dao.interfaces.AsefeDAO;
import vrimplantacao2.dao.interfaces.AtenasDAO;
import vrimplantacao2.dao.interfaces.AthosDAO;
import vrimplantacao2.dao.interfaces.AtmaDAO;
import vrimplantacao2.dao.interfaces.AutoAdmDAO;
import vrimplantacao2.dao.interfaces.AutoSystemDAO;
import vrimplantacao2.dao.interfaces.AutocomDAO;
import vrimplantacao2.dao.interfaces.AutomaqDAO;
import vrimplantacao2.dao.interfaces.AvanceDAO;
import vrimplantacao2.dao.interfaces.AvistareDAO;
import vrimplantacao2.dao.interfaces.BaseDAO;
import vrimplantacao2.dao.interfaces.BrainSoftDAO;
import vrimplantacao2.dao.interfaces.BrajanGestoresDAO;
import vrimplantacao2.dao.interfaces.CgaDAO;
import vrimplantacao2.dao.interfaces.CFSoftSiaECFDAO;
import vrimplantacao2.dao.interfaces.CPGestorDAO;
import vrimplantacao2.dao.interfaces.CPlusDAO;
import vrimplantacao2.dao.interfaces.CadastraFacilDAO;
import vrimplantacao2.dao.interfaces.CefasDAO;
import vrimplantacao2.dao.interfaces.CerebroDAO;
import vrimplantacao2.dao.interfaces.CervantesDAO;
import vrimplantacao2.dao.interfaces.CissDAO;
import vrimplantacao2.dao.interfaces.ClickDAO;
import vrimplantacao2.dao.interfaces.ContechDAO;
import vrimplantacao2.dao.interfaces.ControlWareDAO;
import vrimplantacao2.dao.interfaces.ControlXDAO;
import vrimplantacao2.dao.interfaces.Cronos20DAO;
import vrimplantacao2.dao.interfaces.CupermaxDAO;
import vrimplantacao2.dao.interfaces.DJSystemDAO;
import vrimplantacao2.dao.interfaces.DLinkDAO;
import vrimplantacao2.dao.interfaces.DSoftDAO;
import vrimplantacao2.dao.interfaces.DataSyncDAO;
import vrimplantacao2.dao.interfaces.DelfiDAO;
import vrimplantacao2.dao.interfaces.DestroDAO;
import vrimplantacao2.dao.interfaces.DevMasterDAO;
import vrimplantacao2.dao.interfaces.DirectorDAO;
import vrimplantacao2.dao.interfaces.DtComDAO;
import vrimplantacao2.dao.interfaces.EasySacDAO;
import vrimplantacao2.dao.interfaces.EmporioDAO;
import vrimplantacao2.dao.interfaces.EptusDAO;
import vrimplantacao2.dao.interfaces.EsSystemDAO;
import vrimplantacao2.dao.interfaces.EticaDAO;
import vrimplantacao2.dao.interfaces.ExodusDAO;
import vrimplantacao2.dao.interfaces.FHOnlineDAO;
import vrimplantacao2.dao.interfaces.FabTechDAO;
import vrimplantacao2.dao.interfaces.FaciliteDAO;
import vrimplantacao2.dao.interfaces.Farm2000DAO;
import vrimplantacao2.dao.interfaces.FenixDAO;
import vrimplantacao2.dao.interfaces.FlashDAO;
import vrimplantacao2.dao.interfaces.FlatanDAO;
import vrimplantacao2.dao.interfaces.FortDAO;
import vrimplantacao2.dao.interfaces.FuturaDAO;
import vrimplantacao2.dao.interfaces.G10DAO;
import vrimplantacao2.dao.interfaces.G3DAO;
import vrimplantacao2.dao.interfaces.GComDAO;
import vrimplantacao2.dao.interfaces.GDIDAO;
import vrimplantacao2.dao.interfaces.GDoorDAO;
import vrimplantacao2.dao.interfaces.GR7DAO;
import vrimplantacao2.dao.interfaces.GTechDAO;
import vrimplantacao2.dao.interfaces.GZSistemasDAO;
import vrimplantacao2.dao.interfaces.GestorPdvDAO;
import vrimplantacao2.dao.interfaces.GetWay_ProfitDAO;
import vrimplantacao2.dao.interfaces.GigatronDAO;
import vrimplantacao2.dao.interfaces.GondolaDAO;
import vrimplantacao2.dao.interfaces.GuiaSistemasDAO;
import vrimplantacao2.dao.interfaces.HRTechDAO;
import vrimplantacao2.dao.interfaces.HerculesIntCashDAO;
import vrimplantacao2_5.dao.sistema.HipcomDAO;
import vrimplantacao2.dao.interfaces.HiperDAO;
import vrimplantacao2.dao.interfaces.ICommerceDAO;
import vrimplantacao2.dao.interfaces.IQSistemasDAO;
import vrimplantacao2.dao.interfaces.IServerDAO;
import vrimplantacao2.dao.interfaces.InfoBrasilDAO;
import vrimplantacao2.dao.interfaces.InfoMacDAO;
import vrimplantacao2.dao.interfaces.InovaDAO;
import vrimplantacao2.dao.interfaces.IntelliCashDAO;
import vrimplantacao2.dao.interfaces.IntelliconDAO;
import vrimplantacao2.dao.interfaces.InterDataDAO;
import vrimplantacao2.dao.interfaces.InterageDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.dao.interfaces.InventerDAO;
import vrimplantacao2.dao.interfaces.JM2OnlineDAO;
import vrimplantacao2.dao.interfaces.JMasterDAO;
import vrimplantacao2.dao.interfaces.JacsysDAO;
import vrimplantacao2.dao.interfaces.JrfDAO;
import vrimplantacao2.dao.interfaces.KairosDAO;
import vrimplantacao2.dao.interfaces.KcmsDAO;
import vrimplantacao2.dao.interfaces.LBSoftwareDAO;
import vrimplantacao2.dao.interfaces.LiderNetWorkDAO;
import vrimplantacao2.dao.interfaces.LinceDAO;
import vrimplantacao2.dao.interfaces.LinnerDAO;
import vrimplantacao2.dao.interfaces.LiteciDAO;
import vrimplantacao2.dao.interfaces.LogTECDAO;
import vrimplantacao2.dao.interfaces.LogicboxDAO;
import vrimplantacao2.dao.interfaces.LogusDAO;
import vrimplantacao2.dao.interfaces.LyncisDAO;
import vrimplantacao2.dao.interfaces.MSIInforDAO;
import vrimplantacao2.dao.interfaces.MarketDAO;
import vrimplantacao2.dao.interfaces.MasterDAO;
import vrimplantacao2.dao.interfaces.MercaLiteDAO;
import vrimplantacao2.dao.interfaces.MilenioDAO;
import vrimplantacao2.dao.interfaces.MobilityDAO;
import vrimplantacao2.dao.interfaces.MobnePdvDAO;
import vrimplantacao2.dao.interfaces.MrsDAO;
import vrimplantacao2.dao.interfaces.MultiPdvDAO;
import vrimplantacao2.dao.interfaces.NATISistemasDAO;
import vrimplantacao2.dao.interfaces.NCADAO;
import vrimplantacao2.dao.interfaces.OpenDAO;
import vrimplantacao2.dao.interfaces.OrionDAO;
import vrimplantacao2.dao.interfaces.OrionTechDAO;
import vrimplantacao2.dao.interfaces.OryonDAO;
import vrimplantacao2.dao.interfaces.PhixaDAO;
import vrimplantacao2.dao.interfaces.PlenoKWDAO;
import vrimplantacao2.dao.interfaces.PlenusDAO;
import vrimplantacao2.dao.interfaces.PoligonDAO;
import vrimplantacao2.dao.interfaces.PomaresDAO;
import vrimplantacao2.dao.interfaces.ProtonDAO;
import vrimplantacao2.dao.interfaces.PwGestorDAO;
import vrimplantacao2.dao.interfaces.PwsDAO;
import vrimplantacao2.dao.interfaces.RCNetDAO;
import vrimplantacao2.dao.interfaces.RKSoftwareDAO;
import vrimplantacao2.dao.interfaces.RMSAutomaHelpDAO;
import vrimplantacao2.dao.interfaces.RMSDAO;
import vrimplantacao2.dao.interfaces.RPInfoDAO;
import vrimplantacao2.dao.interfaces.RensoftwareDAO;
import vrimplantacao2.dao.interfaces.RepleisDAO;
import vrimplantacao2.dao.interfaces.ResultMaisDAO;
import vrimplantacao2.dao.interfaces.RootacDAO;
import vrimplantacao2.dao.interfaces.SDInformaticaDAO;
import vrimplantacao2.dao.interfaces.SGMasterDAO;
import vrimplantacao2.dao.interfaces.SIMSDAO;
import vrimplantacao2.dao.interfaces.STIDAO;
import vrimplantacao2.dao.interfaces.STSitemasDAO;
import vrimplantacao2.dao.interfaces.SaacDAO;
import vrimplantacao2.dao.interfaces.SabTechDAO;
import vrimplantacao2.dao.interfaces.SaefDAO;
import vrimplantacao2.dao.interfaces.SambaNetV2DAO;
import vrimplantacao2.dao.interfaces.SatecfeDAO;
import vrimplantacao2.dao.interfaces.SavDAO;
import vrimplantacao2.dao.interfaces.ScefDAO;
import vrimplantacao2.dao.interfaces.ShiDAO;
import vrimplantacao2.dao.interfaces.SiacDAO;
import vrimplantacao2.dao.interfaces.SifatDAO;
import vrimplantacao2.dao.interfaces.SigmaDAO;
import vrimplantacao2.dao.interfaces.SiitDAO;
import vrimplantacao2.dao.interfaces.SircomDAO;
import vrimplantacao2.dao.interfaces.SisMouraDAO;
import vrimplantacao2.dao.interfaces.SnSistemaDAO;
import vrimplantacao2.dao.interfaces.SoftcomDAO;
import vrimplantacao2.dao.interfaces.SofttechDAO;
import vrimplantacao2.dao.interfaces.SolidoDAO;
import vrimplantacao2.dao.interfaces.SolidusDAO;
import vrimplantacao2.dao.interfaces.SolutionSuperaDAO;
import vrimplantacao2.dao.interfaces.SophyxDAO;
import vrimplantacao2.dao.interfaces.SriDAO;
import vrimplantacao2.dao.interfaces.SuperDAO;
import vrimplantacao2.dao.interfaces.SuperLoja10DAO;
import vrimplantacao2.dao.interfaces.SuperusDAO;
import vrimplantacao2.dao.interfaces.SyncTecDAO;
import vrimplantacao2.dao.interfaces.SysAutDAO;
import vrimplantacao2.dao.interfaces.SysERPDAO;
import vrimplantacao2.dao.interfaces.SysPdvDAO;
import vrimplantacao2.dao.interfaces.TGADAO;
import vrimplantacao2.dao.interfaces.TecnosoftDAO;
import vrimplantacao2.dao.interfaces.TeleconDAO;
import vrimplantacao2.dao.interfaces.TiTecnologiaDAO;
import vrimplantacao2.dao.interfaces.TopSystemDAO;
import vrimplantacao2.dao.interfaces.TpaRootacDAO;
import vrimplantacao2.dao.interfaces.TstiDAO;
import vrimplantacao2.dao.interfaces.UniplusDAO;
import vrimplantacao2.dao.interfaces.UpFortiDAO;
import vrimplantacao2.dao.interfaces.VCashDAO;
import vrimplantacao2.dao.interfaces.VRToVRDAO;
import vrimplantacao2.dao.interfaces.ViaSoftDAO;
import vrimplantacao2.dao.interfaces.ViggoDAO;
import vrimplantacao2.dao.interfaces.VisualComercioDAO;
import vrimplantacao2.dao.interfaces.VisualMixDAO;
import vrimplantacao2.dao.interfaces.W2ADAO;
import vrimplantacao2.dao.interfaces.WebSaqDAO;
import vrimplantacao2.dao.interfaces.WeberDAO;
import vrimplantacao2.dao.interfaces.WinNexusDAO;
import vrimplantacao2.dao.interfaces.WisaSoftDAO;
import vrimplantacao2.dao.interfaces.WmsiDAO;
import vrimplantacao2.dao.interfaces.ZoomboxDAO;
import vrimplantacao2.dao.interfaces.ZpfDAO;
import vrimplantacao2.dao.interfaces.linear.LinearDAO;
import vrimplantacao2_5.dao.sistema.BomSoftDAO;
import vrimplantacao2_5.dao.sistema.AssistDAO;
import vrimplantacao2_5.dao.sistema.Dobes_CgaDAO;
import vrimplantacao2_5.dao.sistema.GatewaySistemasDAO;
import vrimplantacao2_5.dao.sistema.MRC6DAO;
import vrimplantacao2_5.dao.sistema.MicroTabDAO;
import vrimplantacao2_5.dao.sistema.SGDAO;
import vrimplantacao2_5.dao.sistema.SygmaDAO;

/**
 *
 * @author Desenvolvimento
 */
public enum ESistema {

    ASOFT(1, "ASOFT", new ASoftDAO()),
    ACCESYS(2, "ACCESYS", new AccesysDAO()),
    ACOM(3, "ACOM", new AcomDAO()),
    ALPHASYS(4, "ALPHASYS", new AlphaSysDAO()),
    APOLLO(5, "APOLLO", new ApolloDAO()),
    ARTSYSTEM(6, "ARTSYSTEM", new ArtSystemDAO()),
    ASEFE(7, "ASEFE", new AsefeDAO()),
    ATENAS(8, "ATENAS", new AtenasDAO()),
    ATHOS(9, "ATHOS", new AthosDAO()),
    ATMA(10, "ATMA", new AtmaDAO()),
    AUTOADM(11, "AUTOADM", new AutoAdmDAO()),
    AUTOSYSTEM(12, "AUTOSYSTEM", new AutoSystemDAO()),
    AUTOCOM(13, "AUTOCOM", new AutocomDAO()),
    AUTOMAQ(14, "AUTOMAQ", new AutomaqDAO()),
    AVANCE(15, "AVANCE", new AvanceDAO()),
    AVISTARE(16, "AVISTARE", new AvistareDAO()),
    BASE(17, "BASE", new BaseDAO()),
    BRAINSOFT(18, "BRAINSOFT", new BrainSoftDAO()),
    BRAJANGESTORES(19, "BRAJANGESTORES", new BrajanGestoresDAO()),
    CFSOFTSIAECF(20, "CFSOFTSIAECF", new CFSoftSiaECFDAO()),
    CPGESTOR(21, "CPGESTOR", new CPGestorDAO()),
    CPLUS(22, "CPLUS", new CPlusDAO()),
    CADASTRAFACIL(23, "CADASTRAFACIL", new CadastraFacilDAO()),
    CEFAS(24, "CEFAS", new CefasDAO()),
    CEREBRO(25, "CEREBRO", new CerebroDAO()),
    CERVANTES(26, "CERVANTES", new CervantesDAO()),
    CGA(27, "CGA", new CgaDAO()),
    CISS(28, "CISS", new CissDAO()),
    CLICK(29, "CLICK", new ClickDAO()),
    CONTECH(30, "CONTECH", new ContechDAO()),
    CONTROLWARE(31, "CONTROLWARE", new ControlWareDAO()),
    CONTROLX(32, "CONTROLX", new ControlXDAO()),
    CRONOS20(33, "CRONOS20", new Cronos20DAO()),
    CUPERMAX(34, "CUPERMAX", new CupermaxDAO()),
    DJSYSTEM(35, "DJSYSTEM", new DJSystemDAO()),
    DLINK(36, "DLINK", new DLinkDAO()),
    DSOFT(37, "DSOFT", new DSoftDAO()),
    DATASYNC(38, "DATASYNC", new DataSyncDAO()),
    DELFI(39, "DELFI", new DelfiDAO()),
    DESTRO(40, "DESTRO", new DestroDAO()),
    DEVMASTER(41, "DEVMASTER", new DevMasterDAO()),
    DIRECTOR(42, "DIRECTOR", new DirectorDAO()),
    DTCOM(43, "DTCOM", new DtComDAO()),
    EASYSAC(44, "EASYSAC", new EasySacDAO()),
    EMPORIO(45, "EMPORIO", new EmporioDAO()),
    EPTUS(46, "EPTUS", new EptusDAO()),
    ESSYSTEM(47, "ESSYSTEM", new EsSystemDAO()),
    ETICA(48, "ETICA", new EticaDAO()),
    EXODUS(49, "EXODUS", new ExodusDAO()),
    FHONLINE(50, "FHONLINE", new FHOnlineDAO()),
    FABTECH(51, "FABTECH", new FabTechDAO()),
    FACILITE(52, "FACILITE", new FaciliteDAO()),
    FARM2000(53, "FARM2000", new Farm2000DAO()),
    FENIX(54, "FENIX", new FenixDAO()),
    FLASH(55, "FLASH", new FlashDAO()),
    FLATAN(56, "FLATAN", new FlatanDAO()),
    FORT(57, "FORT", new FortDAO()),
    FUTURA(58, "FUTURA", new FuturaDAO()),
    G10(59, "G10", new G10DAO()),
    G3(60, "G3", new G3DAO()),
    GCOM(61, "GCOM", new GComDAO()),
    GDI(62, "GDI", new GDIDAO()),
    GDOOR(63, "GDOOR", new GDoorDAO()),
    GR7(64, "GR7", new GR7DAO()),
    GTECH(65, "GTECH", new GTechDAO()),
    GZSISTEMAS(66, "GZSISTEMAS", new GZSistemasDAO()),
    GESTORPDV(67, "GESTORPDV", new GestorPdvDAO()),
    GETWAY(68, "GETWAY", new GetWay_ProfitDAO()),
    GIGATRON(69, "GIGATRON", new GigatronDAO()),
    GONDOLA(70, "GONDOLA", new GondolaDAO()),
    GUIASISTEMAS(71, "GUIASISTEMAS", new GuiaSistemasDAO()),
    HRTECH(72, "HRTECH", new HRTechDAO()),
    HERCULESINTCASH(73, "HERCULESINTCASH", new HerculesIntCashDAO()),
    HIPCOM(74, "HIPCOM", new HipcomDAO()),
    HIPER(75, "HIPER", new HiperDAO()),
    ICOMMERCE(76, "ICOMMERCE", new ICommerceDAO()),
    IQSISTEMAS(77, "IQSISTEMAS", new IQSistemasDAO()),
    ISERVER(78, "ISERVER", new IServerDAO()),
    INFOBRASIL(79, "INFOBRASIL", new InfoBrasilDAO()),
    //INFOMAC(80, "INFOMAC", new InfoMacDAO()),
    INOVA(81, "INOVA", new InovaDAO()),
    INTELLICASH(82, "INTELLICASH", new IntelliCashDAO()),
    INTELLICON(83, "INTELLICON", new IntelliconDAO()),
    INTERDATA(84, "INTERDATA", new InterDataDAO()),
    INTERAGE(85, "INTERAGE", new InterageDAO()),
    INVENTER(86, "INVENTER", new InventerDAO()),
    JM2ONLINE(87, "JM2ONLINE", new JM2OnlineDAO()),
    JMASTER(88, "JMASTER", new JMasterDAO()),
    JACSYS(89, "JACSYS", new JacsysDAO()),
    JRF(90, "JRF", new JrfDAO()),
    KAIROS(91, "KAIROS", new KairosDAO()),
    KCMS(92, "KCMS", new KcmsDAO()),
    LBSOFTWARE(93, "LBSOFTWARE", new LBSoftwareDAO()),
    LIDERNETWORK(94, "LIDERNETWORK", new LiderNetWorkDAO()),
    LINCE(95, "LINCE", new LinceDAO()),
    LINNER(96, "LINNER", new LinnerDAO()),
    LITECI(97, "LITECI", new LiteciDAO()),
    LOGTEC(98, "LOGTEC", new LogTECDAO()),
    LOGICBOX(99, "LOGICBOX", new LogicboxDAO()),
    LOGUS(100, "LOGUS", new LogusDAO()),
    LYNCIS(101, "LYNCIS", new LyncisDAO()),
    MSIINFOR(102, "MSIINFOR", new MSIInforDAO()),
    MARKET(103, "MARKET", new MarketDAO()),
    MASTER(104, "MASTER", new MasterDAO()),
    MERCALITE(105, "MERCALITE", new MercaLiteDAO()),
    MILENIO(106, "MILENIO", new MilenioDAO()),
    MOBILITY(107, "MOBILITY", new MobilityDAO()),
    MOBNEPDV(108, "MOBNEPDV", new MobnePdvDAO()),
    MRS(109, "MRS", new MrsDAO()),
    MULTIPDV(110, "MULTIPDV", new MultiPdvDAO()),
    NATISISTEMAS(111, "NATISISTEMAS", new NATISistemasDAO()),
    NCA(112, "NCA", new NCADAO()),
    OPEN(113, "OPEN", new OpenDAO()),
    ORION(114, "ORION", new OrionDAO()),
    ORIONTECH(115, "ORIONTECH", new OrionTechDAO()),
    ORYON(116, "ORYON", new OryonDAO()),
    PHIXA(117, "PHIXA", new PhixaDAO()),
    PLENOKW(118, "PLENOKW", new PlenoKWDAO()),
    PLENUS(119, "PLENUS", new PlenusDAO()),
    POLIGON(120, "POLIGON", new PoligonDAO()),
    POMARES(121, "POMARES", new PomaresDAO()),
    //PROSUPER(122, "PROSUPER", new ProsuperDAO()),
    PROTON(123, "PROTON", new ProtonDAO()),
    PWGESTOR(124, "PWGESTOR", new PwGestorDAO()),
    PWS(125, "PWS", new PwsDAO()),
    RCNET(126, "RCNET", new RCNetDAO()),
    RKSOFTWARE(127, "RKSOFTWARE", new RKSoftwareDAO()),
    RMSAUTOMAHELP(128, "RMSAUTOMAHELP", new RMSAutomaHelpDAO()),
    RMS(129, "RMS", new RMSDAO()),
    RPINFO(130, "RPINFO", new RPInfoDAO()),
    RENSOFTWARE(131, "RENSOFTWARE", new RensoftwareDAO()),
    REPLEIS(132, "REPLEIS", new RepleisDAO()),
    RESULTMAIS(133, "RESULTMAIS", new ResultMaisDAO()),
    ROOTAC(134, "ROOTAC", new RootacDAO()),
    SDINFORMATICA(135, "SDINFORMATICA", new SDInformaticaDAO()),
    SGMASTER(136, "SGMASTER", new SGMasterDAO()),
    SIMS(137, "SIMS", new SIMSDAO()),
    STI(138, "STI", new STIDAO()),
    STSITEMAS(139, "STSITEMAS", new STSitemasDAO()),
    SAAC(140, "SAAC", new SaacDAO()),
    SABTECH(141, "SABTECH", new SabTechDAO()),
    SAEF(142, "SAEF", new SaefDAO()),
    SAMBANET(143, "SAMBANET", new SambaNetV2DAO()),
    SATECFE(144, "SATECFE", new SatecfeDAO()),
    SAV(145, "SAV", new SavDAO()),
    SCEF(146, "SCEF", new ScefDAO()),
    SHI(147, "SHI", new ShiDAO()),
    //SIACRIARE(148, "SIACRIARE", new SiaCriareDAO()),
    SIAC(149, "SIAC", new SiacDAO()),
    SIFAT(150, "SIFAT", new SifatDAO()),
    SIGMA(151, "SIGMA", new SigmaDAO()),
    SIIT(152, "SIIT", new SiitDAO()),
    SIRCOM(153, "SIRCOM", new SircomDAO()),
    SISMOURA(154, "SISMOURA", new SisMouraDAO()),
    SNSISTEMA(155, "SNSISTEMA", new SnSistemaDAO()),
    SOFTCOM(156, "SOFTCOM", new SoftcomDAO()),
    SOFTTECH(157, "SOFTTECH", new SofttechDAO()),
    SOLIDO(158, "SOLIDO", new SolidoDAO()),
    SOLIDUS(159, "SOLIDUS", new SolidusDAO()),
    SOLUTIONSUPERA(160, "SOLUTIONSUPERA", new SolutionSuperaDAO()),
    SOPHYX(161, "SOPHYX", new SophyxDAO()),
    SRI(162, "SRI", new SriDAO()),
    //SUPERSERVER(163, "SUPERSERVER", new SuperControleDAO()),
    SUPER(164, "SUPER", new SuperDAO()),
    SUPERLOJA10(165, "SUPERLOJA10", new SuperLoja10DAO()),
    SUPERUS(166, "SUPERUS", new SuperusDAO()),
    SYNCTEC(167, "SYNCTEC", new SyncTecDAO()),
    SYSAUT(168, "SYSAUT", new SysAutDAO()),
    SYSERP(169, "SYSERP", new SysERPDAO()),
    SYSPDV(170, "SYSPDV", new SysPdvDAO()),
    //SYSMO(171, "SYSMO", new SysmoDAO()),
    TGA(172, "TGA", new TGADAO()),
    TECNOSOFT(173, "TECNOSOFT", new TecnosoftDAO()),
    TELECON(174, "TELECON", new TeleconDAO()),
    TITECNOLOGIA(175, "TITECNOLOGIA", new TiTecnologiaDAO()),
    TOPSYSTEM(176, "TOPSYSTEM", new TopSystemDAO()),
    TPAROOTAC(177, "TPAROOTAC", new TpaRootacDAO()),
    TSTI(178, "TSTI", new TstiDAO()),
    UNIPLUS(179, "UNIPLUS", new UniplusDAO()),
    UPFORTI(180, "UPFORTI", new UpFortiDAO()),
    VCASH(181, "VCASH", new VCashDAO()),
    VRMASTER(182, "VRMASTER", new VRToVRDAO()),
    VIASOFT(183, "VIASOFT", new ViaSoftDAO()),
    VIGGO(184, "VIGGO", new ViggoDAO()),
    VISUALCOMERCIO(185, "VISUALCOMERCIO", new VisualComercioDAO()),
    VISUALMIX(186, "VISUALMIX", new VisualMixDAO()),
    W2A(187, "W2A", new W2ADAO()),
    WEBSAQ(188, "WEBSAQ", new WebSaqDAO()),
    WEBER(189, "WEBER", new WeberDAO()),
    WINNEXUS(190, "WINNEXUS", new WinNexusDAO()),
    WISASOFT(191, "WISASOFT", new WisaSoftDAO()),
    WMSI(192, "WMSI", new WmsiDAO()),
    //WSHOP(193, "WSHOP", ""),
    ZOOMBOX(194, "ZOOMBOX", new ZoomboxDAO()),
    ZPF(195, "ZPF", new ZpfDAO()),
    LINEAR(196, "LINEAR", new LinearDAO()),
    ARIUS(197, "ARIUS", new AriusDAO()),
    GATEWAYSISTEMAS(198, "GATEWAY SISTEMAS", new GatewaySistemasDAO()),
    SG(199, "SG", new SGDAO()),
    SYGMA(200, "SYGMA", new SygmaDAO()),
    MICROTAB(201, "MICROTAB", new MicroTabDAO()),
    MRC6(202,"MRC6", new MRC6DAO()),
    BOMSOFT(203, "BOMSOFT", new BomSoftDAO()),
    ASSIST(204, "ASSIST", new AssistDAO()),
    DOBESCGA(206,"DOBESCGA", new Dobes_CgaDAO());
    //SUPERCONTROLE(198, "SUPERCONTROLE", "SuperControleDAO");
    
    private int id;
    private String nome;
    private InterfaceDAO dao;

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    
    public InterfaceDAO getDao() {
        return dao;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public void setDao(InterfaceDAO dao) {
        this.dao = dao;
    }

    private ESistema(int id, String nome, InterfaceDAO dao) {
        this.id = id;
        this.nome = nome;
        this.dao = dao;
    }
    
    ESistema() {}
    
    public static ESistema getById(int id) {
        for (ESistema st: values()) {
            if (st.getId() == id) {
                return st;
            }
        }
        return null;
    }

}
