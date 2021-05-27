package vrimplantacao2.dao.cadastro.produto;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.PisCofinsVO;

public class PisCofinsDAO {
    /**
     * PIS/COFINS de crédito padrão.
     */
    public static final PisCofinsVO PISCOFINS_CREDITO_PADRAO = new PisCofinsVO(13, "ISENTO (E)", 71, true);
    /**
     * PIS/COFINS de debito padrão;
     */
    public static final PisCofinsVO PISCOFINS_DEBITO_PADRAO = new PisCofinsVO(1, "ISENTO", 7, false);
    private MultiMap<Comparable, PisCofinsVO> pisConfins;
    private MultiMap<Integer, NaturezaReceitaVO> naturezaReceita;

    /**
     * Retorna o PIS/COFINS de Crédito através do cst.
     * @param cst Cst do PIS/COFINS de crédito.
     * @return PIS/COFINS localizado ou caso contrário null.
     * @throws Exception 
     */
    public PisCofinsVO getPisConfisCredito(int cst) throws Exception {
        if (pisConfins == null) {
            atualizaPisCofins();
        }
        
        return pisConfins.get(true, cst);
    }
    
    /**
     * Retorna o PIS/COFINS de Débito através do cst.
     * @param cst Cst do PIS/COFINS de débito.
     * @return PIS/COFINS localizado ou caso contrário null.
     * @throws Exception 
     */
    public PisCofinsVO getPisConfisDebito(int cst) throws Exception {
        if (pisConfins == null) {
            atualizaPisCofins();
        }
        
        return pisConfins.get(false, cst);
    }    
    
    /**
     * Retorna a Natureza da Receita cadastrada no VR, validando também se para
     * aquele cst existe uma Natureza válida.
     * @param cstDebito Cst de Débito.
     * @param natureza Código da natureza da receita.
     * @return Natureza da receita encontrada ou null quando não.
     * @throws Exception
     */
    public NaturezaReceitaVO getNaturezaReceita(int cstDebito, int natureza) throws Exception {
        if (naturezaReceita == null) {
            atualizaNaturezaReceita();
        }        
        return naturezaReceita.get(cstDebito, natureza);
    }
    
    /**
     * Atualiza a listagem das naturezas da receita.
     * @throws Exception
     */
    public void atualizaNaturezaReceita() throws Exception {
        naturezaReceita = new MultiMap<>(2);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select \n" +
                "	id, \n" +
                "	cst, \n" +
                "	codigo, \n" +
                "	descricao\n" +
                "from \n" +
                "	tiponaturezareceita \n" +
                "order by \n" +
                "	cst, \n" +
                "	codigo"    
            )) {
                while (rst.next()) {
                    naturezaReceita.put(
                            new NaturezaReceitaVO(
                                    rst.getInt("id"), 
                                    rst.getInt("cst"), 
                                    rst.getInt("codigo"), 
                                    rst.getString("descricao")
                            )
                            , rst.getInt("cst"), rst.getInt("codigo"));
                }
            }
        }
    }
    
    /**
     * Atualiza a listagem do PIS/COFINS
     * @throws Exception 
     */
    public void atualizaPisCofins() throws Exception {
        pisConfins = new MultiMap<>(2);
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id,\n" +
                "	descricao,\n" +
                "	cst,\n" +
                "	id_tipoentradasaida = 0 credito\n" +
                "from tipopiscofins\n" +
                "order by cst, id desc"    
            )) {
                while (rst.next()) {
                    PisCofinsVO pisCofins = new PisCofinsVO(
                            rst.getInt("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getBoolean("credito"));
                    pisConfins.put(pisCofins, pisCofins.isCredito(), pisCofins.getCst());
                }
            }
        }
    }

    public Map<Integer, Integer> getPisCofinsByCst() throws Exception {
        Map<Integer, Integer> result = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "	id,\n" +
                "	cst\n" +
                "from tipopiscofins"   
            )) {
                while (rst.next()) {
                    result.put(rst.getInt("cst"), rst.getInt("id"));
                }
            }
        }
        
        return result;
    }
}
