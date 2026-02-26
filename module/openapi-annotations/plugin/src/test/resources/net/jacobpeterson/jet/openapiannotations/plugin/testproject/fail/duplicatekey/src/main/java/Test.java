import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPaths;
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
