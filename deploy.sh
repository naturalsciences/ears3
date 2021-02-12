server=$1
ears_port=$2
#ostrea.rbins.be 8282
#111.1.2.202 8181
echo "Doing stuff on $server:$ears_port"
mvn -Dmaven.test.skip=true install
scp target/ears3.war thomas@$server:/home/thomas

ssh -t thomas@$server "sudo cp /home/thomas/ears3.war /opt/tomcat/webapps && sleep 20 && sudo sed -i 's|ears.navigation.server=http://replace.me|ears.navigation.server=http://localhost:$ears_port|g' /opt/tomcat/webapps/ears3/WEB-INF/classes/application.properties"

