package com.nemiliano.qa.demo.suites;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Pruebas rápidas y críticas. Se ejecuta desde el IDE o con {@code mvnw test -Dtest=SmokeSuite}.
 */
@Suite
@SuiteDisplayName("Smoke")
@SelectPackages("com.nemiliano.qa.demo.tests")
@IncludeTags("smoke")
class SmokeSuite {}
