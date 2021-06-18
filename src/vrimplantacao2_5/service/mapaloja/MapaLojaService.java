package vrimplantacao2_5.service.mapaloja;

import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Util;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2_5.dao.configuracao.ConfiguracaoSistemaDAO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoLojaVO;
import vrimplantacao2_5.vo.cadastro.ConfiguracaoBancoVO;
import vrimplantacao2_5.vo.enums.ESituacaoMigracao;

/**
 *
 * @author guilhermegomes
 */
public class MapaLojaService {

    private final LojaDAO lojaDAO;
    private final ConfiguracaoSistemaDAO configuracaoDAO;

    public MapaLojaService() {
        this.lojaDAO = new LojaDAO();
        this.configuracaoDAO = new ConfiguracaoSistemaDAO();
    }

    public MapaLojaService(LojaDAO lojaDAO, ConfiguracaoSistemaDAO cfgDAO) {
        this.lojaDAO = lojaDAO;
        this.configuracaoDAO = cfgDAO;
    }

    public List<LojaVO> getLojaVR() {
        List<LojaVO> lojas = null;

        try {
            lojas = lojaDAO.carregar();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return lojas;
    }

    public void salvar(ConfiguracaoBancoVO configuracaoBancoVO) {
        try {
            if (configuracaoBancoVO.getConfiguracaoBancoLoja().getId() == 0 &&
                    !existeLojaMapeada(configuracaoBancoVO)) {
                
                /**
                 * Verifica se é a primeira loja mapeada
                 */
                if (verificaLojaMatriz(configuracaoBancoVO)) {
                    configuracaoBancoVO.getConfiguracaoBancoLoja().setLojaMatriz(true);
                }

                configuracaoBancoVO.getConfiguracaoBancoLoja().setDataCadastro(Utils.getDataAtual());
                configuracaoBancoVO.
                        getConfiguracaoBancoLoja().
                        setSituacaoMigracao(ESituacaoMigracao.CONFIGURANDO);

                configuracaoDAO.inserirLoja(configuracaoBancoVO);

            }
        } catch (Exception e) {
            Util.exibirMensagemErro(e, "Mapa de Loja");
        }
    }

    private boolean verificaLojaMatriz(ConfiguracaoBancoVO configuracaoBancoVO) {
        boolean eMatriz = true;

        try {
            eMatriz = configuracaoDAO.verificaLojaMatriz(configuracaoBancoVO);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return eMatriz;
    }

    private boolean existeLojaMapeada(ConfiguracaoBancoVO configuracaoBancoVO) {
        boolean lojaMapeada = false;
        try {
            /**
             * Verifica se já existe uma loja mapeada para loja destino selecionada
             */
            if (configuracaoDAO.existeLojaMapeada(ETipoLoja.LOJADESTINO.name(), configuracaoBancoVO)) {
                Util.exibirMensagem("Loja VR "
                        + configuracaoBancoVO.
                                getConfiguracaoBancoLoja().
                                    getIdLojaVR() + " já mapeada!", "Mapa de Loja");
                lojaMapeada = true;
            }

            /**
             * Verifica se já existe uma loja mapeada para loja origem selecionada
             */
            if (configuracaoDAO.existeLojaMapeada(ETipoLoja.LOJAORIGEM.name(), configuracaoBancoVO)) {
                Util.exibirMensagem("Loja Origem "
                        + configuracaoBancoVO.
                                getConfiguracaoBancoLoja().
                                    getIdLojaOrigem() + " já mapeada!", "Mapa de Loja");
                lojaMapeada = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Exceptions.printStackTrace(ex);
        }
        
        return lojaMapeada;
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

    enum ETipoLoja {
        LOJAORIGEM("LOJA DE ORIGEM"),
        LOJADESTINO("LOJA DESTINO DO VR");

        private final String tipoLoja;

        private ETipoLoja(String tipoLoja) {
            this.tipoLoja = tipoLoja;
        }

        public String getTipoLoja() {
            return tipoLoja;
        }
    }
}
