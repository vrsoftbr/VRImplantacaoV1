package vrimplantacao2.dao.cadastro.mercadologico;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoAnteriorVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoVO;

/**
 *
 * @author Leandro
 */
public class MercadologicoRepositoryProvider {
    
    private final String sistema;
    private final String lojaOrigem;
    private final int lojaVR;
    private MercadologicoDAO mercadologicoDAO;
    private MercadologicoAnteriorDAO mercadologicoAnteriorDAO;

    public MercadologicoRepositoryProvider(String sistema, String lojaOrigem, int lojaVR) throws Exception {
        this.lojaOrigem = lojaOrigem;
        this.lojaVR = lojaVR;
        this.sistema = sistema;
        this.mercadologicoDAO = new MercadologicoDAO();
        this.mercadologicoAnteriorDAO = new MercadologicoAnteriorDAO();
        this.mercadologicoAnteriorDAO.createTable();
    }

    public String getSistema() {
        return sistema;
    }

    public String getLojaOrigem() {
        return lojaOrigem;
    }

    public int getLojaVR() {
        return lojaVR;
    }

    public void gravarMercadologico(MercadologicoVO vo) throws Exception {
        mercadologicoDAO.salvar(vo);
    }

    public void gravarMercadologico(MercadologicoAnteriorVO vo) throws Exception {
        mercadologicoAnteriorDAO.salvar(vo);
    }

    public int getNextMercadologico1() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select coalesce(max(mercadologico1) + 1, 1) merc from mercadologico"
            )) {
                rst.next();
                return rst.getInt("merc");                
            }
        }
    }

    public int getNextMercadologico2(int mercadologico1) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	coalesce(max(mercadologico2) + 1, 1) merc\n" +
                    "from \n" +
                    "	mercadologico\n" +
                    "where\n" +
                    "	mercadologico1 = " + mercadologico1
            )) {
                rst.next();
                return rst.getInt("merc");                
            }
        }
    }

    public int getNextMercadologico3(int mercadologico1, int mercadologico2) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	coalesce(max(mercadologico3) + 1, 1) merc\n" +
                    "from \n" +
                    "	mercadologico\n" +
                    "where\n" +
                    "	mercadologico1 = " + mercadologico1 + " and\n" +
                    "	mercadologico2 = " + mercadologico2
            )) {
                rst.next();
                return rst.getInt("merc");                
            }
        }
    }

    public int getNextMercadologico4(int mercadologico1, int mercadologico2, int mercadologico3) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	coalesce(max(mercadologico4) + 1, 1) merc\n" +
                    "from \n" +
                    "	mercadologico\n" +
                    "where\n" +
                    "	mercadologico1 = " + mercadologico1 + " and\n" +
                    "	mercadologico2 = " + mercadologico2 + " and\n" +
                    "	mercadologico3 = " + mercadologico3
            )) {
                rst.next();
                return rst.getInt("merc");                
            }
        }
    }

    public int getNextMercadologico5(int mercadologico1, int mercadologico2, int mercadologico3, int mercadologico4) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	coalesce(max(mercadologico5) + 1, 1) merc\n" +
                    "from \n" +
                    "	mercadologico\n" +
                    "where\n" +
                    "	mercadologico1 = " + mercadologico1 + " and\n" +
                    "	mercadologico2 = " + mercadologico2 + " and\n" +
                    "	mercadologico3 = " + mercadologico3 + " and\n" +
                    "	mercadologico4 = " + mercadologico4
            )) {
                rst.next();
                return rst.getInt("merc");                
            }
        }
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

    public void setStatus(String status) throws Exception {
        ProgressBar.setStatus(status);
    }

    public void gerarAAcertar(int nivelMaximo) throws Exception {
        mercadologicoDAO.gerarAAcertar(nivelMaximo);
    }
    
    public void excluir() throws Exception {
        mercadologicoDAO.excluir();
    }
    
}
