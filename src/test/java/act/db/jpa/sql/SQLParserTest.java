package act.db.jpa.sql;

/*-
 * #%L
 * ACT JPA Common Module
 * %%
 * Copyright (C) 2018 ActFramework
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static act.db.jpa.sql.SQL.Parser.parse;
import static act.db.jpa.sql.SQL.Type.FIND;
import static act.db.jpa.sql.SQL.Type.UPDATE;

import org.junit.BeforeClass;
import org.junit.Test;
import osgl.ut.TestBase;

public class SQLParserTest extends TestBase {

    private SQL target;

    @BeforeClass
    public static void staticPrepare() {
        Operator.values();
    }

    @Test
    public void testEmptyExpression() {
        parseSelect("");
        eq("SELECT User FROM User User");
    }

    @Test
    public void testOrderBy() {
        parseSelect("name,age order by name");
        eq("SELECT User FROM User User  WHERE User.name = ?1 AND User.age = ?2 ORDER BY User.name");
    }

    @Test
    public void testSingleField() {
        parseSelect("name");
        eq("SELECT User FROM User User  WHERE User.name = ?1");
        parseUpdate("name", "age");
        eq("UPDATE User User  SET  User.age = ?1 WHERE User.name = ?2");
    }

    @Test
    public void testMultipleFields() {
        parseSelect("name,age");
        eq("SELECT User FROM User User  WHERE User.name = ?1 AND User.age = ?2");
        parseUpdate("name", "age", "score");
        eq("UPDATE User User  SET  User.age = ?1,  User.score = ?2 WHERE User.name = ?3");
    }

    @Test
    public void testMultipleFieldsWithOp() {
        parseSelect("name like,age between,score <=,date gt");
        eq("SELECT User FROM User User  WHERE User.name LIKE ?1 AND User.age < ?2 AND User.age > ?3 AND User.score <= ?4 AND User.date > ?5");
        parseUpdate("name like,age between,score <=,date gt", "age", "score");
        eq("UPDATE User User  SET  User.age = ?1,  User.score = ?2 WHERE User.name LIKE ?3 AND User.age < ?4 AND User.age > ?5 AND User.score <= ?6 AND User.date > ?7");
    }

    @Test
    public void testRawJQL() {
        String raw = "select count(*) from User";
        parseSelect(raw);
        eq(raw);
    }

    private void parseSelect(String expression, String... columns) {
        target = parse(FIND, "User", expression, columns);
    }

    private void parseUpdate(String expression, String... columns) {
        target = parse(UPDATE, "User", expression, columns);
    }

    private void eq(String expected) {
        eq(expected, target.rawSql(DefaultSqlDialect.INSTANCE).trim());
    }
}
