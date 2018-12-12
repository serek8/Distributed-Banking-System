# **Instruction for DAS-4**
# 1. Node reservation
Load module for reserving nodes
```
module load prun
```
Reserve a node for 3600s
```
preserve -t 3600 -np 1
```
Check, which node you have
```
preserve -llist
```
Based on the output, ssh to the new allocated node
```
ssh nodeXXX
```
## 2. Cassandra
check if it's already running with **ps**
```
ps aux | grep cassandra
```
if cassandra isn't running, run it
a) with HTTP(only one node should have HTTP)
```
./test_script/autoRunCassandraHTTP.sh
```
b) without HTTP
```
./test_script/autoRunCassandraNO_HTTP.sh
```
## 3. Akka
with HTTP(only one node should have HTTP)
```
./test_script/autoRunAkkaHTTP.sh
```
without HTTP
```
./test_script/autoRunAkkaNO_HTTP.sh
```

## 4. More details for HTTP requests
IP: 
```
cat ./test_script/seed_ip.txt
```
Port: 8061

