package aplicacao.db;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.common.collect.ImmutableMap;

public abstract class SqlServerUtils {

    public static final String ROWNUM_COLUMN = "_ROWNUM_";

    public static <S> String pagedQuery(String innerQuery, long offset, long maxResults) {

        final long firstRow = offset + 1;
        final long lastRow = firstRow + maxResults;

        final JtwigTemplate template = JtwigTemplate.inlineTemplate(""
            + " WITH query AS ("
            + "  SELECT inner_query.*,"
            + "      ROW_NUMBER() OVER() as {{ROWNUM_COLUMN}} "
            + "  FROM ({{innerQuery}}) inner_query "
            + " ) SELECT * "
            + " FROM query "
            + " WHERE {{ROWNUM_COLUMN}} >= {{firstRow}} AND {{ROWNUM_COLUMN}} < {{lastRow}} ");

        final JtwigModel model = JtwigModel.newModel(ImmutableMap.<String, Object> builder()
        //@formatter:off
            .put("ROWNUM_COLUMN", ROWNUM_COLUMN)
            .put("innerQuery"   , innerQuery   )
            .put("firstRow"     , firstRow     )
            .put("lastRow"      , lastRow      )
            //@formatter:on
            .build());

        return template.render(model);
    }
}
