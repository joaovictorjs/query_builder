import com.github.joaovictorjs.QueryBuilder;
import com.github.joaovictorjs.WhereCondition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueryBuilderTest {
    @Test
    public void selectTest() {
        String exp = "select * from users";
        String act = new QueryBuilder().build("users");
        Assertions.assertEquals(exp, act);

        exp = "select name, username, age from users";
        act = new QueryBuilder()
                .select("name")
                .select("username")
                .select("age")
                .build("users");
        Assertions.assertEquals(exp, act);

        act = new QueryBuilder()
                .select(new String[]{"name", "username", "age"})
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select name as n, username as u, age from users";
        act = new QueryBuilder()
                .select("name", "n")
                .select("username", "u")
                .select("age")
                .build("users");
        Assertions.assertEquals(exp, act);
    }

    @Test
    public void whereInTest() {
        String exp = "select * from users where name in ('joao', 'victor', 'js')";
        String act = new QueryBuilder()
                .whereIn(new WhereCondition("name", new String[]{"joao", "victor", "js"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name = ''";
        act = new QueryBuilder()
                .whereIn(new WhereCondition("name", new String[]{}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users";
        act = new QueryBuilder()
                .whereIn(new WhereCondition("name", new String[]{}).setEmptyAllowed(false))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name in ('joaovictorjs') and surname in ('joaovictorjs')";
        act = new QueryBuilder()
                .whereIn(new WhereCondition("name", new String[]{"joaovictorjs"}))
                .whereIn(new WhereCondition("surname", new String[]{"joaovictorjs"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name in ('joaovictorjs') or surname in ('joaovictorjs')";
        act = new QueryBuilder()
                .whereIn(new WhereCondition("name", new String[]{"joaovictorjs"}))
                .whereIn(new WhereCondition("surname", new String[]{"joaovictorjs"}).setConnector("or"))
                .build("users");
        Assertions.assertEquals(exp, act);
    }

    @Test
    public void whereOrTest() {
        String exp = "select * from users where name = 'joao' or name = 'victor' or name = 'js'";
        String act = new QueryBuilder()
                .whereOr(new WhereCondition("name", new String[]{"joao", "victor", "js"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name like '%joao%' or name like '%victor%' or name like '%js%'";
        act = new QueryBuilder()
                .whereOr(new WhereCondition("name", new String[]{"%joao%", "%victor%", "%js%"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name like '%joao%' or name = 'joaovictorjs'";
        act = new QueryBuilder()
                .whereOr(new WhereCondition("name", new String[]{"%joao%", "joaovictorjs"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users";
        act = new QueryBuilder().build("users");
        Assertions.assertEquals(exp, act);
    }

    @Test
    public void whereTest() {
        String exp = "select * from users where name in ('joao', 'victor', 'js')";
        String act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{"joao", "victor", "js"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name = ''";
        act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name in ('')";
        act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{"", "", ""}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name in ('joaovictorjs') or name like '%joaovictorjs%'";
        act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{"joaovictorjs", "%joaovictorjs%"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name like '%joao%' or name like '%victor%' or name like '%js%'";
        act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{"%joao%", "%victor%", "%js%"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name in ('joao', 'victor', 'js', '') or name like '%joao%' or name like '%victor%' or name like '%js%'";
        act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{"joao", "victor", "js", "%joao%", "%victor%", "%js%", ""}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where name in ('%joao%', '%victor%', '%js%')";
        act = new QueryBuilder()
                .where(new WhereCondition("name", new String[]{"%joao%", "%victor%", "%js%"}).setLikeAllowed(false))
                .build("users");
        Assertions.assertEquals(exp, act);
    }

    @Test
    public void whereBetweenDate() {
        String exp = "select * from users where birth between to_date('01/01/2000', 'dd/MM/yyyy') and to_date('31/12/2077', 'dd/MM/yyyy')";
        String act = new QueryBuilder()
                .whereBetweenDate("", new WhereCondition("birth", new String[]{"01/01/2000", "31/12/2077"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where birth between to_date('01/01/2000', 'dd/MM/yyyy') and to_date('01/01/2000', 'dd/MM/yyyy')+1";
        act = new QueryBuilder()
                .whereBetweenDate("", new WhereCondition("birth", new String[]{"01/01/2000"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where birth between to_date('01/01/2000', 'dd/MM/yyyy') and to_date('01/01/2000', 'dd/MM/yyyy')+1";
        act = new QueryBuilder()
                .whereBetweenDate("", new WhereCondition("birth", new String[]{"01/01/2000", ""}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users where birth between to_date('01/01/2000', 'dd/MM/yyyy')-1 and to_date('01/01/2000', 'dd/MM/yyyy')";
        act = new QueryBuilder()
                .whereBetweenDate("", new WhereCondition("birth", new String[]{"", "01/01/2000"}))
                .build("users");
        Assertions.assertEquals(exp, act);

        exp = "select * from users";
        act = new QueryBuilder()
                .whereBetweenDate("", new WhereCondition("birth", new String[]{"", ""}))
                .build("users");
        Assertions.assertEquals(exp, act);
    }

    @Test
    public void generalTest() {
        String exp = "select name as name, username as username from users " +
                "where name in ('joao', 'victor') or name like '%joao%' and " +
                "birth between to_date('01/01/2000', 'dd/MM/yyyy') and to_date('31/12/2077', 'dd/MM/yyyy')";

        String act = new QueryBuilder()
                .select("name", "name")
                .select("username", "username")
                .where(new WhereCondition("name", new String[]{"joao", "victor", "%joao%"}))
                .whereBetweenDate("", new WhereCondition("birth", new String[]{"01/01/2000", "31/12/2077"}))
                .build("users");

        Assertions.assertEquals(exp, act);
    }
}
