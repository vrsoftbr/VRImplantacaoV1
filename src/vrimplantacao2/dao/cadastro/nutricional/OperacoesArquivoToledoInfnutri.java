/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.nutricional;

import java.util.ArrayList;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;

/**
 *
 * @author Desenvolvimento
 */
public class OperacoesArquivoToledoInfnutri {

    Utils util = new Utils();
    NutricionalRepositoryProvider provider;

    public List<NutricionalToledoVO> getNutricionalToledoINFNUTRI(String arquivo) throws Exception {
        ProgressBar.setStatus("Carregando dados Toledo...");
        List<NutricionalToledoVO> result = new ArrayList<>();
        List<String> vToledo = util.lerArquivoBalanca(arquivo);

        boolean isLayoutMgv6 = !vToledo.isEmpty() && vToledo.get(0).startsWith("N");

        if (isLayoutMgv6) {
            for (int i = 0; i < vToledo.size(); i++) {
                NutricionalToledoVO vo = new NutricionalToledoVO();
                if (!vToledo.get(i).trim().isEmpty()) {

                    if (provider.getOpcaoCodigo() == 1) {
                        vo.setId(Utils.stringToInt(vToledo.get(i).substring(1, 7)));
                    } else {
                        vo.setId(Utils.stringToInt(vToledo.get(i).substring(2, 7)));
                    }

                    System.out.println("ID NUTRI: " + vo.getId());
                    vo.setQuantidade(Utils.stringToInt(vToledo.get(i).substring(8, 11)));
                    vo.setId_tipounidadeporcao(Utils.stringToInt(vToledo.get(i).substring(11, 12)));
                    vo.setMedidainteira(Utils.stringToInt(vToledo.get(i).substring(13, 14)));
                    vo.setId_tipomedidadecimal(Utils.stringToInt(vToledo.get(i).substring(14, 15)));
                    vo.setId_tipomedida(Utils.stringToInt(vToledo.get(i).substring(15, 17)));
                    vo.setCaloria(Utils.stringToInt(vToledo.get(i).substring(17, 21)));
                    vo.setCarboidrato(Utils.stringToLong(vToledo.get(i).substring(21, 25)) / 10);
                    vo.setProteina(Utils.stringToDouble(vToledo.get(i).substring(25, 28)) / 10);
                    vo.setGordura(Utils.stringToDouble(vToledo.get(i).substring(28, 31)) / 10);
                    vo.setGordurasaturada(Utils.stringToDouble(vToledo.get(i).substring(31, 34)) / 10);
                    vo.setGorduratrans(Utils.stringToDouble(vToledo.get(i).substring(34, 37)) / 10);
                    vo.setFibra(Utils.stringToDouble(vToledo.get(i).substring(38, 40)) / 10);
                    vo.setSodio(Utils.stringToDouble(vToledo.get(i).substring(41, 45)) / 10);
                    vo.setId_situacaocadastro(1);

                    result.add(vo);
                }
            }
        } else {
            for (int i = 0; i < vToledo.size(); i++) {
                NutricionalToledoVO vo = new NutricionalToledoVO();
                StringLine ln = new StringLine(vToledo.get(i));
                if (!ln.isEmpty()) {
                    vo.setId(ln.sbi(4));//CCCC
                    vo.setIdProduto(vo.getId());
                    ln.jump(1);//A - RESERVADO
                    vo.setQuantidade(ln.sbi(3));//BBB
                    vo.setId_tipounidadeporcao(ln.sbi(1));//D
                    vo.setMedidainteira(ln.sbi(2));//EE
                    vo.setId_tipomedidadecimal(ln.sbi(1));//F
                    switch (ln.sbi(2)) {//GG
                        case 0:
                            vo.setId_tipomedida(0);
                            break;//0	"Colher(es) de Sopa"
                        case 1:
                            vo.setId_tipomedida(1);
                            break;//1	"Colher(es) de Café"
                        case 2:
                            vo.setId_tipomedida(2);
                            break;//2	"Colher(es) de Chá"
                        case 3:
                            vo.setId_tipomedida(3);
                            break;//3	"Xícara(s)"
                        case 4:
                            vo.setId_tipomedida(4);
                            break;//4	"De Xícaras"
                        case 5:
                            vo.setId_tipomedida(5);
                            break;//5	"Unidades(s)"
                        case 6:
                            vo.setId_tipomedida(6);
                            break;//6	"Pacote(s)"
                        case 7:
                            vo.setId_tipomedida(7);
                            break;//7	"Fatia(s)"
                        case 8:
                            vo.setId_tipomedida(8);
                            break;//8	"Fatia(s) Fina(s)"
                        case 9:
                            vo.setId_tipomedida(9);
                            break;//9	"Pedaço(s)"
                        case 10:
                            vo.setId_tipomedida(10);
                            break;//10	"Folha(s)"
                        case 11:
                            vo.setId_tipomedida(11);
                            break;//11	"Pão(es)"
                        case 12:
                            vo.setId_tipomedida(12);
                            break;//12	"Biscoito(s)"
                        case 13:
                            vo.setId_tipomedida(13);
                            break;//13	"Bisnaguinha(s)"
                        case 14:
                            vo.setId_tipomedida(14);
                            break;//14	"Disco(s)"
                        case 15:
                            vo.setId_tipomedida(15);
                            break;//15	"Copo(s)"
                        case 16:
                            vo.setId_tipomedida(16);
                            break;//16	"Porção(ões)"
                        case 17:
                            vo.setId_tipomedida(17);
                            break;//17	"Tablete(s)"
                        case 18:
                            vo.setId_tipomedida(18);
                            break;//18	"Sachê(S)"
                        case 19:
                            vo.setId_tipomedida(19);
                            break;//19	"Almôndega(s)"
                        case 20:
                            vo.setId_tipomedida(20);
                            break;//20	"Bife(s)"
                        case 21:
                            vo.setId_tipomedida(21);
                            break;//21	"Filé(s)"
                        case 22:
                            vo.setId_tipomedida(22);
                            break;//22	"Concha(s)"
                        case 23:
                            vo.setId_tipomedida(23);
                            break;//23	"Bala(s)"
                        case 24:
                            vo.setId_tipomedida(24);
                            break;//24	"Prato(s) Fundo(s)"
                        case 25:
                            vo.setId_tipomedida(25);
                            break;//25	"Pitada(s)"
                        case 26:
                            vo.setId_tipomedida(26);
                            break;//26	"Lata(s)"
                    }

                    vo.setCaloria(ln.sbi(4));//HHHH
                    vo.setCarboidrato(ln.sbd(3));//III
                    vo.setCarboidratoinferior(ln.sbb(1));//J
                    vo.setProteina(ln.sbd(2));//LL
                    vo.setProteinainferior(ln.sbb(1));//M
                    vo.setGordura(ln.sbd(3, 1));//NNN
                    vo.setGordurasaturada(ln.sbd(3, 1));//OOO
                    ln.jump(3);//Colesterou::PPPUtils.stringToDouble(vToledo.get(i).substring(40, 43)) / 10
                    vo.setFibra(ln.sbd(2));//QQ
                    vo.setFibrainferior(ln.sbb(1));//R
                    vo.setCalcio(ln.sbd(3, 1));//SSS
                    vo.setFerro(ln.sbd(4, 2));//TTTT
                    vo.setSodio(ln.sbd(4));//UUUU
                    vo.setPercentualcaloria(ln.sbi(2));//VV
                    vo.setPercentualcarboidrato(ln.sbi(2));//XX
                    vo.setPercentualproteina(ln.sbi(2));//ZZ
                    vo.setPercentualgordura(ln.sbi(2));//WW
                    vo.setPercentualgordurasaturada(ln.sbi(2));//YY
                    ln.jump(2);//Percentual Colesterou: KK                    
                    vo.setPercentualfibra(ln.sbi(2));//&&
                    vo.setPercentualcalcio(ln.sbi(2));//##
                    vo.setPercentualferro(ln.sbi(2));//**
                    vo.setPercentualsodio(ln.sbi(2));//$$

                    result.add(vo);
                }
            }
        }
        return result;
    }
}
