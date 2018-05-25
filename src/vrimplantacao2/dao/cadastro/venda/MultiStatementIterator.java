package vrimplantacao2.dao.cadastro.venda;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro
 * @param <T>
 */
public class MultiStatementIterator<T> implements Iterator<T> {
    
    private static final Logger LOG = Logger.getLogger(MultiStatementIterator.class.getName());
    
    private final Queue<String> statements = new LinkedList<>();
    private Statement activeStatement;
    private ResultSet activeRst;    
    private String sql;
    private NextBuilder<T> nextBuilder;
    private StatementBuilder statementBuilder;
    private boolean nextExecutado = false;
    private boolean hasNext = false;
    private boolean rstFechado = true;

    public MultiStatementIterator(NextBuilder<T> nextBuilder, StatementBuilder statementBuilder) {
        if (nextBuilder == null || statementBuilder == null) {
            throw new NullPointerException("Nenhum dos parâmetros pode ser nulo.");
        }
        this.nextBuilder = nextBuilder;
        this.statementBuilder = statementBuilder;
    }
    
    public void addStatement(String statement) {
        this.statements.add(statement);
    }

    @Override
    public boolean hasNext() {
        try {                        
            callNext();
            return hasNext;            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T next() {
        try {
            callNext();
            try {
                return nextBuilder.makeNext(activeRst);
            } finally {
                nextExecutado = false;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Erro no hasNext()\n" + sql, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private void callNext() throws Exception {
        try {
            if (!nextExecutado) {
                if (activeStatement == null) {
                    nextStatement();
                }
                hasNext = activeRst.next();            
                if (!hasNext) {
                    nextStatement();
                    if (!rstFechado) {
                        hasNext = activeRst.next();
                    }
                }

                nextExecutado = true;
            }
        } catch (Exception e) {
            System.out.println(sql);
            throw e;
        }
    }

    private void nextStatement() throws Exception {
        
        if (activeRst != null) {
            activeRst.close();
            rstFechado = true;
        }
        if (activeStatement != null) {
            activeStatement.close();
        }
        
        System.gc();
        
        sql = statements.poll();
        if (sql != null) {
            LOG.fine("Script executado: " + sql);
            activeStatement = this.statementBuilder.makeStatement();
            activeRst = activeStatement.executeQuery(sql);
            rstFechado = false;
        }
    }
    
    public static interface StatementBuilder {
        public Statement makeStatement() throws Exception;
    }
    
    public static interface NextBuilder<T> {
        public T makeNext(ResultSet rst) throws Exception;
    }
}
