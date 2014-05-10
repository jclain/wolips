#!/bin/bash
# -*- coding: utf-8 mode: sh -*- vim:sw=4:sts=4:et:ai:si:sta:fenc=utf-8

ITER=1
JAVA_HOME=~/opt/jvm64/sun-jdk-1.5

################################################################################
export JAVA_HOME
cd "$(dirname "$0")"
ant -Dbuild.version="3.7.$(date +%Y%m%d).$ITER" "$@" || exit 1
rsync -av --delete temp/dist/ ../WOLips37UR-site
