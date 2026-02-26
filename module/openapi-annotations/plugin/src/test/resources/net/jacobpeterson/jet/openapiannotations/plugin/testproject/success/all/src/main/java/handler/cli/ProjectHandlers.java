package handler.cli;

import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiHeader;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem.MethodEntry;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPaths;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiResponse;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiResponses;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.annotation.schemaname.SchemaName;
import org.jspecify.annotations.NullMarked;

import static handler.Main.AnnotationGroupName.CLI;
import static net.jacobpeterson.jet.common.http.method.Method.DELETE;
import static net.jacobpeterson.jet.common.http.status.Status.OK_200;

@NullMarked
@SchemaName("Project")
@OpenApi(annotationGroupName = CLI, paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
        key = "/status", value = @OpenApiPathItem(methods = @MethodEntry(
        keyEnum = DELETE, value = @OpenApiOperation(responses = @OpenApiResponses(@OpenApiResponse.MapEntry(
        keyEnum = OK_200,
        value = @OpenApiResponse(headers = @OpenApiHeader.MapEntry(
                keyEnum = Header.LOCATION,
                value = @OpenApiHeader(schema = @OpenApiHeader.Schema(
                        schema = @OpenApiSchema(fromClass = String.class)))
        ))
))))))))
public final class ProjectHandlers {

    public static final String TAG_NAME = "project";
}
