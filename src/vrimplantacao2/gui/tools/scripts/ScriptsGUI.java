package vrimplantacao2.gui.tools.scripts;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2.dao.interfaces.PlanilhaDAO;
import vrimplantacao2.parametro.Parametros;

/**
 *
 * @author Leandro
 */
public class ScriptsGUI extends VRInternalFrame {
    
    private final Parametros parametros = Parametros.get();
    private final PlanilhaDAO dao = new PlanilhaDAO();
        
    private ScriptsGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        
        initComponents();
        
        inicializarTreeView();

        centralizarForm();
        this.setMaximum(false);
    }
    
    private static ScriptsGUI instance;

    public static void Exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new ScriptsGUI(i_mdiFrame);
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

        pnlScriptTree = new javax.swing.JScrollPane();
        treeScripts = new javax.swing.JTree();
        tabsScript = new vrframework.bean.tabbedPane.VRTabbedPane();
        pnlScript = new vrframework.bean.panel.VRPanel();
        jButton3 = new javax.swing.JButton();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        pnlParametros = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtScript = new javax.swing.JTextArea();
        pnlResultados = new vrframework.bean.panel.VRPanel();

        setResizable(true);
        setTitle(org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.title")); // NOI18N
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

        treeScripts.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.treeScripts.border.title"))); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Scripts");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Importação");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("SPED");
        javax.swing.tree.DefaultMutableTreeNode treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Gerar sped.produtoalteracao(V1.0)");
        treeNode3.add(treeNode4);
        treeNode4 = new javax.swing.tree.DefaultMutableTreeNode("Gerar sped.produtoalteracao(V2.0)");
        treeNode3.add(treeNode4);
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeScripts.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        pnlScriptTree.setViewportView(treeScripts);

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.jButton3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel1, org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.vRLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.vRLabel2.text")); // NOI18N

        txtScript.setEditable(false);
        txtScript.setColumns(20);
        txtScript.setLineWrap(true);
        txtScript.setRows(5);
        jScrollPane2.setViewportView(txtScript);

        javax.swing.GroupLayout pnlScriptLayout = new javax.swing.GroupLayout(pnlScript);
        pnlScript.setLayout(pnlScriptLayout);
        pnlScriptLayout.setHorizontalGroup(
            pnlScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScriptLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addComponent(pnlParametros, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlScriptLayout.createSequentialGroup()
                        .addGap(0, 253, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlScriptLayout.createSequentialGroup()
                        .addGroup(pnlScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlScriptLayout.setVerticalGroup(
            pnlScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlScriptLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlParametros, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7))
        );

        tabsScript.addTab(org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.pnlScript.TabConstraints.tabTitle"), pnlScript); // NOI18N

        javax.swing.GroupLayout pnlResultadosLayout = new javax.swing.GroupLayout(pnlResultados);
        pnlResultados.setLayout(pnlResultadosLayout);
        pnlResultadosLayout.setHorizontalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );
        pnlResultadosLayout.setVerticalGroup(
            pnlResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 477, Short.MAX_VALUE)
        );

        tabsScript.addTab(org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.pnlResultados.TabConstraints.tabTitle"), pnlResultados); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlScriptTree, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabsScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlScriptTree)
                    .addComponent(tabsScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ScriptsGUI.class, "ScriptsGUI.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane pnlParametros;
    private vrframework.bean.panel.VRPanel pnlResultados;
    private vrframework.bean.panel.VRPanel pnlScript;
    private javax.swing.JScrollPane pnlScriptTree;
    private vrframework.bean.tabbedPane.VRTabbedPane tabsScript;
    private javax.swing.JTree treeScripts;
    private javax.swing.JTextArea txtScript;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    // End of variables declaration//GEN-END:variables

    private void inicializarTreeView() {
        treeScripts.setModel(new DefaultTreeModel(new ScriptRoot()));
        treeScripts.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeScripts.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                ScriptNode node = (ScriptNode)
                       treeScripts.getLastSelectedPathComponent();
                
                if (node == null) {
                    return;
                }
                
                txtScript.setText(node.getInstrucoes());
            }
        });
    }
    
}
