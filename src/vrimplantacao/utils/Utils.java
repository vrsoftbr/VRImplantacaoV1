/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.utils;

import vrimplantacao.classe.ConexaoPostgres;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openide.util.Exceptions;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrimplantacao.classe.Global;

/**
 *
 * @author implantacao
 */
public class Utils {

    public static String VALOR_VAZIO = "";
    public static String TELEFONE_VAZIO = "0000000000";
    public static long LONG_PADRAO = 0l;

    /**
     * Verifica se o schema informado, existe no banco de dados.
     *
     * @param schema Nome do schema a ser localizado.
     * @return True se existir, False caso não.
     */
    public static boolean existeSchema(String schema) throws Exception {
        try (PreparedStatement stm = Conexao.prepareStatement(
                "select * from information_schema.columns where \n"
                + "    upper(table_schema) = upper(?);"
        )) {
            stm.setString(1, schema);
            try (ResultSet rst = stm.executeQuery()) {
                return rst.next();
            }
        }
    }

    public static String stringLong(String celular) {
        if (celular != null) {
            long val = Utils.stringToLong(celular);
            return String.valueOf(val);
        }
        return "0";
    }

    /**
     * Formata números de telefone, incluindo 
     * @param ddd
     * @param telefone
     * @return 
     */
    public static String formataTelefone(int ddd, String telefone) {
        telefone = Utils.stringLong(telefone);
        if ("0".equals(telefone)) {
            return "";
        }
        if (telefone.length() <= 9) {
            return ddd + telefone;
        }
        return telefone;
    }

    /**
     * Converte um texto em {@link Long} removendo os caracteres não numéricos.
     * @param texto Texto a ser convertido.
     * @return {@link Long} convertido ou null caso a string seja null ou ""
     */
    public static Long toLong(String texto) {
        texto = Utils.formataNumero(texto, "");
        int size = String.valueOf(Long.MAX_VALUE).length();
        if (texto.length() > size) {
            texto = texto.substring(0, size);
        }
        return !texto.equals(VALOR_VAZIO) ? Long.parseLong(texto) : null;
    }
    
    /**
     * Converte um texto em {@link Long} removendo os caracteres não numéricos.
     * @param texto Texto a ser convertido.
     * @return {@link Long} convertido ou null caso a string seja null ou ""
     */
    public static Integer toInteger(String texto) {
        Long result = Utils.toLong(texto);
        if (result != null && result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE) {
            return result.intValue();
        } else {
            return null;
        }
    }

    public static double calcularSemImposto(double i_custoComImposto, double i_icmsCredito, double i_pisCofins, double i_valorIva, double i_valorIpi) throws Exception {
        double custoSemImposto = ((i_custoComImposto - i_valorIva - i_valorIpi) * ((100 - i_icmsCredito - i_pisCofins) / 100)) + i_valorIva + i_valorIpi;
        return Util.round(custoSemImposto, 4);
    }

    /**
     * Verifica se a tabela existe no banco de dados.
     *
     * @param schema Schema ao qual pertence a tabela.
     * @param tabela Nome da tabela a ser localizada.
     * @return True se existir, False caso não.
     */
    public static boolean existeTabela(String schema, String tabela) throws Exception {
        try (PreparedStatement stm = Conexao.prepareStatement(
                "select * from information_schema.columns where \n"
                + "    upper(table_schema) = upper(?) and\n"
                + "    upper(table_name) = upper(?);"
        )) {
            stm.setString(1, schema);
            stm.setString(2, tabela);
            try (ResultSet rst = stm.executeQuery()) {
                return rst.next();
            }
        }
    }

    /**
     * Verifica se o campo existe no banco de dados.
     *
     * @param schema Schema ao qual pertence a tabela.
     * @param tabela Tabela a qual pertence o campo.
     * @param campo Campo a ser localizado.
     * @return True se existir, False caso não.
     */
    public static boolean existeCampo(String schema, String tabela, String campo) throws Exception {
        try (PreparedStatement stm = Conexao.prepareStatement(
                "select * from information_schema.columns where \n"
                + "    upper(table_schema) = upper(?) and\n"
                + "    upper(table_name) = upper(?) and\n"
                + "    upper(column_name) = upper(?);"
        )) {
            stm.setString(1, schema);
            stm.setString(2, tabela);
            stm.setString(3, campo);
            try (ResultSet rst = stm.executeQuery()) {
                return rst.next();
            }
        }
    }

    public static double arredondar(double valor, int qtd) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            valor = 0;
        }

        BigDecimal valorExato = new BigDecimal(String.valueOf(valor)).setScale(qtd, RoundingMode.HALF_UP);
        return valorExato.doubleValue();
    }

    /**
     * Trunca o número double informado. Este método utiliza uma abordagem
     * diferente para truncar o número, utilizando Strings e vetor para isso.
     *
     * @param valor Valor double a ser truncado.
     * @param qtdDecimal Quantidade de casas decimais a serem retornadas.
     * @return
     */
    public static double truncar2(double valor, int qtdDecimal) {
        if (qtdDecimal < 0) {
            qtdDecimal = 0;
        }
        String[] numeros = String.valueOf(valor).replace(",", ".").split("\\.");
        //Se for um numero inteiro apenas retorna o valor informado
        if (numeros.length < 2) {
            return valor;
        } else {
            int indexDecimal = numeros.length - 1;

            String inteiro = "";
            for (int i = 0; i < indexDecimal; i++) {
                inteiro += numeros[i];
            }

            if (qtdDecimal == 0) {
                return Double.parseDouble(inteiro);
            }

            String decimal = numeros[indexDecimal];
            if (qtdDecimal > decimal.length()) {
                qtdDecimal = decimal.length();
            }

            return Double.parseDouble(inteiro + "." + decimal.substring(0, qtdDecimal));
        }
    }

    @Deprecated
    public static double truncar(double valor, int qtdDecimal) {
        if (valor < Long.MIN_VALUE) {
            valor = Long.MIN_VALUE;
        } else if (valor > Long.MAX_VALUE) {
            valor = Long.MAX_VALUE;
        }
        if (qtdDecimal < 0) {
            qtdDecimal = 0;
        }
        /*double fator = Math.pow(10, qtdDecimal);
        
         if (valor > 0) {
         return Math.ceil( valor * fator ) / fator;
         } else {
         return Math.floor(valor * fator ) / fator;
         }*/

        BigDecimal bd = new BigDecimal(valor).setScale(qtdDecimal, RoundingMode.DOWN);

        return bd.doubleValue();
    }

    public static long gerarEan13(long i_codigo, boolean i_digito) throws Exception {
        String codigo = String.format("%012d", i_codigo);

        int somaPar = 0;
        int somaImpar = 0;

        for (int i = 0; i < 12; i += 2) {
            somaImpar += Integer.parseInt(String.valueOf(codigo.charAt(i)));
            somaPar += Integer.parseInt(String.valueOf(codigo.charAt(i + 1)));
        }

        int soma = somaImpar + (3 * somaPar);
        int digito = 0;
        boolean verifica = false;
        int calculo = 0;

        do {
            calculo = soma % 10;

            if (calculo != 0) {
                digito += 1;
                soma += 1;
            }
        } while (calculo != 0);

        if (i_digito) {
            return Long.parseLong(codigo + digito);
        } else {
            return Long.parseLong(codigo);
        }
    }

    /**
     * Converte valores String que representam as embalagens no sistema
     * concorrente em integer de acordo com o VR.
     *
     * @param embalagem String da embalagem.
     * @return id da embalagem no VR. Se nada for encontrado retorna 0 = UNIDADE
     */
    public static int converteTipoEmbalagem(String embalagem) {
        switch (Utils.acertarTexto(embalagem).toUpperCase()) {
            case "CX":
                return 1;
            case "KG":
                return 4;
            default:
                return 0;
        }
    }

    /**
     * Retorna o id de um estado brasileiro através da sua sigla.
     *
     * @param sigla Sigla do estado.
     * @return id do Estado.
     */
    public static int getEstadoPelaSigla(String sigla) {
        sigla = Utils.acertarTexto(sigla, 2);
        switch (sigla) {
            case "AC":
                return 12;
            case "AL":
                return 27;
            case "AM":
                return 13;
            case "AP":
                return 16;
            case "BA":
                return 29;
            case "CE":
                return 23;
            case "DF":
                return 53;
            case "ES":
                return 32;
            case "EX":
                return 99;
            case "GO":
                return 52;
            case "MA":
                return 21;
            case "MG":
                return 31;
            case "MS":
                return 50;
            case "MT":
                return 51;
            case "PA":
                return 15;
            case "PB":
                return 25;
            case "PE":
                return 26;
            case "PI":
                return 22;
            case "PR":
                return 41;
            case "RJ":
                return 33;
            case "RN":
                return 24;
            case "RO":
                return 11;
            case "RR":
                return 14;
            case "RS":
                return 43;
            case "SC":
                return 42;
            case "SE":
                return 28;
            case "SP":
                return 35;
            case "TO":
                return 17;
            default:
                return Global.idEstado;
        }
    }

    /**
     * Coloca aspas simples ao redor de uma string não nula. Utilize para
     * auxiliar na criação de SQL.
     *
     * @param string string a ser tratada.
     * @return string = 'string', se for null retorna null.
     */
    public static String quoteSQL(String string) {
        return string != null ? "'" + string + "'" : null;
    }

    /**
     * Formata datas para o padrão do Postgres.
     *
     * @param data
     * @return String com a data formatada ou null se null for passado com
     * parâmetro.
     */
    public static String dateSQL(Date data) {
        if (data != null) {
            return Utils.quoteSQL(Utils.formatDate(data));
        }
        return null;
    }

    public static String formatDate(Date data) {
        if (data != null) {
            return new SimpleDateFormat("yyyy-MM-dd").format(data);
        }
        return null;
    }

    public static String longIntSQL(long number, long nullValue) {
        return number != nullValue ? number + "" : null;
    }

    public static String timestampSQL(Date datahora) {
        if (datahora != null) {
            return Utils.quoteSQL(Utils.formatTimestamp(datahora));
        }
        return null;
    }

    public static String formatTimestamp(Date datahora) {
        if (datahora != null) {
            return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(datahora);
        }
        return null;
    }

    /**
     * Executa as formatações necessárias a um cep
     *
     * @param string
     * @return
     */
    public static long formatCep(String strCep) {
        String cep = formataNumero(strCep);
        int charInicial = cep.length() - 8;
        if (charInicial < 0) {
            charInicial = 0;
        }
        cep = cep.substring(charInicial);
        return stringToLong(cep);
    }

    /**
     * Converte uma string em boolean. C
     *
     * @param bool
     * @return
     */
    public static boolean stringToBool(String bool) {
        if (bool == null) {
            return false;
        } else {
            bool = Utils.acertarTexto(bool);
            return bool.contains("TRUE") || bool.contains("1") || bool.contains("S");
        }
    }

    /**
     * Retorna a data atual do sistema.
     *
     * @return Data atual do computador.
     */
    public static java.sql.Date getDataAtual() {
        return new java.sql.Date(new java.util.Date().getTime());
    }

    /**
     * Converte uma string em uma data de acordo com o formato passado.
     *
     * @param formato Formato da data. Ex "yyyy-MM-dd hh:mm:ss".
     * @param data Data em formato String.
     * @return Data convertida em java.sql.Date ou null caso a data ou o formato
     * seja nulo.
     * @throws ParseException Caso não seja possível a conversão, retorna esta
     * Exception.
     */
    public static java.sql.Date convertStringToDate(String formato, String data) throws ParseException {
        if (data != null && formato != null) {
            return new java.sql.Date(new SimpleDateFormat(formato).parse(data).getTime());
        }
        return null;
    }

    public List<String> lerArquivoBalanca(String file) throws Exception {

        List<String> vDadosArquivo = new ArrayList<>();

        BufferedReader br = null;
        String linha = "";

        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            while ((linha = br.readLine()) != null) {

                vDadosArquivo.add(linha);
            }

            return vDadosArquivo;

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public List<String> lerArquivo(String file) throws Exception {

        List<String> vDadosArquivo = new ArrayList<>();

        BufferedReader br = null;
        String linha = "";

        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

            while ((linha = br.readLine()) != null) {

                vDadosArquivo.add(linha);
            }

            return vDadosArquivo;

        } catch (Exception ex) {

            throw ex;
        }
    }
    /**
     * Determina se o valor informado é um cnpj/cpf válido.
     *
     * @param cnpjCpf cnpj/cpf a ser validado.
     * @return true se for um cnpj/cpf válido e false caso não.
     */
    public static boolean isCnpjCpfValido(long cnpjCpf) {
        //TODO: Incluir a validação de cnpj e cpf
        return String.valueOf(cnpjCpf).length() <= 14;
    }

    /**
     * Determina se o valor informado é um cnpj/cpf válido.
     *
     * @param cnpjCpf cnpj/cpf a ser validado.
     * @return true se for um cnpj/cpf válido e false caso não.
     */
    public static boolean isCnpjCpfValido(String strCnpj) {
        if (strCnpj == null) {
            strCnpj = "";
        }
        boolean resultado = ((strCnpj.length() == 11
                && (strCnpj.equals("11111111111")
                && strCnpj.equals("22222222222")
                && strCnpj.equals("33333333333")
                && strCnpj.equals("44444444444")
                && strCnpj.equals("55555555555")
                && strCnpj.equals("66666666666")
                && strCnpj.equals("77777777777")
                && strCnpj.equals("88888888888")
                && strCnpj.equals("99999999999")))
                || (strCnpj.length() == 14
                && (strCnpj.equals("11111111111111")
                && strCnpj.equals("22222222222222")
                && strCnpj.equals("33333333333333")
                && strCnpj.equals("44444444444444")
                && strCnpj.equals("55555555555555")
                && strCnpj.equals("66666666666666")
                && strCnpj.equals("77777777777777")
                && strCnpj.equals("88888888888888")
                && strCnpj.equals("99999999999999"))));

        return resultado;
    }

    public static long stringToLong(String texto, long valorPadrao) {
        texto = Utils.formataNumero(texto);
        int size = 18;
        if (texto.length() > size) {
            texto = texto.substring(0, size);
        }
        return !texto.equals(VALOR_VAZIO) ? Long.parseLong(texto) : LONG_PADRAO;
    }

    public static double stringToDouble(String texto, double valorPadrao) {
        if (texto != null && !texto.isEmpty()) {

            texto = texto.trim();
            String numeropode = "0123456789,.", novonumero = "";

            boolean negativo = texto.startsWith("-");

            for (int i = 0; i < texto.length(); i++) {
                if (numeropode.indexOf(texto.charAt(i)) != -1) {
                    novonumero = novonumero + texto.charAt(i);
                } else {
                    novonumero = novonumero + "";
                }
            }

            novonumero = novonumero.replace(",", ".");

            if ("".equals(novonumero)) {
                return valorPadrao;
            } else {
                String[] val = novonumero.split("\\.");
                String valor;

                if (val.length > 1) {
                    String decimal = val[val.length - 1];
                    String inteiro = "";
                    for (int i = 0; i < val.length - 1; i++) {
                        inteiro += val[i];
                    }
                    valor = inteiro + "." + decimal;
                } else {
                    valor = novonumero;
                }
                if (negativo) {
                    valor = "-" + valor;
                }
                return Double.parseDouble(valor);
            }
        }
        return valorPadrao;
    }

    public static double stringToDouble(String texto) {
        return Utils.stringToDouble(texto, 0);
    }

    public static long stringToLong(String texto) {
        return Utils.stringToLong(texto, LONG_PADRAO);
    }

    /**
     * Conver uma String em um número inteiro (int). No processo todos os
     * caracteres não numéricos são removidos da String.
     *
     * @param texto Valor a ser convertido.
     * @return valor convertido ou 0 caso o valor informado não seja um Interger
     * válido.
     */
    public static int stringToInt(String texto) {
        return stringToInt(texto, 0);
    }    

    /**
     * Conver uma String em um número inteiro (int). No processo todos os
     * caracteres não numéricos são removidos da String.
     *
     * @param texto Valor a ser convertido.
     * @param valorPadrao Valor padrão caso não seja possível converter em número.
     * @return valor convertido ou 0 caso o valor informado não seja um Interger
     * válido.
     */
    public static int stringToInt(String texto, int valorPadrao) {
        long result = Utils.stringToLong(texto, valorPadrao);
        if (result >= Integer.MIN_VALUE && result <= Integer.MAX_VALUE) {
            return (int) result;
        } else {
            return valorPadrao;
        }
    }

    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @param tamanho Tamanho máximo do campo.
     * @param stringPadrao Caso o texto seja nulo ou vazio, essa string é
     * utilizada.
     * @return Texto padronizado
     */
    public static String acertarTexto(String texto, int tamanho, String stringPadrao) {
        if (tamanho < 0) {
            tamanho = 0;
        }

        texto = acertarTexto(texto);

        if ((texto != null)
                && (!texto.trim().isEmpty())) {

            if (texto.length() > tamanho) {
                texto = texto.substring(0, tamanho);
            }
        } else {
            texto = stringPadrao != null ? stringPadrao : VALOR_VAZIO;
        }

        return texto;
    }
    
    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @param tamanho Tamanho máximo do campo.
     * @param stringPadrao Caso o texto seja nulo ou vazio, essa string é
     * utilizada.
     * @return Texto padronizado
     */
    public static String acertarTextoMultiLinha(String texto, int tamanho, String stringPadrao) {
        if (tamanho < 0) {
            tamanho = 0;
        }

        texto = acertarTextoMultiLinha(texto);

        if ((texto != null)
                && (!texto.trim().isEmpty())) {

            if (texto.length() > tamanho) {
                texto = texto.substring(0, tamanho);
            }
        } else {
            texto = stringPadrao != null ? stringPadrao : VALOR_VAZIO;
        }

        return texto;
    }

    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @param tamanho Tamanho máximo do campo.
     * @return Texto padronizado.
     */
    public static String acertarTexto(String texto, int tamanho) {
        return Utils.acertarTexto(texto, tamanho, VALOR_VAZIO);
    }
    
    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @param tamanho Tamanho máximo do campo.
     * @return Texto padronizado.
     */
    public static String acertarTextoMultiLinha(String texto, int tamanho) {
        return Utils.acertarTextoMultiLinha(texto, tamanho, VALOR_VAZIO);
    }

    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @param stringPadrao Caso o texto seja nulo ou vazio, essa string é
     * utilizada.
     * @return Texto padronizado.
     */
    public static String acertarTexto(String texto, String stringPadrao) {
        return Utils.acertarTexto(texto, 10000000, stringPadrao);
    }
    
    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @param stringPadrao Caso o texto seja nulo ou vazio, essa string é
     * utilizada.
     * @return Texto padronizado.
     */
    public static String acertarTextoMultiLinha(String texto, String stringPadrao) {
        return Utils.acertarTextoMultiLinha(texto, 10000000, stringPadrao);
    }

    /**
     * Método para padronizar as informações de uma string.
     *
     * @param texto Texto a ser ajustado.
     * @return Texto padronizado
     */
    public static String acertarTexto(String texto) {
        if (texto != null && !texto.isEmpty()) {
            String vRetorno = "", textoAcertado = "",
                    strPode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ?!@#%()-_=+[{]}.>,<'|'";

            if (!"".equals(texto)) {
                texto = texto.replace("$", " ");
                texto = texto.replace("&", " ");
                texto = texto.replace("€", "C");
                texto = texto.replace("a", "A");
                texto = texto.replace("á", "A");
                texto = texto.replace("à", "A");
                texto = texto.replace("ã", "A");
                texto = texto.replace("â", "A");
                texto = texto.replace("Á", "A");
                texto = texto.replace("À", "A");
                texto = texto.replace("Ã", "A");
                texto = texto.replace("Â", "A");
                texto = texto.replace("b", "B");
                texto = texto.replace("c", "C");
                texto = texto.replace("ç", "C");
                texto = texto.replace("Ç", "C");
                texto = texto.replace("d", "D");
                texto = texto.replace("e", "E");
                texto = texto.replace("é", "E");
                texto = texto.replace("ê", "E");
                texto = texto.replace("È", "E");
                texto = texto.replace("É", "E");
                texto = texto.replace("Ê", "E");
                texto = texto.replace("f", "F");
                texto = texto.replace("g", "G");
                texto = texto.replace("h", "H");
                texto = texto.replace("i", "I");
                texto = texto.replace("í", "I");
                texto = texto.replace("Í", "I");
                texto = texto.replace("j", "J");
                texto = texto.replace("k", "K");
                texto = texto.replace("l", "L");
                texto = texto.replace("m", "M");
                texto = texto.replace("n", "N");
                texto = texto.replace("o", "O");
                texto = texto.replace("ó", "O");
                texto = texto.replace("õ", "O");
                texto = texto.replace("ô", "O");
                texto = texto.replace("ö", "O");
                texto = texto.replace("Ó", "O");
                texto = texto.replace("Õ", "O");
                texto = texto.replace("Ô", "O");
                texto = texto.replace("Ö", "O");
                texto = texto.replace("p", "P");
                texto = texto.replace("q", "Q");
                texto = texto.replace("r", "R");
                texto = texto.replace("s", "S");
                texto = texto.replace("t", "T");
                texto = texto.replace("u", "U");
                texto = texto.replace("ú", "U");
                texto = texto.replace("Ú", "U");
                texto = texto.replace("v", "V");
                texto = texto.replace("x", "X");
                texto = texto.replace("y", "Y");
                texto = texto.replace("w", "W");
                texto = texto.replace("z", "Z");
                texto = texto.replace("´", "");
                texto = texto.replace("º", "R");
                texto = texto.replace("¦", "R");
                texto = texto.replace("°", " ");
                texto = texto.replace("ª", " ");
                texto = texto.replace("Ñ", "N");
                texto = texto.replace("§", "");
                texto = texto.replace("\"", "");
                texto = texto.replace(";", "");
                texto = texto.replace("ﾓ", "O");
                texto = texto.replace("ﾁ", "A");
                texto = texto.replace(":", " ");
                texto = texto.replace("/", " ");
                texto = texto.replace("\\", " ");
                texto = texto.replace("'", "");
                texto = texto.replace("`", "");
                texto = texto.replace("´", "");
                texto = texto.replace("*", "");

                for (int i = 0; i < texto.length(); i++) {
                    if (strPode.indexOf(texto.charAt(i)) != -1) {
                        textoAcertado = textoAcertado + texto.charAt(i);
                    } else {
                        textoAcertado = textoAcertado + "?";
                    }
                }
            }
            vRetorno = textoAcertado.trim();

            try {
                byte[] bytes = vRetorno.getBytes();

                vRetorno = new String(bytes, "ISO-8859-1");

                return vRetorno;
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
                throw new RuntimeException(ex.getMessage(), ex);
            }
        } else {
            return VALOR_VAZIO;
        }
    }
    
    private static final char[] ACENTUADO = new char[] {'Á','À','Â','Ã','Ç','É','È','Ê','Í','Ì','Î','Ñ','Ó','Ò','Ô','Õ','Ö','\r','Ú','Ù','Û','Ü'};
    private static final char[] SEM_ACENTO = new char[]{'A','A','A','A','C','E','E','E','I','I','I','N','O','O','O','O','O',' ' ,'U','U','U','U'};
    
    /**
     * Método para padronizar as informações de uma string utilizada para campos
     * de observação no VR.
     *
     * @param texto Texto a ser ajustado.
     * @param tamanho Tamanho máximo do campo.
     * @param stringPadrao Caso o texto seja nulo ou vazio, essa string é
     * utilizada.
     * @return Texto padronizado
     */
    public static String acertarObservacao(String texto, int tamanho, String stringPadrao) {
        if (tamanho < 0) {
            tamanho = 0;
        }

        texto = acertarObservacao(texto);

        if ((texto != null)
                && (!texto.trim().isEmpty())) {

            if (texto.length() > tamanho) {
                texto = texto.substring(0, tamanho);
            }
        } else {
            texto = stringPadrao != null ? stringPadrao : VALOR_VAZIO;
        }

        return texto.trim();
    }
    
    /**
     * Método para padronizar as informações de uma string utilizada para campos
     * de observação no VR.
     *
     * @param texto Texto a ser ajustado.
     * @param tamanho Tamanho máximo do campo.
     * @return Texto padronizado
     */
    public static String acertarObservacao(String texto, int tamanho) {
        return acertarObservacao(texto, tamanho, "");
    }
    
    /**
     * Método para padronizar as informações de uma string utilizada para campos
     * de observação no VR.
     *
     * @param texto Texto a ser ajustado.
     * @return Texto padronizado
     */
    public static String acertarObservacao(String texto) {
        if (texto == null) { 
            texto = ""; 
        }
            
        texto = texto.toUpperCase();
        
        StringBuilder b = new StringBuilder();
        
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            for (int j = 0; j < ACENTUADO.length; j++) {
                if (c == ACENTUADO[j]) {
                    c = SEM_ACENTO[j];
                }
            }
            b.append(c);
        }

        try {
            byte[] bytes = b.toString().getBytes();

            String vRetorno = new String(bytes, "ISO-8859-1");

            return vRetorno;
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
    
    public static String acertarTextoMultiLinha(String texto) {
        if (texto != null && !texto.isEmpty()) {
            String vRetorno = "", textoAcertado = "",
                    strPode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ?!@#%()-_=+[{]}.>,<'|'\n\r";

            if (!"".equals(texto)) {
                texto = texto.replace("$", " ");
                texto = texto.replace("&", " ");
                texto = texto.replace("€", "C");
                texto = texto.replace("a", "A");
                texto = texto.replace("á", "A");
                texto = texto.replace("à", "A");
                texto = texto.replace("ã", "A");
                texto = texto.replace("â", "A");
                texto = texto.replace("Á", "A");
                texto = texto.replace("À", "A");
                texto = texto.replace("Ã", "A");
                texto = texto.replace("Â", "A");
                texto = texto.replace("b", "B");
                texto = texto.replace("c", "C");
                texto = texto.replace("ç", "C");
                texto = texto.replace("Ç", "C");
                texto = texto.replace("d", "D");
                texto = texto.replace("e", "E");
                texto = texto.replace("é", "E");
                texto = texto.replace("ê", "E");
                texto = texto.replace("È", "E");
                texto = texto.replace("É", "E");
                texto = texto.replace("Ê", "E");
                texto = texto.replace("f", "F");
                texto = texto.replace("g", "G");
                texto = texto.replace("h", "H");
                texto = texto.replace("i", "I");
                texto = texto.replace("í", "I");
                texto = texto.replace("Í", "I");
                texto = texto.replace("j", "J");
                texto = texto.replace("k", "K");
                texto = texto.replace("l", "L");
                texto = texto.replace("m", "M");
                texto = texto.replace("n", "N");
                texto = texto.replace("o", "O");
                texto = texto.replace("ó", "O");
                texto = texto.replace("õ", "O");
                texto = texto.replace("ô", "O");
                texto = texto.replace("ö", "O");
                texto = texto.replace("Ó", "O");
                texto = texto.replace("Õ", "O");
                texto = texto.replace("Ô", "O");
                texto = texto.replace("Ö", "O");
                texto = texto.replace("p", "P");
                texto = texto.replace("q", "Q");
                texto = texto.replace("r", "R");
                texto = texto.replace("s", "S");
                texto = texto.replace("t", "T");
                texto = texto.replace("u", "U");
                texto = texto.replace("ú", "U");
                texto = texto.replace("Ú", "U");
                texto = texto.replace("v", "V");
                texto = texto.replace("x", "X");
                texto = texto.replace("y", "Y");
                texto = texto.replace("w", "W");
                texto = texto.replace("z", "Z");
                texto = texto.replace("´", "");
                texto = texto.replace("º", "R");
                texto = texto.replace("¦", "R");
                texto = texto.replace("°", " ");
                texto = texto.replace("ª", " ");
                texto = texto.replace("Ñ", "N");
                texto = texto.replace("§", "");
                texto = texto.replace("\"", "");
                texto = texto.replace(";", "");
                texto = texto.replace("ﾓ", "O");
                texto = texto.replace("ﾁ", "A");
                texto = texto.replace(":", " ");
                texto = texto.replace("/", " ");
                texto = texto.replace("\\", " ");
                texto = texto.replace("'", "");
                texto = texto.replace("`", "");
                texto = texto.replace("´", "");
                texto = texto.replace("*", "");

                for (int i = 0; i < texto.length(); i++) {
                    if (strPode.indexOf(texto.charAt(i)) != -1) {
                        textoAcertado = textoAcertado + texto.charAt(i);
                    } else {
                        textoAcertado = textoAcertado + "?";
                    }
                }
            }
            vRetorno = textoAcertado.trim();

            try {
                byte[] bytes = vRetorno.getBytes();

                vRetorno = new String(bytes, "ISO-8859-1");

                return vRetorno;
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
                throw new RuntimeException(ex.getMessage(), ex);
            }
        } else {
            return VALOR_VAZIO;
        }
    }

    /**
     * Remove os caracteres estranhos e verifica se é um email válido, caso
     * contrário retorna "".
     *
     * @param email Email a ser formatado.
     * @param tamanho Tamanho da string do email.
     * @return email formatado.
     */
    public static String formataEmail(String email, int tamanho) {        
        if (email == null) { 
            email = ""; 
        }
        try {
            byte[] bytes = email.toLowerCase().getBytes();
             email = new String(bytes, "ISO-8859-1");
            
            if (email.contains("@")) {
                return email;
            } else {
                return "";
            }
        } catch (UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Efetua a formatação de strings convertendo para números, ela remove todas
     * todos os caracteres não numericos da string
     *
     * @param numero Número a ser ajustado.
     * @return Número ajustado. Caso seja vazio retorna o VALOR_VAZIO
     */
    public static String formataNumero(String numero) {
        if (numero != null && !numero.isEmpty()) {

            numero = numero.trim();
            String retorno = "0", numeropode = "0123456789", novonumero = "";

            for (int i = 0; i < numero.length(); i++) {
                if (numeropode.indexOf(numero.charAt(i)) != -1) {
                    novonumero = novonumero + numero.charAt(i);
                } else {
                    novonumero = novonumero + "";
                }
            }

            if ("".equals(novonumero)) {
                retorno = "0";
            } else {
                retorno = novonumero;
            }

            return retorno;
        } else {
            return "0";
        }
    }

    public static String formataNumeroParcela(String numero) {
        if (numero != null && !numero.isEmpty()) {

            numero = numero.trim();
            String retorno = "0", numeropode = "0123456789/", novonumero = "";

            for (int i = 0; i < numero.length(); i++) {
                if (numeropode.indexOf(numero.charAt(i)) != -1) {
                    novonumero = novonumero + numero.charAt(i);
                } else {
                    novonumero = novonumero + "";
                }
            }

            if ("".equals(novonumero)) {
                retorno = "0";
            } else {
                retorno = novonumero;
            }

            return retorno;
        } else {
            return "0";
        }
    }
    
    public static String formataNumero(String numero, int tamanho, String stringPadrao) {
        //Substitui os numeros.
        numero = Utils.formataNumero(numero);
        //Ajusta o tamanho.
        if (numero.length() > tamanho) {
            numero = numero.substring(0, tamanho);
        }
        //Se for vazio coloca o valor padrão.
        return numero.equals("0") ? stringPadrao : numero;
    }

    public static String formataNumero(String numero, String stringPadrao) {
        return Utils.formataNumero(numero, 10000000, stringPadrao);
    }

    public static String formataNumero(String numero, int tamanho) {
        return Utils.formataNumero(numero, tamanho, "0");
    }

    /*
     public static String formataTelefone(String numero, int tamanho, String stringPadrao) {
     if (tamanho < 0) {
     tamanho = 0;
     }        
     numero = Utils.formataNumero(numero, tamanho, stringPadrao);
        
     return Utils.acertarTexto(numero);
     }
    
     public static String formataTelefone(String numero, int tamanho) {
     return Utils.formataTelefone(numero, tamanho, TELEFONE_VAZIO);
     }*/
    public String retirarLetra(String numero) {
        String retorno = "0", numeropode = "0123456789.", novonumero = "";

        for (int i = 0; i < numero.length(); i++) {
            if (numeropode.indexOf(numero.charAt(i)) != -1) {
                novonumero = novonumero + numero.charAt(i);
            } else {
                novonumero = novonumero + "";
            }
        }

        if ("".equals(novonumero)) {
            retorno = "0";
        } else {
            retorno = novonumero;
        }

        return retorno;
    }

    // TODO: Incluir um valor padrão para ser retornado caso o municipio não seja encontrado
    public static int retornarMunicipioIBGECodigo(int id_municipio) throws Exception {
        int retorno = 0;
        StringBuilder query = new StringBuilder();
        Statement st = null;
        ResultSet rs = null;

        try {
            Conexao.begin();

            query.append("select id from municipio where id = " + id_municipio);
            st = Conexao.createStatement();
            rs = st.executeQuery(query.toString());

            if (rs.next()) {
                retorno = rs.getInt("id");
            }

            rs.close();
            st.close();

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }

        return retorno;
    }

    public static boolean existeMunicipioIBGECodigo(int id_municipio) throws Exception {
        return Utils.retornarMunicipioIBGECodigo(id_municipio) != 0;
    }

    public static int retornarMunicipioIBGEDescricao(String municipio,
            String estado) throws SQLException, Exception {
        int retorno = Global.idMunicipio;
        Statement stm = null;
        ResultSet rs = null;
        StringBuilder query = new StringBuilder();

        Conexao.begin();

        stm = Conexao.createStatement();

        query.append("select * from municipio ");
        query.append("where descricao = " + Utils.quoteSQL(Utils.acertarTexto(municipio)) + " ");
        query.append("and id_estado = " + retornarEstadoDescricao(estado));
        rs = stm.executeQuery(query.toString());

        if (rs.next()) {
            retorno = rs.getInt("id");
        } else {
            retorno = Global.idMunicipio;
        }

        rs.close();
        stm.close();

        Conexao.commit();

        return retorno;
    }

    public int retornarEstado(String estado) throws SQLException, Exception {
        int retorno = 0;
        StringBuilder query = new StringBuilder();
        PreparedStatement pst = null;
        ResultSet rs = null;
        ConexaoPostgres connPG = new ConexaoPostgres();

        query.append("select * from estado ");
        query.append("where id = ? ");
        pst = connPG.getConexao().prepareStatement(query.toString());
        pst.setInt(1, Integer.parseInt(estado));
        rs = pst.executeQuery();

        if (rs.next()) {
            retorno = rs.getInt("id");
        }

        rs.close();
        pst.close();

        return retorno;
    }

    public static int retornarEstadoDescricao(String estado) throws SQLException, Exception {
        int retorno = 0;
        StringBuilder query = new StringBuilder();

        try (Statement stm = Conexao.createStatement()) {
            query.append("select * from estado ");
            query.append("where sigla = '" + estado + "' ");
            try (ResultSet rs = stm.executeQuery(query.toString())) {
                if (rs.next()) {
                    retorno = rs.getInt("id");
                }
            }
        }

        return retorno;
    }

    public String formatarData(String data) {
        String retorno = "", dia, mes, ano;

        if (data.length() == 10) {
            ano = data.substring(data.length() - 4);
            mes = data.substring(3, 5);
            dia = data.substring(0, 2);
            retorno = ano + "-" + mes + "-" + dia;
        } else {
            retorno = "1990/01/01";
        }

        return retorno;
    }

    public static boolean encontrouLetraCampoNumerico(String valor) {
        boolean retorno = false;
        String letras = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.";

        for (int i = 0; i < valor.length(); i++) {
            if (letras.indexOf(valor.charAt(i)) != -1) {
                retorno = true;
                break;
            }
        }

        return retorno;
    }

    public static int retornarPisCofinsDebito(int csttipopiscofins) {
        int retorno;

        if (csttipopiscofins == 1) {
            retorno = 0;
        } else if (csttipopiscofins == 2) {
            retorno = 5;
        } else if (csttipopiscofins == 3) {
            retorno = 6;
        } else if (csttipopiscofins == 4) {
            retorno = 3;
        } else if (csttipopiscofins == 5) {
            retorno = 2;
        } else if (csttipopiscofins == 6) {
            retorno = 7;
        } else if (csttipopiscofins == 7) {
            retorno = 1;
        } else if (csttipopiscofins == 8) {
            retorno = 8;
        } else if (csttipopiscofins == 9) {
            retorno = 4/*10*/;
        } else if (csttipopiscofins == 49) {
            retorno = 9;
        } else {
            retorno = 1;
        }

        return retorno;
    }

    public static int retornarPisCofinsCredito(int csttipopiscofins) {
        int retorno;

        if (csttipopiscofins == 50) {
            retorno = 12;
        } else if (csttipopiscofins == 51) {
            retorno = 18;
        } else if (csttipopiscofins == 60) {
            retorno = 17;
        } else if (csttipopiscofins == 70) {
            retorno = 15;
        } else if (csttipopiscofins == 71) {
            retorno = 13;
        } else if (csttipopiscofins == 72) {
            retorno = 10 /*4*/;
        } else if (csttipopiscofins == 73) {
            retorno = 19;
        } else if (csttipopiscofins == 74) {
            retorno = 20;
        } else if (csttipopiscofins == 75) {
            retorno = 14;
        } else if (csttipopiscofins == 98) {
            retorno = 21;
        } else if (csttipopiscofins == 99) {
            retorno = 21;
        } else {
            retorno = 13;
        }

        return retorno;
    }

    public int retornarPisCofinsCreditoBoeachatSoft(int csttipopiscofins) {
        int retorno;

        if (csttipopiscofins == 1) {
            retorno = 12;
        } else if (csttipopiscofins == 2) {
            retorno = 18;
        } else if (csttipopiscofins == 3) {
            retorno = 17;
        } else if (csttipopiscofins == 4) {
            retorno = 15;
        } else if (csttipopiscofins == 5) {
            retorno = 13;
        } else if (csttipopiscofins == 6) {
            retorno = 19;
        } else if (csttipopiscofins == 7) {
            retorno = 20;
        } else if (csttipopiscofins == 8) {
            retorno = 14;
        } else if (csttipopiscofins == 9) {
            retorno = 21;
        } else {
            retorno = 13;
        }

        return retorno;
    }

    public static int retornarTipoNaturezaReceita(int piscofinsdebito, String naturezareceita) throws SQLException, Exception {
        int retorno = -1;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        if (naturezareceita == null) {
            naturezareceita = "";
        }

        Conexao.begin();

        stm = Conexao.createStatement();

        sql = new StringBuilder();

        if (!"".equals(naturezareceita.trim())) {
            if ((piscofinsdebito == 0) || (piscofinsdebito == 9)) {
                retorno = -1;
            } else if (piscofinsdebito == 1) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 7 ");
                sql.append("and codigo = " + naturezareceita);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 999;
                }
            } else if (piscofinsdebito == 2) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 5 ");
                sql.append("and codigo = " + naturezareceita);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 409;
                }
            } else if (piscofinsdebito == 3) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 4 ");
                sql.append("and codigo = " + naturezareceita);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 403;
                }
            } else if (piscofinsdebito == 5) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 2 ");
                sql.append("and codigo = " + naturezareceita);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 403;
                }
            } else if (piscofinsdebito == 6) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 3 ");
                sql.append("and codigo = " + naturezareceita);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 940;
                }
            } else if (piscofinsdebito == 7) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 6 ");
                sql.append("and codigo = " + naturezareceita);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 999;
                }
            } else if (piscofinsdebito == 8) {
                sql.append("select * from tiponaturezareceita ");
                sql.append("where cst = 8 ");
                sql.append("and codigo = " + naturezareceita);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    retorno = Integer.parseInt(naturezareceita);
                } else {
                    retorno = 999;
                }
            }
        } else {
            if ((piscofinsdebito == 0) || (piscofinsdebito == 9)) {
                retorno = -1;
            } else if (piscofinsdebito == 1) {
                retorno = 999;
            } else if (piscofinsdebito == 2) {
                retorno = 409;
            } else if (piscofinsdebito == 3) {
                retorno = 403;
            } else if (piscofinsdebito == 5) {
                retorno = 403;
            } else if (piscofinsdebito == 6) {
                retorno = 940;
            } else if (piscofinsdebito == 7) {
                retorno = 999;
            } else if (piscofinsdebito == 8) {
                retorno = 999;
            }
        }

        stm.close();
        Conexao.commit();

        return retorno;
    }

    public int retornarPisCofinsDebitoUnificacao(int csttipopiscofins) {
        int retorno;
        /* --- FUNÇÃO USADA APENAS PARA CLIENTE COM TIPO PISCOFINS DIFERENTE DO CONVENCIONAL
         -- SAIDA
         0 - TRIBUTADO
         2 - SUBSTITUIDO
         3 - MONOFASICO
         7 - ALIQUOTA ZERO 
         9 - OUTRAS OPERACOES
         */
        if (csttipopiscofins == 0) {
            retorno = 0;
        } else if (csttipopiscofins == 2) {
            retorno = 2;
        } else if (csttipopiscofins == 3) {
            retorno = 3;
        } else if (csttipopiscofins == 7) {
            retorno = 7;
        } else if (csttipopiscofins == 9) {
            retorno = 9;
        } else {
            retorno = 1;
        }

        return retorno;
    }

    public int retornarPisCofinsCreditoUnificacao(int csttipopiscofins) {
        int retorno;
        /* --- FUNÇÃO USADA APENAS PARA CLIENTE COM TIPO PISCOFINS DIFERENTE DO CONVENCIONAL
         -- ENTRADA
         12 - TRIBUTADO 12
         13 - NAO USAR 21
         14 - SUBSTITUIDO 14
         15 - MONOFASICO  15
         17 - ENT. BOVINOS 21
         19 - ALIQUOTA ZERO 19
         22 - ENT. SUINOS/AVES 21
         23 - NAO USAR 21
         24 - OUTRAS OPERAÇÕES 21     
         */
        if (csttipopiscofins == 12) {
            retorno = 12;
        } else if (csttipopiscofins == 13) {
            retorno = 21;
        } else if (csttipopiscofins == 14) {
            retorno = 14;
        } else if (csttipopiscofins == 15) {
            retorno = 15;
        } else if (csttipopiscofins == 17) {
            retorno = 21;
        } else if (csttipopiscofins == 19) {
            retorno = 19;
        } else if (csttipopiscofins == 22) {
            retorno = 21;
        } else if (csttipopiscofins == 23) {
            retorno = 21;
        } else if (csttipopiscofins == 24) {
            retorno = 21;
        } else {
            retorno = 13;
        }

        return retorno;
    }

    public boolean validarData(int mes, int dia) {

        boolean retorno = true;

        if (mes == 1) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes == 2) {
            if (dia > 28) {
                retorno = false;
            }
        } else if (mes == 3) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes == 4) {
            if (dia > 30) {
                retorno = false;
            }
        } else if (mes == 5) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes == 6) {
            if (dia > 30) {
                retorno = false;
            }
        } else if (mes == 7) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes == 8) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes == 9) {
            if (dia > 30) {
                retorno = false;
            }
        } else if (mes == 10) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes == 11) {
            if (dia > 30) {
                retorno = false;
            }
        } else if (mes == 12) {
            if (dia > 31) {
                retorno = false;
            }
        } else if (mes > 12) {
            retorno = false;
        }

        return retorno;
    }

    public static int retornarBanco(int idBanco) throws Exception {
        int retorno = 0;
        StringBuilder sql;
        Statement stm;
        ResultSet rst;

        Conexao.begin();

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select id from banco ");
        sql.append("where id = " + idBanco);
        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            retorno = rst.getInt("id");
        }

        rst.close();
        stm.close();

        Conexao.commit();

        return retorno;
    }

    public boolean verificaExisteMercadologico(int mercad1, int mercad2, int mercad3)
            throws SQLException, Exception {

        boolean retorno = true;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        Conexao.begin();

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select * from mercadologico ");
        sql.append("where mercadologico1 = " + mercad1 + " ");
        sql.append("and mercadologico2 = " + mercad2 + " ");
        sql.append("and mercadologico3 = " + mercad3 + " ");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            retorno = false;
        }

        stm.close();

        Conexao.commit();

        return retorno;
    }

    //TODO Gerar um novo teste
    public static boolean verificaExisteMercadologico4Nivel(int mercad1, int mercad2, int mercad3, int mercad4)
            throws SQLException, Exception {

        boolean retorno = true;
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("select * from mercadologico ");
        sql.append("where mercadologico1 = " + mercad1 + " ");
        sql.append("and mercadologico2 = " + mercad2 + " ");
        sql.append("and mercadologico3 = " + mercad3 + " ");
        sql.append("and mercadologico4 = " + mercad4 + " ");

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            retorno = false;
        }

        stm.close();

        return retorno;
    }

    /**
     * *
     * Retorna o id da aliquota ICMS do VR baseado no CST, Aliquota e Aliq.
     * Redução. Feita para o estado de São Paulo.
     *
     * @param cst Situação tributária.
     * @param aliquota Aliquota utilizada.
     * @param reduzido Aliquota da redução.
     * @return id do imposto no VR ou 8 caso não encontre nada.
     */
    public static int getAliquotaIcms_GateWay(Integer cstTrib, double valor, double reducao) {
        int retorno = 8;

        valor = Utils.truncar2(valor, 1);
        reducao = Utils.truncar2(reducao, 1);

        if (cstTrib == 0) {
            if (valor == 7.0) {
                retorno = 0;
            } else if (valor == 12.0) {
                retorno = 1;
            } else if (valor == 18.0) {
                retorno = 2;
            } else if (valor == 25.0) {
                retorno = 3;
            } else if (valor == 17.0) {
                retorno = 19;
            } else if (valor == 20.0) {
                retorno = 20;
            } else if (valor == 27.0) {
                retorno = 21;
            }
        } else if (cstTrib == 10) {
            retorno = 7;
        } else if (cstTrib == 20) {
            if (reducao == 61.1) {
                retorno = 4;
            } else if (reducao == 33.3) {
                retorno = 9;
            } else if (reducao == 52.0) {
                retorno = 10;
            } else if (reducao == 41.6) {
                retorno = 5;
            }
        } else if (cstTrib == 40) {
            retorno = 6;
        } else if (cstTrib == 41) {
            retorno = 17;
        } else if (cstTrib == 50) {
            retorno = 13;
        } else if (cstTrib == 51) {
            retorno = 16;
        } else if (cstTrib == 60) {
            retorno = 7;
        } else if (cstTrib == 70) {
            retorno = 7;
        } else if (cstTrib == 90) {
            retorno = 8;
        }

        return retorno;
    }

    public static int getAliquotaIcms(Integer cstTrib, double valor, double reducao) {
        int retorno = 8;

        valor = Utils.truncar2(valor, 1);
        reducao = Utils.truncar2(reducao, 1);

        if (cstTrib == 0) {
            if (valor == 7.0) {
                retorno = 0;
            } else if (valor == 12.0) {
                retorno = 1;
            } else if (valor == 18.0) {
                retorno = 2;
            } else if (valor == 25.0) {
                retorno = 3;
            } else if (valor == 17.0) {
                retorno = 19;
            } else if (valor == 20.0) {
                retorno = 20;
            } else if (valor == 27.0) {
                retorno = 21;
            }
        } else if (cstTrib == 10) {
            retorno = 7;
        } else if (cstTrib == 20) {
            if (reducao == 61.1) {
                retorno = 4;
            } else if (reducao == 33.3) {
                retorno = 9;
            } else if (reducao == 52.0) {
                retorno = 10;
            } else if (reducao == 41.6) {
                retorno = 5;
            }
        } else if (cstTrib == 40) {
            retorno = 6;
        } else if (cstTrib == 41) {
            retorno = 17;
        } else if (cstTrib == 50) {
            retorno = 13;
        } else if (cstTrib == 51) {
            retorno = 16;
        } else if (cstTrib == 60) {
            retorno = 7;
        } else if (cstTrib == 70) {
            retorno = 7;
        } else if (cstTrib == 90) {
            retorno = 8;
        }

        return retorno;
    }

    private static int getAliquotaICMS_SP(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 17) {
                    return 8;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 11) {
                    return 19;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        } else {
                            return 1;
                        }
                    } else if (reduzido == 61.1) {
                        if (!aliqConsumidor) {
                            return 4;
                        } else {
                            return 0;
                        }
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        if (!aliqConsumidor) {
                            return 5;
                        } else {
                            return 0;
                        }
                    } else if (reduzido == 10.4) {
                        return 11;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 25) {
                    if (reduzido == 10.4) {
                        return 12;
                    } else if (reduzido == 52.0) {
                        if (!aliqConsumidor) {
                            return 10;
                        } else {
                            return 1;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    private static int getAliquotaICMS_PB(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);
        int cstTrib = 0;

        if (cst >= 10) {
            cstTrib = Integer.parseInt(String.valueOf(cst).substring(0, 2));
            cst = cstTrib;
        }

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 17) {
                    return 19;
                } else if (aliquota == 20) {
                    return 20;
                } else if (aliquota == 27) {
                    return 21;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        } else {
                            return 1;
                        }
                    } else if (reduzido == 61.1) {
                        if (!aliqConsumidor) {
                            return 4;
                        } else {
                            return 0;
                        }
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        if (!aliqConsumidor) {
                            return 5;
                        } else {
                            return 0;
                        }
                    } else if (reduzido == 10.4) {
                        return 11;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 25) {
                    if (reduzido == 10.4) {
                        return 12;
                    } else if (reduzido == 52.0) {
                        if (!aliqConsumidor) {
                            return 10;
                        } else {
                            return 1;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    private static int getAliquotaICMS_PR(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);
        int cstTrib = 0;

        if (cst >= 10) {
            cstTrib = Integer.parseInt(String.valueOf(cst).substring(0, 2));
            cst = cstTrib;
        }

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 17) {
                    return 19;
                } else if (aliquota == 27) {
                    return 20;
                } else if (aliquota == 29) {
                    return 21;
                } else if (aliquota == 0) {
                    return 6;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        } else {
                            return 1;
                        }
                    } else if (reduzido == 61.1) {
                        if (!aliqConsumidor) {
                            return 4;
                        } else {
                            return 0;
                        }
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        if (!aliqConsumidor) {
                            return 5;
                        } else {
                            return 0;
                        }
                    } else if (reduzido == 10.4) {
                        return 11;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 25) {
                    if (reduzido == 10.4) {
                        return 12;
                    } else if (reduzido == 52.0) {
                        if (!aliqConsumidor) {
                            return 10;
                        } else {
                            return 1;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 6;
        }
    }

    private static int getAliquotaICMS_PA(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);
        int cstTrib = 0;

        if (cst >= 10) {
            cstTrib = Integer.parseInt(String.valueOf(cst).substring(0, 2));
            cst = cstTrib;
        }

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 17) {
                    return 19;
                } else if (aliquota == 27) {
                    return 20;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        } else {
                            return 1;
                        }
                    } else if (reduzido == 61.1) {
                        if (!aliqConsumidor) {
                            return 4;
                        } else {
                            return 0;
                        }
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        if (!aliqConsumidor) {
                            return 5;
                        } else {
                            return 0;
                        }
                    } else if (reduzido == 10.4) {
                        return 11;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 25) {
                    if (reduzido == 10.4) {
                        return 12;
                    } else if (reduzido == 52.0) {
                        if (!aliqConsumidor) {
                            return 10;
                        } else {
                            return 1;
                        }
                    } else {
                        return 3;
                    }
                } else if (aliquota == 17) {
                    if (reduzido == 58.8) {
                        return 22;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    private static int getAliquotaICMS_PE(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);
        int cstTrib = 0;

        if (cst >= 10) {
            cstTrib = Integer.parseInt(String.valueOf(cst).substring(0, 2));
            cst = cstTrib;
        }

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 17) {
                    return 19;
                } else if (aliquota == 27) {
                    return 20;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        }
                    } else if (reduzido == 61.1) {
                        return 4;
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        return 5;
                    } else if (reduzido == 10.4) {
                        return 11;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 25) {
                    if (reduzido == 10.4) {
                        return 12;
                    } else if (reduzido == 52.0) {
                        if (!aliqConsumidor) {
                            return 10;
                        } else {
                            return 1;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    private static int getAliquotaICMS_GO(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 0;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 17) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 3) {
                    return 15;
                } else if (aliquota == 9) {
                    return 19;
                } else if (aliquota == 19) {
                    return 20;
                } else if (aliquota == 27) {
                    return 21;
                } else {
                    return 8;
                }
            }
            case 10: {
                return 7;
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        } else {
                            return 1;
                        }
                    } else if (reduzido == 61.1) {
                        if (!aliqConsumidor) {
                            return 4;
                        } else {
                            return 0;
                        }
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        if (!aliqConsumidor) {
                            return 5;
                        } else {
                            return 0;
                        }
                    } else if (reduzido == 10.4) {
                        return 11;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 25) {
                    if (reduzido == 10.4) {
                        return 12;
                    } else if (reduzido == 52.0) {
                        if (!aliqConsumidor) {
                            return 10;
                        } else {
                            return 1;
                        }
                    } else {
                        return 3;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 17;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 16;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    /**
     * *
     * Retorna o id da aliquota ICMS do VR baseado no CST, Aliquota e Aliq.
     * Redução. Feita para o estado do Ceará.
     *
     * @param cst Situação tributária.
     * @param aliquota Aliquota utilizada.
     * @param reduzido Aliquota da redução.
     * @return id do imposto no VR ou 8 caso não encontre nada.
     */
    private static int getAliquotaICMS_RN(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 25;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 17) {
                    return 20;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 27) {
                    return 32;
                } else if (aliquota == 19) {
                    return 22;
                } else {
                    return 8;
                }
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        return 9;
                    } else if (reduzido == 61.1) {
                        return 4;
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        return 5;
                    } else {
                        return 1;
                    }
                } else if (aliquota == 17) {
                    if (reduzido == 29.4) {
                        return 34;
                    } else if (reduzido == 58.8) {
                        return 26;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 21;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 14;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    private static int getAliquotaICMS_CE(int cst, double aliquota, double reduzido, boolean aliqConsumidor) {

        aliquota = Utils.truncar2(aliquota, 1);
        reduzido = Utils.truncar2(reduzido, 1);

        switch (cst) {
            case 0: {
                if (aliquota == 7) {
                    return 25;
                } else if (aliquota == 12) {
                    return 1;
                } else if (aliquota == 17) {
                    return 20;
                } else if (aliquota == 18) {
                    return 2;
                } else if (aliquota == 25) {
                    return 3;
                } else if (aliquota == 27) {
                    return 32;
                } else {
                    return 8;
                }
            }
            case 20: {
                if (aliquota == 18) {
                    if (reduzido == 33.3) {
                        if (!aliqConsumidor) {
                            return 9;
                        } else {
                            return 1;
                        }
                    } else if (reduzido == 61.1) {
                        if (!aliqConsumidor) {
                            return 4;
                        } else {
                            return 25;
                        }
                    } else {
                        return 2;
                    }
                } else if (aliquota == 12) {
                    if (reduzido == 41.6) {
                        if (!aliqConsumidor) {
                            return 5;
                        } else {
                            return 25;
                        }
                    } else {
                        return 1;
                    }
                } else {
                    return 8;
                }
            }
            case 40: {
                return 6;
            }
            case 41: {
                return 21;
            }
            case 50: {
                return 13;
            }
            case 51: {
                return 14;
            }
            case 60: {
                return 7;
            }
            case 70: {
                return 7;
            }
            default:
                return 8;
        }
    }

    /**
     * *
     * Retorna o id da aliquota ICMS do VR baseado no CST, Aliquota e Aliq.
     * Redução. Feita para o estado de São Paulo.
     *
     * @param uf Estado onde compente a aplicação do imposto.
     * @param cst Situação tributária.
     * @param aliquota Aliquota utilizada.
     * @param reduzido Aliquota da redução.
     * @return id do imposto no VR ou 8 caso não encontre nada.
     */
    public static int getAliquotaICMS(String uf, int cst, double aliquota, double reduzido, boolean aliqConsumidor) {
        uf = Utils.acertarTexto(uf, 2);
        switch (uf) {
            case "CE":
                return Utils.getAliquotaICMS_CE(cst, aliquota, reduzido, aliqConsumidor);
            case "PB":
                return Utils.getAliquotaICMS_PB(cst, aliquota, reduzido, aliqConsumidor);
            case "PA":
                return Utils.getAliquotaICMS_PA(cst, aliquota, reduzido, aliqConsumidor);
            case "PE":
                return Utils.getAliquotaICMS_PE(cst, aliquota, reduzido, aliqConsumidor);
            case "RN":
                return Utils.getAliquotaICMS_RN(cst, aliquota, reduzido, aliqConsumidor);
            case "PR":
                return Utils.getAliquotaICMS_PR(cst, aliquota, reduzido, aliqConsumidor);
            case "GO":
                return Utils.getAliquotaICMS_GO(cst, aliquota, reduzido, aliqConsumidor);
            default:
                return Utils.getAliquotaICMS_SP(cst, aliquota, reduzido, aliqConsumidor);
        }
    }

    /**
     * *
     * Retorna o id da aliquota ICMS do VR baseado no CST, Aliquota e Aliq.
     * Redução. Feita para o estado de São Paulo.
     *
     * @param uf Estado onde compente a aplicação do imposto.
     * @param cst Situação tributária.
     * @param aliquota Aliquota utilizada.
     * @param reduzido Aliquota da redução.
     * @return id do imposto no VR ou 8 caso não encontre nada.
     */
    public static int getAliquotaICMS(String uf, int cst, double aliquota, double reduzido) {
        return Utils.getAliquotaICMS(uf, cst, aliquota, reduzido, false);
    }

    public static int retornarPisCofinsDebito2(int cst) {
        int retorno = 1;

        if ((cst == 1) || (cst == 50)) {
            retorno = 0;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 5;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 6;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 3;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 2;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 7;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 1;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 8;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 9;
        }

        return retorno;
    }

    public static int retornarPisCofinsCredito2(int cst) {
        int retorno = 13;

        if ((cst == 1) || (cst == 50)) {
            retorno = 12;
        } else if ((cst == 2) || (cst == 60)) {
            retorno = 17;
        } else if ((cst == 3) || (cst == 51)) {
            retorno = 18;
        } else if ((cst == 4) || (cst == 70)) {
            retorno = 15;
        } else if ((cst == 5) || (cst == 75)) {
            retorno = 14;
        } else if ((cst == 6) || (cst == 73)) {
            retorno = 19;
        } else if ((cst == 7) || (cst == 71)) {
            retorno = 13;
        } else if ((cst == 8) || (cst == 74)) {
            retorno = 20;
        } else if ((cst == 49) || (cst == 98) || (cst == 99)) {
            retorno = 21;
        }

        return retorno;
    }

    public static String isNull(String value, String padrao) {
        if (value == null) {
            return padrao;
        }
        return value;
    }

    public static String isNull(String value) {
        return isNull(value, "");
    }
}
