package vrimplantacao2.gui.component.importpanel.financeiro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import vrimplantacao2.gui.component.mapatiporecebiveis.MapaTipoRecebiveisView;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao2.dao.cadastro.financeiro.recebercaixa.OpcaoRecebimentoCaixa;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.gui.component.CustomFrame;
import vrimplantacao2.gui.component.mapatiporecebiveis.FinanceiroProvider;

/**
 *
 * @author Leandro
 */
public class FinanceiroImportView extends JPanel {
    
    private FinanceiroProvider provider;
    private Set<OpcaoRecebimentoCaixa> opt = new HashSet<>();
    /**
     * Creates new form FinanceiroImportPanel
     */
    public FinanceiroImportView() {
        initComponents();
        setEnableds();
    }
    
    public void setOpcoes(OpcaoRecebimentoCaixa... opcoes) {
        this.opt = new HashSet<>(Arrays.asList(opcoes));
        setEnableds();        
    }
    
    private void setEnableds() {
        boolean todos = opt.contains(OpcaoRecebimentoCaixa.TODOS);
        chkDataEmissao.setEnabled((todos || opt.contains(OpcaoRecebimentoCaixa.DATA_EMISSAO)) && chkHabilitar.isSelected());
        chkDataVencimento.setEnabled((todos || opt.contains(OpcaoRecebimentoCaixa.DATA_VENCIMENTO)) && chkHabilitar.isSelected());
        chkRecebimentoCaixa.setEnabled((todos || opt.contains(OpcaoRecebimentoCaixa.NOVOS)) && chkHabilitar.isSelected());
        chkObservacao.setEnabled((todos || opt.contains(OpcaoRecebimentoCaixa.OBSERVACAO)) && chkHabilitar.isSelected());
        chkTipoRecebivel.setEnabled((todos || opt.contains(OpcaoRecebimentoCaixa.TIPO_RECEBIVEL)) && chkHabilitar.isSelected());
        chkValor.setEnabled((todos || opt.contains(OpcaoRecebimentoCaixa.VALOR)) && chkHabilitar.isSelected());
        btnMapeador.setEnabled(chkHabilitar.isSelected());
    }
    
    public void executar(Importador importador) throws Exception {
        if (provider != null) {
            if (chkHabilitar.isSelected()) {
                List<OpcaoRecebimentoCaixa> opcoes = new ArrayList<>();
                if (chkRecebimentoCaixa.isSelected()) {
                    opcoes.add(OpcaoRecebimentoCaixa.NOVOS);
                }
                if (chkTipoRecebivel.isSelected()) {
                    opcoes.add(OpcaoRecebimentoCaixa.TIPO_RECEBIVEL);
                }
                if (chkDataEmissao.isSelected()) {
                    opcoes.add(OpcaoRecebimentoCaixa.DATA_EMISSAO);
                }
                if (chkDataVencimento.isSelected()) {
                    opcoes.add(OpcaoRecebimentoCaixa.DATA_VENCIMENTO);
                }
                if (chkObservacao.isSelected()) {
                    opcoes.add(OpcaoRecebimentoCaixa.OBSERVACAO);
                }
                if (chkValor.isSelected()) {
                    opcoes.add(OpcaoRecebimentoCaixa.VALOR);
                }
                importador.importarRecebimentoCaixa(opcoes.toArray(new OpcaoRecebimentoCaixa[] {}));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFinanceiro = new javax.swing.JPanel();
        chkHabilitar = new javax.swing.JCheckBox();
        group1 = new javax.swing.JPanel();
        btnMapeador = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        chkRecebimentoCaixa = new javax.swing.JCheckBox();
        chkTipoRecebivel = new javax.swing.JCheckBox();
        chkDataEmissao = new javax.swing.JCheckBox();
        chkDataVencimento = new javax.swing.JCheckBox();
        chkObservacao = new javax.swing.JCheckBox();
        chkValor = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(chkHabilitar, "Importar recebíveis");
        chkHabilitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkHabilitarActionPerformed(evt);
            }
        });

        group1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        group1.setOpaque(false);

        org.openide.awt.Mnemonics.setLocalizedText(btnMapeador, "Mapear Tipo Recebível");
        btnMapeador.setEnabled(false);
        btnMapeador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapeadorActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

        org.openide.awt.Mnemonics.setLocalizedText(chkRecebimentoCaixa, "Recebimento Caixa (Novos)");
        chkRecebimentoCaixa.setEnabled(false);
        jPanel1.add(chkRecebimentoCaixa);

        org.openide.awt.Mnemonics.setLocalizedText(chkTipoRecebivel, "Tipo Recebível");
        chkTipoRecebivel.setEnabled(false);
        jPanel1.add(chkTipoRecebivel);

        org.openide.awt.Mnemonics.setLocalizedText(chkDataEmissao, "Data Emissão");
        chkDataEmissao.setEnabled(false);
        jPanel1.add(chkDataEmissao);

        org.openide.awt.Mnemonics.setLocalizedText(chkDataVencimento, "Data Vencimento");
        chkDataVencimento.setEnabled(false);
        jPanel1.add(chkDataVencimento);

        org.openide.awt.Mnemonics.setLocalizedText(chkObservacao, "Observações");
        chkObservacao.setEnabled(false);
        jPanel1.add(chkObservacao);

        org.openide.awt.Mnemonics.setLocalizedText(chkValor, "Valor");
        chkValor.setEnabled(false);
        jPanel1.add(chkValor);

        javax.swing.GroupLayout group1Layout = new javax.swing.GroupLayout(group1);
        group1.setLayout(group1Layout);
        group1Layout.setHorizontalGroup(
            group1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(group1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(group1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(group1Layout.createSequentialGroup()
                        .addComponent(btnMapeador, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 5, Short.MAX_VALUE)))
                .addContainerGap())
        );
        group1Layout.setVerticalGroup(
            group1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(group1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnMapeador, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlFinanceiroLayout = new javax.swing.GroupLayout(pnlFinanceiro);
        pnlFinanceiro.setLayout(pnlFinanceiroLayout);
        pnlFinanceiroLayout.setHorizontalGroup(
            pnlFinanceiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFinanceiroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFinanceiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(group1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkHabilitar))
                .addContainerGap())
        );
        pnlFinanceiroLayout.setVerticalGroup(
            pnlFinanceiroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFinanceiroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkHabilitar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(group1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFinanceiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlFinanceiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void chkHabilitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkHabilitarActionPerformed
        setEnableds();
    }//GEN-LAST:event_chkHabilitarActionPerformed

    private void btnMapeadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapeadorActionPerformed
        try {
            MapaTipoRecebiveisView view = new MapaTipoRecebiveisView();
            view.setProvider(this.provider);
            CustomFrame.exibir(null, 
                    "Mapa de Tipos de Recebíveis", 
                    true, 
                    view,
                    800,
                    600
            ).setVisible(true);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Util.exibirMensagemErro(ex, "Erro");
        }
    }//GEN-LAST:event_btnMapeadorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMapeador;
    private javax.swing.JCheckBox chkDataEmissao;
    private javax.swing.JCheckBox chkDataVencimento;
    private javax.swing.JCheckBox chkHabilitar;
    private javax.swing.JCheckBox chkObservacao;
    private javax.swing.JCheckBox chkRecebimentoCaixa;
    private javax.swing.JCheckBox chkTipoRecebivel;
    private javax.swing.JCheckBox chkValor;
    private javax.swing.JPanel group1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlFinanceiro;
    // End of variables declaration//GEN-END:variables

    public void setProvider(FinanceiroProvider provider) {
        this.provider = provider;
    }

    public FinanceiroProvider getProvider() {
        return provider;
    }
    
}
