#!/bin/bash

CYAN='\033[1;36m'
PURPLE='\033[1;35m'
NC='\033[0m' # No Color

clear

echo -ne "${CYAN}(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧ ~~compiling~~ ٩(◕‿◕｡)۶\r"
javac -d bin src/*.java

echo -e "${PURPLE}(*^-^*) ♡ compiled successfully, all systems are online senpai ♡ (*^-^*)${CYAN}"
java -cp bin Step -i s0.in
