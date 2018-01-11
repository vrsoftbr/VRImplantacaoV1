package vrimplantacao2.dao.cadastro.financeiro.contaspagar;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import vrimplantacao2.dao.cadastro.fornecedor.FornecedorTestClasses;
import vrimplantacao2.vo.cadastro.financeiro.ContaPagarAnteriorVO;
import vrimplantacao2.vo.cadastro.financeiro.PagarOutrasDespesasVO;
import vrimplantacao2.vo.importacao.ContaPagarIMP;

/**
 *
 * @author Leandro
 */
public class ContasPagarTestClasses {
    
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    
    public static ContaPagarIMP getImp1() throws Exception {
        ContaPagarIMP imp = new ContaPagarIMP();
        ContaPagarAnteriorVO aux = ContasPagarTestClasses.getAnterior1(false);
        imp.setId(aux.getId());
        imp.setIdFornecedor(aux.getId_fornecedor());
        imp.setDataEmissao(aux.getDataEmissao());
        imp.setDataEntrada(format.parse("03/01/2017"));
        imp.setDataHoraAlteracao(new Timestamp(format.parse("01/01/2017").getTime()));
        imp.setNumeroDocumento("ADD123");
        imp.setObservacao("DESPESA 1");
        imp.setValor(aux.getValor());        
        imp.addVencimento(format.parse("10/01/2017"), 140.65);
        
        return imp;
    }
    
    public static ContaPagarIMP getImp2() throws Exception {
        ContaPagarIMP imp = new ContaPagarIMP();
        ContaPagarAnteriorVO aux = ContasPagarTestClasses.getAnterior2(false);
        imp.setId(aux.getId());
        imp.setIdFornecedor(aux.getId_fornecedor());
        imp.setDataEmissao(aux.getDataEmissao());
        imp.setDataEntrada(format.parse("05/01/2017"));
        imp.setDataHoraAlteracao(new Timestamp(format.parse("06/01/2017").getTime()));
        imp.setNumeroDocumento("AD4568D");
        imp.setObservacao("DESPESA 2");
        imp.setValor(aux.getValor());
        return imp;
    }
    
    public static ContaPagarIMP getImp3() throws Exception {
        ContaPagarIMP imp = new ContaPagarIMP();
        ContaPagarAnteriorVO aux = ContasPagarTestClasses.getAnterior3(false);
        imp.setId(aux.getId());
        imp.setIdFornecedor(aux.getId_fornecedor());
        imp.setDataEmissao(aux.getDataEmissao());
        imp.setDataEntrada(format.parse("15/12/2016"));
        imp.setDataHoraAlteracao(new Timestamp(format.parse("06/01/2017").getTime()));
        imp.setNumeroDocumento("8974");
        imp.setObservacao("DESPESA 3");
        imp.setValor(aux.getValor());
        imp.addVencimento(format.parse("10/01/2017"), 60);
        imp.addVencimento(format.parse("10/02/2017"), 60);
        return imp;
    }
    
    public static PagarOutrasDespesasVO getOutraDespesa1() throws Exception {
        PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();
        vo.setId(1);
        vo.setDataEmissao(format.parse("01/01/2017"));
        vo.setDataEntrada(format.parse("03/01/2017"));
        vo.setDataHoraAlteracao(new Timestamp(format.parse("01/01/2017").getTime()));
        vo.setIdFornecedor(FornecedorTestClasses.getFornecedor1().getId());
        vo.setObservacao("IMPORTADO VR DESPESA 1");
        vo.setId_loja(1);
        vo.setNumeroDocumento(123);
        vo.setValor(140.65);
        vo.addVencimento(format.parse("10/01/2017"), 140.65);
        return vo;
    }
    
    public static PagarOutrasDespesasVO getOutraDespesa2() throws Exception {
        PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();
        vo.setId(2);
        vo.setDataEmissao(format.parse("04/01/2017"));
        vo.setDataEntrada(format.parse("05/01/2017"));
        vo.setDataHoraAlteracao(new Timestamp(format.parse("06/01/2017").getTime()));
        vo.setIdFornecedor(FornecedorTestClasses.getFornecedor2().getId());
        vo.setId_loja(1);
        vo.setObservacao("IMPORTADO VR DESPESA 2");
        vo.setNumeroDocumento(4568);
        vo.setValor(560.36);
        return vo;
    }
    
    public static PagarOutrasDespesasVO getOutraDespesa3() throws Exception {
        PagarOutrasDespesasVO vo = new PagarOutrasDespesasVO();
        vo.setId(3);
        vo.setDataEmissao(format.parse("10/12/2016"));
        vo.setDataEntrada(format.parse("15/12/2016"));
        vo.setDataHoraAlteracao(new Timestamp(format.parse("06/01/2017").getTime()));
        vo.setIdFornecedor(FornecedorTestClasses.getFornecedor2().getId());
        vo.setId_loja(1);
        vo.setObservacao("IMPORTADO VR DESPESA 3");
        vo.setNumeroDocumento(8974);
        vo.setValor(120.00);
        vo.addVencimento(format.parse("10/01/2017"), 60);
        vo.addVencimento(format.parse("10/02/2017"), 60);
        return vo;
    }
    
    public static ContaPagarAnteriorVO getAnterior1(boolean vazio) throws Exception {
        ContaPagarAnteriorVO vo = new ContaPagarAnteriorVO();
        vo.setSistema("TESTE");
        vo.setAgrupador("1");
        vo.setId("ASD123");
        PagarOutrasDespesasVO atual = getOutraDespesa1();
        vo.setCodigoAtual(vazio ? null : atual);
        vo.setDataEmissao(format.parse("01/01/2017"));
        vo.setDocumento("ADD123");
        vo.setId_fornecedor(FornecedorTestClasses.getAnterior1().getImportId());
        vo.setValor(atual.getValor());
        return vo;
    }   
    
    public static ContaPagarAnteriorVO getAnterior2(boolean vazio) throws Exception {
        ContaPagarAnteriorVO vo = new ContaPagarAnteriorVO();
        vo.setSistema("TESTE");
        vo.setAgrupador("1");
        vo.setId("2");
        PagarOutrasDespesasVO atual = getOutraDespesa2();
        vo.setCodigoAtual(vazio ? null : atual);
        vo.setDataEmissao(atual.getDataEmissao());
        vo.setDocumento(String.valueOf(atual.getNumeroDocumento()));
        vo.setId_fornecedor(FornecedorTestClasses.getAnterior1().getImportId());
        vo.setValor(atual.getValor());
        return vo;
    }   
    
    public static ContaPagarAnteriorVO getAnterior3(boolean vazio) throws Exception {
        ContaPagarAnteriorVO vo = new ContaPagarAnteriorVO();
        vo.setSistema("TESTE");
        vo.setAgrupador("1");
        vo.setId("78934531134");
        PagarOutrasDespesasVO atual = getOutraDespesa3();
        vo.setCodigoAtual(vazio ? null : atual);
        vo.setDataEmissao(atual.getDataEmissao());
        vo.setDocumento(String.valueOf(atual.getNumeroDocumento()));
        vo.setId_fornecedor(FornecedorTestClasses.getAnterior3().getImportId());
        vo.setValor(atual.getValor());
        return vo;
    }   
    
}
