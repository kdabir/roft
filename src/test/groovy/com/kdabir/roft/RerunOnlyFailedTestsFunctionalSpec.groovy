package com.kdabir.roft

import directree.DirTree
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class RerunOnlyFailedTestsFunctionalSpec extends Specification {

    @Rule
    TemporaryFolder projectDir = new TemporaryFolder()
    File testProject

    def setup() {
        testProject = projectDir.newFolder("test-project")

        DirTree.create(testProject.absolutePath) {
            file("build.gradle") {
                """\
                plugins {
                    id 'java'
                    id 'com.kdabir.rerun-only-failed-tests'
                }
                
                repositories { jcenter() }
                
                dependencies {
                    testCompile "junit:junit:4.12" 
                }   
                """.stripIndent()
            }

            dir("src/test/java") {
                file("Tests.java") {
                    """\
                    import org.junit.*; 
                    import static org.junit.Assert.*;
                     
                    public class Tests {
                     
                        @Test
                        public void aTestThatFails(){
                            assertTrue(false);
                        }
                    
                        @Test
                        public void aTestThatPasses(){
                            assertTrue(true);
                        }
                        
                    }
                    """.stripIndent()
                }

            }
        }

    }

    def "creates file for failed test"() {
        given: "failed tests file already do not exist"

        when: "running the build expecting failure"
        def result = GradleRunner.create()
            .withProjectDir(testProject)
            .withArguments('test')
            .withPluginClasspath()
            .buildAndFail()

        then: "failed text file is created"
        new File(testProject, "build/failed-tests.txt").exists()
        new File(testProject, "build/failed-tests.txt").text == "Tests.aTestThatFails\n"

    }

    def "honors failed text file if present and updates it"() {
        given: "failed tests already exists"
        DirTree.create(testProject.absolutePath) {
            dir("build") {
                file("failed-tests.txt") {
                    "Tests.aTestThatPasses"
                }
            }
        }

        when: "tests are run successfully and other tests are not executed"
        def result = GradleRunner.create()
            .withProjectDir(testProject)
            .withArguments('test')
            .withPluginClasspath()
            .build()

        then: "only executes test that failed-test files mentions and removes the file"

        result.task(":test").getOutcome() == TaskOutcome.SUCCESS
        !new File(testProject, "build/failed-tests.txt").exists()
    }

}
