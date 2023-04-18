package vrimplantacao2_5.gui.sistema;

import javax.swing.JPanel;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.Util;
import vrimplantacao2.gui.component.checks.ClientePanelGUI;
import vrimplantacao2_5.dao.sistema.ProviderGenericoDAO;
import vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel;
import vrimplantacao2_5.vo.enums.ESistema;

public class Generico2_5GUI extends VRInternalFrame {

    private static final String SISTEMA = ESistema.GENERICO.getNome();
    private static Generico2_5GUI instance;
    private final ProviderGenericoDAO dao = new ProviderGenericoDAO();

    public Generico2_5GUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.title = "Importação " + SISTEMA;

        pnlConn.setSistema(ESistema.GENERICO);
        pnlConn.getNomeConexao();
        //método para carregar conexao
        dao.setLojaOrigem(pnlConn.getLojaOrigem());
        
        geradorDePaineisClientes(pnlConn);
        geradorDePaineisFornecedores(pnlConn);
        geradorDePaineisProdutos(pnlConn);

        centralizarForm();

        this.setMaximum(false);
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new Generico2_5GUI(i_mdiFrame);
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

        try {
            pnlConn = new vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel();
        } catch (java.lang.Exception e1) {
            e1.printStackTrace();
        }
        painelDeAbas = new javax.swing.JTabbedPane();

        setTitle("DataByte");
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

        painelDeAbas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                painelDeAbasMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(painelDeAbas)
                    .addComponent(pnlConn, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(painelDeAbas, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void painelDeAbasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_painelDeAbasMouseClicked
        int aba = painelDeAbas.getSelectedIndex();
        if (aba == 0) {
            ClientePanelGUI painelClientePreferencial;
            try {
                painelClientePreferencial = new ClientePanelGUI();
                painelDeAbas.add(painelClientePreferencial);
                painelDeAbas.setTitleAt(0, "Cliente Preferencial");
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_painelDeAbasMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane painelDeAbas;
    private vrimplantacao2_5.gui.componente.conexao.configuracao.BaseDeDadosPanel pnlConn;
    // End of variables declaration//GEN-END:variables

    private void geradorDePaineisClientes(BaseDeDadosPanel pnlConn) throws Exception {
        ClientePanelGUI painelClientePreferencial = new ClientePanelGUI();
        painelClientePreferencial.setPnlConn(pnlConn);
        painelDeAbas.removeAll();
        painelDeAbas.add(painelClientePreferencial);
        painelDeAbas.setTitleAt(0, "Cliente Preferencial");
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(1, "Crédito Rotativo");
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(2, "Convenio Empresa");
    }

    private void geradorDePaineisFornecedores(BaseDeDadosPanel pnlConn) {
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(3, "Fornecedores");
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(4, "Contas a Pagar");
    }

    private void geradorDePaineisProdutos(BaseDeDadosPanel pnlConn) {
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(5, "Produtos");
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(6, "Eans");
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(7, "Produto Fornecedor");
        painelDeAbas.add(new JPanel());
        painelDeAbas.setTitleAt(8, "Vendas");
    }
}
