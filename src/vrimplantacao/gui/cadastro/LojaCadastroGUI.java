package vrimplantacao.gui.cadastro;

import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.controller.loja.LojaController;

public class LojaCadastroGUI extends VRInternalFrame {

    private LojaVO oLoja = null;
    private LojaConsultaGUI parentFrame = null;

    public LojaCadastroGUI(VRMdiFrame i_mdiFrame, LojaConsultaGUI i_parentFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        centralizarForm();

        parentFrame = i_parentFrame;

        cboCopiarLoja.setTabela("loja");
        cboCopiarLoja.carregar();

        cboRegiao.setTabela("regiao");
        cboRegiao.carregar();
        
        chkCopiaMargem.setEnabled(false);
    }

    public void habilitarTela() throws Exception {
        if (oLoja.id > 0) {
            txtCodigo.setEnabled(false);
            cboCopiarLoja.setEnabled(false);
            cboCopiarLoja.setSelectedIndex(-1);

        } else {
            txtCodigo.setEnabled(true);
            cboCopiarLoja.setEnabled(true);
            cboRegiao.setSelectedIndex(-1);
        }
    }

    public void carregar(int i_id) throws Exception {
        oLoja = new LojaDAO().carregar(i_id);

        txtCodigo.setText(Util.formatNumber(oLoja.id, 6));
        txtDescricao.setText(oLoja.descricao);
        txtFornecedor.setText(Util.formatNumber(oLoja.idFornecedor, 6));
        txtIpServidor.setText(oLoja.nomeServidor);
        chkServidorCentral.setSelected(oLoja.servidorCentral);
        cboRegiao.setId(oLoja.idRegiao);
        chkGeraConcentrador.setSelected(oLoja.geraConcentrador);

        habilitarTela();
    }

    @Override
    public void incluir() throws Exception {
        oLoja = new LojaVO();

        txtCodigo.setText(Util.formatNumber(getNextId(), 6));
        txtDescricao.setText("");
        txtFornecedor.setText("");
        txtIpServidor.setText("");
        chkServidorCentral.setSelected(false);
        chkGeraConcentrador.setSelected(false);
        chkCopiaPrecoVenda.setSelected(false);
        chkCopiaCusto.setSelected(false);
        chkCopiaTecladoLayout.setSelected(false);
        chkCopiaMargem.setSelected(false);

        habilitarTela();
    }

    @Override
    public void salvar() throws Exception {
        Util.validarCampoTela(this.getCampoObrigatorio());

        if (txtCodigo.getInt() == cboCopiarLoja.getId()) {
            throw new VRException("A loja selecionada e a loja copiada não podem ser a mesma!");
        }

        if (txtFornecedor.getInt() == 0) {
            throw new VRException("Informe o código do fornecedor!");
        }

        oLoja.id = txtCodigo.getInt();
        oLoja.descricao = txtDescricao.getText();
        oLoja.idFornecedor = txtFornecedor.getInt();
        oLoja.nomeServidor = txtIpServidor.getText();
        oLoja.servidorCentral = chkServidorCentral.isSelected();
        oLoja.idCopiarLoja = cboCopiarLoja.getId();
        oLoja.idRegiao = cboRegiao.getId();
        oLoja.geraConcentrador = chkGeraConcentrador.isSelected();
        oLoja.copiaPrecoVenda = chkCopiaPrecoVenda.isSelected();
        oLoja.copiaCusto = chkCopiaCusto.isSelected();
        oLoja.copiaTecladoLayout = chkCopiaTecladoLayout.isSelected();
        oLoja.setCopiaMargem(chkCopiaMargem.isSelected());

        new LojaController().salvar(oLoja);

        parentFrame.vLoja.add(oLoja);
        cboCopiarLoja.carregar();

        habilitarTela();
        Util.exibirMensagem(Util.MSG_SALVO_SUCESSO, getTitle());
    }

    private int getNextId() throws Exception {
        int i = 1;

        while (true) {
            boolean achou = false;

            for (LojaVO oLoja : parentFrame.vLoja) {
                if (oLoja.id == i) {
                    achou = true;
                    break;
                }
            }

            if (!achou) {
                return i;
            }

            i++;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        btnTbIncluir = new vrframework.bean.button.VRButton();
        btnTbSalvar = new vrframework.bean.button.VRButton();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        txtFornecedor = new vrframework.bean.textField.VRTextField();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtDescricao = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        txtIpServidor = new vrframework.bean.textField.VRTextField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtCodigo = new vrframework.bean.textField.VRTextField();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        cboCopiarLoja = new vrframework.bean.comboBox.VRComboBox();
        chkServidorCentral = new vrframework.bean.checkBox.VRCheckBox();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        cboRegiao = new vrframework.bean.comboBox.VRComboBox();
        chkGeraConcentrador = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiaPrecoVenda = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiaCusto = new vrframework.bean.checkBox.VRCheckBox();
        chkCopiaTecladoLayout = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel2 = new vr.view.components.panel.VRPanel();
        chkVersao4 = new vr.view.components.checkbox.VRCheckBox();
        chkCopiaMargem = new vrframework.bean.checkBox.VRCheckBox();
        vRPanel3 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        btnSalvar = new vrframework.bean.button.VRButton();

        setTitle("Cadastro de Loja");

        vRToolBar1.setRollover(true);

        btnTbIncluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/incluir.png"))); // NOI18N
        btnTbIncluir.setFocusable(false);
        btnTbIncluir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTbIncluir.setName(""); // NOI18N
        btnTbIncluir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTbIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTbIncluirActionPerformed(evt);
            }
        });
        vRToolBar1.add(btnTbIncluir);

        btnTbSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        btnTbSalvar.setFocusable(false);
        btnTbSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTbSalvar.setName(""); // NOI18N
        btnTbSalvar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTbSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTbSalvarActionPerformed(evt);
            }
        });
        vRToolBar1.add(btnTbSalvar);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Código");
        vRLabel2.setName(""); // NOI18N

        txtFornecedor.setColumns(6);
        txtFornecedor.setMascara("Numero");
        txtFornecedor.setName(""); // NOI18N
        txtFornecedor.setObrigatorio(true);
        txtFornecedor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFornecedorFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel3, "Descrição");
        vRLabel3.setName(""); // NOI18N

        txtDescricao.setColumns(25);
        txtDescricao.setName(""); // NOI18N
        txtDescricao.setObrigatorio(true);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel4, "Nome Servidor");
        vRLabel4.setName(""); // NOI18N

        txtIpServidor.setColumns(30);
        txtIpServidor.setName(""); // NOI18N
        txtIpServidor.setObrigatorio(true);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel5, "Fornecedor");
        vRLabel5.setName(""); // NOI18N

        txtCodigo.setColumns(6);
        txtCodigo.setMascara("Numero");
        txtCodigo.setName(""); // NOI18N
        txtCodigo.setObrigatorio(true);
        txtCodigo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCodigoFocusLost(evt);
            }
        });
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel6, "Copiar da Loja");
        vRLabel6.setName(""); // NOI18N

        cboCopiarLoja.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkServidorCentral, "Servidor Central");
        chkServidorCentral.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel7, "Região");
        vRLabel7.setName(""); // NOI18N

        cboRegiao.setName(""); // NOI18N
        cboRegiao.setObrigatorio(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkGeraConcentrador, "Gera Concentrador");
        chkGeraConcentrador.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkCopiaPrecoVenda, "Copia Preço Venda");
        chkCopiaPrecoVenda.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkCopiaCusto, "Copia Custo");
        chkCopiaCusto.setName(""); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkCopiaTecladoLayout, "Copia Teclado Layout");
        chkCopiaTecladoLayout.setName(""); // NOI18N

        vRPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Versão do Sistema"));

        org.openide.awt.Mnemonics.setLocalizedText(chkVersao4, "Versão 4");
        chkVersao4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkVersao4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkVersao4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chkVersao4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(chkCopiaMargem, "Copia Margem");
        chkCopiaMargem.setName(""); // NOI18N

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(vRPanel1Layout.createSequentialGroup()
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtIpServidor, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cboCopiarLoja, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboRegiao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(vRPanel1Layout.createSequentialGroup()
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
            .addGroup(vRPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkGeraConcentrador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiaTecladoLayout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addComponent(chkServidorCentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCopiaPrecoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCopiaCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkCopiaMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(vRPanel1Layout.createSequentialGroup()
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIpServidor, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCopiarLoja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboRegiao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkGeraConcentrador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkServidorCentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiaPrecoVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiaCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCopiaTecladoLayout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCopiaMargem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSair, "Sair");
        btnSair.setName(""); // NOI18N
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/salvar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnSalvar, "Salvar");
        btnSalvar.setName(""); // NOI18N
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel3Layout = new javax.swing.GroupLayout(vRPanel3);
        vRPanel3.setLayout(vRPanel3Layout);
        vRPanel3Layout.setHorizontalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel3Layout.createSequentialGroup()
                .addContainerGap(287, Short.MAX_VALUE)
                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel3Layout.setVerticalGroup(
            vRPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSalvar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(vRToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 3, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(vRPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        try {
            this.setWaitCursor();
            salvar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnSalvarActionPerformed
    private void btnTbIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTbIncluirActionPerformed
        try {
            this.setWaitCursor();
            incluir();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnTbIncluirActionPerformed
    private void btnTbSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTbSalvarActionPerformed
        try {
            this.setWaitCursor();
            salvar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnTbSalvarActionPerformed
    private void txtFornecedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFornecedorFocusLost
        try {
            this.setWaitCursor();
            txtFornecedor.setText(Util.formatNumber(txtFornecedor.getText(), txtFornecedor.getColumns()));
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_txtFornecedorFocusLost
    private void txtCodigoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoFocusLost
        try {
            this.setWaitCursor();
            txtCodigo.setText(Util.formatNumber(txtCodigo.getText(), txtCodigo.getColumns()));
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_txtCodigoFocusLost

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void chkVersao4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkVersao4ActionPerformed
        // TODO add your handling code here:
        if (chkVersao4.isSelected()) {
            chkCopiaMargem.setEnabled(true);
        } else {
            chkCopiaMargem.setSelected(false);
            chkCopiaMargem.setEnabled(false);            
        }
    }//GEN-LAST:event_chkVersao4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.button.VRButton btnSalvar;
    private vrframework.bean.button.VRButton btnTbIncluir;
    private vrframework.bean.button.VRButton btnTbSalvar;
    private vrframework.bean.comboBox.VRComboBox cboCopiarLoja;
    private vrframework.bean.comboBox.VRComboBox cboRegiao;
    private vrframework.bean.checkBox.VRCheckBox chkCopiaCusto;
    private vrframework.bean.checkBox.VRCheckBox chkCopiaMargem;
    private vrframework.bean.checkBox.VRCheckBox chkCopiaPrecoVenda;
    private vrframework.bean.checkBox.VRCheckBox chkCopiaTecladoLayout;
    private vrframework.bean.checkBox.VRCheckBox chkGeraConcentrador;
    private vrframework.bean.checkBox.VRCheckBox chkServidorCentral;
    private vr.view.components.checkbox.VRCheckBox chkVersao4;
    private vrframework.bean.textField.VRTextField txtCodigo;
    private vrframework.bean.textField.VRTextField txtDescricao;
    private vrframework.bean.textField.VRTextField txtFornecedor;
    private vrframework.bean.textField.VRTextField txtIpServidor;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel6;
    private vrframework.bean.label.VRLabel vRLabel7;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vr.view.components.panel.VRPanel vRPanel2;
    private vrframework.bean.panel.VRPanel vRPanel3;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    // End of variables declaration//GEN-END:variables
}
