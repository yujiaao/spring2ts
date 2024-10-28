export GPG_TTY=$(tty)

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit

mvn clean install -Dmaven.test.skip=true

mvn clean deploy -Ppro -Dmaven.test.skip=true

mvn clean deploy -Pautohome -U -Dmaven.test.skip=true


mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit
