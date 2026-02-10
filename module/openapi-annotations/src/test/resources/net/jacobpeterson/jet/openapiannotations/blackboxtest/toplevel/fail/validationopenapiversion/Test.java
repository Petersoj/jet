import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.component.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.OpenApiServer;

@OpenApi(
        openapi = "a",
        info = @OpenApiInfo(
                title = "Title",
                version = "1.0.0"
        ),
        servers = @OpenApiServer(
                url = "https://a.com"
        ),
        components = @OpenApiComponents
)
public final class Test {}
