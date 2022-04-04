server=$1
ears_port=$2
#ostrea.rbins.be 8282
#111.1.2.202 8181 old belgica
#192.168.36.21 80 ostrea
#172.16.120.100 new belgica
echo "Doing stuff on $server:$ears_port"
mvn -Dmaven.test.skip=true install

if [[ $server = '192.168.36.21' ]]
then
	scp target/ears3.war thomas@$server:/home/thomas/ears3-server
	ssh -t thomas@$server "cd /home/thomas/ears3-server && ./run.sh"
elif [[ $server = '172.16.120.100' ]]
then
	scp target/ears3.war belgica@$server:/home/belgica/ears3-server
        ssh -t belgica@$server "cd /home/belgica/ears3-server && ./run.sh"
else
echo 'nothing here'
#	scp target/ears3.war thomas@$server:/home/thomas
#	ssh -t thomas@$server "sudo cp /home/thomas/ears3.war /opt/tomcat/webapps && sleep 20 && sudo sed -i 's|ears.navigation.server=http://replace.me|ears.navigation.server=http://localhost:$ears_port|g' /opt/tomcat/webapps/ears3/WEB-INF/classes/application.properties"
fi
