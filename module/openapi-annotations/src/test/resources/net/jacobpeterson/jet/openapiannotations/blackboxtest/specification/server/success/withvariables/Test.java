import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.variable.OpenApiServerVariable;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiServer(
        name = "Name",
        url = "https://a.com/{variable1}/{variable2}",
        variables = {
                @OpenApiServerVariable(
                        name = "variable1",
                        enum_ = {"one", "two"},
                        default_ = "one"
                ),
                @OpenApiServerVariable(
                        name = "variable2",
                        default_ = "default",
                        description = "Description"
                )
        }
)
public final class Test {}
