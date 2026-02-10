import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.component.OpenApiComponents;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.externaldoc.OpenApiExternalDoc;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.OpenApiInfo;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.contact.OpenApiContact;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.info.license.OpenApiLicense;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.securityrequirement.OpenApiSecurityRequirement;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.securityrequirement.OpenApiSecurityRequirements;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.OpenApiServer;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.server.variable.OpenApiServerVariable;
import net.jacobpeterson.jet.openapiannotations.annotation.specification.tag.OpenApiTag;

@OpenApi(
        $self = "https://a.com",
        info = @OpenApiInfo(
                title = "Title",
                version = "1.0.0",
                contact = @OpenApiContact(
                        name = "Name",
                        url = "https://a.com"
                )
        ),
        servers = @OpenApiServer(
                url = "https://a.com/{variable}",
                variables = @OpenApiServerVariable(
                        name = "variable",
                        default_ = "default"
                )
        ),
        components = @OpenApiComponents,
        security = {
                @OpenApiSecurityRequirements({
                        @OpenApiSecurityRequirement(name = "API_KEY_NAME"),
                        @OpenApiSecurityRequirement(name = "API_KEY_VALUE")
                })
        },
        tags = @OpenApiTag(
                name = "Name",
                externalDocs = @OpenApiExternalDoc(url = "https://a.com")
        ),
        externalDocs = @OpenApiExternalDoc(
                url = "https://a.com",
                description = "Description"
        )
)
@OpenApi(
        annotationGroupName = "group1",
        $self = "https://b.com",
        info = @OpenApiInfo(
                title = "Title",
                version = "1.0.0",
                license = @OpenApiLicense(name = "MIT")
        ),
        servers = @OpenApiServer(
                url = "https://a.com",
                name = "Name"
        ),
        components = @OpenApiComponents,
        tags = {
                @OpenApiTag(
                        name = "Tag1",
                        externalDocs = @OpenApiExternalDoc(url = "https://a.com")
                ),
                @OpenApiTag(name = "Tag2")
        }
)
public final class Test {

    @OpenApi(
            annotationGroupName = "group2",
            info = @OpenApiInfo(
                    title = "Title",
                    version = "1.0.0",
                    summary = "Summary"
            ),
            servers = @OpenApiServer(
                    url = "https://{variable1}.a.com/{variable2}",
                    variables = {
                            @OpenApiServerVariable(
                                    name = "variable1",
                                    default_ = "enum1",
                                    enum_ = {
                                            "enum1",
                                            "enum2"
                                    }
                            ),
                            @OpenApiServerVariable(
                                    name = "variable2",
                                    default_ = "variable2"
                            )
                    }
            ),
            components = @OpenApiComponents,
            security = {
                    @OpenApiSecurityRequirements({
                            @OpenApiSecurityRequirement(
                                    name = "API_KEY_NAME",
                                    scopes = {"scope1", "scope2"}
                            ),
                            @OpenApiSecurityRequirement(
                                    name = "API_KEY_VALUE",
                                    scopes = {"scope1", "scope2"}
                            )
                    }),
                    @OpenApiSecurityRequirements({
                            @OpenApiSecurityRequirement(name = "AUTH_KEY_NAME"),
                            @OpenApiSecurityRequirement(name = "AUTH_KEY_VALUE")
                    })
            }
    )
    public void test() {}
}
