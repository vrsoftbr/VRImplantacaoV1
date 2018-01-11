package vrimplantacao.dao.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ClientePreferencialDAO;
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
import vrimplantacao.gui.interfaces.classes.LojaClienteVO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.vrimplantacao.ClientePreferencialVO;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
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

// TODO Criar uma classe para ser heradada, que contenha a base do DAO das interfaces

/**
 * Classe dao para obter dados do sistema UltraSyst
 * @author Leandro
 */
public class UltraSystDAO {
    
    public List<LojaClienteVO> getLojas() throws SQLException {
        List<LojaClienteVO> lojas = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cast(LOC_CODI as integer) codigo, LOC_NOME \n" +
                    "from TBLOCALIZACAO \n" +
                    "order by cast(LOC_CODI as integer)"
            )) {
                while (rst.next()) {
                    lojas.add(new LojaClienteVO(rst.getInt("codigo"), rst.getString("LOC_NOME")));
                }
            }
        }
        
        return lojas;
    }
    
    public void importarFornecedor() throws Exception {
        ProgressBar.setStatus("Carregando dados...Fornecedor...");
        List<FornecedorVO> vFornecedor = carregarFornecedor();

        new FornecedorDAO().salvar(vFornecedor);            
    }
    
    public void importarClientePreferencial(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Cliente Preferencial...");
        List<ClientePreferencialVO> vClientePreferencial = carregarCliente(idLojaCliente);
        new PlanoDAO().salvar(idLojaVR);
        
        new ClientePreferencialDAO().salvar(vClientePreferencial, idLojaVR, idLojaCliente);           
    }
    
    public void importarReceberCheque(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Cheque...");
        List<ReceberChequeVO> vReceberCheque = carregarChequeReceber(idLojaCliente);

        new ReceberChequeDAO().salvar(vReceberCheque, idLojaVR);
    }
    
    public void importarReceberCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {
        ProgressBar.setStatus("Carregando dados...Receber Credito Rotativo...");
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = carregarCreditoRotativo(idLojaVR, idLojaCliente);

        new ReceberCreditoRotativoDAO().salvar(vReceberCreditoRotativo, idLojaVR);
    }
    
    public void importarMercadologico() throws Exception {

        List<MercadologicoVO> vMercadologico;

        try {

            ProgressBar.setStatus("Carregando dados...Mercadologico...");
            MercadologicoDAO dao = new MercadologicoDAO();
            
            vMercadologico = carregarMercadologico(1);
            dao.salvar(vMercadologico, true);

            vMercadologico = carregarMercadologico(2);
            dao.salvar(vMercadologico, false);

            vMercadologico = carregarMercadologico(3);
            dao.salvar(vMercadologico, false);
            
            vMercadologico = carregarMercadologico(4);
            dao.salvar(vMercadologico, false);
            
            dao.temNivel4 = true;
            dao.salvarMax();

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        ProgressBar.setStatus("Carregando dados...Produtos.....");
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

        produto.salvar(vProdutoNovo, idLojaVR, vLoja);
    }
    
    public void importarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Preço...");
            Map<Integer, ProdutoVO> vPrecoProduto = carregarPrecoProduto(idLojaVR, idLojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vPrecoProduto.size());

            for (Integer keyId : vPrecoProduto.keySet()) {

                ProdutoVO oProduto = vPrecoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            //produto.alterarPrecoProduto(vProdutoNovo, id_loja);
            produto.alterarPrecoProduto(vProdutoNovo, idLojaVR);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Custo...");
            Map<Integer, ProdutoVO> vCustoProduto = carregarCustoProduto(idLojaVR, idLojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vCustoProduto.size());

            for (Integer keyId : vCustoProduto.keySet()) {

                ProdutoVO oProduto = vCustoProduto.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarCustoProduto(vProdutoNovo, idLojaVR);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Código de Barra...");
            Map<Long, ProdutoVO> vEanProdutoMilenio = carregarEanProduto(idLojaVR, idLojaCliente);

            ProgressBar.setMaximum(vEanProdutoMilenio.size());

            for (Long keyId : vEanProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEanProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.addCodigoBarras(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    public void importarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Estoque...");
            Map<Integer, ProdutoVO> vEstoqueProdutoMilenio = carregarEstoqueProduto(idLojaVR, idLojaCliente);

            List<LojaVO> vLoja = new LojaDAO().carregar();

            ProgressBar.setMaximum(vEstoqueProdutoMilenio.size());

            for (Integer keyId : vEstoqueProdutoMilenio.keySet()) {

                ProdutoVO oProduto = vEstoqueProdutoMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarEstoqueProduto(vProdutoNovo, idLojaVR);

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
    
    public void importarPisCofinsNaturezaReceita(int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();

        try {

            ProgressBar.setStatus("Carregando dados...Produtos...Pis Cofins, Natureza Receita...");
            Map<Integer, ProdutoVO> vPisCofinsNaturezaReceitaMilenio = carregarPisCofins(idLojaCliente);

            ProgressBar.setMaximum(vPisCofinsNaturezaReceitaMilenio.size());

            for (Integer keyId : vPisCofinsNaturezaReceitaMilenio.keySet()) {

                ProdutoVO oProduto = vPisCofinsNaturezaReceitaMilenio.get(keyId);

                vProdutoNovo.add(oProduto);

                ProgressBar.next();
            }

            produto.alterarPisCofinsNaturezaReceitaMilenio(vProdutoNovo);

        } catch (Exception ex) {

            throw ex;
        }
    }
    
    /**
     * Gera uma listagem com os fornecedores do cliente
     * @return Listagem de fornecedores
     */
    private List<FornecedorVO> carregarFornecedor() throws Exception {
        StringBuilder sql;
        Statement stm;
        ResultSet rst;
        List<FornecedorVO> vFornecedor = new ArrayList<>();

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            sql = new StringBuilder();
            sql.append(
                "select\n" +
                "	cast(f.FOR_CODI as integer) id_anterior,\n" +
                "	f.for_nome razaosocial,\n" +
                "	f.FOR_FANT nomefantasia,\n" +
                "	f.FOR_ENDE endereco,\n" +
                "	f.FOR_NUME numero,\n" +
                "	f.FOR_COMP complemento,\n" +
                "	f.FOR_BAIR bairro,\n" +
                "	cast((uf.UND_CDGIEF + '' + f.MUN_CODI) as integer) id_municipio,\n" +
                "	cast(F.CEP_CODI as integer) cep,\n" +
                "	UF.UND_CDGIEF id_estado,\n" +
                "	f.FOR_FONR telefone,\n" +
                "	CASE F.FOR_TPPESSOA WHEN 'F' THEN 1 ELSE 0 END AS id_tipoinscricao,\n" +
                "	f.FOR_INSC inscricaoestadual, --eliminar pontuacao\n" +
                "	f.FOR_CNPJ cnpj, --colocar -1 se o cpf/cnpj for inválido\n" +
                "	CASE f.FOR_SITU WHEN 'I' THEN 0 ELSE 1 END AS id_situacaocadastro,\n" +
                "	f.FOR_FONC telefone2_ou_celular,\n" +
                "	f.FOR_FONF fax,\n" +
                "	f.FOR_EMAI email,\n" +
                "    --Campos de observações\n" +
                "	f.FOR_CONT nome_contato\n" +
                "from\n" +
                "	gestor.dbo.FORNECEDOR f\n" +
                "	left join gestor.dbo.TBMUNICIPIOS m on\n" +
                "		f.MUN_CODI = m.MUN_CODI and\n" +
                "		f.UND_CDNAC = m.UND_CDNAC\n" +
                "	left join Gestor.dbo.tbuf uf on\n" +
                "		m.UND_CDNAC = uf.UND_CDNAC\n" +
                "order by\n" +
                "	f.FOR_CODI");

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {

                FornecedorVO forn = new FornecedorVO();
                
                forn.setCodigoanterior(rst.getInt("id_anterior"));
                forn.setRazaosocial(rst.getString("razaosocial"));
                forn.setNomefantasia(rst.getString("nomefantasia"));
                forn.setEndereco(rst.getString("endereco"));
                forn.setNumero(rst.getString("numero"));
                forn.setComplemento(rst.getString("complemento"));
                forn.setBairro(rst.getString("bairro"));
                //Determina se o municipio esta cadastrado no sistema
                //int municipio = Utils.retornarMunicipioIBGECodigo(2304400);
                forn.setId_municipio(
                    Utils.existeMunicipioIBGECodigo(rst.getInt("id_municipio")) ?
                    rst.getInt("id_municipio") :
                    2304400
                );            
                forn.setCep(
                    rst.getInt("cep") != 0 ?
                    rst.getInt("cep") :
                    60040100
                );               
                forn.setId_estado(
                    rst.getInt("id_estado") != 0 ?
                    rst.getInt("id_estado") :
                    23
                );
                forn.setTelefone(rst.getString("telefone"));
                forn.setInscricaoestadual(rst.getString("inscricaoestadual"));
                forn.setCnpj(Utils.stringToLong(rst.getString("cnpj")));
                forn.setId_tipoinscricao(rst.getInt("id_tipoinscricao"));
                
                String tel = Utils.formataNumero(rst.getString("telefone2_ou_celular"));
                if (tel.length() < 11) {
                    forn.setTelefone2(tel);
                    forn.setCelular("");
                } else {
                    forn.setTelefone2("");
                    forn.setCelular(tel);                    
                }
                forn.setFax(rst.getString("fax"));
                forn.setEmail(rst.getString("email"));
                /*Como não há observações adicionais neste fornecedor, são criadas
                novas observações*/
                forn.setObservacao("Contato: " + rst.getString("nome_contato"));
                
                vFornecedor.add(forn);
            }

            return vFornecedor;

        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private List<ClientePreferencialVO> carregarCliente(int idLojaCliente) throws Exception {
        
        Statement stm;
        ResultSet rst;
        List<ClientePreferencialVO> vClientePreferencial = new ArrayList<>();

        Connection con = ConexaoSqlServer.getConexao();
        stm = con.createStatement();
        try {
            rst = stm.executeQuery(
                "select\n" +
                "    CAST(CLI_CODI AS INTEGER) id,\n" +
                "    c.CLI_NOME nome,\n" +
                "    c.cli_ende endereco,\n" +
                "    c.CLI_BAIR bairro,\n" +
                "    uf.UND_CDGIEF id_estado,\n" +
                "    uf.UND_CDGIEF + '' + mun.MUN_CODI id_municipio,\n" +
                "    c.CEP_CODI cep,\n" +
                "    c.CLI_FONR telefone,\n" +
                "    c.CLI_CPF cnpj,\n" +
                "    c.CLI_IE inscricaoestadual,\n" +
                "    c.CLI_IDEN identidade, --se for pessoa física utilizar este campo no ie\n" +
                "    case c.CLI_SEXO when 'M' then 1 else 0 end as sexo,\n" +
                "    c.CLI_PREF as observacao,\n" +
                "    0 as salario,\n" +
                "    isnull(c.CLI_LCRE,0) valorlimite,\n" +
                "    '' as nomeconjuge,\n" +
                "    c.CLI_NUME numero,\n" +
                "    c.CLI_COMP complemento,\n" +
                "    '' as orgaoemissor,\n" +
                "    case c.CLI_TPPESSOA when 'J' then 0 else 1 end as id_tipoinscricao,\n" +
                "    case c.CLI_SITU when 'A' then 0 else 1 end as bloqueado, --pode ser que este campo represente a situação do cliente c.CLI_SITC\n" +
                "    c.CLI_EMAI email,\n" +
                "    CAST(CLI_CODI AS INTEGER) codigoanterior,\n" +
                "    '' as codigoagente,\n" +
                "    c.CLI_DTCA datacadastro,\n" +
                "	c.CLI_FONC celular,\n" +
                "	c.CLI_FONT telefone2,\n" +
                "	c.CLI_CONT contato,\n" +
                "	c.CLI_DTNA datanascimento,\n" +
                "	c.CLI_DTAL dataatualizacaocadastro\n" +
                "from \n" +
                "    CLIENTE C\n" +
                "    left join TBUF uf on\n" +
                "        c.UND_CDNAC = uf.UND_CDNAC\n" +
                "    left join TBMUNICIPIOS mun on\n" +
                "        c.MUN_CODI = mun.MUN_CODI and\n" +
                "        c.und_cdnac = mun.und_cdnac\n" +
                /*"where\n" +
                "   c.LOC_CODI = " + idLojaCliente + "\n" +  */      
                "ORDER BY\n" +
                "    CAST(CLI_CODI AS INTEGER)"
            );
            
            while (rst.next()) {
                ClientePreferencialVO cli = new ClientePreferencialVO();
                
                cli.setId(rst.getInt("id"));
                cli.setCodigoanterior(rst.getInt("id"));
                
                cli.setNome(rst.getString("nome"));
                cli.setEndereco(rst.getString("endereco"));
                cli.setBairro(rst.getString("bairro"));
                cli.setId_estado(
                    rst.getInt("id_estado") != 0 ? //TODO colocar o método Utils.existeUfIBGECodigo
                    rst.getInt("id_estado") :
                    23
                );
                cli.setId_municipio(
                    Utils.existeMunicipioIBGECodigo(rst.getInt("id_municipio")) ?
                    rst.getInt("id_municipio") :
                    2304400        
                );
                Integer cep = Integer.parseInt(Utils.formataNumero(rst.getString("cep")));
                cli.setCep(
                    cep != 0 ?
                    cep :
                    60040100
                );                
                cli.setId_tipoinscricao(rst.getInt("id_tipoinscricao"));
                cli.setCnpj(Long.parseLong(Utils.formataNumero(rst.getString("cnpj"))));
                cli.setTelefone(rst.getString("telefone"));
                if (cli.getId_tipoinscricao() == 0) {
                    cli.setInscricaoestadual(rst.getString("inscricaoestadual"));
                } else {
                    cli.setInscricaoestadual(rst.getString("identidade"));
                }    
                cli.setSexo(rst.getInt("sexo"));
                cli.setObservacao(rst.getString("observacao"));
                cli.setSalario(rst.getFloat("salario"));
                cli.setValorlimite(rst.getFloat("valorlimite"));
                cli.setNomeconjuge(rst.getString("nomeconjuge"));
                cli.setNumero(rst.getString("numero"));
                cli.setComplemento(rst.getString("complemento"));
                cli.setOrgaoemissor(rst.getString("orgaoemissor"));
                cli.setBloqueado(rst.getInt("bloqueado") == 1);
                cli.setEmail(rst.getString("email"));
                cli.setCodigoanterior(rst.getInt("codigoanterior"));    
                cli.setTelefone2(rst.getString("telefone2"));
                cli.setCelular(rst.getString("celular"));
                if (rst.getDate("datacadastro") != null) {
                    cli.setDatacadastro(new SimpleDateFormat("yyyy/MM/dd").format(rst.getDate("datacadastro")));
                } else {
                    cli.setDatacadastro(new SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date()));
                }
                if (rst.getDate("dataatualizacaocadastro") != null) {
                    cli.setDataatualizacaocadastro(rst.getDate("dataatualizacaocadastro"));
                }
                
                String obsContato = Utils.acertarTexto(rst.getString("contato"));
                String obsDtNasc = Utils.acertarTexto(rst.getString("datanascimento"));
                StringBuilder str = new StringBuilder();
                
                if (!obsContato.isEmpty()) {
                    str.append("CONTATO ").append(obsContato);
                }
                if (!obsDtNasc.isEmpty()) {
                    str.append("DT. NASCIMENTO ").append(obsDtNasc);
                }
                
                cli.setObservacao2(str.toString());
                vClientePreferencial.add(cli);
            }
        } finally {
            stm.close();
            con.close();
        }
        
        return vClientePreferencial;
        
    }

    public void importarProdutoBalanca(String arquivo, int opcao) throws Exception {
        ProgressBar.setStatus("Carregando dados...Produtos de Balanca...");
        List<ProdutoBalancaVO> vProdutoBalanca = new ProdutoBalancaDAO().carregar(arquivo, opcao);

        new ProdutoBalancaDAO().salvar(vProdutoBalanca);
    }
    
    public List<ReceberChequeVO> carregarChequeReceber(int idLojaCliente) throws Exception {
        Statement stm;
        ResultSet rst;
        List<ReceberChequeVO> vReceberCheque = new ArrayList<>();

        int numerocupom, idBanco, cheque, idTipoInscricao;
        double valor, juros;
        long cpfCnpj;
        String observacao, dataemissao, datavencimento,
                agencia, conta, nome, rg, telefone, cmc7;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            rst = stm.executeQuery(
                "select\n" +
                "	cast(c.LOC_CODI as integer) id_loja,\n" +
                "	c.CHQ_EMIS data,\n" +
                "	c.CHQ_VENC datadeposito,\n" +
                "	c.CHQ_CPFEMIT cpf,\n" +
                "	c.CHQ_NCHQ numerocheque,\n" +
                "	cast(c.BCO_CODI as integer) id_banco,\n" +
                "	c.CHQ_AGEN agencia,\n" +
                "	c.CHQ_CONT conta,\n" +
                "	c.CHQ_VEND numerocupom,\n" +
                "	isnull(c.CHQ_VALO,0) valor,	\n" +
                "	case when isnull(c.CHQ_CPFEMIT,'') != '' then 'CPF do emitente ' + isnull(c.chq_cpfemit,'') else '' end observacao,\n" +
                "       '' as rg,\n" +        
                "	coalesce(cli.CLI_FONR,cli.CLi_FONC) telefone,\n" +
                "	c.chq_emit nome,\n" +
                "	case cli.CLI_TPPESSOA when 'F' then 1 else 0 end id_tipoinscricao,\n" +
                "	c.CHQ_VENC vencimento,\n" +
                "	0 as valorjuros,\n" +
                "	c.CHQ_VALO valorinicial,\n" +
                "       c.CHE_CEAN cmc7\n" +
                "from \n" +
                "	CHEQUE c\n" +
                "	left join CLIENTE cli on\n" +
                "		c.CLI_CODI = cli.CLI_CODI\n" +
                "where\n" +
                "	cast(c.LOC_CODI as integer) = " + idLojaCliente + "\n" +        
                "order by\n" +
                "	data"
            );

            while (rst.next()) {

                ReceberChequeVO oReceberCheque = new ReceberChequeVO();

                if (rst.getString("cpf")!=null){
                    cpfCnpj = Long.parseLong(rst.getString("cpf"));
                }else{
                    cpfCnpj = 123;
                }

                if (String.valueOf(cpfCnpj).length() > 11) {
                    idTipoInscricao = 0;
                } else {
                    idTipoInscricao = 1;
                }

                idBanco = Utils.retornarBanco(Integer.parseInt(rst.getString("id_banco").trim()));

                if ((rst.getString("agencia") != null)
                        && (!rst.getString("agencia").trim().isEmpty())) {
                    agencia = Utils.acertarTexto(rst.getString("agencia").trim().replace("'", ""),10);
                } else {
                    agencia = "";
                }

                if ((rst.getString("conta") != null)
                        && (!rst.getString("conta").trim().isEmpty())) {
                    conta = Utils.acertarTexto(rst.getString("conta").trim().replace("'", ""),10);
                } else {
                    conta = "";
                }

                if ((rst.getString("numerocheque") != null)
                        && (!rst.getString("numerocheque").trim().isEmpty())) {

                    cheque = Integer.parseInt(Utils.formataNumero(rst.getString("numerocheque")));

                    if (String.valueOf(cheque).length() > 10) {
                        cheque = Integer.parseInt(String.valueOf(cheque).substring(0, 10));
                    }
                } else {
                    cheque = 0;
                }

                if ((rst.getString("data") != null)
                        && (!rst.getString("data").trim().isEmpty())) {

                    dataemissao = rst.getString("data").trim();
                } else {
                    dataemissao = "2016/02/01";
                }

                if ((rst.getString("vencimento") != null)
                        && (!rst.getString("vencimento").trim().isEmpty())) {

                    datavencimento = rst.getString("vencimento").trim();
                } else {
                    datavencimento = new SimpleDateFormat("yyyu/MM/dd").format(new Date());
                }

                if ((rst.getString("nome") != null)
                        && (!rst.getString("nome").isEmpty())) {
                    nome = Utils.acertarTexto(rst.getString("nome").replace("'", "").trim());
                } else {
                    nome = "";
                }
                
                if ((rst.getString("cmc7") != null)
                        && (!rst.getString("cmc7").isEmpty())) {
                    cmc7 = Utils.formataNumero(rst.getString("cmc7").replace("'", "").trim(), 34);
                } else {
                    cmc7 = "";
                }

                if ((rst.getString("rg") != null) && (!rst.getString("rg").isEmpty())) {
                    rg = Utils.acertarTexto(rst.getString("rg").trim().replace("'", ""));

                    if (rg.length() > 20) {
                        rg = rg.substring(0, 20);
                    }
                } else {
                    rg = "";
                }

                valor = Double.parseDouble(rst.getString("valor"));
                numerocupom = Integer.parseInt(Utils.formataNumero(
                    (rst.getString("numerocupom") == null ? "" : rst.getString("numerocupom"))
                    .replace("/","").replace(".","").replace(",","").replace("-","")));

                juros = rst.getFloat("valorjuros");

                if ((rst.getString("observacao") != null) && (!rst.getString("observacao").isEmpty())) {
                    observacao = "IMPORTADO VR. " + Utils.acertarTexto(rst.getString("observacao").replace("'", "").trim());
                } else {
                    observacao = "IMPORTADO VR";
                }

                if ((rst.getString("telefone") != null) && (!rst.getString("telefone").isEmpty()) && (!"0".equals(rst.getString("telefone").trim()))) {
                    telefone = Utils.formataNumero(rst.getString("telefone"), 14);
                } else {
                    telefone = "";
                }

                oReceberCheque.id_loja = idLojaCliente;
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
                oReceberCheque.cmc7 = cmc7;

                vReceberCheque.add(oReceberCheque);

            }

            return vReceberCheque;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public List<ReceberCreditoRotativoVO> carregarCreditoRotativo(int idLojaVR, int idLojaCliente) throws Exception {

        Statement stm;
        ResultSet rst;
        List<ReceberCreditoRotativoVO> vReceberCreditoRotativo = new ArrayList<>();

        int id_cliente, numerocupom;
        double valor;
        String observacao, dataemissao, datavencimento;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();

            rst = stm.executeQuery(
                "select\n" +
                "	cast(r.LOC_CODI as Integer) id_loja,\n" +
                "	r.DRC_DTEM dataemissao,\n" +
                "	r.DRC_NDOC numerocupom,\n" +
                "	r.DRC_VDUP valor,\n" +
                "	r.drc_dcto,\n" +
                "	cast(r.cli_codi as integer) id_clientepreferencial,\n" +
                "	r.DRC_DTVE datavencimento\n" +
                "from\n" +
                "	TBRECEBER r\n" +
                "	inner join CLIENTE c on\n" +
                "		r.CLI_CODI = c.CLI_CODI\n" +                  
                "where\n" +
                "	cast(r.LOC_CODI as integer) = " + idLojaCliente +            
                " and cast(r.cli_codi as integer) <> 0 \n"    
            );

            while (rst.next()) {

                ReceberCreditoRotativoVO cred = new ReceberCreditoRotativoVO();
                
                id_cliente = rst.getInt("id_clientepreferencial");
                
                dataemissao = rst.getString("dataemissao");
                datavencimento = rst.getString("datavencimento");
                
                try{
                    numerocupom = Integer.parseInt(Utils.formataNumero(rst.getString("numerocupom")));
                }catch(NumberFormatException ex){
                    numerocupom = 0;                    
                }
                
                valor = Double.parseDouble(rst.getString("valor"));

                if ((rst.getString("drc_dcto") != null)
                        && (!rst.getString("drc_dcto").isEmpty())) {
                    observacao = "IMPORTADO VR " + Utils.acertarTexto(rst.getString("drc_dcto").replace("'", ""));
                } else {
                    observacao = "IMPORTADO VR";
                }

                //oReceberCreditoRotativo.cnpjCliente = Long.parseLong(rst.getString("cnpjCliente"));
                cred.id_loja = idLojaVR;
                cred.dataemissao = dataemissao;
                cred.numerocupom = numerocupom;
                cred.valor = valor;
                cred.observacao = observacao;
                cred.id_clientepreferencial = id_cliente;
                cred.datavencimento = datavencimento;

                vReceberCreditoRotativo.add(cred);

            }

            return vReceberCreditoRotativo;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }   
    
    public List<MercadologicoVO> carregarMercadologico(int nivel) throws Exception {

        Statement stm;
        ResultSet rst;
        List<MercadologicoVO> vMercadologico = new ArrayList<>();
        String descricao;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            
            rst = stm.executeQuery(
                "SELECT\n" +
                "	merc1_cod,\n" +
                "	dp.DPT_DESC merc1_desc,\n" +
                "	merc2_cod,\n" +
                "	sc.SEC_NOME merc2_desc,\n" +
                "	merc3_cod,\n" +
                "	gr.GRU_DESC merc3_desc,\n" +
                "	merc4_cod,\n" +
                "	sg.SGR_DESC merc4_desc\n" +
                "FROM\n" +
                "(select DISTINCT \n" +
                "	cast(P.DPT_CODI as integer) merc1_cod, \n" +
                "	cast(P.SEC_CODI as integer) merc2_cod, \n" +
                "	cast(P.GRU_CODI as integer) merc3_cod, \n" +
                "	cast(P.SGR_CODI as integer) merc4_cod\n" +
                "FROM PRODUTO P) A\n" +
                "join TBDEPARTAMENTO dp on\n" +
                "	dp.DPT_CODI = a.merc1_cod\n" +
                "join SECAO sc on\n" +
                "	sc.DPT_CODI = dp.DPT_CODI and\n" +
                "	sc.SEC_CODI = a.merc2_cod\n" +
                "join TBGRUPO gr on\n" +
                "	gr.DPT_CODI = dp.DPT_CODI and\n" +
                "	gr.SEC_CODI = sc.SEC_CODI and\n" +
                "	gr.GRU_CODI = a.merc3_cod\n" +
                "join TBSUBGRUPO sg on\n" +
                "	sg.DPT_CODI = dp.DPT_CODI and\n" +
                "	sg.SEC_CODI = sc.SEC_CODI and\n" +
                "	sg.GRU_CODI = gr.GRU_CODI and\n" +
                "	sg.SGR_CODI = a.merc4_cod\n" +
                "ORDER BY\n" +
                "	merc1_cod,merc2_cod,merc3_cod,merc4_cod"
            );

            while (rst.next()) {

                MercadologicoVO oMercadologico = new MercadologicoVO();

                if (nivel == 1) {
                    descricao = Utils.acertarTexto(rst.getString("merc1_desc"), 35);

                    oMercadologico.mercadologico1 = rst.getInt("merc1_cod");
                    oMercadologico.mercadologico2 = 0;
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;

                } else if (nivel == 2) {

                    descricao = Utils.acertarTexto(rst.getString("merc2_desc"), 35);
                    
                    oMercadologico.mercadologico1 = rst.getInt("merc1_cod");
                    oMercadologico.mercadologico2 = rst.getInt("merc2_cod");
                    oMercadologico.mercadologico3 = 0;
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 3) {

                    descricao = Utils.acertarTexto(rst.getString("merc3_desc"), 35);

                    oMercadologico.mercadologico1 = rst.getInt("merc1_cod");
                    oMercadologico.mercadologico2 = rst.getInt("merc2_cod");
                    oMercadologico.mercadologico3 = rst.getInt("merc3_cod");
                    oMercadologico.mercadologico4 = 0;
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                } else if (nivel == 4) {

                    descricao = Utils.acertarTexto(rst.getString("merc4_desc"), 35);

                    oMercadologico.mercadologico1 = rst.getInt("merc1_cod");
                    oMercadologico.mercadologico2 = rst.getInt("merc2_cod");
                    oMercadologico.mercadologico3 = rst.getInt("merc3_cod");
                    oMercadologico.mercadologico4 = rst.getInt("merc4_cod");
                    oMercadologico.mercadologico5 = 0;
                    oMercadologico.descricao = descricao;
                    oMercadologico.nivel = nivel;
                }

                vMercadologico.add(oMercadologico);
            }
            stm.close();
            return vMercadologico;

        } catch (Exception ex) {

            throw ex;
        }

    }    
    
    public Map<Integer, ProdutoVO> carregarProduto(int idLojaVR, int idLojaCliente) throws SQLException, Exception {

        StringBuilder sql;
        Statement stm , stmPostgres;
        ResultSet rst, rst2, rst3;

        Map<Integer, ProdutoVO> vProduto = new HashMap<>();

        int id, mercadologico1, mercadologico2, mercadologico3, mercadologico4, ncm1 = 0, ncm2 = 0, ncm3 = 0,
                id_familiaproduto, codigoBalanca, id_tipoEmbalagem, validade, referencia,
                qtdEmbalagem = 1,idSituacaocadastro=1;
        String descricaocompleta, descricaoreduzida, descricaogondola, tribAliquota, ncmAtual;
        String dataCadastro;
        double margem=0, custo=0, precoVenda;
        long codigobarras;
        boolean eBalanca, pesavel;
        
        int idEstadoLoja = 23;

        try {            
            int maxMercadologicoId = 0;
            //Localiza o mercadológico máx
            sql = new StringBuilder();
            sql.append("select max(mercadologico1) as mercadologico1 ");
            sql.append("from mercadologico ");       
                        
            Conexao.begin();
            stmPostgres = Conexao.createStatement();
            try {                
                try (ResultSet rstPostgres = stmPostgres.executeQuery(sql.toString())) {
                    if(rstPostgres.next()) {
                       maxMercadologicoId = rstPostgres.getInt("mercadologico1");
                    } 
                }              
                
                Conexao.commit();
            } catch (Exception e) {
                Conexao.rollback();
                throw e;
            } finally {
                stmPostgres.close();
            }

            stm = ConexaoSqlServer.getConexao().createStatement();
            
            try (Statement st = ConexaoSqlServer.getConexao().createStatement()) {
                try (ResultSet rs = st.executeQuery(
                    "select uf.UND_CDGIEF uf_id\n" +
                    "from TBLOCALIZACAO loc\n" + 
                    "join TBUF uf on uf.UND_CDNAC = loc.UND_CDNAC\n" + 
                    "where loc.LOC_CODI = " + idLojaCliente)
                ) {
                    if (rs.next()) {
                        idEstadoLoja = rs.getInt("uf_id");
                    }
                }
            }
            
            rst = stm.executeQuery(getProdutosSQL(idLojaCliente,"PRODUTO"));

            stmPostgres = Conexao.createStatement();
                
            while (rst.next()) {

                eBalanca = false;
                codigoBalanca = 0;
                referencia = -1;
                id_tipoEmbalagem = 4;
                validade = 0;
                pesavel = false;

                ProdutoVO oProduto = new ProdutoVO();

                id = rst.getInt("pro_codi");

                rst2 = stmPostgres.executeQuery(
                    "select \n" +
                    "	codigo, \n" +
                    "	descricao, \n" +
                    "	pesavel, \n" +
                    "	validade \n" +
                    "from \n" +
                    "	implantacao.produtobalanca\n" +
                    "where\n" +
                    "	codigo = " + id
                );

                if (rst2.next()) {
                    eBalanca = true;
                    codigoBalanca = rst2.getInt("codigo");
                    validade = rst2.getInt("validade");
                    if ("P".equals(rst2.getString("pesavel"))) {
                        id_tipoEmbalagem = 4;
                        pesavel = false;
                    } else {
                        id_tipoEmbalagem = 0;
                        pesavel = true;
                    }
                } else {
                    eBalanca = false;
                    codigoBalanca = 0;
                    validade = 0;
                    pesavel = false;
                    if ("CX".equals(rst.getString("id_tipoembalagem").trim())) {                        
                        id_tipoEmbalagem = 1;
                    } else if ("KG".equals(rst.getString("id_tipoembalagem").trim())) {
                        id_tipoEmbalagem = 4;
                    } else if ("UN".equals(rst.getString("id_tipoembalagem").trim())) {
                        id_tipoEmbalagem = 0;
                    } else {
                        id_tipoEmbalagem = 0;
                    }
                }

                qtdEmbalagem = (int) rst.getDouble("qtdEmbalagem");
                idSituacaocadastro = rst.getInt("situacaocadastro");
                        
                if ((rst.getString("descricaocompleta") != null)
                        && (!rst.getString("descricaocompleta").isEmpty())) {
                    descricaocompleta = Utils.acertarTexto(rst.getString("descricaocompleta").replace("'", "").trim());
                } else {
                    descricaocompleta = "PRODUTO SEM DESCRICAO " + id;
                }

                if ((rst.getString("descricaoreduzida") != null)
                        && (!rst.getString("descricaoreduzida").isEmpty())) {
                    descricaoreduzida = Utils.acertarTexto(rst.getString("descricaoreduzida").replace("'", "").trim());
                } else {
                    descricaoreduzida = descricaocompleta;
                }

                if ((rst.getString("descricaogondola") != null)
                        && (!rst.getString("descricaogondola").isEmpty())) {
                    descricaogondola = Utils.acertarTexto(rst.getString("descricaogondola").replace("'", "").trim());
                } else {
                    descricaogondola = descricaocompleta;
                }

                if (rst.getString("dataCadastro") != null) {
                    dataCadastro = Util.formatDataGUI(rst.getDate("dataCadastro"));
                } else {
                    dataCadastro = Util.formatDataGUI(new java.sql.Date(new java.util.Date().getTime()));
                }
                
                if ((rst.getString("mercadologico1") != null)
                        && (!rst.getString("mercadologico1").isEmpty())) {
                    mercadologico1 = Integer.parseInt(rst.getString("mercadologico1"));
                } else {
                    mercadologico1 = 0;
                }

                if ((rst.getString("mercadologico2") != null)
                        && (!rst.getString("mercadologico2").isEmpty())) {
                    mercadologico2 = Integer.parseInt(rst.getString("mercadologico2"));
                } else {
                    mercadologico2 = 0;
                }

                if ((rst.getString("mercadologico3") != null)
                        && (!rst.getString("mercadologico3").isEmpty())) {
                    mercadologico3 = Integer.parseInt(rst.getString("mercadologico3"));
                } else {
                    mercadologico3 = 0;
                }
                
                if ((rst.getString("mercadologico4") != null)
                        && (!rst.getString("mercadologico4").isEmpty())) {
                    mercadologico4 = Integer.parseInt(rst.getString("mercadologico4"));
                } else {
                    mercadologico4 = 0;
                }
                
                if (!Utils.verificaExisteMercadologico4Nivel(mercadologico1, mercadologico2, mercadologico3, mercadologico4)) {
                        mercadologico1 = 7;
                        mercadologico2 = 1;
                        mercadologico3 = 1;   
                        mercadologico4 = 1;
                }

                if ((rst.getString("ncm1") != null)
                        && (!rst.getString("ncm1").isEmpty())
                        && (rst.getString("ncm1").trim().length() > 5)) {

                    ncmAtual = rst.getString("ncm1").trim();

                    NcmVO oNcm = new NcmDAO().validar(ncmAtual);

                    ncm1 = oNcm.ncm1;
                    ncm2 = oNcm.ncm2;
                    ncm3 = oNcm.ncm3;

                } else {
                    ncm1 = 9701;
                    ncm2 = 90;
                    ncm3 = 0;
                }

                if ((rst.getString("id_familiaproduto") != null)
                        && (!rst.getString("id_familiaproduto").isEmpty())) {
                    id_familiaproduto = Integer.parseInt(rst.getString("id_familiaproduto"));
                } else {
                    id_familiaproduto = -1;
                }

                if ((rst.getString("custoSemImposto") != null)
                        && (!"".equals(rst.getString("custoSemImposto")))) {
                    custo = rst.getDouble("custoSemImposto");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("precoVenda") != null)
                        && (!"".equals(rst.getString("precoVenda")))) {
                    precoVenda = rst.getDouble("precoVenda");
                } else {
                    precoVenda = 0;
                }
                
                if ((rst.getString("margem") != null)
                        && (!"".equals(rst.getString("margem")))) {
                   if (rst.getDouble("margem")>0) {
                       margem=rst.getDouble("margem");                   
                   }else{
                        if((custo>0) && (precoVenda>0)){
                            margem=((custo/precoVenda)*100);                    
                        }else{
                            margem=0;
                        }                       
                   }                                        
                }else if((custo>0) && (precoVenda>0)){
                   margem=((custo/precoVenda)*100);                    
                }
                
                if ((rst.getString("tribAliquota") != null)
                        && (!rst.getString("tribAliquota").isEmpty())) {
                    tribAliquota = rst.getString("tribAliquota").trim();
                } else {
                    tribAliquota = "8";
                }

                if (eBalanca) {
                    codigobarras = Long.parseLong(String.valueOf(id));
                } else {
                    codigobarras = -2;// não importar produtoautomacao codigo barras
                }

                if (descricaocompleta.length() > 60) {

                    descricaocompleta = descricaocompleta.substring(0, 60);
                }

                if (descricaoreduzida.length() > 22) {

                    descricaoreduzida = descricaoreduzida.substring(0, 22);
                }

                if (descricaogondola.length() > 60) {

                    descricaogondola = descricaogondola.substring(0, 60);
                }

                sql = new StringBuilder();
                sql.append("select id from familiaproduto ");
                sql.append("where id = " + id_familiaproduto);

                rst3 = stmPostgres.executeQuery(sql.toString());

                if (rst3.next()) {
                    id_familiaproduto = rst3.getInt("id");
                } else {
                    id_familiaproduto = -1;
                }                             

                oProduto.id = id;
                oProduto.codigoAnterior = id;
                oProduto.qtdEmbalagem = qtdEmbalagem;
                oProduto.descricaoCompleta = descricaocompleta;
                oProduto.descricaoReduzida = descricaoreduzida;
                oProduto.descricaoGondola = descricaogondola;
                oProduto.dataCadastro = String.valueOf(dataCadastro);
                oProduto.idSituacaoCadastro = idSituacaocadastro;
                oProduto.mercadologico1 = mercadologico1;
                oProduto.mercadologico2 = mercadologico2;
                oProduto.mercadologico3 = mercadologico3;
                oProduto.mercadologico4 = mercadologico4;
                oProduto.ncm1 = ncm1;
                oProduto.ncm2 = ncm2;
                oProduto.ncm3 = ncm3;
                oProduto.idFamiliaProduto = id_familiaproduto;
                oProduto.margem = margem;
                oProduto.qtdEmbalagem = 1;
                oProduto.idTipoEmbalagem = id_tipoEmbalagem;
                oProduto.idComprador = 1;
                oProduto.idFornecedorFabricante = 1;
                oProduto.pesavel = pesavel;
                oProduto.validade = validade;
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
                oProduto.tipoNaturezaReceita = -1;
                
                //PIS/COFINS
                oProduto.idTipoPisCofinsCredito = this.retornarPisCofinsEntrada(rst.getString("pis_cst_e"));
                oProduto.idTipoPisCofinsDebito = this.retornarPisCofinsSaida(rst.getString("pis_cst_s"));
                oProduto.tipoNaturezaReceita = this.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("cod_natureza_receita"));
                //FIM PIS/COFINS

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();

                
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;
                
                //float custoComImposto, custoSemImposto, custoMedioComImposto, custoMedioSemImposto; 
                
                oComplemento.setCustoComImposto(rst.getDouble("custoComImposto"));
                oComplemento.setCustoComImpostoAnterior(rst.getDouble("custoComImposto"));
                oComplemento.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                oComplemento.setCustoSemImpostoAnterior(rst.getDouble("custoSemImposto"));                
                oComplemento.setCustoMedioComImposto(rst.getDouble("custoMedioComImposto"));
                oComplemento.setCustoMedioComImpostoAnterior(rst.getDouble("custoMedioComImposto"));
                oComplemento.setCustoMedioSemImposto(rst.getDouble("custoMedioSemImposto"));
                oComplemento.setCustoMedioSemImpostoAnterior(rst.getDouble("custoMedioSemImposto"));    
                oComplemento.setIdSituacaoCadastro(idSituacaocadastro);
   
                oComplemento.idLoja = idLojaVR;

                oProduto.vComplemento.add(oComplemento);

                ProdutoAliquotaVO oAliquota = new ProdutoAliquotaVO();

                oAliquota.idEstado = idEstadoLoja;
                oAliquota.idAliquotaDebito = retornarAliquotaICMS(tribAliquota);
                oAliquota.idAliquotaCredito = retornarAliquotaICMS(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstado = retornarAliquotaICMS(tribAliquota);
                oAliquota.idAliquotaCreditoForaEstado = retornarAliquotaICMS(tribAliquota);
                oAliquota.idAliquotaDebitoForaEstadoNF = retornarAliquotaICMS(tribAliquota);

                oProduto.vAliquota.add(oAliquota);

                ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                oAutomacao.codigoBarras = codigobarras;
                oAutomacao.idTipoEmbalagem = id_tipoEmbalagem;
                oAutomacao.qtdEmbalagem = 1;                

                oProduto.vAutomacao.add(oAutomacao);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.setCodigoanterior(id);
                oCodigoAnterior.setCodigobalanca(codigoBalanca);
                oCodigoAnterior.setE_balanca(eBalanca);
                oCodigoAnterior.setMargem(margem);
                oCodigoAnterior.setPrecovenda(precoVenda);
                oCodigoAnterior.setBarras(codigobarras);
                oCodigoAnterior.setReferencia(referencia);
                oCodigoAnterior.setNcm(rst.getString("ncm1"));
                oCodigoAnterior.setId_loja(idLojaVR);

                //PIS/COFINS
                oCodigoAnterior.piscofinscredito = rst.getInt("pis_cst_e");
                oCodigoAnterior.piscofinsdebito = rst.getInt("pis_cst_s");
                oCodigoAnterior.naturezareceita = oProduto.tipoNaturezaReceita;
                //FIM PIS/COFINS

                if ((rst.getString("pro_codi") != null)
                        && (!rst.getString("pro_codi").isEmpty())) {

                    oCodigoAnterior.setRef_icmsdebito(rst.getString("pro_codi"));
                }
                oCodigoAnterior.setId_loja(idLojaVR);
                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(id, oProduto);
            }
            stm.close();
            stmPostgres.close();
            return vProduto;

        } catch (SQLException | NumberFormatException ex) {
            throw ex;
        }
    }

    private int retornarAliquotaICMS(String tribAliquota) {
        switch (tribAliquota) {
            case "001": return 20; 
            case "002": return 25;
            case "003": return 3;
            case "004": return 1;
            case "005": return 6;
            case "006": return 7;
            case "007": return 21;
            case "008": return 1;
            case "009": return 7;
            case "010": return 7;
            case "011": return 20;
            case "012": return 1;
            case "013": return 1;
            case "014": return 3;
            case "015": return 3;
            case "016": return 6;
            case "017": return 6;
            case "018": return 21;
            case "019": return 21;
            case "020": return 25;
            case "021": return 25;
            case "022": return 7;
            case "023": return 7;
            case "024": return 7;
            case "027": return 7;
            case "031": return 7;
            case "032": return 32;
            case "033": return 8;
            case "035": return 8;
            case "036": return 1;
            case "041": return 19;
            case "043": return 28;
            case "044": return 8;
            case "048": return 7;
            case "059": return 7;                
              
            default: return 8;
        }
    }   
    
    public Map<Integer, ProdutoVO> carregarPrecoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double precoVenda=0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            rst = stm.executeQuery(getProdutosSQL(idLojaCliente,"PRECO"));
            
            while (rst.next()) {

                idProduto = rst.getInt("pro_codi");
                
                if ((rst.getString("precoVenda") != null)
                        && (!"".equals(rst.getString("precoVenda")))) {
                    precoVenda = rst.getDouble("precoVenda");
                } else {
                    precoVenda = 0;
                }
                

                ProdutoVO oProduto = new ProdutoVO();
                oProduto.id = idProduto;

                ProdutoComplementoVO oComplemento = new ProdutoComplementoVO();
                
                oComplemento.idLoja = idLojaVR;
                oComplemento.precoVenda = precoVenda;
                oComplemento.precoDiaSeguinte = precoVenda;

                oProduto.vComplemento.add(oComplemento);

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.precovenda = precoVenda;
                oCodigoAnterior.id_loja = idLojaVR;

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }  
    
    public Map<Integer, ProdutoVO> carregarCustoProduto(int idLojaVR, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double custoSemImposto = 0, custo = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            rst = stm.executeQuery(getProdutosSQL(idLojaCliente,"CUSTO"));

            while (rst.next()) {
                idProduto = rst.getInt("pro_codi");

                if ((rst.getString("custoComImposto") != null)
                        && (!"".equals(rst.getString("custoComImposto")))) {
                    custo = rst.getDouble("custoComImposto");
                } else {
                    custo = 0;
                }
                
                if ((rst.getString("custoSemImposto") != null)
                        && (!"".equals(rst.getString("custoSemImposto")))) {
                    custoSemImposto = rst.getDouble("custoSemImposto");
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
    
    public Map<Long, ProdutoVO> carregarEanProduto(int idLojaVR, int idLojaCliente) throws Exception {
        Statement stm = null;
        ResultSet rst;
        Map<Long, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        long codigobarras;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            rst = stm.executeQuery(
                "select\n" +
                "	cast(ean.MAT_CODI as bigint) prodCod,\n" +
                "	cast(ean.EAN_CODI as bigint) ean,\n" +
                "       isnull(m.MAT_FNTP,'') tipoEmbalagem\n" +
                "from\n" +
                "	EAN\n" +
                "    join MATERIAL m on\n" +
                "	 m.MAT_CODI = EAN.MAT_CODI\n" +
                "where\n" +
                "    cast(ean.MAT_CODI as bigint) > 0\n" +
                "order by\n" +
                "    prodCod"
            );

            while (rst.next()) {
                
                int idBalanca = rst.getInt("prodCod");
                Statement stmPostgres = Conexao.createStatement();
                
                ResultSet rst2 = stmPostgres.executeQuery(
                    "select \n" +
                    "	codigo, \n" +
                    "	descricao, \n" +
                    "	pesavel, \n" +
                    "	validade \n" +
                    "from \n" +
                    "	implantacao.produtobalanca\n" +
                    "where\n" +
                    "	codigo = " + idBalanca
                );

                if (!rst2.next()) {  

                    ProdutoVO oProduto = new ProdutoVO();

                    idProduto = rst.getInt("prodCod");

                    oProduto.id = idProduto;

                    if ((rst.getString("ean")!= null)
                            && (!rst.getString("ean").isEmpty())){
                        if (rst.getString("ean").length() <= 14) {
                            codigobarras = rst.getLong("ean");
                        } else {
                            codigobarras = -1;
                        }
                    }else{
                        codigobarras = -1;                    
                    }
                    if (String.valueOf(codigobarras).length() >= 7) {

                        ProdutoAutomacaoVO oAutomacao = new ProdutoAutomacaoVO();

                        oAutomacao.codigoBarras = codigobarras;
                        switch (rst.getString("tipoEmbalagem").toUpperCase()) {
                            case "KG": oAutomacao.idTipoEmbalagem = 4; break;
                            case "CX": oAutomacao.idTipoEmbalagem = 1; break;
                            default: oAutomacao.idTipoEmbalagem = 0; break;
                        }                      
                        oAutomacao.qtdEmbalagem = 1;
                        oProduto.vAutomacao.add(oAutomacao);

                        vProduto.put(codigobarras, oProduto);
                        
                    }

                }
            }

            return vProduto;

        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    public Map<Integer, ProdutoVO> carregarEstoqueProduto(int idLojaVR, int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        int idProduto;
        double estoque = 0;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            rst = stm.executeQuery(getProdutosSQL(idLojaCliente, "ESTOQUE"));
                    
            while (rst.next()) {

                idProduto = Integer.parseInt(rst.getString("cod_prod"));
                estoque = rst.getDouble("estoque");

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

    /**
     * Retorna o SQL que localiza os produtos de acordo com a loja.
     * @param idLojaCliente id da loja do cliente.
     * @return SQL pronto que localiza os produtos.
     */
    private String getProdutosSQL(int idLojaCliente, String Tipo) {
        String SQL="";
        if ("PRODUTO".equals(Tipo)){
            SQL=
                "select \n" +
                "    cast(m.MAT_CODI as integer) pro_codi,\n" +
                "    m.MAT_FNQT qtdEmbalagem,\n" +
                "    m.MAT_DESC descricaocompleta,\n" +
                "    m.MAT_DESR descricaoreduzida,\n" +
                "    m.MAT_DESC descricaogondola,\n" +
                "    m.MAT_DTCD dataCadastro,\n" +
                "    cast(m.DPT_CODI as integer) mercadologico1,\n" +
                "    cast(m.SEC_CODI as integer) mercadologico2,\n" +
                "    cast(m.GRU_CODI as integer) mercadologico3,\n" +
                "    cast(m.SGR_CODI as integer) mercadologico4,\n" +
                "    m.MAT_NCM ncm1,\n" +
                "    -1 ncm2,\n" +
                "    -1 ncm3,\n" +
                "    -1 id_familiaproduto,\n" +
                "    0 as margem,\n" +
                "    isnull(m.MAT_FNTP,'') id_tipoembalagem,\n" +
                "    m.MAT_VALDIAS validade,\n" +
                "    cm.MAT_PRAQ custoSemImposto,\n" +
                "	 cm.MAT_PRCU custoComImposto,\n" +
                "	 cm.MAT_PRMD custoMedioSemImposto,\n" +
                "	 cm.MAT_PRMD custoMedioComImposto,\n" +
                "    case m.MAT_SITU when 'A' then 1 else 0 end situacaocadastro,\n" +
                "	 vm.TAB_PRC1 precoVenda,\n" +
                "    case \n" +
                "       when vm.TAB_PRC1 > 0 then vm.TAB_PRC1\n" +
                "       when vm.TAB_PRC2 > 0 then vm.TAB_PRC2\n" +
                "       when vm.TAB_PRC3 > 0 then vm.TAB_PRC3\n" +
                "       when vm.TAB_PRC4 > 0 then vm.TAB_PRC4\n" +
                "       else cm.MAT_PRAD\n" +
                "	 end as aux,\n" +
                "    tbe.TRB_ALIQ aliquotaDebito,\n" +
                "    tbs.TRB_ALIQ aliquotaCredito,\n" +
                "    tbs.TRB_CODI tribAliquota,\n" +
                "	 piscof.MAT_NATPIS cod_natureza_receita,\n" +
                "    piscof.MAT_CSTPISCOFENT pis_cst_e,\n" +
                "    piscof.MAT_CSTPISCOF pis_cst_s\n" +
                "from \n" +
                "    MATERIAL M\n" +
                "    join TBCUSTOMATE cm on\n" +
                "        cm.LOC_CODI = " + idLojaCliente + " and\n" +
                "        cm.MAT_CODI = m.MAT_CODI\n" +
                "    join TBVENDAMAT vm on\n" +
                "        vm.LOC_CODI = " + idLojaCliente + " and\n" +
                "        vm.MAT_CODI = m.MAT_CODI\n" +
                "    left join TBMATERIAL_TRIBUTACAO_LOCAL mtl on\n" +
                "        mtl.LOC_CODI = " + idLojaCliente + " and\n" +
                "        mtl.MAT_CODI = m.MAT_CODI\n" +
                "    left join TBTRIBUTACAO tbe on\n" +
                "        tbe.TRB_CODI = mtl.TRB_CDEN\n" +
                "    left join TBTRIBUTACAO tbs on\n" +
                "        tbs.TRB_CODI = mtl.TRB_CDSA\n" +
                "    left join TBIMPOSTOFEDERAL piscof on\n" +
                "        piscof.MAT_CODI = m.MAT_CODI and\n" +
                "        piscof.LOC_CODI = " + idLojaCliente + "\n" +
                "where cast(m.MAT_CODI as integer) > 0";
        } else if (("PRECO".equals(Tipo)) || ("CUSTO".equals(Tipo))) {
             SQL =
                "select \n" +
                "    cast(m.MAT_CODI as integer) pro_codi,\n" +
                "    cm.MAT_PRAQ custoSemImposto,\n" +
                "	 cm.MAT_PRCU custoComImposto,\n" +
                "	 cm.MAT_PRMD custoMedioSemImposto,\n" +
                "	 cm.MAT_PRMD custoMedioComImposto,\n" +
                "	 vm.TAB_PRC1 precoVenda\n" +
                "from \n" +
                "    MATERIAL M\n" +
                "    join TBCUSTOMATE cm on\n" +
                "        cm.LOC_CODI = " + idLojaCliente + " and\n" +
                "        cm.MAT_CODI = m.MAT_CODI\n" +
                "    join TBVENDAMAT vm on\n" +
                "        vm.LOC_CODI = " + idLojaCliente + " and\n" +
                "        vm.MAT_CODI = m.MAT_CODI\n" +
                "where cast(m.MAT_CODI as integer) > 0";  
        }else if ("ESTOQUE".equals(Tipo)) {
            SQL =
                "select\n" +
                "	cast(m.MAT_CODI as bigint) cod_prod,\n" +
                "	isnull(e.EST_QUAN,0) estoque\n" +
                "from\n" +
                "	material m\n" +
                "	right join estoque e on	\n" +
                "		m.MAT_CODI = e.PRO_CODI\n" +
                "where\n" +
                "	e.LOC_CODI = " + idLojaCliente + "\n" +
                "order by\n" +
                "	cod_prod";            
        }
        return SQL;
    }    
    public List<ProdutoFornecedorVO> carregarProdutoFornecedor() throws Exception {

        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst = null;
        Utils util = new Utils();
        List<ProdutoFornecedorVO> vProdutoFornecedor = new ArrayList<>();
        int idFornecedor, idProduto, qtdEmbalagem;
        String codigoExterno;
        java.sql.Date dataAlteracao = new java.sql.Date(new java.util.Date().getTime());

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();         
            rst = stm.executeQuery(
                "select \n" +
                "	cast(fm.mat_codi as bigint) cod_prod,\n" +
                "	cast(fm.for_codi as bigint) cod_forn,\n" +
                "       cast(f.FOR_CNPJ as bigint) cnpj,\n" +
                "	fm.FOR_REFE referencia_fornecedor,\n" +
                "	fm.FOR_QUAEMB qtd_embalagem\n" +
                "from \n" +
                "	TBFORNMATERIAL fm\n" +
                "	join MATERIAL m on\n" +
                "		fm.MAT_CODI = m.MAT_CODI\n" +
                "	join FORNECEDOR f on\n" +
                "		fm.FOR_CODI = f.FOR_CODI\n" +
                "order by\n" +
                "	cast(fm.mat_codi as bigint)"
            );

            while (rst.next()) {

                idFornecedor = rst.getInt("cod_forn");
                idProduto = rst.getInt("cod_prod");
                qtdEmbalagem = (int) rst.getDouble("qtd_embalagem");

                if ((rst.getString("referencia_fornecedor") != null)
                        && (!rst.getString("referencia_fornecedor").isEmpty())) {
                    codigoExterno = Utils.acertarTexto(rst.getString("referencia_fornecedor").replace("'", ""));
                } else {
                    codigoExterno = "";
                }

                ProdutoFornecedorVO oProdutoFornecedor = new ProdutoFornecedorVO();

                oProdutoFornecedor.cnpFornecedor = Utils.stringToLong(rst.getString("cnpj"));
                oProdutoFornecedor.id_fornecedor = idFornecedor;
                oProdutoFornecedor.id_produto = idProduto;
                oProdutoFornecedor.qtdembalagem = qtdEmbalagem;
                oProdutoFornecedor.dataalteracao = dataAlteracao;
                oProdutoFornecedor.codigoexterno = codigoExterno;

                vProdutoFornecedor.add(oProdutoFornecedor);
            }

            return vProdutoFornecedor;
        } catch (Exception ex) {
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
            sql.append(" from produto p ");
            sql.append(" where not exists(select pa.id from produtoautomacao pa where pa.id_produto = p.id) ");

            rst = stmPostgres.executeQuery(sql.toString());

            while (rst.next()) {

                idProduto    = Double.parseDouble(rst.getString("id"));
                
                if (rst.getInt("id_tipoembalagem") == 4) {
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
    
    public Map<Integer, ProdutoVO> carregarPisCofins(int idLojaCliente) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        ResultSet rst;
        Utils util = new Utils();
        Map<Integer, ProdutoVO> vProduto = new HashMap<>();
        String strNaturezaReceita = "";
        int idProduto, id_tipopiscofins,
                id_tipopiscofinscredito;

        try {

            stm = ConexaoSqlServer.getConexao().createStatement();
            
            rst = stm.executeQuery(
                "select\n" +
                "	cast(mpc.MAT_CODI as integer) codigo_produto,\n" +
                "	MAT_NATPIS cod_natureza_receita,\n" +
                "	MAT_CSTPISCOFENT pis_cst_e,\n" +
                "	MAT_CSTPISCOF pis_cst_s\n" +
                "from \n" +
                "	TBIMPOSTOFEDERAL mpc\n" +
                "where\n" +
                "	mpc.LOC_CODI = " + idLojaCliente
            );

            while (rst.next()) {

                ProdutoVO oProduto = new ProdutoVO();

                idProduto = Integer.parseInt(rst.getString("codigo_produto"));

                oProduto.id = Integer.parseInt(rst.getString("codigo_produto"));
                oProduto.idTipoPisCofinsCredito = this.retornarPisCofinsEntrada(rst.getString("pis_cst_e"));
                oProduto.idTipoPisCofinsDebito = this.retornarPisCofinsSaida(rst.getString("pis_cst_s"));
                oProduto.tipoNaturezaReceita = this.retornarTipoNaturezaReceita(oProduto.idTipoPisCofinsDebito, rst.getString("cod_natureza_receita"));

                CodigoAnteriorVO oCodigoAnterior = new CodigoAnteriorVO();

                oCodigoAnterior.piscofinscredito = rst.getInt("pis_cst_e");
                oCodigoAnterior.piscofinsdebito = rst.getInt("pis_cst_s");
                oCodigoAnterior.naturezareceita = oProduto.tipoNaturezaReceita;
                //oCodigoAnterior.naturezareceita = (int) Utils.stringToLong(strNaturezaReceita, -1);

                oProduto.vCodigoAnterior.add(oCodigoAnterior);

                vProduto.put(idProduto, oProduto);

            }
            return vProduto;
        } catch (SQLException | NumberFormatException ex) {

            throw ex;
        }
    }
    
    private int retornarPisCofinsEntrada(String pisCofinsEntrada) {
        pisCofinsEntrada = Utils.formataNumero(pisCofinsEntrada);
        switch (pisCofinsEntrada) {
            case "50": return 12;
            case "51": return 18;
            case "52": return 13; //Não encontrou no VR, usar padrão
            case "53": return 13; //Não encontrou no VR, usar padrão
            case "54": return 13; //Não encontrou no VR, usar padrão
            case "56": return 13; //Não encontrou no VR, usar padrão
            case "70": return 15;
            case "71": return 13;
            case "73": return 19;
            case "74": return 20;
            case "75": return 14;
            case "98": return 21; //Não encontrou no VR, usar padrão
            case "99": return 21;
            default: return 13;
        }
    }
    
    private int retornarPisCofinsSaida(String pisCofinsSaida) {
        pisCofinsSaida = Utils.formataNumero(pisCofinsSaida);
        switch (pisCofinsSaida) {
            case "01": return 0;
            case "04": return 3;
            case "05": return 2;
            case "06": return 7;
            case "07": return 1;
            case "09": return 1;
            case "49": return 9;
            case "99": return 21;
            default: return 1;
        }
    }

    private int retornarTipoNaturezaReceita(int pisCofinsDebito, String tipoNaturezaReceita) throws Exception {                    
        if (tipoNaturezaReceita != null) {
            tipoNaturezaReceita = tipoNaturezaReceita.trim();
            //if (!tipoNaturezaReceita.isEmpty()) {
                return Utils.retornarTipoNaturezaReceita(pisCofinsDebito, tipoNaturezaReceita);
            //} else {
            //    return 999;
            //}
        } else {
            return 999;
        }
    }

    public void importarIcms(int idLojaVR, int idLojaCliente) throws Exception {
        List<ProdutoVO> vProdutoNovo = new ArrayList<>();
        ProdutoDAO produto = new ProdutoDAO();
        
        ProgressBar.setStatus("Carregando dados...Produtos...ICMS...");
        Map<Integer, ProdutoVO> vCustoProdutoMilenio = carregarProduto(idLojaVR, idLojaCliente);

        List<LojaVO> vLoja = new LojaDAO().carregar();

        ProgressBar.setMaximum(vCustoProdutoMilenio.size());

        for (Integer keyId : vCustoProdutoMilenio.keySet()) {

            ProdutoVO oProduto = vCustoProdutoMilenio.get(keyId);

            vProdutoNovo.add(oProduto);

            ProgressBar.next();
        }

        produto.alterarICMSProduto(vProdutoNovo);           
    }
 
}
