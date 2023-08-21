#!/bin/bash
PATH_TO_PACKER="$(dirname "$(readlink -f $0)")"
java -classpath ${PATH_TO_PACKER}/../rocky.jar rockstar.tool.Packer -rocky-path "${PATH_TO_PACKER}/.." $*
