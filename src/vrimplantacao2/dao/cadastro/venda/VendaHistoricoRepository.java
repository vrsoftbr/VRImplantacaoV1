package vrimplantacao2.dao.cadastro.venda;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.ProdutoAnteriorDAO;
import vrimplantacao2.dao.cadastro.produto.ProdutoAutomacaoDAO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoAnteriorVO;

/**
 * Executa o tratamento das vendas antes de gravar
 * @author Leandro
 */
public class VendaHistoricoRepository {
    
    private static final Logger LOG = Logger.getLogger(VendaHistoricoRepository.class.getName());
    
    private final VendaHistoricoDAO dao;
    private final ProdutoAnteriorDAO anteriorDAO;
    private String importSistema;
    private String importLoja;
    private int idLojaVR;
    
    public void setIdLojaVR(int idLojaVR) {
        this.idLojaVR = idLojaVR;
    }

    public String getImportSistema() {
        return importSistema;
    }

    public void setImportSistema(String importSistema) {
        this.importSistema = importSistema;
    }

    public String getImportLoja() {
        return importLoja;
    }

    public void setImportLoja(String importLoja) {
        this.importLoja = importLoja;
    }
    
    public VendaHistoricoRepository(VendaHistoricoDAO dao, ProdutoAnteriorDAO anteriorDAO) {
        this.dao = dao;
        this.anteriorDAO = anteriorDAO;
    }

    /**
     * Importa o histórico de vendas a partir da listagem informada.
     * @param historicoVenda 
     * @param utilizarEAN 
     * @exception Exception
     */
    public void importar(List<VendaHistoricoIMP> historicoVenda, boolean utilizarEAN) throws Exception {
        
        try {
            Conexao.begin();            
            
            MultiMap<String, VendaHistoricoIMP> organizados;
            organizados = organizar(historicoVenda);
            historicoVenda.clear();
            
            System.gc();
            
            ProgressBar.setStatus("Gravando histórico de vendas...");
            MultiMap<String, ProdutoAnteriorVO> codigoAnterior; 
            Map<Long, Integer> eans;
            if (utilizarEAN) {
                eans = new ProdutoAutomacaoDAO().getEansCadastrados();
                codigoAnterior = null;
            } else {
                eans = null;
                anteriorDAO.setImportSistema(this.importSistema);
                anteriorDAO.setImportLoja(this.importLoja);
                anteriorDAO.atualizarCodigoAnterior();
                codigoAnterior = anteriorDAO.getCodigoAnterior();
            }
            
            boolean haDivergencia = false;
            int total = organizados.size(), cont1 = 0, cont2 = 0;
            for (VendaHistoricoIMP imp: organizados.values()) {
                
                boolean gravar;
                int idProduto = 0;
                
                
                if (utilizarEAN) {
                    Long ean = Utils.stringToLong(imp.getEan());
                    Integer id = eans.get(ean);
                    if (id != null) {
                        idProduto = (int) id;
                        gravar = true;
                    } else {
                        gravar = false;
                        LOG.warning("Produto: " + imp.getIdProduto() + " ean: " + imp.getEan() + " data: " + imp.getData() + " qtd: " + imp.getQuantidade() + " valor total: " + imp.getValorTotal() + " não encontrado");
                        haDivergencia = true;
                    }
                } else {
                    ProdutoAnteriorVO anterior = codigoAnterior.get(
                            this.importSistema,
                            this.importLoja,
                            imp.getIdProduto()
                    );
                    if (anterior != null) {
                        if (anterior.getCodigoAtual() != null) {
                            idProduto = anterior.getCodigoAtual().getId();
                        }
                        gravar = true;
                    } else {
                        gravar = false;
                        LOG.warning("Produto: " + imp.getIdProduto() + " ean: " + imp.getEan() + " data: " + imp.getData() + " qtd: " + imp.getQuantidade() + " valor total: " + imp.getValorTotal() + " não encontrado");
                        haDivergencia = true;
                    }
                }
                
                if (gravar && idProduto > 0) {
                    VendaHistoricoVO vo = new VendaHistoricoVO();
                
                    vo.setId_loja(this.idLojaVR);
                    vo.setId_produto(idProduto);
                    vo.setData(imp.getData());
                    vo.setPrecoVenda(imp.getPrecoVenda());
                    vo.setQuantidade(imp.getQuantidade());
                    vo.setId_comprador(1);
                    if (imp.getCustoComImposto() == 0 && imp.getCustoSemImposto() > 0) {
                        vo.setCustoComImposto(imp.getCustoSemImposto());
                        vo.setCustoSemImposto(imp.getCustoSemImposto());
                    } else if (imp.getCustoComImposto() > 0 && imp.getCustoSemImposto() == 0) {
                        vo.setCustoComImposto(imp.getCustoComImposto());
                        vo.setCustoSemImposto(imp.getCustoComImposto());
                    } else {
                        vo.setCustoComImposto(imp.getCustoComImposto());
                        vo.setCustoSemImposto(imp.getCustoSemImposto());
                    }
                    vo.setPisCofins(imp.getPisCofinsDebito());
                    vo.setPisCofinsCredito(imp.getPisCofinsCredito());
                    vo.setOperacional(imp.getOperacional());
                    vo.setIcmsCredito(imp.getIcmsCredito());
                    vo.setIcmsDebito(imp.getIcmsDebito());
                    vo.setValorTotal(imp.getValorTotal());
                    vo.setOferta(imp.isOferta());
                    vo.setPerda(0);
                    vo.setCustoMedioSemImposto(0);
                    vo.setCustoMedioComImposto(0);
                    vo.setPisCofinsCredito(imp.getPisCofinsCredito());
                    vo.setCupomfiscal(imp.isCupomFiscal());
                    
                    dao.salvar(vo);
                } else {
                    System.out.println("Produto: " + imp.getIdProduto() + " ean: " + imp.getEan() + " data: " + imp.getData() + " qtd: " + imp.getQuantidade() + " valor total: " + imp.getValorTotal() + " não gravado");
                    LOG.fine("Produto: " + imp.getIdProduto() + " ean: " + imp.getEan() + " data: " + imp.getData() + " qtd: " + imp.getQuantidade() + " valor total: " + imp.getValorTotal() + " não gravado");
                }
                
                cont1++;
                cont2++;
                if (cont2 >= 1000) {
                    ProgressBar.setStatus("Gravando histórico de vendas..." + cont1 + "/" + total);
                    cont2 = 0;
                }
            }
            
            if (!haDivergencia) {
                Conexao.commit();
            } else {
                throw new Exception("Há divergencias na importação");
            }
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public MultiMap<String, VendaHistoricoIMP> organizar(List<VendaHistoricoIMP> historicoVenda) throws Exception {
        MultiMap<String, VendaHistoricoIMP> result = new MultiMap<>();
        
        ProgressBar.setStatus("Organizando a listagem...");
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        int total = historicoVenda.size(), cont1 = 0, cont2 = 0;
        for (VendaHistoricoIMP imp: historicoVenda) {
            VendaHistoricoIMP get = result.get(
                    imp.getIdProduto(), 
                    format.format(imp.getData()),
                    String.valueOf(idLojaVR),
                    String.format("%.2f", imp.getPrecoVenda()),
                    String.format("%.2f", imp.getCustoComImposto()),
                    String.valueOf(imp.isCupomFiscal())
            );
            
            if (get != null) {
                get.setQuantidade(get.getQuantidade() + imp.getQuantidade());
                get.setValorTotal(get.getValorTotal() + imp.getValorTotal());
            } else {
                result.put(
                        imp,
                        imp.getIdProduto(), 
                        format.format(imp.getData()),
                        String.valueOf(idLojaVR),
                        String.format("%.2f", imp.getPrecoVenda()),
                        String.format("%.2f", imp.getCustoComImposto()),
                        String.valueOf(imp.isCupomFiscal())
                );
            }
            
            cont1++;
            cont2++;
            if (cont2 >= 1000) {
                ProgressBar.setStatus("Organizando a listagem..." + cont1 + "/" + total);
                cont2 = 0;
            }
        }

        return result;
    }
    
    
    
}
