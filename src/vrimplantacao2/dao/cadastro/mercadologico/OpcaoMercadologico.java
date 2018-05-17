/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.mercadologico;

import java.util.List;
import vrimplantacao2.vo.importacao.MercadologicoIMP;

/**
 *
 * @author lucasrafael
 */
public enum OpcaoMercadologico {
    DESC_MERCADOLOGICO1 {
        @Override
        public String toString() {
            return "Descrição Mercadológico 1";
        }
    },
    DESC_MERCADOLOGICO2 {
        @Override
        public String toString() {
            return "Descrição Mercadológico 2";
        }
    },
    DESC_MERCADOLOGICO3 {
        @Override
        public String toString() {
            return "Descrição Mercadológico 3";
        }
    },
    DESC_MERCADOLOGICO4 {
        @Override
        public String toString() {
            return "Descrição Mercadológico 4";
        }
    },
    DESC_MERCADOLOGICO5 {
        @Override
        public String toString() {
            return "Descrição Mercadológico 5";
        }
    };
    
    private List<MercadologicoIMP> listaEspecial;

    public List<MercadologicoIMP> getListaEspecial() {
        return listaEspecial;
    }

    public void setListaEspecial(List<MercadologicoIMP> listaEspecial) {
        this.listaEspecial = listaEspecial;
    }
 }
