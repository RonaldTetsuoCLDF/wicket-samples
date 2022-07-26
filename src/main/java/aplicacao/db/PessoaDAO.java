package aplicacao.db;

import java.util.List;

import javax.inject.Inject;

import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.ImmutableMap;

import aplicacao.to.PessoaTO;

@Repository
public class PessoaDAO extends BaseDAO {

    @Inject
    private TransactionTemplate tx;

    public long countAll() {
        return jdbc().getJdbcOperations().queryForObject("SELECT count(id) FROM pessoa", Number.class).longValue();
    }
    public List<PessoaTO> selectAll(Order<String> order, long offset, long maxResults) {
        return jdbc().query(
            SqlServerUtils.pagedQuery(
                "SELECT id, nome FROM pessoa ORDER BY " + order.getOrderByWithDefault("id"),
                offset,
                maxResults),
            new BeanPropertyRowMapper<>(PessoaTO.class));
    }

    public PessoaTO select(Long id) {
        return jdbc().queryForObject("SELECT id, nome FROM pessoa WHERE id = :id",
            ImmutableMap.of("id", id),
            new BeanPropertyRowMapper<>(PessoaTO.class));
    }

    public PessoaTO insert(PessoaTO pessoa) {
        return tx.execute(ts -> {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc().update("INSERT INTO pessoa (nome) VALUES (:nome)",
                new BeanPropertySqlParameterSource(pessoa), keyHolder);
            pessoa.setId(keyHolder.getKey().longValue());
            return pessoa;
        });
    }

    public void delete(Long id) {
        tx.execute(ts -> {
            String sql = "DELETE FROM pessoa WHERE id = (:id)";
            int count = jdbc().update(sql, ImmutableMap.of("id", id));
            if (count != 1)
                throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(sql, 1, count);
            return count;
        });
    }

    public void update(PessoaTO pessoa) {
        tx.execute(ts -> {
            return jdbc().update("UPDATE pessoa SET nome=:nome WHERE ID = :id",
                new BeanPropertySqlParameterSource(pessoa));
        });
    }
}