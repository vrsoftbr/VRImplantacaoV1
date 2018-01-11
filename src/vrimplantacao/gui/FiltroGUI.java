package vrimplantacao.gui;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import vrimplantacao.vo.OrdenacaoConsultaVO;
import vrframework.bean.checkBox.VRCheckBox;
import vrframework.classe.Util;
import vrframework.bean.comboBox.VRComboBox;
import vrframework.bean.consultaCampo.VRConsultaCampo;
import vrframework.bean.consultaCfop.VRConsultaCfop;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.bean.textField.VRTextField;
import vrframework.bean.time.VRTime;
import vrframework.remote.ItemComboVO;
import vrimplantacao.vo.FiltroConsultaVO;
import vrimplantacao.vo.TipoFiltro;

public class FiltroGUI extends VRInternalFrame {

    private List<FiltroConsultaVO> vFiltro = new ArrayList();
    private List<OrdenacaoConsultaVO> vOrdenacao = new ArrayList();

    public FiltroGUI(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        centralizarForm();

        limpar();
    }

    public void exibirFiltro() throws Exception {
        VRColumnTable[] colunas = new VRColumnTable[]{
            new VRColumnTable("Filtro", true, SwingConstants.LEFT, false, null),
            new VRColumnTable("Valor", true, SwingConstants.LEFT, true, null)
        };

        Object[][] dados = new Object[vFiltro.size()][colunas.length];

        int i = 0;

        for (FiltroConsultaVO oFiltro : vFiltro) {
            dados[i][0] = oFiltro.alias;

            if (oFiltro.idTipoFiltro == TipoFiltro.TEXTO.getId()) {
                VRTextField txtCampo = new VRTextField();
                txtCampo.addKeyListener(teclaEnter);
                txtCampo.addFocusListener(focusListener);
                txtCampo.setNextFocusableComponent(cboOrdenacao);
                txtCampo.setTeclaEnter(false);
                txtCampo.setColumns(oFiltro.columns);

                oFiltro.oCampo = txtCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.TEXTO_ILIKE.getId()) {
                VRTextField txtCampo = new VRTextField();
                txtCampo.addKeyListener(teclaEnter);
                txtCampo.addFocusListener(focusListener);
                txtCampo.setNextFocusableComponent(cboOrdenacao);
                txtCampo.setTeclaEnter(false);
                txtCampo.setColumns(oFiltro.columns);

                oFiltro.oCampo = txtCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.NUMERO.getId()) {
                VRTextField txtCampo = new VRTextField();
                txtCampo.addKeyListener(teclaEnter);
                txtCampo.addFocusListener(focusListener);
                txtCampo.setNextFocusableComponent(cboOrdenacao);
                txtCampo.setTeclaEnter(false);
                txtCampo.setMascara(VRTextField.MASK_NUMERO);
                txtCampo.setColumns(oFiltro.columns);

                oFiltro.oCampo = txtCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.NUMERO_TEXTO.getId()) {
                VRTextField txtCampo = new VRTextField();
                txtCampo.addKeyListener(teclaEnter);
                txtCampo.addFocusListener(focusListener);
                txtCampo.setNextFocusableComponent(cboOrdenacao);
                txtCampo.setTeclaEnter(false);
                txtCampo.setMascara(VRTextField.MASK_NUMERO);
                txtCampo.setColumns(oFiltro.columns);

                oFiltro.oCampo = txtCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.CHECKBOX.getId()) {
                VRCheckBox chkCampo = new VRCheckBox();
                chkCampo.addKeyListener(teclaEnter);
                chkCampo.addFocusListener(focusListener);
                chkCampo.setNextFocusableComponent(cboOrdenacao);
                chkCampo.setTeclaEnter(false);

                oFiltro.oCampo = chkCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.COMBOBOX.getId()) {
                VRComboBox cboCampo = new VRComboBox();
                cboCampo.addKeyListener(teclaEnter);
                cboCampo.addFocusListener(focusListener);
                cboCampo.setNextFocusableComponent(cboOrdenacao);
                cboCampo.setTeclaEnter(false);
                cboCampo.setTabela(oFiltro.tabela);
                cboCampo.setFiltro(oFiltro.filtro);

                if (!oFiltro.campoDescricao.equals("")) {
                    cboCampo.setCampoDescricao(oFiltro.campoDescricao);
                }

                if (!oFiltro.campoId.equals("")) {
                    cboCampo.setCampoId(oFiltro.campoId);
                }

                cboCampo.carregar();
                cboCampo.addItem(new ItemComboVO(-1, "TODOS"));
                cboCampo.setId(-1);

                oFiltro.oCampo = cboCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.CONSULTA_CAMPO.getId()) {
                VRConsultaCampo cnsCampo = new VRConsultaCampo();
                cnsCampo.addKeyListener(teclaEnter);
                cnsCampo.addFocusListener(focusListener);
                cnsCampo.setNextFocusableComponent(cboOrdenacao);
                cnsCampo.setTeclaEnter(false);
                cnsCampo.setTabela(oFiltro.tabela);
                cnsCampo.setColumns(oFiltro.columns);
                cnsCampo.setFiltro(oFiltro.filtro);

                if (!oFiltro.campoDescricao.equals("")) {
                    cnsCampo.setCampoDescricao(oFiltro.campoDescricao);
                }

                if (!oFiltro.campoId.equals("")) {
                    cnsCampo.setCampoId(oFiltro.campoId);
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.TIME.getId()) {
                VRTime txtCampo = new VRTime();
                txtCampo.addKeyListener(teclaEnter);
                txtCampo.addFocusListener(focusListener);
                txtCampo.setNextFocusableComponent(cboOrdenacao);
                txtCampo.setTeclaEnter(false);

                oFiltro.oCampo = txtCampo;

            } else if (oFiltro.idTipoFiltro == TipoFiltro.DECIMAL2.getId()) {
                VRTextField txtCampo = new VRTextField();
                txtCampo.addKeyListener(teclaEnter);
                txtCampo.addFocusListener(focusListener);
                txtCampo.setNextFocusableComponent(cboOrdenacao);
                txtCampo.setTeclaEnter(false);
                txtCampo.setColumns(oFiltro.columns);
                txtCampo.setMascara(VRTextField.MASK_DECIMAL2);

                oFiltro.oCampo = txtCampo;

            }

            dados[i][1] = oFiltro.oCampo;

            i++;
        }

        tblFiltro.setModel(dados, colunas);
        tblFiltro.setRowHeight(21);


        TableColumn column = tblFiltro.getColumnModel().getColumn(1);
        column.setCellEditor(new MyDefaultCellEditor());
        column.setCellRenderer(new MyTableCellRenderer());
    }

    public void exibirOrdenacao() throws Exception {
        cboOrdenacao.removeAllItems();

        int i = 0;

        for (OrdenacaoConsultaVO oOrdenacao : vOrdenacao) {
            cboOrdenacao.addItem(new ItemComboVO(i, oOrdenacao.alias));
            i++;
        }

        cboOrdenacao.addItem(new ItemComboVO(-1, "<Padrão>"));
        cboOrdenacao.setId(-1);

        cboTipoOrdenacao.removeAllItems();

        cboTipoOrdenacao.addItem(new ItemComboVO(1, "Crescente"));
        cboTipoOrdenacao.addItem(new ItemComboVO(2, "Decrescente"));
        cboTipoOrdenacao.setId(1);

        cboTipoOrdenacao.setEnabled(false);
    }

    public int getLimite() throws Exception {
        return txtLimite.getInt();
    }

    public String getOrdenacao() throws Exception {
        if (cboOrdenacao.getId() == -1) {
            return "";
        }

        String ordenacao = vOrdenacao.get(cboOrdenacao.getId()).campo;

        if (cboTipoOrdenacao.getId() == 1) {
            ordenacao += " ASC";
        } else if (cboTipoOrdenacao.getId() == 2) {
            ordenacao += " DESC";
        }

        return ordenacao;
    }

    public String getFiltro() throws Exception {
        StringBuilder filtro = new StringBuilder();

        int i = 0;

        for (FiltroConsultaVO oFiltro : vFiltro) {
            if (oFiltro.idTipoFiltro == TipoFiltro.TEXTO.getId()) {
                VRTextField txtCampo = (VRTextField) tblFiltro.getValueAt(i, 1);

                if (!txtCampo.getText().trim().equals("")) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(Util.getGoogle(oFiltro.campo, txtCampo.getText()));
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.NUMERO_TEXTO.getId()) {
                VRTextField txtCampo = (VRTextField) tblFiltro.getValueAt(i, 1);

                if (!txtCampo.getText().trim().equals("")) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " ILIKE '%" + txtCampo.getText() + "%'");
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.TEXTO_ILIKE.getId()) {
                VRTextField txtCampo = (VRTextField) tblFiltro.getValueAt(i, 1);

                if (!txtCampo.getText().trim().equals("")) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " ILIKE '%" + txtCampo.getText() + "%'");
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.NUMERO.getId()) {
                VRTextField txtCampo = (VRTextField) tblFiltro.getValueAt(i, 1);

                if (!txtCampo.getText().equals("")) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " = " + txtCampo.getLong());
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.CHECKBOX.getId()) {
                VRCheckBox chkCampo = (VRCheckBox) tblFiltro.getValueAt(i, 1);

                if (chkCampo.isSelected()) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo);
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.COMBOBOX.getId()) {
                VRComboBox cboCombo = (VRComboBox) tblFiltro.getValueAt(i, 1);

                if (cboCombo.getId() != -1) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");

                    if (oFiltro.tabela.equals("tiposimnao")) {
                        filtro.append(oFiltro.campo + " = " + (cboCombo.getId() == 0 ? "FALSE" : "TRUE"));
                    } else {
                        filtro.append(oFiltro.campo + " = " + cboCombo.getId());
                    }
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.CONSULTA_CAMPO.getId()) {
                VRConsultaCampo cnsCampo = (VRConsultaCampo) tblFiltro.getValueAt(i, 1);

                if (cnsCampo.getId() != -1) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " = " + cnsCampo.getId());
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.CONSULTA_CFOP.getId()) {
                VRConsultaCfop cnsCfop = (VRConsultaCfop) tblFiltro.getValueAt(i, 1);

                if (!cnsCfop.getCfop().isEmpty()) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " = '" + cnsCfop.getCfop() + "'");
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.TIME.getId()) {
                VRTime txtTime = (VRTime) tblFiltro.getValueAt(i, 1);

                if (!txtTime.getClipTextHoraInicio().isEmpty() && !txtTime.getClipTextHoraTermino().isEmpty()) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " BETWEEN '" + txtTime.getHoraInicio() + "' AND '" + txtTime.getHoraTermino() + "'");
                }

            } else if (oFiltro.idTipoFiltro == TipoFiltro.DECIMAL2.getId()) {
                VRTextField txtCampo = (VRTextField) tblFiltro.getValueAt(i, 1);


                if (!txtCampo.getText().equals("")) {
                    filtro.append(filtro.toString().equals("") ? "" : " AND ");
                    filtro.append(oFiltro.campo + " = " + txtCampo.getDouble());
                }
            }

            i++;
        }

        if (filtro.toString().equals("")) {
            return "";
        } else {
            return "(" + filtro.toString() + ")";
        }
    }

    private void limpar() throws Exception {
        exibirFiltro();
        exibirOrdenacao();

        txtLimite.setText("");

        tblFiltro.requestFocus();
    }

    @Override
    public void sair() throws Exception {
        this.setVisible(false);
    }

    public void setvFiltro(List<FiltroConsultaVO> i_vFiltro) {
        vFiltro = i_vFiltro;
    }

    public void setvOrdenacao(List<OrdenacaoConsultaVO> i_vOrdenacao) {
        vOrdenacao = i_vOrdenacao;
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);

        try {
            tblFiltro.editaCampo(0, 1);

        } catch (Exception ex) {
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblFiltro = new vrframework.bean.table.VRTable();
        vRPanel1 = new vrframework.bean.panel.VRPanel();
        btnSair = new vrframework.bean.button.VRButton();
        vRPanel2 = new vrframework.bean.panel.VRPanel();
        vRLabel1 = new vrframework.bean.label.VRLabel();
        cboOrdenacao = new vrframework.bean.comboBox.VRComboBox();
        cboTipoOrdenacao = new vrframework.bean.comboBox.VRComboBox();
        txtLimite = new vrframework.bean.textField.VRTextField();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        vRToolBar1 = new vrframework.bean.toolBar.VRToolBar();
        btnLimpar = new vrframework.bean.button.VRButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Filtro de Consulta");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        tblFiltro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblFiltro.setHabilitaOrdenacao(false);
        tblFiltro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFiltroMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblFiltroMouseReleased(evt);
            }
        });
        tblFiltro.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblFiltroFocusGained(evt);
            }
        });
        tblFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblFiltroKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblFiltroKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblFiltro);

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout vRPanel1Layout = new javax.swing.GroupLayout(vRPanel1);
        vRPanel1.setLayout(vRPanel1Layout);
        vRPanel1Layout.setHorizontalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, vRPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        vRPanel1Layout.setVerticalGroup(
            vRPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        vRLabel1.setText("Ordenado por");

        cboOrdenacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboOrdenacaoActionPerformed(evt);
            }
        });

        txtLimite.setMascara("Numero");

        vRLabel2.setText("Limite");

        vRLabel3.setText("registro(s)");

        javax.swing.GroupLayout vRPanel2Layout = new javax.swing.GroupLayout(vRPanel2);
        vRPanel2.setLayout(vRPanel2Layout);
        vRPanel2Layout.setHorizontalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(cboOrdenacao, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTipoOrdenacao, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(txtLimite, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        vRPanel2Layout.setVerticalGroup(
            vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vRPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(vRPanel2Layout.createSequentialGroup()
                        .addComponent(vRLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(vRPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboOrdenacao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboTipoOrdenacao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        vRToolBar1.setRollover(true);

        btnLimpar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/limpar.png"))); // NOI18N
        btnLimpar.setToolTipText("Limpar");
        btnLimpar.setFocusable(false);
        btnLimpar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLimpar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });
        vRToolBar1.add(btnLimpar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vRPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addComponent(vRPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(vRToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(vRToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        try {
            this.setWaitCursor();
            this.setVisible(false);

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnSairActionPerformed
    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
    }//GEN-LAST:event_formComponentHidden

    private void tblFiltroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblFiltroKeyPressed
    }//GEN-LAST:event_tblFiltroKeyPressed

    private void tblFiltroMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFiltroMouseClicked
    }//GEN-LAST:event_tblFiltroMouseClicked

    private void tblFiltroMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFiltroMouseReleased
    }//GEN-LAST:event_tblFiltroMouseReleased

    private void tblFiltroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblFiltroKeyReleased
    }//GEN-LAST:event_tblFiltroKeyReleased

    private void cboOrdenacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboOrdenacaoActionPerformed
        try {
            this.setWaitCursor();
            if (cboOrdenacao.getId() == -1) {
                cboTipoOrdenacao.setId(1);
                cboTipoOrdenacao.setEnabled(false);
            } else {
                cboTipoOrdenacao.setEnabled(true);

                if (cboTipoOrdenacao.getId() == -1) {
                    cboTipoOrdenacao.setId(1);
                }
            }

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_cboOrdenacaoActionPerformed
    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        try {
            this.setWaitCursor();
            limpar();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
}//GEN-LAST:event_btnLimparActionPerformed
    private void tblFiltroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblFiltroFocusGained
        try {
            this.setWaitCursor();
            tblFiltro.editaCampo(tblFiltro.getLinhaSelecionada(), 1);

        } catch (Exception ex) {
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_tblFiltroFocusGained
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnLimpar;
    private vrframework.bean.button.VRButton btnSair;
    private vrframework.bean.comboBox.VRComboBox cboOrdenacao;
    private vrframework.bean.comboBox.VRComboBox cboTipoOrdenacao;
    private javax.swing.JScrollPane jScrollPane1;
    private vrframework.bean.table.VRTable tblFiltro;
    private vrframework.bean.textField.VRTextField txtLimite;
    private vrframework.bean.label.VRLabel vRLabel1;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.panel.VRPanel vRPanel1;
    private vrframework.bean.panel.VRPanel vRPanel2;
    private vrframework.bean.toolBar.VRToolBar vRToolBar1;
    // End of variables declaration//GEN-END:variables

    private class MyDefaultCellEditor extends DefaultCellEditor {

        private Object obj = null;

        public MyDefaultCellEditor() {
            super(new VRComboBox());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value == null) {
                return null;
            }

            obj = value;

            return (Component) value;
        }

        @Override
        public Object getCellEditorValue() {
            return obj;
        }
    }

    private class MyTableCellRenderer extends VRComboBox implements TableCellRenderer {

        public MyTableCellRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if (column == 0) {
                return new JLabel(value == null ? "" : value.toString());
            }

            return (Component) value;
        }
    }
    private FocusListener focusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            tblFiltro.repaint();
        }
    };
    private KeyListener teclaEnter = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            try {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    tblFiltro.requestFocus();

                    if (tblFiltro.getLinhaSelecionada() < (tblFiltro.getRowCount() - 1)) {
                        tblFiltro.editaCampo(tblFiltro.getLinhaSelecionada() + 1, 1);
                    } else {
                        tblFiltro.editaCampo(0, 1);
                    }
                }

            } catch (Exception ex) {
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };
}
