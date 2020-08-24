package com.smarttoolfactory.data.test_suite

import com.smarttoolfactory.data.mapper.DTOtoEntityMapperTest
import com.smarttoolfactory.data.source.PostDataSourceCoroutinesTest
import com.smarttoolfactory.data.source.PostDataSourceRxJava3Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

// FIXME Not working, FIX! https://github.com/junit-team/junit5/issues/1334
@RunWith(Suite::class)
@Suite.SuiteClasses(
    DTOtoEntityMapperTest::class,
    PostDataSourceCoroutinesTest::class,
    PostDataSourceRxJava3Test::class
)
class JUnit5DataTestSuite
