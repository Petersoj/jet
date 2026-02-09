package net.jacobpeterson.jet.openapiannotations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.toolisticon.cute.Cute;
import io.toolisticon.cute.CuteApi.BlackBoxTestOutcomeInterface;
import io.toolisticon.cute.CuteApi.CompilerTestExpectAndThatInterface;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;

@NullMarked
public final class OpenApiAnnotationsProcessorTest {

    private static final Gson GSON = new GsonBuilder().create();

    @Test
    public void processMetaAnnotationsValidationSuccessNone() {
        blackBoxTestSuccess("meta/annotationsvalidation/success/none/");
    }

    @Test
    public void processMetaAnnotationsValidationSuccessWarning() {
        blackBoxTestSuccess("meta/annotationsvalidation/success/warning/");
    }

    @Test
    public void processMetaAnnotationsValidationFail() {
        blackBoxTestFail("meta/annotationsvalidation/fail/", "required property 'info' not found");
    }

    @Test
    public void processSpecificationSelfSuccess() {
        blackBoxTestSuccess("specification/self/success/");
    }

    @Test
    public void processSpecificationSelfFailDuplicate() {
        blackBoxTestFail("specification/self/fail/duplicate/", "Duplicate");
    }

    @Test
    public void processSpecificationInfoSuccessSimple() {
        blackBoxTestSuccess("specification/info/success/simple/");
    }

    @Test
    public void processSpecificationInfoSuccessWithContact() {
        blackBoxTestSuccess("specification/info/success/withcontact/");
    }

    @Test
    public void processSpecificationInfoFailDuplicate() {
        blackBoxTestFail("specification/info/fail/duplicate/", "Duplicate");
    }

    @Test
    public void processSpecificationInfoFailDuplicateSingleEmpty() {
        blackBoxTestFail("specification/info/fail/duplicatesingleempty/", "Duplicate");
    }

    @Test
    public void processSpecificationInfoFailDuplicateMultipleEmpty() {
        blackBoxTestFail("specification/info/fail/duplicatemultipleempty/", "Duplicate");
    }

    @Test
    public void processSpecificationJsonSchemaDialectSuccess() {
        blackBoxTestSuccess("specification/jsonschemadialect/success/");
    }

    @Test
    public void processSpecificationJsonSchemaDialectFailDuplicate() {
        blackBoxTestFail("specification/jsonschemadialect/fail/duplicate/", "Duplicate");
    }

    @Test
    public void processSpecificationExternalDocSuccess() {
        blackBoxTestSuccess("specification/externalDoc/success/");
    }

    @Test
    public void processSpecificationExternalDocFailDuplicate() {
        blackBoxTestFail("specification/externalDoc/fail/duplicate/", "Duplicate");
    }

    @Test
    public void processSpecificationServerSuccessSimple() {
        blackBoxTestSuccess("specification/server/success/simple/");
    }

    @Test
    public void processSpecificationServerSuccessWithVariables() {
        blackBoxTestSuccess("specification/server/success/withvariables/");
    }

    @Test
    public void processSpecificationServerFailDuplicateName() {
        blackBoxTestFail("specification/server/fail/duplicatename/", "Duplicate");
    }

    @Test
    public void processSpecificationServerFailDuplicateUrl() {
        blackBoxTestFail("specification/server/fail/duplicateurl/", "Duplicate");
    }

    @Test
    public void processSpecificationTagSuccessSimple() {
        blackBoxTestSuccess("specification/tag/success/simple/");
    }

    @Test
    public void processSpecificationTagSuccessWithExternalDoc() {
        blackBoxTestSuccess("specification/tag/success/withexternaldoc/");
    }

    @Test
    public void processSpecificationTagFailDuplicate() {
        blackBoxTestFail("specification/tag/fail/duplicate/", "Duplicate");
    }

    @Test
    public void processSpecificationTagFailAnnotationArrayIsNullableValue() {
        blackBoxTestFail("specification/tag/fail/annotationarrayisnullablevalue/",
                "the length of the array value is not equal to one");
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
        CompilerTestExpectAndThatInterface andThat = null;
        for (final var resultFile : requireNonNull(new File(relativeTestDirectory, "results/").listFiles())) {
            if (!resultFile.getName().endsWith(".json")) {
                continue;
            }
            andThat = (andThat == null ? compilationSucceeds : andThat)
                    .andThat()
                    .generatedResourceFile(getClass().getPackageName(), resultFile.getName())
                    .matches(fileObject -> GSON.fromJson(fileObject.getCharContent(false).toString(), JsonElement.class)
                            .equals(GSON.fromJson(readString(resultFile.toPath()), JsonElement.class)));
        }
        (andThat == null ? compilationSucceeds : andThat).executeTest();
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
