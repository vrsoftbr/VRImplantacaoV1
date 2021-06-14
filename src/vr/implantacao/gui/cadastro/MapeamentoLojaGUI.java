package vr.implantacao.gui.cadastro;

import vr.view.components.panel.VRPanel;

/**
 *
 * @author guilhermegomes
 */
public class MapeamentoLojaGUI extends VRPanel {

    /**
     * Creates new form MapeamentoLojaGUI
     */
    public MapeamentoLojaGUI() {
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chkMatriz = new vrframework.bean.checkBox.VRCheckBox();
        lblLojaOrigem = new vrframework.bean.label.VRLabel();
        cboLojaOrigem = new vrframework.bean.comboBox.VRComboBox();
        lblLojaVR = new vrframework.bean.label.VRLabel();
        cboLojaVR = new vrframework.bean.comboBox.VRComboBox();
        chkEncerrada = new vrframework.bean.checkBox.VRCheckBox();
        btnSalvar = new vrframework.bean.button.VRButton();
        btnParametro = new vrframework.bean.button.VRButton();

        org.openide.awt.Mnemonics.setLocalizedText(chkMatriz, "Loja Matriz");
        chkMatriz.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, "Loja Origem");

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaVR, "Loja VR");

        org.openide.awt.Mnemonics.setLocalizedText(chkEncerrada, "Concluída / Encerrada");
        chkEncerrada.setEnabled(false);

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");

        btnParametro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/parametrizar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnParametro, "Parâmetro Loja");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(chkMatriz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkEncerrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkMatriz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkEncerrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnParametro;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.comboBox.VRComboBox cboLojaOrigem;
    private vrframework.bean.comboBox.VRComboBox cboLojaVR;
    private vrframework.bean.checkBox.VRCheckBox chkEncerrada;
    private vrframework.bean.checkBox.VRCheckBox chkMatriz;
    private vrframework.bean.label.VRLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblLojaVR;
    // End of variables declaration//GEN-END:variables
}
