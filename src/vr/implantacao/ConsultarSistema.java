package vr.implantacao;

import vr.view.components.panel.VRPanel;

/**
 *
 * @author leandro
 */
public class ConsultarSistema extends VRPanel {

    /**
     * Creates new form ConsultarMapaDeImportacao
     */
    public ConsultarSistema() {
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

        jScrollPane1 = new javax.swing.JScrollPane();
        vRTable1 = new vr.view.components.table.VRTable();
        btnIncluir3 = new javax.swing.JButton();
        vRTextField1 = new vr.view.components.textfield.VRTextField();
        vRTitle31 = new vr.view.components.label.VRTitle3();

        vRTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "SoftwareHouse", "Sistema", "Banco", "Observações"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(vRTable1);

        org.openide.awt.Mnemonics.setLocalizedText(btnIncluir3, "Atualizar");
        btnIncluir3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIncluir3OnClick(evt);
            }
        });

        vRTextField1.setText("SysPDV");

        org.openide.awt.Mnemonics.setLocalizedText(vRTitle31, "Consulta de Sistemas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 250, Short.MAX_VALUE)
                        .addComponent(btnIncluir3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                    .addComponent(vRTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vRTitle31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRTitle31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(vRTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnIncluir3)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnIncluir3OnClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIncluir3OnClick
        // TODO add your handling code here:
    }//GEN-LAST:event_btnIncluir3OnClick


    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton btnIncluir3;
    javax.swing.JScrollPane jScrollPane1;
    vr.view.components.table.VRTable vRTable1;
    vr.view.components.textfield.VRTextField vRTextField1;
    vr.view.components.label.VRTitle3 vRTitle31;
    // End of variables declaration//GEN-END:variables
}
