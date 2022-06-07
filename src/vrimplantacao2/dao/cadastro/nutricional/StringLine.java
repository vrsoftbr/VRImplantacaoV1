/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

import vrimplantacao.utils.Utils;

/**
 *
 * @author Michael
 */
public class StringLine {

    private final String ln;
    private int index = 0;

    StringLine(String ln) {
        this.ln = ln == null ? "" : ln.trim();
    }

    boolean isEmpty() {
        return ln.isEmpty();
    }

    String sb(int length) {
        int i;
        if (this.ln.length() >= index + length) {
            i = length;
        } else {
            i = ln.length() - index;
        }

        if (i == 0) {
            return "";
        }

        int iniPos = index;
        jump(i);
        return ln.substring(iniPos, index);
    }

    int sbi(int length) {
        return Utils.stringToInt(sb(length));
    }

    double sbd(int length) {
        return Utils.stringToDouble(sb(length));
    }

    double sbd(int length, int decimal) {
        double fator = Math.pow(10, decimal);
        return Utils.stringToDouble(sb(length)) / fator;
    }

    boolean sbb(int length) {
        return Utils.stringToBool(sb(length));
    }

    void jump(int i) {
        index += i;
    }
}
