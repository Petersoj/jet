package handler.account;

import com.google.gson.annotations.SerializedName;
import net.jacobpeterson.jet.common.http.status.Status;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApi;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiMediaType;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiOperation;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiParameter;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiParameter.Schema;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPathItem.MethodEntry;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiPaths;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiResponse;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiResponses;
import net.jacobpeterson.jet.openapiannotations.annotation.OpenApiSchema;
import net.jacobpeterson.jet.openapiannotations.annotation.schemaname.SchemaName;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static handler.Main.API_PATH;
import static handler.Main.AnnotationGroupName.WEB;
import static net.jacobpeterson.jet.common.http.header.contenttype.ContentType.APPLICATION_JSON_STRING;
import static net.jacobpeterson.jet.common.http.method.Method.ToString.DELETE;
import static net.jacobpeterson.jet.common.http.method.Method.ToString.GET;
import static net.jacobpeterson.jet.openapiannotations.annotation.OpenApiParameter.ParameterLocation.QUERY;

@NullMarked
@SchemaName("Account")
public final class AccountHandlers {

    public static final String TAG_NAME = "account";

    public static class GetInfo {

        public static final String METHOD = GET;
        public static final String PATH = API_PATH + "/" + TAG_NAME + "/info";
        public static final String QUERY_KEY_ID = "id";
        public static final int OK_STATUS_CODE = Status.Code.OK_200;
        public static final int EXCEPTION_STATUS_CODE = Status.Code.BAD_REQUEST_400;

        public static class Success {

            public String name;
            public String email;
            public @Nullable String profilePicture;
        }

        public static class Exception {

            public Reason reason;

            public enum Reason {

                NOT_LOGGED_IN,
                INVALID_ID
            }
        }
    }

    @OpenApi(annotationGroupName = WEB, paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
            key = GetInfo.PATH, value = @OpenApiPathItem(methods = @MethodEntry(
            key = GetInfo.METHOD, value = @OpenApiOperation(tags = TAG_NAME,
            parameters = @OpenApiParameter(
                    name = GetInfo.QUERY_KEY_ID,
                    in = QUERY,
                    schema = @Schema(schema = @OpenApiSchema(fromClass = String.class))
            ),
            responses = @OpenApiResponses({@OpenApiResponse.MapEntry(
                    keyInt = GetInfo.OK_STATUS_CODE,
                    value = @OpenApiResponse(content = @OpenApiMediaType.MapEntry(
                            key = APPLICATION_JSON_STRING,
                            value = @OpenApiMediaType(schema = @OpenApiSchema(fromClass = GetInfo.Success.class))
                    ))
            ), @OpenApiResponse.MapEntry(
                    keyInt = GetInfo.EXCEPTION_STATUS_CODE,
                    value = @OpenApiResponse(content = @OpenApiMediaType.MapEntry(
                            key = APPLICATION_JSON_STRING,
                            value = @OpenApiMediaType(schema = @OpenApiSchema(fromClass = GetInfo.Exception.class))
                    ))
            )})
    ))))))
    public void getInfo() {}

    public static class Delete {

        public static final String METHOD = DELETE;
        public static final String PATH = API_PATH + "/" + TAG_NAME;
        public static final String QUERY_KEY_ID = "id";
        public static final int OK_STATUS_CODE = Status.Code.OK_200;
        public static final int EXCEPTION_STATUS_CODE = Status.Code.BAD_REQUEST_400;

        public static class Exception {

            public Reason reason;

            public enum Reason {

                @SerializedName("NOT_LOGGED_IN_CUSTOM")
                NOT_LOGGED_IN,
                INVALID_ID
            }
        }
    }

    @OpenApi(annotationGroupName = WEB, paths = @OpenApiPaths(@OpenApiPathItem.MapEntry(
            key = Delete.PATH, value = @OpenApiPathItem(methods = @MethodEntry(
            key = Delete.METHOD, value = @OpenApiOperation(tags = TAG_NAME,
            parameters = @OpenApiParameter(
                    name = Delete.QUERY_KEY_ID,
                    in = QUERY,
                    schema = @Schema(schema = @OpenApiSchema(fromClass = String.class))
            ),
            responses = @OpenApiResponses({@OpenApiResponse.MapEntry(
                    keyInt = Delete.OK_STATUS_CODE,
                    value = @OpenApiResponse()
            ), @OpenApiResponse.MapEntry(
                    keyInt = Delete.EXCEPTION_STATUS_CODE,
                    value = @OpenApiResponse(content = @OpenApiMediaType.MapEntry(
                            key = APPLICATION_JSON_STRING,
                            value = @OpenApiMediaType(schema = @OpenApiSchema(fromClass = Delete.Exception.class))
                    ))
            )})
    ))))))
    public void delete() {}
}
