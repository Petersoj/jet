import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;

@OpenApi(
        info = {
                @OpenApiInfo(title = "Title1"),
                @OpenApiInfo(title = "Title2")
        }
)
public final class Test {}
