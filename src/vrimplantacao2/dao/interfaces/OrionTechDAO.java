package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class OrionTechDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null || complemento.trim().equals("") ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        if (!"".equals(this.complemento)) {
            return "OrionTech - " + complemento;
        } else {
            return "OrionTech";
        }
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    CHAVEEMP,\n" +
                    "    ALIAS\n" +
                    "from\n" +
                    "    EMPRESA\n" +
                    "order by\n" +
                    "    1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("CHAVEEMP"),
                            rst.getString("ALIAS")
                    ));
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        //CONJPROD
        return super.getMercadologicos(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                //OpcaoProduto.MERCADOLOGICO,
                //OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                //OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                //OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.CEST,
                OpcaoProduto.NCM,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS
        ));
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.CHAVEPRO id,\n" +
                    "    p.CADASTRO datacadastro,\n" +
                    "    p.ULTIMAALTER dataalteracao,\n" +
                    "    ean.ean ean,\n" +
                    "    ean.QTDEMBALAGEM qtdembalagem,\n" +
                    "    upper(p.UNIDADE) unidade,\n" +
                    "    p.AGRANEL pesavel,\n" +
                    "    0 validade,\n" +
                    "    p.NOME descricaocompleta,\n" +
                    "    p.NOMEECF decricaoreduzida,\n" +
                    "    p.PRODREF id_familia,\n" +
                    "    p.PESOBRUTO pesobruto,\n" +
                    "    p.PESOLIQUIDO pesoliquido,\n" +
                    "    est.ESTOQUEMIN estoqueminimo,\n" +
                    "    est.ESTOQUEMAX estoquemaximo,\n" +
                    "    est.ESTOQUE,\n" +
                    "    est.ESTOQUETRC estoquetroca,\n" +
                    "    preco.LUCRO margem,\n" +
                    "    preco.PC custo,\n" +
                    "    preco.PVN preco,\n" +
                    "    p.ATIVO,\n" +
                    "    substring(lpad(I.CEST, 7, '0') from 1 for 2) || '.' ||\n" +
                    "        substring(lpad(I.CEST, 7, '0') from 3 for 3) || '.' ||\n" +
                    "        substring(lpad(I.CEST, 7, '0') from 6 for 2) as CEST,\n" +
                    "    i.TIPI ncm,\n" +
                    "    P.NRSTPIS as COD_NATUREZA_RECEITA,\n" +
                    "    iif(CP.TRIBUTACAO = 'T', 50, 70) as PIS_CST_E,\n" +
                    "    P.CSTPIS as PIS_CST_S,\n" +
                    "    icmps.CHAVEICM icms_id,\n" +
                    "    P.LUCROPREVST as MVA\n" +
                    "from\n" +
                    "    produto p\n" +
                    "    join prodemp pe on\n" +
                    "        pe.CHAVEPRO = p.CHAVEPRO and\n" +
                    "        pe.CHAVEEMP = " + getLojaOrigem() + "\n" +
                    "    left join (\n" +
                    "        select chavepro, ean, sum(qtdembalagem) qtdembalagem from (\n" +
                    "            select chavepro, codigo ean, qtdembalagem from CODBAR\n" +
                    "            union\n" +
                    "            select chavepro, gtin ean, QTEMBVENDA qtdembalagem from produto where not gtin is null\n" +
                    "        ) a group by 1, 2\n" +
                    "    ) ean on\n" +
                    "        ean.CHAVEPRO = p.CHAVEPRO\n" +
                    "    left join estoqprod est on\n" +
                    "        pe.CHAVEPRE = est.CHAVEPRE\n" +
                    "    left join PRODCTRLPRECO preco on\n" +
                    "        preco.CHAVEPRO = p.CHAVEPRO and\n" +
                    "        preco.CHAVECPC = 0\n" +
                    "    left join IPIPROD I on\n" +
                    "        I.CHAVEPRO = P.CHAVEPRO\n" +
                    "    left join CCTPIS CP on\n" +
                    "        CP.CODIGO = P.CSTPIS\n" +
                    "    left join ICMSPROD icmps on\n" +
                    "        icmps.CHAVEPRO = p.CHAVEPRO and\n" +
                    "        icmps.UF = 'MG' and\n" +
                    "        icmps.CRCTADQUIRENTE = 'F'\n" +
                    "order by\n" +
                    "    1"
            )) {
                //PRODBAB - Produtos de balança
                
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    
                    ProdutoBalancaVO bal = balanca.get(rst.getInt("id"));
                    if (bal != null) {
                        imp.setEan(String.valueOf(bal.getCodigo()));
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                        imp.seteBalanca(true);
                        imp.setValidade(bal.getValidade());
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.seteBalanca("S".equals(rst.getString("pesavel")));
                        imp.setValidade(rst.getInt("validade"));
                    }
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("decricaoreduzida"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("ESTOQUE"));
                    imp.setTroca(rst.getDouble("estoquetroca"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("ATIVO")) ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setCest(rst.getString("CEST"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS_CST_S"));
                    imp.setPiscofinsCstDebito(rst.getString("PIS_CST_E"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("COD_NATUREZA_RECEITA"));
                    imp.setIcmsDebitoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms_id"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms_id"));
                    imp.setIcmsCreditoId(rst.getString("icms_id"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icms_id"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    i.CHAVEICM id,\n" +
                    "    i.CODTRIB cst,\n" +
                    "    i.ALIQUOTA,\n" +
                    "    i.REDUCAO\n" +
                    "from\n" +
                    "    ICMS i"
            )) {
                while (rst.next()) {
                    MapaTributoIMP imp = new MapaTributoIMP(
                            rst.getString("id"),
                            String.format(
                                    "%s - %.2f - %.2f",
                                    rst.getString("cst"),
                                    rst.getDouble("ALIQUOTA"),
                                    rst.getDouble("REDUCAO")
                            ),
                            Utils.stringToInt(rst.getString("cst")),
                            rst.getDouble("ALIQUOTA"),
                            rst.getDouble("REDUCAO")
                    );
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.CHAVEPES id,\n" +
                    "    p.NOME razao,\n" +
                    "    coalesce(jd.NOMEFANTASIA, fs.APELIDO) fantasia,\n" +
                    "    coalesce(jd.CNPJ, fs.CPF) cnpj,\n" +
                    "    coalesce(jd.IE, fs.RG) inscricaoestadual,\n" +
                    "    jd.IM inscricaomunicipal,\n" +
                    "    coalesce(pesemp.ATIVO, 'S') ativo,\n" +
                    "    endp.LOGRADOURO,\n" +
                    "    endp.NUMERO,\n" +
                    "    endp.COMPLEMENTO,\n" +
                    "    endp.BAIRRO,\n" +
                    "    endp.ibgemunicipio,\n" +
                    "    endp.cep,\n" +
                    "    endc.LOGRADOURO cob_endereco,\n" +
                    "    endc.NUMERO cob_numero,\n" +
                    "    endc.COMPLEMENTO cob_complemento,\n" +
                    "    endc.BAIRRO cob_bairro,\n" +
                    "    endc.ibgemunicipio cob_ibgemunicipio,\n" +
                    "    endc.cep cob_cep,\n" +
                    "    p.PEDIDOMINIMO,\n" +
                    "    p.CADASTRO datacadastro,\n" +
                    "    p.OBSERVACAO\n" +
                    "from\n" +
                    "    pessoa p\n" +
                    "    left join pesemp on\n" +
                    "       p.chavepes = pesemp.chavepes and\n" +
                    "       pesemp.chaveemp = " + getLojaOrigem() + "\n" +
                    "    left join fisica fs on\n" +
                    "        p.CHAVEPES = fs.CHAVEPES\n" +
                    "    left join JURIDICA jd on\n" +
                    "        p.CHAVEPES = jd.CHAVEPES\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            endp.CHAVEPES,\n" +
                    "            ep.LOGRADOURO,\n" +
                    "            ep.NUMERO,\n" +
                    "            ep.COMPLEMENTO,\n" +
                    "            ep.BAIRRO,\n" +
                    "            mun.CODIBGE ibgemunicipio,\n" +
                    "            cep.CODIGO cep\n" +
                    "        from\n" +
                    "            ENDPESSOA endp\n" +
                    "        left join ENDERECO ep on\n" +
                    "            ep.CHAVEEND = endp.CHAVEEND\n" +
                    "        left join MUNICIPIO mun on\n" +
                    "            ep.CHAVEMUN = mun.CHAVEMUN\n" +
                    "        left join cep on\n" +
                    "            ep.CHAVECEP = cep.CHAVECEP\n" +
                    "        where\n" +
                    "            endp.FUNCAO = 'P'\n" +
                    "     ) endp on\n" +
                    "        endp.CHAVEPES = p.CHAVEPES\n" +
                    "     left join (\n" +
                    "        select\n" +
                    "            endp.CHAVEPES,\n" +
                    "            ep.LOGRADOURO,\n" +
                    "            ep.NUMERO,\n" +
                    "            ep.COMPLEMENTO,\n" +
                    "            ep.BAIRRO,\n" +
                    "            mun.CODIBGE ibgemunicipio,\n" +
                    "            cep.CODIGO cep\n" +
                    "        from\n" +
                    "            ENDPESSOA endp\n" +
                    "        left join ENDERECO ep on\n" +
                    "            ep.CHAVEEND = endp.CHAVEEND\n" +
                    "        left join MUNICIPIO mun on\n" +
                    "            ep.CHAVEMUN = mun.CHAVEMUN\n" +
                    "        left join cep on\n" +
                    "            ep.CHAVECEP = cep.CHAVECEP\n" +
                    "        where\n" +
                    "            endp.FUNCAO = 'C'\n" +
                    "     ) endc on\n" +
                    "        endc.CHAVEPES = p.CHAVEPES\n" +
                    "where\n" +
                    "    p.fornecedor = 'S'\n" +
                    "order by\n" +
                    "    p.CHAVEPES"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rst.getString("inscricaomunicipal"));
                    imp.setAtivo(!"N".equals(rst.getString("ativo")));
                    imp.setEndereco(rst.getString("LOGRADOURO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setIbge_municipio(rst.getInt("ibgemunicipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_numero(rst.getString("cob_numero"));
                    imp.setCob_complemento(rst.getString("cob_complemento"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_ibge_municipio(rst.getInt("cob_ibgemunicipio"));
                    imp.setCob_cep(rst.getString("cob_cep"));
                    imp.setValor_minimo_pedido(rst.getDouble("PEDIDOMINIMO"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));
                                        
                    addContatosFornecedor(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    private void addContatosFornecedor(FornecedorIMP imp) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.FUNCAO,\n" +
                    "    f.CNIAG ddd,\n" +
                    "    f.CAACNG telefone,\n" +
                    "    f.ORDEM\n" +
                    "from\n" +
                    "    FONEPESSOA f\n" +
                    "where\n" +
                    "    f.CHAVEPES = " + imp.getImportId() + "\n" +
                    "order by\n" +
                    "    f.ORDEM"
            )) {
                while (rst.next()) {
                    String desc;
                    switch (rst.getString("funcao")) {
                        case "C": desc = "COBRANCA"; break;
                        case "P": desc = "PRINCIPAL"; break;
                        default : desc = "TELEFONE";
                    }                    
                    imp.addTelefone(desc, String.format(
                            "%s%s",
                            rst.getString("ddd"),
                            rst.getString("telefone")
                    ));
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.FUNCAO,\n" +
                    "    f.email\n" +
                    "from\n" +
                    "    emailpes f\n" +
                    "where\n" +
                    "    f.CHAVEPES = " + imp.getImportId() + "\n" +
                    "order by\n" +
                    "    f.ORDEM"
            )) {
                int cont = 1;
                while (rst.next()) {
                    imp.addEmail("EMAIL " + cont, rst.getString("email"), TipoContato.COMERCIAL);
                    cont++;
                }
            }
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    p.CHAVEPES id,\n" +
                    "    p.NOME razao,\n" +
                    "    coalesce(jd.NOMEFANTASIA, fs.APELIDO) fantasia,\n" +
                    "    coalesce(jd.CNPJ, fs.CPF) cnpj,\n" +
                    "    coalesce(jd.IE, fs.RG) inscricaoestadual,\n" +
                    "    jd.IM inscricaomunicipal,\n" +
                    "    fs.ORGAO orgaoemissor,\n" +
                    "    coalesce(pesemp.ATIVO, 'S') ativo,\n" +
                    "    endp.LOGRADOURO,\n" +
                    "    endp.NUMERO,\n" +
                    "    endp.COMPLEMENTO,\n" +
                    "    endp.BAIRRO,\n" +
                    "    endp.ibgemunicipio,\n" +
                    "    endp.cep,\n" +
                    "    fs.ESTCIVIL estadocivil,\n" +
                    "    fs.NASCIMENTO datanascimento,\n" +
                    "    fs.SEXO,\n" +
                    "    fs.TRABALHO empresa,\n" +
                    "    fs.ADMISSAO dataadmissao,\n" +
                    "    fs.CARGO,\n" +
                    "    fs.SALARIO,\n" +
                    "    fs.CONJUGE,\n" +
                    "    p.CADCREDIARIO permitecreditorotativo,\n" +
                    "    p.CADCHEQUE permitecheque,\n" +
                    "    p.COMENTARIO observacao, \n" +
                    "    p.OBSERVACAO observacao2,\n" +
                    "    endc.LOGRADOURO cob_endereco,\n" +
                    "    endc.NUMERO cob_numero,\n" +
                    "    endc.COMPLEMENTO cob_complemento,\n" +
                    "    endc.BAIRRO cob_bairro,\n" +
                    "    endc.ibgemunicipio cob_ibgemunicipio,\n" +
                    "    endc.cep cob_cep,\n" +
                    "    p.CADASTRO datacadastro\n" +
                    "from\n" +
                    "    pessoa p\n" +
                    "    left join pesemp on\n" +
                    "        p.CHAVEPES = pesemp.CHAVEPES and\n" +
                    "        pesemp.CHAVEEMP = " + getLojaOrigem() + "\n" +
                    "    left join fisica fs on\n" +
                    "        p.CHAVEPES = fs.CHAVEPES\n" +
                    "    left join JURIDICA jd on\n" +
                    "        p.CHAVEPES = jd.CHAVEPES\n" +
                    "    left join (\n" +
                    "        select\n" +
                    "            endp.CHAVEPES,\n" +
                    "            ep.LOGRADOURO,\n" +
                    "            ep.NUMERO,\n" +
                    "            ep.COMPLEMENTO,\n" +
                    "            ep.BAIRRO,\n" +
                    "            mun.CODIBGE ibgemunicipio,\n" +
                    "            cep.CODIGO cep\n" +
                    "        from\n" +
                    "            ENDPESSOA endp\n" +
                    "        left join ENDERECO ep on\n" +
                    "            ep.CHAVEEND = endp.CHAVEEND\n" +
                    "        left join MUNICIPIO mun on\n" +
                    "            ep.CHAVEMUN = mun.CHAVEMUN\n" +
                    "        left join cep on\n" +
                    "            ep.CHAVECEP = cep.CHAVECEP\n" +
                    "        where\n" +
                    "            endp.FUNCAO = 'P'\n" +
                    "     ) endp on\n" +
                    "        endp.CHAVEPES = p.CHAVEPES\n" +
                    "     left join (\n" +
                    "        select\n" +
                    "            endp.CHAVEPES,\n" +
                    "            ep.LOGRADOURO,\n" +
                    "            ep.NUMERO,\n" +
                    "            ep.COMPLEMENTO,\n" +
                    "            ep.BAIRRO,\n" +
                    "            mun.CODIBGE ibgemunicipio,\n" +
                    "            cep.CODIGO cep\n" +
                    "        from\n" +
                    "            ENDPESSOA endp\n" +
                    "        left join ENDERECO ep on\n" +
                    "            ep.CHAVEEND = endp.CHAVEEND\n" +
                    "        left join MUNICIPIO mun on\n" +
                    "            ep.CHAVEMUN = mun.CHAVEMUN\n" +
                    "        left join cep on\n" +
                    "            ep.CHAVECEP = cep.CHAVECEP\n" +
                    "        where\n" +
                    "            endp.FUNCAO = 'C'\n" +
                    "     ) endc on\n" +
                    "        endc.CHAVEPES = p.CHAVEPES\n" +
                    "where\n" +
                    "    p.CLIENTE = 'S'\n" +
                    "order by\n" +
                    "    p.CHAVEPES"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rst.getString("inscricaomunicipal"));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setAtivo(!"N".equals(rst.getString("ativo")));
                    imp.setEndereco(rst.getString("LOGRADOURO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipioIBGE(rst.getString("ibgemunicipio"));
                    imp.setCep(rst.getString("cep"));
                    imp.setEstadoCivil(rst.getString("estadocivil"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setSexo(rst.getString("SEXO"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setDataAdmissao(rst.getDate("dataadmissao"));
                    imp.setCargo(rst.getString("CARGO"));
                    imp.setSalario(rst.getDouble("SALARIO"));
                    imp.setNomeConjuge(rst.getString("CONJUGE"));
                    imp.setPermiteCreditoRotativo("S".equals(rst.getString("permitecreditorotativo")));
                    imp.setPermiteCheque("S".equals(rst.getString("permitecheque")));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setObservacao2(rst.getString("observacao2"));
                    imp.setCobrancaEndereco(rst.getString("cob_endereco"));
                    imp.setCobrancaNumero(rst.getString("cob_numero"));
                    imp.setCobrancaComplemento(rst.getString("cob_complemento"));
                    imp.setBairro(rst.getString("cob_bairro"));
                    imp.setMunicipioIBGE(rst.getString("cob_ibgemunicipio"));
                    imp.setCobrancaCep(rst.getString("cob_cep"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    
                    addContatosCliente(imp);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
    private void addContatosCliente(ClienteIMP imp) throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.FUNCAO,\n" +
                    "    f.CNIAG ddd,\n" +
                    "    f.CAACNG telefone,\n" +
                    "    f.ORDEM\n" +
                    "from\n" +
                    "    FONEPESSOA f\n" +
                    "where\n" +
                    "    f.CHAVEPES = " + imp.getId() + "\n" +
                    "order by\n" +
                    "    f.ORDEM"
            )) {
                while (rst.next()) {
                    String desc;
                    switch (rst.getString("funcao")) {
                        case "C": desc = "COBRANCA"; break;
                        case "P": desc = "PRINCIPAL"; break;
                        default : desc = "TELEFONE";
                    }                    
                    imp.addTelefone(desc, String.format(
                            "%s%s",
                            rst.getString("ddd"),
                            rst.getString("telefone")
                    ));
                }
            }
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.FUNCAO,\n" +
                    "    f.email\n" +
                    "from\n" +
                    "    emailpes f\n" +
                    "where\n" +
                    "    f.CHAVEPES = " + imp.getId() + "\n" +
                    "order by\n" +
                    "    f.ORDEM"
            )) {
                int cont = 1;
                while (rst.next()) {
                    imp.addEmail("EMAIL " + cont, rst.getString("email"), TipoContato.COMERCIAL);
                    cont++;
                }
            }
        }
    }
    
}
