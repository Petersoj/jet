import java.io.File

fun getJReleaserDeployDirectory(rootDir: File): File = File(rootDir, "build/jreleaser-deploy/")
