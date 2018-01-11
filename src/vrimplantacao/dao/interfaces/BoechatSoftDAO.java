package vrimplantacao.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class BoechatSoftDAO {
    
    //CARREGAMENTOS
    
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append("  from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("ID"));
                
                if (rst.getInt("id_tipoembalagem")==4) {
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
                }
                
                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                oProduto.vAutomacao.add(oAutomacao);
                vProduto.put(codigobarras, oProduto);
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<ClientePreferencialVO> carregarCliente(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone1, inscricaoestadual, email, enderecoEmpresa,  
                   dataResidencia,  dataCadastro , numero, complemento, dataNascimento, nomePai, nomeMae,
                   telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                   conjuge = "", orgaoExp = "", celular = "";
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha=0,
                estadoCivil = 0;
            long cnpj, cep;
            double limite, salario;
            boolean bloqueado;

            try {
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select c.codcli, c.tipdoc, c.datcad, c.nomcli, c.tipcli, ");
                sql.append("c.cgccli, c.inscli, c.endcli, c.baicli, c.cepcli, ");
                sql.append("c.foacli, c.fobcli, c.faxcli, c.celcli, c.concli, ");
                sql.append("c.obscli, c.endcob, c.baicob, c.cidcob, c.limcre, ");
                sql.append("c.blocli, c.obsblo, c.fancli, c.inacli, c.emacli, ");
                sql.append("c.datnas, c.cidcli, cid.nomcid, cid.estcid, c.numend ");
                sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq005] c ");
                sql.append("inner join [BoechatSoft_BailoCentral].[dbo].[arq004] cid ");
                sql.append("on cid.codcid = c.cidcli ");
                sql.append("where tipdoc = 'R' ");

                rst = stm.executeQuery(sql.toString());
                Linha=1;
                try{
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        id = rst.getInt("codcli");
                        
                        dataResidencia = "1990/01/01";
                        
                        if ((rst.getString("inacli") != null) &&
                                (!rst.getString("inacli").trim().isEmpty())) {
                            
                            if ("N".equals(rst.getString("inacli").trim())) {
                                id_situacaocadastro = 1;
                            } else {
                                id_situacaocadastro = 0;
                            }
                        } else {
                            id_situacaocadastro = 0;
                        }

                        if ((rst.getString("tipcli") != null) &&
                                (!rst.getString("tipcli").trim().isEmpty())) {
                            
                            if ("Pessoa Física.".equals(rst.getString("tipcli").trim())) {
                                id_tipoinscricao = 1;
                            } else {
                                id_tipoinscricao = 0;
                            }
                        } else {
                            id_tipoinscricao = 1;
                        }
                                                
                        if ((rst.getString("nomcli") != null) &&
                                (!rst.getString("nomcli").trim().isEmpty())) {
                            byte[] bytes = rst.getBytes("nomcli");
                            String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                            nome = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        
                        
                        if ((rst.getString("endcli") != null) &&
                                (!rst.getString("endcli").trim().isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("endcli").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }
                        
                        if ((rst.getString("cgccli") != null) &&
                                (!rst.getString("cgccli").trim().isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("cgccli").trim()));
                        } else {
                            cnpj = id;
                        }
                        
                        if ((rst.getString("baicli") != null) &&
                                (!rst.getString("baicli").trim().isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("baicli").trim().replace("'", ""));
                        } else {
                            bairro = "";
                        }
                        
                        if ((rst.getString("foacli") != null) &&
                                (!rst.getString("foacli").trim().isEmpty())) {
                            telefone1 = util.formataNumero(rst.getString("foacli").trim());
                        } else {
                            telefone1 = "0";
                        }
                        
                        if ((rst.getString("cepcli") != null) &&
                                (!rst.getString("cepcli").trim().isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("cepcli").trim()));
                        } else {
                            cep = 0;
                        }
                        
                        if ((rst.getString("nomcid") != null) &&
                                (!rst.getString("nomcid").trim().isEmpty())) {
                            if ((rst.getString("estcid") != null) &&
                                    (!rst.getString("estcid").trim().isEmpty())) {
                                id_municipio = util.retornarMunicipioIBGEDescricao(rst.getString("nomcid").trim().replace("'", ""), 
                                        rst.getString("estcid").trim().replace("'", ""));
                                
                                if (id_municipio == 0) {
                                    id_municipio = 3540804;
                                }
                            } else {
                                id_municipio = 3540804;
                            }
                        } else {
                            id_municipio = 3540804;
                        }
                        
                        if ((rst.getString("estcid") != null) &&
                                (!rst.getString("estcid").trim().isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(
                            rst.getString("estcid").trim().replace("'", ""));
                            
                            if (id_estado == 0) {
                                id_estado = 35;
                            } else {
                                id_estado = 35;
                            }
                        } else {
                            id_estado = 35;
                        }
      
                        if ((rst.getString("numend") != null) &&
                                (!rst.getString("numend").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("numend").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        complemento = "";
                        
                        if ((rst.getString("limcre") != null) &&
                                (!rst.getString("limcre").trim().isEmpty())) {
                            limite = Double.parseDouble(rst.getString("limcre"));
                        } else {
                            limite = 0;
                        }
                        
                        if ((rst.getString("inscli") != null) &&
                                (!rst.getString("inscli").trim().isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("inscli").trim());
                            inscricaoestadual = inscricaoestadual.replace("'", "");
                            inscricaoestadual = inscricaoestadual.replace("-", "");
                            inscricaoestadual = inscricaoestadual.replace(".", "");
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                        
                        if ((rst.getString("datcad") != null) &&
                                (!rst.getString("datcad").trim().isEmpty())) {
                            dataCadastro = rst.getString("datcad").substring(0, 10).trim().replace("-", "/");
                        } else {
                            dataCadastro = "";
                        }
                        
                        if ((rst.getString("datnas") != null) &&
                                (!rst.getString("datnas").trim().isEmpty())) {
                            dataNascimento = rst.getString("datnas").substring(0, 10).trim().replace("-", "/");
                        } else {
                            dataNascimento = null;
                        }
                        
                        if ((rst.getString("blocli") != null) &&
                                (!rst.getString("blocli").trim().isEmpty())) {
                            
                            if ("N".equals(rst.getString("blocli").trim())) {
                                bloqueado = false;
                            } else if ("S".equals(rst.getString("blocli").trim())) {
                                bloqueado = true;
                            } else {
                                bloqueado = false;
                            }
                        } else {
                            bloqueado = false;
                        }
                        
                        nomePai = "";
                        
                        nomeMae = "";
                        
                        telefone2 = "";
                        
                        if ((rst.getString("faxcli") != null) &&
                                (!rst.getString("faxcli").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("faxcli").trim());
                        } else {
                            fax = "";
                        }
                        
                        if ((rst.getString("obscli") != null) &&
                                (!rst.getString("obscli").trim().isEmpty())) {
                            observacao = util.acertarTexto(rst.getString("obscli").replace("'", "").trim());
                        } else {
                            observacao = "";
                        }
                        
                        if ((rst.getString("emacli") != null) &&
                                (!rst.getString("emacli").trim().isEmpty()) &&
                                (rst.getString("emacli").contains("@"))) {
                            email = util.acertarTexto(rst.getString("emacli").trim().replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("fobcli") != null) &&
                                (!rst.getString("fobcli").trim().isEmpty())) {
                            telefone2 = util.formataNumero(rst.getString("fobcli").trim());
                        } else {
                            telefone2 = "";
                        }
                        
                        id_sexo = 1;
                        
                        empresa = "";
                        
                        telEmpresa = "";
                        
                        cargo = "";
                        
                        enderecoEmpresa = "";
                        
                        salario = 0;
                        
                        estadoCivil = 0;
                        
                        conjuge = "";
                        
                        orgaoExp = "";
                        
                        if ((rst.getString("celcli") != null) &&
                                (!rst.getString("celcli").trim().isEmpty())) {
                            celular = util.formataNumero(rst.getString("celcli").trim());
                        } else {
                            celular = "";
                        }
                        
                        if (nome.length() > 40) {
                            nome = nome.substring(0, 40);
                        }

                        if (conjuge.length() > 25) {
                            conjuge = conjuge.substring(0, 25);
                        }

                        if (endereco.length() > 40) {
                            endereco = endereco.substring(0, 40);
                        }

                        if (bairro.length() > 30) {
                            bairro = bairro.substring(0, 30);
                        }

                        if (String.valueOf(cep).length() > 8) {
                            cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                        }

                        if (telefone1.length() > 14) {
                            telefone1 = telefone1.substring(0, 14);
                        }

                        if (String.valueOf(cnpj).length() > 14) {
                            cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                        }

                        if (inscricaoestadual.length() > 18) {
                            inscricaoestadual = inscricaoestadual.substring(0, 18);
                        }

                        if (complemento.length() > 30) {
                            complemento = complemento.substring(0, 30);
                        }

                        if (email.length() > 50) {
                            email = email.substring(0, 50);
                        }

                        oClientePreferencial.id = id;                        
                        oClientePreferencial.nome = nome;
                        oClientePreferencial.endereco = endereco;
                        oClientePreferencial.bairro = bairro;
                        oClientePreferencial.id_estado = id_estado;
                        oClientePreferencial.id_municipio = id_municipio;
                        oClientePreferencial.cep = cep;
                        oClientePreferencial.telefone = telefone1;
                        oClientePreferencial.inscricaoestadual = inscricaoestadual;
                        oClientePreferencial.cnpj = cnpj;
                        oClientePreferencial.sexo = id_sexo;
                        oClientePreferencial.dataresidencia = dataResidencia;
                        oClientePreferencial.datacadastro = dataCadastro;
                        oClientePreferencial.email = email;
                        oClientePreferencial.valorlimite = limite;
                        oClientePreferencial.codigoanterior = id;
                        oClientePreferencial.fax = fax;
                        oClientePreferencial.bloqueado = bloqueado;
                        oClientePreferencial.id_situacaocadastro = id_situacaocadastro;
                        oClientePreferencial.telefone2 = telefone2;
                        oClientePreferencial.observacao = observacao;
                        oClientePreferencial.datanascimento = dataNascimento;
                        oClientePreferencial.nomepai = nomePai;
                        oClientePreferencial.nomemae = nomeMae;
                        oClientePreferencial.empresa = empresa;
                        oClientePreferencial.telefoneempresa = telEmpresa;
                        oClientePreferencial.numero = numero;
                        oClientePreferencial.cargo = cargo;
                        oClientePreferencial.enderecoempresa = enderecoEmpresa;
                        oClientePreferencial.id_tipoinscricao = id_tipoinscricao;
                        oClientePreferencial.salario = salario;
                        oClientePreferencial.id_tipoestadocivil = estadoCivil;
                        oClientePreferencial.nomeconjuge = conjuge;
                        oClientePreferencial.orgaoemissor = orgaoExp;
                        oClientePreferencial.celular = celular;
                        vClientePreferencial.add(oClientePreferencial);
                    }
                stm.close();
                } catch (Exception ex) {
                    throw ex;
                    //if (Linha > 0) {
                    //    throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                    //} else {
                    //    throw ex;
                    //}
                }
                return vClientePreferencial;
            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
        }    
    
    public List<ClientePreferencialVO> carregarValorLimteCliente() throws Exception {
        List<ClientePreferencialVO> vCliente = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente = 0;
        double valorLimite = 0;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select c.codcli,  c.limcre ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq005] c ");
            sql.append("where c.tipdoc = 'R' ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idCliente = Integer.parseInt(rst.getString("codcli").trim());
                
                if ((rst.getString("limcre") != null) &&
                        (!rst.getString("limcre").trim().isEmpty())) {
                    valorLimite = Double.parseDouble(rst.getString("limcre").trim());
                } else {
                    valorLimite = 0;
                }
                
                ClientePreferencialVO oCliente = new ClientePreferencialVO();
                oCliente.id = idCliente;
                oCliente.valorlimite = valorLimite;
                
                vCliente.add(oCliente);
            }
            
            return vCliente;
        } catch(Exception ex) {
            throw ex;
        }
    }

    public List<ClientePreferencialVO> carregarNumeroEnderecoCliente() throws Exception {
        List<ClientePreferencialVO> vCliente = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idCliente = 0;
        String numero = "";
        Utils util = new Utils();
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select c.codcli,  c.numend ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq005] c ");
            sql.append("where c.tipdoc = 'R' ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idCliente = Integer.parseInt(rst.getString("codcli").trim());
                
                if ((rst.getString("numend") != null) &&
                        (!rst.getString("numend").trim().isEmpty())) {
                    numero = util.acertarTexto(rst.getString("numend").trim().replace("'", ""));
                } else {
                    numero = "0";
                }
                
                ClientePreferencialVO oCliente = new ClientePreferencialVO();
                oCliente.id = idCliente;
                oCliente.numero = numero;
                
                vCliente.add(oCliente);
            }
            
            return vCliente;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<FornecedorVO> carregarFornecedor() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                   numero = "", complemento = "", telefone = "", email = "", fax = "", telefone2 = "",
                   celular = "";
            int id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            long cnpj, cep;
            boolean ativo=true;
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

            try {
                
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select f.codcli, f.tipdoc, f.datcad, f.nomcli, f.tipcli,  ");
                sql.append("f.cgccli, f.inscli, f.endcli, f.baicli, f.cepcli, ");
                sql.append("f.foacli, f.fobcli, f.faxcli, f.celcli, f.concli, ");
                sql.append("f.obscli, f.endcob, f.baicob, f.cidcob, f.limcre, ");
                sql.append("f.blocli, f.obsblo, f.fancli, f.inacli, f.emacli, ");
                sql.append("f.datnas, f.cidcli, c.nomcid, c.estcid, f.numend ");
                sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq005] f ");
                sql.append("inner join [BoechatSoft_BailoCentral].[dbo].[arq004] c ");
                sql.append("on c.codcid = f.cidcli ");
                sql.append("where tipdoc = 'P' ");
                sql.append("order by nomcli ");

                rst = stm.executeQuery(sql.toString());

                Linha=0;
                
                try{
                    while (rst.next()) {
                        
                        FornecedorVO oFornecedor = new FornecedorVO();

                        Linha++; 
                        if (Linha==3){
                            Linha--;
                            Linha++;                        
                        }                    
                        if ((rst.getString("nomcli") != null)
                                && (!rst.getString("nomcli").isEmpty())) {
                           byte[] bytes = rst.getBytes("nomcli");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "";
                        }

                        if ((rst.getString("fancli") != null)
                                && (!rst.getString("fancli").isEmpty())) {
                           byte[] bytes = rst.getBytes("fancli");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "";
                        }

                        if ((rst.getString("cgccli") != null)
                                && (!rst.getString("cgccli").isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("cgccli").trim()));
                        } else {
                            cnpj = -1;
                        }

                        if ((rst.getString("inscli") != null)
                                && (!rst.getString("inscli").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("inscli").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        if ((rst.getString("tipcli") != null) &&
                                (!rst.getString("tipcli").trim().isEmpty())) {
                        
                            if ("Pessoa Física.".equals(rst.getString("tipcli").trim())) {
                                id_tipoinscricao = 1;
                            } else {
                                id_tipoinscricao = 0;
                            }
                        } else {
                            id_tipoinscricao = 0;
                        }

                        if ((rst.getString("endcli") != null)
                                && (!rst.getString("endcli").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("endcli").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("baicli") != null)
                                && (!rst.getString("baicli").isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("baicli").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("cepcli") != null)
                                && (!rst.getString("cepcli").isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("cepcli").trim()));
                        } else {
                            cep = 15105000;
                        }

                        if ((rst.getString("nomcid") != null)
                                && (!rst.getString("nomcid").isEmpty())) {

                            if ((rst.getString("estcid") != null)
                                    && (!rst.getString("estcid").isEmpty())) {

                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("nomcid").replace("'", "").trim()),
                                        util.acertarTexto(rst.getString("estcid").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = 3540804;
                                }
                            }
                        } else {
                            id_municipio = 3540804;
                        }

                        if ((rst.getString("estcid") != null)
                                && (!rst.getString("estcid").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estcid").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 35;
                            }
                        } else {
                            id_estado = 35;
                        }

                        if (rst.getString("obscli") != null) {
                            obs = util.acertarTexto(rst.getString("obscli").trim().replace("'", ""));
                        } else {
                            obs = "";
                        }

                        if ((rst.getString("datcad") != null) &&
                                (!rst.getString("datcad").trim().isEmpty())) {
                            datacadastro = rst.getString("datcad").substring(0, 10).replace("-", "/").trim();
                        } else {
                            datacadastro = null;
                        }

                        if ((rst.getString("inacli") != null) &&
                                (!rst.getString("inacli").trim().isEmpty())) {
                            
                            if ("N".equals(rst.getString("inacli").trim())) {
                                ativo = true;
                            } else {
                                ativo = false;
                            }
                        } else {
                            ativo = false;
                        }
                        
                        if ((rst.getString("numend") != null) &&
                               (!rst.getString("numend").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("numend").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        complemento = "";
                        
                        if ((rst.getString("foacli") != null) &&
                                (!rst.getString("foacli").trim().isEmpty())) {
                            telefone = util.formataNumero(rst.getString("foacli").trim());
                        } else {
                            telefone = "0";
                        }

                        if ((rst.getString("emacli") != null) &&
                                (!rst.getString("emacli").trim().isEmpty()) &&
                                (rst.getString("emacli").contains("@"))) {
                            email = util.acertarTexto(rst.getString("emacli").replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("faxcli") != null) &&
                                (!rst.getString("faxcli").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("faxcli").trim());
                        } else {
                            fax = "";
                        }
                        
                        if ((rst.getString("fobcli") != null) &&
                                (!rst.getString("fobcli").trim().isEmpty())) {
                            telefone2 = util.formataNumero(rst.getString("fobcli").trim());
                        } else {
                            telefone2 = "";
                        }
                        
                        if ((rst.getString("celcli") != null) &&
                                (!rst.getString("celcli").trim().isEmpty())) {
                            celular = util.formataNumero(rst.getString("celcli").trim());
                        } else {
                            celular = "";
                        }
                        
                        if (razaosocial.length() > 40) {
                            razaosocial = razaosocial.substring(0, 40);
                        }

                        if (nomefantasia.length() > 30) {
                            nomefantasia = nomefantasia.substring(0, 30);
                        }

                        if (endereco.length() > 40) {
                            endereco = endereco.substring(0, 40);
                        }

                        if (bairro.length() > 30) {
                            bairro = bairro.substring(0, 30);
                        }

                        if (String.valueOf(cep).length() > 8) {
                            cep = Long.parseLong(String.valueOf(cep).substring(0, 8));
                        }

                        if (String.valueOf(cnpj).length() > 14) {
                            cnpj = Long.parseLong(String.valueOf(cnpj).substring(0, 14));
                        }

                        if (inscricaoestadual.length() > 20) {
                            inscricaoestadual = inscricaoestadual.substring(0, 20);
                        }
                        
                        if (telefone.length() > 14) {
                            telefone = telefone.substring(0, 14);
                        }
                        
                        if (numero.length() > 6) {
                            numero = numero.substring(0, 6);
                        }

                        oFornecedor.codigoanterior = rst.getInt("codcli");
                        oFornecedor.datacadastro = new java.sql.Date(format.parse(datacadastro).getTime());
                        oFornecedor.razaosocial = razaosocial;
                        oFornecedor.nomefantasia = nomefantasia;
                        oFornecedor.endereco = endereco;
                        oFornecedor.bairro = bairro;
                        oFornecedor.numero = numero;
                        oFornecedor.id_municipio = id_municipio;
                        oFornecedor.cep = cep;
                        oFornecedor.id_estado = id_estado;
                        oFornecedor.id_tipoinscricao = id_tipoinscricao;
                        oFornecedor.inscricaoestadual = inscricaoestadual;
                        oFornecedor.cnpj = cnpj;
                        oFornecedor.id_situacaocadastro = (ativo == true ?  1 : 0);                    
                        oFornecedor.observacao = obs;
                        oFornecedor.complemento = complemento;
                        oFornecedor.telefone = telefone;
                        oFornecedor.email = email;
                        oFornecedor.fax = fax;
                        oFornecedor.telefone2 = telefone2;
                        oFornecedor.celular = celular;

                        vFornecedor.add(oFornecedor);
                    }
                } catch (Exception ex) {
                    if (Linha > 0) {
                        throw new VRException("Linha " + Linha + ": " + ex.getMessage());
                    } else {
                        throw ex;
                    }
                }

                return vFornecedor;

            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
    }   
    
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select codsub, dessub ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq007] ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("codsub"));
                    
                    if ((rst.getString("dessub") != null) &&
                            (!rst.getString("dessub").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("dessub").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if ((nivel == 2)) {
                    
                    mercadologico1 = Integer.parseInt(rst.getString("codsub"));
                    mercadologico2 = 1;
                    
                    if ((rst.getString("dessub") != null) &&
                            (!rst.getString("dessub").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("dessub").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if ((nivel == 3)) {
                    
                    mercadologico1 = Integer.parseInt(rst.getString("codsub"));
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                    
                    if ((rst.getString("dessub") != null) &&
                            (!rst.getString("dessub").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("dessub").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    if (descricao.length() > 35) {

                        descricao = descricao.substring(0, 35);
                    }

                    oMercadologico.mercadologico1 = mercadologico1;
                    oMercadologico.mercadologico2 = mercadologico2;
                    oMercadologico.mercadologico3 = mercadologico3;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }

            return vMercadologico;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<FamiliaProdutoVO> carregarFamiliaProduto() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        String descricao;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select codaso, nomaso ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[Arq046] ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if (!util.encontrouLetraCampoNumerico(rst.getString("codaso").trim())) {
                
                    if ((rst.getString("nomaso") != null)
                            && (!rst.getString("nomaso").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("nomaso").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                    oFamiliaProduto.id = Integer.parseInt(rst.getString("codaso").replace(".", ""));

                    oFamiliaProduto.descricao = descricao;
                    oFamiliaProduto.id_situacaocadastro = 1;
                    oFamiliaProduto.codigoant = Integer.parseInt(rst.getString("codaso").replace(".", ""));

                    vFamiliaProduto.add(oFamiliaProduto);                
                }
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
   
    public Map<Integer, ProdutoVO> carregarProduto() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1 = 0, mercadologico2 = 0, mercadologico3 = 0, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, validade, idProduto;
        String descricaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras,
               strSitTrib, valorIcms, strReducao, dataCadastro;
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0;
        
        try {
            
            stmPostgres = Conexao.createStatement();
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select codpro, datpro, nompro, barpro, grupro, subpro, cunpro, ");
            sql.append("vunpro, vqvpro, cuspro, medpro, icmpro, lucpro, venpro, ");
            sql.append("estpro, inapro, regncm, sittri, InfoNutricionais, InfoExtras, ");
            sql.append("cstpis, cstcof, redicm, asopro ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq008] ");
            sql.append("order by codpro ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ((rst.getString("inapro") != null) &&
                        (!rst.getString("inapro").trim().isEmpty())) {
                    
                    if ("N".equals(rst.getString("inapro").trim())) {
                        idSituacaoCadastro = 1;
                    } else {
                        idSituacaoCadastro = 0;
                    }
                } else {
                    idSituacaoCadastro = 1;
                }
                
                codigoAnterior = Double.parseDouble(rst.getString("codpro").trim());
                idProduto = Integer.parseInt(rst.getString("codpro").trim());
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo =  " + Integer.parseInt(rst.getString("codpro").trim()));

                rstPostgres = stmPostgres.executeQuery(sql.toString());

                if (rstPostgres.next()) {

                    eBalanca = true;
                    codigoBalanca = rstPostgres.getInt("codigo");
                    validade = rstPostgres.getInt("validade");
                    
                    if ("U".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else if ("P".equals(rstPostgres.getString("pesavel").trim())) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                } else {
                    
                    codigoBalanca = -1;
                    eBalanca = false;
                    pesavel = false;
                    validade = 0;                    
                    if ("CX".equals(rst.getString("vunpro").trim())) {                        
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("vunpro").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("vunpro").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                if ((rst.getString("nompro") != null) &&
                        (!rst.getString("nompro").trim().isEmpty())) {
                    
                    byte[] bytes = rst.getBytes("nompro");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    
                    descricaoCompleta = "";
                }
                
                if ((rst.getString("nompro") != null) &&
                        (!rst.getString("nompro").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("nompro");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descricaoCompleta;

                qtdEmbalagem = 1;
                
                if ((rst.getString("asopro") != null) &&
                        (!rst.getString("asopro").trim().isEmpty())) {
                
                    idFamilia = Integer.parseInt(rst.getString("asopro").trim());
                    
                    sql = new StringBuilder();
                    sql.append("select id from familiaproduto ");
                    sql.append("where id = " +idFamilia);
                            
                    rstPostgres  = stmPostgres.executeQuery(sql.toString());
                    
                    if (!rstPostgres.next()) {
                        idFamilia = -1;
                    }
                } else {
                    idFamilia = -1;
                }
                
                if ((rst.getString("subpro") != null) &&
                        (!rst.getString("subpro").trim().isEmpty())) {
                    
                    mercadologico1 = Integer.parseInt(rst.getString("subpro"));
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                    
                    sql = new StringBuilder();
                    sql.append("select mercadologico1 ");
                    sql.append("from mercadologico ");
                    sql.append("where mercadologico1 = " + mercadologico1);
                    
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (!rstPostgres.next()) {
                        
                        sql = new StringBuilder();
                        sql.append("select max(mercadologico1) as mercadologico1 ");
                        sql.append("from mercadologico ");
                        
                        rstPostgres = stmPostgres.executeQuery(sql.toString());
                        
                        if (rstPostgres.next()) {
                            mercadologico1 = rst.getInt("mercadologico1");
                            mercadologico2 = 1;
                            mercadologico3 = 1;
                        }
                    }
                }
                
                if ((rst.getString("regncm") != null) &&
                        (!rst.getString("regncm").isEmpty()) &&
                        (rst.getString("regncm").trim().length() > 5)) {
                    
                    ncmAtual = util.formataNumero(rst.getString("regncm").trim());
                    if ((ncmAtual != null)
                            && (!ncmAtual.isEmpty())
                            && (ncmAtual.length() > 5)) {
                        try{
                            NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                            ncm1 = oNcm.ncm1;
                            ncm2 = oNcm.ncm2;
                            ncm3 = oNcm.ncm3;
                        } catch (Exception ex) {
                            ncm1 = 402;
                            ncm2 = 99;
                            ncm3 = 0;
                        }
                    } else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }
                                
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {
                    
                    if ((rst.getString("barpro") != null) &&
                            (!rst.getString("barpro").trim().isEmpty())) {
                        
                        strCodigoBarras = util.formataNumero(rst.getString("barpro").trim());
                        
                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {                            
                            codigoBarras = -1;
                        } else {
                            codigoBarras = Long.parseLong(util.formataNumero(rst.getString("barpro").trim()));
                        }
                    } else {
                        codigoBarras = -1;
                    }
                }
                
                if ((rst.getString("cstpis") != null) &&
                        (!rst.getString("cstpis").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("cstpis").trim()));
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                } else {                
                    idTipoPisCofins = 1;
                    tipoNaturezaReceita = 999;
                }
                
                if ((rst.getString("cstcof") != null) &&
                        (!rst.getString("cstcof").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCreditoBoeachatSoft(Integer.parseInt(rst.getString("cstcof").trim()));
                } else {
                
                    idTipoPisCofinsCredito = 13;
                }
                
                if ((rst.getString("sittri") != null)
                        && (!rst.getString("sittri").trim().isEmpty())) {

                    if (rst.getString("sittri").length() >= 2) {
                        strSitTrib = rst.getString("sittri").trim().substring(rst.getString("sittri").length() - 2);

                        if ((rst.getString("icmpro") != null)
                                && (!rst.getString("icmpro").trim().isEmpty())) {
                            valorIcms = rst.getString("icmpro").trim();
                        } else {
                            valorIcms = "";
                        }

                        if ((rst.getString("redicm") != null)
                                && (!rst.getString("redicm").trim().isEmpty())) {
                            strReducao = rst.getString("redicm").trim().replace(",", ".");
                        } else {
                            strReducao = "";
                        }

                        idAliquota = retornarICMS(Integer.parseInt(strSitTrib), valorIcms, strReducao);

                    } else {
                        idAliquota = 8;
                    }
                } else {
                    idAliquota = 8;
                }

                if ((rst.getString("datpro") != null) &&
                        (!rst.getString("datpro").trim().isEmpty())) {
                    dataCadastro = rst.getString("datpro").trim().substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }
                
                if ((rst.getString("venpro") != null) &&
                        (!rst.getString("venpro").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("venpro"));
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("cuspro") != null) &&
                        (!rst.getString("cuspro").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("cuspro"));
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("lucpro") != null) &&
                        (!rst.getString("lucpro").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("lucpro"));
                } else {
                    margem = 0;
                }
                
                if (descricaoCompleta.length() > 60) {
                    descricaoCompleta = descricaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }
                
                oProduto.id = idProduto;
                oProduto.dataCadastro = dataCadastro;
                oProduto.descricaoCompleta = descricaoCompleta;
                oProduto.descricaoReduzida = descricaoReduzida;
                oProduto.descricaoGondola = descricaoGondola;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                oProduto.pesavel = pesavel;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = idFamilia;
                oProduto.idFornecedorFabricante = 1;
                oProduto.sugestaoPedido = true;
                oProduto.aceitaMultiplicacaoPdv = true;
                oProduto.sazonal = false;
                oProduto.fabricacaoPropria = false;
                oProduto.consignado = false;
                oProduto.ddv = 0;
                oProduto.permiteTroca = true;
                oProduto.vendaControlada = false;
                oProduto.vendaPdv = true;
                oProduto.conferido = true;
                oProduto.permiteQuebra = true;
                oProduto.permitePerda = true;
                oProduto.utilizaTabelaSubstituicaoTributaria = false;
                oProduto.utilizaValidadeEntrada = false;
                oProduto.margem = margem;
                oProduto.validade = validade;
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                
                oAliquota.idEstado = 35;
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                
                oProduto.vAliquota.add(oAliquota);
                
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                
                oAutomacao.codigoBarras = codigoBarras;
                oAutomacao.qtdEmbalagem = qtdEmbalagem;
                oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                
                oProduto.vAutomacao.add(oAutomacao);
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.codigoanterior = codigoAnterior;                
                
                if((rst.getString("barpro")!=null) && (!rst.getString("barpro").trim().isEmpty())){
                   oCodigoAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("barpro").replace(".", "").trim()));
                } else {
                   oCodigoAnterior.barras = 0; 
                }
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                
                if ((rst.getString("cstpis") != null) &&
                        (!rst.getString("cstpis").trim().isEmpty())) {
                    oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("cstpis").trim());
                } else {
                    oCodigoAnterior.piscofinsdebito = -1;
                }
                
                if ((rst.getString("cstcof") != null) &&
                        (!rst.getString("cstcof").trim().isEmpty())) {
                    oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("cstcof").trim());
                } else {
                    oCodigoAnterior.piscofinscredito = -1;
                }
                
                if ((rst.getString("sittri") != null)
                        && (!rst.getString("sittri").trim().isEmpty())) {

                    if (!"0".equals(rst.getString("sittri").trim())
                            && (!"00".equals(rst.getString("sittri")))) {
                        oCodigoAnterior.ref_icmsdebito = rst.getString("sittri").trim();
                    } else {
                        oCodigoAnterior.ref_icmsdebito = rst.getString("icmpro").trim();
                    }

                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.margem = margem;
                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("regncm") != null) && (!rst.getString("regncm").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("regncm").trim().replace(".", "");
                } else {
                    oCodigoAnterior.ncm = "";
                }
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
            }
            
            stmPostgres.close();
            Conexao.commit();
            return vProduto;
            
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarDataProduto() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProduto = new ArrayList<>();
        int idProduto = 0;
        String dataCadastro = "";
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select codpro, datpro ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq008]");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro").trim());
                
                if ((rst.getString("datpro") != null) &&
                        (!rst.getString("datpro").trim().isEmpty())) {
                    dataCadastro = rst.getString("datpro").trim().substring(0, 10);
                } else {
                    dataCadastro = "";
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.dataCadastro = dataCadastro;
                
                vProduto.add(oProduto);
            }

            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarCustoProduto(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select codpro, cuspro ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq008] ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("codpro"));
                
                if ((rst.getString("cuspro") != null) &&
                        (!rst.getString("cuspro").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("cuspro"));                    
                } else {
                    custo = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                
                oProduto.vComplemento.add(oComplemento);                
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.custocomimposto = custo;                
                oCodigoAnterior.custosemimposto = custo;
                oCodigoAnterior.id_loja = idLoja;
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
       
    public List<ReceberCreditoRotativoVO> carregarReceberCreditoRotativo(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int id_cliente, numerocupom, ecf;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select numfat, codemi, datemi, datven, datpag, valpar ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq022] ");
            sql.append("where datpag is null ");
            sql.append("and datqui is null ");
            sql.append("order by datemi ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("codemi");                
                dataemissao = rst.getString("datemi").substring(0, 10).trim();
                datavencimento = rst.getString("datven").substring(0, 10).trim();
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("numfat")));
                valor = Double.parseDouble(rst.getString("valpar"));
                juros = 0;

                ecf = 0;
                
                observacao = "IMPORTADO VR";
                
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.ecf = ecf;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.id_clientepreferencial = id_cliente;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;
                
                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
            }
            
            return vReceberCreditoRotativo;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    

    public Map<Integer, ProdutoVO> carregarPrecoProduto(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        double preco = 0, margem = 0;
        int idProduto = 0;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select codpro, venpro, lucpro ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq008] ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro"));
                
                if ((rst.getString("venpro") != null) &&
                        (!rst.getString("venpro").trim().isEmpty())) {
                    preco = Double.parseDouble(rst.getString("venpro"));
                } else {
                    preco = 0;
                }
                
                if ((rst.getString("lucpro") != null) &&
                        (!rst.getString("lucpro").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("lucpro").trim());
                } else {
                    margem = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                oProduto.margem = margem;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.precoVenda = preco;
                oComplemento.precoDiaSeguinte = preco;
                
                oProduto.vComplemento.add(oComplemento);                
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.precovenda = preco;
                oCodigoAnterior.id_loja = idLoja;
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public List<ProdutoVO> carregarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0, idAliquota = 8;        
        String strReducao = "", strSitTrib = "", valorIcms = "";
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select codpro, sittri, icmpro, redicm ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[arq008] ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro").trim());
                
                if (idProduto == 23302) {
                    System.out.println("aqui");
                }
                
                if ((rst.getString("sittri") != null)
                        && (!rst.getString("sittri").trim().isEmpty())) {

                    if (rst.getString("sittri").length() >= 2) {
                        strSitTrib = rst.getString("sittri").trim().substring(rst.getString("sittri").length() - 2);

                        if ((rst.getString("icmpro") != null)
                                && (!rst.getString("icmpro").trim().isEmpty())) {
                            valorIcms = rst.getString("icmpro").trim();
                        } else {
                            valorIcms = "";
                        }

                        if ((rst.getString("redicm") != null)
                                && (!rst.getString("redicm").trim().isEmpty())) {
                            strReducao = rst.getString("redicm").trim().replace(",", ".");
                        } else {
                            strReducao = "";
                        }

                        idAliquota = retornarICMS(Integer.parseInt(strSitTrib), valorIcms, strReducao);

                    } else {
                        idAliquota = 8;
                    }
                } else {
                    idAliquota = 8;
                }
                                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                oAliquota.idAliquotaDebito = idAliquota;
                oAliquota.idAliquotaCredito = idAliquota;
                oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                oProduto.vAliquota.add(oAliquota);

                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                
                if ((rst.getString("sittri") != null)
                        && (!rst.getString("sittri").trim().isEmpty())) {

                    if (!"0".equals(rst.getString("sittri").trim())
                            && (!"00".equals(rst.getString("sittri")))) {
                        oAnterior.ref_icmsdebito = rst.getString("sittri").trim();
                    } else {
                        oAnterior.ref_icmsdebito = rst.getString("icmpro").trim();
                    }

                } else {
                    oAnterior.ref_icmsdebito = "";
                }
                
                oProduto.vCodigoAnterior.add(oAnterior);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        double saldo = 0;
        int idProduto = 0;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();            
            sql.append("select produto, quantidade ");
            sql.append("from [BoechatSoft_BailoCentral].[dbo].[Estoque] ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("produto"));
                
                if ((rst.getString("quantidade") != null) &&
                        (!rst.getString("quantidade").trim().isEmpty())) {
                    saldo = Double.parseDouble(rst.getString("quantidade"));    
                } else {
                    saldo = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.estoque = saldo;
                oProduto.vComplemento.add(oComplemento);                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.estoque = saldo;
                oCodigoAnterior.id_loja = idLoja;
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    
    
    public Map<Long, ProdutoVO> carregarCodigoBarras() throws SQLException {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        double idProduto;
        long codigobarras;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select procodaux, procod "); 
            sql.append("from produtoaux ");

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod"));

                if ((rst.getString("procodaux") != null) &&
                        (!rst.getString("procodaux").trim().isEmpty())) {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("procodaux").replace(".", "").trim()));
                } else {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("procod").replace(".", "").trim()));
                }
                
                if (String.valueOf(codigobarras).length() >= 7) {
                
                    if (String.valueOf(codigobarras).length() > 14) {
                        codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                    }                    
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.idDouble = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(codigobarras, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarPisCofinsSysPdv() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProdutoPisCofins = new ArrayList<>();
        double idProduto = 0;
        int idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT PROD.IMPFEDSIM,PROD.PROCOD, IMP.IMPFEDSIM,IMP.IMPFEDDES,IMP.IMPFEDTIP, ");
            sql.append("IMP.IMPFEDST,IMP.IMPFEDSTSAI,PR.NATCODIGO ");
            sql.append("FROM IMPOSTOS_FEDERAIS_PRODUTO AS PROD ");
            sql.append("INNER JOIN  IMPOSTOS_FEDERAIS AS IMP ON IMP.IMPFEDSIM = PROD.IMPFEDSIM ");
            sql.append("INNER JOIN  PRODUTO AS PR ON PR.PROCOD = PROD.PROCOd ");
            sql.append("where IMPFEDTIP = 'P' ");
            sql.append("ORDER BY  PROD.PROCOD ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ((rst.getString("IMPFEDSTSAI") != null) &&
                        (!rst.getString("IMPFEDSTSAI").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("IMPFEDSTSAI").trim()));
                } else {
                    idTipoPisCofins = 0;
                }
                
                if ((rst.getString("IMPFEDST") != null) &&
                        (!rst.getString("IMPFEDST").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("IMPFEDST").trim()));
                } else {
                    idTipoPisCofinsCredito = 12;
                }
                
                if ((rst.getString("NATCODIGO") != null) &&
                        (!rst.getString("NATCODIGO").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            util.acertarTexto(rst.getString("NATCODIGO").trim()));
                } else {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    
                }
                
                idProduto = Double.parseDouble(rst.getString("PROCOD"));
                
                oProduto.idDouble = idProduto;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                
                vProdutoPisCofins.add(oProduto);
                
                
                
            }
            
            return vProdutoPisCofins;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    //IMPORTAÇÕES
    public void importarClientePreferencial(int idLojaCliente, int idLoja) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }

    public void importarValorLimiteCliente() throws Exception {
        List<ClientePreferencialVO> vCliente = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Valor Limte Crédito...");
            
            vCliente = carregarValorLimteCliente();
            
            new ClientePreferencialDAO().alterarValorLimte(vCliente);
        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarNumeroEnderecoCliente() throws Exception {
        List<ClientePreferencialVO> vCliente = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Número Endereço...");
            
            vCliente = carregarNumeroEnderecoCliente();
            
            new ClientePreferencialDAO().alterarNumeroEndereco(vCliente);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Fornecedor...");
            List<FornecedorVO> vFornecedor = carregarFornecedor();

            new FornecedorDAO().salvar(vFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProduto();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologico(1);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologico(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologico(3);
            new MercadologicoDAO().salvar(vMercadologico, false);
            
            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {

            throw ex;
        }
    }   

    public void importarProduto(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProduto = carregarProduto();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProduto.size());
            
            for (Integer keyId : vProduto.keySet()) {
                
                ProdutoVO oProduto = vProduto.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);
                
                
                ProgressBar.next();
            }
            
            produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }

    public void importarDataProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            
            ProgressBar.setStatus("Carregando dados...Data Cadastro Produto...");
            vProduto = carregarDataProduto();
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarCustoProduto(int id_lojaCliente, int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProduto(id_loja, id_lojaCliente);
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Double keyId : vCustoProduto.keySet()) {
                
                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarCustoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarPrecoProduto(int id_lojaCliente, int id_loja) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProduto(id_loja, id_lojaCliente);
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Integer keyId : vPrecoProduto.keySet()) {
                
                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarEstoqueProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProduto = carregarEstoqueProduto(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Integer keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarEstoqueProduto(vProdutoNovo, id_lojaCliente);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    

    public void importarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        try {
            ProgressBar.setStatus("Carregando dados...Icms Produtos...");
            vProduto = carregarIcmsProduto();
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().alterarICMSProduto(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarras();
            
            ProgressBar.setMaximum(vCodigoBarra.size());
            
            for (Long keyId : vCodigoBarra.keySet()) {
                
                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.addCodigoBarrasSysPdv(vProdutoNovo);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    
    
    public void importarReceberCliente(int idLojaCliente, int idLoja) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberCreditoRotativo(idLoja, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLoja);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
            List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

            new ProdutoBalancaDAO().salvar(vProdutoBalanca);
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarPisCofinsProdutoSysPdv() throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Pis Cofins...Natureza Receita...");
            List<ProdutoVO> vProduto = carregarPisCofinsSysPdv();
            
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarCodigoBarraEmBranco() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Codigo Barras...");
            Map<Long, ProdutoVO> vCodigoBarra = carregarCodigoBarrasEmBranco();

            ProgressBar.setMaximum(vCodigoBarra.size());

            for (Long keyId : vCodigoBarra.keySet()) {

                ProdutoVO oProduto = vCodigoBarra.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarrasEmBranco(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }        
    
    // FUNÇÕES
    
    public void corrigirClienteDuplicado() throws Exception {

        try {
            ProgressBar.setStatus("Corrigindo dados...Cliente Duplicados...");
            new ClientePreferencialDAO().corrigirClienteDuplicado();
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    /***************/
    private int retornarICMS(int codSitTrib, String valor, String reducao) {
        int retorno = 8;
        
        if (codSitTrib == 0) {
            
            if (("7".equals(valor)) ||
                    ("7.0".equals(valor))) {
                retorno = 0;
            } else if (("12".equals(valor)) ||
                    ("12.0".equals(valor))) {
                retorno = 1;
            } else if (("18".equals(valor)) ||
                    ("18.0".equals(valor))) {
                retorno = 2;
            } else if (("25".equals(valor)) ||
                    ("25.0".equals(valor))) {
                retorno = 3;
            }
            
        } else if (codSitTrib == 10) {            
            retorno = 18;
        } else if ((codSitTrib == 30) || 
                   (codSitTrib == 60) || (codSitTrib == 70)) {
            retorno = 7;
        } else if (codSitTrib == 20) {

            if (!"0.0".equals(reducao)) {
            
                if (("12".equals(valor))
                        || ("12.0".equals(valor))
                        && ("41.67").equals(reducao)) {
                    retorno = 5;
                } else if (("18".equals(valor))
                        || ("18.0".equals(valor))
                        && ("33.33".equals(reducao))) {
                    retorno = 9;
                } else if (("18".equals(valor))
                        || ("18.0".equals(valor))
                        && ("61.11".equals(reducao))) {
                    retorno = 4;
                } else if (("25".equals(valor))
                        || ("25.0".equals(valor))
                        && ("52.00".equals(reducao))) {
                    retorno = 10;
                }
            
            } else {
                
                if (("7".equals(valor))
                        || ("7.0".equals(valor))) {
                    retorno = 0;
                } else if (("12".equals(valor))
                        || ("12.0".equals(valor))) {
                    retorno = 1;
                } else if (("18".equals(valor))
                        || ("18.0".equals(valor))) {
                    retorno = 2;
                } else if (("25".equals(valor))
                        || ("25.0".equals(valor))) {
                    retorno = 3;
                }
            }
            
        } else if (codSitTrib == 40) {
            retorno = 6;
        } else if (codSitTrib == 41) {
            retorno = 17;
        } else if (codSitTrib == 50) {
            retorno = 13;
        } else if (codSitTrib == 51) {
            retorno = 16;
        } else if (codSitTrib == 90) {
            retorno = 8;
        } else {
            retorno = 8;
        }
        
        return retorno;
    }
    
}