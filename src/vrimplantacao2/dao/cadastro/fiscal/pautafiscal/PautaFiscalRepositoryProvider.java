package vrimplantacao2.dao.cadastro.fiscal.pautafiscal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.dao.cadastro.local.LocalDAO;
import vrimplantacao2.dao.cadastro.produto.NcmDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributacaoDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoVO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalAnteriorVO;
import vrimplantacao2.vo.cadastro.fiscal.pautafiscal.PautaFiscalVO;
import vrimplantacao2.vo.cadastro.local.EstadoVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.NcmVO;
import vrimplantacao2.vo.enums.OpcaoFiscal;

/**
 *
 * @author Leandro
 */
public class PautaFiscalRepositoryProvider {
    
    private final String sistema;
    private final String loja;
    private final int lojaVR;
    private final LocalDAO localDAO;
    private final PautaFiscalAnteriorDAO anteriorDAO;
    private final PautaFiscalDAO dao;
    private final NcmDAO ncmDAO;
    private final MapaTributacaoDAO mapaDAO;

    public PautaFiscalRepositoryProvider(String sistema, String loja, int lojaVR) throws Exception {
        this.sistema = sistema;
        this.loja = loja;
        this.lojaVR = lojaVR;
        this.localDAO = new LocalDAO();
        this.anteriorDAO = new PautaFiscalAnteriorDAO();
        this.dao = new PautaFiscalDAO();
        this.ncmDAO = new NcmDAO();
        this.mapaDAO = new MapaTributacaoDAO();
    }

    public String getSistema() {
        return sistema;
    }

    public String getLoja() {
        return loja;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void begin() throws Exception {
        Conexao.begin();
    }

    public void commit() throws Exception {
        Conexao.commit();
    }

    public void rollback() throws Exception {
        Conexao.rollback();
    }

    public void notificar() throws Exception {
        ProgressBar.next();
    }

    public void notificar(String msg) throws Exception {
        ProgressBar.setStatus(msg);
    }

    public void notificar(String msg, int size) throws Exception {
        notificar(msg);
        ProgressBar.setMaximum(size);
    }

    public Map<String, PautaFiscalAnteriorVO> getAnteriores() throws Exception {
        return anteriorDAO.getAnteriores(getSistema(), getLoja());
    }

    public void atualizar(PautaFiscalAnteriorVO anterior) throws Exception {
        anteriorDAO.atualizar(anterior);
    }

    public void atualizar(PautaFiscalVO vo, Set<OpcaoFiscal> opt) throws Exception {
        dao.atualizar(vo, opt);
    }

    public void gravar(PautaFiscalVO vo, Set<OpcaoFiscal> opt) throws Exception {
        dao.gravar(vo, opt);
    }

    public void gravarAnterior(PautaFiscalAnteriorVO anterior) throws Exception {
        anteriorDAO.gravarAnterior(anterior);
    }

    public Map<String, EstadoVO> getEstados() throws Exception {
        return localDAO.getEstados();
    }

    public int getAliquota(int cst, double aliquota, double reduzido) throws Exception {
        return Icms.getIcms(cst, aliquota, reduzido).getId();
    }

    public NcmVO getNcm(String ncm) throws Exception {
        return ncmDAO.getNcm(ncm);
    }

    public EstadoVO getUfPadrao() {
        return Parametros.get().getUfPadraoV2();
    }
    
    private Map<String, Icms> icms;
    public Icms getAliquotaByMapaId(String icmsId) throws Exception {
        if (icms == null) {
            icms = new HashMap<>();
            for (MapaTributoVO vo: mapaDAO.getMapa(getSistema(), getLoja())) {
                if (vo.getAliquota() != null) {
                    icms.put(vo.getOrigId(), vo.getAliquota());
                }
            }
        }

        Icms icm = icms.get(icmsId);
        if (icm == null) {
            icm = Icms.getIsento();
        }

        return icm;
    }

    public Map<String, ProdutoPautaVO> getNcmsProduto() throws Exception {
        return dao.getNcmsProduto(getSistema(), getLoja(), getLojaVR());
    }

    public Map<Long, ProdutoPautaVO> getNcmsEan() throws Exception {
        return dao.getNcmsProduto(getLojaVR());
    }
    
}
