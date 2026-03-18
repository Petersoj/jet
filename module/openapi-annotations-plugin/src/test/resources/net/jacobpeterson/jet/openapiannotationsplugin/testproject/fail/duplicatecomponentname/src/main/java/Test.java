import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.openapiannotations.OpenApi;
import net.jacobpeterson.jet.openapiannotations.OpenApiMediaType;
import net.jacobpeterson.jet.openapiannotations.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.OpenApiPaths;
import net.jacobpeterson.jet.openapiannotations.OpenApiResponse;
import net.jacobpeterson.jet.openapiannotations.OpenApiResponses;
import net.jacobpeterson.jet.openapiannotations.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.schemaname.SchemaName;
import org.jspecify.annotations.NullMarked;

import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_JSON_STRING;
import static net.jacobpeterson.jet.common.http.method.Method.GET;

@NullMarked
public final class Test {

    @SchemaName("A")
    public static final class A1 {

        public String a;
    }

    @SchemaName("A")
    public static final class A2 {

        public String b;
    }

    @OpenApi(paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
            key = "/test", value = @OpenApiPathItem(methods = @OpenApiPathItem.MethodEntry(
            keyEnum = GET, value = @OpenApiOperation(responses = @OpenApiResponses({
            @OpenApiResponse.MapEntry(
                    keyEnum = Status.OK_200,
                    value = @OpenApiResponse(content = @OpenApiMediaType.MapEntry(
                            key = APPLICATION_JSON_STRING,
                            value = @OpenApiMediaType(schema = @OpenApiSchema(fromClass = A1.class))
                    ))
            ),
            @OpenApiResponse.MapEntry(
                    keyEnum = Status.MULTI_STATUS_207,
                    value = @OpenApiResponse(content = @OpenApiMediaType.MapEntry(
                            key = APPLICATION_JSON_STRING,
                            value = @OpenApiMediaType(schema = @OpenApiSchema(fromClass = A2.class))
                    ))
            )}
    )))))))
    public void getTest() {}
}
