import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiInfo(
        title = "Title",
        summary = "Summary",
        description = "Description",
        version = "1.0.0"
)
public final class Test {}
