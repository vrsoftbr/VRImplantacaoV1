//package vrimplantacao2.dao.interfaces;
//
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.io.UnsupportedEncodingException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.text.DecimalFormat;
//
///**
// * Sistema da empresa G3 Tecnológia
// *
// * @author Leandro
// */
//public class GTechEncriptDAO {
//
//    public static String decodeSenha(String senha) {
//        return Decriptar.decode(senha);
//    }
//
//    public static void main(String[] args) {
//        System.out.println(decodeSenha("00081f6ZHowOQ==5e165MVhOVzFK3351U1RKamVr93ad"));
//    }
//
//}
//
//class Decriptar {
//
//    public static String decode(String s)
//            throws ArrayIndexOutOfBoundsException {
//        try {
//            Integer divisor = Integer.valueOf(Integer.parseInt(s.substring(0, 4)));
//
//            int M1 = 4;
//            int M2 = 5;
//            s = s.substring(7, s.length() - 4);
//            String b1 = s.substring(s.length() - divisor.intValue(), s.length());
//            String b2 = s.substring(s.length() - M1 - divisor.intValue() - divisor.intValue(), s.length() - divisor.intValue() - M1);
//            String b3 = s.substring(0, s.length() - M1 - M2 - divisor.intValue() - divisor.intValue());
//            s = new String(DatatypeConverter.parseBase64Binary(new String(DatatypeConverter.parseBase64Binary(new String(DatatypeConverter.parseBase64Binary(b1 + b2 + b3))))));
//        } catch (Exception e) {
//            try {
//                s = decode(Encriptar.encode(s));
//            } catch (Exception ex) {
//                Logger.getLogger(Decriptar.class.getName()).log(Level.SEVERE, null, ex);
//                return s;
//            }
//        }
//        return s;
//    }
//
//    public static String basicDecode(String s)
//            throws ArrayIndexOutOfBoundsException {
//        try {
//            String tmp = s.substring(5);
//            tmp = tmp.substring(0, tmp.length() - 5);
//            return new String(DatatypeConverter.parseBase64Binary(tmp));
//        } catch (Exception e) {
//        }
//        return s;
//    }
//}
//
//class Encriptar {
//
//    public static String encode(String s)
//            throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        if (s == null) {
//            return "";
//        }
//        String MD5Quebrado = Biblioteca.MD5String(s).substring(0, 16);
//
//        String base643Vezes = DatatypeConverter.printBase64Binary(
//                DatatypeConverter.printBase64Binary(
//                        DatatypeConverter.printBase64Binary(s.getBytes()).getBytes()).getBytes());
//        Integer divisor = Integer.valueOf(Integer.valueOf(base643Vezes.length()).intValue() / 3);
//        String b1 = base643Vezes.substring(0, divisor.intValue());
//        String b2 = base643Vezes.substring(divisor.intValue(), divisor.intValue() + divisor.intValue());
//        String b3 = base643Vezes.substring(divisor.intValue() + divisor.intValue(), base643Vezes.length());
//        return String.format("%04d", new Object[]{divisor}) + MD5Quebrado.substring(0, 3) + b3 + MD5Quebrado
//                .substring(3, 8) + b2 + MD5Quebrado
//                .substring(8, 12) + b1 + MD5Quebrado
//                .substring(12, 16);
//    }
//
//    public static String basicEncode(String s)
//            throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        if (s == null) {
//            return "";
//        }
//        String md5 = Biblioteca.MD5String(s);
//        String md5part1 = md5.substring(0, 5);
//        String md5part2 = md5.substring(10, 15);
//        String b1 = DatatypeConverter.printBase64Binary(s.getBytes());
//        return md5part1 + b1 + md5part2;
//    }
//}
//
//class Biblioteca {
//
//    private static String convertToHex(byte[] data) {
//        StringBuilder buf = new StringBuilder();
//        for (int i = 0; i < data.length; i++) {
//            int halfbyte = data[i] >>> 4 & 0xF;
//            int two_halfs = 0;
//            do {
//                if ((0 <= halfbyte) && (halfbyte <= 9)) {
//                    buf.append((char) (48 + halfbyte));
//                } else {
//                    buf.append((char) (97 + (halfbyte - 10)));
//                }
//                halfbyte = data[i] & 0xF;
//            } while (two_halfs++ < 1);
//        }
//        return buf.toString();
//    }
//
//    public static String formatoDecimal(String tipo, double valor) {
//        String mascara = "0.";
//        switch (tipo) {
//            case "Q":
//                for (int i = 0; i < 3; i++) {
//                    mascara = mascara + "0";
//                }
//                break;
//            case "V":
//                for (int i = 0; i < 2; i++) {
//                    mascara = mascara + "0";
//                }
//        }
//        DecimalFormat formato = new DecimalFormat(mascara);
//        return formato.format(valor);
//    }
//
//    public static String MD5String(String text)
//            throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        MessageDigest md = MessageDigest.getInstance("MD5");
//
//        md.update(text.getBytes("iso-8859-1"), 0, text.length());
//        byte[] md5hash = md.digest();
//        return convertToHex(md5hash);
//    }
//
//    public static String repete(String string, int quantidade) {
//        StringBuilder retorno = new StringBuilder();
//        for (int j = 0; j < quantidade; j++) {
//            retorno.append(string);
//        }
//        return retorno.toString();
//    }
//}
