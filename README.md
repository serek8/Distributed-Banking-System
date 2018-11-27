# Distributed-Banking-System

## Akka
Our [configuration](src/main/resources/application.conf) requires two seed nodes (on ports 2552 and 2553) in order for the cluster to start, so please keep it in mind and set the right environmental variables during your development.

## Cassandra
### Requirements
* [Docker Compose](https://docs.docker.com/compose/install/)
* It is recommended to allocate around 1GB of memory per 1 Cassandra node (we have 3 nodes, so best would be to allocate 3GB). [Docker's Advanced Settings](https://docs.docker.com/docker-for-windows/#shared-drives) is the right place to set the memory allocation limits.

### Getting started
To create/run a 3-node Cassandra cluster, please run the following command in the project directory:
```bash
$ docker-compose up -d
```
After running the command, please check whether all nodes are operational. Run
```bash
$ docker-compose ps
``` 
As the output should see something like this:
```bash
   Name                 Command               State                                      Ports                                    
   ----------------------------------------------------------------------------------------------------------------------------------
   cassandra0   docker-entrypoint.sh cassa ...   Up      7000/tcp, 7001/tcp, 0.0.0.0:7199->7199/tcp, 0.0.0.0:9042->9042/tcp, 9160/tcp
   cassandra1   docker-entrypoint.sh cassa ...   Up      7000/tcp, 7001/tcp, 7199/tcp, 0.0.0.0:9142->9042/tcp, 9160/tcp              
   cassandra2   docker-entrypoint.sh cassa ...   Up      7000/tcp, 7001/tcp, 7199/tcp, 0.0.0.0:9242->9042/tcp, 9160/tcp      
```
If instead of `State: Up`, you see `State: Exit` next to any of the cassandra nodes, it means something went wrong.