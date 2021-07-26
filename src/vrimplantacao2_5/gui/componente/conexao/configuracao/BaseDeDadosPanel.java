package vrimplantacao2_5.gui.componente.conexao.configuracao;

import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import vrframework.bean.panel.VRPanel;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.controller.componente.ComponenteConexaoController;
import vrimplantacao2_5.controller.selecaoloja.SelecaoLojaController;
import vrimplantacao2_5.gui.componente.conexao.ConexaoEvent;
import vrimplantacao2_5.gui.componente.conexao.DriverConexao;
import vrimplantacao2_5.service.cadastro.configuracao.ConfiguracaoPanel;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.enums.EBancoDados;
import vrimplantacao2_5.vo.enums.ESistema;

/**
 *
 * @author guilhermegomes
 */
public class BaseDeDadosPanel extends VRPanel implements ConfiguracaoPanel {

    private SelecaoLojaController controller = new SelecaoLojaController();;
    private List<ConfiguracaoBancoLojaVO> lojas = null;
    private List<ConfiguracaoBaseDadosVO> conexoes = null;
    private ConexaoEvent onConectar;
    private DriverConexao conexao;

    public ConfiguracaoBaseDadosVO cfgVO = null;
    private ConfiguracaoBancoLojaVO configuracaoLojaVO = null;
    private ESistema sistema;

    /**
     * Creates new form ConfiguracaoBaseDados
     *
     * @throws java.lang.Exception
     */
    public BaseDeDadosPanel() throws Exception {
        initComponents();
        desabilitarBotaoConectar();
        setDadosConexao("");
    }

    private void setConfiguracao() throws Exception {
        getNomeConexao();
    }
    
    public void setSistema(ESistema sistema) {
        this.sistema = sistema;
    }

    @Override
    public void setOnConectar(ConexaoEvent onConectar) {
        this.onConectar = onConectar;
    }

    public ConexaoEvent getOnConectar() {
        return onConectar;
    }

    public void getNomeConexao() throws Exception {
        cboConexao.setModel(new DefaultComboBoxModel());

        conexoes = controller.consultar(sistema.getId());
        
        for (ConfiguracaoBaseDadosVO configuracaoVO : conexoes) {
            String complemento = (configuracaoVO.getComplemento() != null && 
                            !configuracaoVO.getComplemento().isEmpty()) ?
                            " - COMPLEMENTO: " + configuracaoVO.getComplemento() : "";
            
            cboConexao.addItem(new ItemComboVO(configuracaoVO.getId(), 
                                configuracaoVO.getDescricao() + complemento));
        }

        if (conexoes.size() > 0) {
            habilitarBotaoConectar();
        }
        
        preencheCampoLojaVR();
    }

    private void getLojaMapeada() {
        cboOrigem.setModel(new DefaultComboBoxModel());

        lojas = controller.getLojaMapeada(cboConexao.getId());

        for (ConfiguracaoBancoLojaVO configuracaoLojaVO : lojas) {
            cboOrigem.addItem(new ItemComboVO(configuracaoLojaVO.getIdLojaOrigem(),
                    configuracaoLojaVO.getIdLojaOrigem() + " - "
                    + (configuracaoLojaVO.isLojaMatriz() ? "MATRIZ" : "FILIAL")));
        }
    }

    private void preencheCampoLojaVR() {
        configuracaoLojaVO = lojas.get(cboOrigem.getSelectedIndex());

        txtLojaVR.setText(configuracaoLojaVO.getIdLojaVR() + " - " + configuracaoLojaVO.getDescricaoVR());
    }

    private void desabilitarBotaoConectar() {
        btnConectar.setEnabled(false);
    }

    private void habilitarBotaoConectar() {
        btnConectar.setEnabled(true);
    }

    private void construirConexao() throws VRException, Exception {
        ComponenteConexaoController conexaoController = new ComponenteConexaoController();
        cfgVO = conexoes.get(cboConexao.getSelectedIndex());
        EBancoDados eBD = EBancoDados.getById(cfgVO.getBancoDados().getId());

        conexao = conexaoController.getConexao(eBD);

        validaInformacao(eBD);
        
        conexao.abrirConexao(cfgVO.getHost(), cfgVO.getPorta(), cfgVO.getSchema(), 
                                                cfgVO.getUsuario(), cfgVO.getSenha());

        btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));
        lblDados.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/disponivel_12.png")));

        setDadosConexao("SISTEMA " + cfgVO.getSistema().getNome() + " - BANCO " + cfgVO.getBancoDados().getNome());

        atualizarParametros();

        if (onConectar != null) {
            onConectar.executar();
        }
    }

    public void fecharConexao() throws Exception {
        if(conexao != null) {
            conexao.close();
        }
    }
    
    private void validaInformacao(EBancoDados eBD) throws VRException {
        if (cfgVO.getHost().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados " + eBD + "!");
        }
        if (cfgVO.getPorta() == 0) {
            throw new VRException("Favor informar a porta do banco de dados " + eBD + "!");
        }
        if (cfgVO.getSchema().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados " + eBD + "!");
        }
        if (cfgVO.getUsuario().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados " + eBD + "!");
        }
    }

    public void atualizarParametros() throws Exception {
        Parametros params = Parametros.get();

        final String SISTEMA = cfgVO.getSistema().getNome();
        
        params.put(cfgVO.getHost(), SISTEMA, "FIREBIRD", "HOST");
        params.put(cfgVO.getSchema(), SISTEMA, "FIREBIRD", "DATABASE");
        params.put(cfgVO.getPorta(), SISTEMA, "FIREBIRD", "PORTA");
        params.put(cfgVO.getUsuario(), SISTEMA, "FIREBIRD", "USUARIO");
        params.put(cfgVO.getSenha(), SISTEMA, "FIREBIRD", "SENHA");
        params.put(cfgVO.getSistema().getNome(), SISTEMA, "FIREBIRD", "SISTEMA");
        params.put(cfgVO.getBancoDados().getNome(), SISTEMA, "FIREBIRD", "ENGINE");
        params.salvar();
    }

    private void setDadosConexao(String texto) {
        lblDados.setText(texto);
    }

    @Override
    public void setDadosConexao(String host, String schema, int porta, String usuario, String senha) {
    }

    @Override
    public String getHost() {
        return cfgVO.getHost();
    }

    @Override
    public String getPorta() {
        return String.valueOf(cfgVO.getPorta());
    }

    @Override
    public String getSchema() {
        return cfgVO.getSchema();
    }

    @Override
    public String getUsuario() {
        return cfgVO.getUsuario();
    }

    @Override
    public String getSenha() {
        return cfgVO.getSenha();
    }
    
    public String getComplemento() {
        return cfgVO.getComplemento();
    }
    
    public String getLojaOrigem() {
        return configuracaoLojaVO.getIdLojaOrigem();
    }
    
    public int getLojaVR() {
        return configuracaoLojaVO.getIdLojaVR();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblConexao = new vrframework.bean.label.VRLabel();
        cboConexao = new vrframework.bean.comboBox.VRComboBox();
        lblLojaOrigem = new vrframework.bean.label.VRLabel();
        cboOrigem = new vrframework.bean.comboBox.VRComboBox();
        lblLojaVR = new vrframework.bean.label.VRLabel();
        txtLojaVR = new vrframework.bean.textField.VRTextField();
        btnConectar = new javax.swing.JToggleButton();
        btnConsultar = new javax.swing.JToggleButton();
        lblDados = new vrframework.bean.label.VRLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Componente de Conexão"));

        org.openide.awt.Mnemonics.setLocalizedText(lblConexao, "Conexão Cadastrada");

        cboConexao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboConexaoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, "Loja Origem");

        cboOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOrigemActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblLojaVR, "Loja VR");

        txtLojaVR.setEnabled(false);

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnConectar, "Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/consultar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnConsultar, "Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        lblDados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(lblDados, "DADOS DA CONEXÃO");
        lblDados.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboConexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboOrigem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(lblDados, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnConectar))
                            .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboConexao, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLojaVR, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConectar)
                    .addComponent(btnConsultar)
                    .addComponent(lblDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOrigemActionPerformed
        preencheCampoLojaVR();
    }//GEN-LAST:event_cboOrigemActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            if (conexao != null) {
                conexao.close();
            }

            construirConexao();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao conectar");
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png")));
        }
    }//GEN-LAST:event_btnConectarActionPerformed

    private void cboConexaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboConexaoActionPerformed
        getLojaMapeada();
    }//GEN-LAST:event_cboConexaoActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            setConfiguracao();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Consulta de Conexão");
        }
    }//GEN-LAST:event_btnConsultarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private javax.swing.JToggleButton btnConsultar;
    private vrframework.bean.comboBox.VRComboBox cboConexao;
    private vrframework.bean.comboBox.VRComboBox cboOrigem;
    private vrframework.bean.label.VRLabel lblConexao;
    private vrframework.bean.label.VRLabel lblDados;
    private vrframework.bean.label.VRLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblLojaVR;
    private vrframework.bean.textField.VRTextField txtLojaVR;
    // End of variables declaration//GEN-END:variables

}
