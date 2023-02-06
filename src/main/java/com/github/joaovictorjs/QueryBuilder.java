package com.github.joaovictorjs;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    private StringBuilder selections, whereClause;

    public QueryBuilder() {
        selections = new StringBuilder();
        whereClause = new StringBuilder();
    }

    public String build(String table) {
        if (table == null || table.isEmpty()) throw new RuntimeException("table cannot be null or empty");
        if (selections.toString().isEmpty()) selections.append("select *");
        return (selections + " from " + table + " " + whereClause).trim();
    }

    public QueryBuilder select(String column, String value) {
        String act = selections.toString();

        if (column == null || column.isEmpty()) throw new RuntimeException("column cannot be null or empty");

        if (act.startsWith("select")) {
            selections.append(",");
        } else {
            selections = new StringBuilder().append("select");
        }

        selections.append(" ").append(column);

        if (value != null && !value.trim().isEmpty()) selections.append(" as ").append(value);
        return this;
    }

    public QueryBuilder select(String column) {
        return select(column, null);
    }

    public QueryBuilder select(String[] columns) {
        if (columns == null || columns.length == 0) throw new RuntimeException("columns cannot be null or empty");
        for (String column : columns) select(column);
        return this;
    }

    private void whereClauseResolver(String connector) {
        if (whereClause.toString().startsWith("where")) {
            if (!whereClause.toString().endsWith("where") && !whereClause.toString().endsWith(connector)) whereClause.append(" ").append(connector);
        } else {
            whereClause.append("where");
        }
    }

    private String argResolver(String value, boolean likeAllowed) {
        if (value.startsWith("%") || value.endsWith("%") && likeAllowed) {
            return "like '" + value + "'";
        }
        return "= '" + value + "'";
    }

    public QueryBuilder whereIn(WhereCondition condition) {
        if (condition.getValues().length == 0) {
            if (condition.isEmptyAllowed()) {
                whereClauseResolver(condition.getConnector());
                whereClause.append(" ").append(condition.getColumn()).append(" = ''");
            }
            return this;
        }

        whereClauseResolver(condition.getConnector());
        List<String> added = new ArrayList<>();
        StringBuilder gen = new StringBuilder();
        for (String v : condition.getValues()) {
            if (added.contains(v)) continue;
            if (v.trim().isEmpty() && !condition.isEmptyAllowed()) continue;
            if (gen.toString().endsWith("'")) gen.append(", ");

            gen.append("'").append(v).append("'");
            added.add(v);
        }
        whereClause.append(" ").append(condition.getColumn()).append(" in (").append(gen).append(")");
        return this;
    }

    public QueryBuilder whereOr(WhereCondition condition) {
        if (condition.getValues().length == 0) {
            if (condition.isEmptyAllowed()) {
                whereClauseResolver(condition.getConnector());
                whereClause.append(" ").append(condition.getColumn()).append(" = ''");
            }
            return this;
        }

        whereClauseResolver(condition.getConnector());
        List<String> added = new ArrayList<>();
        StringBuilder gen = new StringBuilder();
        for (String v : condition.getValues()) {
            if (added.contains(v)) continue;
            if (v.trim().isEmpty() && !condition.isEmptyAllowed()) continue;
            if (gen.toString().endsWith("'")) gen.append(" or ");

            gen.append(condition.getColumn()).append(" ").append(argResolver(v, condition.isLikeAllowed()));
            added.add(v);
        }
        whereClause.append(" ").append(gen);
        return this;
    }

    public QueryBuilder where(WhereCondition condition) {
        if (condition.getValues().length == 0) {
            if (condition.isEmptyAllowed()) {
                whereClauseResolver(condition.getConnector());
                whereClause.append(" ").append(condition.getColumn()).append(" = ''");
            }
            return this;
        }

        whereClauseResolver(condition.getConnector());
        List<String> strWithMod = new ArrayList<>();
        List<String> strNoMod = new ArrayList<>();
        for (String v : condition.getValues()) {
            if (v.trim().isEmpty() & !condition.isEmptyAllowed()) continue;

            if (v.startsWith("%") && condition.isLikeAllowed() || v.endsWith("%") && condition.isLikeAllowed()) {
                strWithMod.add(v);
            } else {
                strNoMod.add(v);
            }
        }

        String connector = "and";

        if (!strNoMod.isEmpty()) {
            whereIn(new WhereCondition(condition.getColumn(), strNoMod.toArray(new String[0])));
        }

        if (condition.isLikeAllowed() && !strWithMod.isEmpty()) {
            if (!strNoMod.isEmpty()) connector = "or";
            whereOr(new WhereCondition(condition.getColumn(), strWithMod.toArray(new String[0])).setConnector(connector));
        }

        return this;
    }

    private String getDateSyntax(String format, String value){
        return "to_date('" + value + "', '" + format + "')";
    }

    public QueryBuilder whereBetweenDate(String format, WhereCondition condition) {
        if (format.trim().isEmpty()) {
            format = "dd/MM/yyyy";
        }

        if (condition.getValues().length == 0) {
            if (condition.isEmptyAllowed()) {
                whereClauseResolver(condition.getConnector());
                whereClause.append(" ").append(condition.getColumn()).append(" = ''");
            }
            return this;
        }

        StringBuilder gen = new StringBuilder();

        String init = "", end = "";

        if (condition.getValues().length == 1) {
            init = getDateSyntax(format, condition.getValues()[0]);
            end = init + "+1";
        }else{
            String v1 = condition.getValues()[0].trim();
            String v2 = condition.getValues()[1].trim();


            if (!v1.isEmpty() && v2.isEmpty()){
                init = getDateSyntax(format, condition.getValues()[0]);
                end = init + "+1";
            }

            if (v1.isEmpty() && !v2.isEmpty()){
                end = getDateSyntax(format, v2);
                init = end + "-1";
            }

            if (!v1.isEmpty() && !v2.isEmpty()){
                init = getDateSyntax(format, v1);
                end = getDateSyntax(format, v2);
            }

            if (v1.isEmpty() && v2.isEmpty()) return this;
        }

        whereClauseResolver(condition.getConnector());

        gen.append(condition.getColumn()).append(" between ").append(init).append(" and ").append(end);
        whereClause.append(" ").append(gen);
        return this;
    }
}