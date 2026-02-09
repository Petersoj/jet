import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiInfo(title = "A")
@OpenApiInfo(title = "B")
public final class Test {}
