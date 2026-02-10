import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.variable.OpenApiServerVariable;

@OpenApi(
        servers = @OpenApiServer(
                variables = {
                        @OpenApiServerVariable(
                                name = "duplicate",
                                default_ = "default1"
                        ),
                        @OpenApiServerVariable(
                                name = "duplicate",
                                default_ = "default2"
                        )
                }
        )
)
public final class Test {}
