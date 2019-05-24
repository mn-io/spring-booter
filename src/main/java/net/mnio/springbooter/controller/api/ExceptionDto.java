package net.mnio.springbooter.controller.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.mnio.springbooter.util.security.RandomUtil;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class ExceptionDto {

    private final String id;
    private final HttpStatus status;
    private final String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, Object> data;

    public ExceptionDto(final String name, final HttpStatus status) {
        this(name, status, null);
    }

    public ExceptionDto(final String name, final HttpStatus status, final Map<String, Object> data) {
        this.name = name;
        this.id = RandomUtil.generateExceptionId();
        this.status = status;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return String.format("%d %s", status.value(), status.getReasonPhrase());
    }

    public Map<String, Object> getData() {
        return data;
    }
}
