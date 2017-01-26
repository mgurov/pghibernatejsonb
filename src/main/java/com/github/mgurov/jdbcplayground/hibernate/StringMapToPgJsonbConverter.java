package com.github.mgurov.jdbcplayground.hibernate;

import com.github.mgurov.jdbcplayground.Throwables;
import com.google.gson.Gson;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import java.sql.SQLException;
import java.util.Map;

public class StringMapToPgJsonbConverter implements AttributeConverter<Map, Object> {

    @Override
    public Object convertToDatabaseColumn(Map map) {
        PGobject jsonB = new PGobject();//
        try {
            jsonB.setValue(gson.toJson(map));
        } catch (SQLException e) {
            throw Throwables.propagate(e);
        }
        jsonB.setType("jsonb");

        return jsonB;
    }

    @Override
    public Map convertToEntityAttribute(Object o) {
        return gson.fromJson(o.toString(), Map.class);
    }

    private final Gson gson = new Gson();
}
