package vrimplantacao2.gui.component.sqleditor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import org.jdesktop.swingx.combobox.EnumComboBoxModel;
import vrframework.bean.internalFrame.VRInternalFrame;
import vrframework.bean.mdiFrame.VRMdiFrame;
import vrframework.bean.table.VRColumnTable;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoDB2;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.vo.vrimplantacao.SqlVO;
import vrimplantacao2.parametro.Parametros;

/**
 *
 * @author Leandro
 */
public class SQLEditor extends VRInternalFrame {

    private SqlVO oSql;    
    
    private SqlVO consultar(String sql) throws Exception {
        try (Statement stm = conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                SqlVO row = new SqlVO();

                for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
                    row.vHeader.add(rst.getMetaData().getColumnName(i));
                }

                while (rst.next()) {
                    List<String> vColuna = new ArrayList();

                    for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
                        vColuna.add(rst.getString(i));
                    }

                    row.vConsulta.add(vColuna);
                }

                return row;
            }
        }
    }
    
    private void configurarColuna() throws Exception {
        List<VRColumnTable> vColuna = new ArrayList();

        for (String coluna : oSql.vHeader) {
            vColuna.add(new VRColumnTable(coluna, 80, true, SwingConstants.LEFT, false, null));
        }

        tblConsulta.configurarColuna(vColuna, this, "tblConsulta", "exibirConsulta", Global.idUsuario);
    }
    
    public void exibirConsulta() throws Exception {
        Object[][] dados = new Object[oSql.vConsulta.size()][tblConsulta.getvColuna().size()];

        int l = 0;

        for (List<String> vColuna : oSql.vConsulta) {
            for (int c = 0; c < vColuna.size(); c++) {
                dados[l][tblConsulta.getOrdem(c)] = vColuna.get(tblConsulta.getOrdem(c));
            }

            l++;
        }

        tblConsulta.setModel(dados);
    }
    
    private void executarSQL() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    setWaitCursor();

                    btnExecutar2.setEnabled(false);

                    String sql = txtSQL.getSelectedText() == null ? txtSQL.getText() : txtSQL.getSelectedText();

                    tblConsulta.clear();
                    txtMensagem.setText("");

                    if (sql.trim().toUpperCase().startsWith("SELECT")) {
                        oSql = consultar(sql);

                        configurarColuna();

                        exibirConsulta();
                        txtMensagem.setText(oSql.vConsulta.size() + " registros recuperados.");

                        if (oSql.vConsulta.size() > 0) {
                            tpnResultado.setSelectedIndex(0);
                        } else {
                            tpnResultado.setSelectedIndex(1);
                        }

                    }

                } catch (Exception ex) {
                    txtMensagem.setText(ex.getMessage());
                    tpnResultado.setSelectedIndex(1);

                } finally {
                    setDefaultCursor();                    
                    btnExecutar2.setEnabled(true);
                }
            }
        };

        thread.start();
    }
    
    private enum Conexao {
        DB2 {
            private ConexaoDB2 conn;
            @Override
            public String toString() {
                return "DB2";
            }
            @Override
            public Connection getConexao() throws Exception {
                return ConexaoDB2.getConexao();
            }
            @Override
            public void conectar(final SQLEditor editor) throws Exception {
                if (conn != null) {
                    conn.close();
                }
                conn = new ConexaoDB2();
                conn.abrirConexao(
                        editor.txtHost.getText(), 
                        editor.txtPorta.getInt(),
                        editor.txtDatabase.getArquivo(),
                        editor.txtUsuario.getText(),
                        editor.txtSenha.getText()
                );
            }
        },
        FIREBIRD {
            private Connection conn;

            @Override
            public String toString() {
                return "Firebird/Interbase";
            }
            
            @Override
            public Connection getConexao() throws Exception {
                return conn;
            }

            @Override
            public void conectar(final SQLEditor editor) throws Exception {
                if (conn != null) {
                    conn.close();
                }
                conn = ConexaoFirebird.getNewConnection(
                        editor.txtHost.getText(), 
                        editor.txtPorta.getInt(),
                        editor.txtDatabase.getArquivo(),
                        editor.txtUsuario.getText(),
                        editor.txtSenha.getText(),
                        editor.txtEncoding.getText()
                );
            }
        },
        SQLSERVER {
            private Connection conn;

            @Override
            public String toString() {
                return "SQL Server";
            }
            
            @Override
            public Connection getConexao() throws Exception {
                return conn;
            }

            @Override
            public void conectar(final SQLEditor editor) throws Exception {
                if (conn != null) {
                    conn.close();
                }
                conn = ConexaoSqlServer.getNewConnection(
                        editor.txtHost.getText(), 
                        editor.txtPorta.getInt(),
                        editor.txtDatabase.getArquivo(),
                        editor.txtUsuario.getText(),
                        editor.txtSenha.getText(),
                        editor.txtEncoding.getText()
                );
            }
        },
        ORACLE {
            private Connection conn;

            @Override
            public String toString() {
                return "Oracle";
            }
            
            @Override
            public Connection getConexao() throws Exception {
                return conn;
            }

            @Override
            public void conectar(final SQLEditor editor) throws Exception {
                if (conn != null) {
                    conn.close();
                }
                conn = ConexaoOracle.getNewConnection(
                        editor.txtHost.getText(), 
                        editor.txtPorta.getInt(),
                        editor.txtDatabase.getArquivo(),
                        editor.txtUsuario.getText(),
                        editor.txtSenha.getText()
                );
            }
        },
        DBF {
            private Connection conn;
            
            @Override
            public String toString() {
                return "DBF";
            }
            
            @Override 
            public void conectar(final SQLEditor editor) throws Exception {
                if (conn != null) {
                    conn.close();
                }
                conn = ConexaoDBF.getNewConnection(
                        editor.txtDatabase.getArquivo()
                );
            }
            
            @Override
            public Connection getConexao() throws Exception {
                return conn;
            }
        };
        
        public abstract Connection getConexao() throws Exception;
        public abstract void conectar(final SQLEditor editor) throws Exception;
        
    }
    

    private Connection conexao;
    
    private void fecharConexao() throws SQLException {
        if (conexao != null) {
            conexao.close();
        }
        conexao = null;
    }
    
    /**
     * Creates new form SQLEditor
     */
    private SQLEditor(VRMdiFrame i_mdiFrame) throws Exception {
        
        super(i_mdiFrame);
        initComponents();        
        
        this.title = "Editor de SQL";
                
        cmbConexoes.setModel(new EnumComboBoxModel(Conexao.class));

        carregarParametros();
        
        centralizarForm();
        this.setMaximum(false);
        
    }

    
    public void validarDadosAcesso() throws Exception {
        
        if (cmbConexoes.getSelectedIndex() == 4) {
            if (txtDatabase.getArquivo().isEmpty()) {
                throw new VRException("Favor informar nome do banco de dados!");
            }
        } else {
            if (txtHost.getText().isEmpty()) {
                throw new VRException("Favor informar host do banco de dados!");
            }
            if (txtPorta.getText().isEmpty()) {
                throw new VRException("Favor informar a porta do banco de dados!");
            }
            if (txtDatabase.getArquivo().isEmpty()) {
                throw new VRException("Favor informar nome do banco de dados!");
            }
            if (txtSenha.getText().isEmpty()) {
                throw new VRException("Favor informar a senha do banco de dados!");
            }
            if (txtUsuario.getText().isEmpty()) {
                throw new VRException("Favor informar o usuário do banco de dados !");
            }
        }

        Conexao selected = (Conexao) cmbConexoes.getSelectedItem();
        selected.conectar(this);
        conexao = selected.getConexao();
                
        gravarParametros();
        
        tabsConn.setSelectedIndex(1);
        
    }
    
    private void carregarParametros() {
        Parametros params = Parametros.get();
        txtHost.setText(params.getWithNull("", SQL_EDITOR, "HOST"));
        txtDatabase.setArquivo(params.getWithNull("", SQL_EDITOR, "DATABASE"));
        txtPorta.setText(params.getWithNull("", SQL_EDITOR, "PORTA"));
        txtUsuario.setText(params.getWithNull("", SQL_EDITOR, "USUARIO"));
        txtSenha.setText(params.getWithNull("", SQL_EDITOR, "SENHA"));
        txtEncoding.setText(params.getWithNull("", SQL_EDITOR, "ENCODING"));
        cmbConexoes.setSelectedIndex(params.getInt(0, SQL_EDITOR, "SGDB"));
    }
    public static final String SQL_EDITOR = "SQL_EDITOR";
    
    private void gravarParametros() throws Exception {
        Parametros params = Parametros.get();
        params.put(txtHost.getText(), SQL_EDITOR, "HOST");
        params.put(txtDatabase.getArquivo(), SQL_EDITOR, "DATABASE");
        params.put(txtPorta.getText(), SQL_EDITOR, "PORTA");
        params.put(txtUsuario.getText(), SQL_EDITOR, "USUARIO");
        params.put(txtSenha.getText(), SQL_EDITOR, "SENHA");
        params.put(txtEncoding.getText(), SQL_EDITOR, "ENCODING");
        params.put(cmbConexoes.getSelectedIndex(), SQL_EDITOR, "SGDB");
        params.salvar();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabsConn = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        btnConectar = new javax.swing.JToggleButton();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        txtUsuario = new vrframework.bean.textField.VRTextField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        tabsConexaoParams = new javax.swing.JTabbedPane();
        pnlConexaoPadrao = new javax.swing.JPanel();
        vRLabel2 = new vrframework.bean.label.VRLabel();
        txtHost = new vrframework.bean.textField.VRTextField();
        vRLabel3 = new vrframework.bean.label.VRLabel();
        txtDatabase = new vrframework.bean.fileChooser.VRFileChooser();
        vRLabel7 = new vrframework.bean.label.VRLabel();
        txtPorta = new vrframework.bean.textField.VRTextField();
        jPanel5 = new javax.swing.JPanel();
        vRLabel26 = new vrframework.bean.label.VRLabel();
        txtStrConexao = new javax.swing.JTextField();
        cmbConexoes = new javax.swing.JComboBox();
        txtEncoding = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel6 = new vrframework.bean.label.VRLabel();
        tabSQL = new javax.swing.JPanel();
        btnExecutar2 = new vrframework.bean.button.VRButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtSQL = new javax.swing.JTextArea();
        tpnResultado = new vrframework.bean.tabbedPane.VRTabbedPane();
        vRPanel15 = new vrframework.bean.panel.VRPanel();
        tblConsulta = new vrframework.bean.tableEx.VRTableEx();
        vRPanel16 = new vrframework.bean.panel.VRPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMensagem = new javax.swing.JTextArea();

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnConectar, "Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        txtSenha.setCaixaAlta(false);
        txtSenha.setMascara("");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel5, "Senha:");

        txtUsuario.setCaixaAlta(false);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel4, "Usuário:");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel2, "Host:");

        txtHost.setCaixaAlta(false);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel3, "Banco de Dados");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel7, "Porta");

        txtPorta.setCaixaAlta(false);

        javax.swing.GroupLayout pnlConexaoPadraoLayout = new javax.swing.GroupLayout(pnlConexaoPadrao);
        pnlConexaoPadrao.setLayout(pnlConexaoPadraoLayout);
        pnlConexaoPadraoLayout.setHorizontalGroup(
            pnlConexaoPadraoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoPadraoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlConexaoPadraoLayout.setVerticalGroup(
            pnlConexaoPadraoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConexaoPadraoLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlConexaoPadraoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9))
        );

        tabsConexaoParams.addTab("Conexão Padrão", pnlConexaoPadrao);

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel26, "String de Conexão");

        txtStrConexao.setText("jdbc:oracle:thin:@10.0.2.250:1521/orcl");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStrConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vRLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStrConexao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabsConexaoParams.addTab("String de Conexão", jPanel5);

        cmbConexoes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtEncoding.setCaixaAlta(false);
        txtEncoding.setMascara("");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel6, "Encoding:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbConexoes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabsConexaoParams)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConectar)
                        .addGap(5, 5, 5)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbConexoes, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabsConexaoParams, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar)
                    .addComponent(vRLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21))
        );

        tabsConn.addTab("Conexão", jPanel1);

        btnExecutar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/sqlexecutar.png"))); // NOI18N
        btnExecutar2.setMnemonic('e');
        btnExecutar2.setToolTipText("Executar (Alt+E)");
        btnExecutar2.setFocusable(false);
        btnExecutar2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExecutar2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExecutar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutar2ActionPerformed(evt);
            }
        });

        txtSQL.setColumns(20);
        txtSQL.setRows(5);
        jScrollPane3.setViewportView(txtSQL);

        javax.swing.GroupLayout tabSQLLayout = new javax.swing.GroupLayout(tabSQL);
        tabSQL.setLayout(tabSQLLayout);
        tabSQLLayout.setHorizontalGroup(
            tabSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabSQLLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabSQLLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(tabSQLLayout.createSequentialGroup()
                        .addComponent(btnExecutar2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        tabSQLLayout.setVerticalGroup(
            tabSQLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabSQLLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(btnExecutar2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsConn.addTab("SQL", tabSQL);

        javax.swing.GroupLayout vRPanel15Layout = new javax.swing.GroupLayout(vRPanel15);
        vRPanel15.setLayout(vRPanel15Layout);
        vRPanel15Layout.setHorizontalGroup(
            vRPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
        );
        vRPanel15Layout.setVerticalGroup(
            vRPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tblConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        );

        tpnResultado.addTab("Saída de Dados", vRPanel15);

        txtMensagem.setEditable(false);
        txtMensagem.setColumns(20);
        txtMensagem.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtMensagem.setRows(5);
        jScrollPane2.setViewportView(txtMensagem);

        javax.swing.GroupLayout vRPanel16Layout = new javax.swing.GroupLayout(vRPanel16);
        vRPanel16.setLayout(vRPanel16Layout);
        vRPanel16Layout.setHorizontalGroup(
            vRPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
        );
        vRPanel16Layout.setVerticalGroup(
            vRPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        );

        tpnResultado.addTab("Mensagens", vRPanel16);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabsConn)
                    .addComponent(tpnResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabsConn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecutar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutar2ActionPerformed
        try {
            this.setWaitCursor();
            executarSQL();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnExecutar2ActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            this.setWaitCursor();

            fecharConexao();

            validarDadosAcesso();
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

        } finally {
            this.setDefaultCursor();
        }
    }//GEN-LAST:event_btnConectarActionPerformed

    private static SQLEditor instance;
    
    public static void exibir(VRMdiFrame i_mdiFrame) {
        try {
            i_mdiFrame.setWaitCursor();
            if (instance == null || instance.isClosed()) {
                instance = new SQLEditor(i_mdiFrame);
            }
            instance.setVisible(true);
        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao abrir");
        } finally {
            i_mdiFrame.setDefaultCursor();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JToggleButton btnConectar;
    protected vrframework.bean.button.VRButton btnExecutar2;
    protected javax.swing.JComboBox cmbConexoes;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel5;
    protected javax.swing.JScrollPane jScrollPane2;
    protected javax.swing.JScrollPane jScrollPane3;
    protected javax.swing.JPanel pnlConexaoPadrao;
    protected javax.swing.JPanel tabSQL;
    protected javax.swing.JTabbedPane tabsConexaoParams;
    protected javax.swing.JTabbedPane tabsConn;
    protected vrframework.bean.tableEx.VRTableEx tblConsulta;
    protected vrframework.bean.tabbedPane.VRTabbedPane tpnResultado;
    protected vrframework.bean.fileChooser.VRFileChooser txtDatabase;
    protected vrframework.bean.passwordField.VRPasswordField txtEncoding;
    protected vrframework.bean.textField.VRTextField txtHost;
    protected javax.swing.JTextArea txtMensagem;
    protected vrframework.bean.textField.VRTextField txtPorta;
    protected javax.swing.JTextArea txtSQL;
    protected vrframework.bean.passwordField.VRPasswordField txtSenha;
    protected javax.swing.JTextField txtStrConexao;
    protected vrframework.bean.textField.VRTextField txtUsuario;
    protected vrframework.bean.label.VRLabel vRLabel2;
    protected vrframework.bean.label.VRLabel vRLabel26;
    protected vrframework.bean.label.VRLabel vRLabel3;
    protected vrframework.bean.label.VRLabel vRLabel4;
    protected vrframework.bean.label.VRLabel vRLabel5;
    protected vrframework.bean.label.VRLabel vRLabel6;
    protected vrframework.bean.label.VRLabel vRLabel7;
    protected vrframework.bean.panel.VRPanel vRPanel15;
    protected vrframework.bean.panel.VRPanel vRPanel16;
    // End of variables declaration//GEN-END:variables

    
}
