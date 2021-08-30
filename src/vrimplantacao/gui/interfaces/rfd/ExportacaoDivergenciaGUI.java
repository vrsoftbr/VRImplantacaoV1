package vrimplantacao.gui.interfaces.rfd;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import vrimplantacao.vo.interfaces.DivergenciaVO;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;

public class ExportacaoDivergenciaGUI extends VRInternalFrame {

    private ArrayList<DivergenciaVO> vDivergencia = null;

    public ExportacaoDivergenciaGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        centralizarForm();
    }

    public void exibeDivergencia() throws Exception {
        
        VRColumnTable[] colunas = new VRColumnTable[]{
            new VRColumnTable("", 35, true, SwingConstants.CENTER, false, null),
            new VRColumnTable("Descrição", 500, true, SwingConstants.LEFT, false, null)
        };

        Object[][] dados = new Object[vDivergencia.size()][colunas.length];

        int i = 0;

        for (DivergenciaVO oDivergencia : vDivergencia) {
            dados[i][0] = "";
            dados[i][1] = oDivergencia.descricao;

            i++;
        }

        tblConsulta.setRowHeight(60);
        tblConsulta.setModel(dados, colunas);
        tblConsulta.setDefaultRenderer(Object.class, new MyTableCellRenderer());

        tblConsulta.requestFocus();
    }

    public void setDivergencia(ArrayList<DivergenciaVO> i_divergencia) {
        this.vDivergencia = i_divergencia;
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRPanel2 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        tblConsulta = new vrframework.bean.tableEx.VRTableEx();

        setTitle("Divergências de Exportação");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel2Layout.createSequentialGroup()
                .addContainerGap(483, Short.MAX_VALUE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tblConsulta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.tableEx.VRTableEx tblConsulta;
    private vrframework.bean.panel.VRPanel vRPanel2;
    // End of variables declaration//GEN-END:variables
    private class MyTableCellRenderer implements TableCellRenderer {
        public MyTableCellRenderer() {
            super();
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            JLabel lblCampo = new JLabel(value == null ? "" : value.toString());
            lblCampo.setOpaque(true);
            if (isSelected) {
                lblCampo.setBackground(UIManager.getColor("Table.selectionBackground"));
                lblCampo.setForeground(UIManager.getColor("Table.selectionForeground"));
            } else {
                if (row % 2 == 0) {
                    lblCampo.setBackground(UIManager.getColor("Table.background"));
                } else {
                    lblCampo.setBackground(Util.COR_ZEBRADO);
                }
                lblCampo.setForeground(UIManager.getColor("Table.foreground"));
            }
            /*if (vDivergencia.get(row) instanceof DivergenciaVO) {
                DivergenciaVO oDivergencia = vDivergencia.get(row);
                if (column == 0) {
                    lblCampo.setHorizontalAlignment(SwingConstants.CENTER);
                    if (oDivergencia.tipo == TipoDivergencia.ERRO.getId()) {
                        lblCampo.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/erro.png")));
                    } else if (oDivergencia.tipo == TipoDivergencia.ALERTA.getId()) {
                        lblCampo.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/alerta.png")));
                    }
                }
            }*/
            return lblCampo;
        }
    }
}
