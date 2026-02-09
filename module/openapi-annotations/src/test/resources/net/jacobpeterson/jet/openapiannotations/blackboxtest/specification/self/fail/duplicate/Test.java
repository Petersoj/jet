import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.self.OpenApiSelf;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(
        annotationGroupName = "group",
        level = NONE
)
@OpenApiSelf(
        annotationGroupName = "group",
        value = "https://a.com"
)
public final class Test {

    @OpenApiSelf(
            annotationGroupName = "group",
            value = "https://a.com"
    )
    public void test() {}
}
