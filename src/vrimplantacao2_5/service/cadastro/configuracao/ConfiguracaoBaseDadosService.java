package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.List;
import vrimplantacao2_5.dao.bancodados.BancoDadosDAO;
import vrimplantacao2_5.dao.sistema.SistemaDAO;
import vrimplantacao2_5.vo.cadastro.BancoDadosVO;
import vrimplantacao2_5.vo.cadastro.SistemaVO;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;

/**
 *
 * @author guilhermegomes
 */
public class ConfiguracaoBaseDadosService {

    private final SistemaDAO sistemaDAO;
    private final BancoDadosDAO bdDAO;
    private final ConfiguracaoBaseDadosDAO conexaoDAO;
    private final ConfiguracaoBaseDadosServiceProvider provider;

    public ConfiguracaoBaseDadosService() {
        sistemaDAO = new SistemaDAO();
        bdDAO = new BancoDadosDAO();
        conexaoDAO = new ConfiguracaoBaseDadosDAO();
        provider = new ConfiguracaoBaseDadosServiceProvider();
    }

    public ConfiguracaoBaseDadosService(SistemaDAO sistemaDAO,
            BancoDadosDAO bdDAO,
            ConfiguracaoBaseDadosDAO conexaoDAO,
            ConfiguracaoBaseDadosServiceProvider provider) {
        this.sistemaDAO = sistemaDAO;
        this.bdDAO = bdDAO;
        this.conexaoDAO = conexaoDAO;
        this.provider = provider;
    }

    public List getSistema() {
        List<SistemaVO> sistemas = null;

        try {

            sistemas = sistemaDAO.getSistema();

        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }

        return sistemas;
    }

    public List getBancoDadosPorSistema(int idSistema) {
        List<BancoDadosVO> bdPorSistema = null;

        try {

            bdPorSistema = bdDAO.getBancoDadosPorSistema(idSistema);

        } catch (Exception e) {
            try {
                Util.exibirMensagem(e.getMessage(), getTitle());
            } catch (Exception ex) {
                Util.exibirMensagemErro(ex, getTitle());
            }
        }

        return bdPorSistema;
    }

    public ConfiguracaoPanel exibiPainelConexao(int idSistema, int idBancoDados) {
        return PanelConexaoFactory.getPanelConexao(idSistema, idBancoDados);
    }

    public void salvar(ConfiguracaoBaseDadosVO conexaoVO) {

        try {
            provider.begin();

            /**
             * Verifica se já existe uma conexão cadastrada
             */
            existeConexao(conexaoVO);

            if (conexaoVO.getId() == 0) {
                conexaoDAO.inserir(conexaoVO);
            } else {
                conexaoDAO.alterar(conexaoVO);
            }

            provider.commit();

        } catch (Exception ex) {
            Util.exibirMensagemErro(ex, getTitle());

            try {
                provider.rollback();
            } catch (Exception ex1) {
                Util.exibirMensagemErro(ex1, getTitle());
            }
        }
    }

    public void existeConexao(ConfiguracaoBaseDadosVO configuracaoVO) throws Exception {
        if (configuracaoVO.getId() == 0 && conexaoDAO.existeConexao(configuracaoVO)) {
            throw new VRException("Já existe uma conexão cadastrada!");
        }
    }

    private String getTitle() {
        return "Configuração de Base de Dados";
    }
}
