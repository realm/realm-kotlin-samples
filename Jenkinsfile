/*
 * Copyright 2020 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import groovy.json.JsonOutput

@Library('realm-ci') _

currentBranch = env.BRANCH_NAME

// Mac CI dedicated machine
node_label = 'osx_kotlin'

pipeline {
    agent { label node_label }
    // The Gradle cache is re-used between stages, in order to avoid builds interleave,
    // and potentially corrupt each others cache, we grab a global lock for the entire
    // build.
    options {
        lock resource: 'kotlin_build_lock'
        timeout(time: 15, activity: true, unit: 'MINUTES')
    }
    environment {
          ANDROID_SDK_ROOT='/Users/realm/Library/Android/sdk/'
          REALM_DISABLE_ANALYTICS=true
    }
    stages {
        stage('SCM') {
            steps {
                runScm()
            }
        }
        stage('Build All Samples') {
            steps {
                runBuild('Intro')
                runBuild('Bookshelf')
            }
        }
//         stage('Static Analysis') {
//             steps {
//                 runStaticAnalysis()
//             }
//         }
    }
    post {
        failure {
            notifySlackIfRequired("*The realm-kotlin-samples/${currentBranch} branch is broken!*")
        }
        unstable {
            notifySlackIfRequired("*The realm-kotlin-samples/${currentBranch} branch is unstable!*")
        }
        fixed {
            notifySlackIfRequired("*The realm-kotlin-samples/${currentBranch} branch has been fixed!*")
        }
    }
}

def runScm() {
    checkout([
            $class           : 'GitSCM',
            branches         : scm.branches,
            gitTool          : 'native git',
            extensions       : scm.extensions + [
                    [$class: 'WipeWorkspace'],
                    [$class: 'CleanCheckout'],
                    [$class: 'SubmoduleOption', recursiveSubmodules: true]
            ],
            userRemoteConfigs: scm.userRemoteConfigs
    ])

    gitSha = sh(returnStdout: true, script: 'git rev-parse HEAD').trim().take(8)
    echo "Building commit: ${version} - ${gitSha}"
    setBuildName(gitSha)
}

def runBuild(project) {
    withEnv(['PATH+USER_BIN=/usr/local/bin']) {
        startEmulatorInBgIfNeeded()
        sh """
              cd ${project}
              chmod +x gradlew && ./gradlew clean assemble --info --stacktrace --no-daemon
           """
    }
}

// def runStaticAnalysis() {
//     try {
//         sh '''
//         ./gradlew --no-daemon ktlintCheck detekt
//         '''
//     } finally {
//         // CheckStyle Publisher plugin is deprecated and does not support multiple Checkstyle files
//         // New Generation Warnings plugin throw a NullPointerException when used with recordIssues()
//         // As a work-around we just stash the output of Ktlint and Detekt for manual inspection.
//         sh '''
//                 rm -rf /tmp/ktlint
//                 rm -rf /tmp/detekt
//                 mkdir /tmp/ktlint
//                 mkdir /tmp/detekt
//                 rsync -a --delete --ignore-errors examples/kmm-sample/androidApp/build/reports/ktlint/ /tmp/ktlint/example/ || true
//                 rsync -a --delete --ignore-errors test/build/reports/ktlint/ /tmp/ktlint/test/ || true
//                 rsync -a --delete --ignore-errors packages/library/build/reports/ktlint/ /tmp/ktlint/library/ || true
//                 rsync -a --delete --ignore-errors packages/plugin-compiler/build/reports/ktlint/ /tmp/ktlint/plugin-compiler/ || true
//                 rsync -a --delete --ignore-errors packages/gradle-plugin/build/reports/ktlint/ /tmp/ktlint/plugin-gradle/ || true
//                 rsync -a --delete --ignore-errors packages/runtime-api/build/reports/ktlint/ /tmp/ktlint/runtime-api/ || true
//                 rsync -a --delete --ignore-errors examples/kmm-sample/androidApp/build/reports/detekt/ /tmp/detekt/example/ || true
//                 rsync -a --delete --ignore-errors test/build/reports/detekt/ /tmp/detekt/test/ || true
//                 rsync -a --delete --ignore-errors packages/library/build/reports/detekt/ /tmp/detekt/library/ || true
//                 rsync -a --delete --ignore-errors packages/plugin-compiler/build/reports/detekt/ /tmp/detekt/plugin-compiler/ || true
//                 rsync -a --delete --ignore-errors packages/gradle-plugin/build/reports/detekt/ /tmp/detekt/plugin-gradle/ || true
//                 rsync -a --delete --ignore-errors packages/runtime-api/build/reports/detekt/ /tmp/detekt/runtime-api/ || true
//             '''
//         zip([
//                 'zipFile': 'ktlint.zip',
//                 'archive': true,
//                 'dir'    : '/tmp/ktlint'
//         ])
//         zip([
//                 'zipFile': 'detekt.zip',
//                 'archive': true,
//                 'dir'    : '/tmp/detekt'
//         ])
//     }
// }
//
// def runPublishSnapshotToMavenCentral() {
//     withEnv(['PATH+USER_BIN=/usr/local/bin']) {
//         withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'maven-central-credentials', passwordVariable: 'MAVEN_CENTRAL_PASSWORD', usernameVariable: 'MAVEN_CENTRAL_USER']]) {
//             sh """
//             cd packages
//             ./gradlew publishToSonatype -PossrhUsername=$MAVEN_CENTRAL_USER -PossrhPassword=$MAVEN_CENTRAL_PASSWORD --no-daemon
//             """
//         }
//     }
// }
//
// def  runPublishReleaseOnMavenCentral() {
//     withCredentials([
//             [$class: 'StringBinding', credentialsId: 'maven-central-kotlin-ring-file', variable: 'SIGN_KEY'],
//             [$class: 'StringBinding', credentialsId: 'maven-central-kotlin-ring-file-password', variable: 'SIGN_KEY_PASSWORD'],
//             [$class: 'StringBinding', credentialsId: 'slack-webhook-java-ci-channel', variable: 'SLACK_URL_CI'],
//             [$class: 'StringBinding', credentialsId: 'slack-webhook-releases-channel', variable: 'SLACK_URL_RELEASE'],
//             [$class: 'StringBinding', credentialsId: 'gradle-plugin-portal-key', variable: 'GRADLE_PORTAL_KEY'],
//             [$class: 'StringBinding', credentialsId: 'gradle-plugin-portal-secret', variable: 'GRADLE_PORTAL_SECRET'],
//             [$class: 'UsernamePasswordMultiBinding', credentialsId: 'maven-central-credentials', passwordVariable: 'MAVEN_CENTRAL_PASSWORD', usernameVariable: 'MAVEN_CENTRAL_USER'],
//             [$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'DOCS_S3_ACCESS_KEY', credentialsId: 'mongodb-realm-docs-s3', secretKeyVariable: 'DOCS_S3_SECRET_KEY'],
//             [$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'REALM_S3_ACCESS_KEY', credentialsId: 'realm-s3', secretKeyVariable: 'REALM_S3_SECRET_KEY']
//     ]) {
//       sh """
//         set +x
//         sh tools/publish_release.sh '$MAVEN_CENTRAL_USER' '$MAVEN_CENTRAL_PASSWORD' \
//         '$REALM_S3_ACCESS_KEY' '$REALM_S3_SECRET_KEY' \
//         '$DOCS_S3_ACCESS_KEY' '$DOCS_S3_SECRET_KEY' \
//         '$SLACK_URL_RELEASE' '$SLACK_URL_CI' \
//         '$GRADLE_PORTAL_KEY' '$GRADLE_PORTAL_SECRET' \
//         '-PsignBuild=true -PsignSecretRingFileKotlin="${env.SIGN_KEY}" -PsignPasswordKotlin=${env.SIGN_KEY_PASSWORD}'
//       """
//     }
// }
//
// def runCompilerPluginTest() {
//     withEnv(['PATH+USER_BIN=/usr/local/bin']) {
//         sh """
//             cd packages
//             ./gradlew --no-daemon clean :plugin-compiler:test --info --stacktrace
//         """
//         step([ $class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: "packages/plugin-compiler/build/**/TEST-*.xml"])
//     }
// }
//
//
// def test(task) {
//     withEnv(['PATH+USER_BIN=/usr/local/bin']) {
//         sh """
//             cd test
//             ./gradlew $task --info --stacktrace --no-daemon
//         """
//         step([$class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: "test/build/**/TEST-*.xml"])
//     }
// }
//
// def runMonkey() {
//     try {
//         withEnv(['PATH+USER_BIN=/usr/local/bin']) {
//             sh """
//                 cd examples/kmm-sample
//                 ./gradlew uninstallAll installDebug --stacktrace --no-daemon
//                 $ANDROID_SDK_ROOT/platform-tools/adb shell monkey -p  io.realm.example.kmmsample.androidApp -v 500 --kill-process-after-error
//             """
//         }
//     } catch (err) {
//         currentBuild.result = 'FAILURE'
//         currentBuild.stageResult = 'FAILURE'
//     }
// }
//
// def notifySlackIfRequired(String slackMessage) {
//     // We should only generate Slack notifications for important branches that all team members use.
//     if (slackNotificationBranches.contains(currentBranch)) {
//         node {
//             withCredentials([[$class: 'StringBinding', credentialsId: 'slack-webhook-java-ci-channel', variable: 'SLACK_URL']]) {
//                 def payload = JsonOutput.toJson([
//                     username: "Realm CI",
//                     icon_emoji: ":realm_new:",
//                     text: "${slackMessage}\n<${env.BUILD_URL}|Click here> to check the build."
//                 ])
//
//                 sh "curl -X POST --data-urlencode \'payload=${payload}\' ${env.SLACK_URL}"
//             }
//         }
//     }
// }
//
// def readGitTag() {
//     def command = 'git describe --exact-match --tags HEAD'
//     def returnStatus = sh(returnStatus: true, script: command)
//     if (returnStatus != 0) {
//         return null
//     }
//     return sh(returnStdout: true, script: command).trim()
// }
//
// def startEmulatorInBgIfNeeded() {
//     def command = '$ANDROID_SDK_ROOT/platform-tools/adb shell pidof com.android.phone'
//     def returnStatus = sh(returnStatus: true, script: command)
//     if (returnStatus != 0) {
//         sh '/usr/local/Cellar/daemonize/1.7.8/sbin/daemonize  -E JENKINS_NODE_COOKIE=dontKillMe  $ANDROID_SDK_ROOT/emulator/emulator -avd Pixel_2_API_30_x86_64 -no-boot-anim -no-window -wipe-data -noaudio -partition-size 4098'
//     }
// }
//
// boolean shouldPublishSnapshot(version) {
//     if (!releaseBranches.contains(currentBranch)) {
//         return false
//     }
//     if (version == null || !version.endsWith("-SNAPSHOT")) {
//         return false
//     }
//     return true
// }
