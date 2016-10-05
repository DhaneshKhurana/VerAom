#!/bin/bash
version=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | awk -F '.' '{print $2}')
if [ $version -gt 6 ] ; then
	var=$(readlink -f $(which java))
	PATH=$var:$PATH
	export PATH
	java -Xmx1g -Xms1g -Xmn768m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -cp lib:lib/* com.infovista.statsRetriever.StatsPoller $*
	else
		echo "Default version of java found is less than 1.7. Please provide the JDK path of version 1.7 or above or press q to exit"
		read pth;
		if [ $pth == "q" ] ; then
			exit;
		 else
			PATH=$pth:$PATH
			export PATH
			java -Xmx1g -Xms1g -Xmn768m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -cp lib:lib/* com.infovista.statsRetriever.StatsPoller $*
		fi
fi

