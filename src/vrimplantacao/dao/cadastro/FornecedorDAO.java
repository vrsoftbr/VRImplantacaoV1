package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.cadastro.TipoFornecedorVO;
import vrimplantacao.vo.loja.FornecedorContatoVO;
import vrimplantacao.vo.loja.FornecedorPrazoVO;
import vrimplantacao.vo.loja.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorFornecedorVO;

public class FornecedorDAO {

    String Erro;
    public int pidLoja = 1;
    private Map<Long, Integer> cnpjExistente;
    public boolean salvarCodigoCidadeSistemaAnterior = false;
    
    public void recarregarCnpj() throws Exception {
        cnpjExistente = carregarCnpj();
    }
    
    public Integer getIdByCnpj(Long cnpj) throws Exception {
        if (cnpjExistente == null) {
            recarregarCnpj();
        }
        return cnpjExistente.get(cnpj);
    }

    public Map<Long, Integer> carregarCnpj() throws Exception {
        Map<Long, Integer> vFornecedorDestino = new HashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT cnpj, id FROM fornecedor"
            )) {
                while (rst.next()) {
                    vFornecedorDestino.put(rst.getLong("cnpj"), rst.getInt("id"));
                }                
            }
        }
        return vFornecedorDestino;
    }
    
    private Set<Integer> carregarIdsExistentes() throws Exception {
        Set<Integer> result = new LinkedHashSet<>();

        Conexao.begin();
        try {
            Statement stm = Conexao.createStatement();
            try (ResultSet rst = stm.executeQuery(
                    "select id from fornecedor order by id")) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            } finally {
                stm.close();
            }
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }

        return result;
    }

    public FornecedorVO carregar(int i_id) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT f.*, municipio.descricao AS municipio, estado.sigla AS estado");
        sql.append(" FROM fornecedor AS f");
        sql.append(" INNER JOIN municipio ON municipio.id = f.id_municipio");
        sql.append(" INNER JOIN estado ON estado.id = municipio.id_estado");
        sql.append(" WHERE f.id = " + i_id);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Fornecedor não encontrado!");
        }

        FornecedorVO oFornecedor = new FornecedorVO();
        oFornecedor.agencia = rst.getString("agencia");
        oFornecedor.bairro = rst.getString("bairro");
        oFornecedor.bairroCobranca = rst.getString("bairrocobranca");
        oFornecedor.idBanco = rst.getInt("id_banco");
        oFornecedor.bloqueado = rst.getBoolean("bloqueado");
        oFornecedor.cep = rst.getInt("cep");
        oFornecedor.cepCobranca = rst.getInt("cepcobranca");
        oFornecedor.cnpj = rst.getLong("cnpj");
        // oFornecedor.condicaoPagamento = rst.getString("condicaopagamento");
        oFornecedor.conta = rst.getString("conta");
        oFornecedor.dataSintegra = rst.getString("datasintegra") == null ? "" : Util.formatDataGUI(rst.getDate("datasintegra"));
        oFornecedor.descontoFunRural = rst.getBoolean("descontofunrural");
        oFornecedor.digitoAgencia = rst.getString("digitoagencia");
        oFornecedor.digitoConta = rst.getString("digitoconta");
        oFornecedor.endereco = rst.getString("endereco");
        oFornecedor.numero = rst.getString("numero");
        oFornecedor.enderecoCobranca = rst.getString("enderecocobranca");
        oFornecedor.id = rst.getInt("id");
        oFornecedor.idBanco = rst.getString("id_banco") == null ? -1 : rst.getInt("id_banco");
        oFornecedor.idEstado = rst.getInt("id_estado");
        oFornecedor.estado = rst.getString("estado");
        oFornecedor.idEstadoCobranca = rst.getString("id_estadocobranca") == null ? -1 : rst.getInt("id_estadocobranca");
        oFornecedor.idFamiliaFornecedor = rst.getString("id_familiafornecedor") == null ? -1 : rst.getInt("id_familiafornecedor");
        oFornecedor.idFornecedorFavorecido = rst.getString("id_fornecedorfavorecido") == null ? -1 : rst.getInt("id_fornecedorfavorecido");
        oFornecedor.idMunicipio = rst.getInt("id_municipio");
        oFornecedor.municipio = rst.getString("municipio");
        oFornecedor.idMunicipioCobranca = rst.getString("id_municipiocobranca") == null ? -1 : rst.getInt("id_municipiocobranca");
        oFornecedor.idSituacaoCadastro = rst.getInt("id_situacaocadastro");
        oFornecedor.idTipoEmpresa = rst.getInt("id_tipoempresa");
        oFornecedor.idTipoFornecedor = rst.getInt("id_tipofornecedor");
        oFornecedor.idTipoInscricao = rst.getInt("id_tipoinscricao");
        oFornecedor.idTipoMotivoFornecedor = rst.getString("id_tipomotivofornecedor") == null ? -1 : rst.getInt("id_tipomotivofornecedor");
        oFornecedor.idTipoPagamento = rst.getInt("id_tipopagamento");
        oFornecedor.idTipoRecebimento = rst.getString("id_tiporecebimento") == null ? -1 : rst.getInt("id_tiporecebimento");
        oFornecedor.inscricaoEstadual = rst.getString("inscricaoestadual");
        oFornecedor.inscricaoSuframa = rst.getString("inscricaosuframa");
        oFornecedor.nomeFantasia = rst.getString("nomefantasia");
        oFornecedor.numeroDoc = rst.getInt("numerodoc");
        oFornecedor.pedidoMinimoQtd = rst.getInt("pedidominimoqtd");
        oFornecedor.pedidoMinimoValor = rst.getDouble("pedidominimovalor");
        oFornecedor.razaoSocial = rst.getString("razaosocial");
        oFornecedor.revenda = rst.getBoolean("revenda");
        oFornecedor.serieNf = rst.getString("serienf");
        oFornecedor.telefone = rst.getString("telefone");
        oFornecedor.utilizaIva = rst.getBoolean("utilizaiva");
        oFornecedor.senha = rst.getInt("senha");
        oFornecedor.idTipoInspecao = rst.getObject("id_tipoinspecao") == null ? -1 : rst.getInt("id_tipoinspecao");
        oFornecedor.numeroInspecao = rst.getInt("numeroinspecao");
        oFornecedor.idTipoTroca = rst.getObject("id_tipotroca") == null ? -1 : rst.getInt("id_tipotroca");
        oFornecedor.idContaContabilFinanceiro = rst.getObject("id_contacontabilfinanceiro") == null ? -1 : rst.getInt("id_contacontabilfinanceiro");
        oFornecedor.utilizaNfe = rst.getBoolean("utilizanfe");
        oFornecedor.utilizaConferencia = rst.getBoolean("utilizaconferencia");

        sql = new StringBuilder();
        sql.append("SELECT contato.id_tipocontato, contato.nome, contato.telefone, contato.celular, tipocontato.descricao AS tipocontato,");
        sql.append(" contato.email");
        sql.append(" FROM fornecedorcontato AS contato");
        sql.append(" INNER JOIN tipocontato ON tipocontato.id = contato.id_tipocontato");
        sql.append(" WHERE contato.id_fornecedor = " + oFornecedor.id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            FornecedorContatoVO oContato = new FornecedorContatoVO();
            oContato.idTipoContato = rst.getInt("id_tipocontato");
            oContato.tipoContato = rst.getString("tipocontato");
            oContato.nome = rst.getString("nome");
            oContato.telefone = rst.getString("telefone");
            oContato.celular = rst.getString("celular");
            oContato.email = rst.getString("email");

            oFornecedor.vContato.add(oContato);
        }

        sql = new StringBuilder();
        sql.append("SELECT fp.id_loja, lj.descricao AS loja, fp.id_divisaofornecedor, df.descricao AS divisaofornecedor,");
        sql.append(" fp.prazoentrega, fp.prazovisita, fp.prazoseguranca");
        sql.append(" FROM fornecedorprazo AS fp");
        sql.append(" INNER JOIN loja AS lj ON lj.id = fp.id_loja");
        sql.append(" INNER JOIN divisaofornecedor AS df ON df.id = fp.id_divisaofornecedor");
        sql.append(" WHERE fp.id_fornecedor = " + i_id);

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {
            FornecedorPrazoVO oPrazo = new FornecedorPrazoVO();
            oPrazo.idLoja = rst.getInt("id_loja");
            oPrazo.idDivisaoFornecedor = rst.getInt("id_divisaofornecedor");
            oPrazo.loja = rst.getString("loja");
            oPrazo.divisaoFornecedor = rst.getString("divisaofornecedor");
            oPrazo.prazoEntrega = rst.getInt("prazoentrega");
            oPrazo.prazoVisita = rst.getInt("prazovisita");
            oPrazo.prazoSeguranca = rst.getInt("prazoseguranca");

            oFornecedor.vPrazo.add(oPrazo);
        }

        stm.close();

        return oFornecedor;
    }

    public void acertarCidadeFornecedor(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_fornecedor.size());
            ProgressBar.setStatus("Acertando cidades...");

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {
                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedor.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {
                    sql = new StringBuilder();
                    sql.append("update fornecedor set ");
                    sql.append("id_municipio = " + i_fornecedor.id_municipio + ", ");
                    sql.append("id_estado = " + i_fornecedor.id_estado + " ");
                    sql.append("where id = " + rst.getInt("id"));

                    stm.execute(sql.toString());
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
    
    public void salvar(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor, int idLojaCliente) throws Exception {
        Global.idLojaFornecedor = idLojaCliente;
        salvar(v_fornecedor);
    }

    public void salvar(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        int id = 0, Linha = 0;

        java.sql.Date datacadastro;

        try {

            Conexao.begin();

            stm = Conexao.createStatement();

            stm2 = Conexao.getConexao().createStatement();

            ProgressBar.setMaximum(v_fornecedor.size());
            ProgressBar.setStatus("Importando Fornecedor...");

            //Carrega uma listagem com os códigos anteriores
            CodigoAnteriorFornecedorDAO dao = new CodigoAnteriorFornecedorDAO();

            //Obtem todos os ids existentes
            Set<Integer> idsExistentes = carregarIdsExistentes();

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                //Se o código anterior estiver na listagem, não executa.
                if (!dao.existe(i_fornecedor.getCodigoanterior(), Global.idLojaFornecedor)) {
                    
                    Integer idCnpj = getIdByCnpj(i_fornecedor.getCnpj());
                    
                    if (idCnpj == null) {
                        //Incrementa o id
                        id++;
                        //Se o id já existir na lista, incrementa mais um
                        while (idsExistentes.contains(id)) {
                            id++;
                        }
                        //Adiciona o id na listagem
                        idsExistentes.add(id);

                        sql = new StringBuilder();
                        sql.append("INSERT INTO fornecedor( ");
                        sql.append("id, razaosocial, nomefantasia, endereco, bairro, id_municipio, ");
                        sql.append("cep, id_estado, telefone, id_tipoinscricao, inscricaoestadual, ");
                        sql.append("cnpj, revenda, id_situacaocadastro, id_tipopagamento, numerodoc, ");
                        sql.append("pedidominimoqtd, pedidominimovalor, serienf, descontofunrural, ");
                        sql.append("senha, id_tiporecebimento, agencia, digitoagencia, conta, digitoconta, ");
                        sql.append("id_banco, id_fornecedorfavorecido, enderecocobranca, bairrocobranca, ");
                        sql.append("cepcobranca, id_municipiocobranca, id_estadocobranca, bloqueado, ");
                        sql.append("id_tipomotivofornecedor, datasintegra, id_tipoempresa, inscricaosuframa, ");
                        sql.append("utilizaiva, id_familiafornecedor, id_tipoinspecao, numeroinspecao, ");
                        sql.append("id_tipotroca, id_tipofornecedor, id_contacontabilfinanceiro, ");
                        sql.append("utilizanfe, datacadastro, utilizaconferencia, numero, permitenfsempedido, ");
                        sql.append("modelonf, emitenf, tiponegociacao, utilizacrossdocking, id_lojacrossdocking, ");
                        sql.append("observacao, id_pais, inscricaomunicipal, id_contacontabilfiscalpassivo, ");
                        sql.append("numerocobranca, complemento, complementocobranca, id_contacontabilfiscalativo, ");
                        sql.append("utilizaedi, tiporegravencimento, id_tipoindicadorie) ");
                        sql.append("VALUES ( ");
                        sql.append(id + ", '" + i_fornecedor.razaosocial + "', '" + i_fornecedor.nomefantasia + "', ");
                        sql.append("'" + i_fornecedor.endereco + "', '" + i_fornecedor.bairro + "', " + i_fornecedor.id_municipio + ", ");
                        sql.append(i_fornecedor.cep + ", " + i_fornecedor.id_estado + ", '" + i_fornecedor.telefone + "', ");
                        sql.append(i_fornecedor.id_tipoinscricao + ", '" + i_fornecedor.inscricaoestadual + "', ");
                        long cnpj = i_fornecedor.cnpj <= 0 ? id : i_fornecedor.cnpj;
                        sql.append(cnpj + ", " + i_fornecedor.revenda + ", " + (cnpj == id ? 0 : i_fornecedor.id_situacaocadastro)  + ", ");
                        sql.append(i_fornecedor.id_tipopagamento + ", " + i_fornecedor.numerodoc + ", " + i_fornecedor.pedidominimoqtd + ", ");
                        sql.append(i_fornecedor.pedidominimovalor + ", '" + i_fornecedor.serienf + "', " + i_fornecedor.descontofunrural + ", ");
                        sql.append(i_fornecedor.senha + ", NULL, '" + i_fornecedor.agencia + "', ");
                        sql.append("'" + i_fornecedor.digitoagencia + "', '" + i_fornecedor.conta + "', '" + i_fornecedor.digitoconta + "', ");
                        sql.append((i_fornecedor.id_banco == -1 ? null : i_fornecedor.id_banco) + ", NULL, '" + i_fornecedor.enderecocobranca + "', ");
                        sql.append("'" + i_fornecedor.bairrocobranca + "', " + i_fornecedor.cepcobranca + ", NULL, ");
                        sql.append("NULL, " + i_fornecedor.bloqueado + ", NULL, ");
                        sql.append("NULL, " + i_fornecedor.id_tipoempresa + ", '" + i_fornecedor.inscricaosuframa + "', ");
                        sql.append(i_fornecedor.utilizaiva + ", NULL , NULL, ");
                        sql.append(i_fornecedor.numeroinspecao + ", NULL, " + i_fornecedor.id_tipofornecedor + ", ");
                        sql.append(/*i_fornecedor.getId_contacontabilfinanceiro().getID() + */" 3, " + i_fornecedor.utilizanfe + ", ");

                        /*if (i_fornecedor.datacadastro != null) {
                            sql.append("'" + i_fornecedor.datacadastro + "',");
                        } else {
                            datacadastro = new java.sql.Date(new java.util.Date().getTime());
                            sql.append("'" + datacadastro + "',");
                        }*/
                        
                        if ((i_fornecedor.datacadastro != null)) {
                            sql.append("'" + i_fornecedor.datacadastro + "',");
                        } else {
                            if ((i_fornecedor.datacadastroStr != null) && (!i_fornecedor.datacadastroStr.trim().isEmpty())) {
                                sql.append("'" + i_fornecedor.datacadastroStr + "',");
                            } else {
                                datacadastro = new java.sql.Date(new java.util.Date().getTime());
                                sql.append("'" + datacadastro + "',");
                            }
                        }
                        
                        sql.append(i_fornecedor.utilizaconferencia + ", '" + i_fornecedor.numero + "', " + i_fornecedor.permitenfsempedido + ", ");
                        sql.append("'" + i_fornecedor.modelonf + "', " + i_fornecedor.emitenf + ", " + i_fornecedor.tiponegociacao + ", " + i_fornecedor.utilizacrossdocking + ", ");
                        sql.append("NULL, '" + i_fornecedor.observacao + "', " + i_fornecedor.id_pais + ", '" + i_fornecedor.inscricaomunicipal + "', ");
                        sql.append("NULL, '" + i_fornecedor.numerocobranca + "', '" + i_fornecedor.complemento + "', ");
                        sql.append("'" + i_fornecedor.complementocobranca + "', NULL, " + i_fornecedor.utilizaedi + ", ");
                        sql.append(i_fornecedor.tiporegravencimento + ", ");
                        sql.append(i_fornecedor.getId_tipoindicadorie().getId())
                                .append(");");

                        Erro = sql.toString();
                        try {
                            stm.execute(sql.toString());
                        } catch (Exception ex) {
                            Conexao.rollback();
                            throw new VRException("Cliente: " + Linha + " SQL: " + Erro + " " + ex.getMessage());
                        }
                        if (!i_fornecedor.telefone2.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'TELEFONE', '" + i_fornecedor.telefone2 + "', 0, '', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.telefone3.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", '" + ("".equals(i_fornecedor.representante) ? "TELEFONE" : i_fornecedor.representante ) + "', "+i_fornecedor.telefone3+", 0, '', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.telefone4.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", '" + ("".equals(i_fornecedor.representante) ? "TELEFONE" : i_fornecedor.representante ) + "', "+i_fornecedor.telefone4+", 0, '', '');");
                            stm.execute(sql.toString());
                        }
                        
                        if (!i_fornecedor.celular.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'CELULAR', '', 0, '', '" + i_fornecedor.celular + "');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.fax.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'FAX', '" + i_fornecedor.fax + "', 0, '', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.email.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'EMAIL', '', 0, '" + Utils.acertarTexto(i_fornecedor.email, 50).toLowerCase() + "', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.email2.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ",'" + ("".equals(i_fornecedor.representante) ? "EMAIL" : i_fornecedor.representante) + "', '', 0, '" + Utils.acertarTexto(i_fornecedor.email2, 50).toLowerCase() + "', '');");
                            stm.execute(sql.toString());
                        }
                        
                        /*Grava o código anterior*/
                        CodigoAnteriorFornecedorVO anterior = new CodigoAnteriorFornecedorVO();
                        anterior.setCodigoAnterior(i_fornecedor.getCodigoanterior());
                        anterior.setCodigoAtual(id);
                        anterior.setIdLoja(Global.idLojaFornecedor);
                        dao.salvar(anterior);
                        dao.getCodigosAnteriores().put(anterior.getChaveUnica(), anterior);
                     
                        cnpjExistente.put(cnpj, id);
                    } else {
                        /*Grava o código anterior*/
                        CodigoAnteriorFornecedorVO anterior = new CodigoAnteriorFornecedorVO();
                        anterior.setCodigoAnterior(i_fornecedor.getCodigoanterior());
                        anterior.setCodigoAtual(idCnpj);
                        anterior.setIdLoja(Global.idLojaFornecedor);                        
                        dao.salvar(anterior);
                        dao.getCodigosAnteriores().put(anterior.getChaveUnica(), anterior);
                    }  
                }

                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            if (Linha > 0) {
                throw new VRException("Cliente: " + Linha + " SQL: " + Erro + " " + ex.getMessage());
            } else {
                Conexao.rollback();
                throw ex;
            }
        }
    }

    public void salvarCnpj(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        salvarCnpj(v_fornecedor, pidLoja);
    }
    
    public void salvarCnpj(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor, int idLojaFornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int id = 0, Linha = 0;

        java.sql.Date datacadastro;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setMaximum(v_fornecedor.size());
            ProgressBar.setStatus("Importando Fornecedor...Comparando Cnpj...");

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                if (i_fornecedor.cnpj > 99999999) {

                    sql = new StringBuilder();
                    sql.append("select id from fornecedor ");
                    sql.append("where cnpj = " + i_fornecedor.cnpj);

                    rst = stm.executeQuery(sql.toString());

                    if (!rst.next()) {

                        id = new CodigoInternoDAO().get("fornecedor");
                        sql = new StringBuilder();
                        sql.append("INSERT INTO fornecedor( ");
                        sql.append("id, razaosocial, nomefantasia, endereco, bairro, id_municipio, ");
                        sql.append("cep, id_estado, telefone, id_tipoinscricao, inscricaoestadual, ");
                        sql.append("cnpj, revenda, id_situacaocadastro, id_tipopagamento, numerodoc, ");
                        sql.append("pedidominimoqtd, pedidominimovalor, serienf, descontofunrural, ");
                        sql.append("senha, id_tiporecebimento, agencia, digitoagencia, conta, digitoconta, ");
                        sql.append("id_banco, id_fornecedorfavorecido, enderecocobranca, bairrocobranca, ");
                        sql.append("cepcobranca, id_municipiocobranca, id_estadocobranca, bloqueado, ");
                        sql.append("id_tipomotivofornecedor, datasintegra, id_tipoempresa, inscricaosuframa, ");
                        sql.append("utilizaiva, id_familiafornecedor, id_tipoinspecao, numeroinspecao, ");
                        sql.append("id_tipotroca, id_tipofornecedor, id_contacontabilfinanceiro, ");
                        sql.append("utilizanfe, datacadastro, utilizaconferencia, numero, permitenfsempedido, ");
                        sql.append("modelonf, emitenf, tiponegociacao, utilizacrossdocking, id_lojacrossdocking, ");
                        sql.append("observacao, id_pais, inscricaomunicipal, id_contacontabilfiscalpassivo, ");
                        sql.append("numerocobranca, complemento, complementocobranca, id_contacontabilfiscalativo, ");
                        sql.append("utilizaedi, tiporegravencimento, id_tipoindicadorie) ");
                        sql.append("VALUES ( ");
                        sql.append(id + ", '" + i_fornecedor.razaosocial + "', '" + i_fornecedor.nomefantasia + "', ");
                        sql.append("'" + i_fornecedor.endereco + "', '" + i_fornecedor.bairro + "', " + i_fornecedor.id_municipio + ", ");
                        sql.append(i_fornecedor.cep + ", " + i_fornecedor.id_estado + ", '" + i_fornecedor.telefone + "', ");
                        sql.append(i_fornecedor.id_tipoinscricao + ", '" + i_fornecedor.inscricaoestadual + "', ");
                        sql.append((i_fornecedor.cnpj == -1 ? id : i_fornecedor.cnpj) + ", " + i_fornecedor.revenda + ", " + i_fornecedor.id_situacaocadastro + ", ");
                        sql.append(i_fornecedor.id_tipopagamento + ", " + i_fornecedor.numerodoc + ", " + i_fornecedor.pedidominimoqtd + ", ");
                        sql.append(i_fornecedor.pedidominimovalor + ", '" + i_fornecedor.serienf + "', " + i_fornecedor.descontofunrural + ", ");
                        sql.append(i_fornecedor.senha + ", NULL, '" + i_fornecedor.agencia + "', ");
                        sql.append("'" + i_fornecedor.digitoagencia + "', '" + i_fornecedor.conta + "', '" + i_fornecedor.digitoconta + "', ");
                        sql.append((i_fornecedor.id_banco == -1 ? null : i_fornecedor.id_banco) + ", NULL, '" + i_fornecedor.enderecocobranca + "', ");
                        sql.append("'" + i_fornecedor.bairrocobranca + "', " + i_fornecedor.cepcobranca + ", NULL, ");
                        sql.append("NULL, " + i_fornecedor.bloqueado + ", NULL, ");
                        sql.append("NULL, " + i_fornecedor.id_tipoempresa + ", '" + i_fornecedor.inscricaosuframa + "', ");
                        sql.append(i_fornecedor.utilizaiva + ", NULL , NULL, ");
                        sql.append(i_fornecedor.numeroinspecao + ", 0, " + i_fornecedor.id_tipofornecedor + ", "+i_fornecedor.getId_contacontabilfinanceiro().getID() + ", ");
                        sql.append(i_fornecedor.utilizanfe + ", ");
                        
                        if ((i_fornecedor.datacadastro != null)) {
                            sql.append("'" + i_fornecedor.datacadastro + "',");
                        } else {
                            if ((i_fornecedor.datacadastroStr != null) && (!i_fornecedor.datacadastroStr.trim().isEmpty())) {
                                sql.append("'" + i_fornecedor.datacadastroStr + "',");
                            } else {
                                datacadastro = new java.sql.Date(new java.util.Date().getTime());
                                sql.append("'" + datacadastro + "',");
                            }
                        }
                        
                        sql.append(i_fornecedor.utilizaconferencia + ", '" + i_fornecedor.numero + "', " + i_fornecedor.permitenfsempedido + ", ");
                        sql.append("'" + i_fornecedor.modelonf + "', " + i_fornecedor.emitenf + ", " + i_fornecedor.tiponegociacao + ", " + i_fornecedor.utilizacrossdocking + ", ");
                        sql.append("NULL, '" + i_fornecedor.observacao + "', " + i_fornecedor.id_pais + ", '" + i_fornecedor.inscricaomunicipal + "', ");
                        sql.append("NULL, '" + i_fornecedor.numerocobranca + "', '" + i_fornecedor.complemento + "', ");
                        sql.append("'" + i_fornecedor.complementocobranca + "', NULL, " + i_fornecedor.utilizaedi + ", ");
                        sql.append(i_fornecedor.tiporegravencimento + ", ");
                        sql.append(i_fornecedor.getId_tipoindicadorie().getId()).append(");");

                        Erro = sql.toString();
                        try {
                            stm.execute(sql.toString());
                        } catch (Exception ex) {
                            Conexao.rollback();
                            throw new VRException("Cliente: " + Linha + " SQL: " + Erro + " " + ex.getMessage());
                        }
                        if (!i_fornecedor.telefone2.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'TELEFONE', '" + i_fornecedor.telefone2 + "', 0, '', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.telefone3.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", '" + ("".equals(i_fornecedor.representante) ? "TELEFONE" : i_fornecedor.representante ) + "', "+i_fornecedor.telefone3+", 0, '', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.telefone4.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", '" + ("".equals(i_fornecedor.representante) ? "TELEFONE" : i_fornecedor.representante ) + "', "+i_fornecedor.telefone4+", 0, '', '');");
                            stm.execute(sql.toString());
                        }
                        
                        if (!i_fornecedor.celular.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'CELULAR', '', 0, '', '" + i_fornecedor.celular + "');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.fax.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'FAX', '" + i_fornecedor.fax + "', 0, '', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.email.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ", 'EMAIL', '', 0, '" + Utils.acertarTexto(i_fornecedor.email, 50).toLowerCase() + "', '');");
                            stm.execute(sql.toString());
                        }

                        if (!i_fornecedor.email2.isEmpty()) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO fornecedorcontato( ");
                            sql.append("id_fornecedor, nome, telefone, id_tipocontato, email, celular) ");
                            sql.append("VALUES ( ");
                            sql.append(id + ",'" + ("".equals(i_fornecedor.representante) ? "EMAIL" : i_fornecedor.representante) + "', '', 0, '" + Utils.acertarTexto(i_fornecedor.email2, 50).toLowerCase() + "', '');");
                            stm.execute(sql.toString());
                        }

                        if (salvarCodigoCidadeSistemaAnterior) {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                            sql.append("codigoanterior, codigoatual, id_loja, codigocidade_sistemaanterior) ");
                            sql.append("VALUES ( ");
                            sql.append(i_fornecedor.codigoanterior + ", ");
                            sql.append(id + ", ");
                            sql.append(idLojaFornecedor + ", ");
                            sql.append(i_fornecedor.getCodigocidade_sistemaanterior() + ");");
                            stm.execute(sql.toString());
                        } else {
                            sql = new StringBuilder();
                            sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                            sql.append("codigoanterior, codigoatual, id_loja) ");
                            sql.append("VALUES ( ");
                            sql.append(i_fornecedor.codigoanterior + ", ");
                            sql.append(id + ", ");
                            sql.append(idLojaFornecedor + "); ");
                            stm.execute(sql.toString());
                        }

                        /*sql = new StringBuilder();
                        sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                        sql.append("codigoanterior, codigoatual, id_loja) ");
                        sql.append("VALUES ( ");
                        sql.append(i_fornecedor.codigoanterior + ", " + id + "," + i_fornecedor.idLoja + ");");
                        stm.execute(sql.toString());*/
                        ProgressBar.next();
                    } else {
                        //Se o fornecedor já existe
                        int codigoAtual = rst.getInt("id");
                        //Verifica se já não esta cadastrado na tabela de códigoanterior
                        try (ResultSet rst2 = stm.executeQuery(
                                "select codigoatual from implantacao.codigoanteriorforn where "
                                + "codigoanterior = " + i_fornecedor.getId() + " "
                                + "and id_loja = " + idLojaFornecedor
                        )) {
                            //Se não estiver cadastrado, cadastra
                            if (!rst2.next()) {                               
                                if (salvarCodigoCidadeSistemaAnterior) {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                                    sql.append("codigoanterior, codigoatual, id_loja, codigocidade_sistemaanterior) ");
                                    sql.append("VALUES ( ");
                                    sql.append(i_fornecedor.codigoanterior + ", ");
                                    sql.append(codigoAtual + ", ");
                                    sql.append(idLojaFornecedor + ", ");
                                    sql.append(i_fornecedor.getCodigocidade_sistemaanterior()+");");
                                    stm.execute(sql.toString());
                                } else {
                                    sql = new StringBuilder();
                                    sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                                    sql.append("codigoanterior, codigoatual, id_loja) ");
                                    sql.append("VALUES ( ");
                                    sql.append(i_fornecedor.codigoanterior + ", ");
                                    sql.append(codigoAtual + ", ");
                                    sql.append(idLojaFornecedor + "); ");
                                    stm.execute(sql.toString());
                                }
                            }                            
                        }
                    }
                } else {             
                    //Verifica se já não esta cadastrado na tabela de códigoanterior
                    try (ResultSet rst2 = stm.executeQuery(
                            "select codigoatual from implantacao.codigoanteriorforn where "
                                    + "codigoanterior = " + i_fornecedor.getId() + " "
                                    + "and id_loja = " + idLojaFornecedor
                    )) {
                        //Se não estiver cadastrado, cadastra
                        if (!rst2.next()) {
                            if (salvarCodigoCidadeSistemaAnterior) {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                                sql.append("codigoanterior, codigoatual, id_loja, codigocidade_sistemaanterior) ");
                                sql.append("VALUES ( ");
                                sql.append(i_fornecedor.codigoanterior + ", ");
                                sql.append("-1, ");
                                sql.append(idLojaFornecedor + ", ");
                                sql.append(i_fornecedor.getCodigocidade_sistemaanterior() + ");");
                                stm.execute(sql.toString());
                            } else {
                                sql = new StringBuilder();
                                sql.append("INSERT INTO implantacao.codigoanteriorforn( ");
                                sql.append("codigoanterior, codigoatual, id_loja) ");
                                sql.append("VALUES ( ");
                                sql.append(i_fornecedor.codigoanterior + ", ");
                                sql.append("-1, ");
                                sql.append(idLojaFornecedor + "); ");
                                stm.execute(sql.toString());
                            }
                        }                            
                    }
                }
                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            if (Linha > 0) {
                throw new VRException("Fornecedor: " + Linha + " SQL: " + Erro + " " + ex.getMessage());
            } else {
                Conexao.rollback();
                throw ex;
            }
        }
    }

    public void alterarSitucaoCadastro(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Situação Cadastro...Fornecedor...");
            ProgressBar.setMaximum(v_fornecedor.size());

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant ");
                sql.append("on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedor.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update fornecedor set ");
                    sql.append("id_situacaocadastro = " + i_fornecedor.id_situacaocadastro + ", ");
                    sql.append("observacao = observacao || '" + i_fornecedor.observacao + "' ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());
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

        public void alterarRazaoFantasia(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Razao Social...Fornecedor...");
            ProgressBar.setMaximum(v_fornecedor.size());

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant ");
                sql.append("on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedor.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update fornecedor set ");
                    sql.append("razaosocial = '" + i_fornecedor.razaosocial + "', ");
                    sql.append("nomefantasia = '" + i_fornecedor.nomefantasia + "' ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());
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

    public void alterarTelefone(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Situação Cadastro...Fornecedor...");
            ProgressBar.setMaximum(v_fornecedor.size());

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant ");
                sql.append("on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedor.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update fornecedor set ");
                    sql.append("telefone = '" + i_fornecedor.telefone + "' ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());
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

    public void alterarNumeroEndereco(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Situação Cadastro...Fornecedor...");
            ProgressBar.setMaximum(v_fornecedor.size());

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant ");
                sql.append("on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedor.id);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update fornecedor set ");
                    sql.append("numero = '" + i_fornecedor.numero + "' ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());
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
    
    public void acertarEndereco(List<vrimplantacao.vo.vrimplantacao.FornecedorVO> v_fornecedor) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;

        try {

            Conexao.begin();
            stm = Conexao.createStatement();

            ProgressBar.setStatus("Importando dados...Endereço...");
            ProgressBar.setMaximum(v_fornecedor.size());

            for (vrimplantacao.vo.vrimplantacao.FornecedorVO i_fornecedor : v_fornecedor) {

                sql = new StringBuilder();
                sql.append("select f.id from fornecedor f ");
                sql.append("inner join implantacao.codigoanteriorforn ant ");
                sql.append("on ant.codigoatual = f.id ");
                sql.append("where ant.codigoanterior = " + i_fornecedor.codigoanterior);

                rst = stm.executeQuery(sql.toString());

                if (rst.next()) {

                    sql = new StringBuilder();
                    sql.append("update fornecedor set ");
                    sql.append("endereco = '" + i_fornecedor.endereco + "', ");
                    sql.append("numero = '" + i_fornecedor.numero + "', ");
                    sql.append("bairro = '" + i_fornecedor.bairro + "', ");
                    sql.append("cep = " + i_fornecedor.cep + ", ");
                    sql.append("id_municipio = " + i_fornecedor.id_municipio + ", ");
                    sql.append("id_estado = " + i_fornecedor.id_estado + ", ");
                    sql.append("complemento = '" + i_fornecedor.complemento + "' ");
                    sql.append("where id = " + rst.getInt("id") + ";");

                    stm.execute(sql.toString());
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

    public FornecedorVO getFornecedor2DaLoja(int idLojaVR) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  f.id,\n" +
                "  f.razaosocial,\n" +
                "  f.nomefantasia,\n" +
                "  f.id_municipio,\n" +
                "  m.descricao cidade,\n" +
                "  m.id_estado,\n" +
                "  e.sigla\n" +
                "from \n" +
                "  fornecedor f\n" +
                "  join municipio m on f.id_municipio = m.id\n" +
                "  join estado e on m.id_estado = e.id\n" +
                "order by\n" +
                "  id"
            )) {
                if (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setId(rst.getInt("id"));
                    vo.setRazaoSocial(rst.getString("razaosocial"));
                    vo.setNomeFantasia(rst.getString("nomefantasia"));
                    vo.setIdMunicipio(rst.getInt("id_municipio"));
                    vo.setMunicipio(rst.getString("municipio"));
                    vo.setIdEstado(rst.getInt("id_estado"));
                    vo.setEstado(rst.getString("sigla"));
                    return vo;
                }
            }
        }
        return null;
    }
    
    public int getId(long i_cnpj) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT id FROM fornecedor WHERE cnpj = " + i_cnpj);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    public int getIdByCodigoAnterior(long i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT f.id FROM fornecedor f "
                + "inner join implantacao.codigoanteriorforn ant "
                + "on f.id = ant.codigoatual "
                + "WHERE ant.codigoanterior = " + i_codigo);

        rst = stm.executeQuery(sql.toString());

        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }
    
    public TipoFornecedorVO carregarTipo(long i_cnpj) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append("(SELECT id, 'C' AS tipo, cnpj FROM clienteeventual ");
        sql.append("UNION ALL SELECT id, 'F' AS tipo, cnpj FROM fornecedor ");
        sql.append("UNION ALL SELECT id, 'M' AS tipo, cpf AS cnpj FROM motorista) AS temp WHERE cnpj = " + i_cnpj);

        rst = stm.executeQuery(sql.toString());

        TipoFornecedorVO oTipoFornecedor = new TipoFornecedorVO();

        if (!rst.next()) {
            if (String.valueOf(i_cnpj).length() > 11) {
                throw new VRException("O cnpj " + i_cnpj + " não existe cadastrado!");
            } else {
                throw new VRException("O cpf " + i_cnpj + " não existe cadastrado!");
            }
        }

        oTipoFornecedor.id = rst.getInt("id");
        oTipoFornecedor.tipo = rst.getString("tipo");

        stm.close();

        return oTipoFornecedor;
    }
    
    public int getIdByCnpj(long i_cnpj) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();
        sql = new StringBuilder();
        sql.append("SELECT f.id FROM fornecedor f ");
        sql.append("where f.cnpj = " + i_cnpj);
        rst = stm.executeQuery(sql.toString());
        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }

    public int getIdByCodAnt_Fornecedor(String i_sistema, String i_loja, String i_codigo) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();
        sql = new StringBuilder();
        sql.append("SELECT f.id FROM fornecedor f "
                + "inner join implantacao.codant_fornecedor ant "
                + "on ant.codigoatual = f.id "
                + "where importsistema = '" + i_sistema + "' "
                + "and importloja = '" + i_loja + "' "
                + "and importid = '" + i_codigo + "'");
        rst = stm.executeQuery(sql.toString());
        if (rst.next()) {
            return rst.getInt("id");
        } else {
            return -1;
        }
    }
}
