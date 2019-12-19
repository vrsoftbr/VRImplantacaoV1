package vrimplantacao2.dao.repositories.produto;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.repositories.MigracaoFuture;
import vrimplantacao2.dao.repositories.Recorder;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class ProdutoRepository2 {
    
    private static final Logger LOG = Logger.getLogger(ProdutoRepository2.class.getName());
    
    private ProdutoRepositoryProvider2 provider;

    public ProdutoRepository2(String sistema, String lojaOrigem, int lojaVR) {
        this.provider = new ProdutoRepositoryProvider2(sistema, lojaOrigem, lojaVR, LOG);
    }

    public void importar(MigracaoFuture<ProdutoIMP> future, Set<OpcaoProduto> opcoes) throws Exception {
        this.provider.setMessage("Analisando a importação");
        
        if (this.provider.isListaVazia() || !opcoes.contains(OpcaoProduto.IMPORTAR_NAO_GERAR_NOVA_LISTAGEM)) {
            try (Recorder<ProdutoIMP> recorder = this.provider.getProdutoRecorder()) {                
                future.call(recorder);
                recorder.done();
            } 
        }
        
    }

}
