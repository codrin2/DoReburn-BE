package com.dubu.backend.todo.api.converter;

import com.dubu.backend.todo.api.dto.request.TodoRequestType;
import org.springframework.core.convert.converter.Converter;

public class StringToTodoRequestTypeConverter implements Converter<String, TodoRequestType> {
    @Override
    public TodoRequestType convert(String type) {
        return TodoRequestType.fromString(type);
    }
}
