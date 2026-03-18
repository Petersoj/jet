package handler.cli;

import net.jacobpeterson.jet.common.http.header.Header;
import net.jacobpeterson.jet.openapiannotations.OpenApi;
import net.jacobpeterson.jet.openapiannotations.OpenApiHeader;
import net.jacobpeterson.jet.openapiannotations.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.OpenApiPathItem.MethodEntry;
import net.jacobpeterson.jet.openapiannotations.OpenApiPaths;
import net.jacobpeterson.jet.openapiannotations.OpenApiResponse;
import net.jacobpeterson.jet.openapiannotations.OpenApiResponses;
import net.jacobpeterson.jet.openapiannotations.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.schemaname.SchemaName;
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
