package vrimplantacao2.dao.cadastro.fornecedor;

import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;

/**
 *
 * @author Leandro
 */
public class FornecedorTestClasses {
    
    public static FornecedorVO getFornecedor1() {
        FornecedorVO vo = new FornecedorVO();
        vo.setId(2);
        vo.setRazaoSocial("FORNECEDOR 2");
        vo.setCnpj(12369568000123L);
        return vo;
    }
    
    public static FornecedorVO getFornecedor2() {
        FornecedorVO vo = new FornecedorVO();
        vo.setId(73);
        vo.setRazaoSocial("FORNECEDOR 2");
        vo.setCnpj(12365478965L);
        return vo;
    }
    
    public static FornecedorVO getFornecedor3() {
        FornecedorVO vo = new FornecedorVO();
        vo.setId(4);
        vo.setRazaoSocial("FORNECEDOR 3");
        vo.setCnpj(56517962000236L);
        return vo;
    }
    
    public static FornecedorAnteriorVO getAnterior1() {
        FornecedorAnteriorVO ant = new FornecedorAnteriorVO();
        ant.setImportSistema("TESTE");
        ant.setImportLoja("1");
        ant.setImportId("ASD123");
        ant.setCodigoAtual(FornecedorTestClasses.getFornecedor1());
        ant.setRazao(ant.getCodigoAtual().getRazaoSocial());
        return ant;
    }
    
    public static FornecedorAnteriorVO getAnterior2() {
        FornecedorAnteriorVO ant = new FornecedorAnteriorVO();
        ant.setImportSistema("TESTE");
        ant.setImportLoja("1");
        ant.setImportId("ASDVVA");
        ant.setCodigoAtual(FornecedorTestClasses.getFornecedor2());
        ant.setRazao(ant.getCodigoAtual().getRazaoSocial());
        return ant;
    }
    
    public static FornecedorAnteriorVO getAnterior3() {
        FornecedorAnteriorVO ant = new FornecedorAnteriorVO();
        ant.setImportSistema("TESTE");
        ant.setImportLoja("1");
        ant.setImportId("4");
        ant.setCodigoAtual(FornecedorTestClasses.getFornecedor3());
        ant.setRazao(ant.getCodigoAtual().getRazaoSocial());
        return ant;
    }
}
