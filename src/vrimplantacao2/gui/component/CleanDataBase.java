package vrimplantacao2.gui.component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoPostgres2;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.VRToVRDAO;
import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import vrimplantacao2.parametro.Parametros;

/**
 *
 * @author Importacao
 */
public class CleanDataBase extends VRInternalFrame {

    private static CleanDataBase instance = null;
    private ConexaoPostgres2 connPost = new ConexaoPostgres2();
    private VRToVRDAO dao = null;

    public CleanDataBase(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.setResizable(false);
        this.title = "Deleta Registro VR";
        dao = new VRToVRDAO();
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                instance = null;
            }
        });

        conexao.setSistema("IMPLANTACAO");
        conexao.host = "localhost";
        conexao.database = "vr";
        conexao.port = "8745";
        conexao.user = "postgres";
        conexao.pass = "VrPost@Server";
        conexao.setOnConectar(new ConexaoEvent() {
            @Override
            public void executar() throws Exception {
                carregarLojaVR();
                gravarParametros();
            }
        });

        carregarParametros();
        centralizarForm();
    }

    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new CleanDataBase(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }

    public void carregarLojaVR() throws Exception {
        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        for (Estabelecimento loja : dao.getLojasVR()) {
            cmbLojaOrigem.addItem(loja);
        }
    }

    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexao.atualizarParametros();

        Estabelecimento cliente = (Estabelecimento) cmbLojaOrigem.getSelectedItem();
        if (cliente != null) {
            params.put(cliente.cnpj, "IMPLANTACAO", "LOJA_CLIENTE");
        }

        params.salvar();
    }

    private void carregarParametros() throws Exception {
        Parametros params = Parametros.get();
        conexao.carregarParametros();
    }

    private void deletarRegistro() {
        Thread td = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);

                    remove();

                    ProgressBar.dispose();
                    Util.exibirMensagem("PROCESSO DE EXCLUSÃO CONCLUÍDO", getTitle());
                } catch (Exception ex) {
                    try {
                        connPost.close();
                    } catch (Exception ex1) {
                        Exceptions.printStackTrace(ex1);
                    }
                    ProgressBar.dispose();
                    Util.exibirMensagemErro(ex, getTitle());
                }
            }
        };

        td.start();
    }

    private void remove() {
        if (chkLogEstoque.isSelected()) {
            try {
                int idLoja = Integer.valueOf(((Estabelecimento) cmbLojaOrigem.getSelectedItem()).cnpj);
                boolean todasLojas = chkAll.isSelected();
                
                FileWriter fw = null;
                PrintWriter pw = null;
                
                if(todasLojas) {
                    fw = new FileWriter("c:\\vr\\implantacao\\logestoque_geral.txt");
                    pw = new PrintWriter(fw);
                    pw.println("Tabela logestoque - Loja Geral");
                } else {
                    fw = new FileWriter("c:\\vr\\implantacao\\logestoque_loja" + idLoja + ".txt");
                    pw = new PrintWriter(fw);
                    pw.println("Tabela logestoque - Loja ID: " + idLoja);
                }
                
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                System.out.println("ID LOJA VR: " + idLoja);

                Date dt1 = df.parse(edtDtInicio.getText());
                Date dt2 = df.parse(edtDtTermino.getText());
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt1);

                for (Date dt = dt1; dt.compareTo(dt2) <= 0;) {
                    System.out.println(df.format(dt));
                    try {
                        ProgressBar.setStatus("Del. logestoque na data de: " + df.format(dt) + "...");
                        
                        dao.deletaLogEstoque(dt, idLoja, todasLojas);
                        pw.println("Dia " + df.format(dt) + " deletado da tabela;");
                        
                        ProgressBar.next();
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    cal.add(Calendar.DATE, +1);
                    dt = cal.getTime();
                }
                fw.close();
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmbLojaOrigem = new javax.swing.JComboBox();
        lblLojaOrigem = new javax.swing.JLabel();
        chkLogEstoque = new vrframework.bean.checkBox.VRCheckBox();
        chkVenda = new vrframework.bean.checkBox.VRCheckBox();
        chkEscrita = new vrframework.bean.checkBox.VRCheckBox();
        pnlPeriodo = new vrframework.bean.panel.VRPanel();
        edtDtInicio = new vrframework.bean.calendar.VRCalendar();
        lblPeriodo = new vrframework.bean.label.VRLabel();
        edtDtTermino = new vrframework.bean.calendar.VRCalendar();
        pnlBotao = new vrframework.bean.panel.VRPanel();
        btnIniciar = new vrframework.bean.button.VRButton();
        conexao = new vrimplantacao2.gui.component.conexao.postgresql.ConexaoPostgreSQLPanel2();
        chkAll = new javax.swing.JCheckBox();

        cmbLojaOrigem.setModel(new DefaultComboBoxModel());
        cmbLojaOrigem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbLojaOrigemActionPerformed(evt);
            }
        });

        lblLojaOrigem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.lblLojaOrigem.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkLogEstoque, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.chkLogEstoque.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkVenda, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.chkVenda.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkEscrita, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.chkEscrita.text")); // NOI18N

        pnlPeriodo.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.pnlPeriodo.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblPeriodo, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.lblPeriodo.text")); // NOI18N

        javax.swing.GroupLayout pnlPeriodoLayout = new javax.swing.GroupLayout(pnlPeriodo);
        pnlPeriodo.setLayout(pnlPeriodoLayout);
        pnlPeriodoLayout.setHorizontalGroup(
            pnlPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPeriodoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(edtDtInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edtDtTermino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPeriodoLayout.setVerticalGroup(
            pnlPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPeriodoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPeriodoLayout.createSequentialGroup()
                        .addComponent(edtDtTermino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlPeriodoLayout.createSequentialGroup()
                        .addComponent(lblPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(pnlPeriodoLayout.createSequentialGroup()
                        .addComponent(edtDtInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20))))
        );

        pnlBotao.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btnIniciar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/importar.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnIniciar, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.btnIniciar.text")); // NOI18N
        btnIniciar.setFocusable(false);
        btnIniciar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnIniciar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBotaoLayout = new javax.swing.GroupLayout(pnlBotao);
        pnlBotao.setLayout(pnlBotaoLayout);
        pnlBotaoLayout.setHorizontalGroup(
            pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBotaoLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlBotaoLayout.setVerticalGroup(
            pnlBotaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnIniciar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(chkAll, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.chkAll.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkAll)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(pnlPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(conexao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmbLojaOrigem, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlBotao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblLojaOrigem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkLogEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkEscrita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(conexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLojaOrigem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbLojaOrigem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(chkAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkLogEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEscrita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlBotao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbLojaOrigemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbLojaOrigemActionPerformed

    }//GEN-LAST:event_cmbLojaOrigemActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        try {
            this.setWaitCursor();
            deletarRegistro();
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());
        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnIniciarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnIniciar;
    private javax.swing.JCheckBox chkAll;
    private vrframework.bean.checkBox.VRCheckBox chkEscrita;
    private vrframework.bean.checkBox.VRCheckBox chkLogEstoque;
    private vrframework.bean.checkBox.VRCheckBox chkVenda;
    private javax.swing.JComboBox cmbLojaOrigem;
    private vrimplantacao2.gui.component.conexao.postgresql.ConexaoPostgreSQLPanel2 conexao;
    private vrframework.bean.calendar.VRCalendar edtDtInicio;
    private vrframework.bean.calendar.VRCalendar edtDtTermino;
    private javax.swing.JLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblPeriodo;
    private vrframework.bean.panel.VRPanel pnlBotao;
    private vrframework.bean.panel.VRPanel pnlPeriodo;
    // End of variables declaration//GEN-END:variables
}
