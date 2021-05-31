package vrimplantacao2.gui.component.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.InterfaceDAO;

/**
 *
 * @author Leandro
 */
public class ChecksFornecedorPanelGUI extends javax.swing.JTabbedPane {

    public Importador importador;
    private Set<OpcaoFornecedor> opt = OpcaoFornecedor.getPadrao();

    public void setImportador(Importador importador) {
        this.importador = importador;
    }
    
    
    public void setOpcoesDisponiveis(InterfaceDAO dao) {
        this.opt = dao.getOpcoesDisponiveisFornecedor();
        tabImportacao.removeAll();
        
        chkFornecedor.setVisible(opt.contains(OpcaoFornecedor.DADOS));
        chkRazao.setVisible(opt.contains(OpcaoFornecedor.RAZAO_SOCIAL));
        chkFantasia.setVisible(opt.contains(OpcaoFornecedor.NOME_FANTASIA));
        chkCnpj.setVisible(opt.contains(OpcaoFornecedor.CNPJ_CPF));
        chkIE.setVisible(opt.contains(OpcaoFornecedor.INSCRICAO_ESTADUAL));
        chkIM.setVisible(opt.contains(OpcaoFornecedor.INSCRICAO_MUNICIPAL));
        chkEndereco.setVisible(opt.contains(OpcaoFornecedor.ENDERECO));
        chkNumero.setVisible(opt.contains(OpcaoFornecedor.NUMERO));
        chkComplemento.setVisible(opt.contains(OpcaoFornecedor.COMPLEMENTO));
        chkBairro.setVisible(opt.contains(OpcaoFornecedor.BAIRRO));
        chkMunicipio.setVisible(opt.contains(OpcaoFornecedor.MUNICIPIO));
        chkMunicipioIbge.setVisible(opt.contains(OpcaoFornecedor.MUNICIPIO));
        chkUf.setVisible(opt.contains(OpcaoFornecedor.UF));
        chkUfIbge.setVisible(opt.contains(OpcaoFornecedor.UF));
        chkCep.setVisible(opt.contains(OpcaoFornecedor.CEP));
        chkTelefone.setVisible(opt.contains(OpcaoFornecedor.TELEFONE));
        chkContatoAdicional.setVisible(opt.contains(OpcaoFornecedor.CONTATOS));
        chkSituacaoCadastro.setVisible(opt.contains(OpcaoFornecedor.SITUACAO_CADASTRO));
        chkPrazoFornecedor.setVisible(opt.contains(OpcaoFornecedor.PRAZO_FORNECEDOR));
        chkCondicaoPagamento.setVisible(opt.contains(OpcaoFornecedor.CONDICAO_PAGAMENTO));
        chkIndicadorIE.setVisible(opt.contains(OpcaoFornecedor.TIPO_INDICADOR_IE));
        chkObservacao.setVisible(opt.contains(OpcaoFornecedor.OBSERVACAO));
        
        tabImportacao.add(pnlDados);
        tabImportacao.add(pnlEndereco);
        tabImportacao.add(pnlContato);
        tabImportacao.add(pnlDadosComplementares);
        
        tabImportacao.revalidate();
    }

    public Set<OpcaoFornecedor> getOpcoesDisponiveis() {
        return opt;
    }
    
    /**
     * Creates new form ChecksFornecedorPanelGUI
     */
    public ChecksFornecedorPanelGUI() {
        super();
        initComponents();
    }
    
    public void importar() throws Exception {
        
        if (chkFornecedor.isSelected()) {
            importador.importarFornecedor();
        }
        
        {
            List<OpcaoFornecedor> opcao = new ArrayList<>();
            
            if (chkRazao.isSelected()) {
                opcao.add(OpcaoFornecedor.RAZAO_SOCIAL);
            }
            if (chkFantasia.isSelected()) {
                opcao.add(OpcaoFornecedor.NOME_FANTASIA);
            }
            if (chkCnpj.isSelected()) {
                opcao.add(OpcaoFornecedor.CNPJ_CPF);
            }
            if (chkIE.isSelected()) {
                opcao.add(OpcaoFornecedor.INSCRICAO_ESTADUAL);
            }
            if (chkIM.isSelected()) {
                opcao.add(OpcaoFornecedor.INSCRICAO_MUNICIPAL);
            }
            if (chkEndereco.isSelected()) {
                opcao.add(OpcaoFornecedor.ENDERECO);
            }
            if (chkNumero.isSelected()) {
                opcao.add(OpcaoFornecedor.NUMERO);
            }
            if (chkComplemento.isSelected()) {
                opcao.add(OpcaoFornecedor.COMPLEMENTO);
            }
            if (chkBairro.isSelected()) {
                opcao.add(OpcaoFornecedor.BAIRRO);
            }
            if (chkMunicipio.isSelected() || chkMunicipioIbge.isSelected()) {
                opcao.add(OpcaoFornecedor.MUNICIPIO);
            }
            if (chkUf.isSelected() || chkUfIbge.isSelected()) {
                opcao.add(OpcaoFornecedor.UF);
            }
            if (chkCep.isSelected()) {
                opcao.add(OpcaoFornecedor.CEP);
            }
            if (chkTelefone.isSelected()) {
                opcao.add(OpcaoFornecedor.TELEFONE);
            }
            if (chkContatoAdicional.isSelected()) {
                opcao.add(OpcaoFornecedor.CONTATOS);
            }
            if (chkSituacaoCadastro.isSelected()) {
                opcao.add(OpcaoFornecedor.SITUACAO_CADASTRO);
            }
            if (chkPrazoFornecedor.isSelected()) {
                opcao.add(OpcaoFornecedor.PRAZO_FORNECEDOR);
            }
            if (chkCondicaoPagamento.isSelected()) {
                opcao.add(OpcaoFornecedor.CONDICAO_PAGAMENTO);
            }
            if (chkIndicadorIE.isSelected()) {
                opcao.add(OpcaoFornecedor.TIPO_INDICADOR_IE);
            }
            if (chkObservacao.isSelected()) {
                opcao.add(OpcaoFornecedor.OBSERVACAO);
            }
            
            if (!opcao.isEmpty()) {
                importador.atualizarFornecedor(opcao.toArray(new OpcaoFornecedor[]{}));
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
        chkFornecedor = new vrframework.bean.checkBox.VRCheckBox();
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
        pnlDadosComplementares = new vrframework.bean.panel.VRPanel();
        jLabel7 = new javax.swing.JLabel();
        chkSituacaoCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkObservacao = new vrframework.bean.checkBox.VRCheckBox();
        chkPrazoFornecedor = new vrframework.bean.checkBox.VRCheckBox();
        chkCondicaoPagamento = new vrframework.bean.checkBox.VRCheckBox();
        chkIndicadorIE = new vrframework.bean.checkBox.VRCheckBox();
        pnlContato = new vrframework.bean.panel.VRPanel();
        jLabel8 = new javax.swing.JLabel();
        chkTelefone = new vrframework.bean.checkBox.VRCheckBox();
        chkContatoAdicional = new vrframework.bean.checkBox.VRCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(vRCheckBox3, "vRCheckBox3");

        scrollImportação.setBorder(null);

        tabImportacao.setLayout(null);

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

        org.openide.awt.Mnemonics.setLocalizedText(chkFornecedor, "Fornecedor");
        chkFornecedor.setEnabled(true);

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
                        .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(96, Short.MAX_VALUE))
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
                    .addComponent(chkFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabImportacao.add(pnlDados);
        pnlDados.setBounds(0, 0, 590, 57);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
                .addComponent(chkMunicipioIbge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkUfIbge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
            .addGroup(pnlEnderecoLayout.createSequentialGroup()
                .addGroup(pnlEnderecoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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

        tabImportacao.add(pnlEndereco);
        pnlEndereco.setBounds(0, 60, 590, 80);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "DADOS COMPLEMENTARES");

        org.openide.awt.Mnemonics.setLocalizedText(chkSituacaoCadastro, "Situação Cadastro");
        chkSituacaoCadastro.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkObservacao, "Observação");
        chkObservacao.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkPrazoFornecedor, "Prazo Fornecedor");
        chkPrazoFornecedor.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkCondicaoPagamento, "Condição Pagamento");
        chkCondicaoPagamento.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkIndicadorIE, "Indicador IE");

        javax.swing.GroupLayout pnlDadosComplementaresLayout = new javax.swing.GroupLayout(pnlDadosComplementares);
        pnlDadosComplementares.setLayout(pnlDadosComplementaresLayout);
        pnlDadosComplementaresLayout.setHorizontalGroup(
            pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIndicadorIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel7)))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        pnlDadosComplementaresLayout.setVerticalGroup(
            pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkPrazoFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkCondicaoPagamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkIndicadorIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        tabImportacao.add(pnlDadosComplementares);
        pnlDadosComplementares.setBounds(0, 200, 590, 60);

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
                .addContainerGap(404, Short.MAX_VALUE))
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

        tabImportacao.add(pnlContato);
        pnlContato.setBounds(0, 140, 590, 60);

        scrollImportação.setViewportView(tabImportacao);

        addTab("Importação de Fornecedores", scrollImportação);

        getAccessibleContext().setAccessibleName("Importação de Fornecedores");
    }// </editor-fold>//GEN-END:initComponents

    private void btnMapaTributActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMapaTributActionPerformed
        
    }//GEN-LAST:event_btnMapaTributActionPerformed

    private void chkMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMunicipioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMunicipioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.ButtonGroup btgPautaFiscal;
    public vrframework.bean.checkBox.VRCheckBox chkBairro;
    public vrframework.bean.checkBox.VRCheckBox chkCep;
    public vrframework.bean.checkBox.VRCheckBox chkCnpj;
    public vrframework.bean.checkBox.VRCheckBox chkComplemento;
    public vrframework.bean.checkBox.VRCheckBox chkCondicaoPagamento;
    public vrframework.bean.checkBox.VRCheckBox chkContatoAdicional;
    public vrframework.bean.checkBox.VRCheckBox chkEndereco;
    public vrframework.bean.checkBox.VRCheckBox chkFantasia;
    public vrframework.bean.checkBox.VRCheckBox chkFornecedor;
    public vrframework.bean.checkBox.VRCheckBox chkIE;
    public vrframework.bean.checkBox.VRCheckBox chkIM;
    public vrframework.bean.checkBox.VRCheckBox chkIndicadorIE;
    public vrframework.bean.checkBox.VRCheckBox chkMunicipio;
    public vrframework.bean.checkBox.VRCheckBox chkMunicipioIbge;
    public vrframework.bean.checkBox.VRCheckBox chkNumero;
    public vrframework.bean.checkBox.VRCheckBox chkObservacao;
    public vrframework.bean.checkBox.VRCheckBox chkPrazoFornecedor;
    public vrframework.bean.checkBox.VRCheckBox chkRazao;
    public vrframework.bean.checkBox.VRCheckBox chkSituacaoCadastro;
    public vrframework.bean.checkBox.VRCheckBox chkTelefone;
    public vrframework.bean.checkBox.VRCheckBox chkUf;
    public vrframework.bean.checkBox.VRCheckBox chkUfIbge;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    public vrframework.bean.panel.VRPanel pnlContato;
    public vrframework.bean.panel.VRPanel pnlDados;
    public vrframework.bean.panel.VRPanel pnlDadosComplementares;
    public vrframework.bean.panel.VRPanel pnlEndereco;
    public javax.swing.JScrollPane scrollImportação;
    public vrframework.bean.panel.VRPanel tabImportacao;
    public vrframework.bean.checkBox.VRCheckBox vRCheckBox3;
    // End of variables declaration//GEN-END:variables

}
