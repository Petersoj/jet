import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.tag.OpenApiTag;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiTag(
        name = "Name"
)
@OpenApiTag(
        name = "Name",
        summary = "Summary"
)
public final class Test {}
