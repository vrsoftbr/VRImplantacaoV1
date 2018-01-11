package vrimplantacao.gui.assistente.mapamercadologico;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.MercadologicoMapDAO;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;

/**
 *
 * @author Leandro
 */
public class MapaMercadologicoGUI extends VRInternalFrame {
    
    private List<RowData> tableData;
    private List<MercadologicoVO> mercadologicoVR;
    private List<MercadologicoVO> mercSelecionado;
    private final MercadologicoMapDAO dao = new MercadologicoMapDAO();
    
    private void buscar() throws Exception {
        mercSelecionado = new ArrayList<>();
        for (MercadologicoVO vo: mercadologicoVR) {
            if (vo.getDescricao().toUpperCase().contains(txtBuscar.getText().toUpperCase())) {
                mercSelecionado.add(vo);
            }
        }
        
        Object[][] dados = new Object[mercSelecionado.size()][2];
        int i = 0;
        for (MercadologicoVO vo: mercSelecionado) {
            dados[i][0] = vo.getId();
            dados[i][1] = vo.getDescricao();
            i++;
        }        
        grdBuscaMerc.setModel(dados);
        if (dados.length > 0) {
            grdBuscaMerc.setLinhaSelecionada(0);
        }
    } 
    
    private MapaMercadologicoGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        
        mercadologicoVR = new MercadologicoDAO().carregarMercadologicoParaMapeamento();
        mercSelecionado = new ArrayList<>(mercadologicoVR);
        
        initComponents();  
        
        List<VRColumnTable> vColunaProduto = new ArrayList();
        vColunaProduto.add(new VRColumnTable("ID", true, SwingConstants.LEFT, true, null));
        vColunaProduto.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, true, null));      
        grdBuscaMerc.configurarColuna(vColunaProduto, this, "Mapa", "");
                
        txtBuscar.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }           

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {                    
                    try {
                        e.consume();
                        if (!mercSelecionado.isEmpty()) {
                            int index = tblMercadologico.getLinhaSelecionada();

                            RowData mapa = tableData.get(index);
                            mapa.setMercadologicoVR((int) mercSelecionado.get(grdBuscaMerc.getLinhaSelecionada()).getId());
                            mapa.salvar();

                            if (index < tableData.size() - 1) {
                                tblMercadologico.setLinhaSelecionada(index + 1);
                            }
                        }
                        txtBuscar.selectAll();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }                    
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    e.consume();
                    int index = grdBuscaMerc.getLinhaSelecionada();
                    if (index < mercSelecionado.size() - 1) {
                        grdBuscaMerc.setLinhaSelecionada(index + 1);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    e.consume();
                    int index = grdBuscaMerc.getLinhaSelecionada();
                    if (index > 0) {
                        grdBuscaMerc.setLinhaSelecionada(index - 1);
                    }
                } else {    
                    try {
                        e.consume();
                        buscar();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        Util.exibirMensagemErro(ex, title);
                    }
                }
            }
            
        });
        
        centralizarForm();
        this.setMaximum(false);
    }
    
    private static MapaMercadologicoGUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new MapaMercadologicoGUI(i_mdiFrame);
            }

            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCarregar = new vrframework.bean.button.VRButton();
        txtGravados = new vrframework.bean.label.VRLabel();
        tblMercadologico = new vrframework.bean.tableEx.VRTableEx();
        grdBuscaMerc = new vrframework.bean.tableEx.VRTableEx();
        txtBuscar = new javax.swing.JTextField();

        setTitle(org.openide.util.NbBundle.getMessage(MapaMercadologicoGUI.class, "MapaMercadologicoGUI.title")); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                onClose(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnCarregar, org.openide.util.NbBundle.getMessage(MapaMercadologicoGUI.class, "MapaMercadologicoGUI.btnCarregar.text")); // NOI18N
        btnCarregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarregarActionPerformed(evt);
            }
        });

        txtGravados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(txtGravados, org.openide.util.NbBundle.getMessage(MapaMercadologicoGUI.class, "MapaMercadologicoGUI.txtGravados.text")); // NOI18N

        txtBuscar.setText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grdBuscaMerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tblMercadologico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCarregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                        .addComponent(txtGravados, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtBuscar))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCarregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGravados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(grdBuscaMerc, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblMercadologico, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void btnCarregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarregarActionPerformed
        try {
            tableData = new ArrayList<>();
            List<MercadologicoMapaVO> mapeado = dao.carregarMapa();
            
            for (MercadologicoMapaVO mapa: mapeado) {
                RowData.make(tableData, mapa);
            }
            
            RowData.carregarComboBox();

            List<VRColumnTable> vColunaProduto = new ArrayList();
            vColunaProduto.add(new VRColumnTable("Chave", true, SwingConstants.LEFT, true, null));
            vColunaProduto.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, true, null));      
            vColunaProduto.add(new VRColumnTable("Mercadológico VR", true, SwingConstants.LEFT, true, null));
            tblMercadologico.configurarColuna(vColunaProduto, this, "Mapa", "exibirMercadologicos");
            
            Object[][] dados = new Object[tableData.size()][tblMercadologico.getvColuna().size()];
            int i = 0;
            for (RowData row: tableData) {
                dados[i][0] = row.getKey();
                dados[i][1] = row.getDescricao();
                dados[i][2] = row;
                i++;
            }            
            
            tblMercadologico.setModel(dados);
            tblMercadologico.setRowHeight(30);
            tblMercadologico.atualizarTamanhoColuna();            
            tblMercadologico.getColumnModel().getColumn(2).setCellEditor(new CustomCellEditor());
            tblMercadologico.getColumnModel().getColumn(2).setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    RowData rowData = (RowData) value;
                    //return new JLabel(rowData.getMercadologico() != null ? rowData.getMercadologico().getDescricao() : "");
                    return rowData.getComponent();
                }
            });
            if (!tableData.isEmpty()) {
                tblMercadologico.setLinhaSelecionada(0);
            }
            
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, title);
        }
    }//GEN-LAST:event_btnCarregarActionPerformed

    private class CustomCellEditor extends AbstractCellEditor implements TableCellEditor {
        
        private RowData rowData;
        
        @Override
        public Object getCellEditorValue() {
            return rowData;
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            rowData = (RowData) value;
            return rowData.getComponent();
        }
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnCarregar;
    private vrframework.bean.tableEx.VRTableEx grdBuscaMerc;
    private vrframework.bean.tableEx.VRTableEx tblMercadologico;
    private javax.swing.JTextField txtBuscar;
    private vrframework.bean.label.VRLabel txtGravados;
    // End of variables declaration//GEN-END:variables

    
}
