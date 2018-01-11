package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.OfertaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class SysPdvSqlServerDAO {
    
    //CARREGAMENTOS
    
    public List<ProdutoVO> carregarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        int mercadologico1 = 0, mercadologico2 = 0, mercadologico3 = 0;
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            stmPostgres = Conexao.createStatement();
            
            sql = new StringBuilder();
            sql.append("select p.procod, p.seccod, ");
            sql.append("p.grpcod, p.sgrcod ");
            sql.append("from produto p ");
            sql.append("where cast(p.procod as numeric(14,0)) > 0 ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod").trim());
                
                if ((rst.getString("seccod") != null) &&
                        (!rst.getString("seccod").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));                    
                } else {
                    mercadologico1 = -1;
                }
                
                if ((rst.getString("grpcod") != null) &&
                        (!rst.getString("grpcod").trim().isEmpty())) {
                    
                    if ("000".equals(rst.getString("grpcod").trim())) {
                        mercadologico2 = 1;
                    } else {
                        mercadologico2 = Integer.parseInt(rst.getString("grpcod"));
                    }
                    
                } else {
                    mercadologico2 = -1;
                }

                if ((rst.getString("sgrcod") != null) &&
                        (!rst.getString("sgrcod").trim().isEmpty())) {
                    
                    if ("000".equals(rst.getString("sgrcod").trim())) {
                        mercadologico3 = 1;
                    } else {
                        mercadologico3 = Integer.parseInt(rst.getString("sgrcod"));
                    }
                    
                } else {
                    mercadologico3 = -1;
                }
                
                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {
                    
                    sql = new StringBuilder();
                    sql.append("select max(mercadologico1) as mercadologico1 ");
                    sql.append("from mercadologico ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        mercadologico1 = rstPostgres.getInt("mercadologico1");
                        mercadologico2 = 1;
                        mercadologico3 = 1;                     
                    }
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                
                vProduto.add(oProduto);
                
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
        
    }
    
    public List<ProdutoVO> carregarTipoEmbalagemSysPdv() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        double idProduto;
        int idTipoEmbalagem = 0, qtdEmbalagem = 1;
        boolean pesavel = false, aceitaMultipicacaoPdv = true;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select procod, proenvbal, prounid, PROPESVAR ");
            sql.append("from produto ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod").trim());
                
                if ((rst.getString("PROPESVAR") != null) &&
                        (!rst.getString("PROPESVAR").trim().isEmpty())) {
                    
                    if ("N".equals(rst.getString("PROPESVAR").trim())) {                        
                        idTipoEmbalagem = 0;
                        pesavel = false;
                        aceitaMultipicacaoPdv = true;                        
                    } else if ("S".equals(rst.getString("PROPESVAR").trim())) {                        
                        idTipoEmbalagem = 4;
                        pesavel = false;
                        aceitaMultipicacaoPdv = true;                        
                    } else if ("P".equals(rst.getString("PROPESVAR").trim())) {                        
                        idTipoEmbalagem = 0;
                        pesavel = true;
                        aceitaMultipicacaoPdv = true;
                    } else if ("U".equals(rst.getString("PROPESVAR").trim())) {                        
                        idTipoEmbalagem = 0;
                        pesavel = false;
                        aceitaMultipicacaoPdv = false;
                    }
                    
                } else {
                    idTipoEmbalagem = 0;
                    pesavel = false;
                    aceitaMultipicacaoPdv = true;
                }
                
                ProdutoVO oProduto = new ProdutoVO();                
                oProduto.idDouble = idProduto;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                oProduto.pesavel = pesavel;
                oProduto.aceitaMultiplicacaoPdv = aceitaMultipicacaoPdv;
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
        
    }
    
    public List<ProdutoVO> carregarProdutoFamiliaSysPdv() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        int idFamiliaProduto;
        double idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select procodsim, procod ");
            sql.append("from item_similares ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                if (!".650".equals(rst.getString("procodsim").trim())) {
                    idFamiliaProduto = Integer.parseInt(rst.getString("procodsim").trim());
                } else {
                    idFamiliaProduto = -1;
                }

                idProduto = Double.parseDouble(rst.getString("procod").trim());
                
                ProdutoVO oProduto = new ProdutoVO();
                
                oProduto.idDouble = idProduto;
                oProduto.idFamiliaProduto = idFamiliaProduto;
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public List<ClientePreferencialVO> carregarClienteSysPdv(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
                   dataResidencia,  dataCadastro , numero, complemento, dataNascimento, nomePai, nomeMae,
                   telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                   conjuge = "", orgaoExp = "";
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id_situacaocadastro, Linha=0,
                estadoCivil = 0;
            long id, cnpj, cep;
            double limite, salario;
            boolean bloqueado;

            try {
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select clicod,clides,cliend,clicpfcgc,clibai,clitel, ");
                sql.append("clicep,clicid,clinum,clicmp,cliest,clilimcre,clilimutl,clirgcgf, ");
                sql.append("clidtcad,clidtnas,clidtblo,clipai,climae,clipfpj,clitel2,clifax, ");
                sql.append("clicon,cliendcob,clibaicob,clicepcob,clicidcob,clinumcob,cliestcob, ");
                sql.append("cliobs,cliemail,clisex,cliemptrb,cliemptel,cliempcar,cliempend, ");
                sql.append("clisal,cliestciv,clicjg,clirgexp,clipais,clicodigoibge ");
                sql.append("from cliente ");
                sql.append("where CAST(clicod AS BIGINT) > 0 ");

                rst = stm.executeQuery(sql.toString());
                Linha=1;
                try{
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        id = rst.getLong("clicod");
                        id_situacaocadastro = 1;
                        dataResidencia = "1990/01/01";

                        if ((rst.getString("clipfpj") != null) &&
                                (!rst.getString("clipfpj").trim().isEmpty())) {
                            if ("F".equals(rst.getString("clipfpj").trim())) {
                                id_tipoinscricao = 1;
                            } else if ("J".equals(rst.getString("clipfpj").trim())) {
                                id_tipoinscricao = 0;
                            }
                        } else {
                            id_tipoinscricao = 1;
                        }

                        if ((rst.getString("clides") != null) &&
                                (!rst.getString("clides").trim().isEmpty())) {
                            byte[] bytes = rst.getBytes("clides");
                            String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                            nome = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        
                        if ((rst.getString("cliend") != null) &&
                                (!rst.getString("cliend").trim().isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("cliend").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }
                        
                        if ((rst.getString("clicpfcgc") != null) &&
                                (!rst.getString("clicpfcgc").trim().isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("clicpfcgc").trim()));
                        } else {
                            cnpj = id;
                        }
                        
                        if ((rst.getString("clibai") != null) &&
                                (!rst.getString("clibai").trim().isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("clibai").trim().replace("'", ""));
                        } else {
                            bairro = "";
                        }
                        
                        if ((rst.getString("clitel") != null) &&
                                (!rst.getString("clitel").trim().isEmpty())) {
                            telefone1 = util.formataNumero(rst.getString("clitel").trim());
                        } else {
                            telefone1 = "0";
                        }
                        
                        if ((rst.getString("clicep") != null) &&
                                (!rst.getString("clicep").trim().isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("clicep").trim()));
                        } else {
                            cep = 0;
                        }
                        
                        if ((rst.getString("clicid") != null) &&
                                (!rst.getString("clicid").trim().isEmpty())) {
                            if ((rst.getString("cliest") != null) &&
                                    (!rst.getString("cliest").trim().isEmpty())) {
                                id_municipio = util.retornarMunicipioIBGEDescricao(rst.getString("clicid").trim().replace("'", ""), 
                                        rst.getString("cliest").trim().replace("'", ""));
                                
                                if (id_municipio == 0) {
                                    id_municipio = 2304400;
                                }
                            } else {
                                id_municipio = 2304400;
                            }
                        } else {
                            id_municipio = 2304400;
                        }
                        
                        if ((rst.getString("cliest") != null) &&
                                (!rst.getString("cliest").trim().isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(
                            rst.getString("cliest").trim().replace("'", ""));
                            
                            if (id_estado == 0) {
                                id_estado = 23;
                            } else {
                                id_estado = 23;
                            }
                        } else {
                            id_estado = 23;
                        }
      
                        if ((rst.getString("clinum") != null) &&
                                (!rst.getString("clinum").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("clinum").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        if ((rst.getString("clicmp") != null) &&
                                (!rst.getString("clicmp").trim().isEmpty())) {
                            complemento = util.acertarTexto(rst.getString("clicmp").trim().replace("'", ""));
                        } else {
                            complemento = "";
                        }
                        
                        if ((rst.getString("clilimcre") != null) &&
                                (!rst.getString("clilimcre").trim().isEmpty())) {
                            limite = Double.parseDouble(rst.getString("clilimcre").trim());
                        } else {
                            limite = 0;
                        }
                        
                        if ((rst.getString("clirgcgf") != null) &&
                                (!rst.getString("clirgcgf").trim().isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("clirgcgf").trim());
                            inscricaoestadual = inscricaoestadual.replace("'", "");
                            inscricaoestadual = inscricaoestadual.replace("-", "");
                            inscricaoestadual = inscricaoestadual.replace(".", "");
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                        
                        if ((rst.getString("clidtcad") != null) &&
                                (!rst.getString("clidtcad").trim().isEmpty())) {
                            dataCadastro = rst.getString("clidtcad").substring(0, 10).trim().replace("-", "/");
                        } else {
                            dataCadastro = "";
                        }
                        //dataCadastro = dataCadastro+"/"+rst.getString("clidtcad").substring(3, 5).trim();
                        //dataCadastro = dataCadastro+"/"+rst.getString("clidtcad").substring(0, 2).trim();
                        
                        if ((rst.getString("clidtnas") != null) &&
                                (!rst.getString("clidtnas").trim().isEmpty())) {
                            dataNascimento = rst.getString("clidtnas").substring(0, 10).trim().replace("-", "/");
                        } else {
                            dataNascimento = null;
                        }
                        
                        if ((rst.getString("clidtblo") != null) &&
                                (!rst.getString("clidtblo").trim().isEmpty())) {
                            bloqueado = true;
                        } else {
                            bloqueado = false;
                        }
                        
                        if ((rst.getString("clipai") != null) &&
                                (!rst.getString("clipai").trim().isEmpty())) {
                            nomePai = util.acertarTexto(rst.getString("clipai").trim().replace("'", ""));
                        } else {
                            nomePai = "";
                        }
                        
                        if ((rst.getString("climae") != null) &&
                                (!rst.getString("climae").trim().isEmpty())) {
                            nomeMae = util.acertarTexto(rst.getString("climae").trim().replace("'", ""));
                        } else {
                            nomeMae = "";
                        }
                        
                        if ((rst.getString("clitel2") != null) &&
                                (!rst.getString("clitel2").trim().isEmpty())) {
                            telefone2 = util.formataNumero(rst.getString("clitel2").trim());
                        } else {
                            telefone2 = "";
                        }
                        
                        if ((rst.getString("clifax") != null) &&
                                (!rst.getString("clifax").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("clifax").trim());
                        } else {
                            fax = "";
                        }
                        
                        if ((rst.getString("cliobs") != null) &&
                                (!rst.getString("cliobs").trim().isEmpty())) {
                            observacao = util.acertarTexto(rst.getString("cliobs").replace("'", "").trim());
                        } else {
                            observacao = "";
                        }
                        
                        if ((rst.getString("cliemail") != null) &&
                                (!rst.getString("cliemail").trim().isEmpty()) &&
                                (rst.getString("cliemail").contains("@"))) {
                            email = util.acertarTexto(rst.getString("cliemail").trim().replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("clisex") != null) &&
                                (!rst.getString("clisex").trim().isEmpty())) {
                            if ("F".equals(rst.getString("clisex").trim())) {
                                id_sexo = 0;
                            } else {
                                id_sexo = 1;
                            }
                        } else {
                            id_sexo = 1;
                        }
                        
                        if ((rst.getString("cliemptrb") != null) &&
                                (!rst.getString("cliemptrb").trim().isEmpty())) {
                            empresa = util.acertarTexto(rst.getString("cliemptrb").trim().replace("'", ""));
                        } else {
                            empresa = "";
                        }
                        
                        if ((rst.getString("cliemptrb") != null) &&
                                (!rst.getString("cliemptrb").trim().isEmpty())) {
                            telEmpresa = util.formataNumero(rst.getString("cliemptrb").trim());
                        } else {
                            telEmpresa = "";
                        }
                        
                        if ((rst.getString("cliempcar") != null) &&
                                (!rst.getString("cliempcar").trim().isEmpty())) {
                            cargo = util.acertarTexto(rst.getString("cliempcar").replace("'", "").trim());
                        } else {
                            cargo = "";
                        }
                        
                        if ((rst.getString("cliempend") != null) &&
                                (!rst.getString("cliempend").trim().isEmpty())) {
                            enderecoEmpresa = util.acertarTexto(rst.getString("cliempend").trim().replace("'", ""));
                        } else {
                            enderecoEmpresa = "";
                        }
                        
                        if ((rst.getString("clisal") != null) &&
                                (!rst.getString("clisal").trim().isEmpty())) {
                            salario = Double.parseDouble(rst.getString("clisal").replace(".", "").replace(",", "."));
                        } else {
                            salario = 0;
                        }
                        
                        if ((rst.getString("cliestciv") != null) &&
                                (!rst.getString("cliestciv").trim().isEmpty())) {
                            if ("C".equals(rst.getString("cliestciv").trim())) {
                                estadoCivil = 2;
                            } else if ("S".equals(rst.getString("cliestciv").trim())) {
                                estadoCivil = 1;
                            } else if ("O".equals(rst.getString("cliestciv").trim())) {
                                estadoCivil = 5;
                            }
                        } else {
                            estadoCivil = 0;
                        }
                        
                        if ((rst.getString("clicjg") != null) &&
                                (!rst.getString("clicjg").trim().isEmpty())) {
                            conjuge = util.acertarTexto(rst.getString("clicjg").trim().replace("'", ""));
                        } else {
                            conjuge = "";
                        }
                        
                        if ((rst.getString("clirgexp") != null) &&
                                (!rst.getString("clirgexp").trim().isEmpty())) {
                            orgaoExp = util.acertarTexto(rst.getString("clirgexp").replace("'", "").trim());
                        } else {
                            orgaoExp = "";
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

                        oClientePreferencial.id = 0;
                        oClientePreferencial.idLong = id;
                        oClientePreferencial.setCodigoanterior(id);
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

    public List<FornecedorVO> carregarFornecedorSysPdv() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                   numero = "", complemento = "", telefone = "", email = "", fax = "";
            int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            Long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;

            try {
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select forcod, fordes, forend, forbai, forcid, forest, ");
                sql.append("fortel, forfax, forcep, fornum, forcmp, forcon, forobs, ");
                sql.append("forfan, forcgc, forcgf, foremail, forpfpj, forpais, forcodibge ");
                sql.append("from fornecedor  ");
                sql.append("order by fordes ");

                rst = stm.executeQuery(sql.toString());

                Linha=0;
                
                try{
                    while (rst.next()) {                    
                        FornecedorVO oFornecedor = new FornecedorVO();

                        id = rst.getInt("forcod");

                        Linha++; 
                        if (Linha==3){
                            Linha--;
                            Linha++;                        
                        }                    
                        if ((rst.getString("fordes") != null)
                                && (!rst.getString("fordes").isEmpty())) {
                           byte[] bytes = rst.getBytes("fordes");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "";
                        }

                        if ((rst.getString("forfan") != null)
                                && (!rst.getString("forfan").isEmpty())) {
                           byte[] bytes = rst.getBytes("forfan");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "";
                        }

                        if ((rst.getString("forcgc") != null)
                                && (!rst.getString("forcgc").isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("forcgc").trim()));
                        } else {
                            cnpj = Long.parseLong(rst.getString("forcod"));
                        }

                        if ((rst.getString("forcgf") != null)
                                && (!rst.getString("forcgf").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("forcgf").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        id_tipoinscricao = 0;

                        if ((rst.getString("forend") != null)
                                && (!rst.getString("forend").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("forend").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("forbai") != null)
                                && (!rst.getString("forbai").isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("forbai").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("forcep") != null)
                                && (!rst.getString("forcep").isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("forcep").trim()));
                        } else {
                            cep = Long.parseLong("0");
                        }

                        if ((rst.getString("forcid") != null)
                                && (!rst.getString("forcid").isEmpty())) {

                            if ((rst.getString("forest") != null)
                                    && (!rst.getString("forest").isEmpty())) {

                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("forcid").replace("'", "").trim()),
                                        util.acertarTexto(rst.getString("forest").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = 3525508;
                                }
                            }
                        } else {
                            id_municipio = 3525508;
                        }

                        if ((rst.getString("forest") != null)
                                && (!rst.getString("forest").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("forest").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 23;
                            }
                        } else {
                            id_estado = 23;
                        }

                        if (rst.getString("forobs") != null) {
                            obs = rst.getString("forobs").trim();
                        } else {
                            obs = "";
                        }

                        datacadastro = "";

                        pedidoMin = 0;

                        ativo = true;
                        
                        if ((rst.getString("forpfpj") != null) &&
                                (!rst.getString("forpfpj").trim().isEmpty())) {
                            if ("J".equals(rst.getString("forpfpj").trim())) {
                                id_tipoinscricao = 0;
                            } else {
                                id_tipoinscricao = 1;
                            }
                        } else {
                            id_tipoinscricao = 0;
                        }

                        if ((rst.getString("fornum") != null) &&
                                (!rst.getString("fornum").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("fornum").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        if ((rst.getString("forcmp") != null) &&
                                (!rst.getString("forcmp").trim().isEmpty())) {
                            complemento = util.acertarTexto(rst.getString("forcmp").replace("'", "").trim());
                        } else {
                            complemento = "";
                        }
                        
                        if ((rst.getString("fortel") != null) &&
                                (!rst.getString("fortel").trim().isEmpty())) {
                            telefone = util.formataNumero(rst.getString("fortel").trim());
                        } else {
                            telefone = "0";
                        }
                        
                        if (razaosocial.length() > 40) {
                            razaosocial = razaosocial.substring(0, 40);
                        }

                        if (nomefantasia.length() > 30) {
                            nomefantasia = nomefantasia.substring(0, 30);
                        }

                        if ((rst.getString("foremail") != null) &&
                                (!rst.getString("foremail").trim().isEmpty()) &&
                                (rst.getString("foremail").contains("@"))) {
                            email = util.acertarTexto(rst.getString("foremail").replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("forfax") != null) &&
                                (!rst.getString("forfax").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("forfax").trim());
                        } else {
                            fax = "";
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

                        oFornecedor.codigoanterior = rst.getInt("forcod");
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
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorSysPdv() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor;
        double idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select procod, forcod, prfreffor, PRFQTD ");
            sql.append("from produto_fornecedor ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("forcod");
                idProduto = Double.parseDouble(rst.getString("procod"));

                if ((rst.getString("prfreffor") != null)
                        && (!rst.getString("prfreffor").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("prfreffor").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                
                int qtdEmbalagem = (int) rst.getDouble("PRFQTD");

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;               
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;
                oProdutoFornecedor.qtdembalagem = qtdEmbalagem;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarMercadologicoSysPdv(int nivel) throws Exception {
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
            /*sql.append("select m1.seccod, m1.secdes, m2.grpcod, m2.grpdes, m3.sgrcod, m3.sgrdes ");
            sql.append("from secao as m1 ");
            sql.append("inner join grupo as m2 on m2.seccod = m1.seccod ");
            sql.append("inner join subgrupo as m3 on m3.seccod = m1.seccod and m3.grpcod = m2.grpcod ");
            sql.append("order by m1.seccod, m2.grpcod, m3.sgrcod; ");*/
            
            sql.append("select m1.seccod, m1.secdes, m2.grpcod, m2.grpdes, m3.sgrcod, m3.sgrdes ");
            sql.append("from secao as m1 ");
            sql.append("left join grupo as m2 on m2.seccod = m1.seccod ");
            sql.append("left join subgrupo as m3 on m3.seccod = m1.seccod and m3.grpcod = m2.grpcod ");
            sql.append("order by m1.seccod, m2.grpcod, m3.sgrcod ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));
                    
                    if ((rst.getString("secdes") != null) &&
                            (!rst.getString("secdes").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("secdes").replace("'", "").trim());
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
                    
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));
                    
                    if ((rst.getString("grpcod") != null) &&
                            (!rst.getString("grpcod").trim().isEmpty())) {
                        
                        mercadologico2 = Integer.parseInt(rst.getString("grpcod"));
                    } else {
                        mercadologico2 = 1;
                    }
                    
                    if ((rst.getString("grpdes") != null) &&
                            (!rst.getString("grpdes").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("grpdes").replace("'", "").trim());
                    } else {
                        descricao = util.acertarTexto(rst.getString("secdes").trim().replace("'", ""));
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
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));
                    
                    if ((rst.getString("grpcod") != null) &&
                            (!rst.getString("grpcod").trim().isEmpty())) {
                        
                        mercadologico2 = Integer.parseInt(rst.getString("grpcod"));
                    } else {
                        mercadologico2 = 1;
                    }
                    
                    if ((rst.getString("sgrcod") != null) &&
                            (!rst.getString("sgrcod").trim().isEmpty())) {
                        mercadologico3 = Integer.parseInt(rst.getString("sgrcod"));
                    } else {
                        mercadologico3 = 1;
                    }
                    
                    if ((rst.getString("sgrdes") != null) &&
                            (!rst.getString("sgrdes").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("sgrdes").replace("'", "").trim());
                    } else {
                        if ((rst.getString("grpdes") != null) &&
                                (!rst.getString("grpdes").trim().isEmpty())) {
                            
                            descricao = util.acertarTexto(rst.getString("grpdes").replace("'", "").trim());
                        } else {
                            
                            descricao = util.acertarTexto(rst.getString("secdes").replace("'", "").trim());
                        }
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
    
    public List<FamiliaProdutoVO> carregarFamiliaProdutoSysPdv() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        String descricao;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select procodsim, similaresdes ");
            sql.append("from similares ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if (!util.encontrouLetraCampoNumerico(rst.getString("procodsim").trim())) {
                
                    if ((rst.getString("similaresdes") != null)
                            && (!rst.getString("similaresdes").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("similaresdes").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                    oFamiliaProduto.id = Integer.parseInt(rst.getString("procodsim").replace(".", ""));

                    oFamiliaProduto.descricao = descricao;
                    oFamiliaProduto.id_situacaocadastro = 1;
                    oFamiliaProduto.codigoant = 0;

                    vFamiliaProduto.add(oFamiliaProduto);                
                }
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
   
    public Map<Integer, ProdutoVO> carregarProdutoSysPdv() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0;
        
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select p.procod, p.procodint, p.prodes, p.prodesrdz, p.seccod, p.trbid, ");
            sql.append("p.prounid, p.proestmin, p.proestmax, p.grpcod, p.sgrcod, p.proncm, ");
            sql.append("p.natcodigo, COALESCE(pa.procodaux,P.PROCOD) as procodaux, p.proprc1, p.proprccst, p.prodatcadinc, p.proiteemb,  p.promrg1,proprcvdavar, p.prodatforlin ");
            sql.append("from produto p ");
            sql.append("left join produtoaux pa on pa.procod =  p.procod ");
            sql.append("where cast(p.procod as numeric(14,0)) > 0 ");
            sql.append("and cast(p.procod as numeric(14,0)) < 1000000 ");
            sql.append("order by   p.procod, p.proenvbal ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ((rst.getString("prodatcadinc") != null) &&
                        (!rst.getString("prodatcadinc").trim().isEmpty())) {
                    dataCadastro = rst.getString("prodatcadinc").substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }
                
                idSituacaoCadastro = rst.getString("prodatforlin") == null ? 1 : 0;
                codigoAnterior = Double.parseDouble(rst.getString("procod").trim().replace(".", ""));
                idProduto = Integer.parseInt(rst.getString("procod").trim().replace(".", ""));
                
                if ((rst.getString("procodint") != null) &&
                        (!rst.getString("procodint").trim().isEmpty())) {
                    referencia = Integer.parseInt(rst.getString("procodint").replace(".", "").trim());
                } else {
                    referencia = -1;
                }
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where codigo = " + rst.getString("procod").replace(".", ""));

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
                    if ("CX".equals(rst.getString("prounid").trim())) {                        
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("prounid").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("prounid").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                if ((rst.getString("prodes") != null) &&
                        (!rst.getString("prodes").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("prodes");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("prodesrdz") != null) &&
                        (!rst.getString("prodesrdz").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("prodesrdz");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descriaoCompleta;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("proiteemb").replace(",", ""));
                }
                
                idFamilia = -1;
                
                /*if ((rst.getString("seccod") != null) &&
                        (!rst.getString("seccod").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));
                    
                    sql = new StringBuilder();
                    sql.append("select mercadologico1 from mercadologico ");
                    sql.append("where mercadologico1 = "+mercadologico1+" ");
                    sql.append("and nivel = 1");
                    
                } else {
                    mercadologico1 = 65;
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }
                
                if ((rst.getString("grpcod") != null) &&
                        (!rst.getString("grpcod").trim().isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("grpcod"));
                } else {
                    mercadologico1 = 65;
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }

                if ((rst.getString("sgrcod") != null) &&
                        (!rst.getString("sgrcod").trim().isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("sgrcod"));
                } else {
                    mercadologico1 = 65;
                    mercadologico2 = 1;
                    mercadologico3 = 1;                
                }
                
                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {
                    mercadologico1 = 65;
                    mercadologico2 = 1;
                    mercadologico3 = 1;                     
                }*/

                if ((rst.getString("seccod") != null) &&
                        (!rst.getString("seccod").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));                    
                } else {
                    mercadologico1 = -1;
                }
                
                if ((rst.getString("grpcod") != null) &&
                        (!rst.getString("grpcod").trim().isEmpty())) {
                    
                    if ("000".equals(rst.getString("grpcod").trim())) {
                        mercadologico2 = 1;
                    } else {
                        mercadologico2 = Integer.parseInt(rst.getString("grpcod"));
                    }
                    
                } else {
                    mercadologico2 = -1;
                }

                if ((rst.getString("sgrcod") != null) &&
                        (!rst.getString("sgrcod").trim().isEmpty())) {
                    
                    if ("000".equals(rst.getString("sgrcod").trim())) {
                        mercadologico3 = 1;
                    } else {
                        mercadologico3 = Integer.parseInt(rst.getString("sgrcod"));
                    }
                    
                } else {
                    mercadologico3 = -1;
                }
                
                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {
                    
                    sql = new StringBuilder();
                    sql.append("select max(mercadologico1) as mercadologico1 ");
                    sql.append("from mercadologico ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        mercadologico1 = rstPostgres.getInt("mercadologico1");
                        mercadologico2 = 1;
                        mercadologico3 = 1;                     
                    }
                }
                
                if ((rst.getString("proncm") != null) &&
                        (!rst.getString("proncm").isEmpty()) &&
                        (rst.getString("proncm").trim().length() > 5)) {
                    
                    ncmAtual = util.formataNumero(rst.getString("proncm").trim());
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
                    
                    if ((rst.getString("procodaux") != null) &&
                            (!rst.getString("procodaux").trim().isEmpty())) {
                        
                        strCodigoBarras = Utils.formataNumero(rst.getString("procodaux"));
                        
                        if (strCodigoBarras.length() >= 7 && strCodigoBarras.length() <= 14) {   
                            codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("procodaux").trim()));                            
                        } else {
                            if (idProduto >= 10000) {
                                codigoBarras = Utils.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = Utils.gerarEan13(idProduto, false);
                            }
                        }
                    } else {
                        strCodigoBarras = "-2";
                        codigoBarras = -2;
                    }
                }
                
                idTipoPisCofins = 0;
                
                idTipoPisCofinsCredito = 12;
                
                tipoNaturezaReceita = -1;
                
                if ((rst.getString("trbid") != null) &&
                        (!rst.getString("trbid").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSSysPdv(rst.getString("trbid").trim().toUpperCase(), "");
                } else {
                    idAliquota = 8;
                }
                                
                if ((rst.getString("proprcvdavar") != null) &&
                        (!rst.getString("proprcvdavar").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("proprcvdavar").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("proprccst") != null) &&
                        (!rst.getString("proprccst").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("proprccst").replace(",", "."));
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("promrg1") != null) &&
                        (!rst.getString("promrg1").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("promrg1").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                if (descriaoCompleta.length() > 60) {

                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {

                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {

                    descricaoGondola = descricaoGondola.substring(0, 60);
                }
                
                oProduto.id = idProduto;
                oProduto.descricaoCompleta = descriaoCompleta;
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
                oProduto.validade = validade;
                oProduto.margem = margem;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                
                oAliquota.idEstado = 23;
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
                oCodigoAnterior.codigoatual = idProduto;
                
                if(rst.getString("procodaux") != null && 
                        !rst.getString("procodaux").trim().isEmpty() &&
                        rst.getString("procodaux").length() < String.valueOf(Long.MAX_VALUE).length() - 1){
                   oCodigoAnterior.barras = Long.parseLong(rst.getString("procodaux"));
                } else {
                   oCodigoAnterior.barras = 0; 
                }
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                
                oCodigoAnterior.piscofinsdebito = -1;
                
                oCodigoAnterior.piscofinscredito = -1;
                
                if ((rst.getString("trbid") != null) && (!rst.getString("trbid").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = rst.getString("trbid").trim().replace(".", "");
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("proncm") != null) && (!rst.getString("proncm").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("proncm").trim().replace(".", "");
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

    public Map<Double, ProdutoVO> carregarProdutoMaior6SysPdv() throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, i = 0, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, idProduto;
        
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select p.procod, p.procodint, p.prodes, p.prodesrdz, p.seccod, p.trbid,  ");
            sql.append("       p.prounid, p.proestmin, p.proestmax, p.grpcod, p.sgrcod, p.proncm, p.prodatforlin,"); 
            sql.append("       p.natcodigo, COALESCE(pa.procodaux,P.PROCOD) AS procodaux, p.proprc1, p.proprccst, p.prodatcadinc, ");
            sql.append("       p.proiteemb,  p.promrg1,proprcvdavar  ");
            sql.append("from [syspdv].[dbo].[produto] p ");
            sql.append("left join [syspdv].[dbo].[PRODUTOAUX] pa on pa.procod = p.PROCOD ");
            sql.append("where cast(p.procod as numeric(14,0)) >= 1000000 ");
            
            //sql.append(" and  exists(select * from [syspdv].[dbo].[itevda] i where ");
            // sql.append("	  i.procod = p.procod and                   ");
            // sql.append("      i.trndat >= '01/01/2014')             ");
            
            sql.append(" order by p.procod            ");            
            

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ((rst.getString("prodatcadinc") != null) &&
                        (!rst.getString("prodatcadinc").trim().isEmpty())) {
                    dataCadastro = rst.getString("prodatcadinc").substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }                
                idSituacaoCadastro = rst.getString("prodatforlin") == null ? 1 : 0;
                codigoAnterior = Double.parseDouble(rst.getString("procod").trim().replace(".", ""));
                idProduto = Double.parseDouble(rst.getString("procod").trim().replace(".", ""));
                
                if ((rst.getString("procodint") != null) &&
                        (!rst.getString("procodint").trim().isEmpty())) {
                    referencia = Integer.parseInt(rst.getString("procodint").replace(".", "").trim());
                } else {
                    referencia = -1;
                }
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where cast(codigo as numeric(14,0)) = cast("+rst.getString("procod").replace(".", "")+" as numeric(14,0))");

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
                    if ("CX".equals(rst.getString("prounid").trim())) {                        
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("prounid").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("prounid").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 4;
                    }
                }
                
                if ((rst.getString("prodes") != null) &&
                        (!rst.getString("prodes").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("prodes");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("prodesrdz") != null) &&
                        (!rst.getString("prodesrdz").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("prodesrdz");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descriaoCompleta;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("proiteemb").replace(",", ""));
                }
                
                idFamilia = -1;

                if ((rst.getString("seccod") != null) &&
                        (!rst.getString("seccod").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("seccod"));                    
                } else {
                    mercadologico1 = -1;
                }
                
                if ((rst.getString("grpcod") != null) &&
                        (!rst.getString("grpcod").trim().isEmpty())) {
                    
                    if ("000".equals(rst.getString("grpcod").trim())) {
                        mercadologico2 = 1;
                    } else {
                        mercadologico2 = Integer.parseInt(rst.getString("grpcod"));
                    }
                    
                } else {
                    mercadologico2 = -1;
                }

                if ((rst.getString("sgrcod") != null) &&
                        (!rst.getString("sgrcod").trim().isEmpty())) {
                    
                    if ("000".equals(rst.getString("sgrcod").trim())) {
                        mercadologico3 = 1;
                    } else {
                        mercadologico3 = Integer.parseInt(rst.getString("sgrcod"));
                    }
                    
                } else {
                    mercadologico3 = -1;
                }
                
                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {
                    
                    sql = new StringBuilder();
                    sql.append("select max(mercadologico1) as mercadologico1 ");
                    sql.append("from mercadologico ");
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        mercadologico1 = rstPostgres.getInt("mercadologico1");
                        mercadologico2 = 1;
                        mercadologico3 = 1;                     
                    }
                }
                
                if ((rst.getString("proncm") != null) &&
                        (!rst.getString("proncm").isEmpty()) &&
                        (rst.getString("proncm").trim().length() > 5)) {
                    
                    ncmAtual = util.formataNumero(rst.getString("proncm").trim());
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
                    
                    if ((rst.getString("procodaux") != null) &&
                            (!rst.getString("procodaux").trim().isEmpty())) {
                        
                        strCodigoBarras = util.formataNumero(rst.getString("procodaux").replace(".", "").trim());
                        
                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {                            
                            if (idProduto >= 10000) {
                                codigoBarras = util.gerarEan13((int) idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13((int) idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(util.formataNumero(rst.getString("procodaux").trim()));
                        }
                    } else {
                        codigoBarras = Long.parseLong(util.formataNumero(rst.getString("procod").trim()));
                    }
                }               
                
                idTipoPisCofins = 0;
                
                idTipoPisCofinsCredito = 12;
                
                tipoNaturezaReceita = -1;
                
                if ((rst.getString("trbid") != null) &&
                        (!rst.getString("trbid").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSSysPdv(rst.getString("trbid").trim().toUpperCase(), "");
                } else {
                    idAliquota = 8;
                }
                                
                if ((rst.getString("proprcvdavar") != null) &&
                        (!rst.getString("proprcvdavar").trim().isEmpty())) {
                    precoVenda = Double.parseDouble(rst.getString("proprc1").replace(",", "."));
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("proprccst") != null) &&
                        (!rst.getString("proprccst").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("proprccst").replace(",", "."));
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("promrg1") != null) &&
                        (!rst.getString("promrg1").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("promrg1").replace(",", "."));
                } else {
                    margem = 0;
                }
                
                if (descriaoCompleta.length() > 60) {
                    descriaoCompleta = descriaoCompleta.substring(0, 60);
                }

                if (descricaoReduzida.length() > 22) {
                    descricaoReduzida = descricaoReduzida.substring(0, 22);
                }

                if (descricaoGondola.length() > 60) {
                    descricaoGondola = descricaoGondola.substring(0, 60);
                }
                
                oProduto.idDouble = idProduto;
                oProduto.descricaoCompleta = descriaoCompleta;
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
                
                oAliquota.idEstado = 23;
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
                oCodigoAnterior.codigoatual = idProduto;
                
                if((rst.getString("procodaux")!=null) && (!rst.getString("procodaux").trim().isEmpty())){
                   oCodigoAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("procodaux").replace(".", "").trim()));
                } else {
                   oCodigoAnterior.barras = 0; 
                }
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                
                oCodigoAnterior.piscofinsdebito = -1;
                
                oCodigoAnterior.piscofinscredito = -1;
                
                if ((rst.getString("trbid") != null) && (!rst.getString("trbid").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = rst.getString("trbid").trim().replace(".", "");
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";
                }
                
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                oCodigoAnterior.custosemimposto = -1;
                oCodigoAnterior.custocomimposto = -1;
                oCodigoAnterior.margem = -1;
                oCodigoAnterior.precovenda = -1;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("proncm") != null) && (!rst.getString("proncm").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("proncm").trim().replace(".", "");
                } else {
                    oCodigoAnterior.ncm = "";
                }
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                vProduto.put(idProduto, oProduto);
                
                i  = i + 1;
            }
            
            stmPostgres.close();
            Conexao.commit();
            return vProduto;
            
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public Map<Double, ProdutoVO> carregarCustoProdutoSysPdv(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select p.procod, p.proprccst ");
            sql.append("from [syspdv].[dbo].[produto] p ");
            //sql.append("where exists(select * from [syspdv].[dbo].[itevda] i where ");
            //sql.append("      i.procod = p.procod and ");                         
            //sql.append("      i.trndat >= '01/01/2014')  ");            
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod").replace(".", ""));
                
                if ((rst.getString("proprccst") != null) &&
                        (!rst.getString("proprccst").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("proprccst").replace(",", "."));                    
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
    
    private List<ProdutoVO> carregarDataCadastroProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0;
        String dataCadastro = "";
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select procod, prodatcadinc ");
            sql.append("from PRODUTO ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod").trim());
                
                if ((rst.getString("prodatcadinc") != null) &&
                        (!rst.getString("prodatcadinc").trim().isEmpty())) {
                    dataCadastro = rst.getString("prodatcadinc").trim().substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.dataCadastro = dataCadastro;
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
       
    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco = 0, cheque, idTipoInscricao = 0, id_tipoalinea, ecf = 0;
        double valor, juros;
        long cpfCnpj = 0;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia = null, conta = null, nome, rg, telefone;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            
            rst = stm.executeQuery(
                "SELECT\n" +
                "  R.CXANUM,\n" +
                "  R.CTRNUM,\n" +
                "  R.CTRDATEMI,\n" +
                "  R.CTRDATVNC,\n" +
                "  R.CTROBS,\n" +
                "  R.CTRNUMBCO,\n" +
                "  c.CLICOD,\n" +
                "  C.CLIDES,\n" +
                "  C.CLICPFCGC,\n" +
                "  R.CTRVLRDEV,\n" +
                "  C.CLIRGCGF,\n" +
                "  C.CLITEL,\n" +
                "  C.CLIPFPJ\n" +
                "FROM CONTARECEBER R\n" +
                "	INNER JOIN CLIENTE C ON C.CLICOD = R.CLICOD\n" +
                "WHERE \n" +
                "	(COALESCE(FZDCOD,'000') IN ('002', '003')) AND\n" +
                "	(COALESCE(CTRVLRPAG,0) < CTRVLRNOM) AND \n" +
                "	COALESCE(ctrvlrdev,0) > 0\n" +
                "order by clides"
            );

            while (rst.next()) {

                if ((rst.getString("CLICPFCGC") != null) &&
                        (!rst.getString("CLICPFCGC").trim().isEmpty())) {
                    cpfCnpj = Long.parseLong(rst.getString("CLICPFCGC").trim());
                } else {
                    cpfCnpj = 0;
                }
                
                if ((rst.getString("CLIPFPJ") != null) &&
                        (!rst.getString("CLIPFPJ").trim().isEmpty())) {
                    
                    if ("J".equals(rst.getString("CLIPFPJ").trim())) {
                        idTipoInscricao = 0;
                    } else if ("F".equals(rst.getString("CLIPFPJ").trim())) {
                        idTipoInscricao = 1;
                    }
                } else {
                    idTipoInscricao = 1;
                }
                
                
                idBanco = 804;

                //if ((rst.getString("agenci") != null) &&
                //        (!rst.getString("agenci").trim().isEmpty())) {
                //    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                //} else {
                    agencia = "";
                //}
                
                //if ((rst.getString("contax") != null) &&
                //        (!rst.getString("contax").trim().isEmpty()))  {
                //    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                //} else {
                    conta = "";
                
           
                
                //if ((rst.getString("cheque") != null) &&
                //        (!rst.getString("cheque").trim().isEmpty())) {
                //    
                //    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                //    
                //    if (String.valueOf(cheque).length() > 10) {
                //        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                //    }
                //} else {
                    cheque = 0;
                //}
                                      
                if ((rst.getString("CTRDATEMI") != null) &&
                        (!rst.getString("CTRDATEMI").trim().isEmpty())) {
                
                    dataemissao = rst.getString("CTRDATEMI").trim().substring(0, 10)/*.replace("-", "/")*/;
                } else {
                    dataemissao = "2016/05/20";
                }
                
                if ((rst.getString("CTRDATVNC") != null) &&
                        (!rst.getString("CTRDATVNC").trim().isEmpty())) {
                
                    datavencimento = rst.getString("CTRDATVNC").trim().substring(0, 10)/*.replace("-", "/")*/;
                } else {
                    datavencimento = "2016/06/20";
                }
                
                if ((rst.getString("CLIDES") != null) &&
                        (!rst.getString("CLIDES").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("CLIDES").replace("'", "").trim());
                } else {
                    nome = "";
                }
                
                if ((rst.getString("CLIRGCGF") != null) &&
                        (!rst.getString("CLIRGCGF").isEmpty())) {
                    rg = util.acertarTexto(rst.getString("CLIRGCGF").trim().replace("'", ""));
                    
                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {
                    rg = "";
                }
                
                valor = Double.parseDouble(rst.getString("CTRVLRDEV"));
                
                
                long cupom = Long.parseLong(Utils.formataNumero(rst.getString("ctrnum")));
                if (cupom <= Integer.MAX_VALUE) {
                    numerocupom = (int) cupom;
                } else {
                    numerocupom = 0;
                }
                
                
                juros = 0;

                if ((rst.getString("CTROBS") != null)
                        && (!rst.getString("CTROBS").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("CTROBS").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("CLITEL") != null) &&
                        (!rst.getString("CLITEL").isEmpty()) &&
                        (!"0".equals(rst.getString("CLITEL").trim()))) {
                    telefone = util.formataNumero(rst.getString("CLITEL"));
                } else {
                    telefone = "";
                }
                    
                //if (rst.getInt("status")==1){
                id_tipoalinea = 0;
                //} else if (rst.getInt("status")==2){
                //    id_tipoalinea = 15;                    
                //} else {
                //    id_tipoalinea = 0;
                //}
                    
                if ((rst.getString("CXANUM") != null) &&
                        (!rst.getString("CXANUM").trim().isEmpty())) {
                    ecf = Integer.parseInt(Utils.formataNumero(rst.getString("CXANUM").trim()));
                } else {
                    ecf = 0;
                }
                
                
                ReceberChequeVO oReceberCheque = new ReceberChequeVO();                
                oReceberCheque.id_loja = id_loja;
                oReceberCheque.ecf = ecf;
                oReceberCheque.id_tipoalinea = id_tipoalinea;
                oReceberCheque.data = dataemissao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.cpf = cpfCnpj;
                oReceberCheque.numerocheque = cheque;
                oReceberCheque.id_banco = idBanco;
                oReceberCheque.agencia = agencia;
                oReceberCheque.conta = conta;
                oReceberCheque.numerocupom = numerocupom;
                oReceberCheque.valor = valor;
                oReceberCheque.observacao = observacao;
                oReceberCheque.rg = rg;
                oReceberCheque.telefone = telefone;
                oReceberCheque.nome = nome;
                oReceberCheque.id_tipoinscricao = idTipoInscricao;
                oReceberCheque.datadeposito = datavencimento;
                oReceberCheque.valorjuros = juros;
                oReceberCheque.valorinicial = valor;

                vReceberCheque.add(oReceberCheque);
            }
            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteSysPdv(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int numerocupom, ecf;
        long id_cliente;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();

            rst = stm.executeQuery(
                "SELECT\n" +
                "	CTRID,\n" +
                "	ctrnum,\n" +
                "	clicod,\n" +
                "	cxanum,\n" +
                "	ctrdatemi,\n" +
                "	ctrdatvnc,\n" +
                "	ctrvlrdev,\n" +
                "	ctrobs\n" +
                "FROM CONTARECEBER\n" +
                "WHERE \n" +
                "	(NOT COALESCE(FZDCOD,'000') IN ('002', '003')) AND\n" +
                "	(COALESCE(CTRVLRPAG,0) < CTRVLRNOM) AND \n" +
                "	COALESCE(ctrvlrdev,0) > 0"
            );
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getLong("clicod");                
                dataemissao = rst.getString("ctrdatemi").substring(0, 10).trim();
                datavencimento = rst.getString("ctrdatvnc").substring(0, 10).trim();
                
                long cupom = Long.parseLong(Utils.formataNumero(rst.getString("ctrnum")));
                if (cupom <= Integer.MAX_VALUE) {
                    numerocupom = (int) cupom;
                } else {
                    numerocupom = 0;
                }
                
                
                valor = Double.parseDouble(rst.getString("ctrvlrdev"));
                juros = 0;

                if ((rst.getString("cxanum") != null) &&
                        (!rst.getString("cxanum").trim().isEmpty())) {
                    ecf = Integer.parseInt(rst.getString("cxanum").trim());
                } else {
                    ecf = 0;
                }
                
                if ((rst.getString("ctrobs") != null) &&
                        (!rst.getString("ctrobs").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("ctrobs").replace("'", "").trim());
                } else { 
                    observacao = "IMPORTADO VR";
                }
                
                oReceberCreditoRotativo.id_clientepreferencial = 0;
                oReceberCreditoRotativo.idClientePreferencialLong = id_cliente;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
                oReceberCreditoRotativo.ecf = ecf;
                oReceberCreditoRotativo.observacao = observacao;
                oReceberCreditoRotativo.datavencimento = datavencimento;
                oReceberCreditoRotativo.valorjuros = juros;
                
                vReceberCreditoRotativo.add(oReceberCreditoRotativo);
                
            }
            
            return vReceberCreditoRotativo;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }    

    public Map<Double, ProdutoVO> carregarPrecoProdutoSysPdv(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double preco = 0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select p.procod, p.proprcvdavar ");
            sql.append("from [syspdv].[dbo].[produto] p ");     

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod"));
                
                if ((rst.getString("proprcvdavar") != null) &&
                        (!rst.getString("proprcvdavar").trim().isEmpty())) {
                    preco = Double.parseDouble(rst.getString("proprcvdavar").replace(",", "."));
                } else {
                    preco = 0;
                }
                

                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                
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
    
    public Map<Double, ProdutoVO> carregarEstoqueProdutoSysPdv(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double saldo = 0, idProduto;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();            
            sql.append("select procod, estatu ");
            sql.append("from estoque ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod"));
                
                if ((rst.getString("estatu") != null) &&
                        (!rst.getString("estatu").trim().isEmpty()) &&
                        (rst.getDouble("estatu") < 100000000000.0)) {
                    saldo = rst.getDouble("estatu");
                } else {
                    saldo = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.setEstoque(saldo);
                oProduto.vComplemento.add(oComplemento);                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.setEstoque(saldo);
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
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    procod,\n" +
                    "    procodaux\n" +
                    "from\n" +
                    "    produtoaux\n" +
                    "where\n" +
                    "    not procod is null and\n" +
                    "    not procodaux is null\n" +
                    "order by\n" +
                    "    procod,\n" +
                    "    procodaux"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    procod,\n" +
                    "    procodaux\n" +
                    "from\n" +
                    "    produtoaux\n" +
                    "where\n" +
                    "    not procod is null and\n" +
                    "    not procodaux is null\n" +
                    "order by\n" +
                    "    procod,\n" +
                    "    procodaux"
            )) {
            
                while (rst.next()) {

                    double idProduto = rst.getDouble("procod");

                    String strCodigo = Utils.formataNumero(rst.getString("procodaux"));

                    long codigobarras = -2;
                    if (!strCodigo.equals("") && strCodigo.length() <= 14) {
                        codigobarras = Long.parseLong(strCodigo);
                    } else {
                        codigobarras = 0;
                    }

                    if (String.valueOf(codigobarras).length() >= 7) {
                        if (String.valueOf(codigobarras).length() > 14) {
                            codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                        }                    

                        ProdutoVO oProduto = new ProdutoVO();

                        oProduto.idDouble = idProduto;
                        oProduto.setId((int) idProduto);

                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                        oAutomacao.setCodigoBarras(codigobarras);

                        oProduto.vAutomacao.add(oAutomacao);

                        vProduto.put(codigobarras, oProduto);
                    }
                }                 
            }
        }
            
        return vProduto;           
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
                    idTipoPisCofins = 1;
                }
                
                if ((rst.getString("IMPFEDST") != null) &&
                        (!rst.getString("IMPFEDST").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("IMPFEDST").trim()));
                } else {
                    idTipoPisCofinsCredito = 13;
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
                
                CodigoAnteriorVO ant = new CodigoAnteriorVO();
                if ((rst.getString("IMPFEDST") != null) &&
                        (!rst.getString("IMPFEDST").trim().isEmpty())) {
                    ant.piscofinscredito = Integer.parseInt(rst.getString("IMPFEDST").trim());
                } else {
                    ant.piscofinscredito = -1;
                }
                if ((rst.getString("IMPFEDSTSAI") != null) &&
                        (!rst.getString("IMPFEDSTSAI").trim().isEmpty())) {
                    ant.piscofinsdebito = Integer.parseInt(rst.getString("IMPFEDSTSAI").trim());
                } else {
                    ant.piscofinsdebito = -1;
                }
                ant.naturezareceita = tipoNaturezaReceita;                
                oProduto.vCodigoAnterior.add(ant);
                
                vProdutoPisCofins.add(oProduto);
            }
            
            return vProdutoPisCofins;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarIcmsProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        int idAliquota = 0;
        
        String uf = "SP";
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "  e.sigla uf\n" +
                "from \n" +
                "  fornecedor f\n" +
                "  join estado e on f.id_estado = e.id  \n" +
                "where f.id = 1"
            )) {
                if (rst.next()) {
                    uf = rst.getString("uf");
                }
            }
        }
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    p.procod,\n" +
                "    t.trbtabb cst,\n" +
                "    t.trbalq aliq,\n" +
                "    t.trbred red\n" +
                "from\n" +
                "    produto p\n" +
                "    join tributacao t on\n" +
                "        p.trbid = t.trbid\n" +
                "order by\n" +
                "    p.procod"
            )) {
                while (rst.next()) {

                    if ((rst.getString("procod") != null) &&
                            (!rst.getString("procod").trim().isEmpty())) {

                        idProduto = Double.parseDouble(rst.getString("procod").trim());                 

                        
                        idAliquota = Utils.getAliquotaICMS(uf, rst.getInt("cst"), rst.getDouble("aliq"), rst.getDouble("red"));

                        ProdutoVO oProduto = new ProdutoVO();
                        oProduto.idDouble = idProduto;

                        ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                        oAliquota.idEstado = Utils.getEstadoPelaSigla(uf);
                        oAliquota.idAliquotaDebito = idAliquota;
                        oAliquota.idAliquotaCredito = idAliquota;
                        oAliquota.idAliquotaDebitoForaEstado = idAliquota;
                        oAliquota.idAliquotaCreditoForaEstado = idAliquota;
                        oAliquota.idAliquotaDebitoForaEstadoNF = idAliquota;
                        oProduto.vAliquota.add(oAliquota);                    

                        CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();

                        if ((rst.getString("cst") != null) &&
                                (!rst.getString("cst").trim().isEmpty())) {
                            oAnterior.ref_icmsdebito = rst.getString("cst").trim();
                        } else {
                            oAnterior.ref_icmsdebito = "";
                        }

                        oProduto.vCodigoAnterior.add(oAnterior);

                        vProduto.add(oProduto);
                    }
                }
            }           
        } 
        
        return vProduto;
    }
    
    //IMPORTAÇÕES
    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Clientes...");
                List<ClientePreferencialVO> vClientePreferencial = carregarClienteSysPdv(idLoja, idLojaCliente);
                new PlanoDAO().salvar(idLoja);
                new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

            } catch (Exception ex) {

                throw ex;
            }
        }  

    public void importarFornecedor() throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Fornecedor...");
                List<FornecedorVO> vFornecedor = carregarFornecedorSysPdv();

                new FornecedorDAO().salvar(vFornecedor);

            } catch (Exception ex) {

                throw ex;
            }
        }
    
    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoSysPdv();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoSysPdv(1);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoSysPdv(2);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoSysPdv(3);
            new MercadologicoDAO().salvar(vMercadologico, false);
            
            //new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {

            throw ex;
        }
    }   
    
    private List<ProdutoVO> carregarProdutos(int idLojaVR, int idLojaCliente) throws Exception{
        List<ProdutoVO> result = new ArrayList();
        String ufEmpresa = "SP";
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select PRPUF from PROPRIO WHERE PRPCOD = " + idLojaCliente
            )) {
                if (rst.next()) {
                    ufEmpresa = rst.getString("PRPUF");
                }
            }
        }
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n" +
                    "  p.procod,\n" +
                    "  p.procodint,\n" +
                    "  p.prodes,\n" +
                    "  p.prodesrdz,\n" +
                    "  p.seccod,\n" +
                    "  p.trbid,\n" +
                    "  p.prounid,\n" +
                    "  p.proestmin,\n" +
                    "  p.proestmax,\n" +
                    "  p.grpcod,\n" +
                    "  p.sgrcod,\n" +
                    "  p.proncm,\n" +
                    "  p.proforlin,\n" +
                    "  p.natcodigo,\n" +
                    "  pa.procodaux,\n" +
                    "  p.proprc1,\n" +
                    "  p.proprccst,\n" +
                    "  p.prodatcadinc,\n" +
                    "  p.proiteemb,\n" +
                    "  p.promrg1,\n" +
                    "  proprcvdavar,\n" +
                    "  items.procodsim,\n" +
                    "  p.procest\n," +
                    "  p.propesbrt\n," +
                    "  p.propesliq\n" +
                    "FROM produto p\n" +
                    "LEFT JOIN produtoaux pa\n" +
                    "  ON pa.procod = p.procod\n" +
                    "LEFT JOIN item_similares items\n" +
                    "  ON items.procod = p.procod\n" +
                    "ORDER BY cast(p.procod as bigint)"
            )) {                
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();  
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    
                    oProduto.getvCodigoAnterior().add(oCodigoAnterior); 
                    oProduto.getvAutomacao().add(oAutomacao);
                    oProduto.getvComplemento().add(oComplemento);
                    oProduto.getvAliquota().add(oAliquota);
                    
                    //<editor-fold defaultstate="expanded" desc="Tratando o id do produto">
                    long id = Utils.stringToLong(rst.getString("procod"));
                    //</editor-fold>
                    
                    oProduto.idDouble = id;
                    oProduto.setDescricaoCompleta(rst.getString("prodes"));
                    oProduto.setDescricaoReduzida(rst.getString("prodesrdz"));
                    oProduto.reajustarDescricoes();
                    oProduto.setIdSituacaoCadastro(Utils.acertarTexto(rst.getString("proforlin")).equals("N") ? 1 : 0);
                    if (rst.getString("prodatcadinc") != null) {
                        oProduto.setDataCadastro(Util.formatDataGUI(rst.getDate("prodatcadinc")));
                    } else {
                        oProduto.setDataCadastro(Util.formatDataGUI(new Date(new java.util.Date().getTime())));
                    }
                    
                    oProduto.setMercadologico1(Integer.parseInt(Utils.formataNumero(rst.getString("seccod"))));
                    oProduto.setMercadologico2(Integer.parseInt(Utils.formataNumero(rst.getString("grpcod"))));
                    oProduto.setMercadologico3(Integer.parseInt(Utils.formataNumero(rst.getString("sgrcod"))));
                    oProduto.setMercadologico4(0);
                    oProduto.setMercadologico5(0);
                    
                    if ((rst.getString("proncm") != null)
                            && (!rst.getString("proncm").isEmpty())
                            && (rst.getString("proncm").trim().length() > 5)) {
                        NcmVO oNcm = new NcmDAO().validar(rst.getString("proncm").trim());

                        oProduto.setNcm1(oNcm.ncm1);
                        oProduto.setNcm2(oNcm.ncm2);
                        oProduto.setNcm3(oNcm.ncm3);
                    } else {
                        oProduto.setNcm1(402);
                        oProduto.setNcm2(99);
                        oProduto.setNcm3(0);
                    }

                    if ((rst.getString("procest") != null) &&
                        (!rst.getString("procest").trim().isEmpty())) {
                    
                        if (rst.getString("procest").trim().length() == 5) {

                            oProduto.setCest1(Integer.parseInt(rst.getString("procest").trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("procest").trim().substring(1, 3)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("procest").trim().substring(3, 5)));

                        } else if (rst.getString("procest").trim().length() == 6) {

                            oProduto.setCest1(Integer.parseInt(rst.getString("procest").trim().substring(0, 1)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("procest").trim().substring(1, 4)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("procest").trim().substring(4, 6)));

                        } else if (rst.getString("procest").trim().length() == 7) {

                            oProduto.setCest1(Integer.parseInt(rst.getString("procest").trim().substring(0, 2)));
                            oProduto.setCest2(Integer.parseInt(rst.getString("procest").trim().substring(2, 5)));
                            oProduto.setCest3(Integer.parseInt(rst.getString("procest").trim().substring(5, 7)));
                        }
                    } else {
                        oProduto.setCest1(-1);
                        oProduto.setCest2(-1);
                        oProduto.setCest3(-1);
                    }
                    
                    oProduto.setIdFamiliaProduto(rst.getString("procodsim") != null ? rst.getInt("procodsim") : -1);
                    oProduto.setMargem(rst.getDouble("promrg1"));
                    if (oProduto.getMargem() == 0) {
                        oProduto.recalcularMargem();
                    }
                    
                    //<editor-fold defaultstate="collapsed" desc="PRODUTOS DE BALANÇA E EMBALAGEM">
                    //Tratando o id da balança.
                    long codigoBarra = Long.parseLong(Utils.formataNumero(rst.getString("procodaux")));
                    
                    ProdutoBalancaVO produtoBalanca = null;                    
                    if (id > 0 && id <= Integer.MAX_VALUE) {
                        produtoBalanca = produtosBalanca.get((int) id);
                    }
                    //Se um produto de balança foi loacalizado
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras(-1);                          
                        oProduto.setValidade(produtoBalanca.getValidade() >= 1 ? produtoBalanca.getValidade() : 0);
                        
                        if ("P".equals(produtoBalanca.getPesavel())) {
                            oAutomacao.setIdTipoEmbalagem(4);
                            oProduto.setPesavel(false);
                        } else {
                            oAutomacao.setIdTipoEmbalagem(0);
                            oProduto.setPesavel(true);
                        }
                        
                        oCodigoAnterior.setCodigobalanca(produtoBalanca.getCodigo());
                        oCodigoAnterior.setE_balanca(true);
                    } else {
                        oProduto.setValidade(0);
                        oProduto.setPesavel(false); 
                        
                        oAutomacao.setCodigoBarras(-2);
                        oAutomacao.setIdTipoEmbalagem(Utils.converteTipoEmbalagem(rst.getString("prounid")));
                        
                        oCodigoAnterior.setCodigobalanca(0);
                        oCodigoAnterior.setE_balanca(false);
                    }
                    oAutomacao.setQtdEmbalagem(1);
                    oProduto.setIdTipoEmbalagem(oAutomacao.getIdTipoEmbalagem()); 
                    //</editor-fold>
                    
                    oProduto.setQtdEmbalagem(1);              
                    oProduto.setIdComprador(1);
                    oProduto.setIdFornecedorFabricante(1);
                    oProduto.setSugestaoPedido(true);
                    oProduto.setAceitaMultiplicacaoPdv(true);
                    oProduto.setSazonal(false);
                    oProduto.setFabricacaoPropria(false);
                    oProduto.setConsignado(false);
                    oProduto.setDdv(0);
                    oProduto.setPermiteTroca(true);
                    oProduto.setVendaControlada(false);
                    oProduto.setVendaPdv(true);
                    oProduto.setConferido(true);
                    oProduto.setPermiteQuebra(true);   
                    oProduto.setPesoBruto(rst.getDouble("PROPESBRT"));
                    oProduto.setPesoLiquido(rst.getDouble("PROPESLIQ"));
                    
                    //<editor-fold defaultstate="collapsed" desc="TRIBUTAÇÃO PIS/CONFINS">
                    oProduto.setIdTipoPisCofinsDebito(13);
                    oProduto.setIdTipoPisCofinsCredito(1);
                    oProduto.setTipoNaturezaReceita(-1);
                    //</editor-fold>

                    //<editor-fold defaultstate="collapsed" desc="COMPLEMENTO DO PRODUTO">
                    oComplemento.setPrecoVenda(rst.getDouble("proprcvdavar"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("proprcvdavar"));
                    oComplemento.setCustoComImposto(rst.getDouble("proprccst"));
                    oComplemento.setCustoSemImposto(rst.getDouble("proprccst"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    //O ajuste do estoque é feito em outro método.
                    oComplemento.setEstoque(0);
                    oComplemento.setEstoqueMinimo(0);
                    oComplemento.setEstoqueMaximo(0);
                    //</editor-fold>                    
                    
                    //<editor-fold defaultstate="collapsed" desc="TRIBUTAÇÃO ICMS">
                    String tribAliquota;
                    if ((rst.getString("TRBID") != null)
                        && (!rst.getString("TRBID").isEmpty())) {
                        tribAliquota = rst.getString("TRBID").trim();
                    } else {
                        tribAliquota = "999";
                    }
                    oAliquota.setIdEstado(Utils.getEstadoPelaSigla(ufEmpresa));
                    if (oAliquota.getIdEstado() == 0) {oAliquota.setIdEstado(35);}
                    oAliquota.setIdAliquotaDebito(8);
                    oAliquota.setIdAliquotaCredito(8);
                    oAliquota.setIdAliquotaDebitoForaEstado(8);
                    oAliquota.setIdAliquotaCreditoForaEstado(8);
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(8);
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="CODIGO ANTERIOR">
                    oCodigoAnterior.setCodigoanterior(id);
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(Long.parseLong(
                            Utils.formataNumero(rst.getString("procodaux")).equals("0") ?
                            "-2" :
                            Utils.formataNumero(rst.getString("procodaux"))
                    ));
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("proncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(13);
                    oCodigoAnterior.setPiscofinscredito(1);
                    oCodigoAnterior.setNaturezareceita(-1);
                    oCodigoAnterior.setRef_icmsdebito(""); 
                    //</editor-fold>
                    
                    
                    result.add(oProduto);
                                        
                }
            }
        }
        return result;
    }

    public void importarProduto6(int idLojaVR, int idLojaCliente) throws Exception {ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
        for(ProdutoVO prod: carregarProdutos(idLojaVR, idLojaCliente)) {
            auxiliar.put(prod.idDouble, prod);
        }
        List<LojaVO> vLoja = new LojaDAO().carregar();
        produto.implantacaoExterna = true;
        produto.salvar(new ArrayList(auxiliar.values()), idLojaVR, vLoja);
        /*
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos Menor 7 digitos...");
            Map<Integer, ProdutoVO> vProdutoSysPdv = carregarProdutoSysPdv();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoSysPdv.size());
            
            for (Integer keyId : vProdutoSysPdv.keySet()) {
                
                ProdutoVO oProduto = vProdutoSysPdv.get(keyId);

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
        }*/
    }

    public void importarProdutoMaior6(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos Maior 6 digitos...");
            Map<Double, ProdutoVO> vProdutoSysPdv = carregarProdutoMaior6SysPdv();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoSysPdv.size());
            
            for (Double keyId : vProdutoSysPdv.keySet()) {
                
                ProdutoVO oProduto = vProdutoSysPdv.get(keyId);

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
    
    public void importarIcmsProduto() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Icms...");
            List<ProdutoVO> vProduto = carregarIcmsProduto();
            
            new ProdutoDAO().alterarICMSProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoSysPdv(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Double keyId : vCustoProduto.keySet()) {
                
                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarCustoProdutoSysPdv(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarPrecoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarPrecoProdutoSysPdv(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Double keyId : vPrecoProduto.keySet()) {
                
                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarPrecoProdutoSysPdv(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }
    
    public void importarEstoqueProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Double, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoSysPdv(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Double keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

                vProdutoNovo.add(oProduto);
                
                ProgressBar.next();
            }
            
            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);
            
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
    
    public void importarChequeReceber(int id_loja, int id_lojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Cheque Receber...");
            List<ReceberChequeVO> vReceberCheque = carregarReceberCheque(id_loja, id_lojaCliente);

            new ReceberChequeDAO().salvar(vReceberCheque,id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }      
    
    public void importarProdutoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorSysPdv();
            ProdutoFornecedorDAO dao = new ProdutoFornecedorDAO(); 
            dao.salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberCliente(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteSysPdv(idLoja, idLojaCliente);

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
    
    public void importarProdutoFamiliaSysPdv() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Família Produto...");
            List<ProdutoVO> vProduto = carregarProdutoFamiliaSysPdv();
            
            new ProdutoDAO().acertarFamiliaProdutoSysPdv(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarTipoEmbalagem() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Tipo Embalagem...");
            List<ProdutoVO> vProduto = carregarTipoEmbalagemSysPdv();
            
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.alterarQtdEmbalagem = true;
            produtoDAO.alterarTipoEmbalagemSysPdv(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarDataProduto() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            
            ProgressBar.setStatus("Carregando dados...Data Cadastro Produto...");
            vProduto = carregarDataCadastroProduto();
            
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().altertarDataCadastroProdutoGdoor(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    // FUNÇÕES
    private int retornarAliquotaICMSSysPdv(String codTrib, String descTrib) {

        int retorno;
        
        if ("F00".equals(codTrib)) {
            retorno = 7;
        } else if ("F07".equals(codTrib)) {
            retorno = 7;
        } else if ("F17".equals(codTrib)) {
            retorno = 7;
        } else if ("F19".equals(codTrib)) {
            retorno = 7;
        } else if ("F58".equals(codTrib)) {
            retorno = 7;
        } else if ("I00".equals(codTrib)) {
            retorno = 6;
        } else if ("N00".equals(codTrib)) {
            retorno = 21;
        } else if ("T07".equals(codTrib)) {
            retorno = 25;
        } else if ("T12".equals(codTrib)) {
            retorno = 24;
        } else if ("T17".equals(codTrib)) {
            retorno = 20;
        } else if ("T18".equals(codTrib)) {
            retorno = 8;
        } else if ("T25".equals(codTrib)) {
            retorno = 3;
        } else if ("T27".equals(codTrib)) {
            retorno = 27;
        } else if ("T29".equals(codTrib)) {
            retorno = 7;
        } else if ("T58".equals(codTrib)) {
            retorno = 26;
        } else {
            retorno = 8;
        }
        
        return retorno;
    }    
    
    public void corrigirClienteDuplicado() throws Exception {

        try {
            ProgressBar.setStatus("Corrigindo dados...Cliente Duplicados...");
            new ClientePreferencialDAO().corrigirClienteDuplicado();
        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarMercadologicoProduto() throws Exception {
        List<ProdutoVO> vProduto  = new ArrayList<>();
        try {
            
            ProgressBar.setStatus("Carregando dados Mercadológico Produtos...");
            vProduto = carregarMercadologicoProduto();
            
            
            
            new ProdutoDAO().alterarMercadologicoProduto(vProduto);

        } catch(Exception ex) {
            throw ex;
        }
    }

    public void importarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente);
        
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }

    private List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        String dataInicio = "24/06/2016";
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	cast(ep.PROCOD as integer) id_produto,\n" +
                    "	cast('" + dataInicio + "' as date) as datainicio,\n" +
                    "	cast(e.ENCDATFIM as date) datatermino,\n" +
                    "	ep.ENCPROPRCOFE precooferta\n" +
                    "from \n" +
                    "	ENCARTE_PRODUTO ep\n" +
                    "	join ENCARTE e on\n" +
                    "		ep.ENCCOD = e.ENCCOD\n" +
                    "where\n" +
                    "	e.ENCDATFIM >= '23/06/2016'\n" +
                    "order by\n" +
                    "	id_produto"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produto(rst.getInt("id_produto"));
                    vo.setDatainicio(rst.getString("datainicio"));
                    vo.setDatatermino(rst.getString("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }

    public void ajustarSituacaoCadastral(int idLojaVR, int idLojaCliente) throws Exception{
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        
        ProgressBar.setStatus("Carregando produtos e suas situações");
        Map<Integer, ProdutoVO> vProdutoSysPdv = carregarProdutoSysPdv();
        
        ProgressBar.setMaximum(vProdutoSysPdv.size());
            
        for (Integer keyId : vProdutoSysPdv.keySet()) {

            ProdutoVO oProduto = vProdutoSysPdv.get(keyId);

            oProduto.idProdutoVasilhame = -1;
            oProduto.excecao = -1;
            oProduto.idTipoMercadoria = -1;

            vProdutoNovo.add(oProduto);


            ProgressBar.next();
        }
        
        new ProdutoDAO().alterarSituacaoCadastroProduto(vProdutoNovo, idLojaVR);
    }

    public void ajustarCodigoAnterior(int idLojaVR, int idLojaCliente) throws Exception{        
        ProgressBar.setStatus("Carregando produtos e suas situações");
        List<ProdutoVO> vProdutoNovo = carregarPisCofinsSysPdv();        
        ProgressBar.setMaximum(vProdutoNovo.size());            
        new ProdutoDAO().alterarCodigoAnterior(vProdutoNovo);
    }
    
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
            sql.append("select id, id_tipoembalagem, pesavel ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("id"));
                
                if (rst.getInt("id_tipoembalagem") == 4) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else if (rst.getInt("id_tipoembalagem") != 4 && rst.getBoolean("pesavel")) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else if (rst.getInt("id_tipoembalagem") != 4 && !rst.getBoolean("pesavel") && idProduto >= 10000) {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                } else if (rst.getInt("id_tipoembalagem") != 4 && idProduto < 10000) {
                    codigobarras = Utils.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = Utils.gerarEan13((int) idProduto, true);
                }
                
                qtdeEmbalagem = 1;

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = (int) idProduto;
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.idTipoEmbalagem = rst.getInt("id_tipoembalagem");
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
    
    public void importarEanEmBranco() throws Exception {
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
    
    public void importarCEST() throws Exception {
        List<ProdutoVO> vProduto;
        
        ProgressBar.setStatus("Carregando dados...Código CEST...");
        vProduto = carregarCest();

        if (!vProduto.isEmpty()) {
            new ProdutoDAO().alterarCestProduto(vProduto);
        }        
    }
    
    private List<ProdutoVO> carregarCest() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double idProduto = 0;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select\n" +
                        "    p.procod,\n" +
                        "    p.prodes,\n" +
                        "    p.prodesrdz,\n" +
                        "    p.proncm,\n" +
                        "    p.procest\n" +
                        "from\n" +
                        "    produto p\n" +
                        "order by\n" +
                        "    p.procod");
            
            rst = stm.executeQuery(sql.toString());
            CestDAO cestDAO = new CestDAO();
            NcmDAO ncmDAO = new NcmDAO();
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod"));
                
                NcmVO ncm = ncmDAO.validar(rst.getString("proncm"));
                CestVO cest = cestDAO.getCestValido(rst.getString("procest"));
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                oProduto.cest1 = cest.getCest1();
                oProduto.cest2 = cest.getCest2();
                oProduto.cest3 = cest.getCest3();
                oProduto.ncm1 = ncm.getNcm1();
                oProduto.ncm2 = ncm.getNcm2();
                oProduto.ncm3 = ncm.getNcm3();
                
                CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                
                oAnterior.setNcm(rst.getString("proncm"));
                oAnterior.setCest(rst.getString("procest"));
                
                oProduto.getvCodigoAnterior().add(oAnterior);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }   
    } 
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteTudo(int id_loja, int id_lojaCliente) throws Exception {
        
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int numerocupom, ecf;
        long id_cliente;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT\n" +
                "	CTRID,\n" +
                "	ctrnum,\n" +
                "	clicod,\n" +
                "	cxanum,\n" +
                "	ctrdatemi,\n" +
                "	ctrdatvnc,\n" +
                "	ctrvlrdev,\n" +
                "	ctrobs\n" +
                "FROM CONTARECEBER\n" +
                "WHERE \n" +
                "	(COALESCE(CTRVLRPAG,0) < CTRVLRNOM) AND \n" +
                "	COALESCE(ctrvlrdev,0) > 0"
            )) {

                while (rst.next()) {

                    ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();

                    id_cliente = rst.getLong("clicod");                
                    dataemissao = rst.getString("ctrdatemi").substring(0, 10).trim();
                    datavencimento = rst.getString("ctrdatvnc").substring(0, 10).trim();

                    long cupom = Long.parseLong(Utils.formataNumero(rst.getString("ctrnum")));
                    if (cupom <= Integer.MAX_VALUE) {
                        numerocupom = (int) cupom;
                    } else {
                        numerocupom = 0;
                    }


                    valor = Double.parseDouble(rst.getString("ctrvlrdev"));
                    juros = 0;

                    if ((rst.getString("cxanum") != null) &&
                            (!rst.getString("cxanum").trim().isEmpty())) {
                        ecf = Integer.parseInt(rst.getString("cxanum").trim());
                    } else {
                        ecf = 0;
                    }

                    if ((rst.getString("ctrobs") != null) &&
                            (!rst.getString("ctrobs").isEmpty())) {
                        observacao = util.acertarTexto(rst.getString("ctrobs").replace("'", "").trim());
                    } else { 
                        observacao = "IMPORTADO VR";
                    }

                    oReceberCreditoRotativo.id_clientepreferencial = 0;
                    oReceberCreditoRotativo.idClientePreferencialLong = id_cliente;
                    oReceberCreditoRotativo.id_loja = id_loja;
                    oReceberCreditoRotativo.dataemissao = dataemissao;
                    oReceberCreditoRotativo.numerocupom = numerocupom;
                    oReceberCreditoRotativo.valor = valor;
                    oReceberCreditoRotativo.ecf = ecf;
                    oReceberCreditoRotativo.observacao = observacao;
                    oReceberCreditoRotativo.datavencimento = datavencimento;
                    oReceberCreditoRotativo.valorjuros = juros;

                    vReceberCreditoRotativo.add(oReceberCreditoRotativo);

                }
            }
        
        }

        return vReceberCreditoRotativo;
    } 

    public void importarRotativoTudo(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteTudo(idLojaVR, idLojaCliente);

            new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLojaVR);
    }
}