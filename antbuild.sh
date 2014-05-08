#!/bin/bash
# -*- coding: utf-8 mode: sh -*- vim:sw=4:sts=4:et:ai:si:sta:fenc=utf-8

SUFFIX=ur1
DATE="$(date +%Y%m%d)"
JAVA_HOME=~/opt/jvm64/sun-jdk-1.5

################################################################################
export JAVA_HOME
cd "$(dirname "$0")"
ant -Dbuild.version="3.7.$DATE$SUFFIX" "$@"
