package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.toolisticon.cute.Cute;
import io.toolisticon.cute.CuteApi.BlackBoxTestOutcomeInterface;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NullMarked
public final class OpenApiAnnotationsProcessorTest {

    private static final Gson GSON = new GsonBuilder().create();

    @Test
    public void processValidationLevelSuccessNone() {
        blackBoxTestSuccess("validationlevel/success/none/");
    }

    @Test
    public void processValidationLevelSuccessWarning() {
        blackBoxTestSuccess("validationlevel/success/warning/");
    }

    @Test
    public void processValidationLevelFail() {
        blackBoxTestFail("validationlevel/fail/", "required property 'info' not found");
    }

    @Test
    public void processTopLevelSuccessRequired() {
        blackBoxTestSuccess("toplevel/success/required/");
    }

    @Test
    public void processTopLevelSuccessMultiple() {
        blackBoxTestSuccess("toplevel/success/multiple/");
    }

    @Test
    public void processTopLevelFailDuplicateAnnotation() {
        blackBoxTestFail("toplevel/fail/duplicateannotation", "Duplicate");
    }

    @Test
    public void processTopLevelFailDuplicateAnnotations() {
        blackBoxTestFail("toplevel/fail/duplicateannotations", "Duplicate");
    }

    @Test
    public void processTopLevelFailDuplicateAnnotationGroupName() {
        blackBoxTestFail("toplevel/fail/duplicateannotationgroupname", "Duplicate");
    }

    @Test
    public void processTopLevelFailCustomScheme() {
        blackBoxTestFail("toplevel/fail/customschema", "`@OpenApi.annotationsValidationLevel` is set to `ERROR`, " +
                "but validation for custom `@OpenApi.$schema` of `https://a.com` is unsupported");
    }

    @Test
    public void processTopLevelFailAnnotationArrayIsMapKey() {
        blackBoxTestFail("toplevel/fail/annotationarrayismapkey",
                "`@OpenApiServer.variables` duplicate `@OpenApiServerVariable.name`: duplicate");
    }

    @Test
    public void processTopLevelFailAnnotationArrayIsNullableValue() {
        blackBoxTestFail("toplevel/fail/annotationarrayisnullablevalue",
                "but the array contains more than one element");
    }

    @Test
    public void processTopLevelFailValidationOpenApiVersion() {
        blackBoxTestFail("toplevel/fail/validationopenapiversion", "/openapi: does not match the regex pattern");
    }

    private void blackBoxTestSuccess(final String relativeTestDirectoryPath) {
        final var blackBoxTestDirectoryPath = getBlackBoxTestDirectoryPath(relativeTestDirectoryPath);
        final var compilationSucceeds = newBlackBoxTest(blackBoxTestDirectoryPath).compilationSucceeds();
        final File relativeTestDirectory;
        try {
            relativeTestDirectory = new File(requireNonNull(getClass().getResource(blackBoxTestDirectoryPath)).toURI());
        } catch (final URISyntaxException uriSyntaxException) {
            throw new RuntimeException(uriSyntaxException);
        }
        var andThat = compilationSucceeds;
        var outputFileCount = 0;
        for (final var outputFile : requireNonNull(new File(relativeTestDirectory, "outputs/").listFiles())) {
            if (!outputFile.getName().endsWith(".json")) {
                continue;
            }
            andThat = andThat.andThat()
                    .generatedResourceFile(getClass().getPackageName(), outputFile.getName())
                    .matches(fileObject -> GSON.fromJson(fileObject.getCharContent(false).toString(), JsonElement.class)
                            .equals(GSON.fromJson(readString(outputFile.toPath()), JsonElement.class)));
            outputFileCount++;
        }
        final var fOutputFileCount = outputFileCount;
        andThat.executeTest()
                .executeCustomAssertions(compilationOutcome -> assertEquals(fOutputFileCount,
                        compilationOutcome.getFileManager().getFileObjects().stream()
                                .filter(fileObject -> fileObject.getLocation() == CLASS_OUTPUT)
                                .count()));
    }

    private void blackBoxTestFail(final String relativeTestDirectoryPath, final String errorMessagesContain) {
        newBlackBoxTest(getBlackBoxTestDirectoryPath(relativeTestDirectoryPath))
                .compilationFails()
                .andThat()
                .compilerMessage()
                .ofKindError()
                .contains(errorMessagesContain)
                .executeTest();
    }

    private BlackBoxTestOutcomeInterface newBlackBoxTest(final String relativeTestDirectoryPath) {
        return Cute.blackBoxTest()
                .given()
                .processor(OpenApiAnnotationsProcessor.class)
                .andSourceFilesFromFolders("/" + getClass().getPackageName().replace('.', '/') +
                        "/" + relativeTestDirectoryPath)
                .whenCompiled()
                .thenExpectThat();
    }

    private String getBlackBoxTestDirectoryPath(final String relativeTestDirectoryPath) {
        return "blackboxtest/" + relativeTestDirectoryPath;
    }
}
