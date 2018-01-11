/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.VRException;
import vrimplantacao.vo.vrimplantacao.TipoPisCofinsVO;

/**
 *
 * @author lucasrafael
 */
public class PisCofinsDAO {

    public Map<String, TipoPisCofinsVO> carregar(int cstSaida, int cstEntrada) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;
        Map<String, TipoPisCofinsVO> vPisCofins = new HashMap<>();

        try {
            stm = Conexao.createStatement();

            sql = new StringBuilder();

            sql.append("SELECT * FROM tipopiscofins WHERE cst IN (" + cstSaida + ", " + cstEntrada + ")");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                TipoPisCofinsVO oTipoPisCofins = new TipoPisCofinsVO();
                oTipoPisCofins.id = rst.getInt("id");

                if (rst.getInt("cst") == cstSaida) {
                    vPisCofins.put("saida", oTipoPisCofins);
                } else if (rst.getInt("cst") == cstEntrada) {
                    vPisCofins.put("entrada", oTipoPisCofins);
                }

            }

            if (!vPisCofins.containsKey("entrada")) {
                throw new VRException("Tipo PIS/COFINS entrada (CST " + cstEntrada + ") não cadastrado!");
            }

            if (!vPisCofins.containsKey("saida")) {
                throw new VRException("Tipo PIS/COFINS saída (CST " + cstSaida + ") não cadastrado!");
            }

            return vPisCofins;

        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        } finally {
            Conexao.destruir(null, stm, rst);
        }

    }
    
    public Map<String, TipoPisCofinsVO> carregarIsento() throws Exception {
        return carregar(7, 71);

    }

    public Map<String, TipoPisCofinsVO> carregarOutros() throws Exception {
        return carregar(49, 99);

    }

    public Map<String, TipoPisCofinsVO> carregarTributado() throws Exception {
        return carregar(1, 50);

    }
}
