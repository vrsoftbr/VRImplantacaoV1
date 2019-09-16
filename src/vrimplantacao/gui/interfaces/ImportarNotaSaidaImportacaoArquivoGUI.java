package vrimplantacao.gui.interfaces;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import vrimplantacao.gui.interfaces.rfd.ExportacaoDivergenciaGUI;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import vrimplantacao.dao.notafiscal.NotaSaidaDAO;
import vrimplantacao.dao.notafiscal.ImportarNotaSaidaImportacaoDAO;
import vrimplantacao.vo.notafiscal.NotaSaidaVO;
import vrimplantacao.vo.notafiscal.TipoSaidaVO;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.dao.fiscal.CfopDAO;
import vrimplantacao.dao.notafiscal.ImportarNotaSaidaImportacaoDAO.LojaV2;
import static vrimplantacao.dao.notafiscal.ImportarNotaSaidaImportacaoDAO.getXML;
import vrimplantacao.vo.interfaces.DivergenciaVO;

public class ImportarNotaSaidaImportacaoArquivoGUI extends VRInternalFrame {   
    
    ImportarNotaSaidaImportacaoDAO dao;
    XmlModel model;

    public ImportarNotaSaidaImportacaoArquivoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        this.dao = new ImportarNotaSaidaImportacaoDAO();
        initComponents();

        carregarLojaV2();
        
        centralizarForm();
        
        flcArquivo.setMultiplosSelecionados(true);
        tblNotas.setModel(new XmlModel());        
        tblNotas.setRowHeight(25);
    }
        
    private void carregarNotas() throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setStatus("Carregando os XMLs das Notas Fiscais de Saída...");

                    model = new XmlModel(flcArquivo.getArquivo().split(";"));
                    tblNotas.setModel(model);
                    tblNotas.getColumnModel().getColumn(2).setCellEditor(new TipoSaidaCellEditor(model));

                    NotaSaidaDAO notaSaidaDAO = new NotaSaidaDAO();

                    if (cboLojaV2.getItemCount() > 0) {
                        dao.impLoja = (LojaV2) cboLojaV2.getSelectedItem();
                    }

                    NfeFile[] itens = model.getItens();

                    ProgressBar.setStatus("Importando Notas Fiscais...");
                    ProgressBar.setMaximum(itens.length);

                    dao.setIncluirClienteEventual(chkCriarEventual.isSelected());
                    for (NfeFile file: itens) {
                        
                        file.notaSaida = dao.carregar(
                            file.arquivo.getAbsolutePath(),
                            file.tipoSaida.id, 
                            chkVerificarCodigoAnterior.isSelected()
                        );                        
                        if (notaSaidaDAO.isNotaExistente(file.notaSaida.chaveNfe)) {
                            file.situacao = new DivergenciaVO(
                                    DivergenciaVO.NOTA_JA_IMPORTADA,
                                    "Nota fiscal já existente no VR",
                                    0
                            );
                        }
                        
                        if (!file.notaSaida.vDivergencia.isEmpty()) {
                            file.situacao = file.notaSaida.vDivergencia.get(0);
                        }
                        
                        ProgressBar.next();
                        model.fireTableDataChanged();
                    }
                } catch (Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                } finally {
                    ProgressBar.dispose();
                }
            }
        };

        thread.start();
        
    }

    @Override
    public void importar() throws Exception {
        Util.validarCampoTela(this.getCampoObrigatorio());

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {                    
                    if (cboLojaV2.getItemCount() > 0) {
                        dao.impLoja = (LojaV2) cboLojaV2.getSelectedItem();
                    }
                    
                    NfeFile[] itens = model.getItens();                            
                    
                    ProgressBar.show();
                    ProgressBar.setStatus("Importando Notas Fiscais...");
                    ProgressBar.setMaximum(itens.length);
                    
                    for (int i = 0; i < itens.length; i++) {
                        NfeFile file = itens[i];
                        tblNotas.setRowSelectionInterval(i, i);
                            
                        if (
                                file.situacao.id == DivergenciaVO.OK || 
                                (
                                    file.situacao.id == DivergenciaVO.NOTA_JA_IMPORTADA && 
                                    chkReimportarNotasExistentes.isSelected()
                                ) ||
                                (
                                    file.situacao.id ==  DivergenciaVO.CNPJ_DESTINATARIO_NAO_ENCONTRADO &&
                                    chkCriarEventual.isSelected()
                                )
                        ) {
                            NotaSaidaDAO notaSaidaDAO = new NotaSaidaDAO();
                            notaSaidaDAO.setCriarEventualCasoCnpjNaoExista(chkCriarEventual.isSelected());
                            notaSaidaDAO.salvar(file.notaSaida, chkCriarEventual.isSelected());
                            file.situacao = new DivergenciaVO(DivergenciaVO.NOTA_JA_IMPORTADA, "Nota importada com sucesso!", 1);
                            model.fireTableDataChanged();
                        }
                        
                        ProgressBar.next();
                    }
                    
                    ProgressBar.dispose();
                    Util.exibirMensagem("Notas Fiscais importada com sucesso!", getTitle());
                    
                } catch (Exception ex) {
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        thread.start();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tlbToolBar = new vrframework.bean.toolBarPadrao.VRToolBarPadrao(this);
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        flcArquivo = new vrframework.bean.fileChooser.VRFileChooser();
        chkVerificarCodigoAnterior = new vrframework.bean.checkBox.VRCheckBox();
        chkReimportarNotasExistentes = new vrframework.bean.checkBox.VRCheckBox();
        chkCriarEventual = new vrframework.bean.checkBox.VRCheckBox();
        scrollNotas = new javax.swing.JScrollPane();
        tblNotas = new javax.swing.JTable();
        vRLabel35 = new vrframework.bean.label.VRLabel();
        cboLojaV2 = new javax.swing.JComboBox();
        btnDivergencias = new vrframework.bean.button.VRButton();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnImportar = new vrframework.bean.button.VRButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Importação de Nota Saída");

        tlbToolBar.setRollover(true);
        tlbToolBar.setVisibleImportar(true);

        vRLabel1.setText("Arquivos de Nf-e de Saída");

        flcArquivo.setObrigatorio(true);
        flcArquivo.addEventoFileChooserListener(new vrframework.bean.fileChooser.VREventoFileChooserListener() {
            public void aposSelecao(vrframework.bean.fileChooser.VREventoFileChooser evt) {
                flcArquivoAposSelecao(evt);
            }
        });

        chkVerificarCodigoAnterior.setText("Verificar Código Anterior");

        chkReimportarNotasExistentes.setText("Reimportar notas existentes");

        chkCriarEventual.setText("Criar como eventual CFP/CNPJ não encontrado");

        tblNotas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Arquivo da Nf-e", "Natureza Operação na NF-e", "Tipo de Saída", "Situação"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollNotas.setViewportView(tblNotas);

        vRLabel35.setText("Lojas encontradas (Importação V2)");

        cboLojaV2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboLojaV2ItemStateChanged(evt);
            }
        });
        cboLojaV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLojaV2ActionPerformed(evt);
            }
        });

        btnDivergencias.setText("Divergências");
        btnDivergencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDivergenciasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cboLojaV2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollNotas)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, vRPanel1Layout.createSequentialGroup()
                                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(flcArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(vRPanel1Layout.createSequentialGroup()
                                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(vRPanel1Layout.createSequentialGroup()
                                                .addComponent(chkVerificarCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(chkReimportarNotasExistentes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(chkCriarEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDivergencias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDivergencias, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(flcArquivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkVerificarCodigoAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollNotas, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLojaV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkReimportarNotasExistentes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkCriarEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnImportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnImportar.setText("Importar");
        btnImportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImportar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnImportar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tlbToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tlbToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private void btnImportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarActionPerformed
        try {
            this.setWaitCursor();
            importar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnImportarActionPerformed
    private void flcArquivoAposSelecao(vrframework.bean.fileChooser.VREventoFileChooser evt) {//GEN-FIRST:event_flcArquivoAposSelecao
        try {
            this.setWaitCursor();
            carregarNotas();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_flcArquivoAposSelecao

    private void cboLojaV2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLojaV2ActionPerformed
        // TODO add your handling code here:        
    }//GEN-LAST:event_cboLojaV2ActionPerformed

    private void cboLojaV2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboLojaV2ItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLojaV2ItemStateChanged

    private void btnDivergenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDivergenciasActionPerformed
        NfeFile file = model.getItem(tblNotas.convertRowIndexToModel(tblNotas.getSelectedRow()));
        if (file != null && file.notaSaida.vDivergencia != null && !file.notaSaida.vDivergencia.isEmpty()) {
            try {
                ExportacaoDivergenciaGUI form = new ExportacaoDivergenciaGUI(mdiFrame);
                form.setDivergencia(file.notaSaida.vDivergencia);
                form.exibeDivergencia();
                form.setVisible(true);
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, title);
            }
        }
    }//GEN-LAST:event_btnDivergenciasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnDivergencias;
    private vrframework.bean.button.VRButton btnImportar;
    private vrframework.bean.button.VRButton btnSair;
    private javax.swing.JComboBox cboLojaV2;
    private vrframework.bean.checkBox.VRCheckBox chkCriarEventual;
    private vrframework.bean.checkBox.VRCheckBox chkReimportarNotasExistentes;
    private vrframework.bean.checkBox.VRCheckBox chkVerificarCodigoAnterior;
    private vrframework.bean.fileChooser.VRFileChooser flcArquivo;
    private javax.swing.JScrollPane scrollNotas;
    private javax.swing.JTable tblNotas;
    private vrframework.bean.toolBarPadrao.VRToolBarPadrao tlbToolBar;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel35;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    // End of variables declaration//GEN-END:variables

    private void carregarLojaV2() throws Exception {
        ImportarNotaSaidaImportacaoDAO dao = new ImportarNotaSaidaImportacaoDAO();
        if (dao.isImportacaoV2()) {
            cboLojaV2.removeAllItems();
            for (LojaV2 oTipo :dao.carregarLojaV2()) {
                cboLojaV2.addItem(oTipo);
            }
        }
    }

}

class NfeFile {
    
    File arquivo;
    String naturezaOperacao;
    String cfop;
    TipoSaidaVO tipoSaida;
    DivergenciaVO situacao;
    NotaSaidaVO notaSaida;
    
    public NfeFile(String arquivo) throws Exception {
        this.arquivo = new File(arquivo);
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        docBuilder.setErrorHandler(null);

        Document doc = docBuilder.parse(new ByteArrayInputStream(getXML(this.arquivo.getAbsolutePath()).getBytes("utf-8")));

        Element infNFe = (Element) doc.getDocumentElement().getElementsByTagName("infNFe").item(0);
        Element ide = (Element) infNFe.getElementsByTagName("ide").item(0);
        
        this.naturezaOperacao = ide.getElementsByTagName("natOp").item(0).getTextContent();        
        
        Element det = (Element) infNFe.getElementsByTagName("det").item(0);
        Element prod = (Element) det.getElementsByTagName("prod").item(0);

        this.cfop = prod.getElementsByTagName("CFOP").item(0).getTextContent();
        this.cfop = cfop.substring(0, 1).concat(".").concat(cfop.substring(1, 4));
        
        List<TipoSaidaVO> tiposDeSaida = new CfopDAO().carregarTipoSaida(cfop);
        if (!tiposDeSaida.isEmpty()) {
            this.tipoSaida = tiposDeSaida.get(0);
        }

    }
    
}

class XmlModel extends AbstractTableModel {

    private final NfeFile[] arquivos;

    public XmlModel(String... arquivos) throws Exception {
        this.arquivos = new NfeFile[arquivos.length];
        for (int i = 0; i < arquivos.length; i++ ) {
            this.arquivos[i] = new NfeFile(arquivos[i]);
        }
    }
    
    @Override
    public int getRowCount() {
        return arquivos.length;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return arquivos[rowIndex].arquivo.getName();
            case 1: return arquivos[rowIndex].naturezaOperacao;
            case 2: return arquivos[rowIndex].tipoSaida != null ? arquivos[rowIndex].tipoSaida.descricao : "--- SEM TIPO SAIDA CORRESPONDENTE ---";
            case 3: return arquivos[rowIndex].situacao != null ? arquivos[rowIndex].situacao.descricao : "";
            default: return "";
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0: return "Arquivo Nf-e";
            case 1: return "Natureza da Operação Nf-e";
            case 2: return "Tipo de Saída VR";
            case 3: return "Situação";
        }
        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 2) {
            arquivos[rowIndex].tipoSaida = (TipoSaidaVO) aValue;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2 && arquivos[rowIndex].tipoSaida != null;
    }

    public NfeFile getItem(int row) {
        return this.arquivos[row];
    }

    public NfeFile[] getItens() {
        return arquivos;
    }

}

class XmlFileCellRenderer extends DefaultTableCellRenderer {
    
    private final XmlModel model;    

    public XmlFileCellRenderer(XmlModel model) {
        this.model = model;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        NfeFile item = this.model.getItem(row);
        
        if (column == 4) {
            
        }
        
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
}

class TipoSaidaCellEditor extends DefaultCellEditor {
    
    private final XmlModel model;    

    public TipoSaidaCellEditor(XmlModel model) {
        super(new JComboBox());
        this.model = model;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {       
        if (column == 2) {            
            String cfop = model.getItem(row).cfop;            
            
            List<TipoSaidaVO> vTipoSaida;
            try {
                vTipoSaida = new CfopDAO().carregarTipoSaida(cfop);
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
            
            JComboBox cboTipoNotaSaida = (JComboBox)getComponent();            
            cboTipoNotaSaida.removeAllItems();
            for (TipoSaidaVO oTipo : vTipoSaida) {
                cboTipoNotaSaida.addItem(oTipo);
            }
            return cboTipoNotaSaida;
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
    
}