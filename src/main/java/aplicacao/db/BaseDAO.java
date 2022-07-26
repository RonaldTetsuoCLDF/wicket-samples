package aplicacao.db;

import javax.inject.Inject;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class BaseDAO {

    @Inject
    private NamedParameterJdbcTemplate jdbc;

    protected NamedParameterJdbcOperations jdbc() {
        return jdbc;
    }
}