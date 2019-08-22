
package vrimplantacao2.dao.cadastro.venda;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 * Dao que controla a gravação do {@link VendaItemIMP}.
 * @author Leandro
 */
public class VendaItemImpDao {    
    
    private static final Logger LOG = Logger.getLogger(VendaItemImpDao.class.getName());
    
    private final Dao<VendaItemIMP, String> dao;
    
    public VendaItemImpDao(JdbcConnectionSource source) throws SQLException {
        this.dao = DaoManager.createDao(source, VendaItemIMP.class);
        this.dao.setObjectCache(false);
    }

    /**
     * Retorna uma listagem com todas as {@link VendaItemIMP} 
     * de um {@link VendaIMP} do banco de dados implantação, buscando através do
     * {@link VendaIMP#getId()} da vendas.
     * @param vendaId
     * @return
     * @throws SQLException 
     */
    public List<VendaItemIMP> getVendaItens(String vendaId) throws SQLException {
        QueryBuilder<VendaItemIMP, String> queryItem = dao.queryBuilder()
                        .orderBy("sequencia", true)
                        .orderBy("id", true);
        return queryItem.where().eq("venda_id", vendaId).query();
    }
    
    /**
     * Grava uma listagem de {@link VendaItemIMP} no banco de dados implantação.
     * @param itens
     * @throws SQLException 
     */
    public void persistir(List<VendaItemIMP> itens) throws Exception {
        TableUtils.createTableIfNotExists(dao.getConnectionSource(), VendaItemIMP.class);
               
        dao.callBatchTasks(new GravarItensTransaction(itens));
    }
    
    /**
     * Grava uma listagem de {@link VendaItemIMP} no banco de dados implantação.
     * @param iterator
     * @throws SQLException 
     */
    public void persistir(Iterator<VendaItemIMP> iterator) throws Exception {
        TableUtils.createTableIfNotExists(dao.getConnectionSource(), VendaItemIMP.class);
                               
        dao.callBatchTasks(new GravarItensTransaction(iterator));
    }
    
    /**
     * Transação para gravar uma listagem de {@link VendaItemIMP}.
     */
    private class GravarItensTransaction implements Callable<Void> {
        
        private List<VendaItemIMP> itens = null;
        private Iterator<VendaItemIMP> iterator = null;

        public GravarItensTransaction(List<VendaItemIMP> itens) {
            this.itens = itens;
        }
        public GravarItensTransaction(Iterator<VendaItemIMP> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Void call() throws Exception {
            VendaItemIMP it = null;
            try {
                
                dao.executeRawNoArgs("delete from vendaitem");
                LOG.fine("Tabela de itens da venda esvaziada no banco temporario");

                ProgressBar.setStatus("Vendas...gravando itens no banco temporário...");
                
                if (iterator != null) {
                    int cont = 0, cont2 = 0;
                    
                    while (iterator.hasNext()) {
                        dao.create(iterator.next());
                        cont++;
                        cont2++;
                        if (cont2 == 10000) {
                            cont2 = 0;
                            ProgressBar.setStatus("Vendas...gravando itens no banco temporário..." + cont);
                        }
                    }
                } else {
                    ProgressBar.setMaximum(itens.size());
                
                    for (VendaItemIMP item: itens) {
                        it = item;
                        dao.create(item);
                        ProgressBar.next();
                    }
                }

                System.gc();                                
                LOG.fine("Itens das vendas gravadas no banco temporário");

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Erro ao gerar o banco " + (it != null ? it.toString() + " - " + it.getId() : "") + e.getMessage(), e);
                if (e.getCause() != null) {                    
                    throw (Exception) e.getCause();
                } else {
                    throw e;
                }
            }

            return null;
            
        }
    
    }
    
}
