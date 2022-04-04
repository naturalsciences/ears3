

server=$1
ears_port=$2
#ostrea.rbins.be 8282
#111.1.2.202 8181
#192.168.36.21 80

mvn -Dmaven.test.skip=true install

if [[ $server = '172.16.120.100' ]] #ears on belgica, which has docker
then
	echo "Doing stuff on Belgica $server:$ears_port"
	scp target/ears3.war thomas@$server:/home/thomas/ears3-server
	ssh -t thomas@$server "cd /home/thomas/ears3-server && run.sh"
elif [[ $server = 'localhost' ]] #local docker
then
	echo "Doing stuff on localhost"
	scp target/ears3.war /home/thomas/dev/ears3-server
	cd /home/thomas/dev/ears3-server
	./run.sh
else
	echo "Doing stuff on $server:$ears_port"
	scp target/ears3.war thomas@$server:/home/thomas
	ssh -t thomas@$server "sudo cp /home/thomas/ears3.war /opt/tomcat/webapps && sleep 20 && sudo sed -i 's|ears.navigation.server=http://replace.me|ears.navigation.server=http://localhost:$ears_port|g' /opt/tomcat/webapps/ears3/WEB-INF/classes/application.properties"
fi

#mvn -Dmaven.test.skip=true install
#scp target/ears3.war /home/thomas/dev/ears3-server
#cd /home/thomas/dev/ears3-server
#./run-freespace.sh
