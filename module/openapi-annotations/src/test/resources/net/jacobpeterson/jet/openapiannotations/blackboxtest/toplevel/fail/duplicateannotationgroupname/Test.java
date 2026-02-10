import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;

@OpenApi(
        annotationGroupName = "group"
)
public final class Test {

    @OpenApi(
            annotationGroupName = "group",
            info = @OpenApiInfo(title = "Title")
    )
    public void test() {}
}
