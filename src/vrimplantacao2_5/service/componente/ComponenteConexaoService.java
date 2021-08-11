package vrimplantacao2_5.service.componente;

import vrframework.classe.VRException;
import vrimplantacao2_5.gui.componente.conexao.DriverConexao;
import vrimplantacao2_5.service.cadastro.configuracao.ConexaoComponenteFactory;
import vrimplantacao2_5.vo.enums.EBancoDados;

/**
 *
 * @author guilhermegomes
 */
public class ComponenteConexaoService {
    
    public DriverConexao getConexao(EBancoDados eBD) throws VRException {
        DriverConexao driver = ConexaoComponenteFactory.getConexao(eBD);
        
        if (driver == null) {
            throw new VRException("Não foi possível localizar o componente de conexão!");
        }
        
        return driver;
    }
}
