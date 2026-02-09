import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.externaldoc.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.tag.OpenApiTag;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiTag(
        externalDocs = {
                @OpenApiExternalDoc(url = "https://a.com"),
                @OpenApiExternalDoc(url = "https://b.com")
        }
)
public final class Test {}
