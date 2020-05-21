package vrimplantacao2.gui.component;

import java.io.FileWriter;
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
import vrframework.classe.Conexao;
import vrframework.classe.OperacaoCanceladaException;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.dao.fiscal.EscritaDAO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.interfaces.VRToVRDAO;
import vrimplantacao2.dao.cadastro.venda.PdvVendaDAO;

/**
 *
 * @author Importacao
 */
public class CleanDataBase extends VRInternalFrame {

    private static CleanDataBase instance = null;
    private CleanDataBaseTableModel model;

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
        this.model = new CleanDataBaseTableModel();
        tblLojas.setModel(this.model);
    }
    
    private void deletarRegistro() {
        Thread td = new Thread() {
            @Override
            public void run() {
                try {
                    ProgressBar.show();
                    ProgressBar.setCancel(true);
                    
                    ArrayList<Integer> list = new ArrayList();
                    for (CleanDataBaseTableModelRecord r: model.lojas) {
                        if (r.selected) {
                            list.add(r.id);
                        }
                    }
                    
                    Set<DatabaseCleanerOpcao> opcoes = new HashSet<>();
                    
                    if (chkLogEstoque.isSelected()) {
                        opcoes.add(DatabaseCleanerOpcao.LOG_ESTOQUE);
                    }
                    if (chkEscrita.isSelected()) {
                        opcoes.add(DatabaseCleanerOpcao.ESCRITA);
                    }
                    if (chkVenda.isSelected()) {
                        opcoes.add(DatabaseCleanerOpcao.VENDA);
                    }
                    
                    new DatabaseCleaner(
                            DatabaseCleaner.DT_FORMAT.parse(edtDtInicio.getText()),
                            DatabaseCleaner.DT_FORMAT.parse(edtDtTermino.getText()),
                            list.toArray(new Integer[]{}),
                            opcoes
                    ).remove();

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
        btnSelTudo = new vrframework.bean.button.VRButton();
        btnSelNada = new vrframework.bean.button.VRButton();

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

        lblLojaOrigem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(lblLojaOrigem, "Lojas");

        org.openide.awt.Mnemonics.setLocalizedText(chkLogEstoque, "Log Estoque");

        org.openide.awt.Mnemonics.setLocalizedText(chkVenda, "Venda");

        org.openide.awt.Mnemonics.setLocalizedText(chkEscrita, "Escrita");

        pnlPeriodo.setBorder(javax.swing.BorderFactory.createTitledBorder("Período"));

        org.openide.awt.Mnemonics.setLocalizedText(lblPeriodo, "á");

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
        org.openide.awt.Mnemonics.setLocalizedText(btnIniciar, "Iniciar");
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

        org.openide.awt.Mnemonics.setLocalizedText(btnSelTudo, "Sel. Tudo");
        btnSelTudo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelTudoActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnSelNada, "Sel. Nada");
        btnSelNada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelNadaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnSelTudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelNada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(lblLojaOrigem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1)
                            .addComponent(pnlBotao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkLogEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkEscrita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnlPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(btnSelTudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelNada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkLogEstoque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEscrita, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

    private void btnSelTudoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelTudoActionPerformed
        for (CleanDataBaseTableModelRecord r: this.model.lojas) {
            r.selected = true;
        }
        this.model.fireTableDataChanged();
    }//GEN-LAST:event_btnSelTudoActionPerformed

    private void btnSelNadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelNadaActionPerformed
        for (CleanDataBaseTableModelRecord r: this.model.lojas) {
            r.selected = false;
        }
        this.model.fireTableDataChanged();
    }//GEN-LAST:event_btnSelNadaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vrframework.bean.button.VRButton btnIniciar;
    private vrframework.bean.button.VRButton btnSelNada;
    private vrframework.bean.button.VRButton btnSelTudo;
    private vrframework.bean.checkBox.VRCheckBox chkEscrita;
    private vrframework.bean.checkBox.VRCheckBox chkLogEstoque;
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
    ESCRITA,
    VENDA
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
    
    public void remove() throws Exception {
        
        cancelado = false;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh-mm-dd");
        SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try (FileWriter log = new FileWriter("log-exclusao-" + fileDateFormat.format(new Date()) + ".txt", true)) {  
            log.write(String.format("Operação de exclusão iniciada em %s\n", format.format(new Date())));                      
            for (int idLoja: this.idLojas) {
                
                if (cancelado) return;

                log.write("LOJA " + idLoja + "\n");

                cal.setTime(this.dtInicio);
                for (Date dt = this.dtInicio; dt.compareTo(this.dtTermino) <= 0; dt = cal.getTime()) {

                    try {
                        Conexao.begin();

                        System.out.println(DT_FORMAT.format(dt));

                        if (opcoes.contains(DatabaseCleanerOpcao.LOG_ESTOQUE)) {
                            apagarLogEstoque(dt, idLoja, log);
                        }
                        if (opcoes.contains(DatabaseCleanerOpcao.ESCRITA)) {
                            apagarEscrita(dt, idLoja, log);
                        }
                        if (opcoes.contains(DatabaseCleanerOpcao.VENDA)) {
                            apagarVenda(dt, idLoja, log);
                        }

                        cal.add(Calendar.DATE, +1);

                        Conexao.commit();
                    } catch (Exception ex) {
                        Conexao.rollback();
                        if (!(ex instanceof OperacaoCanceladaException)) {
                            log.write(String.format("Operação cancelada em %s", DT_FORMAT.format(new Date())));
                            throw ex;
                        }
                        log.write(String.format("Operação de exclusão cancelada em %s\n", format.format(new Date()))); 
                    }
                    
                    log.flush();

                }
                
                if (!cancelado) log.write(String.format("LOJA " + idLoja + " CONCLUÍDA em %s\n", format.format(new Date()))); 
                
            }
            log.write(String.format("Operação de exclusão CONCLUÍDA em %s\n", format.format(new Date()))); 
        }
    }

    private void apagarLogEstoque(Date dt, int idLoja, final FileWriter log) throws Exception {
        try {
            if (cancelado) return;
            ProgressBar.setStatus("LOJA " + idLoja + " - Del. logestoque na data de: " + DT_FORMAT.format(dt) + "...");            
            dao.deletaLogEstoque(dt, idLoja);
            log.write("Dia " + DT_FORMAT.format(dt) + " - LOJA " + idLoja + " - deletado do LOGESTOQUE\n");
            
            ProgressBar.next();
        } catch (Exception ex) {
            if (ex instanceof OperacaoCanceladaException) {
                cancelado = true;
                throw ex;
            }
            Exceptions.printStackTrace(ex);
            log.write("\n");
            log.write("ERRO: " + ex.getMessage() + "\n");
            log.write(ExceptionUtils.getStackTrace(ex));
        }
    }

    private boolean cancelado = false;
    private final EscritaDAO escritaDAO = new EscritaDAO();
    private void apagarEscrita(Date dt, int idLoja, FileWriter log) throws Exception {
        try {
            ProgressBar.setStatus("LOJA " + idLoja + " - Del. escrita na data de: " + DT_FORMAT.format(dt) + "...");
            List<Integer> ids = escritaDAO.getIdsPorData(idLoja, dt);
            ProgressBar.setMaximum(ids.size());
            
            for (Integer id: ids) {
                if (cancelado) return;
                escritaDAO.excluirId(id);
                ProgressBar.next();
            }
            
            log.write("Dia " + DT_FORMAT.format(dt) + " - LOJA " + idLoja + " - deletado da ESCRITA\n");
        } catch (Exception ex) {
            if (ex instanceof OperacaoCanceladaException) {
                cancelado = true;
                throw ex;
            }
            Exceptions.printStackTrace(ex);
            log.write("\n");
            log.write("ERRO: " + ex.getMessage() + "\n");
            log.write(ExceptionUtils.getStackTrace(ex));
        }
    }
    
    private PdvVendaDAO pdvDAO;
    private void apagarVenda(Date dt, int idLoja, FileWriter log) throws Exception {
        try {
            pdvDAO = new PdvVendaDAO();
            ProgressBar.setStatus("LOJA " + idLoja + " - Del. venda na data de: " + DT_FORMAT.format(dt) + "...");
            List<Integer> ids = pdvDAO.getIdsPorData(idLoja, dt);
            ProgressBar.setMaximum(ids.size());
            
            for (Integer id: ids) {
                if (cancelado) return;
                pdvDAO.cleanerVenda(id);
                ProgressBar.next();
            }
            
            log.write("Dia " + DT_FORMAT.format(dt) + " - LOJA " + idLoja + " - deletado da VENDA\n");
        } catch (Exception ex) {
            if (ex instanceof OperacaoCanceladaException) {
                cancelado = true;
                throw ex;
            }
            Exceptions.printStackTrace(ex);
            log.write("\n");
            log.write("ERRO: " + ex.getMessage() + "\n");
            log.write(ExceptionUtils.getStackTrace(ex));
        }
    }
    
}
