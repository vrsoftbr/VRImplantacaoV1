package vrimplantacao.dao.interfaces;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.classe.Global;
import vrimplantacao.dao.cadastro.CestDAO;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
import vrimplantacao.dao.cadastro.FamiliaProdutoDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoAnteriorDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.OfertaDAO;
import vrimplantacao.dao.cadastro.PlanoDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.dao.cadastro.ProdutoFornecedorDAO;
import vrimplantacao.dao.cadastro.ReceberChequeDAO;
import vrimplantacao.dao.cadastro.ReceberCreditoRotativoDAO;
import vrimplantacao.gui.assistente.mapamercadologico.MercadologicoMapaAdapter;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.CestVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FamiliaProdutoVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoMapaVO;
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
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;

public class SysPdvSqlServer2DAO implements MercadologicoMapaAdapter {

    public List<ItemComboVO> getTipoDocumento() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select TPDCOD, TPDDES from TIPODOCUMENTO ORDER BY TPDCOD"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("TPDCOD"), 
                            rst.getString("TPDCOD") + " - " + 
                            rst.getString("TPDDES")));
                }
            }
        }        
        return result;
    }
    
    public List<ProdutoVO> carregarProdutoAutomacaoLoja(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        double idProduto = 0, precoVenda = 0;
        int qtdEmbalagem = 0;
        
        try {
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();
            sql.append("select procod, proprcvda2, proqtdminprc2\n" +
                        "from produto\n" +
                        "where prounid <> 'KG'\n" +
                        "and proprcvda2 > 0\n" +
                        "and proqtdminprc2 > 1");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                idProduto = rst.getDouble("procod");                
                precoVenda = rst.getDouble("proprcvda2");
                qtdEmbalagem = rst.getInt("proqtdminprc2");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                ProdutoAutomacaoLojaVO oAutomacaoLoja = new ProdutoAutomacaoLojaVO();
                oAutomacaoLoja.qtdEmbalagem = qtdEmbalagem;
                oAutomacaoLoja.codigobarras = -1;                
                oAutomacaoLoja.precovenda = precoVenda;
                oProduto.vAutomacaoLoja.add(oAutomacaoLoja);
                vProduto.add(oProduto);
            }
            
            stm.close();
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void importarProdutoAutomacaoLoja(int idLoja) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...Produtos Atacado...");
            vProduto = carregarProdutoAutomacaoLoja(idLoja);
            if (!vProduto.isEmpty()) {
                new ProdutoDAO().addCodigoBarrasAtacado(vProduto);
            }
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public Map<Long, ProdutoVO> carregarCodigoBarrasEmBranco() throws SQLException, Exception {
        StringBuilder sql = null;
        Statement stmPostgres = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int qtdeEmbalagem;
        double idProduto = 0;
        long codigobarras = -1;
        Utils util = new Utils();

        try {

            stmPostgres = Conexao.createStatement();

            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem ");
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto = Double.parseDouble(rst.getString("id"));

                if ((rst.getInt("id_tipoembalagem") == 4) || (idProduto <= 9999)) {
                    codigobarras = util.gerarEan13((int) idProduto, false);
                } else {
                    codigobarras = util.gerarEan13((int) idProduto, true);
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
    
    /**
     * Importar somente os produtos ativos
     */
    private boolean importarSomenteAtivos = false;

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

                if ((rst.getString("PROPESVAR") != null)
                        && (!rst.getString("PROPESVAR").trim().isEmpty())) {

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
    
    public List<ProdutoVO> carregarProdutoFamilia() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery("select procodsim, procod from item_similares")) {            
                while (rst.next()) {

                    ProdutoVO oProduto = new ProdutoVO();

                    oProduto.idDouble = rst.getDouble("procod");
                    //oProduto.setId(rst.getInt("procod"));
                    oProduto.setIdFamiliaProduto(rst.getInt("procodsim"));

                    vProduto.add(oProduto);
                }
            }
            
        }
        
        return vProduto;
    }
    
    public List<ClientePreferencialVO> carregarClienteSysPdv(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone1, inscricaoestadual, email, enderecoEmpresa,  
                   dataResidencia,  dataCadastro , numero, complemento, dataNascimento, nomePai, nomeMae,
                   telefone2 = "", fax = "", empresa = "", telEmpresa = "", cargo = "",
                   conjuge = "", orgaoExp = "";
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id_situacaocadastro,
                estadoCivil = 0;
            long cnpj, cep, id;
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
                sql.append("clisal,cliestciv,clicjg,clirgexp,clipais,clicodigoibge, cliprz ");
                sql.append("from cliente ");
                sql.append("where clicod > '0' ");

                rst = stm.executeQuery(sql.toString());

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
                            nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        
                        if ((rst.getString("cliend") != null) &&
                                (!rst.getString("cliend").trim().isEmpty())) {
                            endereco = Utils.acertarTexto(rst.getString("cliend").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }
                        
                        if ((rst.getString("clicpfcgc") != null) &&
                                (!rst.getString("clicpfcgc").trim().isEmpty())) {
                            cnpj = Long.parseLong(Utils.formataNumero(rst.getString("clicpfcgc").trim()));
                        } else {
                            cnpj = id;
                        }
                        
                        if ((rst.getString("clibai") != null) &&
                                (!rst.getString("clibai").trim().isEmpty())) {
                            bairro = Utils.acertarTexto(rst.getString("clibai").trim().replace("'", ""));
                        } else {
                            bairro = "";
                        }
                        
                        if ((rst.getString("clitel") != null) &&
                                (!rst.getString("clitel").trim().isEmpty())) {
                            telefone1 = Utils.formataNumero(rst.getString("clitel").trim());
                        } else {
                            telefone1 = "0";
                        }
                        
                        if ((rst.getString("clicep") != null) &&
                                (!rst.getString("clicep").trim().isEmpty())) {
                            cep = Long.parseLong(Utils.formataNumero(rst.getString("clicep").trim()));
                        } else {
                            cep = Long.parseLong(String.valueOf(Global.Cep));
                        }
                        
                        if ((rst.getString("clicid") != null) &&
                                (!rst.getString("clicid").trim().isEmpty())) {
                            if ((rst.getString("cliest") != null) &&
                                    (!rst.getString("cliest").trim().isEmpty())) {
                                id_municipio = Utils.retornarMunicipioIBGEDescricao(rst.getString("clicid").trim().replace("'", ""), 
                                        rst.getString("cliest").trim().replace("'", ""));
                                
                                if (id_municipio == 0) {
                                    id_municipio = Global.idMunicipio;
                                }
                            } else {
                                id_municipio = Global.idMunicipio;
                            }
                        } else {
                            id_municipio = Global.idMunicipio;
                        }
                        
                        if ((rst.getString("cliest") != null) &&
                                (!rst.getString("cliest").trim().isEmpty())) {
                            id_estado = Utils.retornarEstadoDescricao(
                            rst.getString("cliest").trim().replace("'", ""));
                            
                            if (id_estado == 0) {
                                id_estado = Global.idEstado;
                            } else {
                                id_estado = Global.idEstado;
                            }
                        } else {
                            id_estado = Global.idEstado;
                        }
      
                        if ((rst.getString("clinum") != null) &&
                                (!rst.getString("clinum").trim().isEmpty())) {
                            numero = Utils.acertarTexto(rst.getString("clinum").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        if ((rst.getString("clicmp") != null) &&
                                (!rst.getString("clicmp").trim().isEmpty())) {
                            complemento = Utils.acertarTexto(rst.getString("clicmp").trim().replace("'", ""));
                        } else {
                            complemento = "";
                        }                       
                        
                        if ((rst.getString("clirgcgf") != null) &&
                                (!rst.getString("clirgcgf").trim().isEmpty())) {
                            inscricaoestadual = Utils.acertarTexto(rst.getString("clirgcgf").trim());
                            inscricaoestadual = inscricaoestadual.replace("'", "");
                            inscricaoestadual = inscricaoestadual.replace("-", "");
                            inscricaoestadual = inscricaoestadual.replace(".", "");
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                        
                         if ((rst.getString("clidtcad") != null) &&
                                (!rst.getString("clidtcad").trim().isEmpty())) {                        
                            dataCadastro = rst.getString("clidtcad").substring(0, 10).trim().replace("-", "/");
                         }else{
                             dataCadastro = Util.formatData(new java.util.Date(), "yyyy-MM-dd");
                         }
                        
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
                            nomePai = Utils.acertarTexto(rst.getString("clipai").trim().replace("'", ""));
                        } else {
                            nomePai = "";
                        }
                        
                        if ((rst.getString("climae") != null) &&
                                (!rst.getString("climae").trim().isEmpty())) {
                            nomeMae = Utils.acertarTexto(rst.getString("climae").trim().replace("'", ""));
                        } else {
                            nomeMae = "";
                        }
                        
                        if ((rst.getString("clitel2") != null) &&
                                (!rst.getString("clitel2").trim().isEmpty())) {
                            telefone2 = Utils.formataNumero(rst.getString("clitel2").trim());
                        } else {
                            telefone2 = "";
                        }
                        
                        if ((rst.getString("clifax") != null) &&
                                (!rst.getString("clifax").trim().isEmpty())) {
                            fax = Utils.formataNumero(rst.getString("clifax").trim());
                        } else {
                            fax = "";
                        }
                        
                        /* ((rst.getString("cliobs") != null) &&
                                (!rst.getString("cliobs").trim().isEmpty())) {
                            observacao = util.acertarTexto(rst.getString("cliobs").replace("'", "").trim());
                        } else {
                            observacao = "";
                        }*/
                        
                        if ((rst.getString("cliemail") != null) &&
                                (!rst.getString("cliemail").trim().isEmpty()) &&
                                (rst.getString("cliemail").contains("@"))) {
                            email = Utils.acertarTexto(rst.getString("cliemail").trim().replace("'", ""));
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
                            empresa = Utils.acertarTexto(rst.getString("cliemptrb").trim().replace("'", ""));
                        } else {
                            empresa = "";
                        }
                        
                        if ((rst.getString("cliemptrb") != null) &&
                                (!rst.getString("cliemptrb").trim().isEmpty())) {
                            telEmpresa = Utils.formataNumero(rst.getString("cliemptrb").trim());
                        } else {
                            telEmpresa = "";
                        }
                        
                        if ((rst.getString("cliempcar") != null) &&
                                (!rst.getString("cliempcar").trim().isEmpty())) {
                            cargo = Utils.acertarTexto(rst.getString("cliempcar").replace("'", "").trim());
                        } else {
                            cargo = "";
                        }
                        
                        if ((rst.getString("cliempend") != null) &&
                                (!rst.getString("cliempend").trim().isEmpty())) {
                            enderecoEmpresa = Utils.acertarTexto(rst.getString("cliempend").trim().replace("'", ""));
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
                            conjuge = Utils.acertarTexto(rst.getString("clicjg").trim().replace("'", ""));
                        } else {
                            conjuge = "";
                        }
                        
                        if ((rst.getString("clirgexp") != null) &&
                                (!rst.getString("clirgexp").trim().isEmpty())) {
                            orgaoExp = Utils.acertarTexto(rst.getString("clirgexp").replace("'", "").trim());
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

                        oClientePreferencial.idLong = id;
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
                        oClientePreferencial.setValorlimite(rst.getDouble("clilimcre"));
                        oClientePreferencial.codigoanterior = id;
                        oClientePreferencial.fax = fax;
                        oClientePreferencial.bloqueado = bloqueado;
                        oClientePreferencial.id_situacaocadastro = id_situacaocadastro;
                        oClientePreferencial.telefone2 = telefone2;
                        oClientePreferencial.setObservacao2(rst.getString("cliobs"));
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
                        oClientePreferencial.vencimentocreditorotativo = (int) rst.getDouble("cliprz");
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
            long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;
           

            try {
                stm = ConexaoSqlServer.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("select forcod, fordes, forend, forbai, forcid, forest, ");
                sql.append("fortel, forfax, forcep, fornum, forcmp, forcon, forobs, ");
                sql.append("forfan, forcgc, forcgf, foremail, forpfpj, forpais, forcodibge ");
                sql.append("from fornecedor ");
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
                           razaosocial = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "";
                        }

                        if ((rst.getString("forfan") != null)
                                && (!rst.getString("forfan").isEmpty())) {
                           byte[] bytes = rst.getBytes("forfan");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "";
                        }

                        if ((rst.getString("forcgc") != null)
                                && (!rst.getString("forcgc").isEmpty())) {
                            cnpj = Long.parseLong(Utils.formataNumero(rst.getString("forcgc").trim()));
                        } else {
                            cnpj = Long.parseLong("-1");
                        }

                        if ((rst.getString("forcgf") != null)
                                && (!rst.getString("forcgf").isEmpty())) {
                            inscricaoestadual = Utils.acertarTexto(rst.getString("forcgf").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        if ((rst.getString("forend") != null)
                                && (!rst.getString("forend").isEmpty())) {
                            endereco = Utils.acertarTexto(rst.getString("forend").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("forbai") != null)
                                && (!rst.getString("forbai").isEmpty())) {
                            bairro = Utils.acertarTexto(rst.getString("forbai").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("forcep") != null)
                                && (!rst.getString("forcep").isEmpty())) {
                            cep = Long.parseLong(Utils.formataNumero(rst.getString("forcep").trim()));
                        } else {
                            cep = Long.parseLong(String.valueOf(Global.Cep));
                        }

                        if ((rst.getString("forcid") != null)
                                && (!rst.getString("forcid").isEmpty())) {

                            if ((rst.getString("forest") != null)
                                    && (!rst.getString("forest").isEmpty())) {

                                id_municipio = Utils.retornarMunicipioIBGEDescricao(Utils.acertarTexto(rst.getString("forcid").replace("'", "").trim()),
                                        Utils.acertarTexto(rst.getString("forest").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = Global.idMunicipio;
                                }
                            }
                        } else {
                            id_municipio = Global.idMunicipio;
                        }

                        if ((rst.getString("forest") != null)
                                && (!rst.getString("forest").isEmpty())) {
                            id_estado = Utils.retornarEstadoDescricao(Utils.acertarTexto(rst.getString("forest").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = Global.idEstado;
                            }
                        } else {
                            id_estado = Global.idEstado;
                        }

                        if (rst.getString("forobs") != null) {
                            obs = rst.getString("forobs").trim();
                        } else {
                            obs = "";
                        }

                        datacadastro = "";

                        pedidoMin = 0;

                        ativo = true;

                        if (String.valueOf(cnpj).length() > 11) {
                            id_tipoinscricao = 0;
                        } else {
                            id_tipoinscricao = 1;
                        }

                        if ((rst.getString("fornum") != null) &&
                                (!rst.getString("fornum").trim().isEmpty())) {
                            numero = Utils.acertarTexto(rst.getString("fornum").trim().replace("'", ""));
                        } else {
                            numero = "0";
                        }
                        
                        if ((rst.getString("forcmp") != null) &&
                                (!rst.getString("forcmp").trim().isEmpty())) {
                            complemento = Utils.acertarTexto(rst.getString("forcmp").replace("'", "").trim());
                        } else {
                            complemento = "";
                        }
                        
                        if ((rst.getString("fortel") != null) &&
                                (!rst.getString("fortel").trim().isEmpty())) {
                            telefone = Utils.formataNumero(rst.getString("fortel").trim());
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
                            email = Utils.acertarTexto(rst.getString("foremail").replace("'", ""));
                        } else {
                            email = "";
                        }
                        
                        if ((rst.getString("forfax") != null) &&
                                (!rst.getString("forfax").trim().isEmpty())) {
                            fax = Utils.formataNumero(rst.getString("forfax").trim());
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

        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor;
        double idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    cast (PROCOD as bigint) PROCOD,\n" +
                "    FORCOD,\n" +
                "    PRFREFFOR,\n" +
                "    PRFUNID,\n" +
                "    PRFQTD\n" +
                "from\n" +
                "    PRODUTO_FORNECEDOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();
                    oProdutoFornecedor.setId_fornecedor(rst.getInt("forcod"));
                    oProdutoFornecedor.setId_produtoDouble(rst.getDouble("procod"));               
                    oProdutoFornecedor.setDataalteracao(dataAlteracao);
                    oProdutoFornecedor.setCodigoexterno(rst.getString("prfreffor"));
                    oProdutoFornecedor.setQtdembalagem(rst.getInt("PRFQTD"));                    
                    vProdutoFornecedor.add(oProdutoFornecedor);
                }                
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarMercadologicoSysPdv(int nivel,boolean geraMercadologicoPadrao) throws Exception {
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
                        descricao = Utils.acertarTexto(rst.getString("secdes").replace("'", "").trim());
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
                        descricao = Utils.acertarTexto(rst.getString("grpdes").replace("'", "").trim());
                    } else {
                        descricao = Utils.acertarTexto(rst.getString("secdes").trim().replace("'", ""));
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
                        descricao = Utils.acertarTexto(rst.getString("sgrdes").replace("'", "").trim());
                    } else {
                        if ((rst.getString("grpdes") != null) &&
                                (!rst.getString("grpdes").trim().isEmpty())) {                            
                            descricao = Utils.acertarTexto(rst.getString("grpdes").replace("'", "").trim());
                        } else {                            
                            descricao = Utils.acertarTexto(rst.getString("secdes").replace("'", "").trim());
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
    
    public List<FamiliaProdutoVO> carregarFamilia() throws Exception {

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

                    oFamiliaProduto.setId(Integer.parseInt(rst.getString("procodsim").replace(".", "")));
                    oFamiliaProduto.setDescricao(descricao);
                    oFamiliaProduto.setId_situacaocadastro(1);
                    oFamiliaProduto.setCodigoant(0);

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
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, validade, idCest = -1;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras, dataCadastro = "";
        boolean eBalanca, pesavel = false, aceitaMultipicacaoPdv;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0;
        
        try {
            stmPostgres = Conexao.createStatement();
            stm = ConexaoSqlServer.getConexao().createStatement();
            sql = new StringBuilder();                      
            sql.append("select p.procod, p.procodint, p.prodes, p.prodesrdz, p.seccod, p.trbid, ");
            sql.append("p.prounid, p.proestmin, p.proestmax, p.grpcod, p.sgrcod, p.proncm, p.natcodigo, ");
            sql.append("COALESCE(pa.procodaux,P.PROCOD) as procodaux, p.proprc1, p.proprccst, ");
            sql.append("p.prodatcadinc, p.proiteemb,  p.promrg1,proprcvdavar, p.proforlin, ");
            sql.append("i.trbtabb, i.trbalq, i.trbred, p.procest, vw.pis_cst_e, vw.pis_cst_s, ");
            sql.append("vw.cod_natureza_receita, p.PROPESVAR ");
            sql.append("from produto p ");
            sql.append("left join produtoaux pa on pa.procod =  p.procod ");
            sql.append("inner join tributacao i on i.trbid = p.trbid ");
            sql.append("inner join mxf_vw_pis_cofins vw on vw.codigo_produto = p.procod ");
            sql.append("order by   p.procod, p.proenvbal ");
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                aceitaMultipicacaoPdv = true;
                if ((rst.getString("prodatcadinc") != null) &&
                        (!rst.getString("prodatcadinc").trim().isEmpty())) {
                    dataCadastro = rst.getString("prodatcadinc").substring(0, 10).replace("-", "/");
                } else {
                    dataCadastro = "";
                }
                
                idSituacaoCadastro = Utils.acertarTexto(rst.getString("proforlin")).equals("N") ? 1 : 0;
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
                    if (null != rstPostgres.getString("pesavel").trim()) switch (rstPostgres.getString("pesavel").trim()) {
                        case "U":
                            pesavel = true;
                            idTipoEmbalagem = 0;
                            break;
                        case "P":
                            pesavel = false;
                            idTipoEmbalagem = 4;
                            break;
                    }
                } else {
                    codigoBalanca = -1;
                    eBalanca = false;
                    validade = 0;                    
                    if (null != rst.getString("PROPESVAR").trim()) switch (rst.getString("PROPESVAR").trim()) {
                        case "N":
                            idTipoEmbalagem = 0;
                            pesavel = false;
                            aceitaMultipicacaoPdv = true;
                            break;
                        case "S":
                            idTipoEmbalagem = 4;
                            pesavel = false;
                            aceitaMultipicacaoPdv = true;
                            break;
                        case "P":
                            idTipoEmbalagem = 0;
                            pesavel = true;
                            aceitaMultipicacaoPdv = true;
                            break;
                        case "U":
                            idTipoEmbalagem = 0;
                            pesavel = false;
                            aceitaMultipicacaoPdv = false;
                            break;
                    }
                    
                    idTipoEmbalagem = Utils.converteTipoEmbalagem(rst.getString("prounid"));
                }
                
                if ((rst.getString("prodes") != null) &&
                        (!rst.getString("prodes").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("prodes");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("prodesrdz") != null) &&
                        (!rst.getString("prodesrdz").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("prodesrdz");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descriaoCompleta;

                if (idTipoEmbalagem == 4) {
                    qtdEmbalagem = 1;
                } else {
                    qtdEmbalagem = (int) rst.getDouble("proiteemb");
                }
                
                idFamilia = -1;
                mercadologico1 = (rst.getInt("seccod") == 0 ? 1 : rst.getInt("seccod"));
                mercadologico2 = (rst.getInt("grpcod") == 0 ? 1 : rst.getInt("grpcod"));
                mercadologico3 = (rst.getInt("sgrcod") == 0 ? 1 : rst.getInt("sgrcod"));

                if ((rst.getString("proncm") != null) &&
                        (!rst.getString("proncm").isEmpty()) &&
                        (rst.getString("proncm").trim().length() > 5)) {
                    
                    ncmAtual = Utils.formataNumero(rst.getString("proncm").trim());
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
                    
                if ((rst.getString("procest") != null) &&
                        (!rst.getString("procest").trim().isEmpty())) {
                    idCest = new CestDAO().validar(Integer.parseInt(
                            Utils.formataNumero(rst.getString("procest").trim())));
                } else {
                    idCest = -1;
                }
                
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {                    
                    if ((rst.getString("procodaux") != null) &&
                            (!rst.getString("procodaux").trim().isEmpty())) {
                        
                        codigoBarras = Long.parseLong(Utils.formataNumero(rst.getString("procodaux").trim()));
                        
                        if (codigoBarras < 1000000) {
                            codigoBarras = -2;
                        } else {
                            if (String.valueOf(codigoBarras).length() > 14) {
                                codigoBarras = Long.parseLong(String.valueOf(codigoBarras).substring(0, 14));
                            }
                        }                        
                    } else {
                        codigoBarras = -1;
                    }
                }
                
                idTipoPisCofins = Utils.retornarPisCofinsDebito(rst.getInt("pis_cst_s") == 0 ? -1 : rst.getInt("pis_cst_s"));
                idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(rst.getInt("pis_cst_e") == 0 ? -1 : rst.getInt("pis_cst_e"));
                tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, 
                        (rst.getString("cod_natureza_receita") == null ? "" : rst.getString("cod_natureza_receita").trim()));
                
                idAliquota = Utils.getAliquotaICMS(("".equals(Global.ufEstado) ? "SP" : Global.ufEstado), 
                        rst.getInt("trbtabb"), rst.getDouble("trbalq"), rst.getDouble("trbred"), false);
                                
                precoVenda = rst.getDouble("proprcvdavar");
                custo = rst.getDouble("proprccst");
                margem = rst.getDouble("promrg1");
                
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
                oProduto.aceitaMultiplicacaoPdv = aceitaMultipicacaoPdv;
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
                oProduto.idCest = idCest;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                oComplemento.emiteEtiqueta = true;
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();                
                oAliquota.idEstado = (Global.idEstado == 0 ? 35 : Global.idEstado);
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
                if((rst.getString("procodaux")!=null) && (!rst.getString("procodaux").trim().isEmpty()) && 
                   (Utils.formataNumero(rst.getString("procodaux").replace(".", "").trim()).length() < String.valueOf(Long.MAX_VALUE).length())
                ){                    
                   oCodigoAnterior.barras = Long.parseLong(Utils.formataNumero(rst.getString("procodaux").replace(".", "").trim()));
                } else {
                   oCodigoAnterior.barras = -1;
                }
                
                oCodigoAnterior.naturezareceita = rst.getInt("cod_natureza_receita");
                oCodigoAnterior.piscofinsdebito = (rst.getInt("cst_pis_s") == 0 ? -1 : rst.getInt("cst_pis_e"));
                oCodigoAnterior.piscofinscredito = (rst.getInt("cst_pis_e") == 0 ? -1 : rst.getInt("cst_pis_e"));
                
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
                
                if ((rst.getString("proncm") != null) && 
                        (!rst.getString("proncm").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("proncm").trim().replace(".", "");
                } else {
                    oCodigoAnterior.ncm = "";
                }
                
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                if (this.importarSomenteAtivos) {
                    if (idSituacaoCadastro == 1) {
                        vProduto.put(idProduto, oProduto);
                    }
                } else {
                    vProduto.put(idProduto, oProduto);
                }
            }
            
            stmPostgres.close();
            return vProduto;            
        } catch(Exception ex) {
            throw ex;
        }
    }

    public Map<Integer, ProdutoVO> carregarProdutoMaior6SysPdv() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, idProduto, i = 0;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel = false;
        long codigoBarras = 0;
        double precoVenda, custo, margem, codigoAnterior = 0;
        
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select p.procod, p.procodint, p.prodes, p.prodesrdz, p.seccod, p.trbid, ");
            sql.append("p.prounid, p.proestmin, p.proestmax, p.grpcod, p.sgrcod, p.proncm, p.proforlin, ");
            sql.append("p.natcodigo, pa.procodaux, p.proprc1, p.proprccst, p.prodatcadinc, ");
            sql.append("p.proiteemb,  p.promrg1,proprcvdavar, items.procodsim ");
            sql.append("from produto p ");
            sql.append("left join produtoaux pa on pa.procod =  p.procod ");
            sql.append("left join item_similares items on items.procod =  p.procod ");            
            sql.append("where cast(p.procod as numeric(14,0)) >= 1000000 ");
            sql.append("order by   p.procod, p.proenvbal ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                idSituacaoCadastro = Utils.acertarTexto(rst.getString("proforlin")).equals("N") ? 1 : 0;
                codigoAnterior = Double.parseDouble(rst.getString("procod").trim().replace(".", ""));
                idProduto = 1000000 + i;
                
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

                String unidade = rst.getString("prounid") != null ? rst.getString("prounid").trim() : "";
                
                if (rstPostgres.next()) {

                    eBalanca = true;
                    codigoBalanca = rstPostgres.getInt("codigo");

                    if ("U".equals(unidade)) {
                        pesavel = true;
                        idTipoEmbalagem = 0;
                    } else if ("P".equals(unidade)) {
                        pesavel = false;
                        idTipoEmbalagem = 4;
                    }
                } else {
                    codigoBalanca = -1;
                    eBalanca = false;
                    pesavel = false;
                    idTipoEmbalagem = Utils.converteTipoEmbalagem(rst.getString("prounid"));
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
                /*if ((rst.getString("procodsim") != null) &&
                        (!rst.getString("procodsim").trim().isEmpty())) {
                    if (!".650".equals(rst.getString("procodsim").trim())) {
                        idFamilia = Integer.parseInt(rst.getString("procodsim").trim());
                    } else {
                        idFamilia = -1;
                    }
                } else {
                    idFamilia = -1;
                }        */        
                
                mercadologico1 = Integer.parseInt(Utils.formataNumero(rst.getString("seccod")));
                mercadologico2 = Integer.parseInt(Utils.formataNumero(rst.getString("grpcod")));
                mercadologico3 = Integer.parseInt(Utils.formataNumero(rst.getString("sgrcod")));
                
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
                            if ((idProduto >= 10000) && (!"KG".equals(unidade.trim()))) {                                      
                                codigoBarras = util.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13(idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(util.formataNumero(rst.getString("procodaux").trim()));
                        }
                    } else {
                        codigoBarras = -1;
                    }
                }
                
                idTipoPisCofins = 1;
                
                idTipoPisCofinsCredito = 13;
                
                tipoNaturezaReceita = -1;
                
                idAliquota = 8;                
                                
                if ((rst.getString("proprc1") != null) &&
                        (!rst.getString("proprc1").trim().isEmpty())) {
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
                oProduto.margem = margem;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                oComplemento.custoComImposto = custo;
                oComplemento.custoSemImposto = custo;
                
                oProduto.vComplemento.add(oComplemento);
                
                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                
                oAliquota.idEstado = Global.idEstado;
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
                
                if (this.importarSomenteAtivos) {
                    if (idSituacaoCadastro == 1) {
                        vProduto.put(idProduto, oProduto);
                    }
                } else {
                    vProduto.put(idProduto, oProduto);
                }
                
                
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
        double custoComImposto = 0, custoSemImposto = 0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select p.procod, p.proprccst, PROPRCCSTFIS, PROPRCCSTMED ");
            sql.append("from produto p ");
            sql.append("order by p.procod ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Double.parseDouble(rst.getString("procod").replace(".", ""));
                
                if ((rst.getString("proprccst") != null) &&
                        (!rst.getString("proprccst").trim().isEmpty())) {
                    custoComImposto = Double.parseDouble(rst.getString("proprccst").replace(",", "."));                    
                } else {
                    custoComImposto = 0;
                }

                if ((rst.getString("PROPRCCSTFIS") != null) &&
                        (!rst.getString("PROPRCCSTFIS").trim().isEmpty())) {
                    custoSemImposto = Double.parseDouble(rst.getString("PROPRCCSTFIS").replace(",", "."));                    
                } else {
                    custoSemImposto = 0;
                }
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.idDouble = idProduto;
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLoja;
                oComplemento.custoComImposto = custoComImposto;
                oComplemento.custoSemImposto = custoSemImposto;
                
                oProduto.vComplemento.add(oComplemento);                
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                
                oCodigoAnterior.custocomimposto = custoComImposto;                
                oCodigoAnterior.custosemimposto = custoSemImposto;
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
        Statement stm = null;
        ResultSet rst = null;
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
                    "SELECT\n"
                    + "  R.CXANUM,\n"
                    + "  R.CTRNUM,\n"
                    + "  R.CTRDATEMI,\n"
                    + "  R.CTRDATVNC,\n"
                    + "  R.CTROBS,\n"
                    + "  R.CTRNUMBCO,\n"
                    + "  c.CLICOD,\n"
                    + "  C.CLIDES,\n"
                    + "  C.CLICPFCGC,\n"
                    + "  R.CTRVLRDEV,\n"
                    + "  C.CLIRGCGF,\n"
                    + "  C.CLITEL,\n"
                    + "  C.CLIPFPJ\n"
                    + "FROM CONTARECEBER r\n"
                    + "inner join cliente c on c.clicod = r.clicod\n"
                    + "WHERE\n"
                    + "(COALESCE(CTRVLRPAG,0) < CTRVLRNOM) AND\n"
                    + "COALESCE(ctrvlrdev,0) > 0\n"
                    + "and c.CLIDES like '%CHEQUE%'"
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
                oReceberCheque.setId_loja(id_loja);
                oReceberCheque.setEcf(ecf);
                oReceberCheque.setId_tipoalinea(id_tipoalinea);
                oReceberCheque.setData(dataemissao);
                oReceberCheque.setDatadeposito(datavencimento);
                oReceberCheque.setCpf(cpfCnpj);
                oReceberCheque.setNumerocheque(cheque);
                oReceberCheque.setId_banco(idBanco);
                oReceberCheque.setAgencia(agencia);
                oReceberCheque.setConta(conta);
                oReceberCheque.setNumerocupom(numerocupom);
                oReceberCheque.setValor(valor);
                oReceberCheque.setObservacao(observacao);
                oReceberCheque.setRg(rg);
                oReceberCheque.setTelefone(telefone);
                oReceberCheque.setNome(nome);
                oReceberCheque.setId_tipoinscricao(idTipoInscricao);
                oReceberCheque.setDatadeposito(datavencimento);
                oReceberCheque.setValorjuros(juros);
                oReceberCheque.setValorinicial(valor);

                vReceberCheque.add(oReceberCheque);
            }
            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    /**
     * Retorna uma listagem com crédito rotativo do cliente.
     * @param idLojaVR Id da loja no VR.
     * @param idLojaCliente Id da loja no banco cliente.
     * @param incluirChequesNoRotativo Se for afirmativo, todos itens em aberto 
     * no contas a receber, incluindo cheques, serão importados como crédito
     * rotativo.
     * @param tipoDocumento
     * @return Listagem do crédito rotativo.
     * @throws Exception 
     */
    public List<ReceberCreditoRotativoVO> carregarReceberClienteSysPdv(int idLojaVR, int idLojaCliente, 
            boolean incluirChequesNoRotativo) throws Exception {
        
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
                    "SELECT\n"
                    + "r.FZDCOD,\n"
                    + "r.CTRID,\n"
                    + "r.ctrnum,\n"
                    + "r.clicod,\n"
                    + "r.cxanum,\n"
                    + "r.ctrdatemi,\n"
                    + "r.ctrdatvnc,\n"
                    + "r.ctrvlrdev,\n"
                    + "r.ctrobs,\n"
                    + "c.clicpfcgc\n"
                    + "FROM CONTARECEBER r\n"
                    + "inner join cliente c on c.clicod = r.clicod\n"
                    + "WHERE\n"
                    + "(COALESCE(CTRVLRPAG,0) < CTRVLRNOM) AND\n"
                    + "COALESCE(ctrvlrdev,0) > 0\n"
                    + "and c.CLIDES not like '%CHEQUE%'"
            );
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = Long.parseLong(Utils.formataNumero(rst.getString("clicod"))); 
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
                    observacao = Utils.acertarTexto(rst.getString("ctrobs").replace("'", "").trim());
                } else { 
                    observacao = "IMPORTADO VR";
                }
                
                oReceberCreditoRotativo.id_clientepreferencial = 0;
                oReceberCreditoRotativo.idClientePreferencialLong = id_cliente;
                oReceberCreditoRotativo.id_loja = idLojaVR;
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
            sql.append("from produto p ");
            sql.append("order by p.procod ");

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
                
                idProduto = Long.parseLong(Utils.formataNumero(rst.getString("procod")));
                
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
        String strCodigoBarras;
        long codigobarras;
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select distinct p.procod, p.procod procodaux "
                            + "from produto p "
                            + "where cast(p.procod as bigint) > 999999 "
                            + "union\n" +
                    "select distinct pa.procod, pa.procodaux ean from produtoaux pa"*/
                    "select p.procod, p.procodaux\n"
                    + "from produtoaux p"                    
            )) {
            
                while (rst.next()) {

                    double idProduto = rst.getDouble("procod");
                    strCodigoBarras = Utils.formataNumero(rst.getString("procodaux"));
                    codigobarras = -2;
                    if (strCodigoBarras.length() >= 7) {
                        if (strCodigoBarras.length() > 14) {
                            codigobarras = Utils.stringToLong(strCodigoBarras.substring(0, 14));
                        } else {
                            codigobarras = Utils.stringToLong(strCodigoBarras);
                        }                   

                        if (String.valueOf(codigobarras).length() >= 7) {
                        
                            ProdutoVO oProduto = new ProdutoVO();
                            oProduto.idDouble = idProduto;
                            oProduto.setId((int) idProduto);
                            ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                            oAutomacao.setCodigoBarras(codigobarras);
                            oProduto.vAutomacao.add(oAutomacao);
                            vProduto.put(codigobarras, oProduto);
                            CodigoAnteriorVO oAnterior = new CodigoAnteriorVO();
                            oAnterior.setBarras(codigobarras);
                        }
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
        int idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita, 
            idTipoPisCofinsCodigoAnterior, idTipoPisCofinsCreditoCodigoAnterior, tipoNaturezaReceitaCodigoAnterior;
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
                    idTipoPisCofins = Utils.retornarPisCofinsDebito(Integer.parseInt(rst.getString("IMPFEDSTSAI").trim()));
                    idTipoPisCofinsCodigoAnterior = idTipoPisCofins;//util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("IMPFEDSTSAI").trim()));
                } else {
                    idTipoPisCofins = 1;
                    idTipoPisCofinsCodigoAnterior = -1;
                }
                
                if ((rst.getString("IMPFEDST") != null) &&
                        (!rst.getString("IMPFEDST").trim().isEmpty())) {
                    idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(Integer.parseInt(rst.getString("IMPFEDST").trim()));
                    idTipoPisCofinsCreditoCodigoAnterior = idTipoPisCofinsCredito;//util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("IMPFEDST").trim()));
                } else {
                    idTipoPisCofinsCredito = 13;
                    idTipoPisCofinsCreditoCodigoAnterior = -1;                    
                }
                
                if ((rst.getString("NATCODIGO") != null) &&
                        (!rst.getString("NATCODIGO").trim().isEmpty())) {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            Utils.acertarTexto(rst.getString("NATCODIGO").trim()));
                    tipoNaturezaReceitaCodigoAnterior = tipoNaturezaReceita/*util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            util.acertarTexto(rst.getString("NATCODIGO").trim()))*/;                    
                } else {
                    tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(idTipoPisCofins, "");
                    tipoNaturezaReceitaCodigoAnterior = -1;                    
                }
                
                idProduto = Double.parseDouble(rst.getString("PROCOD"));
                
                oProduto.idDouble = idProduto;
                oProduto.idTipoPisCofinsDebito = idTipoPisCofins;
                oProduto.idTipoPisCofinsCredito = idTipoPisCofinsCredito;
                oProduto.tipoNaturezaReceita = tipoNaturezaReceita;
                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                oCodigoAnterior.codigoatual = idProduto;
                oCodigoAnterior.piscofinscredito = idTipoPisCofinsCreditoCodigoAnterior;
                oCodigoAnterior.piscofinsdebito = idTipoPisCofinsCodigoAnterior;                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceitaCodigoAnterior;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                
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
    
    public void importarFamilia() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamilia();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico;

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            vMercadologico = carregarMercadologicoSysPdv(1,true);
            new MercadologicoDAO().salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoSysPdv(2,true);
            new MercadologicoDAO().salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoSysPdv(3,true);
            new MercadologicoDAO().salvar(vMercadologico, false);
            
            new MercadologicoAnteriorDAO().reorganizaMercadologico();
            
            new MercadologicoDAO().salvarMax();

        } catch (Exception ex) {

            throw ex;
        }
    }   

    public void importarProduto6(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
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
        }
    }

    public void importarProdutoMaior6(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos Maior 6 digitos...");
            Map<Integer, ProdutoVO> vProdutoSysPdv = carregarProdutoMaior6SysPdv();
            
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
        }
    }
    
    public void importarCustoProduto(int id_loja, int id_lojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Double, ProdutoVO> vCustoProduto = carregarCustoProdutoSysPdv(id_loja, id_lojaCliente);
            
            ProgressBar.setMaximum(vCustoProduto.size());
            for (Double keyId : vCustoProduto.keySet()) {
                ProdutoVO oProduto = vCustoProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            
            produto.alterarCustoProdutoRapido(vProdutoNovo, id_loja);
            
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
            
            ProgressBar.setMaximum(vPrecoProduto.size());
            for (Double keyId : vPrecoProduto.keySet()) {
                ProdutoVO oProduto = vPrecoProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            
            produto.alterarPrecoProdutoRapido(vProdutoNovo, id_loja);
            
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
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Double keyId : vEstoqueProduto.keySet()) {
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);
                vProdutoNovo.add(oProduto);
                ProgressBar.next();
            }
            produto.alterarEstoqueProdutoRapido(vProdutoNovo, id_loja);
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
            
            produto.alterarBarraAnterio = true;
            produto.addCodigoBarras(vProdutoNovo);            
        } catch(Exception ex) {            
            throw ex;
        }        
    }    
    
    private void addCodigoBarras(List<ProdutoVO> v_produto) throws Exception {
        try {
            Conexao.begin();

            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Atualizando dados produto...Codigo Barra2...");
            
            class Temp {
                int id = -1;
                int id_tipoembalagem = 0;
                boolean e_balanca = false;
            }
            
            MultiMap<Double, Temp> anteriores = new MultiMap<>(1);
            {
                StringBuilder sql = new StringBuilder();
                sql.append("select p.id, id_tipoembalagem, e_balanca, codigoanterior from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            sql.toString()
                    )) {
                        while (rst.next()) {
                            Temp tp = new Temp();
                            tp.id = rst.getInt("id");
                            tp.id_tipoembalagem = rst.getInt("id_tipoembalagem");
                            tp.e_balanca = rst.getBoolean("e_balanca");
                            anteriores.put(tp, rst.getDouble("codigoanterior"));
                        }
                    }
                }
            }
            Set<Long> eansExistentes = new LinkedHashSet<>();
            {
                try (Statement stm = Conexao.createStatement()) {
                    try (ResultSet rst = stm.executeQuery(
                            "select codigobarras from produtoautomacao"
                    )) {
                        while (rst.next()) {
                            eansExistentes.add(rst.getLong("codigobarras"));
                        }
                    }
                }
            }

            int cont = 0;
            try (Statement stm = Conexao.createStatement()) {
                for (ProdutoVO i_produto : v_produto) {
                    for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {

                        if (String.valueOf(oAutomacao.getCodigoBarras()).length() >= 7
                                && String.valueOf(oAutomacao.getCodigoBarras()).length() <= 14) {

                            Temp temp = anteriores.get(i_produto.idDouble);

                            if (temp != null) {
                                if (!temp.e_balanca) {

                                    if (!eansExistentes.contains(oAutomacao.codigoBarras)) {
                                        int qtdEmbalagem = oAutomacao.qtdEmbalagem;
                                        if (qtdEmbalagem <= 0) {
                                            qtdEmbalagem = 1;
                                        }

                                        SQLBuilder sql = new SQLBuilder();
                                        sql.setTableName("produtoautomacao");
                                        sql.put("id_produto", temp.id);
                                        sql.put("codigobarras", oAutomacao.codigoBarras);
                                        sql.put("qtdembalagem", qtdEmbalagem);
                                        sql.put("id_tipoembalagem", (oAutomacao.idTipoEmbalagem == -1 ? temp.id_tipoembalagem : oAutomacao.idTipoEmbalagem));

                                        SQLBuilder sql2 = new SQLBuilder();
                                        sql2.setTableName("codigoanterior");
                                        sql2.setSchema("implantacao");
                                        sql2.put("barras", (oAutomacao.codigoBarras > 0
                                                        ? oAutomacao.codigoBarras + ""
                                                        : null));
                                        sql2.setWhere("codigoatual = " + temp.id);

                                        try {
                                            stm.execute(sql.getInsert() + ";" + sql2.getUpdate());
                                        } catch (Exception e) {
                                            Util.exibirMensagem(sql.getInsert() + ";" + sql2.getUpdate(), "");
                                            throw e;
                                        }
                                        eansExistentes.add(oAutomacao.codigoBarras);
                                        cont++;
                                    }
                                }
                            }
                        }
                    }
                    ProgressBar.next();
                }
            }
            Util.exibirMensagem("Executados: " + cont, "");
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
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
            new ProdutoFornecedorDAO().salvar2(vProdutoFornecedor);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarReceberCliente(int idLoja, int idLojaCliente, boolean incluirChequesNoRotativo) 
            throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteSysPdv(idLoja, idLojaCliente, incluirChequesNoRotativo);
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
    
    public void importarProdutoFamilia() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Família Produto...");
            List<ProdutoVO> vProduto = carregarProdutoFamilia();
            
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
    
    public void importarIcmsProduto() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Icms...");
            List<ProdutoVO> vProduto = carregarIcmsProduto();
            
            new ProdutoDAO().alterarICMSProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }
    
    public void corrigirClienteDuplicado() throws Exception {

        try {
            ProgressBar.setStatus("Corrigindo dados...Cliente Duplicados...");
            new ClientePreferencialDAO().corrigirClienteDuplicado();
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
                        //"    ncm.ncmcest\n" +
                        "    p.procest\n" +
                        "from\n" +
                        "    produto p\n" +
                        //"    left join ncm on p.proncm = ncm.ncmcod\n" +
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

    public void importarOfertas(int idLojaVR, int idLojaCliente, String data) throws Exception {
        ProgressBar.setStatus("Carregando dados das ofertas");
        List<OfertaVO> ofertas = carregarOfertas(idLojaVR, idLojaCliente, data);
        
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }
    
    private List<OfertaVO> carregarOfertas(int idLojaVR, int idLojaCliente, String data) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        data = data.replace("/", ".");
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select\n" +
                    "	cast(ep.PROCOD as integer) id_produto,\n" +
                    "	cast('" + data + "' as date) as datainicio,\n" +
                    "	cast(e.ENCDATFIM as date) datatermino,\n" +
                    "	ep.ENCPROPRCOFE precooferta\n" +
                    "from \n" +
                    "	ENCARTE_PRODUTO ep\n" +
                    "	join ENCARTE e on\n" +
                    "		ep.ENCCOD = e.ENCCOD\n" +
                    "where\n" +
                    "	e.ENCDATFIM >= '" + data + "'\n" +
                    "order by\n" +
                    "	id_produto"*/
                    "select PROCOD as id_produto,\n"
                    + "cast('" + data + "' as date) as datainicio,\n"
                    + "PPRDATFIM as datatermino,\n"
                    + "PPRPRCPROG as precooferta \n"
                    + "from PRECO_PROGRAMADO "
                    + "where PPRDATFIM >= '"+data+"'"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produtoDouble(rst.getDouble("id_produto"));
                    vo.setDatainicio(rst.getString("datainicio"));
                    vo.setDatatermino(rst.getString("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }
    
    private List<OfertaVO> carregarOfertaSemData(int idLojaVR, int idLojaCliente) throws Exception{
        List<OfertaVO> ofertas = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.procod id_produto,\n" +
                    "    current_date + 1 datainicio,\n" +
                    "    cast('31.12.2016' as date) datatermino,\n" +
                    "    p.proprcofevar precooferta\n" +
                    "from\n" +
                    "    produto p\n" +
                    "where\n" +
                    "    p.proprcofevar > 0.01\n" +
                    "order by\n" +
                    "    p.procod"
            )) {
                while (rst.next()) {
                    OfertaVO vo = new OfertaVO();
                    vo.setId_loja(idLojaVR);
                    vo.setId_produtoDouble(rst.getLong("id_produto"));
                    vo.setDatainicio(rst.getString("datainicio"));
                    vo.setDatatermino(rst.getString("datatermino"));
                    vo.setPrecooferta(rst.getDouble("precooferta"));
                    ofertas.add(vo);
                }
            }
        }
        
        return ofertas;
    }
    
    private List<ProdutoVO> carregarProdutos(int idLojaVR, int idLojaCliente) throws Exception{
        List<ProdutoVO> result = new ArrayList();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select PRPUF from PROPRIO WHERE PRPCOD = " + idLojaCliente
            )) {
                if (rst.next()) {
                    Global.ufEstado = rst.getString("PRPUF");
                    Global.idEstado = Utils.getEstadoPelaSigla(Global.ufEstado);
                }
            }
        }
        int cont = 1;
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
                    "  p.proprc1,\n" +
                    "  p.proprccst,\n" +
                    "  p.PROPRCCSTFIS, p.PROPRCCSTMED, " +
                    "  p.prodatcadinc,\n" +
                    "  p.proiteemb,\n" +
                    "  p.promrg1,\n" +
                    "  proprcvdavar,\n" +
                    "  items.procodsim,\n" +
                    "  p.procest\n," +
                    "  p.propesbrt\n," +
                    "  p.propesliq\n," +
                    "  case p.propesvar\n" +
                    "    when 'S' then 4\n" +
                    "    when 'P' then 4\n" +
                    "    else 1 end as id_tipoembalagem,\n" +
                    "  case when p.proenvbal = 'S' then 1 else 0 end as e_balanca,\n" +
                    "  p.provld validade,\n" +
                    "i.trbtabb, i.trbalq, i.trbred, p.procest, vw.pis_cst_e, vw.pis_cst_s, " +
                    "vw.cod_natureza_receita, p.PROPESVAR " +
                    "FROM produto p\n" +
                    "LEFT JOIN item_similares items\n" +
                    "  ON items.procod = p.procod\n" +
                    "left join tributacao i on i.trbid = p.trbid\n" +
                    "left join mxf_vw_pis_cofins vw on vw.codigo_produto = p.procod" +
                    " ORDER BY cast(p.procod as bigint)"
            )) {         
                int contator = 1;
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
                    long id = Long.parseLong(rst.getString("procod"));
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
                    
                    if ((rst.getString("seccod") != null) &&
                            (!rst.getString("seccod").trim().isEmpty()) &&
                            (Integer.parseInt(rst.getString("seccod")) != 0)) {
                        oProduto.setMercadologico1(Integer.parseInt(Utils.formataNumero(rst.getString("seccod").trim())));
                    } else {
                        oProduto.setMercadologico1(1);
                    }
                    
                    if ((rst.getString("grpcod") != null) &&
                            (!rst.getString("grpcod").trim().isEmpty()) &&
                            (Integer.parseInt(rst.getString("grpcod")) != 0)) {
                        oProduto.setMercadologico2(Integer.parseInt(Utils.formataNumero(rst.getString("grpcod").trim())));
                    } else {
                        oProduto.setMercadologico2(1);
                    }
                    
                    if ((rst.getString("sgrcod") != null) &&
                            (!rst.getString("sgrcod").trim().isEmpty()) &&
                            (Integer.parseInt(rst.getString("sgrcod")) != 0)) {
                        oProduto.setMercadologico3(Integer.parseInt(Utils.formataNumero(rst.getString("sgrcod").trim())));
                    } else {
                        oProduto.setMercadologico3(1);
                    }
                    
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
                    long codigoBarra = Utils.stringToLong(rst.getString("procod"), 0);
                    
                    ProdutoBalancaVO produtoBalanca = null;                    
                    if (codigoBarra > 0 && codigoBarra <= 999999) {
                        produtoBalanca = produtosBalanca.get((int) codigoBarra);
                    }
                    //Se um produto de balança foi loacalizado
                    //if (produtoBalanca != null) {
                    if (produtoBalanca != null) {
                        oAutomacao.setCodigoBarras(-1);                          
                        oProduto.setValidade(produtoBalanca.getValidade() >= 1 ? produtoBalanca.getValidade() : Utils.stringToInt(rst.getString("validade")));
                        
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
                        oProduto.setValidade(Utils.stringToInt(rst.getString("validade")));
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
                    
                    oProduto.setIdTipoPisCofinsDebito(Utils.retornarPisCofinsDebito(rst.getInt("pis_cst_s") == 0 ? -1 : rst.getInt("pis_cst_s")));
                    oProduto.setIdTipoPisCofinsCredito(Utils.retornarPisCofinsCredito(rst.getInt("pis_cst_e") == 0 ? -1 : rst.getInt("pis_cst_e")));
                    oProduto.setTipoNaturezaReceita(Utils.retornarTipoNaturezaReceita(oProduto.getIdTipoPisCofins(), 
                        (rst.getString("cod_natureza_receita") == null ? "" : rst.getString("cod_natureza_receita").trim())));

                    //<editor-fold defaultstate="collapsed" desc="COMPLEMENTO DO PRODUTO">
                    oComplemento.setPrecoVenda(rst.getDouble("proprcvdavar"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("proprcvdavar"));
                    oComplemento.setCustoComImposto(rst.getDouble("proprccst"));
                    oComplemento.setCustoSemImposto(rst.getDouble("PROPRCCSTFIS"));
                    oComplemento.setIdLoja(idLojaVR);
                    oComplemento.setIdSituacaoCadastro(oProduto.getIdSituacaoCadastro());
                    //O ajuste do estoque é feito em outro método.
                    oComplemento.setEstoque(0);
                    oComplemento.setEstoqueMinimo(0);
                    oComplemento.setEstoqueMaximo(0);
                    //</editor-fold>                    
                    
                    oAliquota.setIdEstado(Global.idEstado == 0 ? 35 : Global.idEstado);
                    oAliquota.setIdAliquotaDebito(Utils.getAliquotaICMS(("".equals(Global.ufEstado) ? "SP" : Global.ufEstado), 
                        rst.getInt("trbtabb"), rst.getDouble("trbalq"), rst.getDouble("trbred"), false));
                    oAliquota.setIdAliquotaCredito(Utils.getAliquotaICMS(("".equals(Global.ufEstado) ? "SP" : Global.ufEstado), 
                        rst.getInt("trbtabb"), rst.getDouble("trbalq"), rst.getDouble("trbred"), false));
                    oAliquota.setIdAliquotaDebitoForaEstado(Utils.getAliquotaICMS(("".equals(Global.ufEstado) ? "SP" : Global.ufEstado), 
                        rst.getInt("trbtabb"), rst.getDouble("trbalq"), rst.getDouble("trbred"), false));
                    oAliquota.setIdAliquotaCreditoForaEstado(Utils.getAliquotaICMS(("".equals(Global.ufEstado) ? "SP" : Global.ufEstado), 
                        rst.getInt("trbtabb"), rst.getDouble("trbalq"), rst.getDouble("trbred"), false));
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(Utils.getAliquotaICMS(("".equals(Global.ufEstado) ? "SP" : Global.ufEstado), 
                        rst.getInt("trbtabb"), rst.getDouble("trbalq"), rst.getDouble("trbred"), false));
                    //</editor-fold>
                    
                    //<editor-fold defaultstate="collapsed" desc="CODIGO ANTERIOR">
                    oCodigoAnterior.setCodigoanterior(id);
                    oCodigoAnterior.setMargem(oProduto.getMargem());
                    oCodigoAnterior.setPrecovenda(oComplemento.getPrecoVenda());
                    oCodigoAnterior.setBarras(-2);
                    /*oCodigoAnterior.setBarras(Long.parseLong(
                            Utils.formataNumero(rst.getString("procodaux")).equals("0") ?
                            "-2" :
                            Utils.formataNumero(rst.getString("procodaux"))
                    ));*/
                    oCodigoAnterior.setReferencia((int) oProduto.getId());
                    oCodigoAnterior.setNcm(rst.getString("proncm"));
                    oCodigoAnterior.setId_loja(idLojaVR);
                    oCodigoAnterior.setPiscofinsdebito(Utils.retornarPisCofinsDebito(rst.getInt("pis_cst_s") == 0 ? -1 : rst.getInt("pis_cst_s")));
                    oCodigoAnterior.setPiscofinscredito(Utils.retornarPisCofinsCredito(rst.getInt("pis_cst_e") == 0 ? -1 : rst.getInt("pis_cst_e")));
                    oCodigoAnterior.setNaturezareceita(Utils.stringToInt(rst.getString("cod_natureza_receita")));
                    oCodigoAnterior.setRef_icmsdebito(rst.getString("trbid") == null ? "" : rst.getString("trbid").trim()); 
                    //</editor-fold>
                    
                    if (this.importarSomenteAtivos) {
                        if (oProduto.getIdSituacaoCadastro() == 1) {
                            result.add(oProduto);
                        }
                    } else {
                        result.add(oProduto);
                    }
                    
                    ProgressBar.setStatus("Carregando dados...Produtos... "+contator);
                    contator ++; 
                }
            }
        }
        return result;
    }

    public void setImportarSomenteAtivos(boolean importarSomenteAtivos) {
        this.importarSomenteAtivos = importarSomenteAtivos;
    }
    
    private List<ProdutoVO> carregarIcmsLoja() throws Exception {
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
                int cont = 1;
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
                        ProgressBar.setStatus("Carregando dados...ICMS....." + cont);
                        cont++;
                    }
                }
            }           
        } 
        
        return vProduto;
    }
    
    public void importarIcmsLoja(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...ICMS.....");
        Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
        for(ProdutoVO prod: carregarIcmsLoja()) {
            auxiliar.put(prod.idDouble, prod);
        }
        Util.exibirMensagem("Total de itens: " + auxiliar.size(), "");
        produto.incluirICMSLoja(new ArrayList(auxiliar.values()));
    }

    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        Map<Double, ProdutoVO> auxiliar = new LinkedHashMap<>();
        for(ProdutoVO prod: carregarProdutos(idLojaVR, idLojaCliente)) {
            auxiliar.put(prod.idDouble, prod);
        }
        List<LojaVO> vLoja = new LojaDAO().carregar();
        produto.implantacaoExterna = true;
        produto.salvar(new ArrayList(auxiliar.values()), idLojaVR, vLoja);
        //ProgressBar.setStatus("Acertando balanca...");   
        //new ProdutoDAO().corrigirBalanca(new ArrayList(auxiliar.values()));
    }

    public void corrigirProdutoDeBalanca(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos de Balança....");        
        new ProdutoDAO().corrigirBalanca(carregarProdutos(idLojaVR, idLojaCliente));
    }

    
    
    
    
    
    
    
    //
    //
    //
    //
    //
    //
    //    INTEGRAÇÕES
    //
    //
    //
    //
    //
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public Map<Long, ProdutoVO> carregarIntegCustoPorEAN(int idLojaVR, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new LinkedHashMap<>();
        double custoComImposto = 0, custoSemImposto = 0, idProduto;
        
        try {
            
            stm = ConexaoSqlServer.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("select\n" +
                "    p.procod,\n" +
                "    p.proprccst,\n" +
                "    p.PROPRCCSTFIS, PROPRCCSTMED, "+
                "    pa.procodaux\n" +
                "from\n" +
                "    produto p\n" +
                "    join produtoaux pa on p.procod = pa.procod\n" +
                "order by p.procod");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                String strCodigoBarras = Utils.formataNumero(rst.getString("procodaux"));
                long codigobarras = Long.parseLong(strCodigoBarras);
                
                if (String.valueOf(codigobarras).length() <= 14 && String.valueOf(codigobarras).length() >= 7) {
                
                    idProduto = Double.parseDouble(rst.getString("procod").replace(".", ""));

                    if ((rst.getString("proprccst") != null) &&
                            (!rst.getString("proprccst").trim().isEmpty())) {
                        custoComImposto = Double.parseDouble(rst.getString("proprccst").replace(",", "."));                    
                    } else {
                        custoComImposto = 0;
                    }

                    if ((rst.getString("PROPRCCSTFIS") != null) &&
                            (!rst.getString("PROPRCCSTFIS").trim().isEmpty())) {
                        custoComImposto = Double.parseDouble(rst.getString("PROPRCCSTFIS").replace(",", "."));                    
                    } else {
                        custoComImposto = 0;
                    }
                    
                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.idDouble = idProduto;

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                    oComplemento.idLoja = idLojaVR;
                    oComplemento.custoComImposto = custoComImposto;
                    oComplemento.custoSemImposto = custoSemImposto;

                    oProduto.vComplemento.add(oComplemento);                

                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                    oCodigoAnterior.custocomimposto = custoComImposto;                
                    oCodigoAnterior.custosemimposto = custoSemImposto;
                    oCodigoAnterior.id_loja = idLojaCliente;

                    oProduto.vCodigoAnterior.add(oCodigoAnterior);

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                    oAutomacao.setCodigoBarras(codigobarras);
                    oProduto.getvAutomacao().add(oAutomacao);

                    oProduto.setCodigoBarras(codigobarras);
                    
                    vProduto.put(codigobarras, oProduto);  
                    
                }
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
    }
    
    private List<ProdutoVO> carregarIntegPrecoPorEAN(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        double preco = 0, idProduto;
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.procod,\n" +
                    "    p.proprcvdavar,\n" +
                    "    pa.procodaux\n" +
                    "from\n" +
                    "    produto p\n" +
                    "    join produtoaux pa on p.procod = pa.procod\n" +
                    "order by p.procod"
            )) {            
                while (rst.next()) {

                    String strCodigoBarras = Utils.formataNumero(rst.getString("procodaux"));
                    long codigobarras = Long.parseLong(strCodigoBarras);

                    if (String.valueOf(codigobarras).length() <= 14 && String.valueOf(codigobarras).length() >= 7) {

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
                        oComplemento.idLoja = idLojaVR;
                        oComplemento.precoVenda = preco;
                        oComplemento.precoDiaSeguinte = preco;

                        oProduto.vComplemento.add(oComplemento);                

                        CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                        oCodigoAnterior.precovenda = preco;
                        oCodigoAnterior.id_loja = idLojaCliente;
                        oProduto.vCodigoAnterior.add(oCodigoAnterior);
                        
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.setCodigoBarras(codigobarras);
                        oProduto.getvAutomacao().add(oAutomacao);
                        
                        oProduto.setCodigoBarras(codigobarras);

                        vProduto.add(oProduto);
                    }
                }
            }        
        }
            
        return vProduto;
    }
    
    private List<ProdutoVO> carregarIntegEstoquePorEAN(int idLojaVR, int idLojaCliente) throws Exception{
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        List<ProdutoVO> vProduto = new ArrayList<>();
        double saldo = 0, idProduto;

        stm = ConexaoSqlServer.getConexao().createStatement();
        sql = new StringBuilder();            
        sql.append("select\n" +
            "    p.procod,\n" +
            "    p.estatu,\n" +
            "    pa.procodaux\n" +
            "from\n" +
            "    estoque p\n" +
            "    join produtoaux pa on p.procod = pa.procod\n" +
            "order by p.procod");

        rst = stm.executeQuery(sql.toString());

        while (rst.next()) {

            String strCodigoBarras = Utils.formataNumero(rst.getString("procodaux"));
            long codigobarras = Long.parseLong(strCodigoBarras);

            if (String.valueOf(codigobarras).length() <= 14 && String.valueOf(codigobarras).length() >= 7) {

                idProduto = Long.parseLong(Utils.formataNumero(rst.getString("procod")));

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

                oComplemento.idLoja = idLojaVR;
                oComplemento.setEstoque(saldo);
                oProduto.vComplemento.add(oComplemento);                
                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.setEstoque(saldo);
                oCodigoAnterior.id_loja = idLojaVR;
                oProduto.vCodigoAnterior.add(oCodigoAnterior);
                
                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                oAutomacao.setCodigoBarras(codigobarras);
                oProduto.getvAutomacao().add(oAutomacao);
                
                oProduto.setCodigoBarras(codigobarras);

                vProduto.add(oProduto);
            }

        }

        return vProduto;
    }
   
    public void integImportarCustoSysPDV(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Custo dos produtos por ean....");        
        Map<Long, ProdutoVO> produtos = carregarIntegCustoPorEAN(idLojaVR, idLojaCliente);
        new ProdutoDAO().alterarCustoPorEAN(produtos.values(), idLojaVR);
    }
    
    public void integImportarEstoqueSysPDV(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Estoque dos produtos por ean....");        
        List<ProdutoVO> produtos = carregarIntegEstoquePorEAN(idLojaVR, idLojaCliente);
        new ProdutoDAO().alterarEstoquePorEAN(produtos, idLojaVR);
    }

    public void integImportarPrecoSysPDV(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Preço dos produtos por ean....");        
        List<ProdutoVO> produtos = carregarIntegPrecoPorEAN(idLojaVR, idLojaCliente);
        new ProdutoDAO().alterarPrecoPorEAN(produtos, idLojaVR);
    }    

    public void integClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente preferêncial por CNPJ/CPF....");        
        List<ClientePreferencialVO> clientes = carregarClienteSysPdv(idLojaCliente, idLojaCliente);
        ClientePreferencialDAO dao = new ClientePreferencialDAO();
        dao.unificacao = true;
        dao.salvarVrSoftware(clientes, idLojaVR, idLojaVR);
    }

    public void integCreditoRotativo(int idLojaVR, int idLojaCliente, boolean incluirChequesNoRotativo, int tipoDocumento) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Cliente...");
        List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteSysPdv(idLojaCliente, idLojaCliente, incluirChequesNoRotativo);
        new ReceberCreditoRotativoDAO().salvar(vReceberCliente, idLojaVR);
    }

    @Override
    public List<MercadologicoMapaVO> obterListagem() throws Exception {
        List<MercadologicoMapaVO> result = new ArrayList<>();
        /*try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select m1.seccod, m1.secdes, m2.grpcod, m2.grpdes, m3.sgrcod, m3.sgrdes " +
                    "from secao as m1 " +
                    "left join grupo as m2 on m2.seccod = m1.seccod " +
                    "left join subgrupo as m3 on m3.seccod = m1.seccod and m3.grpcod = m2.grpcod " + 
                    "order by m1.seccod, m2.grpcod, m3.sgrcod "
            )) {
                while (rst.next()) {
                    result.add(new MercadologicoMapaVO(
                            "SysPDV",
                            "1",
                            rst.getString("seccod") + "-" + rst.getString("grpcod") + "-" + rst.getString("sgrcod"),
                            rst.getd
                    ));
                }
            }
        }*/
        return result;
    }

    public void importarOfertaSemData(int idLojaVR, int idLojaCliente) throws Exception{
        ProgressBar.setStatus("Carregando dados das ofertas sem data");
        List<OfertaVO> ofertas = carregarOfertaSemData(idLojaVR, idLojaCliente);
        
        new OfertaDAO().salvar(ofertas, idLojaVR);
    }
}