package com.suraj.qa.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

public class MaskedLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(MaskedLoggingFilter.class);

    // Headers whose values must always be redacted
    private static final Set<String> MASKED_HEADERS = Set.of(
            "authorization", "x-api-key", "x-client-secret", "cookie"
    );

    // JSON body fields whose values must be redacted
    private static final Set<String> MASKED_FIELDS = Set.of(
            "password", "ssn", "account_number", "routing_number", "pin"
    );

    private static final String REDACTED = "[REDACTED]";

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        logRequest(req);
        Response response = ctx.next(req, res);
        logResponse(response);
        return response;
    }

    private void logRequest(FilterableRequestSpecification req) {
        StringBuilder sb = new StringBuilder("\n>>> REQUEST\n");
        sb.append(req.getMethod()).append(" ").append(req.getURI()).append("\n");
        req.getHeaders().forEach(h -> {
            String val = MASKED_HEADERS.contains(h.getName().toLowerCase())
                    ? REDACTED : h.getValue();
            sb.append(h.getName()).append(": ").append(val).append("\n");
        });
        if (req.getBody() != null) {
            sb.append("Body: ").append(maskBody(req.getBody().toString()));
        }
        log.info(sb.toString());
    }

    private void logResponse(Response response) {
        log.info("\n<<< RESPONSE {} ({} ms)\n{}",
                response.statusCode(),
                response.time(),
                maskBody(response.asPrettyString()));
    }

    private String maskBody(String json) {
        if (json == null) return "";
        for (String field : MASKED_FIELDS) {
            json = json.replaceAll(
                    "(\\\"" + field + "\\\"\\s*:\\s*\\\")[^\\\"]*(\\\")",
                    "$1" + REDACTED + "$2");
        }
        return json;
    }
}