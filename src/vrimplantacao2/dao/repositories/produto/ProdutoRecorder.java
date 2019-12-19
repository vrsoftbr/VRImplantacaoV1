package vrimplantacao2.dao.repositories.produto;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import org.openide.util.Exceptions;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.repositories.Recorder;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class ProdutoRecorder extends Recorder<ProdutoIMP> {
    
    private final JdbcConnectionSource source;
    private final Dao<ProdutoIMP, Long> dao;

    public ProdutoRecorder() {
        super();
        try {
            this.source = Parametros.get().getEmpresaAtiva().getSource();
            this.dao = DaoManager.createDao(source, ProdutoIMP.class);
            this.dao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    dao.executeRawNoArgs("create schema if not exists miglog;");
                    dao.executeRawNoArgs("create sequence if not exists miglog.produto_id_seq;");
                    TableUtils.createTableIfNotExists(source, ProdutoIMP.class);
                    dao.deleteBuilder().delete();
                    return null;
                }
            });
        } catch (Exception ex) {
            ex.getCause().printStackTrace();
            throw new RuntimeException(ex.getLocalizedMessage(), ex.getCause());
        }
    }

    @Override
    public void add(ProdutoIMP imp) {
        try {
            this.dao.create(imp);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
}
