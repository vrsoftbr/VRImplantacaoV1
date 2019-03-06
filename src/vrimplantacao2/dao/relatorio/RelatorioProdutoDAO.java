package vrimplantacao2.dao.relatorio;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.remote.ItemComboVO;
import vrimplantacao2.vo.relatorio.ProdutoRelatorioVO;

/**
 *
 * @author Importacao
 */
public class RelatorioProdutoDAO {

    public List<ProdutoRelatorioVO> getPlanilhaProduto(int idLoja) throws Exception {
        List<ProdutoRelatorioVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n"
                    + "declare\n"
                    + "	pid_loja integer = " + idLoja + ";\n"
                    + "	sql text;\n"
                    + "begin \n"
                    + "if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'planilhaproduto') then\n"      
                    + "	create temp table tp_lj on commit drop as (select loja.id, f.id_estado from loja join fornecedor f on loja.id_fornecedor = f.id where loja.id = " + idLoja + ");\n"
                    + "	create temp table tp_merc on commit drop as (\n"
                    + "		select\n"
                    + "			m.mercadologico1 cod_mercadologico1,\n"
                    + "			(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and nivel = 1) mercadologico1,\n"
                    + "			m.mercadologico2 cod_mercadologico2,\n"
                    + "			(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and nivel = 2) mercadologico2,\n"
                    + "			m.mercadologico3 cod_mercadologico3,\n"
                    + "			(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and nivel = 3) mercadologico3,\n"
                    + "			m.mercadologico4 cod_mercadologico4,\n"
                    + "			(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and nivel = 4) mercadologico4,\n"
                    + "			m.mercadologico5 cod_mercadologico5,\n"
                    + "			(select descricao from mercadologico where mercadologico1 = m.mercadologico1 and mercadologico2 = m.mercadologico2 and mercadologico3 = m.mercadologico3 and mercadologico4 = m.mercadologico4 and mercadologico5 = m.mercadologico5 and nivel = 5) mercadologico5\n"
                    + "		from\n"
                    + "			mercadologico m\n"
                    + "		where \n"
                    + "			nivel = (select valor::integer from public.parametrovalor where id_loja = " + idLoja + " and id_parametro = 1)\n"
                    + "	);\n"
                    + "	\n"
                    + "	sql = '\n"
                    + "select\n"
                    + "	p.id,\n"
                    + "	p.qtdembalagem qtdembalagemcotacao,\n"
                    + "	un.descricao unidadecotacao,\n"
                    + "	(\n"
                    + "		select \n"
                    + "			string_agg(''ean: ''||ean.codigobarras::varchar||'' emb: ''||un.descricao||'' qtdemb: ''||ean.qtdembalagem ,''|'') \n"
                    + "		from \n"
                    + "			produtoautomacao ean\n"
                    + "			join tipoembalagem un on ean.id_tipoembalagem = un.id\n"
                    + "		where \n"
                    + "			ean.id_produto = p.id\n"
                    + "	)::text eans,\n"
                    + "	case \n"
                    + "	when p.id_tipoembalagem = 4 then ''PESAVEL'' \n"
                    + "	when p.id_tipoembalagem = 0 and pesavel then ''UNI. PESAVEL''\n"
                    + "	else ''UNI'' end pesavel,\n"
                    + "	p.validade,\n"
                    + "	p.descricaocompleta,\n"
                    + "	p.descricaoreduzida,\n"
                    + "	merc.*,\n"
                    + "	p.id_familiaproduto,\n"
                    + "	fam.descricao familiaproduto,\n"
                    + "   to_char(p.pesobruto, ''999999990D00'') pesobruto,\n"
                    + "	to_char(p.pesoliquido, ''999999990D00'') pesoliquido,\n"
                    + "	to_char(vend.estoquemaximo, ''999999990D00'') estoquemaximo,\n"
                    + "	to_char(vend.estoqueminimo, ''999999990D00'') estoqueminimo,\n"
                    + "	to_char(vend.estoque, ''999999990D00'') estoque,\n"
                    + "	to_char(vend.custosemimposto, ''999999990D00'') custosemimposto,\n"
                    + "	to_char(vend.custocomimposto, ''999999990D00'') custocomimposto,\n"
                    + "	to_char(vend.precovenda, ''999999990D00'') precovenda,\n"
                    + "	case vend.id_situacaocadastro when 1 then ''S'' else ''N'' end as ativo,\n"
                    + "	case when vend.descontinuado then ''S'' else ''N'' end as descontinuado,\n"
                    + "	lpad(p.ncm1::varchar,4,''0'') || lpad(p.ncm2::varchar,2,''0'') || lpad(p.ncm3::varchar,2,''0'') ncm,\n"
                    + "	lpad(cest.cest1::varchar,2,''0'') || lpad(cest.cest2::varchar,3,''0'') || lpad(cest.cest3::varchar,2,''0'') cest,\n"
                    + "	to_char(piscofdeb.cst,''00'') piscofins_cst_debito,\n"
                    + "	to_char(piscofcred.cst,''00'') piscofins_cst_credito,\n"
                    + "	p.tiponaturezareceita piscofins_natureza_receita,\n"
                    + "	to_char(icms.situacaotributaria,''000D'')||to_char(icms.porcentagem, ''990D00'')||to_char(icms.reduzido, ''990D00'') icms_debito,\n"
                    + "	to_char(icmse.situacaotributaria,''000D'')||to_char(icmse.porcentagem, ''990D00'')||to_char(icmse.reduzido, ''990D00'') icms_credito,\n"
                    + "	to_char(icmssf.situacaotributaria,''000D'')||to_char(icmssf.porcentagem, ''990D00'')||to_char(icmssf.reduzido, ''990D00'') icms_debito_fora,\n"
                    + "	to_char(icmsef.situacaotributaria,''000D'')||to_char(icmsef.porcentagem, ''990D00'')||to_char(icmsef.reduzido, ''990D00'') icms_credito_fora,\n"
                    + "	to_char(icmscf.situacaotributaria,''000D'')||to_char(icmscf.porcentagem, ''990D00'')||to_char(icmscf.reduzido, ''990D00'') icms_consumidor\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	join tp_lj lj on true\n"
                    + "	left join tipoembalagem un on un.id = p.id_tipoembalagem\n"
                    + "	left join tp_merc merc on\n"
                    + "		merc.cod_mercadologico1 = p.mercadologico1 and\n"
                    + "		merc.cod_mercadologico2 = p.mercadologico2 and\n"
                    + "		merc.cod_mercadologico3 = p.mercadologico3 and\n"
                    + "		merc.cod_mercadologico4 = p.mercadologico4 and\n"
                    + "		merc.cod_mercadologico5 = p.mercadologico5\n"
                    + "	left join familiaproduto fam on p.id_familiaproduto = fam.id\n"
                    + "	join produtocomplemento vend on p.id = vend.id_produto and vend.id_loja = lj.id\n"
                    + "	left join cest on cest.id = p.id_cest\n"
                    + "	left join tipopiscofins piscofcred on \n"
                    + "		p.id_tipopiscofinscredito = piscofcred.id\n"
                    + "	left join tipopiscofins piscofdeb on \n"
                    + "		p.id_tipopiscofins = piscofdeb.id\n"
                    + "	join produtoaliquota aliq on p.id = aliq.id_produto and aliq.id_estado = lj.id_estado\n"
                    + "	join aliquota icms on icms.id = aliq.id_aliquotadebito\n"
                    + "	join aliquota icmse on icmse.id = aliq.id_aliquotacredito\n"
                    + "	join aliquota icmssf on icmssf.id = aliq.id_aliquotadebitoforaestado\n"
                    + "	join aliquota icmsef on icmsef.id = aliq.id_aliquotacreditoforaestado\n"
                    + "	join aliquota icmscf on icmscf.id = aliq.id_aliquotaconsumidor\n"
                    + "--&WHERE&';\n"
                    + "	\n"
                    + "	execute 'create table if not exists implantacao.planilhaproduto as ('||replace(sql,'--&WHERE&','	where false)');\n"
                    + "	\n"
                    + "	execute 'insert into implantacao.planilhaproduto '||replace(sql,'--&WHERE&','where p.id_tipoembalagem = 4 order by random() limit 10');\n"
                    + "	execute 'insert into implantacao.planilhaproduto '||replace(sql,'--&WHERE&','where p.id_tipoembalagem = 0 and pesavel order by random() limit 10');\n"
                    + "	execute 'insert into implantacao.planilhaproduto '||replace(sql,'--&WHERE&','where p.mercadologico1 in (select mercadologico1 from mercadologico where nivel=1 order by random() limit 10) and not p.id in (select id from implantacao.planilhaproduto) order by random() limit 20');\n"
                    + "end if;\n"        
                    + "end;\n"
                    + "$$;");
            try (ResultSet rs = stm.executeQuery(
                    "select * from implantacao.planilhaproduto order by descricaocompleta")) {
                while (rs.next()) {
                    ProdutoRelatorioVO vo = new ProdutoRelatorioVO();
                    vo.setId(rs.getInt("id"));
                    vo.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    vo.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    vo.setEan(rs.getString("eans"));
                    vo.setBalanca(rs.getString("pesavel"));
                    vo.setValidade(rs.getInt("validade"));
                    vo.setCodMerc1(rs.getInt("cod_mercadologico1"));
                    vo.setMerc1(rs.getString("mercadologico1"));
                    vo.setCodMerc2(rs.getInt("cod_mercadologico2"));
                    vo.setMerc2(rs.getString("mercadologico2"));
                    vo.setCodMerc3(rs.getInt("cod_mercadologico3"));
                    vo.setMerc3(rs.getString("mercadologico3"));
                    vo.setCodMerc4(rs.getInt("cod_mercadologico4"));
                    vo.setMerc4(rs.getString("mercadologico4"));
                    vo.setCodMerc5(rs.getInt("cod_mercadologico5"));
                    vo.setMerc5(rs.getString("mercadologico5"));
                    vo.setIdFamiliaProduto(rs.getInt("id_familiaproduto"));
                    vo.setFamiliaProduto(rs.getString("familiaproduto"));
                    vo.setPesoBruto(rs.getString("pesobruto"));
                    vo.setPesoLiquido(rs.getString("pesoliquido"));
                    vo.setEstoque(rs.getString("estoque"));
                    vo.setEstoqueMax(rs.getString("estoquemaximo"));
                    vo.setEstoqueMin(rs.getString("estoqueminimo"));
                    vo.setPrecoVenda(rs.getString("precovenda"));
                    vo.setCustoComImposto(rs.getString("custocomimposto"));
                    vo.setCustoSemImposto(rs.getString("custosemimposto"));
                    vo.setAtivo(rs.getString("ativo"));
                    vo.setDescontinuado(rs.getString("descontinuado"));
                    vo.setNcm(rs.getString("ncm"));
                    vo.setCest(rs.getString("cest"));
                    vo.setPisCofinsDebito(rs.getString("piscofins_cst_debito"));
                    vo.setPisCofinsCredito(rs.getString("piscofins_cst_credito"));
                    vo.setPisCofinsNaturezaReceita(rs.getString("piscofins_natureza_receita"));
                    vo.setIcmsAliquotaDebito(rs.getString("icms_debito"));
                    vo.setIcmsAliquotaCredito(rs.getString("icms_credito"));
                    vo.setIcmsAliquotaConsumidor(rs.getString("icms_consumidor"));

                    result.add(vo);
                }
            }

        }
        return result;
    }

    public List<ItemComboVO> getEmbalagem() throws Exception {
        List<ItemComboVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id,\n"
                    + "	descricao \n"
                    + "from \n"
                    + "	tipoembalagem \n"
                    + "order by 1")) {
                while (rs.next()) {
                    result.add(new ItemComboVO(rs.getInt("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }
    
    public void dropTable() throws Exception {
        try(Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "do $$\n" +
                    "begin\n" +
                    "	if exists (select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'planilhaproduto') then\n" +
                    "	execute 'drop table implantacao.planilhaproduto';\n" +
                    "end if;\n" +
                    "end;\n" +
                    "$$");
        }
    }
}
