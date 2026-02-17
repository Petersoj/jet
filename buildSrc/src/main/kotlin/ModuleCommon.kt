import java.io.File

const val GITHUB_PROJECT_DOMAIN_PATH = "github.com/Petersoj/jet"

fun getJReleaserDeployDirectory(rootDir: File) = File(rootDir, "build/jreleaser-deploy/")
