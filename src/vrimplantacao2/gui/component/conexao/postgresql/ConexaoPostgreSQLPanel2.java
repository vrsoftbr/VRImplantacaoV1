package vrimplantacao2.gui.component.conexao.postgresql;

import vrimplantacao2.gui.component.conexao.ConexaoEvent;
import javax.swing.ImageIcon;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoPostgres2;
import vrimplantacao2.parametro.Parametros;

/**
 *
 * @author Leandro
 */
public class ConexaoPostgreSQLPanel2 extends javax.swing.JPanel {

    private String sistema;
    private ConexaoPostgres2 conexao = new ConexaoPostgres2();
    private ConexaoEvent onConectar;

    public void setOnConectar(ConexaoEvent onConectar) {
        this.onConectar = onConectar;
    }

    public ConexaoEvent getOnConectar() {
        return onConectar;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }    

    public String getSistema() {
        return sistema;
    }
    
    /**
     * Creates new form ConexaoMySQLPanel
     */
    public ConexaoPostgreSQLPanel2() {
        initComponents();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtUsuario = new vrframework.bean.textField.VRTextField();
        txtSenha = new vrframework.bean.passwordField.VRPasswordField();
        vRLabel4 = new vrframework.bean.label.VRLabel();
        vRLabel5 = new vrframework.bean.label.VRLabel();
        btnConectar = new javax.swing.JToggleButton();
        tabsCon = new javax.swing.JTabbedPane();
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

        setBorder(javax.swing.BorderFactory.createTitledBorder("Conexão PostgreSQL"));

        txtUsuario.setCaixaAlta(false);

        txtSenha.setCaixaAlta(false);
        txtSenha.setMascara("");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel4, "Usuário:");

        org.openide.awt.Mnemonics.setLocalizedText(vRLabel5, "Senha:");

        btnConectar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(btnConectar, "Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

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
                .addComponent(txtDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
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

        tabsCon.addTab("Conexão Padrão", pnlConexaoPadrao);

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
                .addComponent(txtStrConexao, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
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

        tabsCon.addTab("String de Conexão", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(tabsCon)
                .addGap(5, 5, 5))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSenha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConectar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tabsCon, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(vRLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vRLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConectar)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        try {
            if (conexao != null) {
                conexao.close();
            }

            validarDadosAcesso();
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/conectado.png")));

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, "Erro ao conectar");
            btnConectar.setIcon(new ImageIcon(getClass().getResource("/vrframework/img/chat/desconectado.png")));
        }
    }//GEN-LAST:event_btnConectarActionPerformed

    public void validarDadosAcesso() throws Exception {
        if (txtHost.getText().isEmpty()) {
            throw new VRException("Favor informar host do banco de dados PostgreSQL!");
        }
        if (txtPorta.getText().isEmpty()) {
            throw new VRException("Favor informar a porta do banco de dados PostgreSQL!");
        }
        if (txtDatabase.getArquivo().isEmpty()) {
            throw new VRException("Favor informar nome do banco de dados PostgreSQL!");
        }
        if (txtUsuario.getText().isEmpty()) {
            throw new VRException("Favor informar o usuário do banco de dados PostgreSQL!");
        }

        if (tabsCon.getSelectedIndex() == 0) {
            conexao.abrirConexao(txtHost.getText(), txtPorta.getInt(), 
                    txtDatabase.getArquivo(), txtUsuario.getText(), txtSenha.getText());
        } else {
            conexao.abrirConexao(txtStrConexao.getText(), txtUsuario.getText(), txtSenha.getText());
        }
        
        atualizarParametros();

        if (onConectar != null) {
            onConectar.executar();
        }
    }
    
    public void carregarParametros() {
        Parametros params = Parametros.get();
        txtHost.setText(params.getWithNull(host, sistema, "POSTGRES", "HOST"));
        txtDatabase.setArquivo(params.getWithNull(database, sistema, "POSTGRES", "DATABASE"));
        txtPorta.setText(params.getWithNull(port, sistema, "POSTGRES", "PORTA"));
        txtUsuario.setText(params.getWithNull(user, sistema, "POSTGRES", "USUARIO"));
        txtSenha.setText(params.getWithNull(pass, sistema, "POSTGRES", "SENHA"));
    }
    
    public String pass = "postgres";
    public String user = "postgres";
    public String port = "5432";
    public String database = "database";
    public String host = "localhost";
    
    public void atualizarParametros() {
        Parametros params = Parametros.get();
        params.put(txtHost.getText(), sistema, "POSTGRES", "HOST");
        params.put(txtDatabase.getArquivo(), sistema, "POSTGRES", "DATABASE");
        params.put(txtPorta.getText(), sistema, "POSTGRES", "PORTA");
        params.put(txtUsuario.getText(), sistema, "POSTGRES", "USUARIO");
        params.put(txtSenha.getText(), sistema, "POSTGRES", "SENHA");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConectar;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel pnlConexaoPadrao;
    private javax.swing.JTabbedPane tabsCon;
    private vrframework.bean.fileChooser.VRFileChooser txtDatabase;
    private vrframework.bean.textField.VRTextField txtHost;
    private vrframework.bean.textField.VRTextField txtPorta;
    private vrframework.bean.passwordField.VRPasswordField txtSenha;
    private javax.swing.JTextField txtStrConexao;
    private vrframework.bean.textField.VRTextField txtUsuario;
    private vrframework.bean.label.VRLabel vRLabel2;
    private vrframework.bean.label.VRLabel vRLabel26;
    private vrframework.bean.label.VRLabel vRLabel3;
    private vrframework.bean.label.VRLabel vRLabel4;
    private vrframework.bean.label.VRLabel vRLabel5;
    private vrframework.bean.label.VRLabel vRLabel7;
    // End of variables declaration//GEN-END:variables
}
