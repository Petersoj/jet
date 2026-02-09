import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.externaldoc.OpenApiExternalDoc;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiExternalDoc(url = "https://a.com")
public final class Test {

    @OpenApiExternalDoc(url = "https://b.com")
    public void test() {}
}
