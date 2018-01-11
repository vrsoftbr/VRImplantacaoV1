package vrimplantacao.dao.interfaces;

import java.sql.Date;
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
import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrframework.remote.ItemComboVO;
import vrimplantacao.classe.ConexaoFirebird;
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
import vrimplantacao.classe.Global;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeHistoricoVO;
import vrimplantacao.vo.vrimplantacao.ReceberChequeItemVO;

public class ShiDAO {
    
        
    public List<ItemComboVO> carregarLojasCliente() throws Exception {        
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    codigo,\n" +
                    "    codigo || ' - ' || nomexx descricao\n" +
                    "from filial\n" +
                    "order by codigo"
            )) {
                while (rst.next()) {
                    result.add(new ItemComboVO(rst.getInt("codigo"), rst.getString("descricao")));
                }
            }
        }
        
        return result;
    }
    
    //CARREGAMENTOS
    public List<ClientePreferencialVO> carregarClienteShi(int idLoja, int idLojaCliente) throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

            String nome, endereco , bairro, telefone1, telefone2,celular,inscricaoestadual, email, enderecoEmpresa, nomeConjuge,  
                   dataResidencia,  dataCadastro, numero; 
            int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao=0, id, agente, id_situacaocadastro = 0, Linha=0;
            Long cnpj, cep;
            double limite;
            boolean bloqueado = false;

            try {
                stm = ConexaoFirebird.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("SELECT CODIGO ID, NOMEXX NOME, SITUAC ID_SITUACAOCADASTRO, ENDERE_RES ENDERECO,           ");
                sql.append("       BAIRRO_RES BAIRRO,ESTADO_RES ID_ESTADO, CIDADE_RES ID_MUNICIPIO, CEPEND_RES CEP,   ");
                sql.append("       TELDDD_RES||' '||TELFON_RES TELEFONE, TELFON_CEL CELULAR, TELFON_REC RECADO,  EMAILX EMAIL,                   ");
                sql.append("       RGNUME_FIS INSCRICAOESTADUAL, '' ORGAOEMISSOR, ciccgc CNPJ,                        ");
                sql.append("       estciv_fis ID_TIPOESTADOCIVIL, NASCIM_FIS DATANASCIMENTO, DATCAD DATACADASTRO,     ");
                sql.append("       SEXOXX_FIS SEXO, NULL OBSERVACAO, NOMEXX_COM EMPRESA, ENDERE_COM ENDERECOEMPRESA,  ");
                sql.append("       BAIRRO_COM BAIRROEMPRESA,CEPXXX_COM CEPEMPRESA, TELFON_COM TELEFONEEMPRESA,        ");
                sql.append("       '01'||TEMPOX_COM DATAADMISSAO, DEPART_COM CARGO,NOMCON_FIS NOMECONJUGE,            ");
                sql.append("       NASCON_FIS DATANASCIMENTOCONJUGE, EMPCON_FIS EMPRESACONJUGE, NOMMAE_FIS NOMEMAE,   ");
                sql.append("       NOMPAI_FIS NOMEPAI, NUMERO_RES AS NUMERO,                                                                ");
                sql.append("       COALESCE(                                                                         "); 
                sql.append("       COALESCE((SELECT VALORX FROM LIMITE WHERE CODIGO = C.limche),0) +                 ");
                sql.append("       COALESCE((SELECT VALORX FROM LIMITE WHERE CODIGO = C.limcre),0) +                 ");
                sql.append("       COALESCE((SELECT VALORX FROM LIMITE WHERE CODIGO = C.limcon),0),0)  AS LIMITE,       ");
                sql.append("       COALESCE(C.DIAVEN,30) DIAVEN ");
                sql.append("FROM CLIENTES C ");                                      

                rst = stm.executeQuery(sql.toString());
                Linha=1;
                try{
                    while (rst.next()) {                    
                        ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();

                        // gerar codigo novo cliente
                        id = new CodigoInternoDAO().get("clientepreferencial");                       

                        if ((rst.getString("CNPJ")!="0") && 
                                    (rst.getString("CNPJ")!=null)){
                           if (util.formataNumero(rst.getString("CNPJ").trim()).length() <= 11){
                               id_tipoinscricao = 1; // PESSOA FISICA
                           }else if (rst.getString("CNPJ").length() == 14){  
                               id_tipoinscricao = 0; // PESSOA JURIDICA                          
                           }
                        } 
                        if ((rst.getString("NOME")!=  null) &&
                                (!rst.getString("NOME").isEmpty())) {
                            byte[] bytes = rst.getBytes("NOME");
                            String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                            nome = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nome = "SEM NOME VR "+id;
                        }
                        endereco            = util.acertarTexto(rst.getString("ENDERECO").replace("'", "").trim());
                        if (endereco.length()>50){
                            endereco = endereco.substring(0,50);
                        }
                        bairro              = util.acertarTexto(rst.getString("BAIRRO").replace("'", "").trim());
                        if ((rst.getString("ID_MUNICIPIO")!=null) && (rst.getString("ID_ESTADO")!=null)){
                           id_estado           = util.retornarEstadoDescricao(rst.getString("ID_ESTADO"));     
                           if (id_estado==0){
                               id_estado=35; // ESTADO ESTADO DO CLIENTE
                           }
                           id_municipio        = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("ID_MUNICIPIO").toString()),rst.getString("ID_ESTADO").toString());
                           if(id_municipio==0){
                               id_municipio=3525508;// CIDADE DO CLIENTE;
                           }
                        } else{
                            id_estado    = 35; // ESTADO ESTADO DO CLIENTE
                            id_municipio = 3525508; // CIDADE DO CLIENTE;                   
                        }
                        if (rst.getString("CEP")!=null){
                            cep                 = Long.parseLong(util.formataNumero(util.acertarTexto(rst.getString("CEP").replace("'", ""))));
                        }else
                            cep = Long.parseLong("0");
                        if (rst.getString("TELEFONE")!=null){
                            telefone1           = util.formataNumero(rst.getString("TELEFONE"));  
                        }else{
                            telefone1           = "";
                        }
                        if (rst.getString("RECADO")!=null){
                            telefone2           = util.formataNumero(rst.getString("RECADO"));  
                        }else{
                            telefone2           = "";
                        }
                        if (rst.getString("CELULAR")!=null){
                            celular           = util.formataNumero(rst.getString("CELULAR"));  
                        }else{
                            celular           = "";
                        }                        
                        if (rst.getString("EMAIL")!=null){
                            email               = util.acertarTexto(rst.getString("EMAIL"));  
                            if(email.length()>50){
                                email = email.substring(0,50);
                            }
                        }else{
                            email               = "";
                        }
                        if (rst.getString("INSCRICAOESTADUAL")!=null){
                            inscricaoestadual   = util.acertarTexto(rst.getString("INSCRICAOESTADUAL"));    
                            inscricaoestadual   = inscricaoestadual.replace(".","").replace("/", "").replace(",", "");
                            if (inscricaoestadual.length()>18){
                                inscricaoestadual = inscricaoestadual.substring(0,18);
                            }
                        }else{
                            inscricaoestadual   = "ISENTO";
                        }
                        
                        cnpj = Utils.stringToLong(rst.getString("CNPJ"), -1);
                                                 
                        if (rst.getString("SEXO")!=null){
                            if ((rst.getInt("SEXO")==0) || (rst.getInt("SEXO")==1)){
                                id_sexo             = rst.getInt("SEXO");     
                            }else{
                                id_sexo                = 0;
                            }
                        }else{
                            id_sexo                = 0;
                        }
                        if (rst.getString("DATACADASTRO")!=null){
                            dataCadastro                = rst.getString("DATACADASTRO");
                        }else{
                            dataCadastro                = "";
                        }
                        dataResidencia   = "1990/01/01";
                        if (id==1190){
                            Linha++; 
                        }else{
                            Linha++;
                        }
                        if (rst.getString("LIMITE")!=null){
                            limite           = rst.getDouble("LIMITE");  
                        }else{
                            limite           = 0;
                        }
                        if (rst.getString("NUMERO")!=null){
                            numero   = rst.getString("NUMERO");
                            if (numero.length()>6){
                                numero = numero.substring(1, 6);
                            }
                        }else{
                            numero = "";                        
                        }
                        oClientePreferencial.id = id;
                        oClientePreferencial.codigoanterior = rst.getInt("ID");                        
                        oClientePreferencial.nome = nome;
                        oClientePreferencial.id_situacaocadastro = id_situacaocadastro;
                        oClientePreferencial.setBloqueado(bloqueado);
                        oClientePreferencial.id_tipoinscricao = id_tipoinscricao; 
                        oClientePreferencial.endereco = endereco;
                        oClientePreferencial.bairro = bairro;
                        oClientePreferencial.id_estado = id_estado;
                        oClientePreferencial.numero = numero;                        
                        oClientePreferencial.id_municipio = id_municipio;
                        oClientePreferencial.cep = cep;
                        oClientePreferencial.setTelefone(telefone1);
                        oClientePreferencial.setTelefone2(telefone2);                        
                        oClientePreferencial.setCelular(celular);
                        oClientePreferencial.inscricaoestadual = inscricaoestadual;
                        oClientePreferencial.setCnpj(cnpj);
                        oClientePreferencial.sexo = id_sexo;
                        oClientePreferencial.dataresidencia = dataResidencia;
                        oClientePreferencial.datacadastro = dataCadastro;
                        oClientePreferencial.email = email;
                        oClientePreferencial.valorlimite = limite;
                        oClientePreferencial.setVencimentocreditorotativo(rst.getInt("DIAVEN"));
                        vClientePreferencial.add(oClientePreferencial);
                    }
                stm.close();
                } catch (Exception ex) {
                    throw ex;
                }
                return vClientePreferencial;
            } catch(SQLException | NumberFormatException ex) {

                throw ex;
            }
        }    

    public List<FornecedorVO> carregarFornecedorShi() throws Exception {
            StringBuilder sql = null;
            Statement stm = null;
            ResultSet rst = null;
            Utils util = new Utils();
            List<FornecedorVO> vFornecedor = new ArrayList<>();

            String razaosocial, nomefantasia, obs, inscricaoestadual, endereco, bairro, datacadastro, telefone, celular ;
            int id, id_municipio = 0, id_estado, id_tipoinscricao, Linha=0;
            Long cnpj, cep;
            double pedidoMin;
            boolean ativo=true;

            try {
                stm = ConexaoFirebird.getConexao().createStatement();

                sql = new StringBuilder();
                sql.append("SELECT f.codigo, f.ciccgc, f.fantas, f.nomexx, f.endere, f.bairro, f.cidade, f.estado, f.cepxxx, ");
                sql.append("f.inscrg, f.dtcada, f.observ, f.altera, f.comprasusp, f.motsusp, f.entreg, f.pedmin, f.frecom,   ");
                sql.append("f.homepage, f.atuali, f.cotaca, f.usuari, f.senha, f.inativ, f.usualt, f.acordo, f.pedobrig,     ");
                sql.append("f.pagame, f.datest, f.setor, f.concorrente, f.atacado, f.codmun, f.cnae, f.plconta, f.cfop,      ");
                sql.append("f.verba, f.inclusao, f.soxml, f.prodrural, f.simplesnac, f.alerta, f.criticaxml, f.verbasemst,   ");
                sql.append("(select first 1 c.telefone from contato c where c.telefone <> '' and c.fornecedor = f.codigo) as telefone, ");
                sql.append("(select first 1 c.celular from contato c where c.celular <> '' and c.fornecedor = f.codigo) as celular ");
                sql.append("FROM FORNECEDOR f                                                                                ");            

                rst = stm.executeQuery(sql.toString());
                Linha=0;
                try{
                    while (rst.next()) {                    
                        FornecedorVO oFornecedor = new FornecedorVO();

                        id = rst.getInt("codigo");

                        Linha++; 
                        if (Linha==3){
                            Linha--;
                            Linha++;                        
                        }                    
                        if ((rst.getString("nomexx") != null)
                                && (!rst.getString("nomexx").isEmpty())) {
                           byte[] bytes = rst.getBytes("nomexx");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           razaosocial = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            razaosocial = "";
                        }

                        if ((rst.getString("fantas") != null)
                                && (!rst.getString("fantas").isEmpty())) {
                           byte[] bytes = rst.getBytes("fantas");
                           String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                           nomefantasia = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                        } else {
                            nomefantasia = "";
                        }

                        if ((rst.getString("ciccgc") != null)
                                && (!rst.getString("ciccgc").isEmpty())) {
                            cnpj = Long.parseLong(util.formataNumero(rst.getString("ciccgc").trim()));
                        } else {
                            cnpj = Long.parseLong(rst.getString("codigo"));
                        }

                        if ((rst.getString("inscrg") != null)
                                && (!rst.getString("inscrg").isEmpty())) {
                            inscricaoestadual = util.acertarTexto(rst.getString("inscrg").replace("'", "").trim());
                        } else {
                            inscricaoestadual = "ISENTO";
                        }

                        id_tipoinscricao = 0;

                        if ((rst.getString("endere") != null)
                                && (!rst.getString("endere").isEmpty())) {
                            endereco = util.acertarTexto(rst.getString("endere").replace("'", "").trim());
                        } else {
                            endereco = "";
                        }

                        if ((rst.getString("bairro") != null)
                                && (!rst.getString("bairro").isEmpty())) {
                            bairro = util.acertarTexto(rst.getString("bairro").replace("'", "").trim());
                        } else {
                            bairro = "";
                        }

                        if ((rst.getString("cepxxx") != null)
                                && (!rst.getString("cepxxx").isEmpty())) {
                            cep = Long.parseLong(util.formataNumero(rst.getString("cepxxx").trim()));
                        } else {
                            cep = Long.parseLong("0");
                        }

                        if ((rst.getString("cidade") != null)
                                && (!rst.getString("cidade").isEmpty())) {

                            if ((rst.getString("estado") != null)
                                    && (!rst.getString("estado").isEmpty())) {

                                id_municipio = util.retornarMunicipioIBGEDescricao(util.acertarTexto(rst.getString("cidade").replace("'", "").trim()),
                                        util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                                if (id_municipio == 0) {
                                    id_municipio = 3525508;
                                }
                            }
                        } else {
                            id_municipio = 3525508;
                        }

                        if ((rst.getString("estado") != null)
                                && (!rst.getString("estado").isEmpty())) {
                            id_estado = util.retornarEstadoDescricao(util.acertarTexto(rst.getString("estado").replace("'", "").trim()));

                            if (id_estado == 0) {
                                id_estado = 35;
                            }
                        } else {
                            id_estado = 35;
                        }

                        if (rst.getString("OBSERV") != null) {
                            obs = rst.getString("OBSERV").trim();
                        } else {
                            obs = "";
                        }

                        if (rst.getString("DTCADA") != null) {
                            datacadastro = rst.getString("DTCADA");
                        } else {
                            datacadastro = "";
                        }

                        if (rst.getString("PEDMIN") != null) {
                            pedidoMin = rst.getDouble("PEDMIN");
                        } else {
                            pedidoMin = 0;
                        }

                        if (rst.getString("INATIV") != null) {
                            if (!"S".equals(rst.getString("INATIV").trim())){
                                ativo = true;
                            }else{
                                ativo = false;    
                            }
                        } else {
                            ativo = true;
                        }
                        if (rst.getString("TELEFONE") != null) {
                            telefone = util.formataNumero(rst.getString("TELEFONE"));
                        } else {
                            telefone = "";
                        }
                        if (rst.getString("CELULAR") != null) {
                            celular = util.formataNumero(rst.getString("CELULAR"));
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

                        oFornecedor.codigoanterior = rst.getInt("codigo");
                        oFornecedor.razaosocial = razaosocial;
                        oFornecedor.nomefantasia = nomefantasia;
                        oFornecedor.endereco = endereco;
                        oFornecedor.setTelefone(telefone);
                        oFornecedor.setCelular(celular);
                        oFornecedor.bairro = bairro;
                        oFornecedor.id_municipio = id_municipio;
                        oFornecedor.cep = cep;
                        oFornecedor.id_estado = id_estado;
                        oFornecedor.id_tipoinscricao = id_tipoinscricao;
                        oFornecedor.inscricaoestadual = inscricaoestadual;
                        oFornecedor.setCnpj(cnpj);
                        oFornecedor.id_situacaocadastro = (ativo == true ?  1 : 0);                    
                        oFornecedor.observacao = obs;

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
    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto;
        String codigoExterno;
        java.sql.Date dataAlteracao = new Date(new java.util.Date().getTime());

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("select fornec, codpro, codfor ");
            sql.append("from CODFORNEC ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                idFornecedor = rst.getInt("fornec");
                idProduto = rst.getInt("codpro");

                if ((rst.getString("codfor") != null)
                        && (!rst.getString("codfor").isEmpty())) {
                    codigoExterno = util.acertarTexto(rst.getString("codfor").replace("'", "").trim());
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;               
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public List<MercadologicoVO> carregarMercadologicoShi(int nivel) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao = "";
        int mercadologico1, mercadologico2, mercadologico3;

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            //FIXME Incluir uma tratativa para corrigir os ids do mercadologico
            sql = new StringBuilder();
            sql.append(" SELECT CODIGO, DESCRI ");
            sql.append(" FROM GRUPO            ");
            sql.append(" ORDER BY CODIGO       ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                mercadologico1 = 0;
                mercadologico2 = 0;
                mercadologico3 = 0;
                
                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    mercadologico1 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));
                    descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());

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

                } else if ((nivel == 2) && (rst.getString("CODIGO").length() == 6)) {
                    
                    mercadologico1 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));
                    mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(3, 6));
                    descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());

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
                } else if ((nivel == 3) && (rst.getString("CODIGO").length() == 9)) {
                    mercadologico1 = Integer.parseInt(rst.getString("CODIGO").substring(0, 2));
                    mercadologico2 = Integer.parseInt(rst.getString("CODIGO").substring(3, 6));
                    mercadologico3 = Integer.parseInt(rst.getString("CODIGO").substring(7, 9));
                    descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());

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

            return vMercadologico;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<FamiliaProdutoVO> carregarFamiliaProdutoShi() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<FamiliaProdutoVO> vFamiliaProduto = new ArrayList<>();

        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT ID, DESCRI ");
            sql.append("FROM ALTERN ");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FamiliaProdutoVO oFamiliaProduto = new FamiliaProdutoVO();

                oFamiliaProduto.id = Integer.parseInt(rst.getString("ID"));
                oFamiliaProduto.descricao = util.acertarTexto(rst.getString("DESCRI").replace("'", "").trim());
                oFamiliaProduto.id_situacaocadastro = 1;
                oFamiliaProduto.codigoant = 0;

                vFamiliaProduto.add(oFamiliaProduto);
            }

            return vFamiliaProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
   
    public Map<Integer, ProdutoVO> carregarProdutoShi() throws Exception {
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        StringBuilder sql = null;
        Statement stm = null, stmPostgres = null;
        ResultSet rst = null, rstPostgres = null;
        Utils util = new Utils();
        int idProduto, idTipoEmbalagem = 0, qtdEmbalagem, idTipoPisCofins, idTipoPisCofinsCredito, tipoNaturezaReceita,
               idAliquota, idFamilia, mercadologico1, mercadologico2, mercadologico3, idSituacaoCadastro, 
               ncm1, ncm2, ncm3, codigoBalanca, referencia = -1, validade;
        String descriaoCompleta, descricaoReduzida, descricaoGondola, ncmAtual, strCodigoBarras;
        boolean eBalanca, pesavel;
        long codigoBarras = 0;
        
        try {
            
            Conexao.begin();
            
            stmPostgres = Conexao.createStatement();
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();
            sql.append("select P.CODIGO, coalesce(b.barras,P.CODIGO) barras ,P.BALANC,P.FANTAS , P.DESCRI, P.UNIEMB, P.UNIPRO, P.REFFAB, ");
            sql.append("       P.FORNEC, P.EMBALA,  P.ALTERN, P.VALPRE, P.COMPRA,P.ETQGON,P.GRUPOX,P.PROVEN , ");
            sql.append("       P.INATIV,P.QTDDEC,P.CATEGO,P.ALTERA,P.USUALT,P.PESO , p.SAZONAL , p.SECAO, ");
            sql.append("       p.COFINS, p.ESTOQUE, p.RECEITA, p.CODASSOC, p.QTDASSOC, p.PREASSOC, p.INCLUSAO, ");
            sql.append("       p.CUBAGEM, p.IDCLAFIS, coalesce(claf.clafis,'') as clafis, P.IDPISCOFINS, ICMPROD.icms , ");
            sql.append("       ICMPROD.ICMBAS, ICMPROD.ICMDEB, ICMPROD.ICMCRE, ICMPROD.reduzi, ICMPROD.IVAST, ");
            sql.append("       ICMPROD.CST, ICMPROD.ICMBAS_E, ICMPROD.reduzi_E ,ICMS2.subtri ,ICMS2.tripdv, ");
            sql.append("       ICMS2.tripdv2 ,ICMS2.descri as descri1 ,ICMS2.reduzi ,ICMS2.icmdeb,p.cstpis,p.cstcofins, ");
            sql.append("       p.cstpiscr,p.cstcofinscr,P.NATREC ");
            sql.append("from produtos AS P ");
            sql.append("LEFT JOIN barras as b on b.codpro = p.codigo ");
            sql.append("LEFT JOIN clafis as claf on claf.id = p.idclafis ");
            sql.append("LEFT JOIN ICMSPROD AS ICMPROD ON ICMPROD.CODPRO = P.CODIGO ");
            sql.append("LEFT join icms as icms2 on icms2.codigo =  ICMPROD.icms ");
            sql.append("where p.inativ = 'N' order by P.BALANC desc ,P.CODIGO,b.barras ");
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ProdutoVO oProduto = new ProdutoVO();
                
                if ("N".equals(rst.getString("inativ").trim())) {
                    idSituacaoCadastro = 1;
                } else {
                    idSituacaoCadastro = 0;
                }
                
                eBalanca = false;
                codigoBalanca = -1;
                pesavel = false;
                idTipoEmbalagem = 0;
                
                if ("S".equals(rst.getString("BALANC").trim())) {
                
                    eBalanca = true; 
                    sql = new StringBuilder();
                    sql.append("select codigo, descricao, pesavel, validade ");
                    sql.append("from implantacao.produtobalanca ");
                    sql.append("where codigo = " + rst.getString("BARRAS").replace(".", ""));
                    
                    rstPostgres = stmPostgres.executeQuery(sql.toString());
                    
                    if (rstPostgres.next()) {
                        
                        idProduto = Integer.parseInt(rst.getString("CODIGO").trim().replace(".", ""));               
                        codigoBalanca = rstPostgres.getInt("codigo");
                        validade =  rstPostgres.getInt("validade");
                        
                        if ("P".equals(rstPostgres.getString("PESAVEL").trim())){
                            idTipoEmbalagem = 4;
                            pesavel = false;
                        } else {
                            idTipoEmbalagem = 0;
                            pesavel = true;
                        }
                            
                    } else {
                        idProduto = Integer.parseInt(rst.getString("CODIGO").trim().replace(".", ""));
                        if ("CX".equals(rst.getString("UNIPRO").trim())) {
                            idTipoEmbalagem = 1;
                        } else if ("KG".equals(rst.getString("UNIPRO").trim())) {
                            idTipoEmbalagem = 4;
                        } else if ("UN".equals(rst.getString("UNIPRO").trim())) {
                            idTipoEmbalagem = 0;
                        } else {
                            idTipoEmbalagem = 0;
                        }                        
                    }
                } else {
                    eBalanca = false;
                    pesavel = false;
                    idProduto = Integer.parseInt(rst.getString("CODIGO").trim().replace(".", ""));
                    validade =  0;                    
                    
                    if ("CX".equals(rst.getString("UNIPRO").trim())) {
                        idTipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("UNIPRO").trim())) {
                        idTipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("UNIPRO").trim())) {
                        idTipoEmbalagem = 0;
                    } else {
                        idTipoEmbalagem = 0;
                    }
                }
                
                if ((rst.getString("DESCRI") != null) &&
                        (!rst.getString("DESCRI").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("DESCRI");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descriaoCompleta = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descriaoCompleta = "";
                }
                
                if ((rst.getString("FANTAS") != null) &&
                        (!rst.getString("FANTAS").trim().isEmpty())) {
                    byte[] bytes = rst.getBytes("FANTAS");
                    String textoAcertado = new String(bytes, "ISO-8859-1");                                     
                    descricaoReduzida = util.acertarTexto(textoAcertado.replace("'", "").trim());                     
                } else {
                    descricaoReduzida = "";
                }
                
                descricaoGondola = descricaoReduzida;

                qtdEmbalagem = 1;
                
                if ((rst.getString("ALTERN") != null) &&
                        (!rst.getString("ALTERN").trim().isEmpty()) &&
                        (!"0".equals(rst.getString("ALTERN").trim()))) {
                    idFamilia = Integer.parseInt(rst.getString("ALTERN").trim().replace(".", ""));
                } else {
                    idFamilia = -1;
                }
                
                if (rst.getString("GRUPOX").length() == 9) {
                    mercadologico1 = Integer.parseInt(rst.getString("GRUPOX").substring(0, 2));
                    mercadologico2 = Integer.parseInt(rst.getString("GRUPOX").substring(3, 6));
                    mercadologico3 = Integer.parseInt(rst.getString("GRUPOX").substring(7, 9));
                } else {
                    mercadologico1 = 50;
                    mercadologico2 = 1;
                    mercadologico3 = 1;
                }
                
                if ((rst.getString("clafis") != null) &&
                        (!rst.getString("clafis").isEmpty()) &&
                        (rst.getString("clafis").trim().length() > 5)) {
                    
                    ncmAtual = util.formataNumero(rst.getString("clafis").trim());
                    
                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);
                    
                    ncm1 = oNcm.ncm1;    
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;
                    
                } else {
                    ncm1 = 402;
                    ncm2 = 99;
                    ncm3 = 0;
                }
                                
                if (eBalanca == true) {
                    codigoBarras = Long.parseLong(String.valueOf(idProduto));
                } else {
                    if ((rst.getString("barras") != null) &&
                            (!rst.getString("barras").trim().isEmpty())) {
                        
                        strCodigoBarras = rst.getString("barras").replace(".", "").trim();
                        
                        if (String.valueOf(Long.parseLong(strCodigoBarras)).length() < 7) {                            
                            if ((idProduto >= 10000) && (!"KG".equals(rst.getString("UNIPRO").trim()))) {
                                codigoBarras = util.gerarEan13(idProduto, true);
                            } else {
                                codigoBarras = util.gerarEan13(idProduto, false);
                            }
                        } else {
                            codigoBarras = Long.parseLong(rst.getString("barras").trim());
                        }
                    }
                }
                
                if ((rst.getString("CSTPIS") != null) &&
                        (!rst.getString("CSTPIS").trim().isEmpty())) {
                    idTipoPisCofins = util.retornarPisCofinsDebito(Integer.parseInt(rst.getString("CSTPIS").trim()));
                } else {
                    idTipoPisCofins = Global.pisCofinsDebito;
                }
                
                if ((rst.getString("CSTPISCR") != null) &&
                        (!rst.getString("CSTPISCR").trim().isEmpty())) {
                    idTipoPisCofinsCredito = util.retornarPisCofinsCredito(Integer.parseInt(rst.getString("CSTPISCR").trim()));
                } else {
                    idTipoPisCofinsCredito = Global.pisCofinsCredito;
                }
                
                if ((rst.getString("NATREC") != null) &&
                        (!rst.getString("NATREC").trim().isEmpty())) {
                    tipoNaturezaReceita = util.retornarTipoNaturezaReceita(idTipoPisCofins, 
                            rst.getString("NATREC").trim());
                } else {
                    tipoNaturezaReceita = Global.tipoNaturezaReceita;
                }
                
                if ((rst.getString("descri1") != null) &&
                        (!rst.getString("descri1").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSShi(rst.getString("descri1").trim().toUpperCase());
                } else {
                    idAliquota = 8;
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
                
                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idSituacaoCadastro = idSituacaoCadastro;
                
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
                
                oCodigoAnterior.codigoanterior = Integer.parseInt(rst.getString("CODIGO").replace(".", ""));
                oCodigoAnterior.codigoatual = idProduto;
                if((rst.getString("barras")!=null) && (!rst.getString("barras").trim().isEmpty())){
                   oCodigoAnterior.barras = Long.parseLong(rst.getString("barras").replace(".", ""));
                } else {
                   oCodigoAnterior.barras = 0; 
                }
                
                oCodigoAnterior.naturezareceita = tipoNaturezaReceita;
                
                if ((rst.getString("CSTPIS") != null) && (!rst.getString("CSTPIS").trim().isEmpty())) {
                    oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("CSTPIS").replace(".", ""));
                } else {
                    oCodigoAnterior.piscofinsdebito = -1;
                }
                
                if ((rst.getString("CSTPISCR") != null) && (!rst.getString("CSTPISCR").trim().isEmpty())) {
                    oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("CSTPISCR").replace(".", ""));
                } else {
                    oCodigoAnterior.piscofinscredito = -1;
                }
                
                if ((rst.getString("tripdv") != null) && (!rst.getString("tripdv").trim().isEmpty())) {
                    oCodigoAnterior.ref_icmsdebito = rst.getString("tripdv").trim().replace(".", "");
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
                
                if ((rst.getString("clafis") != null) && (!rst.getString("clafis").trim().isEmpty())) {
                    oCodigoAnterior.ncm = rst.getString("clafis").trim().replace(".", "");
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
    
    public Map<Integer, ProdutoVO> carregarCustoProduto(int idLoja, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custo = 0;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("WITH ");
            sql.append("alteracao AS( ");
            
            sql.append("select pc.codpro, MAX(alteracao) as alteracao FROM precocusto as pc where  pc.filial in("+String.valueOf(idLojaCliente)+") group by pc.codpro ");
            
            sql.append(") ");
            
            sql.append("select  pc.codpro,  pc.custo,  pc.alteracao ");
            sql.append("from precocusto as pc ");
            sql.append("inner join alteracao as alt on alt.codpro=pc.codpro AND alt.alteracao=pc.alteracao ");
            sql.append("where  pc.filial in ("+String.valueOf(idLojaCliente)+") ");
           
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro").replace(".", ""));
                custo = Double.parseDouble(rst.getString("custo").replace(",", "."));
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
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
       
        public List<ReceberChequeVO> carregarReceberCheque(int idLojaVR, int idLojaCliente) throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();
        
        try {

            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT c.cheque, c.ciccgc, c.client, c.bancox, c.agenci, c.contax, ");
            sql.append("c.valorx, c.dataxx, c.vencim, c.status, c.devol1, c.motdv1, c.devol2, c.motdv2, coalesce(c.devol1, c.devol2) devolvido, ");
            sql.append("c.reapre, c.quitad, c.codfor, c.nomfor, c.datfor, c.caixax, c.observ, c.seqdev, ");
            sql.append("c.datcad, c.usucad, c.datalt, c.usualt, c.cobran, c.datcob, c.entrad ");
            sql.append("FROM CHEQUES c   ");             
            sql.append("WHERE not c.quitad is null and c.FILIAL = "+String.valueOf(idLojaCliente));                         

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                
                ReceberChequeVO oReceberCheque = new ReceberChequeVO();
                    
                int cheque = Integer.parseInt(Utils.formataNumero(rst.getString("cheque"), 10, "0"));
                int idBanco = Utils.retornarBanco((int) Utils.stringToLong(rst.getString("bancox"), 804l));
                String dataemissao, datavencimento;

                if ((rst.getString("dataxx") != null) &&
                        (!rst.getString("dataxx").trim().isEmpty())) {
                    dataemissao = Util.formatDataGUI(rst.getDate("dataxx"));
                } else {
                    dataemissao = Util.formatDataGUI(new java.util.Date());
                }

                if ((rst.getString("vencim") != null) &&
                        (!rst.getString("vencim").trim().isEmpty())) {

                    datavencimento = Util.formatDataGUI(rst.getDate("vencim"));
                } else {
                    datavencimento = Util.formatDataGUI(new java.util.Date());
                }

                oReceberCheque.setId_loja(idLojaVR);
                oReceberCheque.setId_tipoalinea(rst.getString("devolvido") == null ? 0 : 12);
                oReceberCheque.setData(dataemissao);
                oReceberCheque.setDatadeposito(datavencimento);
                oReceberCheque.setCpf(Long.parseLong(Utils.formataNumero(rst.getString("ciccgc"))));
                oReceberCheque.setNumerocheque(cheque);
                oReceberCheque.setId_banco(idBanco);
                oReceberCheque.setAgencia(rst.getString("agenci"));
                oReceberCheque.setConta(rst.getString("contax"));
                oReceberCheque.setNumerocupom(0);
                oReceberCheque.setValor(rst.getDouble("valorx"));
                oReceberCheque.setObservacao("IMPORTADO VR");
                oReceberCheque.setRg("");
                oReceberCheque.setTelefone("");
                oReceberCheque.setNome(rst.getString("observ"));
                oReceberCheque.setId_tipoinscricao(String.valueOf(oReceberCheque.getCpf()).length() > 11 ? 0 : 1);
                oReceberCheque.setDatadeposito(datavencimento);
                oReceberCheque.setValorjuros(0);
                oReceberCheque.setValorinicial(oReceberCheque.getValor());
                oReceberCheque.setId_tipolocalcobranca(rst.getInt("cobran"));

                vReceberCheque.add(oReceberCheque); 

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }    
    
    public List<ReceberCreditoRotativoVO> carregarReceberClienteShi(int id_loja, int id_lojaCliente) throws Exception {
        
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();
        
        int id_cliente, numerocupom;
        double valor, juros;
        String observacao, dataemissao, datavencimento;
        long cnpj;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append("SELECT D.SEQUEN, D.CLIENT, D.DATAXX, D.VENCIM, D.BAIXAX, D.VALORX,");
            sql.append("       D.VALPAG, STATUS, C.ciccgc AS CNPJ, D.DATCAD, D.OBSERV ");
            sql.append("  FROM DOCUMENTOS D ");
            sql.append("INNER JOIN CLIENTES C ON                                ");
            sql.append("    C.CODIGO = D.CLIENT ");               
            sql.append("WHERE NOT EXISTS(SELECT B.FILIAL ");
            sql.append("                   FROM BOLETOSDOC B ");
            sql.append("                  WHERE B.filial_doc = D.filial AND ");
            sql.append("                        B.tipdoc_doc = D.tipdoc AND ");
            sql.append("                        B.sequen_doc = D.sequen AND ");
            sql.append("                        B.desdob_doc = D.desdob)    ");
            sql.append("  AND D.STATUS = 1 ");

            // só ignora o numero do cadastro de BOLETOS - feito para o cliente GRILO!
            sql.append("  AND D.TIPDOC IN (0,1,5,6) ");           
            //
            
            sql.append("  AND D.FILIAL = "+String.valueOf(id_lojaCliente)); 
            sql.append("ORDER BY D.CLIENT, D.DATAXX, D.VENCIM ");            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                ReceberCreditoRotativoVO oReceberCreditoRotativo = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("CLIENT");                
                dataemissao = rst.getString("DATCAD");
                datavencimento = rst.getString("VENCIM");
                numerocupom = Integer.parseInt(util.formataNumero(rst.getString("SEQUEN")));
                valor = Double.parseDouble(rst.getString("VALORX"));
                juros = 0;
                
                if ((rst.getString("OBSERV") != null) &&
                        (!rst.getString("OBSERV").isEmpty())) {
                    observacao = util.acertarTexto(rst.getString("OBSERV").replace("'", ""));
                } else { 
                    observacao = "IMPORTADO VR";
                }
                
                if ((rst.getString("CNPJ")!=  null) &&
                            (!rst.getString("CNPJ").isEmpty())) {
                    cnpj = Long.parseLong(util.formataNumero(rst.getString("CNPJ").trim()));
                }else{
                    cnpj = Long.parseLong("0");
                }
                
                oReceberCreditoRotativo.cnpjCliente = cnpj;
                oReceberCreditoRotativo.id_loja = id_loja;
                oReceberCreditoRotativo.dataemissao = dataemissao;
                oReceberCreditoRotativo.numerocupom = numerocupom;
                oReceberCreditoRotativo.valor = valor;
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
        int idProduto;
        double preco = 0, margem = 0;
        
        try {
            
            stm = ConexaoFirebird.getConexao().createStatement();
            
            sql = new StringBuilder();            
            sql.append("WITH ");
            sql.append("altpreco AS( ");
            sql.append("select pv.codpro, MAX(altpreco) as altpreco FROM precovenda as pv where  pv.filial in ("+String.valueOf(id_lojaCliente)+") group by pv.codpro ");
            sql.append(") ");
            sql.append("select  pv.codpro,  pv.preco,  pv.altpreco, pv.lucro ");
            sql.append("from precovenda as pv ");
            sql.append("inner join altpreco as alt on alt.codpro=pv.codpro AND alt.altpreco=pv.altpreco ");
            sql.append("where  pv.filial in ("+String.valueOf(id_lojaCliente)+")");


            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro"));
                preco = rst.getDouble("preco");
                
                if ((rst.getString("lucro") != null) &&
                        !rst.getString("lucro").trim().isEmpty()) {
                    margem = Double.parseDouble(rst.getString("lucro").replace(",", "."));
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
    
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLoja, int id_lojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double saldo = 0;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();            
            sql.append(" WITH data AS(select e.codpro, MAX(data) as data FROM estoque as e                         "); 
            sql.append("              where  e.filial in ("+String.valueOf(id_lojaCliente)+") group by e.codpro )  ");
            sql.append(" select  e.codpro,  e.saldoatu,  e.data                                                    ");  
            sql.append(" from estoque as e                                                                         ");  
            sql.append(" inner join data as alt on alt.codpro = e.codpro AND alt.data = e.data                     ");
            sql.append(" where e.filial in ("+String.valueOf(id_lojaCliente)+")                                    ");           

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro"));
                saldo = rst.getDouble("saldoatu");
                
                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;
                
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
        int idProduto;
        long codigobarras;
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();            
            sql.append("select codpro, barras from barras"); 

            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("codpro"));

                if ((rst.getString("barras") != null) &&
                        (!rst.getString("barras").trim().isEmpty())) {
                    codigobarras = Long.parseLong(rst.getString("barras").replace(".", ""));
                } else {
                    codigobarras = 0;
                }
                
                if (String.valueOf(codigobarras).length() >= 7) {
                
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    oProduto.id = idProduto;

                    ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                    oAutomacao.codigoBarras = codigobarras;
                    oAutomacao.qtdEmbalagem = 1;

                    oProduto.vAutomacao.add(oAutomacao);

                    vProduto.put(codigobarras, oProduto);
                }
                
            }
            
            return vProduto;
            
        } catch(SQLException | NumberFormatException ex) {
            
            throw ex;
        }
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
    
    public List<ProdutoVO> carregarICMS() throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0, idAliquota = 8;        
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            sql = new StringBuilder();
            
            sql.append("select P.CODIGO, trim(Upper(icms2.descri)) as descri1 ");
            sql.append("from produtos AS P  ");
            sql.append("LEFT JOIN ICMSPROD AS ICMPROD ON ICMPROD.CODPRO = P.CODIGO  ");
            sql.append("LEFT join icms as icms2 on icms2.codigo =  ICMPROD.icms  ");
            sql.append("where p.inativ = 'N'  ");
            sql.append("order by P.CODIGO        ");   
            
            rst = stm.executeQuery(sql.toString());
            
            while (rst.next()) {
                
                idProduto = Integer.parseInt(rst.getString("CODIGO").trim());
                
                if ((rst.getString("descri1") != null)
                        && (!rst.getString("descri1").trim().isEmpty())) {
                    idAliquota = retornarAliquotaICMSShi(rst.getString("descri1").trim());
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
                
                oAnterior.ref_icmsdebito = String.valueOf(idAliquota);
                
                oProduto.vCodigoAnterior.add(oAnterior);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }   

    /**
     * Importa informação de quitação de cheques recebidos.
     * @param idLojaVR
     * @param idLojaCliente 
     */
    public void importarQuitacaoDeChequesDevolvidos(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Importando informações de quitação dos cheques");
        List<ReceberChequeVO> cheques = this.carregarQuitacaoCheque(idLojaVR, idLojaCliente);
        new ReceberChequeDAO().importarQuitacaoChequeSHI(cheques, idLojaVR);
    }

    private List<ReceberChequeVO> carregarQuitacaoCheque(int idLojaVR, int idLojaCliente) throws Exception {
        List<ReceberChequeVO> result = new ArrayList<>();
        
        //Obtendo as informações de pagamento
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                "SELECT\n" +
                "    c.bancox,\n" +
                "    c.agenci,\n" +
                "    c.contax,\n" +
                "    c.cheque,\n" +
                "    c.ciccgc,\n" +
                "    c.quitad,\n" +
                "    c.valorx,\n" +
                "    c.dataxx,\n" +
                "    c.vencim,\n" +
                "    cp.*\n" +
                "FROM\n" +
                "    cheques c\n" +
                "    left join chequespagto cp on cp.filial = c.filial and cp.sequen = c.sequen\n" +
                "WHERE c.FILIAL = " + idLojaCliente + " and not c.quitad is null\n" +
                "ORDER BY\n" +
                "    c.bancox,\n" +
                "    c.agenci,\n" +
                "    c.contax,\n" +
                "    c.cheque"
            )) {
                while (rst.next()) {
                    ReceberChequeVO ch = new ReceberChequeVO();                    
                    ReceberChequeItemVO vo = new ReceberChequeItemVO();
                    ReceberChequeHistoricoVO hist = new ReceberChequeHistoricoVO();
                    ch.getvBaixa().add(vo);        
                    ch.getvHistorico().add(hist);
                    
                    String dataemissao, datavencimento;
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    if ((rst.getString("dataxx") != null) &&
                        (!rst.getString("dataxx").trim().isEmpty())) {
                        dataemissao = formatter.format(rst.getDate("dataxx"));
                    } else {
                        dataemissao = formatter.format(new java.util.Date());
                    }

                    if ((rst.getString("vencim") != null) &&
                            (!rst.getString("vencim").trim().isEmpty())) {
                        datavencimento = formatter.format(rst.getDate("vencim"));
                    } else {
                        datavencimento = formatter.format(new java.util.Date());
                    }
                    
                    ch.impSistemaId = NOME_SISTEMA;
                    ch.impLojaId = rst.getString("FILIAL");
                    ch.impId = rst.getString("ciccgc") + "-" + rst.getString("bancox") + "-" + rst.getString("agenci") + "-" + rst.getString("contax") + "-" + rst.getString("cheque") + "-" + new SimpleDateFormat("yyyy-MM-dd").format(rst.getDate("dataxx")) + "-" + rst.getString("valorx"); 
                    ch.setData(dataemissao);
                    ch.setDatadeposito(datavencimento);
                    ch.setId_banco(rst.getInt("bancox"));
                    ch.setAgencia(rst.getString("agenci"));
                    ch.setConta(rst.getString("contax"));
                    ch.setValor(rst.getDouble("valorx"));
                    ch.setNumerocheque(Utils.stringToInt(rst.getString("cheque")));
                    ch.setCpf(Utils.stringToLong(rst.getString("ciccgc")));
                    
                    hist.impSistemaId = NOME_SISTEMA;
                    hist.impLojaId = rst.getString("FILIAL");
                    hist.setId_tipoalinea(0);
                    hist.setDatahora(rst.getDate("DATAXX") == null ? rst.getDate("QUITAD") : rst.getDate("DATAXX"));
                    hist.impId = ch.impId + "-0-" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(hist.getDatahora());
                    
                    vo.impSistemaId = NOME_SISTEMA;
                    vo.impLojaId = rst.getString("FILIAL");
                    vo.impId = ch.impId + "-0-" + rst.getString("SEQUEN") + "/" + rst.getString("IDXXXX");                                       
                    vo.setValor(rst.getDouble("VALORX"));
                    vo.setValordesconto(0);                    
                    vo.setValorjuros(rst.getDouble("VALJUR"));
                    vo.setValormulta(0);
                    vo.setDatabaixa(rst.getDate("QUITAD"));
                    vo.setDatapagamento(rst.getDate("DATAXX"));
                    vo.setId_tiporecebimento(rst.getInt("TIPPAG"));
                    vo.setId_loja(idLojaVR);                    
                    vo.setObservacao("IMPORTADO VR");
                    
                    result.add(ch);
                }
            }
        }
        return result;
    }
    private static final String NOME_SISTEMA = "SHI";
    
    public static class IcmsShi{
        public String triPdv2 = "I";
        public double reduzi = 0d;
        public double icmDeb = 0d;
        public boolean substituido = false;
        public boolean isento = false;
        public boolean diferido = false;

        public IcmsShi(String triPdv2, double reduzi, double icmDeb, boolean substituido, boolean isento, boolean diferido) {            this.triPdv2 = triPdv2;
            this.reduzi = reduzi;
            this.icmDeb = icmDeb;
            this.substituido = substituido;
            this.isento = isento;
            this.diferido = diferido;
        }

        @Override
        public String toString() {
            return triPdv2 + "-" + reduzi + "-" + icmDeb + "-" + substituido + "-" + isento + "-" + diferido;
        }        
    }
    
    public static final Map<String, Integer> mapIcmsCeara;
    public static final Map<String, Integer> mapIcmsSp;
    
    static {
        mapIcmsCeara = new HashMap<>();   //substituido|isento
        mapIcmsCeara.put(new IcmsShi("I", 0d, 0d, false, true, false).toString(), 6);
        mapIcmsCeara.put(new IcmsShi("F", 0d, 0d, true, false, false).toString(), 7);
        mapIcmsCeara.put(new IcmsShi("T0", 61.11d, 7d, false, false, false).toString(), 4);
        mapIcmsCeara.put(new IcmsShi("T0", 41.67d, 7d, false, false, false).toString(), 5);
        mapIcmsCeara.put(new IcmsShi("T1", 33.33d, 12d, false, false, false).toString(), 9);
        mapIcmsCeara.put(new IcmsShi("I", 0d, 0d, false, false, false).toString(), 8);
        mapIcmsCeara.put(new IcmsShi("T0", 0d, 7d, false, false, false).toString(), 25);
        mapIcmsCeara.put(new IcmsShi("I", 0d, 0d, false, true, true).toString(), 14);
        mapIcmsCeara.put(new IcmsShi("I", 0d, 4d, false, false, false).toString(), 6);
        mapIcmsCeara.put(new IcmsShi("T1", 0d, 12d, false, false, false).toString(), 1);
        mapIcmsCeara.put(new IcmsShi("T2", 0d, 18d, false, false, false).toString(), 2);
        mapIcmsCeara.put(new IcmsShi("T3", 0d, 25d, false, false, false).toString(), 3);
        
        mapIcmsSp = new HashMap<>();   //substituido|isento|diferido
        mapIcmsSp.put(new IcmsShi("I", 0d, 0d, false, true, false).toString(), 6);
        mapIcmsSp.put(new IcmsShi("F", 0d, 0d, true, false, false).toString(), 7);
        mapIcmsSp.put(new IcmsShi("T0", 61.11d, 7d, false, false, false).toString(), 4);
        mapIcmsSp.put(new IcmsShi("T0", 41.67d, 7d, false, false, false).toString(), 5);
        mapIcmsSp.put(new IcmsShi("T1", 33.33d, 12d, false, false, false).toString(), 9);
        mapIcmsSp.put(new IcmsShi("I", 0d, 0d, false, false, false).toString(), 6);
        mapIcmsSp.put(new IcmsShi("T0", 0d, 7d, false, false, false).toString(), 0);
        mapIcmsSp.put(new IcmsShi("I", 0d, 0d, false, true, true).toString(), 14);
        mapIcmsSp.put(new IcmsShi("I", 0d, 4d, false, false, false).toString(), 6);
        mapIcmsSp.put(new IcmsShi("T1", 0d, 12d, false, false, false).toString(), 1);
        mapIcmsSp.put(new IcmsShi("T2", 0d, 18d, false, false, false).toString(), 2);
        mapIcmsSp.put(new IcmsShi("T3", 0d, 25d, false, false, false).toString(), 3);
    }
    
    public List<ProdutoVO> carregarICMS2(int ufId) throws Exception {
        List<ProdutoVO> vProduto = new ArrayList<>();
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        int idProduto = 0, idAliquota = 8;        
        
        try {
            stm = ConexaoFirebird.getConexao().createStatement();
            /*sql = new StringBuilder();
            
            
            sql.append("select P.CODIGO, trim(Upper(icms2.descri)) as descri1 ");
            sql.append("from produtos AS P  ");
            sql.append("LEFT JOIN ICMSPROD AS ICMPROD ON ICMPROD.CODPRO = P.CODIGO  ");
            sql.append("LEFT join icms as icms2 on icms2.codigo =  ICMPROD.icms  ");
            sql.append("where p.inativ = 'N'  ");
            sql.append("order by P.CODIGO        ");   
            
            rst = stm.executeQuery(sql.toString());*/
            
            rst = stm.executeQuery(
                "select\n" +
                "    P.CODIGO cod_produto,\n" +
                "    trim(upper(ICMS2.DESCRI)) as descr,\n" +
                "    ICMPROD.CST,\n" +
                "    ICMS2.TRIPDV2 tribpdv,\n" +
                "    ICMS2.REDUZI reduzido,\n" +
                "    ICMS2.ICMDEB aliq,\n" +
                "    coalesce(ICMS2.SUBTRI,'N') substituido,\n" +
                "    coalesce(ICMS2.ISENTO,'N') isento,\n" +
                "    case trim(upper(ICMS2.DESCRI)) when 'DIFERIDO' then 'S' else 'N' end as diferimento\n" +
                "from PRODUTOS as P\n" +
                "left join ICMSPROD as ICMPROD on ICMPROD.CODPRO = P.CODIGO\n" +
                "left join ICMS as ICMS2 on ICMS2.CODIGO = ICMPROD.ICMS\n" +
                "where P.INATIV = 'N'\n" +
                "order by P.CODIGO "
            );
            
            while (rst.next()) {                
                
                idProduto = Integer.parseInt(rst.getString("cod_produto").trim());
                
                IcmsShi icms = new IcmsShi(
                    rst.getString("tribpdv"), 
                    rst.getDouble("reduzido"), 
                    rst.getDouble("aliq"), 
                    rst.getString("substituido") != null && rst.getString("substituido").equals("S"), 
                    rst.getString("isento") != null && rst.getString("isento").equals("S"), 
                    rst.getString("diferimento") != null && rst.getString("diferimento").equals("S"));
                
                switch(ufId) {
                    case 23: {
                        if (mapIcmsCeara.containsKey(icms.toString())) {
                            idAliquota = mapIcmsCeara.get(icms.toString());
                        } else {
                            idAliquota = 8;
                        }
                        break;
                    }                    
                    default: {
                        if (mapIcmsSp.containsKey(icms.toString())) {
                            idAliquota = mapIcmsSp.get(icms.toString());
                        } else {
                            idAliquota = 8;
                        }
                        break;
                    }
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
                
                oAnterior.ref_icmsdebito = String.valueOf(idAliquota);
                
                oProduto.vCodigoAnterior.add(oAnterior);
                
                vProduto.add(oProduto);
            }
            
            return vProduto;
        } catch(Exception ex) {
            throw ex;
        }
    }    
    
    //IMPORTAÇÕES
    
    public void importarCnpjCpf(int idLoja, int idLojaCliente) throws Exception {
        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClienteShi(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().atualizaCnpjCpf(vClientePreferencial, idLojaCliente);

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public void importarClientePreferencial(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Clientes...");
            List<ClientePreferencialVO> vClientePreferencial = carregarClienteShi(idLoja, idLojaCliente);
            new PlanoDAO().salvar(idLoja);
            new ClientePreferencialDAO().salvar(vClientePreferencial, idLoja, idLojaCliente);

        } catch (Exception ex) {

            throw ex;
        }
    }  

    public void importarFornecedor() throws Exception {

            try {

                ProgressBar.setStatus("Carregando dados...Fornecedor...");
                List<FornecedorVO> vFornecedor = carregarFornecedorShi();

                new FornecedorDAO().salvar(vFornecedor);

            } catch (Exception ex) {

                throw ex;
            }
        }
    
    public void importarFamiliaProduto() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Familia Produto...");
            List<FamiliaProdutoVO> vFamiliaProduto = carregarFamiliaProdutoShi();

            new FamiliaProdutoDAO().salvar(vFamiliaProduto);

        } catch (Exception ex) {

            throw ex;
        }
    }    
    
    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vMercadologico;
        try {
            
            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            MercadologicoDAO dao = new MercadologicoDAO();            
            
            vMercadologico = carregarMercadologicoShi(1);
            dao.salvar(vMercadologico, true);

            vMercadologico = carregarMercadologicoShi(2);
            dao.salvar(vMercadologico, false);

            vMercadologico = carregarMercadologicoShi(3);
            dao.salvar(vMercadologico, false);
            
            dao.temNivel4 = false;
            dao.salvarMax();
        } catch (Exception ex) {

            throw ex;
        }
    }   

    public void importarProdutoShi(int id_loja) throws Exception {
        
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...");
            Map<Integer, ProdutoVO> vProdutoMilenio = carregarProdutoShi();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vProdutoMilenio.size());
            
            for (Integer keyId : vProdutoMilenio.keySet()) {
                
                ProdutoVO oProduto = vProdutoMilenio.get(keyId);

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
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProduto(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vCustoProduto.size());
            
            for (Integer keyId : vCustoProduto.keySet()) {
                
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
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProduto(id_loja, id_lojaCliente);
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
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
            
            produto.alterarEstoqueProduto(vProdutoNovo, id_loja);
            
        } catch(Exception ex) {
            
            throw ex;
        }        
    }    

    public void importarICMS() throws Exception {
        try {
            
            ProgressBar.setStatus("Carregando dados...ICMS...");
            //FIXME Corrigir esse erro
            List<ProdutoVO> vProduto = carregarICMS2(35);
            
            new ProdutoDAO().alterarICMSProduto(vProduto);
        } catch(Exception ex) {
            throw ex;
        }
    }    
    
    public void importarCodigoBarra() throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
                
        try {
            
            ProgressBar.setStatus("Carregando dados...Produtos...CodigoBarra...");
            Map<Long, ProdutoVO> vEstoqueProduto = carregarCodigoBarras();
            
            List<LojaVO> vLoja = new LojaDAO().carregar();
            
            ProgressBar.setMaximum(vEstoqueProduto.size());
            
            for (Long keyId : vEstoqueProduto.keySet()) {
                
                ProdutoVO oProduto = vEstoqueProduto.get(keyId);

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

            new ReceberChequeDAO().salvarComCondicao(vReceberCheque, id_loja);

        } catch (Exception ex) {

            throw ex;
        }
    }      
    
    public void importarProdutoFornecedor() throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Produto Fornecedor...");
            List<ProdutoFornecedorVO> vProdutoFornecedor = carregarProdutoFornecedor();

            new ProdutoFornecedorDAO().salvar(vProdutoFornecedor);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarReceberClienteShi(int idLoja, int idLojaCliente) throws Exception {

        try {

            ProgressBar.setStatus("Carregando dados...Receber Cliente...");
            List<ReceberCreditoRotativoVO> vReceberCliente = carregarReceberClienteShi(idLoja, idLojaCliente);

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
    private int retornarAliquotaICMSShi(String descTrib) {

        int retorno;
        
        if ("07%".equals(descTrib)) {
            retorno = 0;
        } else if ("12%".equals(descTrib)) {
            retorno = 1;
        } else if ("18%".equals(descTrib)) {
            retorno = 2;
        } else if ("25%".equals(descTrib)) {
            retorno = 3;
        } else if ("ISENTO".equals(descTrib)) {
            retorno = 6;
        } else if (("SUBST.TRIB".equals(descTrib))||
                ("SUBST".equals(descTrib.substring(0, 5)))){
            retorno = 7;
        } else if ("RED 41.67".equals(descTrib)) {
            retorno = 5;
        } else if ("RED 33.33".equals(descTrib)) {
            retorno = 9;
        } else if ("RED 61.11".equals(descTrib)) {
            retorno = 4;
        } else  {
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
}