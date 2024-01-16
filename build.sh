#!/bin/bash

# set -xe

# if [[ $1 != '' ]]; then
#     java -cp libs/jna-5.12.1.jar:Berjis.jar com.berjis.Main
#     exit 0
# fi

javac ./com/berjis/*.java -cp libs/jna-5.14.0.jar -d out

if [[ $? -ne 0 ]]; then
    exit 1
fi

jar cvmf Manifest.txt Berjis.jar ./out/com/berjis/*.class -C out .

if [[ $? -ne 0 ]]; then
    exit 1
fi

if [[ $1 == 'run' ]]; then
    case "$(uname -sr)" in
        Linux*)
            java -cp 'Berjis.jar:libs/jna-5.14.0.jar' com.berjis.Main -verbose
            ;;

        CYGWIN*|MINGW*|MINGW32*|MSYS*)
            java -cp 'Berjis.jar;libs/jna-5.14.0.jar' com.berjis.Main -verbose
            ;;

    *)
        echo "This script doesn't work on this os"
        exit 1 
        ;;
    esac
fi
