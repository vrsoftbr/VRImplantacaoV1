package vrimplantacao.gui.assistente.parametro;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.VerticalLayout;
import org.openide.util.Exceptions;
import vrframework.bean.button.VRButton;
import vrframework.bean.checkBox.VRCheckBox;
import vrframework.bean.comboBox.VRComboBox;
import vrframework.bean.fileChooser.VRFileChooser;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.label.VRLabel;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.panel.VRPanel;
import vrframework.bean.tabbedPane.VRTabbedPane;
import vrframework.bean.table.VRTable;
import vrframework.bean.textField.VRTextField;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.EstadoVO;
import vrimplantacao.vo.vrimplantacao.MunicipioVO;
import vrimplantacao2.dao.cadastro.LocalDAO;
import vrimplantacao2.dao.cadastro.financeiro.diversos.TipoPagamentoDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.logging.LoggingConfig;
import vrimplantacao2.utils.logging.LoggingType;
import vrimplantacao2.vo.enums.TipoPagamento;

/**
 *
 * @author Leandro
 */
public class ParametroGUI extends VRInternalFrame {
    
    private static final Logger LOG = Logger.getLogger(ParametroGUI.class.getName());
    
    private final Map<Integer, EstadoVO> estados = new LinkedHashMap<>();
    private final Parametros parametros = Parametros.get();
        
    private ParametroGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        
        initComponents();  
        
        carregarParametros();

        centralizarForm();
        this.setMaximum(false);
    }
    
    private static ParametroGUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {        
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new ParametroGUI(i_mdiFrame);
            }
            LOG.fine("Abrindo formulário de parâmetros");
            instance.setVisible(true);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error ao abrir o formulário de parâmetros", ex);
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdgLogLevel = new ButtonGroup();
        rdgLogType = new ButtonGroup();
        tabs = new VRTabbedPane();
        tabValorPadrão = new VRPanel();
        pnlLocalizacao = new VRPanel();
        vRLabel1 = new VRLabel();
        vRLabel2 = new VRLabel();
        vRLabel3 = new VRLabel();
        cmbUfPadrao = new VRComboBox();
        cmbMunicipioPadrao = new VRComboBox();
        txtCepPadrao = new VRTextField();
        vRPanel1 = new VRPanel();
        chkGerarBancoImplantacao = new VRCheckBox();
        chkImportarBancoImplantacao = new VRCheckBox();
        txtBancoImplantacao = new VRFileChooser();
        pnlDiversos = new VRPanel();
        vRLabel4 = new VRLabel();
        cmbTipoPagamento = new JComboBox();
        tabLogging = new VRPanel();
        btnLogGravar = new VRButton();
        btnLogCancelar = new VRButton();
        pnlLogDados = new VRPanel();
        jLabel1 = new JLabel();
        txtLogNome = new JTextField();
        pnlTipoLog = new VRPanel();
        rdTipoConsole = new JRadioButton();
        rdTipoArquivo = new JRadioButton();
        pnlNivelLog = new VRPanel();
        rdOff = new JRadioButton();
        rdSevere = new JRadioButton();
        rdWarning = new JRadioButton();
        rdInfo = new JRadioButton();
        rdConfig = new JRadioButton();
        rdFine = new JRadioButton();
        rdFiner = new JRadioButton();
        rdFinest = new JRadioButton();
        rdAll = new JRadioButton();
        btnLogExcluir = new VRButton();
        jScrollPane1 = new JScrollPane();
        tableLogging = new VRTable();
        btnGravar = new VRButton();
        btnCancelar = new VRButton();

        setTitle("Configurar parametros do sistema");
        addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameActivated(InternalFrameEvent evt) {
            }
            public void internalFrameClosed(InternalFrameEvent evt) {
                onClose(evt);
            }
            public void internalFrameClosing(InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(InternalFrameEvent evt) {
            }
            public void internalFrameIconified(InternalFrameEvent evt) {
            }
            public void internalFrameOpened(InternalFrameEvent evt) {
            }
        });

        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                tabsStateChanged(evt);
            }
        });

        tabValorPadrão.setBorder(BorderFactory.createEtchedBorder());

        pnlLocalizacao.setBorder(BorderFactory.createTitledBorder("Localização"));

        vRLabel1.setText("Estado");

        vRLabel2.setText("Cidade");

        vRLabel3.setText("Cep");

        cmbUfPadrao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmbUfPadraoActionPerformed(evt);
            }
        });

        cmbMunicipioPadrao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmbMunicipioPadraoActionPerformed(evt);
            }
        });

        txtCepPadrao.setMascara("Cep");

        GroupLayout pnlLocalizacaoLayout = new GroupLayout(pnlLocalizacao);
        pnlLocalizacao.setLayout(pnlLocalizacaoLayout);
        pnlLocalizacaoLayout.setHorizontalGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalizacaoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cmbUfPadrao, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLocalizacaoLayout.createSequentialGroup()
                        .addComponent(vRLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 272, Short.MAX_VALUE))
                    .addComponent(cmbMunicipioPadrao, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtCepPadrao, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlLocalizacaoLayout.setVerticalGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlLocalizacaoLayout.createSequentialGroup()
                .addGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cmbUfPadrao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlLocalizacaoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbMunicipioPadrao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCepPadrao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        vRPanel1.setBorder(BorderFactory.createTitledBorder("Nome do banco implantação"));

        chkGerarBancoImplantacao.setText("Gerar banco");

        chkImportarBancoImplantacao.setText("Importar do banco");

        GroupLayout vRPanel1Layout = new GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(vRPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkGerarBancoImplantacao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkImportarBancoImplantacao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtBancoImplantacao, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        vRPanel1Layout.setVerticalGroup(vRPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(chkGerarBancoImplantacao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBancoImplantacao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkImportarBancoImplantacao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDiversos.setBorder(BorderFactory.createTitledBorder("Diversos"));

        vRLabel4.setText("Tipo de Pagamento");

        GroupLayout pnlDiversosLayout = new GroupLayout(pnlDiversos);
        pnlDiversos.setLayout(pnlDiversosLayout);
        pnlDiversosLayout.setHorizontalGroup(pnlDiversosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiversosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDiversosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDiversosLayout.createSequentialGroup()
                        .addComponent(vRLabel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cmbTipoPagamento, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDiversosLayout.setVerticalGroup(pnlDiversosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlDiversosLayout.createSequentialGroup()
                .addComponent(vRLabel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTipoPagamento, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        GroupLayout tabValorPadrãoLayout = new GroupLayout(tabValorPadrão);
        tabValorPadrão.setLayout(tabValorPadrãoLayout);
        tabValorPadrãoLayout.setHorizontalGroup(tabValorPadrãoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tabValorPadrãoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabValorPadrãoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlLocalizacao, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDiversos, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabValorPadrãoLayout.setVerticalGroup(tabValorPadrãoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tabValorPadrãoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlLocalizacao, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDiversos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(205, Short.MAX_VALUE))
        );

        tabs.addTab("Valores padrão", tabValorPadrão);

        btnLogGravar.setText("Salvar");
        btnLogGravar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnLogGravarActionPerformed(evt);
            }
        });

        btnLogCancelar.setText("Cancela");
        btnLogCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnLogCancelarActionPerformed(evt);
            }
        });

        pnlLogDados.setBorder(null);

        jLabel1.setText("Nome do LOG");

        pnlTipoLog.setBorder(BorderFactory.createTitledBorder("Tipo de LOG"));
        VerticalLayout verticalLayout2 = new VerticalLayout();
        verticalLayout2.setGap(3);
        pnlTipoLog.setLayout(verticalLayout2);

        rdgLogType.add(rdTipoConsole);
        rdTipoConsole.setSelected(true);
        rdTipoConsole.setText("Console");
        pnlTipoLog.add(rdTipoConsole);

        rdgLogType.add(rdTipoArquivo);
        rdTipoArquivo.setText("Arquivo TXT");
        pnlTipoLog.add(rdTipoArquivo);

        pnlNivelLog.setBorder(BorderFactory.createTitledBorder("Nível do LOG"));
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setGap(3);
        pnlNivelLog.setLayout(verticalLayout1);

        rdgLogLevel.add(rdOff);
        rdOff.setText("Não logar nada");
        pnlNivelLog.add(rdOff);

        rdgLogLevel.add(rdSevere);
        rdSevere.setText("Erros");
        pnlNivelLog.add(rdSevere);

        rdgLogLevel.add(rdWarning);
        rdWarning.setText("Alertas");
        pnlNivelLog.add(rdWarning);

        rdgLogLevel.add(rdInfo);
        rdInfo.setSelected(true);
        rdInfo.setText("Informações importantes");
        pnlNivelLog.add(rdInfo);

        rdgLogLevel.add(rdConfig);
        rdConfig.setText("Avisos sobre configurações");
        pnlNivelLog.add(rdConfig);

        rdgLogLevel.add(rdFine);
        rdFine.setText("Informações do processo");
        pnlNivelLog.add(rdFine);

        rdgLogLevel.add(rdFiner);
        rdFiner.setText("Informações detalhadas do processo");
        pnlNivelLog.add(rdFiner);

        rdgLogLevel.add(rdFinest);
        rdFinest.setText("Informações altamente detalhadas");
        pnlNivelLog.add(rdFinest);

        rdgLogLevel.add(rdAll);
        rdAll.setText("Logar tudo");
        pnlNivelLog.add(rdAll);

        GroupLayout pnlLogDadosLayout = new GroupLayout(pnlLogDados);
        pnlLogDados.setLayout(pnlLogDadosLayout);
        pnlLogDadosLayout.setHorizontalGroup(pnlLogDadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(txtLogNome)
            .addComponent(jLabel1)
            .addComponent(pnlNivelLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlTipoLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlLogDadosLayout.setVerticalGroup(pnlLogDadosLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pnlLogDadosLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLogNome, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTipoLog, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlNivelLog, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnLogExcluir.setText("Excluir");
        btnLogExcluir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnLogExcluirActionPerformed(evt);
            }
        });

        tableLogging.setModel(new DefaultTableModel());
        tableLogging.setRowHeight(20);
        jScrollPane1.setViewportView(tableLogging);
        tableLogging.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    carregarItem();
                }
            }
        );

        GroupLayout tabLoggingLayout = new GroupLayout(tabLogging);
        tabLogging.setLayout(tabLoggingLayout);
        tabLoggingLayout.setHorizontalGroup(tabLoggingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, tabLoggingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(tabLoggingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(GroupLayout.Alignment.TRAILING, tabLoggingLayout.createSequentialGroup()
                        .addComponent(btnLogGravar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLogCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLogExcluir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlLogDados, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        tabLoggingLayout.setVerticalGroup(tabLoggingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(tabLoggingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabLoggingLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(tabLoggingLayout.createSequentialGroup()
                        .addComponent(pnlLogDados, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tabLoggingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLogGravar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLogCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLogExcluir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        tabs.addTab("Logging", tabLogging);

        btnGravar.setText("Gravar");
        btnGravar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGravarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(tabs, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGravar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnCancelar, btnGravar});

        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabs, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGravar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void btnCancelarActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        try {
            carregarParametros();
            Util.exibirMensagem("Valor dos parâmetros foram restaurados com sucesso!", title);
            LOG.finer("Parametros restaurados na tela");
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, title);
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGravarActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnGravarActionPerformed
        try {
            gravarParametros();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, title);
        }
    }//GEN-LAST:event_btnGravarActionPerformed

    private void cmbMunicipioPadraoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cmbMunicipioPadraoActionPerformed
        
    }//GEN-LAST:event_cmbMunicipioPadraoActionPerformed

    private void cmbUfPadraoActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cmbUfPadraoActionPerformed
        if (cmbUfPadrao.getSelectedItem() != null) {
            EstadoVO uf = estados.get(((ItemComboVO) cmbUfPadrao.getSelectedItem()).id);
            cmbMunicipioPadrao.removeAllItems();
            cmbMunicipioPadrao.setModel(new DefaultComboBoxModel());
            for (MunicipioVO municipio: uf.getMunicipios()) {
                cmbMunicipioPadrao.addItem(new ItemComboVO(municipio.getId(), municipio.getDescricao()));
            }
        }
    }//GEN-LAST:event_cmbUfPadraoActionPerformed

    private void tabsStateChanged(ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
        if (tabs.getSelectedIndex() == 1) {
            carregarLogging();
        }
    }//GEN-LAST:event_tabsStateChanged

    private void btnLogCancelarActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnLogCancelarActionPerformed
        cancelarLogging();
    }//GEN-LAST:event_btnLogCancelarActionPerformed

    private void btnLogGravarActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnLogGravarActionPerformed
        salvarLogging();
    }//GEN-LAST:event_btnLogGravarActionPerformed

    private void btnLogExcluirActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnLogExcluirActionPerformed
        excluirLogging();
    }//GEN-LAST:event_btnLogExcluirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private VRButton btnCancelar;
    private VRButton btnGravar;
    private VRButton btnLogCancelar;
    private VRButton btnLogExcluir;
    private VRButton btnLogGravar;
    private VRCheckBox chkGerarBancoImplantacao;
    private VRCheckBox chkImportarBancoImplantacao;
    private VRComboBox cmbMunicipioPadrao;
    private JComboBox cmbTipoPagamento;
    private VRComboBox cmbUfPadrao;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private VRPanel pnlDiversos;
    private VRPanel pnlLocalizacao;
    private VRPanel pnlLogDados;
    private VRPanel pnlNivelLog;
    private VRPanel pnlTipoLog;
    private JRadioButton rdAll;
    private JRadioButton rdConfig;
    private JRadioButton rdFine;
    private JRadioButton rdFiner;
    private JRadioButton rdFinest;
    private JRadioButton rdInfo;
    private JRadioButton rdOff;
    private JRadioButton rdSevere;
    private JRadioButton rdTipoArquivo;
    private JRadioButton rdTipoConsole;
    private JRadioButton rdWarning;
    private ButtonGroup rdgLogLevel;
    private ButtonGroup rdgLogType;
    private VRPanel tabLogging;
    private VRPanel tabValorPadrão;
    private VRTable tableLogging;
    private VRTabbedPane tabs;
    private VRFileChooser txtBancoImplantacao;
    private VRTextField txtCepPadrao;
    private JTextField txtLogNome;
    private VRLabel vRLabel1;
    private VRLabel vRLabel2;
    private VRLabel vRLabel3;
    private VRLabel vRLabel4;
    private VRPanel vRPanel1;
    // End of variables declaration//GEN-END:variables

    private void carregarMunicipiosEstados() throws Exception {
        estados.clear();
        cmbUfPadrao.removeAllItems();
        cmbUfPadrao.setModel(new DefaultComboBoxModel());
        for(EstadoVO estado: new LocalDAO().getEstados()) {
            estados.put(estado.getId(), estado);  
            cmbUfPadrao.addItem(new ItemComboVO(estado.getId(), estado.getSigla()));       
        }
    }
    
    private void carregarTipoPagamento() throws Exception {        
        cmbTipoPagamento.setModel(new DefaultComboBoxModel<>(new TipoPagamentoDAO().all().toArray(new TipoPagamento[]{})));
    }
    
    private void carregarParametros() throws Exception {        
        carregarMunicipiosEstados();  
        carregarTipoPagamento();

        parametros.carregar();        
        if (parametros.getUfPadrao() != null) {
            cmbUfPadrao.setId(parametros.getUfPadrao().getId());
        } else {
            cmbUfPadrao.setSelectedIndex(-1);
        }
        if (parametros.getMunicipioPadrao() != null) {
            cmbMunicipioPadrao.setId(parametros.getMunicipioPadrao().getId());
        } else {
            cmbMunicipioPadrao.setSelectedIndex(-1);
        }
        if (parametros.getCepPadrao() > 0) {
            txtCepPadrao.setText(String.valueOf(parametros.getCepPadrao()));        
        } else {
            txtCepPadrao.setText("");
        }
        if (parametros.getBancoImplantacao() != null) {
            txtBancoImplantacao.setArquivo(parametros.getBancoImplantacao());
        } else {
            txtBancoImplantacao.setArquivo("implantacao-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".db");
        }

        cmbTipoPagamento.setSelectedItem(parametros.getTipoPagamento());

        chkGerarBancoImplantacao.setSelected(parametros.isGerarBancoImplantacao());
        chkImportarBancoImplantacao.setSelected(parametros.isImportarBancoImplantacao());
        
        LOG.fine("Parametros carregados na tela");
    }
    
    private void gravarParametros() throws Exception {
        if (isValido()) {
            parametros.setUfPadrao(((ItemComboVO) cmbUfPadrao.getSelectedItem()).id);
            parametros.setMunicipioPadrao(((ItemComboVO) cmbMunicipioPadrao.getSelectedItem()).id);
            parametros.setCepPadrao(Utils.stringToInt(txtCepPadrao.getText()));
            parametros.setParametrosConfigurados(true);
            parametros.setBancoImplantacao(txtBancoImplantacao.getArquivo());
            parametros.setGerarBancoImplantacao(chkGerarBancoImplantacao.isSelected());
            parametros.setImportarBancoImplantacao(chkImportarBancoImplantacao.isSelected());
            parametros.setTipoPagamento((TipoPagamento) cmbTipoPagamento.getSelectedItem());
            parametros.salvar();
            Util.exibirMensagem("Parâmetros gravados com sucesso!", title);
        } else {
            Util.exibirMensagem("Campos obrigatórios não preenchidos", title, 0, JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isValido() {
        boolean valido = 
                (cmbUfPadrao.getSelectedItem() != null) &&
                (cmbMunicipioPadrao.getSelectedItem() != null) &&
                (cmbTipoPagamento.getSelectedItem() != null) &&
                (!"0".equals(Utils.formataNumero(txtCepPadrao.getText()))) &&
                (!"".equals(txtBancoImplantacao.getArquivo()));
        return valido;
    }
    
    private void carregarLogging() {
        try {
            preencherLogTable();
            
            setEnableds();
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro ao carregar dados do log \n\n" + Arrays.toString(e.getStackTrace()), e);
            Util.exibirMensagemErro(e, "Erro ao carregar dados do log");
        }
    }

    private void preencherLogTable() {
        try {
            
            int index = -1;
            if (tableLogging.getSelectedRow() >= 0) {
                index = tableLogging.convertRowIndexToModel(tableLogging.getSelectedRow());
            }
            
            tableLogging.setModel(new LoggingTableModel());

            if (index >= 0 && index < tableLogging.getModel().getRowCount()) {
                tableLogging.setRowSelectionInterval(index, index);
            } else if (index == tableLogging.getModel().getRowCount() && index > 0) {
                tableLogging.setRowSelectionInterval(index - 1, index - 1);
            } else if (tableLogging.getModel().getRowCount() > 0) {
                tableLogging.setRowSelectionInterval(0, 0);
            }
            
            tableLogging.atualizarTamanhoColuna();
            
            setEnableds();
        
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro ao preencher a tabela de log\n\n" + Arrays.toString(e.getStackTrace()), e);
            Util.exibirMensagemErro(e, "Erro ao preencher a tabela de log");
        }
    }

    private void setEnableds() {
        boolean existeLoggers = !parametros.getLoggers().isEmpty();
        btnLogCancelar.setEnabled(existeLoggers);
        btnLogExcluir.setEnabled(existeLoggers);
    }

    private void cancelarLogging() {
        carregarItem();
    }

    private void salvarLogging() {
        
        try {
            
            if ("".equals(txtLogNome.getText())) {
                Util.exibirMensagem("Preencha o nome do logger!", "Atenção");
            } else {
                LoggingConfig loggingSelecionado = parametros.getLoggers().get(txtLogNome.getText());
                boolean incluso = false;
                
                if (loggingSelecionado == null) {
                    loggingSelecionado = new LoggingConfig();
                    incluso = true;
                }

                loggingSelecionado.setNome(txtLogNome.getText());

                if (rdTipoConsole.isSelected()) {
                    loggingSelecionado.setType(LoggingType.CONSOLE);
                } else if (rdTipoArquivo.isSelected()) {
                    loggingSelecionado.setType(LoggingType.FILE);
                }

                if (rdOff.isSelected()) {
                    loggingSelecionado.setLevel(Level.OFF);
                } else if (rdSevere.isSelected()) {
                    loggingSelecionado.setLevel(Level.SEVERE);
                } else if (rdWarning.isSelected()) {
                    loggingSelecionado.setLevel(Level.WARNING);
                } else if (rdInfo.isSelected()) {
                    loggingSelecionado.setLevel(Level.INFO);
                } else if (rdConfig.isSelected()) {
                    loggingSelecionado.setLevel(Level.CONFIG);
                } else if (rdFine.isSelected()) {
                    loggingSelecionado.setLevel(Level.FINE);
                } else if (rdFiner.isSelected()) {
                    loggingSelecionado.setLevel(Level.FINER);
                } else if (rdFinest.isSelected()) {
                    loggingSelecionado.setLevel(Level.FINEST);
                } else if (rdAll.isSelected()) {
                    loggingSelecionado.setLevel(Level.ALL);
                }

                parametros.getLoggers().put(loggingSelecionado.getNome(), loggingSelecionado);

                preencherLogTable();
                
                if (incluso) {
                    tableLogging.setLinhaSelecionada(tableLogging.getRowCount() - 1);
                }
            
            }
                    
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Erro ao salvar a tabela de log\n\n" + Arrays.toString(e.getStackTrace()), e);
            Util.exibirMensagemErro(e, "Erro ao salvar a tabela de log");
        }
        
    }

    private void carregarItem() {
        
        int index = tableLogging.convertRowIndexToModel(tableLogging.getSelectedRow());  
        
        LoggingConfig loggingSelecionado;
        if (index >= 0) {
            loggingSelecionado = ((LoggingTableModel) tableLogging.getModel()).getLines().get(index);
        } else {
            loggingSelecionado = new LoggingConfig();
        }
        
        if (loggingSelecionado != null) {
            txtLogNome.setText(loggingSelecionado.getNome());
            
            switch (loggingSelecionado.getType()) {
                case CONSOLE: rdTipoConsole.setSelected(true); break;
                case FILE: rdTipoArquivo.setSelected(true); break;            
            }
            
            if (Level.OFF.equals(loggingSelecionado.getLevel())) {
                rdOff.setSelected(true);
            } else if (Level.SEVERE.equals(loggingSelecionado.getLevel())) {
                rdSevere.setSelected(true);
            } else if (Level.WARNING.equals(loggingSelecionado.getLevel())) {
                rdWarning.setSelected(true);
            } else if (Level.INFO.equals(loggingSelecionado.getLevel())) {
                rdInfo.setSelected(true);
            } else if (Level.CONFIG.equals(loggingSelecionado.getLevel())) {
                rdConfig.setSelected(true);
            } else if (Level.FINE.equals(loggingSelecionado.getLevel())) {
                rdFine.setSelected(true);
            } else if (Level.FINER.equals(loggingSelecionado.getLevel())) {
                rdFiner.setSelected(true);
            } else if (Level.FINEST.equals(loggingSelecionado.getLevel())) {
                rdFinest.setSelected(true);
            } else if (Level.ALL.equals(loggingSelecionado.getLevel())) {
                rdAll.setSelected(true);
            }
        }
        
    }

    private void excluirLogging() {
        if (JOptionPane.showConfirmDialog(
                this, 
                "Deseja excluir este Logger?", 
                "Confirme", 
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION) {
            int index = tableLogging.convertRowIndexToModel(tableLogging.getSelectedRow());        
            LoggingConfig loggingSelecionado = ((LoggingTableModel) tableLogging.getModel()).getLines().get(index);            
            parametros.getLoggers().remove(loggingSelecionado.getNome());            
                        
            preencherLogTable();
        }
    }

 
    private class LoggingTableModel extends AbstractTableModel {
    
        private List<LoggingConfig> lines = new ArrayList<>(parametros.getLoggers().values());

        public List<LoggingConfig> getLines() {
            return lines;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0: return "Nome do Logger"; 
                case 1: return "Tipo"; 
                case 2: return "Nível"; 
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public int getRowCount() {
            return parametros.getLoggers().size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LoggingConfig log = lines.get(rowIndex);
            switch (columnIndex) {
                case 0: return log.getNome();
                case 1: return log.getType().toString();
                case 2: return log.getLevel().toString();
            }
            return null;
        }
    
    }
}
