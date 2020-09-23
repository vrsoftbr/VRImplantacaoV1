package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class SolidoDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Solido";
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[] {
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM
                }
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	id_empresa_tributacao id,\n" +
                    "	nome descricao,\n" +
                    "	aliquota,\n" +
                    "	sit_tributaria cst,\n" +
                    "	reducao,\n" +
                    "	aliquota_reduzida\n" +
                    "from\n" +
                    "	empresa_tributacao")) {
                while(rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), 
                                                rs.getString("descricao"), 
                                                rs.getInt("cst"), 
                                                rs.getDouble("aliquota"), 
                                                rs.getDouble("reducao")));
                }
            }
        }
        return result;
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n" +
                    "	id_empresa id,\n" +
                    "	NOME_FANTASIA fantasia\n" +
                    "FROM\n" +
                    "	EMPRESA"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
                }
            }
        }
        return lojas;
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	id_produto_familia id,\n" +
                    "	nome descricao\n" +
                    "from\n" +
                    "	produto_familia"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	p.id_produto id,\n" +
                    "	ean.codigo_pdv ean,\n" +
                    "	p.produto descricaocompleta,\n" +
                    "	p.descricao_pdv,\n" +
                    "	p.data_cadastro,\n" +
                    "	p.id_produto_familia idfamilia,\n" +
                    "	pl.estoque,\n" +
                    "	pl.estoque_minimo,\n" +
                    "	pl.troca,\n" +
                    "	pl.preco_medio,\n" +
                    "	pl.preco_compra,\n" +
                    "	pl.preco_custo,\n" +
                    "	pl.preco_venda,\n" +
                    "	pl.preco_nota, \n" +
                    "	pl.margem,\n" +
                    "	p.embalagem,\n" +
                    "	p.unidade,\n" +
                    "	p.unidade_baixa,\n" +
                    "	p.peso_unidade,\n" +
                    "	p.validade,\n" +
                    "	p.produto_ativo,\n" +
                    "	pl.produto_liberado,\n" +
                    "	p.produto_ativo_compra,\n" +
                    "	p.produto_balanca,\n" +
                    "	p.ncm,\n" +
                    "	p.cest,\n" +
                    "	pe.codigo piscredito,\n" +
                    "	ps.codigo pisdebito,\n" +
                    "	pl.id_empresa_tributacao idaliquota,\n" +
                    "	p.peso_bruto,\n" +
                    "	p.peso_liquido \n" +
                    "from \n" +
                    "	produto p\n" +
                    "inner join produto_codigo_pdv ean on p.id_produto = ean.id_produto\n" +
                    "inner join produto_loja pl on p.id_produto = pl.id_produto\n" +
                    "left join pis_entrada pe on p.id_pis_entrada = pe.id_pis_entrada\n" +
                    "left join pis_saida ps on p.id_pis_saida = ps.id_pis_saida \n" +
                    "where \n" +
                    "	pl.id_empresa = " + getLojaOrigem())) {
                while(rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.seteBalanca("S".equals(rs.getString("produto_balanca")));
                    imp.setDescricaoCompleta(Utils.acertarTexto(rs.getString("descricaocompleta")));
                    imp.setDescricaoReduzida(Utils.acertarTexto(rs.getString("descricao_pdv")));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setPesoBruto(rs.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setPrecovenda(rs.getDouble("preco_venda"));
                    imp.setCustoComImposto(rs.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("unidade"));
                    imp.setQtdEmbalagem(rs.getInt("unidade_baixa"));
                    imp.setSituacaoCadastro("N".equals(rs.getString("produto_ativo"))
                            ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstCredito(rs.getString("piscredito"));
                    imp.setPiscofinsCstDebito(rs.getString("pisdebito"));
                    imp.setIcmsDebitoId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	f.id_fornecedor id,\n" +
                    "	f.nome,\n" +
                    "	f.fantasia,\n" +
                    "	f.endereco,\n" +
                    "	f.numero,\n" +
                    "	f.bairro,\n" +
                    "	f.cidade,\n" +
                    "	f.estado,\n" +
                    "	f.cep,\n" +
                    "	f.cnpj,\n" +
                    "	f.inscricao_estadual,\n" +
                    "	f.e_mail,\n" +
                    "	f.observacao,\n" +
                    "	f.fornecedor_ativo,\n" +
                    "	f.data_cadastro\n" +
                    "from \n" +
                    "	fornecedor f where f.empresa_id = " + getLojaOrigem())) {
                while(rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("inscricao_estadual"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo("S".equals(rs.getString("fornecedor_ativo")));
                    
                    String emailTelefone = rs.getString("e_mail");
                    
                    if(emailTelefone != null && !"".equals(emailTelefone)) {
                        imp.addContato("A", "EMAIL", null, null, TipoContato.NFE, emailTelefone);
                    }
                    
                    int i = 1;
                    try(ResultSet rs1 = stm.executeQuery(
                            "select\n" +
                            "	ft.id_fornecedor idfornecedor,\n" +
                            "	ft.ddd,\n" +
                            "	ft.telefone,\n" +
                            "	ft.contato,\n" +
                            "	ft.e_mail \n" +
                            "from\n" +
                            "	fornecedor_telefone where ft.id_fornecedor = " + imp.getImportId())) {
                        while(rs.next()) {
                            String contato = rs.getString("contato"),
                                    telefone = rs.getString("ddd") + rs.getString("telefone");
                            
                            if(contato == null && "".equals(contato)) {
                                contato = "SEM CONTATO";
                            }
                            
                            imp.addContato(String.valueOf(i), contato, telefone, null, TipoContato.NFE, rs.getString("e_mail"));
                            i++;
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select\n" +
                    "	pc.id_fornecedor,\n" +
                    "	pc.id_produto,\n" +
                    "	pc.id_produto_codfornecedor id,\n" +
                    "	pc.codigo_fornecedor,\n" +
                    "	coalesce(pc.unidade_baixa, 1) qtdembalagem\n" +
                    "from 	\n" +
                    "	produto_codfornecedor pc\n" +
                    "order by\n" +
                    "	id_fornecedor, id_produto")) {
                while(rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codigo_fornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtdembalagem"));
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try(Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try(ResultSet rs = stm.executeQuery(
                    "select \n" +
                    "	c.id_cliente id,\n" +
                    "	c.nome,\n" +
                    "	c.endereco,\n" +
                    "	c.bairro,\n" +
                    "	c.cidade,\n" +
                    "	c.estado,\n" +
                    "	c.numero,\n" +
                    "	c.cep,\n" +
                    "	c.rg,\n" +
                    "	c.cnpj_cpf,\n" +
                    "	c.inscricao_estadual,\n" +
                    "	c.data_cadastro,\n" +
                    "	c.data_nascimento,\n" +
                    "	c.e_mail,\n" +
                    "	c.nome_mae,\n" +
                    "	c.nome_pai,\n" +
                    "	c.salario,\n" +
                    "	c.limite_compra,\n" +
                    "	c.bloqueado,\n" +
                    "	c.cliente_ativo,\n" +
                    "	c.estado_civil,\n" +
                    "	c.id_cliente_grupo,\n" +
                    "	c.id_cliente_status \n" +
                    "from \n" +
                    "	cliente c")) {
                while(rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("nome")));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    String rg = rs.getString("rg"), ie = rs.getString("inscricao_estadual");
                    
                    imp.setInscricaoestadual(ie);
                    
                    if(rg != null && !"".equals(rg)) {
                        imp.setInscricaoestadual(rg);
                    }
                    
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setCep(rs.getString("cep"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setDataNascimento(rs.getDate("data_nascimento"));
                    imp.setEmail(rs.getString("e_mail"));
                    imp.setNomeMae(rs.getString("nome_mae"));
                    imp.setNomePai(rs.getString("nome_pai"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite_compra"));
                    imp.setAtivo("S".equals(rs.getString("cliente_ativo")));
                    
                    String estCivil = rs.getString("estado_civil");
                    
                    if(estCivil != null && !"".equals(estCivil)) {
                        switch(Utils.acertarTexto(estCivil.toUpperCase().trim())) {
                            case "CASADO": imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                                break;
                            case "AMASIADO": imp.setEstadoCivil(TipoEstadoCivil.AMAZIADO);
                                break;  
                            case "DIVORCIADO": imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                                break;    
                            case "VIUVO": imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                                break;    
                            default:
                                imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO); break;
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
}
