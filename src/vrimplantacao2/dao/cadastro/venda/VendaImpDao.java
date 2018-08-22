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

/**
 * Dao que controla a gravação do {@link VendaIMP}.
 * @author Leandro
 */
public class VendaImpDao {
    
    private static final Logger LOG = Logger.getLogger(VendaImpDao.class.getName());
    private final Dao<VendaIMP, String> dao;

    public VendaImpDao(JdbcConnectionSource source) throws SQLException {
        this.dao = DaoManager.createDao(source, VendaIMP.class);
        this.dao.setObjectCache(false);
    }

    /**
     * Retorna uma listagem com todas as {@link VendaIMP} do banco de dados implantação.
     * @return Lista com as vendas encontradas.
     * @throws java.sql.SQLException Caso ocorra um erro.
     */
    public Iterator<VendaIMP> getVendas() throws SQLException {
        QueryBuilder<VendaIMP, String> query = dao.queryBuilder().orderBy("id", true);
        return query.iterator();
    }
    
    /**
     * Grava uma listagem de {@link VendaIMP} no banco de dados implantação.
     * @param vendas Vendas a serem persistidas.
     * @throws SQLException 
     */
    public void persistir(List<VendaIMP> vendas) throws Exception {
        TableUtils.createTableIfNotExists(dao.getConnectionSource(), VendaIMP.class);

        dao.callBatchTasks(new GravarVendasTransaction(vendas));
    }
    
    /**
     * Grava uma listagem de {@link VendaIMP} no banco de dados implantação.
     * @param iterator Vendas a serem persistidas.
     * @throws SQLException 
     */
    public void persistir(Iterator<VendaIMP> iterator) throws Exception {
        TableUtils.createTableIfNotExists(dao.getConnectionSource(), VendaIMP.class);

        dao.callBatchTasks(new GravarVendasTransaction(iterator));
    }

    public long getCount() throws SQLException {
        return dao.countOf();
    }
    
    /**
     * Classe de transação para gravar as vendas.
     */
    private class GravarVendasTransaction implements Callable<Void> {

        private List<VendaIMP> vendas = null;
        private Iterator<VendaIMP> iterator = null;

        public GravarVendasTransaction(List<VendaIMP> vendas) {
            this.vendas = vendas;
        }
        
        public GravarVendasTransaction(Iterator<VendaIMP> iterator) {
            this.iterator = iterator;
        }
        
        @Override
        public Void call() throws Exception {
            VendaIMP imp = null;
            try {        
                
                ProgressBar.setStatus("Vendas...gravando vendas no banco temporário...");
                dao.executeRawNoArgs("delete from venda");
                LOG.fine("Tabela de venda esvaziada no banco temporario");

                if (iterator != null) {
                    int cont = 0, cont2 = 0;
                    while (iterator.hasNext()) {
                        dao.create(iterator.next());
                        cont++;
                        cont2++;
                        if (cont2 == 10000) {
                            cont2 = 0;
                            ProgressBar.setStatus("Vendas...gravando vendas no banco temporário..." + cont);
                        }
                    }
                } else {                    
                    ProgressBar.setMaximum(vendas.size());
                    for (VendaIMP venda: vendas) {
                        imp = venda;
                        dao.create(venda);
                        ProgressBar.next();
                    }
                    vendas.clear();
                }                
                System.gc();

                LOG.fine("Vendas gravadas no banco temporário");

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Erro ao gerar o banco " + (imp != null ? imp.toString() : ""), e);                
                throw e;
            }

            return null;
        }
    
    }
    
}
