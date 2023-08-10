package vrimplantacao2_5.service.loja;

import java.util.List;
import javax.swing.JOptionPane;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.vo.loja.LojaFiltroConsultaVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.provider.ConexaoProvider;

/**
 *
 * @author Desenvolvimento
 */
public class LojaService {

    private final ConexaoProvider provider;
    private final LojaDAO lojaDAO;

    public LojaService() {
        this.lojaDAO = new LojaDAO();
        this.provider = new ConexaoProvider();
    }

    public LojaService(LojaDAO lojaDAO,
            ConexaoProvider provider) {

        this.lojaDAO = lojaDAO;
        this.provider = provider;
    }

    public void salvar(LojaVO vo) throws Exception {

        try {
            provider.begin();

            if (isLojaExiste(vo)) {
                lojaDAO.atualizarLoja(vo);
            } 
            else {
                lojaDAO.salvar(vo);
            }
            provider.commit();

        } catch (Exception ex) {
            //ex.printStackTrace();
            //Util.exibirMensagemErro(ex, getTitle());
            provider.rollback();
            throw ex;
        }

    }

    private boolean isLojaExiste(LojaVO vo) throws Exception {
        return lojaDAO.isLojaExiste(vo);
    }
    
    
    private boolean isFornecedorCadastrado(LojaVO vo) throws Exception {
        return lojaDAO.isCnpjCadastrado(vo);
    }

    public List<LojaVO> consultar(LojaFiltroConsultaVO i_filtro) throws Exception {
        return lojaDAO.consultar(i_filtro);
    }

    public LojaVO carregar(int i_id) throws Exception {
        return lojaDAO.carregar(i_id);
    }

    private String getTitle() {
        return "Cadastro Loja";
    }

    public void deletarLoja(LojaVO oLoja) throws Exception {
        lojaDAO.deletarLoja(oLoja);
    }
}
