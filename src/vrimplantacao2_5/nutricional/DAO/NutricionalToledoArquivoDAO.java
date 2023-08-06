/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.nutricional.DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.postgresql.util.PSQLException;
import vr.core.collection.Properties;
import vr.implantacao.main.App;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao2_5.nutricional.vo.InfnutriVO;
import vrimplantacao2_5.nutricional.vo.ItensMgvVO;
import vrimplantacao2_5.nutricional.vo.TxtInfoVO;

/**
 *
 * @author Michael
 */
@SuppressWarnings("static-access")
public class NutricionalToledoArquivoDAO extends InterfaceDAO {

    ConexaoPostgres con = new ConexaoPostgres();
    Properties prop = App.properties();
    private final String ip = prop.get("database.ip");
    private final String banco = prop.get("database.nome");
    private final int porta = Integer.parseInt(prop.get("database.porta"));
    int tipoScript = 0;
    private String sql = "";
    private String sistemaOrigem = "ARQUIVOS";
    private boolean usarContador = false;

    public boolean isUsarContador() {
        return usarContador;
    }

    public String getSistemaOrigem() {
        return sistemaOrigem;
    }

    public void setSistemaOrigem(String sistemaOrigem) {
        this.sistemaOrigem = sistemaOrigem;
    }

    public int getTipoScript() {
        return tipoScript;
    }

    public void setTipoScript(int tipoScript) {
        this.tipoScript = tipoScript;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSistema() {
        return getSistemaOrigem();
    }

    int contador = 0;

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();
        try (Statement stm = con.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    getSql()
            )) {
                if (!rst.next()) {
                    System.out.println("Select do getnutricional retornou vazio, teste o script no banco.");
                    throw new Exception("Select do getnutricional retornou vazio, teste o script no banco.");
                } else {
                    while (rst.next()) {
                        NutricionalIMP imp = new NutricionalIMP();
                        if (getTipoScript() == 1) {
                            if (isUsarContador()) {
                                imp.setId(String.valueOf(contador++));
                            } else {
                                imp.setId(rst.getString("id_nutricional") == null ? "0" : rst.getString("id_nutricional"));
                            }

                            imp.setDescricao(rst.getString("descricao") == null ? "DESCRICAO VAZIA" : rst.getString("descricao") + " " + Integer.parseInt(rst.getString("quantidade")));
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setCaloria(rst.getString("caloria") == null ? 0 : rst.getInt("caloria"));
                            imp.setCarboidrato(rst.getString("carboidrato") == null ? 0 : rst.getDouble("carboidrato"));
                            imp.setCarboidratoInferior(rst.getString("carboidratoInferior") == null ? false : "1".equals(rst.getString("carboidratoInferior")));
                            imp.setProteina(rst.getString("proteina") == null ? 0 : rst.getDouble("proteina"));
                            imp.setProteinaInferior(rst.getString("proteinaInferior") == null ? false : "1".equals(rst.getString("proteinaInferior")));
                            imp.setGordura(rst.getString("gordura") == null ? 0 : rst.getDouble("gordura"));
                            imp.setGorduraSaturada(rst.getString("gordurasaturada") == null ? 0 : rst.getDouble("gordurasaturada"));
                            imp.setGorduraTrans(rst.getString("gorduratrans") == null ? 0 : rst.getDouble("gorduratrans"));
                            imp.setColesterolInferior(rst.getString("colesterolInferior") == null ? false : "1".equals(rst.getString("colesterolInferior")));
                            imp.setFibra(rst.getString("fibra") == null ? 0 : rst.getDouble("fibra"));
                            imp.setFibraInferior(rst.getString("fibraInferior") == null ? false : "1".equals(rst.getString("fibraInferior")));
                            imp.setCalcio(rst.getString("calcio") == null ? 0 : rst.getDouble("calcio"));
                            imp.setFerro(rst.getString("ferro") == null ? 0 : rst.getDouble("ferro"));
                            imp.setSodio(rst.getString("sodio") == null ? 0 : rst.getDouble("sodio"));
                            imp.setMedidaInteira(rst.getString("medidaInteira") == null ? 1 : rst.getInt("medidaInteira"));
                            imp.setPorcao(rst.getString("quantidade") == null ? "0" : rst.getString("quantidade"));
                            imp.setId_tipomedidadecimal(rst.getString("id_tipomedidadecimal") == null ? 0 : rst.getInt("id_tipomedidadecimal"));
                            imp.setIdTipoMedida(rst.getString("id_tipomedida") == null ? -1 : rst.getInt("id_tipomedida"));
                            imp.setAcucaresadicionados(rst.getString("acucares") == null ? 0 : rst.getString("acucares").equals("") ? 0 : rst.getDouble("acucares"));
                            imp.setAcucarestotais(rst.getString("acucares_total") == null ? 0 : rst.getString("acucares_total").equals("") ? 0 : rst.getDouble("acucares_total"));
                            imp.setId_tipounidadeporcao(rst.getString("Id_tipounidadeporcao") == null ? 1 : rst.getInt("Id_tipounidadeporcao"));
                            imp.getMensagemAlergico().add(rst.getString("alergenicos") == null ? "" : rst.getString("alergenicos"));
                            imp.addProduto(rst.getString("id_produto") == null ? "0" : rst.getString("id_produto"));
                        } else {
                            if (isUsarContador()) {
                                imp.setId(String.valueOf(contador++));
                            } else {
                                imp.setId(rst.getString("id_nutricional") == null ? "0" : rst.getString("id_nutricional"));
                            }
                            imp.setDescricao(rst.getString("descricao") == null ? "DESCRICAO VAZIA" : rst.getString("descricao") + " " + Integer.parseInt(rst.getString("quantidade")));
                            imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                            imp.setCaloria(rst.getString("caloria") == null ? 0 : rst.getInt("caloria"));
                            imp.setCarboidrato(rst.getString("carboidrato") == null ? 0 : rst.getDouble("carboidrato"));
                            imp.setProteina(rst.getString("proteina") == null ? 0 : rst.getDouble("proteina"));
                            imp.setGordura(rst.getString("gordura") == null ? 0 : rst.getDouble("gordura"));
                            imp.setGorduraSaturada(rst.getString("gordurasaturada") == null ? 0 : rst.getDouble("gordurasaturada"));
                            imp.setGorduraTrans(rst.getString("gorduratrans") == null ? 0 : rst.getDouble("gorduratrans"));
                            imp.setFibra(rst.getString("fibra") == null ? 0 : rst.getDouble("fibra"));
                            imp.setSodio(rst.getString("sodio") == null ? 0 : rst.getDouble("sodio"));
                            imp.setMedidaInteira(rst.getString("medidaInteira") == null ? 1 : rst.getInt("medidaInteira"));
                            imp.setPorcao(rst.getString("quantidade") == null ? "0" : rst.getString("quantidade"));
                            imp.setId_tipomedidadecimal(rst.getString("id_tipomedidadecimal") == null ? 5 : rst.getInt("id_tipomedidadecimal"));
                            imp.setIdTipoMedida(rst.getString("id_tipomedida") == null ? -1 : rst.getInt("id_tipomedida"));
                            imp.setId_tipounidadeporcao(rst.getString("Id_tipounidadeporcao") == null ? 2 : rst.getInt("Id_tipounidadeporcao"));
                            imp.setAcucaresadicionados(rst.getString("acucares") == null ? 0 : rst.getString("acucares").equals("") ? 0 : rst.getDouble("acucares"));
                            imp.setAcucarestotais(rst.getString("acucares_total") == null ? 0 : rst.getString("acucares_total").equals("") ? 0 : rst.getDouble("acucares_total"));
                            imp.getMensagemAlergico().add(rst.getString("alergenicos") == null ? "" : rst.getString("alergenicos"));
                            imp.addProduto(rst.getString("id_produto") == null ? "0" : rst.getString("id_produto"));
                        }
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void criarTabelaMgv() throws Exception {
        try (Statement stm = con.getConexao().createStatement()) {
            String tabelaMgv = "CREATE TABLE IF NOT EXISTS implantacao.itens_mgv6 (\n"
                    + "	id SERIAL4 NOT NULL PRIMARY key,\n"
                    + " impsistema varchar(100),\n"
                    + " imploja varchar(6),\n"
                    + "	preco varchar(10),\n"
                    + "	codigo_item int4,\n"
                    + " balanca varchar(10),\n"
                    + "	validade varchar(10),\n"
                    + "	descricao varchar(100),\n"
                    + "	codigo_nutricional int4,\n"
                    + "	demais_dados text\n"
                    + ")";
            stm.execute(
                    tabelaMgv
            );
        } catch (PSQLException e) {
            System.out.println("criarTabelaMgv\n\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("criarTabelaMgv\n\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void popularItensMgv6(ItensMgvVO vo, String imploja, String sistema) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        try (Statement stm = con.getConexao().createStatement()) {

            sql.setSchema("implantacao");
            sql.setTableName("itens_mgv6");
            sql.put("impsistema", sistema);
            sql.put("imploja", imploja);
            sql.put("codigo_item", vo.getCodigo());
            sql.put("balanca", vo.getPesavel());
            sql.put("preco", vo.getPreco());
            sql.put("validade", vo.getValidade());
            sql.put("descricao", vo.getDescricao());
            sql.put("codigo_nutricional", vo.getNutricional());
            sql.put("demais_dados", vo.getDemaisDados());

            stm.execute(sql.getInsert());
        } catch (Exception e) {
            System.out.println(sql.getInsert());
            System.out.println(e.getMessage());
            e.printStackTrace();
            //throw e;
        }
    }

    public void atualizaItensMgv6(ItensMgvVO vo, String imploja, String sistema) throws Exception {
        popularItensMgv6(vo, imploja, sistema);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void criarTabelaInfnutri() throws Exception {
        try (Statement stm = con.getConexao().createStatement()) {
            String tabelaInfnutri = "CREATE TABLE IF NOT EXISTS implantacao.infnutri_v6 (\n"
                    + "	id SERIAL4 PRIMARY KEY NOT NULL,\n"
                    + " impsistema varchar(100),\n"
                    + " imploja varchar(6),\n"
                    + "	indicador varchar(10),\n"
                    + "	codigo_nutricional int4,\n"
                    + "	reservado varchar(10),\n"
                    + "	quantidade varchar(10),\n"
                    + "	un_porcao varchar(10),\n"
                    + "	pt_int_med_caseira varchar(10),\n"
                    + "	fracao_dec_med_caseira varchar(10),\n"
                    + "	med_caseira_utilizada_xic varchar(10),\n"
                    + "	valor_energetico_caloria varchar(10),\n"
                    + "	carboidratos varchar(10),\n"
                    + "	proteinas varchar(10),\n"
                    + "	gorduras_totais varchar(10),\n"
                    + "	gorduras_saturadas varchar(10),\n"
                    + "	gorduras_trans varchar(10),\n"
                    + "	fibra_alimentar varchar(10),\n"
                    + "	sodio varchar(10),\n"
                    + " acucares varchar(10),\n"
                    + " acucares_total varchar(10)\n"
                    + ")";
            stm.execute(
                    tabelaInfnutri
            );
        } catch (PSQLException e) {
            System.out.println("criarTabelaInfnutri\n\n" + e.getMessage());
            e.printStackTrace();
            //throw e;
        } catch (Exception e) {
            System.out.println("criarTabelaInfnutri\n\n" + e.getMessage());
            e.printStackTrace();
            //throw e;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void popularInfnutri(InfnutriVO vo, String imploja, String sistema) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        try (Statement stm = con.getConexao().createStatement()) {

            sql.setSchema("implantacao");
            sql.setTableName("infnutri_v6");
            sql.put("impsistema", sistema);
            sql.put("imploja", imploja);
            sql.put("indicador", vo.getIndicador());
            sql.put("codigo_nutricional", vo.getNutricional());
            sql.put("reservado", vo.getReservado());
            sql.put("quantidade", vo.getQuantidade());
            sql.put("un_porcao", vo.getPorcaoUnGr());
            sql.put("codigo_nutricional", vo.getNutricional());
            sql.put("pt_int_med_caseira", vo.getMedidaCaseiraInteira());
            sql.put("fracao_dec_med_caseira", vo.getMedidaCaseiraDecimalFracionado());
            sql.put("med_caseira_utilizada_xic", vo.getMedidaCaseiraXicaraFatia());
            sql.put("valor_energetico_caloria", vo.getCalorias());
            sql.put("carboidratos", vo.getCarboidratos());
            sql.put("proteinas", vo.getProteinas());
            sql.put("gorduras_totais", vo.getGordurasTotais());
            sql.put("gorduras_saturadas", vo.getGordurasSaturadas());
            sql.put("gorduras_trans", vo.getGordurasTrans());
            sql.put("fibra_alimentar", vo.getFibra());
            sql.put("sodio", vo.getSodio());
            sql.put("acucares", vo.getAcucares());
            sql.put("acucares_total", vo.getAcucaresTotais());

            stm.execute(sql.getInsert());
        } catch (Exception e) {
            System.out.println(sql.getInsert());
            System.out.println(e.getMessage());
            e.printStackTrace();
            //throw e;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void atualizaInfnutri(InfnutriVO vo, String imploja) throws Exception {
        try (Statement stm = con.getConexao().createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setSchema("implantacao");
            sql.setTableName("infnutri_v6");
            sql.put("impsistema", getSistema() + " - " + imploja);
            sql.put("imploja", imploja);
            sql.put("indicador", vo.getIndicador());
            sql.put("codigo_nutricional", vo.getNutricional());
            sql.put("reservado", vo.getReservado());
            sql.put("quantidade", vo.getQuantidade());
            sql.put("un_porcao", vo.getPorcaoUnGr());
            sql.put("codigo_nutricional", vo.getNutricional());
            sql.put("pt_int_med_caseira", vo.getMedidaCaseiraInteira());
            sql.put("fracao_dec_med_caseira", vo.getMedidaCaseiraDecimalFracionado());
            sql.put("med_caseira_utilizada_xic", vo.getMedidaCaseiraXicaraFatia());
            sql.put("valor_energetico_caloria", vo.getCalorias());
            sql.put("carboidratos", vo.getCarboidratos());
            sql.put("proteinas", vo.getProteinas());
            sql.put("gorduras_totais", vo.getGordurasTotais());
            sql.put("gorduras_saturadas", vo.getGordurasSaturadas());
            sql.put("gorduras_trans", vo.getGordurasTrans());
            sql.put("fibra_alimentar", vo.getFibra());
            sql.put("sodio", vo.getSodio());
            sql.setWhere("codigo_nutricional = '" + vo.getNutricional() + "'");

            String strSql = sql.getUpdate();
            stm.execute(strSql);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            //throw e;
        }
    }

    public boolean confereItensMgvPorLoja(String imploja) throws SQLException {
        boolean retorno = false;
        try (Statement stm = con.getConexao().createStatement()) {
            String contagemRegistro = "select count(*) total from implantacao.itens_mgv6 where imploja = '" + imploja + "'";
            try (ResultSet rst = stm.executeQuery(
                    contagemRegistro
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("total") == 0;
                }
            }
        }
        return retorno;
    }

    public boolean confereInfnutriPorLoja(String imploja) throws SQLException {
        boolean retorno = false;
        try (Statement stm = con.getConexao().createStatement()) {
            String contagemRegistro = "select count(*) total from implantacao.infnutri_v6 where imploja = '" + imploja + "'";
            try (ResultSet rst = stm.executeQuery(
                    contagemRegistro
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("total") == 0;
                }
            }
        }
        return retorno;
    }

    public void iniciarConexao() throws Exception {
        con.abrirConexao(ip, porta, banco, "postgres", "VrPost@Server");
    }

    public void finalizarConexao() throws Exception {
        con.close();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void deletarTabelaMgv(String imploja, String sistema) {
        try (Statement stm = con.getConexao().createStatement()) {
            stm.execute(
                    "delete from implantacao.itens_mgv6 where imploja ='" + imploja + "' and impsistema = '" + sistema + "';"
            );
            stm.execute("do $$\n"
                    + "declare maxid int;\n"
                    + "begin\n"
                    + "    select max(id)+1 from implantacao.itens_mgv6 into maxid;\n"
                    + "   	if maxid = 0 then maxid := 1;\n"
                    + "   	elseif maxid is null then maxid := 1;\n"
                    + "   	end if;\n"
                    + "    execute 'alter SEQUENCE implantacao.itens_mgv6_id_seq RESTART with '|| maxid;   \n"
                    + "end;\n"
                    + "$$ language plpgsql");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void deletarTabelaInfnutri(String imploja, String sistema) {
        try (Statement stm = con.getConexao().createStatement()) {
            stm.execute(
                    "delete from implantacao.infnutri_v6 where imploja ='" + imploja + "' and impsistema = '" + sistema + "';"
            );
            stm.execute("do $$\n"
                    + "declare maxid int;\n"
                    + "begin\n"
                    + "    select max(id)+1 from implantacao.infnutri_v6 into maxid;\n"
                    + "   	if maxid = 0 then maxid := 1;\n"
                    + "   	elseif maxid is null then maxid := 1;\n"
                    + "   	end if;\n"
                    + "    execute 'alter SEQUENCE implantacao.infnutri_v6_id_seq RESTART with '|| maxid;   \n"
                    + "end;\n"
                    + "$$ language plpgsql");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void deletarTabelaTxtinfo(String imploja, String sistema) {
        try (Statement stm = con.getConexao().createStatement()) {
            stm.execute(
                    "delete from implantacao.txtinfo_v6 where imploja ='" + imploja + "' and impsistema = '" + sistema + "';"
            );
            stm.execute("do $$\n"
                    + "declare maxid int;\n"
                    + "begin\n"
                    + "    select max(id)+1 from implantacao.txtinfo_v6 into maxid;\n"
                    + "   	if maxid = 0 then maxid := 1;\n"
                    + "   	elseif maxid is null then maxid := 1;\n"
                    + "   	end if;\n"
                    + "    execute 'alter SEQUENCE implantacao.txtinfo_v6_id_seq RESTART with '|| maxid;   \n"
                    + "end;\n"
                    + "$$ language plpgsql");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getSistemaOrigemCombo() throws Exception {
        List<String> result = new ArrayList<>();
        try (Statement stm = con.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct impsistema sistema from implantacao.codant_produto;"
            )) {
                while (rst.next()) {
                    String sistema = rst.getString("sistema");
                    result.add(sistema);
                }
            }
        }
        return result;
    }

    public List<String> getLojaOrigemCombo() throws Exception {
        List<String> result = new ArrayList<>();
        try (Statement stm = con.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct imploja loja from implantacao.codant_produto;"
            )) {
                while (rst.next()) {
                    String loja = rst.getString("loja");
                    result.add(loja);
                }
            }
        }
        return result;
    }

    public List<String> getLojaVrCombo() throws Exception {
        List<String> result = new ArrayList<>();
        try (Statement stm = con.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from loja;"
            )) {
                while (rst.next()) {
                    String lojaVr = rst.getString("id");
                    result.add(lojaVr);
                }
            }
        }
        return result;
    }

    public void setUsarContador(boolean b) {
        this.usarContador = b;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void deletarNutricionalToledo(String sistema, String lojaOrigem) {
        try (Statement stm = con.getConexao().createStatement()) {
            stm.execute(
                    "delete from nutricionaltoledoitem where id_nutricionaltoledo in (select id from nutricionaltoledo);");
            stm.execute("delete from nutricionaltoledo where id in (select codigoatualtoledo from implantacao.codant_nutricional cn);");
            stm.execute("delete from implantacao.codant_nutricional cn where sistema = '" + sistema + "' and loja = '" + lojaOrigem + "';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void criarTabelaTxtinfo() {
        try (Statement stm = con.getConexao().createStatement()) {
            String tabelaTxtinfo = "CREATE TABLE IF NOT EXISTS implantacao.txtinfo_v6 (\n"
                    + "	id SERIAL4 PRIMARY KEY NOT NULL,\n"
                    + " impsistema varchar(100),\n"
                    + " imploja varchar(6),\n"
                    + "	codigo int4,\n"
                    + "	obs text,\n"
                    + "	linha1 text,\n"
                    + "	linha2 text,\n"
                    + "	linha3 text,\n"
                    + "	linha4 text,\n"
                    + "	linha5 text,\n"
                    + "	linha6 text,\n"
                    + "	linha7 text,\n"
                    + "	linha8 text,\n"
                    + "	linha9 text,\n"
                    + "	linha10 text,\n"
                    + "	linha11 text,\n"
                    + "	linha12 text,\n"
                    + "	linha13 text,\n"
                    + "	linha14e15 text\n"
                    + ")";
            stm.execute(tabelaTxtinfo);
        } catch (PSQLException e) {
            System.out.println("criarTabelaTxtinfo\n\n" + e.getMessage());
            e.printStackTrace();
            //throw e;
        } catch (Exception e) {
            System.out.println("criarTabelaTxtinfo\n\n" + e.getMessage());
            e.printStackTrace();
            //throw e;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void popularTxtinfo(TxtInfoVO vo, String imploja, String sistema) throws Exception {
        SQLBuilder sql = new SQLBuilder();
        try (Statement stm = con.getConexao().createStatement()) {

            sql.setSchema("implantacao");
            sql.setTableName("txtinfo_v6");
            sql.put("impsistema", sistema);
            sql.put("imploja", imploja);
            sql.put("codigo", vo.getCodigo());
            sql.put("obs", vo.getObs());
            sql.put("linha1", vo.getLinha1());
            sql.put("linha2", vo.getLinha2());
            sql.put("linha3", vo.getLinha3());
            sql.put("linha4", vo.getLinha4());
            sql.put("linha5", vo.getLinha5());
            sql.put("linha6", vo.getLinha6());
            sql.put("linha7", vo.getLinha7());
            sql.put("linha8", vo.getLinha8());
            sql.put("linha9", vo.getLinha9());
            sql.put("linha10", vo.getLinha10());
            sql.put("linha11", vo.getLinha11());
            sql.put("linha12", vo.getLinha12());
            sql.put("linha13", vo.getLinha13());
            sql.put("linha14e15", vo.getLinha14E15());

            stm.execute(sql.getInsert());
        } catch (Exception e) {
            System.out.println(sql.getInsert());
            System.out.println(e.getMessage());
            e.printStackTrace();
            //throw e;
        }
    }

    public boolean confereTxtinfoPorLoja(String lojaOrigem) throws SQLException {
        boolean retorno = false;
        try (Statement stm = con.getConexao().createStatement()) {
            String contagemRegistro = "select count(*) total from implantacao.txtinfo_v6 where imploja = '" + lojaOrigem + "'";
            try (ResultSet rst = stm.executeQuery(
                    contagemRegistro
            )) {
                if (rst.next()) {
                    retorno = rst.getInt("total") == 0;
                }
            }
        }
        return retorno;
    }
}
