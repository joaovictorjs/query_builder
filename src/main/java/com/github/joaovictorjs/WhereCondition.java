package com.github.joaovictorjs;

public class WhereCondition {
    private String column, connector, comparator;
    private String[] values;
    private boolean likeAllowed, emptyAllowed;

    public WhereCondition(String column, String[] values) {
        setColumn(column);
        setValues(values);
        setConnector("and");
        setComparator("=");
        setLikeAllowed(true);
        setEmptyAllowed(true);
    }

    public String getColumn() {
        return column;
    }

    public WhereCondition setColumn(String column) {
        if (column == null || column.isEmpty()) throw new RuntimeException("column cannot be null or empty");
        this.column = column;
        return this;
    }

    public String getConnector() {
        return connector;
    }

    public WhereCondition setConnector(String connector) {
        if (connector == null || connector.isEmpty()) throw new RuntimeException("connector cannot be null or empty");
        if (!connector.equals("or") && !connector.equals("and")) {
            throw new RuntimeException("invalid connector. You should use: or || and");
        };
        this.connector = connector;
        return this;
    }

    public String getComparator() {
        return comparator;
    }

    public WhereCondition setComparator(String comparator) {
        if (comparator == null || comparator.isEmpty())
            throw new RuntimeException("comparator cannot be null or empty");
        if (!comparator.equals("=")
                && !comparator.equals("!=")
                && !comparator.equals("<")
                && !comparator.equals(">")
                && !comparator.equals("<=")
                && !comparator.equals(">=")) {
            throw new RuntimeException("invalid comparator. You should use: = || != || < || > || <= || >=");
        }
        this.comparator = comparator;
        return this;
    }

    public String[] getValues() {
        return values;
    }

    public WhereCondition setValues(String[] values) {
        if (values == null) throw new RuntimeException("values cannot be null");
        for(String v : values) {
            if (v == null) throw new RuntimeException("null is not allowed in values array");
        }
        this.values = values;
        return this;
    }

    public boolean isLikeAllowed() {
        return likeAllowed;
    }

    public WhereCondition setLikeAllowed(boolean likeAllowed) {
        this.likeAllowed = likeAllowed;
        return this;
    }

    public boolean isEmptyAllowed() {
        return emptyAllowed;
    }

    public WhereCondition setEmptyAllowed(boolean emptyAllowed) {
        this.emptyAllowed = emptyAllowed;
        return this;
    }
}
