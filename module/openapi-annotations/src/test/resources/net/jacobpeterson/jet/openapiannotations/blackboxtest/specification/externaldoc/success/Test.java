import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.externaldoc.OpenApiExternalDoc;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(
        annotationGroupName = "group",
        level = NONE
)
@OpenApiExternalDoc(
        annotationGroupName = "group",
        url = "https://a.com"
)
public final class Test {}
