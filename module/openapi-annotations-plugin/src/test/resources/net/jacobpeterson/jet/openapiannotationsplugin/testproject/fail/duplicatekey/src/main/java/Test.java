import net.jacobpeterson.jet.openapiannotations.OpenApi;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.OpenApiPaths;
import org.jspecify.annotations.NullMarked;

@NullMarked
@OpenApi(paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
        key = "/test",
        value = @OpenApiPathItem(methods = {
                @OpenApiPathItem.MethodEntry(key = "DELETE"),
                @OpenApiPathItem.MethodEntry(key = "DELETE")
        })
)))
public final class Test {}
