package vrimplantacao.gui;

import vrframework.bean.busca.VRBusca;
import vrframework.bean.busca.VREventoBusca;
import vrframework.bean.busca.VREventoBuscaListener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.Box;
import javax.swing.DefaultDesktopManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.gui.SobreGUI;
import vrframework.classe.Util;
import vrframework.remote.Arquivo;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.DataProcessamentoDAO;
import vrimplantacao.gui.assistente.mapamercadologico.MapaMercadologicoGUI;
import vrimplantacao.gui.assistente.parametro.ParametroGUI;
import vrimplantacao.gui.cadastro.LojaConsultaGUI;
import vrimplantacao.gui.interfaces.AcertarCodigoInternoGUI;
import vrimplantacao.gui.interfaces.ActiveGUI;
import vrimplantacao.gui.interfaces.ArquivoPadraoGUI;
import vrimplantacao2.gui.interfaces.CgaGUI;
import vrimplantacao.gui.interfaces.FMGUI;
import vrimplantacao.gui.interfaces.EverastGUI;
import vrimplantacao.gui.interfaces.GdoorGUI;
import vrimplantacao.gui.interfaces.RMSGUI;
import vrimplantacao.gui.interfaces.JMasterGUI;
import vrimplantacao.gui.interfaces.MilenioGUI;
import vrimplantacao2.gui.interfaces.GetWayGUI;
import vrimplantacao.gui.interfaces.IdealGUI;
import vrimplantacao.gui.interfaces.rfd.ImportacaoLogVendaGUI;
import vrimplantacao.gui.interfaces.MobilityGUI;
import vrimplantacao.gui.interfaces.PlanilhaClientesGUI;
import vrimplantacao.gui.interfaces.PlanilhaCodigoBarrasLeaoGUI;
import vrimplantacao.gui.interfaces.PlanilhaContasPagarGUI;
import vrimplantacao.gui.interfaces.PlanilhaContasReceberGUI;
import vrimplantacao.gui.interfaces.PlanilhaConveniadoGUI;
import vrimplantacao.gui.interfaces.PlanilhaPadraoGUI;
import vrimplantacao.gui.interfaces.PlanilhaVendasGUI;
import vrimplantacao.gui.interfaces.VRSoftwareGUI;
import vrimplantacao.gui.interfaces.WisaSoftGUI_2;
import vrimplantacao.gui.interfaces.GuiaSistemasGUI;
import vrimplantacao.gui.interfaces.GCFGUI;
import vrimplantacao.gui.interfaces.MultiPdvGUI;
import vrimplantacao2.gui.interfaces.OrionGUI;
import vrimplantacao.gui.interfaces.PCSistemasGUI;
import vrimplantacao.gui.interfaces.PlanilhaChequeGUI;
import vrimplantacao.gui.interfaces.BoechatSoftGUI;
import vrimplantacao.gui.interfaces.ConcretizeGUI;
import vrimplantacao.gui.interfaces.ControlWareGUI;
import vrimplantacao.gui.interfaces.IntelliCashGUI;
import vrimplantacao.gui.interfaces.SoftaExGUI;
import vrimplantacao.gui.interfaces.UltraSistGUI;
import vrimplantacao.gui.interfaces.KairosGUI;
import vrimplantacao.gui.interfaces.RootacGUI;
import vrimplantacao.gui.interfaces.DirectorGUI;
import vrimplantacao.gui.interfaces.EccusInformaticaGUI;
import vrimplantacao.gui.interfaces.MRSGUI;
import vrimplantacao.gui.interfaces.VRSoftwarePDVGUI;
import vrimplantacao.gui.interfaces.SuperServerGUI;
import vrimplantacao2.gui.interfaces.DestroGUI;
import vrimplantacao.gui.interfaces.GZSistemasGUI;
import vrimplantacao.gui.interfaces.ImportarNotaSaidaImportacaoArquivoGUI;
import vrimplantacao.gui.interfaces.PlanilhaNCMGUI;
import vrimplantacao.gui.interfaces.SBOMarketGUI;
import vrimplantacao.gui.interfaces.SciGUI;
import vrimplantacao.gui.interfaces.SysMouraGUI;
import vrimplantacao2.gui.interfaces.TopSystemGUI;
import vrimplantacao.gui.interfaces.GetWayCloudGUI;
import vrimplantacao.gui.interfaces.SicsGUI;
import vrimplantacao.gui.interfaces.SimSoftGUI;
import vrimplantacao.gui.interfaces.FaucomGUI;
import vrimplantacao2.gui.interfaces.GR7GUI;
import vrimplantacao.gui.interfaces.ImportacoesDiversasGUI;
import vrimplantacao.gui.interfaces.PlanilhaFornecedorGUI;
import vrimplantacao.gui.interfaces.SIMSGUI;
import vrimplantacao.gui.interfaces.SuperusGUI;
import vrimplantacao.gui.interfaces.PlanilhaPdvVendaGUI;
import vrimplantacao.gui.interfaces.PlanilhaLogEstoqueGUI;
import vrimplantacao.gui.interfaces.SofgceGUI;
import vrimplantacao.vo.Formulario;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao.gui.interfaces.Maximus_DatasyncGUI;
import vrimplantacao.gui.interfaces.SoftClass_AutoComGUI;
import vrimplantacao.gui.interfaces.AlfaSoftwareGUI;
import vrimplantacao.gui.interfaces.AriusGUI;
import vrimplantacao.gui.interfaces.ControllGUI;
import vrimplantacao.gui.interfaces.InfoBrasilGUI;
import vrimplantacao2.gui.planilha.PlanilhaV2GUI;
import vrimplantacao.gui.interfaces.PlanilhaInventarioGUI;
import vrimplantacao.gui.interfaces.SoftLineGUI;
import vrimplantacao2.gui.tools.scripts.ScriptsGUI;
import vrimplantacao.gui.interfaces.PlanilhaProdutosLanchoneteGUI;
import vrimplantacao.gui.interfaces.InfoStoreGUI;
import vrimplantacao.gui.interfaces.DGComGUI;
import vrimplantacao.gui.interfaces.SaacGUI;
import vrimplantacao.gui.interfaces.LogusGUI;
import vrimplantacao.gui.interfaces.SacLumiGUI;
import vrimplantacao.gui.interfaces.InteragemGUI_2;
import vrimplantacao.gui.interfaces.FGGUI;
import vrimplantacao.gui.interfaces.GestoraGUI;
import vrimplantacao.gui.interfaces.IdealSoftGUI;
import vrimplantacao.gui.interfaces.RMSGUI_2;
import vrimplantacao.gui.interfaces.SicomGUI;
import vrimplantacao.gui.interfaces.HostMundoGUI;
import vrimplantacao.gui.interfaces.SuperusGUI2;
import vrimplantacao.gui.interfaces.FlatanGUI;
import vrimplantacao2.gui.interfaces.BrainSoftGUI;
import vrimplantacao2.gui.interfaces.WinthorGUI;
import vrimplantacao.gui.interfaces.DelfiiGUI;
import vrimplantacao.gui.interfaces.FabTechGUI;
import vrimplantacao2.gui.interfaces.TiTecnologiaGUI;
import vrimplantacao2.gui.interfaces.JacsysGUI;
import vrimplantacao.gui.interfaces.GerarCodigoBarrasAtacadoGUI;
import vrimplantacao.gui.interfaces.nfce.NotaSaidaNfceImportacaoArquivoGUI;
import vrimplantacao2.gui.interfaces.RepleisGUI;
import vrimplantacao2.gui.interfaces.ASoftGUI;
import vrimplantacao2.gui.interfaces.ApolloGUI;
import vrimplantacao2.gui.interfaces.BaseGUI;
import vrimplantacao2.gui.interfaces.ContechGUI;
import vrimplantacao2.gui.interfaces.EmporioGUI;
import vrimplantacao2.gui.interfaces.ShiGUI;
import vrimplantacao2.gui.interfaces.SigmaGUI;
import vrimplantacao2.gui.interfaces.EsSystemGUI;
import vrimplantacao2.gui.interfaces.Farm2000GUI;
import vrimplantacao2.gui.interfaces.HipicomGUI;
import vrimplantacao2.gui.interfaces.MSIInforGUI;
import vrimplantacao2.gui.interfaces.RCNetGUI;
import vrimplantacao2.gui.interfaces.SifatGUI;
import vrimplantacao2.gui.interfaces.SatecfeGUI;
import vrimplantacao2.gui.interfaces.JrfGUI;
import vrimplantacao2.gui.interfaces.LinnerGUI;
import vrimplantacao2.gui.interfaces.PomaresGUI;
import vrimplantacao2.gui.interfaces.SDInformaticaGUI;
import vrimplantacao2.gui.interfaces.CPGestorGUI;
import vrimplantacao.gui.interfaces.AlterarProdutoPdvVendaItemGUI;
import vrimplantacao2.gui.component.sqleditor.SQLEditor;
import vrimplantacao2.gui.interfaces.AsefeGUI;
import vrimplantacao2.gui.interfaces.AutoSystemGUI;
import vrimplantacao2.gui.interfaces.AvanceGUI;
import vrimplantacao2.gui.interfaces.TstiGUI;
import vrimplantacao2.gui.interfaces.CPlusGUI;
import vrimplantacao2.gui.interfaces.CissGUI;
import vrimplantacao2.gui.interfaces.HipcomGUI;
import vrimplantacao2.gui.interfaces.ScefGUI;
import vrimplantacao2.gui.interfaces.SisMouraGUI;
import vrimplantacao2.gui.interfaces.SolidusGUI;
import vrimplantacao2.gui.interfaces.SuperGUI;
import vrimplantacao2.gui.interfaces.SysPdvGUI;
import vrimplantacao2.gui.interfaces.VisualComercioGUI;
import vrimplantacao2.gui.interfaces.Wm_byFileGUI;
import vrimplantacao2.gui.interfaces.WmsiGUI;
import vrimplantacao2.gui.interfaces.PdvVrGUI;
import vrimplantacao2.gui.interfaces.InfoMacGUI;
import vrimplantacao2.gui.interfaces.RMSAutomaHelpGUI;
import vrimplantacao2.gui.interfaces.WebSaqGUI;

public final class MenuGUI extends VRMdiFrame {

    public LojaConsultaGUI formLojaConsulta = null;
    public VRSoftwareGUI formMigracaoVR = null;
    public CgaGUI formImportarCga = null;
    public MilenioGUI formImportarMilenio = null;
    public JMasterGUI formImportarJMaster = null;    
    public GetWayGUI formImportarGetWay = null;
    public IdealGUI formImportarIdeal = null;
    public ImportacaoLogVendaGUI formImportacaoLogVendaGUI = null;
    public RMSGUI formImportarRM = null;
    public MobilityGUI formImportarMobility = null;
    public GdoorGUI formImportarGdoor = null;
    public WisaSoftGUI_2 formImportarWisaSoft = null;
    public SoftaExGUI formImportarSoftaEx = null;
    public FMGUI formImportarFM = null;
    public EverastGUI formImportarEverast = null;
    public MRSGUI formImportarMRS = null;
    public GuiaSistemasGUI formImportarGuiaSistemas = null;
    public GCFGUI formImportarGCF = null;
    public MultiPdvGUI formImportarMultiPdv = null;
    public OrionGUI formImportarOrion = null;
    public RootacGUI formImportarRootac = null;
    public PCSistemasGUI formImportarPCSistemas = null;
    public BoechatSoftGUI formImportarBoechatSoft = null;
    public UltraSistGUI formImportarUltraSyst = null;
    public IntelliCashGUI formImportarIntelliCash = null;
    public ConcretizeGUI formImportarConcretize = null;
    public KairosGUI formImportarKairos = null;
    public DirectorGUI formImportarDirector = null;
    public VRSoftwarePDVGUI formImportarVRSoftwarePDV = null;
    public EccusInformaticaGUI formImportarEccusInformatica = null;
    public SuperServerGUI formImportarSuperServer = null;
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
    public GR7GUI formImportarGR7 = null;
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
    public DelfiiGUI formImportarDelfi = null;
    public FabTechGUI formImportarFabTech = null;
    public JacsysGUI formImportarJacsys = null;
    public SifatGUI formImportarSifat = null;
    public GerarCodigoBarrasAtacadoGUI formImportarGerarCodigoBarrasAtacado = null;
    public NotaSaidaNfceImportacaoArquivoGUI formNotaSaidaNfceImportacaoArquivoGUI = null;
    public AlterarProdutoPdvVendaItemGUI formAlterarProdutoPdvVendaItem = null;    

    public PlanilhaPdvVendaGUI formImportarPlanilhaPdvVenda = null;
    public PlanilhaLogEstoqueGUI formImportarPlanilhaLogEstoque = null;
    public PlanilhaPadraoGUI formPlanilhaPadrao = null;
    public PlanilhaContasPagarGUI formPlanilhaContasPagar = null;
    public PlanilhaContasReceberGUI formPlanilhaContasReceber = null;
    public ArquivoPadraoGUI formArquivoPadrao = null;
    public PlanilhaVendasGUI formPlanilhaVendas = null;
    public PlanilhaChequeGUI formPlanilhaCheque = null;
    public PlanilhaCodigoBarrasLeaoGUI formPlanilhaCodigoBarrasLeao = null;
    public PlanilhaClientesGUI formPlanilhaClientes = null;
    public PlanilhaConveniadoGUI formPlanilhaConveniado = null;
    public PlanilhaFornecedorGUI formPlanilhaFornecedor = null;
    public PlanilhaV2GUI formPlanilhaV2 = null;
    public AcertarCodigoInternoGUI formAcertarCodigoInterno = null;
    public PlanilhaNCMGUI formPlanilhaNCM = null;
    public PlanilhaInventarioGUI formPlanilhaInventario = null;
    public PlanilhaProdutosLanchoneteGUI formPlanilhaProdutosLanchonete = null;

    public VRBusca txtBusca = null;

    private LoginGUI loginFrame = null;

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

        /*jMenu1.setVisible(false);
        jMenu3.setVisible(false);
        jMenu4.setVisible(false);
        jMenuItem9.setVisible(false);
        jMenuItem13.setVisible(false);
        jMenuItem15.setVisible(false);
        jMenu11.setVisible(false);
        jMenuItem17.setVisible(false);
        mnuPlanilhaPadrao.setVisible(false);
        jMenuItem20.setVisible(false);
        jMenuItem6.setVisible(false);
        jMenuItem7.setVisible(false);
        jMenuItem30.setVisible(false);
        jMenuItem21.setVisible(false);
        jMenuItem22.setVisible(false);*/
    }

    @Override
    public void atualizarRodape() throws Exception {
        lblRazaoSocial.setText(Global.fornecedor);
        lblLoja.setText(Global.loja);
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
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SHI.getId()) {
                        jMenuItemSHIActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SYSPDV_FIREBIRD.getId()) {
                        jMenuItemSysPDVActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_IDEAL.getId()) {
                        jMenuItemIdealActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_MOBILITY.getId()) {
                        jMenuItemMobilityActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GDOOR.getId()) {
                        jMenuItemGdoorActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_WISASOFT.getId()) {
                        jMenuItemGdoorActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SOFTAEX.getId()) {
                        jMenuItemSoftaExActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_JMASTER.getId()) {
                        jMenuItemJMasterActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_MILENIO.getId()) {
                        jMenuItemMilenioActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GETWAY.getId()) {
                        jMenuItemGetWayActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_SYSPDV_SQLSERVER.getId()) {
                        jMenuItemSysPDVSQLServerActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GUIASISTEMAS.getId()) {
                        jMenuItemGuiaSistemasActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_BOECHATSOFT.getId()) {
                        jMenuItemBoechatSoftActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_RMS.getId()) {
                        jMenuItemRMSActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_GCF.getId()) {
                        jMenuItemGCFActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_PCSISTEMAS.getId()) {
                        jMenuItemPCSistemasActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_FMSISTEMAS.getId()) {
                        jMenuItemFMSistemasActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_EVEREST.getId()) {
                        jMenuItemEverastActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_SISTEMA_ORION.getId()) {
                        jMenuItemOrionActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_ULTRASYST.getId()) {
                        jMenuItemUltraSystActionPerformed(null);
                    }else if (evt.idFormulario == Formulario.IMPORTACAO_CONCRETIZE.getId()) {
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
        jSeparator3 = new javax.swing.JToolBar.Separator();
        vrDesktopPane = new vrframework.bean.desktopPane.VRDesktopPane();
        vRPanel5 = new vrframework.bean.panel.VRPanel();
        lblRazaoSocial = new vrframework.bean.label.VRLabel();
        vRPanel6 = new vrframework.bean.panel.VRPanel();
        lblVersao = new vrframework.bean.label.VRLabel();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        lblLoja = new vrframework.bean.label.VRLabel();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        lblData = new vrframework.bean.label.VRLabel();
        mnuMenu = new javax.swing.JMenuBar();
        mnuCaixa = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnuInterface = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenuItemCGA = new javax.swing.JMenuItem();
        jMenuItemSHI = new javax.swing.JMenuItem();
        jMenuItemSysPDV = new javax.swing.JMenuItem();
        jMenuItemIdeal = new javax.swing.JMenuItem();
        jMenuItemMobility = new javax.swing.JMenuItem();
        jMenuItemGdoor = new javax.swing.JMenuItem();
        jMenuItemWisaSoft = new javax.swing.JMenuItem();
        jMenuItemSoftaEx = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItemDestro = new javax.swing.JMenuItem();
        jMenuItemInfoBrasil = new javax.swing.JMenuItem();
        jMenuItemActive = new javax.swing.JMenuItem();
        mnuSaac = new javax.swing.JMenuItem();
        mnuControll = new javax.swing.JMenuItem();
        mnuSigma = new javax.swing.JMenuItem();
        mnuInteragem = new javax.swing.JMenuItem();
        mnuDelfi = new javax.swing.JMenuItem();
        mnuRepleis = new javax.swing.JMenuItem();
        mnuASoft = new javax.swing.JMenuItem();
        mnuSDInformatica = new javax.swing.JMenuItem();
        mnuCplus = new javax.swing.JMenuItem();
        mnuSolidus = new javax.swing.JMenuItem();
        mnuSuper = new javax.swing.JMenuItem();
        mnuScef = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItemJMaster = new javax.swing.JMenuItem();
        jMenuItemMilenio = new javax.swing.JMenuItem();
        jMenuItemGetWay = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItemSysPDVSQLServer = new javax.swing.JMenuItem();
        jMenuItemGuiaSistemas = new javax.swing.JMenuItem();
        jMenuItemBoechatSoft = new javax.swing.JMenuItem();
        jMenuItemUltraSyst = new javax.swing.JMenuItem();
        jMenuItemKairos = new javax.swing.JMenuItem();
        jMenuItemDirector = new javax.swing.JMenuItem();
        jMenuItemEcosInformatica = new javax.swing.JMenuItem();
        jMenuItemSuperServer = new javax.swing.JMenuItem();
        jMenuItemSysMoura = new javax.swing.JMenuItem();
        jMenuItemSBOMarket = new javax.swing.JMenuItem();
        jMenuItemSimSoft = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItemSofgce = new javax.swing.JMenuItem();
        jMenuItem19 = new javax.swing.JMenuItem();
        mmnuDGCom = new javax.swing.JMenuItem();
        mmnuGestora = new javax.swing.JMenuItem();
        mnuIdealSoft = new javax.swing.JMenuItem();
        mnuFabTech = new javax.swing.JMenuItem();
        mnuHipicom = new javax.swing.JMenuItem();
        mnuFabTech1 = new javax.swing.JMenuItem();
        mnuVisualComercio = new javax.swing.JMenuItem();
        mnuAsefe = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItemRMS = new javax.swing.JMenuItem();
        jMenuItemGCF = new javax.swing.JMenuItem();
        jMenuItemPCSistemas = new javax.swing.JMenuItem();
        jMenuItemConcretize = new javax.swing.JMenuItem();
        jMenuItemSuperus = new javax.swing.JMenuItem();
        jMenuItemArius = new javax.swing.JMenuItem();
        mnuSicom = new javax.swing.JMenuItem();
        mnuRMS_2 = new javax.swing.JMenuItem();
        mnuRMS_3 = new javax.swing.JMenuItem();
        mnuApollo = new javax.swing.JMenuItem();
        mnuLinner = new javax.swing.JMenuItem();
        mnuCPGestor = new javax.swing.JMenuItem();
        mnuWmsi = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItemControlWare = new javax.swing.JMenuItem();
        mnuFlatan = new javax.swing.JMenuItem();
        mnuJrf = new javax.swing.JMenuItem();
        mnuAutoSystem = new javax.swing.JMenuItem();
        mnuAutoSystem1 = new javax.swing.JMenuItem();
        mnuWebsaq = new javax.swing.JMenuItem();
        jMenu9 = new javax.swing.JMenu();
        jMenuItemFMSistemas = new javax.swing.JMenuItem();
        jMenuItemEverast = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItemTopSystem = new javax.swing.JMenuItem();
        jMenuItemSci = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItemGR7 = new javax.swing.JMenuItem();
        jMenuItemSacLumi = new javax.swing.JMenuItem();
        jMenuItemTiTecnologia = new javax.swing.JMenuItem();
        mnuSifat = new javax.swing.JMenuItem();
        mnuRCNet = new javax.swing.JMenuItem();
        mnuEmporio = new javax.swing.JMenuItem();
        mnuSatecfe = new javax.swing.JMenuItem();
        mnuTsti = new javax.swing.JMenuItem();
        mnuAvance = new javax.swing.JMenuItem();
        mnuHipcom = new javax.swing.JMenuItem();
        mnuOrion = new javax.swing.JMenu();
        jMenuItemOrion = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItemSics = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        mnuFG = new javax.swing.JMenuItem();
        mnuJacsys = new javax.swing.JMenuItem();
        mnuEsSystem = new javax.swing.JMenuItem();
        mnuMSIInfor = new javax.swing.JMenuItem();
        mnuDB2 = new javax.swing.JMenu();
        jMenuItemCISS = new javax.swing.JMenuItem();
        mnuCache = new javax.swing.JMenu();
        jMenuItemSIMS = new javax.swing.JMenuItem();
        jMenu12 = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        mnuBase = new javax.swing.JMenuItem();
        jMenu13 = new javax.swing.JMenu();
        mnuLogus = new javax.swing.JMenuItem();
        mnuBrainSoft = new javax.swing.JMenuItem();
        mnuFarm2000 = new javax.swing.JMenuItem();
        mnuADT = new javax.swing.JMenu();
        mnuInfoMacStore = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenu10 = new javax.swing.JMenu();
        mnuPlanilhaPadrao = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        mnuPlanilhaForn = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem32 = new javax.swing.JMenuItem();
        mnuHostMundo = new javax.swing.JMenuItem();
        mnuWmByFile = new javax.swing.JMenuItem();
        jMenu11 = new javax.swing.JMenu();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        mnuCodigoBarrasAtacado = new javax.swing.JMenuItem();
        mnuPlanilhaV2 = new javax.swing.JMenuItem();
        mnuImportarNfce = new javax.swing.JMenuItem();
        jMenu14 = new javax.swing.JMenu();
        mnupdvvendaitem = new javax.swing.JMenuItem();
        jMenuItemVRPdv = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        mnuSistema = new javax.swing.JMenu();
        mnuSistemaLogin = new javax.swing.JMenuItem();
        mnuFerramentas = new javax.swing.JMenu();
        mnuScripts = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuAvancadas = new javax.swing.JMenu();
        mnuMapeamentoMercadologico = new javax.swing.JMenuItem();
        mnuImportarNCM = new javax.swing.JMenuItem();
        mnuParametros = new javax.swing.JMenuItem();
        mnuParametros1 = new javax.swing.JMenuItem();
        mnuJanela = new javax.swing.JMenu();
        mnuAjuda = new javax.swing.JMenu();
        jSeparator4 = new javax.swing.JSeparator();
        mnuAjudaSobre = new javax.swing.JMenuItem();

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

        lblRazaoSocial.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRazaoSocial.setText("@@@@@@@@");

        javax.swing.GroupLayout vRPanel5Layout = new javax.swing.GroupLayout(vRPanel5);
        vRPanel5.setLayout(vRPanel5Layout);
        vRPanel5Layout.setHorizontalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRazaoSocial, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel5Layout.setVerticalGroup(
            vRPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(lblVersao, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
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

        lblLoja.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLoja.setText("@@@");

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLoja, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(lblData, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
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

        mnuCaixa.setText("Cadastro");

        jMenuItem1.setText("Loja");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        mnuCaixa.add(jMenuItem1);

        mnuMenu.add(mnuCaixa);

        mnuInterface.setText("Interface");

        jMenu1.setText("Importação");

        jMenu2.setText("Sistemas");

        jMenu5.setText("Firebird / Interbase");

        jMenuItemCGA.setText("CGA");
        jMenuItemCGA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCGAActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemCGA);

        jMenuItemSHI.setText("SHI");
        jMenuItemSHI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSHIActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemSHI);

        jMenuItemSysPDV.setText("SysPDV");
        jMenuItemSysPDV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysPDVActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemSysPDV);

        jMenuItemIdeal.setText("Ideal");
        jMenuItemIdeal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemIdealActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemIdeal);

        jMenuItemMobility.setText("Mobility");
        jMenuItemMobility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMobilityActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemMobility);

        jMenuItemGdoor.setText("GDOOR");
        jMenuItemGdoor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGdoorActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemGdoor);

        jMenuItemWisaSoft.setText("WisaSoft");
        jMenuItemWisaSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWisaSoftActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemWisaSoft);

        jMenuItemSoftaEx.setText("SoftaEx");
        jMenuItemSoftaEx.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSoftaExActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemSoftaEx);

        jMenuItem2.setText("IntelliCash");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem2);

        jMenuItemDestro.setText("Destro");
        jMenuItemDestro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDestroActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemDestro);

        jMenuItemInfoBrasil.setText("InfoBrasil");
        jMenuItemInfoBrasil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInfoBrasilActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemInfoBrasil);

        jMenuItemActive.setText("Active");
        jMenuItemActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemActiveActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItemActive);

        mnuSaac.setText("Saac");
        mnuSaac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSaacActionPerformed(evt);
            }
        });
        jMenu5.add(mnuSaac);

        mnuControll.setText("Controll");
        mnuControll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuControllActionPerformed(evt);
            }
        });
        jMenu5.add(mnuControll);

        mnuSigma.setText("Sigma");
        mnuSigma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSigmaActionPerformed(evt);
            }
        });
        jMenu5.add(mnuSigma);

        mnuInteragem.setText("Interagem");
        mnuInteragem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInteragemActionPerformed(evt);
            }
        });
        jMenu5.add(mnuInteragem);

        mnuDelfi.setText("Delfi");
        mnuDelfi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDelfiActionPerformed(evt);
            }
        });
        jMenu5.add(mnuDelfi);

        mnuRepleis.setText("Répleis");
        mnuRepleis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRepleisActionPerformed(evt);
            }
        });
        jMenu5.add(mnuRepleis);

        mnuASoft.setText("ASoft");
        mnuASoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuASoftActionPerformed(evt);
            }
        });
        jMenu5.add(mnuASoft);

        mnuSDInformatica.setText("SD Informática");
        mnuSDInformatica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSDInformaticaActionPerformed(evt);
            }
        });
        jMenu5.add(mnuSDInformatica);

        mnuCplus.setText("CPlus");
        mnuCplus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCplusActionPerformed(evt);
            }
        });
        jMenu5.add(mnuCplus);

        mnuSolidus.setText("Solidus");
        mnuSolidus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSolidusActionPerformed(evt);
            }
        });
        jMenu5.add(mnuSolidus);

        mnuSuper.setText("Super");
        mnuSuper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSuperActionPerformed(evt);
            }
        });
        jMenu5.add(mnuSuper);

        mnuScef.setText("Scef");
        mnuScef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuScefActionPerformed(evt);
            }
        });
        jMenu5.add(mnuScef);

        jMenu2.add(jMenu5);

        jMenu6.setText("SQL Server");

        jMenuItemJMaster.setText("JMaster");
        jMenuItemJMaster.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemJMasterActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemJMaster);

        jMenuItemMilenio.setText("Milênio");
        jMenuItemMilenio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMilenioActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemMilenio);

        jMenuItemGetWay.setText("GetWay");
        jMenuItemGetWay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGetWayActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemGetWay);

        jMenuItem12.setText("GetWay (Cloud)");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem12);

        jMenuItemSysPDVSQLServer.setText("SysPDV");
        jMenuItemSysPDVSQLServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysPDVSQLServerActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSysPDVSQLServer);

        jMenuItemGuiaSistemas.setText("Guia Sistemas");
        jMenuItemGuiaSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuiaSistemasActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemGuiaSistemas);

        jMenuItemBoechatSoft.setText("BoechatSoft");
        jMenuItemBoechatSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemBoechatSoftActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemBoechatSoft);

        jMenuItemUltraSyst.setText("UltraSyst");
        jMenuItemUltraSyst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUltraSystActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemUltraSyst);

        jMenuItemKairos.setText("Kairos");
        jMenuItemKairos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemKairosActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemKairos);

        jMenuItemDirector.setText("Director");
        jMenuItemDirector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDirectorActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemDirector);

        jMenuItemEcosInformatica.setText("Eccus Informática");
        jMenuItemEcosInformatica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEcosInformaticaActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemEcosInformatica);

        jMenuItemSuperServer.setText("SuperServer");
        jMenuItemSuperServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSuperServerActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSuperServer);

        jMenuItemSysMoura.setText("SisMoura");
        jMenuItemSysMoura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSysMouraActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSysMoura);

        jMenuItemSBOMarket.setText("SBOMarket");
        jMenuItemSBOMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSBOMarketActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSBOMarket);

        jMenuItemSimSoft.setText("SimSoft");
        jMenuItemSimSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSimSoftActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSimSoft);

        jMenuItem14.setText("Faucom");
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem14);

        jMenuItemSofgce.setText("SOFGCE");
        jMenuItemSofgce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSofgceActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSofgce);

        jMenuItem19.setText("Datasync/Maximus");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem19);

        mmnuDGCom.setText("DGCom");
        mmnuDGCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmnuDGComActionPerformed(evt);
            }
        });
        jMenu6.add(mmnuDGCom);

        mmnuGestora.setText("Gestora");
        mmnuGestora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mmnuGestoraActionPerformed(evt);
            }
        });
        jMenu6.add(mmnuGestora);

        mnuIdealSoft.setText("IdealSoft - Shop9");
        mnuIdealSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIdealSoftActionPerformed(evt);
            }
        });
        jMenu6.add(mnuIdealSoft);

        mnuFabTech.setText("FabTech");
        mnuFabTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFabTechActionPerformed(evt);
            }
        });
        jMenu6.add(mnuFabTech);

        mnuHipicom.setText("Hipicom");
        mnuHipicom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHipicomActionPerformed(evt);
            }
        });
        jMenu6.add(mnuHipicom);

        mnuFabTech1.setText("Costa Azul - Pomares");
        mnuFabTech1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFabTech1ActionPerformed(evt);
            }
        });
        jMenu6.add(mnuFabTech1);

        mnuVisualComercio.setText("Visual Comercio");
        mnuVisualComercio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVisualComercioActionPerformed(evt);
            }
        });
        jMenu6.add(mnuVisualComercio);

        mnuAsefe.setText("Asefe");
        mnuAsefe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsefeActionPerformed(evt);
            }
        });
        jMenu6.add(mnuAsefe);

        jMenu2.add(jMenu6);

        jMenu7.setText("Oracle");

        jMenuItemRMS.setText("RMS");
        jMenuItemRMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRMSActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItemRMS);

        jMenuItemGCF.setText("GCF");
        jMenuItemGCF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGCFActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItemGCF);

        jMenuItemPCSistemas.setText("PCSistemas");
        jMenuItemPCSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPCSistemasActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItemPCSistemas);

        jMenuItemConcretize.setText("Concretize");
        jMenuItemConcretize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemConcretizeActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItemConcretize);

        jMenuItemSuperus.setText("Superus");
        jMenuItemSuperus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSuperusActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItemSuperus);

        jMenuItemArius.setText("Arius");
        jMenuItemArius.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAriusActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItemArius);

        mnuSicom.setText("Sicom");
        mnuSicom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSicomActionPerformed(evt);
            }
        });
        jMenu7.add(mnuSicom);

        mnuRMS_2.setText("RMS v2");
        mnuRMS_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRMS_2ActionPerformed(evt);
            }
        });
        jMenu7.add(mnuRMS_2);

        mnuRMS_3.setText("Winthor");
        mnuRMS_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRMS_3ActionPerformed(evt);
            }
        });
        jMenu7.add(mnuRMS_3);

        mnuApollo.setText("Apollo");
        mnuApollo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuApolloActionPerformed(evt);
            }
        });
        jMenu7.add(mnuApollo);

        mnuLinner.setText("Linner");
        mnuLinner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLinnerActionPerformed(evt);
            }
        });
        jMenu7.add(mnuLinner);

        mnuCPGestor.setText("CPGestor");
        mnuCPGestor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCPGestorActionPerformed(evt);
            }
        });
        jMenu7.add(mnuCPGestor);

        mnuWmsi.setText("Wmsi");
        mnuWmsi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWmsiActionPerformed(evt);
            }
        });
        jMenu7.add(mnuWmsi);

        jMenu2.add(jMenu7);

        jMenu8.setText("PostgreSQL");

        jMenuItemControlWare.setText("ControlWare");
        jMenuItemControlWare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemControlWareActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItemControlWare);

        mnuFlatan.setText("Flatan");
        mnuFlatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFlatanActionPerformed(evt);
            }
        });
        jMenu8.add(mnuFlatan);

        mnuJrf.setText("Jrf");
        mnuJrf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuJrfActionPerformed(evt);
            }
        });
        jMenu8.add(mnuJrf);

        mnuAutoSystem.setText("AutoSystem");
        mnuAutoSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoSystemActionPerformed(evt);
            }
        });
        jMenu8.add(mnuAutoSystem);

        mnuAutoSystem1.setText("RMS Compras");
        mnuAutoSystem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAutoSystem1ActionPerformed(evt);
            }
        });
        jMenu8.add(mnuAutoSystem1);
        mnuWebsaq.setText("WebSaq");
        mnuWebsaq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWebsaqActionPerformed(evt);
            }
        });
        jMenu8.add(mnuWebsaq);

        jMenu2.add(jMenu8);

        jMenu9.setText("MySQL");

        jMenuItemFMSistemas.setText("FM Sistemas");
        jMenuItemFMSistemas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFMSistemasActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemFMSistemas);

        jMenuItemEverast.setText("Everest");
        jMenuItemEverast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEverastActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemEverast);

        jMenuItem4.setText("MRS");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem4);

        jMenuItemTopSystem.setText("TopSystem");
        jMenuItemTopSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTopSystemActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemTopSystem);

        jMenuItemSci.setText("Sci");
        jMenuItemSci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSciActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemSci);

        jMenuItem11.setText("GZ Sistemas");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItem11);

        jMenuItemGR7.setText("GR7");
        jMenuItemGR7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGR7ActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemGR7);

        jMenuItemSacLumi.setText("SAC Lumi");
        jMenuItemSacLumi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSacLumiActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemSacLumi);

        jMenuItemTiTecnologia.setText("Ti Tecnologia");
        jMenuItemTiTecnologia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemTiTecnologiaActionPerformed(evt);
            }
        });
        jMenu9.add(jMenuItemTiTecnologia);

        mnuSifat.setText("Sifat");
        mnuSifat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSifatActionPerformed(evt);
            }
        });
        jMenu9.add(mnuSifat);

        mnuRCNet.setText("RCNet");
        mnuRCNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRCNetActionPerformed(evt);
            }
        });
        jMenu9.add(mnuRCNet);

        mnuEmporio.setText("Emporio");
        mnuEmporio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEmporioActionPerformed(evt);
            }
        });
        jMenu9.add(mnuEmporio);

        mnuSatecfe.setText("Satecfe");
        mnuSatecfe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSatecfeActionPerformed(evt);
            }
        });
        jMenu9.add(mnuSatecfe);

        mnuTsti.setText("TSTI");
        mnuTsti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTstiActionPerformed(evt);
            }
        });
        jMenu9.add(mnuTsti);

        mnuAvance.setText("Avance");
        mnuAvance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAvanceActionPerformed(evt);
            }
        });
        jMenu9.add(mnuAvance);

        mnuHipcom.setText("Hipcom");
        mnuHipcom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHipcomActionPerformed(evt);
            }
        });
        jMenu9.add(mnuHipcom);

        jMenu2.add(jMenu9);

        mnuOrion.setText("DBF");

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

        mnuEsSystem.setText("ES System");
        mnuEsSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEsSystemActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuEsSystem);

        mnuMSIInfor.setText("MSI Infor");
        mnuMSIInfor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMSIInforActionPerformed(evt);
            }
        });
        mnuOrion.add(mnuMSIInfor);

        jMenu2.add(mnuOrion);

        mnuDB2.setText("IBM DB2");

        jMenuItemCISS.setText("CISS");
        jMenuItemCISS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCISSActionPerformed(evt);
            }
        });
        mnuDB2.add(jMenuItemCISS);

        jMenu2.add(mnuDB2);

        mnuCache.setText("Caché");

        jMenuItemSIMS.setText("SIMS");
        jMenuItemSIMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSIMSActionPerformed(evt);
            }
        });
        mnuCache.add(jMenuItemSIMS);

        jMenu2.add(mnuCache);

        jMenu12.setText("Access");

        jMenuItem25.setText("Alfa Software");
        jMenuItem25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem25ActionPerformed(evt);
            }
        });
        jMenu12.add(jMenuItem25);

        mnuBase.setText("Base");
        mnuBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBaseActionPerformed(evt);
            }
        });
        jMenu12.add(mnuBase);

        jMenu2.add(jMenu12);

        jMenu13.setText("Paradox");

        mnuLogus.setText("Logus");
        mnuLogus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLogusActionPerformed(evt);
            }
        });
        jMenu13.add(mnuLogus);

        mnuBrainSoft.setText("BrainSoft");
        mnuBrainSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBrainSoftActionPerformed(evt);
            }
        });
        jMenu13.add(mnuBrainSoft);

        mnuFarm2000.setText("Farm 2000");
        mnuFarm2000.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFarm2000ActionPerformed(evt);
            }
        });
        jMenu13.add(mnuFarm2000);

        jMenu2.add(jMenu13);

        mnuADT.setText("ADS");

        mnuInfoMacStore.setText("InfoMac - Store");
        mnuInfoMacStore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInfoMacStoreActionPerformed(evt);
            }
        });
        mnuADT.add(mnuInfoMacStore);

        jMenu2.add(mnuADT);

        jMenu1.add(jMenu2);

        jMenu3.setText("Planilhas");

        jMenuItem17.setText("Produto - Código Barras (Leão)");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem17);

        jMenu10.setText("Padrões");

        mnuPlanilhaPadrao.setText("Produtos Padrão");
        mnuPlanilhaPadrao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlanilhaPadraoActionPerformed(evt);
            }
        });
        jMenu10.add(mnuPlanilhaPadrao);

        jMenuItem20.setText("Cliente Preferencial");
        jMenuItem20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem20ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem20);

        jMenuItem6.setText("Contas a Pagar");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem6);

        jMenuItem7.setText("Contas a Receber");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem7);

        jMenuItem30.setText("Cheque");
        jMenuItem30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem30ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem30);

        jMenuItem21.setText("Cliente Conveniado");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem21);

        jMenuItem22.setText("Receber Conveniado");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem22);

        jMenuItem10.setText("Vendas");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem10);

        jMenuItem5.setText("NCM");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem5);

        jMenuItem16.setText("Pdv Venda");
        jMenuItem16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu10.add(jMenuItem16);

        mnuPlanilhaForn.setText("Fornecedores");
        mnuPlanilhaForn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlanilhaFornActionPerformed(evt);
            }
        });
        jMenu10.add(mnuPlanilhaForn);

        jMenu3.add(jMenu10);

        jMenuItem18.setText("Log Estoque");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem18);

        jMenuItem26.setText("Inventário");
        jMenuItem26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem26ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem26);

        jMenuItem31.setText("Produtos Lanchonete");
        jMenuItem31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem31ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem31);

        jMenu1.add(jMenu3);

        jMenu4.setText("Arquivos");

        jMenuItem8.setText("Padrão");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenuItem24.setText("Contech");
        jMenuItem24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem24ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem24);

        jMenuItem28.setText("MultiPDV");
        jMenuItem28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem28ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem28);

        jMenuItem27.setText("SoftLine");
        jMenuItem27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem27ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem27);

        jMenuItem32.setText("InfoStore");
        jMenuItem32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem32ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem32);

        mnuHostMundo.setText("HostMundo");
        mnuHostMundo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHostMundoActionPerformed(evt);
            }
        });
        jMenu4.add(mnuHostMundo);

        mnuWmByFile.setText("Wm");
        mnuWmByFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuWmByFileActionPerformed(evt);
            }
        });
        jMenu4.add(mnuWmByFile);

        jMenu1.add(jMenu4);

        jMenu11.setText("Alteração ID Produtos");

        jMenuItem29.setText("Padrão");
        jMenuItem29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem29ActionPerformed(evt);
            }
        });
        jMenu11.add(jMenuItem29);

        jMenu1.add(jMenu11);

        jMenuItem15.setText("Log Venda");
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem15);

        jMenuItem9.setText("NFe (Saída)");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem9);

        mnuCodigoBarrasAtacado.setText("Gerar Codigo Barras Atacado");
        mnuCodigoBarrasAtacado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCodigoBarrasAtacadoActionPerformed(evt);
            }
        });
        jMenu1.add(mnuCodigoBarrasAtacado);

        mnuPlanilhaV2.setText("Planilha (2.0)");
        mnuPlanilhaV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPlanilhaV2ActionPerformed(evt);
            }
        });
        jMenu1.add(mnuPlanilhaV2);

        mnuImportarNfce.setText("NFC-e");
        mnuImportarNfce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportarNfceActionPerformed(evt);
            }
        });
        jMenu1.add(mnuImportarNfce);

        jMenu14.setText("Especiais");

        mnupdvvendaitem.setText("Alterar Produto pdv.vendaitem");
        mnupdvvendaitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnupdvvendaitemActionPerformed(evt);
            }
        });
        jMenu14.add(mnupdvvendaitem);

        jMenu1.add(jMenu14);

        mnuInterface.add(jMenu1);

        jMenuItemVRPdv.setText("VR Software (PDV)");
        jMenuItemVRPdv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemVRPdvActionPerformed(evt);
            }
        });
        mnuInterface.add(jMenuItemVRPdv);

        jMenuItem13.setText("Integração VR para VR");
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        mnuInterface.add(jMenuItem13);

        mnuMenu.add(mnuInterface);

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

        mnuFerramentas.setText("Ferramentas");

        mnuScripts.setText("Scripts");
        mnuScripts.setToolTipText("");
        mnuScripts.setEnabled(false);
        mnuScripts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuScriptsActionPerformed(evt);
            }
        });
        mnuFerramentas.add(mnuScripts);
        mnuFerramentas.add(jSeparator1);

        mnuAvancadas.setText("Avançadas");

        mnuMapeamentoMercadologico.setText("Mapeamento de Mercadológico");
        mnuMapeamentoMercadologico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMapeamentoMercadologicoActionPerformed(evt);
            }
        });
        mnuAvancadas.add(mnuMapeamentoMercadologico);

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

        mnuFerramentas.add(mnuAvancadas);

        mnuMenu.add(mnuFerramentas);

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

        setJMenuBar(mnuMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuAjudaSobreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAjudaSobreActionPerformed
        try {
            this.setWaitCursor();
            SobreGUI form = new SobreGUI();

            form.setSobre("VR Implantaçao", Global.VERSAO, Global.DATA);
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
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
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
    }//GEN-LAST:event_jMenuItem1ActionPerformed
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

        try {
            this.setWaitCursor();
            if (formImportarMilenio == null || formImportarMilenio.isClosed()) {
                formImportarMilenio = new MilenioGUI(this);
            }

            formImportarMilenio.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

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


    private void mnuPlanilhaPadraoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlanilhaPadraoActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaPadrao == null || formPlanilhaPadrao.isClosed()) {
                formPlanilhaPadrao = new PlanilhaPadraoGUI(this);
            }

            formPlanilhaPadrao.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuPlanilhaPadraoActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaContasPagar == null || formPlanilhaContasPagar.isClosed()) {
                formPlanilhaContasPagar = new PlanilhaContasPagarGUI(this);
            }

            formPlanilhaContasPagar.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaContasReceber == null || formPlanilhaContasReceber.isClosed()) {
                formPlanilhaContasReceber = new PlanilhaContasReceberGUI(this);
            }

            formPlanilhaContasReceber.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_jMenuItem7ActionPerformed

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
        ShiGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSHIActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed

        try {
            this.setWaitCursor();
            if (formPlanilhaVendas == null || formPlanilhaVendas.isClosed()) {
                formPlanilhaVendas = new PlanilhaVendasGUI(this);
            }

            formPlanilhaVendas.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItemGetWayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGetWayActionPerformed
        GetWayGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemGetWayActionPerformed

    private void jMenuItemSysPDVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSysPDVActionPerformed
        SysPdvGUI.exibir(this);
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

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
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
    }//GEN-LAST:event_jMenuItem15ActionPerformed

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

    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaCodigoBarrasLeao == null || formPlanilhaCodigoBarrasLeao.isClosed()) {
                formPlanilhaCodigoBarrasLeao = new PlanilhaCodigoBarrasLeaoGUI(this);
            }

            formPlanilhaCodigoBarrasLeao.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItemMobilityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMobilityActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarMobility == null || formImportarMobility.isClosed()) {
                formImportarMobility = new MobilityGUI(this);
            }

            formImportarMobility.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

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

    private void jMenuItem20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem20ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaClientes == null || formPlanilhaClientes.isClosed()) {
                formPlanilhaClientes = new PlanilhaClientesGUI(this);
            }

            formPlanilhaClientes.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem20ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaConveniado == null || formPlanilhaConveniado.isClosed()) {
                formPlanilhaConveniado = new PlanilhaConveniadoGUI(this);
            }

            formPlanilhaConveniado.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed

    }//GEN-LAST:event_jMenuItem22ActionPerformed

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
        /*try {
            this.setWaitCursor();
            if (formImportarContech == null || formImportarContech.isClosed()) {
                formImportarContech = new ContechGUI(this);
            }

            formImportarContech.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }*/
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
        try {
            this.setWaitCursor();
            if (formImportarGuiaSistemas == null || formImportarGuiaSistemas.isClosed()) {
                formImportarGuiaSistemas = new GuiaSistemasGUI(this);
            }

            formImportarGuiaSistemas.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
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
        try {
            this.setWaitCursor();
            if (formImportarMultiPdv == null || formImportarMultiPdv.isClosed()) {
                formImportarMultiPdv = new MultiPdvGUI(this);
            }

            formImportarMultiPdv.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
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

    private void jMenuItem30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem30ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaCheque == null || formPlanilhaCheque.isClosed()) {
                formPlanilhaCheque = new PlanilhaChequeGUI(this);
            }

            formPlanilhaCheque.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem30ActionPerformed

    private void jMenuItemPCSistemasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPCSistemasActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarPCSistemas == null || formImportarPCSistemas.isClosed()) {
                formImportarPCSistemas = new PCSistemasGUI(this);
            }

            formImportarPCSistemas.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemPCSistemasActionPerformed

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

        try {
            this.setWaitCursor();
            if (formImportarKairos == null || formImportarKairos.isClosed()) {
                formImportarKairos = new KairosGUI(this);
            }
            formImportarKairos.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemKairosActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        try {
            this.setWaitCursor();
            if (formImportarRootac == null || formImportarRootac.isClosed()) {
                formImportarRootac = new RootacGUI(this);
            }
            formImportarRootac.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItemDirectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDirectorActionPerformed

        try {
            this.setWaitCursor();
            if (formImportarDirector == null || formImportarDirector.isClosed()) {
                formImportarDirector = new DirectorGUI(this);
            }
            formImportarDirector.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemDirectorActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        try {
            this.setWaitCursor();
            if (formImportarMRS == null || formImportarMRS.isClosed()) {
                formImportarMRS = new MRSGUI(this);
            }
            formImportarMRS.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItemVRPdvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemVRPdvActionPerformed

        PdvVrGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemVRPdvActionPerformed

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
        try {
            this.setWaitCursor();
            if (formImportarSuperServer == null || formImportarSuperServer.isClosed()) {
                formImportarSuperServer = new SuperServerGUI(this);
            }

            formImportarSuperServer.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSuperServerActionPerformed

    private void jMenuItemCISSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCISSActionPerformed
        CissGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemCISSActionPerformed

    private void jMenuItemControlWareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemControlWareActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarControlWare == null || formImportarControlWare.isClosed()) {
                formImportarControlWare = new ControlWareGUI(this);
            }

            formImportarControlWare.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemControlWareActionPerformed

    private void jMenuItemDestroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDestroActionPerformed
    
        DestroGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemDestroActionPerformed

    private void jMenuItemSysMouraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSysMouraActionPerformed
        SisMouraGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemSysMouraActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaNCM == null || formPlanilhaNCM.isClosed()) {
                formPlanilhaNCM = new PlanilhaNCMGUI(this);
            }

            formPlanilhaNCM.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
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
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItemTopSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemTopSystemActionPerformed
        try {
            this.setWaitCursor();
            if (formTopSystemGUI == null || formTopSystemGUI.isClosed()) {
                formTopSystemGUI = new TopSystemGUI(this);
            }

            formTopSystemGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
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
        try {
            this.setWaitCursor();
            if (formImportarGZSistemas == null || formImportarGZSistemas.isClosed()) {
                formImportarGZSistemas = new GZSistemasGUI(this);
            }

            formImportarGZSistemas.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }

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

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarGetWayCloud == null || formImportarGetWayCloud.isClosed()) {
                formImportarGetWayCloud = new GetWayCloudGUI(this);
            }

            formImportarGetWayCloud.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_jMenuItem12ActionPerformed

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

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        try {
            this.setWaitCursor();
            if (formMigracaoVR == null || formMigracaoVR.isClosed()) {
                formMigracaoVR = new VRSoftwareGUI(this);
            }

            formMigracaoVR.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem13ActionPerformed

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
        try {
            this.setWaitCursor();
            if (formImportarSIMS == null || formImportarSIMS.isClosed()) {
                formImportarSIMS = new SIMSGUI(this);
            }

            formImportarSIMS.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemSIMSActionPerformed

    private void jMenuItemGR7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGR7ActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarGR7 == null || formImportarGR7.isClosed()) {
                formImportarGR7 = new GR7GUI(this);
            }

            formImportarGR7.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
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

    private void jMenuItem16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarPlanilhaPdvVenda == null || formImportarPlanilhaPdvVenda.isClosed()) {
                formImportarPlanilhaPdvVenda = new PlanilhaPdvVendaGUI(this);
            }

            formImportarPlanilhaPdvVenda.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        try {
            this.setWaitCursor();
            if (formImportarPlanilhaLogEstoque == null || formImportarPlanilhaLogEstoque.isClosed()) {
                formImportarPlanilhaLogEstoque = new PlanilhaLogEstoqueGUI(this);
            }

            formImportarPlanilhaLogEstoque.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void mnuPlanilhaFornActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlanilhaFornActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaFornecedor == null || formPlanilhaFornecedor.isClosed()) {
                formPlanilhaFornecedor = new PlanilhaFornecedorGUI(this);
            }

            formPlanilhaFornecedor.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuPlanilhaFornActionPerformed

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
        try {
            this.setWaitCursor();
            if (formImportarInfoBrasil == null || formImportarInfoBrasil.isClosed()) {
                formImportarInfoBrasil = new InfoBrasilGUI(this);
            }

            formImportarInfoBrasil.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItemInfoBrasilActionPerformed

    private void mnuPlanilhaV2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPlanilhaV2ActionPerformed
        PlanilhaV2GUI.Exibir(this);
    }//GEN-LAST:event_mnuPlanilhaV2ActionPerformed

    private void jMenuItem26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem26ActionPerformed
        try {
            this.setWaitCursor();
            if (formPlanilhaInventario == null || formPlanilhaInventario.isClosed()) {
                formPlanilhaInventario = new PlanilhaInventarioGUI(this);
            }

            formPlanilhaInventario.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_jMenuItem26ActionPerformed

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

    private void mnuScriptsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuScriptsActionPerformed
        ScriptsGUI.Exibir(this);
    }//GEN-LAST:event_mnuScriptsActionPerformed

    private void jMenuItemAriusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAriusActionPerformed
        AriusGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemAriusActionPerformed

    private void jMenuItemActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemActiveActionPerformed
        ActiveGUI.exibir(this);
    }//GEN-LAST:event_jMenuItemActiveActionPerformed

    private void jMenuItem31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem31ActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formPlanilhaProdutosLanchonete == null || formPlanilhaProdutosLanchonete.isClosed()) {
                formPlanilhaProdutosLanchonete = new PlanilhaProdutosLanchoneteGUI(this);
            }

            formPlanilhaProdutosLanchonete.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }

    }//GEN-LAST:event_jMenuItem31ActionPerformed

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
        GestoraGUI.exibir(this);
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




    private void mnuRMS_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRMS_3ActionPerformed
        WinthorGUI.exibir(this);
    }//GEN-LAST:event_mnuRMS_3ActionPerformed

    private void mnuDelfiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDelfiActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarDelfi == null || formImportarDelfi.isClosed()) {
                formImportarDelfi = new DelfiiGUI(this);
            }
            formImportarDelfi.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_mnuDelfiActionPerformed

    private void mnuFabTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFabTechActionPerformed
        // TODO add your handling code here:
        try {
            this.setWaitCursor();
            if (formImportarFabTech == null || formImportarFabTech.isClosed()) {
                formImportarFabTech = new FabTechGUI(this);
            }
            formImportarFabTech.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
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
        try {
            this.setWaitCursor();
            if (formImportarSifat == null || formImportarSifat.isClosed()) {
                formImportarSifat = new SifatGUI(this);
            }

            formImportarSifat.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
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
        HipicomGUI.exibir(this);
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
        HipcomGUI.exibir(this);
    }//GEN-LAST:event_mnuHipcomActionPerformed

    private void mnuParametros1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuParametros1ActionPerformed
        SQLEditor.exibir(this);
    }//GEN-LAST:event_mnuParametros1ActionPerformed

    private void mnuAutoSystem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAutoSystem1ActionPerformed
        RMSAutomaHelpGUI.exibir(this);
    }//GEN-LAST:event_mnuAutoSystem1ActionPerformed
	
    private void mnuWebsaqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuWebsaqActionPerformed
        // TODO add your handling code here:
        WebSaqGUI.exibir(this);
    }//GEN-LAST:event_mnuWebsaqActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSair;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu11;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu14;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenu jMenu9;
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
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
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
    private javax.swing.JMenuItem jMenuItemPCSistemas;
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
    private javax.swing.JMenuItem jMenuItemVRPdv;
    private javax.swing.JMenuItem jMenuItemWisaSoft;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private vrframework.bean.label.VRLabel lblData;
    private vrframework.bean.label.VRLabel lblLoja;
    private vrframework.bean.label.VRLabel lblRazaoSocial;
    private vrframework.bean.label.VRLabel lblVersao;
    private javax.swing.JMenuItem mmnuDGCom;
    private javax.swing.JMenuItem mmnuGestora;
    private javax.swing.JMenu mnuADT;
    private javax.swing.JMenuItem mnuASoft;
    private javax.swing.JMenu mnuAjuda;
    private javax.swing.JMenuItem mnuAjudaSobre;
    private javax.swing.JMenuItem mnuApollo;
    private javax.swing.JMenuItem mnuAsefe;
    private javax.swing.JMenuItem mnuAutoSystem;
    private javax.swing.JMenuItem mnuAutoSystem1;
    private javax.swing.JMenu mnuAvancadas;
    private javax.swing.JMenuItem mnuAvance;
    private javax.swing.JMenuItem mnuBase;
    private javax.swing.JMenuItem mnuBrainSoft;
    private javax.swing.JMenuItem mnuCPGestor;
    private javax.swing.JMenu mnuCache;
    private javax.swing.JMenu mnuCaixa;
    private javax.swing.JMenuItem mnuCodigoBarrasAtacado;
    private javax.swing.JMenuItem mnuControll;
    private javax.swing.JMenuItem mnuCplus;
    private javax.swing.JMenu mnuDB2;
    private javax.swing.JMenuItem mnuDelfi;
    private javax.swing.JMenuItem mnuEmporio;
    private javax.swing.JMenuItem mnuEsSystem;
    private javax.swing.JMenuItem mnuFG;
    private javax.swing.JMenuItem mnuFabTech;
    private javax.swing.JMenuItem mnuFabTech1;
    private javax.swing.JMenuItem mnuFarm2000;
    private javax.swing.JMenu mnuFerramentas;
    private javax.swing.JMenuItem mnuFlatan;
    private javax.swing.JMenuItem mnuHipcom;
    private javax.swing.JMenuItem mnuHipicom;
    private javax.swing.JMenuItem mnuHostMundo;
    private javax.swing.JMenuItem mnuIdealSoft;
    private javax.swing.JMenuItem mnuImportarNCM;
    private javax.swing.JMenuItem mnuImportarNfce;
    private javax.swing.JMenuItem mnuInfoMacStore;
    private javax.swing.JMenuItem mnuInteragem;
    private javax.swing.JMenu mnuInterface;
    private javax.swing.JMenuItem mnuJacsys;
    private javax.swing.JMenu mnuJanela;
    private javax.swing.JMenuItem mnuJrf;
    private javax.swing.JMenuItem mnuLinner;
    private javax.swing.JMenuItem mnuLogus;
    private javax.swing.JMenuItem mnuMSIInfor;
    private javax.swing.JMenuItem mnuMapeamentoMercadologico;
    private javax.swing.JMenuBar mnuMenu;
    private javax.swing.JMenu mnuOrion;
    private javax.swing.JMenuItem mnuParametros;
    private javax.swing.JMenuItem mnuParametros1;
    private javax.swing.JMenuItem mnuPlanilhaForn;
    private javax.swing.JMenuItem mnuPlanilhaPadrao;
    private javax.swing.JMenuItem mnuPlanilhaV2;
    private javax.swing.JMenuItem mnuRCNet;
    private javax.swing.JMenuItem mnuRMS_2;
    private javax.swing.JMenuItem mnuRMS_3;
    private javax.swing.JMenuItem mnuRepleis;
    private javax.swing.JMenuItem mnuSDInformatica;
    private javax.swing.JMenuItem mnuSaac;
    private javax.swing.JMenuItem mnuSatecfe;
    private javax.swing.JMenuItem mnuScef;
    private javax.swing.JMenuItem mnuScripts;
    private javax.swing.JMenuItem mnuSicom;
    private javax.swing.JMenuItem mnuSifat;
    private javax.swing.JMenuItem mnuSigma;
    private javax.swing.JMenu mnuSistema;
    private javax.swing.JMenuItem mnuSistemaLogin;
    private javax.swing.JMenuItem mnuSolidus;
    private javax.swing.JMenuItem mnuSuper;
    private javax.swing.JMenuItem mnuTsti;
    private javax.swing.JMenuItem mnuVisualComercio;
    private javax.swing.JMenuItem mnuWebsaq;
    private javax.swing.JMenuItem mnuWmByFile;
    private javax.swing.JMenuItem mnuWmsi;
    private javax.swing.JMenuItem mnupdvvendaitem;
    private vrframework.bean.toolBar.VRToolBar tlbAtalho;
    private vrframework.bean.toolBar.VRToolBar tlbFixo;
    private javax.swing.JToolBar tlbToolBar;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.panel.VRPanel vRPanel5;
    private vrframework.bean.panel.VRPanel vRPanel6;
    private vrframework.bean.desktopPane.VRDesktopPane vrDesktopPane;
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
}
// IMPLANTACAO
