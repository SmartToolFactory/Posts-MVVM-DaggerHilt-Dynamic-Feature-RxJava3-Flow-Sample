package com.smarttoolfactory.data

import org.junit.runner.RunWith
import org.junit.runners.Suite

// Runs all unit tests with JUnit4.
@RunWith(Suite::class)
@Suite.SuiteClasses(
    PostDaoCoroutinesCoroutinesFlowTest::class,
    PostDaoCoroutinesRxJavaTest::class
)
class PostDaoCoroutinesTestSuite
