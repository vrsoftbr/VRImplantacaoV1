/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.util.ArrayList;
import java.util.List;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author lucasrafael
 */
public class RootacDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Rootac";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        return null;
    }
}
