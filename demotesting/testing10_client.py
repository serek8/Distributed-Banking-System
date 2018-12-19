#####################################
###  Testing v1
###  a=bab2501 v= 1.0 d= 2018.12.08
####################################

###
# Dependencies 
# => pip install requests
#########################
import requests
import time

###
# Global Settings
#########################

host = "145.100.111.54"
protocol = "http"
port = "8080"
baseurl = protocol + "://" + host + ":" + port

###
# Standard Functions (theAPI) 
#########################
def createAccount(accountNumber):
	url = baseurl+"/accounts/create"
	jdata={"accountNumber": str(accountNumber)}
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- createAccount " + str(accountNumber))

def balanceAccount(accountNumber):
	url = baseurl+"/accounts/"+str(accountNumber)+"/balance"
	#print(url)
	#jdata={"amount" : str(amount)}
	#r = requests.post(url,json=jdata)
	r = requests.get(url)
	print("Status: " + str(r.status_code) + " Message: " + r.text)
	#return float(r.text);

def depositAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/deposit"
	jdata={"accountNumber," : str(accountNumber), "amount" : amount}
	#print(url)
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- depositAccount " + str(amount) + " to " + str(accountNumber))

def withdrawAccount(accountNumber, amount):
	url = baseurl+"/accounts/"+str(accountNumber)+"/withdraw"
	jdata={"amount" : amount}
	#print(url)
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- withdrawAccount " + str(amount) + " from " + str(accountNumber))

def transferAccount(accountNumber, amount, accountNumberDestination):
	url = baseurl+"/accounts/"+str(accountNumber)+"/transfer"
	jdata={"amount" : amount, "accountNumberDestination" : str(accountNumberDestination)}
	#print(url)	
	r = requests.post(url,json=jdata)
	print("Status: " + str(r.status_code) + " Message: " + r.text + " -- transferAccount " + str(amount) + " from " + str(accountNumber) + " to " + str(accountNumberDestination))
	
## Test Loop

while True:
	depositAccount(5, 1000)
	time.sleep(1)
	for lp in range(100):
		transferAccount(5, 10, 6)
	time.sleep(1)
	for lp in range(100):
        	transferAccount(6, 10, 7)
	time.sleep(1)
	for lp in range(100):
        	transferAccount(7, 10, 8)
	time.sleep(1)
	withdrawAccount(8, 1000)
	time.sleep(60)

