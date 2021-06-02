package vrimplantacao2.gui.component.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.InterfaceDAO;

/**
 *
 * @author Leandro
 */
public class ChecksClientePanelGUI extends javax.swing.JTabbedPane {

    public Importador importador;
    private Set<OpcaoCliente> opt = OpcaoCliente.getPadrao();

    public void setImportador(Importador importador) {
        this.importador = importador;
    }
    
    
    public void setOpcoesDisponiveis(InterfaceDAO dao) {
        this.opt = dao.getOpcoesDisponiveisCliente();
        tabImportacao.removeAll();
                
        if (opt.contains(OpcaoCliente.DADOS)
                || opt.contains(OpcaoCliente.RAZAO)
                || opt.contains(OpcaoCliente.FANTASIA)
                || opt.contains(OpcaoCliente.CNPJ)
                || opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL)) {

            chkClientePreferencial.setVisible(opt.contains(OpcaoCliente.DADOS));
            chkRazao.setVisible(opt.contains(OpcaoCliente.RAZAO));
            chkFantasia.setVisible(opt.contains(OpcaoCliente.FANTASIA));
            chkCnpj.setVisible(opt.contains(OpcaoCliente.CNPJ));
            chkIE.setVisible(opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL));

            tabImportacao.add(pnlDados);
        }
        
        if (opt.contains(OpcaoCliente.ENDERECO)
                || opt.contains(OpcaoCliente.NUMERO)
                || opt.contains(OpcaoCliente.COMPLEMENTO)
                || opt.contains(OpcaoCliente.BAIRRO)
                || opt.contains(OpcaoCliente.MUNICIPIO)
                || opt.contains(OpcaoCliente.UF)
                || opt.contains(OpcaoCliente.CEP)) {

            chkEndereco.setVisible(opt.contains(OpcaoCliente.ENDERECO));
            chkNumero.setVisible(opt.contains(OpcaoCliente.NUMERO));
            chkComplemento.setVisible(opt.contains(OpcaoCliente.COMPLEMENTO));
            chkBairro.setVisible(opt.contains(OpcaoCliente.BAIRRO));
            chkMunicipio.setVisible(opt.contains(OpcaoCliente.MUNICIPIO));
            chkMunicipioIbge.setVisible(opt.contains(OpcaoCliente.MUNICIPIO));
            chkUf.setVisible(opt.contains(OpcaoCliente.UF));
            chkUfIbge.setVisible(opt.contains(OpcaoCliente.UF));
            chkCep.setVisible(opt.contains(OpcaoCliente.CEP));

            tabImportacao.add(pnlEndereco);
        } else {
            pnlEndereco.setVisible(false);
        }
        
        if (opt.contains(OpcaoCliente.TELEFONE)
                || opt.contains(OpcaoCliente.CONTATOS)) {

            chkTelefone.setVisible(opt.contains(OpcaoCliente.TELEFONE));
            chkContatoAdicional.setVisible(opt.contains(OpcaoCliente.CONTATOS));

            tabImportacao.add(pnlContato);
        } else {
            pnlContato.setVisible(false);
        }

        tabImportacao.revalidate();
    }

    public Set<OpcaoCliente> getOpcoesDisponiveis() {
        return opt;
    }
    
    /**
     * Creates new form ChecksFornecedorPanelGUI
     */
    public ChecksClientePanelGUI() {
        super();
        initComponents();
    }
    
    public void importar() throws Exception {
        
        if (chkClientePreferencial.isSelected()) {
            importador.importarClientePreferencial();
        }
        
        {
            List<OpcaoCliente> opcao = new ArrayList<>();
            
            if (chkRazao.isSelected()) {
                opcao.add(OpcaoCliente.RAZAO);
            }
            if (chkFantasia.isSelected()) {
                opcao.add(OpcaoCliente.FANTASIA);
            }
            if (chkCnpj.isSelected()) {
                opcao.add(OpcaoCliente.CNPJ);
            }
            if (chkIE.isSelected()) {
                opcao.add(OpcaoCliente.INSCRICAO_ESTADUAL);
            }
            if (chkEndereco.isSelected()) {
                opcao.add(OpcaoCliente.ENDERECO);
            }
            if (chkNumero.isSelected()) {
                opcao.add(OpcaoCliente.NUMERO);
            }
            if (chkComplemento.isSelected()) {
                opcao.add(OpcaoCliente.COMPLEMENTO);
            }
            if (chkBairro.isSelected()) {
                opcao.add(OpcaoCliente.BAIRRO);
            }
            if (chkMunicipio.isSelected() || chkMunicipioIbge.isSelected()) {
                opcao.add(OpcaoCliente.MUNICIPIO);
            }
            if (chkUf.isSelected() || chkUfIbge.isSelected()) {
                opcao.add(OpcaoCliente.UF);
            }
            if (chkCep.isSelected()) {
                opcao.add(OpcaoCliente.CEP);
            }
            if (chkTelefone.isSelected()) {
                opcao.add(OpcaoCliente.TELEFONE);
            }
            if (chkContatoAdicional.isSelected()) {
                opcao.add(OpcaoCliente.CONTATOS);
            }
            
            if (!opcao.isEmpty()) {
                importador.atualizarClientePreferencial(opcao.toArray(new OpcaoCliente[]{}));
            }
        }
        
    }
    
    public void executarImportacao() throws Exception {
        importar();
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btgPautaFiscal = new javax.swing.ButtonGroup();
        vRCheckBox3 = new vrframework.bean.checkBox.VRCheckBox();
        scrollImportação = new javax.swing.JScrollPane();
        tabImportacao = new vrframework.bean.panel.VRPanel();
        pnlDados = new vrframework.bean.panel.VRPanel();
        jLabel5 = new javax.swing.JLabel();
        chkRazao = new vrframework.bean.checkBox.VRCheckBox();
        chkFantasia = new vrframework.bean.checkBox.VRCheckBox();
        chkCnpj = new vrframework.bean.checkBox.VRCheckBox();
        chkIE = new vrframework.bean.checkBox.VRCheckBox();
        chkIM = new vrframework.bean.checkBox.VRCheckBox();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        pnlEndereco = new vrframework.bean.panel.VRPanel();
        jLabel6 = new javax.swing.JLabel();
        chkEndereco = new vrframework.bean.checkBox.VRCheckBox();
        chkNumero = new vrframework.bean.checkBox.VRCheckBox();
        chkComplemento = new vrframework.bean.checkBox.VRCheckBox();
        chkBairro = new vrframework.bean.checkBox.VRCheckBox();
        chkUf = new vrframework.bean.checkBox.VRCheckBox();
        chkMunicipio = new vrframework.bean.checkBox.VRCheckBox();
        chkMunicipioIbge = new vrframework.bean.checkBox.VRCheckBox();
        chkUfIbge = new vrframework.bean.checkBox.VRCheckBox();
        chkCep = new vrframework.bean.checkBox.VRCheckBox();
        pnlContato = new vrframework.bean.panel.VRPanel();
        jLabel8 = new javax.swing.JLabel();
        chkTelefone = new vrframework.bean.checkBox.VRCheckBox();
        chkContatoAdicional = new vrframework.bean.checkBox.VRCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(vRCheckBox3, "vRCheckBox3");

        scrollImportação.setBorder(null);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "DADOS");

        org.openide.awt.Mnemonics.setLocalizedText(chkRazao, "Razão");
        chkRazao.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkFantasia, "Fantasia");

        org.openide.awt.Mnemonics.setLocalizedText(chkCnpj, "CNPJ");
        chkCnpj.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkIE, "Inscrição Estadual");
        chkIE.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkIM, "Inscrição Municipal");
        chkIM.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkClientePreferencial, "Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);

        javax.swing.GroupLayout pnlDadosLayout = new javax.swing.GroupLayout(pnlDados);
        pnlDados.setLayout(pnlDadosLayout);
        pnlDadosLayout.setHorizontalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel5))
                    .addGroup(pnlDadosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkRazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(415, Short.MAX_VALUE))
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkRazao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "ENDEREÇO");

        org.openide.awt.Mnemonics.setLocalizedText(chkEndereco, "Endereço");
        chkEndereco.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkNumero, "Número");

        org.openide.awt.Mnemonics.setLocalizedText(chkComplemento, "Complemento");
        chkComplemento.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkBairro, "Bairro");
        chkBairro.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkUf, "UF");
        chkUf.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkMunicipio, "Município");
        chkMunicipio.setToolTipText("Corrige o relacionamento entre o produto e a família.");
        chkMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMunicipioActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(chkMunicipioIbge, "Município IBGE");
        chkMunicipioIbge.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkUfIbge, "UF IBGE");
        chkUfIbge.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkCep, "Cep");
        chkCep.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        javax.swing.GroupLayout pnlEnderecoLayout = new javax.swing.GroupLayout(pnlEndereco);
        pnlEndereco.setLayout(pnlEnderecoLayout);
        pnlEnderecoLayout.setHorizontalGroup(
            pnlEnderecoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEnderecoLayout.createSequentialGroup()
                .addGroup(pnlEnderecoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEnderecoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkMunicipioIbge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUfIbge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlEnderecoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel6))
                    .addGroup(pnlEnderecoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlEnderecoLayout.setVerticalGroup(
            pnlEnderecoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEnderecoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEnderecoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUfIbge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkMunicipioIbge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, "CONTATO");

        org.openide.awt.Mnemonics.setLocalizedText(chkTelefone, "Telefone");
        chkTelefone.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkContatoAdicional, "Contato Adicional");
        chkContatoAdicional.setEnabled(true);

        javax.swing.GroupLayout pnlContatoLayout = new javax.swing.GroupLayout(pnlContato);
        pnlContato.setLayout(pnlContatoLayout);
        pnlContatoLayout.setHorizontalGroup(
            pnlContatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContatoLayout.createSequentialGroup()
                .addGroup(pnlContatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlContatoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkContatoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlContatoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel8)))
                .addContainerGap(761, Short.MAX_VALUE))
        );
        pnlContatoLayout.setVerticalGroup(
            pnlContatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContatoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlContatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkContatoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout tabImportacaoLayout = new javax.swing.GroupLayout(tabImportacao);
        tabImportacao.setLayout(tabImportacaoLayout);
        tabImportacaoLayout.setHorizontalGroup(
            tabImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImportacaoLayout.createSequentialGroup()
                .addGroup(tabImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlDados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlEndereco, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlContato, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        tabImportacaoLayout.setVerticalGroup(
            tabImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImportacaoLayout.createSequentialGroup()
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(pnlEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlContato, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );

        scrollImportação.setViewportView(tabImportacao);

        addTab("Importação de Cliente", scrollImportação);

        getAccessibleContext().setAccessibleName("Importação de Fornecedores");
    }// </editor-fold>//GEN-END:initComponents

    private void chkMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMunicipioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMunicipioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.ButtonGroup btgPautaFiscal;
    public vrframework.bean.checkBox.VRCheckBox chkBairro;
    public vrframework.bean.checkBox.VRCheckBox chkCep;
    public vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    public vrframework.bean.checkBox.VRCheckBox chkCnpj;
    public vrframework.bean.checkBox.VRCheckBox chkComplemento;
    public vrframework.bean.checkBox.VRCheckBox chkContatoAdicional;
    public vrframework.bean.checkBox.VRCheckBox chkEndereco;
    public vrframework.bean.checkBox.VRCheckBox chkFantasia;
    public vrframework.bean.checkBox.VRCheckBox chkIE;
    public vrframework.bean.checkBox.VRCheckBox chkIM;
    public vrframework.bean.checkBox.VRCheckBox chkMunicipio;
    public vrframework.bean.checkBox.VRCheckBox chkMunicipioIbge;
    public vrframework.bean.checkBox.VRCheckBox chkNumero;
    public vrframework.bean.checkBox.VRCheckBox chkRazao;
    public vrframework.bean.checkBox.VRCheckBox chkTelefone;
    public vrframework.bean.checkBox.VRCheckBox chkUf;
    public vrframework.bean.checkBox.VRCheckBox chkUfIbge;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel8;
    public vrframework.bean.panel.VRPanel pnlContato;
    public vrframework.bean.panel.VRPanel pnlDados;
    public vrframework.bean.panel.VRPanel pnlEndereco;
    public javax.swing.JScrollPane scrollImportação;
    public vrframework.bean.panel.VRPanel tabImportacao;
    public vrframework.bean.checkBox.VRCheckBox vRCheckBox3;
    // End of variables declaration//GEN-END:variables

}
