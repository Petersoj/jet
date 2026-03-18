import org.gradle.api.file.DirectoryProperty

const val GITHUB_PROJECT_DOMAIN_PATH = "github.com/Petersoj/jet"
const val MAVEN_PUBLICATION_NAME = "maven"

fun getJReleaserDeployDirectory(rootBuildDirectory: DirectoryProperty) = rootBuildDirectory.dir("jreleaser-deploy/")
