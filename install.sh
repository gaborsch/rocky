#!/bin/bash

# create binary executable
(echo '#!/bin/sh' && echo 'exec java -jar $0 "$@"' && cat rocky.jar) >rockstar.bin
chmod +x rockstar.bin

# move it to /usr/bin
rm -f /usr/bin/rockstar
mv rockstar.bin /usr/bin/rockstar
