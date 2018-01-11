package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.MercadologicoDAO;
import vrimplantacao.dao.cadastro.NcmDAO;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.dao.cadastro.ProdutoDAO;
import vrimplantacao.gui.interfaces.classes.LojaClienteVO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.FornecedorVO;
import vrimplantacao.vo.vrimplantacao.MercadologicoVO;
import vrimplantacao.vo.vrimplantacao.NcmVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAliquotaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoComplementoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoFornecedorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/*

Este módulo precisa de mais análise, pois foi feito especificamente para o Rei do Alho - PA
Este módulo precisa de mais análise, pois foi feito especificamente para o Rei do Alho - PA
Este módulo precisa de mais análise, pois foi feito especificamente para o Rei do Alho - PA
Este módulo precisa de mais análise, pois foi feito especificamente para o Rei do Alho - PA

*/


/**
 * Dao para importar informações do sistema Contretize
 * @author Leandro
 */
public class ConcretizeDao extends AbstractIntefaceDao {

    public List<LojaClienteVO> getLojasDoCliente() throws SQLException{
        List<LojaClienteVO> lojas = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codfilial, filial from filial order by codfilial"
            )) {
                while (rst.next()) {
                    lojas.add(new LojaClienteVO(rst.getInt("codfilial"), rst.getString("filial")));
                }
            }
        }   
        return lojas;     
    }

    @Override
    public void importarMercadologico() throws Exception {
        List<MercadologicoVO> vMercadologico;

        ProgressBar.setStatus("Carregando dados...Mercadologico...");
        MercadologicoDAO dao = new MercadologicoDAO();

        vMercadologico = carregarMercadologico(1);
        dao.salvar(vMercadologico, true);

        vMercadologico = carregarMercadologico(2);
        dao.salvar(vMercadologico, false);

        vMercadologico = carregarMercadologico(3);
        dao.salvar(vMercadologico, false);
        
        dao.salvarMax();
    }

    
    
    @Override
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {
        List<MercadologicoVO> mercadologicos = new ArrayList<>();
        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {
            String sql;
            
            sql = 
                    "select distinct\n" +
                    "    coalesce(cast(depto.codepto as integer),0) as id_merc1,\n" +
                    "    depto.departamento as merc1,\n" +
                    "    coalesce(cast(secao.codsec as integer),0) as id_merc2,\n" +
                    "    secao.secao as merc2,\n" +
                    "    coalesce(cast(categ.codcateg as integer),0) as id_merc3,\n" +
                    "    categ.categoria as merc3\n" +
                    "from depto \n" +
                    "    left join secao on secao.codepto = depto.codepto\n" +
                    "    left join categ on secao.codepto = categ.codepto and secao.codsec = categ.codcateg\n" +
                    "order by\n" +
                    "    id_merc1, id_merc2, id_merc3";
            
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    MercadologicoVO oMercadologico = new MercadologicoVO();

                    String descricao;
                 
                    if (nivel == 1) {
                        descricao = Utils.acertarTexto(rst.getString("merc1"), 35);

                        oMercadologico.mercadologico1 = rst.getInt("id_merc1");
                        oMercadologico.mercadologico2 = 0;
                        oMercadologico.mercadologico3 = 0;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = descricao;
                        oMercadologico.nivel = nivel;

                    } else if (nivel == 2) {

                        descricao = Utils.acertarTexto(rst.getString("merc2"), 35);

                        oMercadologico.mercadologico1 = rst.getInt("id_merc1");
                        
                        if (rst.getInt("id_merc2") != 0) {
                            oMercadologico.mercadologico2 = rst.getInt("id_merc2");
                        } else {
                            oMercadologico.mercadologico2 = 1;
                        }
                        oMercadologico.mercadologico3 = 0;
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = Utils.acertarTexto(descricao, rst.getString("merc1"));
                        oMercadologico.nivel = nivel;
                    } else if (nivel == 3) {

                        descricao = Utils.acertarTexto(rst.getString("merc3"), 35);
                        
                        oMercadologico.mercadologico1 = rst.getInt("id_merc1");
                        if (rst.getInt("id_merc2") != 0) {
                            oMercadologico.mercadologico2 = rst.getInt("id_merc2");
                        } else {
                            oMercadologico.mercadologico2 = 1;
                        }
                        if (rst.getInt("id_merc3") != 0) {
                            oMercadologico.mercadologico3 = rst.getInt("id_merc3");
                        } else {
                            oMercadologico.mercadologico3 = 1;
                        }
                        oMercadologico.mercadologico4 = 0;
                        oMercadologico.mercadologico5 = 0;
                        oMercadologico.descricao = Utils.acertarTexto(descricao, rst.getString("merc2"));
                        if (oMercadologico.descricao.equals("")) {
                            oMercadologico.descricao = rst.getString("merc1");
                        }
                        oMercadologico.nivel = nivel;
                    }

                    mercadologicos.add(oMercadologico);
                }
            }
        }
        return mercadologicos;        
        

    }

    @Override
    public List<FornecedorVO> carregarFornecedor() throws Exception {
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        try (Statement stm = ConexaoOracle.createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    f.codfornec,\n" +
                "    f.fornecedor,\n" +
                "    f.fantasia,\n" +
                "    f.endereco,\n" +
                "    f.bairro,\n" +
                "    f.estado,\n" +
                "    f.cidade,\n" +
                "    cast(f.codmunicipio as integer) codmunicipio,\n" +
                "    f.cep,    \n" +
                "    f.endercob,\n" +
                "    f.bairrocob,\n" +
                "    f.estcob,\n" +
                "    f.cidadecob,\n" +
                "    f.cepcob,    \n" +
                "    f.telefone,\n" +
                "    f.telefone2,\n" +
                "    f.telcob telefone3,\n" +
                "    f.ie,\n" +
                "    f.cpfcnpj,\n" +
                "    f.fax,\n" +
                "    f.email,\n" +
                "    f.obs,\n" +
                "    f.dtcadastro,\n" +
                "    f.bloqueio,    \n" +
                "    f.tipofj,\n" +
                "    cd.coduf id_uf,\n" +
                "    cd.codmunicipio id_municipio,\n" +
                "    cdcob.coduf id_ufcob,\n" +
                "    cdcob.codmunicipio id_municipiocob\n" +
                "from \n" +
                "    fornecedor f\n" +
                "    left join cidade cd on cd.cidade = f.cidade and cd.estado = f.estado\n" +
                "    left join cidade cdcob on cdcob.cidade = f.cidadecob and cdcob.estado = f.estadocob\n" +
                "order by\n" +
                "    f.codfornec\n"
            )) {
                
                while (rst.next()) {

                    FornecedorVO forn = new FornecedorVO();

                    forn.setCodigoanterior(rst.getInt("codfornec"));
                    forn.setRazaosocial(rst.getString("fornecedor"));
                    forn.setNomefantasia(rst.getString("fantasia"));
                    forn.setEndereco(rst.getString("endereco"));
                    forn.setNumero("0");
                    forn.setComplemento("");
                    forn.setBairro(rst.getString("bairro"));
                    //Determina se o municipio esta cadastrado no sistema
                    //int municipio = Utils.retornarMunicipioIBGECodigo(2304400);
                    forn.setId_municipio(
                        Utils.existeMunicipioIBGECodigo(rst.getInt("id_municipio")) ?
                        rst.getInt("id_municipio") :
                        1500800
                    );      

                    forn.setCep(
                        Integer.parseInt(Utils.formataNumero(rst.getString("cep"))) != 0 ?
                        Integer.parseInt(Utils.formataNumero(rst.getString("cep"))) :
                        67145074
                    );        
                    forn.setId_estado(
                        rst.getInt("id_uf") != 0 ?
                        rst.getInt("id_uf") :
                        15
                    );                
                    //COBRANÇA

                    forn.setEnderecocobranca(rst.getString("endercob"));
                    forn.setNumerocobranca("SN");
                    forn.setComplementocobranca("");
                    forn.setBairrocobranca(rst.getString("bairrocob"));
                    //Determina se o municipio esta cadastrado no sistema
                    //int municipio = Utils.retornarMunicipioIBGECodigo(2304400);
                    forn.setId_municipiocobranca(
                        Utils.existeMunicipioIBGECodigo(rst.getInt("id_municipiocob")) ?
                        rst.getInt("id_municipiocob") :
                        1500800
                    );            
                    forn.setCepcobranca(
                        Integer.parseInt(Utils.formataNumero(rst.getString("cepcob"))) != 0 ?
                        Integer.parseInt(Utils.formataNumero(rst.getString("cepcob"))) :
                        67145074
                    );               
                    forn.setId_estado(
                        rst.getInt("id_ufcob") != 0 ?
                        rst.getInt("id_ufcob") :
                        15
                    );



                    forn.setTelefone(rst.getString("telefone"));
                    forn.setTelefone2(rst.getString("telefone2"));
                    forn.setTelefone3(rst.getString("telefone3"));
                    forn.setInscricaoestadual(rst.getString("ie"));
                    forn.setCnpj(Utils.stringToLong(rst.getString("cpfcnpj")));
                    if (rst.getString("tipofj") != null && rst.getString("tipofj").equals("J")) {
                        forn.setId_tipoinscricao(0);
                    } else {
                        forn.setId_tipoinscricao(1);
                    }
              
                    forn.setFax(rst.getString("fax"));
                    forn.setEmail(rst.getString("email"));
                    /*Como não há observações adicionais neste fornecedor, são criadas
                    novas observações*/
                    forn.setObservacao(rst.getString("obs"));
                    forn.setDatacadastro(rst.getDate("dtcadastro"));
                    forn.setBloqueado(rst.getString("bloqueio") != null && rst.getString("bloqueio").equals("S"));

                    vFornecedor.add(forn);
                }

                return vFornecedor;
            }
        }
    }

    @Override
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new java.sql.Date(new java.util.Date().getTime());

        try (Statement stm = ConexaoOracle.createStatement()) {    
            try (ResultSet rst = stm.executeQuery(
                "select\n" +
                "    f.cpfcnpj,\n" +
                "    pf.codfornec,\n" +
                "    pf.codprod,\n" +
                "    p.qtunitcx,\n" +
                "    pf.codprodfor   \n" +
                "from \n" +
                "    prodfornec pf\n" +
                "    join produto p on pf.codprod = p.codprod\n" +
                "    join fornecedor f on pf.codfornec = f.codfornec\n" +
                "order by\n" +
                "    codfornec, codprod"
            )){

                while (rst.next()) {

                    idFornecedor = rst.getInt("codfornec");
                    idProduto = rst.getInt("codprod");
                    qtdEmbalagem = (int) rst.getDouble("qtunitcx");

                    if ((rst.getString("codprodfor") != null)
                            && (!rst.getString("codprodfor").isEmpty())) {
                        codigoExterno = Utils.acertarTexto(rst.getString("codprodfor").replace("'", ""));
                    } else {
                        codigoExterno = "";
                    }

                    ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                    oProdutoFornecedor.cnpFornecedor = Utils.stringToLong(rst.getString("cpfcnpj"));
                    oProdutoFornecedor.id_fornecedor = idFornecedor;
                    oProdutoFornecedor.id_produto = idProduto;
                    oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                    oProdutoFornecedor.dataalteracao = dataAlteracao;
                    oProdutoFornecedor.codigoexterno = codigoExterno;

                    vProdutoFornecedor.add(oProdutoFornecedor);
                }

                return vProdutoFornecedor;
            }
        }
    }

    @Override
    public List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Utils util = new Utils();
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        String nome, endereco, bairro, telefone1, inscricaoestadual, email, enderecoEmpresa, nomeConjuge,
                dataResidencia, dataCadastro, numero, complemento, dataNascimento, nomePai, nomeMae,
                telefone2 = "", fax = "", observacao = "", empresa = "", telEmpresa = "", cargo = "",
                conjuge = "", orgaoExp = "", observacao2 = "";
        int id_municipio = 0, id_estado, id_sexo, id_tipoinscricao = 1, id, id_situacaocadastro,
                estadoCivil = 0;
        long cnpj, cep;
        double limite, salario;
        boolean bloqueado;
        //DecimalFormat df = new DecimalFormat("#.00");

        try (Statement stm = ConexaoOracle.getConexao().createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select    \n" +
                    "    c.codcli as id,\n" +
                    "    c.cliente as nome,\n" +
                    "    c.fantasia as fantasia,\n" +
                    "    c.endereco as endereco,\n" +
                    "    c.bairro as bairro,\n" +
                    "    cd.coduf as id_estado,\n" +
                    "    cd.codmunicipio as id_municipio,\n" +
                    "    c.cep as cep,\n" +
                    "    c.telefone as telefone1,\n" +
                    "    c.ie as inscricaoestadual,\n" +
                    "    c.cpfcnpj as cnpj,\n" +
                    "    c.sexo as id_sexo,\n" +
                    "    c.identidade,\n" +
                    "    null as dataResidencia,\n" +
                    "    c.dtcadastro as dataCadastro,\n" +
                    "    c.email as email,\n" +
                    "    c.limcred as limite,\n" +
                    "    c.codcli as codigoanterior,\n" +
                    "    c.fax as fax,\n" +
                    "    case when c.dtbloq is null then 0 else 1 end as bloqueado,\n" +
                    "    case when dtexclusao is null then 1 else 0 end as id_situacaocadastro,\n" +
                    "    c.telefone2 as telefone2,\n" +
                    "    c.telent as telefone_entrega,\n" +
                    "    c.telcob as telefone_entrega,\n" +
                    "    c.obs as observacao,\n" +
                    "    coalesce(c.enderent,'') || ',' || coalesce(c.bairroent,'') || ',' || coalesce(c.cidadeent,'') || ' - ' || coalesce(c.estadoent,'') || ' - CEP ' || coalesce(c.cepent,'') as endereco_entrega,\n" +
                    "    coalesce(c.endercob,'')  || ',' || coalesce(c.bairrocob,'') || ',' || coalesce(c.cidadecob,'') || ' - ' || coalesce(c.estadocob,'') || ' - CEP ' || coalesce(c.cepcob,'') as endereco_cobranca,\n" +
                    "    '' as observacao2,\n" +
                    "    null as dataNascimento,\n" +
                    "    c.pai as  nomePai,\n" +
                    "    c.mae as nomeMae,\n" +
                    "    c.empresa as empresa,\n" +
                    "    c.telemp as telEmpresa,\n" +
                    "    0 as numero,\n" +
                    "    c.profissao as cargo,\n" +
                    "    coalesce(c.enderemp,'') || ', ' || coalesce(c.bairroemp,'') enderecoEmpresa,\n" +
                    "    c.tipofj as id_tipoinscricao,\n" +
                    "    c.salario as salario,\n" +
                    "    c.estcivil as estadoCivil,\n" +
                    "    c.conjuge as conjuge,\n" +
                    "    '' as orgaoExp\n" +
                    "from \n" +
                    "    cliente c\n" +
                    "    left join cidade cd on cd.cidade = c.cidade and cd.estado = c.estado\n" +
                    "order by\n" +
                    "    c.codcli"
            )) {
                while (rst.next()) {
                    ClientePreferencialVO oClientePreferencial = new ClientePreferencialVO();



                    if ((rst.getString("endereco") != null)
                            && (!rst.getString("endereco").trim().isEmpty())) {
                        endereco = Utils.acertarTexto(rst.getString("endereco").replace("'", "").trim());
                    } else {
                        endereco = "";
                    }

                    if ((rst.getString("bairro") != null)
                            && (!rst.getString("bairro").trim().isEmpty())) {
                        bairro = Utils.acertarTexto(rst.getString("bairro").trim().replace("'", ""));
                    } else {
                        bairro = "";
                    }

                    numero = "0";              
                    complemento = "";


                    if ((rst.getString("telefone1") != null)
                            && (!rst.getString("telefone1").trim().isEmpty())) {
                        telefone1 = Utils.formataNumero(rst.getString("telefone1").trim());
                    } else {
                        telefone1 = "0";
                    }

                    if ((rst.getString("cep") != null)
                            && (!rst.getString("cep").trim().isEmpty())) {
                        cep = Long.parseLong(Utils.formataNumero(rst.getString("cep").trim()));
                    } else {
                        cep = 0;
                    }

                    id_estado = rst.getInt("id_estado");
                    if (id_estado == 0) {
                        id_estado = 15;
                    }
                    id_municipio = rst.getInt("id_municipio");
                    if (id_municipio == 0) {
                        id_municipio = 1500800;
                    }                

                    if ((rst.getString("limite") != null)
                            && (!rst.getString("limite").trim().isEmpty())) {

                        limite = rst.getDouble("limite");
                    } else {
                        limite = 0;
                    }

                    if ((rst.getString("inscricaoestadual") != null)
                            && (!rst.getString("inscricaoestadual").trim().isEmpty())) {
                        inscricaoestadual = Utils.acertarTexto(rst.getString("inscricaoestadual").trim());
                        inscricaoestadual = inscricaoestadual.replace("'", "");
                        inscricaoestadual = inscricaoestadual.replace("-", "");
                        inscricaoestadual = inscricaoestadual.replace(".", "");
                    } else {

                        if ((rst.getString("identidade") != null) &&
                                (!rst.getString("identidade").trim().isEmpty())) {
                            inscricaoestadual = Utils.acertarTexto(rst.getString("identidade").trim());
                            inscricaoestadual = inscricaoestadual.replace("'", "");
                            inscricaoestadual = inscricaoestadual.replace("-", "");
                            inscricaoestadual = inscricaoestadual.replace(".", "");

                            if ((rst.getString("orgaoExp") != null) &&
                                    (!rst.getString("orgaoExp").trim().isEmpty())) {
                                orgaoExp = Utils.acertarTexto(rst.getString("orgaoExp").trim().replace("'", ""));
                            } else {
                                orgaoExp = "";
                            }
                        } else {
                            inscricaoestadual = "ISENTO";
                        }
                    }

                    if ((rst.getString("dataCadastro") != null)
                            && (!rst.getString("dataCadastro").trim().isEmpty())) {
                        dataCadastro = rst.getString("dataCadastro").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }

                    if ((rst.getString("dataNascimento") != null)
                            && (!rst.getString("dataNascimento").trim().isEmpty())) {
                        dataNascimento = rst.getString("dataNascimento").substring(0, 10).trim().replace("-", "/");
                    } else {
                        dataNascimento = null;
                    }

                    telefone2 = rst.getString("telefone2");

                    fax = rst.getString("fax");

                    if ((rst.getString("observacao") != null)
                            && (!rst.getString("observacao").trim().isEmpty())) {
                        observacao = Utils.acertarTexto(rst.getString("observacao").replace("'", "").trim());
                    } else {
                        observacao = "";
                    }

                    email = "";

                    if ((rst.getString("id_sexo") != null)
                            && (!rst.getString("id_sexo").trim().isEmpty())) {
                        if ("F".equals(rst.getString("id_sexo").trim())) {
                            id_sexo = 0;
                        } else {
                            id_sexo = 1;
                        }
                    } else {
                        id_sexo = 1;
                    }

                    empresa = rst.getString("empresa");

                    telEmpresa = rst.getString("telEmpresa");

                    cargo = rst.getString("cargo");

                    enderecoEmpresa = rst.getString("enderecoEmpresa");

                    salario = rst.getDouble("salario");

                    if ((rst.getString("estadoCivil") != null)
                            && (!rst.getString("estadoCivil").trim().isEmpty())) {
                        if (null != rst.getString("estadoCivil").trim()) 
                            switch (rst.getString("estadoCivil").trim()) {
                            case "C":
                                estadoCivil = 2;
                                break;
                            case "S":
                                estadoCivil = 1;
                                break;
                            case "O":
                                estadoCivil = 5;
                                break;
                        }
                    } else {
                        estadoCivil = 0;
                    }

                    conjuge = "";

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


                    oClientePreferencial.setId(rst.getInt("id"));
                    oClientePreferencial.setCodigoanterior(rst.getInt("id"));
                    if ((rst.getString("nome") != null)
                            && (!rst.getString("nome").trim().isEmpty())) {
                        byte[] bytes = rst.getBytes("nome");
                        String textoAcertado = new String(bytes, "ISO-8859-1");
                        nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());
                    } else {

                        if ((rst.getString("fantasia") != null) &&
                                (!rst.getString("fantasia").trim().isEmpty())) {
                            byte[] bytes = rst.getBytes("nome");
                            String textoAcertado = new String(bytes, "ISO-8859-1");
                            nome = Utils.acertarTexto(textoAcertado.replace("'", "").trim());                            
                        } else {
                            nome = "SEM NOME VR " + oClientePreferencial.getId();
                        }
                    }
                    oClientePreferencial.setNome(nome);
                    oClientePreferencial.setEndereco(endereco);
                    oClientePreferencial.setBairro(bairro);
                    oClientePreferencial.setId_estado(id_estado);
                    oClientePreferencial.setId_municipio(id_municipio);
                    oClientePreferencial.setCep(cep);
                    oClientePreferencial.setTelefone(telefone1);
                    oClientePreferencial.setInscricaoestadual(inscricaoestadual);
                    bloqueado = rst.getInt("bloqueado") == 1;
                    dataResidencia = "1990/01/01";
                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        cnpj = Long.parseLong(Utils.formataNumero(rst.getString("cnpj").trim()));
                    } else {
                        cnpj = -1;
                    }
                    oClientePreferencial.setCnpj(cnpj);
                    oClientePreferencial.setSexo(id_sexo);
                    oClientePreferencial.setDataresidencia(dataResidencia);
                    oClientePreferencial.setDatacadastro(dataCadastro);
                    oClientePreferencial.setEmail(email);
                    oClientePreferencial.setValorlimite(limite);
                    oClientePreferencial.setFax(fax);
                    oClientePreferencial.setBloqueado(bloqueado);
                    oClientePreferencial.setId_situacaocadastro(rst.getInt("id_situacaocadastro"));
                    oClientePreferencial.setTelefone2(telefone2);
                    oClientePreferencial.setObservacao(observacao);
                    oClientePreferencial.setObservacao2(observacao2);
                    oClientePreferencial.setDatanascimento(dataNascimento);
                    oClientePreferencial.setNomepai(rst.getString("nomePai"));
                    oClientePreferencial.setNomemae(rst.getString("nomeMae"));
                    oClientePreferencial.setEmpresa(empresa);
                    oClientePreferencial.setTelefoneempresa(telEmpresa);
                    oClientePreferencial.setNumero(numero);
                    oClientePreferencial.setCargo(cargo);
                    oClientePreferencial.setEnderecoempresa(enderecoEmpresa);
                    if (rst.getString("id_tipoinscricao") != null
                            && !rst.getString("id_tipoinscricao").trim().isEmpty()) {
                        if (rst.getString("id_tipoinscricao").equals("F")) {
                            id_tipoinscricao = 1;
                        } else {
                            id_tipoinscricao = 0;
                        }
                    } 
                    oClientePreferencial.setId_tipoinscricao(id_tipoinscricao);
                    oClientePreferencial.setSalario(salario);
                    oClientePreferencial.setId_tipoestadocivil(estadoCivil);
                    oClientePreferencial.setNomeconjuge(conjuge);
                    oClientePreferencial.setOrgaoemissor(orgaoExp);
                    vClientePreferencial.add(oClientePreferencial);
                }
            }
        }

        return vClientePreferencial;
    }

    
    public void importarCodigoDeBarrasBalanca(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
        Map<Integer, ProdutoVO> vProdutos = carregarProdutoDaBalanca(idLojaVR, idLojaCliente, "c:\\vr\\implantacao\\codigodebalancainexistente.txt");

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        for (Integer keyId : vProdutos.keySet()) { 

            ProdutoVO oProduto = vProdutos.get(keyId);

            oProduto.idProdutoVasilhame = -1;
            oProduto.excecao = -1;
            oProduto.idTipoMercadoria = -1;

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.salvar(vProdutoNovo, idLojaVR, vLoja);
        this.importarCodigoBarraEmBranco();
    }
    
    public void importarPrecoBalanca(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos de balança.....");
        Map<Integer, ProdutoVO> vProdutos = carregarProdutoDaBalanca(idLojaVR, idLojaCliente, "c:\\vr\\implantacao\\atualizandopreçosbalanca.txt");

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        for (Integer keyId : vProdutos.keySet()) { 

            ProdutoVO oProduto = vProdutos.get(keyId);

            oProduto.idProdutoVasilhame = -1;
            oProduto.excecao = -1;
            oProduto.idTipoMercadoria = -1;

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarPrecoProdutoConcretize(vProdutoNovo, idLojaVR);
    }

    private Map<Integer, ProdutoVO> carregarProdutoDaBalanca(int idLojaVR, int idLojaCliente, String fileName) throws Exception {
  
        File f = new File(fileName);
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Produtos de balança que não existem no sistema CONCRETIZE");
        bw.newLine();
        bw.write("*************************************************");
        bw.newLine();
        bw.write("CODIGO | DESCRIÇÃO | PESAVEL | VALIDADE | VALIDA");
        bw.newLine();
        
        Map<Integer, ProdutoVO> result = new LinkedHashMap<>();       
        Map<Integer, ProdutoBalancaVO> produtosDeBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
        
        for (ProdutoBalancaVO produtoBalanca: produtosDeBalanca.values()) {
            try (Statement stm = ConexaoOracle.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct codprod from embalagem where codbarra = " + produtoBalanca.getCodigo()
                )) {
                    if (rst.next()) {
                        double valorDeVenda = 0;
                        double margem = 0;
                        
                        try (ResultSet rs = ConexaoOracle.createStatement().executeQuery(
                                "select ptabela, margem from embalagem where codbarra = " + produtoBalanca.getCodigo() + " and codfilial = " + idLojaCliente
                        )) {
                            if (rs.next()) {
                                valorDeVenda = rs.getDouble("ptabela");
                                if ((rs.getString("margem") != null)
                                        && (!"".equals(rs.getString("margem")))) {
                                    margem = rs.getDouble("margem");
                                }
                            }
                        }
                        
                        int codprod = rst.getInt("codprod");
                        ProdutoVO ret = carregarProdutoPorId(codprod, produtoBalanca.getCodigo(), idLojaVR, idLojaCliente);
                        ret.setId(produtoBalanca.getCodigo());
                        ret.codigoAnterior = codprod;
                        ret.margem = margem;
                        ProdutoComplementoVO auto = ret.vComplemento.get(0);
                        if (auto != null) {
                            auto.precoVenda = valorDeVenda;
                            auto.precoDiaSeguinte = valorDeVenda;
                        }
                        
                        /*if (ret.id == 120114) {
                            Util.exibirMensagem(ret.vComplemento.isEmpty() ? "VAZIO" : "COM VALOR: " + ret.vComplemento.get(0).precoVenda,"");
                        }*/
                        
                        result.put(ret.id, ret);
                    } else {
                        bw.write(produtoBalanca.getCodigo() + " | " + 
                                produtoBalanca.getDescricao() + " | " + 
                                produtoBalanca.getPesavel() + " | " + 
                                produtoBalanca.getValidade() + " | " + 
                                produtoBalanca.getValida());
                        bw.newLine();
                    }
                }
            }
        }
        
        bw.flush();
        bw.close();
        //Util.exibirMensagem("Contou " + cont, null);
        /*
        se existir inclui o produto com o codigo de barras informado
        se não não faz nada
        */
        
        return result;
    }
    
    public ProdutoVO carregarProdutoPorId(int codprod, int codbarrabalanca, int idLojaVR, int idLojaCliente) throws Exception { 
        
        /*
        Obter uma lista dos códigos de barras cadastrados no sistema do cliente
        Separados por codigo do cliente Map<CodProd, List<CodiBarras>>
        */
        try (Statement stm = ConexaoOracle.createStatement()) {
            String sql = 
                "select\n" +
                "    p.codprod,\n" +
                "    p.descricao as desc_completa,\n" +
                "    p.abreviacao as desc_reduzida,\n" +
                "    p.descricao as desc_gondola,\n" +
                "    p.unidade,\n" +
                "    p.qtunitcx as qtdEmbalagem,\n" +
                "    ne.cstpis as cstPisEntr,\n" +
                "    ns.cstpis as cstPisSaid,\n" +
                "    ns.codnatpis as naturezaReceita,\n" +
                "    --verifica pesavel\n" +
                //TODO Efetuar um tratamento no mercadologico, pois o mesmo permite receber string neste sistema
                "    --coalesce(cast(p.codepto as integer),-1) as mercadologico1,\n" +
                "    --coalesce(cast(p.codsec as integer),-1) as mercadologico2,\n" +
                "    --coalesce(cast(p.codcat as integer),-1) as mercadologico3,\n" +
                "    cast(p.cod_ncm as integer) as ncm,\n" +
                "    p.prazovalid as validade,\n" +
                "    p.margem,\n" +
                "    p.pesobruto,\n" +
                "    p.pesoliq,\n" +
                "    p.dtcadastro,\n" +
                "    case p.tipo\n" +
                "    when 'VN' then 1\n" +
                "    else 0 end as situacaoCadastro,\n" +
                "    0/*pc.ptabela*/ as precovenda,\n" +
                "    est.custoreal as custo_imposto,\n" +
                "    coalesce(est.custocont, est.custoreal) as custo_sem_imposto,\n" +
                "    fl.codestado as id_uf,\n" +
                "    fl.estado as sigla_uf,\n" +
                "    trib.aliqicms,\n" +
                "    trib.sittribut,\n" +
                "    trib.obstribut,\n" +
                "    (select count(*) from embalagem where embalagem.codprod = p.codprod and embalagem.codfilial = fl.codfilial) as qtdBarra    \n" +
                "--select count(*)\n" +
                "from \n" +
                "    produto p\n" +
                "    left join cadncm ne on p.cod_ncm = ne.cod_ncm and ne.operacao = 'E' and ne.regtribut = 3\n" +
                "    left join cadncm ns on p.cod_ncm = ns.cod_ncm and ns.operacao = 'S' and ns.regtribut = 3\n" +
                "    left join preco pc on pc.codprod = p.codprod and pc.numregiao = 1\n" +
                "    left join filial fl on fl.codfilial = " + idLojaCliente + "\n" +
                "    left join estoque est on est.codprod = p.codprod and est.codfilial = fl.codfilial\n" +
                "    left join tributacao trib on trib.codtribut = pc.codtribut\n" +
                "where p.codprod = " + codprod;  
            
            //Carrega uma listagem com os produtos de balança
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    int idProduto = rst.getInt("codprod");
                    
                    oProduto.setId(idProduto);
                    oProduto.setDescricaoCompleta(rst.getString("desc_completa"));
                    oProduto.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    oProduto.setDescricaoGondola(rst.getString("desc_gondola"));   
                    oProduto.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    
                    ProdutoBalancaVO prodBal = null;
                    
                    /*try (Statement stm2 = ConexaoOracle.createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery("select codbarra, codprod from embalagem where codfilial = " + idLojaCliente + " and codprod = " + idProduto)) {
                            while (rst2.next()) {
                                if (prodBal == null) {
                                    prodBal = new ProdutoBalancaDAO().localizar(Long.valueOf(codbarrabalanca));
                                }
                            }
                        }
                    }*/
                    
                    if (prodBal == null) {
                        prodBal = new ProdutoBalancaDAO().localizar(Long.valueOf(codbarrabalanca));
                    }
                    
                    int validade = rst.getInt("validade");
                    
                    if (prodBal != null) { 
                        validade = prodBal.getValidade() > 0 ? prodBal.getValidade() : rst.getInt("validade");

                        oProduto.eBalanca = true;
                        oProduto.codigoBalanca = prodBal.getCodigo();
                        oProduto.validade = validade;
                        
                        if (null != prodBal.getPesavel().trim()) {
                            switch (prodBal.getPesavel().trim()) {
                                case "U":
                                    oProduto.pesavel = true;
                                    oProduto.idTipoEmbalagem = 0;
                                    break;
                                case "P":
                                    oProduto.pesavel = false;
                                    oProduto.idTipoEmbalagem = 4;
                                    break;
                            }
                        }
                        
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = Long.parseLong(String.valueOf(idProduto));
                        oAutomacao.qtdEmbalagem = 1;//oProduto.getQtdEmbalagem();
                        oAutomacao.idTipoEmbalagem = oProduto.getIdTipoEmbalagem();
                        oProduto.vAutomacao.add(oAutomacao);
                    } else {
                        oProduto.codigoBalanca = -1;
                        oProduto.eBalanca = false;
                        oProduto.pesavel = false;
                        oProduto.validade = rst.getInt("validade");

                        String unidade = Utils.acertarTexto(rst.getString("unidade")).toUpperCase();
                         
                        switch (unidade) {
                            case "KG":
                                oProduto.idTipoEmbalagem = 4;
                                break;
                            case "UN":
                                oProduto.idTipoEmbalagem = 0;
                                break;
                            default:
                                oProduto.idTipoEmbalagem = 0;
                                break;
                        }
                        
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = -2;
                        oAutomacao.qtdEmbalagem = oProduto.getQtdEmbalagem();
                        oAutomacao.idTipoEmbalagem = oProduto.getIdTipoEmbalagem();
                        oProduto.vAutomacao.add(oAutomacao);
                    }
                    
                    //oProduto.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.idTipoPisCofinsDebito = Utils.retornarPisCofinsDebito(rst.getInt("cstPisSaid"));
                    oProduto.idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(rst.getInt("cstPisEntr"));
                    oProduto.tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("naturezaReceita"));
                    //oProduto.pesavel = pesavel;
                    oProduto.mercadologico1 = 14;
                    oProduto.mercadologico2 = 1;
                    oProduto.mercadologico3 = 1;
                    
                    String ncmAtual = null;
                    int ncm1, ncm2, ncm3;
                    
                    if ((rst.getString("ncm") != null)
                        && (!rst.getString("ncm").trim().isEmpty())
                        && (rst.getString("ncm").trim().length() > 5)) {

                        ncmAtual = Utils.formataNumero(rst.getString("ncm").trim());
                        if ((ncmAtual != null)
                                && (!ncmAtual.isEmpty())
                                && (ncmAtual.length() > 5)) {
                            try {
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
                    }else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                    
                    
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setIdFamiliaProduto(-1);
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
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setPesoBruto(rst.getDouble("pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliq"));
                    
                    String dataCadastro;
                    if ((rst.getString("dtcadastro") != null)
                        && (!rst.getString("dtcadastro").trim().isEmpty())) {
                        dataCadastro = rst.getString("dtcadastro").substring(0, 10).replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }                    
                    oProduto.dataCadastro = dataCadastro;
                    
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.setIdSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("precovenda"));
                    oComplemento.setCustoComImposto(rst.getDouble("custo_imposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custo_sem_imposto"));
                    oProduto.vComplemento.add(oComplemento);
                    
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    int idAliquota = getIdAliquota(rst.getInt("sittribut"), rst.getFloat("aliqicms"));                    
                    oAliquota.setIdEstado(rst.getInt("id_uf"));
                    oAliquota.setIdAliquotaDebito(idAliquota);
                    oAliquota.setIdAliquotaCredito(idAliquota);
                    oAliquota.setIdAliquotaDebitoForaEstado(idAliquota);
                    oAliquota.setIdAliquotaCreditoForaEstado(idAliquota);
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(idAliquota);
                    oProduto.vAliquota.add(oAliquota);                    
                    
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                    oCodigoAnterior.codigoanterior = idProduto;
                    if (prodBal != null) {
                        oCodigoAnterior.barras = idProduto;
                    } else {
                        oCodigoAnterior.barras = -1;
                    }
                    
                    if ((rst.getString("cstPisSaid") != null)
                            && (!rst.getString("cstPisSaid").trim().isEmpty())) {
                        oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("cstPisSaid").trim());
                    } else {
                        oCodigoAnterior.piscofinsdebito = -1;
                    }

                    if ((rst.getString("cstPisEntr") != null)
                            && (!rst.getString("cstPisEntr").trim().isEmpty())) {
                        oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("cstPisEntr").trim());
                    } else {
                        oCodigoAnterior.piscofinscredito = -1;
                    }

                    if ((rst.getString("naturezaReceita") != null)
                            && (!rst.getString("naturezaReceita").trim().isEmpty())) {
                        oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("naturezaReceita").trim());
                    } else {
                        oCodigoAnterior.naturezareceita = -1;
                    }
                    
                    if ((rst.getString("sittribut") != null) && (!rst.getString("sittribut").trim().isEmpty())) {
                        oCodigoAnterior.ref_icmsdebito = rst.getString("sittribut").trim().replace(".", "");
                    } else {
                        oCodigoAnterior.ref_icmsdebito = "";
                    }
                    
                    /* ((rst.getString("cstPisSaid") != null)
                            && (!rst.getString("cstPisSaid").trim().isEmpty())) {
                        oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("cstPisSaid").trim());
                    } else {
                        oCodigoAnterior.piscofinsdebito = -1;
                    }

                    if ((rst.getString("cstPisEntr") != null)
                            && (!rst.getString("cstPisEntr").trim().isEmpty())) {
                        oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("cstPisEntr").trim());
                    } else {
                        oCodigoAnterior.piscofinscredito = -1;
                    }

                    if ((rst.getString("naturezaReceita") != null)
                            && (!rst.getString("naturezaReceita").trim().isEmpty())) {
                        oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("naturezaReceita").trim());
                    } else {
                        oCodigoAnterior.naturezareceita = -1;
                    }

                    if ((rst.getString("CodigoSituacaoTributariaB") != null)
                            && (!rst.getString("CodigoSituacaoTributariaB").trim().isEmpty())) {
                        oCodigoAnterior.ref_icmsdebito = rst.getString("CodigoSituacaoTributariaB").trim().replace(".", "");
                    } else {
                        oCodigoAnterior.ref_icmsdebito = "";
                    }*/

                    oCodigoAnterior.estoque = -1;
                    oCodigoAnterior.e_balanca = oProduto.eBalanca;
                    oCodigoAnterior.codigobalanca = oProduto.codigoBalanca;
                    oCodigoAnterior.custosemimposto = -1;
                    oCodigoAnterior.custocomimposto = -1;
                    oCodigoAnterior.margem = -1;
                    oCodigoAnterior.precovenda = -1;
                    oCodigoAnterior.referencia = -1;
                    oCodigoAnterior.setNcm(rst.getString("ncm") != null ? rst.getString("ncm").trim() : "");
                    
                    oProduto.vCodigoAnterior.add(oCodigoAnterior);

                    return oProduto;
                }
            }   
        }
        return null;
    }

    public void ajustarExcluidos(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Ajuste de excluídos.....");
        Map<Integer, ProdutoVO> vProdutos = carregarProduto(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        for (Integer keyId : vProdutos.keySet()) { 

            ProdutoVO oProduto = vProdutos.get(keyId);

            oProduto.idProdutoVasilhame = -1;
            oProduto.excecao = -1;
            oProduto.idTipoMercadoria = -1;

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarSituacaoCadastroProduto(vProdutoNovo, idLojaVR);
    }

    public void corrigirEAN1789() throws Exception {
        
        Conexao.begin();
        try {        
                        
            List<CodigoBarrasAlteradoVO> codigoBarras = new ArrayList<>();
            //Preencher a tabela
            
            ProgressBar.setStatus("Carregando dados...Correção do EAN 1789.....");
            try (ResultSet rst = Conexao.createStatement().executeQuery(
                    "select * from implantacao.codigodebarrasalterado pa where cast(pa.codigobarras_anterior as varchar) like '1789%'"
            )) {
                while (rst.next()) {
                    CodigoBarrasAlteradoVO vo = new CodigoBarrasAlteradoVO();
                    vo.id_produto = rst.getInt("id_produto");
                    vo.codigobarras_anterior = rst.getLong("codigobarras_anterior");                    
                    vo.codigobarras_atual = rst.getLong("codigobarras_atual");
                    vo.id_tipoembalagem = rst.getInt("id_tipoembalagem");
                    vo.qtd_embalagem = rst.getInt("qtd_embalagem");
                    codigoBarras.add(vo);
                }
            }
            
            
            
            ProgressBar.setStatus("Atualizando dados...Correção do EAN 1789...");
            ProgressBar.setMaximum(codigoBarras.size());
            
            for (CodigoBarrasAlteradoVO vo: codigoBarras) {
                
                long codigoBarrasNovo = 0;
                
                if (vo.id_produto >= 10000) {
                    codigoBarrasNovo = Utils.gerarEan13(vo.id_produto, true);
                } else if (vo.id_tipoembalagem == 4 || vo.id_produto < 10000) {
                    codigoBarrasNovo = Utils.gerarEan13(vo.id_produto, false);
                } else {
                    codigoBarrasNovo = Utils.gerarEan13(vo.id_produto, true);
                }
                
                boolean jaExiste = false;
                try (ResultSet rs = Conexao.createStatement().executeQuery(
                        "select * from produtoautomacao where codigobarras = " + codigoBarrasNovo + " and id_produto = " + vo.id_produto
                )) {
                    jaExiste = rs.next();
                }
                
                if (!jaExiste) {
                    //atualizo a tabela com o código anterior, mudando o codigo atual
                    Conexao.createStatement().execute(
                            "update implantacao.codigodebarrasalterado \n" +
                            "set codigobarras_atual = " + codigoBarrasNovo + " \n" +
                            "where id_produto = " + vo.id_produto + 
                            " and codigobarras_anterior = " + vo.codigobarras_anterior
                    );  
                    //atualizo o produtoautomacao com o novo código
                    Conexao.createStatement().execute(
                            "update produtoautomacao \n" +
                            "set codigobarras = " + codigoBarrasNovo + " \n" +
                            "where id_produto = " + vo.id_produto + 
                            " and codigobarras = " + vo.codigobarras_anterior
                    );
                }
                
                ProgressBar.next(); 
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    public void importarCodigoAnteriorTributacao(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Integer, ProdutoVO> produtos = carregarProdutoDaBalanca(idLojaVR, idLojaCliente, "c:\\Prodbalanca.txt");
        produtos.putAll(carregarProduto(idLojaVR, idLojaCliente));
        new ProdutoDAO().alterarCodigoAnterior(produtos.values());
    }
    
    private class CodigoBarrasAlteradoVO {
        int id_produto = 0;
        long codigobarras_anterior = 0;
        long codigobarras_atual = 0;
        int qtd_embalagem = 1;
        int id_tipoembalagem = 0;
    }

    public void incluirDesconto3Porc(int idLojaVr) throws Exception {
        int contagem = 0;        
        
        try (ResultSet rst = Conexao.createStatement().executeQuery("select count(*) ct from implantacao.codigodebarrasalterado pa where cast(pa.codigobarras_anterior as varchar) like '1789%'")) {
            if (rst.next()) {
                contagem = rst.getInt("ct");
            }
        }
        
        ProgressBar.setStatus("Atualizando dados...Incluir desconto de 3% no atacado...");
        ProgressBar.setMaximum(contagem);
        
        Conexao.begin();
        try {
            try (ResultSet rst = Conexao.createStatement().executeQuery(
                    "select * from implantacao.codigodebarrasalterado pa where cast(pa.codigobarras_anterior as varchar) like '1789%'"
            )) {
                while (rst.next()) {            
                    long codigoBarrasAtual = 0;

                    codigoBarrasAtual = rst.getLong("codigobarras_atual");
                    //Verifico se o registro do desconto já existe
                    try (ResultSet rst2 = Conexao.createStatement().executeQuery(
                            "select * from produtoautomacaodesconto where codigobarras = " + codigoBarrasAtual + " and id_loja = " + idLojaVr 
                    )) {
                        //Se encontrar da update
                        if (rst2.next()) { 
                            Conexao.createStatement().execute("update produtoautomacaodesconto set desconto = 3 where codigobarras = " + codigoBarrasAtual + " and id_loja = " + idLojaVr);
                        } else {
                            Conexao.createStatement().execute("insert into produtoautomacaodesconto (codigobarras, id_loja, desconto) values (" + codigoBarrasAtual + "," + idLojaVr + ",3);");
                        }
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
    
    private class CodigoBarrasConcretize {
        public Long codbarras;
        public Long codProduto;

        public CodigoBarrasConcretize() {
        }

        public CodigoBarrasConcretize(Long codbarras, Long codProduto) {
            this.codbarras = codbarras;
            this.codProduto = codProduto;
        }
    }
    
    private Map<Long, ProdutoBalancaVO> carregarProdutosDeBalanca(int idLojaCliente) throws Exception {
        Map<Long, CodigoBarrasConcretize> codBarras = getCodigoDeBarras(idLojaCliente);
        Map<Long, ProdutoBalancaVO> result = new LinkedHashMap<>();        
        for (ProdutoBalancaVO vo: new ProdutoBalancaDAO().carregarProdutosBalanca().values()) {
            if (codBarras.containsKey((long) vo.codigo)) {
                result.put(codBarras.get((long) vo.codigo).codProduto, vo);
            }
        }
        
        return result;
    }

    private Map<Long, CodigoBarrasConcretize> getCodigoDeBarras(int idLojaCliente) throws Exception {
        Map<Long, CodigoBarrasConcretize> codBarras = new LinkedHashMap<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            String sql = "select distinct codbarra, codprod from embalagem where not codbarra is null group by codbarra, codprod";
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    codBarras.put(Long.parseLong(rst.getString("codbarra")), new CodigoBarrasConcretize(Long.parseLong(rst.getString("codbarra")), rst.getLong("codprod")));
                }
            }
        }
        return codBarras;
    }

    private int getIdAliquota(int cst, float aliq) {
        switch (cst) {
            case 90: return 8;
            case 60: return 7;
            case 0: {
                if (aliq == 17) {
                    return 18;
                } else if (aliq == 12) {
                    return 1;
                }
            }
            case 40: return 6;
            default: return 8;
        }        
    }
    
    @Override
    public Map<Integer, ProdutoVO> carregarProduto(int idLojaVR, int idLojaCliente) throws Exception { 
        
        /*
        Obter uma lista dos códigos de barras cadastrados no sistema do cliente
        Separados por codigo do cliente Map<CodProd, List<CodiBarras>>
        */
        
        Map<Integer, ProdutoVO> produtos = new LinkedHashMap<>();
        try (Statement stm = ConexaoOracle.createStatement()) {
            String sql = 
                "select\n" +
                "    p.codprod,\n" +
                "    p.descricao as desc_completa,\n" +
                "    p.abreviacao as desc_reduzida,\n" +
                "    p.descricao as desc_gondola,\n" +
                "    p.unidade,\n" +
                "    p.qtunitcx as qtdEmbalagem,\n" +
                "    ne.cstpis as cstPisEntr,\n" +
                "    ns.cstpis as cstPisSaid,\n" +
                "    ns.codnatpis as naturezaReceita,\n" +
                "    --verifica pesavel\n" +
                //TODO Efetuar um tratamento no mercadologico, pois o mesmo permite receber string neste sistema
                "    --coalesce(cast(p.codepto as integer),-1) as mercadologico1,\n" +
                "    --coalesce(cast(p.codsec as integer),-1) as mercadologico2,\n" +
                "    --coalesce(cast(p.codcat as integer),-1) as mercadologico3,\n" +
                "    cast(p.cod_ncm as integer) as ncm,\n" +
                "    p.prazovalid as validade,\n" +
                "    p.margem,\n" +
                "    p.pesobruto,\n" +
                "    p.pesoliq,\n" +
                "    p.dtcadastro,\n" +
                "    case when p.dtexclusao is null then 1 else 0 end as excluido,\n" +
                "    case p.tipo\n" +
                "    when 'VN' then 1\n" +
                "    else 0 end as situacaoCadastro,\n" +
                "    0/*pc.ptabela*/ as precovenda,\n" +
                "    est.custoreal as custo_imposto,\n" +
                "    coalesce(est.custocont, est.custoreal) as custo_sem_imposto,\n" +
                "    fl.codestado as id_uf,\n" +
                "    fl.estado as sigla_uf,\n" +
                "    trib.aliqicms,\n" +
                "    trib.sittribut,\n" +
                "    trib.obstribut,\n" +
                "    (select count(*) from embalagem where embalagem.codprod = p.codprod and embalagem.codfilial = fl.codfilial) as qtdBarra    \n" +
                "--select count(*)\n" +
                "from \n" +
                "    produto p\n" +
                "    left join cadncm ne on p.cod_ncm = ne.cod_ncm and ne.operacao = 'E' and ne.regtribut = 3\n" +
                "    left join cadncm ns on p.cod_ncm = ns.cod_ncm and ns.operacao = 'S' and ns.regtribut = 3\n" +
                "    left join preco pc on pc.codprod = p.codprod and pc.numregiao = 1\n" +
                "    left join filial fl on fl.codfilial = " + idLojaCliente + "\n" +
                "    left join estoque est on est.codprod = p.codprod and est.codfilial = fl.codfilial\n" +
                "    left join tributacao trib on trib.codtribut = pc.codtribut";  
            
            //Carrega uma listagem com os produtos de balança
            Map<Long, ProdutoBalancaVO> produtoBalancaPorId = carregarProdutosDeBalanca(idLojaCliente);
            boolean notificado = false;
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    ProdutoVO oProduto = new ProdutoVO();
                    
                    int idProduto = rst.getInt("codprod");
                    
                    oProduto.setId(idProduto);
                    oProduto.setDescricaoCompleta(rst.getString("desc_completa"));
                    oProduto.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    oProduto.setDescricaoGondola(rst.getString("desc_gondola"));   
                    oProduto.setQtdEmbalagem(rst.getInt("qtdEmbalagem"));
                    
                    ProdutoBalancaVO prodBal = null;
                    
                    try (Statement stm2 = ConexaoOracle.createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery("select codbarra, codprod from embalagem where codfilial = " + idLojaCliente + " and codprod = " + idProduto)) {
                            while (rst2.next()) {
                               if (prodBal == null) {
                                    prodBal = new ProdutoBalancaDAO().localizar(Long.parseLong(rst2.getString("codbarra")));
                               }
                            }
                        }
                    }
                    
                    int validade = rst.getInt("validade");
                    
                    /*if (prodBal != null) { 
                        validade = prodBal.getValidade() > 0 ? prodBal.getValidade() : rst.getInt("validade");
                        if (!notificado) {
                            notificado = true;
                            Util.exibirMensagem("Achou id: " + idProduto, "titulo");
                        }
                        oProduto.eBalanca = true;
                        oProduto.codigoBalanca = prodBal.getCodigo();
                        oProduto.validade = validade;
                        
                        if (null != prodBal.getPesavel().trim()) {
                            switch (prodBal.getPesavel().trim()) {
                                case "U":
                                    oProduto.pesavel = true;
                                    oProduto.idTipoEmbalagem = 0;
                                    break;
                                case "P":
                                    oProduto.pesavel = false;
                                    oProduto.idTipoEmbalagem = 4;
                                    break;
                            }
                        }
                        
                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = Long.parseLong(String.valueOf(idProduto));
                        oAutomacao.qtdEmbalagem = oProduto.getQtdEmbalagem();
                        oAutomacao.idTipoEmbalagem = oProduto.getIdTipoEmbalagem();
                        oProduto.vAutomacao.add(oAutomacao);
                    } else {*/
                        oProduto.codigoBalanca = -1;
                        oProduto.eBalanca = false;
                        oProduto.pesavel = false;
                        oProduto.validade = rst.getInt("validade");

                        String unidade = Utils.acertarTexto(rst.getString("unidade")).toUpperCase();
                         
                        switch (unidade) {
                            case "KG":
                                oProduto.idTipoEmbalagem = 4;
                                break;
                            case "UN":
                                oProduto.idTipoEmbalagem = 0;
                                break;
                            default:
                                oProduto.idTipoEmbalagem = 0;
                                break;
                        }
                        
                        /*ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();
                        oAutomacao.codigoBarras = -2;
                        oAutomacao.qtdEmbalagem = oProduto.getQtdEmbalagem();
                        oAutomacao.idTipoEmbalagem = oProduto.getIdTipoEmbalagem();
                        oProduto.vAutomacao.add(oAutomacao);*/
                    //}
                    
                    //oProduto.idTipoEmbalagem = idTipoEmbalagem;
                    oProduto.idTipoPisCofinsDebito = Utils.retornarPisCofinsDebito(rst.getInt("cstPisSaid"));
                    oProduto.idTipoPisCofinsCredito = Utils.retornarPisCofinsCredito(rst.getInt("cstPisEntr"));
                    oProduto.tipoNaturezaReceita = Utils.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("naturezaReceita"));
                    //oProduto.pesavel = pesavel;
                    oProduto.mercadologico1 = 14;
                    oProduto.mercadologico2 = 1;
                    oProduto.mercadologico3 = 1;
                    
                    String ncmAtual = null;
                    int ncm1, ncm2, ncm3;
                    
                    if ((rst.getString("ncm") != null)
                        && (!rst.getString("ncm").trim().isEmpty())
                        && (rst.getString("ncm").trim().length() > 5)) {

                        ncmAtual = Utils.formataNumero(rst.getString("ncm").trim());
                        if ((ncmAtual != null)
                                && (!ncmAtual.isEmpty())
                                && (ncmAtual.length() > 5)) {
                            try {
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
                    }else {
                        ncm1 = 402;
                        ncm2 = 99;
                        ncm3 = 0;
                    }
                    
                    
                    oProduto.setNcm1(ncm1);
                    oProduto.setNcm2(ncm2);
                    oProduto.setNcm3(ncm3);
                    oProduto.setIdFamiliaProduto(-1);
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
                    oProduto.setMargem(rst.getDouble("margem"));
                    oProduto.setPesoBruto(rst.getDouble("pesobruto"));
                    oProduto.setPesoLiquido(rst.getDouble("pesoliq"));
                    
                    String dataCadastro;
                    if ((rst.getString("dtcadastro") != null)
                        && (!rst.getString("dtcadastro").trim().isEmpty())) {
                        dataCadastro = rst.getString("dtcadastro").substring(0, 10).replace("-", "/");
                    } else {
                        dataCadastro = "";
                    }                    
                    oProduto.dataCadastro = dataCadastro;
                    
                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.setIdSituacaoCadastro(rst.getInt("excluido"));
                    oComplemento.setPrecoVenda(rst.getDouble("precovenda"));
                    oComplemento.setPrecoDiaSeguinte(rst.getDouble("precovenda"));
                    oComplemento.setCustoComImposto(rst.getDouble("custo_imposto"));
                    oComplemento.setCustoSemImposto(rst.getDouble("custo_sem_imposto"));
                    oProduto.vComplemento.add(oComplemento);
                    
                    ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();
                    int idAliquota = getIdAliquota(rst.getInt("sittribut"), rst.getFloat("aliqicms"));                    
                    oAliquota.setIdEstado(rst.getInt("id_uf"));
                    oAliquota.setIdAliquotaDebito(idAliquota);
                    oAliquota.setIdAliquotaCredito(idAliquota);
                    oAliquota.setIdAliquotaDebitoForaEstado(idAliquota);
                    oAliquota.setIdAliquotaCreditoForaEstado(idAliquota);
                    oAliquota.setIdAliquotaDebitoForaEstadoNF(idAliquota);
                    oProduto.vAliquota.add(oAliquota);                    
                    
                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                    oCodigoAnterior.codigoanterior = idProduto;
                    if (prodBal != null) {
                        oCodigoAnterior.barras = idProduto;
                    } else {
                        oCodigoAnterior.barras = -1;
                    }
                    
                    if ((rst.getString("cstPisSaid") != null)
                            && (!rst.getString("cstPisSaid").trim().isEmpty())) {
                        oCodigoAnterior.piscofinsdebito = Integer.parseInt(rst.getString("cstPisSaid").trim());
                    } else {
                        oCodigoAnterior.piscofinsdebito = -1;
                    }

                    if ((rst.getString("cstPisEntr") != null)
                            && (!rst.getString("cstPisEntr").trim().isEmpty())) {
                        oCodigoAnterior.piscofinscredito = Integer.parseInt(rst.getString("cstPisEntr").trim());
                    } else {
                        oCodigoAnterior.piscofinscredito = -1;
                    }

                    if ((rst.getString("naturezaReceita") != null)
                            && (!rst.getString("naturezaReceita").trim().isEmpty())) {
                        oCodigoAnterior.naturezareceita = Integer.parseInt(rst.getString("naturezaReceita").trim());
                    } else {
                        oCodigoAnterior.naturezareceita = -1;
                    }
                    
                    if ((rst.getString("sittribut") != null) && (!rst.getString("sittribut").trim().isEmpty())) {
                        oCodigoAnterior.ref_icmsdebito = rst.getString("sittribut").trim().replace(".", "");
                    } else {
                        oCodigoAnterior.ref_icmsdebito = "";
                    }

                    oCodigoAnterior.estoque = -1;
                    oCodigoAnterior.e_balanca = oProduto.eBalanca;
                    oCodigoAnterior.codigobalanca = oProduto.codigoBalanca;
                    oCodigoAnterior.custosemimposto = -1;
                    oCodigoAnterior.custocomimposto = -1;
                    oCodigoAnterior.margem = -1;
                    oCodigoAnterior.precovenda = -1;
                    oCodigoAnterior.referencia = -1;
                    oCodigoAnterior.setNcm(rst.getString("ncm") != null ? rst.getString("ncm").trim() : "");
                    
                    oProduto.vCodigoAnterior.add(oCodigoAnterior);

                    produtos.put(idProduto, oProduto);
                }
            }   
        }
        return produtos;
    }

    @Override
    public Map<Integer, ProdutoVO> carregarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {

        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double precoVenda = 0;

        try (Statement st = ConexaoOracle.createStatement()) {
            try (ResultSet rst = st.executeQuery(
                    "select p.codprod, e.ptabela, e.margem\n" +
                    "from produto p\n" +
                    "join embalagem e on p.codbarra = e.codbarra and e.codfilial = " + idLojaCliente + "\n" +
                    "order by\n" +
                    "    p.codprod"
            )) {
                while (rst.next()) {
                    idProduto = rst.getInt("codprod");

                    if ((rst.getString("ptabela") != null)
                            && (!"".equals(rst.getString("ptabela")))) {
                        precoVenda = rst.getDouble("ptabela");
                    } else {
                        precoVenda = 0;
                    }


                    ProdutoVO oProduto = new ProdutoVO();
                    oProduto.id = idProduto;
                    if ((rst.getString("margem") != null)
                            && (!"".equals(rst.getString("margem")))) {
                        oProduto.margem = rst.getDouble("margem");
                    }

                    ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                    oComplemento.idLoja = idLojaVR;
                    oComplemento.precoVenda = precoVenda;
                    oComplemento.precoDiaSeguinte = precoVenda;
                    oProduto.vComplemento.add(oComplemento);


                    CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();
                    oCodigoAnterior.precovenda = precoVenda;
                    oCodigoAnterior.id_loja = idLojaVR;
                    oCodigoAnterior.margem = oProduto.margem;
                    oProduto.vCodigoAnterior.add(oCodigoAnterior);


                    vProduto.put(idProduto, oProduto);
                }
            }
            
            return vProduto;
        }
    }

    @Override
    public Map<Integer, ProdutoVO> carregarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custoSemImposto = 0, custo = 0;

        try {

            stm = ConexaoOracle.createStatement();
            rst = stm.executeQuery (
                "select\n" +
                "    p.codprod,\n" +
                "    est.custoreal as custo_imposto,\n" +
                "    coalesce(est.custocont, est.custoreal) as custo_sem_imposto\n" +
                "from \n" +
                "    produto p\n" +
                "    join filial fl on fl.codfilial =  " + idLojaCliente + "\n" +
                "    left join estoque est on est.codprod = p.codprod and est.codfilial = fl.codfilial"
            );

            while (rst.next()) {
                idProduto = rst.getInt("codprod");

                if ((rst.getString("custo_imposto") != null)
                        && (!"".equals(rst.getString("custo_imposto")))) {
                    custo = rst.getDouble("custo_imposto");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("custo_sem_imposto") != null)
                        && (!"".equals(rst.getString("custo_sem_imposto")))) {
                    custoSemImposto = rst.getDouble("custo_sem_imposto");
                } else {
                    custoSemImposto = 0;
                }
                
                if (custo==0){
                    if (custoSemImposto>0){
                        custo=custoSemImposto;
                    }
                }
                if (custoSemImposto==0){
                    if (custo>0){
                        custoSemImposto=custo;
                    }                    
                }                

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.setId(idProduto);

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.setIdLoja(idLojaVR); 
                
                oComplemento.setCustoComImposto(custo);
                oComplemento.setCustoSemImposto(custoSemImposto);

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.setCustocomimposto(custo);
                oCodigoAnterior.setCustosemimposto(custo);
                oCodigoAnterior.setId_loja(idLojaVR);

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }

    @Override
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        double idProduto;
        long codigobarras;

        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select emb.codprod, emb.codbarra, emb.ptabela, emb.codfilial, p.codbarra codbarra_produto\n" +
                    "from embalagem emb\n" +
                    "join produto p on emb.codprod = p.codprod\n" +
                    "group by emb.codprod, emb.codbarra, emb.ptabela, emb.codfilial, p.codbarra order by emb.codprod"
                )){
                
                Conexao.begin();
                
                try {
                
                    Conexao.prepareStatement(
                            "create table if not exists implantacao.codibarraanterior (\n" +
                            "	codbarra_antigo numeric(20,0) not null,\n" +
                            "	codbarra_preco numeric(10,2) default 0,\n" +
                            "	codprod_antigo integer,\n" +
                            "   principal boolean,\n" +
                            "	loja_id integer\n" +
                            ")"
                    ).execute();

                    while (rst.next()) {

                        idProduto = Double.parseDouble(rst.getString("codprod"));

                        if ((rst.getString("codbarra") != null)
                                && (!rst.getString("codbarra").trim().isEmpty())) {
                            codigobarras = Long.parseLong(Utils.formataNumero(rst.getString("codbarra")));
                        } else {
                            codigobarras = -1;
                        }
                        
                        String cod = rst.getString("codbarra") != null ? rst.getString("codbarra") : "";
                        String codProduto = rst.getString("codbarra_produto") != null ? rst.getString("codbarra_produto") : "";
                        boolean principal = cod.equals(codProduto);
                        
                        Conexao.prepareStatement(
                                "insert into implantacao.codibarraanterior (\n" +
                                "        codbarra_antigo,\n" +
                                "        codbarra_preco,\n" +
                                "        codprod_antigo,\n" +
                                "        principal,\n" +
                                "        loja_id\n" +
                                ") values (\n" +
                                "        " + cod + ",\n" +
                                "        " + rst.getDouble("ptabela") + ",\n" +
                                "        " + String.valueOf(idProduto) + ",\n" +                                
                                "        " + principal + ",\n" +                                
                                "        " + Integer.parseInt(rst.getString("codfilial")) + "\n" +
                                ");"
                        ).execute();

                        if (String.valueOf(codigobarras).length() >= 7) {

                            if (String.valueOf(codigobarras).length() > 14) {
                                codigobarras = Long.parseLong(String.valueOf(codigobarras).substring(0, 14));
                            }

                            ProdutoVO oProduto = new ProdutoVO();

                            oProduto.idDouble = idProduto;

                            ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                            oAutomacao.codigoBarras = codigobarras;
                            oAutomacao.qtdEmbalagem = -1;                        
                            oProduto.vAutomacao.add(oAutomacao);

                            vProduto.put(codigobarras, oProduto);
                        }
                    }
                
                    Conexao.commit();
                } catch (Exception e) {
                    Conexao.rollback();
                    throw e;
                }
            }
        }

        return vProduto;        
    }

    @Override
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double estoque = 0;

        try {

            stm = ConexaoOracle.createStatement();
            rst = stm.executeQuery(
                "select codprod, qtest from estoque where codfilial = " + idLojaCliente
            );
                    
            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("codprod"));
                estoque = rst.getDouble("qtest");

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                oComplemento.idLoja = idLojaVR;
                oComplemento.estoque = estoque;
                oComplemento.estoqueMinimo = 0;
                oComplemento.estoqueMaximo = 0;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.estoque = estoque;
                oCodigoAnterior.setId_loja(idLojaVR);

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public void importarAtacado(int idLojaVR, int idLojaCliente) throws Exception {
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Atacado.....");
        List<ProdutoAutomacaoLojaVO> vProdutos = carregarAtacado(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vProdutos.size());

        produto.gravarAtacado(vProdutos, idLojaVR, vLoja);
    }
    
    public List<ProdutoAutomacaoLojaVO> carregarAtacado(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoAutomacaoLojaVO> produtos = new ArrayList<>();
                
        try (Statement stm = ConexaoOracle.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.codprod,\n" +
                    "    e.qtunit,\n" +
                    "    e.codbarra as barra_embalagem,\n" +
                    "    p.codbarra as barra_produto,\n" +
                    "    coalesce((select ptabela from embalagem em where em.codfilial = " + idLojaCliente + " and em.codprod = p.codprod and em.codbarra = p.codbarra),0) as preco_principal,\n" +
                    "    coalesce(e.ptabela,0) as preco_embalagem\n" +
                    "from\n" +
                    "    embalagem e\n" +
                    "    join produto p on\n" +
                    "        p.codprod = e.codprod and\n" +
                    "        e.codfilial = " + idLojaCliente                                       
            )) {
            
                while (rst.next()) {
                    
                    
                    String barraProduto = rst.getString("barra_produto");
                    barraProduto = barraProduto != null ? barraProduto.trim() : "";
                    String barraEmbalagem = rst.getString("barra_embalagem");
                    barraEmbalagem = barraEmbalagem != null ? barraEmbalagem.trim() : "";

                    if (!barraProduto.equals(barraEmbalagem)) {
                        if (rst.getDouble("preco_principal") != rst.getDouble("preco_embalagem")) {
                            ProdutoAutomacaoLojaVO vo = new ProdutoAutomacaoLojaVO();
                    
                            vo.id_produto = rst.getInt("codprod");
                            vo.id_loja = idLojaVR;
                            vo.codigobarras = Long.parseLong(barraEmbalagem);
                            vo.precovenda = rst.getDouble("preco_embalagem");
                            vo.gravarAutomacao = true;
                            if (barraEmbalagem.startsWith("1789")) {
                                vo.qtdEmbalagem = 5;
                            } else {
                                vo.qtdEmbalagem = rst.getInt("qtunit");
                            }
                            /*
                            if (vo.id_produto == 7482) {
                                Util.exibirMensagem(vo.toString(),"");
                            }
                            */
                            if (vo.qtdEmbalagem < 0)
                                vo.qtdEmbalagem = 1;
                            
                            produtos.add(vo);                            
                        } else {
                            ProdutoAutomacaoLojaVO vo = new ProdutoAutomacaoLojaVO();
                    
                            vo.id_produto = rst.getInt("codprod");
                            vo.id_loja = idLojaVR;
                            vo.codigobarras = Long.parseLong(barraEmbalagem);
                            vo.precovenda = rst.getDouble("preco_embalagem");
                            vo.gravarAutomacao = false;
                            if (barraEmbalagem.startsWith("1789")) {
                                vo.qtdEmbalagem = 5;
                            } else {
                                vo.qtdEmbalagem = rst.getInt("qtunit");
                            }
                            /*
                            if (vo.id_produto == 7482) {
                                Util.exibirMensagem(vo.toString(),"");
                            }
                            */
                            if (vo.qtdEmbalagem < 0)
                                vo.qtdEmbalagem = 1;
                            
                            produtos.add(vo);   
                        }
                    } else {
                        ProdutoAutomacaoLojaVO vo = new ProdutoAutomacaoLojaVO();
                    
                        vo.id_produto = rst.getInt("codprod");
                        vo.id_loja = idLojaVR;
                        vo.gravarAutomacao = false;
                        vo.codigobarras = Long.parseLong(barraEmbalagem);
                        vo.precovenda = rst.getDouble("preco_embalagem");
                        if (barraEmbalagem.startsWith("1789")) {
                            vo.qtdEmbalagem = 5;
                        } else {
                            vo.qtdEmbalagem = rst.getInt("qtunit");
                        }
                        /*
                        if (vo.id_produto == 7482) {
                            Util.exibirMensagem(vo.toString(),"");
                        }
                        */
                        if (vo.qtdEmbalagem < 0)
                            vo.qtdEmbalagem = 1;
                        
                        produtos.add(vo);   
                    }                
                }
                
            }
        }
        
        return produtos;        
    }

    
    
}
