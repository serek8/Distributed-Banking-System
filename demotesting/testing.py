import requests

# Senerio A: Too account with same number
r = requests.post('http://127.0.0.1:8080/accounts/create',json={"accountNumber" : "1"})
r = requests.post('http://127.0.0.1:8080/accounts/create',json={"accountNumber" : "1"})

# Senerio B:  
r = requests.post('http://127.0.0.1:8080/accounts/1/deposit',json={"amount" : "200"})
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "50"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "50"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "50"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "50"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "150"}) #should NOT happen

# Senerio C:  
r = requests.post('http://127.0.0.1:8080/accounts/1/deposit',json={"amount" : "400"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "200"}) #should happen

# Senerio Ca: More then one  
while True:
	r = requests.post('http://127.0.0.1:8080/accounts/1/deposit',json={"amount" : "400"}) #should happen
	r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "200"}) #should happen
	r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "200"}) #should happen
	#break;

# Senerio D:
r = requests.post('http://127.0.0.1:8080/accounts/1/deposit',json={"amount" : "100"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/transfer',json={"amount" : "100","accountNumberDestination" : "2"}) #should happen
r = requests.post('http://127.0.0.1:8080/accounts/1/withdraw',json={"amount" : "100"}) #should NOT happen
r = requests.post('http://127.0.0.1:8080/accounts/2/withdraw',json={"amount" : "100"}) #should happen



http://127.0.0.1:8080/accounts/1/transfer 
print(r.text)
print(r.headers)