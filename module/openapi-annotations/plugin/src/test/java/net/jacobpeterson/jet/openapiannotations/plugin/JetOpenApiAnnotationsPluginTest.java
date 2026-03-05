package net.jacobpeterson.jet.openapiannotations.plugin;

import com.google.gson.JsonParser;
import org.gradle.testkit.runner.GradleRunner;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.walkFileTree;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsPlugin.BUILD_OUTPUT_DEFAULT_DIRECTORY_NAME;
import static net.jacobpeterson.jet.openapiannotations.plugin.JetOpenApiAnnotationsPlugin.TASK_NAME;
import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NullMarked
public final class JetOpenApiAnnotationsPluginTest {

    @Test
    public void successNone(final @TempDir Path tempDir) {
        testSuccess(tempDir, "none");
    }

    @Test
    public void successEmptyNoValidation(final @TempDir Path tempDir) {
        testSuccess(tempDir, "emptynovalidation");
    }

    @Test
    public void successSimpleTypeMappings(final @TempDir Path tempDir) {
        testSuccess(tempDir, "simpletypemappings");
    }

    @Test
    public void successAll(final @TempDir Path tempDir) {
        testSuccess(tempDir, "all");
    }

    @Test
    public void failEmpty(final @TempDir Path tempDir) {
        testFail(tempDir, "empty", "required property 'info' not found");
    }

    @Test
    public void failCouldNotBeInlined(final @TempDir Path tempDir) {
        testFail(tempDir, "couldnotbeinlined", "could not be inlined");
    }

    @Test
    public void failArrayContainsMoreThanOneElement(final @TempDir Path tempDir) {
        testFail(tempDir, "arraycontainsmorethanoneelement", "the array contains more than one element");
    }

    @Test
    public void failExactlyOneKey(final @TempDir Path tempDir) {
        testFail(tempDir, "exactlyonekey", "Exactly one key must be set");
    }

    @Test
    public void failDuplicateKey(final @TempDir Path tempDir) {
        testFail(tempDir, "duplicatekey", "duplicate key");
    }

    @Test
    public void failInAnnotationGroup(final @TempDir Path tempDir) {
        testFail(tempDir, "inannotationgroup", "in annotation group \"TEST\"");
    }

    @Test
    public void failDuplicateComponentName(final @TempDir Path tempDir) {
        testFail(tempDir, "duplicatecomponentname", "different schemas share the same component name of \"TestA\"");
    }

    private void testSuccess(final Path tempDir, final String projectDirectoryName) {
        final var testProjectPath = getTestProjectPath("success/" + projectDirectoryName);
        assertEquals(SUCCESS, requireNonNull(copyTestProjectAndPrepareGradleRunner(tempDir, testProjectPath).build()
                .task(":" + TASK_NAME)).getOutcome());
        final var buildOutputsPath = tempDir.resolve("build/" + BUILD_OUTPUT_DEFAULT_DIRECTORY_NAME);
        var expectedOutputFileCount = 0;
        for (final var expectedOutputFile : requireNonNull(testProjectPath.resolve("outputs/").toFile().listFiles())) {
            if (!expectedOutputFile.getName().endsWith(".json")) {
                continue;
            }
            try {
                assertEquals(JsonParser.parseString(readString(expectedOutputFile.toPath())),
                        JsonParser.parseString(readString(buildOutputsPath.resolve(expectedOutputFile.getName()))));
            } catch (final IOException ioException) {
                throw new RuntimeException(ioException);
            }
            expectedOutputFileCount++;
        }
        assertEquals(expectedOutputFileCount, requireNonNull(buildOutputsPath.toFile().list()).length);
    }

    private void testFail(final Path tempDir, final String projectDirectoryName, final String buildOutputContains) {
        final var buildAndFail = copyTestProjectAndPrepareGradleRunner(tempDir,
                getTestProjectPath("fail/" + projectDirectoryName)).buildAndFail();
        assertEquals(FAILED, requireNonNull(buildAndFail.task(":" + TASK_NAME)).getOutcome());
        assertTrue(buildAndFail.getOutput().contains(buildOutputContains));
    }

    private GradleRunner copyTestProjectAndPrepareGradleRunner(final Path tempDir, final Path testProjectPath) {
        try {
            walkFileTree(testProjectPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
                        throws IOException {
                    createDirectories(resolveDestination(dir));
                    return CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                        throws IOException {
                    copy(file, resolveDestination(file));
                    return CONTINUE;
                }

                private Path resolveDestination(final Path path) {
                    return tempDir.resolve(testProjectPath.relativize(path));
                }
            });
        } catch (final IOException ioException) {
            throw new RuntimeException(ioException);
        }
        final var gradleRunner = GradleRunner.create()
                .withPluginClasspath();
        return gradleRunner.withEnvironment(Map.of("plugin-classpath", gradleRunner.getPluginClasspath().stream()
                        .map(File::toString)
                        .collect(joining("\n"))))
                .withProjectDir(tempDir.toFile())
                .withArguments("build", "--info", "--stacktrace");
    }

    private Path getTestProjectPath(final String relativePath) {
        try {
            return Paths.get(requireNonNull(getClass().getResource("testproject/" + relativePath)).toURI());
        } catch (final URISyntaxException uriSyntaxException) {
            throw new RuntimeException(uriSyntaxException);
        }
    }
}
