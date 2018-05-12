# Rerun only Failed Tests

[![Build Status](https://travis-ci.org/kdabir/roft.svg?branch=master)](https://travis-ci.org/kdabir/roft)

Runs only the failed tests from last run. This is especially important productivity boost when we have a very
large test suite.

### Usage

Apply the plugin to the project

    plugins {
        id "com.kdabir.rerun-only-failed-tests" version "0.0.1"
    }

  
Run tests

    $ gradle test

If there are failures the next `gradle test` would execute only the failed tests. Once those tests
pass, subsequent `gradle test` will run the entire test suite.

Caveat: If we have introduced a test failure in another cases while fixing failed ones, it might go 
unnoticed since we are only running the part of test suite.  

 

### Contributing

1. Make Changes

2. Add test coverage for changes

3. Publish it to local maven repository using `gradle publishToMavenLocal` 

4. To consume the plugin built locally, add this to consuming project

```
buildscript{
    repositories { mavenLocal() }
    dependencies { classpath "com.kdabir.roft:roft:+" }
}

apply plugin: 'com.kdabir.rerun-only-failed-tests'

```

5. Once changes look good, Send a pull request


#### Acknowledgement

This plugin is heavily inspired by the following StackOverflow [answer](https://stackoverflow.com/a/48826059/469414).
 

