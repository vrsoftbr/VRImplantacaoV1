package vrimplantacao2.dao.cadastro.notafiscal;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import vrimplantacao2.vo.cadastro.notafiscal.NotaEntrada;
import vrimplantacao2.vo.cadastro.notafiscal.NotaSaida;
import vrimplantacao2.vo.cadastro.notafiscal.SituacaoNotaEntrada;
import vrimplantacao2.vo.importacao.NotaFiscalIMP;

/**
 * Repositório de operações com a nota fiscal.
 * @author Leandro
 */
public class NotaFiscalRepository {
    
    private final NotaFiscalRepositoryProvider provider;
    
    private final int tipoNotaEntrada;
    private final int tipoNotaSaida;

    public NotaFiscalRepository(NotaFiscalRepositoryProvider provider) throws Exception {
        this.provider = provider;
        this.tipoNotaEntrada = provider.getTipoNotaEntrada();
        this.tipoNotaSaida = provider.getTipoNotaSaida();
    }

    public void importar(List<NotaFiscalIMP> notas, HashSet<OpcaoNotaFiscal> opt) throws Exception {
        
        
        
    }
    
    public NotaEntrada converterNotaEntrada(NotaFiscalIMP imp) {
        NotaEntrada n = new NotaEntrada();
        
        n.setIdLoja(this.provider.getLojaVR());
        n.setNumeroNota(imp.getNumeroNota());
        n.setIdFornecedor(getFornecedor(imp.getDestinatario().getId()));
        n.setDataEntrada(imp.getDataEmissao());
        n.setIdTipoEntrada(this.tipoNotaEntrada);
        n.setDataEmissao(imp.getDataEmissao());
        n.setDataHoraLancamento(new Timestamp(imp.getDataHoraAlteracao().getTime()));
        n.setValorIpi(imp.getValorIpi());
        n.setValorFrete(imp.getValorFrete());
        n.setValorDesconto(imp.getValorDesconto());
        n.setValorOutraDespesa(imp.getValorOutrasDespesas());
        n.setValorDespesaAdicional(0);
        n.setValorMercadoria(imp.getValorProduto());
        n.setValorTotal(imp.getValorTotal());
        n.setValorIcms(imp.getValorIcms());
        n.setValorIcmsSubstituicao(imp.getValorIcmsSubstituicao());
        n.setIdUsuario(0);
        n.setProdutorRural(imp.isProdutorRural());
        n.setSituacaoNotaEntrada(SituacaoNotaEntrada.FINALIZADO);
        n.setSerie(imp.getSerie());
        n.setChaveNfe(imp.getChaveNfe());
        n.setTipoFreteNotaFiscal(imp.getTipoFreteNotaFiscal());
        n.setTipoNota(imp.getTipoNota());
        n.setModelo(imp.getModelo());
        n.setDataHoraFinalizacao(new Timestamp(imp.getDataHoraAlteracao().getTime()));
        n.setInformacaoComplementar(imp.getInformacaoComplementar());
        n.setDataHoraAlteracao(new Timestamp(imp.getDataHoraAlteracao().getTime()));        
        
        return n;
    }

    private int getFornecedor(String id) {
        throw new UnsupportedOperationException("Funcao ainda nao suportada.");
    }
    
    public NotaSaida converterNotaSaida(NotaFiscalIMP imp) {
        NotaSaida n = new NotaSaida();
        
        
        
        return n;
    }
    
}
