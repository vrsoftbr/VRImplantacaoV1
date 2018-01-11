package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.VRException;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.utils.classe.exclusiva.TributacaoICMSGuiaSistemas;
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
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeVO;
import vrimplantacao.vo.vrimplantacao.ReceberCreditoRotativoVO;

public class GuiaSistemasDAO {
    
    //CARREGAMENTOS
    
    public List<ProdutoVO> carregarTipoEmbalagemGuiaSistemas() throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ProdutoVO> vProduto = new ArrayList<>();
        
        double idProduto;
        int idTipoEmbalagem;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select procod, proenvbal, prounid ");
            sql.append("from produto ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                idProduto = Double.parseDouble(rst.getString("procod").trim());
                
                if ((rst.getString("proenvbal") != null) &&
                        (!rst.getString("proenvbal").trim().isEmpty())) {
                    if ("N".equals(rst.getString("proenvbal").trim())) {

                        if ("KG".equals(rst.getString("prounid").trim())) {
                            idTipoEmbalagem = 4;
                        } else {
                            idTipoEmbalagem = 0;
                        }
                    } else {
                        idTipoEmbalagem = 4;
                    }
                } else {
                    idTipoEmbalagem = 0;
                }
                
                oProduto.idDouble = idProduto;
                oProduto.idTipoEmbalagem = idTipoEmbalagem;
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
        
    }
    
    public List<ProdutoVO> carregarProdutoFamiliaGuiaSistemas() throws Exception {
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
    
    public List<ClientePreferencialVO> carregarClienteGuiaSistemas(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
                   dataResidencia,  dataCadastro , numero, complemento, dataNascimento, nomePai, nomeMae,
                   telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                   conjuge = "", orgaoExp = "";
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro, Linha=0,
                estadoCivil = 0;
            long cnpj, cep;
            double limite, salario;
            boolean bloqueado;

            try {
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select vfd_codCliente, vfd_nomecliente, vfd_tipopessoa, vfd_rg, vfd_cpf, ");
                sql.append("       vfd_nomepdv, vfd_sexo, vfd_cidade, vfd_estadocivil, vfd_estado,   ");
                sql.append("       vfd_endereco, vfd_numero, vfd_complemento, vfd_cep, vfd_ddd, vfd_fone, ");
                sql.append("       vfd_bairro, vfd_datanascimento, vfd_renda, vfd_situacao, ");
                sql.append("       vfd_datacadastro, vfd_limitecheque, vfd_email,vfd_dddcelular, ");
                sql.append("       vfd_celular, vfd_limitecredito, vfd_observacoes ");
                sql.append("from tab_clientes   ");
                sql.append("order by vfd_codCliente ");                

                rst = stm.executeQuery(sql.toString());
                Linha=1;
                try{
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        id = rst.getInt("vfd_codCliente");
                        id_situacaocadastro = 1;
                        dataResidencia = "1990/01/01";

                        if ((rst.getString("vfd_tipopessoa") != null) &&
                                (!rst.getString("vfd_tipopessoa").trim().isEmpty())) {
                            if ("F".equals(rst.getString("vfd_tipopessoa").trim())) {
                                id_tipoinscricao = 1;
                            } else if ("J".equals(rst.getString("vfd_tipopessoa").trim())) {
                                id_tipoinscricao = 0;
                            } else {
                                id_tipoinscricao = 1;                                
                            }
                        } else {
                            id_tipoinscricao = 1;
                        }

                        if ((rst.getString("vfd_nomecliente") != null) &&
                                (!rst.getString("vfd_nomecliente").trim().isEmpty())) {
                            byte[] bytes = rst.getBytes("vfd_nomecliente");
                            String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                            nome = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        
                        if ((rst.getString("vfd_endereco") != null) &&
                                (!rst.getString("vfd_endereco").trim().isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("vfd_endereco").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }
                        
                        if ((rst.getString("vfd_cpf") != null) &&
                                (!rst.getString("vfd_cpf").trim().isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("vfd_cpf").trim()));
                        } else {
                            cnpj = id;
                        }
                        
                        if ((rst.getString("vfd_bairro") != null) &&
                                (!rst.getString("vfd_bairro").trim().isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("vfd_bairro").trim().replace("'", ""));
                        } else {
                            bairro = "";
                        }
                        
                        if ((rst.getString("vfd_fone") != null) &&
                                (!rst.getString("vfd_fone").trim().isEmpty())) {
                            telefone1 = util.formataNumero(rst.getString("vfd_ddd").trim()+rst.getString("vfd_fone").trim());
                        } else {
                            telefone1 = "0";
                        }
                        
                        if ((rst.getString("VFD_CEP") != null) &&
                                (!rst.getString("VFD_CEP").trim().isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("VFD_CEP").trim()));
                        } else {
                            cep = 0;
                        }
                        
                        if ((rst.getString("vfd_cidade") != null) &&
                                (!rst.getString("vfd_cidade").trim().isEmpty())) {
                            if ((rst.getString("vfd_estado") != null) &&
                                    (!rst.getString("vfd_estado").trim().isEmpty())) {
                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("vfd_cidade").trim().replace("'", "").toUpperCase()), 
                                        rst.getString("vfd_estado").trim().replace("'", ""));
                                
                                if (id_municipio == 0) {
                                    id_municipio = 3548906;
                                }
                            } else {
                                id_municipio = 3548906;
                            }
                        } else {
                            id_municipio = 3548906;
                        }
                        
                        if ((rst.getString("vfd_estado") != null) &&
                                (!rst.getString("vfd_estado").trim().isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(
                            rst.getString("vfd_estado").trim().replace("'", "").toUpperCase());
                            
                            if (id_estado == 0) {
                                id_estado = 35;
                            } 
                        } else {
                            id_estado = 35;
                        }
      
                        if ((rst.getString("vfd_numero") != null) &&
                                (!rst.getString("vfd_numero").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("vfd_numero").trim().replace("'", ""));
                            if (numero.length()>6){
                                numero = numero.substring(0, 6);
                            }
                        } else {
                            numero = "0";
                        }
                        
                        if ((rst.getString("vfd_complemento") != null) &&
                                (!rst.getString("vfd_complemento").trim().isEmpty())) {
                            complemento = util.acertarTexto(rst.getString("vfd_complemento").trim().replace("'", ""));
                        } else {
                            complemento = "";
                        }
                        
                        if ((rst.getString("vfd_limitecredito") != null) &&
                                (!rst.getString("vfd_limitecredito").trim().isEmpty())) {
                            limite = Double.parseDouble(rst.getString("vfd_limitecredito").replace(".", "").replace(",", "."));
                        } else {
                            limite = 0;
                        }
                        
                        if ((rst.getString("vfd_rg") != null) &&
                                (!rst.getString("vfd_rg").trim().isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("vfd_rg").trim());
                            inscricaoestadual = inscricaoestadual.replace("'", "").replace("-", "").replace(".", "");
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                        
                        if ((rst.getString("vfd_datacadastro") != null) &&
                                (!rst.getString("vfd_datacadastro").trim().isEmpty())) {
                            dataCadastro = rst.getString("vfd_datacadastro").substring(0, 10).trim().replace("-", "/");
                        }else{
                            dataCadastro = "";
                        }
                        if ((rst.getString("vfd_datanascimento") != null) &&
                                (!rst.getString("vfd_datanascimento").trim().isEmpty())) {
                            dataNascimento = rst.getString("vfd_datanascimento").substring(0, 10).trim().replace("-", "/");
                        } else {
                            dataNascimento = null;
                        }
                        
                        /*if ((rst.getString("clidtblo") != null) &&
                                (!rst.getString("clidtblo").trim().isEmpty())) {
                            bloqueado = true;
                        } else {*/
                            bloqueado = false;
                        //}
                        /*
                        if ((rst.getString("clipai") != null) &&
                                (!rst.getString("clipai").trim().isEmpty())) {
                            nomePai = util.acertarTexto(rst.getString("clipai").trim().replace("'", ""));
                        } else {*/
                            nomePai = "";
                        //}
                        /*
                        if ((rst.getString("climae") != null) &&
                                (!rst.getString("climae").trim().isEmpty())) {
                            nomeMae = util.acertarTexto(rst.getString("climae").trim().replace("'", ""));
                        } else {*/
                            nomeMae = "";
                        //}
                        
                        if ((rst.getString("vfd_celular") != null) &&
                                (!rst.getString("vfd_celular").trim().isEmpty())) {
                            telefone2 = util.formataNumero(rst.getString("vfd_dddcelular").trim()+rst.getString("vfd_celular").trim());
                        } else {
                            telefone2 = "";
                        }
                        /*
                        if ((rst.getString("clifax") != null) &&
                                (!rst.getString("clifax").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("clifax").trim());
                        } else {*/
                            fax = "";
                        //}
                        
                        if ((rst.getString("vfd_observacoes") != null) &&
                                (!rst.getString("vfd_observacoes").trim().isEmpty())) {
                            observacao = util.acertarTexto(rst.getString("vfd_observacoes").replace("'", "").trim());
                        } else {
                            observacao = "";
                        }
                        
                        if ((rst.getString("vfd_email") != null) &&
                                (!rst.getString("vfd_email").trim().isEmpty()) &&
                                (rst.getString("vfd_email").contains("@"))) {
                            email = util.acertarTexto(rst.getString("vfd_email").trim().replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("vfd_sexo") != null) &&
                                (!rst.getString("vfd_sexo").trim().isEmpty())) {
                            if ("F".equals(rst.getString("vfd_sexo").trim())) {
                                id_sexo = 0;
                            } else {
                                id_sexo = 1;
                            }
                        } else {
                            id_sexo = 1;
                        }
                        /*
                        if ((rst.getString("cliemptrb") != null) &&
                                (!rst.getString("cliemptrb").trim().isEmpty())) {
                            empresa = util.acertarTexto(rst.getString("cliemptrb").trim().replace("'", ""));
                        } else {*/
                            empresa = "";
                        //}
                        /*
                        if ((rst.getString("cliemptrb") != null) &&
                                (!rst.getString("cliemptrb").trim().isEmpty())) {
                            telEmpresa = util.formataNumero(rst.getString("cliemptrb").trim());
                        } else {*/
                            telEmpresa = "";
                        //}
                        /*
                        if ((rst.getString("cliempcar") != null) &&
                                (!rst.getString("cliempcar").trim().isEmpty())) {
                            cargo = util.acertarTexto(rst.getString("cliempcar").replace("'", "").trim());
                        } else {*/
                            cargo = "";
                        //}
                        /*
                        if ((rst.getString("cliempend") != null) &&
                                (!rst.getString("cliempend").trim().isEmpty())) {
                            enderecoEmpresa = util.acertarTexto(rst.getString("cliempend").trim().replace("'", ""));
                        } else {*/
                            enderecoEmpresa = "";
                        //}
                        
                        if ((rst.getString("vfd_renda") != null) &&
                                (!rst.getString("vfd_renda").trim().isEmpty())) {
                            salario = Double.parseDouble(rst.getString("vfd_renda").replace(".", "").replace(",", "."));
                        } else {
                            salario = 0;
                        }
                        
                        if ((rst.getString("vfd_estadocivil") != null) &&
                                (!rst.getString("vfd_estadocivil").trim().isEmpty())) {
                            if (rst.getInt("vfd_estadocivil")==1) {
                                estadoCivil = 1;
                            } else if (rst.getInt("vfd_estadocivil")==2) {
                                estadoCivil = 2;
                            } else if (rst.getInt("vfd_estadocivil")==3) {
                                estadoCivil = 3;
                            } else if (rst.getInt("vfd_estadocivil")==4) {
                                estadoCivil = 4;
                            } else if (rst.getInt("vfd_estadocivil")==5) {
                                estadoCivil = 5;
                            }else{
                                estadoCivil = 0;
                            }
                        } else {
                            estadoCivil = 0;
                        }
                        /*
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
                        */
                        
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
                        
                        if (telefone2.length() > 14) {
                            telefone2 = telefone2.substring(0, 14);
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

                        if (observacao.length() > 80) {
                            observacao = observacao.substring(0, 80);
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
                        oClientePreferencial.celular = telefone2;
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

    public List<FornecedorVO> carregarFornecedorGuiaSistemas() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro,
                   numero = "", complemento = "", telefone = "",telefone2 = "", email = "", fax = "";
            int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            Long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;

            try {
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select fornecedor.vfd_codFornecedor, fornecedor.vfd_razao, fornecedor.vfd_Apelido, fornecedor.vfd_endereco, fornecedor.vfd_cidade, ");
                sql.append("	   fornecedor.vfd_bairro, fornecedor.vfd_uf, fornecedor.vfd_cep, fornecedor.vfd_ie, fornecedor.vfd_rg, fornecedor.vfd_fone, ");
                sql.append("	   fornecedor.vfd_fax, fornecedor.vfd_prazo, fornecedor.vfd_nomevendedor, fornecedor.vfd_faxvendedor, fornecedor.vfd_TipoPessoa, "); 
                sql.append("	   fornecedor.vfd_cpf, fornecedor.vfd_emailvendedor, fornecedor.vfd_emailvendas,  ");
                sql.append("	   prazo.vfd_dias as dias, fornecedor.VFD_NUMERO, ");
                sql.append("	   tipoforn.vfd_codtipfornecedor as tipoforn, vfd_fonevendedor  ");
                sql.append("from tab_fornecedor as fornecedor  ");
                sql.append("	inner join tab_prazopagamento as prazo on prazo.vfd_codprazo = fornecedor.vfd_codprazo ");
                sql.append("	inner join tab_tipofornecedor as tipoforn on tipoforn.vfd_codtipfornecedor = fornecedor.vfd_codtipofornecedor ");
                sql.append("order by fornecedor.vfd_codfornecedor                 ");

                rst = stm.executeQuery(sql.toString());

                Linha=0;
                
                try{
                    while (rst.next()) {                    
                        FornecedorVO oFornecedor = new FornecedorVO();

                        id = rst.getInt("vfd_codFornecedor");

                        Linha++; 
                        if (Linha==1049){
                            Linha--;
                            Linha++;                        
                        }                    
                        if ((rst.getString("vfd_razao") != null)
                                && (!rst.getString("vfd_razao").isEmpty())) {
                           byte[] bytes = rst.getBytes("vfd_razao");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "";
                        }

                        if ((rst.getString("vfd_Apelido") != null)
                                && (!rst.getString("vfd_Apelido").isEmpty())) {
                           byte[] bytes = rst.getBytes("vfd_Apelido");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "";
                        }

                        if ((rst.getString("vfd_tipoPessoa") != null)
                                && (!rst.getString("vfd_tipoPessoa").isEmpty())
                                && (("F".equals(rst.getString("vfd_tipoPessoa").toUpperCase().trim()))||
                                    ("P".equals(rst.getString("vfd_tipoPessoa").toUpperCase().trim())))) {
                            id_tipoinscricao = 1;                            
                            if ((rst.getString("vfd_cpf") != null)
                                    && (!rst.getString("vfd_cpf").isEmpty())) {
                                cnpj = Long.parseLong(util.formataNumero(rst.getString("vfd_cpf").trim()));
                            } else {
                                cnpj = Long.parseLong(rst.getString("vfd_codFornecedor"));
                            }

                            if ((rst.getString("vfd_rg") != null)
                                    && (!rst.getString("vfd_rg").isEmpty())) {
                                inscricaoestadual = util.acertarTexto(rst.getString("vfd_rg").replace("'", "").trim());
                            } else {
                                inscricaoestadual = "ISENTO";
                            }
                        }else{
                            id_tipoinscricao = 0;                            
                            if ((rst.getString("vfd_cpf") != null)
                                    && (!rst.getString("vfd_cpf").isEmpty())) {
                                cnpj = Long.parseLong(util.formataNumero(rst.getString("vfd_cpf").trim()));
                            } else {
                                cnpj = Long.parseLong(rst.getString("vfd_codFornecedor"));
                            }

                            if ((rst.getString("vfd_ie") != null)
                                    && (!rst.getString("vfd_ie").isEmpty())) {
                                inscricaoestadual = util.acertarTexto(rst.getString("vfd_ie").replace("'", "").trim());
                            } else {
                                inscricaoestadual = "ISENTO";
                            }                            
                        }

                        if ((rst.getString("vfd_endereco") != null)
                                && (!rst.getString("vfd_endereco").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("vfd_endereco").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("vfd_bairro") != null)
                                && (!rst.getString("vfd_bairro").isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("vfd_bairro").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("vfd_cep") != null)
                                && (!rst.getString("vfd_cep").isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("vfd_cep").trim()));
                        } else {
                            cep = Long.parseLong("0");
                        }

                        if ((rst.getString("vfd_cidade") != null)
                                && (!rst.getString("vfd_cidade").isEmpty())) {
                            if ((rst.getString("vfd_uf") != null)
                                    && (!rst.getString("vfd_uf").isEmpty())) {

                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("vfd_cidade").replace("'", "").trim()),
                                        util.acertarTexto(rst.getString("vfd_uf").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = 3550308;
                                }
                            }else{
                                id_municipio = 3550308;
                            }
                        } else {
                            id_municipio = 3550308;
                        }

                        if ((rst.getString("vfd_uf") != null)
                                && (!rst.getString("vfd_uf").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("vfd_uf").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 35;
                            }
                        } else {
                            id_estado = 35;
                        }

                        datacadastro = "";

                        pedidoMin = 0;

                        ativo = true;
                        
                        if ((rst.getString("VFD_NUMERO") != null) &&
                                (!rst.getString("VFD_NUMERO").trim().isEmpty())) {
                            numero = util.acertarTexto(rst.getString("VFD_NUMERO").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        if ((rst.getString("vfd_fone") != null) &&
                                (!rst.getString("vfd_fone").trim().isEmpty())) {
                            telefone = util.formataNumero(rst.getString("vfd_fone").trim());
                        } else {
                            telefone = "0";
                        }
                        
                        if ((rst.getString("vfd_fonevendedor") != null) &&
                                (!rst.getString("vfd_fonevendedor").trim().isEmpty())) {
                            telefone2 = util.formataNumero(rst.getString("vfd_fonevendedor").trim());
                        } else {
                            telefone2 = "0";
                        }
                        
                        if (razaosocial.length() > 40) {
                            razaosocial = razaosocial.substring(0, 40);
                        }

                        if (nomefantasia.length() > 30) {
                            nomefantasia = nomefantasia.substring(0, 30);
                        }

                        if ((rst.getString("vfd_emailvendedor") != null) &&
                                (!rst.getString("vfd_emailvendedor").trim().isEmpty()) &&
                                (rst.getString("vfd_emailvendedor").contains("@"))) {
                            email = util.acertarTexto(rst.getString("vfd_emailvendedor").replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("vfd_fax") != null) &&
                                (!rst.getString("vfd_fax").trim().isEmpty())) {
                            fax = util.formataNumero(rst.getString("vfd_fax").trim());
                            if (fax.length()>12){
                                fax = fax.substring(0, 12);
                            }
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
                        
                        if (telefone2.length() > 14) {
                            telefone2 = telefone2.substring(0, 14);
                        }
                        oFornecedor.codigoanterior = id;
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
                        oFornecedor.complemento = complemento;
                        oFornecedor.telefone = telefone;
                        oFornecedor.email = email;
                        oFornecedor.fax = fax;
                        oFornecedor.telefone2 = telefone2;

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
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedorGuiaSistemas() throws Exception {

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
            sql.append("SELECT VFD_CODPRODUTO, VFD_CODFORNECEDOR, VFD_CODREFERENCIA ");
            sql.append("  FROM TAB_REFPRODUTO ORDER BY VFD_CODPRODUTO            ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("VFD_CODFORNECEDOR");
                idProduto = Double.parseDouble(rst.getString("VFD_CODPRODUTO"));

                if ((rst.getString("VFD_CODREFERENCIA") != null)
                        && (!rst.getString("VFD_CODREFERENCIA").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("VFD_CODREFERENCIA").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }
                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produtoDouble = idProduto;               
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarMercadologicoGuiaSistemas(int nivel, boolean geraMercadologicoPadrao) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            if (nivel == 1) {            
                sql = new StringBuilder();
                sql.append(" SELECT vfd_CodDepartamento as merc1, vfd_Descricao as Descricao ");
                sql.append(" FROM Tab_Departamento                               ");
                rst = stm.executeQuery(sql.toString());

                while (rst.next()) {
                    mercadologico1 = 0;
                    mercadologico2 = 0;
                    mercadologico3 = 0;              
                    MercadologicoVO oMercadologico = new MercadologicoVO();

                    mercadologico1 = Integer.parseInt(rst.getString("merc1"));

                    if ((rst.getString("Descricao") != null) &&
                            (!rst.getString("Descricao").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("Descricao").replace("'", "").trim());
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
                    vMercadologico.add(oMercadologico);                                        
                }
            }else if ((nivel == 2)) {
                sql = new StringBuilder();
                sql.append(" SELECT vfd_CodDepartamento as merc1,                     ");
                sql.append("        vfd_CodSecao as merc2, vfd_Descricao as Descricao ");
                sql.append(" FROM Tab_Secao                                           ");                                        
                rst = stm.executeQuery(sql.toString());

                while (rst.next()) { 
                    mercadologico1 = 0;
                    mercadologico2 = 0;
                    mercadologico3 = 0;              
                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    
                    mercadologico1 = Integer.parseInt(rst.getString("merc1"));
                    mercadologico2 = Integer.parseInt(rst.getString("merc2"));
                    
                    if ((rst.getString("descricao") != null) &&
                            (!rst.getString("descricao").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("descricao").replace("'", "").trim());
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
                    vMercadologico.add(oMercadologico);                                        
                } 
            } else if ((nivel == 3)) {
                sql = new StringBuilder();
                sql.append(" SELECT vfd_CodDepartamento merc1, vfd_CodSecao merc2, ");
                sql.append(" 	   vfd_CodGrupo merc3, vfd_Descricao as Descricao  ");
                sql.append(" FROM Tab_Grupo                                        "); 
                rst = stm.executeQuery(sql.toString());

                while (rst.next()) { 
                    mercadologico1 = 0;
                    mercadologico2 = 0;
                    mercadologico3 = 0;              
                    MercadologicoVO oMercadologico = new MercadologicoVO();
                    
                    mercadologico1 = Integer.parseInt(rst.getString("merc1"));
                    mercadologico2 = Integer.parseInt(rst.getString("merc2"));
                    mercadologico3 = Integer.parseInt(rst.getString("merc3"));
                    
                    if ((rst.getString("descricao") != null) &&
                            (!rst.getString("descricao").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("descricao").replace("'", "").trim());
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
                    vMercadologico.add(oMercadologico);                    
                }                
            }
            
            if (geraMercadologicoPadrao){
                // MERCADOLOGICO PADRÃO
                if (nivel == 1) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();            
                    oMercadologico.mercadologico1 = 100;
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = "DIVERSOS";
                    oMercadologico.nivel = 1;            
                    vMercadologico.add(oMercadologico);                    
                }else if (nivel == 2) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();            
                    oMercadologico.mercadologico1 = 100;
                    oMercadologico.mercadologico2 = 1;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = "DIVERSOS";
                    oMercadologico.nivel = 2;            
                    vMercadologico.add(oMercadologico); 
                } else if (nivel == 3) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();            
                    oMercadologico.mercadologico1 = 100;
                    oMercadologico.mercadologico2 = 1;
                    oMercadologico.mercadologico3 = 1;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = "DIVERSOS";
                    oMercadologico.nivel =3;            
                    vMercadologico.add(oMercadologico);             
                }
            }

            return vMercadologico;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<FamiliaProdutoVO> carregarFamiliaProdutoGuiaSistemas() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();
        String descricao;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select distinct 'FAMILIA ' AS  TIPO ,vfd_codequival from tab_produto ");
            sql.append(" WHERE VFD_CODEQUIVAL IS NOT NULL ");
            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                if (!util.encontrouLetraCampoNumerico(rst.getString("vfd_codequival").trim())) {
                
                    if ((rst.getString("vfd_codequival") != null)
                            && (!rst.getString("vfd_codequival").trim().isEmpty())) {
                        descricao = util.acertarTexto(rst.getString("TIPO").replace("'", "").trim()+
                                                      rst.getString("vfd_codequival").replace("'", "").trim());
                    } else {
                        descricao = "";
                    }

                    FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                    oFamiliaProduto.id = Integer.parseInt(rst.getString("vfd_codequival").replace(".", ""));
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

    public Map<Double, ProdutoVO> carregarProdutoGuiaSistemas(int id_Loja, int id_LojaCliente) throws Exception {
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito,idTipoPisCofinsCodigoAnterior,
            idTipoPisCofinsCreditoCodigoAnterior, tipoNaturezaReceita,idAliquotaDebito, idAliquotaCredito, idFamilia, 
            mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, ncm1, ncm2, ncm3, codigoBalanca, 
            i = 0, validade, tipoNaturezaReceitaCodigoAnterior = -1;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, datacadastro;
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0, idProduto;
        
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("SELECT COALESCE(BALANCA.VFD_CODPRODUTOEAN,0) AS BALANCA, PROD.vfd_FlagBalanca,prod.vfd_codproduto, ");
            sql.append("       prod.vfd_descricao,prod.vfd_descricaopdv,prod.vfd_tippeso, ");
            sql.append("       prod.vfd_codfornecedor,prod.vfd_icmse,prod.vfd_icmss,prod.vfd_situatributaria, ");
            sql.append("       prod.vfd_margem,prod.vfd_codgrupo, prod.vfd_codsubgrupo,prod.vfd_codsecao, "); 
            sql.append("       prod.vfd_coddepartamento, prod.vfd_codequival,prod.vfd_validade,  ");
            sql.append("       prod.vfd_dtcadastro,prod.vfd_classificacaofiscal,prod.vfd_flagpiscofins, ");
            sql.append("       prod.vfd_codmercadologico, prod.vfd_situacao, prod.vfd_codclassificacao, ");
            sql.append("       prod.vfd_idcomprador, prod.vfd_nbmsh, Prod.vfd_SetorBalanca ,  ");
            sql.append("       prod.vfd_codcofins, prod.vfd_codEQUIVAL, ");
            sql.append("       COFINS.VFD_CSTENTRADA, COFINS.VFD_CSTSAIDA, VFD_SITUACAO AS ATIVO,  ");
            sql.append("       vfd_TipoInventarioFatorConversao as ProUnid ");
            sql.append("From tab_produto as prod  ");
            sql.append("LEFT JOIN tmp_ListProdBalanca AS BALANCA ON  ");
            sql.append("	BALANCA.VFD_CODPRODUTO = prod.vfd_codproduto ");
            sql.append("LEFT OUTER JOIN [Tab_cadCOFINS] AS COFINS ON  ");
            sql.append("	COFINS.vfd_CodCOFINS = PROD.VFD_CODCOFINS  ");
            sql.append("ORDER BY prod.vfd_codproduto          ");     
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {               
                ProdutoVO oProduto = new ProdutoVO();
                
                codigoAnterior = Double.parseDouble(rst.getString("vfd_codproduto").trim().replace(".", ""));
                idProduto = Double.parseDouble(rst.getString("vfd_codproduto").trim().replace(".", ""));
                
                if (idProduto==166740){
                    idProduto=idProduto;
                }
                
                sql = new StringBuilder();
                sql.append("select codigo, descricao, pesavel, validade ");
                sql.append("from implantacao.produtobalanca ");
                sql.append("where cast(codigo as numeric(14,0)) = cast("+rst.getString("BALANCA").replace(".", "")+" as numeric(14,0))");

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
                    
                    if (rst.getInt("VFD_TIPPESO")==1) {                        
                        idTipoEmbalagem = 0;
                    } else if (rst.getInt("VFD_TIPPESO")==2){
                        idTipoEmbalagem = 4;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                              
                if ((rst.getString("vfd_descricao") != null) &&
                        (!rst.getString("vfd_descricao").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("vfd_descricao");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("vfd_descricaopdv") != null) &&
                        (!rst.getString("vfd_descricaopdv").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("vfd_descricaopdv");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descricaoReduzida;
                qtdEmbalagem = 1;
                /*if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = (int) Double.parseDouble(rst.getString("vfd_qtdembalagem").replace(",", ""));
                }*/
                
                if ((rst.getString("vfd_codequival") != null) &&
                        (!rst.getString("vfd_codequival").trim().isEmpty())) {
                    idFamilia = rst.getInt("vfd_codequival");
                }else{
                    idFamilia = -1;                    
                }
                
                if ((rst.getString("vfd_codDepartamento") != null) &&
                        (!rst.getString("vfd_codDepartamento").trim().isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("vfd_codDepartamento"));
                } else {
                    mercadologico1 = 100;
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }
                
                if ((rst.getString("vfd_codSecao") != null) &&
                        (!rst.getString("vfd_codSecao").trim().isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("vfd_codSecao"));
                } else {
                    mercadologico1 = 100;
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }

                if ((rst.getString("vfd_codGrupo") != null) &&
                        (!rst.getString("vfd_codGrupo").trim().isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("vfd_codGrupo"));
                } else {
                    mercadologico1 = 100;
                    mercadologico2 = 1;
                    mercadologico3 = 1;                
                }

                if (!util.verificaExisteMercadologico(mercadologico1, mercadologico2, mercadologico3)) {
                    mercadologico1 = 100;
                    mercadologico2 = 1;
                    mercadologico3 = 1;                     
                }
                
                if ((rst.getString("vfd_classificacaofiscal") != null) &&
                        (!rst.getString("vfd_classificacaofiscal").isEmpty()) &&
                        (rst.getString("vfd_classificacaofiscal").trim().length() > 5)) {
                    
                    ncmAtual = util.formataNumero(rst.getString("vfd_classificacaofiscal").trim());
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
                if ((rst.getString("vfd_dtcadastro") != null) &&
                        (!rst.getString("vfd_dtcadastro").trim().isEmpty())) {
                    datacadastro = rst.getString("vfd_dtcadastro").substring(0, 10).trim().replace("-", "/");
                }else{
                    datacadastro = "";
                }
               
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf((int) idProduto));
                } else {
                    codigoBarras = -1;                       
                }

                if ((rst.getString("VFD_CSTSAIDA") != null) &&
                        (!rst.getString("VFD_CSTSAIDA").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("VFD_CSTSAIDA").trim()));
                    idTipoPisCofinsCodigoAnterior = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("VFD_CSTSAIDA").trim()));
                } else {
                    idTipoPisCofins = 1;
                    idTipoPisCofinsCodigoAnterior = -1;
                }
                
                if ((rst.getString("VFD_CSTENTRADA") != null) &&
                        (!rst.getString("VFD_CSTENTRADA").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("VFD_CSTENTRADA").trim()));
                    idTipoPisCofinsCreditoCodigoAnterior = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("VFD_CSTENTRADA").trim()));
                } else {
                    idTipoPisCofinsCredito = 13;
                    idTipoPisCofinsCreditoCodigoAnterior = -1;                    
                }
                
                tipoNaturezaReceita               = util.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                tipoNaturezaReceitaCodigoAnterior = -1;                    

                if ((rst.getString("ATIVO") != null) &&
                        (!rst.getString("ATIVO").trim().isEmpty())) {
                    if ("ATIV".equals(rst.getString("ATIVO").trim().toUpperCase().substring(0, 4))){
                        idSituacaoCadastro = 1;
                    }else{
                        idSituacaoCadastro = 0;                        
                    }
                } else {
                    idSituacaoCadastro = 1;
                }                        
                
                if ((rst.getString("VFD_ICMSS") != null) &&
                        (!rst.getString("VFD_ICMSS").trim().isEmpty())) {
                    idAliquotaDebito = retornarAliquotaICMSGuiaSistemas(rst.getInt("VFD_ICMSS"));
                } else {
                    idAliquotaDebito = 8;
                }
                if ((rst.getString("VFD_ICMSE") != null) &&
                        (!rst.getString("VFD_ICMSE").trim().isEmpty())) {
                    idAliquotaCredito = retornarAliquotaICMSGuiaSistemas(rst.getInt("VFD_ICMSE"));
                } else {
                    idAliquotaCredito = 8;
                }        

                if ((rst.getString("VFD_MARGEM") != null) &&
                        (!rst.getString("VFD_MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("VFD_MARGEM").replace(",", "."));
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
                oProduto.dataCadastro = datacadastro;
                oProduto.validade = validade;
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                //oComplemento.precoVenda = precoVenda;
                //oComplemento.precoDiaSeguinte = precoVenda;
                //oComplemento.custoComImposto = custo;
                //oComplemento.custoSemImposto = custo;
                
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                
                oAliquota.idEstado = 35;
                oAliquota.idAliquotaDebito  = idAliquotaDebito;
                oAliquota.idAliquotaCredito = idAliquotaCredito;
                oAliquota.idAliquotaDebitoForaEstado = idAliquotaDebito;
                oAliquota.idAliquotaCreditoForaEstado = idAliquotaDebito;
                oAliquota.idAliquotaDebitoForaEstadoNF = idAliquotaDebito;
                
                oProduto.vAliquota.add(oAliquota);
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.codigoanterior = codigoAnterior;
                oCodigoAnterior.codigoatual = idProduto;
                
                if(eBalanca){
                    oCodigoAnterior.barras = Long.parseLong(String.valueOf(codigoBalanca));
                }
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceitaCodigoAnterior;
                
                oCodigoAnterior.piscofinsdebito  = idTipoPisCofinsCodigoAnterior;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCreditoCodigoAnterior;
                
                if ((rst.getString("VFD_ICMSS") != null) &&
                        (!rst.getString("VFD_ICMSS").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = rst.getString("VFD_ICMSS").trim().replace(".", "");                    
                } else {
                    oCodigoAnterior.ref_icmsdebito = "";                    
                }
                oCodigoAnterior.estoque = -1;
                oCodigoAnterior.e_balanca = eBalanca;
                oCodigoAnterior.codigobalanca = codigoBalanca;
                //oCodigoAnterior.custosemimposto = custo;
                //oCodigoAnterior.custocomimposto = custo;
                oCodigoAnterior.margem = margem;
                //oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.referencia = -1;
                
                if ((rst.getString("vfd_classificacaofiscal") != null) &&
                        (!rst.getString("vfd_classificacaofiscal").isEmpty()) &&
                        (rst.getString("vfd_classificacaofiscal").trim().length() > 5)) {
                    oCodigoAnterior.ncm = rst.getString("vfd_classificacaofiscal").trim();
                } else {
                    oCodigoAnterior.ncm = "";
                }
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                if (eBalanca){
                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigoBarras;
                    oAutomacao.qtdEmbalagem = 1;
                    oAutomacao.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.vAutomacao.add(oAutomacao);
                }
                
                vProduto.put(idProduto, oProduto);
                
                i  = i + 1;
            }
            
            stmPostgres.close();
            Conexao.commit();
            return vProduto;
            
        } catch(Exception ex) {
            Conexao.rollback();
            if (i > 0) {
                throw new VRException("Linha " + i + ": " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }
    
    public Map<Double, ProdutoVO> carregarCustoProdutoGuiaSistemas(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double custo = 0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();    
            sql.append("SELECT PROD.VFD_CODPRODUTO, PRECO.VFD_CUSTOAQUISICAO    ");
            sql.append("From tab_produto as prod                                ");
            sql.append("LEFT JOIN tab_PrecoAtual AS PRECO ON                    ");
            sql.append("	PRECO.VFD_CODPRODUTO = prod.vfd_codproduto AND  ");
            sql.append("	PRECO.VFD_QTDEMB = 1 AND                        ");
            sql.append("	PRECO.vfd_CodFilial = "+idLojaCliente);             
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("VFD_CODPRODUTO").replace(".", ""));
                
                if ((rst.getString("VFD_CUSTOAQUISICAO") != null) &&
                        (!rst.getString("VFD_CUSTOAQUISICAO").trim().isEmpty())) {
                    custo = Double.parseDouble(rst.getString("VFD_CUSTOAQUISICAO").replace(",", "."));                    
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
       
    public List<ReceberChequeVO> carregarReceberCheque(int id_loja, int id_lojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null, rst2 = null;
        Utils util = new Utils();
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao, id_tipoalinea;
        double valor, juros;
        long cpfCnpj;
        String observacao = "", dataemissao = "", datavencimento = "",
                agencia, conta, nome, rg, telefone;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
            sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, ");
            sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
            sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
            sql.append("FROM CHEQUES c ");             
            sql.append("WHERE c.FILIAL = "+String.valueOf(id_lojaCliente));                         

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                cpfCnpj = Long.parseLong(rst.getString("ciccgc").trim());
                
                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }
                
                idBanco = util.retornarBanco(Integer.parseInt(rst.getString("bancox").trim()));                

                if ((rst.getString("agenci") != null) &&
                        (!rst.getString("agenci").trim().isEmpty())) {
                    agencia = util.acertarTexto(rst.getString("agenci").trim().replace("'", ""));
                } else {
                    agencia = "";
                }
                
                if ((rst.getString("contax") != null) &&
                        (!rst.getString("contax").trim().isEmpty()))  {
                    conta = util.acertarTexto(rst.getString("contax").trim().replace("'", ""));
                } else {
                    conta = "";
                }
                
                if ((rst.getString("cheque") != null) &&
                        (!rst.getString("cheque").trim().isEmpty())) {
                    
                    cheque = Integer.parseInt(util.formataNumero(rst.getString("cheque")));
                    
                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }
                                      
                if ((rst.getString("dataxx") != null) &&
                        (!rst.getString("dataxx").trim().isEmpty())) {
                
                    dataemissao = rst.getString("dataxx").trim();
                } else {
                    dataemissao = "2016/02/01";
                }
                
                if ((rst.getString("vencim") != null) &&
                        (!rst.getString("vencim").trim().isEmpty())) {
                
                    datavencimento = rst.getString("vencim").trim();
                } else {
                    datavencimento = "2016/02/12";
                }
                
                if ((rst.getString("observ") != null) &&
                        (!rst.getString("observ").isEmpty())) {
                    nome = util.acertarTexto(rst.getString("observ").replace("'", "").trim());
                } else {
                    nome = "";
                }
                
                /*if ((rst.getString("chrinscrg") != null) &&
                        (!rst.getString("chrinscrg").isEmpty())) {
                    rg = util.acertarTexto(rst.getString("chrinscrg").trim().replace("'", ""));
                    
                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {*/
                    rg = "";
                //}
                
                valor = Double.parseDouble(rst.getString("valorx"));
                numerocupom = 0;
                juros = 0;

                /*if ((rst.getString("chrobserv1") != null)
                        && (!rst.getString("chrobserv1").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("chrobserv1").replace("'", "").trim());
                } else {*/
                    observacao = "IMPORTADO VR";
                //}

                /*if ((rst.getString("chrtelefone") != null) &&
                        (!rst.getString("chrtelefone").isEmpty()) &&
                        (!"0".equals(rst.getString("chrtelefone").trim()))) {
                    telefone = util.formataNumero(rst.getString("chrtelefone"));
                } else {*/
                    telefone = "";
                //}
                    
                if (rst.getInt("status")==1){
                    id_tipoalinea = 0;
                } else if (rst.getInt("status")==2){
                    id_tipoalinea = 15;                    
                } else {
                    id_tipoalinea = 0;
                }
                
                oReceberCheque.id_loja = id_loja;
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
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteGuiaSistemas(int id_loja, int id_lojaCliente) throws Exception {
        
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
            sql.append("SELECT ctrnum, clicod, cxanum, ctrdatemi, ctrdatvnc, ctrvlrdev, ctrobs ");
            sql.append("FROM CONTARECEBER ");
            sql.append("WHERE CTRVLRPAG < CTRVLRNOM ");
            sql.append("or CTRVLRPAG IS NULL ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("clicod");                
                dataemissao = rst.getString("ctrdatemi").substring(0, 10).trim();
                datavencimento = rst.getString("ctrdatvnc").substring(0, 10).trim();
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("ctrnum")));
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
                    observacao = util.acertarTexto(rst.getString("ctrobs").replace("'", ""));
                } else { 
                    observacao = "IMPORTADO VR";
                }
                
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

    public Map<Double, ProdutoVO> carregarPrecoProdutoGuiaSistemas(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double margem=0, preco=0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();     
            sql.append("SELECT PROD.VFD_CODPRODUTO, PROD.VFD_MARGEM, PRECO.VFD_PRECOVENDA, PRECO.VFD_PRECOVENDA, ");
            sql.append("	   CASE WHEN (PRECO.VFD_DTFIMPROM>=GETDATE()) THEN PRECO.VFD_PRECOPROMOCAO "); 
            sql.append("                WHEN (PRECO.VFD_DTFIMPROM<GETDATE()) THEN PRECO.VFD_PRECOVENDA  ");
            sql.append("		ELSE PRECO.VFD_PRECOVENDA ");
            sql.append("           END AS VFD_PRECOVENDA  ");
            sql.append("From tab_produto as prod   ");
            sql.append("LEFT JOIN tab_PrecoAtual AS PRECO ON   ");
            sql.append("	PRECO.VFD_CODPRODUTO = prod.vfd_codproduto AND   ");
            sql.append("	PRECO.VFD_QTDEMB = 1 AND  ");
            sql.append("	PRECO.vfd_CodFilial = "+id_lojaCliente);  
            sql.append("WHERE PRECO.VFD_CODPRODUTO IS NOT NULL ");
            sql.append("ORDER BY 1             ");
            

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("VFD_CODPRODUTO"));
                
                if ((rst.getString("VFD_PRECOVENDA") != null) &&
                          (!rst.getString("VFD_PRECOVENDA").trim().isEmpty())) {
                     preco = Double.parseDouble(rst.getString("VFD_PRECOVENDA").replace(",", "."));
                } else {
                     preco = 0;
                }

                if ((rst.getString("VFD_MARGEM") != null) &&
                        (!rst.getString("VFD_MARGEM").trim().isEmpty())) {
                    margem = Double.parseDouble(rst.getString("VFD_MARGEM").replace(",", "."));
                } else {
                    margem = 0;
                }
               
            
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
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
    
    public Map<Double, ProdutoVO> carregarEstoqueProdutoGuiaSistemas(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Double, ProdutoVO> vProduto = new HashMap<>();
        double saldo = 0, idProduto;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();    
            sql.append("SELECT VFD_CODPRODUTO, COALESCE(VFD_QTDLOJA,0) AS VFD_QTDLOJA");
            sql.append("  FROM TAB_ESTOQUEATUAL ");
            sql.append(" WHERE VFD_CODFILIAL = "+id_lojaCliente);
            sql.append("   AND VFD_QTDLOJA>0 ");          
           
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("VFD_CODPRODUTO"));
                
                if ((rst.getString("VFD_QTDLOJA") != null) &&
                        (!rst.getString("VFD_QTDLOJA").trim().isEmpty()) &&
                        (rst.getDouble("VFD_QTDLOJA") < 100000000000.0)) {
                    saldo = rst.getDouble("VFD_QTDLOJA");    
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
    
    public Map<Long, ProdutoVO> carregarCodigoBarras() throws SQLException, Exception {
        StringBuilder sql, sql2 = null;
        Statement stm, stmPostgres = null;
        ResultSet rst, rst2;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto;
        long codigobarras;
        Utils util = new Utils();
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            stmPostgres = Conexao.createStatement();            
            
            sql = new StringBuilder();     
            sql.append("SELECT COALESCE(BALANCA.VFD_CODPRODUTOEAN,0) as BALANCA, PROD.VFD_CODPRODUTO, EMB.VFD_CODBARRA, EMB.VFD_QTDEMBALAGEM ");
            sql.append("From tab_produto as prod "); 
            sql.append("LEFT JOIN tab_EMBALAGEM AS EMB ON   ");
            sql.append("	EMB.VFD_CODPRODUTO = prod.vfd_codproduto  ");
            sql.append("LEFT JOIN tmp_ListProdBalanca AS BALANCA ON   ");
            sql.append("	BALANCA.VFD_CODPRODUTO = prod.vfd_codproduto ");
            sql.append("ORDER BY 2            ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("vfd_codproduto"));

                if ((rst.getString("vfd_codbarra") != null) &&
                        (!rst.getString("vfd_codbarra").trim().isEmpty())) {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("vfd_codbarra").replace(".", "").trim()));
                } else {
                    codigobarras = Long.parseLong(util.formataNumero(rst.getString("vfd_codproduto").replace(".", "").trim()));
                }
                
                if ((rst.getString("vfd_qtdembalagem") != null) &&
                        (!rst.getString("vfd_qtdembalagem").trim().isEmpty())) {
                    qtdeEmbalagem = (int) rst.getDouble("vfd_qtdembalagem");
                } else {
                    qtdeEmbalagem = 1;
                }
                
                sql2 = new StringBuilder();
                sql2.append("select codigo, descricao, pesavel, validade ");
                sql2.append("from implantacao.produtobalanca ");
                sql2.append("where codigo = " + rst.getString("BALANCA").replace(".", ""));
                rst2 = stmPostgres.executeQuery(sql2.toString());    
                
                if ((String.valueOf(codigobarras).length() >= 7) && (!rst2.next())) {
                
                    if (String.valueOf(codigobarras).length() > 14) {
                        codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                    }                    
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = (int) idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.codigoBarras = codigobarras;
                    oAutomacao.qtdEmbalagem = qtdeEmbalagem;
                    oProduto.vAutomacao.add(oAutomacao);
 
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    oCodigoAnterior.codigoatual = idProduto;
                    if((rst.getString("vfd_codbarra")!=null) && (!rst.getString("vfd_codbarra").trim().isEmpty())){
                       oCodigoAnterior.barras = Long.parseLong(util.formataNumero(rst.getString("vfd_codbarra").replace(".", "").trim()));
                    } else {
                       oCodigoAnterior.barras = 0; 
                    }
                    oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                    vProduto.put(codigobarras, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    public List<ProdutoVO> carregarPisCofinsGuiaSistemas() throws SQLException, Exception {
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
    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Clientes...");
                List<ClientePreferencialVO> vClientePreferencial = carregarClienteGuiaSistemas(idLoja, idLojaCliente);
                new PlanoDAO().salvar(idLoja);
                new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

            } catch (Exception ex) {

                throw ex;
            }
        }  

    public void importarFornecedor() throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Fornecedor...");
                List<FornecedorVO> vFornecedor = carregarFornecedorGuiaSistemas();

                new FornecedorDAO().salvar(vFornecedor);

            } catch (Exception ex) {

                throw ex;
            }
        }
    
    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoGuiaSistemas();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico = new ArrayList<>();

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoGuiaSistemas(1, true);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoGuiaSistemas(2, true);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoGuiaSistemas(3, true);
            new MercadologicoDAO().salvar(vMercadologico, false);

        } catch (Exception ex) {

            throw ex;
        }
    }   

    public void importarProduto(int id_loja, int id_lojaCliente) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados... Produtos...");
            Map<Double, ProdutoVO> vProdutoGuiaSistemas = carregarProdutoGuiaSistemas(id_loja,id_lojaCliente);
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoGuiaSistemas.size());
            
            for (Double keyId : vProdutoGuiaSistemas.keySet()) {
                
                ProdutoVO oProduto = vProdutoGuiaSistemas.get(keyId);

                oProduto.idProdutoVasilhame = -1;
                oProduto.excecao = -1;
                oProduto.idTipoMercadoria = -1;

                vProdutoNovo.add(oProduto);
                
                
                ProgressBar.next();
            }
            
            //produto.implantacaoExterna = true;
            produto.salvar(vProdutoNovo, id_loja, vLoja);
            
        } catch(Exception ex) {
            
            throw ex;
        }
    }
    
    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoGuiaSistemas(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
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
    
    public void importarPrecoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Double, ProdutoVO> vPrecoProduto = carregarPrecoProdutoGuiaSistemas(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            
            for (Double keyId : vPrecoProduto.keySet()) {
                
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
            Map<Double, ProdutoVO> vEstoqueProduto = carregarEstoqueProdutoGuiaSistemas(id_loja, id_lojaCliente);
            
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
            
            produto.addCodigoBarras(vProdutoNovo);
            
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
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedorGuiaSistemas();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberCliente(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteGuiaSistemas(idLoja, idLojaCliente);

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
    
    public void importarPisCofinsProdutoGuiaSistemas() throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...Pis Cofins...Natureza Receita...");
            List<ProdutoVO> vProduto = carregarPisCofinsGuiaSistemas();
            
            new ProdutoDAO().alterarPisCofinsProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutoFamiliaGuiaSistemas() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Família Produto...");
            List<ProdutoVO> vProduto = carregarProdutoFamiliaGuiaSistemas();
            
            new ProdutoDAO().acertarFamiliaProdutoSysPdv(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarTipoEmbalagem() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Tipo Embalagem...");
            List<ProdutoVO> vProduto = carregarTipoEmbalagemGuiaSistemas();
            
            new ProdutoDAO().alterarTipoEmbalagemSysPdv(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    // FUNÇÕES
    private int retornarAliquotaICMSGuiaSistemas(int codTrib) {      
        int retorno=-1;
        if (TributacaoICMSGuiaSistemas.getTributacaoICMSParana().get(codTrib) == null){
           retorno=8;
        }else{
           retorno=TributacaoICMSGuiaSistemas.getTributacaoICMSParana().get(codTrib);
        }
        if (retorno == -1){
            retorno=8;
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
}