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
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;

public class ClientePreferencialDAO {

    public boolean unificacao = false;
    public boolean utilizarCodigoAnterior = false;
    public boolean manterID = false;
    public boolean salvarCodigoCidadeSistemaAnterior = false;
    /**
     * Quando esta variável é setada com true, o método salvar não grava o cliente
     * se já houver alguém com o mesmo CPF/CNPJ. Se for false, o id do cliente
     * é colocado no lugar no CPF/CNPJ.
     */
    public boolean naoGravarCpfCnpjRepetidos = false;
    
    /**
     * Procedimento que criptografa a senha do cliente.
     * @param senha senha que será gravada no banco.
     * @return senha criptografada.
     */
    public static int criptografarSenha(int senha) {
        if (senha < 0) {
            senha = 0;
        }
        return senha * 34;
    }
    
    /**
     * Procedimento que descriptografa a senha do cliente.
     * @param senha Senha criptografada.
     * @return Senha descriptografada.
     */
    public static int descriptografarSenha(int senha) {
        senha = senha / 34;
        if (senha < 0) {
            senha = 0;
        }
        return senha / 34;
    }

    public void salvar(List<ClientePreferencialVO> v_clientePreferencial, int id_loja, int id_lojaCliente) throws Exception {
        salvar(v_clientePreferencial, id_loja, id_lojaCliente, false);
    }
    
    /**
     * Retorna o código anterior do cliente de acordo com o parâmetro utilizarCodigoAnterior
     * @param i_clientePreferencial Cliente preferêncial.
     * @return Código anterior.
     */
    private long getCodigoAnterior(ClientePreferencialVO i_clientePreferencial) {
        if (!utilizarCodigoAnterior) {
            if (i_clientePreferencial.getIdLong() > 0) {
                return i_clientePreferencial.getIdLong();
            } else {
                return i_clientePreferencial.getId();
            }
        } else {
            return i_clientePreferencial.getCodigoanterior();
        }
    }

    public void salvar(List<ClientePreferencialVO> v_clientePreferencial, int id_loja, int id_lojaCliente, boolean deletar) throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null,
                stm3 = null, stm4 = null;
        ResultSet rst = null,
                rst2 = null,
                rst3 = null;
        java.sql.Date datacadastro;
        int Linha = 0;    
        String Erro = "";
        datacadastro = new java.sql.Date(new java.util.Date().getTime());
        
        
        
        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Importando Cliente Preferencial...");

            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {

                boolean mesmoCnpj = false;
                boolean gravar = true;
                
                long idAnterior;                
                if (!utilizarCodigoAnterior) {
                    if (i_clientePreferencial.getIdLong() > 0) {
                        idAnterior = i_clientePreferencial.getIdLong();
                    } else {
                        idAnterior = i_clientePreferencial.getId();
                    }
                } else {
                    idAnterior = i_clientePreferencial.getCodigoanterior();
                }
                
                if (!unificacao) {

                    sql = new StringBuilder();
                    sql.append("select c.id from clientepreferencial c ")
                        .append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ")
                        .append("where ant.codigoanterior = ")
                        .append(idAnterior)
                        .append(" and ant.id_loja = ")
                        .append(id_loja);

                    rst3 = stm4.executeQuery(sql.toString());

                    if (!rst3.next()) {

                        //Verifica se o id está dentro do intervalo permitido pelo VR
                        if (!manterID) {
                            if (idAnterior <= 999999) {
                                sql = new StringBuilder();
                                sql.append("select id from clientepreferencial ");
                                sql.append("where id = " + idAnterior);

                                rst = stm.executeQuery(sql.toString());

                                if (rst.next()) {                            
                                    i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                                } else {
                                    i_clientePreferencial.id = (int) idAnterior;
                                }
                            } else {
                                i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                            }
                        } else {
                            if (i_clientePreferencial.id <= 999999) {
                                sql = new StringBuilder();
                                sql.append("select id from clientepreferencial ");
                                sql.append("where id = " + i_clientePreferencial.id);

                                rst = stm.executeQuery(sql.toString());

                                if (rst.next()) {                            
                                    i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                                }
                            } else {
                                i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                            }
                        }
                        
                        sql = new StringBuilder();
                        sql.append("SELECT cnpj from clientepreferencial ");
                        sql.append("WHERE cnpj = " + i_clientePreferencial.cnpj);
                        rst = stm3.executeQuery(sql.toString());

                        if (rst.next()) {
                            mesmoCnpj = true;
                            i_clientePreferencial.cnpj = i_clientePreferencial.id;
                        } 
                        
                        if (naoGravarCpfCnpjRepetidos) {
                            gravar = !mesmoCnpj;
                        }
                        
                        if (gravar) {
                        
                            Linha = i_clientePreferencial.id;

                            sql = new StringBuilder();
                            sql.append("INSERT INTO clientepreferencial( ");
                            sql.append("id, nome, id_situacaocadastro, endereco, bairro, id_estado, id_municipio, ");
                            sql.append("cep, telefone, celular, email, inscricaoestadual, orgaoemissor, ");
                            sql.append("cnpj, id_tipoestadocivil, datanascimento, dataresidencia, datacadastro, ");
                            sql.append("id_tiporesidencia, sexo, id_banco, agencia, conta, praca, observacao, ");
                            sql.append("empresa, id_estadoempresa, id_municipioempresa, enderecoempresa, ");
                            sql.append("bairroempresa, cepempresa, telefoneempresa, dataadmissao, cargo, ");
                            sql.append("salario, outrarenda, valorlimite, nomeconjuge, datanascimentoconjuge, ");
                            sql.append("cpfconjuge, rgconjuge, orgaoemissorconjuge, empresaconjuge, id_estadoconjuge, ");
                            sql.append("id_municipioconjuge, enderecoempresaconjuge, bairroempresaconjuge, ");
                            sql.append("cepempresaconjuge, telefoneempresaconjuge, dataadmissaoconjuge, ");
                            sql.append("cargoconjuge, salarioconjuge, outrarendaconjuge, id_tipoinscricao, ");
                            sql.append("vencimentocreditorotativo, observacao2, permitecreditorotativo, ");
                            sql.append("permitecheque, nomemae, nomepai, datarestricao, bloqueado, id_plano, ");
                            sql.append("bloqueadoautomatico, numero, senha, id_tiporestricaocliente, ");
                            sql.append("dataatualizacaocadastro, numeroempresa, numeroempresaconjuge, ");
                            sql.append("complemento, complementoempresa, complementoempresaconjuge, id_contacontabilfiscalpassivo,  ");
                            sql.append("id_contacontabilfiscalativo, enviasms, enviaemail, id_grupo,  ");
                            sql.append("id_regiaocliente) ");
                            sql.append(" values ( ");
                            sql.append(i_clientePreferencial.id + ", '" + i_clientePreferencial.nome + "', " + i_clientePreferencial.id_situacaocadastro + ", ");
                            sql.append("'" + i_clientePreferencial.endereco + "', '" + i_clientePreferencial.bairro + "', ");
                            sql.append(i_clientePreferencial.id_estado + ", " + i_clientePreferencial.id_municipio + ", ");
                            sql.append(i_clientePreferencial.cep + ", '" + i_clientePreferencial.telefone + "', '" + i_clientePreferencial.celular + "', ");
                            sql.append("'" + i_clientePreferencial.email + "', '" + i_clientePreferencial.inscricaoestadual + "', ");
                            sql.append("'" + i_clientePreferencial.orgaoemissor + "', ");

                            if ((i_clientePreferencial.cnpj == -1) || (i_clientePreferencial.cnpj == 0)) {

                                sql.append(i_clientePreferencial.id + ", ");
                            } else {
                                sql.append(i_clientePreferencial.cnpj + ", ");
                            }

                            sql.append(i_clientePreferencial.id_tipoestadocivil + ", ");

                            if ((i_clientePreferencial.datanascimento != null)
                                    && (!i_clientePreferencial.datanascimento.isEmpty())) {
                                sql.append("'" + i_clientePreferencial.datanascimento + "',");
                            } else {
                                sql.append(null + ",");
                            }
                            String dataCadastro;
                            if ((i_clientePreferencial.getDatacadastro() != null) &&
                                    (!i_clientePreferencial.datacadastro.trim().isEmpty())) {
                                dataCadastro = i_clientePreferencial.getDatacadastro().trim();
                            } else {
                                dataCadastro = Utils.formatDate(new java.util.Date());
                            }

                            sql.append("'1990-01-01', " + Utils.quoteSQL(dataCadastro) + ", " + i_clientePreferencial.id_tiporesidencia + ", ");
                            sql.append(i_clientePreferencial.sexo + ", " + i_clientePreferencial.id_banco + ", '" + i_clientePreferencial.agencia + "', ");
                            sql.append("'" + i_clientePreferencial.conta + "', '" + i_clientePreferencial.praca + "', '" + i_clientePreferencial.observacao + "', ");
                            sql.append("'" + i_clientePreferencial.empresa + "',");
                            sql.append((i_clientePreferencial.id_estadoempresa == 0 ? null : i_clientePreferencial.id_estadoempresa) + ", ");
                            sql.append((i_clientePreferencial.id_municipioempresa == 0 ? null : i_clientePreferencial.id_municipioempresa) + ", ");
                            sql.append("'" + i_clientePreferencial.enderecoempresa + "', '" + i_clientePreferencial.bairroempresa + "', " + i_clientePreferencial.cepempresa + ", '" + i_clientePreferencial.telefoneempresa + "', ");
                            sql.append((i_clientePreferencial.dataadmissao != null ? "'"+i_clientePreferencial.dataadmissao+"'" : "NULL") +  ", '" + i_clientePreferencial.cargo + "', " + i_clientePreferencial.salario + ", " + i_clientePreferencial.outrarenda + ", ");
                            sql.append(i_clientePreferencial.valorlimite + ", '" + i_clientePreferencial.nomeconjuge + "', NULL, " + i_clientePreferencial.cpfconjuge + ", ");
                            sql.append("'" + i_clientePreferencial.rgconjuge + "', '" + i_clientePreferencial.orgaoemissorconjuge + "', '" + i_clientePreferencial.empresaconjuge + "', ");
                            sql.append("NULL, NULL, '" + i_clientePreferencial.enderecoempresaconjuge + "', '" + i_clientePreferencial.bairroempresaconjuge + "', ");
                            sql.append(i_clientePreferencial.cepempresaconjuge + ", '" + i_clientePreferencial.telefoneempresaconjuge + "', NULL, ");
                            sql.append("'" + i_clientePreferencial.cargoconjuge + "', " + i_clientePreferencial.salarioconjuge + ", " + i_clientePreferencial.outrarendaconjuge + ", ");
                            sql.append(i_clientePreferencial.id_tipoinscricao + ", " + i_clientePreferencial.vencimentocreditorotativo + ", '" + i_clientePreferencial.observacao2 + "', " + (i_clientePreferencial.valorlimite <= 0 ? false : true) + ", ");
                            sql.append((i_clientePreferencial.valorlimite <= 0 ? false : true) + ", '" + i_clientePreferencial.nomemae + "', '" + i_clientePreferencial.nomepai + "', NULL, ");
                            sql.append(i_clientePreferencial.bloqueado + ", " + i_clientePreferencial.id_plano + ", " + i_clientePreferencial.bloqueadoautomatico + ", ");
                            sql.append("'" + i_clientePreferencial.numero + "', " + criptografarSenha(i_clientePreferencial.senha) + ", " + i_clientePreferencial.id_tiporestricaocliente + ", ");

                            if ((i_clientePreferencial.datanascimentoconjuge != null)
                                    && (!i_clientePreferencial.datanascimentoconjuge.isEmpty())) {
                                sql.append("'" + i_clientePreferencial.datanascimentoconjuge + "',");
                            } else {
                                sql.append(null + ",");
                            }
                            sql.append("'" + i_clientePreferencial.numeroempresa + "', '" + i_clientePreferencial.numeroempresaconjuge + "', ");
                            sql.append("'" + i_clientePreferencial.complemento + "', '" + i_clientePreferencial.complementoempresa + "', '" + i_clientePreferencial.complementoempresaconjuge + "', ");
                            sql.append((i_clientePreferencial.id_contacontabilfiscalpassivo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalpassivo) + ", ");
                            sql.append((i_clientePreferencial.id_contacontabilfiscalativo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalativo) + ", ");
                            sql.append(i_clientePreferencial.enviasms + ", ");
                            sql.append(i_clientePreferencial.enviaemail + ", NULL, 1);");

                            Erro = sql.toString();
                            try {
                                stm.execute(sql.toString());


                            } catch (Exception ex) {
                                throw new VRException(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
                            }

                            if (!i_clientePreferencial.fax.isEmpty()) {
                                sql = new StringBuilder();
                                sql.append("insert into clientepreferencialcontato (");
                                sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                                sql.append("values (");
                                sql.append(i_clientePreferencial.id + ",");
                                sql.append("'FAX',");
                                sql.append("'" + i_clientePreferencial.fax + "',");
                                sql.append("'',");
                                sql.append("0);");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }

                            if (!i_clientePreferencial.telefone2.isEmpty()) {
                                sql = new StringBuilder();
                                sql.append("insert into clientepreferencialcontato (");
                                sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                                sql.append("values (");
                                sql.append(i_clientePreferencial.id + ",");
                                sql.append("'TELEFONE',");
                                sql.append("'" + i_clientePreferencial.telefone2 + "',");
                                sql.append("'',");
                                sql.append("0);");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }

                            if (!i_clientePreferencial.celular.isEmpty()) {
                                sql = new StringBuilder();
                                sql.append("insert into clientepreferencialcontato (");
                                sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                                sql.append("values (");
                                sql.append(i_clientePreferencial.id + ",");
                                sql.append("'CELULAR',");
                                sql.append("'" + i_clientePreferencial.celular + "',");
                                sql.append("'',");
                                sql.append("0);");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }
                            //TODO Ajustar a rotina para caso o cliente já exista, mapear o códigoanterior corretamente.
                            
                            if (salvarCodigoCidadeSistemaAnterior) {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                                sql.append("codigoagente, codigoanterior, codigoatual, id_loja, codigocidade_sistemaanterior) ");
                                sql.append("VALUES ( ");
                                sql.append(i_clientePreferencial.codigoAgente + ",");
                                sql.append(i_clientePreferencial.codigoanterior + ", ");
                                sql.append(i_clientePreferencial.id + ", ");
                                sql.append(id_loja + ", ");
                                sql.append(i_clientePreferencial.getCodigocidade_sistemaanterior() + ");");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            } else {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                                sql.append("codigoagente, codigoanterior, codigoatual, id_loja) ");
                                sql.append("VALUES ( ");
                                sql.append(i_clientePreferencial.codigoAgente + ",");
                                sql.append(i_clientePreferencial.codigoanterior + ", ");
                                sql.append(i_clientePreferencial.id + ", ");
                                sql.append(id_loja + ");");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }
                        }
                    }
                } else {
                    i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial");

                    sql = new StringBuilder();
                    sql.append("SELECT cnpj from clientepreferencial ");
                    sql.append("WHERE cnpj = " + i_clientePreferencial.cnpj);
                    rst = stm.executeQuery(sql.toString());
                    if (rst.next()) {
                        i_clientePreferencial.cnpj = i_clientePreferencial.id;
                        mesmoCnpj = true;
                    }
                    
                    if (naoGravarCpfCnpjRepetidos) {
                        gravar = !mesmoCnpj;
                    }
                    
                    if (gravar) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO clientepreferencial( ");
                        sql.append("id, nome, id_situacaocadastro, endereco, bairro, id_estado, id_municipio, ");
                        sql.append("cep, telefone, celular, email, inscricaoestadual, orgaoemissor, ");
                        sql.append("cnpj, id_tipoestadocivil, datanascimento, dataresidencia, datacadastro, ");
                        sql.append("id_tiporesidencia, sexo, id_banco, agencia, conta, praca, observacao, ");
                        sql.append("empresa, id_estadoempresa, id_municipioempresa, enderecoempresa, ");
                        sql.append("bairroempresa, cepempresa, telefoneempresa, dataadmissao, cargo, ");
                        sql.append("salario, outrarenda, valorlimite, nomeconjuge, datanascimentoconjuge, ");
                        sql.append("cpfconjuge, rgconjuge, orgaoemissorconjuge, empresaconjuge, id_estadoconjuge, ");
                        sql.append("id_municipioconjuge, enderecoempresaconjuge, bairroempresaconjuge, ");
                        sql.append("cepempresaconjuge, telefoneempresaconjuge, dataadmissaoconjuge, ");
                        sql.append("cargoconjuge, salarioconjuge, outrarendaconjuge, id_tipoinscricao, ");
                        sql.append("vencimentocreditorotativo, observacao2, permitecreditorotativo, ");
                        sql.append("permitecheque, nomemae, nomepai, datarestricao, bloqueado, id_plano, ");
                        sql.append("bloqueadoautomatico, numero, senha, id_tiporestricaocliente, ");
                        sql.append("dataatualizacaocadastro, numeroempresa, numeroempresaconjuge, ");
                        sql.append("complemento, complementoempresa, complementoempresaconjuge, id_contacontabilfiscalpassivo,  ");
                        sql.append("id_contacontabilfiscalativo, enviasms, enviaemail, id_grupo,  ");
                        sql.append("id_regiaocliente) ");
                        sql.append(" values ( ");

                        sql.append(i_clientePreferencial.id + ", '" + i_clientePreferencial.nome + "', " + i_clientePreferencial.id_situacaocadastro + ", ");
                        sql.append("'" + i_clientePreferencial.endereco + "', '" + i_clientePreferencial.bairro + "', ");
                        sql.append(i_clientePreferencial.id_estado + ", " + i_clientePreferencial.id_municipio + ", ");
                        sql.append(i_clientePreferencial.cep + ", '" + i_clientePreferencial.telefone + "', '" + i_clientePreferencial.celular + "', ");
                        sql.append("'" + i_clientePreferencial.email + "', '" + i_clientePreferencial.inscricaoestadual + "', ");
                        sql.append("'" + i_clientePreferencial.orgaoemissor + "', " + i_clientePreferencial.cnpj + ", " + i_clientePreferencial.id_tipoestadocivil + ", ");

                        if ((i_clientePreferencial.datanascimento != null)
                                && (!i_clientePreferencial.datanascimento.isEmpty())) {
                            sql.append("'" + i_clientePreferencial.datanascimento + "',");
                        } else {
                            sql.append(null + ",");
                        }
                        sql.append("'1990-01-01', '" + (i_clientePreferencial.datacadastro == "" ? datacadastro : i_clientePreferencial.datacadastro) + "', " + i_clientePreferencial.id_tiporesidencia + ", ");
                        sql.append(i_clientePreferencial.sexo + ", " + i_clientePreferencial.id_banco + ", '" + i_clientePreferencial.agencia + "', ");
                        sql.append("'" + i_clientePreferencial.conta + "', '" + i_clientePreferencial.praca + "', '" + i_clientePreferencial.observacao + "', ");
                        sql.append("'" + i_clientePreferencial.empresa + "',");
                        sql.append((i_clientePreferencial.id_estadoempresa == 0 ? null : i_clientePreferencial.id_estadoempresa) + ", ");
                        sql.append((i_clientePreferencial.id_municipioempresa == 0 ? null : i_clientePreferencial.id_municipioempresa) + ", ");
                        sql.append("'" + i_clientePreferencial.enderecoempresa + "', '" + i_clientePreferencial.bairroempresa + "', " + i_clientePreferencial.cepempresa + ", '" + i_clientePreferencial.telefoneempresa + "', ");

                        if (i_clientePreferencial.dataadmissao == null) {
                            sql.append(null + ", ");
                        } else {
                            sql.append("'" + i_clientePreferencial.dataadmissao + "', ");
                        }

                        sql.append("'" + i_clientePreferencial.cargo + "', " + i_clientePreferencial.salario + ", " + i_clientePreferencial.outrarenda + ", ");
                        sql.append(i_clientePreferencial.valorlimite + ", '" + i_clientePreferencial.nomeconjuge + "', ");

                        if (i_clientePreferencial.datanascimentoconjuge == null) {
                            sql.append(null + ", ");
                        } else {
                            sql.append("'" + i_clientePreferencial.datanascimentoconjuge + "', ");
                        }

                        sql.append(i_clientePreferencial.cpfconjuge + ", ");
                        sql.append("'" + i_clientePreferencial.rgconjuge + "', '" + i_clientePreferencial.orgaoemissorconjuge + "', '" + i_clientePreferencial.empresaconjuge + "', ");
                        sql.append((i_clientePreferencial.id_estadoconjuge == 0 ? null : i_clientePreferencial.id_estadoconjuge) + ", ");
                        sql.append((i_clientePreferencial.id_municipioconjuge == 0 ? null : i_clientePreferencial.id_municipioconjuge) + ", ");
                        sql.append("'" + i_clientePreferencial.enderecoempresaconjuge + "', '" + i_clientePreferencial.bairroempresaconjuge + "', ");
                        sql.append(i_clientePreferencial.cepempresaconjuge + ", '" + i_clientePreferencial.telefoneempresaconjuge + "', ");

                        if (i_clientePreferencial.dataadmissaoconjuge == null) {
                            sql.append(null + ", ");
                        } else {
                            sql.append("'" + i_clientePreferencial.dataadmissaoconjuge + "', ");
                        }

                        sql.append("'" + i_clientePreferencial.cargoconjuge + "', " + i_clientePreferencial.salarioconjuge + ", " + i_clientePreferencial.outrarendaconjuge + ", ");
                        sql.append(i_clientePreferencial.id_tipoinscricao + ", " + i_clientePreferencial.vencimentocreditorotativo + ", '" + i_clientePreferencial.observacao2 + "', " + i_clientePreferencial.permitecreditorotativo + ", ");
                        sql.append(i_clientePreferencial.permitecheque + ", '" + i_clientePreferencial.nomemae + "', '" + i_clientePreferencial.nomepai + "', ");

                        if (i_clientePreferencial.datarestricao == null) {
                            sql.append(null + ", ");
                        } else {
                            sql.append("'" + i_clientePreferencial.datarestricao + "', ");
                        }

                        sql.append(i_clientePreferencial.bloqueado + ", " + i_clientePreferencial.id_plano + ", " + i_clientePreferencial.bloqueadoautomatico + ", ");
                        sql.append("'" + i_clientePreferencial.numero + "', " + criptografarSenha(i_clientePreferencial.senha) + ", " + i_clientePreferencial.id_tiporestricaocliente + ", ");
                        if (i_clientePreferencial.dataatualizacaocadastro != null) {
                            sql.append("'" + i_clientePreferencial.dataatualizacaocadastro + "',");
                        } else {
                            sql.append(null + ",");
                        }
                        sql.append("'" + i_clientePreferencial.numeroempresa + "', '" + i_clientePreferencial.numeroempresaconjuge + "', ");
                        sql.append("'" + i_clientePreferencial.complemento + "', '" + i_clientePreferencial.complementoempresa + "', '" + i_clientePreferencial.complementoempresaconjuge + "', ");
                        sql.append((i_clientePreferencial.id_contacontabilfiscalpassivo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalpassivo) + ", ");
                        sql.append((i_clientePreferencial.id_contacontabilfiscalativo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalativo) + ", ");
                        sql.append(i_clientePreferencial.enviasms + ", ");
                        sql.append(i_clientePreferencial.enviaemail + "," + (i_clientePreferencial.id_grupo == 0 ? null : i_clientePreferencial.id_grupo) + ", 1);");

                        stm.execute(sql.toString());

                        if (!i_clientePreferencial.fax.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clientepreferencialcontato (");
                            sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                            sql.append("values (");
                            sql.append(i_clientePreferencial.id + ",");
                            sql.append("'FAX',");
                            sql.append("'" + i_clientePreferencial.fax + "',");
                            sql.append("'',");
                            sql.append("0);");
                            Erro = sql.toString();
                            stm.execute(sql.toString());
                        }

                        if (!i_clientePreferencial.telefone2.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clientepreferencialcontato (");
                            sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                            sql.append("values (");
                            sql.append(i_clientePreferencial.id + ",");
                            sql.append("'TELEFONE',");
                            sql.append("'" + i_clientePreferencial.telefone2 + "',");
                            sql.append("'',");
                            sql.append("0);");
                            Erro = sql.toString();
                            stm.execute(sql.toString());
                        }

                        if (!i_clientePreferencial.celular.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("insert into clientepreferencialcontato (");
                            sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                            sql.append("values (");
                            sql.append(i_clientePreferencial.id + ",");
                            sql.append("'CELULAR',");
                            sql.append("'" + i_clientePreferencial.celular + "',");
                            sql.append("'',");
                            sql.append("0);");
                            Erro = sql.toString();
                            stm.execute(sql.toString());
                        }

                        if (salvarCodigoCidadeSistemaAnterior) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                            sql.append("codigoagente, codigoanterior, codigoatual, id_loja, codigocidade_sistemaanterior) ");
                            sql.append("VALUES ( ");
                            sql.append(i_clientePreferencial.codigoAgente + ",");
                            sql.append(i_clientePreferencial.codigoanterior + ", ");
                            sql.append(i_clientePreferencial.id + ", ");
                            sql.append(id_loja + ", "); // loja sempre do VR 
                            sql.append(i_clientePreferencial.getCodigocidade_sistemaanterior() + ");");
                            Erro = sql.toString();
                            stm.execute(sql.toString());
                        } else {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                            sql.append("codigoagente, codigoanterior, codigoatual, id_loja) ");
                            sql.append("VALUES ( ");
                            sql.append(i_clientePreferencial.codigoAgente + ",");
                            sql.append(i_clientePreferencial.codigoanterior + ", ");
                            sql.append(i_clientePreferencial.id + ", ");
                            sql.append(id_loja + ");"); // loja sempre do VR 
                            Erro = sql.toString();
                            stm.execute(sql.toString());
                        }
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
            if (Linha > 0) {
                throw new VRException(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    public void corrigirSenhas(List<ClientePreferencialVO> clientes, boolean sobrescreverSenha, int idLojaCliente) throws Exception {
        try {
            Conexao.begin();
            
            ProgressBar.setStatus("Atualizando senhas....");
            ProgressBar.setMaximum(clientes.size());

            class Temp {
                int codigoatual = 0;
                long codigoanterior = 0;
                int senha = 0;
            }
            
            //<editor-fold defaultstate="collapsed" desc="PREPARANDO A LISTAGEM DO CÓDIGO ANTERIOR">
            Map<Long, Temp> codigoAnterior = new LinkedHashMap<>();
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select \n" +
                        "	ant.codigoatual, \n" +
                        "	ant.codigoanterior, \n" +
                        "	c.senha \n" +
                        "from \n" +
                        "	clientepreferencial c\n" +
                        "	inner join (select distinct * from implantacao.codigoanteriorcli) ant on ant.codigoatual = c.id\n" +
                        "where \n" +
                        "	ant.id_loja = " + idLojaCliente + "\n" +
                        (!sobrescreverSenha ? "	and c.senha = 0\n" : "") +
                        "order by ant.codigoanterior"
                )) {
                    while (rst.next()) {
                        Temp tp = new Temp();
                        tp.codigoatual = rst.getInt("codigoatual");
                        tp.codigoanterior = rst.getLong("codigoanterior");
                        tp.senha = descriptografarSenha(rst.getInt("senha"));
                        codigoAnterior.put(tp.codigoanterior, tp);
                    }
                }
            }
            //</editor-fold>

            try (Statement stm = Conexao.createStatement()) {
                for (ClientePreferencialVO cliente: clientes) {                
                    Temp tp = codigoAnterior.get(getCodigoAnterior(cliente));
                    if (tp != null) {
                        stm.executeUpdate("update clientepreferencial set senha = " + criptografarSenha(cliente.getSenha()) + " where id = " + tp.codigoatual + ";");
                    }

                    ProgressBar.next();
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }
    
    public static enum OpcaoClientePreferencial {
        BLOQUEADO,
        DIA_VENCIMENTO
    }
    
    public void corrigirInformacoes(List<ClientePreferencialVO> v_clientePreferencial, int idLojaVR, OpcaoClientePreferencial... opcoes) throws Exception {
        Set<OpcaoClientePreferencial> opt = new LinkedHashSet<>();
        for (int i = 0; i < opcoes.length; i++) {
            opt.add(opcoes[i]);
        }
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        String Erro = "";

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Corrigindo Cliente Preferencial...");
            int cont = 0;
            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {
                
                int opcoesRestantes = opt.size();
                
                long idAnterior;
                if (!utilizarCodigoAnterior) {
                    if (i_clientePreferencial.getIdLong() > 0) {
                        idAnterior = i_clientePreferencial.getIdLong();
                    } else {
                        idAnterior = i_clientePreferencial.getId();
                    }
                } else {
                    idAnterior = i_clientePreferencial.getCodigoanterior();
                }
                
                sql = new StringBuilder();
                    sql.append("select c.id from clientepreferencial c ")
                    .append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ")
                    .append("where ant.codigoanterior = ")
                    .append(idAnterior)
                    .append(" and ant.id_loja = ")
                    .append(idLojaVR);
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    int idVR = rst.getInt("id");
                    
                    sql = new StringBuilder("update clientepreferencial set ");
                    if (opt.contains(OpcaoClientePreferencial.BLOQUEADO)) {
                        sql.append(" bloqueado = ").append(i_clientePreferencial.bloqueado).append(opcoesRestantes > 1 ? ", " : " ");
                        opcoesRestantes--;
                    }
                    if (opt.contains(OpcaoClientePreferencial.DIA_VENCIMENTO)) {
                        sql.append(" vencimentocreditorotativo = ").append(i_clientePreferencial.vencimentocreditorotativo).append(opcoesRestantes > 1 ? ", " : " ");
                        opcoesRestantes--;
                    }
                    sql.append(" where id = ").append(idVR).append(";");
                    
                    Erro = sql.toString();
                    stm.execute(sql.toString());
                    if (cont < 5) {
                        Util.exibirMensagem("SQL: " + sql.toString(), "Rotina");
                        cont++;
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw new Exception(" SQL: " + Erro + " Cliente: " + ex.getMessage(), ex);
        }
    }

    public void salvarVrSoftware(List<ClientePreferencialVO> v_clientePreferencial, int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null,
                stm3 = null, stm4 = null;
        ResultSet rst = null, rst2 = null;
        java.sql.Date datacadastro;
        int Linha = 0;
        String Erro = "";
        datacadastro = new java.sql.Date(new java.util.Date().getTime());

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Importando Cliente Preferencial...");

            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {

                i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial");

                sql = new StringBuilder();
                sql.append("SELECT id, cnpj from clientepreferencial ");
                sql.append("WHERE cnpj = " + i_clientePreferencial.cnpj);
                rst = stm.executeQuery(sql.toString());

                if (!rst.next()) {
                    sql = new StringBuilder();
                    sql.append("INSERT INTO clientepreferencial( ");
                    sql.append("id, nome, id_situacaocadastro, endereco, bairro, id_estado, id_municipio, ");
                    sql.append("cep, telefone, celular, email, inscricaoestadual, orgaoemissor, ");
                    sql.append("cnpj, id_tipoestadocivil, datanascimento, dataresidencia, datacadastro, ");
                    sql.append("id_tiporesidencia, sexo, id_banco, agencia, conta, praca, observacao, ");
                    sql.append("empresa, id_estadoempresa, id_municipioempresa, enderecoempresa, ");
                    sql.append("bairroempresa, cepempresa, telefoneempresa, dataadmissao, cargo, ");
                    sql.append("salario, outrarenda, valorlimite, nomeconjuge, datanascimentoconjuge, ");
                    sql.append("cpfconjuge, rgconjuge, orgaoemissorconjuge, empresaconjuge, id_estadoconjuge, ");
                    sql.append("id_municipioconjuge, enderecoempresaconjuge, bairroempresaconjuge, ");
                    sql.append("cepempresaconjuge, telefoneempresaconjuge, dataadmissaoconjuge, ");
                    sql.append("cargoconjuge, salarioconjuge, outrarendaconjuge, id_tipoinscricao, ");
                    sql.append("vencimentocreditorotativo, observacao2, permitecreditorotativo, ");
                    sql.append("permitecheque, nomemae, nomepai, datarestricao, bloqueado, id_plano, ");
                    sql.append("bloqueadoautomatico, numero, senha, id_tiporestricaocliente, ");
                    sql.append("dataatualizacaocadastro, numeroempresa, numeroempresaconjuge, ");
                    sql.append("complemento, complementoempresa, complementoempresaconjuge, id_contacontabilfiscalpassivo,  ");
                    sql.append("id_contacontabilfiscalativo, enviasms, enviaemail, id_grupo,  ");
                    sql.append("id_regiaocliente) ");
                    sql.append(" values ( ");

                    sql.append(i_clientePreferencial.id + ", '" + i_clientePreferencial.nome + "', " + i_clientePreferencial.id_situacaocadastro + ", ");
                    sql.append("'" + i_clientePreferencial.endereco + "', '" + i_clientePreferencial.bairro + "', ");
                    sql.append(i_clientePreferencial.id_estado + ", " + i_clientePreferencial.id_municipio + ", ");
                    sql.append(i_clientePreferencial.cep + ", '" + i_clientePreferencial.telefone + "', '" + i_clientePreferencial.celular + "', ");
                    sql.append("'" + i_clientePreferencial.email + "', '" + i_clientePreferencial.inscricaoestadual + "', ");
                    sql.append("'" + i_clientePreferencial.orgaoemissor + "', " + i_clientePreferencial.cnpj + ", " + i_clientePreferencial.id_tipoestadocivil + ", ");

                    if ((i_clientePreferencial.datanascimento != null)
                            && (!i_clientePreferencial.datanascimento.trim().isEmpty())) {
                        sql.append("'" + i_clientePreferencial.datanascimento + "',");
                    } else {
                        sql.append(null + ",");
                    }
                    sql.append("'1990-01-01', " + (i_clientePreferencial.datacadastro.trim().isEmpty() ? datacadastro : Utils.quoteSQL(i_clientePreferencial.datacadastro)) + ", " + i_clientePreferencial.id_tiporesidencia + ", ");
                    sql.append(i_clientePreferencial.sexo + ", " + i_clientePreferencial.id_banco + ", '" + i_clientePreferencial.agencia + "', ");
                    sql.append("'" + i_clientePreferencial.conta + "', '" + i_clientePreferencial.praca + "', '" + i_clientePreferencial.observacao + "', ");
                    sql.append("'" + i_clientePreferencial.empresa + "',");
                    sql.append((i_clientePreferencial.id_estadoempresa == 0 ? null : i_clientePreferencial.id_estadoempresa) + ", ");
                    sql.append((i_clientePreferencial.id_municipioempresa == 0 ? null : i_clientePreferencial.id_municipioempresa) + ", ");
                    sql.append("'" + i_clientePreferencial.enderecoempresa + "', '" + i_clientePreferencial.bairroempresa + "', " + i_clientePreferencial.cepempresa + ", '" + i_clientePreferencial.telefoneempresa + "', ");

                    if (i_clientePreferencial.dataadmissao == null) {
                        sql.append(null + ", ");
                    } else {
                        sql.append("'" + i_clientePreferencial.dataadmissao + "', ");
                    }

                    sql.append("'" + i_clientePreferencial.cargo + "', " + i_clientePreferencial.salario + ", " + i_clientePreferencial.outrarenda + ", ");
                    sql.append(i_clientePreferencial.valorlimite + ", '" + i_clientePreferencial.nomeconjuge + "', ");

                    if (i_clientePreferencial.datanascimentoconjuge == null || i_clientePreferencial.datanascimentoconjuge.trim().isEmpty()) {
                        sql.append(null + ", ");
                    } else {
                        sql.append("'" + i_clientePreferencial.datanascimentoconjuge + "', ");
                    }

                    sql.append(i_clientePreferencial.cpfconjuge + ", ");
                    sql.append("'" + i_clientePreferencial.rgconjuge + "', '" + i_clientePreferencial.orgaoemissorconjuge + "', '" + i_clientePreferencial.empresaconjuge + "', ");
                    sql.append((i_clientePreferencial.id_estadoconjuge == 0 ? null : i_clientePreferencial.id_estadoconjuge) + ", ");
                    sql.append((i_clientePreferencial.id_municipioconjuge == 0 ? null : i_clientePreferencial.id_municipioconjuge) + ", ");
                    sql.append("'" + i_clientePreferencial.enderecoempresaconjuge + "', '" + i_clientePreferencial.bairroempresaconjuge + "', ");
                    sql.append(i_clientePreferencial.cepempresaconjuge + ", '" + i_clientePreferencial.telefoneempresaconjuge + "', ");

                    sql.append(Utils.dateSQL(i_clientePreferencial.getDataadmissaoconjuge()) + ", ");
                    /*if (i_clientePreferencial.dataadmissaoconjuge == null) {
                        sql.append(null + ", ");
                    } else {
                        sql.append("'" + i_clientePreferencial.dataadmissaoconjuge + "', ");
                    }*/

                    sql.append("'" + i_clientePreferencial.cargoconjuge + "', " + i_clientePreferencial.salarioconjuge + ", " + i_clientePreferencial.outrarendaconjuge + ", ");
                    sql.append(i_clientePreferencial.id_tipoinscricao + ", " + i_clientePreferencial.vencimentocreditorotativo + ", '" + i_clientePreferencial.observacao2 + "', " + i_clientePreferencial.permitecreditorotativo + ", ");
                    sql.append(i_clientePreferencial.permitecheque + ", '" + i_clientePreferencial.nomemae + "', '" + i_clientePreferencial.nomepai + "', ");

                    if (i_clientePreferencial.datarestricao == null) {
                        sql.append(null + ", ");
                    } else {
                        sql.append("'" + i_clientePreferencial.datarestricao + "', ");
                    }

                    sql.append(i_clientePreferencial.bloqueado + ", " + (i_clientePreferencial.id_plano < 0 ? null : i_clientePreferencial.id_plano) + ", " + i_clientePreferencial.bloqueadoautomatico + ", ");
                    sql.append("'" + i_clientePreferencial.numero + "', " + criptografarSenha(i_clientePreferencial.senha) + ", " + i_clientePreferencial.id_tiporestricaocliente + ", ");
                    if (i_clientePreferencial.dataatualizacaocadastro != null) {
                        sql.append("'" + i_clientePreferencial.dataatualizacaocadastro + "',");
                    } else {
                        sql.append(null + ",");
                    }
                    sql.append("'" + i_clientePreferencial.numeroempresa + "', '" + i_clientePreferencial.numeroempresaconjuge + "', ");
                    sql.append("'" + i_clientePreferencial.complemento + "', '" + i_clientePreferencial.complementoempresa + "', '" + i_clientePreferencial.complementoempresaconjuge + "', ");
                    sql.append((i_clientePreferencial.id_contacontabilfiscalpassivo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalpassivo) + ", ");
                    sql.append((i_clientePreferencial.id_contacontabilfiscalativo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalativo) + ", ");
                    sql.append(i_clientePreferencial.enviasms + ", ");
                    sql.append(i_clientePreferencial.enviaemail + ",null, 1);");

                    Linha = 559;
                    Erro = sql.toString();
                    stm.execute(sql.toString());

                    if (!i_clientePreferencial.fax.isEmpty()) {
                        sql = new StringBuilder();
                        sql.append("insert into clientepreferencialcontato (");
                        sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                        sql.append("values (");
                        sql.append(i_clientePreferencial.id + ",");
                        sql.append("'FAX',");
                        sql.append("'" + i_clientePreferencial.fax + "',");
                        sql.append("'',");
                        sql.append("0);");
                        Erro = sql.toString();
                        stm.execute(sql.toString());
                    }

                    if (!i_clientePreferencial.telefone2.isEmpty()) {
                        sql = new StringBuilder();
                        sql.append("insert into clientepreferencialcontato (");
                        sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                        sql.append("values (");
                        sql.append(i_clientePreferencial.id + ",");
                        sql.append("'TELEFONE',");
                        sql.append("'" + i_clientePreferencial.telefone2 + "',");
                        sql.append("'',");
                        sql.append("0);");
                        Erro = sql.toString();
                        stm.execute(sql.toString());
                    }

                    //if (id_loja > 1) {
                        sql = new StringBuilder();
                        sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                        sql.append("codigoanterior, codigoatual, id_loja) ");
                        sql.append("VALUES ( ");
                        sql.append(i_clientePreferencial.codigoanterior + ", " + i_clientePreferencial.id + "," + id_lojaCliente + ");");
                        Erro = sql.toString();
                        stm.execute(sql.toString());
                    //}
                } else {
                    if (id_loja > 1) {
                        try (Statement st = Conexao.createStatement()) {
                            try (ResultSet rs = st.executeQuery(
                                    "select codigoatual from implantacao.codigoanteriorcli where\n"
                                            + "codigoanterior = " + i_clientePreferencial.codigoanterior + " and\n"
                                            + "codigoatual = " + rst.getInt("id") + " and\n"
                                            + "id_loja = " + id_lojaCliente
                            )) {
                                //NAO TESTADO
                                if (!rs.next()) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                                    sql.append("codigoanterior, codigoatual, id_loja) ");
                                    sql.append("VALUES ( ");
                                    sql.append(i_clientePreferencial.codigoanterior + ", " + rst.getInt("id") + "," + id_lojaCliente + ");");
                                    Erro = sql.toString();
                                    stm.execute(sql.toString());
                                }
                            }                            
                        }                        
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
            if (Linha > 0) {
                throw new Exception(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    public void corrigirClienteDuplicado() throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null, stm3 = null, stm4 = null;
        ResultSet rst = null, rst2 = null, rst3 = null;
        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();

            ProgressBar.setStatus("Importando Cliente Preferencial...");

            sql = new StringBuilder();

            sql.append("SELECT ID, NOME FROM CLIENTEPREFERENCIAL C ");
            sql.append("WHERE (SELECT COUNT(ID) FROM CLIENTEPREFERENCIAL C2 WHERE C2.NOME = C.NOME) > 1  ");
            sql.append("AND TRIM(UPPER(C.NOME)) NOT IN ('ABRIR CONTA','ACORDO PASTA','CANCELADA','CLIENTE EXCLUIDO SPC', ");
            sql.append("                  'CONTA CANCELADA','CONTA CANCELADA PASTA DE ACORDO','CONTA EM ACORDO', ");
            sql.append("                  'CONTA ENCERRADA','CONTA EXCLUIDA','CTA CANCELADA','EM ACORDO','ESTA EM ACORDO', ");
            sql.append("                  'EX FUNC','EXCLUIDA','EXCLUIDO','EXFUNC','FALECEU','FECHAMENTO DE CONTA','LOJA 01', ");
            sql.append("                  'LOJA 1','LOJA1','NAO TEM MAIS','PASTA DE ACORDO EXCLUIDO','RETIRADO PARA ACORDO', ");
            sql.append("                  'TIREI P/ ACORDO','TIREI PARA ACORDO','TIROU PARA ACORDO','ACORDO EDUARDINHO','ACORDO','') ");
            sql.append("    AND C.ID = C.CNPJ			 ");
            sql.append("    AND EXISTS(SELECT * FROM CLIENTEPREFERENCIAL C3 WHERE C3.NOME = C.NOME AND C3.CNPJ <> C3.ID) ");
            sql.append("ORDER BY C.NOME, C.CNPJ DESC; ");
            rst = stm.executeQuery(sql.toString());
            while (rst.next()) {
                int ClienteVrAntigo = rst.getInt("ID");

                sql = new StringBuilder();
                sql.append("SELECT C.ID FROM CLIENTEPREFERENCIAL C ");
                sql.append("WHERE C.ID<>C.CNPJ AND C.NOME = '" + rst.getString("NOME") + "'");
                rst2 = stm2.executeQuery(sql.toString());

                if (rst2.next()) {
                    int ClienteVr = rst2.getInt("ID");
                    if (ClienteVr > 0) {
                        sql = new StringBuilder();
                        sql.append("UPDATE RECEBERCREDITOROTATIVO SET ID_CLIENTEPREFERENCIAL = " + String.valueOf(ClienteVr));
                        sql.append("WHERE ID_CLIENTEPREFERENCIAL = " + String.valueOf(ClienteVrAntigo));
                        stm2.execute(sql.toString());
                        Conexao.commit();

                        sql = new StringBuilder();
                        sql.append("SELECT ID_CLIENTEPREFERENCIAL FROM RECEBERCREDITOROTATIVO WHERE ID_CLIENTEPREFERENCIAL = " + String.valueOf(ClienteVrAntigo));
                        rst3 = stm3.executeQuery(sql.toString());

                        if (!rst3.next()) {
                            sql = new StringBuilder();
                            sql.append("DELETE FROM CLIENTEPREFERENCIAL WHERE ID = " + String.valueOf(ClienteVrAntigo));
                            stm2.execute(sql.toString());
                            Conexao.commit();
                        }

                    }
                }
                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * ******** CONTECH ***************
     */
    public void alterarLimiteClienteContech(List<ClientePreferencialVO> v_clientePreferencial) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Alterando valor limite Cliente Preferencial...");
            ProgressBar.setMaximum(v_clientePreferencial.size());

            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {

                if (i_clientePreferencial.cnpj != -1) {
                    sql = new StringBuilder();
                    sql.append("select id from clientepreferencial ");
                    sql.append("where cnpj = " + i_clientePreferencial.cnpj + ";");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update clientepreferencial set valorlimite = " + i_clientePreferencial.valorlimite + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");

                        stm.execute(sql.toString());
                    }
                } else {

                    sql = new StringBuilder();
                    sql.append("select id from clientepreferencial ");
                    sql.append("where nome like '%" + i_clientePreferencial.nome + "%' ;");

                    rst = stm.executeQuery(sql.toString());

                    if (rst.next()) {
                        sql = new StringBuilder();
                        sql.append("update clientepreferencial set valorlimite = " + i_clientePreferencial.valorlimite + " ");
                        sql.append("where id = " + rst.getInt("id") + ";");

                        stm.execute(sql.toString());
                    }
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarValorLimte(List<ClientePreferencialVO> v_clientePreferencial) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        //File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Bailo Morada Sol (BoechatSoft)\\script\\update_limite.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Valor Limite Crédito...");
            ProgressBar.setMaximum(v_clientePreferencial.size());

            for (ClientePreferencialVO i_cliente : v_clientePreferencial) {
                
                long idAnterior;
                if (!utilizarCodigoAnterior) {
                    if (i_cliente.getIdLong() > 0) {
                        idAnterior = i_cliente.getIdLong();
                    } else {
                        idAnterior = i_cliente.getId();
                    }
                } else {
                    idAnterior = i_cliente.getCodigoanterior();
                }
                
                sql = new StringBuilder();
                sql.append("select c.id from clientepreferencial c ")
                    .append("inner join implantacao.codigoanteriorcli ant on ant.codigoatual = c.id ")
                    .append("where ant.codigoanterior = ")
                    .append(idAnterior);

                try (Statement stm2 = Conexao.createStatement()) {
                    try (ResultSet rst2 = stm2.executeQuery(sql.toString())) {
                        if (rst2.next()) {
                            sql = new StringBuilder();
                            sql.append("update clientepreferencial set ");
                            sql.append("valorlimite = " + i_cliente.valorlimite + " ");
                            sql.append("where id = " + rst2.getInt("id") + "; ");

                            stm.execute(sql.toString());
                        }
                    }
                }

                
                //bw.write(sql.toString());
                //bw.newLine();

                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarNumeroEndereco(List<ClientePreferencialVO> v_clientePreferencial) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        //File f = new File("C:\\svn\\repositorioImplantacao\\Importacoes_OFFICIAL\\Bailo Morada Sol (BoechatSoft)\\script\\update_numeroend.txt");
        //FileWriter fw = new FileWriter(f);
        //BufferedWriter bw = new BufferedWriter(fw);

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Número Endereço...");
            ProgressBar.setMaximum(v_clientePreferencial.size());

            for (ClientePreferencialVO i_cliente : v_clientePreferencial) {

                sql = new StringBuilder();
                sql.append("update clientepreferencial set ");
                sql.append("numero = '" + i_cliente.numero + "' ");
                sql.append("where id = " + i_cliente.id + "; ");

                stm.execute(sql.toString());
                //bw.write(sql.toString());
                //bw.newLine();

                ProgressBar.next();
            }

            //bw.flush();
            //bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    /**
     * Este método gera um mapa com a listagem dos códigos anteriores dos clientes.
     * @param idLojaCliente id da loja a qual o cliente pertence no antigo sistema.
     * @return Lista com o id anterior e o atual. EX Map<Id anterior, Id atual>.
     * @throws Exception 
     */
    public Map<Long, Long> getCodigoAnterior(int idLojaCliente) throws Exception{     
        try (Statement stm = Conexao.createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select codigoatual, codigoanterior, id_loja "
                            + "from implantacao.codigoanteriorcli "
                            + "where id_loja = " + idLojaCliente
                            + "order by codigoanterior")
                ){

                Map<Long,Long> result = new LinkedHashMap<>();
                while (rst.next()) {
                    result.put(rst.getLong("codigoanterior"), rst.getLong("codigoatual"));
                }
                return result;
            }
        }
    }

    public void atualizaCnpjCpf(List<ClientePreferencialVO> vClientePreferencial, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            Conexao.createStatement().executeUpdate("update clientepreferencial set cnpj = -id");
            
            ProgressBar.setStatus("Importando dados...CNPJ/CPF...");
            ProgressBar.setMaximum(vClientePreferencial.size());       
            
            Map<Long, Long> codigoAnterior = getCodigoAnterior(idLojaCliente);
            
            for (ClientePreferencialVO i_cliente : vClientePreferencial) {
                
                if (codigoAnterior.containsKey(i_cliente.getCodigoanterior())) {
                    
                    sql = new StringBuilder();
                    sql.append("update clientepreferencial set ");
                    sql.append("cnpj = " + (i_cliente.getCnpj() > 0 ? i_cliente.getCnpj() : "id") +" ");
                    sql.append("where id = " + codigoAnterior.get(i_cliente.getCodigoanterior()) + "; ");

                    stm.execute(sql.toString());
                }
                
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void acertarEndereco(List<ClientePreferencialVO> v_clientePreferencial) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        
        try {
            
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando dados...Endereço Cliente...");
            ProgressBar.setMaximum(v_clientePreferencial.size());
            
            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {
            
                sql = new StringBuilder();
                sql.append("select c.id from clientepreferencial c ");
                sql.append("inner join implantacao.codigoanteriorcli ant ");
                sql.append("on ant.codigoatual = c.id ");
                sql.append("where ant.codigoanterior = " + i_clientePreferencial.id);
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                    
                    sql = new StringBuilder();
                    sql.append("update clientepreferencial set ");
                    sql.append("endereco = '"+i_clientePreferencial.endereco+"', ");
                    sql.append("numero = '"+i_clientePreferencial.numero+"', ");
                    sql.append("bairro = '"+i_clientePreferencial.bairro+"', ");
                    sql.append("cep = " + i_clientePreferencial.cep + ", ");
                    sql.append("id_municipio = " + i_clientePreferencial.id_municipio+", ");
                    sql.append("id_estado = " + i_clientePreferencial.id_estado+", ");
                    sql.append("complemento = '"+ i_clientePreferencial.complemento+"' ");
                    sql.append("where id = " + rst.getInt("id")+";");
                    
                    stm.execute(sql.toString());
                }
            
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void verificarClienteIntegracao(List<ClientePreferencialVO> v_clientePreferencial, int id_loja, int id_lojaCliente, 
            String pathLog) throws Exception {
        Statement stm  = null;
        ResultSet rst = null;        
        File f = new File(pathLog+"\\clientes.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Verificando Cliente Preferencial comparando cnpj...");
            
            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {
                if (i_clientePreferencial.cnpj > 99999999) {
                    rst = stm.executeQuery("SELECT cnpj "
                            + "from clientepreferencial "
                            + "WHERE cnpj = " + i_clientePreferencial.cnpj);
                    if (rst.next()) {
                    
                        bw.write("CLIENTE CPF CADASTRADO: " + i_clientePreferencial.cnpj + " "
                                + "NOME: " + i_clientePreferencial.nome+";");
                        bw.newLine();
                    } else {
                        bw.write("CLIENTE CPF NAO CADASTRADO: " + i_clientePreferencial.cnpj + " "
                                + "NOME: " + i_clientePreferencial.nome+";");
                        bw.newLine();
                    }                    
                } else {
                    bw.write("CLIENTE CPF INVALIDO: " + i_clientePreferencial.cnpj + " "
                            + "NOME: " + i_clientePreferencial.nome + ";");
                    bw.newLine();                    
                }
                ProgressBar.next();
            }
            
            bw.flush();
            bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void salvarCpf(List<ClientePreferencialVO> v_clientePreferencial, int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null, stm2 = null,
                stm3 = null, stm4 = null;
        ResultSet rst = null,
                rst2 = null;
        java.sql.Date datacadastro;
        int Linha = 0;
        String Erro = "";
        datacadastro = new java.sql.Date(new java.util.Date().getTime());

        try {
            Conexao.begin();

            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            stm4 = Conexao.createStatement();

            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Importando Cliente Preferencial comparando cnpj...");

            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {

                if (i_clientePreferencial.cnpj > 99999999) {

                    if (!String.valueOf(i_clientePreferencial.cnpj).contains("000000000")
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("111111111"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("222222222"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("333333333"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("444444444"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("555555555"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("666666666"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("777777777"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("888888888"))
                            && (!String.valueOf(i_clientePreferencial.cnpj).contains("999999999"))) {

                        sql = new StringBuilder();
                        sql.append("SELECT cnpj from clientepreferencial ");
                        sql.append("WHERE cnpj = " + i_clientePreferencial.cnpj);
                        rst = stm3.executeQuery(sql.toString());

                        if (!rst.next()) {

                            sql = new StringBuilder();
                            sql.append("select id from clientepreferencial ");
                            sql.append("where id = " + i_clientePreferencial.id);

                            rst2 = stm.executeQuery(sql.toString());

                            if (rst2.next()) {
                                i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                            }

                            if ((i_clientePreferencial.id > 0) && (i_clientePreferencial.id > 999999)) {
                                i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                            } else if ((i_clientePreferencial.idLong > 0) && (i_clientePreferencial.idLong > 999999)) {
                                i_clientePreferencial.id = new CodigoInternoDAO().get("clientepreferencial", 60000);
                            } else {
                                if (i_clientePreferencial.idLong > 0) {
                                    i_clientePreferencial.id = (int) i_clientePreferencial.idLong;
                                }
                            }

                            Linha = i_clientePreferencial.id;

                            sql = new StringBuilder();
                            sql.append("INSERT INTO clientepreferencial( ");
                            sql.append("id, nome, id_situacaocadastro, endereco, bairro, id_estado, id_municipio, ");
                            sql.append("cep, telefone, celular, email, inscricaoestadual, orgaoemissor, ");
                            sql.append("cnpj, id_tipoestadocivil, datanascimento, dataresidencia, datacadastro, ");
                            sql.append("id_tiporesidencia, sexo, id_banco, agencia, conta, praca, observacao, ");
                            sql.append("empresa, id_estadoempresa, id_municipioempresa, enderecoempresa, ");
                            sql.append("bairroempresa, cepempresa, telefoneempresa, dataadmissao, cargo, ");
                            sql.append("salario, outrarenda, valorlimite, nomeconjuge, datanascimentoconjuge, ");
                            sql.append("cpfconjuge, rgconjuge, orgaoemissorconjuge, empresaconjuge, id_estadoconjuge, ");
                            sql.append("id_municipioconjuge, enderecoempresaconjuge, bairroempresaconjuge, ");
                            sql.append("cepempresaconjuge, telefoneempresaconjuge, dataadmissaoconjuge, ");
                            sql.append("cargoconjuge, salarioconjuge, outrarendaconjuge, id_tipoinscricao, ");
                            sql.append("vencimentocreditorotativo, observacao2, permitecreditorotativo, ");
                            sql.append("permitecheque, nomemae, nomepai, datarestricao, bloqueado, id_plano, ");
                            sql.append("bloqueadoautomatico, numero, senha, id_tiporestricaocliente, ");
                            sql.append("dataatualizacaocadastro, numeroempresa, numeroempresaconjuge, ");
                            sql.append("complemento, complementoempresa, complementoempresaconjuge, id_contacontabilfiscalpassivo,  ");
                            sql.append("id_contacontabilfiscalativo, enviasms, enviaemail, id_grupo,  ");
                            sql.append("id_regiaocliente) ");
                            sql.append(" values ( ");
                            sql.append(i_clientePreferencial.id + ", '" + i_clientePreferencial.nome + "', " + i_clientePreferencial.id_situacaocadastro + ", ");
                            sql.append("'" + i_clientePreferencial.endereco + "', '" + i_clientePreferencial.bairro + "', ");
                            sql.append(i_clientePreferencial.id_estado + ", " + i_clientePreferencial.id_municipio + ", ");
                            sql.append(i_clientePreferencial.cep + ", '" + i_clientePreferencial.telefone + "', '" + i_clientePreferencial.celular + "', ");
                            sql.append("'" + i_clientePreferencial.email + "', '" + i_clientePreferencial.inscricaoestadual + "', ");
                            sql.append("'" + i_clientePreferencial.orgaoemissor + "', ");

                            if ((i_clientePreferencial.cnpj == -1) || (i_clientePreferencial.cnpj == 0)) {

                                sql.append(i_clientePreferencial.id + ", ");
                            } else {
                                sql.append(i_clientePreferencial.cnpj + ", ");
                            }

                            sql.append(i_clientePreferencial.id_tipoestadocivil + ", ");

                            if ((i_clientePreferencial.datanascimento != null)
                                    && (!i_clientePreferencial.datanascimento.isEmpty())) {
                                sql.append("'" + i_clientePreferencial.datanascimento + "',");
                            } else {
                                sql.append(null + ",");
                            }
                            sql.append("'1990-01-01', '" + (i_clientePreferencial.datacadastro == "" ? datacadastro : i_clientePreferencial.datacadastro) + "', " + i_clientePreferencial.id_tiporesidencia + ", ");
                            sql.append(i_clientePreferencial.sexo + ", " + i_clientePreferencial.id_banco + ", '" + i_clientePreferencial.agencia + "', ");
                            sql.append("'" + i_clientePreferencial.conta + "', '" + i_clientePreferencial.praca + "', '" + i_clientePreferencial.observacao + "', ");
                            sql.append("'" + i_clientePreferencial.empresa + "',");
                            sql.append((i_clientePreferencial.id_estadoempresa == 0 ? null : i_clientePreferencial.id_estadoempresa) + ", ");
                            sql.append((i_clientePreferencial.id_municipioempresa == 0 ? null : i_clientePreferencial.id_municipioempresa) + ", ");
                            sql.append("'" + i_clientePreferencial.enderecoempresa + "', '" + i_clientePreferencial.bairroempresa + "', " + i_clientePreferencial.cepempresa + ", '" + i_clientePreferencial.telefoneempresa + "', ");
                            sql.append("NULL, '" + i_clientePreferencial.cargo + "', " + i_clientePreferencial.salario + ", " + i_clientePreferencial.outrarenda + ", ");
                            sql.append(i_clientePreferencial.valorlimite + ", '" + i_clientePreferencial.nomeconjuge + "', NULL, " + i_clientePreferencial.cpfconjuge + ", ");
                            sql.append("'" + i_clientePreferencial.rgconjuge + "', '" + i_clientePreferencial.orgaoemissorconjuge + "', '" + i_clientePreferencial.empresaconjuge + "', ");
                            sql.append("NULL, NULL, '" + i_clientePreferencial.enderecoempresaconjuge + "', '" + i_clientePreferencial.bairroempresaconjuge + "', ");
                            sql.append(i_clientePreferencial.cepempresaconjuge + ", '" + i_clientePreferencial.telefoneempresaconjuge + "', NULL, ");
                            sql.append("'" + i_clientePreferencial.cargoconjuge + "', " + i_clientePreferencial.salarioconjuge + ", " + i_clientePreferencial.outrarendaconjuge + ", ");
                            sql.append(i_clientePreferencial.id_tipoinscricao + ", " + i_clientePreferencial.vencimentocreditorotativo + ", '" + i_clientePreferencial.observacao2 + "', " + i_clientePreferencial.permitecreditorotativo + ", ");
                            sql.append(i_clientePreferencial.permitecheque + ", '" + i_clientePreferencial.nomemae + "', '" + i_clientePreferencial.nomepai + "', NULL, ");
                            sql.append(i_clientePreferencial.bloqueado + ", " + i_clientePreferencial.id_plano + ", " + i_clientePreferencial.bloqueadoautomatico + ", ");
                            sql.append("'" + i_clientePreferencial.numero + "', " + criptografarSenha(i_clientePreferencial.senha) + ", " + i_clientePreferencial.id_tiporestricaocliente + ", ");

                            if ((i_clientePreferencial.datanascimentoconjuge != null)
                                    && (!i_clientePreferencial.datanascimentoconjuge.isEmpty())) {
                                sql.append("'" + i_clientePreferencial.datanascimentoconjuge + "',");
                            } else {
                                sql.append(null + ",");
                            }

                            sql.append("'" + i_clientePreferencial.numeroempresa + "', '" + i_clientePreferencial.numeroempresaconjuge + "', ");
                            sql.append("'" + i_clientePreferencial.complemento + "', '" + i_clientePreferencial.complementoempresa + "', '" + i_clientePreferencial.complementoempresaconjuge + "', ");
                            sql.append((i_clientePreferencial.id_contacontabilfiscalpassivo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalpassivo) + ", ");
                            sql.append((i_clientePreferencial.id_contacontabilfiscalativo == 0 ? null : i_clientePreferencial.id_contacontabilfiscalativo) + ", ");
                            sql.append(i_clientePreferencial.enviasms + ", ");
                            sql.append(i_clientePreferencial.enviaemail + ", NULL, 1);");

                            Erro = sql.toString();
                            try {
                                stm.execute(sql.toString());

                            } catch (Exception ex) {
                                throw new VRException(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
                            }

                            if (!i_clientePreferencial.fax.isEmpty()) {
                                sql = new StringBuilder();
                                sql.append("insert into clientepreferencialcontato (");
                                sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                                sql.append("values (");
                                sql.append(i_clientePreferencial.id + ",");
                                sql.append("'FAX',");
                                sql.append("'" + i_clientePreferencial.fax + "',");
                                sql.append("'',");
                                sql.append("0);");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }

                            if (!i_clientePreferencial.telefone2.isEmpty()) {
                                sql = new StringBuilder();
                                sql.append("insert into clientepreferencialcontato (");
                                sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                                sql.append("values (");
                                sql.append(i_clientePreferencial.id + ",");
                                sql.append("'TELEFONE',");
                                sql.append("'" + i_clientePreferencial.telefone2 + "',");
                                sql.append("'',");
                                sql.append("0);");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }

                            if (!i_clientePreferencial.celular.isEmpty()) {
                                sql = new StringBuilder();
                                sql.append("insert into clientepreferencialcontato (");
                                sql.append("id_clientepreferencial, nome, telefone, celular, id_tipocontato) ");
                                sql.append("values (");
                                sql.append(i_clientePreferencial.id + ",");
                                sql.append("'CELULAR',");
                                sql.append("'" + i_clientePreferencial.celular + "',");
                                sql.append("'',");
                                sql.append("0);");
                                Erro = sql.toString();
                                stm.execute(sql.toString());
                            }

                            sql = new StringBuilder();
                            sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                            sql.append("codigoagente, codigoanterior, codigoatual, id_loja) ");
                            sql.append("VALUES ( ");
                            sql.append(i_clientePreferencial.codigoAgente + ",");
                            sql.append(i_clientePreferencial.codigoanterior + ", ");
                            sql.append(i_clientePreferencial.id + ", ");
                            sql.append(id_loja + ");");
                            Erro = sql.toString();
                            stm.execute(sql.toString());

                        }
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
            if (Linha > 0) {
                throw new VRException(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }
    
    public void salvarNome(List<ClientePreferencialVO> v_clientePreferencial, int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int Linha = 0;
        String Erro = "";

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_clientePreferencial.size());
            ProgressBar.setStatus("Importando Cliente Preferencial...Comparar Nome...");

            for (ClientePreferencialVO i_clientePreferencial : v_clientePreferencial) {

                sql = new StringBuilder();
                sql.append("SELECT id, cnpj from clientepreferencial ");
                sql.append("WHERE nome like '%" + i_clientePreferencial.nome + "%'");
                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("INSERT INTO implantacao.codigoanteriorcli( ");
                    sql.append("codigoagente, codigoanterior, codigoatual, id_loja, novo) ");
                    sql.append("VALUES ( ");
                    sql.append(i_clientePreferencial.codigoAgente + ",");
                    sql.append(i_clientePreferencial.codigoanterior + ", ");
                    sql.append(rst.getInt("id") + ", ");
                    sql.append(id_loja + ", ");
                    sql.append("true);");
                    Erro = sql.toString();
                    stm.execute(sql.toString());

                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            if (Linha > 0) {
                throw new VRException(" SQL: " + Erro + " Cliente: " + Linha + " " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    public int getIdCodigoAnterior(int i_codigo, int idLoja) throws Exception {
        int retorno = -1;
        ResultSet rst = null;
        Statement stm = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select c.id from clientepreferencial c "
                + "inner join implantacao.codigoanteriorcli ant "
                + "on ant.codigoatual = c.id "
                + "where ant.codigoanterior = " + i_codigo + ""
                + "and ant.id_loja = " + idLoja);
        
        if (rst.next()) {
            retorno = rst.getInt("id");
        } else {
            retorno = -1;
        }
        
        return retorno;
    }
    
    public int getIdByCodigoAnterior(int i_codigo, int idLoja) throws Exception {
        int retorno = -1;
        ResultSet rst = null;
        Statement stm = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select c.id from clientepreferencial c "
                + "inner join implantacao.codigoanteriorcli ant "
                + "on ant.codigoatual = c.id "
                + "where ant.codigoanterior = " + i_codigo + ""
                + "and ant.id_loja = " + idLoja);
        
        if (rst.next()) {
            retorno = rst.getInt("id");
        } else {
            retorno = -1;
        }
        
        return retorno;
    }
    
    public int getId(int i_codigo) throws Exception {
        int retorno = -1;
        ResultSet rst = null;
        Statement stm = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select id from clientepreferencial "
                + "where id = " + i_codigo);
        
        if (rst.next()) {
            retorno = rst.getInt("id");
        } else {
            retorno = -1;
        }
        
        return retorno;
    }
    
    public int getIdByCnpj(int i_cnpj) throws Exception {
        int retorno = -1;
        ResultSet rst = null;
        Statement stm = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select id from clientepreferencial "
                + "where cnpj = " + i_cnpj);
        
        if (rst.next()) {
            retorno = rst.getInt("id");
        } else {
            retorno = -1;
        }
        
        return retorno;
    }
    
    public long getCnpj(int i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select cnpj from clientepreferencial where id = " + i_codigo);
        if (rst.next()) {
            return rst.getLong("cnpj");
        } else {
            return -1;
        }
    }
    
    public String getNome(int i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select nome from clientepreferencial where id = " + i_codigo);
        if (rst.next()) {
            return rst.getString("nome");
        } else {
            return "";
        }
    }
    
    public String getTelefone(int i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        
        stm = Conexao.createStatement();
        rst = stm.executeQuery("select telefone from clientepreferencial where id = " + i_codigo);
        if (rst.next()) {
            return rst.getString("telefone");
        } else {
            return "";
        }
    }
    
    public void acertarValorLimite(List<ClientePreferencialVO> v_result, int idLoja) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_result.size());
            ProgressBar.setStatus("Acertando Valor Limite Cliente...");

            for (ClientePreferencialVO i_result : v_result) {
                sql = new StringBuilder();
                sql.append("update clientepreferencial set "
                        + "valorlimite = " + i_result.getValorlimite() + " "
                        + "where id = " + i_result.getId());
                stm.execute(sql.toString());

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}