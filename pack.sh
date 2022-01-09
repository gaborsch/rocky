#!/bin/bash
PATH_TO_ROCKY="$(dirname "$(readlink -f $0)")"
java -classpath ${PATH_TO_ROCKY}/rocky.jar rockstar.tool.Packer -rocky-path "${PATH_TO_ROCKY}" $*
