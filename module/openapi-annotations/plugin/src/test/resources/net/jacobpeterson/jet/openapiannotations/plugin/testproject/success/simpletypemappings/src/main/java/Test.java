import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiServer;
import org.jspecify.annotations.NullMarked;

import java.net.InetAddress;

@NullMarked
@OpenApi(
        info = @OpenApiInfo(
                title = "Test",
                version = "1.0.0"
        ),
        servers = @OpenApiServer(url = "https://a.com"),
        components = @OpenApiComponents(schemas = @OpenApiSchema.MapEntry(
                key = "Test",
                value = @OpenApiSchema(fromClass = InetAddress.class)
        ))
)
public final class Test {}
