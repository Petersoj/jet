import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.contact.OpenApiContact;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiInfo(
        title = "Title",
        version = "1.0.0",
        contact = @OpenApiContact(
                name = "Name",
                url = "https://a.com"
        )
)
public final class Test {}
