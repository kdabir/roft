package com.kdabir.roft

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

class RerunOnlyFailedTestsPlugin implements Plugin<Project> {

    public static final String FAILED_TESTS_FILE_NAME = "failed-tests.txt"

    void apply(Project project) {

        // Locate the file
        final File failuresFile = new File(project.buildDir, FAILED_TESTS_FILE_NAME)

        // Load the list of failed test names
        final List<String> failedTests = failuresFile.exists() ? failuresFile.readLines() : []

        // Remove the file. If failures still exist, it will be created again
        failuresFile.delete()

        project.tasks.withType(Test) {

            filter {
                failedTests.each {
                    includeTestsMatching "${it}"
                }
            }

            afterTest { TestDescriptor test, TestResult result ->
                if (result.resultType == TestResult.ResultType.FAILURE) {
                    failuresFile.withWriterAppend {
                        it.println("${test.className}.${test.name}")
                    }
                }
            }
        }
    }
}


