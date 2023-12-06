package vrimplantacao2.gui.component.checks;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import vrframework.bean.panel.VRPanel;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.interfaces.Importador;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2_5.controller.interfaces.InterfaceController;

public class ChecksClientePanelGUI extends javax.swing.JTabbedPane {

    public Importador importador;
    private Set<OpcaoCliente> opt = OpcaoCliente.getPadrao();

    public void setImportador(Importador importador) {
        this.importador = importador;
    }

    public void setOpcoesDisponiveis(InterfaceDAO dao) {
        this.opt = dao.getOpcoesDisponiveisCliente();
        tabImportacao.removeAll();
        tabCreditoRotativo.removeAll();
        tabCheque.removeAll();

        if (opt.contains(OpcaoCliente.IMPORTAR_SOMENTE_ATIVO)) {
            chkImportarClienteAtivo.setVisible(true);
        } else {
            chkImportarClienteAtivo.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.DADOS)
                || opt.contains(OpcaoCliente.RAZAO)
                || opt.contains(OpcaoCliente.FANTASIA)
                || opt.contains(OpcaoCliente.CNPJ)
                || opt.contains(OpcaoCliente.TIPO_INSCRICAO)
                || opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL)) {

            chkClientePreferencial.setVisible(opt.contains(OpcaoCliente.DADOS));
            chkClienteEventual.setVisible(opt.contains(OpcaoCliente.DADOS));
            chkNome.setVisible(opt.contains(OpcaoCliente.RAZAO));
            chkCnpj.setVisible(opt.contains(OpcaoCliente.CNPJ));
            chkIE.setVisible(opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL));
            chkTipoInscricao.setVisible(opt.contains(OpcaoCliente.TIPO_INSCRICAO));

            tabImportacao.add(pnlDados);
        } else {
            pnlDados.setVisible(false);
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
                || opt.contains(OpcaoCliente.CELULAR)
                || opt.contains(OpcaoCliente.EMAIL)
                || opt.contains(OpcaoCliente.CONTATOS)) {

            chkTelefone.setVisible(opt.contains(OpcaoCliente.TELEFONE));
            chkCelular.setVisible(opt.contains(OpcaoCliente.CELULAR));
            chkEmail.setVisible(opt.contains(OpcaoCliente.EMAIL));
            chkContatoAdicional.setVisible(opt.contains(OpcaoCliente.CONTATOS));

            tabImportacao.add(pnlContato);
        } else {
            pnlContato.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.DATA_CADASTRO)
                || opt.contains(OpcaoCliente.SITUACAO_CADASTRO)
                || opt.contains(OpcaoCliente.BLOQUEADO)
                || opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO)
                || opt.contains(OpcaoCliente.PERMITE_CHEQUE)
                || opt.contains(OpcaoCliente.VALOR_LIMITE)
                || opt.contains(OpcaoCliente.NOME_PAI)
                || opt.contains(OpcaoCliente.NOME_MAE)
                || opt.contains(OpcaoCliente.NOME_CONJUGE)
                || opt.contains(OpcaoCliente.DATA_NASCIMENTO)
                || opt.contains(OpcaoCliente.OBSERVACOES)
                || opt.contains(OpcaoCliente.OBSERVACOES2)) {

            chkDataCadastro.setVisible(opt.contains(OpcaoCliente.DATA_CADASTRO));
            chkSituacaoCadastro.setVisible(opt.contains(OpcaoCliente.SITUACAO_CADASTRO));
            chkBloqueado.setVisible(opt.contains(OpcaoCliente.BLOQUEADO));
            chkPermiteCreditoRotativo.setVisible(opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO));
            chkPermiteCheque.setVisible(opt.contains(OpcaoCliente.PERMITE_CHEQUE));
            chkValorLimite.setVisible(opt.contains(OpcaoCliente.VALOR_LIMITE));
            chkNomePai.setVisible(opt.contains(OpcaoCliente.NOME_PAI));
            chkNomeMae.setVisible(opt.contains(OpcaoCliente.NOME_MAE));
            chkNomeConjuge.setVisible(opt.contains(OpcaoCliente.NOME_CONJUGE));
            chkDataNascimento.setVisible(opt.contains(OpcaoCliente.DATA_NASCIMENTO));
            chkObservacao.setVisible(opt.contains(OpcaoCliente.OBSERVACOES));
            chkObservacao2.setVisible(opt.contains(OpcaoCliente.OBSERVACOES2));
            chkSexo.setVisible(opt.contains(OpcaoCliente.SEXO));
            chkEstadoCivil.setVisible(opt.contains(OpcaoCliente.ESTADO_CIVIL));

            tabImportacao.add(pnlDadosComplementares);

        } else {
            pnlDadosComplementares.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.EMPRESA)
                || opt.contains(OpcaoCliente.CARGO)
                || opt.contains(OpcaoCliente.DATA_ADMISSAO)
                || opt.contains(OpcaoCliente.SALARIO)) {

            chkEmpresa.setVisible(opt.contains(OpcaoCliente.EMPRESA));
            chkCargo.setVisible(opt.contains(OpcaoCliente.CARGO));
            chkDataAdmissao.setVisible(opt.contains(OpcaoCliente.DATA_ADMISSAO));
            chkSalario.setVisible(opt.contains(OpcaoCliente.SALARIO));

            tabImportacao.add(pnlDadosEmpresa);
        } else {
            pnlDadosEmpresa.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.CLIENTE_EVENTUAL)) {
            chkClienteEventual.setVisible(true);
        } else {
            this.remove(tabClienteEventual);
        }

        if (opt.contains(OpcaoCliente.RECEBER_CREDITOROTATIVO)) {
            chkCreditoRotativo.setVisible(opt.contains(OpcaoCliente.RECEBER_CREDITOROTATIVO));
            tabCreditoRotativo.add(pnlCreditoRotativo);
        } else {
            pnlCreditoRotativo.setVisible(false);
            this.remove(tabCreditoRotativo);
        }

        if (opt.contains(OpcaoCliente.OUTRAS_RECEITAS)) {
            chkOutrasReceitas.setVisible(opt.contains(OpcaoCliente.OUTRAS_RECEITAS));
            tabCreditoRotativo.add(pnlCreditoRotativo);
        } else {
            pnlCreditoRotativo.setVisible(true);
        }

        if (opt.contains(OpcaoCliente.RECEBER_CHEQUE)) {
            chkCheque.setVisible(opt.contains(OpcaoCliente.RECEBER_CHEQUE));
            tabCheque.add(pnlCheque);
        } else {
            pnlCheque.setVisible(false);
            this.remove(tabCheque);
        }

        if (opt.contains(OpcaoCliente.CONVENIO_CONVENIADO)) {
            chkConveniado.setVisible(true);
            chkConveniadoEmpresa.setVisible(true);
            chkConveniadoTransacao.setVisible(true);
            tabConvenio.add(pnlConvenio);
        } else {
            pnlConvenio.setVisible(false);
            this.remove(tabConvenio);
        }

        tabImportacao.revalidate();
        tabCreditoRotativo.revalidate();
        tabCheque.revalidate();
    }

    public void setOpcoesDisponiveis(InterfaceController controller) {
        this.opt = controller.getOpcoesDisponiveisCliente();
        tabImportacao.removeAll();
        tabCreditoRotativo.removeAll();
        tabCheque.removeAll();

        if (opt.contains(OpcaoCliente.IMPORTAR_SOMENTE_ATIVO)) {
            chkImportarClienteAtivo.setVisible(true);
        } else {
            chkImportarClienteAtivo.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.DADOS)
                || opt.contains(OpcaoCliente.RAZAO)
                || opt.contains(OpcaoCliente.FANTASIA)
                || opt.contains(OpcaoCliente.CNPJ)
                || opt.contains((OpcaoCliente.TIPO_INSCRICAO))
                || opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL)) {

            chkClientePreferencial.setVisible(opt.contains(OpcaoCliente.DADOS));
            chkClienteEventual.setVisible(opt.contains(OpcaoCliente.DADOS));
            chkNome.setVisible(opt.contains(OpcaoCliente.RAZAO));
            chkCnpj.setVisible(opt.contains(OpcaoCliente.CNPJ));
            chkTipoInscricao.setVisible(opt.contains((OpcaoCliente.TIPO_INSCRICAO)));
            chkIE.setVisible(opt.contains(OpcaoCliente.INSCRICAO_ESTADUAL));

            tabImportacao.add(pnlDados);
        } else {
            pnlDados.setVisible(false);
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
                || opt.contains(OpcaoCliente.CELULAR)
                || opt.contains(OpcaoCliente.EMAIL)
                || opt.contains(OpcaoCliente.CONTATOS)) {

            chkTelefone.setVisible(opt.contains(OpcaoCliente.TELEFONE));
            chkCelular.setVisible(opt.contains(OpcaoCliente.CELULAR));
            chkEmail.setVisible(opt.contains(OpcaoCliente.EMAIL));
            chkContatoAdicional.setVisible(opt.contains(OpcaoCliente.CONTATOS));

            tabImportacao.add(pnlContato);
        } else {
            pnlContato.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.DATA_CADASTRO)
                || opt.contains(OpcaoCliente.SITUACAO_CADASTRO)
                || opt.contains(OpcaoCliente.BLOQUEADO)
                || opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO)
                || opt.contains(OpcaoCliente.PERMITE_CHEQUE)
                || opt.contains(OpcaoCliente.VALOR_LIMITE)
                || opt.contains(OpcaoCliente.VENCIMENTO_ROTATIVO)
                || opt.contains(OpcaoCliente.NOME_PAI)
                || opt.contains(OpcaoCliente.NOME_MAE)
                || opt.contains(OpcaoCliente.NOME_CONJUGE)
                || opt.contains(OpcaoCliente.DATA_NASCIMENTO)
                || opt.contains(OpcaoCliente.OBSERVACOES)
                || opt.contains(OpcaoCliente.OBSERVACOES2)) {

            chkDataCadastro.setVisible(opt.contains(OpcaoCliente.DATA_CADASTRO));
            chkSituacaoCadastro.setVisible(opt.contains(OpcaoCliente.SITUACAO_CADASTRO));
            chkBloqueado.setVisible(opt.contains(OpcaoCliente.BLOQUEADO));
            chkPermiteCreditoRotativo.setVisible(opt.contains(OpcaoCliente.PERMITE_CREDITOROTATIVO));
            chkPermiteCheque.setVisible(opt.contains(OpcaoCliente.PERMITE_CHEQUE));
            chkValorLimite.setVisible(opt.contains(OpcaoCliente.VALOR_LIMITE));
            chkVencimento.setVisible(opt.contains(OpcaoCliente.VENCIMENTO_ROTATIVO));
            chkNomePai.setVisible(opt.contains(OpcaoCliente.NOME_PAI));
            chkNomeMae.setVisible(opt.contains(OpcaoCliente.NOME_MAE));
            chkNomeConjuge.setVisible(opt.contains(OpcaoCliente.NOME_CONJUGE));
            chkDataNascimento.setVisible(opt.contains(OpcaoCliente.DATA_NASCIMENTO));
            chkObservacao.setVisible(opt.contains(OpcaoCliente.OBSERVACOES));
            chkObservacao2.setVisible(opt.contains(OpcaoCliente.OBSERVACOES2));
            chkSexo.setVisible(opt.contains(OpcaoCliente.SEXO));
            chkEstadoCivil.setVisible(opt.contains(OpcaoCliente.ESTADO_CIVIL));

            tabImportacao.add(pnlDadosComplementares);

        } else {
            pnlDadosComplementares.setVisible(false);
        }

        if (opt.contains(OpcaoCliente.EMPRESA)
                || opt.contains(OpcaoCliente.CARGO)
                || opt.contains(OpcaoCliente.DATA_ADMISSAO)
                || opt.contains(OpcaoCliente.SALARIO)) {

            chkEmpresa.setVisible(opt.contains(OpcaoCliente.EMPRESA));
            chkCargo.setVisible(opt.contains(OpcaoCliente.CARGO));
            chkDataAdmissao.setVisible(opt.contains(OpcaoCliente.DATA_ADMISSAO));
            chkSalario.setVisible(opt.contains(OpcaoCliente.SALARIO));

            tabImportacao.add(pnlDadosEmpresa);
        } else {
            pnlDadosEmpresa.setVisible(false);
        }

        if (!opt.contains(OpcaoCliente.DADOS)) {
            this.remove(scrollImportação);
        }

        if (opt.contains(OpcaoCliente.CLIENTE_EVENTUAL)) {
            chkClienteEventual.setVisible(true);
        } else {
            this.remove(tabClienteEventual);
        }

        if (opt.contains(OpcaoCliente.RECEBER_CREDITOROTATIVO)) {
            chkCreditoRotativo.setVisible(opt.contains(OpcaoCliente.RECEBER_CREDITOROTATIVO));
            tabCreditoRotativo.add(pnlCreditoRotativo);
        } else {
            pnlCreditoRotativo.setVisible(false);
            this.remove(tabCreditoRotativo);
        }

        if (opt.contains(OpcaoCliente.OUTRAS_RECEITAS)) {
            chkOutrasReceitas.setVisible(opt.contains(OpcaoCliente.OUTRAS_RECEITAS));
            tabCreditoRotativo.add(pnlCreditoRotativo);
        } else {
            pnlCreditoRotativo.setVisible(false);
            this.remove(tabCreditoRotativo);
        }

        if (opt.contains(OpcaoCliente.RECEBER_CHEQUE)) {
            chkCheque.setVisible(opt.contains(OpcaoCliente.RECEBER_CHEQUE));
            tabCheque.add(pnlCheque);
        } else {
            pnlCheque.setVisible(false);
            this.remove(tabCheque);
        }

        tabImportacao.revalidate();
        tabCreditoRotativo.revalidate();
        tabCheque.revalidate();
    }

    public Set<OpcaoCliente> getOpcoesDisponiveis() {
        return opt;
    }

    /**
     * Creates new form ChecksClientePanelGUI
     */
    public ChecksClientePanelGUI() {
        super();
        initComponents();
    }

    public void importar() throws Exception {

        List<OpcaoCliente> opcao = new ArrayList<>();

        if (chkForcarUnificacao.isSelected()) {
            opcao.add(OpcaoCliente.FORCAR_UNIFICACAO);
        }

        if (chkImportarClienteAtivo.isSelected()) {
            opcao.add(OpcaoCliente.IMPORTAR_SOMENTE_ATIVO);
        }

        if (chkClientePreferencial.isSelected()) {
            importador.importarClientePreferencial(opcao.toArray(new OpcaoCliente[]{}));
        }
        
        if (chkPessoaImpM5_0.isSelected()) {
            importador.importarPessoaImp();
        }

        if (chkClienteEventual.isSelected()) {
            importador.importarClienteEventual(opcao.toArray(new OpcaoCliente[]{}));
        }

        if (chkNome.isSelected()) {
            opcao.add(OpcaoCliente.RAZAO);
        }
        if (chkCnpj.isSelected()) {
            opcao.add(OpcaoCliente.CNPJ);
        }
        if (chkIE.isSelected()) {
            opcao.add(OpcaoCliente.INSCRICAO_ESTADUAL);
        }
        if(chkTipoInscricao.isSelected()){
            opcao.add(OpcaoCliente.TIPO_INSCRICAO);
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
        if (chkCelular.isSelected()) {
            opcao.add(OpcaoCliente.CELULAR);
        }
        if (chkEmail.isSelected()) {
            opcao.add(OpcaoCliente.EMAIL);
        }
        if (chkContatoAdicional.isSelected()) {
            opcao.add(OpcaoCliente.CONTATOS);
        }
        if (chkDataCadastro.isSelected()) {
            opcao.add(OpcaoCliente.DATA_CADASTRO);
        }
        if (chkSituacaoCadastro.isSelected()) {
            opcao.add(OpcaoCliente.SITUACAO_CADASTRO);
        }
        if (chkBloqueado.isSelected()) {
            opcao.add(OpcaoCliente.BLOQUEADO);
        }
        if (chkPermiteCreditoRotativo.isSelected()) {
            opcao.add(OpcaoCliente.PERMITE_CREDITOROTATIVO);
        }
        if (chkPermiteCheque.isSelected()) {
            opcao.add(OpcaoCliente.PERMITE_CHEQUE);
        }
        if (chkValorLimite.isSelected()) {
            opcao.add(OpcaoCliente.VALOR_LIMITE);
        }
        if (chkVencimento.isSelected()) {
            opcao.add(OpcaoCliente.VENCIMENTO_ROTATIVO);
        }
        if (chkObservacao.isSelected()) {
            opcao.add(OpcaoCliente.OBSERVACOES);
        }
        if (chkObservacao2.isSelected()) {
            opcao.add(OpcaoCliente.OBSERVACOES2);
        }
        if (chkNomePai.isSelected()) {
            opcao.add(OpcaoCliente.NOME_PAI);
        }
        if (chkNomeMae.isSelected()) {
            opcao.add(OpcaoCliente.NOME_MAE);
        }
        if (chkNomeConjuge.isSelected()) {
            opcao.add(OpcaoCliente.NOME_CONJUGE);
        }
        if (chkDataNascimento.isSelected()) {
            opcao.add(OpcaoCliente.DATA_NASCIMENTO);
        }
        if (chkEmpresa.isSelected()) {
            opcao.add(OpcaoCliente.EMPRESA);
        }
        if (chkCargo.isSelected()) {
            opcao.add(OpcaoCliente.CARGO);
        }
        if (chkDataAdmissao.isSelected()) {
            opcao.add(OpcaoCliente.DATA_ADMISSAO);
        }
        if (chkSalario.isSelected()) {
            opcao.add(OpcaoCliente.SALARIO);
        }
        if (chkSexo.isSelected()) {
            opcao.add(OpcaoCliente.SEXO);
        }
        if (chkEstadoCivil.isSelected()) {
            opcao.add(OpcaoCliente.ESTADO_CIVIL);
        }

        if (!opcao.isEmpty()) {
            importador.atualizarClientePreferencial(opcao.toArray(new OpcaoCliente[]{}));
        }

        if (chkCreditoRotativo.isSelected()) {
            importador.importarCreditoRotativo();
        }

        if (chkOutrasReceitas.isSelected()) {
            importador.importarOutrasReceitas();
        }

        if (chkCheque.isSelected()) {
            importador.importarCheque();
        }

        if (chkConveniado.isSelected()) {
            importador.importarConvenioConveniado();
        }

        if (chkConveniadoEmpresa.isSelected()) {
            importador.importarConvenioEmpresa();
        }

        if (chkConveniadoTransacao.isSelected()) {
            importador.importarConvenioTransacao();
        }
    }

    public void executarImportacao() throws Exception {
        importar();
    }

    /*
    Procura por checkbox dentro dos componentes e os seta como false.
     */
    public void limparCliente() {
        for (Component p : tabImportacao.getComponents()) {
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
        for (Component p : tabCheque.getComponents()) {
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
        for (Component p : tabClienteEventual.getComponents()) {
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
        for (Component p : tabConvenio.getComponents()) {
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
        for (Component p : tabCreditoRotativo.getComponents()) {
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
        for (Component p : tabParametros.getComponents()) {
            if (p instanceof VRPanel) {
                for (Component c : ((VRPanel) p).getComponents()) {
                    if (c instanceof JCheckBox) {
                        ((JCheckBox) c).setSelected(false);
                    }
                }
            }
            if (p instanceof JCheckBox) {
                ((JCheckBox) p).setSelected(false);
            }
        }
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
        tabParametros = new javax.swing.JPanel();
        pnlDados2 = new vrframework.bean.panel.VRPanel();
        chkImportarClienteAtivo = new vrframework.bean.checkBox.VRCheckBox();
        chkForcarUnificacao = new vrframework.bean.checkBox.VRCheckBox();
        scrollImportação = new javax.swing.JScrollPane();
        tabImportacao = new vrframework.bean.panel.VRPanel();
        pnlDados = new vrframework.bean.panel.VRPanel();
        jLabel5 = new javax.swing.JLabel();
        chkNome = new vrframework.bean.checkBox.VRCheckBox();
        chkCnpj = new vrframework.bean.checkBox.VRCheckBox();
        chkIE = new vrframework.bean.checkBox.VRCheckBox();
        chkClientePreferencial = new vrframework.bean.checkBox.VRCheckBox();
        chkTipoInscricao = new vrframework.bean.checkBox.VRCheckBox();
        chkPessoaImpM5_0 = new javax.swing.JCheckBox();
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
        chkCelular = new vrframework.bean.checkBox.VRCheckBox();
        chkEmail = new vrframework.bean.checkBox.VRCheckBox();
        pnlDadosComplementares = new vrframework.bean.panel.VRPanel();
        jLabel9 = new javax.swing.JLabel();
        chkSituacaoCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkBloqueado = new vrframework.bean.checkBox.VRCheckBox();
        chkPermiteCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkPermiteCheque = new vrframework.bean.checkBox.VRCheckBox();
        chkObservacao = new vrframework.bean.checkBox.VRCheckBox();
        chkObservacao2 = new vrframework.bean.checkBox.VRCheckBox();
        chkValorLimite = new vrframework.bean.checkBox.VRCheckBox();
        chkNomePai = new vrframework.bean.checkBox.VRCheckBox();
        chkNomeMae = new vrframework.bean.checkBox.VRCheckBox();
        chkNomeConjuge = new vrframework.bean.checkBox.VRCheckBox();
        chkDataNascimento = new vrframework.bean.checkBox.VRCheckBox();
        chkDataCadastro = new vrframework.bean.checkBox.VRCheckBox();
        chkSexo = new vrframework.bean.checkBox.VRCheckBox();
        chkEstadoCivil = new vrframework.bean.checkBox.VRCheckBox();
        chkVencimento = new vrframework.bean.checkBox.VRCheckBox();
        pnlDadosEmpresa = new vrframework.bean.panel.VRPanel();
        jLabel10 = new javax.swing.JLabel();
        chkEmpresa = new vrframework.bean.checkBox.VRCheckBox();
        chkCargo = new vrframework.bean.checkBox.VRCheckBox();
        chkDataAdmissao = new vrframework.bean.checkBox.VRCheckBox();
        chkSalario = new vrframework.bean.checkBox.VRCheckBox();
        tabClienteEventual = new javax.swing.JPanel();
        pnlDados1 = new vrframework.bean.panel.VRPanel();
        jLabel12 = new javax.swing.JLabel();
        chkClienteEventual = new vrframework.bean.checkBox.VRCheckBox();
        tabCreditoRotativo = new javax.swing.JPanel();
        pnlCreditoRotativo = new vrframework.bean.panel.VRPanel();
        jLabel7 = new javax.swing.JLabel();
        chkCreditoRotativo = new vrframework.bean.checkBox.VRCheckBox();
        chkOutrasReceitas = new vrframework.bean.checkBox.VRCheckBox();
        tabCheque = new javax.swing.JPanel();
        pnlCheque = new vrframework.bean.panel.VRPanel();
        jLabel11 = new javax.swing.JLabel();
        chkCheque = new vrframework.bean.checkBox.VRCheckBox();
        tabConvenio = new javax.swing.JPanel();
        pnlConvenio = new javax.swing.JPanel();
        chkConveniado = new javax.swing.JCheckBox();
        chkConveniadoEmpresa = new javax.swing.JCheckBox();
        chkConveniadoTransacao = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(vRCheckBox3, "vRCheckBox3");

        setName("tabMain"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chkImportarClienteAtivo, "Importar Clientes Ativos");
        chkImportarClienteAtivo.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkForcarUnificacao, "Forçar Unificação");
        chkForcarUnificacao.setEnabled(true);

        javax.swing.GroupLayout pnlDados2Layout = new javax.swing.GroupLayout(pnlDados2);
        pnlDados2.setLayout(pnlDados2Layout);
        pnlDados2Layout.setHorizontalGroup(
            pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkImportarClienteAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkForcarUnificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(603, Short.MAX_VALUE))
        );
        pnlDados2Layout.setVerticalGroup(
            pnlDados2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chkImportarClienteAtivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkForcarUnificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabParametrosLayout = new javax.swing.GroupLayout(tabParametros);
        tabParametros.setLayout(tabParametrosLayout);
        tabParametrosLayout.setHorizontalGroup(
            tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDados2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabParametrosLayout.setVerticalGroup(
            tabParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabParametrosLayout.createSequentialGroup()
                .addComponent(pnlDados2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 319, Short.MAX_VALUE))
        );

        addTab("Parâmetros", tabParametros);

        scrollImportação.setBorder(null);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "DADOS");

        org.openide.awt.Mnemonics.setLocalizedText(chkNome, "Nome");
        chkNome.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkCnpj, "CNPJ");
        chkCnpj.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkIE, "Inscrição Estadual");
        chkIE.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkClientePreferencial, "Cliente Preferencial");
        chkClientePreferencial.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkTipoInscricao, "Tipo Inscrição");
        chkTipoInscricao.setToolTipText("Corrige o relacionamento entre o produto e a família.");

        org.openide.awt.Mnemonics.setLocalizedText(chkPessoaImpM5_0, "Pessoa M5.0");

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
                        .addComponent(chkNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkTipoInscricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkPessoaImpM5_0)))
                .addContainerGap(195, Short.MAX_VALUE))
        );
        pnlDadosLayout.setVerticalGroup(
            pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCnpj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClientePreferencial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkTipoInscricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPessoaImpM5_0))
                .addContainerGap(16, Short.MAX_VALUE))
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

        org.openide.awt.Mnemonics.setLocalizedText(chkCelular, "Celular");
        chkCelular.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkEmail, "Email");
        chkEmail.setEnabled(true);

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
                        .addComponent(chkCelular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkContatoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlContatoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlContatoLayout.setVerticalGroup(
            pnlContatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContatoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlContatoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkTelefone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkContatoAdicional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCelular, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, "DADOS COMPLEMENTARES");

        org.openide.awt.Mnemonics.setLocalizedText(chkSituacaoCadastro, "Situação Cadastro");
        chkSituacaoCadastro.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkBloqueado, "Bloqueado");
        chkBloqueado.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkPermiteCreditoRotativo, "Permite Crédito Rotativo");
        chkPermiteCreditoRotativo.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkPermiteCheque, "Permite Cheque");
        chkPermiteCheque.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkObservacao, "Observação");
        chkObservacao.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkObservacao2, "Observação 2");
        chkObservacao2.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkValorLimite, "Valor Limite");
        chkValorLimite.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkNomePai, "Nome Pai");
        chkNomePai.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkNomeMae, "Nome Mãe");
        chkNomeMae.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkNomeConjuge, "Nome Conjuge");
        chkNomeConjuge.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkDataNascimento, "Data Nascimento");
        chkDataNascimento.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkDataCadastro, "Data Cadastro");
        chkDataCadastro.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkSexo, "Sexo");
        chkSexo.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkEstadoCivil, "Estado Civil");
        chkEstadoCivil.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkVencimento, "Vencimento Rotativo");
        chkVencimento.setEnabled(true);

        javax.swing.GroupLayout pnlDadosComplementaresLayout = new javax.swing.GroupLayout(pnlDadosComplementares);
        pnlDadosComplementares.setLayout(pnlDadosComplementaresLayout);
        pnlDadosComplementaresLayout.setHorizontalGroup(
            pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPermiteCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkPermiteCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel9))
                    .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkNomePai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkNomeMae, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkNomeConjuge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkObservacao2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        pnlDadosComplementaresLayout.setVerticalGroup(
            pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosComplementaresLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkSituacaoCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkBloqueado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPermiteCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkPermiteCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkValorLimite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDataCadastro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNomePai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNomeMae, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkNomeConjuge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkObservacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkObservacao2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDadosComplementaresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkVencimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, "EMPRESA");

        org.openide.awt.Mnemonics.setLocalizedText(chkEmpresa, "Empresa");
        chkEmpresa.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkCargo, "Cargo");
        chkCargo.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkDataAdmissao, "Data Admissão");
        chkDataAdmissao.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkSalario, "Salário");
        chkSalario.setEnabled(true);

        javax.swing.GroupLayout pnlDadosEmpresaLayout = new javax.swing.GroupLayout(pnlDadosEmpresa);
        pnlDadosEmpresa.setLayout(pnlDadosEmpresaLayout);
        pnlDadosEmpresaLayout.setHorizontalGroup(
            pnlDadosEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosEmpresaLayout.createSequentialGroup()
                .addGroup(pnlDadosEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDadosEmpresaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkDataAdmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSalario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDadosEmpresaLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDadosEmpresaLayout.setVerticalGroup(
            pnlDadosEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDadosEmpresaLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDadosEmpresaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkDataAdmissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSalario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabImportacaoLayout = new javax.swing.GroupLayout(tabImportacao);
        tabImportacao.setLayout(tabImportacaoLayout);
        tabImportacaoLayout.setHorizontalGroup(
            tabImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImportacaoLayout.createSequentialGroup()
                .addGroup(tabImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlEndereco, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlContato, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDadosComplementares, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDadosEmpresa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        tabImportacaoLayout.setVerticalGroup(
            tabImportacaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabImportacaoLayout.createSequentialGroup()
                .addComponent(pnlDados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlContato, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDadosComplementares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlDadosEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        scrollImportação.setViewportView(tabImportacao);

        addTab("Importação de Cliente Preferencial", scrollImportação);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, "DADOS");

        org.openide.awt.Mnemonics.setLocalizedText(chkClienteEventual, "Cliente Eventual");
        chkClienteEventual.setEnabled(true);

        javax.swing.GroupLayout pnlDados1Layout = new javax.swing.GroupLayout(pnlDados1);
        pnlDados1.setLayout(pnlDados1Layout);
        pnlDados1Layout.setHorizontalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGroup(pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDados1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel12))
                    .addGroup(pnlDados1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(641, Short.MAX_VALUE))
        );
        pnlDados1Layout.setVerticalGroup(
            pnlDados1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDados1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkClienteEventual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabClienteEventualLayout = new javax.swing.GroupLayout(tabClienteEventual);
        tabClienteEventual.setLayout(tabClienteEventualLayout);
        tabClienteEventualLayout.setHorizontalGroup(
            tabClienteEventualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlDados1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabClienteEventualLayout.setVerticalGroup(
            tabClienteEventualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabClienteEventualLayout.createSequentialGroup()
                .addComponent(pnlDados1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 327, Short.MAX_VALUE))
        );

        addTab("Importação Cliente Eventual", tabClienteEventual);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "CREDITO ROTATIVO");

        org.openide.awt.Mnemonics.setLocalizedText(chkCreditoRotativo, "Receber Crédito Rotativo");
        chkCreditoRotativo.setEnabled(true);

        org.openide.awt.Mnemonics.setLocalizedText(chkOutrasReceitas, "Outras Receitas");
        chkOutrasReceitas.setEnabled(true);

        javax.swing.GroupLayout pnlCreditoRotativoLayout = new javax.swing.GroupLayout(pnlCreditoRotativo);
        pnlCreditoRotativo.setLayout(pnlCreditoRotativoLayout);
        pnlCreditoRotativoLayout.setHorizontalGroup(
            pnlCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreditoRotativoLayout.createSequentialGroup()
                .addGroup(pnlCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCreditoRotativoLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel7))
                    .addGroup(pnlCreditoRotativoLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(chkOutrasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(476, Short.MAX_VALUE))
        );
        pnlCreditoRotativoLayout.setVerticalGroup(
            pnlCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreditoRotativoLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOutrasReceitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabCreditoRotativoLayout = new javax.swing.GroupLayout(tabCreditoRotativo);
        tabCreditoRotativo.setLayout(tabCreditoRotativoLayout);
        tabCreditoRotativoLayout.setHorizontalGroup(
            tabCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCreditoRotativo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabCreditoRotativoLayout.setVerticalGroup(
            tabCreditoRotativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabCreditoRotativoLayout.createSequentialGroup()
                .addComponent(pnlCreditoRotativo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 301, Short.MAX_VALUE))
        );

        addTab("Importação Crédito Rotativo", tabCreditoRotativo);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, "CHEQUE");

        org.openide.awt.Mnemonics.setLocalizedText(chkCheque, "Receber Cheque");
        chkCheque.setEnabled(true);

        javax.swing.GroupLayout pnlChequeLayout = new javax.swing.GroupLayout(pnlCheque);
        pnlCheque.setLayout(pnlChequeLayout);
        pnlChequeLayout.setHorizontalGroup(
            pnlChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChequeLayout.createSequentialGroup()
                .addGroup(pnlChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChequeLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel11))
                    .addGroup(pnlChequeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(626, Short.MAX_VALUE))
        );
        pnlChequeLayout.setVerticalGroup(
            pnlChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChequeLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout tabChequeLayout = new javax.swing.GroupLayout(tabCheque);
        tabCheque.setLayout(tabChequeLayout);
        tabChequeLayout.setHorizontalGroup(
            tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlCheque, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabChequeLayout.setVerticalGroup(
            tabChequeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabChequeLayout.createSequentialGroup()
                .addComponent(pnlCheque, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 327, Short.MAX_VALUE))
        );

        addTab("Importação Cheque", tabCheque);

        org.openide.awt.Mnemonics.setLocalizedText(chkConveniado, "Conveniado");

        org.openide.awt.Mnemonics.setLocalizedText(chkConveniadoEmpresa, "Empresa");

        org.openide.awt.Mnemonics.setLocalizedText(chkConveniadoTransacao, "Transação");

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Dados");

        javax.swing.GroupLayout pnlConvenioLayout = new javax.swing.GroupLayout(pnlConvenio);
        pnlConvenio.setLayout(pnlConvenioLayout);
        pnlConvenioLayout.setHorizontalGroup(
            pnlConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConvenioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlConvenioLayout.createSequentialGroup()
                        .addComponent(chkConveniado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkConveniadoEmpresa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkConveniadoTransacao))
                    .addComponent(jLabel1))
                .addContainerGap(425, Short.MAX_VALUE))
        );
        pnlConvenioLayout.setVerticalGroup(
            pnlConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConvenioLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkConveniado)
                    .addComponent(chkConveniadoEmpresa)
                    .addComponent(chkConveniadoTransacao))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout tabConvenioLayout = new javax.swing.GroupLayout(tabConvenio);
        tabConvenio.setLayout(tabConvenioLayout);
        tabConvenioLayout.setHorizontalGroup(
            tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlConvenio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabConvenioLayout.setVerticalGroup(
            tabConvenioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabConvenioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlConvenio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(289, Short.MAX_VALUE))
        );

        addTab("Importação Convênio", tabConvenio);

        getAccessibleContext().setAccessibleName("Importação de Clientes");
    }// </editor-fold>//GEN-END:initComponents

    private void chkMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMunicipioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkMunicipioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.ButtonGroup btgPautaFiscal;
    public vrframework.bean.checkBox.VRCheckBox chkBairro;
    public vrframework.bean.checkBox.VRCheckBox chkBloqueado;
    public vrframework.bean.checkBox.VRCheckBox chkCargo;
    public vrframework.bean.checkBox.VRCheckBox chkCelular;
    public vrframework.bean.checkBox.VRCheckBox chkCep;
    public vrframework.bean.checkBox.VRCheckBox chkCheque;
    public vrframework.bean.checkBox.VRCheckBox chkClienteEventual;
    public vrframework.bean.checkBox.VRCheckBox chkClientePreferencial;
    public vrframework.bean.checkBox.VRCheckBox chkCnpj;
    public vrframework.bean.checkBox.VRCheckBox chkComplemento;
    public vrframework.bean.checkBox.VRCheckBox chkContatoAdicional;
    public javax.swing.JCheckBox chkConveniado;
    public javax.swing.JCheckBox chkConveniadoEmpresa;
    public javax.swing.JCheckBox chkConveniadoTransacao;
    public vrframework.bean.checkBox.VRCheckBox chkCreditoRotativo;
    public vrframework.bean.checkBox.VRCheckBox chkDataAdmissao;
    public vrframework.bean.checkBox.VRCheckBox chkDataCadastro;
    public vrframework.bean.checkBox.VRCheckBox chkDataNascimento;
    public vrframework.bean.checkBox.VRCheckBox chkEmail;
    public vrframework.bean.checkBox.VRCheckBox chkEmpresa;
    public vrframework.bean.checkBox.VRCheckBox chkEndereco;
    public vrframework.bean.checkBox.VRCheckBox chkEstadoCivil;
    public vrframework.bean.checkBox.VRCheckBox chkForcarUnificacao;
    public vrframework.bean.checkBox.VRCheckBox chkIE;
    public vrframework.bean.checkBox.VRCheckBox chkImportarClienteAtivo;
    public vrframework.bean.checkBox.VRCheckBox chkMunicipio;
    public vrframework.bean.checkBox.VRCheckBox chkMunicipioIbge;
    public vrframework.bean.checkBox.VRCheckBox chkNome;
    public vrframework.bean.checkBox.VRCheckBox chkNomeConjuge;
    public vrframework.bean.checkBox.VRCheckBox chkNomeMae;
    public vrframework.bean.checkBox.VRCheckBox chkNomePai;
    public vrframework.bean.checkBox.VRCheckBox chkNumero;
    public vrframework.bean.checkBox.VRCheckBox chkObservacao;
    public vrframework.bean.checkBox.VRCheckBox chkObservacao2;
    public vrframework.bean.checkBox.VRCheckBox chkOutrasReceitas;
    public vrframework.bean.checkBox.VRCheckBox chkPermiteCheque;
    public vrframework.bean.checkBox.VRCheckBox chkPermiteCreditoRotativo;
    public javax.swing.JCheckBox chkPessoaImpM5_0;
    public vrframework.bean.checkBox.VRCheckBox chkSalario;
    public vrframework.bean.checkBox.VRCheckBox chkSexo;
    public vrframework.bean.checkBox.VRCheckBox chkSituacaoCadastro;
    public vrframework.bean.checkBox.VRCheckBox chkTelefone;
    public vrframework.bean.checkBox.VRCheckBox chkTipoInscricao;
    public vrframework.bean.checkBox.VRCheckBox chkUf;
    public vrframework.bean.checkBox.VRCheckBox chkUfIbge;
    public vrframework.bean.checkBox.VRCheckBox chkValorLimite;
    public vrframework.bean.checkBox.VRCheckBox chkVencimento;
    public javax.swing.JLabel jLabel1;
    public javax.swing.JLabel jLabel10;
    public javax.swing.JLabel jLabel11;
    public javax.swing.JLabel jLabel12;
    public javax.swing.JLabel jLabel5;
    public javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    public javax.swing.JLabel jLabel9;
    public vrframework.bean.panel.VRPanel pnlCheque;
    public vrframework.bean.panel.VRPanel pnlContato;
    public javax.swing.JPanel pnlConvenio;
    public vrframework.bean.panel.VRPanel pnlCreditoRotativo;
    public vrframework.bean.panel.VRPanel pnlDados;
    public vrframework.bean.panel.VRPanel pnlDados1;
    public vrframework.bean.panel.VRPanel pnlDados2;
    public vrframework.bean.panel.VRPanel pnlDadosComplementares;
    public vrframework.bean.panel.VRPanel pnlDadosEmpresa;
    public vrframework.bean.panel.VRPanel pnlEndereco;
    public javax.swing.JScrollPane scrollImportação;
    public javax.swing.JPanel tabCheque;
    public javax.swing.JPanel tabClienteEventual;
    public javax.swing.JPanel tabConvenio;
    public javax.swing.JPanel tabCreditoRotativo;
    public vrframework.bean.panel.VRPanel tabImportacao;
    public javax.swing.JPanel tabParametros;
    public vrframework.bean.checkBox.VRCheckBox vRCheckBox3;
    // End of variables declaration//GEN-END:variables

    private String[] concat(String[] params, String novo) {
        params = Arrays.copyOf(params, params.length + 1);
        params[params.length - 1] = novo;
        return params;
    }

    public void gravarParametros(Parametros parametros, String... params) {
        parametros.put(chkImportarClienteAtivo.isSelected(), concat(params, "SOMENTE_CLIENTES_ATIVO"));
    }

    public void carregarParametros(Parametros parametros, String... params) {
        chkImportarClienteAtivo.setSelected(parametros.getBool(concat(params, "SOMENTE_CLIENTES_ATIVO")));
    }
}
