package vrimplantacao2.gui.component.mapatributacao;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import org.openide.util.Exceptions;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.vo.enums.Icms;

/**
 *
 * @author Leandro
 */
public class MapaTributacaoView extends JPanel {
    
    private MapaTributacaoController controller = new MapaTributacaoController(this);

    public MapaTributacaoController getController() {
        return controller;
    }

    public String getSistema() {
        return controller.getSistema();
    }

    public void setSistema(String sistema) {
        controller.setSistema(sistema);
        txtSistema.setText(sistema);
    }

    public String getAgrupador() {
        return controller.getAgrupador();
    }

    public void setAgrupador(String agrupador) {
        controller.setAgrupador(agrupador);
        txtAgrupador.setText(agrupador);
    }

    public void setProvider(MapaTributoProvider provider) {
        controller.setProvider(provider);
    }

    public MapaTributoProvider getProvider() {
        return controller.getProvider();
    }
    
    /**
     * Creates new form MapaTributacaoPanel
     */
    public MapaTributacaoView() {
        initComponents();
    }    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        txtBusca = new javax.swing.JTextField();
        btnAtualizar = new javax.swing.JButton();
        tblVR = new vrframework.bean.tableEx.VRTableEx();
        jCheckBox1 = new javax.swing.JCheckBox();
        tblMapa = new vrframework.bean.tableEx.VRTableEx();
        jLabel1 = new javax.swing.JLabel();
        txtSistema = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtAgrupador = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.jLabel3.text")); // NOI18N

        txtBusca.setText(org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.txtBusca.text")); // NOI18N
        txtBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaKeyRelesed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnAtualizar, org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.btnAtualizar.text")); // NOI18N
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarOnClick(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox1, org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.jCheckBox1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.jLabel1.text")); // NOI18N

        txtSistema.setEditable(false);
        txtSistema.setText(org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.txtSistema.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.jLabel2.text")); // NOI18N

        txtAgrupador.setEditable(false);
        txtAgrupador.setText(org.openide.util.NbBundle.getMessage(MapaTributacaoView.class, "MapaTributacaoView.txtAgrupador.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblVR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                        .addComponent(btnAtualizar))
                    .addComponent(tblMapa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtSistema))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtAgrupador, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAtualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblVR, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblMapa, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAgrupador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAtualizarOnClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarOnClick
        try {
            controller.atualizarMapa();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "");            
        }
    }//GEN-LAST:event_btnAtualizarOnClick

    static JDialog frame;
    public static void exibir(Frame mdiFrame, String sistema, String agrupador, MapaTributoProvider provider) throws Exception {
        frame = new JDialog(mdiFrame, "Mapa de Tributação", true);
        MapaTributacaoView view = new MapaTributacaoView();
        view.setSistema(sistema);
        view.setAgrupador(agrupador);
        view.setProvider(provider);
        view.controller.atualizarMapa();
        
        
        frame.setContentPane(view);
        frame.setPreferredSize(new Dimension(600, 600));
        frame.pack();

        Dimension ds = mdiFrame.getSize();
        Dimension dw = frame.getSize();

        int x = (ds.width - dw.width) / 2;
        int y = (ds.height - dw.height) / 3;

        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }

        frame.setLocation(x, y);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        frame.setVisible(true);
        
        MapaTributacaoView.frame = null;
    }
    
    private void txtBuscaKeyRelesed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaKeyRelesed
        try {
            int keyCode = evt.getKeyCode();
            evt.consume();
            if (keyCode == KeyEvent.VK_ENTER) {
                txtBusca.selectAll();
                controller.gravarTributo();
            } else if (keyCode == KeyEvent.VK_DOWN) {
                controller.nextTributVR();
            } else if (keyCode == KeyEvent.VK_UP) {
                controller.previousTributVR();
            } else if (keyCode == KeyEvent.VK_ESCAPE) {
                if (MapaTributacaoView.frame != null) {
                    MapaTributacaoView.frame.setVisible(false);
                }
            } else {
                controller.buscar(txtBusca.getText());
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "");
        } 
    }//GEN-LAST:event_txtBuscaKeyRelesed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnAtualizar;
    javax.swing.JCheckBox jCheckBox1;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel2;
    javax.swing.JLabel jLabel3;
    vrframework.bean.tableEx.VRTableEx tblMapa;
    vrframework.bean.tableEx.VRTableEx tblVR;
    javax.swing.JTextField txtAgrupador;
    javax.swing.JTextField txtBusca;
    javax.swing.JTextField txtSistema;
    // End of variables declaration//GEN-END:variables

    private void atualizarMapa() throws Exception {
        
    }

    private void buscar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void refresh() throws Exception {
        List<VRColumnTable> vColunaProduto = new ArrayList();
        vColunaProduto.add(new VRColumnTable("Código", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, false, null));      
        vColunaProduto.add(new VRColumnTable("Cód. VR", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Descrição VR", true, SwingConstants.LEFT, false, null));      
        vColunaProduto.add(new VRColumnTable("CST", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Aliq.", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Red.", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Aliq. Final", true, SwingConstants.LEFT, false, null));
        tblMapa.configurarColuna(vColunaProduto, this, "Mapa", "mapaTributacoes");
        
        Object[][] dados = new Object[controller.getMapa().size()][tblMapa.getvColuna().size()];
        int i = 0;
        for (MapaTributoVO row: controller.getMapa()) {
            
            dados[i][0] = row.getOrigId();
            dados[i][1] = row.getOrigDescricao();
            if (row.getAliquota() == null) {
                dados[i][2] = "";
                dados[i][3] = "";
                dados[i][4] = "";
                dados[i][5] = "";
                dados[i][6] = "";
                dados[i][7] = "";
            } else {
                double aliq = row.getAliquota().getAliquota();
                double red = row.getAliquota().getReduzido();
                double aliqFinal = MathUtils.round(aliq - (aliq * (red / 100)), 2);
                
                dados[i][2] = row.getAliquota().getId();
                dados[i][3] = row.getAliquota().getDescricao();
                dados[i][4] = String.format("%02d", row.getAliquota().getCst());
                dados[i][5] = String.format("%.02f", row.getAliquota().getAliquota());
                dados[i][6] = String.format("%.02f", row.getAliquota().getReduzido());
                dados[i][7] = String.format("%.02f", aliqFinal);
            }
            i++;
        }            

        tblMapa.setModel(dados);
        tblMapa.setRowHeight(30);
        tblMapa.atualizarTamanhoColuna();
        if (tblMapa.getRowCount() > 0) {
            tblMapa.setLinhaSelecionada(0);
        }
    }

    void refreshBusca() throws Exception {
        List<VRColumnTable> vColunaProduto = new ArrayList();      
        vColunaProduto.add(new VRColumnTable("Cód. VR", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("CST", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Aliq.", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Red.", true, SwingConstants.LEFT, false, null));
        vColunaProduto.add(new VRColumnTable("Aliq. Final", true, SwingConstants.LEFT, false, null));
        tblVR.configurarColuna(vColunaProduto, this, "Trib", "tributos");
        
        Object[][] dados = new Object[controller.getAliquotas().size()][tblVR.getvColuna().size()];
        int i = 0;
        for (Icms aliquota: controller.getAliquotas()) {
            
            double aliq = aliquota.getAliquota();
            double red = aliquota.getReduzido();
            double aliqFinal = MathUtils.round(aliq - (aliq * (red / 100)), 2);
            
            dados[i][0] = aliquota.getId();
            dados[i][1] = aliquota.getDescricao();
            dados[i][2] = String.format("%02d", aliquota.getCst());
            dados[i][3] = String.format("%.02f", aliquota.getAliquota());
            dados[i][4] = String.format("%.02f", aliquota.getReduzido());
            dados[i][5] = String.format("%.02f", aliqFinal);
            
            i++;
        }            

        tblVR.setModel(dados);
        tblVR.setRowHeight(30);
        tblVR.atualizarTamanhoColuna();
        if (tblVR.getRowCount() > 0) {
            tblVR.setLinhaSelecionada(0);
        }
    }
}
