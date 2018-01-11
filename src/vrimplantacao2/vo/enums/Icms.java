package vrimplantacao2.vo.enums;

import java.sql.ResultSet;
import java.sql.Statement;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;

/**
 * Classe que representa o ICMS.
 * @author Leandro
 */
public class Icms {
    
    private static Icms isento;
    
    private final int id;
    private final String descricao;
    private final int cst;
    private final double aliquota;
    private final double reduzido;

    public Icms(int id, String descricao, int cst, double aliquota, double reduzido) {
        this.id = id;
        this.descricao = descricao;
        this.cst = cst;
        this.aliquota = aliquota;
        this.reduzido = reduzido;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCst() {
        return cst;
    }

    public double getAliquota() {
        return aliquota;
    }

    public double getReduzido() {
        return reduzido;
    }
    
    private static MultiMap<Comparable, Icms> icms;
    
    /**
     * Retorna o icms correspondente ao cst, aliquota e redução, caso não 
     * nenhum correspondente retorna o icms que possui "ISENTO" como descrição.
     * @param cst Cst do icms.
     * @param aliquota Aliquota do icms.
     * @param reduzido Redução do icms.
     * @return {@link Icms} encontrado ou null.
     * @throws Exception 
     */
    public static Icms getIcms(int cst, double aliquota, double reduzido) throws Exception {
        Icms result = getIcmsPorValor(cst, aliquota, reduzido);
        if (result == null) {
            return getIsento();
        } else {
            return result;
        }
    }
    
    /**
     * Retorna o icms correspondente ao cst, aliquota e redução.
     * @param cst Cst do icms.
     * @param aliquota Aliquota do icms.
     * @param reduzido Redução do icms.
     * @return {@link Icms} encontrado ou null.
     * @throws Exception 
     */
    public static Icms getIcmsPorValor(int cst, double aliquota, double reduzido) throws Exception {
        if (icms == null) {
            atualizaIcms();
        }
        aliquota = MathUtils.trunc(aliquota, 1);
        reduzido = MathUtils.trunc(reduzido, 1);
        if (cst != 20 && cst != 0) {
            aliquota = 0;
            reduzido = 0;
        }
        Icms icm = icms.get(cst, aliquota, reduzido);        
        return icm;
    }

    /**
     * Retorna o Icms do isento.
     * @return Icms isento.
     * @throws Exception 
     */
    public static Icms getIsento() throws Exception {
        if (isento == null) {
            atualizaIcms();
        }
        return isento;
    }
    
    /**
     * Atualiza os valores da listagem.
     * @throws Exception 
     */
    public static void atualizaIcms() throws Exception {
        icms = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id,\n" +
                "	descricao,\n" +
                "	situacaotributaria,\n" +
                "	porcentagem,\n" +
                "	reduzido\n" +
                "from \n" +
                "	aliquota \n" +
                "where\n" +
                "       id_situacaocadastro = 1\n" +
                "order by \n" +
                "	situacaotributaria, \n" +
                "	porcentagem, \n" +
                "	reduzido"
            )) {
                while (rst.next()) {
                    double aliq = rst.getDouble("porcentagem");
                    double red = rst.getDouble("reduzido");
                    int cst = rst.getInt("situacaotributaria");
                    Icms icm = new Icms(
                            rst.getInt("id"), 
                            rst.getString("descricao"), cst, 
                            aliq, 
                            red);
                    
                    aliq = MathUtils.trunc(aliq, 1);
                    red = MathUtils.trunc(red, 1);
                    
                    icms.put(icm, cst, aliq, red);
                }
            }
            
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id,\n" +
                "	descricao,\n" +
                "	situacaotributaria,\n" +
                "	porcentagem,\n" +
                "	reduzido\n" +
                "from \n" +
                "	aliquota \n" +
                "where upper(descricao) like '%ISENTO%'\n" +
                "order by \n" +
                "	situacaotributaria, \n" +
                "	porcentagem, \n" +
                "	reduzido"
            )) {
                while (rst.next()) {
                    double aliq = rst.getDouble("porcentagem");
                    double red = rst.getDouble("reduzido");
                    int cst = rst.getInt("situacaotributaria");
                    isento = new Icms(
                            rst.getInt("id"), 
                            rst.getString("descricao"), cst, 
                            aliq, 
                            red);
                }
            }
        }
    }
    
}
