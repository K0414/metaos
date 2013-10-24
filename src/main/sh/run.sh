#!/bin/bash

CP=$(dirname $0)
for a in $(dirname $0)/../lib/*.jar; do
  CP=$CP:$a
done

os=`uname`
if [ "$os" == 'Darwin' ]; then
  R_HOME=/Library/Frameworks/R.framework/Resources
  R_SHARE_DIR=/Library/Frameworks/R.framework/Resources/share
  export R_SHARE_DIR
  R_INCLUDE_DIR=/Library/Frameworks/R.framework/Resources/include
  export R_INCLUDE_DIR
  R_DOC_DIR=/Library/Frameworks/R.framework/Resources/doc
  export R_DOC_DIR

  JRI_LD_PATH=${R_HOME}/lib:${R_HOME}/bin:
  if test -z "$LD_LIBRARY_PATH"; then
    LD_LIBRARY_PATH=$JRI_LD_PATH
  else
    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JRI_LD_PATH
  fi
fi


if [ "${RCONSOLE}" ]; then
  R_CONSOLE=-DRCONSOLE
else
  R_CONSOLE=
fi

export R_HOME
export LD_LIBRARY_PATH

${JAVA_HOME}/bin/java $R_CONSOLE -Dpython.path=$(dirname $0) -Djava.library.path=$(dirname $0)/../lib -cp ${CP} com.metaos.engine.Engine init.py $*
