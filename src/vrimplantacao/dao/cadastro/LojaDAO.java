package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import vr.core.parametro.versao.Versao;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.vo.loja.LojaFiltroConsultaVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2_5.dao.cadastro.pdv.PdvBalancaLayoutDAO;
import vrimplantacao2_5.dao.cadastro.pdv.PdvCartaoLayoutDAO;
import vrimplantacao2_5.dao.cadastro.pdv.PdvParametroValorDAO;
import vrimplantacao2_5.dao.teclado.TecladoLayoutDAO;
import vrimplantacao2_5.dao.utils.LojaGeradorScripts;

@SuppressWarnings("FieldMayBeFinal")
public class LojaDAO {

    private Versao versao = Versao.createFromConnectionInterface(Conexao.getConexao());
    private LojaGeradorScripts script = new LojaGeradorScripts(versao);
    private PdvParametroValorDAO pdvParametroValorDAO = new PdvParametroValorDAO();
    private PdvCartaoLayoutDAO pdvCartaoLayoutDAO = new PdvCartaoLayoutDAO();
    private PdvBalancaLayoutDAO pdvBalancaLayoutDAO = new PdvBalancaLayoutDAO();
    private TecladoLayoutDAO tecladoLayoutDAO = new TecladoLayoutDAO();
    private SQLBuilder scriptSQLBuilder = null;
    private EcfDAO ecfDAO = new EcfDAO();

    public List<LojaVO> consultar(LojaFiltroConsultaVO i_filtro) throws Exception {
        List<LojaVO> result = new ArrayList();

        String sql = "SELECT "
                + "     lj.*, sc.descricao AS situacaocadastro,\n"
                + "     r.descricao AS regiao\n"
                + "FROM loja AS lj\n"
                + "INNER JOIN situacaocadastro AS sc ON sc.id = lj.id_situacaocadastro\n"
                + "INNER JOIN regiao AS r ON r.id = lj.id_regiao\n"
                + "WHERE 1 = 1";

        if (i_filtro.getId() > -1) {
            sql = sql + " AND lj.id = " + i_filtro.getId();
        }

        if (!i_filtro.getDescricao().trim().equals("")) {
            sql = sql + " AND " + Util.getGoogle("lj.descricao", i_filtro.getDescricao());
        }

        if (i_filtro.getOrdenacao().isEmpty()) {
            sql = sql + " ORDER BY lj.descricao";
        } else {
            sql = sql + " ORDER BY " + i_filtro.getOrdenacao() + ", lj.descricao";
        }

        if (i_filtro.getLimite() > 0) {
            sql = sql + " LIMIT " + i_filtro.getLimite();
        }

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    LojaVO oLoja = new LojaVO();
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));
                    oLoja.setIdFornecedor(rst.getInt("id_fornecedor"));
                    oLoja.setNomeServidor(rst.getString("nomeservidor"));
                    oLoja.setServidorCentral(rst.getBoolean("servidorcentral"));
                    oLoja.setIdSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    oLoja.setSituacaoCadastro(rst.getString("situacaocadastro"));
                    oLoja.setIdRegiao(rst.getInt("id_regiao"));
                    oLoja.setGeraConcentrador(rst.getBoolean("geraconcentrador"));
                    oLoja.setRegiao(rst.getString("regiao"));

                    result.add(oLoja);
                }
            }
        }
        return result;
    }

    public LojaVO carregar(int i_id) throws Exception {
        LojaVO oLoja = new LojaVO();

        String sql = "SELECT * FROM loja WHERE id = " + i_id;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));
                    oLoja.setIdFornecedor(rst.getInt("id_fornecedor"));
                    oLoja.setNomeServidor(rst.getString("nomeservidor"));
                    oLoja.setServidorCentral(rst.getBoolean("servidorcentral"));
                    oLoja.setIdRegiao(rst.getInt("id_regiao"));
                    oLoja.setGeraConcentrador(rst.getBoolean("geraconcentrador"));
                }
            }
        }
        return oLoja;
    }

    public List<LojaVO> carregar() throws Exception {
        List<LojaVO> result = new ArrayList<>();

        String sql = "SELECT * FROM loja ORDER BY loja ASC";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                while (rst.next()) {
                    LojaVO oLoja = new LojaVO();
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));

                    result.add(oLoja);
                }
            }
            return result;
        }
    }

    public LojaVO carregar2(int i_id) throws Exception {
        LojaVO oLoja = new LojaVO();

        String sql = "SELECT * FROM loja WHERE id = " + i_id;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    oLoja.setId(rst.getInt("id"));
                    oLoja.setDescricao(rst.getString("descricao"));
                    oLoja.setIdFornecedor(rst.getInt("id_fornecedor"));
                    oLoja.setNomeServidor(rst.getString("nomeservidor"));
                    oLoja.setServidorCentral(rst.getBoolean("servidorcentral"));
                    oLoja.setIdRegiao(rst.getInt("id_regiao"));
                    oLoja.setGeraConcentrador(rst.getBoolean("geraconcentrador"));
                }
            }
        }

        return oLoja;
    }

    public boolean isLojaExiste(LojaVO i_loja) throws Exception {
        String sql = "SELECT id FROM loja WHERE id = " + i_loja.getId();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public boolean validaOferta(LojaVO i_loja) throws Exception {
        String sql = "SELECT id FROM oferta WHERE id_loja = " + i_loja.getIdCopiarLoja();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public boolean validaPromocao(LojaVO i_loja) throws Exception {
        String sql = "SELECT id FROM promocao WHERE id_loja = " + i_loja.getIdCopiarLoja();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public boolean validaReceita(LojaVO i_loja) throws Exception {
        String sql = "SELECT id FROM receitaloja WHERE id_loja = " + i_loja.getIdCopiarLoja();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public boolean validaReceitaToledo(LojaVO i_loja) throws Exception {
        String sql = "SELECT id FROM receitatoledoloja WHERE id_loja = " + i_loja.getIdCopiarLoja();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                return rst.next();
            }
        }
    }

    public boolean isCnpjCadastrado(LojaVO i_loja) throws Exception {
        String sql = "SELECT id_fornecedor FROM loja WHERE id_fornecedor = " + i_loja.getIdFornecedor();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public void salvar(LojaVO i_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {

            //("Criando Loja.");
            stm.execute(script.criarLoja(i_loja).getInsert());
            ProgressBar.setStatus("salvando loja... 3%");

            //("Copiando produtocomplemento.");
            stm.execute(script.copiarProdutoComplemento(i_loja));
            ProgressBar.setStatus("salvando loja... 9%");

            //("Copiando fornecedorprazo.");
            stm.execute(script.copiarFornecedorPrazo(i_loja));
            ProgressBar.setStatus("salvando loja... 10%");

            //("Copiando fornecedorprazopedido.");
            stm.execute(script.copiarFornecedorPrazoPedido(i_loja));
            ProgressBar.setStatus("salvando loja... 11%");

            //("Copiando parametrovalor.");
            stm.execute(script.copiarParametroValor(i_loja));
            ProgressBar.setStatus("salvando loja... 15%");

            //("Copiando pdv.funcaoniveloperador.");
            stm.execute(script.copiarPdvFuncaoNivelOperador(i_loja));
            ProgressBar.setStatus("salvando loja... 18%");

            //("Copiando pdv.parametrovalor.");
            stm.execute(script.copiarPdvParametroValor(i_loja));
            ProgressBar.setStatus("salvando loja... 21%");

            //("Copiando pdv.parametrovalor.");
            pdvParametroValorDAO.atualizarValorPdvParametroValor(i_loja);
            ProgressBar.setStatus("salvando loja... 24%");

            scriptSQLBuilder = pdvCartaoLayoutDAO.copiarPdvCartaoLayout(i_loja);
            if (scriptSQLBuilder != null && !scriptSQLBuilder.isEmpty()) {
                //("Copiando pdv.cartaolayout.");
                stm.execute(scriptSQLBuilder.getInsert());
            }
            ProgressBar.setStatus("salvando loja... 27%");

            List<SQLBuilder> listaDeInserts = new ArrayList<>();
            listaDeInserts = pdvBalancaLayoutDAO.carregarPdvBalancaEtiquetaLayout(i_loja);
            //("Copiando pdv.balancaetiquetalayout.");
            if (listaDeInserts != null && !listaDeInserts.isEmpty()) {

                for (SQLBuilder listaDeInsert : listaDeInserts) {
                    stm.execute(listaDeInsert.getInsert());
                }

            }
            listaDeInserts.clear();
            ProgressBar.setStatus("salvando loja... 35%");

            if (i_loja.isCopiaTecladoLayout()) {
                //("Copiando pdv.tecladolayout.");
                tecladoLayoutDAO.copiarPdvTecladoLayout(i_loja);
                tecladoLayoutDAO.copiarPdvTecladoLayoutFuncao(i_loja);
            }
            ProgressBar.setStatus("salvando loja... 40%");

            //("Copiando pdv.finalizadoraconfiguracao.");
            stm.execute(script.copiarPdvFinalizadoraConfiguracao(i_loja));

            //("Copiando dataprocessamento.");
            stm.execute(script.inserirDataProcessamento(i_loja).getInsert());

            //("Copiando comprovante.");
            stm.execute(script.inserirComprovante(i_loja));

            //("Copiando pdv.operador.");
            stm.execute(script.copiarPdvOperador(i_loja));
            ProgressBar.setStatus("salvando loja... 50%");

            //("Copiando notasaidasequencia.");
            stm.execute(script.inserirNotaSaidaSequencia(i_loja).getInsert());

            //("Copiando tiposaidanotasaidasequencia.");
            stm.execute(script.copiarTipoSaidaNotaSaidaSequencia(i_loja));
            ProgressBar.setStatus("salvando loja... 60%");

            if (i_loja.isCopiaOferta() == true) {
                if (validaOferta(i_loja) == false) {
                    JOptionPane.showMessageDialog(null,
                            "Não existem ofertas cadastradas para a loja " + i_loja.descricao);
                } else {
                    stm.execute(script.copiaOferta(i_loja));
                }
            }

            if (i_loja.isCopiaPromocao() == true) {
                if (validaPromocao(i_loja) == false) {
                    JOptionPane.showMessageDialog(null,
                            "Não existem promoções cadastradas para a loja " + i_loja.descricao);
                } else {
                    stm.execute(script.copiaPromocao(i_loja));
                    stm.execute(script.copiaPromocaoItem(i_loja));
                    stm.execute(script.copiaPromocaoFinalizadora(i_loja));
                    stm.execute(script.copiaPromocaoDesconto(i_loja));

                }

            }
            if (i_loja.isCopiaEcf() == true) {
                //("Copiando ECF.");
                try {

                    stm.execute(script.copiaEcf(i_loja));

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Erro ao copiar ECF, certifique-se de que tenha ecf's na base.");
                }
                try {
                    stm.execute(script.copiaPdvAcumuladorLayout(i_loja));
                } catch (SQLException e) {
                    e.getMessage();
                    throw new Exception("Erro ao copiar PdvAcumuladorLayout.");
                }
                try {
                    stm.execute(script.copiaPdvFinalizadoraLayout(i_loja));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Erro ao copiar PdvFinalizadoraLayout.");
                }
                try {
                    stm.execute(script.copiaPdvAliquotaLayout(i_loja));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Erro ao copiar PdvAliquotaLayout.");
                }
                try {
                    stm.execute(script.copiaAliquotaLayoutRetorno(i_loja));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Erro ao copiar AliquotaLayoutRetorno.");
                }
                try {
                    stm.execute(script.copiaAcumuladorLayoutRetorno(i_loja));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("Erro ao copiar AcumuladorLayoutRetorno.");
                }

                List<String> listaDeInsertsEcf = new ArrayList<>();
                listaDeInsertsEcf = ecfDAO.carregarCopiaEcfLayout(i_loja);
                if (listaDeInsertsEcf.isEmpty()) {
                    throw new Exception("Não foi econtrado valores para cópia de ecf Layout, confira se está copiando de uma loja válida.");
                }
                for (String string : listaDeInsertsEcf) {
                    stm.execute(string);
                }

            }
            ProgressBar.setStatus("salvando loja... 80%");

            if (i_loja.isCopiaOperador() == true) {
                //("Copiando Operador.");
                stm.execute(script.copiarOperador(i_loja));
            }

            if (i_loja.isCopiaUsuario() == true) {
                //("Copiando Usuario.");
                stm.execute(script.copiaUsuarioPermissao(i_loja));
            }
            ProgressBar.setStatus("salvando loja... 90%");

            if (versao.igualOuMaiorQue(4, 1, 39)) {
                //("Copiando parametroagendarecebimento.");
                stm.execute(script.copiarParametroAgendaecebimento(i_loja));
            }

            if (validaReceita(i_loja) == true) {
                if (JOptionPane.showConfirmDialog(null, "Existem receitas na loja anterior deseja copiar ?", "Copia de Loja",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    stm.execute(script.copiaReceitaLoja(i_loja));
                }
                if (validaReceitaToledo(i_loja)) {
                    stm.execute(script.copiaReceitaToledoLoja(i_loja));
                }
            }
            //  stm.execute(copiaEcf(i_loja));

            /* inserir loja na tabela contabilidade.grupoeconomicoloja. */
            //("Adicionando loja no grupoeconomico.");
            stm.execute(script.inserirGrupoEconomicoLoja(i_loja));
            
            if(isSchemaVrHistoricoVendaExiste()){
                stm.execute(script.insereLojaPdvHistoricoVenda(i_loja));
            }
            ProgressBar.setStatus("salvando loja... 100%");

        } catch (Exception ex) {
            System.out.println("\nErro iniciado em LojaDAO no método Salvar\n" + ex.getMessage() + "\n");
            ex.printStackTrace();
            throw ex;
        }

    }

    public void atualizarLoja(LojaVO i_loja) throws Exception {
        ProgressBar.setStatus("Atualizando Loja...");
        SQLBuilder sql = new SQLBuilder();
        sql.setSchema("public");
        sql.setTableName("loja");

        sql.put("descricao", i_loja.getDescricao());
        sql.put("id_fornecedor", i_loja.getIdFornecedor());
        //sql.put("id_situacaocadastro", SituacaoCadastro.ATIVO.getId());
        sql.put("nomeservidor", i_loja.getNomeServidor());
        sql.put("servidorcentral", i_loja.isServidorCentral());
        sql.put("id_regiao", i_loja.getIdRegiao());
        sql.put("geraconcentrador", i_loja.isGeraConcentrador());

        sql.setWhere("id = " + i_loja.getId());

        if (!sql.isEmpty()) {
            try (Statement stmUpdate = Conexao.createStatement()) {
                stmUpdate.execute(sql.getUpdate());
            } catch (Exception ex) {
                System.out.println("Erro iniciado em LojaDAO no método Atualizar\n" + ex.getMessage());
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    public boolean isLoja(int i_idLoja) throws Exception {
        String sql = "SELECT id from loja where id = " + i_idLoja;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public boolean isFornecedor(int i_idFornecedor) throws Exception {
        String sql = "SELECT id FROM loja WHERE id_fornecedor = " + i_idFornecedor;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    public int getId(int i_idFornecedor) throws Exception {
        String sql = "SELECT id FROM loja WHERE id_fornecedor = " + i_idFornecedor;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    return rst.getInt("id");
                } else {
                    return 0;
                }
            }
        }
    }

    public int getIdFornecedor(int i_idLoja) throws Exception {
        String sql = "SELECT id_fornecedor FROM loja WHERE id = " + i_idLoja;

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                if (rst.next()) {
                    return rst.getInt("id_fornecedor");
                } else {
                    return 0;
                }
            }
        }
    }

    public List<Estabelecimento> getLojasVR() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.id,\n"
                    + "	l.descricao,\n"
                    + "	f.nomefantasia,\n"
                    + "	f.razaosocial \n"
                    + "from \n"
                    + "	loja l \n"
                    + "inner join fornecedor f on l.id_fornecedor = f.id where l.id_situacaocadastro = 1\n"
                    + "order by\n"
                    + "	l.id")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    public List<LojaVO> getLojasVRMapeada() throws Exception {
        List<LojaVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	l.id,\n"
                    + "	l.descricao,\n"
                    + "	f.nomefantasia,\n"
                    + "	f.razaosocial\n"
                    + "from\n"
                    + "	loja l\n"
                    + "inner join fornecedor f on\n"
                    + "	l.id_fornecedor = f.id\n"
                    + "where\n"
                    + "	l.id_situacaocadastro = 1 and \n"
                    + "	l.id not in (select distinct id_lojadestino from implantacao2_5.conexaoloja)\n"
                    + "order by\n"
                    + "	l.id")) {
                while (rs.next()) {
                    LojaVO vo = new LojaVO();

                    vo.setId(rs.getInt("id"));
                    vo.setDescricao(rs.getString("descricao"));

                    result.add(vo);
                }
            }
        }
        return result;
    }

    public boolean isSchemaVrHistoricoVendaExiste() throws Exception {
        String sql = "select * \n"
                + "from information_schema.tables\n"
                + "where table_schema = 'vrhistoricovenda'";

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    sql
            )) {
                return rst.next();
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Metodo deletar loja">
    public void deletarLoja(LojaVO i_loja) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("do $$\n"
                    + "begin\n"
                    + "delete from implantacao2_5.operacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebercheque where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.usuario where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.escrita where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.venda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.mdfeemissao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.verbaselloutloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.estoquecongelado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.certificadodigitalloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.balancoestoque where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.transferenciainterna where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.venda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebercaixa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.consistenciaecf where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mensagem.contatovendatotal where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mastermaq.parametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from ficha.setor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from encerramento.logencerramento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.lancamentolacs where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.vendapromocaopontuacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from confere.configuracaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from confere.filaexportacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendatef where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from confere.vendaconfere where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from centralrede.venda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.escritaoutrosvalores where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from ativo.bem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebervendaprazo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from arcos.sugestaopreco where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.promocao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.ofertarelampago where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.produtocomplemento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.parametrovalor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from paf.estoque where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from van.lojaoperadora where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from supersoft.configuracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from sped.configuracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from sped.configuracaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from sef.configuracaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.workflowdivergenciamensagem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.workflowusuarioloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendaecommerce where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendaatacado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendasinteticaprevisao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendasintetica where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendakit where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendacupommedio where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.veiculo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vasilhame where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.veiculodespesa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendamediadata where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.usuarioloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.trocanegociacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.tipoworkflowloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.transformado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.tiposaidanotasaidasequencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.tipoentradatipocentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.tarefa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.tabelaproduto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.setorbalancatoledo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.setorbalancafilizola where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.simplesoutrosvalores where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.rupturacoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reposicaoseparacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reposicaoconferencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reposicaocoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reposicaoseparacaocoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reposicaoconferenciacoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reposicaolojacentralizado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.reservaveiculo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receitaloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receitaoutras where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebivelconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebimento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebervendaprazoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebercreditorotativoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberdevolucaoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberdevolucao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberconveniadoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberchequeitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberconveniado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebercontratoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.produtolojavirtualitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.rebaixacusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.programacaoprecoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberbonificacaoverba where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberbonificacaocontrato where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberbonificacaodevolucao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.produtoautomacaodescontolote where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.produtoautomacaodescontooferta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pricinganalisefiltroloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.producaoordem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.previsaodespesa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.producao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pesquisasatisfacaorespostas where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pesquisalistagem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.portariacat where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.piscofinsoutrosvalores where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.precotacaofornecedorcoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.precotacaofornecedoritem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.planoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.prepedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidonegociacaotroca where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.permissaoloja where id_loja = 1;\n"
                    + "delete from public.pedidoinformacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pesquisa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidolimitesemanafornecedoritem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidolimitesemanacomprador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.peps where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidolimitesemanamercadologicoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidocoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidoconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidocrossdocking where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pedidodespesa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pagaroutrasdespesas where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pagaroutrasdespesascentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pagaradiantamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.ofertalote where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.outrosvalorespiscofins where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaservico where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pagarfechamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notasaidaconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notasaidacoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notasaidasequencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaentradavalefornecedor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaentradanfe where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaentradacoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaentradanfependencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notadespesa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.nfcesequencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaentradacentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notadespesacentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.midia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.mudancatributacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.recebercreditorotativo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.lucrodiariocliente where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.maparesumosequencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logtroca where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.lojareposicao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logtransacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.maparesumoconfiguracaoaliquota where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logtransacaopedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.maparesumo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.mensagemgrupo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.maparesumoconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.mensagem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logpedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logcestabasica where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logcusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logpreco where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.listagem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.logestoque where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.grupoprecoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.grupopreco where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.inventario where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.inventarioterceiro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.guiaentrega where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.inventarioicmsst where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.geracaoretencaotributoitemnotasaida where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.fornecedorprazopedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.fornecedorsaldo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.fornecedorprazo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.geracaoretencaotributoitemnotadespesa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.estoqueterceiro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.feriado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.formulariohorario where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.formularioloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.escritasaldo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.estoque where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.estornooutrosvalores where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.escritafechamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.escritacentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.divergenciasworkflowpedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.divergenciasworkflowentrada where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.droprevisaotipoentrada where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.droprevisaovenda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.diasestoqueprojecao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.vendamedia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.dianaoutil where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.doacaoentidade where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.descontocondicionalitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.divergenciasworkflowcotacaocliente where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.diefconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.dmaconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.oferta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.ddvmercadologico where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.cotacaoclienteinformacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.deliverycoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.cotacaocliente where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.conveniadotransacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.delivery where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.cotacaoclientecoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contratoexcecaoacordo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contratoacordoexcecaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contratolancamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contratofornecedorexcecaoacordo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contabilidade where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contabilidadelancamentocentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.conveniado where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.conveniado_ant where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.controlepoliciacivilconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.contabilidadeanterior where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.concentrador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.configuracaologotipoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.conciliacaobancariacentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.configuracaodaicms where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.comprovante where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.clientepreferencialdiasbloqueioparametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.clientepreferencialpromocaovenda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.caixasaldo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.centralizacaoicmslojaparametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.caixa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.balancoprelancamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.balancoestoqueanterior where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.balanco where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.caixasaldoempresa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.centrocustoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.bancoconta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberverba where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberoutrasreceitasitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.perda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.pagarfornecedor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.verba where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberverbaitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.quebra where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notasaida where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.receberoutrasreceitas where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.consumo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.parametrovalor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.notaentrada where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.administracaoprecodivergencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.administracaoprecolojavirtual where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.administracaoprecoconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.dataprocessamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.analisecentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.administracaopreco where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.agendafornecedorloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.agenda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.administracaoprecolote where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.produtoautomacaodesconto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.agendamentorecebimento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from public.abastecimento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendapromocaopontuacaoitem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendapromocaopontuacaobaixaproduto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendapromocaopontuacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendaoperador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendaoperadorrecebiveltmp where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendaoperadorauditoria where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.valepresente where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.valegasproduto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.vendakit where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.trocacupom where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.valegas where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.tecladolayoutfuncao where id_tecladolayout = " + i_loja.getId() + "; \n"
                    + "delete from pdv.tecladolayout where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.pos where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.retirada where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.maparesumo where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.logtransacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.ficha where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.funcaoniveloperador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.finalizadoralayout where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.finalizadoraconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.ecf where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.documento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.devolucaocupom where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.operador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.descontooperador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.agendatema where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.creditorotativoparcela where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.consistencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.contravale where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.concentrador where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from paf.envioreducaoz where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.cartaolayout where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.balancaetiquetalayout where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.aliquotalayout where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.acumuladorlayout where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from pdv.comprovantenaofiscal where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from paf.envioestoquemensal where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mensagem.contatoencerramentodiario where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mensagem.contatoprecotacaofornecedor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mensagem.contatopedidoatendido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mensagem.contatovendaparcial where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from mensagem.contatodivergenciapedido where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from implantacao.codigobarrasanterior where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from implantacao.codigoanteriorforn where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from implantacao.codigoanteriorcli where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from implantacao.codigoanterior where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from gerenciadornfce.vendaprocessada where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from gerenciadordmcard.exportacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from gerenciadorarcos.filadiferenca where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from gerenciadorarcos.filaexportacao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from fortes.parametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from gerenciadorarcos.precificacaotemp where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from gerenciadorarcos.ofertatemp where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from food.venda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from fiscal.processosjudiciais where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from fiscal.escritaprodutocomplementost where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from ficha.impressora where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from fgf.configuracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from encerramento.configuracaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from folhamatic.parametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from ficha.mesa where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from encerramento.dataestornoconsistencia where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from emissoretiqueta.impressora where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from emissoretiqueta.produtocoletor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from emissoretiqueta.configuracaoetiqueta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from dominio.parametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from crescevendas.log where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from crescevendas.vendacrescevendas where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contmatic.parametro where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from emissoretiqueta.logproduto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.lancamentolalurpartea where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.planocontasaldoinicial where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.grupoeconomicoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.dresaldoinicial where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.agendamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from contabilidade.agendamentoitemcentrocusto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.voucher where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from consultapreco.exportacaoproduto where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.produtopontuacaoexclusaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.produtopontuacaoloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.vendapromocaopontuacaoexclusao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.produtobloqueiocancelamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.produtobloqueio where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.ofertadataprocessamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.produtopontuacaoimagemloja where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.dataencerramentodiario where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.folhetooferta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.folhetoofertacancelamento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.lojaconfiguracao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.log where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from connect.lembrete where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from confere.controleconsulta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from confere.configuracaotef where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from comissao.venda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from centralrede.parametrovalor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from centralcompra.vendagestao where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from comissao.vendedor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from centralcompra.notasaida where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from ativo.baixabem where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from atacarejo.produtocomplemento where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from atacarejo.parametrovalor where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from atacarejo.venda where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from arcos.sugestaooferta where id_loja = " + i_loja.getId() + ";\n"
                    + "delete from loja where id = " + i_loja.getId() + ";\n"
                    + "end;\n"
                    + "$$ language plpgsql");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro iniciado em LojaDAO no método Salvar\n"
                    + "Consequentemente o erro seguiu para o método de deletar.\n"
                    + "Entre em contato com o setor de Migração.");
        }
    }
    //</editor-fold>
}
