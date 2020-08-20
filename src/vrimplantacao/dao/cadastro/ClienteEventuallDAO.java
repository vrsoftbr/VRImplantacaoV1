package vrimplantacao.dao.cadastro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClienteEventualVO;

public class ClienteEventuallDAO {

    public void salvar(List<ClienteEventualVO> v_clienteEventual) throws Exception {
        salvar(v_clienteEventual, 1, false); 
    }
    
    public void salvar(List<ClienteEventualVO> v_clienteEventual, int id_lojaCliente, boolean gerarCodigo) throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null,
                  stm3 = null, stm4 = null;
        
        String erro = "";

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_clienteEventual.size());
            ProgressBar.setStatus("Importando Cliente Eventual...");
            
            Map<Long, Integer> cnpjsExistentes = getCnpjExistentes();
            Map<String, Integer> codigoAnterior = getCodigosAnteriores();
            Set<Integer> idsExistentes = getIdsExistentes();
            Stack<Integer> idsVagos = new CodigoInternoDAO().getIdsVagosClienteEventual(60000);

            for (ClienteEventualVO i_clienteEventual : v_clienteEventual) {      
                Integer idAtual = cnpjsExistentes.get(i_clienteEventual.getCnpj());
                //Verifica se o CNPJ existe, se existir não grava, mas relaciona na tabela de cliente anterior.
                if (idAtual == null) {
                    //i_clienteEventual.cnpj = i_clienteEventual.id;

                        boolean gerarId = false;
                        
                        if (gerarCodigo) {
                            gerarId = true;
                        } else {
                            if (i_clienteEventual.id > 999999) {
                                gerarId = true;
                            } else {
                                if (idsExistentes.contains(i_clienteEventual.id)) {
                                    gerarId = true;
                                }
                            }
                        }
 
                        long idAnterior = i_clienteEventual.id;

                        if (gerarId) {
                            i_clienteEventual.setId(idsVagos.pop());
                        } else {
                            idsVagos.remove(Integer.valueOf(i_clienteEventual.getId()));
                        }
                        
                        long cnpj = (i_clienteEventual.getCnpj() <= 0 ? i_clienteEventual.getId() : i_clienteEventual.getCnpj());
                        
                        idsExistentes.add(i_clienteEventual.getId());
                        cnpjsExistentes.put(cnpj, i_clienteEventual.getId());

                        sql = new StringBuilder();
                        sql.append("INSERT INTO clienteeventual(");
                        sql.append("id, nome, endereco, bairro, id_estado, telefone, id_tipoinscricao, ");
                        sql.append("inscricaoestadual, id_situacaocadastro, fax, enderecocobranca, ");
                        sql.append("bairrocobranca, id_estadocobranca, telefonecobranca, prazopagamento, ");
                        sql.append("id_tipoorgaopublico, datacadastro, limitecompra, cobrataxanotafiscal, ");
                        sql.append("id_municipio, id_municipiocobranca, cep, cnpj, cepcobranca, id_tiporecebimento, ");
                        sql.append("bloqueado, numero, observacao, id_pais, inscricaomunicipal, id_contacontabilfiscalpassivo, ");
                        sql.append("numerocobranca, complemento, complementocobranca, id_contacontabilfiscalativo) ");
                        sql.append("VALUES (");
                        sql.append(i_clienteEventual.id + ", ");
                        sql.append("'" + i_clienteEventual.nome + "', ");
                        sql.append("'" + i_clienteEventual.endereco + "', ");
                        sql.append("'" + i_clienteEventual.bairro + "', ");
                        sql.append(i_clienteEventual.id_estado + ", ");
                        sql.append("'" + i_clienteEventual.telefone + "', ");
                        sql.append(i_clienteEventual.id_tipoinscricao + ", ");
                        sql.append("'" + i_clienteEventual.inscricaoestadual + "', ");
                        sql.append(i_clienteEventual.id_situacaocadastro + ", ");
                        sql.append("'" + i_clienteEventual.fax + "', ");
                        sql.append("'" + i_clienteEventual.enderecocobranca + "', ");
                        sql.append("'" + i_clienteEventual.bairrocobranca + "', ");
                        sql.append((i_clienteEventual.id_estadocobranca == -1 ? null : i_clienteEventual.id_estadocobranca) + ", ");
                        sql.append("'" + i_clienteEventual.telefonecobranca + "', ");
                        sql.append(i_clienteEventual.prazopagamento + ", ");
                        sql.append(i_clienteEventual.id_tipoorgaopublico + ", ");

                        if (Utils.acertarTexto(i_clienteEventual.datacadastro).isEmpty()) {
                            sql.append("now(), ");
                        } else {
                            sql.append("'" + i_clienteEventual.datacadastro + "', ");
                        }

                        sql.append(i_clienteEventual.limitecompra + ", ");
                        sql.append(i_clienteEventual.cobrataxanotafiscal + ", ");
                        sql.append(i_clienteEventual.id_municipio + ", ");
                        sql.append((i_clienteEventual.id_municipiocobranca == -1 ? null : i_clienteEventual.id_municipiocobranca) + ", ");
                        sql.append(i_clienteEventual.cep + ", ");
                        sql.append(cnpj + ", ");
                        sql.append(i_clienteEventual.cepcobranca + ", ");
                        sql.append((i_clienteEventual.id_tiporecebimento == -1 ? null : i_clienteEventual.id_tiporecebimento) + ", ");
                        sql.append(i_clienteEventual.bloqueado + ", ");
                        sql.append("'" + i_clienteEventual.numero + "', ");
                        sql.append("'" + i_clienteEventual.observacao + "', ");
                        sql.append(i_clienteEventual.id_pais + ", ");
                        sql.append("'" + i_clienteEventual.inscricaomunicipal + "', ");
                        sql.append((i_clienteEventual.id_contacontabilfiscalpassivo == -1 ? null : i_clienteEventual.id_contacontabilfiscalpassivo) + ", ");
                        sql.append("'" + i_clienteEventual.numerocobranca + "', ");
                        sql.append("'" + i_clienteEventual.complemento + "', ");
                        sql.append("'" + i_clienteEventual.complementocobranca + "', ");
                        sql.append((i_clienteEventual.id_contacontabilfiscalativo == -1 ? null : i_clienteEventual.id_contacontabilfiscalativo) + " ");
                        sql.append(");");
                        
                        erro = sql.toString();

                        stm.execute(sql.toString());

                        if (!i_clienteEventual.telefone2.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clienteeventualcontato (");
                            sql.append("id_clienteeventual, nome, telefone, celular, id_tipocontato, email) ");
                            sql.append("values (");
                            sql.append(i_clienteEventual.id + ",");
                            sql.append("'TELEFONE',");
                            sql.append("'" + i_clienteEventual.telefone2 + "',");
                            sql.append("'',");
                            sql.append("0,'');");

                            stm.execute(sql.toString());
                        }

                        if (!i_clienteEventual.telefone3.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clienteeventualcontato (");
                            sql.append("id_clienteeventual, nome, telefone, celular, id_tipocontato, email) ");
                            sql.append("values (");
                            sql.append(i_clienteEventual.id + ",");
                            sql.append("'TELEFONE',");
                            sql.append("'" + i_clienteEventual.telefone3 + "',");
                            sql.append("'',");
                            sql.append("0,'');");

                            stm.execute(sql.toString());
                        }

                        if (!i_clienteEventual.email.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clienteeventualcontato (");
                            sql.append("id_clienteeventual, nome, telefone, celular, id_tipocontato, email) ");
                            sql.append("values (");
                            sql.append(i_clienteEventual.id + ",");
                            sql.append("'EMAIL',");
                            sql.append("'',");
                            sql.append("'',");
                            sql.append("0," + Utils.quoteSQL(i_clienteEventual.email) + ");");

                            stm.execute(sql.toString());
                        }

                        if (!i_clienteEventual.contato.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clienteeventualcontato (");
                            sql.append("id_clienteeventual, nome, telefone, celular, id_tipocontato, email) ");
                            sql.append("values (");
                            sql.append(i_clienteEventual.id + ",");
                            sql.append("'TELEFONE',");
                            sql.append("'" + i_clienteEventual.contato + "',");
                            sql.append("'',");
                            sql.append("0,'');");

                            stm.execute(sql.toString());
                        }
                        
                        stm.execute(
                                "insert into implantacao.codigoanteriorclienteeventual (codigoanterior, codigoatual, id_loja) values (" +
                                        String.valueOf(idAnterior) + ", " +
                                        String.valueOf(i_clienteEventual.getId()) + ", " +
                                        id_lojaCliente
                                        + ");");
                        codigoAnterior.put(id_lojaCliente + "-" + idAnterior, i_clienteEventual.getId());                    
                } else { 
                    if (!codigoAnterior.containsKey(id_lojaCliente + "-" + i_clienteEventual.getId())) {
                        stm.execute(
                                    "insert into implantacao.codigoanteriorclienteeventual (codigoanterior, codigoatual, id_loja) values (" +
                                            String.valueOf(i_clienteEventual.getId()) + ", " +
                                            String.valueOf(idAtual) + ", " +
                                            id_lojaCliente
                                            + ");");
                        codigoAnterior.put(id_lojaCliente + "-" + i_clienteEventual.getId(), idAtual);
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            stm4.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;// new Exception("SQL: " + erro, ex);
        }
    }

    public void acertarCnpj(List<ClienteEventualVO> vClienteEventual) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Mori (Orion)\\scripts\\update_clienteEventual.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);

        try {

            Conexao.begin();

            ProgressBar.setMaximum(vClienteEventual.size());
            ProgressBar.setStatus("Acertar cnpj...Cliente Eventual...");

            stm = Conexao.createStatement();

            for (ClienteEventualVO i_clienteEventual : vClienteEventual) {

                sql = new StringBuilder();
                sql.append("update clienteeventual set ");
                sql.append("cnpj = " + (i_clienteEventual.cnpj == -1 ? i_clienteEventual.id : i_clienteEventual.cnpj) + ", ");
                sql.append("id_tipoinscricao = " + i_clienteEventual.id_tipoinscricao + " ");
                sql.append("where id = " + i_clienteEventual.id + ";");

                bw.write(sql.toString());
                bw.newLine();

                ProgressBar.next();

            }

            bw.flush();
            bw.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public int getId(long i_cnpj) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM clienteeventual WHERE cnpj = " + i_cnpj);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("O cnpj " + i_cnpj + " não esta cadastrado!");
        }

        int id = rst.getInt("id");

        stm.close();

        return id;
    }

    private Map<Long, Integer> getCnpjExistentes() throws Exception {
        Map<Long, Integer> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, cnpj from clienteeventual order by id"
            )) {
                while (rst.next()) {
                    result.put(rst.getLong("cnpj"), rst.getInt("id"));
                }
            }
        }
        return result;
    }

    private Map<String, Integer> getCodigosAnteriores() throws Exception{
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id_loja, codigoanterior, codigoatual from implantacao.codigoanteriorclienteeventual order by id_loja, codigoanterior"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id_loja") + "-" + rst.getString("codigoanterior"), rst.getInt("codigoatual"));
                }
            }
        }
        return result;
    }

    private Set<Integer> getIdsExistentes() throws Exception {
        Set<Integer> result = new LinkedHashSet<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id from clienteeventual order by id"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
    
    public int getIdByCodAnt(String sistema, String loja, String id) throws Exception {
        int result;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select ant.id as codigoanterior, c.id as codigoatual "
                    + "from implantacao.codant_clienteeventual ant \n"
                    + "join clienteeventual c on c.id = ant.codigoatual\n"
                    + "where ant.sistema = '" + sistema + "'\n"
                    + "and ant.loja = '" + loja + "' \n"
                    + "and ant.id = '" + id + "'"
            )) {
                if (rst.next()) {
                    result = rst.getInt("codigoatual");
                } else {
                    result = -1;
                }
            }
        }
        return result;
    }

    public int getIdByCnpj(Long cnpj) throws Exception {
        int result;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select id, cnpj from clienteeventual where cnpj = " + cnpj
            )) {
                if (rst.next()) {
                    result = rst.getInt("id");
                } else {
                    result = -1;
                }
            }
        }
        return result;
    }
    
}
