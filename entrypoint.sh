#!/bin/bash
set -ex

#run_main() {
#    run_app
#}

run_app() {
  java ${APP_JVM_OPTS} -jar ${APP_HOME}/app.jar
}

run_app
