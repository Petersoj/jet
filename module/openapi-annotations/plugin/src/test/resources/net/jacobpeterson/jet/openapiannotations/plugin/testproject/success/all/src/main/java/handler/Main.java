package handler;

import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSecurityRequirement;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiServerVariable;
import org.jspecify.annotations.NullMarked;

@OpenApi(
        annotationGroupName = Main.AnnotationGroupName.WEB,
        info = @OpenApiInfo(
                title = "Web",
                version = "1.0.0"
        ),
        servers = @OpenApiServer(
                url = "https://a.com/{type}",
                variables = {
                        @OpenApiServerVariable.MapEntry(
                                key = "type",
                                value = @OpenApiServerVariable(
                                        default_ = "PRODUCTION",
                                        enum_ = {"PRODUCTION", "DEVELOPMENT"}
                                )
                        )
                }
        ),
        externalDocs = @OpenApiExternalDoc(url = "https://a.com/external-docs"),
        rawJson = """
                {
                  "x-readme": {
                    "some-key": "some-value"
                  }
                }
                """
)
@OpenApi(
        annotationGroupName = Main.AnnotationGroupName.CLI,
        info = @OpenApiInfo(
                title = "CLI",
                version = "1.0.0"
        ),
        servers = @OpenApiServer(url = "https://a.com"),
        security = {
                @OpenApiSecurityRequirement({
                        @OpenApiSecurityRequirement.Entry(key = "API_KEY_NAME"),
                        @OpenApiSecurityRequirement.Entry(key = "API_KEY_VALUE")
                }),
                @OpenApiSecurityRequirement(rawJson = """
                        {
                          "RAW_JSON": []
                        }
                        """)
        }
)
@NullMarked
public final class Main {

    public static final class AnnotationGroupName {

        public static final String WEB = "web";
        public static final String CLI = "cli";
    }

    public static final String API_PATH = "/api";
}
