package com.eduhub.model.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Custom Hibernate UserType for PostgreSQL pgvector extension.
 * Maps between PostgreSQL's vector type and Java List<Double>.
 * 
 * This allows JPA to persist and retrieve vector embeddings seamlessly.
 */
public class VectorType implements UserType<List<Double>> {

    @Override
    public int getSqlType() {
        // Use OTHER for PostgreSQL pgvector compatibility
        return Types.OTHER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<Double>> returnedClass() {
        return (Class<List<Double>>) (Class<?>) List.class;
    }

    @Override
    public boolean equals(List<Double> x, List<Double> y) {
        if (x == y)
            return true;
        if (x == null || y == null)
            return false;
        return x.equals(y);
    }

    @Override
    public int hashCode(List<Double> x) {
        return x != null ? x.hashCode() : 0;
    }

    @Override
    public List<Double> nullSafeGet(ResultSet rs, int position,
            SharedSessionContractImplementor session,
            Object owner) throws SQLException {
        String value = rs.getString(position);
        if (value == null) {
            return null;
        }
        return parseVector(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, List<Double> value,
            int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            // Use setObject for PostgreSQL compatibility
            st.setObject(index, vectorToString(value), Types.OTHER);
        }
    }

    @Override
    public List<Double> deepCopy(List<Double> value) {
        return value != null ? new ArrayList<>(value) : null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(List<Double> value) {
        return (Serializable) deepCopy(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Double> assemble(Serializable cached, Object owner) {
        return deepCopy((List<Double>) cached);
    }

    /**
     * Parse PostgreSQL vector string format "[1.0, 2.0, 3.0]" to List<Double>
     */
    private List<Double> parseVector(String vectorString) {
        List<Double> result = new ArrayList<>();
        String cleaned = vectorString.replace("[", "").replace("]", "").trim();

        if (cleaned.isEmpty()) {
            return result;
        }

        String[] parts = cleaned.split(",");
        for (String part : parts) {
            result.add(Double.parseDouble(part.trim()));
        }
        return result;
    }

    /**
     * Convert List<Double> to PostgreSQL vector string format "[1.0, 2.0, 3.0]"
     */
    private String vectorToString(List<Double> vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.size(); i++) {
            sb.append(vector.get(i));
            if (i < vector.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
