/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.vo.enums;

import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao.dao.interfaces.AriusDAO;
import vrimplantacao2.dao.cadastro.Stock_PostgresDAO;
import vrimplantacao2.dao.interfaces.AlterData_WShopDAO;
import vrimplantacao2.dao.interfaces.ArgoDAO;
import vrimplantacao2.dao.interfaces.AutomaqDAO;
import vrimplantacao2.dao.interfaces.AvanceDAO;
import vrimplantacao2.dao.interfaces.AvistareDAO;
import vrimplantacao2.dao.interfaces.BaseDAO;
import vrimplantacao2.dao.interfaces.BrainSoftDAO;
import vrimplantacao2.dao.interfaces.CgaDAO;
import vrimplantacao2.dao.interfaces.CPGestorDAO;
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
import vrimplantacao2.dao.interfaces.GSoftDAO;
import vrimplantacao2.dao.interfaces.GTechDAO;
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
import vrimplantacao2_5.dao.sistema.RMSDAO;
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
import vrimplantacao2.dao.interfaces.SiaCriareMySqlDAO;
import vrimplantacao2.dao.interfaces.SiacDAO;
import vrimplantacao2.dao.interfaces.SifatDAO;
import vrimplantacao2.dao.interfaces.SigmaDAO;
import vrimplantacao2.dao.interfaces.SiitDAO;
import vrimplantacao2.dao.interfaces.SincDAO;
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
import vrimplantacao2.dao.interfaces.SuperControle_SuperServerDAO;
import vrimplantacao2.dao.interfaces.SuperDAO;
import vrimplantacao2.dao.interfaces.SuperLoja10DAO;
import vrimplantacao2.dao.interfaces.SuperusDAO;
import vrimplantacao2.dao.interfaces.SyncTecDAO;
import vrimplantacao2.dao.interfaces.SysAutDAO;
import vrimplantacao2.dao.interfaces.SysERPDAO;
import vrimplantacao2.dao.interfaces.SysPdvDAO;
import vrimplantacao2.dao.interfaces.SysmoPostgresDAO;
import vrimplantacao2.dao.interfaces.TGADAO;
import vrimplantacao2.dao.interfaces.TecnosoftDAO;
import vrimplantacao2.dao.interfaces.TeleconDAO;
import vrimplantacao2.dao.interfaces.TiTecnologiaDAO;
import vrimplantacao2.dao.interfaces.TpaRootacDAO;
import vrimplantacao2.dao.interfaces.UniplusDAO;
import vrimplantacao2.dao.interfaces.UpFortiDAO;
import vrimplantacao2.dao.interfaces.VCashDAO;
import vrimplantacao2.dao.interfaces.VRToVRDAO;
import vrimplantacao2.dao.interfaces.ViaSoftDAO;
import vrimplantacao2.dao.interfaces.ViggoDAO;
import vrimplantacao2.dao.interfaces.VisualComercioDAO;
import vrimplantacao2_5.dao.sistema.VisualMixDAO;
import vrimplantacao2.dao.interfaces.W2ADAO;
import vrimplantacao2.dao.interfaces.WebSacDAO;
import vrimplantacao2.dao.interfaces.WeberDAO;
import vrimplantacao2.dao.interfaces.WinNexusDAO;
import vrimplantacao2.dao.interfaces.WisaSoftDAO;
import vrimplantacao2.dao.interfaces.WmsiDAO;
import vrimplantacao2.dao.interfaces.ZoomboxDAO;
import vrimplantacao2.dao.interfaces.ZpfDAO;
import vrimplantacao2.dao.interfaces.gestora.GestoraDAO;
import vrimplantacao2.dao.interfaces.linear.LinearDAO;
import vrimplantacao2.dao.interfaces.winthor.Winthor_PcSistemasDAO;
import vrimplantacao2_5.dao.sistema.ASoft2_5DAO;
import vrimplantacao2_5.dao.sistema.Accesys2_5DAO;
import vrimplantacao2_5.dao.sistema.Acom2_5DAO;
import vrimplantacao2_5.dao.sistema.AlphaSys2_5DAO;
import vrimplantacao2_5.dao.sistema.Apollo2_5DAO;
import vrimplantacao2_5.dao.sistema.ArautoDAO;
import vrimplantacao2_5.dao.sistema.AriusWebDAO;
import vrimplantacao2_5.dao.sistema.ArtSystem2_5DAO;
import vrimplantacao2_5.dao.sistema.BomSoftDAO;
import vrimplantacao2_5.dao.sistema.AssistDAO;
import vrimplantacao2_5.dao.sistema.Atenas2_5DAO;
import vrimplantacao2_5.dao.sistema.Athos2_5DAO;
import vrimplantacao2_5.dao.sistema.Atma2_5DAO;
import vrimplantacao2_5.dao.sistema.AutoAdm2_5DAO;
import vrimplantacao2_5.dao.sistema.AutoSystem2_5DAO;
import vrimplantacao2_5.dao.sistema.Autocom2_5DAO;
import vrimplantacao2_5.dao.sistema.BrDataDAO;
import vrimplantacao2_5.dao.sistema.BrajanGestores2_5DAO;
import vrimplantacao2_5.dao.sistema.CFSoftSiaECF2_5DAO;
import vrimplantacao2_5.dao.sistema.CMMDAO;
import vrimplantacao2_5.dao.sistema.CPlus2_5DAO;
import vrimplantacao2_5.dao.sistema.CadastraFacil2_5DAO;
import vrimplantacao2_5.dao.sistema.CefasConcretizeDAO2_5;
import vrimplantacao2_5.dao.sistema.CenterInformaticaDAO2_5;
import vrimplantacao2_5.dao.sistema.ConnexOneDAO;
import vrimplantacao2_5.dao.sistema.ConsincoDAO;
import vrimplantacao2_5.dao.sistema.DSICDAO;
import vrimplantacao2_5.dao.sistema.Dobes_CgaDAO;
import vrimplantacao2_5.dao.sistema.DataByteDAO;
import vrimplantacao2_5.dao.sistema.Dellasta_PrismaFlexDAO;
import vrimplantacao2_5.dao.sistema.DevSisDAO;
import vrimplantacao2_5.dao.sistema.Director2_5DAO;
import vrimplantacao2_5.dao.sistema.DuplaFace_LivreDAO;
import vrimplantacao2_5.dao.sistema.DxDAO;
import vrimplantacao2_5.dao.sistema.ETradeDAO;
import vrimplantacao2_5.dao.sistema.EcoCentauro2_5DAO;
import vrimplantacao2_5.dao.sistema.EmpresoftDAO;
import vrimplantacao2_5.dao.sistema.FXSistemasDAO;
import vrimplantacao2_5.dao.sistema.FacilDAO;
import vrimplantacao2_5.dao.sistema.FenixMEDAO;
import vrimplantacao2_5.dao.sistema.FocusDAO;
import vrimplantacao2_5.dao.sistema.G3DAO2_5;
import vrimplantacao2_5.dao.sistema.GEPDAO;
import vrimplantacao2_5.dao.sistema.GZProdadosDAO;
import vrimplantacao2_5.dao.sistema.GZSistemas2_5DAO;
import vrimplantacao2_5.dao.sistema.GansoDAO;
import vrimplantacao2_5.dao.sistema.GatewaySistemasDAO2_5;
import vrimplantacao2_5.dao.sistema.Jmaster2_5DAO;
import vrimplantacao2_5.dao.sistema.ProviderGenericoDAO;
import vrimplantacao2_5.dao.sistema.Jnp_MSuperDAO;
import vrimplantacao2_5.dao.sistema.LCSistemasDAO;
import vrimplantacao2_5.dao.sistema.LJSistemas_SigDAO;
import vrimplantacao2_5.dao.sistema.MRC6DAO;
import vrimplantacao2_5.dao.sistema.MicroTabDAO;
import vrimplantacao2_5.dao.sistema.Provenco_TentaculoDAO;
import vrimplantacao2_5.dao.sistema.SGDAO;
import vrimplantacao2_5.dao.sistema.SatFacilDAO;
import vrimplantacao2_5.dao.sistema.SygmaDAO;
import vrimplantacao2_5.dao.sistema.TslDAO;
import vrimplantacao2_5.dao.sistema.VersatilDAO;
import vrimplantacao2_5.dao.sistema.WBADAO;
import vrimplantacao2_5.dao.sistema.LogusDAO;
import vrimplantacao2_5.dao.sistema.ManagerDAO;
import vrimplantacao2_5.dao.sistema.MbdDAO;
import vrimplantacao2_5.dao.sistema.MegaSoftwareDAO;
import vrimplantacao2_5.dao.sistema.Milenio2_5DAO;
import vrimplantacao2_5.dao.sistema.Modelo2_5DAO;
import vrimplantacao2_5.dao.sistema.Orion_PostgresDAO;
import vrimplantacao2_5.dao.sistema.NereusDAO;
import vrimplantacao2_5.dao.sistema.PallasDAO;
import vrimplantacao2_5.dao.sistema.PrimeDAO;
import vrimplantacao2_5.dao.sistema.ResulthBusinessDAO;
import vrimplantacao2_5.dao.sistema.STI32_5DAO;
import vrimplantacao2_5.dao.sistema.SaurusPDVDAO;
import vrimplantacao2_5.dao.sistema.ScorpionDAO;
import vrimplantacao2_5.dao.sistema.ScvDAO;
import vrimplantacao2_5.dao.sistema.ShiDAO2_5;
import vrimplantacao2_5.dao.sistema.SisMoura2_5DAO;
import vrimplantacao2_5.dao.sistema.SoftLogDAO;
import vrimplantacao2_5.dao.sistema.Target_G3DAO;
import vrimplantacao2_5.dao.sistema.TopSystemDAO;
import vrimplantacao2_5.dao.sistema.TstiDAO2_5;
import vrimplantacao2_5.dao.sistema.VisualComercio2_5DAO;
import vrimplantacao2_5.dao.sistema.VivaSistemasDAO;
import vrimplantacao2_5.dao.sistema.WLSDAO;
import vrimplantacao2_5.dao.sistema.WiseDAO;
import vrimplantacao2_5.gui.sistema.ASoft2_5GUI;
import vrimplantacao2_5.gui.sistema.Accesys2_5GUI;
import vrimplantacao2_5.gui.sistema.Acom2_5GUI;
import vrimplantacao2_5.gui.sistema.Alcance2_5GUI;
import vrimplantacao2_5.gui.sistema.AlphaSys2_5GUI;
import vrimplantacao2_5.gui.sistema.AlterData_WShop2_5GUI;
import vrimplantacao2_5.gui.sistema.Apollo2_5GUI;
import vrimplantacao2_5.gui.sistema.Arauto2_5GUI;
import vrimplantacao2_5.gui.sistema.Argo2_5GUI;
import vrimplantacao2_5.gui.sistema.Arius2_5GUI;
import vrimplantacao2_5.gui.sistema.AriusWeb2_5GUI;
import vrimplantacao2_5.gui.sistema.Arpa2_5GUI;
import vrimplantacao2_5.gui.sistema.ArtSystem2_5GUI;
import vrimplantacao2_5.gui.sistema.Asefe2_5GUI;
import vrimplantacao2_5.gui.sistema.Assist2_5GUI;
import vrimplantacao2_5.gui.sistema.Atenas2_5GUI;
import vrimplantacao2_5.gui.sistema.Athos2_5GUI;
import vrimplantacao2_5.gui.sistema.Atma2_5GUI;
import vrimplantacao2_5.gui.sistema.AutoAdm2_5GUI;
import vrimplantacao2_5.gui.sistema.AutoSystem2_5GUI;
import vrimplantacao2_5.gui.sistema.Autocom2_5GUI;
import vrimplantacao2_5.gui.sistema.Avance2_5GUI;
import vrimplantacao2_5.gui.sistema.Avistare2_5GUI;
import vrimplantacao2_5.gui.sistema.BomSoft2_5GUI;
import vrimplantacao2_5.gui.sistema.BrData2_5GUI;
import vrimplantacao2_5.gui.sistema.BrajanGestores2_5GUI;
import vrimplantacao2_5.gui.sistema.CFSoftSiaECF2_5GUI;
import vrimplantacao2_5.gui.sistema.CMM2_5GUI;
import vrimplantacao2_5.gui.sistema.CPGestorByView2_5GUI;
import vrimplantacao2_5.gui.sistema.CPlus2_5GUI;
import vrimplantacao2_5.gui.sistema.CadastraFacil2_5GUI;
import vrimplantacao2_5.gui.sistema.CefasConcretize2_5GUI;
import vrimplantacao2_5.gui.sistema.Cefas_Concretize2_5GUI;
import vrimplantacao2_5.gui.sistema.CenterInformatica2_5GUI;
import vrimplantacao2_5.gui.sistema.ConnexOne2_5GUI;
import vrimplantacao2_5.gui.sistema.Consinco2_5GUI;
import vrimplantacao2_5.gui.sistema.DSIC2_5GUI;
import vrimplantacao2_5.gui.sistema.DataByte2_5GUI;
import vrimplantacao2_5.gui.sistema.Dellasta_PrismaFlex2_5GUI;
import vrimplantacao2_5.gui.sistema.DevSis2_5GUI;
import vrimplantacao2_5.gui.sistema.Director2_5GUI;
import vrimplantacao2_5.gui.sistema.Dobes_Cga2_5GUI;
import vrimplantacao2_5.gui.sistema.DuplaFace_Livre2_5GUI;
import vrimplantacao2_5.gui.sistema.Dx2_5GUI;
import vrimplantacao2_5.gui.sistema.ETrade_VRSystem2_5GUI;
import vrimplantacao2_5.gui.sistema.EasySac2_5GUI;
import vrimplantacao2_5.gui.sistema.EcoCentauro2_5GUI;
import vrimplantacao2_5.gui.sistema.Empresoft2_5GUI;
//import vrimplantacao2_5.gui.sistema.Empresoft2_5GUI;
import vrimplantacao2_5.gui.sistema.FXSistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.Facil2_5GUI;
import vrimplantacao2_5.gui.sistema.Fenix2_5GUI;
import vrimplantacao2_5.gui.sistema.FenixME2_5GUI;
import vrimplantacao2_5.gui.sistema.Focus2_5GUI;
import vrimplantacao2_5.gui.sistema.G32_5GUI;
import vrimplantacao2_5.gui.sistema.GEP2_5GUI;
import vrimplantacao2_5.gui.sistema.GSoft2_5GUI;
import vrimplantacao2_5.gui.sistema.GZProdados2_5GUI;
import vrimplantacao2_5.gui.sistema.GZSistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.Ganso2_5GUI;
//import vrimplantacao2_5.gui.sistema.GatewaySistemasGUI;
import vrimplantacao2_5.gui.sistema.GatewaySistemas2_5GUI_old;
import vrimplantacao2_5.gui.sistema.GatewaySistemasGUI;
import vrimplantacao2_5.gui.sistema.Generico2_5GUI;
import vrimplantacao2_5.gui.sistema.Gestora2_5GUI;
import vrimplantacao2_5.gui.sistema.GetWay_Profit2_5GUI;
import vrimplantacao2_5.gui.sistema.GuiaSistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.Hipcom2_5GUI;
import vrimplantacao2_5.gui.sistema.Hiper2_5GUI;
import vrimplantacao2_5.gui.sistema.IServer2_5GUI;
import vrimplantacao2_5.gui.sistema.Inova2_5GUI;
import vrimplantacao2_5.gui.sistema.Interage2_5GUI;
import vrimplantacao2_5.gui.sistema.Jmaster2_5GUI;
import vrimplantacao2_5.gui.sistema.Jnp_MSuper2_5GUI;
import vrimplantacao2_5.gui.sistema.KCMS2_5GUI;
import vrimplantacao2_5.gui.sistema.LCSistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.LJSistemas_Sig2_5GUI;
import vrimplantacao2_5.gui.sistema.Lince2_5GUI;
import vrimplantacao2_5.gui.sistema.Linear2_5GUI;
import vrimplantacao2_5.gui.sistema.Logus2_5GUI;
import vrimplantacao2_5.gui.sistema.MRC62_5GUI;
import vrimplantacao2_5.gui.sistema.MRS2_5GUI;
import vrimplantacao2_5.gui.sistema.Manager2_5GUI;
import vrimplantacao2_5.gui.sistema.Mbd2_5GUI;
import vrimplantacao2_5.gui.sistema.Market2_5GUI;
import vrimplantacao2_5.gui.sistema.MegaSoftware2_5GUI;
import vrimplantacao2_5.gui.sistema.MicroTab2_5GUI;
import vrimplantacao2_5.gui.sistema.Milenio2_5GUI;
import vrimplantacao2_5.gui.sistema.Mobility2_5GUI;
import vrimplantacao2_5.gui.sistema.Modelo2_5GUI;
import vrimplantacao2_5.gui.sistema.Orion_postgres2_5GUI;
import vrimplantacao2_5.gui.sistema.Nereus2_5GUI;
import vrimplantacao2_5.gui.sistema.Pallas2_5GUI;
import vrimplantacao2_5.gui.sistema.Plenus2_5GUI;
import vrimplantacao2_5.gui.sistema.Prime2_5GUI;
import vrimplantacao2_5.gui.sistema.Scorpion2_5GUI;
import vrimplantacao2_5.gui.sistema.Provenco_Tentaculo2_5GUI;
import vrimplantacao2_5.gui.sistema.RMS2_5GUI;
import vrimplantacao2_5.gui.sistema.RPInfo2_5GUI;
import vrimplantacao2_5.gui.sistema.ResulthBusiness2_5GUI;
import vrimplantacao2_5.gui.sistema.SG2_5GUI;
import vrimplantacao2_5.gui.sistema.STI32_5GUI;
import vrimplantacao2_5.gui.sistema.Shi2_5GUI;
import vrimplantacao2_5.gui.sistema.SatFacil2_5GUI;
import vrimplantacao2_5.gui.sistema.SaurusPDV2_5GUI;
import vrimplantacao2_5.gui.sistema.Scv2_5GUI;
import vrimplantacao2_5.gui.sistema.Siac2_5GUI;
import vrimplantacao2_5.gui.sistema.Sinc2_5GUI;
import vrimplantacao2_5.gui.sistema.SisMoura2_5GUI;
import vrimplantacao2_5.gui.sistema.Sismaster2_5GUI;
import vrimplantacao2_5.gui.sistema.SoftLog2_5GUI;
import vrimplantacao2_5.gui.sistema.Stock_Postgres2_5GUI;
import vrimplantacao2_5.gui.sistema.SuperControle_SuperServer2_5GUI;
import vrimplantacao2_5.gui.sistema.Sygma2_5GUI;
import vrimplantacao2_5.gui.sistema.SysPdv2_5GUI;
import vrimplantacao2_5.gui.sistema.Target_G32_5GUI;
import vrimplantacao2_5.gui.sistema.TopSystem2_5GUI;
import vrimplantacao2_5.gui.sistema.Tsl2_5GUI;
import vrimplantacao2_5.gui.sistema.Tsti2_5GUI;
import vrimplantacao2_5.gui.sistema.Uniplus2_5GUI;
import vrimplantacao2_5.gui.sistema.VRToVR2_5GUI;
import vrimplantacao2_5.gui.sistema.Versatil2_5GUI;
import vrimplantacao2_5.gui.sistema.VisualComercio2_5GUI;
import vrimplantacao2_5.gui.sistema.VivaSistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.VisualMix2_5GUI;
import vrimplantacao2_5.gui.sistema.WBA2_5GUI;
import vrimplantacao2_5.gui.sistema.WLS2_5GUI;
import vrimplantacao2_5.gui.sistema.WebSac2_5GUI;
import vrimplantacao2_5.gui.sistema.Winthor_PcSistemas2_5GUI;
import vrimplantacao2_5.gui.sistema.Wise2_5GUI;

/**
 *
 * @author Desenvolvimento
 */
public enum ESistema {

    ASOFT(1, "ASOFT", new ASoft2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new ASoft2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ACCESYS(2, "ACCESYS", new Accesys2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Accesys2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ACOM(3, "ACOM", new Acom2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Acom2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ALPHASYS(4, "ALPHASYS", new AlphaSys2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new AlphaSys2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    APOLLO(5, "APOLLO", new Apollo2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Apollo2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ARTSYSTEM(6, "ARTSYSTEM", new ArtSystem2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new ArtSystem2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ASEFE(7, "ASEFE", new ASoft2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Asefe2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ATENAS(8, "ATENAS", new Atenas2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Atenas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ATHOS(9, "ATHOS", new Athos2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Athos2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ATMA(10, "ATMA", new Atma2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Atma2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    AUTOADM(11, "AUTOADM", new AutoAdm2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new AutoAdm2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    AUTOSYSTEM(12, "AUTOSYSTEM", new AutoSystem2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new AutoSystem2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    AUTOCOM(13, "AUTOCOM", new Autocom2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Autocom2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    AUTOMAQ(14, "AUTOMAQ", new AutomaqDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    AVANCE(15, "AVANCE", new AvanceDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Avance2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    AVISTARE(16, "AVISTARE", new AvistareDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Avistare2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    BASE(17, "BASE", new BaseDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    BRAINSOFT(18, "BRAINSOFT", new BrainSoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    BRAJANGESTORES(19, "BRAJANGESTORES", new BrajanGestores2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new BrajanGestores2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CFSOFTSIAECF(20, "CFSOFTSIAECF", new CFSoftSiaECF2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new CFSoftSiaECF2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CPGESTOR(21, "CPGESTOR", new CPGestorDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new CPGestorByView2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CPLUS(22, "CPLUS", new CPlus2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new CPlus2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CADASTRAFACIL(23, "CADASTRAFACIL", new CadastraFacil2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new CadastraFacil2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CEFAS(24, "CEFAS", new CefasConcretizeDAO2_5()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new CefasConcretize2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CEREBRO(25, "CEREBRO", new CerebroDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CERVANTES(26, "CERVANTES", new CervantesDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CGA(27, "CGA", new CgaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CISS(28, "CISS", new CissDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CLICK(29, "CLICK", new ClickDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CONTECH(30, "CONTECH", new ContechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CONTROLWARE(31, "CONTROLWARE", new ControlWareDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CONTROLX(32, "CONTROLX", new ControlXDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CRONOS20(33, "CRONOS20", new Cronos20DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    CUPERMAX(34, "CUPERMAX", new CupermaxDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DJSYSTEM(35, "DJSYSTEM", new DJSystemDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DLINK(36, "DLINK", new DLinkDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DSOFT(37, "DSOFT", new DSoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DATASYNC(38, "DATASYNC", new DataSyncDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DELFI(39, "DELFI", new DelfiDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DESTRO(40, "DESTRO", new DestroDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DEVMASTER(41, "DEVMASTER", new DevMasterDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    DIRECTOR(42, "DIRECTOR", new Director2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Director2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    DTCOM(43, "DTCOM", new DtComDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    EASYSAC(44, "EASYSAC", new EasySacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new EasySac2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }

            return null;
        }
    },
    EMPORIO(45, "EMPORIO", new EmporioDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    EPTUS(46, "EPTUS", new EptusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ESSYSTEM(47, "ESSYSTEM", new EsSystemDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ETICA(48, "ETICA", new EticaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    EXODUS(49, "EXODUS", new ExodusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FHONLINE(50, "FHONLINE", new FHOnlineDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FABTECH(51, "FABTECH", new FabTechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FACILITE(52, "FACILITE", new FaciliteDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FARM2000(53, "FARM2000", new Farm2000DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FENIX(54, "FENIX", new FenixDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Fenix2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    FLASH(55, "FLASH", new FlashDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FLATAN(56, "FLATAN", new FlatanDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FORT(57, "FORT", new FortDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    FUTURA(58, "FUTURA", new FuturaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    G10(59, "G10", new G10DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    G3(60, "G3", new G3DAO2_5()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new G32_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GCOM(61, "GCOM", new GComDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GDI(62, "GDI", new GDIDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GDOOR(63, "GDOOR", new GDoorDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GR7(64, "GR7", new GR7DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GTECH(65, "GTECH", new GTechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GZSISTEMAS(66, "GZSISTEMAS", new GZSistemas2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new GZSistemas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GESTORPDV(67, "GESTORPDV", new GestorPdvDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GETWAY(68, "GETWAY", new GetWay_ProfitDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new GetWay_Profit2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }

            return null;
        }
    },
    GIGATRON(69, "GIGATRON", new GigatronDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GONDOLA(70, "GONDOLA", new GondolaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    GUIASISTEMAS(71, "GUIASISTEMAS", new GuiaSistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new GuiaSistemas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    HRTECH(72, "HRTECH", new HRTechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    HERCULESINTCASH(73, "HERCULESINTCASH", new HerculesIntCashDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    HIPCOM(74, "HIPCOM", new HipcomDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Hipcom2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    HIPER(75, "HIPER", new HiperDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Hiper2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ICOMMERCE(76, "ICOMMERCE", new ICommerceDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    IQSISTEMAS(77, "IQSISTEMAS", new IQSistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ISERVER(78, "ISERVER", new IServerDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new IServer2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    INFOBRASIL(79, "INFOBRASIL", new InfoBrasilDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    INFOMAC(80, "INFOMAC", new InfoMacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    INOVA(81, "INOVA", new InovaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Inova2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    INTELLICASH(82, "INTELLICASH", new IntelliCashDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    INTELLICON(83, "INTELLICON", new IntelliconDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    INTERDATA(84, "INTERDATA", new InterDataDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    INTERAGE(85, "INTERAGE", new InterageDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Interage2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    INVENTER(86, "INVENTER", new InventerDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    JM2ONLINE(87, "JM2ONLINE", new JM2OnlineDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    JMASTER(88, "JMASTER", new Jmaster2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Jmaster2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    JACSYS(89, "JACSYS", new JacsysDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    JRF(90, "JRF", new JrfDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    KAIROS(91, "KAIROS", new KairosDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    KCMS(92, "KCMS", new KcmsDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new KCMS2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    LBSOFTWARE(93, "LBSOFTWARE", new LBSoftwareDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LIDERNETWORK(94, "LIDERNETWORK", new LiderNetWorkDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LINCE(95, "LINCE", new LinceDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Lince2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "Erro ao abrir Lince GUI em Esistema");
            }
            return null;
        }
    },
    LINNER(96, "LINNER", new LinnerDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LITECI(97, "LITECI", new LiteciDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LOGTEC(98, "LOGTEC", new LogTECDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LOGICBOX(99, "LOGICBOX", new LogicboxDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LOGUS(100, "LOGUS", new LogusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Logus2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    LYNCIS(101, "LYNCIS", new LyncisDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    MSIINFOR(102, "MSIINFOR", new MSIInforDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    MARKET(103, "MARKET", new MarketDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Market2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MASTER(104, "MASTER", new MasterDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    MERCALITE(105, "MERCALITE", new MercaLiteDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    MILENIO(106, "MILENIO", new Milenio2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Milenio2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MOBILITY(107, "MOBILITY", new MobilityDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Mobility2_5GUI(frame);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }
    },
    MOBNEPDV(108, "MOBNEPDV", new MobnePdvDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    MRS(109, "MRS", new MrsDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new MRS2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MULTIPDV(110, "MULTIPDV", new MultiPdvDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    NATISISTEMAS(111, "NATISISTEMAS", new NATISistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    NCA(112, "NCA", new NCADAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    OPEN(113, "OPEN", new OpenDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ORION(114, "ORION", new OrionDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ORIONTECH(115, "ORIONTECH", new OrionTechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ORYON(116, "ORYON", new OryonDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    PHIXA(117, "PHIXA", new PhixaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    PLENOKW(118, "PLENOKW", new PlenoKWDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    PLENUS(119, "PLENUS", new PlenusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Plenus2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    POLIGON(120, "POLIGON", new PoligonDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    POMARES(121, "POMARES", new PomaresDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    MODELO(122, "MODELO", new Modelo2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Modelo2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    PROTON(123, "PROTON", new ProtonDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    PWGESTOR(124, "PWGESTOR", new PwGestorDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    PWS(125, "PWS", new PwsDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    RCNET(126, "RCNET", new RCNetDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    RKSOFTWARE(127, "RKSOFTWARE", new RKSoftwareDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    RMSAUTOMAHELP(128, "RMSAUTOMAHELP", new RMSAutomaHelpDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    RMS(129, "RMS", new RMSDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new RMS2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    RPINFO(130, "RPINFO", new RPInfoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new RPInfo2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    RENSOFTWARE(131, "RENSOFTWARE", new RensoftwareDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    REPLEIS(132, "REPLEIS", new RepleisDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    RESULTMAIS(133, "RESULTMAIS", new ResultMaisDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ROOTAC(134, "ROOTAC", new RootacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SDINFORMATICA(135, "SDINFORMATICA", new SDInformaticaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SGMASTER(136, "SGMASTER", new SGMasterDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SIMS(137, "SIMS", new SIMSDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    STI(138, "STI", new STIDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    STSITEMAS(139, "STSITEMAS", new STSitemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SAAC(140, "SAAC", new SaacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SABTECH(141, "SABTECH", new SabTechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SAEF(142, "SAEF", new SaefDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SAMBANET(143, "SAMBANET", new SambaNetV2DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SATECFE(144, "SATECFE", new SatecfeDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SAV(145, "SAV", new SavDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SCEF(146, "SCEF", new ScefDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SHI(147, "SHI", new ShiDAO2_5()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Shi2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SIACRIARE(148, "SIACRIARE", new SiaCriareMySqlDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SIAC(149, "SIAC", new SiacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Siac2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SIFAT(150, "SIFAT", new SifatDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SIGMA(151, "SIGMA", new SigmaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SIIT(152, "SIIT", new SiitDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SIRCOM(153, "SIRCOM", new SircomDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SISMOURA(154, "SISMOURA", new SisMoura2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new SisMoura2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SNSISTEMA(155, "SNSISTEMA", new SnSistemaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SOFTCOM(156, "SOFTCOM", new SoftcomDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SOFTTECH(157, "SOFTTECH", new SofttechDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SOLIDO(158, "SOLIDO", new SolidoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SOLIDUS(159, "SOLIDUS", new SolidusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SOLUTIONSUPERA(160, "SOLUTIONSUPERA", new SolutionSuperaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SOPHYX(161, "SOPHYX", new SophyxDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SRI(162, "SRI", new SriDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SUPERSERVER(163, "SUPERSERVER", new SuperControle_SuperServerDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SUPER(164, "SUPER", new SuperDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SUPERLOJA10(165, "SUPERLOJA10", new SuperLoja10DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SUPERUS(166, "SUPERUS", new SuperusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SYNCTEC(167, "SYNCTEC", new SyncTecDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SYSAUT(168, "SYSAUT", new SysAutDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SYSERP(169, "SYSERP", new SysERPDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    SYSPDV(170, "SYSPDV", new SysPdvDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new SysPdv2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SYSMO(171, "SYSMO_POSTGRES", new SysmoPostgresDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    TGA(172, "TGA", new TGADAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    TECNOSOFT(173, "TECNOSOFT", new TecnosoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    TELECON(174, "TELECON", new TeleconDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    TITECNOLOGIA(175, "TITECNOLOGIA", new TiTecnologiaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    TOPSYSTEM(176, "TOPSYSTEM", new TopSystemDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new TopSystem2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    TPAROOTAC(177, "TPAROOTAC", new TpaRootacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    TSTI(178, "TSTI", new TstiDAO2_5()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Tsti2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    UNIPLUS(179, "UNIPLUS", new UniplusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Uniplus2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    UPFORTI(180, "UPFORTI", new UpFortiDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    VCASH(181, "VCASH", new VCashDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    VRMASTER(182, "VRMASTER", new VRToVRDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new VRToVR2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    VIASOFT(183, "VIASOFT", new ViaSoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    VIGGO(184, "VIGGO", new ViggoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    VISUALCOMERCIO(185, "VISUALCOMERCIO", new VisualComercio2_5DAO()) {
         @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new VisualComercio2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    VISUALMIX(186, "VISUALMIX", new VisualMixDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new VisualMix2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    W2A(187, "W2A", new W2ADAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    WEBSAC(188, "WEBSAC", new WebSacDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new WebSac2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    WEBER(189, "WEBER", new WeberDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    WINNEXUS(190, "WINNEXUS", new WinNexusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    WISASOFT(191, "WISASOFT", new WisaSoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    WMSI(192, "WMSI", new WmsiDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ECO_CENTAURO(193, "ECO_CENTAURO", new EcoCentauro2_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new EcoCentauro2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ZOOMBOX(194, "ZOOMBOX", new ZoomboxDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    ZPF(195, "ZPF", new ZpfDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    },
    LINEAR(196, "LINEAR", new LinearDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Linear2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ARIUS(197, "ARIUS", new AriusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Arius2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GATEWAYSISTEMAS(198, "GATEWAY SISTEMAS", new GatewaySistemasDAO2_5()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new GatewaySistemasGUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SG(199, "SG", new SGDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new SG2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SYGMA(200, "SYGMA", new SygmaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Sygma2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MICROTAB(201, "MICROTAB", new MicroTabDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new MicroTab2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MRC6(202, "MRC6", new MRC6DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new MRC62_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    BOMSOFT(203, "BOMSOFT", new BomSoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new BomSoft2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ASSIST(204, "ASSIST", new AssistDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Assist2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    DATABYTE(205, "DATABYTE", new DataByteDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new DataByte2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    DOBESCGA(206, "DOBESCGA", new Dobes_CgaDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Dobes_Cga2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    TENTACULO(207, "TENTACULO", new Provenco_TentaculoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Provenco_Tentaculo2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    FXSISTEMAS(208, "FXSISTEMAS", new FXSistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new FXSistemas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CONSINCO(209, "CONSINCO", new ConsincoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Consinco2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    VERSATIL(210, "VERSATIL", new VersatilDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Versatil2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    DSIC(211, "DSIC", new DSICDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new DSIC2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    TSL(212, "TSL", new TslDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Tsl2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SATFACIL(213, "SATFACIL", new SatFacilDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new SatFacil2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    WBA(214, "WBA", new WBADAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new WBA2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    STOCK(215, "STOCK", new Stock_PostgresDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Stock_Postgres2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CMM(216, "CMM", new CMMDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new CMM2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GZPRODADOS(217, "GZPRODADOS", new GZProdadosDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new GZProdados2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    PRISMAFLEX(218, "PRISMAFLEX", new Dellasta_PrismaFlexDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Dellasta_PrismaFlex2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    JNP_MSUPER(219, "JNP-MSUPER", new Jnp_MSuperDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Jnp_MSuper2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ETRADE(220, "ETRADE", new ETradeDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new ETrade_VRSystem2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GANSO(221, "GANSO", new GansoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Ganso2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    LCSISTEMAS(222, "LCSISTEMAS", new LCSistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new LCSistemas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GESTORA(223, "GESTORA", new GestoraDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Gestora2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    NEREUS(224, "NEREUS", new NereusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Nereus2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SCORPION(225, "SCORPION", new ScorpionDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Scorpion2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MEGASOFTWARE(226, "MEGA SOFTWARE", new MegaSoftwareDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new MegaSoftware2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ORION_POSTGRES(227, "ORION_POSTGRES", new Orion_PostgresDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Orion_postgres2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ARAUTO(228, "ARAUTO", new ArautoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Arauto2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SUPERCONTROLE(229, "SUPERCONTROLE", new SuperControle_SuperServerDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new SuperControle_SuperServer2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    FOCUS(230, "FOCUS", new FocusDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Focus2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MANAGER(231, "MANAGER", new ManagerDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Manager2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SCV(232, "SCV", new ScvDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Scv2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SINC(233, "SINC", new SincDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Sinc2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    DX(234, "DX", new DxDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Dx2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    LIVRE(235, "LIVRE", new DuplaFace_LivreDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new DuplaFace_Livre2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    WINTHOR(236, "WINTHOR", new Winthor_PcSistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Winthor_PcSistemas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    WLS(237, "WLS", new WLSDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new WLS2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GSOFT(238, "GSOFT", new GSoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new GSoft2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    FACIL(239, "FACIL", new FacilDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Facil2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    BRDATA(240, "BRDATA", new BrDataDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new BrData2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ARIUSWEB(241, "ARIUSWEB", new AriusWebDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new AriusWeb2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    DEVSIS(242, "DEVSIS", new DevSisDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new DevSis2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    PRIME(243, "PRIME", new PrimeDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Prime2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    PALLAS(244, "PALLAS", new PallasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Pallas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ALCANCE(245, "ALCANCE", new DevSisDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame) {
            try {
                return new Alcance2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GEP(246, "GEP", new GEPDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new GEP2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SOFTLOG(247, "SOFTLOG", new SoftLogDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new SoftLog2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    MBD(248, "MBD", new MbdDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Mbd2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    FENIXME(249, "FENIXME", new FenixMEDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new FenixME2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    TARGET_G3(250, "TARGET-G3", new Target_G3DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Target_G32_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    WISE(251, "WISE", new WiseDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Wise2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    GENERICO(252, "GENERICO", new ProviderGenericoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Generico2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SISMASTER(253, "SISMASTER", new ProviderGenericoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Sismaster2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ALTERDATAWSHOP(254, "ALTERDATAWSHOP", new AlterData_WShopDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new AlterData_WShop2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    STI3(255, "STI3", new STI32_5DAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new STI32_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CENTER_INFORMATICA(256, "CENTER_INFORMATICA", new CenterInformaticaDAO2_5()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new CenterInformatica2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ARGO(257, "ARGO", new ArgoDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Argo2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    VIVASISTEMAS(258, "VIVASISTEMAS", new VivaSistemasDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new VivaSistemas2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    RESULTHBUSINESS(259, "RESULTHBUSINESS", new ResulthBusinessDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new ResulthBusiness2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    LJSISTEMAS_SIG(260, "LJSISTEMAS_SIG", new LJSistemas_SigDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new LJSistemas_Sig2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    SAURUSPDV(261, "SAURUSPDV", new SaurusPDVDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new SaurusPDV2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    ARPA(262, "ARPA", new SaurusPDVDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Arpa2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    EMPRESOFT(263, "EMPRESOFT", new EmpresoftDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new Empresoft2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    },
    CONNEXONE(264, "CONNEXONE", new ConnexOneDAO()) {
        @Override
        public VRInternalFrame getInternalFrame(VRMdiFrame frame
        ) {
            try {
                return new ConnexOne2_5GUI(frame);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, "");
            }
            return null;
        }
    };

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

    ESistema() {
    }

    public static ESistema getById(int id) {
        for (ESistema st : values()) {
            if (st.getId() == id) {
                return st;
            }
        }
        return null;
    }

    public abstract VRInternalFrame getInternalFrame(VRMdiFrame frame);
}
