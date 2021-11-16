package vrimplantacao2_5.service.cadastro.configuracao;

import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoBaseDadosDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBaseDadosVO;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;
import vrimplantacao2_5.vo.enums.ETipoLoja;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaService {

    private final LojaDAO lojaDAO;
    private final ConfiguracaoBaseDadosDAO configuracaoDAO;

    public MapaLojaService() {
        this.lojaDAO = new LojaDAO();
        this.configuracaoDAO = new ConfiguracaoBaseDadosDAO();
    }

    public MapaLojaService(LojaDAO lojaDAO, ConfiguracaoBaseDadosDAO cfgDAO) {
        this.lojaDAO = lojaDAO;
        this.configuracaoDAO = cfgDAO;
    }

    public List<LojaVO> getLojaVR() {
        List<LojaVO> lojas = null;

        try {
            lojas = lojaDAO.getLojasVRMapeada();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return lojas;
    }

    public void salvar(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {
        verificaLojaDestino(configuracaoBancoVO);

        verificaLojaOrigem(configuracaoBancoVO);

        verificaLojaMatriz(configuracaoBancoVO);

        if (configuracaoBancoVO.getConfiguracaoBancoLoja().getId() == 0) {

            configuracaoBancoVO.getConfiguracaoBancoLoja().setDataCadastro(Utils.getDataAtual());
            configuracaoBancoVO.
                getConfiguracaoBancoLoja().
                    setSituacaoMigracao(ESituacaoMigracao.CONFIGURANDO);

            configuracaoDAO.inserirLoja(configuracaoBancoVO);
            configuracaoDAO.inserirLojaOrigem(configuracaoBancoVO);

        }
    }

    /**
     * Verifica se já existe loja informada como matriz (loja principal para o
     * mix de produto que será mantido os códigos)
     *
     * @param configuracaoBancoVO
     * @throws java.lang.Exception
     */
    public void verificaLojaMatriz(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {
        String eMatriz = configuracaoDAO.verificaLojaMatriz(configuracaoBancoVO);

        if (configuracaoBancoVO.getConfiguracaoBancoLoja().isLojaMatriz() && !eMatriz.isEmpty()) {
            throw new VRException("Loja Origem " + eMatriz + " já selecionada como principal!");
        }
    }

    /**
     * Verifica se já existe uma loja mapeada para loja destino selecionada
     *
     * @param configuracaoBancoVO
     * @throws java.lang.Exception
     */
    public void verificaLojaDestino(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {

        if (configuracaoDAO.existeLojaMapeada(ETipoLoja.LOJA_DESTINO.name(), configuracaoBancoVO)) {
            throw new VRException("Loja VR "
                    + configuracaoBancoVO.
                            getConfiguracaoBancoLoja().
                            getIdLojaVR() + " já mapeada!");
        }
    }

    /**
     * Verifica se já existe uma loja mapeada para loja origem selecionada
     *
     * @param configuracaoBancoVO
     * @throws java.lang.Exception
     */
    public void verificaLojaOrigem(ConfiguracaoBaseDadosVO configuracaoBancoVO) throws Exception {

        if (configuracaoDAO.existeLojaMapeada(ETipoLoja.LOJA_ORIGEM.name(), configuracaoBancoVO)) {
            throw new VRException("Loja Origem "
                    + configuracaoBancoVO.
                            getConfiguracaoBancoLoja().
                            getIdLojaOrigem() + " já mapeada!");
        }
    }

    public List<ConfiguracaoBancoLojaVO> getLojaMapeada(int idConexao) {
        List<ConfiguracaoBancoLojaVO> lojas = null;

        try {
            lojas = configuracaoDAO.getLojaMapeada(idConexao);
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Mapa de Loja");
        }

        return lojas;
    }

    public void excluirLojaMapeada(ConfiguracaoBancoLojaVO configuracaoBancoLojaVO) throws Exception {
      
        verificaSituacaoLoja(configuracaoBancoLojaVO);

        configuracaoDAO.excluirLojaMapeada(configuracaoBancoLojaVO);
    }

    /**
     * Verifica se a situação está diferente de CONFIGURANDO 
     * para não excluir a loja com a importação em andamento
     * @param configuracaoBancoLojaVO
     * @throws vrframework.classe.VRException
    */
    public void verificaSituacaoLoja(ConfiguracaoBancoLojaVO configuracaoBancoLojaVO) throws VRException {
        if (configuracaoBancoLojaVO.getSituacaoMigracao() != ESituacaoMigracao.CONFIGURANDO) {
            throw new VRException("Processo de migração iniciado, não é possível excluir a loja mapeada!");
        }
    }
    
    public void alterarSituacaoMigracao(String idLojaOrigem, int idLojaVR, int situacaoMigracao) throws Exception {
        configuracaoDAO.alterarSituacaoMigracao(idLojaOrigem, idLojaVR, situacaoMigracao);
    }
}
