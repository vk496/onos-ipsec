docker-compose ps | grep netopeer | awk '{print $1}' | xargs -I {} sh -c "docker ps | grep {} | cut -d' ' -f1" | xargs docker inspect -f '{{range 
.NetworkSettings.Networks}} {{.IPAddress}}{{end}} {{.Name}}' | grep $(docker logs tfg_onos_1 | grep "org.foo.app" | grep "Root device" | rev | cut 
-d" " -f1 | rev)
