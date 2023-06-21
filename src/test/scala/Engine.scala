import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object Engine extends App {

	//Configure logback  log level in test/resources/logback-test.xml to:
	// "<root level="WARN">" - to prevent logging of session variables and values
	// "<root level="INFO">" - to enable logging of session variables and values

	val props = new GatlingPropertiesBuilder()
		.resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString)
		.resultsDirectory(IDEPathHelper.resultsDirectory.toString)
		.binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString)

	Gatling.fromMap(props.build)
}
