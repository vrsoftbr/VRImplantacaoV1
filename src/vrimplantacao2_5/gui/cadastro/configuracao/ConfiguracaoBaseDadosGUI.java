package vrimplantacao2_5.gui.cadastro.configuracao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrframework.remote.ItemComboVO;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;
import vrimplantacao2_5.controller.cadastro.configuracao.MapaLojaController;
import vrimplantacao2_5.controller.cadastro.configuracao.ConfiguracaoBaseDadosController;
import vrimplantacao2_5.controller.migracao.MigracaoSistemasController;
import vrimplantacao2_5.service.cadastro.configuracao.ConfiguracaoPanel;
import vrimplantacao2_5.gui.cadastro.mapaloja.MapaLojaGUI;
import vrimplantacao2_5.gui.componente.conexao.ConexaoEvent;
import vrimplantacao2_5.gui.selecaoloja.SelecaoLojaGUI;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosGUI extends VRInternalFrame {

    public VRMdiFrame parentFrame = null;
    public static ConsultaConfiguracaoBaseDadosGUI consultaConfiguracaoBancoDadosGUI = null;
    private static ConfiguracaoBaseDadosGUI configuracaoBaseDadosGUI = null;
    private static MapaLojaGUI mapaLojaGUI = null;
    private SelecaoLojaGUI migracaoGUI = null;

    private ConfiguracaoBaseDadosController controller = null;
    private MapaLojaController mapaController = null;

    private ConfiguracaoPanel painelDeConexaoDinamico;
    private ConfiguracaoBaseDadosVO configuracaoBancoVO = null;

    private MigracaoSistemasController migracaoSistemasController = null;
    /**
     * Creates new form ConfiguracaoPrincipalGUI
     * @param menuGUI
     * @throws java.lang.Exception
     */
    public ConfiguracaoBaseDadosGUI(VRMdiFrame menuGUI) throws Exception {
        super(menuGUI);
        initComponents();
        
        this.parentFrame = menuGUI;
        setConfiguracao();
        
        migracaoSistemasController = new MigracaoSistemasController();
    }
    
    private void setConfiguracao() throws Exception {
        centralizarForm();
        setTitle("Configuração de Base de Dados");

        controller = new ConfiguracaoBaseDadosController();
        configuracaoBancoVO = new ConfiguracaoBaseDadosVO();
        mapaController = new MapaLojaController(this);

        getSistema();
        configurarColuna();
    }

    private void configurarColuna() throws Exception {
        List<VRColumnTable> column = new ArrayList();

        column.add(new VRColumnTable("Matriz", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Código Loja Origem", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Descrição Loja Origem", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Código Loja VR", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Descrição Loja VR", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Cadastro", true, SwingConstants.LEFT, false, null));
        column.add(new VRColumnTable("Situação", true, SwingConstants.LEFT, false, null));

        tblLoja.configurarColuna(column, this, "Mapa", "");
    }

    private void getSistema() {
        cboSistema.setModel(new DefaultComboBoxModel());

        List<SistemaVO> sistemas = controller.getSistema();

        if (sistemas == null) {
            return;
        }

        for (SistemaVO vo : sistemas) {
            cboSistema.addItem(new ItemComboVO(vo.getId(), vo.getNome()));
        }
    }

    private void getBancoDadosPorSistema() {
        cboBD.setModel(new DefaultComboBoxModel());

        List<BancoDadosVO> bancosPorSistema = controller.getBancoDadosPorSistema(cboSistema.getId());

        if (bancosPorSistema == null) {
            return;
        }

        for (BancoDadosVO bdVO : bancosPorSistema) {
            cboBD.addItem(new ItemComboVO(bdVO.getId(), bdVO.getNome()));
        }

        desabilitarBotao();
    }
    
    private void selecionarBancoDados() throws Exception {        
        migracaoSistemasController.setIdBancoDados(cboBD.getId());
    }
    
    private void selecionarSistema() throws Exception {
        migracaoSistemasController.setIdSistema(cboSistema.getId());
    }
    
    private void exibiPainelConexao() {
        tabConexao.removeAll();
        desabilitarBotao();

        painelDeConexaoDinamico = controller.exibiPainelConexao(cboSistema.getId(), cboBD.getId());

        if (painelDeConexaoDinamico == null) {
            try {
                Util.exibirMensagem("Nenhum painel configurado para o banco de dados "
                        + EBancoDados.getById(cboBD.getId()) + "!",
                        getTitle());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            tabConexao.add("Paramêtros de Conexão", (JPanel) painelDeConexaoDinamico);
        }

        if (configuracaoBancoVO.getId() > 0) {
            painelDeConexaoDinamico.setDadosConexao(configuracaoBancoVO.getHost(),
                    configuracaoBancoVO.getSchema(),
                    configuracaoBancoVO.getPorta(),
                    configuracaoBancoVO.getUsuario(),
                    configuracaoBancoVO.getSenha());
        }

        habilitarBotaoSalvar();
    }

    private void habilitarBotaoSalvar() {
        painelDeConexaoDinamico.setOnConectar(new ConexaoEvent() {
            @Override
            public void executar() throws Exception {
                btnSalvar.setEnabled(true);

                configuracaoBancoVO.setHost(painelDeConexaoDinamico.getHost());
                configuracaoBancoVO.setUsuario(painelDeConexaoDinamico.getUsuario());
                configuracaoBancoVO.setSenha(painelDeConexaoDinamico.getSenha());
                configuracaoBancoVO.setPorta(Integer.valueOf(painelDeConexaoDinamico.getPorta()));
                configuracaoBancoVO.setSchema(painelDeConexaoDinamico.getSchema());
            }
        });
    }

    private void desabilitarBotao() {
        btnSalvar.setEnabled(false);
        btnMapear.setEnabled(false);
    }

    @Override
    public void salvar() throws Exception {
        if (txtNomeConexao.getText().isEmpty()) {
            try {
                Util.exibirMensagem("Campo Nome da Conexão obrigatório!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }

            return;
        }

        BancoDadosVO bancoDadosVO = new BancoDadosVO();
        bancoDadosVO.setId(cboBD.getId());

        SistemaVO sistemaVO = new SistemaVO();
        sistemaVO.setId(cboSistema.getId());

        configuracaoBancoVO.setDescricao(txtNomeConexao.getText());
        configuracaoBancoVO.setBancoDados(bancoDadosVO);
        configuracaoBancoVO.setSistema(sistemaVO);
        configuracaoBancoVO.setComplemento(txtComplemento.getText());

        controller.salvar(configuracaoBancoVO);

        if (configuracaoBancoVO.getId() != 0) {

            consultaConfiguracaoBancoDadosGUI.controller.consultar();
            btnMapear.setEnabled(true);

            try {
                Util.exibirMensagem("Conexão salva com sucesso!", getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }
    }

    public void consultaConfiguracaoLoja() throws Exception {
        List<ConfiguracaoBancoLojaVO> lojas = mapaController.getLojaMapeada();

        Object[][] dados = new Object[lojas.size()][7];

        int i = 0;
        for (ConfiguracaoBancoLojaVO lj : lojas) {
            dados[i][0] = lj.isLojaMatriz() ? "LOJA MIX PRINCIPAL" : "LOJA NORMAL";
            dados[i][1] = lj.getIdLojaOrigem();
            dados[i][2] = lj.getDescricaoLojaOrigem();
            dados[i][3] = lj.getIdLojaVR();
            dados[i][4] = lj.getDescricaoVR();
            dados[i][5] = Util.formatDataGUI(lj.getDataCadastro());
            dados[i][6] = ESituacaoMigracao.getById(lj.getSituacaoMigracao().getId());

            i++;
        }

        tblLoja.setRowHeight(20);
        tblLoja.setModel(dados);

        if (lojas.size() > 0) {
            btnExcluirLoja.setEnabled(true);
            btnProximo.setEnabled(true);
        }
    }

    @Override
    public void excluir() {
        try {
            if (tblLoja.getSelectedRow() == -1) {
                Util.exibirMensagem("Selecione uma loja para ser excluída!", getTitle());
                return;
            }

            mapaController.excluirLojaMapeada(mapaController.
                    getLojaMapeada().
                        get(tblLoja.
                            getLinhaSelecionada()));

            mapaController.consultaLojaMapeada(configuracaoBancoVO.getId());
            consultaConfiguracaoLoja();

            Util.exibirMensagem("Loja excluída com sucesso!", getTitle());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void editar(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {
        this.configuracaoBancoVO = configuracaoBancoVO;

        txtNomeConexao.setText(configuracaoBancoVO.getDescricao());
        cboSistema.setDescricao(configuracaoBancoVO.getSistema().getNome());
        cboBD.setDescricao(configuracaoBancoVO.getBancoDados().getNome());

        exibiPainelConexao();
        mapaController.consultaLojaMapeada(configuracaoBancoVO.getId());

        if (mapaController.getLojaMapeada().size() > 0) {
            btnProximo.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNomeCon = new vrframework.bean.label.VRLabel();
        txtNomeConexao = new vrframework.bean.textField.VRTextField();
        lblSistema = new vrframework.bean.label.VRLabel();
        cboSistema = new vrframework.bean.comboBox.VRComboBox();
        lblBD = new vrframework.bean.label.VRLabel();
        cboBD = new vrframework.bean.comboBox.VRComboBox();
        pnlLoja = new vrframework.bean.panel.VRPanel();
        btnMapear = new vrframework.bean.button.VRButton();
        tblLoja = new vrframework.bean.tableEx.VRTableEx();
        btnExcluirLoja = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();
        tabConexao = new vrframework.bean.tabbedPane.VRTabbedPane();
        btnDica = new vrframework.bean.button.VRButton();
        btnProximo = new vrframework.bean.button.VRButton();
        lblComplemento = new vrframework.bean.label.VRLabel();
        txtComplemento = new vrframework.bean.textField.VRTextField();

        org.openide.awt.Mnemonics.setLocalizedText(lblNomeCon, "Nome da Conexão");

        txtNomeConexao.setObrigatorio(true);

        org.openide.awt.Mnemonics.setLocalizedText(lblSistema, "Sistema");

        cboSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSistemaActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblBD, "Banco de Dados");

        cboBD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBDActionPerformed(evt);
            }
        });

        pnlLoja.setBorder(javax.swing.BorderFactory.createTitledBorder("Loja"));

        btnMapear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/configurar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnMapear, "Mapear Loja");
        btnMapear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMapearActionPerformed(evt);
            }
        });

        btnExcluirLoja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/excluir.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnExcluirLoja, "Excluir Loja");
        btnExcluirLoja.setEnabled(false);
        btnExcluirLoja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirLojaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLojaLayout = new javax.swing.GroupLayout(pnlLoja);
        pnlLoja.setLayout(pnlLojaLayout);
        pnlLojaLayout.setHorizontalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tblLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlLojaLayout.createSequentialGroup()
                        .addComponent(btnMapear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlLojaLayout.setVerticalGroup(
            pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLojaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLojaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMapear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluirLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tblLoja, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnDica.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/ignorar.png"))); // NOI18N
        btnDica.setToolTipText("Dica!");
        btnDica.setBorderPainted(false);
        btnDica.setContentAreaFilled(false);
        btnDica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDicaActionPerformed(evt);
            }
        });

        btnProximo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/proximo.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnProximo, "Pŕoximo");
        btnProximo.setEnabled(false);
        btnProximo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProximoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblComplemento, "Complemento");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlLoja, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboSistema, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboBD, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtNomeConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblNomeCon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btnDica, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabConexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnProximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNomeCon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtNomeConexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnDica, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnProximo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabConexao.getAccessibleContext().setAccessibleName("Painel de Conexão");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSistemaActionPerformed
        getBancoDadosPorSistema();
    }//GEN-LAST:event_cboSistemaActionPerformed

    private void cboBDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBDActionPerformed
        
        try {
            exibiPainelConexao();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }//GEN-LAST:event_cboBDActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
            salvar();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnDicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDicaActionPerformed
        try {
            Util.exibirMensagem("Informe uma descrição para a conexão\n para uma melhor identificação!\n"
                    + "Exemplo: CONEXÃO DA LOJA MATRIZ - SERVIDOR 0.0.0.0", getTitle());
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        }
    }//GEN-LAST:event_btnDicaActionPerformed

    private void btnMapearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapearActionPerformed
        
        try {
            selecionarBancoDados();
            selecionarSistema();
            exibirMapaLoja();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        }
        
    }//GEN-LAST:event_btnMapearActionPerformed

    private void btnExcluirLojaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirLojaActionPerformed
        excluir();
    }//GEN-LAST:event_btnExcluirLojaActionPerformed

    private void btnProximoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProximoActionPerformed
        exibirSelecaoLoja();
    }//GEN-LAST:event_btnProximoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnDica;
    private vrframework.bean.button.VRButton btnExcluirLoja;
    private vrframework.bean.button.VRButton btnMapear;
    private vrframework.bean.button.VRButton btnProximo;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.comboBox.VRComboBox cboBD;
    private vrframework.bean.comboBox.VRComboBox cboSistema;
    private vrframework.bean.label.VRLabel lblBD;
    private vrframework.bean.label.VRLabel lblComplemento;
    private vrframework.bean.label.VRLabel lblNomeCon;
    private vrframework.bean.label.VRLabel lblSistema;
    private vrframework.bean.panel.VRPanel pnlLoja;
    private vrframework.bean.tabbedPane.VRTabbedPane tabConexao;
    private vrframework.bean.tableEx.VRTableEx tblLoja;
    private vrframework.bean.textField.VRTextField txtComplemento;
    private vrframework.bean.textField.VRTextField txtNomeConexao;
    // End of variables declaration//GEN-END:variables

    public void exibirMapaLoja() {
        try {
            if (mapaLojaGUI == null || !mapaLojaGUI.isActive()) {
                mapaLojaGUI = new MapaLojaGUI();
            }

            mapaLojaGUI.setMapaLojaController(mapaController);
            mapaLojaGUI.configuracaoBaseDadosGUI = this;
            mapaLojaGUI.setConfiguracaoConexao(configuracaoBancoVO);
            
            mapaLojaGUI.setConfiguracao();
            mapaLojaGUI.setVisible(true);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Mapeamento de Loja");
        }
    }

    public static void exibir(VRMdiFrame menuGUI) {
        try {
            menuGUI.setWaitCursor();

            if (configuracaoBaseDadosGUI == null || configuracaoBaseDadosGUI.isClosed()) {
                configuracaoBaseDadosGUI = new ConfiguracaoBaseDadosGUI(menuGUI);
            }

            configuracaoBaseDadosGUI.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta Configuração de Base de Dados");
        } finally {
            menuGUI.setDefaultCursor();
        }
    }
    
    public void exibirSelecaoLoja() {
        try {
            if (migracaoGUI == null || !migracaoGUI.isActive()) {
                migracaoGUI = new SelecaoLojaGUI();
            }

            migracaoGUI.parentFrame = this.parentFrame;
            migracaoGUI.setVisible(true);
            
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Seleção de Loja");
        }
    }
}
