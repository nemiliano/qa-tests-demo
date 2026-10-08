package com.nemiliano.qa.demo.suites;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/** Regresión completa (incluye smoke). Los que fallan a propósito quedan afuera. */
@Suite
@SuiteDisplayName("Regresión")
@SelectPackages("com.nemiliano.qa.demo.tests")
@IncludeTags({"smoke", "regression"})
@ExcludeTags("demo-failure")
class RegressionSuite {}
