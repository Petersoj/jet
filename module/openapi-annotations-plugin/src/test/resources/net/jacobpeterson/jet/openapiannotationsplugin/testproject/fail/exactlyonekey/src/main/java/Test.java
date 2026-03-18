import net.jacobpeterson.jet.openapiannotations.OpenApi;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.OpenApiPaths;
import org.jspecify.annotations.NullMarked;

import static net.jacobpeterson.jet.common.http.method.Method.DELETE;

@NullMarked
@OpenApi(paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
        key = "/test",
        value = @OpenApiPathItem(methods = @OpenApiPathItem.MethodEntry(
                key = "DELETE",
                keyEnum = DELETE
        ))
)))
public final class Test {}
