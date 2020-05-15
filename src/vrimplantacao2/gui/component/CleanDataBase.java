package vrimplantacao2.gui.component;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openide.util.Exceptions;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoPostgres2;
import vrimplantacao.dao.fiscal.EscritaDAO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.VRToVRDAO;

/**
 *
 * @author Importacao
 */
public class CleanDataBase extends VRInternalFrame {

    private static CleanDataBase instance = null;
    private ConexaoPostgres2 connPost = new ConexaoPostgres2();

    public CleanDataBase(VRMdiFrame i_mdiFrame) throws Exception {
        super(i_mdiFrame);
        initComponents();

        this.setResizable(false);
        this.title = "Deleta Registro VR";
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                instance = null;
            }
        });

        carregarLojaVR();
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
        tblLojas.setModel(new CleanDataBaseTableModel());
    }
    
    private void deletarRegistro() {
        Thread td = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    ArrayList<Integer> list = new ArrayList();                    
                    
                    Set<DatabaseCleanerOpcao> opcoes = new HashSet<>();
                    
                    if (chkLogEstoque.isSelected()) {
                        opcoes.add(DatabaseCleanerOpcao.LOG_ESTOQUE);
                    }
                    if (chkEscrita.isSelected()) {
                        opcoes.add(DatabaseCleanerOpcao.ESCRITA);
                    }
                    
                    new DatabaseCleaner(
                            DatabaseCleaner.DT_FORMAT.parse(edtDtInicio.getText()),
                            DatabaseCleaner.DT_FORMAT.parse(edtDtTermino.getText()),
                            list.toArray(new Integer[]{}),
                            opcoes
                    );

                    ProgressBar.dispose();
                    Util.exibirMensagem("PROCESSO DE EXCLUSÃO CONCLUÍDO", getTitle());
                } catch (Exception ex) {
                    Util.exibirMensagemErro(ex, getTitle());
                } finally {                    
                    ProgressBar.dispose();
                }
            }
        };

        td.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblLojas = new vrframework.bean.table.VRTable();
        chkAll = new javax.swing.JCheckBox();
        chkNone = new javax.swing.JCheckBox();
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

        tblLojas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblLojas);

        org.openide.awt.Mnemonics.setLocalizedText(chkAll, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.chkAll.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkNone, org.openide.util.NbBundle.getMessage(CleanDataBase.class, "CleanDataBase.chkNone.text")); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblLojaOrigem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlBotao, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkLogEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkEscrita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkNone)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLojaOrigem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkAll)
                    .addComponent(chkNone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkLogEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEscrita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlBotao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JCheckBox chkNone;
    private vrframework.bean.checkBox.VRCheckBox chkVenda;
    private vrframework.bean.calendar.VRCalendar edtDtInicio;
    private vrframework.bean.calendar.VRCalendar edtDtTermino;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLojaOrigem;
    private vrframework.bean.label.VRLabel lblPeriodo;
    private vrframework.bean.panel.VRPanel pnlBotao;
    private vrframework.bean.panel.VRPanel pnlPeriodo;
    private vrframework.bean.table.VRTable tblLojas;
    // End of variables declaration//GEN-END:variables

    private static class CleanDataBaseTableModelRecord {
        boolean selected;
        int id;
        String descricao;

        public CleanDataBaseTableModelRecord(boolean selected, int id, String descricao) {
            this.selected = selected;
            this.id = id;
            this.descricao = descricao;
        }
    }
    
    private static class CleanDataBaseTableModel extends AbstractTableModel {
        
        private List<CleanDataBaseTableModelRecord> lojas;

        public CleanDataBaseTableModel() throws Exception {
            this.lojas = new ArrayList<>();
            for (Estabelecimento e: new VRToVRDAO().getLojas()) {
                this.lojas.add(new CleanDataBaseTableModelRecord(
                        false,
                        Integer.parseInt(e.cnpj),
                        e.razao
                ));
            }
        }

        @Override
        public int getRowCount() {
            return lojas.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0: return "Sel.";
                case 1: return "ID";
                case 2: return "Descrição";
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                this.lojas.get(rowIndex).selected = (boolean) aValue;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return Boolean.class;
                case 1: return Integer.class;
                case 2: return String.class;
                default: return null;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CleanDataBaseTableModelRecord e = this.lojas.get(rowIndex);
            switch (columnIndex) {
                case 0: return e.selected;
                case 1: return e.id;
                case 2: return e.descricao;
                default: return null;
            }
        }
        
    }
}

enum DatabaseCleanerOpcao {
    LOG_ESTOQUE,
    ESCRITA
}

class DatabaseCleaner {
    
    public static final DateFormat DT_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    private final Date dtInicio;
    private final Date dtTermino;
    private final Integer[] idLojas;
    private final Set<DatabaseCleanerOpcao> opcoes;
    private final VRToVRDAO dao = new VRToVRDAO();
    
    public DatabaseCleaner(Date dtInicio, Date dtTermino, Integer[] idLojas, Set<DatabaseCleanerOpcao> opcoes) {
        this.idLojas = idLojas;        
        this.dtTermino = dtTermino;
        this.dtInicio = dtInicio;
        this.opcoes = opcoes;
    }
    
    public void remove() throws IOException {        

        Calendar cal = Calendar.getInstance();
        
        try (FileWriter log = new FileWriter("log" + new SimpleDateFormat("yyyy-MM-dd hh-mm-dd") + ".txt")) {                        
            for (int idLoja: this.idLojas) {
                try {
                    ConexaoPostgres2.begin();

                    log.write("Inicio : " + idLoja + "\n");

                    String file = "logestoque_loja" + idLoja + ".txt";
                    try (FileWriter fw = new FileWriter(file)) {                    
                        fw.write("Tabela logestoque - Loja ID: " + idLoja + "\n");

                        cal.setTime(this.dtInicio);
                        for (Date dt = this.dtInicio; dt.compareTo(this.dtTermino) <= 0; dt = cal.getTime()) {

                            System.out.println(DT_FORMAT.format(dt));
                            log.write(String.format("", DT_FORMAT.format(dt)));

                            if (opcoes.contains(DatabaseCleanerOpcao.LOG_ESTOQUE)) {
                                apagarLogEstoque(dt, idLoja, fw, log);
                            }
                            if (opcoes.contains(DatabaseCleanerOpcao.ESCRITA)) {
                                apagarEscrita(dt, idLoja, fw, log);
                            }

                            cal.add(Calendar.DATE, +1);
                        }
                    }

                    ConexaoPostgres2.commit();
                } catch (Exception ex) {
                    ConexaoPostgres2.rollback();
                    throw ex;
                }
            }
        }
    }

    private void apagarLogEstoque(Date dt, int idLoja, final FileWriter fw, final FileWriter log) throws IOException {
        try {
            ProgressBar.setStatus("Del. logestoque na data de: " + DT_FORMAT.format(dt) + "...");
            
            dao.deletaLogEstoque(dt, idLoja);
            fw.write("Dia " + DT_FORMAT.format(dt) + " deletado da tabela;\n");
            
            ProgressBar.next();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            log.write("\n");
            log.write("ERRO: " + ex.getMessage() + "\n");
            log.write(ExceptionUtils.getStackTrace(ex));
        }
    }

    private final EscritaDAO escritaDAO = new EscritaDAO();
    private void apagarEscrita(Date dt, int idLoja, FileWriter fw, FileWriter log) throws IOException {
        try {
            ProgressBar.setStatus("Del. escrita na data de: " + DT_FORMAT.format(dt) + "...");
            
            
            escritaDAO.excluirData(dt, idLoja);
            fw.write("Dia " + DT_FORMAT.format(dt) + " deletado da tabela;\n");
            
            ProgressBar.next();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            log.write("\n");
            log.write("ERRO: " + ex.getMessage() + "\n");
            log.write(ExceptionUtils.getStackTrace(ex));
        }
    }
    
}
