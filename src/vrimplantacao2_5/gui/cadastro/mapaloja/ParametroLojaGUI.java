package vrimplantacao2_5.gui.cadastro.mapaloja;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import vr.view.components.panel.VRPanel;
import vrframework.bean.table.VRColumnTable;

/**
 *
 * @author guilhermegomes
 */
public class ParametroLojaGUI extends VRPanel {

    /**
     * Creates new form ParametroLoja
     * @throws java.lang.Exception
     */
    public ParametroLojaGUI() throws Exception {
        initComponents();
        
        setConfiguracao();
    }
    
    private void setConfiguracao() throws Exception {
        configurarColuna();
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Parâmetro", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Valor", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Observação", true, SwingConstants.LEFT, false, null));

        tblParametro.configurarColuna(column, this, "Parametro", "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnSalvar = new vrframework.bean.button.VRButton();
        tblParametro = new vrframework.bean.tableEx.VRTableEx();

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tblParametro, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tblParametro, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.tableEx.VRTableEx tblParametro;
    // End of variables declaration//GEN-END:variables
}
