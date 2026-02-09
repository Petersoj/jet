import net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.OpenApiAnnotationsValidation;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.jsonschemadialect.OpenApiJsonSchemaDialect;

import static net.jacobpeterson.jet.openapiannotations.annotation.meta.annotationsvalidation.AnnotationsValidationLevel.NONE;

@OpenApiAnnotationsValidation(level = NONE)
@OpenApiJsonSchemaDialect("https://a.com")
public final class Test {

    @OpenApiJsonSchemaDialect("https://a.com")
    public void test() {}
}
