package vrimplantacao.gui.interfaces.MapeamentoVendasGetWay;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrimplantacao.gui.interfaces.rfd.ItensNaoExistentesController;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa.TipoMapa;
import vrimplantacao.gui.interfaces.rfd.ProdutoVR;

public class ItensNaoExistenteGetWayGUI extends VRInternalFrame {
    
    private static ItensNaoExistenteGetWayGUI instance;
    private ItensNaoExistentesController controller;    

    public ItensNaoExistenteGetWayGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();
        
        List<VRColumnTable> vColunaLoclizados = new ArrayList();
        vColunaLoclizados.add(new VRColumnTable("ID", true, SwingConstants.LEFT, false, null));
        vColunaLoclizados.add(new VRColumnTable("EAN", true, SwingConstants.LEFT, false, null));
        vColunaLoclizados.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, false, null));      
        vColunaLoclizados.add(new VRColumnTable("Reduzida", true, SwingConstants.LEFT, false, null));      
        grdLocalizados.configurarColuna(vColunaLoclizados, this, "Mapa", "");
        
        List<VRColumnTable> vColunaMapa = new ArrayList();
        vColunaMapa.add(new VRColumnTable("Cód. Prod.", true, SwingConstants.LEFT, false, null));
        vColunaMapa.add(new VRColumnTable("Descrição", true, SwingConstants.LEFT, false, null));
        vColunaMapa.add(new VRColumnTable("Cód. Atual", true, SwingConstants.LEFT, false, null));      
        grdMapear.configurarColuna(vColunaMapa, this, "Mapa", "");

        centralizarForm();
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();            
            if (instance == null || instance.isClosed()) {
                instance = new ItensNaoExistenteGetWayGUI(i_mdiFrame);
                instance.controller = new ItensNaoExistentesController();
                instance.carregarMapa();
            }

            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRLabel1 = new vrframework.bean.label.VRLabel();
        txtBusca = new javax.swing.JTextField();
        grdLocalizados = new vrframework.bean.tableEx.VRTableEx();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        btnAtualizarListagem = new vrframework.bean.button.VRButton();
        grdMapear = new vrframework.bean.tableEx.VRTableEx();
        btnCriarProduto = new vrframework.bean.button.VRButton();

        setTitle("Divergências de Exportação");
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

        vRLabel1.setText("Descrição, código ou EAN");

        txtBusca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscaKeyReleased(evt);
            }
        });

        vRLabel2.setText("Produtos a ser mapeados");

        btnAtualizarListagem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnAtualizarListagem.setText("Atualizar Listagem");
        btnAtualizarListagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarListagemActionPerformed(evt);
            }
        });

        btnCriarProduto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        btnCriarProduto.setText("Criar produto (F1)");
        btnCriarProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCriarProdutoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(grdMapear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(grdLocalizados, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAtualizarListagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtBusca)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCriarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBusca, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCriarProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(grdLocalizados, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(grdMapear, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAtualizarListagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onClose(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_onClose
        instance = null;
    }//GEN-LAST:event_onClose

    private void txtBuscaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscaKeyReleased
        try {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_UP: {
                    evt.consume();
                    up();
                }; break;
                case KeyEvent.VK_DOWN: {
                    evt.consume();
                    down();
                }; break;
                case KeyEvent.VK_ENTER: {
                    evt.consume();
                    selecionarProduto();
                }; break;
                case KeyEvent.VK_F1:  {
                    evt.consume();
                    criarItem();
                }break;
                case KeyEvent.VK_LEFT: {}; break;
                case KeyEvent.VK_RIGHT: {}; break;
                default: {
                    buscarProdutos();
                    evt.consume();
                }
            }
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Erro");
        }
    }//GEN-LAST:event_txtBuscaKeyReleased

    private void btnAtualizarListagemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarListagemActionPerformed
        try {
            this.setWaitCursor();
            
            carregarMapa();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnAtualizarListagemActionPerformed

    private void btnCriarProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCriarProdutoActionPerformed
        try {
            criarItem();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro");
        }
    }//GEN-LAST:event_btnCriarProdutoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnAtualizarListagem;
    private vrframework.bean.button.VRButton btnCriarProduto;
    private vrframework.bean.tableEx.VRTableEx grdLocalizados;
    private vrframework.bean.tableEx.VRTableEx grdMapear;
    private javax.swing.JTextField txtBusca;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    // End of variables declaration//GEN-END:variables

    private void up() throws Exception {
        System.out.println("UP");
        int index = controller.priorLocalizado();
        if (index > -1) {
            grdLocalizados.setLinhaSelecionada(index);
        }
    }

    private void down() throws Exception {
        System.out.println("DOWN");
        int index = controller.nextLocalizado();
        if (index > -1) {
            grdLocalizados.setLinhaSelecionada(index);
        }
    }

    private void selecionarProduto() throws Exception {
        controller.selecionarProduto(grdMapear.getLinhaSelecionada());
        updateMapaGrid();
        txtBusca.selectAll();
    }

    private void buscarProdutos() throws Exception {        
        controller.atualizarLocalizados(txtBusca.getText());        
        updateLocalizadosGrid();
    }

    private void updateLocalizadosGrid() throws Exception {
        Object[][] dados = new Object[controller.getLocalizados().size()][4];
        int i = 0;
        for (ProdutoVR vo: controller.getLocalizados()) {
            dados[i][0] = vo.getId();
            dados[i][1] = vo.getEan();
            dados[i][2] = vo.getDescricaoCompleta();
            dados[i][3] = vo.getDescricaoReduzida();
            i++;
        }       
        
        grdLocalizados.setRowHeight(20);
        grdLocalizados.setModel(dados);
        if (dados.length > 0) {
            grdLocalizados.setLinhaSelecionada(0);
        }
    }

    private void carregarMapa() throws Exception {
        controller.carregarMapa(false, TipoMapa.EAN);        
        updateMapaGrid();
    }

    private void updateMapaGrid() throws Exception {
        Object[][] dados = new Object[controller.getMapeados().size()][3];
        int i = 0;
        for (ProdutoMapa vo: controller.getMapeados()) {
            dados[i][0] = vo.getCodrfd();
            dados[i][1] = vo.getDescricao();
            dados[i][2] = vo.getCodigoAtual();
            i++;
        }       
        
        grdMapear.setRowHeight(20);
        grdMapear.setModel(dados);
        if (dados.length > 0) {
            grdMapear.setLinhaSelecionada(0);
        }
    }
    
    private void criarItem() throws Exception {
        controller.criarProduto(grdMapear.getLinhaSelecionada());        
        updateMapaGrid();
        txtBusca.selectAll();
    }

}
